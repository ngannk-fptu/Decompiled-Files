/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.core;

public class AprStatus {
    private static volatile boolean aprInitialized = false;
    private static volatile boolean aprAvailable = false;
    private static volatile boolean useAprConnector = false;
    private static volatile boolean useOpenSSL = true;
    private static volatile boolean instanceCreated = false;

    public static boolean isAprInitialized() {
        return aprInitialized;
    }

    public static boolean isAprAvailable() {
        return aprAvailable;
    }

    public static boolean getUseAprConnector() {
        return useAprConnector;
    }

    public static boolean getUseOpenSSL() {
        return useOpenSSL;
    }

    public static boolean isInstanceCreated() {
        return instanceCreated;
    }

    public static void setAprInitialized(boolean aprInitialized) {
        AprStatus.aprInitialized = aprInitialized;
    }

    public static void setAprAvailable(boolean aprAvailable) {
        AprStatus.aprAvailable = aprAvailable;
    }

    public static void setUseAprConnector(boolean useAprConnector) {
        AprStatus.useAprConnector = useAprConnector;
    }

    public static void setUseOpenSSL(boolean useOpenSSL) {
        AprStatus.useOpenSSL = useOpenSSL;
    }

    public static void setInstanceCreated(boolean instanceCreated) {
        AprStatus.instanceCreated = instanceCreated;
    }
}

