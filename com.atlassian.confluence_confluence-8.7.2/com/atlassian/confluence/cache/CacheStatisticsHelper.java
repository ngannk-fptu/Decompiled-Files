/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cache;

import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import java.text.DecimalFormat;

public class CacheStatisticsHelper {
    public static String formatSizeInMegabytes(FormatSettingsManager formatSettingsManager, long localHeapSizeInBytes) {
        float sizeInMB = (float)localHeapSizeInBytes / 1024.0f / 1024.0f;
        if (sizeInMB > 0.0f && sizeInMB < 1.0f) {
            return "<1";
        }
        DecimalFormat format = new DecimalFormat(formatSettingsManager.getLongNumberFormat());
        return format.format((long)sizeInMB);
    }

    public static String getDisplayableName(String cacheName, I18NBean i18nBean) {
        String i18nValue;
        String i18nKey = "cache.name." + cacheName.replace("_v5", "");
        return i18nKey.equals(i18nValue = i18nBean.getText(i18nKey)) ? cacheName : i18nValue;
    }

    public static int asPercentage(long numerator, long denominator) {
        return denominator > 0L ? (int)((double)numerator / (double)denominator * 100.0) : 0;
    }

    public static int calculateCapacityPercentage(long currentSize, long maxSize) {
        return (int)((float)currentSize / (float)maxSize * 100.0f);
    }
}

