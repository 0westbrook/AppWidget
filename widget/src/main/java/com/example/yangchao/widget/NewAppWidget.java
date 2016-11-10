package com.example.yangchao.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    private static Set idsSet = new HashSet();
    private final Intent EXAMPLE_SERVICE_INTENT =
            new Intent("android.appwidget.action.EXAMPLE_APP_WIDGET_SERVICE");
    // 更新 widget 的广播对应的action
    private final String ACTION_UPDATE_ALL = "com.skywang.widget.UPDATE_ALL";
    // 保存 widget 的id的HashSet，每新建一个 widget 都会为该 widget 分配一个 id。

    private static final String[] ARR_WORDS = {
            "邓易林",
            "毛同生",
            "朱琪",
            "周鹏",
            "倪远杰",
            "杨艺琳",
            "吴凡",
            "曾太",
    };

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            idsSet.add(Integer.valueOf(appWidgetId));
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds){
            idsSet.remove(Integer.valueOf(appWidgetId));
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        context.startService(EXAMPLE_SERVICE_INTENT);
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        context.stopService(EXAMPLE_SERVICE_INTENT);
        super.onDisabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        if(ACTION_UPDATE_ALL.equals(action)){
            updateAllAppWidgets(context,AppWidgetManager.getInstance(context),idsSet);
        }

        super.onReceive(context,intent);
    }

    private void updateAllAppWidgets(Context context, AppWidgetManager appWidgetManager, Set set) {
        int appID;
        // 迭代器，用于遍历所有保存的widget的id
        Iterator it = set.iterator();

        while (it.hasNext()){
            appID =  ((Integer)it.next()).intValue();
            int index = (new Random().nextInt(ARR_WORDS.length));

            CharSequence widgetText = ARR_WORDS[index];
            RemoteViews remoteView = new RemoteViews(context.getPackageName(),R.layout.new_app_widget);
            remoteView.setTextViewText(R.id.widget_tv,widgetText);

            appWidgetManager.updateAppWidget(appID,remoteView);
        }
    }
}

