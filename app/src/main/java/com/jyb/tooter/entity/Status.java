/* Copyright 2017 Andrew Dawson
 *
 * This file is part of Tusky.
 *
 * Tusky is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Tusky is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Tusky. If not, see
 * <http://www.gnu.org/licenses/>. */

package com.jyb.tooter.entity;

import android.text.Spanned;
import android.text.SpannedString;

import com.google.gson.annotations.SerializedName;
import com.jyb.tooter.interfaces.Message;

import java.util.Date;

public class Status implements Message {

    public static final int MAX_MEDIA_ATTACHMENTS = 4;

    public String id;

    @SerializedName("created_at")
    public Date createdAt;

    @SerializedName("in_reply_to_id")
    public String inReplyToId;

    @SerializedName("in_reply_to_account_id")
    public String inReplyToAccountId;

    public boolean sensitive;

    @SerializedName("spoiler_text")
    public String spoilerText;

//    public Visibility visibility;

    public String language;

    public String uri;

    public String content;

    public String url;

    @SerializedName("replies_count")
    public String repliesCount;

    @SerializedName("reblogs_count")
    public String reblogsCount;

    @SerializedName("favourites_count")
    public String favouritesCount;

    public boolean favourited;

    public boolean reblogged;

    public boolean muted;

    private Status reblog;

    public Account account;

    @SerializedName("media_attachments")
    public MediaAttachment[] attachments;

    public Mention[] mentions;

//    public String[] tag;
//    public String[] emojis;
//    public String[] card;
//    public String[] poll;

    public static class MediaAttachment {
        public enum Type {
            @SerializedName("image")
            IMAGE,
            @SerializedName("gifv")
            GIFV,
            @SerializedName("video")
            VIDEO,
            UNKNOWN,
        }

        public String url;

        @SerializedName("preview_url")
        public String previewUrl;

        @SerializedName("text_url")
        public String textUrl;

        @SerializedName("remote_url")
        public String remoteUrl;

        public Type type;
    }

    public class Mention {
        public String id;

        public String url;

        @SerializedName("acct")
        public String acct;
    }

    public enum Visibility {
        @SerializedName("public")
        PUBLIC,
        @SerializedName("unlisted")
        UNLISTED,
        @SerializedName("private")
        PRIVATE,
    }

    public String getActionableId() {

        return reblog == null ? id : reblog.id;
    }

    public Status getActionableStatus() {

        return reblog == null ? this : reblog;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this.id == null) {
            return this == other;
        } else if (!(other instanceof Status)) {
            return false;
        }
        Status status = (Status) other;
        return status.id.equals(this.id);
    }
}
