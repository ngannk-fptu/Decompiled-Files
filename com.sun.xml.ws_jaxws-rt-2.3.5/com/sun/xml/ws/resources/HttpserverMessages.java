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

public final class HttpserverMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.httpserver";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.httpserver", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableUNEXPECTED_HTTP_METHOD(Object arg0) {
        return MESSAGE_FACTORY.getMessage("unexpected.http.method", new Object[]{arg0});
    }

    public static String UNEXPECTED_HTTP_METHOD(Object arg0) {
        return LOCALIZER.localize(HttpserverMessages.localizableUNEXPECTED_HTTP_METHOD(arg0));
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(HttpserverMessages.BUNDLE_NAME, locale);
        }
    }
}

