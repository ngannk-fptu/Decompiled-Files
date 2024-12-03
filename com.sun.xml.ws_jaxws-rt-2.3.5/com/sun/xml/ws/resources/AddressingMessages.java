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

public final class AddressingMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.addressing";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.addressing", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableADDRESSING_NOT_ENABLED(Object arg0) {
        return MESSAGE_FACTORY.getMessage("addressing.notEnabled", new Object[]{arg0});
    }

    public static String ADDRESSING_NOT_ENABLED(Object arg0) {
        return LOCALIZER.localize(AddressingMessages.localizableADDRESSING_NOT_ENABLED(arg0));
    }

    public static Localizable localizableWSAW_ANONYMOUS_PROHIBITED() {
        return MESSAGE_FACTORY.getMessage("wsaw.anonymousProhibited", new Object[0]);
    }

    public static String WSAW_ANONYMOUS_PROHIBITED() {
        return LOCALIZER.localize(AddressingMessages.localizableWSAW_ANONYMOUS_PROHIBITED());
    }

    public static Localizable localizableNULL_SOAP_VERSION() {
        return MESSAGE_FACTORY.getMessage("null.soap.version", new Object[0]);
    }

    public static String NULL_SOAP_VERSION() {
        return LOCALIZER.localize(AddressingMessages.localizableNULL_SOAP_VERSION());
    }

    public static Localizable localizableNULL_HEADERS() {
        return MESSAGE_FACTORY.getMessage("null.headers", new Object[0]);
    }

    public static String NULL_HEADERS() {
        return LOCALIZER.localize(AddressingMessages.localizableNULL_HEADERS());
    }

    public static Localizable localizableFAULT_TO_CANNOT_PARSE() {
        return MESSAGE_FACTORY.getMessage("faultTo.cannot.parse", new Object[0]);
    }

    public static String FAULT_TO_CANNOT_PARSE() {
        return LOCALIZER.localize(AddressingMessages.localizableFAULT_TO_CANNOT_PARSE());
    }

    public static Localizable localizableNON_ANONYMOUS_RESPONSE_NULL_HEADERS(Object arg0) {
        return MESSAGE_FACTORY.getMessage("nonAnonymous.response.nullHeaders", new Object[]{arg0});
    }

    public static String NON_ANONYMOUS_RESPONSE_NULL_HEADERS(Object arg0) {
        return LOCALIZER.localize(AddressingMessages.localizableNON_ANONYMOUS_RESPONSE_NULL_HEADERS(arg0));
    }

    public static Localizable localizableUNKNOWN_WSA_HEADER() {
        return MESSAGE_FACTORY.getMessage("unknown.wsa.header", new Object[0]);
    }

    public static String UNKNOWN_WSA_HEADER() {
        return LOCALIZER.localize(AddressingMessages.localizableUNKNOWN_WSA_HEADER());
    }

    public static Localizable localizableINVALID_ADDRESSING_HEADER_EXCEPTION(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("invalid.addressing.header.exception", new Object[]{arg0, arg1});
    }

    public static String INVALID_ADDRESSING_HEADER_EXCEPTION(Object arg0, Object arg1) {
        return LOCALIZER.localize(AddressingMessages.localizableINVALID_ADDRESSING_HEADER_EXCEPTION(arg0, arg1));
    }

    public static Localizable localizableNULL_WSDL_PORT() {
        return MESSAGE_FACTORY.getMessage("null.wsdlPort", new Object[0]);
    }

    public static String NULL_WSDL_PORT() {
        return LOCALIZER.localize(AddressingMessages.localizableNULL_WSDL_PORT());
    }

    public static Localizable localizableNON_ANONYMOUS_UNKNOWN_PROTOCOL(Object arg0) {
        return MESSAGE_FACTORY.getMessage("nonAnonymous.unknown.protocol", new Object[]{arg0});
    }

    public static String NON_ANONYMOUS_UNKNOWN_PROTOCOL(Object arg0) {
        return LOCALIZER.localize(AddressingMessages.localizableNON_ANONYMOUS_UNKNOWN_PROTOCOL(arg0));
    }

    public static Localizable localizableNON_ANONYMOUS_RESPONSE_SENDING(Object arg0) {
        return MESSAGE_FACTORY.getMessage("nonAnonymous.response.sending", new Object[]{arg0});
    }

    public static String NON_ANONYMOUS_RESPONSE_SENDING(Object arg0) {
        return LOCALIZER.localize(AddressingMessages.localizableNON_ANONYMOUS_RESPONSE_SENDING(arg0));
    }

    public static Localizable localizableNON_ANONYMOUS_RESPONSE() {
        return MESSAGE_FACTORY.getMessage("nonAnonymous.response", new Object[0]);
    }

    public static String NON_ANONYMOUS_RESPONSE() {
        return LOCALIZER.localize(AddressingMessages.localizableNON_ANONYMOUS_RESPONSE());
    }

    public static Localizable localizableREPLY_TO_CANNOT_PARSE() {
        return MESSAGE_FACTORY.getMessage("replyTo.cannot.parse", new Object[0]);
    }

    public static String REPLY_TO_CANNOT_PARSE() {
        return LOCALIZER.localize(AddressingMessages.localizableREPLY_TO_CANNOT_PARSE());
    }

    public static Localizable localizableINVALID_WSAW_ANONYMOUS(Object arg0) {
        return MESSAGE_FACTORY.getMessage("invalid.wsaw.anonymous", new Object[]{arg0});
    }

    public static String INVALID_WSAW_ANONYMOUS(Object arg0) {
        return LOCALIZER.localize(AddressingMessages.localizableINVALID_WSAW_ANONYMOUS(arg0));
    }

    public static Localizable localizableVALIDATION_CLIENT_NULL_ACTION() {
        return MESSAGE_FACTORY.getMessage("validation.client.nullAction", new Object[0]);
    }

    public static String VALIDATION_CLIENT_NULL_ACTION() {
        return LOCALIZER.localize(AddressingMessages.localizableVALIDATION_CLIENT_NULL_ACTION());
    }

    public static Localizable localizableWSDL_BOUND_OPERATION_NOT_FOUND(Object arg0) {
        return MESSAGE_FACTORY.getMessage("wsdlBoundOperation.notFound", new Object[]{arg0});
    }

    public static String WSDL_BOUND_OPERATION_NOT_FOUND(Object arg0) {
        return LOCALIZER.localize(AddressingMessages.localizableWSDL_BOUND_OPERATION_NOT_FOUND(arg0));
    }

    public static Localizable localizableMISSING_HEADER_EXCEPTION(Object arg0) {
        return MESSAGE_FACTORY.getMessage("missing.header.exception", new Object[]{arg0});
    }

    public static String MISSING_HEADER_EXCEPTION(Object arg0) {
        return LOCALIZER.localize(AddressingMessages.localizableMISSING_HEADER_EXCEPTION(arg0));
    }

    public static Localizable localizableNULL_BINDING() {
        return MESSAGE_FACTORY.getMessage("null.binding", new Object[0]);
    }

    public static String NULL_BINDING() {
        return LOCALIZER.localize(AddressingMessages.localizableNULL_BINDING());
    }

    public static Localizable localizableNULL_WSA_HEADERS() {
        return MESSAGE_FACTORY.getMessage("null.wsa.headers", new Object[0]);
    }

    public static String NULL_WSA_HEADERS() {
        return LOCALIZER.localize(AddressingMessages.localizableNULL_WSA_HEADERS());
    }

    public static Localizable localizableNON_ANONYMOUS_RESPONSE_ONEWAY() {
        return MESSAGE_FACTORY.getMessage("nonAnonymous.response.oneway", new Object[0]);
    }

    public static String NON_ANONYMOUS_RESPONSE_ONEWAY() {
        return LOCALIZER.localize(AddressingMessages.localizableNON_ANONYMOUS_RESPONSE_ONEWAY());
    }

    public static Localizable localizableVALIDATION_SERVER_NULL_ACTION() {
        return MESSAGE_FACTORY.getMessage("validation.server.nullAction", new Object[0]);
    }

    public static String VALIDATION_SERVER_NULL_ACTION() {
        return LOCALIZER.localize(AddressingMessages.localizableVALIDATION_SERVER_NULL_ACTION());
    }

    public static Localizable localizableWRONG_ADDRESSING_VERSION(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("wrong.addressing.version", new Object[]{arg0, arg1});
    }

    public static String WRONG_ADDRESSING_VERSION(Object arg0, Object arg1) {
        return LOCALIZER.localize(AddressingMessages.localizableWRONG_ADDRESSING_VERSION(arg0, arg1));
    }

    public static Localizable localizableACTION_NOT_SUPPORTED_EXCEPTION(Object arg0) {
        return MESSAGE_FACTORY.getMessage("action.not.supported.exception", new Object[]{arg0});
    }

    public static String ACTION_NOT_SUPPORTED_EXCEPTION(Object arg0) {
        return LOCALIZER.localize(AddressingMessages.localizableACTION_NOT_SUPPORTED_EXCEPTION(arg0));
    }

    public static Localizable localizableNULL_MESSAGE() {
        return MESSAGE_FACTORY.getMessage("null.message", new Object[0]);
    }

    public static String NULL_MESSAGE() {
        return LOCALIZER.localize(AddressingMessages.localizableNULL_MESSAGE());
    }

    public static Localizable localizableADDRESSING_SHOULD_BE_ENABLED() {
        return MESSAGE_FACTORY.getMessage("addressing.should.be.enabled.", new Object[0]);
    }

    public static String ADDRESSING_SHOULD_BE_ENABLED() {
        return LOCALIZER.localize(AddressingMessages.localizableADDRESSING_SHOULD_BE_ENABLED());
    }

    public static Localizable localizableNULL_PACKET() {
        return MESSAGE_FACTORY.getMessage("null.packet", new Object[0]);
    }

    public static String NULL_PACKET() {
        return LOCALIZER.localize(AddressingMessages.localizableNULL_PACKET());
    }

    public static Localizable localizableNULL_ADDRESSING_VERSION() {
        return MESSAGE_FACTORY.getMessage("null.addressing.version", new Object[0]);
    }

    public static String NULL_ADDRESSING_VERSION() {
        return LOCALIZER.localize(AddressingMessages.localizableNULL_ADDRESSING_VERSION());
    }

    public static Localizable localizableNULL_ACTION() {
        return MESSAGE_FACTORY.getMessage("null.action", new Object[0]);
    }

    public static String NULL_ACTION() {
        return LOCALIZER.localize(AddressingMessages.localizableNULL_ACTION());
    }

    public static Localizable localizableNON_UNIQUE_OPERATION_SIGNATURE(Object arg0, Object arg1, Object arg2, Object arg3) {
        return MESSAGE_FACTORY.getMessage("non.unique.operation.signature", new Object[]{arg0, arg1, arg2, arg3});
    }

    public static String NON_UNIQUE_OPERATION_SIGNATURE(Object arg0, Object arg1, Object arg2, Object arg3) {
        return LOCALIZER.localize(AddressingMessages.localizableNON_UNIQUE_OPERATION_SIGNATURE(arg0, arg1, arg2, arg3));
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(AddressingMessages.BUNDLE_NAME, locale);
        }
    }
}

