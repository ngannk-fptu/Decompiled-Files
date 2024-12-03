/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc.secure;

public interface SecurityConstants {
    public static final String DEFAULT_SECURITY_PROVIDER_CLASS = "com.sun.net.ssl.internal.ssl.Provider";
    public static final String SECURITY_PROVIDER_CLASS = "security.provider";
    public static final String DEFAULT_SECURITY_PROTOCOL = "TLS";
    public static final String SECURITY_PROTOCOL = "security.protocol";
    public static final String DEFAULT_KEY_STORE = "testkeys";
    public static final String KEY_STORE = "javax.net.ssl.keyStore";
    public static final String DEFAULT_KEY_STORE_TYPE = "JKS";
    public static final String KEY_STORE_TYPE = "javax.net.ssl.keyStoreType";
    public static final String DEFAULT_KEY_STORE_PASSWORD = "password";
    public static final String KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";
    public static final String DEFAULT_TRUST_STORE_TYPE = "JKS";
    public static final String TRUST_STORE_TYPE = "javax.net.ssl.trustStoreType";
    public static final String DEFAULT_TRUST_STORE = "truststore";
    public static final String TRUST_STORE = "javax.net.ssl.trustStore";
    public static final String DEFAULT_TRUST_STORE_PASSWORD = "password";
    public static final String TRUST_STORE_PASSWORD = "javax.net.ssl.trustStorePassword";
    public static final String DEFAULT_KEY_MANAGER_TYPE = "SunX509";
    public static final String KEY_MANAGER_TYPE = "sun.ssl.keymanager.type";
    public static final String TRUST_MANAGER_TYPE = "sun.ssl.trustmanager.type";
    public static final String DEFAULT_PROTOCOL_HANDLER_PACKAGES = "com.sun.net.ssl.internal.www.protocol";
    public static final String PROTOCOL_HANDLER_PACKAGES = "java.protocol.handler.pkgs";
}

