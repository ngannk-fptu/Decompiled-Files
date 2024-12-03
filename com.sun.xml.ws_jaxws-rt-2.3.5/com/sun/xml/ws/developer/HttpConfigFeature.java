/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.developer;

import java.lang.reflect.Constructor;
import java.net.CookieHandler;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public final class HttpConfigFeature
extends WebServiceFeature {
    public static final String ID = "http://jax-ws.java.net/features/http-config";
    private static final Constructor cookieManagerConstructor;
    private static final Object cookiePolicy;
    private final CookieHandler cookieJar;

    public HttpConfigFeature() {
        this(HttpConfigFeature.getInternalCookieHandler());
    }

    public HttpConfigFeature(CookieHandler cookieJar) {
        this.enabled = true;
        this.cookieJar = cookieJar;
    }

    private static CookieHandler getInternalCookieHandler() {
        try {
            return (CookieHandler)cookieManagerConstructor.newInstance(null, cookiePolicy);
        }
        catch (Exception e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    public String getID() {
        return ID;
    }

    public CookieHandler getCookieHandler() {
        return this.cookieJar;
    }

    static {
        Object tempPolicy;
        Constructor<?> tempConstructor;
        try {
            Class<?> policyClass = Class.forName("java.net.CookiePolicy");
            Class<?> storeClass = Class.forName("java.net.CookieStore");
            tempConstructor = Class.forName("java.net.CookieManager").getConstructor(storeClass, policyClass);
            tempPolicy = policyClass.getField("ACCEPT_ALL").get(null);
        }
        catch (Exception e) {
            try {
                Class<?> policyClass = Class.forName("com.sun.xml.ws.transport.http.client.CookiePolicy");
                Class<?> storeClass = Class.forName("com.sun.xml.ws.transport.http.client.CookieStore");
                tempConstructor = Class.forName("com.sun.xml.ws.transport.http.client.CookieManager").getConstructor(storeClass, policyClass);
                tempPolicy = policyClass.getField("ACCEPT_ALL").get(null);
            }
            catch (Exception ce) {
                throw new WebServiceException((Throwable)ce);
            }
        }
        cookieManagerConstructor = tempConstructor;
        cookiePolicy = tempPolicy;
    }
}

