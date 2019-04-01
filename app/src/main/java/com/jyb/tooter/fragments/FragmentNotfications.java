package com.jyb.tooter.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jyb.tooter.R;
import com.jyb.tooter.activitys.BaseActivity;
import com.jyb.tooter.adapter.RecycleNotficationAdapter;
import com.jyb.tooter.entity.Notification;
import com.jyb.tooter.job.Job;
import com.jyb.tooter.job.maneger.JobManager;
import com.jyb.tooter.utils.Pt;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import retrofit2.Call;
import retrofit2.Response;

@SuppressLint("ValidFragment")
public class FragmentNotfications extends Fragment {

    private static String TAG = "FragmentNotfications";

    SmartRefreshLayout mRefresh;

    RecyclerView mRecycle;

    LinearLayout mLayout;

    private RecycleNotficationAdapter mRecycleAdapter;

    private ArrayList<Notification> mData;
    private BaseActivity mBaseActivity;

    private final int mMaxDisplayCount;
    private final int mMaxResponseCount;
    private final int mNewMaxResponseCount;
    private final int mMaxLimit;

    private Call<List<Notification>> mHttpCall;

    @SuppressLint("HandlerLeak")
    public FragmentNotfications(BaseActivity baseActivity) {
        super();
        mBaseActivity = baseActivity;
        mData = new ArrayList<>();
        mMaxDisplayCount = 10;
        mNewMaxResponseCount = 5;
        mMaxResponseCount = mMaxDisplayCount + mNewMaxResponseCount;
        mMaxLimit = 40;
    }

    public ArrayList<Notification> getData() {
        return mData;
    }

    public BaseActivity getBaseActivity() {
        return mBaseActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_recycle, container, false);

        mRefresh = view.findViewById(R.id.refresh);
        mRecycle = view.findViewById(R.id.recycle);
        mLayout = view.findViewById(R.id.layout_refresh_recycle);

