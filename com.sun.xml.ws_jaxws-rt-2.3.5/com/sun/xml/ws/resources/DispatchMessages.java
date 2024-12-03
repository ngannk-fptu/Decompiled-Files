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

public final class DispatchMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.dispatch";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.dispatch", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableINVALID_NULLARG_SOAP_MSGMODE(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("invalid.nullarg.soap.msgmode", new Object[]{arg0, arg1});
    }

    public static String INVALID_NULLARG_SOAP_MSGMODE(Object arg0, Object arg1) {
        return LOCALIZER.localize(DispatchMessages.localizableINVALID_NULLARG_SOAP_MSGMODE(arg0, arg1));
    }

    public static Localizable localizableINVALID_QUERY_STRING(Object arg0) {
        return MESSAGE_FACTORY.getMessage("invalid.query.string", new Object[]{arg0});
    }

    public static String INVALID_QUERY_STRING(Object arg0) {
        return LOCALIZER.localize(DispatchMessages.localizableINVALID_QUERY_STRING(arg0));
    }

    public static Localizable localizableINVALID_URI_DECODE() {
        return MESSAGE_FACTORY.getMessage("invalid.uri.decode", new Object[0]);
    }

    public static String INVALID_URI_DECODE() {
        return LOCALIZER.localize(DispatchMessages.localizableINVALID_URI_DECODE());
    }

    public static Localizable localizableINVALID_URI_RESOLUTION(Object arg0) {
        return MESSAGE_FACTORY.getMessage("invalid.uri.resolution", new Object[]{arg0});
    }

    public static String INVALID_URI_RESOLUTION(Object arg0) {
        return LOCALIZER.localize(DispatchMessages.localizableINVALID_URI_RESOLUTION(arg0));
    }

    public static Localizable localizableINVALID_NULLARG_URI() {
        return MESSAGE_FACTORY.getMessage("invalid.nullarg.uri", new Object[0]);
    }

    public static String INVALID_NULLARG_URI() {
        return LOCALIZER.localize(DispatchMessages.localizableINVALID_NULLARG_URI());
    }

    public static Localizable localizableINVALID_URI_PATH_QUERY(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("invalid.uri.path.query", new Object[]{arg0, arg1});
    }

    public static String INVALID_URI_PATH_QUERY(Object arg0, Object arg1) {
        return LOCALIZER.localize(DispatchMessages.localizableINVALID_URI_PATH_QUERY(arg0, arg1));
    }

    public static Localizable localizableINVALID_URI(Object arg0) {
        return MESSAGE_FACTORY.getMessage("invalid.uri", new Object[]{arg0});
    }

    public static String INVALID_URI(Object arg0) {
        return LOCALIZER.localize(DispatchMessages.localizableINVALID_URI(arg0));
    }

    public static Localizable localizableINVALID_DATASOURCE_DISPATCH_MSGMODE(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("invalid.datasource.dispatch.msgmode", new Object[]{arg0, arg1});
    }

    public static String INVALID_DATASOURCE_DISPATCH_MSGMODE(Object arg0, Object arg1) {
        return LOCALIZER.localize(DispatchMessages.localizableINVALID_DATASOURCE_DISPATCH_MSGMODE(arg0, arg1));
    }

    public static Localizable localizableDUPLICATE_PORT(Object arg0) {
        return MESSAGE_FACTORY.getMessage("duplicate.port", new Object[]{arg0});
    }

    public static String DUPLICATE_PORT(Object arg0) {
        return LOCALIZER.localize(DispatchMessages.localizableDUPLICATE_PORT(arg0));
    }

    public static Localizable localizableINVALID_SOAPMESSAGE_DISPATCH_BINDING(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("invalid.soapmessage.dispatch.binding", new Object[]{arg0, arg1});
    }

    public static String INVALID_SOAPMESSAGE_DISPATCH_BINDING(Object arg0, Object arg1) {
        return LOCALIZER.localize(DispatchMessages.localizableINVALID_SOAPMESSAGE_DISPATCH_BINDING(arg0, arg1));
    }

    public static Localizable localizableINVALID_QUERY_LEADING_CHAR(Object arg0) {
        return MESSAGE_FACTORY.getMessage("invalid.query.leading.char", new Object[]{arg0});
    }

    public static String INVALID_QUERY_LEADING_CHAR(Object arg0) {
        return LOCALIZER.localize(DispatchMessages.localizableINVALID_QUERY_LEADING_CHAR(arg0));
    }

    public static Localizable localizableINVALID_RESPONSE_DESERIALIZATION() {
        return MESSAGE_FACTORY.getMessage("invalid.response.deserialization", new Object[0]);
    }

    public static String INVALID_RESPONSE_DESERIALIZATION() {
        return LOCALIZER.localize(DispatchMessages.localizableINVALID_RESPONSE_DESERIALIZATION());
    }

    public static Localizable localizableINVALID_RESPONSE() {
        return MESSAGE_FACTORY.getMessage("invalid.response", new Object[0]);
    }

    public static String INVALID_RESPONSE() {
        return LOCALIZER.localize(DispatchMessages.localizableINVALID_RESPONSE());
    }

    public static Localizable localizableINVALID_SOAPMESSAGE_DISPATCH_MSGMODE(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("invalid.soapmessage.dispatch.msgmode", new Object[]{arg0, arg1});
    }

    public static String INVALID_SOAPMESSAGE_DISPATCH_MSGMODE(Object arg0, Object arg1) {
        return LOCALIZER.localize(DispatchMessages.localizableINVALID_SOAPMESSAGE_DISPATCH_MSGMODE(arg0, arg1));
    }

    public static Localizable localizableINVALID_DATASOURCE_DISPATCH_BINDING(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("invalid.datasource.dispatch.binding", new Object[]{arg0, arg1});
    }

    public static String INVALID_DATASOURCE_DISPATCH_BINDING(Object arg0, Object arg1) {
        return LOCALIZER.localize(DispatchMessages.localizableINVALID_DATASOURCE_DISPATCH_BINDING(arg0, arg1));
    }

    public static Localizable localizableINVALID_NULLARG_XMLHTTP_REQUEST_METHOD(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("invalid.nullarg.xmlhttp.request.method", new Object[]{arg0, arg1});
    }

    public static String INVALID_NULLARG_XMLHTTP_REQUEST_METHOD(Object arg0, Object arg1) {
        return LOCALIZER.localize(DispatchMessages.localizableINVALID_NULLARG_XMLHTTP_REQUEST_METHOD(arg0, arg1));
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(DispatchMessages.BUNDLE_NAME, locale);
        }
    }
}

