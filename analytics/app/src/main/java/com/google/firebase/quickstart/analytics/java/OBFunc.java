package com.google.firebase.quickstart.analytics.java;

import com.google.firebase.analytics.FirebaseAnalytics;
import android.os.Bundle;
import android.os.Parcelable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class OBFunc {
    private static String obFn_getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat(); // 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a"); // a为am/pm的标记
        Date date = new Date(); // 获取当前时间
        return sdf.format(date); // 格式化的当前时间（24小时制）
    }
    private static String obFn_getTi(){
        Date date = new Date(); // 获取当前时间
        return "ob_ti_"+date.getTime(); // 格式化的当前时间（24小时制）
    }
    protected static Bundle obFn_getPrdItems() {
        Bundle prdItems = new Bundle();
        prdItems.putString("item_id", "obv_item_id");
        prdItems.putString("item_name","obv_item_name");
        prdItems.putString("item_brand", "obv_brand");
        prdItems.putString("item_variant", "obv_item_variant");
        prdItems.putString("item_category","obv_item_category");
        prdItems.putString("item_category2","obv_item_category2");
        prdItems.putString("item_list_id", "obv_item_list_id");
        prdItems.putString("item_list_name", "obv_item_list_name");
        prdItems.putString("promotion_id", "$promotion_id");
        prdItems.putString("promotion_name", "$promotion_name");
        prdItems.putString("creative_name", "$creative_name");
        prdItems.putString("creative_slot", "$creative_slot");
        prdItems.putLong("index", 1);
        prdItems.putLong("quantity", 2);
        prdItems.putDouble("price",100);
        return prdItems;
    }

    protected static void obFn_custom_event(FirebaseAnalytics mfa){
        Bundle params = new Bundle();
        params.putString("epa","epa_value");
        params.putString("epb","epb_value");
        params.putString("epc","epc_value");
        params.putString("epd","epd_value");
        params.putString("epe","epe_value");
        params.putString("epf","epf_value");
        params.putString("epg","epg_value");
        params.putString("page_name","test_page_name");
        params.putString("page_title","test_page_title");
        params.putString("page_location","https://demo.tmfe.site");
        params.putString("page_path","/#/test/path");
        mfa.logEvent("custom_event",params);
        mfa.setUserId("ac303f8bace51d8d4a407b2dd6636aafc0");
        mfa.setUserProperty("user_nbr", "ac303f8bace51d8d4a407b2dd6636aafc0");
    }
    protected static void obFn_click(FirebaseAnalytics mfa){
        Bundle params = new Bundle();
        params.putString("epa","epa_value");
        params.putString("epb","epb_value");
        params.putString("epc","epc_value");
        params.putString("epd","epd_value");
        params.putString("epe","epe_value");
        params.putString("epf","epf_value");
        params.putString("epg","epg_value");
        mfa.logEvent("click",params);
    }
    protected static void obFn_view(FirebaseAnalytics mfa){
        Bundle params = new Bundle();
        params.putString("epa","epa_value");
        params.putString("epb","epb_value");
        params.putString("epc","epc_value");
        params.putString("epd","epd_value");
        params.putString("epe","epe_value");
        params.putString("epf","epf_value");
        params.putString("epg","epg_value");
        mfa.logEvent("view",params);
    }

    protected static void obFn_select_content(FirebaseAnalytics mfa){
        Bundle eParams = obFn_getPrdItems();
        eParams.putString("content_type", "obv_content_type");
        mfa.logEvent("select_content", eParams);
    }
    protected static void obFn_dynamic_link_app_open(FirebaseAnalytics mfa){
        Date date = new Date();
        Bundle params = new Bundle();
        params.putString("medium","obf_utm_medium_t3");
        params.putString("source","obf_utm_source_t3");
        params.putString("campaign","obf_utm_campaign_t3");
        params.putString("dynamic_link_accept_time", String.valueOf(date));
        params.putString("obv","demoApp_FA_test_dyn");
        params.putString("dynamic_link_link_name","app.ahoh.xyz");
        params.putString("dynamic_link_link_id","https://dyn1984.page.link/6SuK");
        // mfa.logEvent("dynamic_link_app_open",params);
        mfa.logEvent("dynamic_link_first_open",params);
    }


    protected static void obFn_view_promotion(FirebaseAnalytics mfa){
        Bundle eParams = new Bundle();
        eParams.putString("oben","view_promotion");
        eParams.putParcelableArray("items",new Parcelable[]{ obFn_getPrdItems()});
        mfa.logEvent("view_promotion", eParams);
    }
    protected static void obFn_select_promotion(FirebaseAnalytics mfa){
        Bundle eParams = new Bundle();
        eParams.putString("oben","select_promotion");
        eParams.putParcelableArray("items",new Parcelable[]{ obFn_getPrdItems()});
        mfa.logEvent("select_promotion", eParams);
    }

    protected static void obFn_view_item_list(FirebaseAnalytics mfa){
        Bundle item1 = new Bundle();
        item1.putString("item_id", "ob_item_id_1");
        item1.putString("item_name","ob_item_name_1");
        item1.putString("item_brand", "ob_brand");
        item1.putString("item_category","ob_item_category");
        item1.putString("item_variant", "ob_item_variant");
        item1.putString("item_list_id", "ob_item_list_id");
        item1.putString("item_list_name", "ob_item_list_name");
        item1.putString("item_category2","ob_item_category2");
        item1.putLong("index", 1);
        item1.putLong("quantity", 3);
        item1.putDouble("price",101.0);
        Bundle item2 = new Bundle();
        item2.putString("item_id", "ob_item_id_2");
        item2.putString("item_name","ob_item_name_2");
        item2.putString("item_brand", "ob_brand");
        item2.putString("item_category","ob_item_category");
        item2.putString("item_variant", "ob_item_variant");
        item2.putString("item_list_id", "ob_item_list_id");
        item2.putString("item_list_name", "ob_item_list_name");
        item2.putString("item_category2","ob_item_category2");
        item2.putLong("index", 2);
        item2.putLong("quantity", 2);
        item2.putDouble("price",102.0);
        Bundle item3 = new Bundle();
        item3.putString("item_id", "ob_item_id_3");
        item3.putString("item_name","ob_item_name_3");
        item3.putString("item_brand", "ob_brand");
        item3.putString("item_category","ob_item_category");
        item3.putString("item_variant", "ob_item_variant");
        item3.putString("item_list_id", "ob_item_list_id");
        item3.putString("item_list_name", "ob_item_list_name");
        item3.putString("item_category2","ob_item_category2");
        item3.putLong("index", 3);
        item3.putLong("quantity", 1);
        item3.putDouble("price",103.0);
        Bundle eParams = new Bundle();
        eParams.putString("oben","view_item_list");
        eParams.putParcelableArray("items", new Parcelable[]{ item1,item2,item3 });
        mfa.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, eParams);
    }
    protected static void obFn_select_item(FirebaseAnalytics mfa){
        Bundle item1 = new Bundle();
        item1.putString("item_id", "obv_item_id");
        item1.putString("item_name","obv_item_name");
        item1.putString("item_brand", "obv_brand");
        item1.putString("item_variant", "obv_item_variant");
        item1.putString("item_category","obv_item_category");
        item1.putString("item_category2","obv_item_category2");
        item1.putString("item_list_id", "obv_item_list_id");
        item1.putString("item_list_name", "obv_item_list_name");
        item1.putLong("index", 1);
        item1.putLong("quantity", 2);
        item1.putDouble("price",100);
        Bundle eParams = new Bundle();
        eParams.putString("oben","select_item");
        eParams.putParcelableArray("items", new Parcelable[]{ item1 });
        mfa.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, eParams);
    }

    protected static void obFn_add_to_cart(FirebaseAnalytics mfa){
        Bundle cartParams = new Bundle();
        Bundle item1 = new Bundle();
        item1.putString("item_id", "4213700084");
        item1.putString("item_name","obv_item_name");
        item1.putDouble("price",810);
        item1.putString("item_brand", "obv_brand");
        item1.putString("item_category","obv_item_category");
        item1.putString("item_variant", "obv_item_variant");
        item1.putString("item_list_id", "obv_item_list_id");
        item1.putString("item_list_name", "obv_item_list_name");
        item1.putLong("index", 1);
        item1.putLong("quantity", 3);
        item1.putString("item_category2","obv_款式");
        cartParams.putString("oben","add_to_cart");
        cartParams.putString("product_id","2022030401");
        cartParams.putString("item_id","2022030402");
        cartParams.putParcelableArray("items", new Parcelable[] { item1});
        mfa.logEvent("add_to_cart", cartParams );
    }
    protected static void obFn_view_cart(FirebaseAnalytics mfa){
        Bundle viewCartParams = new Bundle();
        Bundle item1 = obFn_getPrdItems();
        viewCartParams.putString("oben","view_cart");
        viewCartParams.putString("page_type","obv_page_type");
        viewCartParams.putString("version","obv_version");
        viewCartParams.putString("link","obv_link");
        viewCartParams.putString(FirebaseAnalytics.Param.CURRENCY, "FRF");
        viewCartParams.putDouble(FirebaseAnalytics.Param.VALUE, 1000.12);
        viewCartParams.putParcelableArray("items",new Parcelable[]{ item1 });
        mfa.logEvent(FirebaseAnalytics.Event.VIEW_CART, viewCartParams);
    }
    protected static void obFn_remove_from_cart(FirebaseAnalytics mfa){
        Bundle eParams = new Bundle();
        eParams.putString("oben","remove_from_cart");
        eParams.putParcelableArray("items",new Parcelable[]{ obFn_getPrdItems()});
        mfa.logEvent("remove_from_cart", eParams);
    }
    protected static void obFn_add_to_wishlist(FirebaseAnalytics mfa){
        Bundle eParams = new Bundle();
        eParams.putString("oben","add_to_wishlist");
        eParams.putParcelableArray("items",new Parcelable[]{ obFn_getPrdItems()});
        mfa.logEvent("add_to_wishlist", eParams);
    }

    protected static void obFn_add_payment_info(FirebaseAnalytics mfa){
        Bundle eParams = new Bundle();
        eParams.putString("oben","add_payment_info");
        eParams.putString("currency","USD");
        eParams.putDouble("value",200);
        eParams.putString("coupon","ob_coupon");
        eParams.putString("payment_type","ob_payment_type");
        eParams.putParcelableArray("items",new Parcelable[]{ obFn_getPrdItems()});
        mfa.logEvent("add_payment_info", eParams);
    }
    protected static void obFn_add_shipping_info(FirebaseAnalytics mfa){
        Bundle eParams = new Bundle();
        eParams.putString("oben","add_shipping_info");
        eParams.putString("currency","USD");
        eParams.putDouble("value",200);
        eParams.putString("coupon","ob_coupon");
        eParams.putString("shipping_tier","ob_payment_type");
        eParams.putParcelableArray("items",new Parcelable[]{ obFn_getPrdItems()});
        mfa.logEvent("add_shipping_info", eParams);
    }
    protected static void obFn_begin_checkout(FirebaseAnalytics mfa){
        Bundle eParams = new Bundle();
        eParams.putString("oben","begin_checkout");
        eParams.putString("currency","USD");
        eParams.putDouble("value",200);
        eParams.putString("coupon","ob_coupon");
        eParams.putParcelableArray("items",new Parcelable[]{ obFn_getPrdItems()});
        mfa.logEvent("begin_checkout", eParams);
    }
    protected static void obFn_purchase(FirebaseAnalytics mfa){
        Bundle purchaseParams = new Bundle();
        Bundle item1 = obFn_getPrdItems();
        purchaseParams.putString("oben","purchase");
        purchaseParams.putString("currency","USD");
        purchaseParams.putString("transaction_id", obFn_getTi());
        purchaseParams.putDouble("value", 1200.12);
        purchaseParams.putString("affiliation","ob_affiliation");
        purchaseParams.putString("coupon","ob_coupon");
        purchaseParams.putDouble("shipping", 3.33);
        purchaseParams.putDouble("tax", 1.11);
        purchaseParams.putParcelableArray("items",new Parcelable[]{ item1 });
        mfa.logEvent("purchase", purchaseParams);
    }
    protected static void obFn_refund(FirebaseAnalytics mfa){
        Bundle purchaseParams = new Bundle();
        Bundle item1 = obFn_getPrdItems();
        purchaseParams.putString("oben","refund");
        purchaseParams.putString("currency","USD");
        purchaseParams.putString("transaction_id", obFn_getTi());
        purchaseParams.putDouble("value", 1200.12);
        purchaseParams.putParcelableArray("items",new Parcelable[]{ item1 });
        mfa.logEvent("refund", purchaseParams);
    }
}
