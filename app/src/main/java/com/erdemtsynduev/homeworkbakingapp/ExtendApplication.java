package com.erdemtsynduev.homeworkbakingapp;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.erdemtsynduev.homeworkbakingapp.ldlingResource.SimpleIdlingResource;

import io.paperdb.Paper;

public class ExtendApplication extends Application {

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    private SimpleIdlingResource initializeIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    public ExtendApplication() {
        // The IdlingResource will be null in production.
        if (BuildConfig.DEBUG) {
            initializeIdlingResource();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Init NoSQL DB
        Paper.init(getApplicationContext());
    }

    public void setIdleState(boolean state) {
        if (mIdlingResource != null)
            mIdlingResource.setIdleState(state);
    }

    @Nullable
    public SimpleIdlingResource getIdlingResource() {
        return mIdlingResource;
    }
}