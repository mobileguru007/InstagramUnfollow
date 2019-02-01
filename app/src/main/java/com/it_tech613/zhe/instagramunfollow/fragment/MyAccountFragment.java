package com.it_tech613.zhe.instagramunfollow.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.it_tech613.zhe.instagramunfollow.activity.LoginActivity;
import com.it_tech613.zhe.instagramunfollow.activity.NavigationActivity;
import com.it_tech613.zhe.instagramunfollow.utils.ConfirmExitDlg;
import com.it_tech613.zhe.instagramunfollow.utils.PreferenceManager;
import com.it_tech613.zhe.instagramunfollow.R;
import com.it_tech613.zhe.instagramunfollow.utils.UnfollowStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class MyAccountFragment extends Fragment{

    TextView num_followers,num_following,username,num_non_follower, today_unfollowed;//num_posts,
    AdView adView;
    CircleImageView user_profile;
    Button logout;
    ImageView unfollow;
    AsyncTask unfollowingTask;
    boolean isUnfollowingActive = false;
    Random random = new Random();
    private LinearLayout unfollow_btn_group;
    ConfirmExitDlg confirmExitDlg;
    ConstraintLayout constraintLayout;
    View view;
    public static MyAccountFragment newInstance() {
        MyAccountFragment fragment = new MyAccountFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_my_account, container, false);
        ImageView refresh=getActivity().findViewById(R.id.refresh);
        unfollow=getActivity().findViewById(R.id.unfollow);
        ImageView str_logo=getActivity().findViewById(R.id.str_logo);
        SearchView searchView=getActivity().findViewById(R.id.searchView);
        str_logo.setVisibility(View.GONE);
        searchView.setVisibility(View.GONE);
        refresh.setVisibility(View.VISIBLE);
        unfollow.setVisibility(View.VISIBLE);
        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfollow_btn_group.setVisibility(View.VISIBLE);
            }
        });
        unfollow_btn_group=getActivity().findViewById(R.id.unfollow_btn_group);
        unfollow_btn_group.setVisibility(View.GONE);
        LinearLayout lay_followus=getActivity().findViewById(R.id.lay_followus);
        lay_followus.setVisibility(View.VISIBLE);
        Button followus=getActivity().findViewById(R.id.followus);
        Button rateus=getActivity().findViewById(R.id.rateus);
        Button first10=getActivity().findViewById(R.id.first10);
        first10.setText(getResources().getString(R.string.privacy));
        Button last10=getActivity().findViewById(R.id.last10);
        last10.setText(getResources().getString(R.string.faq));
        Button cancel=getActivity().findViewById(R.id.cancel_action);
        first10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfollow_btn_group.setVisibility(View.GONE);
                PreferenceManager.webviewUri="file:///android_asset/html/privacy_policy.html";
                FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame,FaqFragment.newInstance()).addToBackStack("tag").commit();
            }
        });
        last10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfollow_btn_group.setVisibility(View.GONE);
                PreferenceManager.webviewUri="file:///android_asset/html/faq.html";
                FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame,FaqFragment.newInstance()).addToBackStack("tag").commit();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfollow_btn_group.setVisibility(View.GONE);
            }
        });
        followus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfollow_btn_group.setVisibility(View.GONE);
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                try {
                    if (getContext().getPackageManager().getPackageInfo("com.instagram.android", 0) != null) {
                        intent.setData(Uri.parse("https://www.instagram.com/inst_unfollow/"));
                        intent.setPackage("com.instagram.android");
                    }
                } catch (PackageManager.NameNotFoundException ignored) {}
                intent.setData(Uri.parse("https://www.instagram.com/inst_unfollow/"));
                startActivity(intent);
            }
        });
        rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfollow_btn_group.setVisibility(View.GONE);
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                try {
                    if (getContext().getPackageManager().getPackageInfo("com.android.vending", 0) != null) {
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.it_tech613.zhe.instagramunfollow"));
                        intent.setPackage("com.android.vending");
                    }
                } catch (PackageManager.NameNotFoundException ignored) {}
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.it_tech613.zhe.instagramunfollow"));
                startActivity(intent);
            }
        });
        constraintLayout=view.findViewById(R.id.constraintLayout);
        logout=view.findViewById(R.id.logout);
        user_profile=view.findViewById(R.id.user_profile);
        num_followers=view.findViewById(R.id.num_followers);
        num_following=view.findViewById(R.id.num_following);
//        num_posts=view.findViewById(R.id.num_posts);
        username=view.findViewById(R.id.username);
        num_non_follower=view.findViewById(R.id.num_non_follower);
        today_unfollowed=view.findViewById(R.id.today_unfollowed);
        adView=view.findViewById(R.id.adView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivityForResult(new Intent(getActivity(), LoginActivity.class), NavigationActivity.loginRequestCode);
            }
        });
        ArrayList<String> unfollowed_24=PreferenceManager.getUnfollwed24_ids();
        today_unfollowed.setText(String.valueOf(unfollowed_24.size()));
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile);
        requestOptions.error(R.drawable.profile);
        if (PreferenceManager.currentUser!=null){
            Glide.with(getActivity())
                    .load(PreferenceManager.currentUser.getProfile_pic_url())
                    .into(user_profile);
        }
        else user_profile.setImageResource(R.drawable.profile);
        num_followers.setText(String.valueOf(PreferenceManager.followers.size()));
        num_following.setText(String.valueOf(PreferenceManager.following.size()));
