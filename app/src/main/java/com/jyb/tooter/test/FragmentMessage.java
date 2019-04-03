//package com.jyb.tooter.test;
//
//import android.annotation.SuppressLint;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.Toast;
//
//import com.jyb.tooter.R;
//import com.jyb.tooter.activitys.BaseActivity;
//import com.jyb.tooter.adapter.RecycleStatusAdapter;
//import com.jyb.tooter.entity.Notification;
//import com.jyb.tooter.entity.Status;
//import com.jyb.tooter.interfaces.Message;
//import com.jyb.tooter.job.Job;
//import com.jyb.tooter.job.maneger.JobManager;
//import com.jyb.tooter.utils.Pt;
//import com.scwang.smartrefresh.header.MaterialHeader;
//import com.scwang.smartrefresh.layout.SmartRefreshLayout;
//import com.scwang.smartrefresh.layout.api.RefreshLayout;
//import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
//import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
//import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
//import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import io.reactivex.annotations.NonNull;
//import retrofit2.Call;
//import retrofit2.Response;
//
//@SuppressLint("ValidFragment")
//public class FragmentMessage extends Fragment {
//
//    SmartRefreshLayout mRefresh;
//
//    RecyclerView mRecycle;
//
//    LinearLayout mLayout;
//
//    public final int mStartDisplayCount;
//    public final int mMaxDisplayCount;
//    public final int mResponseCount;
//    private final int mMaxLimit;
//
//    private BaseActivity mBaseActivity;
//    private ArrayList<Message> mData;
//    private RecycleStatusAdapter mRecycleAdapter;
//
//    private MessageType mCallType;
//    private Call<List<Notification>> mNotificationCall;
//    private Call<List<Status>> mStatusCall;
//
//    public FragmentMessage(BaseActivity activity, MessageType type) {
//        super();
//        mBaseActivity = activity;
//        mData = new ArrayList<>();
//        mMaxDisplayCount = 200;
//        mStartDisplayCount = 20;
//        mResponseCount = 10;
//        mMaxLimit = 40;
//        mCallType = type;
//        mNotificationCall = null;
//        mStatusCall = null;
//    }
//
//    public BaseActivity getBaseActivity() {
//        return mBaseActivity;
//    }
//
//    public ArrayList<Message> getData() {
//        return mData;
//    }
//
//    public SmartRefreshLayout getRefresh() {
//        return mRefresh;
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
//
//        View view = inflater.inflate(R.layout.fragment_recycle, container, false);
//
//        mRefresh = view.findViewById(R.id.refresh);
//        mRecycle = view.findViewById(R.id.recycle);
//        mLayout = view.findViewById(R.id.layout_refresh_recycle);
//
//        initRecycle();
//        initRefresh();
//        return view;
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (mData.size() == 0) {
//            mRefresh.autoRefresh();
//            Pt.d("autoRefresh");
//        }
//    }
//
//    @SuppressLint("WrongConstant")
//    private void initRecycle() {
//
//        mRecycleAdapter = new RecycleStatusAdapter(this);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//
//        mRecycle.setLayoutManager(layoutManager);
//        mRecycle.setAdapter(mRecycleAdapter);
//        mRecycle.setItemAnimator(null);
//    }
//
//    private void initRefresh() {
//        mRefresh.setRefreshHeader(new MaterialHeader(mBaseActivity));
//        mRefresh.setRefreshFooter(new BallPulseFooter(mBaseActivity)
//                .setSpinnerStyle(SpinnerStyle.Scale)
//                .setAnimatingColor(Color.LTGRAY));
//
//        mRefresh.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                Message message = mData.size() == 0 ? null : mData.get(mData.size() - 1);
//                String id = message == null ? null : message.id;
//
//                Pt.d("first api id:" + id);
//                mNotificationCall = null;
//                mStatusCall = null;
//
//                switch (mCallType) {
//                    case HOME:
//                        mStatusCall = mBaseActivity
//                                .getMastApi()
//                                .timelineHome(false, false, null, id, mMaxLimit);
//                        break;
//                    case LOCAL:
//                        mStatusCall = mBaseActivity
//                                .getMastApi()
//                                .timelinePublic(true, null, id, mMaxLimit);
//                        break;
//                    case PUBLIC:
//                        mStatusCall = mBaseActivity
//                                .getMastApi()
//                                .timelinePublic(false, null, id, mMaxLimit);
//                        break;
//                    case NOTFICATIONS:
//                        mNotificationCall = mBaseActivity
//                                .getMastApi()
//                                .notifications(null, id, mMaxLimit);
//                        break;
//                }
//                updateHeader();
//            }
//        });
//
//        mRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(RefreshLayout refreshLayout) {
//
//                Message message = mData.size() == 0 ? null : mData.get(0);
//                String id = message == null ? null : message.id;
//
//                mStatusCall = null;
//
//                switch (mCallType) {
//                    case HOME:
//                        mStatusCall = mBaseActivity
//                                .getMastApi()
//                                .timelineHome(false, false, id, null, mResponseCount);
//                        break;
//                    case LOCAL:
//                        mStatusCall = mBaseActivity
//                                .getMastApi()
//                                .timelinePublic(true, id, null, mResponseCount);
//                        break;
//                    case PUBLIC:
//                        mStatusCall = mBaseActivity
//                                .getMastApi()
//                                .timelinePublic(false, id, null, mResponseCount);
//                        break;
//                    case NOTFICATIONS:
//                        mNotificationCall = mBaseActivity
//                                .getMastApi()
//                                .notifications(id, null, mResponseCount);
//                        break;
//                }
//
//                updateLast();
//            }
//        });
//    }
//
//    private void updateHeader() {
//
//        Job job = new Job() {
//
//            private Response<List<Status>> sStatusResponse;
//            private Response<List<Notification>> sNotificationResponse;
//
//            @Override
//            public void onStart() {
//                super.onStart();
//            }
//
//            @Override
//            public void onSend() {
//                super.onSend();
//                try {
//                    if (mCallType == MessageType.NOTFICATIONS) {
//                        sNotificationResponse = mNotificationCall.execute();
//                    } else {
//
//                        sStatusResponse = mStatusCall.execute();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onReceive() {
//                super.onReceive();
//                if (sNotificationResponse != null && sNotificationResponse.isSuccessful() ||
//                        sStatusResponse != null && sStatusResponse.isSuccessful()) {
//
//                    List<?> list = sNotificationResponse.body();
//                    List<Message> messages = (List<Message>) list;
//
//                    if (messages.size() > mMaxDisplayCount) {
//                        mData.clear();
//                    }
//                    mData.addAll(0,messages);
//
//                    mRecycleAdapter.notifyDataSetChanged();
//                    Pt.d(mCallType + " onResponse");
//                } else {
//                    Toast.makeText(mBaseActivity, mBaseActivity.getString(R.string.network_request_timeout)
//                            , Toast.LENGTH_SHORT).show();
//                    Pt.d(mCallType + " unResponse");
//                }
//                mRefresh.finishRefresh(true);
//            }
//
//            @Override
//            public void onTimeout() {
//                super.onTimeout();
//                Toast.makeText(mBaseActivity, mBaseActivity.getString(R.string.network_request_timeout)
//                        , Toast.LENGTH_SHORT).show();
//                Pt.d(mCallType + " onTimeout");
//                mRefresh.finishRefresh(false);
//            }
//
//        };
//
//        JobManager.get()
//                .add(job);
//    }
//
//    private void updateLast() {
//
//        Job job = new Job() {
//
//            private Response<List<Status>> sStatusResponse;
//            private Response<List<Notification>> sNotificationResponse;
//
//            @Override
//            public void onStart() {
//                super.onStart();
//            }
//
//            @Override
//            public void onSend() {
//                super.onSend();
//                try {
//                    if (mCallType == MessageType.NOTFICATIONS) {
//                        sNotificationResponse = mNotificationCall.execute();
//                    } else {
//
//                        sStatusResponse = mStatusCall.execute();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onReceive() {
//                super.onReceive();
//                if (sNotificationResponse != null && sNotificationResponse.isSuccessful() ||
//                        sStatusResponse != null && sStatusResponse.isSuccessful()) {
//
//                    List<?> list = sNotificationResponse.body();
//                    List<Message> messages = (List<Message>) list;
//
//                    if (messages.size() > mMaxDisplayCount) {
//                        mData.clear();
//                    }
//                    mData.addAll(0,messages);
//
//                    mRecycleAdapter.notifyDataSetChanged();
//                    Pt.d(mCallType + " onResponse");
//                } else {
//                    Toast.makeText(mBaseActivity, mBaseActivity.getString(R.string.network_request_timeout)
//                            , Toast.LENGTH_SHORT).show();
//                    Pt.d(mCallType + " unResponse");
//                }
//                mRefresh.finishRefresh(true);
//            }
//
//            @Override
//            public void onTimeout() {
//                super.onTimeout();
//                Toast.makeText(mBaseActivity, getString(R.string.network_request_timeout)
//                        , Toast.LENGTH_SHORT).show();
//                Pt.d(mCallType + " onTimeout");
//                mRefresh.finishLoadMore(false);
//            }
//
//        };
//
//        JobManager.get()
//                .add(job);
//
//    }
//
//    public enum MessageType {
//        HOME,
//        NOTFICATIONS,
//        LOCAL,
//        PUBLIC
//    }
//}
