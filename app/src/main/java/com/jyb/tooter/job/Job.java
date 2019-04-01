package com.jyb.tooter.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jyb.tooter.job.maneger.JobManager;
import com.jyb.tooter.statics.SharedVar;

import java.util.concurrent.atomic.AtomicBoolean;

public class Job {

    private int mId;
    private String mTag;
    private JobStatus mStatus;

    private long mTimeStrat;
    private long mTimeout;

    private AtomicBoolean mLock;

    public Job() {
        init();
    }

    public Job(int timeout) {
        init();
        mTimeout = timeout;
    }

    private void init() {
        mId = JobManager.get().newId();
        mTag = null;
        mStatus = null;
        mTimeStrat = 0;
        mTimeout = SharedVar.RESPONSE_TIME_OUT;
        mLock = new AtomicBoolean(false);
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

    public Job reset() {
        init();
        return this;
    }

}

enum JobStatus {
    START,
    SEND,
    RECEIVE,
    TIMEOUT
}