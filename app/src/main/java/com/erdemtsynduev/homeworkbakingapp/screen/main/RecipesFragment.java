package com.erdemtsynduev.homeworkbakingapp.screen.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erdemtsynduev.homeworkbakingapp.ExtendApplication;
import com.erdemtsynduev.homeworkbakingapp.R;
import com.erdemtsynduev.homeworkbakingapp.network.RestClient;
import com.erdemtsynduev.homeworkbakingapp.network.api.Api;
import com.erdemtsynduev.homeworkbakingapp.network.response.Recipe;
import com.erdemtsynduev.homeworkbakingapp.screen.Listeners;
import com.erdemtsynduev.homeworkbakingapp.screen.adapters.RecipesAdapter;
import com.erdemtsynduev.homeworkbakingapp.utils.Misc;
import com.erdemtsynduev.homeworkbakingapp.utils.SpacingItemDecoration;
import com.erdemtsynduev.homeworkbakingapp.widget.ExtendAppWidgetService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class RecipesFragment extends Fragment {

    @BindView(R.id.recipes_recycler_view)
    RecyclerView mRecipesRecyclerView;
    @BindView(R.id.pull_to_refresh)
    SwipeRefreshLayout mPullToRefresh;
    @BindView(R.id.noDataContainer)
    ConstraintLayout mNoDataContainer;

    private static String RECIPES_KEY = "recipes";

    private OnRecipeClickListener mListener;
    private Unbinder unbinder;
    private List<Recipe> mRecipes;
    private ExtendApplication globalApplication;
    private CompositeDisposable mCompositeDisposable;
    private Api api = RestClient.getInstance().getApi();

    /**
     * Will load the movies when the app launch, or if the app will launch without an internet connection
     * and then reconnects, will load them without the need for user to perform a (pull to refresh)
     */
    private final BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mRecipes == null) {
                loadRecipes();
            }
        }
    };

    public RecipesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment bind view to butter knife
        View viewRoot = inflater.inflate(R.layout.fragment_recipes, container, false);
        unbinder = ButterKnife.bind(this, viewRoot);

        mPullToRefresh.setOnRefreshListener(() -> loadRecipes());

        mNoDataContainer.setVisibility(View.VISIBLE);
        setupRecyclerView();

        // Get the IdlingResource instance
        globalApplication = (ExtendApplication) getActivity().getApplicationContext();

        globalApplication.setIdleState(false);


        if (savedInstanceState != null && savedInstanceState.containsKey(RECIPES_KEY)) {
            mRecipes = savedInstanceState.getParcelableArrayList(RECIPES_KEY);

            mRecipesRecyclerView.setAdapter(new RecipesAdapter(getActivity().getApplicationContext(),
                    mRecipes, position -> mListener.onRecipeSelected(mRecipes.get(position))));
            dataLoadedTakeCareLayout();
        }
        return viewRoot;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecipeClickListener) {
            mListener = (OnRecipeClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecipeClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        Timber.d("onDestroyView");
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mRecipes != null && !mRecipes.isEmpty())
            outState.putParcelableArrayList(RECIPES_KEY, (ArrayList<? extends Parcelable>) mRecipes);
    }

    private void setupRecyclerView() {
        mRecipesRecyclerView.setVisibility(View.GONE);
        mRecipesRecyclerView.setHasFixedSize(true);

        boolean twoPaneMode = getResources().getBoolean(R.bool.twoPaneMode);
        if (twoPaneMode) {
            mRecipesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 3));
        } else {
            mRecipesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        }

        mRecipesRecyclerView.addItemDecoration(new SpacingItemDecoration((int) getResources().getDimension(R.dimen.margin_medium)));
        mRecipesRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener());
    }

    private void loadRecipes() {
        // Set SwipeRefreshLayout that refreshing in case that loadRecipes get called by the networkChangeReceiver
        if (Misc.isNetworkAvailable(getActivity().getApplicationContext())) {
            mPullToRefresh.setRefreshing(true);

            mCompositeDisposable.add(api.getRecipes()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        if (result != null) {
                            mRecipes = result;
                            mRecipesRecyclerView.setAdapter(new RecipesAdapter(getActivity().getApplicationContext(), mRecipes, new Listeners.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    mListener.onRecipeSelected(mRecipes.get(position));
                                }
                            }));
                            // Set the default recipe for the widget
                            if (Paper.book().read("recipe") == null) {
                                ExtendAppWidgetService.updateWidget(getActivity(), mRecipes.get(0));
                            }
                        } else {
                            Misc.makeSnackBar(getActivity(), getView(), getString(R.string.failed_to_load_data), true);
                        }
                        dataLoadedTakeCareLayout();
                    }, throwable -> {
                        dataLoadedTakeCareLayout();
                        throwable.printStackTrace();
                    }));
        } else {
            Misc.makeSnackBar(getActivity(), getView(), getString(R.string.no_internet), true);
        }
    }


    /**
     * Check if data is loaded and show/hide Recipes RecyclerView & NoDataContainer regarding the recipes data state
     */
    private void dataLoadedTakeCareLayout() {
        boolean loaded = mRecipes != null && mRecipes.size() > 0;
        mPullToRefresh.setRefreshing(false);

        mRecipesRecyclerView.setVisibility(loaded ? View.VISIBLE : View.GONE);
        mNoDataContainer.setVisibility(loaded ? View.GONE : View.VISIBLE);

        globalApplication.setIdleState(true);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRecipeClickListener {
        void onRecipeSelected(Recipe recipe);
    }
}