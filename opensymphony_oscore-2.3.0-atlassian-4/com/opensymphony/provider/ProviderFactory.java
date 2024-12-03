/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.provider;

import com.opensymphony.provider.Provider;
import com.opensymphony.provider.ProviderConfigurationException;

public class ProviderFactory {
    private static ProviderFactory instance;

    private ProviderFactory() {
    }

    public static ProviderFactory getInstance() {
        if (instance == null) {
            instance = new ProviderFactory();
        }
        return instance;
    }

    public Provider getProvider(String property, String defaultClass) {
        String providerClassName = System.getProperty(property);
        Provider result = null;
        if (providerClassName != null && providerClassName.trim().length() > 0) {
            result = this.load(providerClassName);
            if (result == null) {
                System.err.println("Provider " + providerClassName + " cannot be loaded. \nUsing " + defaultClass + " instead.");
                result = this.load(defaultClass);
            }
        } else {
            result = this.load(defaultClass);
        }
        if (result == null) {
            System.err.println("!!! CANNOT LOAD DEFAULT PROVIDER : " + defaultClass + "!!!");
        }
        return result;
    }

    private Provider load(String className) {
        try {
            Class<?> providerClass = null;
            try {
                providerClass = Class.forName(className);
            }
            catch (ClassNotFoundException e) {
                providerClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
            }
            Provider result = (Provider)providerClass.newInstance();
            result.init();
            return result;
        }
        catch (ProviderConfigurationException e) {
            if (e.getCause() != null) {
                e.getCause().printStackTrace(System.err);
            } else {
                e.printStackTrace(System.err);
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }
}

