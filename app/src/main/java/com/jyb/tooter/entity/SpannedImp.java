package com.jyb.tooter.entity;

import android.text.Spanned;

public class SpannedImp implements Spanned {
    @Override
    public <T> T[] getSpans(int start, int end, Class<T> type) {
        return null;
    }

    @Override
    public int getSpanStart(Object tag) {
        return 0;
    }

    @Override
    public int getSpanEnd(Object tag) {
        return 0;
    }

    @Override
    public int getSpanFlags(Object tag) {
        return 0;
    }

    @Override
    public int nextSpanTransition(int start, int limit, Class type) {
        return 0;
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return null;
    }
}
