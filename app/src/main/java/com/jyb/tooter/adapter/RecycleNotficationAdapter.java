package com.jyb.tooter.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyb.tooter.R;
import com.jyb.tooter.entity.Account;
import com.jyb.tooter.entity.Notification;
import com.jyb.tooter.entity.Status;
import com.jyb.tooter.fragments.FragmentNotfications;
import com.jyb.tooter.utils.HtmlUtils;
import com.jyb.tooter.view.StatusHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import io.reactivex.annotations.NonNull;

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

        holder.clean();
        holder.setControllerGroupVisibility(false);

        switch (notification.type) {
            case FOLLOW:
                follow(holder, notification);
                break;
            case REBLOG:
                reblog(holder, notification);
                break;
            case FAVOURITE:
                favourite(holder, notification);
                break;
            case MENTION:
                mention(holder, notification);
                break;
        }
    }

    private void follow(StatusHolder holder, Notification notification) {

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
    }

    private void reblog(StatusHolder holder, Notification notification) {

        Status status = notification.status;
        String username = notification.account.acct;
        String displayName = notification.account.displayName;
        String name = displayName.equals("") ? username : displayName;
        name += mFragment.getString(R.string.strong_reblog);
        String avtUrl1 = notification.account.avatar;

        Spanned spdName = HtmlUtils.fromHtml(name);
        holder.setUsername(spdName);

        holder.setDate(notification.createdAt);

        holder.setRRFCount(status.reblogsCount,
                status.reblogsCount,
                status.favouritesCount);

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

        Drawable retweet = new IconicsDrawable(mView.getContext())
                .icon(FontAwesome.Icon.faw_retweet)
                .color(mFragment.getResources().getColor(R.color.status_reblog_button_marked_dark))
                .sizeDp(22);
        holder.setStatusType(retweet);
    }

    private void favourite(StatusHolder holder, Notification notification) {

        Status status = notification.status;
        String username = notification.account.acct;
        String displayName = notification.account.displayName;
        String name = displayName.equals("") ? username : displayName;
        name += mFragment.getString(R.string.strong_favourite);
        String avtUrl1 = notification.account.avatar;

        Spanned spdName = HtmlUtils.fromHtml(name);
        holder.setUsername(spdName);

        holder.setDate(notification.createdAt);

        holder.setRRFCount(status.reblogsCount,
                status.reblogsCount,
                status.favouritesCount);

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

        Drawable retweet = new IconicsDrawable(mView.getContext())
                .icon(FontAwesome.Icon.faw_star)
                .color(mFragment.getResources().getColor(R.color.status_favourite_button_marked_dark))
                .sizeDp(22);
        holder.setStatusType(retweet);
    }

    private void mention(StatusHolder holder, Notification notification) {

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

        holder.setRRFCount(status.repliesCount, status.reblogsCount, status.favouritesCount);

        holder.setFavouriteSelete(status.favourited);

        holder.setReblogSelecte(status.reblogged);

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

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
