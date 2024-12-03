/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.BasicScheme;
import org.apache.commons.httpclient.auth.DigestScheme;
import org.apache.commons.httpclient.auth.NTLMScheme;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AuthPolicy {
    private static final HashMap SCHEMES = new HashMap();
    private static final ArrayList SCHEME_LIST = new ArrayList();
    public static final String AUTH_SCHEME_PRIORITY = "http.auth.scheme-priority";
    public static final String NTLM = "NTLM";
    public static final String DIGEST = "Digest";
    public static final String BASIC = "Basic";
    protected static final Log LOG;

    public static synchronized void registerAuthScheme(String id, Class clazz) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("Authentication scheme class may not be null");
        }
        SCHEMES.put(id.toLowerCase(Locale.ENGLISH), clazz);
        SCHEME_LIST.add(id.toLowerCase(Locale.ENGLISH));
    }

    public static synchronized void unregisterAuthScheme(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        SCHEMES.remove(id.toLowerCase(Locale.ENGLISH));
        SCHEME_LIST.remove(id.toLowerCase(Locale.ENGLISH));
    }

    public static synchronized AuthScheme getAuthScheme(String id) throws IllegalStateException {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        Class clazz = (Class)SCHEMES.get(id.toLowerCase(Locale.ENGLISH));
        if (clazz != null) {
            try {
                return (AuthScheme)clazz.newInstance();
            }
            catch (Exception e) {
                LOG.error((Object)("Error initializing authentication scheme: " + id), (Throwable)e);
                throw new IllegalStateException(id + " authentication scheme implemented by " + clazz.getName() + " could not be initialized");
            }
        }
        throw new IllegalStateException("Unsupported authentication scheme " + id);
    }

    public static synchronized List getDefaultAuthPrefs() {
        return (List)SCHEME_LIST.clone();
    }

    static {
        AuthPolicy.registerAuthScheme(NTLM, NTLMScheme.class);
        AuthPolicy.registerAuthScheme(DIGEST, DigestScheme.class);
        AuthPolicy.registerAuthScheme(BASIC, BasicScheme.class);
        LOG = LogFactory.getLog(AuthPolicy.class);
    }
}

