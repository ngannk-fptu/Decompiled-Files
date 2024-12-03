/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset;

import com.sun.xml.fastinfoset.AbstractResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;

public class CommonResourceBundle
extends AbstractResourceBundle {
    public static final String BASE_NAME = "com.sun.xml.fastinfoset.resources.ResourceBundle";
    private static volatile CommonResourceBundle instance = null;
    private static Locale locale = null;
    private ResourceBundle bundle = null;

    protected CommonResourceBundle() {
        this.bundle = ResourceBundle.getBundle(BASE_NAME);
    }

    protected CommonResourceBundle(Locale locale) {
        this.bundle = ResourceBundle.getBundle(BASE_NAME, locale);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static CommonResourceBundle getInstance() {
        if (instance != null) return instance;
        Class<CommonResourceBundle> clazz = CommonResourceBundle.class;
        synchronized (CommonResourceBundle.class) {
            instance = new CommonResourceBundle();
            locale = CommonResourceBundle.parseLocale(null);
            // ** MonitorExit[var0] (shouldn't be in output)
            return instance;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static CommonResourceBundle getInstance(Locale locale) {
        if (instance == null) {
            Class<CommonResourceBundle> clazz = CommonResourceBundle.class;
            synchronized (CommonResourceBundle.class) {
                instance = new CommonResourceBundle(locale);
                // ** MonitorExit[var1_1] (shouldn't be in output)
            }
        }
        Class<CommonResourceBundle> clazz = CommonResourceBundle.class;
        synchronized (CommonResourceBundle.class) {
            if (CommonResourceBundle.locale != locale) {
                instance = new CommonResourceBundle(locale);
            }
            // ** MonitorExit[var1_2] (shouldn't be in output)
            return instance;
        }
    }

    @Override
    public ResourceBundle getBundle() {
        return this.bundle;
    }

    public ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle(BASE_NAME, locale);
    }
}

