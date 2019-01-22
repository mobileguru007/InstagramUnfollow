package com.it_tech613.zhe.instagramunfollow.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.it_tech613.zhe.instagramunfollow.R;

import java.util.ArrayList;

import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public abstract class WhitelistAdapter extends RecyclerView.Adapter<WhitelistAdapter.UserViewHolder> {

    private ArrayList<InstagramUserSummary> fullList = new ArrayList<>();
    private ArrayList<InstagramUserSummary> users = new ArrayList<>();

    private boolean isBlocked = false;
    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public void setUsers(ArrayList<InstagramUserSummary> list, boolean firstLoad) {
        users = list;
        notifyDataSetChanged();

        if (firstLoad) {
            fullList.clear();
            fullList.addAll(users);
        }
    }

    public void removeItem(final int position) {
        int fullListPosition = -1;
        for (int i = 0; i < fullList.size(); i++)
            if (fullList.get(i).getPk() == users.get(position).getPk()) {
                fullListPosition = i;
                break;
            }
        if (fullListPosition != -1) {
            fullList.remove(fullListPosition);
            PreferenceManager.whitelist.remove(users.get(position));

        }

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, users.size(), null);
    }

    public void filter(final String s) {
        if (s.equals(""))
            setUsers(fullList, false);
        ArrayList<InstagramUserSummary> result = new ArrayList<>();
        for (InstagramUserSummary i : fullList)
            if (i.getUsername().contains(s.toLowerCase()))
                result.add(i);
        setUsers(result, false);
    }

    private Intent openProfile(final String username, Context context) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            if (context.getPackageManager().getPackageInfo("com.instagram.android", 0) != null) {
                intent.setData(Uri.parse("http://instagram.com/_u/" + username));
                intent.setPackage("com.instagram.android");
                return intent;
            }
        } catch (PackageManager.NameNotFoundException ignored) {}
        intent.setData(Uri.parse("http://instagram.com/" + username));
        return intent;
    }

    public abstract void remove_whitelist(final long pk, InstagramUserSummary userSummary);

    @Override
    public void onBindViewHolder(final UserViewHolder holder, final int i) {
        holder.username.setText(users.get(i).getUsername());
        Glide.with(holder.imageView.getContext())
                .load(users.get(i).getProfile_pic_url())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView);

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBlocked)
                    return;

                final int position = holder.getAdapterPosition();
                long id=users.get(position).getPk();
                InstagramUserSummary userSummary=users.get(i);
                removeItem(position);
                remove_whitelist(id, userSummary);
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = holder.layout.getContext();
                context.startActivity(openProfile(users.get(holder.getAdapterPosition()).getUsername(),
                        context));
            }
        });
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_person, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        ImageView imageView;
        TextView username;
        Button button;
        Button add_to_whitelist;
        UserViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            imageView = itemView.findViewById(R.id.person_photo);
            username = itemView.findViewById(R.id.cvUsername);
            button = itemView.findViewById(R.id.button);
            add_to_whitelist=itemView.findViewById(R.id.add_to_whitelist);
            add_to_whitelist.setVisibility(View.GONE);
            button.setText(itemView.getContext().getString(R.string.remove));
        }
    }
}