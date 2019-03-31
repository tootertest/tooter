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

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spanned;

import com.google.gson.annotations.SerializedName;

public class Account {
    public String id;

    @SerializedName("username")
    public String username;

    @SerializedName("acct")
    public String acct;

    @SerializedName("display_name")
    public String displayName;

    public boolean locked;

    public String note;

    public String url;

    public String avatar;

    @SerializedName("avatar_static")
    public String avatarStatic;

    public String header;

    @SerializedName("header_static")
    public String headerStatic;

    @SerializedName("followers_count")
    public String followersCount;

    @SerializedName("following_count")
    public String followingCount;

    @SerializedName("statuses_count")
    public String statusesCount;

    public Emojis[] emojis;

    public Fields[] fields;

    @Override
    public int hashCode() {

        return id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this.id == null) {
            return this == other;
        } else if (!(other instanceof Account)) {
            return false;
        }
        Account account = (Account) other;
        return account.id.equals(this.id);
    }

    public String getDisplayName() {
        if (displayName.length() == 0) {
            return username;
        }

        return displayName;
    }

//    @Override
//    public String getBody() {
//        return acct;
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//
//    }

    public Account() {

    }

    protected Account(Parcel in) {

    }

    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    public class Emojis {

        public String shortcode;

        public String url;

        @SerializedName("static_url")
        public String staticUrl;

        @SerializedName("visible_in_picker")
        public String visibleInPicker;

    }

    public class Fields {

        public String name;

        public String value;

        @SerializedName("verified_at")
        public Boolean verifiedAt;

    }
}
