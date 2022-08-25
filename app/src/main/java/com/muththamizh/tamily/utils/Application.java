package com.muththamizh.tamily.utils;


import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.FirebaseApp;
import com.onesignal.OneSignal;
import com.orhanobut.hawk.Hawk;

public class Application extends android.app.Application {

    private static final String ONESIGNAL_APP_ID = "474f825f-ed43-405a-9d75-5027b970ee44";


    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(this).build();
        //FirebaseApp.initializeApp(this);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        // promptForPushNotifications will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
        OneSignal.promptForPushNotifications();

    }
}
