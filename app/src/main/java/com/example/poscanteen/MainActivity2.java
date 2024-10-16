package com.example.poscanteen;
import android.app.Application;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity2 extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
