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

public final class SoapMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.soap";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.soap", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableSOAP_FAULT_CREATE_ERR(Object arg0) {
        return MESSAGE_FACTORY.getMessage("soap.fault.create.err", new Object[]{arg0});
    }

    public static String SOAP_FAULT_CREATE_ERR(Object arg0) {
        return LOCALIZER.localize(SoapMessages.localizableSOAP_FAULT_CREATE_ERR(arg0));
    }

    public static Localizable localizableSOAP_PROTOCOL_INVALID_FAULT_CODE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("soap.protocol.invalidFaultCode", new Object[]{arg0});
    }

    public static String SOAP_PROTOCOL_INVALID_FAULT_CODE(Object arg0) {
        return LOCALIZER.localize(SoapMessages.localizableSOAP_PROTOCOL_INVALID_FAULT_CODE(arg0));
    }

    public static Localizable localizableSOAP_VERSION_MISMATCH_ERR(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("soap.version.mismatch.err", new Object[]{arg0, arg1});
    }

    public static String SOAP_VERSION_MISMATCH_ERR(Object arg0, Object arg1) {
        return LOCALIZER.localize(SoapMessages.localizableSOAP_VERSION_MISMATCH_ERR(arg0, arg1));
    }

    public static Localizable localizableSOAP_MSG_FACTORY_CREATE_ERR(Object arg0) {
        return MESSAGE_FACTORY.getMessage("soap.msg.factory.create.err", new Object[]{arg0});
    }

    public static String SOAP_MSG_FACTORY_CREATE_ERR(Object arg0) {
        return LOCALIZER.localize(SoapMessages.localizableSOAP_MSG_FACTORY_CREATE_ERR(arg0));
    }

    public static Localizable localizableSOAP_MSG_CREATE_ERR(Object arg0) {
        return MESSAGE_FACTORY.getMessage("soap.msg.create.err", new Object[]{arg0});
    }

    public static String SOAP_MSG_CREATE_ERR(Object arg0) {
        return LOCALIZER.localize(SoapMessages.localizableSOAP_MSG_CREATE_ERR(arg0));
    }

    public static Localizable localizableSOAP_FACTORY_CREATE_ERR(Object arg0) {
        return MESSAGE_FACTORY.getMessage("soap.factory.create.err", new Object[]{arg0});
    }

    public static String SOAP_FACTORY_CREATE_ERR(Object arg0) {
        return LOCALIZER.localize(SoapMessages.localizableSOAP_FACTORY_CREATE_ERR(arg0));
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(SoapMessages.BUNDLE_NAME, locale);
        }
    }
}

