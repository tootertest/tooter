package com.jyb.tooter.utils;

import android.util.Log;

public class Pt {

    static private final String TAG = "DEBUG_Pt";
    static private final boolean DEBUG = true;

    static public void d(String string) {
        if (DEBUG) {
            Log.d(TAG, string);
        }
    }

    static public void d(String tag, String string) {
        if (DEBUG) {
            Log.d(tag, string);
        }
    }
}
