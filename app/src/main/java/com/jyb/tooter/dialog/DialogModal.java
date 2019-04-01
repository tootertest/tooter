package com.jyb.tooter.dialog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.jyb.tooter.utils.Pt;

public class DialogModal extends DialogFragment {

    protected boolean mModal;
    protected int mLayout;
    protected float mDimAmount;
    protected boolean mCleanBackground;
    protected View mView;
    protected ViewGroup.LayoutParams mParams;
    protected boolean mMax;

    public DialogModal() {
        mModal = true;
        mLayout = 0;
        mDimAmount = 0.3f;
        mCleanBackground = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMax){
            getDialog().getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(mLayout, null, false);
//        mView.setLayoutParams(mParams);
        onBindView();
        onInitView();
        onInitEvent();
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = mDimAmount;
        window.setAttributes(windowParams);
    }

    public DialogModal setModal(boolean modal) {
        mModal = modal;
        return this;
    }

    public DialogModal setLayout(int layout) {
        mLayout = layout;
        return this;
    }

    public DialogModal setParams(ViewGroup.LayoutParams params) {
        mParams = params;
        return this;
    }

    public DialogModal setDimAmount(float dimAmount) {
        mDimAmount = dimAmount;
        return this;
    }

    public DialogModal setCleanBackground(boolean clean) {
        mCleanBackground = clean;
        return this;
    }

    public DialogModal setMax(boolean max) {
        mMax = max;
        return this;
    }

    protected void onBindView() {
    }

    protected void onInitView() {
        if (mCleanBackground) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    protected void onInitEvent() {
        if (mModal) {
            getDialog().setCancelable(false);
            getDialog().setCanceledOnTouchOutside(false);
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            });
        }

    }
}
