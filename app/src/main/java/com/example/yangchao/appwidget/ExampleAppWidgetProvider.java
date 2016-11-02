package com.example.yangchao.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Created by yangchao on 2016/11/1.
 */

public class ExampleAppWidgetProvider extends AppWidgetProvider{
    private static final String TAG = "ExampleAppWidgetProvider";
    private boolean DEBUG = false;
    /**
     * 启动ExampleAppWidgetService服务对应的action
      */
    private final Intent EXAMPLE_SERVICE_INTENT = new Intent("android.appwidget.action.EXAMPLE_APP_WIDGET_SERVICE");
    private final String ACTION_UPDATE_ALL = "com.skywang.widget.UPDATE_ALL";
    private static Set idsSet = new HashSet();
    private static final int BUTTON_SHOW = 1;
    private static final int[] ARR_IMAGES = {
        R.drawable.sample_0,
        R.drawable.sample_1,
        R.drawable.sample_2,
        R.drawable.sample_3,
        R.drawable.sample_4,
        R.drawable.sample_5,
        R.drawable.sample_6,
        R.drawable.sample_7
    };

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
        Log.i("TAG","onUpdate(): appWidgetIds.length="+appWidgetIds.length);

        for(int appWidgetId : appWidgetIds){
            idsSet.add(Integer.valueOf(appWidgetId));
        }
        prtSet();
    }

    /**
     *当 widget 被初次添加 或者 当 widget 的大小被改变时，被调用
     */
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Log.i("TAG","onAppWidgetOptionsChanged");
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i("TAG","onDeleted(): appWidgetIds.length="+appWidgetIds.length);

        for (int appWidgetId : appWidgetIds){
            idsSet.remove(Integer.valueOf(appWidgetId));
        }
        prtSet();

        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Log.i("TAG","onEnabled");
        context.startService(EXAMPLE_SERVICE_INTENT);

        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.i("TAG", "onDisabled");

        // 在最后一个 widget 被删除时，终止服务
        context.stopService(EXAMPLE_SERVICE_INTENT);

        super.onDisabled(context);
    }

    // 接收广播的回调函数
    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        Log.i("TAG","OnReceive:Action: " + action);
        if (ACTION_UPDATE_ALL.equals(action)){
            updateAllAppWidgets(context,AppWidgetManager.getInstance(context),idsSet);
        }else if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
            // “按钮点击”广播
            Uri data = intent.getData();
            int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
            if (buttonId == BUTTON_SHOW) {
                Log.i("TAG", "Button wifi clicked");
                Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show();
            }
        } 
        super.onReceive(context, intent);
    }

    private void updateAllAppWidgets(Context context,AppWidgetManager appWidgetManager,Set set) {

        Log.i("TAG", "updateAllAppWidgets(): size="+set.size());

        int appID;
        // 迭代器，用于遍历所有保存的widget的id
        Iterator it = set.iterator();
        while (it.hasNext()){
            appID = ((Integer)it.next()).intValue();
            // 随机获取一张图片
            int index = (new java.util.Random().nextInt(ARR_IMAGES.length));
            int string = new Random().nextInt(ARR_WORDS.length);
            CharSequence widgetText = ARR_WORDS[string];

            if (DEBUG) Log.i("TAG", "onUpdate(): index="+index);
            // 获取 example_appwidget.xml 对应的RemoteViews
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.example_appwidget);


            // 设置显示图片
            remoteView.setImageViewResource(R.id.iv_show, ARR_IMAGES[index]);
            remoteView.setTextViewText(R.id.tv_show,widgetText);

            // 设置点击按钮对应的PendingIntent：即点击按钮时，发送广播。
            remoteView.setOnClickPendingIntent(R.id.btn_show, getPendingIntent(context,
                    BUTTON_SHOW));

            // 更新 widget
            appWidgetManager.updateAppWidget(appID, remoteView);
        }
    }

    private PendingIntent getPendingIntent(Context context, int buttonId) {
        Intent intent = new Intent();
        intent.setClass(context, ExampleAppWidgetProvider.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse("custom:" + buttonId));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0 );
        return pi;
    }

    private void prtSet() {
        if (DEBUG) {
            int index = 0;
            int size = idsSet.size();
            Iterator it = idsSet.iterator();
            Log.d("TAG", "total:"+size);
            while (it.hasNext()) {
                Log.d("TAG", index + " -- " + ((Integer)it.next()).intValue());
            }
        }
    }
}
