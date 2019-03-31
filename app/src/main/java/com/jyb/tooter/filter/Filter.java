package com.jyb.tooter.filter;

import java.util.ArrayList;

public class Filter {

    private final static ArrayList<String> mFilterInstaces = new ArrayList();
    private final static ArrayList<String> mFilterIds = new ArrayList();

    private static Filter mInstance = new Filter();

    public static Filter get() {
        return mInstance;
    }

    private Filter() {
        mFilterInstaces.add("");
        mFilterIds.add("");
    }

    public boolean pass(String instance,String id){
        for (String i: mFilterInstaces) {
            if (i.equals(instance)){
                return false;
            }
        }
        for (String i: mFilterIds) {
            if (i.equals(id)){
                return false;
            }
        }
        return true;
    }
}
