package com.erdemtsynduev.homeworkbakingapp.screen.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.erdemtsynduev.homeworkbakingapp.R;
import com.erdemtsynduev.homeworkbakingapp.network.response.Step;
import com.erdemtsynduev.homeworkbakingapp.screen.recipestepdetail.RecipeStepDetailFragment;

import java.util.List;

public class StepsFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private List<Step> mSteps;

    public StepsFragmentPagerAdapter(Context context, List<Step> steps, FragmentManager fm) {
        super(fm);
        this.mContext = context;
        this.mSteps = steps;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(RecipeStepDetailFragment.STEP_KEY, mSteps.get(position));
        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return String.format(mContext.getString(R.string.step), position);
    }

    @Override
    public int getCount() {
        return mSteps.size();
    }
}