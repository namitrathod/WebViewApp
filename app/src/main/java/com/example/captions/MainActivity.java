package com.example.captions;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {
    private static final String ONESIGNAL_APP_ID = "b484b8c1-5084-444f-b919-860d0ef98642";

    com.google.android.gms.ads.nativead.NativeAd adobj;
    AdLoader adLoader;
    AdView mAdView;


    String websiteURL = "https://NameHere/"; // sets web url
    private WebView webview;
    SwipeRefreshLayout mySwipeRefreshLayout;


    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// One signal Notification code start
        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        // promptForPushNotifications will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
        OneSignal.promptForPushNotifications();
// end one signal notification code

        // adMob code
        MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        AdLoader.Builder builder = new AdLoader.Builder(MainActivity.this, "ca-app-pub-3940256099942544/2247696110");
        builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        builder.forNativeAd(new com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                adobj = nativeAd;
                Toast.makeText(MainActivity.this, "ad loaded", Toast.LENGTH_SHORT).show();
            }
        });


//        {
//            @Override
//            public void onUnifiedNativeAdLoaded(@NonNull NativeAd unifiedNativeAd) {
//                adobj = NativeAd;
//                Toast.makeText(MainActivity.this, "ad loaded", Toast.LENGTH_SHORT).show();
//            }
//        });
        AdLoader adLoader = builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });
        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest= new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);






        if( ! CheckNetwork.isInternetAvailable(this)) //returns true if internet available
        {
            //if there is no internet do this
            setContentView(R.layout.activity_main);
            //Toast.makeText(this,"No Internet Connection, Chris",Toast.LENGTH_LONG).show();

            new AlertDialog.Builder(this) //alert the person knowing they are about to close
                    .setTitle("No internet connection available")
                    .setMessage("Please Check you're Mobile data or Wifi network.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    //.setNegativeButton("No", null)
                    .show();

        }
        else
        {
            //Webview stuff
            webview = findViewById(R.id.webView);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setDomStorageEnabled(true);
            webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
            webview.loadUrl(websiteURL);
            webview.setWebViewClient(new WebViewClientDemo());

        }

        //Swipe to refresh functionality
        mySwipeRefreshLayout = (SwipeRefreshLayout)this.findViewById(R.id.swipeContainer);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        webview.reload();
                    }
                }
        );


    }
    //adMob
    @Override
    public void onBackPressed()  {
        if (webview.isFocused() && webview.canGoBack()) {
            //check if in webview and the user can go back
            webview.goBack(); //go back in webview
        }
        else { //do this if the webview cannot go back any further

            ExitDialog exitDialog = new ExitDialog(MainActivity.this, this.adobj);
            exitDialog.show();
            Window window = exitDialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
//    OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
//        private NativeAd adobj;
//
//
//        @Override
//        public void handleOnBackPressed() {
//            ExitDialog exitDialog =new ExitDialog(MainActivity.this, this.adobj);
//            exitDialog.show();
//        }
//    };


    private class WebViewClientDemo extends WebViewClient {
        @Override
        //Keep webview in app when clicking links
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }

    //set back button functionality
//    @Override
//    public void onBackPressed()  { //if user presses the back button do this
//        if (webview.isFocused() && webview.canGoBack()) {
//            //check if in webview and the user can go back
//            webview.goBack(); //go back in webview
//        } else { //do this if the webview cannot go back any further
//
//            new AlertDialog.Builder(this) //alert the person knowing they are about to close
//                    .setTitle("EXIT")
//                    .setMessage("Are you sure. You want to close Captions For You app?")
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    })
//                    .setNegativeButton("No", null)
//                    .show();
//        }
//    }


}

class CheckNetwork {

    private static final String TAG = CheckNetwork.class.getSimpleName();

    public static boolean isInternetAvailable(Context context)
    {
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null)
        {
            Log.d(TAG,"no internet connection");
            return false;
        }
        else
        {
            if(info.isConnected())
            {
                Log.d(TAG," internet connection available...");
                return true;
            }
            else
            {
                Log.d(TAG," internet connection");
                return true;
            }

        }
    }
}