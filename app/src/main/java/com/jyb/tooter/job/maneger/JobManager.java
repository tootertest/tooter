package com.jyb.tooter.job.maneger;

import android.support.annotation.NonNull;

import com.jyb.tooter.job.Job;

import java.util.concurrent.LinkedBlockingQueue;

public class JobManager {

    private static JobManager mInstance = new JobManager();

    public static JobManager get() {
        return mInstance;
    }

    private int mSeed = 0;
    private LinkedBlockingQueue<Job> mJobs = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Job> mJobsAnsyc = new LinkedBlockingQueue<>();

    private JobRunThread mRunThread;
    private JobRunAnsyc mRunAnsyc;

    private JobManager() {
        mRunThread = new JobRunThread(mJobs);
        mRunThread.start();
        mRunAnsyc = new JobRunAnsyc(mJobsAnsyc);
        mRunAnsyc.start();
    }

    public void add(@NonNull Job job) {
        mJobs.add(job);
    }

    public void remove(@NonNull Job job) {
        mJobs.remove(job);
    }

    public void remove(@NonNull String tag) {
        for (Job job : mJobs) {
            if (job.getTag() == null) {
                return;
            }
            if (job.getTag().equals(tag)) {
                mJobs.remove(job);
            }
        }
    }

    public void remove(int id) {
        for (Job job : mJobs) {
            if (job.getId() == id) {
                mJobs.remove(job);
            }
        }
    }

    public void addAnsyc(@NonNull Job job) {
        mJobsAnsyc.add(job);
    }

    public void removeAnsyc(@NonNull Job job) {
        mJobsAnsyc.remove(job);
    }

    public void removeAnsyc(@NonNull String tag) {
        for (Job job : mJobsAnsyc) {
            if (job.getTag() == null) {
                return;
            }
            if (job.getTag().equals(tag)) {
                mJobsAnsyc.remove(job);
            }
        }
    }

    public void removeAnsyc(int id) {
        for (Job job : mJobsAnsyc) {
            if (job.getId() == id) {
                mJobsAnsyc.remove(job);
            }
        }
    }

    public void run() {
        mRunThread.safeRun();
    }

    public void stop() {
        mRunThread.safeStop();
    }

    public int newId() {
        return mSeed++;
    }

    void debug() {
    }
}