        initRecycle();
        initRefresh();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mData.size() == 0) {
            mRefresh.autoRefresh();
        }
    }

    @SuppressLint("WrongConstant")
    private void initRecycle() {

        mRecycleAdapter = new RecycleNotficationAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecycle.setLayoutManager(layoutManager);
        mRecycle.setAdapter(mRecycleAdapter);
        mRecycle.setItemAnimator(null);
    }

    private void initRefresh() {
        mRefresh.setRefreshHeader(new MaterialHeader(mBaseActivity));
        mRefresh.setRefreshFooter(new BallPulseFooter(mBaseActivity)
                .setSpinnerStyle(SpinnerStyle.Scale)
                .setAnimatingColor(Color.LTGRAY));

        mRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {

                Notification notification = mData.size() == 0 ? null : mData.get(mData.size()-1);
                String id = notification == null ? null : notification.id;

                mHttpCall = null;

                mHttpCall = mBaseActivity
                        .getMastApi()
                        .notifications(null, id, mMaxResponseCount);

                updateHeader();
            }
        });

        mRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {

                Notification notification = mData.size() == 0 ? null : mData.get(0);
                String id = notification == null ? null : notification.id;

                mHttpCall = null;

                mHttpCall = mBaseActivity
                        .getMastApi()
                        .notifications(id, null, mMaxResponseCount);

                updateLast();
            }
        });
    }

    public void updateHeader() {

        Job job = new Job() {

            private Response<List<Notification>> sResponse;

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSend() {
                super.onSend();
                try {
                    sResponse = mHttpCall.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onReceive() {
                super.onReceive();
                if (sResponse != null && sResponse.isSuccessful()) {
                    List<Notification> list = sResponse.body();
                    List<Notification> nlist = new ArrayList<>();

                    int limit = 0;
                    if (list.size() < mMaxResponseCount) {
                        limit = list.size();
                    } else {
                        limit = mMaxResponseCount;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        Pt.d("source item id:" + list.get(i).id);
                    }
                    for (int i = 0; i < limit; i++) {
                        nlist.add(0, list.get(list.size() - 1 - i));
                    }

                    int pos = 0;

                    if (mData.isEmpty()) {
                        if (list.size() > mMaxDisplayCount) {

                            for (int i = 0; i < mMaxDisplayCount; i++) {
                                mData.add(list.get(i));
                            }

                        } else {
                            mData.addAll(list);
                        }
                        for (int i = 0; i < list.size(); i++) {
                            Pt.d("item id:" + list.get(i).id);
                        }
                        for (int i = 0; i < mData.size(); i++) {
                            Pt.d("mData id:" + mData.get(i).id);
                        }
                    } else {

                        Notification reff = mData.get(0);
                        Notification refl = mData.get(mData.size() - 1);
                        nlist.add(refl);
                        if (nlist.size() > mMaxResponseCount) {
                            nlist.remove(nlist.get(0));
                        }
                        Pt.d("reff id:" + reff.id);
                        Pt.d("refl id:" + refl.id);
                        for (int i = 0; i < nlist.size(); i++) {
                            Pt.d("item id:" + nlist.get(i).id);
                        }

                        mData.clear();

                        if (nlist.size() > mMaxDisplayCount) {
                            for (int i = 0; i < mMaxDisplayCount; i++) {
                                Notification notification = nlist.get(i);
                                mData.add(notification);
                                if (reff.id.equals(notification.id)) {
                                    pos = i;
                                }
                                Pt.d("mData id:" + notification.id);
                            }
                        } else {
                            mData.addAll(nlist);
                            for (int i = 0; i < nlist.size(); i++) {
                                Notification notification = nlist.get(i);
                                if (reff.id.equals(notification.id)) {
                                    pos = i;
                                }
                                Pt.d("mData id:" + notification.id);
                            }
                        }
                    }

                    mRecycleAdapter.notifyDataSetChanged();
                    mRecycle.scrollToPosition(pos);
                    Pt.d("add nlist size:" + nlist.size());
                    Pt.d("pos:" + pos);
                    Pt.d("NOTIFICATION onResponse");
                } else {
                    Toast.makeText(mBaseActivity, mBaseActivity.getString(R.string.network_request_timeout)
                            , Toast.LENGTH_SHORT).show();
                    Pt.d("NOTIFICATION unResponse");
                }
                mRefresh.finishRefresh(true);
            }

            @Override
            public void onTimeout() {
                super.onTimeout();
                Toast.makeText(mBaseActivity, mBaseActivity.getString(R.string.network_request_timeout)
                        , Toast.LENGTH_SHORT).show();
                Pt.d("NOTIFICATION onTimeout");
                mRefresh.finishRefresh(false);
            }

        };

        JobManager.get()
                .add(job);
    }

    public void updateLast() {

        Job job = new Job() {

            private Response<List<Notification>> sResponse;

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSend() {
                super.onSend();
                try {
                    sResponse = mHttpCall.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onReceive() {
                super.onReceive();
                if (sResponse != null && sResponse.isSuccessful()) {
                    List<Notification> list = sResponse.body();

                    int pos = 0;

                    if (mData.isEmpty()){
                        if (list.size() > mMaxDisplayCount) {

                            for (int i = 0; i < mMaxDisplayCount; i++) {
                                mData.add(list.get(i));
                            }

                        } else {
                            mData.addAll(list);
                        }
                    }else {

                        Notification reff = mData.get(0);
                        Notification refl = mData.get(mData.size() - 1);
                        list.add(0, reff);
                        list.remove(list.size() - 1);
                        Pt.d("reff id:" + reff.id);
                        Pt.d("refl id:" + refl.id);

                        mData.clear();
                        for (int i = 0; i < mMaxDisplayCount; i++) {
                            Notification notification = list.get(list.size() - 1 - i);
                            mData.add(0, notification);
                            if (refl.id.equals(notification.id)) {
                                pos = mMaxDisplayCount - 1 - i;
                            }
                        }

                        for (int i = 0; i < list.size(); i++) {
                            Pt.d("item id:" + list.get(i).id);
                        }
                        for (int i = 0; i < mData.size(); i++) {
                            Pt.d("mData id:" + mData.get(i).id);
                        }
                    }

                    mRecycleAdapter.notifyDataSetChanged();
                    mRecycle.scrollToPosition(pos);
                    Pt.d("add list size:" + list.size());
                    Pt.d("pos:" + pos);
                    Pt.d("NOTIFICATION onResponse");
                } else {
                    Toast.makeText(mBaseActivity, mBaseActivity.getString(R.string.network_request_timeout)
                            , Toast.LENGTH_SHORT).show();
                    Pt.d("NOTIFICATION unResponse");
                }
                mRefresh.finishLoadMore(true);
            }

            @Override
            public void onTimeout() {
                super.onTimeout();
                Toast.makeText(mBaseActivity, mBaseActivity.getString(R.string.network_request_timeout)
                        , Toast.LENGTH_SHORT).show();
                Pt.d("NOTIFICATION onTimeout");
                mRefresh.finishLoadMore(false);
            }

        };

        JobManager.get()
                .add(job);
    }

}
