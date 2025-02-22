/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.internal;

import org.aspectj.runtime.internal.cflowstack.ThreadCounter;
import org.aspectj.runtime.internal.cflowstack.ThreadStackFactory;
import org.aspectj.runtime.internal.cflowstack.ThreadStackFactoryImpl;
import org.aspectj.runtime.internal.cflowstack.ThreadStackFactoryImpl11;

public class CFlowCounter {
    private static ThreadStackFactory tsFactory;
    private ThreadCounter flowHeightHandler = tsFactory.getNewThreadCounter();

    public void inc() {
        this.flowHeightHandler.inc();
    }

    public void dec() {
        this.flowHeightHandler.dec();
        if (!this.flowHeightHandler.isNotZero()) {
            this.flowHeightHandler.removeThreadCounter();
        }
    }

    public boolean isValid() {
        return this.flowHeightHandler.isNotZero();
    }

    private static ThreadStackFactory getThreadLocalStackFactory() {
        return new ThreadStackFactoryImpl();
    }

    private static ThreadStackFactory getThreadLocalStackFactoryFor11() {
        return new ThreadStackFactoryImpl11();
    }

    private static void selectFactoryForVMVersion() {
        String v;
        String override = CFlowCounter.getSystemPropertyWithoutSecurityException("aspectj.runtime.cflowstack.usethreadlocal", "unspecified");
        boolean useThreadLocalImplementation = false;
        useThreadLocalImplementation = override.equals("unspecified") ? (v = System.getProperty("java.class.version", "0.0")).compareTo("46.0") >= 0 : override.equals("yes") || override.equals("true");
        tsFactory = useThreadLocalImplementation ? CFlowCounter.getThreadLocalStackFactory() : CFlowCounter.getThreadLocalStackFactoryFor11();
    }

    private static String getSystemPropertyWithoutSecurityException(String aPropertyName, String aDefaultValue) {
        try {
            return System.getProperty(aPropertyName, aDefaultValue);
        }
        catch (SecurityException ex) {
            return aDefaultValue;
        }
    }

    public static String getThreadStackFactoryClassName() {
        return tsFactory.getClass().getName();
    }

    static {
        CFlowCounter.selectFactoryForVMVersion();
    }
}

