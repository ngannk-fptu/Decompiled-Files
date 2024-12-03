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

public final class UtilMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.util";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.util", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableUTIL_HANDLER_CANNOT_COMBINE_SOAPMESSAGEHANDLERS() {
        return MESSAGE_FACTORY.getMessage("util.handler.cannot.combine.soapmessagehandlers", new Object[0]);
    }

    public static String UTIL_HANDLER_CANNOT_COMBINE_SOAPMESSAGEHANDLERS() {
        return LOCALIZER.localize(UtilMessages.localizableUTIL_HANDLER_CANNOT_COMBINE_SOAPMESSAGEHANDLERS());
    }

    public static Localizable localizableUTIL_LOCATION(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("util.location", new Object[]{arg0, arg1});
    }

    public static String UTIL_LOCATION(Object arg0, Object arg1) {
        return LOCALIZER.localize(UtilMessages.localizableUTIL_LOCATION(arg0, arg1));
    }

    public static Localizable localizableUTIL_HANDLER_CLASS_NOT_FOUND(Object arg0) {
        return MESSAGE_FACTORY.getMessage("util.handler.class.not.found", new Object[]{arg0});
    }

    public static String UTIL_HANDLER_CLASS_NOT_FOUND(Object arg0) {
        return LOCALIZER.localize(UtilMessages.localizableUTIL_HANDLER_CLASS_NOT_FOUND(arg0));
    }

    public static Localizable localizableUTIL_HANDLER_NO_WEBSERVICE_ANNOTATION(Object arg0) {
        return MESSAGE_FACTORY.getMessage("util.handler.no.webservice.annotation", new Object[]{arg0});
    }

    public static String UTIL_HANDLER_NO_WEBSERVICE_ANNOTATION(Object arg0) {
        return LOCALIZER.localize(UtilMessages.localizableUTIL_HANDLER_NO_WEBSERVICE_ANNOTATION(arg0));
    }

    public static Localizable localizableUTIL_HANDLER_ENDPOINT_INTERFACE_NO_WEBSERVICE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("util.handler.endpoint.interface.no.webservice", new Object[]{arg0});
    }

    public static String UTIL_HANDLER_ENDPOINT_INTERFACE_NO_WEBSERVICE(Object arg0) {
        return LOCALIZER.localize(UtilMessages.localizableUTIL_HANDLER_ENDPOINT_INTERFACE_NO_WEBSERVICE(arg0));
    }

    public static Localizable localizableUTIL_PARSER_WRONG_ELEMENT(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("util.parser.wrong.element", new Object[]{arg0, arg1, arg2});
    }

    public static String UTIL_PARSER_WRONG_ELEMENT(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(UtilMessages.localizableUTIL_PARSER_WRONG_ELEMENT(arg0, arg1, arg2));
    }

    public static Localizable localizableUTIL_FAILED_TO_PARSE_HANDLERCHAIN_FILE(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("util.failed.to.parse.handlerchain.file", new Object[]{arg0, arg1});
    }

    public static String UTIL_FAILED_TO_PARSE_HANDLERCHAIN_FILE(Object arg0, Object arg1) {
        return LOCALIZER.localize(UtilMessages.localizableUTIL_FAILED_TO_PARSE_HANDLERCHAIN_FILE(arg0, arg1));
    }

    public static Localizable localizableUTIL_FAILED_TO_FIND_HANDLERCHAIN_FILE(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("util.failed.to.find.handlerchain.file", new Object[]{arg0, arg1});
    }

    public static String UTIL_FAILED_TO_FIND_HANDLERCHAIN_FILE(Object arg0, Object arg1) {
        return LOCALIZER.localize(UtilMessages.localizableUTIL_FAILED_TO_FIND_HANDLERCHAIN_FILE(arg0, arg1));
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(UtilMessages.BUNDLE_NAME, locale);
        }
    }
}

