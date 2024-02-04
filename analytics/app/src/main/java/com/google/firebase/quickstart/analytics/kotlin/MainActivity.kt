package com.google.firebase.quickstart.analytics.kotlin

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.quickstart.analytics.R
import com.google.firebase.quickstart.analytics.databinding.ActivityMainBinding
import com.google.firebase.quickstart.analytics.java.MainActivity
import kotlinx.coroutines.launch
import net.consentmanager.sdk.CmpManager
import net.consentmanager.sdk.common.utils.CmpFrameLayoutHelper
import net.consentmanager.sdk.consentlayer.model.CmpConfig
import java.util.Arrays
import java.util.EnumMap
import java.util.Locale

/**
 * Activity which displays numerous background images that may be viewed. These background images
 * are shown via {@link ImageFragment}.
 */
class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "OBLOG"
        private const val KEY_FAVORITE_FOOD = "favorite_food"

        private val IMAGE_INFOS = arrayOf(
            ImageInfo(R.drawable.favorite, R.string.pattern1_title, R.string.pattern1_id),
            ImageInfo(R.drawable.flash, R.string.pattern2_title, R.string.pattern2_id),
            ImageInfo(R.drawable.face, R.string.pattern3_title, R.string.pattern3_id),
            ImageInfo(R.drawable.whitebalance, R.string.pattern4_title, R.string.pattern4_id),
        )
    }

    private lateinit var binding: ActivityMainBinding

    /**
     * The [androidx.viewpager2.widget.PagerAdapter] that will provide fragments for each image.
     * This uses a [FragmentStateAdapter], which keeps every loaded fragment in memory.
     */
    private lateinit var imagePagerAdapter: ImagePagerAdapter

    private lateinit var cmpManager: CmpManager

    private suspend fun openCmp(language: String) {
        val layoutId = createFrameLayout()
        // or add the containerID here
        CmpConfig.activateCustomLayer(layoutId)
        val consentTool = CmpManager.createInstance(
            context = applicationContext,
            codeId = "xxxxxxxxxxx",
            serverDomain = "delivery.consentmanager.net",
            appName = "DemoAppTM",
            lang = language,
            timeout = 7000,
        )
        consentTool.openConsentLayer(this)
    }

    private suspend fun createFrameLayout(): Int {
        return CmpFrameLayoutHelper(this).createFrameLayout(Rect(0, 200, 400, 600), 0F)
    }

    /**
     * The `FirebaseAnalytics` used to record screen views.
     */
    // [START declare_analytics]
    private lateinit var mFBA: FirebaseAnalytics
    // [END declare_analytics]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val config = CmpConfig.apply {
            id ="xxxxxxxxxxxx"
            domain = "delivery.consentmanager.net"
            appName = "DemoAppTM"
            language = "EN"
        }
        cmpManager = CmpManager.createInstance(this, config)
        cmpManager.initialize(this)


//        lifecycleScope.launch {
//            openCmp("EN")
//        }

