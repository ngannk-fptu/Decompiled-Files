/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 */
package com.atlassian.plugins.navlink.consumer.menu.client.capabilities;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;

public class CapabilitiesRestResourceUrlFactory {
    public static String createRequestUrl(ReadOnlyApplicationLink applicationLink) {
        return applicationLink.getRpcUrl().toASCIIString() + "/rest/capabilities";
    }
}

