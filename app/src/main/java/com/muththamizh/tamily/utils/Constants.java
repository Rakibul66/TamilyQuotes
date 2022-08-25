package com.muththamizh.tamily.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import com.muththamizh.tamily.BuildConfig;
import com.muththamizh.tamily.R;

import java.util.ArrayList;

public class Constants {

    public static final int SPLASH_TIMEOUT = 2500;
    static MediaPlayer mediaPlayer;

    public static final String CATEGORIES_COLLECTIONS = "category";
    public static final String TRENDING_CATEGORIES_COLLECTIONS = "trending_category";
    public static final String QUOTES_COLLECTIONS = "quotes";
    public static final String ADD_OR_EDIT_QUOTE = "add_or_edit";
    public static final String CATEGORY_DOC_ID = "category_doc_id";
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";
    public static final String QUOTE_ID = "quote_id";
    public static final String QUOTE_NAME = "quote_name";

    // POSSIBLE USER MODES --- user/admin
    public static final String USER_MODE = "user";


    public static void playTone(Context context, int resource){
        try {
            mediaPlayer = MediaPlayer.create(context,resource);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context,"error due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public static Integer getColorsList(int pos){
        ArrayList<Integer> colorsList = new ArrayList<Integer>();
        colorsList.add(R.color.colorPrimary);
        colorsList.add(R.color.colorTwo);
        colorsList.add(R.color.colorThree);
        colorsList.add(R.color.colorFour);
        colorsList.add(R.color.colorFive);
        colorsList.add(R.color.colorSix);
        colorsList.add(R.color.colorSeven);
        colorsList.add(R.color.colorEight);
        colorsList.add(R.color.colorNine);
        colorsList.add(R.color.colorOne);
        return colorsList.get(pos);
    }

    public static void rateApp(Context context){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.playStoreURL)+ BuildConfig.APPLICATION_ID));
        context.startActivity(intent);
    }

    public static void shareApp(Context context){
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_SUBJECT,context.getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT,context.getString(R.string.installDesc)+" \n\n\n "+context.getString(R.string.playStoreURL)+BuildConfig.APPLICATION_ID);
        share.setType("text/plain");
        context.startActivity(Intent.createChooser(share, "Share"));
    }

    public static void contactUs(Context context,String subject,String body){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:muththamizhsocial@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,subject+" is the subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT,body+" is body");
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        context.startActivity(Intent.createChooser(emailIntent, "Select Mail Client"));
    }

    public static void redirectToWebPage(Context context,String url){
        Intent redirectIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
        context.startActivity(Intent.createChooser(redirectIntent,"Complete Action via"));
    }

    public static void shareQuote(Context context,String quote){
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_SUBJECT,context.getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT,quote+" \n\n "+context.getString(R.string.installDesc)+" \n "+context.getString(R.string.playStoreURL)+BuildConfig.APPLICATION_ID);
        share.setType("text/plain");
        context.startActivity(Intent.createChooser(share, "Share"));
    }

    public static void copyQuote2ClipBoard(Context context,String quote){
        try {
            ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Quote copied from Quotes App","\n *"+ quote+"* \n\n"+"Quote copied from Quotes App" +" \n\n"+context.getString(R.string.installDesc)+" : "+context.getString(R.string.playStoreURL)+BuildConfig.APPLICATION_ID);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Quote Copied to Clipboard", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            e.getMessage();
        }
    }





}
