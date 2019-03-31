package com.jyb.tooter.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jyb.tooter.job.maneger.JobManager;
import com.jyb.tooter.statics.SharedVar;

import java.util.concurrent.atomic.AtomicBoolean;

public class Job {

    private int mId = JobManager.instance().newId();
    private String mTag = null;
    private JobStatus mStatus = null;

    private long mTimeStrat = 0;
    private long mTimeout = SharedVar.RESPONSE_TIME_OUT;

    private AtomicBoolean mLock = new AtomicBoolean(false);

    public Job() {

    }

    public Job(int timeout) {
        mTimeout = timeout;
    }

    public AtomicBoolean getLock() {
        return mLock;
    }

    public int getId() {
        return mId;
    }

    public @Nullable
    String getTag() {
        return mTag;
    }

    public @NonNull
    JobStatus getStatus() {
        return mStatus;
    }

    public long getTimeout() {
        return mTimeout;
    }

    public long getTimeStrat() {
        return mTimeStrat;
    }

    public Job setTag(String tag) {
        mTag = tag;
        return this;
    }

    public Job setTimeout(int timeout) {
        mTimeout = timeout;
        return this;
    }

    public void onStart() {
        mTimeStrat = System.currentTimeMillis();
        mStatus = JobStatus.START;
    }

    public void onSend() {
        mTimeStrat = System.currentTimeMillis();
        mStatus = JobStatus.SEND;
    }

    public void onReceive() {
        mStatus = JobStatus.RECEIVE;
    }

    public void onTimeout() {
        mStatus = JobStatus.TIMEOUT;
    }

    public void lock() {
        getLock().set(true);
    }

}

enum JobStatus {
    START,
    SEND,
    RECEIVE,
    TIMEOUT
}