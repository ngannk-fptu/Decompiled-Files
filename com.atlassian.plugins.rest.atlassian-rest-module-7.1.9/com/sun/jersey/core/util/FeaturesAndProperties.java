/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.util;

import java.util.Map;

public interface FeaturesAndProperties {
    public static final String FEATURE_DISABLE_XML_SECURITY = "com.sun.jersey.config.feature.DisableXmlSecurity";
    public static final String FEATURE_FORMATTED = "com.sun.jersey.config.feature.Formatted";
    public static final String FEATURE_XMLROOTELEMENT_PROCESSING = "com.sun.jersey.config.feature.XmlRootElementProcessing";
    public static final String FEATURE_PRE_1_4_PROVIDER_PRECEDENCE = "com.sun.jersey.config.feature.Pre14ProviderPrecedence";

    public Map<String, Boolean> getFeatures();

    public boolean getFeature(String var1);

    public Map<String, Object> getProperties();

    public Object getProperty(String var1);
}

