/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc.secure;

import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import org.apache.xmlrpc.secure.SecurityConstants;

public class SecurityTool
implements SecurityConstants {
    protected static String securityProviderClass;
    private static String securityProtocol;
    private static String keyStorePassword;
    private static String keyStoreType;
    private static String keyStore;
    private static String trustStorePassword;
    private static String trustStoreType;
    private static String trustStore;
    private static String keyManagerType;
    private static String protocolHandlerPackages;

    public static void setup() throws Exception {
        Security.addProvider((Provider)Class.forName(SecurityTool.getSecurityProviderClass()).newInstance());
        System.setProperty("java.protocol.handler.pkgs", SecurityTool.getProtocolHandlerPackages());
        System.setProperty("javax.net.ssl.keyStoreType", SecurityTool.getKeyStoreType());
        System.setProperty("javax.net.ssl.keyStore", SecurityTool.getKeyStore());
        System.setProperty("javax.net.ssl.keyStorePassword", SecurityTool.getKeyStorePassword());
        System.setProperty("javax.net.ssl.trustStoreType", SecurityTool.getTrustStoreType());
        System.setProperty("javax.net.ssl.trustStore", SecurityTool.getTrustStore());
        System.setProperty("javax.net.ssl.trustStorePassword", SecurityTool.getTrustStorePassword());
    }

    public static void setProtocolHandlerPackages(String x) {
        protocolHandlerPackages = x;
    }

    public static String getProtocolHandlerPackages() {
        if (System.getProperty("java.protocol.handler.pkgs") != null) {
            return System.getProperty("java.protocol.handler.pkgs");
        }
        if (protocolHandlerPackages == null) {
            return "com.sun.net.ssl.internal.www.protocol";
        }
        return protocolHandlerPackages;
    }

    public static void setSecurityProviderClass(String x) {
        securityProviderClass = x;
    }

    public static String getSecurityProviderClass() {
        if (System.getProperty("security.provider") != null) {
            return System.getProperty("security.provider");
        }
        if (securityProviderClass == null) {
            return "com.sun.net.ssl.internal.ssl.Provider";
        }
        return securityProviderClass;
    }

    public static void setKeyStorePassword(String x) {
        keyStorePassword = x;
    }

    public static void setSecurityProtocol(String x) {
        securityProtocol = x;
    }

    public static String getSecurityProtocol() {
        if (System.getProperty("security.protocol") != null) {
            return System.getProperty("security.protocol");
        }
        if (securityProtocol == null) {
            return "TLS";
        }
        return securityProtocol;
    }

    public static void setKeyStore(String x) {
        keyStore = x;
    }

    public static String getKeyStore() {
        if (System.getProperty("javax.net.ssl.keyStore") != null) {
            return System.getProperty("javax.net.ssl.keyStore");
        }
        if (keyStore == null) {
            return "testkeys";
        }
        return keyStore;
    }

    public static void setKeyStoreType(String x) {
        keyStoreType = x;
    }

    public static String getKeyStoreType() {
        if (System.getProperty("javax.net.ssl.keyStoreType") != null) {
            return System.getProperty("javax.net.ssl.keyStoreType");
        }
        if (keyStoreType == null) {
            return KeyStore.getDefaultType();
        }
        return keyStoreType;
    }

    public static String getKeyStorePassword() {
        if (System.getProperty("javax.net.ssl.keyStorePassword") != null) {
            return System.getProperty("javax.net.ssl.keyStorePassword");
        }
        if (keyStorePassword == null) {
            return "password";
        }
        return keyStorePassword;
    }

    public static void setTrustStore(String x) {
        trustStore = x;
    }

    public static String getTrustStore() {
        if (System.getProperty("javax.net.ssl.trustStore") != null) {
            return System.getProperty("javax.net.ssl.trustStore");
        }
        if (trustStore == null) {
            return "truststore";
        }
        return trustStore;
    }

    public static void setTrustStoreType(String x) {
        trustStoreType = x;
    }

    public static String getTrustStoreType() {
        if (System.getProperty("javax.net.ssl.trustStoreType") != null) {
            return System.getProperty("javax.net.ssl.trustStoreType");
        }
        if (trustStoreType == null) {
            return KeyStore.getDefaultType();
        }
        return trustStoreType;
    }

    public static void setTrustStorePassword(String x) {
        trustStorePassword = x;
    }

    public static String getTrustStorePassword() {
        if (System.getProperty("javax.net.ssl.trustStorePassword") != null) {
            return System.getProperty("javax.net.ssl.trustStorePassword");
        }
        if (trustStorePassword == null) {
            return "password";
        }
        return trustStorePassword;
    }

    public static void setKeyManagerType(String x) {
        keyManagerType = x;
    }

    public static String getKeyManagerType() {
        if (System.getProperty("sun.ssl.keymanager.type") != null) {
            return System.getProperty("sun.ssl.keymanager.type");
        }
        if (keyManagerType == null) {
            return "SunX509";
        }
        return keyManagerType;
    }
}

