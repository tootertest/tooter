package com.jyb.tooter.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jyb.tooter.R;
import com.jyb.tooter.activitys.TootActivity;
import com.jyb.tooter.dialog.DialogModalImage;
import com.jyb.tooter.entity.Status;
import com.jyb.tooter.fragments.FragmentStatus;
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

public class RecycleStatusAdapter extends RecyclerView.Adapter<StatusHolder> {

    FragmentStatus mFragment;
    ArrayList<Status> mData;
    View mView;

    public RecycleStatusAdapter(FragmentStatus fragment) {
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
    public void onBindViewHolder(@NonNull final StatusHolder holder, final int position) {

        final Status status = mData.get(position);
        final Status actionableStatus = status.getActionableStatus();
        final Status cStatus;

        holder.clear();

        final Date date = status.createdAt;
        holder.setDate(date);

        if (status == actionableStatus) {

            cStatus = status;

        } else {

            cStatus = actionableStatus;

        }

        String username = cStatus.account.acct;
        String displayName = cStatus.account.displayName;
        String name = displayName.equals("") ? username : displayName;

        Spanned spdName = HtmlUtils.fromHtml(name);
        holder.setUsername(spdName);

        Spanned spdContent = HtmlUtils.fromHtml(cStatus.content);
        holder.setContent(spdContent);

        holder.setControllerGroup(false, cStatus.repliesCount,
                cStatus.reblogged, cStatus.reblogsCount,
                cStatus.favourited, cStatus.favouritesCount);

        holder.getReplie().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = mFragment.getActivity();
                Intent intent = new Intent(activity, TootActivity.class);
                Toot toot = new Toot();
                toot.getMentions()
                        .add("@" + cStatus.account.acct);
//                for (int i = 0; i < cStatus.mentions.length; i++) {
//                    toot.getMentions()
//                            .add("@" + status.mentions[i].acct);
//                }
                toot.replyId = cStatus.id;
                String gson = new Gson().toJson(toot);
                intent.putExtra("toot", gson);
                activity.startActivity(intent);
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
                        cReb = cStatus.reblogged;
                        cStatus.reblogged = !cReb;
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
                                        .reblogStatus(cStatus.id)
                                        .execute();
                            } else {
                                response = mFragment.getBaseActivity()
                                        .getMastApi()
                                        .unreblogStatus(cStatus.id)
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
//                                int count = Integer.parseInt(respStatus.reblogsCount);
//                                if (respStatus.reblogged) {
//                                    respStatus.reblogsCount = count + "";
//                                } else {
//                                    count -= 1;
//                                    if (count < 0) count = 0;
//                                    respStatus.reblogsCount = count + "";
//                                }
                                if (!respStatus.reblogged) {
//                                    respStatus.reblogsCount = respStatus.reblogsCount;
//                                } else {
                                    int count = Integer.parseInt(respStatus.reblogsCount);
                                    count -= 1;
                                    if (count < 0) count = 0;
                                    respStatus.reblogsCount = count + "";
                                }
//                                Pt.d(cStatus.id);
//                                Pt.d(respStatus.id);
//                                Pt.d(respStatus.reblogsCount);
                                cStatus.repliesCount = respStatus.repliesCount;
                                cStatus.reblogged = respStatus.reblogged;
                                cStatus.reblogsCount = respStatus.reblogsCount;
                                cStatus.favourited = respStatus.favourited;
                                cStatus.favouritesCount = respStatus.favouritesCount;
                                notifyItemChanged(position);
                                holder.getReblog().setEnabled(true);
                                Pt.d("onReceive");
                                return;
                            }
                        }
                        cStatus.reblogged = cReb;
                        notifyItemChanged(position);
                        holder.getReblog().setEnabled(true);
                        Pt.d("onReceive Error");
                    }

                    @Override
                    public void onTimeout() {
                        super.onTimeout();
                        cStatus.reblogged = cReb;
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
                        cFav = cStatus.favourited;
                        cStatus.favourited = !cFav;
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
                                        .favouriteStatus(cStatus.id)
                                        .execute();
                            } else {
                                response = mFragment.getBaseActivity()
                                        .getMastApi()
                                        .unfavouriteStatus(cStatus.id)
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
                                    cStatus.favouritesCount = respStatus.favouritesCount;
                                } else {
                                    int count = Integer.parseInt(respStatus.favouritesCount);
                                    count -= 1;
                                    if (count < 0) count = 0;
                                    cStatus.favouritesCount = count + "";
                                }
                                cStatus.repliesCount = respStatus.repliesCount;
                                cStatus.reblogged = respStatus.reblogged;
                                cStatus.reblogsCount = respStatus.reblogsCount;
                                cStatus.favourited = respStatus.favourited;
