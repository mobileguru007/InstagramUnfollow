package com.it_tech613.zhe.instagramunfollow.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.it_tech613.zhe.instagramunfollow.R;
import com.it_tech613.zhe.instagramunfollow.activity.NavigationActivity;
import com.it_tech613.zhe.instagramunfollow.utils.PreferenceManager;
import com.it_tech613.zhe.instagramunfollow.utils.RewardVideosAdapter;

import java.util.ArrayList;

public class RewardsFragment extends Fragment {
//    private SpinKitView spin_kit;
    private RewardedVideoAd mRewardedVideoAd;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recycler;
    RewardVideosAdapter adapter;
    int loading_video_position=-1;
    String[] reward_ids={
            "ca-app-pub-7166764673125229/5401936506",
            "ca-app-pub-7166764673125229/7411261981",
            "ca-app-pub-7166764673125229/4091087784",
            "ca-app-pub-7166764673125229/1392648549",
            "ca-app-pub-7166764673125229/6573603286",
//            "ca-app-pub-7166764673125229/6110568859",
//            "ca-app-pub-7166764673125229/4111091473",
            "ca-app-pub-7166764673125229/2741512941"
        };
    public static RewardsFragment newInstance() {
        RewardsFragment fragment = new RewardsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        View view= inflater.inflate(R.layout.fragment_rewards, container, false);
        ImageView refresh=getActivity().findViewById(R.id.refresh);
        ImageView unfollow=getActivity().findViewById(R.id.unfollow);
        ImageView str_logo=getActivity().findViewById(R.id.str_logo);
        SearchView searchView=getActivity().findViewById(R.id.searchView);
        LinearLayout lay=getActivity().findViewById(R.id.lay);
        lay.setGravity(Gravity.END);
        str_logo.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.GONE);
        unfollow.setVisibility(View.GONE);
        LinearLayout unfollow_btn_group=getActivity().findViewById(R.id.unfollow_btn_group);
        unfollow_btn_group.setVisibility(View.GONE);
        AdView adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);
//        spin_kit=view.findViewById(R.id.spin_kit);
//        spin_kit.setVisibility(View.GONE);
        // Use an activity context to get the rewarded video instance.


        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        adapter=new RewardVideosAdapter() {
            @Override
            public void watchRewardVideo(int position) {
//                spin_kit.setVisibility(View.VISIBLE);
                NavigationActivity.instance().kpHUD.setLabel("Loading Reward ads...").show();
                loadRewardedVideoAd(reward_ids[position]);
                loading_video_position=position;
            }
        };

        adapter.setList_redeemed(PreferenceManager.getListRedeemed());
        recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setItemViewCacheSize(30);
        recycler.setDrawingCacheEnabled(true);
        recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(adapter);
        return view;
    }

    private void loadRewardedVideoAd(String reward_ad_id) {
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
//                Toast.makeText(getActivity(), "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
                if (mRewardedVideoAd.isLoaded()) {
//                    spin_kit.setVisibility(View.GONE);
                    NavigationActivity.instance().kpHUD.dismiss();
                    mRewardedVideoAd.show();
                }
            }

            @Override
            public void onRewardedVideoAdOpened() {
//                Toast.makeText(getActivity(), "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onRewardedVideoStarted() {
//                Toast.makeText(getActivity(), "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onRewardedVideoAdClosed() {
//                Toast.makeText(getActivity(), "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                ArrayList<Boolean> list=PreferenceManager.getListRedeemed();
                list.remove(loading_video_position);
                list.add(loading_video_position,true);
                PreferenceManager.setListRedeemed(list);
                adapter.setList_redeemed(PreferenceManager.getListRedeemed());
                PreferenceManager.setRewardLimit(PreferenceManager.getRewardLimit()+50);
                Toast.makeText(getActivity(),
                        String.format(getString(R.string.reward_success_alert),PreferenceManager.getFreeLimit()+PreferenceManager.getRewardLimit()),
                        Toast.LENGTH_SHORT).show();
                mRewardedVideoAd.destroy(getContext());
                // Reward the user.
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
//                Toast.makeText(getActivity(), "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
//                spin_kit.setVisibility(View.GONE);
                NavigationActivity.instance().kpHUD.dismiss();
                Toast.makeText(getActivity(), getString(R.string.reward_fail_alert), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onRewardedVideoCompleted() {
//                Toast.makeText(getActivity(), "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();

            }
        });
        mRewardedVideoAd.loadAd(reward_ad_id,
                new AdRequest.Builder().build());
    }

//    @Override
//    public void onResume() {
//        if (mRewardedVideoAd != null) mRewardedVideoAd.resume(getActivity());
//        super.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        if (mRewardedVideoAd != null) mRewardedVideoAd.pause(getActivity());
//        super.onPause();
//    }
//
//    @Override
//    public void onDestroy() {
//        mRewardedVideoAd.destroy(getActivity());
//        super.onDestroy();
//    }
}
