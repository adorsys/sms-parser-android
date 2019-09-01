package de.adorsys.android.smsparser

object SmsConfig {
    internal lateinit var beginIndex: String
    internal lateinit var endIndex: String
    internal lateinit var smsSenderNumbers: List<String>

    fun initializeSmsConfig(beginIndex: String, endIndex: String, vararg smsSenderNumbers: String) {
        this.beginIndex = beginIndex
        this.endIndex = endIndex
        this.smsSenderNumbers = smsSenderNumbers.toList()
    }
}