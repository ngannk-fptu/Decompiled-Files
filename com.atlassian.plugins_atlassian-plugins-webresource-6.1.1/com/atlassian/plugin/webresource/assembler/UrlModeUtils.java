/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.UrlMode
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.plugin.webresource.UrlMode;

public class UrlModeUtils {
    public static com.atlassian.webresource.api.UrlMode convert(UrlMode urlMode) {
        switch (urlMode) {
            case ABSOLUTE: {
                return com.atlassian.webresource.api.UrlMode.ABSOLUTE;
            }
            case RELATIVE: {
                return com.atlassian.webresource.api.UrlMode.RELATIVE;
            }
            case AUTO: {
                return com.atlassian.webresource.api.UrlMode.AUTO;
            }
        }
        throw new IllegalArgumentException("Unrecognised UrlMode: " + (Object)((Object)urlMode));
    }
}

