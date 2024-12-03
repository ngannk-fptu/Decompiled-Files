/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base.config;

import java.util.Enumeration;
import java.util.Vector;
import org.jfree.base.config.HierarchicalConfiguration;

public class SystemPropertyConfiguration
extends HierarchicalConfiguration {
    public void setConfigProperty(String key, String value) {
        throw new UnsupportedOperationException("The SystemPropertyConfiguration is readOnly");
    }

    public String getConfigProperty(String key, String defaultValue) {
        try {
            String value = System.getProperty(key);
            if (value != null) {
                return value;
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        return super.getConfigProperty(key, defaultValue);
    }

    public boolean isLocallyDefined(String key) {
        try {
            return System.getProperties().containsKey(key);
        }
        catch (SecurityException se) {
            return false;
        }
    }

    public Enumeration getConfigProperties() {
        try {
            return System.getProperties().keys();
        }
        catch (SecurityException se) {
            return new Vector().elements();
        }
    }
}

