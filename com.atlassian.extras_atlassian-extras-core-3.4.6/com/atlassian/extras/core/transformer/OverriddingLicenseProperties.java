/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.common.util.LicenseProperties
 *  com.atlassian.extras.common.util.ProductLicenseProperties
 */
package com.atlassian.extras.core.transformer;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.common.util.ProductLicenseProperties;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class OverriddingLicenseProperties
extends ProductLicenseProperties {
    private final LicenseProperties originalProperties;

    public OverriddingLicenseProperties(Product product, LicenseProperties config, Properties modifications) {
        super(product, modifications);
        this.originalProperties = config;
    }

    public String getProperty(String propertyName, String defaultValue) {
        String value = super.getProperty(propertyName, null);
        if (value == null) {
            return this.originalProperties.getProperty(propertyName, defaultValue);
        }
        return value;
    }

    public Map<String, String> getPropertiesEndingWith(String ending) {
        HashMap<String, String> props = new HashMap<String, String>();
        props.putAll(this.originalProperties.getPropertiesEndingWith(ending));
        props.putAll(super.getPropertiesEndingWith(ending));
        return props;
    }
}

