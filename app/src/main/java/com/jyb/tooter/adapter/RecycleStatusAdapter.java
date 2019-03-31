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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jyb.tooter.R;
import com.jyb.tooter.activitys.TootActivity;
import com.jyb.tooter.dialog.DialogModalImage;
import com.jyb.tooter.entity.Status;
import com.jyb.tooter.fragments.FragmentStatus;
import com.jyb.tooter.job.Job;
import com.jyb.tooter.job.maneger.JobManager;
import com.jyb.tooter.utils.HtmlUtils;
import com.jyb.tooter.utils.Pt;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.reactivex.annotations.NonNull;
import retrofit2.Response;

public class RecycleStatusAdapter extends RecyclerView.Adapter<RecycleStatusAdapter.SimpleHolder> {

    FragmentStatus mFragment;
    ArrayList<Status> mData;
    View mView;

    public RecycleStatusAdapter(FragmentStatus fragment) {
        mData = fragment.getData();
        mFragment = fragment;
    }

    @NonNull
    @Override
    public SimpleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_layout, parent, false);
        SimpleHolder holder = new SimpleHolder(mView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleHolder holder, final int position) {
        final Status status = mData.get(position);

        holder.mAvatarLayout.removeAllViews();
        holder.mImageStatusType.setImageDrawable(null);

        String username = status.account.acct;
        String displayName = status.account.displayName;
        String name = displayName.equals("") ? username : displayName;

        Spanned spdName = HtmlUtils.fromHtml(name);
        holder.mUsername.setText(spdName);

        Spanned spdContent = HtmlUtils.fromHtml(status.content);
        holder.mText.setText(spdContent);

        final Date date = status.createdAt;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        String dateStr = format.format(date);

        holder.mDate.setText(dateStr);

        holder.mRepliesCount.setText(status.repliesCount);
        holder.mReblogsCount.setText(status.reblogsCount);
        holder.mFavouritesCount.setText(status.favouritesCount);

        holder.mLayoutReplies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = mFragment.getActivity();
                Intent intent = new Intent(activity, TootActivity.class);
                String json = new Gson().toJson(status);
                intent.putExtra("status", json);
                activity.startActivity(intent);
            }
        });

        holder.setFavourites(status.favourited);

        holder.mLayoutFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.mLayoutFavourites.setEnabled(false);

                Job job = new Job() {

                    boolean cFav;
//                    int cFavCount;

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
                                status.favourited = respStatus.favourited;
                                if (respStatus.favourited) {
                                    status.favouritesCount = respStatus.favouritesCount;
                                } else {
                                    int count = Integer.parseInt(respStatus.favouritesCount);
                                    count -= 1;
                                    if (count < 0) count = 0;
                                    status.favouritesCount = count + "";
                                }
                                notifyItemChanged(position);
                                holder.mLayoutFavourites.setEnabled(true);
                                Pt.d("onReceive");
                                return;
                            }
                        }
                        status.favourited = cFav;
//                        status.favouritesCount = cFavCount + "";
                        notifyItemChanged(position);
                        holder.mLayoutFavourites.setEnabled(true);
                        Pt.d("onReceive Error");
                    }

                    @Override
                    public void onTimeout() {
                        super.onTimeout();
                        status.favourited = cFav;
//                        status.favouritesCount = cFavCount + "";
                        notifyItemChanged(position);
                        holder.mLayoutFavourites.setEnabled(true);
                        Pt.d("onTimeout");
                    }
                };
                JobManager.instance()
                        .add(job);
            }
        });

        holder.setReblogs(status.reblogged);

        holder.mLayoutReblogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.mLayoutReblogs.setEnabled(false);

                Job job = new Job() {

                    boolean cReb;
//                    int cFavCount;

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
                                status.reblogged = respStatus.reblogged;
                                int count = Integer.parseInt(respStatus.reblogsCount);
                                if (respStatus.reblogged) {
                                    status.reblogsCount = count + 1 + "";
                                } else {
                                    count -= 1;
                                    if (count < 0) count = 0;
                                    status.reblogsCount = count + "";
                                }
                                notifyItemChanged(position);
                                holder.mLayoutReblogs.setEnabled(true);
                                Pt.d("onReceive");
                                return;
                            }
                        }
                        status.reblogged = cReb;
//                        status.favouritesCount = cFavCount + "";
                        notifyItemChanged(position);
                        holder.mLayoutReblogs.setEnabled(true);
                        Pt.d("onReceive Error");
                    }

                    @Override
                    public void onTimeout() {
                        super.onTimeout();
                        status.reblogged = cReb;
//                        status.favouritesCount = cFavCount + "";
                        notifyItemChanged(position);
                        holder.mLayoutReblogs.setEnabled(true);
                        Pt.d("onTimeout");
                    }
                };
                JobManager.instance()
                        .add(job);
            }
        });

        float radius = 16;
        float borderWidth = 1;
        int borderColor = Color.LTGRAY;
        holder.mImageStatusType.setBackground(null);
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
            holder.mAvatarLayout.addView(view);
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
            holder.mAvatarLayout.addView(view);

            Drawable avtType = new IconicsDrawable(mView.getContext())
                    .icon(FontAwesome.Icon.faw_retweet)
                    .color(mFragment.getResources().getColor(R.color.status_button_dark))
//                        .color(Color.RED)
                    .sizeDp(22);
            holder.mImageStatusType.setBackground(avtType);
        }

        holder.mMediaLayout.removeAllViews();

//        Pt.d("position:" + position);
//        Pt.d("id:" + status.id);
//        Pt.d("length:" + status.getActionableStatus().attachments.length);
//        Pt.d("add media postion:" + position);

        Status actionableStatus = status.getActionableStatus();

        if (actionableStatus != null) {
//            for (int i = 0; i < actionableStatus.attachments.length; i++) {
//                Pt.d("previewUrl:" + actionableStatus.attachments[i].previewUrl);
//                Pt.d("url:" + actionableStatus.attachments[i].url);
//            }

            for (int i = 0; i < actionableStatus.attachments.length; i++) {
                final Status.MediaAttachment media = actionableStatus.attachments[i];
                if (media.type == Status.MediaAttachment.Type.IMAGE) {
                    final ImageView imageView = new ImageView(mView.getContext());
                    final Drawable drawable = new IconicsDrawable(mView.getContext())
                            .icon(FontAwesome.Icon.faw_file_image_o)
                            .color(Color.LTGRAY)
                            .sizeDp(48);
                    imageView.setImageDrawable(drawable);
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    imageView.setLayoutParams(params);
                    Picasso.get()
                            .load(media.previewUrl)
                            .into(imageView);
                    holder.mMediaLayout.addView(imageView);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentManager manager = mFragment.getBaseActivity().getSupportFragmentManager();
                            DialogModalImage dialog = new DialogModalImage();
                            dialog
                                    .setUrl(media.url)
                                    .setModal(true)
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

            mLayoutReplies = itemView.findViewById(R.id.status_replies).findViewById(R.id.status_group_layout);
            mLayoutReblogs = itemView.findViewById(R.id.status_reblogs).findViewById(R.id.status_group_layout);
            mLayoutFavourites = itemView.findViewById(R.id.status_favourites).findViewById(R.id.status_group_layout);

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
