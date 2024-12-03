/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.localization.Localizable
 *  com.sun.istack.localization.LocalizableMessageFactory
 *  com.sun.istack.localization.LocalizableMessageFactory$ResourceBundleSupplier
 *  com.sun.istack.localization.Localizer
 */
package com.sun.xml.ws.resources;

import com.sun.istack.localization.Localizable;
import com.sun.istack.localization.LocalizableMessageFactory;
import com.sun.istack.localization.Localizer;
import java.util.Locale;
import java.util.ResourceBundle;

public final class ManagementMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.management";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.management", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableWSM_1008_EXPECTED_INTEGER_DISPOSE_DELAY_VALUE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSM_1008_EXPECTED_INTEGER_DISPOSE_DELAY_VALUE", new Object[]{arg0});
    }

    public static String WSM_1008_EXPECTED_INTEGER_DISPOSE_DELAY_VALUE(Object arg0) {
        return LOCALIZER.localize(ManagementMessages.localizableWSM_1008_EXPECTED_INTEGER_DISPOSE_DELAY_VALUE(arg0));
    }

    public static Localizable localizableWSM_1003_MANAGEMENT_ASSERTION_MISSING_ID(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSM_1003_MANAGEMENT_ASSERTION_MISSING_ID", new Object[]{arg0});
    }

    public static String WSM_1003_MANAGEMENT_ASSERTION_MISSING_ID(Object arg0) {
        return LOCALIZER.localize(ManagementMessages.localizableWSM_1003_MANAGEMENT_ASSERTION_MISSING_ID(arg0));
    }

    public static Localizable localizableWSM_1005_EXPECTED_COMMUNICATION_CHILD() {
        return MESSAGE_FACTORY.getMessage("WSM_1005_EXPECTED_COMMUNICATION_CHILD", new Object[0]);
    }

    public static String WSM_1005_EXPECTED_COMMUNICATION_CHILD() {
        return LOCALIZER.localize(ManagementMessages.localizableWSM_1005_EXPECTED_COMMUNICATION_CHILD());
    }

    public static Localizable localizableWSM_1006_CLIENT_MANAGEMENT_ENABLED() {
        return MESSAGE_FACTORY.getMessage("WSM_1006_CLIENT_MANAGEMENT_ENABLED", new Object[0]);
    }

    public static String WSM_1006_CLIENT_MANAGEMENT_ENABLED() {
        return LOCALIZER.localize(ManagementMessages.localizableWSM_1006_CLIENT_MANAGEMENT_ENABLED());
    }

    public static Localizable localizableWSM_1002_EXPECTED_MANAGEMENT_ASSERTION(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSM_1002_EXPECTED_MANAGEMENT_ASSERTION", new Object[]{arg0});
    }

    public static String WSM_1002_EXPECTED_MANAGEMENT_ASSERTION(Object arg0) {
        return LOCALIZER.localize(ManagementMessages.localizableWSM_1002_EXPECTED_MANAGEMENT_ASSERTION(arg0));
    }

    public static Localizable localizableWSM_1001_FAILED_ASSERTION(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSM_1001_FAILED_ASSERTION", new Object[]{arg0});
    }

    public static String WSM_1001_FAILED_ASSERTION(Object arg0) {
        return LOCALIZER.localize(ManagementMessages.localizableWSM_1001_FAILED_ASSERTION(arg0));
    }

    public static Localizable localizableWSM_1007_FAILED_MODEL_TRANSLATOR_INSTANTIATION() {
        return MESSAGE_FACTORY.getMessage("WSM_1007_FAILED_MODEL_TRANSLATOR_INSTANTIATION", new Object[0]);
    }

    public static String WSM_1007_FAILED_MODEL_TRANSLATOR_INSTANTIATION() {
        return LOCALIZER.localize(ManagementMessages.localizableWSM_1007_FAILED_MODEL_TRANSLATOR_INSTANTIATION());
    }

    public static Localizable localizableWSM_1004_EXPECTED_XML_TAG(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("WSM_1004_EXPECTED_XML_TAG", new Object[]{arg0, arg1});
    }

    public static String WSM_1004_EXPECTED_XML_TAG(Object arg0, Object arg1) {
        return LOCALIZER.localize(ManagementMessages.localizableWSM_1004_EXPECTED_XML_TAG(arg0, arg1));
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(ManagementMessages.BUNDLE_NAME, locale);
        }
    }
}

