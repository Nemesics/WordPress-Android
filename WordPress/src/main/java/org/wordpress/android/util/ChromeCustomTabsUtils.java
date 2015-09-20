package org.wordpress.android.util;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.text.TextUtils;

import org.wordpress.android.R;
import org.wordpress.android.ui.WPWebViewActivity;

public class ChromeCustomTabsUtils {

    private static Boolean mIsCustomTabsSupported;

    private static boolean isCustomTabsSupported(Context context) {
        if (mIsCustomTabsSupported == null) {
            String packageNameToBind = CustomTabsHelper.getPackageNameToUse(context);
            if (packageNameToBind == null) {
                mIsCustomTabsSupported = false;
                return false;
            }

            CustomTabsServiceConnection connection = new CustomTabsServiceConnection() {
                @Override
                public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) { }
                @Override
                public void onServiceDisconnected(ComponentName name) { }
            };

            mIsCustomTabsSupported = CustomTabsClient.bindCustomTabsService(context, packageNameToBind, connection);
            if (mIsCustomTabsSupported) {
                context.unbindService(connection);
            }
        }

        return mIsCustomTabsSupported;
    }

    public static void openUrl(Context context, String url) {
        if (context == null || TextUtils.isEmpty(url)) return;

        if (isCustomTabsSupported(context)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            Bundle extras = new Bundle();
            extras.putBinder(CustomTabsIntent.EXTRA_SESSION, null);
            intent.putExtras(extras);

            intent.putExtra(CustomTabsIntent.EXTRA_TITLE_VISIBILITY_STATE, CustomTabsIntent.SHOW_PAGE_TITLE);

            Bitmap icon = BitmapFactory.decodeResource(
                    context.getResources(), org.wordpress.android.R.drawable.ic_arrow_back_black_24dp);
            intent.putExtra(CustomTabsIntent.EXTRA_CLOSE_BUTTON_ICON, icon);

            Bundle finishBundle = ActivityOptions.makeCustomAnimation(
                    context, org.wordpress.android.R.anim.do_nothing, R.anim.activity_slide_out_to_right).toBundle();
            intent.putExtra(CustomTabsIntent.EXTRA_EXIT_ANIMATION_BUNDLE, finishBundle);

            Bundle startBundle = ActivityOptions.makeCustomAnimation(
                    context, org.wordpress.android.R.anim.activity_slide_in_from_right, R.anim.do_nothing).toBundle();

            context.startActivity(intent, startBundle);
        } else {
            WPWebViewActivity.openURL(context, url);
        }
    }
}
