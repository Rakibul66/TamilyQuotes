package com.muththamizh.tamily.ui.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.muththamizh.tamily.BuildConfig;
import com.muththamizh.tamily.R;
import com.muththamizh.tamily.utils.PrefManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoreBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Context mContext;
    private static MoreBottomSheet instance = null;
    @BindView(R.id.tvAboutLL) LinearLayout aboutUs;
    @BindView(R.id.tvPrivacyLL) LinearLayout tvPrivacyLL;

    @BindView(R.id.tvVersion) TextView appVersion;
    @BindView(R.id.llInstagram) LinearLayout llInstagram;
    @BindView(R.id.llFacebook) LinearLayout llFacebook;
    @BindView(R.id.llRate) LinearLayout llRate;
    @BindView(R.id.llShareApp) LinearLayout llShareApp;

    @BindView(R.id.checkTap) CheckBox checkTap;
    OnMenuItemsClicked listener;
    PrefManager prefManager;


    public MoreBottomSheet(Context context) {
        this.mContext = context;
        listener = (OnMenuItemsClicked)mContext;
        prefManager = PrefManager.getInstance(mContext);
    }

    public static MoreBottomSheet getInstance(Context mContext){
        if (instance == null){
            instance = new MoreBottomSheet(mContext);
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.more_bottom_sheet,container,false);
        ButterKnife.bind(this,view);
        appVersion.setText(mContext.getResources().getString(R.string.appVersion)+" "+BuildConfig.VERSION_NAME);
        llInstagram.setOnClickListener(this);
        llFacebook.setOnClickListener(this);
        llRate.setOnClickListener(this);
        llShareApp.setOnClickListener(this);
        checkTap.setOnCheckedChangeListener(this);

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create(); //Read Update
                alertDialog.setTitle("About Us");
                alertDialog.setMessage("Tamily Quotes is a Tamil Quotes App Which Provides the Latest Collections of Tamil Quotes, Tamil WhatsApp Status Kavithai, Kadhal Kavithaigal and Natpu Kavithaigal. Powered By Muththamizh Social");



                alertDialog.show();  //<-- See This!
            }
        });
        tvPrivacyLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mContext.getResources().getString(R.string.privacy_policy_url)));
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkTap.setChecked(prefManager.getBoolean(PrefManager.SOUND_OFF_ON));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llInstagram:
                listener.followOnInstaGramClicked();
                break;
            case R.id.llFacebook:
                listener.followOnFaceBookClicked();
                break;
            case R.id.llRate:
                listener.rateOurAppClicked();
                break;
            case R.id.llShareApp:
                listener.shareOurAppClicked();
                break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.checkTap:
                checkTap.setChecked(b);
                listener.tapSoundOnOffClicked(b);
                break;
        }
    }


    public interface OnMenuItemsClicked{
        void tapSoundOnOffClicked(boolean isSelected);
        void followOnInstaGramClicked();
        void followOnFaceBookClicked();
        void rateOurAppClicked();
        void shareOurAppClicked();
    }








}


