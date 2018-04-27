package co.deucate.smsbomber;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProtectedActivity extends AppCompatActivity implements RewardedVideoAdListener {

    EditText mEditText;
    Button mButton;
    String mNumber;

    private RewardedVideoAd mRewardedVideoAd;

    boolean isError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protected);

        AdView mAdView = findViewById(R.id.protectedAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AdView mAdView1 = findViewById(R.id.protectedAdViewLarge);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest1);

        MobileAds.initialize(this, "ca-app-pub-8086732239748075~8890173650");

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        mButton = findViewById(R.id.protectNumberBtn);
        //todo : make it false
        mButton.setEnabled(true);

        mEditText = findViewById(R.id.protectNumberET);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNumber = mEditText.getText().toString();

                if (isError){
                    try {
                        startAddNumber();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                }

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
                Toast.makeText(this, "Created by spidy0471", Toast.LENGTH_SHORT).show();
                return true;
            }

            case R.id.menuWeb: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://deucate.com/"));
                startActivity(intent);
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-8086732239748075/7406638658", new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        mButton.setEnabled(true);

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        try {
            startAddNumber();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        loadRewardedVideoAd();
        Toast.makeText(this, "Error: "+i, Toast.LENGTH_SHORT).show();
        isError = true;
    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    private void startAddNumber() throws IOException {

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
                HashMap<String ,Object> data = new HashMap<>();

                try {
                    JSONObject root = new JSONObject(JSONData);
                    Object time = root.get("Time");
                    HashMap<String ,Object> temp = new HashMap<>();
                    temp.put("Time",time);
                    data = temp;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("Protected").document(mNumber).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

            }
        });



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
