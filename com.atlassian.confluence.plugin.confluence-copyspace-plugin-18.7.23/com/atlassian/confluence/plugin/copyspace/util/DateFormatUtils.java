/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.core.util.DateUtils
 *  com.atlassian.core.util.DateUtils$Duration
 */
package com.atlassian.confluence.plugin.copyspace.util;

import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.core.util.DateUtils;

public final class DateFormatUtils {
    public static String prettyTime(long time, I18NBean i18NBean) {
        StringBuilder result = new StringBuilder();
        long numSec = time / DateUtils.SECOND_MILLIS;
        if (numSec >= DateUtils.Duration.HOUR.getSeconds()) {
            long timeInHours = numSec / DateUtils.Duration.HOUR.getSeconds();
            result.append(timeInHours).append(' ');
            if (timeInHours > 1L) {
                result.append(i18NBean.getText("core.dateutils.hours"));
            } else {
                result.append(i18NBean.getText("core.dateutils.hour"));
            }
            numSec %= DateUtils.Duration.HOUR.getSeconds();
        }
        if (numSec >= DateUtils.Duration.MINUTE.getSeconds()) {
            long timeInMinutes = numSec / DateUtils.Duration.MINUTE.getSeconds();
            result.append(result.length() > 0 ? ", " : "").append(timeInMinutes).append(' ');
            if (timeInMinutes > 1L) {
                result.append(i18NBean.getText("core.dateutils.minutes"));
            } else {
                result.append(i18NBean.getText("core.dateutils.minute"));
            }
            numSec %= DateUtils.Duration.MINUTE.getSeconds();
        }
        if (numSec >= 1L && numSec < DateUtils.Duration.MINUTE.getSeconds()) {
            result.append(result.length() > 0 ? ", " : "").append(numSec).append(' ');
            if (numSec > 1L) {
                result.append(i18NBean.getText("core.dateutils.seconds"));
            } else {
                result.append(i18NBean.getText("core.dateutils.second"));
            }
        }
        if (time >= 1L && time < DateUtils.SECOND_MILLIS) {
            result.append("1 ").append(i18NBean.getText("core.dateutils.second"));
        }
        return result.toString();
    }
}

