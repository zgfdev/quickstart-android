//https://firebase.google.com/docs/analytics/android

package com.google.firebase.quickstart.analytics.java;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.quickstart.analytics.R;
import com.google.firebase.quickstart.analytics.databinding.ActivityMainBinding;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.EnumMap;
import java.util.Objects;

import net.consentmanager.sdk.CMPConsentTool;
import net.consentmanager.sdk.common.CmpError;
import net.consentmanager.sdk.common.callbacks.OnOpenCallback;
import net.consentmanager.sdk.common.callbacks.OnCloseCallback;
import net.consentmanager.sdk.common.callbacks.OnErrorCallback;
import net.consentmanager.sdk.common.callbacks.OnCMPNotOpenedCallback;
import net.consentmanager.sdk.common.callbacks.OnCmpButtonClickedCallback;
import net.consentmanager.sdk.consentlayer.model.CMPConfig;
import net.consentmanager.sdk.consentlayer.model.valueObjects.CmpButtonEvent;
import net.consentmanager.sdk.common.callbacks.CmpImportCallback;


/**
 * Activity which displays numerous background images that may be viewed. These background images
 * are shown via {@link ImageFragment}.
 */
public class MainActivity extends AppCompatActivity {
    private CMPConsentTool consentTool;

    // private static final String TAG = "MainActivity";
    private static final String TAG = "OBLOG";
    private static final String KEY_FAVORITE_FOOD = "favorite_food";

    private static final ImageInfo[] IMAGE_INFOS = {
            new ImageInfo(R.drawable.favorite, R.string.pattern1_title, R.string.pattern1_id),
            new ImageInfo(R.drawable.flash, R.string.pattern2_title, R.string.pattern2_id),
            new ImageInfo(R.drawable.face, R.string.pattern3_title, R.string.pattern3_id),
            new ImageInfo(R.drawable.whitebalance, R.string.pattern4_title, R.string.pattern4_id),
    };

    private ActivityMainBinding binding;

    /**
     * The {@link androidx.viewpager.widget.PagerAdapter} that will provide fragments for each image.
     * This uses a {@link FragmentStateAdapter}, which keeps every loaded fragment in memory.
     */
    private ImagePagerAdapter mImagePagerAdapter;

    /**
     * The {@link ViewPager} that will host the patterns.
     */
    private ViewPager2 mViewPager;

    /**
     * The {@code FirebaseAnalytics} used to record screen views.
     */
    // [START declare_analytics]
    private FirebaseAnalytics mFBA;
    // [END declare_analytics]

    /**
     * The user's favorite food, chosen from a dialog.
     */
    private String mFavoriteFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //consentmanager instance
        CMPConfig obcmpConfig = CMPConfig.INSTANCE;
        obcmpConfig.setServerDomain("delivery.consentmanager.net");
        obcmpConfig.setAppName("DemoAppTM");
        obcmpConfig.setLanguage("EN");
        obcmpConfig.setId("xxxxx"); //replace these configuration variables with your own CMP information
        obcmpConfig.setDebug(true);
        consentTool = CMPConsentTool.createInstance(this, obcmpConfig);
//        consentTool = CMPConsentTool.createInstance(this, cmpID, "delivery.consentmanager.net", "DemoAppTM", "EN");

        //consentmanager event listener
        consentTool.setCallbacks(
            ()->obFn_consent_listener("open"),
            ()->obFn_consent_listener("close"),
            ()->obFn_consent_listener("not-open"),
            new OnErrorCallback() {
                @Override
                public void errorOccurred(@NonNull CmpError type, @NonNull String message) {
                    obFn_consent_listener("error");
                    OnErrorCallback.super.errorOccurred(type, message);
                }
            },
            new OnCmpButtonClickedCallback() {
                @Override
                public void onButtonClicked(@NonNull CmpButtonEvent cmpButtonEvent) {
                    obFn_consent_listener("click");
                }
        });

        //consentmanager import&export
        // final String CMP_STRING = "QlAyZkFuQ1AyZkFuQ0FmUjJCRU5BQkFBQUFBQUFBI181MV81Ml81M181NF81NV8jX3MyM19zMjZfczkwNV9VXyMxWU5OIw";
        // consentTool.importCmpString(CMP_STRING, new CmpImportCallback() {
        //     @Override
        //     public void onImportResult(boolean b, @NonNull String s) {
        //         obFn_consent_check();
        //     }
        // });
        // String consentData = consentTool.exportCmpString();
        // Log.d(TAG,"cmp_string:"+consentData);


        // [START shared_app_measurement]
        // Obtain the FirebaseAnalytics instance.
        mFBA = FirebaseAnalytics.getInstance(this);
        // [END shared_app_measurement]

