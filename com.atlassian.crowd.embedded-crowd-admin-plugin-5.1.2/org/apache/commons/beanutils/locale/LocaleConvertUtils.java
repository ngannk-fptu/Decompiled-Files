/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.FastHashMap
 */
package org.apache.commons.beanutils.locale;

import java.util.Locale;
import org.apache.commons.beanutils.locale.LocaleConvertUtilsBean;
import org.apache.commons.beanutils.locale.LocaleConverter;
import org.apache.commons.collections.FastHashMap;

public class LocaleConvertUtils {
    public static Locale getDefaultLocale() {
        return LocaleConvertUtilsBean.getInstance().getDefaultLocale();
    }

    public static void setDefaultLocale(Locale locale) {
        LocaleConvertUtilsBean.getInstance().setDefaultLocale(locale);
    }

    public static boolean getApplyLocalized() {
        return LocaleConvertUtilsBean.getInstance().getApplyLocalized();
    }

    public static void setApplyLocalized(boolean newApplyLocalized) {
        LocaleConvertUtilsBean.getInstance().setApplyLocalized(newApplyLocalized);
    }

    public static String convert(Object value) {
        return LocaleConvertUtilsBean.getInstance().convert(value);
    }

    public static String convert(Object value, String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(value, pattern);
    }

    public static String convert(Object value, Locale locale, String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(value, locale, pattern);
    }

    public static Object convert(String value, Class<?> clazz) {
        return LocaleConvertUtilsBean.getInstance().convert(value, clazz);
    }

    public static Object convert(String value, Class<?> clazz, String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(value, clazz, pattern);
    }

    public static Object convert(String value, Class<?> clazz, Locale locale, String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(value, clazz, locale, pattern);
    }

    public static Object convert(String[] values, Class<?> clazz, String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(values, clazz, pattern);
    }

    public static Object convert(String[] values, Class<?> clazz) {
        return LocaleConvertUtilsBean.getInstance().convert(values, clazz);
    }

    public static Object convert(String[] values, Class<?> clazz, Locale locale, String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(values, clazz, locale, pattern);
    }

    public static void register(LocaleConverter converter, Class<?> clazz, Locale locale) {
        LocaleConvertUtilsBean.getInstance().register(converter, clazz, locale);
    }

    public static void deregister() {
        LocaleConvertUtilsBean.getInstance().deregister();
    }

    public static void deregister(Locale locale) {
        LocaleConvertUtilsBean.getInstance().deregister(locale);
    }

    public static void deregister(Class<?> clazz, Locale locale) {
        LocaleConvertUtilsBean.getInstance().deregister(clazz, locale);
    }

    public static LocaleConverter lookup(Class<?> clazz, Locale locale) {
        return LocaleConvertUtilsBean.getInstance().lookup(clazz, locale);
    }

    @Deprecated
    protected static FastHashMap lookup(Locale locale) {
        return LocaleConvertUtilsBean.getInstance().lookup(locale);
    }

    @Deprecated
    protected static FastHashMap create(Locale locale) {
        return LocaleConvertUtilsBean.getInstance().create(locale);
    }
}