//        cmpManager.setGoogleAnalyticsCallback(updateGoogleConsent)

        // [START shared_app_measurement]
        // Obtain the FirebaseAnalytics instance.
        mFBA = Firebase.analytics
        // [END shared_app_measurement]

        // On first app open, ask the user his/her favorite food. Then set this as a user property
        // on all subsequent opens.
        val userFavoriteFood = getUserFavoriteFood()
        if (userFavoriteFood == null) {
            askFavoriteFood()
        } else {
            setUserFavoriteFood(userFavoriteFood)
        }

        // Create the adapter that will return a fragment for each image.
        imagePagerAdapter = ImagePagerAdapter(supportFragmentManager, IMAGE_INFOS, lifecycle)

        // Set up the ViewPager with the pattern adapter.
        binding.viewPager.adapter = imagePagerAdapter

        val pageChangedCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                recordImageView()
                recordScreenView()
            }
        }

        binding.viewPager.registerOnPageChangeCallback(pageChangedCallback)

        val tabLayout: TabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, binding.viewPager) { tab, position ->
            tab.setText(IMAGE_INFOS[position].title)
        }.attach()

        // Send initial screen screen view hit.
        recordImageView()
    }

    public override fun onResume() {
        super.onResume()
        recordScreenView()
    }

    /**
     * Display a dialog prompting the user to pick a favorite food from a list, then record
     * the answer.
     */
    private fun askFavoriteFood() {
        val choices = resources.getStringArray(R.array.food_items)
        val ad = AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(R.string.food_dialog_title)
            .setItems(choices) { _, which ->
                val food = choices[which]
                setUserFavoriteFood(food)
            }.create()

        ad.show()
    }

    /**
     * Get the user's favorite food from shared preferences.
     * @return favorite food, as a string.
     */
    private fun getUserFavoriteFood(): String? {
        return PreferenceManager.getDefaultSharedPreferences(this)
            .getString(KEY_FAVORITE_FOOD, null)
    }

    /**
     * Set the user's favorite food as an app measurement user property and in shared preferences.
     * @param food the user's favorite food.
     */
    private fun setUserFavoriteFood(food: String) {
        Log.d(TAG, "setFavoriteFood: $food")

        PreferenceManager.getDefaultSharedPreferences(this).edit()
            .putString(KEY_FAVORITE_FOOD, food)
            .apply()

        // [START user_property]
        mFBA.setUserProperty("favorite_food", food)
        // [END user_property]
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        if (i == R.id.menu_share) {
            val name = getCurrentImageTitle()
            val text = "I'd love you to hear about $name"

            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)

            // [START custom_event]
            mFBA.logEvent("share_image") {
                param("image_name", name)
                param("full_text", text)
            }
            // [END custom_event]
        }
        return false
    }

    /**
     * Return the title of the currently displayed image.
     *
     * @return title of image
     */
    private fun getCurrentImageTitle(): String {
        val position = binding.viewPager.currentItem
        val info = IMAGE_INFOS[position]
        return getString(info.title)
    }

    /**
     * Return the id of the currently displayed image.
     *
     * @return id of image
     */
    private fun getCurrentImageId(): String {
        val position = binding.viewPager.currentItem
        val info = IMAGE_INFOS[position]
        return getString(info.id)
    }

    /**
     * Record a screen view for the visible [ImageFragment] displayed
     * inside [FragmentStateAdapter].
     */
    private fun recordImageView() {
        val id = getCurrentImageId()
        val name = getCurrentImageTitle()

        // [START image_view_event]
        mFBA.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, id)
            param(FirebaseAnalytics.Param.ITEM_NAME, name)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        }
        // [END image_view_event]
    }

    fun ob_btnClick(view: View) {
        val btn = view as Button
        val btnText = btn.text.toString()
        Log.d(TAG, "ob_btnClick: $btnText")
        when (btnText) {
            "consent_open" -> obFn_consent_open()
            "consent_check" -> obFn_consent_check()
            "GA_enable" -> obFn_GA_enable()
            "GA_disable" -> obFn_GA_disable()
            "custom_event" -> OB_Func.obFn_custom_event(mFBA)
            "click" -> OB_Func.obFn_click(mFBA)
            "view" -> OB_Func.obFn_view(mFBA)
            "select_content" -> OB_Func.obFn_select_content(mFBA)
            "dynamic_link_app_open" -> OB_Func.obFn_dynamic_link_app_open(mFBA)
            "view_promotion" -> OB_Func.obFn_view_promotion(mFBA)
            "select_promotion" -> OB_Func.obFn_select_promotion(mFBA)
            "view_item_list" -> OB_Func.obFn_view_item_list(mFBA)
            "select_item" -> OB_Func.obFn_select_item(mFBA)
            "add_to_cart" -> OB_Func.obFn_add_to_cart(mFBA)
            "view_cart" -> OB_Func.obFn_view_cart(mFBA)
            "remove_from_cart" -> OB_Func.obFn_remove_from_cart(mFBA)
            "add_to_wishlist" -> OB_Func.obFn_add_to_wishlist(mFBA)
            "add_payment_info" -> OB_Func.obFn_add_payment_info(mFBA)
            "add_shipping_info" -> OB_Func.obFn_add_shipping_info(mFBA)
            "begin_checkout" -> OB_Func.obFn_begin_checkout(mFBA)
            "purchase" -> OB_Func.obFn_purchase(mFBA)
            "refund" -> OB_Func.obFn_refund(mFBA)
            else -> {
                // Handle the case when the button text doesn't match any of the above
            }
        }
    }

    private fun obFn_consent_open() {
        cmpManager.openConsentLayer(application)
    }
    private fun obFn_consentCheck_purposes(purposes: Array<String>): Boolean {
        var allTrue = true
        for (purpose in purposes) {
            Log.d(TAG, purpose + " " + cmpManager.hasPurposeConsent(purpose))
            if (!cmpManager.hasPurposeConsent(purpose)) {
                allTrue = false
                break
            }
        }
        Log.d(TAG, "obFn_consentCheck_purposes: " + allTrue + " [" + purposes.contentToString() + "]")
        return allTrue
    }

    private fun obFn_consentCheck_vendors(vendors: Array<String>): Boolean {
        var allTrue = true
        for (vendor in vendors) {
            if (!cmpManager.hasVendorConsent(vendor)) {
                allTrue = false
                break
            }
        }
        Log.d(TAG, "obFn_consentCheck_vendors: " + allTrue + " [" + vendors.contentToString() + "]")
        return allTrue
    }
    private fun obFn_consent_check() {
        Log.d(TAG, "obFn_consent_check")
        if (obFn_consentCheck_purposes(
                arrayOf<String>(
                    "c51",
                    "c52",
                    "c53",
                    "c54",
                    "c55",
                    "c56"
                )
            ) && obFn_consentCheck_vendors(
                arrayOf<String>("s26")
            )
        ) {
            obFn_GA_enable()
        } else {
            obFn_GA_disable()
        }
    }
    private fun obFn_GA_enable() {
//        mFBA.setAnalyticsCollectionEnabled(true);
        val consentMap: MutableMap<FirebaseAnalytics.ConsentType, FirebaseAnalytics.ConsentStatus> = EnumMap(
            FirebaseAnalytics.ConsentType::class.java
        )
        consentMap[FirebaseAnalytics.ConsentType.ANALYTICS_STORAGE] = FirebaseAnalytics.ConsentStatus.GRANTED
        consentMap[FirebaseAnalytics.ConsentType.AD_STORAGE] = FirebaseAnalytics.ConsentStatus.GRANTED
        consentMap[FirebaseAnalytics.ConsentType.AD_USER_DATA] = FirebaseAnalytics.ConsentStatus.GRANTED
        consentMap[FirebaseAnalytics.ConsentType.AD_PERSONALIZATION] = FirebaseAnalytics.ConsentStatus.GRANTED
        mFBA.setConsent(consentMap)
    }

    private fun obFn_GA_disable() {
//        mFBA.setAnalyticsCollectionEnabled(false);
        val consentMap: MutableMap<FirebaseAnalytics.ConsentType, FirebaseAnalytics.ConsentStatus> = EnumMap(
            FirebaseAnalytics.ConsentType::class.java
        )
        consentMap[FirebaseAnalytics.ConsentType.ANALYTICS_STORAGE] = FirebaseAnalytics.ConsentStatus.DENIED
        consentMap[FirebaseAnalytics.ConsentType.AD_STORAGE] = FirebaseAnalytics.ConsentStatus.DENIED
        consentMap[FirebaseAnalytics.ConsentType.AD_USER_DATA] = FirebaseAnalytics.ConsentStatus.DENIED
        consentMap[FirebaseAnalytics.ConsentType.AD_PERSONALIZATION] = FirebaseAnalytics.ConsentStatus.DENIED
        mFBA.setConsent(consentMap)
    }

    /**
     * This sample has a single Activity, so we need to manually record "screen views" as
     * we change fragments.
     */
    private fun recordScreenView() {
        // This string must be <= 36 characters long.
        val screenName = "${getCurrentImageId()}-${getCurrentImageTitle()}"

        // [START set_current_screen]
        mFBA.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        }
        // [END set_current_screen]
    }

    /**
     * A [FragmentStateAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class ImagePagerAdapter(
        fm: FragmentManager,
        private val infos: Array<ImageInfo>,
        lifecyle: Lifecycle,
    ) : FragmentStateAdapter(fm, lifecyle) {

        fun getPageTitle(position: Int): CharSequence? {
            if (position < 0 || position >= infos.size) {
                return null
            }
            val l = Locale.getDefault()
            val info = infos[position]
            return getString(info.title).uppercase(l)
        }

        override fun getItemCount(): Int = infos.size

        override fun createFragment(position: Int): Fragment {
            val info = infos[position]
            return ImageFragment.newInstance(info.image)
        }
    }
}
