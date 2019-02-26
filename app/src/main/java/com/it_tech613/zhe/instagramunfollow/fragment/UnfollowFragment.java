package com.it_tech613.zhe.instagramunfollow.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.it_tech613.zhe.instagramunfollow.activity.NavigationActivity;
import com.it_tech613.zhe.instagramunfollow.utils.UnfollowAdapter;
import com.it_tech613.zhe.instagramunfollow.utils.DelayedProgressDialog;
import com.it_tech613.zhe.instagramunfollow.utils.PreferenceManager;
import com.it_tech613.zhe.instagramunfollow.R;
import com.it_tech613.zhe.instagramunfollow.utils.UnfollowStatus;
import com.it_tech613.zhe.instagramunfollow.utils.UnfollowingDlg;

import java.util.Random;

import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class UnfollowFragment extends Fragment {
    DelayedProgressDialog spinner = new DelayedProgressDialog();
    Toolbar toolbar;
    SearchView searchView;
    ImageView userProfileImage;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recycler;
    UnfollowAdapter adapter;
    ImageView unfollow;
    Random random = new Random();
    UnfollowingDlg unfollowingDlg;
    TextView free_credit_d,reward_credit_d;
    AsyncTask unfollowingTask;
    boolean isUnfollowingActive = false;
    AdView adView;
    private InterstitialAd mInterstitialAd;
    LinearLayout unfollow_btn_group;
    public static UnfollowFragment newInstance() {
        UnfollowFragment fragment = new UnfollowFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_unfollow, container, false);
//        loadInterstitialAd();
        initialize(view);
        return view;
    }

    @SuppressLint("CommitPrefEdits")
    private void initialize(View view) {
        ImageView refresh=getActivity().findViewById(R.id.refresh);
        ImageView str_logo=getActivity().findViewById(R.id.str_logo);
        str_logo.setVisibility(View.GONE);
        refresh.setVisibility(View.GONE);
        LinearLayout lay=getActivity().findViewById(R.id.lay);
        lay.setGravity(Gravity.END);
        unfollow_btn_group=getActivity().findViewById(R.id.unfollow_btn_group);
        unfollow_btn_group.setVisibility(View.GONE);
        LinearLayout lay_followus=getActivity().findViewById(R.id.lay_followus);
        lay_followus.setVisibility(View.GONE);
        Button first50=getActivity().findViewById(R.id.first10);
        first50.setText(getResources().getString(R.string.first_ten));
        Button last50=getActivity().findViewById(R.id.last10);
        last50.setText(getResources().getString(R.string.last_ten));
        Button cancel=getActivity().findViewById(R.id.cancel_action);
        first50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfollow_btn_group.setVisibility(View.GONE);
                isUnfollowingActive = true;
                unfollow.setClickable(false);
                unfollowTen(true);
            }
        });
        last50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfollow_btn_group.setVisibility(View.GONE);
                isUnfollowingActive = true;
                unfollow.setClickable(false);
                unfollowTen(false);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfollow_btn_group.setVisibility(View.GONE);
            }
        });
        unfollow=getActivity().findViewById(R.id.unfollow);
        unfollow.setVisibility(View.VISIBLE);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isUnfollowingActive)
                    unfollowingTask.cancel(false);
                searchView.setQuery("", false);
                searchView.setIconified(true);
                loadData();
            }
        });
        reward_credit_d=view.findViewById(R.id.reward_credit);
        free_credit_d=view.findViewById(R.id.free_credit);
        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUnfollowingActive)
                    unfollowingTask.cancel(false);
                else {
                    unfollow_btn_group.setVisibility(View.VISIBLE);
                }
            }
        });
        toolbar = view.findViewById(R.id.toolbar);

        adapter = new UnfollowAdapter() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void unfollow(final int position, final InstagramUserSummary userSummary) {
                loadInterstitialAd();
                NavigationActivity.instance().kpHUD.setLabel("Unfollowing...").setCancellable(false).show();
                new AsyncTask<Void, Void, UnfollowStatus>() {
                    @Override
                    protected UnfollowStatus doInBackground(Void... voids) {
                        final UnfollowStatus result=PreferenceManager.unfollow(userSummary);
                        return result;
                    }

                    @Override
                    protected void onPostExecute(UnfollowStatus result) {
                        super.onPostExecute(result);
                        if (result==UnfollowStatus.success){
                            Toast.makeText(getActivity().getBaseContext(),getString(R.string.unfollow_1)+userSummary.getFull_name(),Toast.LENGTH_LONG).show();
                            adapter.removeItem(position);
                        } else if (result==UnfollowStatus.failed)
                            Toast.makeText(getActivity().getBaseContext(),getString(R.string.unfollow_1_fail)+userSummary.getFull_name(),Toast.LENGTH_LONG).show();
                        else  if (result==UnfollowStatus.limited)
                            Toast.makeText(getActivity().getBaseContext(),getString(R.string.limited_unfollow_alert),Toast.LENGTH_LONG).show();
                        else  if (result==UnfollowStatus.limited_per_hour)
                            Toast.makeText(getActivity().getBaseContext(),getString(R.string.limited_unfollow_one_hour_alert),Toast.LENGTH_LONG).show();
                        else  if (result==UnfollowStatus.limited_per_12hours)
                            Toast.makeText(getActivity().getBaseContext(),getString(R.string.limited_unfollow_12_hours_alert),Toast.LENGTH_LONG).show();
                        showCredits();
                        NavigationActivity.instance().kpHUD.dismiss();
                    }
                }.execute();
            }
        };
        loadData();
        recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setItemViewCacheSize(30);
        recycler.setDrawingCacheEnabled(true);
        recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(adapter);

        userProfileImage = getActivity().findViewById(R.id.profile);
