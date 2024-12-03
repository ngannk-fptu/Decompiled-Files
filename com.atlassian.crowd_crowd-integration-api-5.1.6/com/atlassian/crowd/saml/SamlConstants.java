/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.saml;

import org.apache.commons.lang3.StringUtils;

public final class SamlConstants {
    public static final String CROWD_ATTRIBUTES_PREFIX = "atl.crowd";
    public static final String CROWD_MISC_PROPERTIES_PREFIX = "atl.crowd.properties";
    public static final String REMEMBER_ME_ATTRIBUTE_NAME = "atl.crowd.properties.remember_me";
    public static final String PLUGIN_CONSUMER_SUFFIX = "plugins/servlet/samlconsumer";

    private SamlConstants() {
    }

    public static boolean isPluginConsumerUrl(String samlConsumerUrl) {
        return StringUtils.stripEnd((String)samlConsumerUrl, (String)"/").endsWith(PLUGIN_CONSUMER_SUFFIX);
    }
}

