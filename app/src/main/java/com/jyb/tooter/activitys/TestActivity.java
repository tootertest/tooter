package com.jyb.tooter.activitys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.jyb.tooter.R;
import com.jyb.tooter.dialog.DialogModalImage;

public class TestActivity extends FragmentActivity {

    private final static String TAG = "DEBUG_TestActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

//        for (int i = 0; i < 100; i++) {
//            Job job = new Job() {
//                @Override
//                public void onStart() {
//                    super.onStart();
//                    Pt.d(getId() + " onStart");
//                }
//
//                @Override
//                public void onSend() {
//                    super.onSend();
//                    Pt.d(getId() + " onSend");
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onReceive() {
//                    super.onReceive();
//                    Pt.d(getId() + " onReceive " + getTimeout());
//                }
//
//                @Override
//                public void onTimeout() {
//                    super.onTimeout();
//                    Pt.d(getId() + " onTimeout " + getTimeout());
//                }
//            };
//            double r = Math.random();
//            int mtime = (int) (Math.random() * 100);
//            if (r > .5) {
//                mtime = -mtime;
//            }
//            job.setTimeout(2000 + mtime);
//            JobManager.get()
//                    .addAnsyc(job);
//            JobManager.get()
//                    .add(job);
//        }

        FragmentManager manager = getSupportFragmentManager();
        DialogModalImage dialog = new DialogModalImage();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        dialog
                .setModal(true)
                .setMax(true)
                .setCleanBackground(true)
                .setLayout(R.layout.dialog_image);
        dialog.show(manager, null);

    }
}
