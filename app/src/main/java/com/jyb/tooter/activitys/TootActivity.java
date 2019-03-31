package com.jyb.tooter.activitys;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jyb.tooter.R;
import com.jyb.tooter.dialog.DialogModalEmoji;
import com.jyb.tooter.entity.Status;
import com.jyb.tooter.job.Job;
import com.jyb.tooter.job.maneger.JobManager;
import com.jyb.tooter.statics.SharedVar;
import com.jyb.tooter.utils.HtmlUtils;
import com.jyb.tooter.utils.Pt;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.IOException;

import retrofit2.Response;

public class TootActivity extends BaseActivity {

    private final static String TAG = "DEBUG_TootActivity";

    TextView mTextAcct;

    EditText mInputEdit;

    Button mBtnSend;

    ImageButton mBtnEmoji;

    ImageButton mBtnMedia;

    ImageButton mBtnImage;

    Status mStatus;
    String mReplyId = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toot);
        mTextAcct = findViewById(R.id.toot_acct);
        mInputEdit = findViewById(R.id.toot_input_edit);
        mBtnSend = findViewById(R.id.toot_btn_send);
        mBtnEmoji = findViewById(R.id.toot_insert_emoji);
        mBtnMedia = findViewById(R.id.toot_insert_media);
        mBtnImage = findViewById(R.id.toot_insert_image);

        String acct = "";
        String json = (String) getIntent().getSerializableExtra("status");
        if (json != null) {
            mStatus = new Gson().fromJson(json, Status.class);
            mReplyId = mStatus.id;
            acct = "@" + mStatus.account.acct;
        }

        mTextAcct.setText(acct);

        final String finalAcct = acct;
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "";
                text += finalAcct;
                text += " ";
                text += mInputEdit.getText();
                text += "\n ";
                text += "\n ";
                text += "\n ";
                text += "\n ";
                text += "\n ";
                text += getString(R.string.use);
                String m = mShared.getString(SharedVar.TOKEN, null);
                final String finalText = text;
                Job job = new Job() {

                    Response<Status> sResponse;

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSend() {
                        super.onSend();
                        try {
                            sResponse = getMastApi().postStatus(finalText, mReplyId, null, null, null, null)
                                    .execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onReceive() {
                        super.onReceive();
                        if (sResponse != null && sResponse.isSuccessful()) {
                            Pt.d("TootActivity send message seccuse");
                        } else {
                            Pt.d("TootActivity send message fail");
                        }
                    }

                    @Override
                    public void onTimeout() {
                        super.onTimeout();
                        Pt.d("TootActivity send message onTimeout");
                    }
                };

                JobManager.instance()
                        .addAnsyc(job);
                mBtnSend.setEnabled(false);
                finish();
            }
        });

        Drawable drawableEmoji = new IconicsDrawable(this
                , FontAwesome.Icon.faw_meh_o)
                .color(Color.GRAY)
                .paddingDp(8)
                .sizeDp(48);
        mBtnEmoji.setBackground(drawableEmoji);

        final TootActivity avtivity = this;
        mBtnEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                DialogModalEmoji dialog = new DialogModalEmoji(avtivity);
                dialog.show(manager, null);
            }
        });


        Drawable drawableMedia = new IconicsDrawable(this
                , FontAwesome.Icon.faw_film)
                .color(Color.GRAY)
                .paddingDp(8)
                .sizeDp(48);
        mBtnMedia.setBackground(drawableMedia);

//        mBtnMedia.setOnClickListener(v -> {
//            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
//            DialogModalEmoji dialog = new DialogModalEmoji(this);
//            dialog.show(manager, null);
//        });


        Drawable drawableImage = new IconicsDrawable(this
                , FontAwesome.Icon.faw_file_image_o)
                .color(Color.GRAY)
                .paddingDp(8)
                .sizeDp(48);
        mBtnImage.setBackground(drawableImage);
        mBtnImage.setEnabled(false);

//        mBtnImage.setOnClickListener(v -> {
//            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
//            DialogModalEmoji dialog = new DialogModalEmoji(this);
//            dialog.show(manager, null);
//        });

    }

    @SuppressLint("SetTextI18n")
    public void insertCharSequence(String str) {
        mInputEdit.setText(mInputEdit.getText() + str);
    }

    private void showEmojiDialog() {

    }
}
