package co.deucate.smsbomber.utility

class Validator(private var phoneNumber: String) {

    private val phoneRegExp = Regex("((\\+*)((0[ -]+)*|(91 )*)(\\d{12}+|\\d{10}+))|\\d{5}([- ]*)\\d{6}")

    init {
        phoneNumber = phoneNumber.trim()
        if (phoneNumber.length > 10) {
            phoneNumber = phoneNumber.substring(phoneNumber.length - 10, phoneNumber.length)
        }
    }

    fun validate(): String? {
        if (!phoneRegExp.matches(phoneNumber)){
            return  "Invalid phone number"
        }
        if(isDeveloper()){
            return  "You are bombing on developer of this app and developer is not fool."
        }
        return null
    }

    private fun isDeveloper(): Boolean {
        if(phoneNumber == "6352122123"){
            return true
        }
        return false
    }

}