package co.deucate.smsbomber.ui

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import co.deucate.smsbomber.R
import co.deucate.smsbomber.service.ProtectedNumberService
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener

class ProtectedActivity : AppCompatActivity(), RewardedVideoAdListener {
    private val protectedNumberService = ProtectedNumberService()
    private lateinit var rewardedVideoAd: RewardedVideoAd

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_protected)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading...")
        progressDialog.setMessage("Initialized server...")
        progressDialog.show()

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        rewardedVideoAd.rewardedVideoAdListener = this
        rewardedVideoAd.loadAd(resources.getString(R.string.RewardedID), AdRequest.Builder().build())

        findViewById<Button>(R.id.protectNumberBtn).setOnClickListener {
            if (rewardedVideoAd.isLoaded) {
                rewardedVideoAd.show()
            } else {
                AlertDialog.Builder(this).setTitle("Error").setMessage("Rewarded video ad is not loaded. Please wait and try again after some time.").setPositiveButton("Ok") { _, _ -> }.show()
            }
        }
    }

    override fun onRewardedVideoAdClosed() {}

    override fun onRewardedVideoAdLeftApplication() {}

    override fun onRewardedVideoAdLoaded() {
        progressDialog.dismiss()
    }

    override fun onRewardedVideoAdOpened() {}

    override fun onRewardedVideoCompleted() {}

    override fun onRewarded(p0: RewardItem?) {
        val number = findViewById<EditText>(R.id.protectNumberET).text.toString().replace(" ", "").replace("+91", "")
        protectedNumberService.addProtectedNumber(number)
    }

    override fun onRewardedVideoStarted() {}

    override fun onRewardedVideoAdFailedToLoad(p0: Int) {
        AlertDialog.Builder(this).setTitle("Error").setMessage("Failed to load ad. Error code : $p0").setPositiveButton("Ok") { _, _ -> }.show()
        progressDialog.dismiss()
    }

}
