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

public final class HandlerMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.handler";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.handler", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableHANDLER_NESTED_ERROR(Object arg0) {
        return MESSAGE_FACTORY.getMessage("handler.nestedError", new Object[]{arg0});
    }

    public static String HANDLER_NESTED_ERROR(Object arg0) {
        return LOCALIZER.localize(HandlerMessages.localizableHANDLER_NESTED_ERROR(arg0));
    }

    public static Localizable localizableCANNOT_EXTEND_HANDLER_DIRECTLY(Object arg0) {
        return MESSAGE_FACTORY.getMessage("cannot.extend.handler.directly", new Object[]{arg0});
    }

    public static String CANNOT_EXTEND_HANDLER_DIRECTLY(Object arg0) {
        return LOCALIZER.localize(HandlerMessages.localizableCANNOT_EXTEND_HANDLER_DIRECTLY(arg0));
    }

    public static Localizable localizableHANDLER_NOT_VALID_TYPE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("handler.not.valid.type", new Object[]{arg0});
    }

    public static String HANDLER_NOT_VALID_TYPE(Object arg0) {
        return LOCALIZER.localize(HandlerMessages.localizableHANDLER_NOT_VALID_TYPE(arg0));
    }

    public static Localizable localizableHANDLER_MESSAGE_CONTEXT_INVALID_CLASS(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("handler.messageContext.invalid.class", new Object[]{arg0, arg1});
    }

    public static String HANDLER_MESSAGE_CONTEXT_INVALID_CLASS(Object arg0, Object arg1) {
        return LOCALIZER.localize(HandlerMessages.localizableHANDLER_MESSAGE_CONTEXT_INVALID_CLASS(arg0, arg1));
    }

    public static Localizable localizableHANDLER_PREDESTROY_IGNORE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("handler.predestroy.ignore", new Object[]{arg0});
    }

    public static String HANDLER_PREDESTROY_IGNORE(Object arg0) {
        return LOCALIZER.localize(HandlerMessages.localizableHANDLER_PREDESTROY_IGNORE(arg0));
    }

    public static Localizable localizableHANDLER_CHAIN_CONTAINS_HANDLER_ONLY(Object arg0) {
        return MESSAGE_FACTORY.getMessage("handler.chain.contains.handler.only", new Object[]{arg0});
    }

    public static String HANDLER_CHAIN_CONTAINS_HANDLER_ONLY(Object arg0) {
        return LOCALIZER.localize(HandlerMessages.localizableHANDLER_CHAIN_CONTAINS_HANDLER_ONLY(arg0));
    }

    public static Localizable localizableCANNOT_INSTANTIATE_HANDLER(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("cannot.instantiate.handler", new Object[]{arg0, arg1});
    }

    public static String CANNOT_INSTANTIATE_HANDLER(Object arg0, Object arg1) {
        return LOCALIZER.localize(HandlerMessages.localizableCANNOT_INSTANTIATE_HANDLER(arg0, arg1));
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(HandlerMessages.BUNDLE_NAME, locale);
        }
    }
}

