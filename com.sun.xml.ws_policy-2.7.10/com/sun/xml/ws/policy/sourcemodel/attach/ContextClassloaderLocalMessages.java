/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.localization.Localizable
 *  com.sun.istack.localization.LocalizableMessageFactory
 *  com.sun.istack.localization.LocalizableMessageFactory$ResourceBundleSupplier
 *  com.sun.istack.localization.Localizer
 */
package com.sun.xml.ws.policy.sourcemodel.attach;

import com.sun.istack.localization.Localizable;
import com.sun.istack.localization.LocalizableMessageFactory;
import com.sun.istack.localization.Localizer;
import java.util.Locale;
import java.util.ResourceBundle;

public final class ContextClassloaderLocalMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.policy.sourcemodel.attach.ContextClassloaderLocal";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.policy.sourcemodel.attach.ContextClassloaderLocal", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableFAILED_TO_CREATE_NEW_INSTANCE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("FAILED_TO_CREATE_NEW_INSTANCE", new Object[]{arg0});
    }

    public static String FAILED_TO_CREATE_NEW_INSTANCE(Object arg0) {
        return LOCALIZER.localize(ContextClassloaderLocalMessages.localizableFAILED_TO_CREATE_NEW_INSTANCE(arg0));
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(ContextClassloaderLocalMessages.BUNDLE_NAME, locale);
        }
    }
}

