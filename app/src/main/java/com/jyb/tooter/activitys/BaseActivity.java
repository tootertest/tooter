/* Copyright 2017 Andrew Dawson
 *
 * This file is a part of Tusky.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * Tusky is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Tusky; if not,
 * see <http://www.gnu.org/licenses>. */

package com.jyb.tooter.activitys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jyb.tooter.R;
import com.jyb.tooter.nerwork.MastodonAPI;
import com.jyb.tooter.nerwork.TooterAPI;
import com.jyb.tooter.statics.SharedVar;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    private static String TAG = "DEBUG_BaseActivity";

    private MastodonAPI mMastApi;
    private TooterAPI mTooterAPI;

    protected SharedPreferences mShared = null;

    private Dispatcher mDispatcher;


    public MastodonAPI getMastApi() {
        return mMastApi;
    }

    public TooterAPI getTooterAPI() {
        return mTooterAPI;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mShared = PreferenceManager.getDefaultSharedPreferences(this);
        initMastodonApi();
//        initTooterApi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    private void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    protected String getBaseApiUrl() {
        return mShared.getString(SharedVar.DOMAIN, null);
    }

    protected String getTooterApiUrl() {
        return getString(R.string.tooter_api_url);
    }

    protected String getAccessToken() {
        String token = mShared.getString(SharedVar.TOKEN, null);
        return token;
    }

    protected void initMastodonApi() {

        mDispatcher = new Dispatcher();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        Request originalRequest = chain.request();

                        Request.Builder builder = originalRequest.newBuilder();
                        String accessToken = getAccessToken();
                        if (accessToken != null) {
                            builder.header("Authorization", String.format("Bearer %s", accessToken));
                        }
                        Request newRequest = builder.build();

                        return chain.proceed(newRequest);
                    }
                })
                .dispatcher(mDispatcher)
                .build();

        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(Spanned.class, new SpannedTypeAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseApiUrl())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mMastApi = retrofit.create(MastodonAPI.class);
    }

    protected void initTooterApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getTooterApiUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mTooterAPI = retrofit.create(TooterAPI.class);
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        String domain = mShared.getString(SharedVar.DOMAIN,null);
//        String token = mShared.getString(SharedVar.TOKEN,null);
//
//        mShared.edit()
//                .putString(SharedVar.DOMAIN, domain)
//                .putString(SharedVar.TOKEN, token)
//                .apply();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//
//        String domain = mShared.getString(SharedVar.DOMAIN,null);
//        String token = mShared.getString(SharedVar.TOKEN,null);
//
//        outState.putString(SharedVar.DOMAIN, domain);
//        outState.putString(SharedVar.TOKEN, token);
//        super.onSaveInstanceState(outState);
//    }

//    protected void redirectIfNotLoggedIn() {
//        SharedPreferences preferences = getPrivatePreferences();
//        String domain = preferences.getString("domain", null);
//        String accessToken = preferences.getString("accessToken", null);
//        if (domain == null || accessToken == null) {
//            Intent intent = new Intent(this, LoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        TypedValue value = new TypedValue();
//        int color;
//        if (getTheme().resolveAttribute(R.attr.toolbar_icon_tint, value, true)) {
//            color = value.data;
//        } else {
//            color = Color.WHITE;
//        }
//        for (int i = 0; i < menu.size(); i++) {
//            Drawable icon = menu.getItem(i).getIcon();
//            if (icon != null) {
//                icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
//            }
//        }
//        return super.onCreateOptionsMenu(menu);
//    }

//    protected void enablePushNotifications() {
//        // Start up the PullNotificationService on a repeating interval.
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String minutesString = preferences.getString("pullNotificationCheckInterval", "15");
//        long minutes = Long.valueOf(minutesString);
//        long checkInterval = 1000 * 60 * minutes;
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, PullNotificationService.class);
//        PendingIntent serviceAlarmIntent = PendingIntent.getService(this, SERVICE_REQUEST_CODE,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime(), checkInterval, serviceAlarmIntent);
//    }
//
//    protected void disablePushNotifications() {
//        // Cancel the repeating call for "pull" notifications.
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, PullNotificationService.class);
//        PendingIntent serviceAlarmIntent = PendingIntent.getService(this, SERVICE_REQUEST_CODE,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmManager.cancel(serviceAlarmIntent);
//    }
//
//    protected void clearNotifications() {
//        SharedPreferences notificationPreferences = getApplicationContext()
//                .getSharedPreferences("Notifications", MODE_PRIVATE);
//        notificationPreferences.edit()
//                .putString("current", "[]")
//                .apply();
//
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        manager.cancel(PullNotificationService.NOTIFY_ID);
//    }
//
//    protected void setPullNotificationCheckInterval(long minutes) {
//        long checkInterval = 1000 * 60 * minutes;
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, PullNotificationService.class);
//        PendingIntent serviceAlarmIntent = PendingIntent.getService(this, SERVICE_REQUEST_CODE,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmManager.cancel(serviceAlarmIntent);
//        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime(), checkInterval, serviceAlarmIntent);
//    }
}
