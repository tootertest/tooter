package com.jyb.tooter.utils;

import java.util.ArrayList;

public class EmojiStyle {

    private ArrayList<String> mEmojoStyle1 = new ArrayList<>();
    private ArrayList<String> mEmojoStyle2 = new ArrayList<>();

    public EmojiStyle() {
        initEmojiStyle1();
        initEmojiStyle2();
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

    private void initEmojiStyle1() {
        mEmojoStyle1.add("\uD83D\uDE00");
        mEmojoStyle1.add("\uD83D\uDE01");
        mEmojoStyle1.add("\uD83D\uDE02");
        mEmojoStyle1.add("\uD83E\uDD23");
        mEmojoStyle1.add("\uD83D\uDE03");
        mEmojoStyle1.add("\uD83D\uDE04");
        mEmojoStyle1.add("\uD83D\uDE05");
        mEmojoStyle1.add("\uD83D\uDE06");
        mEmojoStyle1.add("\uD83D\uDE09");
        mEmojoStyle1.add("\uD83D\uDE0A");
        mEmojoStyle1.add("\uD83D\uDE0B");
        mEmojoStyle1.add("\uD83D\uDE0E");
        mEmojoStyle1.add("\uD83D\uDE0D");
        mEmojoStyle1.add("\uD83D\uDE18");
        mEmojoStyle1.add("\uD83D\uDE17");
        mEmojoStyle1.add("\uD83D\uDE19");
        mEmojoStyle1.add("\uD83D\uDE1A");
        mEmojoStyle1.add("☺️");
        mEmojoStyle1.add("\uD83D\uDE42");
        mEmojoStyle1.add("\uD83E\uDD17");
        mEmojoStyle1.add("\uD83E\uDD29");
        mEmojoStyle1.add("\uD83E\uDD14");
        mEmojoStyle1.add("\uD83E\uDD28");
        mEmojoStyle1.add("\uD83D\uDE10");
        mEmojoStyle1.add("\uD83D\uDE11");
        mEmojoStyle1.add("\uD83D\uDE36");
        mEmojoStyle1.add("\uD83D\uDE44");
        mEmojoStyle1.add("\uD83D\uDE0F");
        mEmojoStyle1.add("\uD83D\uDE23");
        mEmojoStyle1.add("\uD83D\uDE25");
        mEmojoStyle1.add("\uD83D\uDE2E");
        mEmojoStyle1.add("\uD83E\uDD10");
        mEmojoStyle1.add("\uD83D\uDE2F");
        mEmojoStyle1.add("\uD83D\uDE2A");
        mEmojoStyle1.add("\uD83D\uDE2B");
        mEmojoStyle1.add("\uD83D\uDE34");
        mEmojoStyle1.add("\uD83D\uDE0C");
        mEmojoStyle1.add("\uD83D\uDE1B");
        mEmojoStyle1.add("\uD83D\uDE1C");
        mEmojoStyle1.add("\uD83D\uDE1D");
        mEmojoStyle1.add("\uD83E\uDD24");
        mEmojoStyle1.add("\uD83D\uDE12");
        mEmojoStyle1.add("\uD83D\uDE13");
        mEmojoStyle1.add("\uD83D\uDE14");
        mEmojoStyle1.add("\uD83D\uDE15");
        mEmojoStyle1.add("\uD83D\uDE43");
        mEmojoStyle1.add("\uD83E\uDD11");
        mEmojoStyle1.add("\uD83D\uDE32");
        mEmojoStyle1.add("☹️");
        mEmojoStyle1.add("\uD83D\uDE41");
        mEmojoStyle1.add("\uD83D\uDE16");
        mEmojoStyle1.add("\uD83D\uDE1E");
        mEmojoStyle1.add("\uD83D\uDE1F");
        mEmojoStyle1.add("\uD83D\uDE24");
        mEmojoStyle1.add("\uD83D\uDE22");
        mEmojoStyle1.add("\uD83D\uDE2D");
        mEmojoStyle1.add("\uD83D\uDE26");
        mEmojoStyle1.add("\uD83D\uDE27");
        mEmojoStyle1.add("\uD83D\uDE28");
        mEmojoStyle1.add("\uD83D\uDE29");
        mEmojoStyle1.add("\uD83E\uDD2F");
        mEmojoStyle1.add("\uD83D\uDE2C");
        mEmojoStyle1.add("\uD83D\uDE30");
        mEmojoStyle1.add("\uD83D\uDE31");
        mEmojoStyle1.add("\uD83D\uDE33");
        mEmojoStyle1.add("\uD83E\uDD2A");
        mEmojoStyle1.add("\uD83D\uDE35");
        mEmojoStyle1.add("\uD83D\uDE21");
        mEmojoStyle1.add("\uD83D\uDE20");
        mEmojoStyle1.add("\uD83E\uDD2C");
        mEmojoStyle1.add("\uD83D\uDE37");
        mEmojoStyle1.add("\uD83E\uDD12");
        mEmojoStyle1.add("\uD83E\uDD15");
        mEmojoStyle1.add("\uD83E\uDD22");
        mEmojoStyle1.add("\uD83E\uDD2E");
        mEmojoStyle1.add("\uD83E\uDD27");
        mEmojoStyle1.add("\uD83D\uDE07");
        mEmojoStyle1.add("\uD83E\uDD20");
        mEmojoStyle1.add("\uD83E\uDD21");
        mEmojoStyle1.add("\uD83E\uDD25");
        mEmojoStyle1.add("\uD83E\uDD2B");
        mEmojoStyle1.add("\uD83E\uDD2D");
        mEmojoStyle1.add("\uD83E\uDDD0");
        mEmojoStyle1.add("\uD83E\uDD13");
        mEmojoStyle1.add("\uD83D\uDE08");
        mEmojoStyle1.add("\uD83D\uDC7F");
        mEmojoStyle1.add("\uD83D\uDC79");
        mEmojoStyle1.add("\uD83D\uDC7A");
    }

    private void initEmojiStyle2(){
        mEmojoStyle1.add("\uD83C\uDFC1 ");
        mEmojoStyle1.add("\uD83C\uDDE8\uD83C\uDDF3 ");
        mEmojoStyle1.add("\uD83C\uDF8C ");
        mEmojoStyle1.add("\uD83C\uDDE9\uD83C\uDDEA ");
        mEmojoStyle1.add("\uD83C\uDDEA\uD83C\uDDF8 ");
        mEmojoStyle1.add("\uD83C\uDDE6\uD83C\uDDE8 ");
        mEmojoStyle1.add("\uD83C\uDDE6\uD83C\uDDE9 ");
        mEmojoStyle1.add("\uD83C\uDDE6\uD83C\uDDEA ");
        mEmojoStyle1.add("\uD83C\uDDE6\uD83C\uDDEB ");
        mEmojoStyle1.add("\uD83C\uDDE6\uD83C\uDDEC ");
        mEmojoStyle1.add("\uD83C\uDDE6\uD83C\uDDEE ");
        mEmojoStyle1.add("\uD83C\uDDE6\uD83C\uDDF1 ");
        mEmojoStyle1.add("\uD83C\uDDE6\uD83C\uDDF2 ");
        mEmojoStyle1.add("\uD83C\uDDE6\uD83C\uDDF4 ");
        mEmojoStyle1.add("\uD83C\uDDE6\uD83C\uDDF6 ");
        mEmojoStyle1.add("\uD83C\uDDE6\uD83C\uDDF7 ");
        mEmojoStyle1.add("\uD83C\uDDE6\uD83C\uDDF8 ");
        mEmojoStyle1.add("\uD83C\uDDE6\uD83C\uDDF9 ");
        mEmojoStyle1.add("\uD83C\uDDE6\uD83C\uDDFA ");
        mEmojoStyle1.add("\uD83C\uDDE6\uD83C\uDDFC ");
        mEmojoStyle1.add("\uD83C\uDDE6\uD83C\uDDFD ");
        mEmojoStyle1.add("\uD83C\uDDE6\uD83C\uDDFF ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDE6 ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDE7 ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDE9 ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDEA ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDEB ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDEC ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDED ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDEE ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDEF ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDF1 ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDF2 ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDF3 ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDF4 ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDF6 ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDF7 ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDF8 ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDF9 ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDFB ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDFC ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDFE ");
        mEmojoStyle1.add("\uD83C\uDDE7\uD83C\uDDFF ");
        mEmojoStyle1.add("\uD83C\uDDE8\uD83C\uDDE6 ");
        mEmojoStyle1.add("\uD83C\uDDE8\uD83C\uDDE8 ");
        mEmojoStyle1.add("\uD83C\uDDE8\uD83C\uDDE9 ");
        mEmojoStyle1.add("\uD83C\uDDE8\uD83C\uDDEB ");
        mEmojoStyle1.add("\uD83C\uDDE8\uD83C\uDDEC ");
    }
}