/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.util.profiling;

import com.google.common.base.CharMatcher;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

public class ConfluenceMonitoringNameGenerator {
    private static final Pattern URI_COLLAPSE_ON_PREFIX = Pattern.compile("^/(s|x|display/[^~/][^/]*|download|images|label/[^/]+|plugins/servlet/[^/]+|questions|rest/[^/]+/[^/]+|rpc/trackback)(:?/.*|)", 2);
    private static final Pattern URI_COLLAPSE_USER_SPACE = Pattern.compile("^/display/~[^/]+(|/.*)", 2);

    private ConfluenceMonitoringNameGenerator() {
    }

    public static String generateName(HttpServletRequest request) {
        Matcher mp;
        int jsessionOffset;
        int semiOffset;
        int contextOffset = request.getContextPath().length();
        Object result = request.getRequestURI().substring(contextOffset);
        if (((String)result).length() > 0 && ((String)result).charAt(0) == '/') {
            result = "/" + CharMatcher.is((char)'/').trimLeadingFrom((CharSequence)result);
        }
        if (0 <= (semiOffset = ((String)result).indexOf(59))) {
            result = ((String)result).substring(0, semiOffset);
        }
        if (0 <= (jsessionOffset = ((String)result).toLowerCase().lastIndexOf("jsessionid"))) {
            result = ((String)result).substring(0, jsessionOffset);
        }
        if ((mp = URI_COLLAPSE_ON_PREFIX.matcher((CharSequence)result)).matches()) {
            result = mp.group(1) + ".%";
        } else {
            Matcher mu = URI_COLLAPSE_USER_SPACE.matcher((CharSequence)result);
            if (mu.matches()) {
                result = "display.[userspace].%";
            }
        }
        return CharMatcher.is((char)'.').trimFrom((CharSequence)CharMatcher.is((char)'/').replaceFrom((CharSequence)result, '.'));
    }
}

