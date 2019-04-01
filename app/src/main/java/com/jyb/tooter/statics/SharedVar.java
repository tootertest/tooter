package com.jyb.tooter.statics;

import com.jyb.tooter.entity.Account;

public class SharedVar {
    public static final String KEY = "TOTER";
    public static final String DOMAIN = "domain";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String TOKEN = "token";
    public static final String REMEMBER = "remember";

    public static final String SCOPES = "read write follow";

    public static final int UNIT_1KB = 1024;
    public static final int UNIT_1MB = UNIT_1KB * 1024;

    public static final int CHARS_LIMIT = 500;
    public static final int IMAGE_LIMIT = UNIT_1MB * 8;
    public static final int MP4_LIMIT = UNIT_1MB * 40;

    public static final int RESPONSE_TIME_OUT = 5000;
    public static final int CONNECT_TIME_OUT = 10000;

    public static final String FILE_ROOT = "/mnt/sdcard/tooter/";
//    public static final String FILE_TOKEN = "token/";
    public static final String FILE_MEDIA = "media/";
    public static final String FILE_AUDIO = "audio/";

    public static Account ACCOUNT;
}
