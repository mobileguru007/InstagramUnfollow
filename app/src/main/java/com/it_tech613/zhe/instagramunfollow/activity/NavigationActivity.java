package com.it_tech613.zhe.instagramunfollow.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.it_tech613.zhe.instagramunfollow.utils.DailyReceiver;
import com.it_tech613.zhe.instagramunfollow.utils.ConfirmExitDlg;
import com.it_tech613.zhe.instagramunfollow.utils.DelayedProgressDialog;
import com.it_tech613.zhe.instagramunfollow.utils.LoadingDlg;
import com.it_tech613.zhe.instagramunfollow.utils.PreferenceManager;
import com.it_tech613.zhe.instagramunfollow.R;
import com.it_tech613.zhe.instagramunfollow.fragment.MyAccountFragment;
import com.it_tech613.zhe.instagramunfollow.fragment.RewardsFragment;
import com.it_tech613.zhe.instagramunfollow.fragment.ShareFragment;
import com.it_tech613.zhe.instagramunfollow.fragment.UnfollowFragment;
import com.it_tech613.zhe.instagramunfollow.fragment.WhiteListFragment;
import com.it_tech613.zhe.instagramunfollow.utils.WeeklyReceiver;
import com.kaopiz.kprogresshud.KProgressHUD;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.niekirk.com.instagram4android.Instagram4Android;
import dev.niekirk.com.instagram4android.requests.InstagramGetUserFollowersRequest;
import dev.niekirk.com.instagram4android.requests.InstagramGetUserFollowingRequest;
import dev.niekirk.com.instagram4android.requests.InstagramUserFeedRequest;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramGetUserFollowersResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramLoginResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class NavigationActivity extends AppCompatActivity{
    private ConfirmExitDlg confirmExitDlg;
    AlarmManager alarmManager;
    AHBottomNavigation bottomNavigation;
    private PendingIntent pendingIntent;
    private static NavigationActivity inst;
    public static final int loginRequestCode=0;
    boolean notificationVisible=false;
//    NoSwipePager viewPager;
//    BottomBarAdapter pagerAdapter;
    private final String purchaseItemName="sku_01";
    DelayedProgressDialog spinner = new DelayedProgressDialog();
    LoadingDlg loadingDlg;
    CircleImageView userProfileImage;
    String username="";
    public KProgressHUD kpHUD;
    private InterstitialAd mInterstitialAd;

    public static NavigationActivity instance() {
        return inst;
    }
    @Override
    protected void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
//        setSupportActionBar(toolbar);
//        //noinspection ConstantConditions
//        getSupportActionBar().setTitle("Bottom Navigation");
        PreferenceManager.setAlarm();
        kpHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setLabel("Unfollowing")
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);
        loadingDlg=new LoadingDlg(this);
        if (PreferenceManager.instagram!=null) {
            setUpGUI();
            return;
        }
        userProfileImage =findViewById(R.id.profile);
        if (savedInstanceState != null)
            login(savedInstanceState.getString("username"),
                    savedInstanceState.getString("password"));
        else if (PreferenceManager.isSaved())
            login(PreferenceManager.getUserName(),
                    PreferenceManager.getPassword());
        else
            startLoginActivity(false);
