package com.jyb.tooter.test;

import com.jyb.tooter.job.Job;
import com.jyb.tooter.utils.Pt;

public class TestJob extends Job {

    @Override
    public void onStart() {
        super.onStart();
        Pt.d(getId() + " onStart");
    }

    @Override
    public void onSend() {
        super.onSend();
        Pt.d(getId() + " onSend");
    }

    @Override
    public void onReceive() {
        super.onReceive();
        Pt.d(getId() + " onReceive " + getTimeout());
    }

    @Override
    public void onTimeout() {
        super.onTimeout();
        Pt.d(getId() + " onTimeout " + getTimeout());
    }

}
