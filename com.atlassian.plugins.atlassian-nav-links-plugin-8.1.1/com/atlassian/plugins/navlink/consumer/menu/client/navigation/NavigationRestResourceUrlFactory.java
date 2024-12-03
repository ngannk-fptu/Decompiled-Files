/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.navlink.consumer.menu.client.navigation;

import com.atlassian.plugins.navlink.producer.capabilities.CapabilityKey;
import com.atlassian.plugins.navlink.producer.capabilities.RemoteApplicationWithCapabilities;
import com.atlassian.plugins.navlink.producer.navigation.rest.LanguageParameter;
import java.util.Locale;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class NavigationRestResourceUrlFactory {
    @Nullable
    public static String createRequestUrl(RemoteApplicationWithCapabilities application, Locale locale) {
        String capabilityUrl = StringUtils.trimToNull((String)application.getCapabilityUrl(CapabilityKey.NAVIGATION.getKey()));
        return capabilityUrl != null ? String.format("%s?lang=%s", capabilityUrl, LanguageParameter.encodeValue(locale)) : null;
    }
}

