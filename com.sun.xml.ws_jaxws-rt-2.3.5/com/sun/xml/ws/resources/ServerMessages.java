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

public final class ServerMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.server";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.server", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableDUPLICATE_ABSTRACT_WSDL(Object arg0) {
        return MESSAGE_FACTORY.getMessage("duplicate.abstract.wsdl", new Object[]{arg0});
    }

    public static String DUPLICATE_ABSTRACT_WSDL(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableDUPLICATE_ABSTRACT_WSDL(arg0));
    }

    public static Localizable localizableNOT_KNOW_HTTP_CONTEXT_TYPE(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("not.know.HttpContext.type", new Object[]{arg0, arg1, arg2});
    }

    public static String NOT_KNOW_HTTP_CONTEXT_TYPE(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(ServerMessages.localizableNOT_KNOW_HTTP_CONTEXT_TYPE(arg0, arg1, arg2));
    }

    public static Localizable localizableUNSUPPORTED_CONTENT_TYPE(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("unsupported.contentType", new Object[]{arg0, arg1});
    }

    public static String UNSUPPORTED_CONTENT_TYPE(Object arg0, Object arg1) {
        return LOCALIZER.localize(ServerMessages.localizableUNSUPPORTED_CONTENT_TYPE(arg0, arg1));
    }

    public static Localizable localizableRUNTIME_PARSER_WSDL_NOT_FOUND(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.parser.wsdl.not.found", new Object[]{arg0});
    }

    public static String RUNTIME_PARSER_WSDL_NOT_FOUND(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_PARSER_WSDL_NOT_FOUND(arg0));
    }

    public static Localizable localizableSOAPENCODER_ERR() {
        return MESSAGE_FACTORY.getMessage("soapencoder.err", new Object[0]);
    }

    public static String SOAPENCODER_ERR() {
        return LOCALIZER.localize(ServerMessages.localizableSOAPENCODER_ERR());
    }

    public static Localizable localizableWSDL_REQUIRED() {
        return MESSAGE_FACTORY.getMessage("wsdl.required", new Object[0]);
    }

    public static String WSDL_REQUIRED() {
        return LOCALIZER.localize(ServerMessages.localizableWSDL_REQUIRED());
    }

    public static Localizable localizableRUNTIME_PARSER_WSDL_NOSERVICE_IN_WSDLMODEL(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.parser.wsdl.noservice.in.wsdlmodel", new Object[]{arg0});
    }

    public static String RUNTIME_PARSER_WSDL_NOSERVICE_IN_WSDLMODEL(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_PARSER_WSDL_NOSERVICE_IN_WSDLMODEL(arg0));
    }

    public static Localizable localizableNULL_IMPLEMENTOR() {
        return MESSAGE_FACTORY.getMessage("null.implementor", new Object[0]);
    }

    public static String NULL_IMPLEMENTOR() {
        return LOCALIZER.localize(ServerMessages.localizableNULL_IMPLEMENTOR());
    }

    public static Localizable localizableSERVER_RT_ERR(Object arg0) {
        return MESSAGE_FACTORY.getMessage("server.rt.err", new Object[]{arg0});
    }

    public static String SERVER_RT_ERR(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableSERVER_RT_ERR(arg0));
    }

    public static Localizable localizableWRONG_PARAMETER_TYPE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("wrong.parameter.type", new Object[]{arg0});
    }

    public static String WRONG_PARAMETER_TYPE(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableWRONG_PARAMETER_TYPE(arg0));
    }

    public static Localizable localizableRUNTIME_PARSER_WSDL_INCORRECTSERVICEPORT(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("runtime.parser.wsdl.incorrectserviceport", new Object[]{arg0, arg1, arg2});
    }

    public static String RUNTIME_PARSER_WSDL_INCORRECTSERVICEPORT(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_PARSER_WSDL_INCORRECTSERVICEPORT(arg0, arg1, arg2));
    }

    public static Localizable localizableRUNTIME_PARSER_XML_READER(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.parser.xmlReader", new Object[]{arg0});
    }

    public static String RUNTIME_PARSER_XML_READER(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_PARSER_XML_READER(arg0));
    }

    public static Localizable localizableDD_MTOM_CONFLICT(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("dd.mtom.conflict", new Object[]{arg0, arg1});
    }

    public static String DD_MTOM_CONFLICT(Object arg0, Object arg1) {
        return LOCALIZER.localize(ServerMessages.localizableDD_MTOM_CONFLICT(arg0, arg1));
    }

    public static Localizable localizableRUNTIME_PARSER_INVALID_ATTRIBUTE_VALUE(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("runtime.parser.invalid.attribute.value", new Object[]{arg0, arg1});
    }

    public static String RUNTIME_PARSER_INVALID_ATTRIBUTE_VALUE(Object arg0, Object arg1) {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_PARSER_INVALID_ATTRIBUTE_VALUE(arg0, arg1));
    }

    public static Localizable localizableRUNTIME_SAXPARSER_EXCEPTION(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("runtime.saxparser.exception", new Object[]{arg0, arg1});
    }

    public static String RUNTIME_SAXPARSER_EXCEPTION(Object arg0, Object arg1) {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_SAXPARSER_EXCEPTION(arg0, arg1));
    }

    public static Localizable localizableRUNTIME_PARSER_INVALID_VERSION_NUMBER() {
        return MESSAGE_FACTORY.getMessage("runtime.parser.invalidVersionNumber", new Object[0]);
    }

    public static String RUNTIME_PARSER_INVALID_VERSION_NUMBER() {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_PARSER_INVALID_VERSION_NUMBER());
    }

    public static Localizable localizableWRONG_TNS_FOR_PORT(Object arg0) {
        return MESSAGE_FACTORY.getMessage("wrong.tns.for.port", new Object[]{arg0});
    }

    public static String WRONG_TNS_FOR_PORT(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableWRONG_TNS_FOR_PORT(arg0));
    }

    public static Localizable localizableANNOTATION_ONLY_ONCE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("annotation.only.once", new Object[]{arg0});
    }

    public static String ANNOTATION_ONLY_ONCE(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableANNOTATION_ONLY_ONCE(arg0));
    }

    public static Localizable localizableSTATEFUL_COOKIE_HEADER_INCORRECT(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("stateful.cookie.header.incorrect", new Object[]{arg0, arg1});
    }

    public static String STATEFUL_COOKIE_HEADER_INCORRECT(Object arg0, Object arg1) {
        return LOCALIZER.localize(ServerMessages.localizableSTATEFUL_COOKIE_HEADER_INCORRECT(arg0, arg1));
    }

    public static Localizable localizableSERVICE_NAME_REQUIRED() {
        return MESSAGE_FACTORY.getMessage("service.name.required", new Object[0]);
    }

    public static String SERVICE_NAME_REQUIRED() {
        return LOCALIZER.localize(ServerMessages.localizableSERVICE_NAME_REQUIRED());
    }

    public static Localizable localizableRUNTIME_PARSER_WSDL_INCORRECTSERVICE(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("runtime.parser.wsdl.incorrectservice", new Object[]{arg0, arg1});
    }

    public static String RUNTIME_PARSER_WSDL_INCORRECTSERVICE(Object arg0, Object arg1) {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_PARSER_WSDL_INCORRECTSERVICE(arg0, arg1));
    }

    public static Localizable localizableDUPLICATE_PRIMARY_WSDL(Object arg0) {
        return MESSAGE_FACTORY.getMessage("duplicate.primary.wsdl", new Object[]{arg0});
    }

    public static String DUPLICATE_PRIMARY_WSDL(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableDUPLICATE_PRIMARY_WSDL(arg0));
    }

    public static Localizable localizableGENERATE_NON_STANDARD_WSDL() {
        return MESSAGE_FACTORY.getMessage("generate.non.standard.wsdl", new Object[0]);
    }

    public static String GENERATE_NON_STANDARD_WSDL() {
        return LOCALIZER.localize(ServerMessages.localizableGENERATE_NON_STANDARD_WSDL());
    }

    public static Localizable localizableRUNTIME_PARSER_MISSING_ATTRIBUTE_NO_LINE() {
        return MESSAGE_FACTORY.getMessage("runtime.parser.missing.attribute.no.line", new Object[0]);
    }

    public static String RUNTIME_PARSER_MISSING_ATTRIBUTE_NO_LINE() {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_PARSER_MISSING_ATTRIBUTE_NO_LINE());
    }

    public static Localizable localizableWRONG_FIELD_TYPE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("wrong.field.type", new Object[]{arg0});
    }

    public static String WRONG_FIELD_TYPE(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableWRONG_FIELD_TYPE(arg0));
    }

    public static Localizable localizableFAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("failed.to.instantiate.instanceResolver", new Object[]{arg0, arg1, arg2});
    }

    public static String FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(ServerMessages.localizableFAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(arg0, arg1, arg2));
    }

    public static Localizable localizablePROVIDER_NOT_PARAMETERIZED(Object arg0) {
        return MESSAGE_FACTORY.getMessage("provider.not.parameterized", new Object[]{arg0});
    }

    public static String PROVIDER_NOT_PARAMETERIZED(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizablePROVIDER_NOT_PARAMETERIZED(arg0));
    }

    public static Localizable localizableDISPATCH_CANNOT_FIND_METHOD(Object arg0) {
        return MESSAGE_FACTORY.getMessage("dispatch.cannotFindMethod", new Object[]{arg0});
    }

    public static String DISPATCH_CANNOT_FIND_METHOD(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableDISPATCH_CANNOT_FIND_METHOD(arg0));
    }

    public static Localizable localizableRUNTIME_PARSER_WRONG_ELEMENT(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("runtime.parser.wrong.element", new Object[]{arg0, arg1, arg2});
    }

    public static String RUNTIME_PARSER_WRONG_ELEMENT(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_PARSER_WRONG_ELEMENT(arg0, arg1, arg2));
    }

    public static Localizable localizableUNSUPPORTED_CHARSET(Object arg0) {
        return MESSAGE_FACTORY.getMessage("unsupported.charset", new Object[]{arg0});
    }

    public static String UNSUPPORTED_CHARSET(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableUNSUPPORTED_CHARSET(arg0));
    }

    public static Localizable localizableSTATEFUL_COOKIE_HEADER_REQUIRED(Object arg0) {
        return MESSAGE_FACTORY.getMessage("stateful.cookie.header.required", new Object[]{arg0});
    }

    public static String STATEFUL_COOKIE_HEADER_REQUIRED(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableSTATEFUL_COOKIE_HEADER_REQUIRED(arg0));
    }

    public static Localizable localizableRUNTIME_WSDL_PATCHER() {
        return MESSAGE_FACTORY.getMessage("runtime.wsdl.patcher", new Object[0]);
    }

    public static String RUNTIME_WSDL_PATCHER() {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_WSDL_PATCHER());
    }

    public static Localizable localizableSTATEFUL_REQURES_ADDRESSING(Object arg0) {
        return MESSAGE_FACTORY.getMessage("stateful.requres.addressing", new Object[]{arg0});
    }

    public static String STATEFUL_REQURES_ADDRESSING(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableSTATEFUL_REQURES_ADDRESSING(arg0));
    }

    public static Localizable localizableNON_UNIQUE_DISPATCH_QNAME(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("non.unique.dispatch.qname", new Object[]{arg0, arg1});
    }

    public static String NON_UNIQUE_DISPATCH_QNAME(Object arg0, Object arg1) {
        return LOCALIZER.localize(ServerMessages.localizableNON_UNIQUE_DISPATCH_QNAME(arg0, arg1));
    }

    public static Localizable localizableALREADY_HTTP_SERVER(Object arg0) {
        return MESSAGE_FACTORY.getMessage("already.http.server", new Object[]{arg0});
    }

    public static String ALREADY_HTTP_SERVER(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableALREADY_HTTP_SERVER(arg0));
    }

    public static Localizable localizableWRONG_NO_PARAMETERS(Object arg0) {
        return MESSAGE_FACTORY.getMessage("wrong.no.parameters", new Object[]{arg0});
    }

    public static String WRONG_NO_PARAMETERS(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableWRONG_NO_PARAMETERS(arg0));
    }

    public static Localizable localizableNOT_ZERO_PARAMETERS(Object arg0) {
        return MESSAGE_FACTORY.getMessage("not.zero.parameters", new Object[]{arg0});
    }

    public static String NOT_ZERO_PARAMETERS(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableNOT_ZERO_PARAMETERS(arg0));
    }

    public static Localizable localizableRUNTIME_PARSER_INVALID_ATTRIBUTE_VALUE(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("runtime.parser.invalidAttributeValue", new Object[]{arg0, arg1, arg2});
    }

    public static String RUNTIME_PARSER_INVALID_ATTRIBUTE_VALUE(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_PARSER_INVALID_ATTRIBUTE_VALUE(arg0, arg1, arg2));
    }

    public static Localizable localizableRUNTIME_PARSER_MISSING_ATTRIBUTE(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("runtime.parser.missing.attribute", new Object[]{arg0, arg1, arg2});
    }

    public static String RUNTIME_PARSER_MISSING_ATTRIBUTE(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_PARSER_MISSING_ATTRIBUTE(arg0, arg1, arg2));
    }

    public static Localizable localizableRUNTIME_PARSER_UNEXPECTED_CONTENT(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.parser.unexpectedContent", new Object[]{arg0});
    }

    public static String RUNTIME_PARSER_UNEXPECTED_CONTENT(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_PARSER_UNEXPECTED_CONTENT(arg0));
    }

    public static Localizable localizableRUNTIME_PARSER_CLASS_NOT_FOUND(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.parser.classNotFound", new Object[]{arg0});
    }

    public static String RUNTIME_PARSER_CLASS_NOT_FOUND(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_PARSER_CLASS_NOT_FOUND(arg0));
    }

    public static Localizable localizableSTATEFUL_INVALID_WEBSERVICE_CONTEXT(Object arg0) {
        return MESSAGE_FACTORY.getMessage("stateful.invalid.webservice.context", new Object[]{arg0});
    }

    public static String STATEFUL_INVALID_WEBSERVICE_CONTEXT(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableSTATEFUL_INVALID_WEBSERVICE_CONTEXT(arg0));
    }

    public static Localizable localizableNO_CURRENT_PACKET() {
        return MESSAGE_FACTORY.getMessage("no.current.packet", new Object[0]);
    }

    public static String NO_CURRENT_PACKET() {
        return LOCALIZER.localize(ServerMessages.localizableNO_CURRENT_PACKET());
    }

    public static Localizable localizableDUPLICATE_PORT_KNOWN_HEADER(Object arg0) {
        return MESSAGE_FACTORY.getMessage("duplicate.portKnownHeader", new Object[]{arg0});
    }

    public static String DUPLICATE_PORT_KNOWN_HEADER(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableDUPLICATE_PORT_KNOWN_HEADER(arg0));
    }

    public static Localizable localizableSTATIC_RESOURCE_INJECTION_ONLY(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("static.resource.injection.only", new Object[]{arg0, arg1});
    }

    public static String STATIC_RESOURCE_INJECTION_ONLY(Object arg0, Object arg1) {
        return LOCALIZER.localize(ServerMessages.localizableSTATIC_RESOURCE_INJECTION_ONLY(arg0, arg1));
    }

    public static Localizable localizableCAN_NOT_GENERATE_WSDL(Object arg0) {
        return MESSAGE_FACTORY.getMessage("can.not.generate.wsdl", new Object[]{arg0});
    }

    public static String CAN_NOT_GENERATE_WSDL(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableCAN_NOT_GENERATE_WSDL(arg0));
    }

    public static Localizable localizableALREADY_HTTPS_SERVER(Object arg0) {
        return MESSAGE_FACTORY.getMessage("already.https.server", new Object[]{arg0});
    }

    public static String ALREADY_HTTPS_SERVER(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableALREADY_HTTPS_SERVER(arg0));
    }

    public static Localizable localizableRUNTIME_PARSER_INVALID_ELEMENT(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("runtime.parser.invalidElement", new Object[]{arg0, arg1});
    }

    public static String RUNTIME_PARSER_INVALID_ELEMENT(Object arg0, Object arg1) {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_PARSER_INVALID_ELEMENT(arg0, arg1));
    }

    public static Localizable localizableRUNTIME_PARSER_WSDL_MULTIPLEBINDING(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("runtime.parser.wsdl.multiplebinding", new Object[]{arg0, arg1, arg2});
    }

    public static String RUNTIME_PARSER_WSDL_MULTIPLEBINDING(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_PARSER_WSDL_MULTIPLEBINDING(arg0, arg1, arg2));
    }

    public static Localizable localizableRUNTIMEMODELER_INVALIDANNOTATION_ON_IMPL(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("runtimemodeler.invalidannotationOnImpl", new Object[]{arg0, arg1, arg2});
    }

    public static String RUNTIMEMODELER_INVALIDANNOTATION_ON_IMPL(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIMEMODELER_INVALIDANNOTATION_ON_IMPL(arg0, arg1, arg2));
    }

    public static Localizable localizablePROVIDER_INVALID_PARAMETER_TYPE(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("provider.invalid.parameterType", new Object[]{arg0, arg1});
    }

    public static String PROVIDER_INVALID_PARAMETER_TYPE(Object arg0, Object arg1) {
        return LOCALIZER.localize(ServerMessages.localizablePROVIDER_INVALID_PARAMETER_TYPE(arg0, arg1));
    }

    public static Localizable localizableRUNTIME_PARSER_WSDL(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.parser.wsdl", new Object[]{arg0});
    }

    public static String RUNTIME_PARSER_WSDL(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableRUNTIME_PARSER_WSDL(arg0));
    }

    public static Localizable localizableNO_CONTENT_TYPE() {
        return MESSAGE_FACTORY.getMessage("no.contentType", new Object[0]);
    }

    public static String NO_CONTENT_TYPE() {
        return LOCALIZER.localize(ServerMessages.localizableNO_CONTENT_TYPE());
    }

    public static Localizable localizableNOT_IMPLEMENT_PROVIDER(Object arg0) {
        return MESSAGE_FACTORY.getMessage("not.implement.provider", new Object[]{arg0});
    }

    public static String NOT_IMPLEMENT_PROVIDER(Object arg0) {
        return LOCALIZER.localize(ServerMessages.localizableNOT_IMPLEMENT_PROVIDER(arg0));
    }

    public static Localizable localizableSOAPDECODER_ERR() {
        return MESSAGE_FACTORY.getMessage("soapdecoder.err", new Object[0]);
    }

    public static String SOAPDECODER_ERR() {
        return LOCALIZER.localize(ServerMessages.localizableSOAPDECODER_ERR());
    }

    public static Localizable localizablePORT_NAME_REQUIRED() {
        return MESSAGE_FACTORY.getMessage("port.name.required", new Object[0]);
    }

    public static String PORT_NAME_REQUIRED() {
        return LOCALIZER.localize(ServerMessages.localizablePORT_NAME_REQUIRED());
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(ServerMessages.BUNDLE_NAME, locale);
        }
    }
}

