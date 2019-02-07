package com.it_tech613.zhe.instagramunfollow.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.it_tech613.zhe.instagramunfollow.activity.LoginActivity;
import com.it_tech613.zhe.instagramunfollow.activity.NavigationActivity;
import com.it_tech613.zhe.instagramunfollow.utils.PreferenceManager;
import com.it_tech613.zhe.instagramunfollow.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShareFragment extends Fragment {
    TextView num_followers,num_following,username;//num_posts,
    CircleImageView user_profile;

    public static ShareFragment newInstance() {
        ShareFragment fragment = new ShareFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_share, container, false);
        ImageView refresh=getActivity().findViewById(R.id.refresh);
        ImageView unfollow=getActivity().findViewById(R.id.unfollow);
        ImageView str_logo=getActivity().findViewById(R.id.str_logo);
        SearchView searchView=getActivity().findViewById(R.id.searchView);
        str_logo.setVisibility(View.GONE);
        searchView.setVisibility(View.GONE);
        refresh.setVisibility(View.VISIBLE);
        refresh.setClickable(false);
        unfollow.setVisibility(View.VISIBLE);
        unfollow.setClickable(false);
        LinearLayout unfollow_btn_group=getActivity().findViewById(R.id.unfollow_btn_group);
        unfollow_btn_group.setVisibility(View.GONE);


        user_profile=view.findViewById(R.id.user_profile);
        num_followers=view.findViewById(R.id.num_followers);
        num_following=view.findViewById(R.id.num_following);
//        num_posts=view.findViewById(R.id.num_posts);
        username=view.findViewById(R.id.username);
        if (PreferenceManager.currentUser==null) {
            getActivity().startActivityForResult(new Intent(getActivity(), LoginActivity.class), NavigationActivity.loginRequestCode);
        }
        RequestOptions requestOptions = new RequestOptions();
        if (PreferenceManager.currentUser==null){
            PreferenceManager.logoutManager();
            NavigationActivity.instance().startLoginActivity(false);
        }
        requestOptions.placeholder(R.drawable.profile);
        requestOptions.error(R.drawable.profile);
        Glide.with(getActivity())
                .load(PreferenceManager.currentUser.getProfile_pic_url())
                .into(user_profile);
        num_followers.setText(String.valueOf(PreferenceManager.followers.size()));
        num_following.setText(String.valueOf(PreferenceManager.following.size()));
//        num_posts.setText(String.valueOf(PreferenceManager.feedItems.size()));
        username.setText(PreferenceManager.getUserName());
        share();
        ConstraintLayout share_device=view.findViewById(R.id.share_device);
        share_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
        return view;
    }

    private void share(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this app to Unfollow people on Instagram faster. And it’s Free! https://play.google.com/store/apps/details?id=com.it_tech613.zhe.instagramunfollow");
        getActivity().startActivity(Intent.createChooser(sendIntent, "Share to..."));
    }
}
