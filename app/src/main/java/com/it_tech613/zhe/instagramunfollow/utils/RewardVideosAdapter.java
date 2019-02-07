package com.it_tech613.zhe.instagramunfollow.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.it_tech613.zhe.instagramunfollow.R;

import java.util.ArrayList;

import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public abstract class RewardVideosAdapter extends RecyclerView.Adapter<RewardVideosAdapter.UserViewHolder> {

    private ArrayList<Boolean> list_redeemed=new ArrayList<>();
    public abstract void watchRewardVideo(final int position);

    @Override
    public void onBindViewHolder(final UserViewHolder holder, final int i) {
        if (!list_redeemed.get(i)) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.white));
            holder.watch.setText(holder.itemView.getContext().getString(R.string.watch));
            holder.watch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    watchRewardVideo(i);
                }
            });
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    watchRewardVideo(i);
                }
            });
        }
        else {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.grey_transparent));
            holder.watch.setText(holder.itemView.getContext().getString(R.string.redeemed));
            holder.watch.setClickable(false);
            holder.layout.setClickable(false);
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_person, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return list_redeemed.size();
    }

    public void setList_redeemed(ArrayList<Boolean> list_redeemed) {
        this.list_redeemed = list_redeemed;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        ImageView imageView;
        TextView username;
        Button watch;
        Button add_to_whitelist;
        UserViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            imageView = itemView.findViewById(R.id.person_photo);
            imageView.setVisibility(View.GONE);
            username = itemView.findViewById(R.id.cvUsername);
            username.setText(itemView.getContext().getString(R.string.reward_description));
            watch = itemView.findViewById(R.id.button);
            add_to_whitelist=itemView.findViewById(R.id.add_to_whitelist);
            add_to_whitelist.setVisibility(View.GONE);
        }
    }
}