package com.jyb.tooter.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.jyb.tooter.R;
import com.jyb.tooter.activitys.TootActivity;
import com.jyb.tooter.entity.Account;
import com.jyb.tooter.entity.Notification;
import com.jyb.tooter.entity.Status;
import com.jyb.tooter.fragments.FragmentNotfications;
import com.jyb.tooter.job.Job;
import com.jyb.tooter.job.maneger.JobManager;
import com.jyb.tooter.model.Toot;
import com.jyb.tooter.utils.HtmlUtils;
import com.jyb.tooter.utils.Pt;
import com.jyb.tooter.view.StatusHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.annotations.NonNull;
import retrofit2.Response;

public class RecycleNotficationAdapter extends RecyclerView.Adapter<StatusHolder> {

    FragmentNotfications mFragment;
    ArrayList<Notification> mData;
    View mView;

    public RecycleNotficationAdapter(FragmentNotfications fragment) {
        mData = fragment.getData();
        mFragment = fragment;
    }

    @NonNull
    @Override
    public StatusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_layout, parent, false);
        StatusHolder holder = new StatusHolder(mView, mFragment);
        return holder;
    }

    @Override
    public void onBindViewHolder(@Nullable final StatusHolder holder, final int position) {
        Notification notification = mData.get(position);

        holder.clear();

        switch (notification.type) {
            case FOLLOW:
                follow(holder, position, notification);
                break;
            case REBLOG:
                reblog(holder, position, notification);
                break;
            case FAVOURITE:
                favourite(holder, position, notification);
                break;
            case MENTION:
                mention(holder, position, notification);
                break;
        }
    }

    private void follow(StatusHolder holder, int position, final Notification notification) {

        holder.setControllerGroupVisible(true, false, false, true);

        Account account = notification.account;
        String username = account.acct;
        String displayName = account.displayName;
        String name = displayName.equals("") ? username : displayName;
        name += mFragment.getString(R.string.strong_follow);
        String avatarUrl = account.avatar;

        Spanned spdName = HtmlUtils.fromHtml(name);
        holder.setUsername(spdName);

        Spanned spdContent = HtmlUtils.fromHtml(account.url);
        holder.setContent(spdContent);

        holder.setDate(notification.createdAt);

        View view = LayoutInflater.from(mView.getContext()).inflate(R.layout.status_item_avatar_layout1, null, false);
        RoundedImageView roundImage1 = view.findViewById(R.id.avt1);

        roundImage1.setCornerRadius(16);
        roundImage1.setBorderWidth(1f);
        roundImage1.setBorderColor(Color.LTGRAY);
        Picasso.get()
                .load(avatarUrl)
                .into(roundImage1);

        holder.addAvatar(view);

        Drawable userPlus = new IconicsDrawable(mView.getContext())
                .icon(FontAwesome.Icon.faw_user_plus)
                .color(mFragment.getResources().getColor(R.color.status_reblog_button_marked_dark))
                .sizeDp(22);
        holder.setStatusType(userPlus);

        holder.getReplie()
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toot toot = new Toot();
                        toot.getMentions()
                                .add("@" + notification.account.acct);
                        String gson = new Gson().toJson(toot);
                        Intent intent = new Intent(mFragment.getBaseActivity(), TootActivity.class);
                        intent.putExtra("toot", gson);
                        mFragment.startActivity(intent);
                    }
                });
    }

    private void reblog(final StatusHolder holder, final int position, final Notification notification) {

//        holder.setControllerGroupVisible(true, false, false, true);

//        final Status status = notification.status;
        final Status actionableStatus = notification.status.getActionableStatus();

        String username = notification.account.acct;
        String displayName = notification.account.displayName;
        String name = displayName.equals("") ? username : displayName;
        name += mFragment.getString(R.string.strong_reblog);
        String avtUrl1 = notification.account.avatar;

        Spanned spdName = HtmlUtils.fromHtml(name);
        holder.setUsername(spdName);

        holder.setDate(notification.createdAt);

        Spanned spdContent = HtmlUtils.fromHtml(actionableStatus.content);
        holder.setContent(spdContent);

        View view = LayoutInflater.from(mView.getContext()).inflate(R.layout.status_item_avatar_layout2, null, false);
        RoundedImageView roundImage1 = view.findViewById(R.id.avt1);
        RoundedImageView roundImage2 = view.findViewById(R.id.avt2);

        roundImage1.setCornerRadius(16);
        roundImage2.setCornerRadius(16);

        roundImage1.setBorderColor(Color.LTGRAY);
        roundImage2.setBorderColor(Color.LTGRAY);

        roundImage1.setBorderWidth(1f);
        roundImage2.setBorderWidth(1f);

        String avtUrl2 = actionableStatus.account.avatar;
        Picasso.get()
                .load(avtUrl1)
                .into(roundImage1);
        Picasso.get()
                .load(avtUrl2)
                .into(roundImage2);
        holder.addAvatar(view);

        Drawable retweet = new IconicsDrawable(mView.getContext())
                .icon(FontAwesome.Icon.faw_retweet)
                .color(mFragment.getResources().getColor(R.color.status_reblog_button_marked_dark))
                .sizeDp(22);
        holder.setStatusType(retweet);

        holder.setControllerGroup(false, actionableStatus.repliesCount,
                actionableStatus.reblogged, actionableStatus.reblogsCount,
                actionableStatus.favourited, actionableStatus.favouritesCount);

        holder.getReplie()
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toot toot = new Toot();
                        toot.replyId = notification.status.id;
                        toot.getMentions()
                                .add("@" + notification.account.acct);
                        String gson = new Gson().toJson(toot);
                        Intent intent = new Intent(mFragment.getBaseActivity(), TootActivity.class);
                        intent.putExtra("toot", gson);
                        mFragment.startActivity(intent);
                    }
                });

        holder.setControllerGroup(false, actionableStatus.repliesCount,
                actionableStatus.reblogged, actionableStatus.reblogsCount,
                actionableStatus.favourited, actionableStatus.favouritesCount);

        holder.getReblog().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.getReblog().setEnabled(false);

                Job job = new Job() {

                    boolean cReb;

                    Response<Status> response;

                    @Override
                    public void onStart() {
                        super.onStart();
                        cReb = actionableStatus.reblogged;
                        actionableStatus.reblogged = !cReb;
                        notifyItemChanged(position);
                        Pt.d("onStart");
                    }

                    @Override
                    public void onSend() {
                        super.onSend();
                        try {
                            if (!cReb) {
                                response = mFragment.getBaseActivity()
                                        .getMastApi()
                                        .reblogStatus(actionableStatus.id)
                                        .execute();
                            } else {
                                response = mFragment.getBaseActivity()
                                        .getMastApi()
                                        .unreblogStatus(actionableStatus.id)
                                        .execute();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Pt.d("onSend");
                    }

                    @Override
                    public void onReceive() {
                        super.onReceive();
                        if (response != null && response.isSuccessful()) {
                            Status respStatus = response.body();
                            if (respStatus != null) {
                                if (respStatus.getActionableStatus()!=respStatus){
                                    respStatus = respStatus.getActionableStatus();
                                }
                                if (!respStatus.reblogged) {
//                                    respStatus.reblogsCount = respStatus.reblogsCount;
//                                } else {
                                    int count = Integer.parseInt(respStatus.reblogsCount);
                                    count -= 1;
                                    if (count < 0) count = 0;
                                    respStatus.reblogsCount = count + "";
                                }
                                actionableStatus.repliesCount = respStatus.repliesCount;
                                actionableStatus.reblogged = respStatus.reblogged;
                                actionableStatus.reblogsCount = respStatus.reblogsCount;
                                actionableStatus.favourited = respStatus.favourited;
                                actionableStatus.favouritesCount = respStatus.favouritesCount;
                                notifyItemChanged(position);
                                holder.getReblog().setEnabled(true);
                                Pt.d("onReceive");
                                return;
                            }
                        }
                        actionableStatus.reblogged = cReb;
                        notifyItemChanged(position);
                        holder.getReblog().setEnabled(true);
                        Pt.d("onReceive Error");
                    }

                    @Override
                    public void onTimeout() {
                        super.onTimeout();
                        actionableStatus.reblogged = cReb;
                        notifyItemChanged(position);
                        holder.getReblog().setEnabled(true);
                        Pt.d("onTimeout");
                    }
                };
                JobManager.get()
                        .addAnsyc(job);
            }
        });

        holder.getFavourite().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.getFavourite().setEnabled(false);

                Job job = new Job() {

                    boolean cFav;

                    Response<Status> response;

                    @Override
                    public void onStart() {
                        super.onStart();
                        cFav = actionableStatus.favourited;
                        actionableStatus.favourited = !cFav;
                        notifyItemChanged(position);
                        Pt.d("onStart");
                    }

                    @Override
                    public void onSend() {
                        super.onSend();
                        try {
                            if (!cFav) {
                                response = mFragment.getBaseActivity()
                                        .getMastApi()
                                        .favouriteStatus(actionableStatus.id)
                                        .execute();
                            } else {
                                response = mFragment.getBaseActivity()
                                        .getMastApi()
                                        .unfavouriteStatus(actionableStatus.id)
                                        .execute();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Pt.d("onSend");
                    }

                    @Override
                    public void onReceive() {
                        super.onReceive();
                        if (response != null && response.isSuccessful()) {
                            Status respStatus = response.body();
                            if (respStatus != null) {
                                if (respStatus.favourited) {
                                    actionableStatus.favouritesCount = respStatus.favouritesCount;
                                } else {
                                    int count = Integer.parseInt(respStatus.favouritesCount);
                                    count -= 1;
                                    if (count < 0) count = 0;
                                    actionableStatus.favouritesCount = count + "";
                                }
                                actionableStatus.repliesCount = respStatus.repliesCount;
                                actionableStatus.reblogged = respStatus.reblogged;
                                actionableStatus.reblogsCount = respStatus.reblogsCount;
                                actionableStatus.favourited = respStatus.favourited;
//                                actionableStatus.favouritesCount = respStatus.favouritesCount;
                                notifyItemChanged(position);
                                holder.getFavourite().setEnabled(true);
                                Pt.d("onReceive");
                                return;
                            }
                        }
                        actionableStatus.favourited = cFav;
                        notifyItemChanged(position);
                        holder.getFavourite().setEnabled(true);
                        Pt.d("onReceive Error");
                    }

                    @Override
                    public void onTimeout() {
                        super.onTimeout();
                        actionableStatus.favourited = cFav;
                        notifyItemChanged(position);
                        holder.getFavourite().setEnabled(true);
                        Pt.d("onTimeout");
                    }
                };
                JobManager.get()
                        .addAnsyc(job);
            }
        });
    }

    private void favourite(final StatusHolder holder, final int position, final Notification notification) {

//        holder.setControllerGroupVisible(true, false, false, true);

        final Status status = notification.status;
        String username = notification.account.acct;
        String displayName = notification.account.displayName;
        String name = displayName.equals("") ? username : displayName;
        name += mFragment.getString(R.string.strong_favourite);
        String avtUrl1 = notification.account.avatar;

        Spanned spdName = HtmlUtils.fromHtml(name);
        holder.setUsername(spdName);

        holder.setDate(notification.createdAt);

        Spanned spdContent = HtmlUtils.fromHtml(status.content);
        holder.setContent(spdContent);

        View view = LayoutInflater.from(mView.getContext()).inflate(R.layout.status_item_avatar_layout2, null, false);
        RoundedImageView roundImage1 = view.findViewById(R.id.avt1);
        RoundedImageView roundImage2 = view.findViewById(R.id.avt2);

        roundImage1.setCornerRadius(16);
        roundImage2.setCornerRadius(16);

        roundImage1.setBorderColor(Color.LTGRAY);
        roundImage2.setBorderColor(Color.LTGRAY);

        roundImage1.setBorderWidth(1f);
        roundImage2.setBorderWidth(1f);

        String avtUrl2 = status.account.avatar;
        Picasso.get()
                .load(avtUrl1)
                .into(roundImage1);
        Picasso.get()
                .load(avtUrl2)
                .into(roundImage2);
        holder.addAvatar(view);

        holder.setControllerGroup(false, status.repliesCount,
                status.reblogged, status.reblogsCount,
                status.favourited, status.favouritesCount);

        Drawable retweet = new IconicsDrawable(mView.getContext())
                .icon(FontAwesome.Icon.faw_star)
                .color(mFragment.getResources().getColor(R.color.status_favourite_button_marked_dark))
                .sizeDp(22);
        holder.setStatusType(retweet);

        holder.getReplie()
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toot toot = new Toot();
                        toot.replyId = status.id;
                        toot.getMentions()
                                .add("@" + notification.account.acct);
                        String gson = new Gson().toJson(toot);
                        Intent intent = new Intent(mFragment.getBaseActivity(), TootActivity.class);
                        intent.putExtra("toot", gson);
                        mFragment.startActivity(intent);
                    }
                });

        holder.getReblog().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.getReblog().setEnabled(false);

                Job job = new Job() {

                    boolean cReb;

                    Response<Status> response;

                    @Override
                    public void onStart() {
                        super.onStart();
                        cReb = status.reblogged;
                        status.reblogged = !cReb;
                        notifyItemChanged(position);
                        Pt.d("onStart");
                    }

                    @Override
                    public void onSend() {
                        super.onSend();
                        try {
                            if (!cReb) {
                                response = mFragment.getBaseActivity()
                                        .getMastApi()
                                        .reblogStatus(status.id)
                                        .execute();
                            } else {
                                response = mFragment.getBaseActivity()
                                        .getMastApi()
                                        .unreblogStatus(status.id)
                                        .execute();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Pt.d("onSend");
                    }

                    @Override
                    public void onReceive() {
                        super.onReceive();
                        if (response != null && response.isSuccessful()) {
                            Status respStatus = response.body();
                            if (respStatus != null) {
                                if (respStatus.getActionableStatus()!=respStatus){
                                    respStatus = respStatus.getActionableStatus();
                                }
                                if (!respStatus.reblogged) {
//                                    respStatus.reblogsCount = respStatus.reblogsCount;
//                                } else {
                                    int count = Integer.parseInt(respStatus.reblogsCount);
                                    count -= 1;
                                    if (count < 0) count = 0;
                                    respStatus.reblogsCount = count + "";
                                }
                                Pt.d(status.id);
                                Pt.d(respStatus.id);
                                Pt.d(respStatus.reblogsCount);
                                status.repliesCount = respStatus.repliesCount;
                                status.reblogged = respStatus.reblogged;
                                status.reblogsCount = respStatus.reblogsCount;
                                status.favourited = respStatus.favourited;
                                status.favouritesCount = respStatus.favouritesCount;
                                notifyItemChanged(position);
                                holder.getReblog().setEnabled(true);
                                Pt.d("onReceive");
                                return;
                            }
                        }
                        status.reblogged = cReb;
                        notifyItemChanged(position);
                        holder.getReblog().setEnabled(true);
                        Pt.d("onReceive Error");
                    }

                    @Override
                    public void onTimeout() {
                        super.onTimeout();
                        status.reblogged = cReb;
                        notifyItemChanged(position);
                        holder.getReblog().setEnabled(true);
                        Pt.d("onTimeout");
                    }
                };
                JobManager.get()
                        .addAnsyc(job);
            }
        });

        holder.getFavourite().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.getFavourite().setEnabled(false);

                Job job = new Job() {

                    boolean cFav;

                    Response<Status> response;

                    @Override
                    public void onStart() {
                        super.onStart();
                        cFav = status.favourited;
                        status.favourited = !cFav;
                        notifyItemChanged(position);
                        Pt.d("onStart");
                    }

                    @Override
                    public void onSend() {
                        super.onSend();
                        try {
                            if (!cFav) {
                                response = mFragment.getBaseActivity()
                                        .getMastApi()
                                        .favouriteStatus(status.id)
                                        .execute();
                            } else {
                                response = mFragment.getBaseActivity()
                                        .getMastApi()
                                        .unfavouriteStatus(status.id)
                                        .execute();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Pt.d("onSend");
                    }

                    @Override
                    public void onReceive() {
                        super.onReceive();
                        if (response != null && response.isSuccessful()) {
                            Status respStatus = response.body();
                            if (respStatus != null) {
                                if (respStatus.favourited) {
                                    status.favouritesCount = respStatus.favouritesCount;
                                } else {
                                    int count = Integer.parseInt(respStatus.favouritesCount);
                                    count -= 1;
                                    if (count < 0) count = 0;
                                    status.favouritesCount = count + "";
                                }
                                status.repliesCount = respStatus.repliesCount;
                                status.reblogged = respStatus.reblogged;
                                status.reblogsCount = respStatus.reblogsCount;
                                status.favourited = respStatus.favourited;
//                                status.favouritesCount = respStatus.favouritesCount;
                                notifyItemChanged(position);
                                holder.getFavourite().setEnabled(true);
                                Pt.d("onReceive");
                                return;
                            }
                        }
                        status.favourited = cFav;
                        notifyItemChanged(position);
                        holder.getFavourite().setEnabled(true);
                        Pt.d("onReceive Error");
                    }

                    @Override
                    public void onTimeout() {
                        super.onTimeout();
                        status.favourited = cFav;
                        notifyItemChanged(position);
                        holder.getFavourite().setEnabled(true);
                        Pt.d("onTimeout");
                    }
                };
                JobManager.get()
                        .addAnsyc(job);
            }
        });
    }

    private void mention(final StatusHolder holder, final int position, final Notification notification) {

        final Status status = notification.status;

        String username = status.account.acct;
        String displayName = status.account.displayName;
        String name = displayName.equals("") ? username : displayName;
        name += mFragment.getString(R.string.strong_mention);
        Spanned spdName = HtmlUtils.fromHtml(name);
        holder.setUsername(spdName);

        Spanned spdContent = HtmlUtils.fromHtml(status.content);
        holder.setContent(spdContent);

        final Date date = status.createdAt;
        holder.setDate(date);

        float radius = 16;
        float borderWidth = 1;
        int borderColor = Color.LTGRAY;
        String avtUrl1 = status.account.avatar;

        if (status.getActionableStatus() == status) {
            View view = LayoutInflater.from(mView.getContext()).inflate(R.layout.status_item_avatar_layout1, null, false);
            RoundedImageView roundImage1 = view.findViewById(R.id.avt1);
            roundImage1.setCornerRadius(radius);
            roundImage1.setBorderWidth(borderWidth);
            roundImage1.setBorderColor(borderColor);
            Picasso.get()
                    .load(avtUrl1)
                    .into(roundImage1);
            holder.addAvatar(view);
        } else {
            View view = LayoutInflater.from(mView.getContext()).inflate(R.layout.status_item_avatar_layout2, null, false);
            RoundedImageView roundImage1 = view.findViewById(R.id.avt1);
            RoundedImageView roundImage2 = view.findViewById(R.id.avt2);

            roundImage1.setCornerRadius(radius);
            roundImage2.setCornerRadius(radius);

            roundImage1.setBorderColor(borderColor);
            roundImage2.setBorderColor(borderColor);

            roundImage1.setBorderWidth(borderWidth);
            roundImage2.setBorderWidth(borderWidth);

            String avtUrl2 = status.getActionableStatus().account.avatar;
            Picasso.get()
                    .load(avtUrl1)
                    .into(roundImage1);
            Picasso.get()
                    .load(avtUrl2)
                    .into(roundImage2);
            holder.addAvatar(view);

            Drawable drawable = new IconicsDrawable(mView.getContext())
                    .icon(FontAwesome.Icon.faw_retweet)
                    .color(mFragment.getResources().getColor(R.color.status_button_dark))
                    .sizeDp(22);
            holder.setStatusType(drawable);
        }

        holder.setControllerGroup(false, status.repliesCount,
                status.reblogged, status.reblogsCount,
                status.favourited, status.favouritesCount);

        holder.getReplie()
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toot toot = new Toot();
                        toot.replyId = status.id;
                        toot.getMentions()
                                .add("@" + notification.account.acct);
                        String gson = new Gson().toJson(toot);
                        Intent intent = new Intent(mFragment.getBaseActivity(), TootActivity.class);
                        intent.putExtra("toot", gson);
                        mFragment.startActivity(intent);
                    }
                });

        holder.getReblog().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.getReblog().setEnabled(false);

                Job job = new Job() {

                    boolean cReb;

                    Response<Status> response;

                    @Override
                    public void onStart() {
                        super.onStart();
                        cReb = status.reblogged;
                        status.reblogged = !cReb;
                        notifyItemChanged(position);
                        Pt.d("onStart");
                    }

                    @Override
                    public void onSend() {
                        super.onSend();
                        try {
                            if (!cReb) {
                                response = mFragment.getBaseActivity()
                                        .getMastApi()
                                        .reblogStatus(status.id)
                                        .execute();
                            } else {
                                response = mFragment.getBaseActivity()
                                        .getMastApi()
                                        .unreblogStatus(status.id)
                                        .execute();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Pt.d("onSend");
                    }

                    @Override
                    public void onReceive() {
                        super.onReceive();
                        if (response != null && response.isSuccessful()) {
                            Status respStatus = response.body();
                            if (respStatus != null) {
                                int count = Integer.parseInt(respStatus.reblogsCount);
                                if (respStatus.reblogged) {
                                    status.reblogsCount = count + 1 + "";
                                } else {
                                    count -= 1;
                                    if (count < 0) count = 0;
                                    status.reblogsCount = count + "";
                                }
                                status.repliesCount = respStatus.repliesCount;
                                status.reblogged = respStatus.reblogged;
//                                status.reblogsCount = respStatus.reblogsCount;
                                status.favourited = respStatus.favourited;
                                status.favouritesCount = respStatus.favouritesCount;
                                notifyItemChanged(position);
                                holder.getReblog().setEnabled(true);
                                Pt.d("onReceive");
                                return;
                            }
                        }
                        status.reblogged = cReb;
                        notifyItemChanged(position);
                        holder.getReblog().setEnabled(true);
                        Pt.d("onReceive Error");
                    }

                    @Override
                    public void onTimeout() {
                        super.onTimeout();
                        status.reblogged = cReb;
                        notifyItemChanged(position);
                        holder.getReblog().setEnabled(true);
                        Pt.d("onTimeout");
                    }
                };
                JobManager.get()
                        .addAnsyc(job);
            }
        });

        holder.getFavourite().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.getFavourite().setEnabled(false);

                Job job = new Job() {

                    boolean cFav;

                    Response<Status> response;

                    @Override
                    public void onStart() {
                        super.onStart();
                        cFav = status.favourited;
                        status.favourited = !cFav;
                        notifyItemChanged(position);
                        Pt.d("onStart");
                    }

                    @Override
                    public void onSend() {
                        super.onSend();
                        try {
                            if (!cFav) {
                                response = mFragment.getBaseActivity()
                                        .getMastApi()
                                        .favouriteStatus(status.id)
                                        .execute();
                            } else {
                                response = mFragment.getBaseActivity()
                                        .getMastApi()
                                        .unfavouriteStatus(status.id)
                                        .execute();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Pt.d("onSend");
                    }

                    @Override
                    public void onReceive() {
                        super.onReceive();
                        if (response != null && response.isSuccessful()) {
                            Status respStatus = response.body();
                            if (respStatus != null) {
                                if (respStatus.favourited) {
                                    status.favouritesCount = respStatus.favouritesCount;
                                } else {
                                    int count = Integer.parseInt(respStatus.favouritesCount);
                                    count -= 1;
                                    if (count < 0) count = 0;
                                    status.favouritesCount = count + "";
                                }
                                status.repliesCount = respStatus.repliesCount;
                                status.reblogged = respStatus.reblogged;
                                status.reblogsCount = respStatus.reblogsCount;
                                status.favourited = respStatus.favourited;
//                                status.favouritesCount = respStatus.favouritesCount;
                                notifyItemChanged(position);
                                holder.getFavourite().setEnabled(true);
                                Pt.d("onReceive");
                                return;
                            }
                        }
                        status.favourited = cFav;
                        notifyItemChanged(position);
                        holder.getFavourite().setEnabled(true);
                        Pt.d("onReceive Error");
                    }

                    @Override
                    public void onTimeout() {
                        super.onTimeout();
                        status.favourited = cFav;
                        notifyItemChanged(position);
                        holder.getFavourite().setEnabled(true);
                        Pt.d("onTimeout");
                    }
                };
                JobManager.get()
                        .addAnsyc(job);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
