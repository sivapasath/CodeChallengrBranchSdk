package com.branch.codechallengrbranchsdk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.util.Date;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.BranchContentSchema;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.CurrencyType;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ProductCategory;
import io.branch.referral.util.ShareSheetStyle;
import io.branch.referral.validators.IntegrationValidator;

public class LauncherActivity extends AppCompatActivity {

    BranchUniversalObject branchUniversalObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);

        Branch.getInstance().redeemRewards(0, (changed, error) -> {
            if (error != null) {
                Log.e("BranchSDK_Tester", "branch redeem rewards failed. Caused by -" + error.getMessage());
            } else {
                if (changed) {
                    Log.e("BranchSDK_Tester", "redeemed rewards = " + true);
                } else {
                    Log.e("BranchSDK_Tester", "redeem rewards unknown error ");
                }
            }
        });

        // Create a BranchUniversal object for the content referred on this activity instance
        branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("item/12345")
                .setCanonicalUrl("https://branch.io/deepviews")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PRIVATE)
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setTitle("My Content Title")
                .setContentDescription("my_product_description1")
                .setContentImageUrl("https://example.com/mycontent-12345.png")
                .setContentExpiration(new Date(1573415635000L))
                .setContentImageUrl("https://test_img_url")
                .addKeyWord("My_Keyword1")
                .addKeyWord("My_Keyword2")
                .setContentMetadata(
                        new ContentMetadata().setProductName("my_product_name1")
                                .setProductBrand("my_prod_Brand1")
                                .setProductVariant("3T")
                                .setProductCategory(ProductCategory.BABY_AND_TODDLER)
                                .setProductCondition(ContentMetadata.CONDITION.EXCELLENT)
                                .setAddress("Street_name1", "city1", "Region1", "Country1", "postal_code")
                                .setLocation(12.07, -97.5)
                                .setSku("1994320302")
                                .setRating(6.0, 5.0, 7.0, 5)
                                .addImageCaptions("my_img_caption1", "my_img_caption_2")
                                .setQuantity(2.0)
                                .setPrice(23.2, CurrencyType.USD)
                                .setContentSchema(BranchContentSchema.COMMERCE_PRODUCT)
                                .addCustomMetadata(
                                        "Custom_Content_metadata_key1",
                                        "Custom_Content_metadata_val1"
                                )
                );


        floatingActionButton.setOnClickListener(view -> {
            LinkProperties linkProperties = new LinkProperties()
                    .setChannel("facebook")
                    .setFeature("sharing")
                    .addControlParameter("$desktop_url", "http://example.com/home")
                    .addControlParameter("$ios_url", "http://example.com/ios");

            ShareSheetStyle shareSheetStyle = new ShareSheetStyle(LauncherActivity.this, "Check this out!", "This stuff is awesome: ")
                    .setCopyUrlStyle(getResources().getDrawable(android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                    .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "Show more")
                    .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                    .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                    .setAsFullWidthStyle(true)
                    .setSharingTitle("Share With");

            branchUniversalObject.showShareSheet(LauncherActivity.this,
                    linkProperties,
                    shareSheetStyle,
                    new Branch.ExtendedBranchLinkShareListener() {
                        @Override
                        public void onShareLinkDialogLaunched() {
                        }
                        @Override
                        public void onShareLinkDialogDismissed() {
                        }
                        @Override
                        public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                        }
                        @Override
                        public void onChannelSelected(String channelName) {
                        }
                        @Override
                        public boolean onChannelSelected(String channelName, BranchUniversalObject buo, LinkProperties linkProperties) {
                            return false;
                        }
                    });

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).withData(getIntent() != null ? getIntent().getData() : null).init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // if activity is in foreground (or in backstack but partially visible) launching the same
        // activity will skip onStart, handle this case with reInitSession
        if (intent != null &&
                intent.hasExtra("branch_force_new_session") &&
                intent.getBooleanExtra("branch_force_new_session", true)) {
            Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
        }
    }

    private Branch.BranchReferralInitListener branchReferralInitListener = new Branch.BranchReferralInitListener() {
        @Override
        public void onInitFinished(JSONObject linkProperties, BranchError error) {
            Log.e("Link",linkProperties.toString());
            // do stuff with deep link data (nav to page, display content, etc)
        }
    };
}
