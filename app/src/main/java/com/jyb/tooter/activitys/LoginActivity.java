package com.jyb.tooter.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jyb.tooter.R;
import com.jyb.tooter.dialog.DialogModal;
import com.jyb.tooter.entity.AccessToken;
import com.jyb.tooter.entity.OAuth;
import com.jyb.tooter.job.Job;
import com.jyb.tooter.job.maneger.JobManager;
import com.jyb.tooter.nerwork.MastodonAPI;
import com.jyb.tooter.statics.SharedVar;
import com.jyb.tooter.model.User;
import com.jyb.tooter.utils.Pt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "DEBUG_LoginActivity";

    private String mDomain;
    private String mInstace;
    private String mClientId;
    private String mClientSecret;
    private String mToken;
//    private boolean mRemember;

    private MastodonAPI mApi;

    private SharedPreferences mShared;

    private User mUser;

    private DialogModal mLoadDialog;
    private FragmentManager mManager;


    EditText mTextInput;
    Button mBtnLogin;
    TextView mBtnAbout;
    CheckBox mCheckRemember;
    Activity mActivity;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTextInput = findViewById(R.id.login_domain);
        mBtnLogin = findViewById(R.id.login_btn);
        mBtnAbout = findViewById(R.id.login_about);
        mCheckRemember = findViewById(R.id.login_remember);

        mShared = PreferenceManager.getDefaultSharedPreferences(this);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String instance = mTextInput.getText().toString();
                requestOauth(instance);
            }
        });
        mBtnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAbout();
            }
        });

