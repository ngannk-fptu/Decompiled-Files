/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.http;

import org.apache.tomcat.util.res.StringManager;

public enum SameSiteCookies {
    UNSET("Unset"),
    NONE("None"),
    LAX("Lax"),
    STRICT("Strict");

    private static final StringManager sm;
    private final String value;

    private SameSiteCookies(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static SameSiteCookies fromString(String value) {
        for (SameSiteCookies sameSiteCookies : SameSiteCookies.values()) {
            if (!sameSiteCookies.getValue().equalsIgnoreCase(value)) continue;
            return sameSiteCookies;
        }
        throw new IllegalStateException(sm.getString("cookies.invalidSameSiteCookies", new Object[]{value}));
    }

    static {
        sm = StringManager.getManager(SameSiteCookies.class);
    }
}

