package com.jyb.tooter.job.maneger;

import com.jyb.tooter.job.Job;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class JobRunThread extends Thread {
    LinkedBlockingQueue<Job> mJobs;


    private boolean mRuning = true;
    private AtomicBoolean mBlock = new AtomicBoolean(false);

    public boolean getIsRun() {
        return this.mRuning;
    }
    private AtomicBoolean mStart = new AtomicBoolean(false);

    public JobRunThread(LinkedBlockingQueue<Job> jobs) {
        mJobs = jobs;
    }

    @Override
    public void run() {
        super.run();
        while (mRuning) {

            if (mBlock.get()) {
                continue;
            }
            mBlock.set(true);

            Job job = null;
            try {
                job = mJobs.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            final Job fjob = job;
//            fjob.onStart();
            Observable.create(new ObservableOnSubscribe<Integer>() {

                @Override
                public void subscribe(final ObservableEmitter<Integer> emitter) throws Exception {
                    emitter.onNext(0);
                    while (!mStart.get()){}
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            fjob.onSend();
                            if (!fjob.getLock().get()) {
                                fjob.getLock().set(true);;
                                emitter.onNext(1);
                            }
                        }
                    }).start();
                    while (fjob.getTimeout() > System.currentTimeMillis() - fjob.getTimeStrat()
                            && !fjob.getLock().get()) {
                    }

//                    Pt.d("time: " + (System.currentTimeMillis() - fjob.getTimeStrat()));
//                    Pt.d("lock: " + (!fjob.getLock().get()));

                    if (!fjob.getLock().get()) {
                        fjob.getLock().set(true);;
                        emitter.onNext(2);
                    }
                }
            })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Integer o) {
                            switch (o) {
                                case 0:
                                    fjob.onStart();
                                    mStart.set(true);
                                    break;
                                case 1:
                                    fjob.onReceive();
                                    mStart.set(false);
                                    mBlock.set(false);
                                    break;
                                case 2:
                                    fjob.onTimeout();
                                    mStart.set(false);
                                    mBlock.set(false);
                                    break;
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    public void safeRun() {
        mRuning = true;
    }

    public void safeStop() {
        mRuning = false;
    }
}
