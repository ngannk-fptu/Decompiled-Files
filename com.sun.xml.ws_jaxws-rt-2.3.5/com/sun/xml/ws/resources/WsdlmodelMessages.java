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

public final class WsdlmodelMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.wsdlmodel";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.wsdlmodel", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableWSDL_PORTADDRESS_EPRADDRESS_NOT_MATCH(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("wsdl.portaddress.epraddress.not.match", new Object[]{arg0, arg1, arg2});
    }

    public static String WSDL_PORTADDRESS_EPRADDRESS_NOT_MATCH(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(WsdlmodelMessages.localizableWSDL_PORTADDRESS_EPRADDRESS_NOT_MATCH(arg0, arg1, arg2));
    }

    public static Localizable localizableWSDL_IMPORT_SHOULD_BE_WSDL(Object arg0) {
        return MESSAGE_FACTORY.getMessage("wsdl.import.should.be.wsdl", new Object[]{arg0});
    }

    public static String WSDL_IMPORT_SHOULD_BE_WSDL(Object arg0) {
        return LOCALIZER.localize(WsdlmodelMessages.localizableWSDL_IMPORT_SHOULD_BE_WSDL(arg0));
    }

    public static Localizable localizableMEX_METADATA_SYSTEMID_NULL() {
        return MESSAGE_FACTORY.getMessage("Mex.metadata.systemid.null", new Object[0]);
    }

    public static String MEX_METADATA_SYSTEMID_NULL() {
        return LOCALIZER.localize(WsdlmodelMessages.localizableMEX_METADATA_SYSTEMID_NULL());
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(WsdlmodelMessages.BUNDLE_NAME, locale);
        }
    }
}

