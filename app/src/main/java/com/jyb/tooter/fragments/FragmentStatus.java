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
import com.jyb.tooter.adapter.RecycleStatusAdapter;
import com.jyb.tooter.entity.Status;
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
public class FragmentStatus extends Fragment {

    private static String TAG = "DEBUG_FragmentHome";

    SmartRefreshLayout mRefresh;

    RecyclerView mRecycle;

    LinearLayout mLayout;

    public final int mMaxDisplayCount;
    public final int mMaxResponseCount;
    private final int mMaxLimit;

    private BaseActivity mBaseActivity;
    private ArrayList<Status> mData;
    private RecycleStatusAdapter mRecycleAdapter;

    private StatusType mCallType;
    private Call<List<Status>> mHttpCall;

    @SuppressLint("HandlerLeak")
    public FragmentStatus(BaseActivity activity, StatusType type) {
        super();
        mBaseActivity = activity;
        mData = new ArrayList<>();
//        mStartDisplayCount = 200;
        mMaxDisplayCount = 200;
        mMaxResponseCount = 20;
        mMaxLimit = 40;
        mCallType = type;
        mHttpCall = null;
    }

    public BaseActivity getBaseActivity() {
        return mBaseActivity;
    }

    public ArrayList<Status> getData() {
        return mData;
    }

    public SmartRefreshLayout getRefresh() {
        return mRefresh;
    }

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
            Pt.d("autoRefresh");
        }
    }

    @SuppressLint("WrongConstant")
    private void initRecycle() {

        mRecycleAdapter = new RecycleStatusAdapter(this);
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
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                Status status = mData.size() == 0 ? null : mData.get(0);
                String id = status == null ? null : status.id;

                Pt.d("first api id:" + id);
                mHttpCall = null;

                switch (mCallType) {
                    case HOME:
                        mHttpCall = mBaseActivity
                                .getMastApi()
                                .timelineHome(false, false, null, id, mMaxLimit);
                        break;
                    case LOCAL:
                        mHttpCall = mBaseActivity
                                .getMastApi()
                                .timelinePublic(true, null, id, mMaxLimit);
                        break;
                    case PUBLIC:
                        mHttpCall = mBaseActivity
                                .getMastApi()
                                .timelinePublic(false, null, id, mMaxLimit);
                        break;
                }
                updateHeader();
            }
        });

        mRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {

                Status status = mData.size() == 0 ? null : mData.get(mData.size() - 1);
                String id = status == null ? null : status.id;

                mHttpCall = null;

                switch (mCallType) {
                    case HOME:
                        mHttpCall = mBaseActivity
                                .getMastApi()
                                .timelineHome(false, false, id, null, mMaxResponseCount);
                        break;
                    case LOCAL:
                        mHttpCall = mBaseActivity
                                .getMastApi()
                                .timelinePublic(true, id, null, mMaxResponseCount);
                        break;
                    case PUBLIC:
                        mHttpCall = mBaseActivity
                                .getMastApi()
                                .timelinePublic(false, id, null, mMaxResponseCount);
                        break;
                }

                updateLast();
            }
        });
    }

    private void updateHeader() {

        Job job = new Job() {

            private Response<List<Status>> sResponse;

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

                    List<Status> list = sResponse.body();
                    List<Status> nlist = new ArrayList<>();

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

                        Status ofirst = mData.get(0);

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
                    Pt.d(mCallType + " onResponse");
                } else {
                    Toast.makeText(mBaseActivity, mBaseActivity.getString(R.string.network_request_timeout)
                            , Toast.LENGTH_SHORT).show();
                    Pt.d(mCallType + " unResponse");
                }
                mRefresh.finishRefresh(true);
            }

            @Override
            public void onTimeout() {
                super.onTimeout();
                Toast.makeText(mBaseActivity, mBaseActivity.getString(R.string.network_request_timeout)
                        , Toast.LENGTH_SHORT).show();
                Pt.d(mCallType + " onTimeout");
                mRefresh.finishRefresh(false);
            }

        };

        JobManager.get()
                .add(job);
    }

    private void updateLast() {

        Job job = new Job() {

            private Response<List<Status>> sResponse;

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
                    List<Status> list = sResponse.body();

//                    Pt.d("start list size:" + list.size());
//                    Pt.d("start mData size:" + mData.size());
//
//                    for (int i = 0; i < list.size(); i++) {
//                        Pt.d("start list item:" + list.get(i).id);
//                    }
//                    for (int i = 0; i < mData.size(); i++) {
//                        Pt.d("start mData item:" + mData.get(i).id);
//                    }

                    int pos = 0;
                    boolean clear = false;

                    if (mData.isEmpty()) {

                        mData.addAll(list);

                    } else {

                        Status olast = mData.get(mData.size() - 1);

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
//                        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecycle.getLayoutManager();
//                        layoutManager.scrollToPositionWithOffset(0,0);
//                        Pt.d(mCallType + " clear");
//                    }

                    Pt.d(mCallType + " onResponse");
                } else {
                    Toast.makeText(mBaseActivity, getString(R.string.network_request_timeout)
                            , Toast.LENGTH_SHORT).show();
                    Pt.d(mCallType + " unResponse");
                }
                mRefresh.finishLoadMore(true);
            }

            @Override
            public void onTimeout() {
                super.onTimeout();
                Toast.makeText(mBaseActivity, getString(R.string.network_request_timeout)
                        , Toast.LENGTH_SHORT).show();
                Pt.d(mCallType + " onTimeout");
                mRefresh.finishLoadMore(false);
            }

        };

        JobManager.get()
                .add(job);

    }

    public enum StatusType {
        HOME,
        LOCAL,
        PUBLIC
    }
}
