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

public final class SenderMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.sender";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.sender", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableSENDER_REQUEST_MESSAGE_NOT_READY() {
        return MESSAGE_FACTORY.getMessage("sender.request.messageNotReady", new Object[0]);
    }

    public static String SENDER_REQUEST_MESSAGE_NOT_READY() {
        return LOCALIZER.localize(SenderMessages.localizableSENDER_REQUEST_MESSAGE_NOT_READY());
    }

    public static Localizable localizableSENDER_RESPONSE_CANNOT_DECODE_FAULT_DETAIL() {
        return MESSAGE_FACTORY.getMessage("sender.response.cannotDecodeFaultDetail", new Object[0]);
    }

    public static String SENDER_RESPONSE_CANNOT_DECODE_FAULT_DETAIL() {
        return LOCALIZER.localize(SenderMessages.localizableSENDER_RESPONSE_CANNOT_DECODE_FAULT_DETAIL());
    }

    public static Localizable localizableSENDER_REQUEST_ILLEGAL_VALUE_FOR_CONTENT_NEGOTIATION(Object arg0) {
        return MESSAGE_FACTORY.getMessage("sender.request.illegalValueForContentNegotiation", new Object[]{arg0});
    }

    public static String SENDER_REQUEST_ILLEGAL_VALUE_FOR_CONTENT_NEGOTIATION(Object arg0) {
        return LOCALIZER.localize(SenderMessages.localizableSENDER_REQUEST_ILLEGAL_VALUE_FOR_CONTENT_NEGOTIATION(arg0));
    }

    public static Localizable localizableSENDER_NESTED_ERROR(Object arg0) {
        return MESSAGE_FACTORY.getMessage("sender.nestedError", new Object[]{arg0});
    }

    public static String SENDER_NESTED_ERROR(Object arg0) {
        return LOCALIZER.localize(SenderMessages.localizableSENDER_NESTED_ERROR(arg0));
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(SenderMessages.BUNDLE_NAME, locale);
        }
    }
}

