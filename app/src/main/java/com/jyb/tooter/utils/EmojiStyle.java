package com.jyb.tooter.utils;

import java.util.ArrayList;
import java.util.List;

public class EmojiStyle {

    private ArrayList<String> mEmojoStyle1 = new ArrayList<>();
//    private ArrayList<String> mEmojoStyle2 = new ArrayList<>();

    public EmojiStyle() {
        initEmojiStyle(mEmojoStyle1, 0x01, 0x4f, "\uD83D", "\uDE01");
    }

    public String getEmojo(int style, int id) {
        switch (style) {
            case 0:
                return mEmojoStyle1.get(id);
//            case 1:
//                return mEmojoStyle2.get(id);
            default:
                return null;

        }
    }

    public int getLength(int style) {
        switch (style) {
            case 0:
                return mEmojoStyle1.size();
//            case 1:
//                return mEmojoStyle2.size();
            default:
                return 0;

        }
    }

    private void initEmojiStyle(List<String> emojiStyle
            , int start, int end
            , String s1, String s2) {
        int lenght = end - start + 1;
        for (int i = 0; i < lenght; i++) {
            char c1[] = s2.toCharArray();
            int n1 = c1[0];
            n1 += i;
            c1[0] = (char) n1;
            String emoji = s1 + new String(c1);
            emojiStyle.add(emoji);
        }
    }
}