package com.jyb.tooter.dialog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.jyb.tooter.R;
import com.jyb.tooter.activitys.TootActivity;
import com.jyb.tooter.utils.EmojiStyle;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

@SuppressLint("ValidFragment")
public class DialogModalEmoji extends DialogModal {

    ImageButton mBtnLast;

    ImageButton mBtnNext;

    ImageButton mBtnClose;

    TableLayout mLayoutEmoji;

    private TootActivity mActivity;
    private int mPage;

    private int mDisplayRow;
    private int mDisplayColumn;

    public DialogModalEmoji(TootActivity activity) {
        super();
        mActivity = activity;
        mDisplayRow = 4;
        mDisplayColumn = 8;
        mPage = 0;
    }

    @Override
    protected void onBindView() {
        super.onBindView();
        mBtnLast = mView.findViewById(R.id.dialog_modal_emoji_last);
        mBtnNext = mView.findViewById(R.id.dialog_modal_emoji_next);
        mBtnClose = mView.findViewById(R.id.dialog_modal_emoji_close);
        mLayoutEmoji = mView.findViewById(R.id.dialog_modal_layout_emoji);
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        Drawable drawableUp = new IconicsDrawable(getActivity())
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

        mBtnLast.setImageDrawable(drawableUp);
        mBtnNext.setImageDrawable(drawableDown);
        mBtnClose.setImageDrawable(drawableClose);

        displayEmoji(mPage);
    }

    @Override
    protected void onInitEvent() {
        super.onInitEvent();
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);

        mBtnLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPage > 0) {
                    mLayoutEmoji.removeAllViews();
                    mPage -= 1;
                    displayEmoji(mPage);
                }
            }
        });

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPage < 2) {
                    mLayoutEmoji.removeAllViews();
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
                int index = (page * mDisplayRow + i) * mDisplayColumn + j;
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
            mLayoutEmoji.addView(row);
        }
    }
}
