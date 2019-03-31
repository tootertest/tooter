package com.jyb.tooter.view;

import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyb.tooter.R;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StatusHolder extends RecyclerView.ViewHolder {

    ConstraintLayout mAvatarLayout;
    LinearLayout mMediaLayout;

    TextView mUsername;
    TextView mContent;
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

    Fragment mFragment;
    View mView;
    int mDrawableSize = 22;

    public StatusHolder(View itemView, Fragment fragment) {
        super(itemView);

        mView = itemView;
        mFragment = fragment;

        mDrawableReplies = new IconicsDrawable(mView.getContext())
                .icon(FontAwesome.Icon.faw_reply_all)
                .color(mFragment.getResources().getColor(R.color.status_button_dark))
                .sizeDp(mDrawableSize);

        mDrawableReblogs = new IconicsDrawable(mView.getContext())
                .icon(FontAwesome.Icon.faw_retweet)
                .color(mFragment.getResources().getColor(R.color.status_button_dark))
                .sizeDp(mDrawableSize);

        mDrawableFavourites = new IconicsDrawable(mView.getContext())
                .icon(FontAwesome.Icon.faw_star)
                .color(mFragment.getResources().getColor(R.color.status_button_dark))
                .sizeDp(mDrawableSize);

        mDrawableMore = new IconicsDrawable(mView.getContext())
                .icon(FontAwesome.Icon.faw_ellipsis_h)
                .color(mFragment.getResources().getColor(R.color.status_button_dark))
                .sizeDp(mDrawableSize);

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

        mAvatarLayout = itemView.findViewById(R.id.status_avatar_layout);
        mMediaLayout = itemView.findViewById(R.id.status_layout_media);

        mUsername = itemView.findViewById(R.id.status_username);
        mContent = itemView.findViewById(R.id.status_content);
        mImageStatusType = itemView.findViewById(R.id.status_type);
    }

    public void setFavourites(boolean flag) {
        if (flag) {
            Drawable drawable = new IconicsDrawable(mView.getContext())
                    .icon(FontAwesome.Icon.faw_star)
                    .color(mFragment.getResources().getColor(R.color.status_favourite_button_marked_dark))
                    .sizeDp(mDrawableSize);
            mImageFavourites.setBackground(drawable);
        } else {
            mImageFavourites.setBackground(mDrawableFavourites);
        }
    }

    public void setReblogs(boolean flag) {
        if (flag) {
            Drawable drawable = new IconicsDrawable(mView.getContext())
                    .icon(FontAwesome.Icon.faw_retweet)
                    .color(mFragment.getResources().getColor(R.color.status_favourite_button_marked_dark))
                    .sizeDp(mDrawableSize);
            mImageReblogs.setBackground(drawable);
        } else {
            mImageReblogs.setBackground(mDrawableReblogs);
        }
    }

    public void setUsername(Spanned spanned) {
        mUsername.setText(spanned);
    }

    public void setContent(Spanned spanned) {
        mContent.setText(spanned);
    }

    public void setRRFCount(String  count1, String  count2, String  count3) {
        mRepliesCount.setText(count1);
        mReblogsCount.setText(count2);
        mFavouritesCount.setText(count3);
    }

    public void setControllerGroupVisibility(boolean flag){
        if (flag){
            mLayoutReplies.setVisibility(View.VISIBLE);
            mLayoutReblogs.setVisibility(View.VISIBLE);
            mLayoutFavourites.setVisibility(View.VISIBLE);
            mImageMore.setVisibility(View.VISIBLE);
        }else {
            mLayoutReplies.setVisibility(View.GONE);
            mLayoutReblogs.setVisibility(View.GONE);
            mLayoutFavourites.setVisibility(View.GONE);
            mImageMore.setVisibility(View.GONE);
        }
    }

    public void setStatusType(Drawable drawable){
        mImageStatusType.setImageDrawable(drawable);
    }

    public void setDate(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        String dateStr = format.format(date);
        mDate.setText(dateStr);
    }

    public void addAvatar(View view){
        mAvatarLayout.addView(view);
    }

    public void addMedias(View view){
        mMediaLayout.addView(view);
    }

    public void clean() {

        setControllerGroupVisibility(true);

        mImageReplies.setBackground(mDrawableReplies);
        mImageReblogs.setBackground(mDrawableReblogs);
        mImageFavourites.setBackground(mDrawableFavourites);
        mImageMore.setBackground(mDrawableMore);

        mImageFavourites.setOnClickListener(null);
        mImageReblogs.setOnClickListener(null);
        mImageFavourites.setOnClickListener(null);
        mImageMore.setOnClickListener(null);

        mMediaLayout.removeAllViews();
        mAvatarLayout.removeAllViews();
        mImageStatusType.setImageDrawable(null);
        mImageStatusType.setBackground(null);
    }
}