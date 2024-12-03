/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.embedded.admin.util;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;

public final class UrlValidationUtil {
    private UrlValidationUtil() {
    }

    public static boolean isValidUrl(String url) {
        if (!StringUtils.isEmpty((CharSequence)url)) {
            try {
                URI uri = new URI(url);
                if (uri.getHost() == null) {
                    return false;
                }
            }
            catch (URISyntaxException e) {
                return false;
            }
        }
        return true;
    }
}

