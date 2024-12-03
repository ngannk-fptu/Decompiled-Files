/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0.impl;

import java.util.Enumeration;
import java.util.Properties;

public class AuthMaskingProperties
extends Properties {
    public AuthMaskingProperties() {
    }

    public AuthMaskingProperties(Properties p) {
        super(p);
    }

    public static AuthMaskingProperties fromAnyProperties(Properties p) {
        AuthMaskingProperties out = new AuthMaskingProperties();
        Enumeration<?> e = p.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            out.setProperty(key, p.getProperty(key));
        }
        return out;
    }

    private String normalToString() {
        return super.toString();
    }

    @Override
    public String toString() {
        boolean hasPassword;
        boolean hasUser = this.get("user") != null;
        boolean bl = hasPassword = this.get("password") != null;
        if (hasUser || hasPassword) {
            AuthMaskingProperties clone = (AuthMaskingProperties)this.clone();
            if (hasUser) {
                clone.put("user", "******");
            }
            if (hasPassword) {
                clone.put("password", "******");
            }
            return clone.normalToString();
        }
        return this.normalToString();
    }
}

