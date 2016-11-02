package com.thebluealliance.spectrum.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

public class Utils {
    public static int dpToPx(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dp * metrics.density);
    }
}
