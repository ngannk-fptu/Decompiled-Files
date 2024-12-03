/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.util.UserAgentUtil;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

public class MobileUtils {
    private static final String MOBILE_PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-mobile-plugin";

    public static MobileOS getMobileOS(HttpServletRequest request) {
        String userAgent = StringUtils.lowerCase((String)UserAgentUtil.getUserAgent(request));
        if (StringUtils.isBlank((CharSequence)userAgent)) {
            return MobileOS.UNKNOWN;
        }
        return Arrays.stream(MobileOS.values()).filter(value -> value.isMatched(userAgent)).findFirst().orElse(MobileOS.UNKNOWN);
    }

    public static boolean shouldShowBanner(HttpServletRequest request) {
        MobileOS mobileOS = MobileUtils.getMobileOS(request);
        return mobileOS == MobileOS.ANDROID || mobileOS == MobileOS.IOS;
    }

    public static String getMobilePluginKey() {
        return MOBILE_PLUGIN_KEY;
    }

    public static enum MobileOS {
        IOS("ios", ".*(iphone|ipod|ipad).*"),
        ANDROID("android", ".*(android|googletv).*"),
        WINDOWS("windows", ".*(windows phone).*"),
        UNKNOWN("unknown", "");

        private final String value;
        private final String regex;

        private MobileOS(String value, String regex) {
            this.value = value;
            this.regex = regex;
        }

        public String getValue() {
            return this.value;
        }

        public boolean isMatched(@NonNull String userAgent) {
            return userAgent.matches(this.regex);
        }
    }
}

