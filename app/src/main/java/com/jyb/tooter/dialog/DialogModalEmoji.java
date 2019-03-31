package com.jyb.tooter.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.jyb.tooter.R;
import com.jyb.tooter.activitys.BaseActivity;
import com.jyb.tooter.activitys.TootActivity;
import com.jyb.tooter.utils.EmojiStyle;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import io.reactivex.annotations.NonNull;
import android.support.annotation.Nullable;

@SuppressLint("ValidFragment")
public class DialogModalEmoji extends DialogFragment {

    ImageButton mBtnUp;

    ImageButton mBtnDown;

    ImageButton mBtnClose;

    TableLayout mLayout;

    private TootActivity mActivity;
    private View mView;
    private int mPage;

    private int mDisplayRow;
    private int mDisplayColumn;

    public DialogModalEmoji(TootActivity activity) {
        super();
        mActivity = activity;
        mDisplayRow = 4 - 1;
        mDisplayColumn = 10 - 1;
        mPage = 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.dialog_modal_emoji, container, false);
        bindView();
        initView();
        initEvent();
        return mView;
    }

    private void bindView() {
        mBtnUp = mView.findViewById(R.id.dialog_modal_emoji_up);
        mBtnDown = mView.findViewById(R.id.dialog_modal_emoji_down);
        mBtnClose = mView.findViewById(R.id.dialog_modal_emoji_close);
        mLayout = mView.findViewById(R.id.dialog_modal_emoji_layout);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0f;
        window.setAttributes(windowParams);
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    }

    private void initView() {

//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Drawable drawableUp = new IconicsDrawable(mActivity)
                .icon(FontAwesome.Icon.faw_arrow_left)
                .color(Color.BLACK)
                .sizeDp(16);

        Drawable drawableDown = new IconicsDrawable(mActivity)
                .icon(FontAwesome.Icon.faw_arrow_right)
                .color(Color.BLACK)
                .sizeDp(16);

        Drawable drawableClose = new IconicsDrawable(mActivity)
                .icon(FontAwesome.Icon.faw_window_close)
                .color(Color.BLACK)
                .sizeDp(16);

        mBtnUp.setImageDrawable(drawableUp);
        mBtnDown.setImageDrawable(drawableDown);
        mBtnClose.setImageDrawable(drawableClose);

        displayEmoji(mPage);
    }

    private void initEvent() {

        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);

        mBtnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPage > 0) {
                    mLayout.removeAllViews();
                    mPage -= 1;
                    displayEmoji(mPage);
                }
            }
        });

        mBtnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPage < 2) {
                    mLayout.removeAllViews();
                    mPage += 1;
                    displayEmoji(mPage);
                }
            }
        });

        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

    }

    private void displayEmoji(int page) {
        EmojiStyle emojiStyle = new EmojiStyle();
        int style = 0;
        for (int i = 0; i < mDisplayRow; i++) {
            TableRow row = new TableRow(this.getContext());
            TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            for (int j = 0; j < mDisplayColumn; j++) {
                int index = (page * 4 + i) * 8 + j;
                final String emoji;
                if (index < emojiStyle.getLength(style)) {
                    emoji = emojiStyle.getEmojo(style, index);

                } else {
                    emoji = "";
                }
                Button btn = new Button(this.getContext());
                btn.setBackgroundColor(Color.TRANSPARENT);
                btn.setText(emoji);
                btn.setLayoutParams(params);
                btn.setTextSize(28);
                btn.setSoundEffectsEnabled(false);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.insertCharSequence(emoji);
                    }
                });
                row.addView(btn);
            }
            mLayout.addView(row);
        }
    }
}
