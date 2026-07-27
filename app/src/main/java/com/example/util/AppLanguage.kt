package com.example.util

enum class AppLanguage(val displayName: String, val code: String) {
    ENGLISH("English", "en"),
    BURMESE("မြန်မာစာ", "my")
}

class AppStrings(val language: AppLanguage) {
    val appTitle: String = "Kruger VPN"

    val appSubtitle: String = when (language) {
        AppLanguage.ENGLISH -> "Native VPN Protection"
        AppLanguage.BURMESE -> "မူရင်း VPN ကာကွယ်မှု"
    }

    val shieldInactive: String = when (language) {
        AppLanguage.ENGLISH -> "SHIELD INACTIVE"
        AppLanguage.BURMESE -> "ကာကွယ်မှု မဖွင့်ရသေးပါ"
    }

    val shieldActive: String = when (language) {
        AppLanguage.ENGLISH -> "SHIELD ACTIVE"
        AppLanguage.BURMESE -> "ကာကွယ်မှု ဖွင့်ထားသည်"
    }

    val shieldConnecting: String = when (language) {
        AppLanguage.ENGLISH -> "CONNECTING..."
        AppLanguage.BURMESE -> "ချိတ်ဆက်နေသည်..."
    }

    val tapToSecure: String = when (language) {
        AppLanguage.ENGLISH -> "Tap below to secure your connection"
        AppLanguage.BURMESE -> "ချိတ်ဆက်ရန် အောက်ပါခလုတ်ကို နှိပ်ပါ"
    }

    val tapToDisconnect: String = when (language) {
        AppLanguage.ENGLISH -> "Tap below to disconnect VPN"
        AppLanguage.BURMESE -> "ချိတ်ဆက်မှု ဖြတ်ရန် အောက်ပါခလုတ်ကို နှိပ်ပါ"
    }

    val connectionEncrypted: String = when (language) {
        AppLanguage.ENGLISH -> "Your connection is encrypted and secure"
        AppLanguage.BURMESE -> "သင်၏ ချိတ်ဆက်မှု လုံခြုံစိတ်ချရပါသည်"
    }

    val btnSecureConnection: String = when (language) {
        AppLanguage.ENGLISH -> "Secure Connection"
        AppLanguage.BURMESE -> "ချိတ်ဆက်မည်"
    }

    val btnDisconnect: String = when (language) {
        AppLanguage.ENGLISH -> "Disconnect"
        AppLanguage.BURMESE -> "ချိတ်ဆက်မှု ဖြတ်မည်"
    }

    val btnConnecting: String = when (language) {
        AppLanguage.ENGLISH -> "Connecting..."
        AppLanguage.BURMESE -> "ချိတ်ဆက်နေသည်..."
    }

    val activeSince: String = when (language) {
        AppLanguage.ENGLISH -> "ACTIVE SINCE"
        AppLanguage.BURMESE -> "ချိတ်ဆက်ချိန်"
    }

    val notConnected: String = when (language) {
        AppLanguage.ENGLISH -> "Not connected"
        AppLanguage.BURMESE -> "ချိတ်ဆက်မထားပါ"
    }

    val selectedServerLabel: String = when (language) {
        AppLanguage.ENGLISH -> "SELECTED SERVER"
        AppLanguage.BURMESE -> "ရွေးချယ်ထားသော ဆာဗာ"
    }

    val latencyLabel: String = when (language) {
        AppLanguage.ENGLISH -> "LATENCY"
        AppLanguage.BURMESE -> "တုံ့ပြန်ချိန်"
    }

    val tabShield: String = when (language) {
        AppLanguage.ENGLISH -> "Shield"
        AppLanguage.BURMESE -> "ဒိုင်း"
    }

    val tabSettings: String = when (language) {
        AppLanguage.ENGLISH -> "Settings"
        AppLanguage.BURMESE -> "ဆက်တင်များ"
    }

    val languageSetting: String = when (language) {
        AppLanguage.ENGLISH -> "Language"
        AppLanguage.BURMESE -> "ဘာသာစကား"
    }

    val selectServer: String = when (language) {
        AppLanguage.ENGLISH -> "Select VPN Server"
        AppLanguage.BURMESE -> "VPN ဆာဗာ ရွေးချယ်ပါ"
    }

    val autoRotate: String = when (language) {
        AppLanguage.ENGLISH -> "Auto-Rotate"
        AppLanguage.BURMESE -> "အလိုအလျောက် ပြောင်းမည်"
    }

    val protocol: String = when (language) {
        AppLanguage.ENGLISH -> "Protocol"
        AppLanguage.BURMESE -> "ပရိုတိုကော"
    }

    val backendApiUrl: String = when (language) {
        AppLanguage.ENGLISH -> "Backend Controller API"
        AppLanguage.BURMESE -> "ဘက်ခ်အန်း API လိပ်စာ"
    }

    val generalSettings: String = when (language) {
        AppLanguage.ENGLISH -> "General Settings"
        AppLanguage.BURMESE -> "အထွေထွေ ဆက်တင်များ"
    }

    val developerTools: String = when (language) {
        AppLanguage.ENGLISH -> "Developer & Deployment Tools"
        AppLanguage.BURMESE -> "ဆာဗာနှင့် ကုတ်ဒ် ထိန်းချုပ်မှု"
    }

    val vps1Name: String = "VPS 1 (Primary): Oracle Cloud Free (Singapore)"
    val vps1Desc: String = when (language) {
        AppLanguage.ENGLISH -> "Oracle Cloud Free (Singapore) -> Always Primary"
        AppLanguage.BURMESE -> "Oracle Cloud Free (Singapore) -> အမြဲသုံးမည်"
    }

    val vps2Name: String = "VPS 2 (Secondary): AWS Free Tier (Tokyo)"
    val vps2Desc: String = when (language) {
        AppLanguage.ENGLISH -> "AWS Free Tier (Tokyo) -> Fallback if VPS 1 is down"
        AppLanguage.BURMESE -> "AWS Free Tier (Tokyo) -> VPS 1 မရရင် သုံးမည်"
    }

    val vps3Name: String = "VPS 3 (Backup): Google Cloud Free (Taiwan)"
    val vps3Desc: String = when (language) {
        AppLanguage.ENGLISH -> "Google Cloud Free (Taiwan) -> Backup if VPS 1 & 2 fail"
        AppLanguage.BURMESE -> "Google Cloud Free (Taiwan) -> VPS 1 & 2 မရရင် သုံးမည်"
    }
}