//        mShared = PreferenceManager.getDefaultSharedPreferences(this);
//        mShared.edit()
//                .putString(SharedVar.DOMAIN, "")
//                .putString(SharedVar.CLIENT_ID, "")
//                .putString(SharedVar.CLIENT_SECRET, "")
//                .putString(SharedVar.TOKEN, "")
//                .putBoolean(SharedVar.REMEMBER, false)
//                .apply();
        mManager = getSupportFragmentManager();
        mLoadDialog = new DialogModal();
        mLoadDialog.setLayout(R.layout.dialog_layout_load);

        mActivity = this;
    }

    @Override
    protected void onStart() {
        super.onStart();

//        String token = mShared.getString(SharedVar.TOKEN, "");

        boolean remember = mShared.getBoolean(SharedVar.REMEMBER, false);
        mCheckRemember.setChecked(remember);

        mUser = new User();
        mUser = readStoreUser();

        if (mUser != null) {
            mShared.edit()
                    .putString(SharedVar.DOMAIN, mUser.domain)
                    .putString(SharedVar.TOKEN, String.valueOf(mUser.token))
                    .apply();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        onOauthCallBack();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mUser == null) {
            mShared.edit()
                    .putString(SharedVar.DOMAIN, mDomain)
                    .putString(SharedVar.CLIENT_ID, mClientId)
                    .putString(SharedVar.CLIENT_SECRET, mClientSecret)
                    .putString(SharedVar.TOKEN, mToken)
                    .putBoolean(SharedVar.REMEMBER, mCheckRemember.isChecked())
                    .apply();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mUser == null) {
            outState.putString(SharedVar.DOMAIN, mDomain);
            outState.putString(SharedVar.CLIENT_ID, mClientId);
            outState.putString(SharedVar.CLIENT_SECRET, mClientSecret);
            outState.putString(SharedVar.TOKEN, mToken);
            outState.putBoolean(SharedVar.REMEMBER, mCheckRemember.isChecked());
        }
        super.onSaveInstanceState(outState);
    }

    void requestOauth(String instance) {
        final String clientName = getString(R.string.app_name);
        final String appWeb = getString(R.string.app_website);

        mInstace = instance;
        mDomain = "https://" + mInstace;

        Pattern pattern = Pattern.compile("^[A-Za-z]+\\.[A-Za-z]+$");

//        Pt.d(""+pattern.matcher(get).find());
        if (!pattern.matcher(instance).find()) {
            return;
        }

        bindUrl(mDomain);

        Job job = new Job() {

            Response<OAuth> sResponse;

            @Override
            public void onStart() {
                super.onStart();
                mLoadDialog.show(mManager,null);
                lockInput(false);
            }

            @Override
            public void onSend() {
                super.onSend();
                try {
                    sResponse = mApi
                            .oauth(clientName, getOauthCallBackUri(), SharedVar.SCOPES, appWeb)
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onReceive() {
                super.onReceive();
                if (sResponse != null && sResponse.isSuccessful()) {
                    OAuth oauth = sResponse.body();
                    if (oauth != null) {
                        httpOauthLogin(oauth);
                        Pt.d("requestOauth isSuccessful");
                        mLoadDialog.dismiss();
                    }
                } else {
                    Pt.d("requestOauth unSuccessful");
                    mLoadDialog.dismiss();
                    lockInput(false);
                }
            }

            @Override
            public void onTimeout() {
                super.onTimeout();
                mLoadDialog.dismiss();
                lockInput(false);
            }
        };

        JobManager.get()
                .add(job);
    }

    private void httpOauthLogin(OAuth oauth) {
        String endpoint = MastodonAPI.ENDPOINT_AUTHORIZE;
        String redirectUri = getOauthCallBackUri();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("client_id", oauth.clientId);
        parameters.put("redirect_uri", redirectUri);
        parameters.put("response_type", "code");
        parameters.put("scope", SharedVar.SCOPES);
        String url = mDomain + endpoint + "?" + encodeQueryString(parameters);
        Uri uri = Uri.parse(url);
        Intent viewIntent = new Intent(Intent.ACTION_VIEW, uri);
        mClientId = oauth.clientId;
        mClientSecret = oauth.clientSecret;
//        mRemember = mCheckRemember.isChecked();
        startActivity(viewIntent);
    }

    private void onOauthCallBack() {

        Uri uri = getIntent().getData();
        final String redirectUri = getOauthCallBackUri();

        if (uri != null && uri.toString().startsWith(redirectUri)) {

            final String code = uri.getQueryParameter("code");
//            String error = uri.getQueryParameter("error");

            mDomain = mShared.getString(SharedVar.DOMAIN, null);
            if (code != null && mDomain != null) {

                mClientId = mShared.getString(SharedVar.CLIENT_ID, null);
                mClientSecret = mShared.getString(SharedVar.CLIENT_SECRET, null);

                bindUrl(mDomain);

                Job job = new Job() {

                    Response<AccessToken> sResponse;

                    @Override
                    public void onStart() {
                        super.onStart();
                        mLoadDialog.show(mManager,null);
                        lockInput(false);
                    }

                    @Override
                    public void onSend() {
                        super.onSend();
                        try {
                            sResponse = mApi
                                    .token(mClientId, mClientSecret, redirectUri, code, "authorization_code")
                                    .execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onReceive() {
                        super.onReceive();
                        if (sResponse != null && sResponse.isSuccessful()) {

                            AccessToken token = sResponse.body();

                            mToken = token.accessToken;

                            if (mCheckRemember.isChecked()) {
                                Pt.d("remember user");
                                User user = new User();
                                user.domain = mDomain;
                                user.token = mToken;
                                saveStoreUser(user);
                            } else {
                                Pt.d("no remember user");
                            }
                            Pt.d("onOauthCallBack isSuccessful");
                            Pt.d("token: " + token.accessToken);
                            Intent intent = new Intent(mActivity, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            lockInput(false);
                            mLoadDialog.dismiss();
                            Pt.d("onOauthCallBack unSuccessful");
                        }
                    }

                    @Override
                    public void onTimeout() {
                        super.onTimeout();
                        lockInput(false);
                        mLoadDialog.dismiss();
                    }
                };

                JobManager.get()
                        .add(job);
            }
        }
    }

    private void onAbout() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.login_about_title)
                .setMessage(getString(R.string.login_about_text))
                .setPositiveButton(R.string.login_action_close,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .show();
    }

    private String encodeQueryString(Map<String, String> parameters) {
        StringBuilder s = new StringBuilder();
        String between = "";
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            s.append(between);
            s.append(Uri.encode(entry.getKey()));
            s.append("=");
            s.append(Uri.encode(entry.getValue()));
            between = "&";
        }
        return s.toString();
    }

    private String getOauthCallBackUri() {
        String scheme = getString(R.string.oauth_scheme);
        String host = getString(R.string.oauth_redirect_host);
        return scheme + "://" + host + "/";
    }

    private void bindUrl(String domain) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(domain)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApi = retrofit.create(MastodonAPI.class);
    }

    private void lockInput(boolean lock) {
        lock = !lock;
        mTextInput.setEnabled(lock);
        mBtnLogin.setEnabled(lock);
        mCheckRemember.setEnabled(lock);
        mBtnAbout.setEnabled(lock);
    }

    private User readStoreUser() {
        InputStream inputStream = null;
        try {
            inputStream = openFileInput("user");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (inputStream == null)
            return null;

        int size = 0;
        char[] cjson = new char[1024];
        InputStreamReader reader = new InputStreamReader(inputStream);
        try {
            size = reader.read(cjson);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (size <= 0) {
            return null;
        }

        User user = new Gson().fromJson(String.valueOf(cjson, 0, size), User.class);

        if (user.domain.equals("") || user.token.equals("")) {
            return null;
        }

        return user;
    }

    private User saveStoreUser(User user) {
        OutputStream outputStream;

        try {
            outputStream = openFileOutput("user", Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        if (outputStream == null)
            return null;

        OutputStreamWriter writer = new OutputStreamWriter(outputStream);

//        User user = new User();
        String json = new Gson().toJson(user);

        try {
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return user;
    }

}
