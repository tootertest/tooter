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
    private final int mMaxLimit;

    private Call<List<Notification>> mHttpCall;

    @SuppressLint("HandlerLeak")
    public FragmentNotfications(BaseActivity baseActivity) {
        super();
        mBaseActivity = baseActivity;
        mData = new ArrayList<>();
        mMaxDisplayCount = 200;
        mMaxResponseCount = 20;
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

                Notification notification = mData.size() == 0 ? null : mData.get(0);
                String id = notification == null ? null : notification.id;

                mHttpCall = null;

                mHttpCall = mBaseActivity
                        .getMastApi()
                        .notifications(null, id, mMaxLimit);

                updateHeader();
            }
        });

        mRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {

                Notification notification = mData.size() == 0 ? null : mData.get(mData.size() - 1);
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

                    if (list.size() > mMaxResponseCount) {

                        for (int i = 0; i < mMaxResponseCount; i++) {
                            nlist.add(0, list.get(list.size() - 1 - i));
                        }

                    } else {

                        nlist.addAll(0, list);

                    }

//                    Pt.d("start list size:" + list.size());
//                    Pt.d("start nlist size:" + nlist.size());
//                    Pt.d("start mData size:" + mData.size());
//
//                    for (int i = 0; i < list.size(); i++) {
//                        Pt.d("start list item:" + list.get(i).id);
//                    }
//                    for (int i = 0; i < nlist.size(); i++) {
//                        Pt.d("start nlist item:" + nlist.get(i).id);
//                    }
//                    for (int i = 0; i < mData.size(); i++) {
//                        Pt.d("start mData item:" + mData.get(i).id);
//                    }

                    int pos = 0;

                    if (mData.isEmpty()) {

//                        mData.addAll(nlist);
                        mData.addAll(list);

                    } else {

                        Notification ofirst = mData.get(0);

                        if (mData.size() > mMaxDisplayCount) {

                            mData.clear();

                            if (nlist.size() >= mMaxDisplayCount) {
                                nlist.remove(0);
                            }
                            nlist.add(ofirst);

                        }

                        mData.addAll(0, nlist);

                        for (int i = 0; i < mData.size(); i++) {
                            if (ofirst.id.equals(mData.get(i).id)) {
                                pos = i;
                            }
                        }
                    }

                    mRecycle.scrollToPosition(pos);

//                    for (int i = 0; i < mData.size(); i++) {
//                        Pt.d("end mData item:" + mData.get(i).id);
//                    }
//                    Pt.d("end mData size:" + mData.size());

                    mRecycleAdapter.notifyDataSetChanged();
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
                    boolean clear = false;

                    if (mData.isEmpty()) {

                        mData.addAll(list);

                    } else {

                        Notification olast = mData.get(mData.size() - 1);

                        if (mData.size() > mMaxDisplayCount) {

                            mData.clear();
                            clear = true;

                            if (list.size() >= mMaxDisplayCount) {
                                list.remove(list.size() - 1);
                            }
                            list.add(0, olast);

                        }

                        mData.addAll(list);

                        for (int i = 0; i < mData.size(); i++) {
                            if (olast.id.equals(mData.get(i).id)) {
                                pos = i;
                            }
                        }
                    }

                    mRecycleAdapter.notifyDataSetChanged();

                    mRecycle.scrollToPosition(pos);
//                    if (clear) {
////                        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecycle.getLayoutManager();
////                        layoutManager.scrollToPositionWithOffset(0,0);
//                        Pt.d(" clear");
//                    }

                    Pt.d(" onResponse");
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
