package com.jyb.tooter.model;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class Toot {
    private final ArrayList<String> mMedias = new ArrayList<>();
    private final Set<String> mMentions = new TreeSet<>();
    private final Set<String> mFillterMentions = new TreeSet<>();
    public String replyId;
    public String text;
    public String spoilerTextt;
    public String visibility;
    public boolean sensitive;

    public Set<String> getMentions() {
        mMentions.removeAll(mFillterMentions);
        return mMentions;
    }

    public Set<String> getFillterMentions() {
        return mFillterMentions;
    }

    public ArrayList<String> getMedias() {
        return mMedias;
    }

    public static class Visibility {
        public static final String PUBLIC = "public";
        public static final String UNLISTED = "unlisted";
        public static final String PRIVATE = "private";

    }
}
