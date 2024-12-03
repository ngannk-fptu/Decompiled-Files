/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.http;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Deprecated(forRemoval=true)
public abstract class Authenticator
implements Serializable {
    private Map properties = new HashMap();
    @Deprecated
    public static final String[] SIMPLE_PROPERTIES = new String[]{"username", "password"};

    public String[] getPropertyNames() {
        return new String[]{"username", "password"};
    }

    public void setProperty(String key, String value) {
        for (int i = 0; i < this.getPropertyNames().length; ++i) {
            String propertyName = this.getPropertyNames()[i];
            if (!propertyName.equals(key)) continue;
            this.properties.put(key, value);
            return;
        }
        throw new IllegalArgumentException("Unknown property: " + key);
    }

    protected String getProperty(String key) {
        return (String)this.properties.get(key);
    }
}

