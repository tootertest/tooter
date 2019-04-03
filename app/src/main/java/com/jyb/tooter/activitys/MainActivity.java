package com.jyb.tooter.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jyb.tooter.R;
import com.jyb.tooter.adapter.PagerFragmentAdapter;
import com.jyb.tooter.dialog.DialogModal;
import com.jyb.tooter.entity.Account;
import com.jyb.tooter.fragments.FragmentNotfications;
import com.jyb.tooter.fragments.FragmentStatus;
import com.jyb.tooter.job.Job;
import com.jyb.tooter.job.maneger.JobManager;
import com.jyb.tooter.model.Toot;
import com.jyb.tooter.model.User;
import com.jyb.tooter.statics.SharedVar;
import com.jyb.tooter.utils.Pt;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private static final String TAG = "DEBUG_MainActivity";

    private String[] mTags = new String[]{"Home", "Notifications", "Public", "Federated"};

    private TabLayout mTabs;
    private FloatingActionButton mFloating;
    private ViewPager mPager;
    private Drawer mDrawer;
    private ImageButton mBtnDrawer;

    public ImageButton getDrawer() {
        return mBtnDrawer;
    }

    public TabLayout getTabs() {
        return mTabs;
    }

    public FloatingActionButton getFloating() {
        return mFloating;
    }

    public ViewPager getPager() {
        return mPager;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mBtnDrawer = findViewById(R.id.main_drawer);
        mTabs = findViewById(R.id.main_tabs);
        mFloating = findViewById(R.id.main_floating);
        mPager = findViewById(R.id.main_pager);

        initTabs();
        initPager();
        initDrawer();
        initFloating();
        ready();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    private void ready() {

        Pt.d("ready");

        final Activity activity = this;

        FragmentManager manager = getSupportFragmentManager();
        final DialogModal dialogModal = new DialogModal();
        dialogModal.setLayout(R.layout.dialog_layout_load)
//                .setDimAmount(1)
                .show(manager, null);

        Job job = new Job() {

            Response<Account> sResponse;

            @Override
            public void onStart() {
                super.onStart();
                Pt.d("onStart get ACCOUNT");
            }

            @Override
            public void onSend() {
                super.onSend();
                Pt.d("onSend get ACCOUNT");
                try {
                    sResponse = getMastApi()
                            .accountVerifyCredentials()
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onReceive() {
                super.onReceive();
                if (sResponse != null && sResponse.isSuccessful()) {
                    SharedVar.ACCOUNT = sResponse.body();
                    dialogModal.dismiss();
                    Pt.d("onReceive get ACCOUNT succuse");
                } else {
                    Pt.d("onReceive get ACCOUNT fail");
                    Toast.makeText(activity, getString(R.string.network_request_timeout)
                            , Toast.LENGTH_SHORT).show();
                    JobManager.get()
                            .add(this.reset());
                }
            }

            @Override
            public void onTimeout() {
                super.onTimeout();
                Pt.d("onTimeout get ACCOUNT");
                Toast.makeText(activity, getString(R.string.network_request_timeout)
                        , Toast.LENGTH_SHORT).show();
                JobManager.get()
                        .add(this.reset());
            }
        };
        JobManager.get()
                .add(job);
    }

    private void initTabs() {
        for (String title : mTags) {
            TabLayout.Tab tab = mTabs.newTab();
            tab.setTag(title);
            mTabs.addTab(tab);
        }

        Drawable drawableHome = new IconicsDrawable(this
                , FontAwesome.Icon.faw_home)
                .sizeDp(24)
                .color(Color.BLACK);

        Drawable drawableNotifications = new IconicsDrawable(this
                , FontAwesome.Icon.faw_bell)
                .sizeDp(24)
                .color(Color.BLACK);

        Drawable drawableLocal = new IconicsDrawable(this
                , FontAwesome.Icon.faw_users)
                .sizeDp(24)
                .color(Color.BLACK);

        Drawable drawablePublic = new IconicsDrawable(this,
                FontAwesome.Icon.faw_globe)
                .sizeDp(24)
                .color(Color.BLACK);

        mTabs.getTabAt(0).setIcon(drawableHome);
        mTabs.getTabAt(1).setIcon(drawableNotifications);
        mTabs.getTabAt(2).setIcon(drawableLocal);
        mTabs.getTabAt(3).setIcon(drawablePublic);

        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() != mPager.getCurrentItem()) {
                    mPager.setCurrentItem(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initPager() {

        ArrayList<Fragment> list = new ArrayList<>();
        list.add(new FragmentStatus(this, FragmentStatus.StatusType.HOME));
        list.add(new FragmentNotfications(this));
        list.add(new FragmentStatus(this, FragmentStatus.StatusType.LOCAL));
        list.add(new FragmentStatus(this, FragmentStatus.StatusType.PUBLIC));

        FragmentManager manager = getSupportFragmentManager();
        PagerFragmentAdapter adapter = new PagerFragmentAdapter(manager, list);
        mPager.setAdapter(adapter);
        mPager.setOffscreenPageLimit(5);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != mTabs.getSelectedTabPosition()) {
                    mTabs.getTabAt(position).select();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initDrawer() {
//
        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .build();

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(header)
                .build();

        Drawable icon = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_bars)
                .color(Color.BLACK)
                .sizeDp(24);


        String listStr[] = new String[]
                {
                        getString(R.string.drawer_profile),//profile
                        getString(R.string.drawer_filter),//search
                        getString(R.string.drawer_search),//search
                        getString(R.string.drawer_direct),//direct
                        getString(R.string.drawer_favourites),//favourites
                        getString(R.string.drawer_lists),//lists
                        getString(R.string.drawer_preferences),//preferences
                        getString(R.string.drawer_loginout),//loginout
                        getString(R.string.drawer_info),//info
                        getString(R.string.drawer_exit)//exit
                };

        HashMap listItem = new HashMap();
        listItem.put(getString(R.string.drawer_loginout), true);
        listItem.put(getString(R.string.drawer_exit), true);

        FontAwesome.Icon listIcon[] = new FontAwesome.Icon[]
                {
                        FontAwesome.Icon.faw_address_book,
                        FontAwesome.Icon.faw_filter,
                        FontAwesome.Icon.faw_search,
                        FontAwesome.Icon.faw_envelope,
                        FontAwesome.Icon.faw_star,
                        FontAwesome.Icon.faw_list,
                        FontAwesome.Icon.faw_cog,
                        FontAwesome.Icon.faw_user_times,
                        FontAwesome.Icon.faw_question,
                        FontAwesome.Icon.faw_sign_out
                };


        for (int i = 0; i < listStr.length; i++) {
            String itemStr = listStr[i];

            Object value = listItem.get(itemStr);
            if (value == null) {
                value = false;
            } else {
                value = true;
            }
            boolean v = (boolean) value;

            PrimaryDrawerItem item = new PrimaryDrawerItem()
                    .withName(listStr[i])
                    .withIcon(listIcon[i])
                    .withIdentifier(i)
                    .withSelectable(false)
                    .withTag(listStr[i])
                    .withEnabled(v);
            mDrawer.addItem(item);
        }
        mBtnDrawer.setImageDrawable(icon);
        mBtnDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer();
            }
        });

        mDrawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                String clickTag = drawerItem.getTag().toString();
                if (clickTag.equals(getString(R.string.drawer_loginout))) {
                    User user = new User();
                    user.domain = "";
                    user.token = "";
                    saveStoreUser(user);
                    finish();
                }
                if (clickTag.equals(getString(R.string.drawer_exit))) {
                    finish();
                }
                return false;
            }
        });
    }

    private void initFloating() {

        Drawable drawerFloating = new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_plus)
                .color(Color.WHITE)
                .sizeDp(48);

        mFloating.setImageDrawable(drawerFloating);

        final Activity activity = this;
        mFloating.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TootActivity.class);
                Toot toot = new Toot();
                String gson = new Gson().toJson(toot);
                intent.putExtra("toot", gson);
                startActivity(intent);
            }
        });
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