//        num_posts.setText(String.valueOf(PreferenceManager.feedItems.size()));
        username.setText(PreferenceManager.getUserName());
        num_non_follower.setText(String.valueOf(PreferenceManager.unfollowers.size()+PreferenceManager.whitelist.size()));
        adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);
        RelativeLayout relativeLayout5=view.findViewById(R.id.relativeLayout5);
        relativeLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goto_upgrade();
            }
        });
        TextView textView6=view.findViewById(R.id.textView6);
        TextView textView7=view.findViewById(R.id.textView7);
        textView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goto_upgrade();
            }
        });
        textView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goto_upgrade();
            }
        });
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goto_upgrade();
            }
        });
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
    }

    private void goto_upgrade() {
        confirmExitDlg =new ConfirmExitDlg(
                getContext(),
                new ConfirmExitDlg.DialogNumberListener() {
                    @Override
                    public void OnYesClick(Dialog dialog) {
                        confirmExitDlg.dismiss();
                    }

                    @Override
                    public void OnCancelClick(Dialog dialog) {
                        confirmExitDlg.dismiss();
                    }
                },
                getResources().getString(R.string.upgrade),
                getResources().getString(R.string.upgrade_description),
                false);
        confirmExitDlg.show();
    }

//    public void Unfollow10() {
//        if (isUnfollowingActive)
//            unfollowingTask.cancel(false);
//        else {
//            unfollow_btn_group.setVisibility(View.VISIBLE);
//        }
//    }

//    @SuppressLint("StaticFieldLeak")
//    void unfollowTen(final boolean is_first) {
//        InstagramUserSummary[] userIds;
//        if (is_first) userIds = getFirstFiftyUnfollowList();
//        else userIds = getLastFiftyUnfollowList();
//        final int[] unfollowed_number = {0};
//        unfollowingTask = new AsyncTask<InstagramUserSummary[], Void, Boolean>() {
//            @Override
//            protected Boolean doInBackground(InstagramUserSummary[]... longs) {
//                for (final InstagramUserSummary user : longs[0]) {
//                    if (isCancelled())
//                        return null;
//                    try {
//                        Thread.sleep(random.nextInt(5) * 1000);
//                        final UnfollowStatus result=PreferenceManager.unfollow(user);
//                        if (result==UnfollowStatus.success){
//                            unfollowed_number[0] +=1;
//                        }else {
//                            getActivity().runOnUiThread(new Runnable() {
//                                public void run() {
//                                    if (result==UnfollowStatus.limited){
//                                        Toast.makeText(getActivity().getBaseContext(),getString(R.string.limited_unfollow_alert),Toast.LENGTH_LONG).show();
//                                    } else if (result==UnfollowStatus.failed){
//                                        Toast.makeText(getActivity().getBaseContext(),getString(R.string.unfollow_1_fail)+user.getFull_name(),Toast.LENGTH_LONG).show();
//                                    }
//                                }
//                            });
//                        }
//                        if (result==UnfollowStatus.limited){
//                            return true;
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        return false;
//                    }
//                }
//                return true;
//            }
//
//            @Override
//            protected void onProgressUpdate(Void... voids) {
//                if (is_first)PreferenceManager.unfollowers.remove(0);
//                else PreferenceManager.unfollowers.remove(PreferenceManager.unfollowers.size()-1);
//            }
//
//            @Override
//            protected void onPreExecute() {
//            }
//
//            @Override
//            protected void onPostExecute(Boolean is_successed) {
//                onStop(is_successed);
//            }
//
//            @Override
//            protected void onCancelled() {
//                onStop(false);
//            }
//
//            void onStop(Boolean is_successed) {
//                unfollow.setClickable(true);
//                isUnfollowingActive = false;
//                if (is_successed) {
//                    if (unfollowed_number[0]!=0) Toast.makeText(getContext(),String.format(getString(R.string.unfollow_10),unfollowed_number[0]),Toast.LENGTH_LONG).show();
//                } else Toast.makeText(getContext(),getString(R.string.unfollow_10_fail),Toast.LENGTH_LONG).show();
//            }
//        }.execute(userIds);
//    }

//    public InstagramUserSummary[] getFirstFiftyUnfollowList() {
//        int count = PreferenceManager.unfollowers.size() >= 10 ? 10 : PreferenceManager.unfollowers.size();
//        if (count == 0)
//            return new InstagramUserSummary[] {new InstagramUserSummary()};
//
//        InstagramUserSummary[] result = new InstagramUserSummary[count];
//        for (int i = 0; i < count; i++)
//            result[i] = PreferenceManager.unfollowers.get(i);
//        return result;
//    }
//
//    public InstagramUserSummary[] getLastFiftyUnfollowList() {
//        int count = PreferenceManager.unfollowers.size() >= 10 ? 10 : PreferenceManager.unfollowers.size();
//        if (count == 0)
//            return new InstagramUserSummary[] {new InstagramUserSummary()};
//
//        InstagramUserSummary[] result = new InstagramUserSummary[count];
//        for (int i = 0; i < count; i++)
//            result[i] = PreferenceManager.unfollowers.get(PreferenceManager.unfollowers.size()-i-1);
//        return result;
//    }

}