        // On first app open, ask the user his/her favorite food. Then set this as a user property
        // on all subsequent opens.
        String userFavoriteFood = getUserFavoriteFood();
        if (userFavoriteFood == null) {
            askFavoriteFood();
        } else {
            setUserFavoriteFood(userFavoriteFood);
        }

        // 设置所有事件的默认参数
        Bundle dParams = new Bundle();
        dParams.putString("obv", "demoApp_FA_test");
        // dParams.putString(FirebaseAnalytics.Param.SCREEN_NAME, "sn_set2all");
        mFBA.setDefaultEventParameters(dParams);


        // Create the adapter that will return a fragment for each image.
        mImagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), IMAGE_INFOS, getLifecycle());

        // Set up the ViewPager with the pattern adapter.
        mViewPager = binding.viewPager;
        mViewPager.setAdapter(mImagePagerAdapter);

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                recordImageView();
                recordScreenView();
            }
        });

        TabLayout tabLayout = binding.tabLayout;

        // When the visible image changes, send a screen view hit.
        new TabLayoutMediator(tabLayout, mViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
               tab.setText(IMAGE_INFOS[position].title);
            }
        }).attach();

        // Send initial screen screen view hit.
        recordImageView();
    }

    @Override
    public void onResume() {
        super.onResume();
        recordScreenView();
    }

    /**
     * Display a dialog prompting the user to pick a favorite food from a list, then record
     * the answer.
     */
    private void askFavoriteFood() {
        final String[] choices = getResources().getStringArray(R.array.food_items);
        AlertDialog ad = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.food_dialog_title)
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String food = choices[which];
                        setUserFavoriteFood(food);
                    }
                }).create();

        ad.show();
    }

    /**
     * Get the user's favorite food from shared preferences.
     * @return favorite food, as a string.
     */
    private String getUserFavoriteFood() {
        return PreferenceManager.getDefaultSharedPreferences(this)
                .getString(KEY_FAVORITE_FOOD, null);
    }

    /**
     * Set the user's favorite food as an app measurement user property and in shared preferences.
     * @param food the user's favorite food.
     */
    private void setUserFavoriteFood(String food) {
        Log.d(TAG, "setFavoriteFood: " + food);
        mFavoriteFood = food;

        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putString(KEY_FAVORITE_FOOD, food)
                .apply();

        // [START user_property]
        mFBA.setUserProperty("favorite_food", mFavoriteFood);
        // [END user_property]
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_share) {
            String name = getCurrentImageTitle();
            String text = "I'd love you to hear about " + name;

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

            // [START custom_event]
            Bundle params = new Bundle();
            params.putString("image_name", name);
            params.putString("full_text", text);
            mFBA.logEvent("share_image", params);
            // [END custom_event]
        }
        return false;
    }

    /**
     * Return the title of the currently displayed image.
     *
     * @return title of image
     */
    private String getCurrentImageTitle() {
        int position = mViewPager.getCurrentItem();
        ImageInfo info = IMAGE_INFOS[position];
        return getString(info.title);
    }

    /**
     * Return the id of the currently displayed image.
     *
     * @return id of image
     */
    private String getCurrentImageId() {
        int position = mViewPager.getCurrentItem();
        ImageInfo info = IMAGE_INFOS[position];
        return getString(info.id);
    }

    /**
     * Record a screen view for the visible {@link ImageFragment} displayed
     * inside {@link FragmentStateAdapter}.
     */
    private void recordImageView() {
        String id =  getCurrentImageId();
        String name = getCurrentImageTitle();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "sn_set2event");
        mFBA.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void ob_btnClick(View view) {
        Button btn = (Button)view;
        String btnText = btn.getText().toString();
        Log.d(TAG, "ob_btnClick: "+btnText);
        switch (btnText) {
            case "consent_open" -> obFn_consent_open();
            case "consent_check" -> obFn_consent_check();
            case "GA_enable" -> obFn_GA_enable();
            case "GA_disable" -> obFn_GA_disable();
            case "custom_event" -> OBFunc.obFn_custom_event(mFBA);
            case "click" -> OBFunc.obFn_click(mFBA);
            case "view" -> OBFunc.obFn_view(mFBA);
            case "select_content" -> OBFunc.obFn_select_content(mFBA);
            case "dynamic_link_app_open" -> OBFunc.obFn_dynamic_link_app_open(mFBA);
            case "view_promotion" -> OBFunc.obFn_view_promotion(mFBA);
            case "select_promotion" -> OBFunc.obFn_select_promotion(mFBA);
            case "view_item_list" -> OBFunc.obFn_view_item_list(mFBA);
            case "select_item" -> OBFunc.obFn_select_item(mFBA);
            case "add_to_cart" -> OBFunc.obFn_add_to_cart(mFBA);
            case "view_cart" -> OBFunc.obFn_view_cart(mFBA);
            case "remove_from_cart" -> OBFunc.obFn_remove_from_cart(mFBA);
            case "add_to_wishlist" -> OBFunc.obFn_add_to_wishlist(mFBA);
            case "add_payment_info" -> OBFunc.obFn_add_payment_info(mFBA);
            case "add_shipping_info" -> OBFunc.obFn_add_shipping_info(mFBA);
            case "begin_checkout" -> OBFunc.obFn_begin_checkout(mFBA);
            case "purchase" -> OBFunc.obFn_purchase(mFBA);
            case "refund" -> OBFunc.obFn_refund(mFBA);
        }
    }

    private void obFn_consent_open(){
        consentTool.openCmpConsentToolView(this);
    }
    private void obFn_GA_enable(){
        mFBA.setAnalyticsCollectionEnabled(true);
        Map<FirebaseAnalytics.ConsentType, FirebaseAnalytics.ConsentStatus> consentMap = new EnumMap<>(FirebaseAnalytics.ConsentType.class);
        consentMap.put(FirebaseAnalytics.ConsentType.ANALYTICS_STORAGE, FirebaseAnalytics.ConsentStatus.GRANTED);
        consentMap.put(FirebaseAnalytics.ConsentType.AD_STORAGE, FirebaseAnalytics.ConsentStatus.GRANTED);
        consentMap.put(FirebaseAnalytics.ConsentType.AD_USER_DATA, FirebaseAnalytics.ConsentStatus.GRANTED);
        consentMap.put(FirebaseAnalytics.ConsentType.AD_PERSONALIZATION, FirebaseAnalytics.ConsentStatus.GRANTED);
        mFBA.setConsent(consentMap);
    }
    private void obFn_GA_disable(){
        mFBA.setAnalyticsCollectionEnabled(false);
    }
    private void obFn_consent_listener(String info){
        Log.d(TAG,"obFn_consent_listener: "+info);
        if(Objects.equals(info, "close")){
            obFn_consent_check();
        }
    }
    
