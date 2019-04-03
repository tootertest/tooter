package com.jyb.tooter.dialog;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jyb.tooter.R;
import com.jyb.tooter.utils.Pt;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.ortiz.touchview.TouchImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("ValidFragment")
public class DialogModalImage extends DialogModal {

    TouchImageView mTouchImageView;
//    ImageButton mImageClose;
    TextView mLoadProgress;
    Bitmap mBitmap;
    String mUrl;
    AtomicBoolean mIsDownload;

    Handler mHandler;

    @SuppressLint("HandlerLeak")
    public DialogModalImage() {
        super();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        mTouchImageView.setImageBitmap(mBitmap);
                        mLoadProgress.setText("");
                        break;
                    case 1:
                        mLoadProgress.setText(msg.obj.toString());
                        break;
                    case 2:
                        break;
                }
            }
        };
    }

    public DialogModalImage setUrl(String url) {
        mUrl = url;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void onBindView() {
        super.onBindView();
        mTouchImageView = mView.findViewById(R.id.touch_image_view);
//        mImageClose = mView.findViewById(R.id.touch_image_close);
        mLoadProgress = mView.findViewById(R.id.touch_image_loadprogress);
    }

    @Override
    protected void onInitView() {
        super.onInitView();

//        IconicsDrawable iconClose = new IconicsDrawable(this.getContext())
//                .icon(FontAwesome.Icon.faw_window_close)
//                .color(Color.LTGRAY)
//                .sizeDp(40);

//        mImageClose.setImageDrawable(iconClose);
//        mImageClose.setBackground(null);

        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmap  = Bitmap.createBitmap(2,2,config);

        mTouchImageView.setImageBitmap(bitmap);

        mIsDownload = new AtomicBoolean(true);

        downloadImage();

    }

    @Override
    protected void onInitEvent() {
        super.onInitEvent();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                mIsDownload.set(false);
                dismiss();
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
    }

    private void downloadImage() {
//        String url = "https://media.w3.org/2010/05/sintel/trailer.mp4";
//        String url = "https://cmx.social/system/media_attachments/files/001/764/188/original/c7fa915d95df169f.png?1553743766";
//        String url = "https://img-blog.csdn.net/20180926102959839?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1ZWhlbmd5YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70";
        String url = mUrl;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().get().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException { //将响应数据转化为输入流数据
                InputStream inputStream; //将输入流数据转化为Bitmap位图数据
                inputStream = response.body().byteStream();
                long fileSize = response.body().contentLength();
                long loadSize = 1024;
                long readSize = 0;
                long offset = 0;
                byte[] data = new byte[(int) fileSize];
                Pt.d("fileSize: " + fileSize);
                while (mIsDownload.get()) {
                    long lastSize = fileSize - offset;
                    if (lastSize < loadSize) {
                        readSize = inputStream.read(data, (int) offset, (int) lastSize);
                    } else {
                        readSize = inputStream.read(data, (int) offset, (int) loadSize);
                    }
                    if (readSize <= 0) {
//                        Pt.d("readSize: " + readSize);
                        break;
                    }
                    offset += readSize;
                    float loadProgress = (float) offset / fileSize;
                    Message message = new Message();
                    String str = String.valueOf(loadProgress * 100);
                    String strList[] = str.split("\\.");
                    String newstr = strList[0];
                    message.obj = newstr+"%";
                    message.what = 1;
                    mHandler.sendMessage(message);
                    Pt.d("loaded: " + newstr);
                }
                if (mIsDownload.get()){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, (int) fileSize);
                    mBitmap = bitmap;
                    mHandler.sendEmptyMessage(0);
                }else {
                    if (!call.isCanceled()) {
                        call.cancel();
                    }
                    Pt.d("stop download");
                }
            }
        });

    }
}
