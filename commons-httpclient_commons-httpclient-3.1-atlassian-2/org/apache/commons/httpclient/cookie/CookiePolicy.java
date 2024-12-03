/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.cookie;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.cookie.CookieSpecBase;
import org.apache.commons.httpclient.cookie.IgnoreCookiesSpec;
import org.apache.commons.httpclient.cookie.NetscapeDraftSpec;
import org.apache.commons.httpclient.cookie.RFC2109Spec;
import org.apache.commons.httpclient.cookie.RFC2965Spec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class CookiePolicy {
    private static Map SPECS = Collections.synchronizedMap(new HashMap());
    public static final String BROWSER_COMPATIBILITY = "compatibility";
    public static final String NETSCAPE = "netscape";
    public static final String RFC_2109 = "rfc2109";
    public static final String RFC_2965 = "rfc2965";
    public static final String IGNORE_COOKIES = "ignoreCookies";
    public static final String DEFAULT = "default";
    public static final int COMPATIBILITY = 0;
    public static final int NETSCAPE_DRAFT = 1;
    public static final int RFC2109 = 2;
    public static final int RFC2965 = 3;
    private static int defaultPolicy;
    protected static final Log LOG;

    public static void registerCookieSpec(String id, Class clazz) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("Cookie spec class may not be null");
        }
        SPECS.put(id.toLowerCase(Locale.ENGLISH), clazz);
    }

    public static void unregisterCookieSpec(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        SPECS.remove(id.toLowerCase(Locale.ENGLISH));
    }

    public static CookieSpec getCookieSpec(String id) throws IllegalStateException {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        Class clazz = (Class)SPECS.get(id.toLowerCase(Locale.ENGLISH));
        if (clazz != null) {
            try {
                return (CookieSpec)clazz.newInstance();
            }
            catch (Exception e) {
                LOG.error((Object)("Error initializing cookie spec: " + id), (Throwable)e);
                throw new IllegalStateException(id + " cookie spec implemented by " + clazz.getName() + " could not be initialized");
            }
        }
        throw new IllegalStateException("Unsupported cookie spec " + id);
    }

    public static int getDefaultPolicy() {
        return defaultPolicy;
    }

    public static void setDefaultPolicy(int policy) {
        defaultPolicy = policy;
    }

    public static CookieSpec getSpecByPolicy(int policy) {
        switch (policy) {
            case 0: {
                return new CookieSpecBase();
            }
            case 1: {
                return new NetscapeDraftSpec();
            }
            case 2: {
                return new RFC2109Spec();
            }
            case 3: {
                return new RFC2965Spec();
            }
        }
        return CookiePolicy.getDefaultSpec();
    }

    public static CookieSpec getDefaultSpec() {
        try {
            return CookiePolicy.getCookieSpec(DEFAULT);
        }
        catch (IllegalStateException e) {
            LOG.warn((Object)"Default cookie policy is not registered");
            return new RFC2109Spec();
        }
    }

    public static CookieSpec getSpecByVersion(int ver) {
        switch (ver) {
            case 0: {
                return new NetscapeDraftSpec();
            }
            case 1: {
                return new RFC2109Spec();
            }
        }
        return CookiePolicy.getDefaultSpec();
    }

    public static CookieSpec getCompatibilitySpec() {
        return CookiePolicy.getSpecByPolicy(0);
    }

    public static String[] getRegisteredCookieSpecs() {
        return SPECS.keySet().toArray(new String[SPECS.size()]);
    }

    static {
        CookiePolicy.registerCookieSpec(DEFAULT, RFC2109Spec.class);
        CookiePolicy.registerCookieSpec(RFC_2109, RFC2109Spec.class);
        CookiePolicy.registerCookieSpec(RFC_2965, RFC2965Spec.class);
        CookiePolicy.registerCookieSpec(BROWSER_COMPATIBILITY, CookieSpecBase.class);
        CookiePolicy.registerCookieSpec(NETSCAPE, NetscapeDraftSpec.class);
        CookiePolicy.registerCookieSpec(IGNORE_COOKIES, IgnoreCookiesSpec.class);
        defaultPolicy = 2;
        LOG = LogFactory.getLog(CookiePolicy.class);
    }
}