/**
    c51 Function
    c52 Marketing
    c53 Preferences
    c54 Measurement
    c55 Other
    c56 Social
    -++-++++=-++---+-=-++++---=-++++--+=-++++-+-=--+-+++-=-++---++=-++-++++=-++-++-+
    s1 Google Ads
    s26 Google Analytics
**/
    private boolean obFn_consentCheck_purposes(String[] purposes){
        boolean allTrue = true;
        for (String purpose : purposes) {
            Log.d(TAG,purpose+" "+consentTool.hasPurposeConsent(this, purpose, false, false));
            if (!consentTool.hasPurposeConsent(this, purpose, false, false)) {
                allTrue = false;
                break;
            }
        }
        Log.d(TAG, "obFn_consentCheck_purposes: "+allTrue+" ["+ Arrays.toString(purposes) +"]");
        return allTrue;
    }
    private boolean obFn_consentCheck_vendors(String[] vendors){
        boolean allTrue = true;
        for (String vendor : vendors) {
            if (!consentTool.hasVendorConsent(this, vendor, false)) {
                allTrue = false;
                break;
            }
        }
        Log.d(TAG, "obFn_consentCheck_vendors: "+allTrue+" ["+ Arrays.toString(vendors) +"]");
        return allTrue;
    }

    private void obFn_consent_check(){
        Log.d(TAG, "obFn_consent_check");
        if(obFn_consentCheck_purposes(new String[]{"c51", "c52", "c53", "c54", "c55", "c56"}) && obFn_consentCheck_vendors(new String[]{"s26"})){
            obFn_GA_enable();
        }else{
            obFn_GA_disable();
        }
    }


    /**
     * This sample has a single Activity, so we need to manually record "screen views" as
     * we change fragments.
     */
    private void recordScreenView() {
        // This string must be <= 36 characters long.
        String screenName = getCurrentImageId() + "-" + getCurrentImageTitle();

        // [START set_current_screen]
        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity");
        bundle.putString("opa", "test_SN");
        // mFBA.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
        mFBA.logEvent("screen_view", bundle);
        // [END set_current_screen]
    }

    /**
     * A {@link FragmentStateAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class ImagePagerAdapter extends FragmentStateAdapter {

        private final ImageInfo[] infos;

        public ImagePagerAdapter(FragmentManager fm, ImageInfo[] infos, Lifecycle lifecyle) {
            super(fm, lifecyle);
            this.infos = infos;
        }

        public CharSequence getPageTitle(int position) {
            if (position < 0 || position >= infos.length) {
                return null;
            }
            Locale l = Locale.getDefault();
            ImageInfo info = infos[position];
            return getString(info.title).toUpperCase(l);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            ImageInfo info = infos[position];
            return ImageFragment.newInstance(info.image);
        }

        @Override
        public int getItemCount() {
            return infos.length;
        }
    }
}
