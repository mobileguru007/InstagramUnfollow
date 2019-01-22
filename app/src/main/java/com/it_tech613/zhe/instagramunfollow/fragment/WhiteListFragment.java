package com.it_tech613.zhe.instagramunfollow.fragment;

import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.it_tech613.zhe.instagramunfollow.utils.DelayedProgressDialog;
import com.it_tech613.zhe.instagramunfollow.utils.PreferenceManager;
import com.it_tech613.zhe.instagramunfollow.R;
import com.it_tech613.zhe.instagramunfollow.utils.WhitelistAdapter;

import java.util.ArrayList;
import java.util.Set;

import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class WhiteListFragment extends Fragment {

    ArrayList<InstagramUserSummary> users_whitelist;
    DelayedProgressDialog spinner = new DelayedProgressDialog();
    SwipeRefreshLayout refreshLayout;
    RecyclerView recycler;
    WhitelistAdapter adapter;
    ImageView userProfileImage;
    public static WhiteListFragment newInstance() {
        WhiteListFragment fragment = new WhiteListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_white_list, container, false);
        ImageView refresh=getActivity().findViewById(R.id.refresh);
        ImageView unfollow=getActivity().findViewById(R.id.unfollow);
        ImageView str_logo=getActivity().findViewById(R.id.str_logo);
        LinearLayout lay=getActivity().findViewById(R.id.lay);
        lay.setGravity(Gravity.START);
        LinearLayout unfollow_btn_group=getActivity().findViewById(R.id.unfollow_btn_group);
        unfollow_btn_group.setVisibility(View.GONE);
        final SearchView searchView=getActivity().findViewById(R.id.searchView);
        str_logo.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.VISIBLE);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery("", false);
                searchView.setIconified(true);
            }
        });
        unfollow.setVisibility(View.GONE);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchView.setQuery("", false);
                searchView.setIconified(true);
                loadData();
            }
        });
        users_whitelist=PreferenceManager.whitelist;
        userProfileImage = getActivity().findViewById(R.id.profile);

        adapter=new WhitelistAdapter() {
            @Override
            public void remove_whitelist(long pk, InstagramUserSummary instagramUserSummary) {
                PreferenceManager.removeWhitelist_ids(pk, instagramUserSummary);

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
                adapter.filter(newText);
                return false;
            }
        });
        AdView adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        return view;
    }

    private void loadData() {
        adapter.setUsers(users_whitelist, true);
        spinner.cancel();
        refreshLayout.setRefreshing(false);
    }
}
