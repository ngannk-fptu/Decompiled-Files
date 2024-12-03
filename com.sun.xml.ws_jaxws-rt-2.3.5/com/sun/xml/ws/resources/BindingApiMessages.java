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

public final class BindingApiMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.bindingApi";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.bindingApi", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableBINDING_API_NO_FAULT_MESSAGE_NAME() {
        return MESSAGE_FACTORY.getMessage("binding.api.no.fault.message.name", new Object[0]);
    }

    public static String BINDING_API_NO_FAULT_MESSAGE_NAME() {
        return LOCALIZER.localize(BindingApiMessages.localizableBINDING_API_NO_FAULT_MESSAGE_NAME());
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(BindingApiMessages.BUNDLE_NAME, locale);
        }
    }
}

