/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class Localizer {
    private static Localizer instance = null;
    private static final String DEFAULT_BUNDLE = "abderamessages";
    private final Locale locale;
    private final ResourceBundle bundle;

    public static synchronized Localizer getInstance() {
        if (instance == null) {
            instance = new Localizer();
        }
        return instance;
    }

    public static synchronized void setInstance(Localizer localizer) {
        instance = localizer;
    }

    public static String get(String key) {
        return Localizer.getInstance().getValue(key);
    }

    public static String get(String key, String defaultValue) {
        return Localizer.getInstance().getValue(key, defaultValue);
    }

    public static String format(String key, Object ... args) {
        return Localizer.getInstance().formatValue(key, args);
    }

    public static String sprintf(String key, Object ... args) {
        return Localizer.getInstance().sprintfValue(key, args);
    }

    public Localizer() {
        this(Locale.getDefault(), Thread.currentThread().getContextClassLoader());
    }

    public Localizer(Locale locale, ClassLoader loader) {
        this(Localizer.initResourceBundle(DEFAULT_BUNDLE, locale, loader), locale);
    }

    public Localizer(String bundle) {
        this(Localizer.initResourceBundle(bundle, Locale.getDefault(), Thread.currentThread().getContextClassLoader()));
    }

    public Localizer(String bundle, Locale locale) {
        this(Localizer.initResourceBundle(bundle, locale, Thread.currentThread().getContextClassLoader()));
    }

    public Localizer(ResourceBundle bundle) {
        this(bundle, bundle.getLocale());
    }

    public Localizer(ResourceBundle bundle, Locale locale) {
        this.bundle = bundle;
        this.locale = locale;
    }

    private static ResourceBundle initResourceBundle(String bundle, Locale locale, ClassLoader loader) {
        try {
            return ResourceBundle.getBundle(bundle, locale, loader);
        }
        catch (Exception e) {
            return null;
        }
    }

    public Locale getLocale() {
        return this.locale;
    }

    public String getValue(String key) {
        try {
            return this.bundle.getString(key);
        }
        catch (Exception e) {
            return null;
        }
    }

    public String getValue(String key, String defaultValue) {
        String value = this.getValue(key);
        return value != null ? value : defaultValue;
    }

    public String formatValue(String key, Object ... args) {
        String value = this.getValue(key);
        return value != null ? MessageFormat.format(value, args) : null;
    }

    public String sprintfValue(String key, Object ... args) {
        String value = this.getValue(key);
        return value != null ? String.format(this.locale, value, args) : null;
    }
}

