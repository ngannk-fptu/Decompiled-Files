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

public final class ProviderApiMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.providerApi";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.providerApi", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableNULL_SERVICE() {
        return MESSAGE_FACTORY.getMessage("null.service", new Object[0]);
    }

    public static String NULL_SERVICE() {
        return LOCALIZER.localize(ProviderApiMessages.localizableNULL_SERVICE());
    }

    public static Localizable localizableNULL_ADDRESS_SERVICE_ENDPOINT() {
        return MESSAGE_FACTORY.getMessage("null.address.service.endpoint", new Object[0]);
    }

    public static String NULL_ADDRESS_SERVICE_ENDPOINT() {
        return LOCALIZER.localize(ProviderApiMessages.localizableNULL_ADDRESS_SERVICE_ENDPOINT());
    }

    public static Localizable localizableNULL_PORTNAME() {
        return MESSAGE_FACTORY.getMessage("null.portname", new Object[0]);
    }

    public static String NULL_PORTNAME() {
        return LOCALIZER.localize(ProviderApiMessages.localizableNULL_PORTNAME());
    }

    public static Localizable localizableNULL_WSDL() {
        return MESSAGE_FACTORY.getMessage("null.wsdl", new Object[0]);
    }

    public static String NULL_WSDL() {
        return LOCALIZER.localize(ProviderApiMessages.localizableNULL_WSDL());
    }

    public static Localizable localizableNO_WSDL_NO_PORT(Object arg0) {
        return MESSAGE_FACTORY.getMessage("no.wsdl.no.port", new Object[]{arg0});
    }

    public static String NO_WSDL_NO_PORT(Object arg0) {
        return LOCALIZER.localize(ProviderApiMessages.localizableNO_WSDL_NO_PORT(arg0));
    }

    public static Localizable localizableNOTFOUND_PORT_IN_WSDL(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("notfound.port.in.wsdl", new Object[]{arg0, arg1, arg2});
    }

    public static String NOTFOUND_PORT_IN_WSDL(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(ProviderApiMessages.localizableNOTFOUND_PORT_IN_WSDL(arg0, arg1, arg2));
    }

    public static Localizable localizableNOTFOUND_SERVICE_IN_WSDL(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("notfound.service.in.wsdl", new Object[]{arg0, arg1});
    }

    public static String NOTFOUND_SERVICE_IN_WSDL(Object arg0, Object arg1) {
        return LOCALIZER.localize(ProviderApiMessages.localizableNOTFOUND_SERVICE_IN_WSDL(arg0, arg1));
    }

    public static Localizable localizableNULL_EPR() {
        return MESSAGE_FACTORY.getMessage("null.epr", new Object[0]);
    }

    public static String NULL_EPR() {
        return LOCALIZER.localize(ProviderApiMessages.localizableNULL_EPR());
    }

    public static Localizable localizableNULL_ADDRESS() {
        return MESSAGE_FACTORY.getMessage("null.address", new Object[0]);
    }

    public static String NULL_ADDRESS() {
        return LOCALIZER.localize(ProviderApiMessages.localizableNULL_ADDRESS());
    }

    public static Localizable localizableERROR_WSDL(Object arg0) {
        return MESSAGE_FACTORY.getMessage("error.wsdl", new Object[]{arg0});
    }

    public static String ERROR_WSDL(Object arg0) {
        return LOCALIZER.localize(ProviderApiMessages.localizableERROR_WSDL(arg0));
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(ProviderApiMessages.BUNDLE_NAME, locale);
        }
    }
}

