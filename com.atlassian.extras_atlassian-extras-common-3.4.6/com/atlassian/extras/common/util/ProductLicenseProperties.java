/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 */
package com.atlassian.extras.common.util;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.common.DateEditor;
import com.atlassian.extras.common.LicensePropertiesConstants;
import com.atlassian.extras.common.util.LicenseProperties;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ProductLicenseProperties
implements LicenseProperties {
    private final Product product;
    private final Properties properties;

    public ProductLicenseProperties(Product product, Properties properties) {
        this.product = product;
        if (product == null) {
            throw new IllegalArgumentException("Product must NOT be null!");
        }
        if (properties == null) {
            throw new IllegalArgumentException("Properties must NOT be null!");
        }
        this.properties = properties;
    }

    @Override
    public String getProperty(String s) {
        return this.getProperty(s, null);
    }

    @Override
    public String getProperty(String s, String defaultValue) {
        String o = this.properties.getProperty(LicensePropertiesConstants.getKey(this.product, s));
        return o != null ? o : this.properties.getProperty(s, defaultValue);
    }

    @Override
    public int getInt(String propertyName, int defaultValue) {
        String stringValue = this.getProperty(propertyName);
        if (stringValue == null || stringValue.length() == 0) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(stringValue);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public Date getDate(String key, Date defaultValue) {
        String stringValue = this.getProperty(key);
        if (stringValue == null || stringValue.length() == 0) {
            return defaultValue;
        }
        return DateEditor.getDate(stringValue);
    }

    @Override
    public boolean getBoolean(String key) {
        return Boolean.valueOf(this.getProperty(key));
    }

    @Override
    public Map<String, String> getPropertiesEndingWith(String ending) {
        HashMap<String, String> props = new HashMap<String, String>();
        Enumeration<?> enumeration = this.properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String propName = enumeration.nextElement().toString();
            if (!propName.endsWith(ending)) continue;
            props.put(propName, this.getProperty(propName));
        }
        return props;
    }
}