//        if(Build.VERSION.SDK_INT >= 23) {
//            if (!Settings.canDrawOverlays(this)) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                        Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent, REQUEST_OVERLAY);
//            }
//        }
    }

    private void loadInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        //TODO Interstitial ID
        mInterstitialAd.setAdUnitId("ca-app-pub-7166764673125229/4811981390");
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if(mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Toast.makeText(NavigationActivity.this, "onAdFailedToLoad()", Toast.LENGTH_SHORT).show();
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void setUpGUI() {
//        setupViewPager();
        bottomNavigation=(AHBottomNavigation) findViewById(R.id.bottom_navigation);
        setupBottomNavBehaviors();
        setupBottomNavStyle();

//        createFakeNotification();
        bottomNavigation.removeAllItems();
        bottomNavigation.refresh();
        addBottomNavigationItems();

        // Setting the very 1st item as home screen.

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                Fragment selectedFragment=null;
                ImageView refresh=findViewById(R.id.refresh);
                switch (position){
                    case 0:
                        if(bottomNavigation.getCurrentItem()==0) break;
                        bottomNavigation.removeAllItems();
                        bottomNavigation.refresh();
                        selectedFragment=UnfollowFragment.newInstance();
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_0), R.drawable.menuicon_selected, R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_1), R.drawable.add_contact, R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_2), R.drawable.share, R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_3), R.drawable.badge ,R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_4), R.drawable.profile,R.color.colorPrimary));
                        break;
                    case 1:
                        if(bottomNavigation.getCurrentItem()==1) break;
                        bottomNavigation.removeAllItems();
                        bottomNavigation.refresh();
                        selectedFragment=WhiteListFragment.newInstance();
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_0), R.drawable.menuicon, R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_1), R.drawable.add_contact_selected, R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_2), R.drawable.share, R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_3), R.drawable.badge ,R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_4), R.drawable.profile,R.color.colorPrimary));
                        break;
                    case 2:
                        if(bottomNavigation.getCurrentItem()==2) break;
                        bottomNavigation.removeAllItems();
                        bottomNavigation.refresh();
                        selectedFragment=ShareFragment.newInstance();
                        refresh.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(spinner.isAdded())
                                {
                                    return; //or return false/true, based on where you are calling from
                                }
                                spinner.show(getSupportFragmentManager(), "login");
                                loadData_following(false);
                            }
                        });
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_0), R.drawable.menuicon, R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_1), R.drawable.add_contact, R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_2), R.drawable.share_selected, R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_3), R.drawable.badge ,R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_4), R.drawable.profile,R.color.colorPrimary));
                        break;
                    case 3:
                        if(bottomNavigation.getCurrentItem()==3) break;
                        bottomNavigation.removeAllItems();
                        bottomNavigation.refresh();
                        selectedFragment=RewardsFragment.newInstance();
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_0), R.drawable.menuicon, R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_1), R.drawable.add_contact, R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_2), R.drawable.share, R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_3), R.drawable.badge_selected ,R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_4), R.drawable.profile,R.color.colorPrimary));
                        break;
                    case 4:
                        if(bottomNavigation.getCurrentItem()==4) break;
                        bottomNavigation.removeAllItems();
                        bottomNavigation.refresh();
                        selectedFragment=MyAccountFragment.newInstance();
                        refresh.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(spinner.isAdded())
                                {
                                    return; //or return false/true, based on where you are calling from
                                }
                                spinner.show(getSupportFragmentManager(), "login");
                                loadData_following(false);
                            }
                        });
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_0), R.drawable.menuicon, R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_1), R.drawable.add_contact, R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_2), R.drawable.share, R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_3), R.drawable.badge ,R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_4), R.drawable.profile_selected,R.color.colorPrimary));
                        break;
                    default:
                        bottomNavigation.removeAllItems();
                        bottomNavigation.refresh();
                        selectedFragment=UnfollowFragment.newInstance();
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_0), R.drawable.menuicon_selected , R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_1), R.drawable.add_contact , R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_2), R.drawable.share, R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_3), R.drawable.badge ,R.color.colorPrimary));
                        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_4), R.drawable.profile ,R.color.colorPrimary));
                        break;
                }
               if (selectedFragment!=null)getSupportFragmentManager().beginTransaction().replace(R.id.frame,selectedFragment).addToBackStack("tag").commitAllowingStateLoss();
//                viewPager.setCurrentItem(position);
                Log.e("selected_tab",position+"");
                // remove notification badge
//                int lastItemPos = bottomNavigation.getItemsCount() - 1;
//                if (notificationVisible && position == lastItemPos)
//                    bottomNavigation.setNotification(new AHNotification(), lastItemPos);

                return true;
            }
        });
        bottomNavigation.setCurrentItem(4);
    }

    public void startLoginActivity(boolean show_faq) {
        Intent intent=new Intent(this, LoginActivity.class);
        intent.putExtra("show_faq",show_faq);
        startActivityForResult(intent, loginRequestCode);
    }

    @SuppressLint("StaticFieldLeak")
    private void login(final String username, final String password) {
        this.username=username;
//        loadInterstitialAd();
        loadingDlg.show();
        loadingDlg.setCancelable(false);
//        spinner.show(getSupportFragmentManager(), "login");

        new AsyncTask<Void, Void, Void>() {
            InstagramLoginResult loginResult;
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    PreferenceManager.instagram = Instagram4Android.builder()
                            .username(username)
                            .password(password)
                            .build();
                    PreferenceManager.instagram.setup();
                    loginResult = PreferenceManager.instagram.login();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(loginResult!=null) Log.e("loginresult",loginResult.toString());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (loginResult==null || !PreferenceManager.instagram.isLoggedIn()) {
//                    spinner.cancel();
                    if (loadingDlg!=null && loadingDlg.isShowing()) loadingDlg.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.loginError, Toast.LENGTH_LONG).show();
                    startLoginActivity(true);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDlg.setMsg_1(getString(R.string.login_success));
                        }
                    });
                    PreferenceManager.currentUser=loginResult.getLogged_in_user();
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.profile);
                    requestOptions.error(R.drawable.profile);
                    if (NavigationActivity.this.isDestroyed()) return;
                    Glide.with(NavigationActivity.this)
                            .load(PreferenceManager.currentUser.getProfile_pic_url())
                            .into(userProfileImage);
                    loadData_following(true);
