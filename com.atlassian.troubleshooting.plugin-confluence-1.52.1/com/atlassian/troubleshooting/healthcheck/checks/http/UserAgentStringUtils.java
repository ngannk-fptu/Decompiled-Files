/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.atlassian.troubleshooting.healthcheck.checks.http;

import com.atlassian.troubleshooting.healthcheck.checks.http.ProtocolsEvent;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

final class UserAgentStringUtils {
    private static final Pattern NUMBER = Pattern.compile("\\d+");

    private UserAgentStringUtils() {
    }

    public static boolean supportsModernProtocols(ProtocolsEvent protocolsEvent) {
        Optional<String> maybeUserAgent = protocolsEvent.getUserAgent();
        return maybeUserAgent.filter(UserAgentStringUtils::supportsModernProtocols).isPresent();
    }

    static boolean supportsModernProtocols(String userAgentString) {
        if ("use-js-client-hints".equals(userAgentString)) {
            return true;
        }
        if (userAgentString.contains("rv:11.0") && userAgentString.contains("Windows NT 10")) {
            return true;
        }
        if (userAgentString.contains("Edge/")) {
            return UserAgentStringUtils.getVersion(userAgentString, "Edge/", NUMBER) >= 12;
        }
        if (userAgentString.contains("Chrome")) {
            return UserAgentStringUtils.getVersion(userAgentString, "Chrome/", NUMBER) >= 51;
        }
        if (userAgentString.contains("Safari/")) {
            return UserAgentStringUtils.getVersion(userAgentString, "Version/", NUMBER) >= 11;
        }
        if (userAgentString.contains("Firefox")) {
            return UserAgentStringUtils.getVersion(userAgentString, "Firefox/", NUMBER) >= 36;
        }
        return false;
    }

    private static int getVersion(String userAgent, String versionStart, Pattern versionPattern) {
        String str = StringUtils.substringAfter((String)userAgent, (String)versionStart);
        Matcher matcher = versionPattern.matcher(str);
        if (!matcher.find()) {
            return 0;
        }
        return NumberUtils.toInt((String)matcher.group(), (int)0);
    }
}