//                                cStatus.favouritesCount = respStatus.favouritesCount;
                                notifyItemChanged(position);
                                holder.getFavourite().setEnabled(true);
                                Pt.d("onReceive");
                                return;
                            }
                        }
                        cStatus.favourited = cFav;
                        notifyItemChanged(position);
                        holder.getFavourite().setEnabled(true);
                        Pt.d("onReceive Error");
                    }

                    @Override
                    public void onTimeout() {
                        super.onTimeout();
                        cStatus.favourited = cFav;
                        notifyItemChanged(position);
                        holder.getFavourite().setEnabled(true);
                        Pt.d("onTimeout");
                    }
                };
                JobManager.get()
                        .addAnsyc(job);
            }
        });

        float radius = 16;
        float borderWidth = 1;
        int borderColor = Color.LTGRAY;
        String avtUrl1 = status.account.avatar;

        if (actionableStatus == status) {
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

            String avtUrl2 = actionableStatus.account.avatar;
            Picasso.get()
                    .load(avtUrl1)
                    .into(roundImage1);
            Picasso.get()
                    .load(avtUrl2)
                    .into(roundImage2);
            holder.addAvatar(view);

            Drawable avtType = new IconicsDrawable(mView.getContext())
                    .icon(FontAwesome.Icon.faw_retweet)
                    .color(mFragment.getResources().getColor(R.color.status_button_dark))
                    .sizeDp(22);
            holder.setStatusType(avtType);
        }

//        Pt.d("position:" + position);
//        Pt.d("id:" + status.id);
//        Pt.d("length:" + status.getActionableStatus().attachments.length);
//        Pt.d("add media postion:" + position);