//                    tvUsername.setText(username);
                    if (!PreferenceManager.isSaved()) {
                        PreferenceManager.setIsSaved(true);
                        PreferenceManager.setUserName(username);
                        PreferenceManager.setPassword(password);
                        PreferenceManager.checkLimit();
                    }
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame);
        if(fragment == null) {
            confirmExitDlg =new ConfirmExitDlg(
                    NavigationActivity.this,
                    new ConfirmExitDlg.DialogNumberListener() {
                @Override
                public void OnYesClick(Dialog dialog) {
                    confirmExitDlg.dismiss();
                    finish();
                    System.exit(0);
                }

                @Override
                public void OnCancelClick(Dialog dialog) {
                    confirmExitDlg.dismiss();
                }
            },
                    getResources().getString(R.string.warning),
                    getResources().getString(R.string.exit_alert),
                    false);
            confirmExitDlg.show();
        }
    }

//    public void showRateUs(){
//        confirmExitDlg =new ConfirmExitDlg(
//                NavigationActivity.this,
//                new ConfirmExitDlg.DialogNumberListener() {
//                    @Override
//                    public void OnYesClick(Dialog dialog) {
//                        confirmExitDlg.dismiss();
//                        final Intent intent = new Intent(Intent.ACTION_VIEW);
//                        try {
//                            if (getPackageManager().getPackageInfo("com.android.vending", 0) != null) {
//                                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.it_tech613.zhe.instagramunfollow"));
//                                intent.setPackage("com.android.vending");
//                            }
//                        } catch (PackageManager.NameNotFoundException ignored) {}
//                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.it_tech613.zhe.instagramunfollow"));
//                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void OnCancelClick(Dialog dialog) {
//                        confirmExitDlg.dismiss();
//                    }
//                },
//                getResources().getString(R.string.rateus_title),
//                getResources().getString(R.string.rateus_body),
//                false);
//        confirmExitDlg.show();
//    }
    @Override
    protected void onStop() {
        if (username!=null) if (!username.equals("")) PreferenceManager.setLastLogin();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (PreferenceManager.instagram != null && PreferenceManager.instagram.isLoggedIn()) {
            outState.putString("username", PreferenceManager.instagram.getUsername());
            outState.putString("password", PreferenceManager.instagram.getPassword());
        }
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("StaticFieldLeak")
    void loadData_following(final boolean go_on) {
        new AsyncTask<Void, Void, Void>() {
            ArrayList<InstagramUserSummary> following = new ArrayList<>();
            @Override
            protected Void doInBackground(Void... voids) {
                InstagramGetUserFollowersResult result;
                if (PreferenceManager.instagram==null) {
                    cancel(true);
                    startLoginActivity(false);
                }
                final long userId = PreferenceManager.instagram.getUserId();
                try {
                    result = PreferenceManager.instagram.sendRequest(new InstagramGetUserFollowingRequest(userId));
                    following.addAll(result.getUsers());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDlg.setMsg_1(following.size()+" followings scanned...");
                        }
                    });
                    while (result.getNext_max_id() != null){
                        result = PreferenceManager.instagram.sendRequest(new InstagramGetUserFollowingRequest(userId, result.getNext_max_id()));
                        following.addAll(result.getUsers());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingDlg.setMsg_1(following.size()+" followings scanned...");
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                PreferenceManager.following=following;
                if (go_on)loadData_follower();
                else endGetData();
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    void loadData_follower() {
        new AsyncTask<Void, Void, Void>() {
            ArrayList<InstagramUserSummary> followers = new ArrayList<>();
            @Override
            protected Void doInBackground(Void... voids) {
                InstagramGetUserFollowersResult result;
                if (PreferenceManager.instagram==null) {
                    cancel(true);
                    startLoginActivity(false);
                }
                final long userId = PreferenceManager.instagram.getUserId();
                try {
                    result = PreferenceManager.instagram.sendRequest(new InstagramGetUserFollowersRequest(userId));
                    if(result==null) return null;
                    followers.addAll(result.getUsers());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDlg.setMsg_2(followers.size()+" followers scanned...");
                        }
                    });
                    while (result.getNext_max_id() != null){
                        result = PreferenceManager.instagram.sendRequest(new InstagramGetUserFollowersRequest(userId, result.getNext_max_id()));
                        if(result==null) return null;
                        followers.addAll(result.getUsers());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingDlg.setMsg_2(followers.size()+" followers scanned...");
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                PreferenceManager.followers=followers;
                endGetData();
            }
        }.execute();
    }

    private void endGetData() {
        ArrayList<InstagramUserSummary> unfollowers = new ArrayList<>(PreferenceManager.following);
        for (InstagramUserSummary i : PreferenceManager.following) {
            Set<String> whitelist=PreferenceManager.getWhitelist_ids();
            ArrayList<String> whitelist_ids=new ArrayList<>(whitelist);
            for (String id:whitelist_ids){
                if (Long.valueOf(id)==i.getPk()) {
                    PreferenceManager.whitelist.add(i);
                    unfollowers.remove(i);
                    break;
                }
            }
            for (InstagramUserSummary j : PreferenceManager.followers) {
                if (i.equals(j)) {
                    unfollowers.remove(i);
                    break;
                }
            }
        }
        PreferenceManager.unfollowers=unfollowers;
        if (loadingDlg!=null && loadingDlg.isShowing()) loadingDlg.dismiss();
        setUpGUI();
        spinner.cancel();
    }

    @SuppressLint("StaticFieldLeak")
    void loadData_post() {
        new AsyncTask<Void, Void, Void>() {
            ArrayList<InstagramFeedItem> feedItems = new ArrayList<>();
            @Override
            protected Void doInBackground(Void... voids) {
                InstagramFeedResult posts;
                final long userId = PreferenceManager.instagram.getUserId();
                try {
                    posts = PreferenceManager.instagram.sendRequest(new InstagramUserFeedRequest());
                    feedItems.addAll(posts.getItems());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                PreferenceManager.feedItems=feedItems;
                Log.e("feed_number",String.valueOf(feedItems.size()));
//                spinner.cancel();
                if (loadingDlg!=null && loadingDlg.isShowing()) loadingDlg.dismiss();
                setUpGUI();
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            startLoginActivity(false);
        }
        else{
            if (requestCode==loginRequestCode)
                login(data.getStringExtra("username"),
                    data.getStringExtra("password"));
        }

    }

    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }

//    private void setupViewPager() {
//        viewPager = (NoSwipePager) findViewById(R.id.viewpager);
//        viewPager.setPagingEnabled(false);
//        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());
//
//        pagerAdapter.addFragments(UnfollowFragment.newInstance());
//        pagerAdapter.addFragments(WhiteListFragment.newInstance());
//        pagerAdapter.addFragments(ShareFragment.newInstance());
//        pagerAdapter.addFragments(RewardsFragment.newInstance());
//        pagerAdapter.addFragments(MyAccountFragment.newInstance());
//
//        viewPager.setAdapter(pagerAdapter);
//    }

//    private void createFakeNotification() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                AHNotification notification = new AHNotification.Builder()
//                        .setText("1")
//                        .setBackgroundColor(Color.YELLOW)
//                        .setTextColor(Color.BLACK)
//                        .build();
//                // Adding notification to last item.
//                bottomNavigation.setNotification(notification, bottomNavigation.getItemsCount() - 1);
//                notificationVisible = true;
//            }
//        }, 1000);
//    }

    private void addBottomNavigationItems() {
        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_0), R.drawable.menuicon, R.color.colorPrimary));
        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_1), R.drawable.add_contact, R.color.colorPrimary));
        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_2), R.drawable.share, R.color.colorPrimary));
        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_3), R.drawable.badge ,R.color.colorPrimary));
        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.bottomnav_title_4), R.drawable.profile,R.color.colorPrimary));
    }

    public static Drawable drawableFromUrl(String url_str) throws IOException {
        Bitmap x;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
//        URL url = new URL(url_str);
//        x=BitmapFactory.decodeStream((InputStream)url.getContent());
        HttpURLConnection connection = (HttpURLConnection) new URL(url_str).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(x);
    }

    public void setupBottomNavBehaviors() {
        bottomNavigation.setBehaviorTranslationEnabled(true);
        /*
        Before enabling this. Change MainActivity theme to MyTheme.TranslucentNavigation in
        AndroidManifest.
        Warning: Toolbar Clipping might occur. Solve this by wrapping it in a LinearLayout with a top
        View of 24dp (status bar size) height.
         */
//        bottomNavigation.setTranslucentNavigationEnabled(false);
    }

    /**
     * Adds styling properties to {@link AHBottomNavigation}
     */
    private void setupBottomNavStyle() {
        /*
        Set Bottom Navigation colors. Accent color for active item,
        Inactive color when its view is disabled.
        Will not be visible if setColored(true) and default current item is set.
         */
        bottomNavigation.setDefaultBackgroundColor(fetchColor(R.color.colorPrimary));
        bottomNavigation.setAccentColor(Color.WHITE);
        bottomNavigation.setSelectedBackgroundVisible(false);
        bottomNavigation.setInactiveColor(fetchColor(R.color.colorBackground));

//        // Colors for selected (active) and non-selected items.
//        bottomNavigation.setColoredModeColors(Color.WHITE,
//                fetchColor(R.color.grey));
//
//        //  Enables Reveal effect
//        bottomNavigation.setColored(true);

        //  Displays item Title always (for selected and non-selected items)
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);
    }

}