//        Glide.with(getActivity())
//                .load(PreferenceManager.currentUser.getProfile_pic_url())
//                .transition(DrawableTransitionOptions.withCrossFade())
//                .into(userProfileImage);
        searchView = getActivity().findViewById(R.id.searchView);
        searchView.setVisibility(View.VISIBLE);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userProfileImage.setVisibility(View.GONE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                userProfileImage.setVisibility(View.VISIBLE);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (isUnfollowingActive)
                    unfollowingTask.cancel(false);

                adapter.filter(newText);
                return false;
            }
        });
        adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);
//        adView.setAdListener(new AdListener() {
//
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//                Toast.makeText(getActivity(), "onAdLoaded()", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdOpened() {
//                super.onAdOpened();
//                Toast.makeText(getActivity(), "onAdOpened()", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
//                Toast.makeText(getActivity(), "onAdClosed()", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
//                Toast.makeText(getActivity(), "onAdFailedToLoad()"+i, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                super.onAdLeftApplication();
//                Toast.makeText(getActivity(), "onAdLeftApplication()", Toast.LENGTH_SHORT).show();
//            }
//        });
        showCredits();
    }

    @SuppressLint("SetTextI18n")
    private void showCredits(){
        free_credit_d.setText(String.format(getResources().getString(R.string.free_credit_d),PreferenceManager.getFreeLimit()));
        reward_credit_d.setText(String.format(getResources().getString(R.string.reward_credit_d),PreferenceManager.getRewardLimit()));
    }

    private void loadInterstitialAd() {
        mInterstitialAd = new InterstitialAd(getActivity());
        //TODO Interstitial ID
        mInterstitialAd.setAdUnitId("ca-app-pub-7166764673125229/4811981390");
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
//                Toast.makeText(getActivity(), "onAdLoaded()", Toast.LENGTH_SHORT).show();
                if(mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Toast.makeText(getActivity(), "onAdFailedToLoad()", Toast.LENGTH_SHORT).show();
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onPause() {
        // This method should be called in the parent Activity's onPause() method.
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        // This method should be called in the parent Activity's onResume() method.
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        // This method should be called in the parent Activity's onDestroy() method.
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @SuppressLint("StaticFieldLeak")
    void unfollowTen(final boolean is_first) {
        loadInterstitialAd();
        InstagramUserSummary[] userIds;
        unfollowingDlg =new UnfollowingDlg(getActivity());
        unfollowingDlg.show();
        unfollowingDlg.setCancelable(false);
        if (is_first) userIds = adapter.getFirstFiftyUnfollowList();
        else userIds = adapter.getLastFiftyUnfollowList();
        final int[] unfollowed_number = {0};
        unfollowingTask = new AsyncTask<InstagramUserSummary[], Void, UnfollowStatus>() {
            @Override
            protected UnfollowStatus doInBackground(InstagramUserSummary[]... longs) {
                for (int i=0; i<longs[0].length; i++) {
                    final InstagramUserSummary user=longs[0][i];
                    if (isCancelled())
                        return null;
                    try {
                        final int finalI = i;
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                // Stuff that updates the UI
                                unfollowingDlg.setProgress(user.getFull_name(), finalI);
                            }
                        });

                        Thread.sleep(random.nextInt(10) * 1000);
                        final UnfollowStatus result=PreferenceManager.unfollow(user);
                        if (result==UnfollowStatus.success){
                            if (is_first)adapter.removeItem(0);
                            else adapter.removeItem(adapter.getItemCount()-1);
                            unfollowed_number[0] +=1;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showCredits();
                                }
                            });

                        }else  {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    if (result==UnfollowStatus.limited){
                                        Toast.makeText(getActivity().getBaseContext(),getString(R.string.limited_unfollow_alert),Toast.LENGTH_LONG).show();
                                    } else if (result==UnfollowStatus.failed){
                                        Toast.makeText(getActivity().getBaseContext(),getString(R.string.unfollow_1_fail)+user.getFull_name(),Toast.LENGTH_LONG).show();
                                    } else if (result==UnfollowStatus.limited_per_hour){
                                        Toast.makeText(getActivity().getBaseContext(),getString(R.string.limited_unfollow_one_hour_alert),Toast.LENGTH_LONG).show();
                                    } else if (result==UnfollowStatus.limited_per_12hours){
                                        Toast.makeText(getActivity().getBaseContext(),getString(R.string.limited_unfollow_12_hours_alert),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        if (result==UnfollowStatus.limited){
                            return UnfollowStatus.limited;
                        }else if (result==UnfollowStatus.limited_per_hour){
                            return UnfollowStatus.limited_per_hour;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return UnfollowStatus.failed;
                    }
                }
                return UnfollowStatus.success;
            }

            @Override
            protected void onProgressUpdate(Void... voids) {
            }

            @Override
            protected void onPreExecute() {
                adapter.setBlocked(true);
            }

            @Override
            protected void onPostExecute(UnfollowStatus aVoid) {
                onStop(aVoid);
            }

            @Override
            protected void onCancelled() {
                onStop(UnfollowStatus.failed);
            }

            void onStop(UnfollowStatus is_successed) {
                if (unfollowingDlg !=null && unfollowingDlg.isShowing()) unfollowingDlg.dismiss();
                unfollow.setClickable(true);
                isUnfollowingActive = false;
                adapter.setBlocked(false);
                if (is_successed == UnfollowStatus.success) {
                    if (unfollowed_number[0]!=0) Toast.makeText(getContext(),String.format(getString(R.string.unfollow_10),unfollowed_number[0]),Toast.LENGTH_LONG).show();
                } else if (is_successed == UnfollowStatus.failed) Toast.makeText(getContext(),getString(R.string.unfollow_10_fail),Toast.LENGTH_LONG).show();
            }
        }.execute(userIds);
    }

    private void loadData() {
        adapter.setUsers(PreferenceManager.unfollowers, true);
        refreshLayout.setRefreshing(false);
    }

    boolean doubleBackToExitPressedOnce = false;

    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
            return;
        }

        if (doubleBackToExitPressedOnce) {
            getActivity().onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(getActivity(), R.string.backPressed, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
