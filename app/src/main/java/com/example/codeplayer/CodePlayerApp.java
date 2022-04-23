package com.example.codeplayer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.TaskStackBuilder;

import com.example.codeplayer.utils.Constant;
import com.lidroid.xutils.DbUtils;

/**
 * @author ASUS_JAJIAN
 */
public class CodePlayerApp extends Application {

    public static SharedPreferences sp;
    public static DbUtils dbUtils;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        dbUtils = DbUtils.create(getApplicationContext(),Constant.DB_NAME);
        context = getApplicationContext();
    }
}
