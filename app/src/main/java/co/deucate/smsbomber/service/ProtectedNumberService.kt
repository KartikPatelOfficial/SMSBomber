package co.deucate.smsbomber.service

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ProtectedNumberService {

    val firestore = FirebaseFirestore.getInstance()

    fun isProtectedNumber(number: String, complition: (Boolean) -> Unit) {
        firestore.collection("Protected").document(number).get().addOnCompleteListener {
            val documentSnapshot = it.result
            if (documentSnapshot != null && documentSnapshot.exists()) {
                val protectedTime = documentSnapshot.getTimestamp("time")
                val currentTime = Timestamp.now()
                if (protectedTime == null) {
                    complition(false)
                    return@addOnCompleteListener
                }
                complition((currentTime.seconds - protectedTime.seconds) <= 18000)
            } else {
                complition(false)
            }
        }
    }

    fun addProtectedNumber(number: String) {
        val data = HashMap<String, Any>()
        data["time"] = Timestamp.now()
        firestore.collection("Protected").document(number).set(data)
    }

}