//package com.google.firebase.quickstart.analytics.kotlin

import androidx.fragment.app.FragmentActivity
import com.google.firebase.analytics.FirebaseAnalytics
import net.consentmanager.sdk.consentlayer.model.valueObjects.ConsentStatus
import net.consentmanager.sdk.consentlayer.model.valueObjects.ConsentType
import net.consentmanager.sdk.consentmode.CmpGoogleAnalyticsInterface

// add the AnalyticsInterface
public class ConsentActivity() : FragmentActivity(), CmpGoogleAnalyticsInterface {

    // Set the Callback
//    cmpManager.setGoogleAnalyticsCallback(this)

    // Define Callback
    override fun updateGoogleConsent(consentMap: Map<ConsentType, ConsentStatus>) {
        val firebaseConsentMap = consentMap.entries.associate { entry ->
            val firebaseConsentType = when (entry.key) {
                ConsentType.ANALYTICS_STORAGE -> FirebaseAnalytics.ConsentType.ANALYTICS_STORAGE
                ConsentType.AD_STORAGE -> FirebaseAnalytics.ConsentType.AD_STORAGE
                ConsentType.AD_USER_DATA -> FirebaseAnalytics.ConsentType.AD_USER_DATA
                ConsentType.AD_PERSONALIZATION -> FirebaseAnalytics.ConsentType.AD_PERSONALIZATION
            }

            val firebaseConsentStatus = when (entry.value) {
                ConsentStatus.GRANTED -> FirebaseAnalytics.ConsentStatus.GRANTED
                ConsentStatus.DENIED -> FirebaseAnalytics.ConsentStatus.DENIED
            }

            firebaseConsentType to firebaseConsentStatus
        }

        FirebaseAnalytics.getInstance(applicationContext).setConsent(firebaseConsentMap)
    }
}