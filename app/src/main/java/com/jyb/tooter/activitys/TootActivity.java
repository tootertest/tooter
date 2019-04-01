package com.jyb.tooter.activitys;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.jyb.tooter.R;
import com.jyb.tooter.dialog.DialogModalEmoji;
import com.jyb.tooter.entity.Status;
import com.jyb.tooter.job.Job;
import com.jyb.tooter.job.maneger.JobManager;
import com.jyb.tooter.model.Toot;
import com.jyb.tooter.statics.SharedVar;
import com.jyb.tooter.utils.HtmlUtils;
import com.jyb.tooter.utils.Pt;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.IOException;

import retrofit2.Response;

public class TootActivity extends BaseActivity {

    private final static String TAG = "DEBUG_TootActivity";

//    TextView mTextAcct;

    EditText mInputEdit;

    Button mBtnSend;


    ImageButton mBtnPrivacy;
    ImageButton mBtnEmoji;
    ImageButton mBtnMedia;
    ImageButton mBtnImage;

    Toot mToot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toot);
//        mTextAcct = findViewById(R.id.toot_acct);
        mInputEdit = findViewById(R.id.toot_edit_input);
        mBtnSend = findViewById(R.id.toot_btn_send);
        mBtnEmoji = findViewById(R.id.toot_btn_emoji);
        mBtnMedia = findViewById(R.id.toot_btn_media);
        mBtnImage = findViewById(R.id.toot_btn_image);
        mBtnPrivacy = findViewById(R.id.toot_btn_privacy);

        String gson = (String) getIntent().getSerializableExtra("toot");

        mToot = new Gson().fromJson(gson, Toot.class);

        Spanned acctsSpd;
        String acctsHtml = "";

        mToot.getFillterMentions().add("@" + SharedVar.ACCOUNT.acct);

        for (String mention : mToot.getMentions()) {
            acctsHtml += "<a href=\"\">" + mention + "</a>&nbsp";
        }
//        for (int i = 0; i < mToot.getMentions().size(); i++) {
//            String mention = mToot.getMentions().get(i);
//            acctsHtml += "<a href=\"\">" + mention + "</a>&nbsp";
//        }
        acctsSpd = HtmlUtils.fromHtml(acctsHtml);

        mInputEdit.setText(acctsSpd);
        mInputEdit.setSelection(acctsSpd.length());

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToot.text = "";
                mToot.text += mInputEdit.getText();
                mToot.text += "\n ";
                mToot.text += "\n ";
                mToot.text += "\n ";
                mToot.text += "\n ";
                mToot.text += "\n ";
                mToot.text += getString(R.string.use);

                if (mInputEdit.getText().length()==0){
                    return;
                }
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
                            sResponse = getMastApi()
                                    .postStatus(mToot.text,
                                            mToot.replyId,
                                            mToot.spoilerTextt,
                                            mToot.visibility,
//                                            Toot.Visibility.PRIVATE,
                                            mToot.sensitive,
                                            mToot.getMedias())
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

                JobManager.get()
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
                dialog.setDimAmount(0)
                        .setLayout(R.layout.dialog_modal_emoji)
                        .setMax(true)
                        .setCleanBackground(false)
                        .show(manager, null);
            }
        });


        Drawable drawableMedia = new IconicsDrawable(this
                , FontAwesome.Icon.faw_film)
                .color(Color.LTGRAY)
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
                .color(Color.LTGRAY)
                .paddingDp(8)
                .sizeDp(48);
        mBtnImage.setBackground(drawableImage);
        mBtnImage.setEnabled(false);

        Drawable drawablePrivacy = new IconicsDrawable(this
                , FontAwesome.Icon.faw_globe)
                .color(Color.LTGRAY)
                .paddingDp(8)
                .sizeDp(48);
        mBtnPrivacy.setBackground(drawablePrivacy);

        mBtnPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
//        mBtnPrivacy.setEnabled(false);

//        mBtnImage.setOnClickListener(v -> {
//            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
//            DialogModalEmoji dialog = new DialogModalEmoji(this);
//            dialog.show(manager, null);
//        });

    }

    @SuppressLint("SetTextI18n")
    public void insertCharSequence(String str) {
//        String text = mInputEdit.getText() + str;
//        mInputEdit.setText(text);
//        mInputEdit.setSelection(text.length());
//        Pt.d(mInputEdit.getSelectionEnd() + "");
        Editable editable = mInputEdit.getText();
        editable.append(str);
        mInputEdit.setText(editable);
        mInputEdit.setSelection(editable.length());
        Pt.d(mInputEdit.getSelectionEnd() + "");
    }

}
