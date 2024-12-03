/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.editor.Editor
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.tinymceplugin;

import com.atlassian.confluence.plugin.editor.Editor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class TinyMceEditor
implements Editor {
    private static final Pattern MOZILLA_PATTERN = Pattern.compile("^Mozilla/((?:\\d{2,}\\.)|(?:[\\d&&[^1234]]\\.))");
    private static final Pattern SAFARI_PATTERN = Pattern.compile("AppleWebKit.*Version/(\\d+)\\.");
    private static final Pattern IE_PATTERN = Pattern.compile("MSIE\\s+((?:\\d+)(?:\\.(?:\\d+))*).+Win");

    public boolean supportedUserAgent(String userAgent) {
        if (StringUtils.isNotEmpty((CharSequence)userAgent)) {
            if (userAgent.indexOf("Opera") != -1) {
                return true;
            }
            Matcher mozillaMatcher = MOZILLA_PATTERN.matcher(userAgent);
            if (mozillaMatcher.find()) {
                Matcher safariMatcher = SAFARI_PATTERN.matcher(userAgent);
                if (safariMatcher.find()) {
                    String majorVersionString = safariMatcher.group(1);
                    try {
                        int majorVersion = Integer.parseInt(majorVersionString);
                        if (majorVersion <= 2) {
                            return false;
                        }
                    }
                    catch (NumberFormatException ex) {
                        return false;
                    }
                }
                return true;
            }
            Matcher ieMatcher = IE_PATTERN.matcher(userAgent);
            if (ieMatcher.find()) {
                String versionNumberString = ieMatcher.group(1);
                try {
                    float versionNumber = Float.parseFloat(versionNumberString);
                    if ((double)versionNumber >= 5.5) {
                        return true;
                    }
                }
                catch (NumberFormatException ex) {
                    return false;
                }
            }
            return false;
        }
        return true;
    }
}

