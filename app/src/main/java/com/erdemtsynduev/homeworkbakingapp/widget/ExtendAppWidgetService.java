package com.erdemtsynduev.homeworkbakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViewsService;

import com.erdemtsynduev.homeworkbakingapp.network.response.Recipe;

import io.paperdb.Paper;

public class ExtendAppWidgetService extends RemoteViewsService {

    public static void updateWidget(Context context, Recipe recipe) {
        Paper.book().write("recipe", recipe);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, ExtendAppWidgetProvider.class));
        ExtendAppWidgetProvider.updateAppWidgets(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        return new ListRemoteViewsFactory(getApplicationContext());
    }

}