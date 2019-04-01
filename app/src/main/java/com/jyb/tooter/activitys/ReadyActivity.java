package com.jyb.tooter.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.jyb.tooter.R;
import com.jyb.tooter.statics.SharedVar;
import com.jyb.tooter.utils.Pt;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import io.reactivex.functions.Consumer;

@SuppressLint("CheckResult")
public class ReadyActivity extends FragmentActivity {

    private static String TAG = "DEBUG_ReadyActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.INTERNET
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.ACCESS_NETWORK_STATE
                )
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            Pt.d("RxPermissions true");
                            initFile();
                            entry();
//                            testEntry();
                        } else {
                            Pt.d("RxPermissions false");
                        }
                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    void entry() {

        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        shared.edit()
                .remove(SharedVar.DOMAIN)
                .remove(SharedVar.CLIENT_ID)
                .remove(SharedVar.CLIENT_SECRET)
                .remove(SharedVar.TOKEN)
                .remove(SharedVar.REMEMBER)
                .apply();
//        User user;
//        user = new User();
//        user.domain = "";
//        user.token = "";
//        user.domain = "https://cmx.social";
//        user.token = "7781a506f7e950518bd684d30594fc2c069c35fbb50beea71e9f8547ed207196";
//        user = saveStoreUser(user);
//        user = readStoreUser();
//
//        Intent intent;
//        if (user != null) {
//            shared = PreferenceManager.getDefaultSharedPreferences(this);
//            SharedPreferences.Editor editor = shared.edit();
//            editor.putString(SharedVar.DOMAIN, user.domain);
//            editor.putString(SharedVar.TOKEN, String.valueOf(user.token));
//            editor.apply();
//            intent = new Intent(this, MainActivity.class);
//        } else {
//        }
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void initFile() {

        File fileRoot = new File(SharedVar.FILE_ROOT);
        if (!fileRoot.exists()) {
            fileRoot.mkdir();
            Pt.d("crate mkdirs " + SharedVar.FILE_ROOT);
        } else {
            Pt.d("crate mkdirs " + SharedVar.FILE_ROOT + "exists");
        }

        String fstrMedia = SharedVar.FILE_ROOT + SharedVar.FILE_MEDIA;
        File fileMedia = new File(fstrMedia);
        if (!fileMedia.exists()) {
            fileMedia.mkdir();
            Pt.d("crate mkdirs " + fstrMedia);
        } else {
            Pt.d("crate mkdirs " + fstrMedia + "exists");
        }

        String fstrAudio = fstrMedia + SharedVar.FILE_AUDIO;
        File fileAudio = new File(fstrAudio);
        if (!fileAudio.exists()) {
            fileAudio.mkdir();
            Pt.d("crate mkdirs " + fstrAudio);
        } else {
            Pt.d("crate mkdirs " + fstrAudio + "exists");
        }
    }
}
