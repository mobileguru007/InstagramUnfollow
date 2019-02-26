package com.it_tech613.zhe.instagramunfollow.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.it_tech613.zhe.instagramunfollow.R;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import dev.niekirk.com.instagram4android.Instagram4Android;
import dev.niekirk.com.instagram4android.requests.InstagramUnfollowRequest;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramLoggedUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class PreferenceManager extends MultiDexApplication {

    public static PreferenceManager mInstance;
    public static SharedPreferences.Editor editor;
    public static SharedPreferences settings;
    public static Instagram4Android instagram;
    public static ArrayList<InstagramUserSummary> followers;
    public static ArrayList<InstagramUserSummary> following;
    public static ArrayList<InstagramUserSummary> unfollowers;
    public static ArrayList<InstagramUserSummary> whitelist;
    public static InstagramLoggedUser currentUser;
    public static ArrayList<InstagramFeedItem> feedItems;
    public static ArrayList<Boolean> list_redeemed=new ArrayList<>();
    public static String webviewUri="";
    private static SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy:MM:dd_hh:mm aa");
    public static int free_limit_perday=100;
    public static int limit_perhour =100;
    public static int limit_per12hour =200;
    private static String seperater=";;";
    public static File myDir;
    public static boolean is_credit_noti_showed=false;
    public static boolean is_rateus_noti_showed=false;
    public static int left_time_12limit=12;
    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreate() {
        super.onCreate();
        //TODO APP ID
        mInstance=this;
        MobileAds.initialize(this, "ca-app-pub-7166764673125229~3007945136");
        settings = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        editor = settings.edit();
        followers = new ArrayList<>();
        following = new ArrayList<>();
        unfollowers = new ArrayList<>();
        whitelist=new ArrayList<>();
        myDir = new File(Environment.getExternalStorageDirectory() +File.separator+ getString(R.string.app_name));//Constant.sp+getString(R.string.app_name)
        try{
            if(myDir.mkdir()) {
                System.out.println("Directory created");
            } else {
                System.out.println("Directory is not created");
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    public static void logoutManager(){
        setIsSaved(false);
        setUserName("");
        setPassword("");
        unfollowers=new ArrayList<>();
        whitelist=new ArrayList<>();
        following=new ArrayList<>();
        followers=new ArrayList<>();
        currentUser=new InstagramLoggedUser();
        feedItems=new ArrayList<>();
    }

    public static void checkLimit(){

        Date last_login=new Date();
        try {
            last_login=dateFormat.parse(getLastLogin());
            Date valid_date=new Date(last_login.getTime()+(24*60*60*1000));
            //if one day more time passed
            if (valid_date.before(new Date())) {
                restoreFreeCredit();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void restoreFreeCredit() {
        setFreeLimit(free_limit_perday);
        ArrayList<Boolean> list_redeemed=new ArrayList<>();
        list_redeemed.add(false);
        list_redeemed.add(false);
        list_redeemed.add(false);
        list_redeemed.add(false);
        list_redeemed.add(false);
        list_redeemed.add(false);
//        list_redeemed.add(false);
//        list_redeemed.add(false);
        setListRedeemed(list_redeemed);
    }

    public static Set<String> getWhitelist_ids() {
        String key=getUserName();
        return settings.getStringSet(key,new HashSet<String>());
    }

    public static void setWhitelist_ids(Set<String> whitelist_ids) {
        String key=getUserName();
        editor.remove(key);
        editor.apply();
        editor.putStringSet(key,whitelist_ids);
        editor.apply();
    }

    public static void addWhitelist_ids(long id, InstagramUserSummary userSummary){
        ArrayList<String> whitelist_ids=new ArrayList<>(getWhitelist_ids());
        if (whitelist_ids.contains(String.valueOf(id))) return;
        whitelist_ids.add(0, String.valueOf(id));
        setWhitelist_ids(new HashSet<String>(whitelist_ids));
        whitelist.add(0, userSummary);
    }

    public static void removeWhitelist_ids(long id, InstagramUserSummary userSummary){
        Set<String> whitelist_ids=getWhitelist_ids();
        whitelist_ids.remove(String.valueOf(id));
        setWhitelist_ids(whitelist_ids);
        unfollowers.add(0, userSummary);
    }

    public static ArrayList<String> getUnfollwed24_ids() {
        String key=getUserName()+seperater+"24";
        Set<String> Unfollowed_ids=settings.getStringSet(key,new HashSet<String>());
        ArrayList<String> unfollow24_ids=new ArrayList<>(Unfollowed_ids);
        for (int i=0;i<unfollow24_ids.size();i++){
            String time=unfollow24_ids.get(i).split(seperater)[1];
            long DAY_IN_MS = 1000 * 60 * 60 * 24;
            Date one_day_ago=new Date(System.currentTimeMillis() - (DAY_IN_MS));
            try {
                Date that_time=dateFormat.parse(time);
                if (that_time.before(one_day_ago)) unfollow24_ids.remove(i);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return unfollow24_ids;
    }

    public static void addUnfollwed24_ids(long id){
        String key=getUserName()+seperater+"24";
        Set<String> Unfollowed24_ids=new HashSet<String>(getUnfollwed24_ids());
        Date now=new Date();
        Unfollowed24_ids.add(String.valueOf(id)+seperater+dateFormat.format(now));
        editor.remove(key);
        editor.apply();
        editor.putStringSet(key,Unfollowed24_ids);
        editor.apply();
    }

    public static ArrayList<String> getUnfollwed1Hour_ids() {
        String key=getUserName()+seperater+"1";
        Set<String> Unfollowed_ids=settings.getStringSet(key,new HashSet<String>());
        ArrayList<String> unfollow1_ids=new ArrayList<>(Unfollowed_ids);
        for (int i=0;i<unfollow1_ids.size();i++){
            String time=unfollow1_ids.get(i).split(seperater)[1];
            long Hour_IN_MS = 1000 * 60 * 60;//1 hour
            Date now=new Date();
            try {
                Date that_time=dateFormat.parse(time);
                if ((now.getTime()-that_time.getTime())>Hour_IN_MS) unfollow1_ids.remove(i);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return unfollow1_ids;
    }

    public static void addUnfollwed1Hour_ids(long id){
        String key=getUserName()+seperater+"1";
        Set<String> Unfollowed1hour_ids=new HashSet<String>(getUnfollwed1Hour_ids());
        Date now=new Date();
        Unfollowed1hour_ids.add(String.valueOf(id)+seperater+dateFormat.format(now));
        editor.remove(key);
        editor.apply();
        editor.putStringSet(key,Unfollowed1hour_ids);
        editor.apply();
    }

    public static ArrayList<String> getUnfollwed12Hour_ids() {
        String key=getUserName()+seperater+"12";
        Set<String> Unfollowed_12hour_ids=settings.getStringSet(key,new HashSet<String>());
        ArrayList<String> unfollow12hour_ids=new ArrayList<>(Unfollowed_12hour_ids);
        for (int i=0;i<unfollow12hour_ids.size();i++){
            String time=unfollow12hour_ids.get(i).split(seperater)[1];
            Log.e("PreferenceManager","unfollow12hour"+time);
            long Twelve_Hour_IN_MS = 1000 * 60 * 60 * 12;//12 hours
            Date now=new Date();
            try {
                Date unfollwed_time=dateFormat.parse(time);
                long milliSec=now.getTime()-unfollwed_time.getTime();
                left_time_12limit= 12 - (int) TimeUnit.MILLISECONDS.toHours(milliSec);
                Log.e("PreferenceManager","unfollow12hour"+left_time_12limit);
                if ((now.getTime()-unfollwed_time.getTime())>Twelve_Hour_IN_MS) unfollow12hour_ids.remove(i);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return unfollow12hour_ids;
    }

    public static void addUnfollwed12Hour_ids(long id){
        String key=getUserName()+seperater+"12";
        Set<String> Unfollowed12hour_ids=new HashSet<String>(getUnfollwed12Hour_ids());
        Date now=new Date();
        Unfollowed12hour_ids.add(String.valueOf(id)+seperater+dateFormat.format(now));
        editor.remove(key);
        editor.apply();
        editor.putStringSet(key,Unfollowed12hour_ids);
        editor.apply();
    }

    public static void setUserName(String userName){
        editor.remove("username");
        editor.apply();
        editor.putString("username",userName);
        editor.apply();
    }

    public static String getUserName(){
        return settings.getString("username","");
    }

    public static void setPassword(String password){
        editor.remove("password");
        editor.apply();
        editor.putString("password",password);
        editor.apply();
    }

    public static String getPassword(){
        return settings.getString("password","");
    }

    public static void setIsSaved(boolean saved){
        editor.remove("saved");
        editor.apply();
        editor.putBoolean("saved",saved);
        editor.apply();
    }

    public static boolean isSaved(){
        return settings.getBoolean("saved",false);
    }

    public static UnfollowStatus unfollow(InstagramUserSummary userSummary){
        try {
            int limit=getFreeLimit()+getRewardLimit();
            Log.e("freelimit",getFreeLimit()+"");
            Log.e("rewardlimit",getRewardLimit()+"");
            if (limit == 0) return UnfollowStatus.limited;
            if (getUnfollwed1Hour_ids().size()>= limit_perhour) return UnfollowStatus.limited_per_hour;
            if (getUnfollwed12Hour_ids().size()>= limit_per12hour)return UnfollowStatus.limited_per_12hours;
            instagram.sendRequest(new InstagramUnfollowRequest(userSummary.getPk()));
            following.remove(userSummary);
            addUnfollwed24_ids(userSummary.getPk());
            addUnfollwed1Hour_ids(userSummary.getPk());
            addUnfollwed12Hour_ids(userSummary.getPk());
            if (getFreeLimit()!=0) setFreeLimit(getFreeLimit()-1);
            else if (getRewardLimit()!=0 && getFreeLimit()==0) setRewardLimit(getRewardLimit()-1);
            return UnfollowStatus.success;
        } catch (IOException e) {
            e.printStackTrace();
            return UnfollowStatus.failed;
        }
    }

    public static int getFreeLimit(){
        return settings.getInt(getUserName()+seperater+"free_limit",0);
    }

    public static void setFreeLimit(int limit){
        editor.remove(getUserName()+seperater+"free_limit");
        editor.apply();
        editor.putInt(getUserName()+seperater+"free_limit",limit);
        editor.apply();
    }

    public static int getRewardLimit(){
        return settings.getInt(getUserName()+seperater+"reward_limit",0);
    }

    public static void setRewardLimit(int limit){
        editor.remove(getUserName()+seperater+"reward_limit");
        editor.apply();
        editor.putInt(getUserName()+seperater+"reward_limit",limit);
        editor.apply();
    }

    public static String getLastLogin(){
        return settings.getString(getUserName() + seperater + "last_login","0000:00:00_00:00 AM");
    }

    public static void setLastLogin(){
        Date now=new Date();
        editor.remove(getUserName() + seperater + "last_login");
        editor.apply();
        editor.putString(getUserName() + seperater + "last_login",dateFormat.format(now));
        editor.apply();
    }

    public static ArrayList<Boolean> getListRedeemed(){
        //1: true, 0: false- 8 reward ads redeemed list
        ArrayList<Boolean> list_redeemed=new ArrayList<>();
        String[] list = settings.getString(getUserName() + seperater + "list_redeemed","0,0,0,0,0,0,0,0").split(",");
        for (String aList : list) {
            if (aList.equals("0")) list_redeemed.add(false);
            else list_redeemed.add(true);
        }
        return list_redeemed;
    }

    public static void setListRedeemed(ArrayList<Boolean> listRedeemed){
        StringBuilder list= new StringBuilder();
        for (int i=0;i<listRedeemed.size();i++){
            if (i!=0) list.append(",");
            if (listRedeemed.get(i)) list.append("1");
            else list.append("0");
        }
        editor.remove(getUserName() + seperater + "list_redeemed");
        editor.apply();
        editor.putString(getUserName() + seperater + "list_redeemed", list.toString());
        editor.apply();
    }

    public static String getAccessToken(){
        return settings.getString(getUserName() + seperater + "access_token","");
    }

    public static void setAccessToken(String accessToken){
        editor.remove(getUserName() + seperater + "access_token");
        editor.apply();
        editor.putString(getUserName() + seperater + "access_token",accessToken);
        editor.apply();
    }

    public static void setAlarm(){
        if (!settings.getBoolean("alarm_set",false)){
            //Daily Push Notification
            DailyReceiver.setCtx(mInstance);
            DailyReceiver.setAlarm(true);
            WeeklyReceiver.setCtx(mInstance);
            WeeklyReceiver.setAlarm(true);
            editor.remove("alarm_set");
            editor.apply();
            editor.putBoolean("alarm_set",true);
            editor.apply();
        }
    }
}