//            for (int i = 0; i < actionableStatus.attachments.length; i++) {
//                Pt.d("previewUrl:" + actionableStatus.attachments[i].previewUrl);
//                Pt.d("url:" + actionableStatus.attachments[i].url);
//            }

        if (cStatus.attachments.length>0){

//            LinearLayout layout = new LinearLayout(mView.getContext());
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT
//            );
//            layout.setOrientation(LinearLayout.HORIZONTAL);

            for (int i = 0; i < cStatus.attachments.length; i++) {
                final Status.MediaAttachment media = cStatus.attachments[i];
                if (media.type == Status.MediaAttachment.Type.IMAGE) {
                    final RoundedImageView round = new RoundedImageView(mView.getContext());
                    final Drawable drawable = new IconicsDrawable(mView.getContext())
                            .icon(FontAwesome.Icon.faw_file_image_o)
                            .color(Color.LTGRAY)
                            .sizeDp(48);
                    round.setCornerRadius(8);
                    round.setBorderColor(Color.GRAY);
                    round.setBorderWidth(1f);
                    round.setImageDrawable(drawable);

                    int width = mView.getWidth()/5;
                    ConstraintLayout.LayoutParams imageParams = new ConstraintLayout.LayoutParams(
                            400,
                            400
                    );
                    imageParams.topMargin = 10;
                    imageParams.bottomMargin = 10;
                    imageParams.leftMargin = 10;
                    imageParams.rightMargin = 10;
                    round.setLayoutParams(imageParams);
                    round.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                roundedImageView.setMinimumWidth(100);
//                roundedImageView.setMaxWidth(100);
//                roundedImageView.setMinimumHeight(100);
//                roundedImageView.setMaxHeight(100);
                    Picasso.get()
                            .load(media.previewUrl)
                            .into(round);

                    holder.addMedias(round);
                    round.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentManager manager = mFragment.getBaseActivity().getSupportFragmentManager();
                            DialogModalImage dialog = new DialogModalImage();
                            dialog
                                    .setUrl(media.url)
                                    .setModal(true)
//                                    .setBlockBack(false)
                                    .setMax(true)
                                    .setDimAmount(.7f)
                                    .setCleanBackground(true)
                                    .setLayout(R.layout.dialog_image);
                            dialog.show(manager, null);
                        }
                    });
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class SimpleHolder extends RecyclerView.ViewHolder {

        ConstraintLayout mAvatarLayout;
        LinearLayout mMediaLayout;

        TextView mUsername;
        TextView mText;
        TextView mDate;

        ViewGroup mLayoutReplies;
        ViewGroup mLayoutReblogs;
        ViewGroup mLayoutFavourites;

        ImageView mImageReplies;
        ImageView mImageReblogs;
        ImageView mImageFavourites;
        ImageButton mImageMore;

        Drawable mDrawableReplies;
        Drawable mDrawableReblogs;
        Drawable mDrawableFavourites;
        Drawable mDrawableMore;

        TextView mRepliesCount;
        TextView mReblogsCount;
        TextView mFavouritesCount;

        ImageView mImageStatusType;

        SimpleHolder(View itemView) {
            super(itemView);

            mDrawableReplies = new IconicsDrawable(mView.getContext())
                    .icon(FontAwesome.Icon.faw_reply_all)
                    .color(mFragment.getResources().getColor(R.color.status_button_dark))
                    .sizeDp(22);

            mDrawableReblogs = new IconicsDrawable(mView.getContext())
                    .icon(FontAwesome.Icon.faw_retweet)
                    .color(mFragment.getResources().getColor(R.color.status_button_dark))
                    .sizeDp(22);

            mDrawableFavourites = new IconicsDrawable(mView.getContext())
                    .icon(FontAwesome.Icon.faw_star)
                    .color(mFragment.getResources().getColor(R.color.status_button_dark))
                    .sizeDp(22);

            mDrawableMore = new IconicsDrawable(mView.getContext())
                    .icon(FontAwesome.Icon.faw_ellipsis_h)
                    .color(mFragment.getResources().getColor(R.color.status_button_dark))
                    .sizeDp(22);

            mLayoutReplies = itemView.findViewById(R.id.status_replies_layout).findViewById(R.id.status_group_layout);
            mLayoutReblogs = itemView.findViewById(R.id.status_reblogs_layout).findViewById(R.id.status_group_layout);
            mLayoutFavourites = itemView.findViewById(R.id.status_favourites_layout).findViewById(R.id.status_group_layout);

            mImageMore = itemView.findViewById(R.id.status_more);
            mDate = itemView.findViewById(R.id.status_date);

            mImageReplies = mLayoutReplies.findViewById(R.id.status_item_group_button_img);
            mImageReblogs = mLayoutReblogs.findViewById(R.id.status_item_group_button_img);
            mImageFavourites = mLayoutFavourites.findViewById(R.id.status_item_group_button_img);

            mRepliesCount = mLayoutReplies.findViewById(R.id.status_item_group_button_text);
            mReblogsCount = mLayoutReblogs.findViewById(R.id.status_item_group_button_text);
            mFavouritesCount = mLayoutFavourites.findViewById(R.id.status_item_group_button_text);

            mImageReplies.setBackground(mDrawableReplies);
            mImageReblogs.setBackground(mDrawableReblogs);
            mImageFavourites.setBackground(mDrawableFavourites);
            mImageMore.setBackground(mDrawableMore);

            mAvatarLayout = itemView.findViewById(R.id.status_avatar_layout);
            mUsername = itemView.findViewById(R.id.status_username);
            mText = itemView.findViewById(R.id.status_content);

            mMediaLayout = itemView.findViewById(R.id.status_layout_media);

            mImageStatusType = itemView.findViewById(R.id.status_type);
        }

        private void setFavourites(boolean flag) {
            if (flag) {
                mDrawableFavourites = new IconicsDrawable(mView.getContext())
                        .icon(FontAwesome.Icon.faw_star)
                        .color(mFragment.getResources().getColor(R.color.status_favourite_button_marked_dark))
//                        .color(Color.RED)
                        .sizeDp(22);
                mImageFavourites.setBackground(mDrawableFavourites);
            } else {
                mDrawableFavourites = new IconicsDrawable(mView.getContext())
                        .icon(FontAwesome.Icon.faw_star)
                        .color(mFragment.getResources().getColor(R.color.status_button_dark))
//                        .color(Color.BLACK)
                        .sizeDp(22);
                mImageFavourites.setBackground(mDrawableFavourites);
            }
        }

        private void setReblogs(boolean flag) {
            if (flag) {
                mDrawableReblogs = new IconicsDrawable(mView.getContext())
                        .icon(FontAwesome.Icon.faw_retweet)
                        .color(mFragment.getResources().getColor(R.color.status_reblog_button_marked_dark))
//                        .color(Color.RED)
                        .sizeDp(22);
                mImageReblogs.setBackground(mDrawableReblogs);
            } else {
                mDrawableReblogs = new IconicsDrawable(mView.getContext())
                        .icon(FontAwesome.Icon.faw_retweet)
                        .color(mFragment.getResources().getColor(R.color.status_button_dark))
//                        .color(Color.BLACK)
                        .sizeDp(22);
                mImageReblogs.setBackground(mDrawableReblogs);
            }
        }

        private void setMedias(String[] url) {

        }
    }
}
