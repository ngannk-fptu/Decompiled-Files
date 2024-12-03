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

public final class ClientMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.client";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.client", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableINVALID_WSDL_URL(Object arg0) {
        return MESSAGE_FACTORY.getMessage("invalid.wsdl.url", new Object[]{arg0});
    }

    public static String INVALID_WSDL_URL(Object arg0) {
        return LOCALIZER.localize(ClientMessages.localizableINVALID_WSDL_URL(arg0));
    }

    public static Localizable localizableINVALID_EPR_PORT_NAME(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("invalid.epr.port.name", new Object[]{arg0, arg1});
    }

    public static String INVALID_EPR_PORT_NAME(Object arg0, Object arg1) {
        return LOCALIZER.localize(ClientMessages.localizableINVALID_EPR_PORT_NAME(arg0, arg1));
    }

    public static Localizable localizableINVALID_SOAP_ROLE_NONE() {
        return MESSAGE_FACTORY.getMessage("invalid.soap.role.none", new Object[0]);
    }

    public static String INVALID_SOAP_ROLE_NONE() {
        return LOCALIZER.localize(ClientMessages.localizableINVALID_SOAP_ROLE_NONE());
    }

    public static Localizable localizableEPR_WITHOUT_ADDRESSING_ON() {
        return MESSAGE_FACTORY.getMessage("epr.without.addressing.on", new Object[0]);
    }

    public static String EPR_WITHOUT_ADDRESSING_ON() {
        return LOCALIZER.localize(ClientMessages.localizableEPR_WITHOUT_ADDRESSING_ON());
    }

    public static Localizable localizableFAILED_TO_PARSE_WITH_MEX(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("failed.to.parseWithMEX", new Object[]{arg0, arg1, arg2});
    }

    public static String FAILED_TO_PARSE_WITH_MEX(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(ClientMessages.localizableFAILED_TO_PARSE_WITH_MEX(arg0, arg1, arg2));
    }

    public static Localizable localizableHTTP_STATUS_CODE(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("http.status.code", new Object[]{arg0, arg1});
    }

    public static String HTTP_STATUS_CODE(Object arg0, Object arg1) {
        return LOCALIZER.localize(ClientMessages.localizableHTTP_STATUS_CODE(arg0, arg1));
    }

    public static Localizable localizableINVALID_SERVICE_NAME(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("invalid.service.name", new Object[]{arg0, arg1});
    }

    public static String INVALID_SERVICE_NAME(Object arg0, Object arg1) {
        return LOCALIZER.localize(ClientMessages.localizableINVALID_SERVICE_NAME(arg0, arg1));
    }

    public static Localizable localizableRUNTIME_WSDLPARSER_INVALID_WSDL(Object arg0, Object arg1, Object arg2, Object arg3) {
        return MESSAGE_FACTORY.getMessage("runtime.wsdlparser.invalidWSDL", new Object[]{arg0, arg1, arg2, arg3});
    }

    public static String RUNTIME_WSDLPARSER_INVALID_WSDL(Object arg0, Object arg1, Object arg2, Object arg3) {
        return LOCALIZER.localize(ClientMessages.localizableRUNTIME_WSDLPARSER_INVALID_WSDL(arg0, arg1, arg2, arg3));
    }

    public static Localizable localizableNON_LOGICAL_HANDLER_SET(Object arg0) {
        return MESSAGE_FACTORY.getMessage("non.logical.handler.set", new Object[]{arg0});
    }

    public static String NON_LOGICAL_HANDLER_SET(Object arg0) {
        return LOCALIZER.localize(ClientMessages.localizableNON_LOGICAL_HANDLER_SET(arg0));
    }

    public static Localizable localizableINVALID_PORT_NAME(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("invalid.port.name", new Object[]{arg0, arg1});
    }

    public static String INVALID_PORT_NAME(Object arg0, Object arg1) {
        return LOCALIZER.localize(ClientMessages.localizableINVALID_PORT_NAME(arg0, arg1));
    }

    public static Localizable localizableINVALID_SOAP_ACTION() {
        return MESSAGE_FACTORY.getMessage("invalid.soap.action", new Object[0]);
    }

    public static String INVALID_SOAP_ACTION() {
        return LOCALIZER.localize(ClientMessages.localizableINVALID_SOAP_ACTION());
    }

    public static Localizable localizableINVALID_ADDRESS(Object arg0) {
        return MESSAGE_FACTORY.getMessage("invalid.address", new Object[]{arg0});
    }

    public static String INVALID_ADDRESS(Object arg0) {
        return LOCALIZER.localize(ClientMessages.localizableINVALID_ADDRESS(arg0));
    }

    public static Localizable localizableFAILED_TO_PARSE(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("failed.to.parse", new Object[]{arg0, arg1});
    }

    public static String FAILED_TO_PARSE(Object arg0, Object arg1) {
        return LOCALIZER.localize(ClientMessages.localizableFAILED_TO_PARSE(arg0, arg1));
    }

    public static Localizable localizableHTTP_NOT_FOUND(Object arg0) {
        return MESSAGE_FACTORY.getMessage("http.not.found", new Object[]{arg0});
    }

    public static String HTTP_NOT_FOUND(Object arg0) {
        return LOCALIZER.localize(ClientMessages.localizableHTTP_NOT_FOUND(arg0));
    }

    public static Localizable localizableUNSUPPORTED_OPERATION(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("unsupported.operation", new Object[]{arg0, arg1, arg2});
    }

    public static String UNSUPPORTED_OPERATION(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(ClientMessages.localizableUNSUPPORTED_OPERATION(arg0, arg1, arg2));
    }

    public static Localizable localizableWSDL_NOT_FOUND(Object arg0) {
        return MESSAGE_FACTORY.getMessage("wsdl.not.found", new Object[]{arg0});
    }

    public static String WSDL_NOT_FOUND(Object arg0) {
        return LOCALIZER.localize(ClientMessages.localizableWSDL_NOT_FOUND(arg0));
    }

    public static Localizable localizableINVALID_SERVICE_NAME_NULL(Object arg0) {
        return MESSAGE_FACTORY.getMessage("invalid.service.name.null", new Object[]{arg0});
    }

    public static String INVALID_SERVICE_NAME_NULL(Object arg0) {
        return LOCALIZER.localize(ClientMessages.localizableINVALID_SERVICE_NAME_NULL(arg0));
    }

    public static Localizable localizableHTTP_CLIENT_FAILED(Object arg0) {
        return MESSAGE_FACTORY.getMessage("http.client.failed", new Object[]{arg0});
    }

    public static String HTTP_CLIENT_FAILED(Object arg0) {
        return LOCALIZER.localize(ClientMessages.localizableHTTP_CLIENT_FAILED(arg0));
    }

    public static Localizable localizableFAILED_TO_PARSE_EPR(Object arg0) {
        return MESSAGE_FACTORY.getMessage("failed.to.parse.epr", new Object[]{arg0});
    }

    public static String FAILED_TO_PARSE_EPR(Object arg0) {
        return LOCALIZER.localize(ClientMessages.localizableFAILED_TO_PARSE_EPR(arg0));
    }

    public static Localizable localizableLOCAL_CLIENT_FAILED(Object arg0) {
        return MESSAGE_FACTORY.getMessage("local.client.failed", new Object[]{arg0});
    }

    public static String LOCAL_CLIENT_FAILED(Object arg0) {
        return LOCALIZER.localize(ClientMessages.localizableLOCAL_CLIENT_FAILED(arg0));
    }

    public static Localizable localizableUNDEFINED_BINDING(Object arg0) {
        return MESSAGE_FACTORY.getMessage("undefined.binding", new Object[]{arg0});
    }

    public static String UNDEFINED_BINDING(Object arg0) {
        return LOCALIZER.localize(ClientMessages.localizableUNDEFINED_BINDING(arg0));
    }

    public static Localizable localizableINVALID_SERVICE_NO_WSDL(Object arg0) {
        return MESSAGE_FACTORY.getMessage("invalid.service.no.wsdl", new Object[]{arg0});
    }

    public static String INVALID_SERVICE_NO_WSDL(Object arg0) {
        return LOCALIZER.localize(ClientMessages.localizableINVALID_SERVICE_NO_WSDL(arg0));
    }

    public static Localizable localizableWSDL_CONTAINS_NO_SERVICE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("wsdl.contains.no.service", new Object[]{arg0});
    }

    public static String WSDL_CONTAINS_NO_SERVICE(Object arg0) {
        return LOCALIZER.localize(ClientMessages.localizableWSDL_CONTAINS_NO_SERVICE(arg0));
    }

    public static Localizable localizableINVALID_BINDING_ID(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("invalid.binding.id", new Object[]{arg0, arg1});
    }

    public static String INVALID_BINDING_ID(Object arg0, Object arg1) {
        return LOCALIZER.localize(ClientMessages.localizableINVALID_BINDING_ID(arg0, arg1));
    }

    public static Localizable localizableUNDEFINED_PORT_TYPE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("undefined.portType", new Object[]{arg0});
    }

    public static String UNDEFINED_PORT_TYPE(Object arg0) {
        return LOCALIZER.localize(ClientMessages.localizableUNDEFINED_PORT_TYPE(arg0));
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(ClientMessages.BUNDLE_NAME, locale);
        }
    }
}

