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

public final class ModelerMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.modeler";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.modeler", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableRUNTIME_MODELER_EXTERNAL_METADATA_UNSUPPORTED_SCHEMA(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.external.metadata.unsupported.schema", new Object[]{arg0, arg1});
    }

    public static String RUNTIME_MODELER_EXTERNAL_METADATA_UNSUPPORTED_SCHEMA(Object arg0, Object arg1) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_EXTERNAL_METADATA_UNSUPPORTED_SCHEMA(arg0, arg1));
    }

    public static Localizable localizableRUNTIME_MODELER_ONEWAY_OPERATION_NO_CHECKED_EXCEPTIONS(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.oneway.operation.no.checked.exceptions", new Object[]{arg0, arg1, arg2});
    }

    public static String RUNTIME_MODELER_ONEWAY_OPERATION_NO_CHECKED_EXCEPTIONS(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_ONEWAY_OPERATION_NO_CHECKED_EXCEPTIONS(arg0, arg1, arg2));
    }

    public static Localizable localizableRUNTIME_MODELER_NO_PACKAGE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.no.package", new Object[]{arg0});
    }

    public static String RUNTIME_MODELER_NO_PACKAGE(Object arg0) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_NO_PACKAGE(arg0));
    }

    public static Localizable localizableRUNTIME_MODELER_EXTERNAL_METADATA_UNABLE_TO_READ(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.external.metadata.unable.to.read", new Object[]{arg0});
    }

    public static String RUNTIME_MODELER_EXTERNAL_METADATA_UNABLE_TO_READ(Object arg0) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_EXTERNAL_METADATA_UNABLE_TO_READ(arg0));
    }

    public static Localizable localizableRUNTIME_MODELER_WEBMETHOD_MUST_BE_NONSTATIC(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.webmethod.must.be.nonstatic", new Object[]{arg0});
    }

    public static String RUNTIME_MODELER_WEBMETHOD_MUST_BE_NONSTATIC(Object arg0) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_WEBMETHOD_MUST_BE_NONSTATIC(arg0));
    }

    public static Localizable localizableRUNTIMEMODELER_INVALID_SOAPBINDING_ON_METHOD(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("runtimemodeler.invalid.soapbindingOnMethod", new Object[]{arg0, arg1, arg2});
    }

    public static String RUNTIMEMODELER_INVALID_SOAPBINDING_ON_METHOD(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIMEMODELER_INVALID_SOAPBINDING_ON_METHOD(arg0, arg1, arg2));
    }

    public static Localizable localizableRUNTIME_MODELER_EXTERNAL_METADATA_WRONG_FORMAT(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.external.metadata.wrong.format", new Object[]{arg0});
    }

    public static String RUNTIME_MODELER_EXTERNAL_METADATA_WRONG_FORMAT(Object arg0) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_EXTERNAL_METADATA_WRONG_FORMAT(arg0));
    }

    public static Localizable localizableRUNTIME_MODELER_NO_WEBSERVICE_ANNOTATION(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.no.webservice.annotation", new Object[]{arg0});
    }

    public static String RUNTIME_MODELER_NO_WEBSERVICE_ANNOTATION(Object arg0) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_NO_WEBSERVICE_ANNOTATION(arg0));
    }

    public static Localizable localizableRUNTIME_MODELER_SOAPBINDING_CONFLICT(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.soapbinding.conflict", new Object[]{arg0, arg1, arg2});
    }

    public static String RUNTIME_MODELER_SOAPBINDING_CONFLICT(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_SOAPBINDING_CONFLICT(arg0, arg1, arg2));
    }

    public static Localizable localizableNESTED_MODELER_ERROR(Object arg0) {
        return MESSAGE_FACTORY.getMessage("nestedModelerError", new Object[]{arg0});
    }

    public static String NESTED_MODELER_ERROR(Object arg0) {
        return LOCALIZER.localize(ModelerMessages.localizableNESTED_MODELER_ERROR(arg0));
    }

    public static Localizable localizableRUNTIME_MODELER_METHOD_NOT_FOUND(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.method.not.found", new Object[]{arg0, arg1});
    }

    public static String RUNTIME_MODELER_METHOD_NOT_FOUND(Object arg0, Object arg1) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_METHOD_NOT_FOUND(arg0, arg1));
    }

    public static Localizable localizableUNABLE_TO_CREATE_JAXB_CONTEXT() {
        return MESSAGE_FACTORY.getMessage("unable.to.create.JAXBContext", new Object[0]);
    }

    public static String UNABLE_TO_CREATE_JAXB_CONTEXT() {
        return LOCALIZER.localize(ModelerMessages.localizableUNABLE_TO_CREATE_JAXB_CONTEXT());
    }

    public static Localizable localizableRUNTIME_MODELER_NO_OPERATIONS(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.no.operations", new Object[]{arg0});
    }

    public static String RUNTIME_MODELER_NO_OPERATIONS(Object arg0) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_NO_OPERATIONS(arg0));
    }

    public static Localizable localizableRUNTIME_MODELER_WRAPPER_NOT_FOUND(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.wrapper.not.found", new Object[]{arg0});
    }

    public static String RUNTIME_MODELER_WRAPPER_NOT_FOUND(Object arg0) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_WRAPPER_NOT_FOUND(arg0));
    }

    public static Localizable localizableRUNTIME_MODELER_INVALID_SOAPBINDING_PARAMETERSTYLE(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.invalid.soapbinding.parameterstyle", new Object[]{arg0, arg1});
    }

    public static String RUNTIME_MODELER_INVALID_SOAPBINDING_PARAMETERSTYLE(Object arg0, Object arg1) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_INVALID_SOAPBINDING_PARAMETERSTYLE(arg0, arg1));
    }

    public static Localizable localizableRUNTIME_MODELER_EXTERNAL_METADATA_GENERIC(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.external.metadata.generic", new Object[]{arg0});
    }

    public static String RUNTIME_MODELER_EXTERNAL_METADATA_GENERIC(Object arg0) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_EXTERNAL_METADATA_GENERIC(arg0));
    }

    public static Localizable localizableRUNTIME_MODELER_ADDRESSING_RESPONSES_NOSUCHMETHOD(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.addressing.responses.nosuchmethod", new Object[]{arg0});
    }

    public static String RUNTIME_MODELER_ADDRESSING_RESPONSES_NOSUCHMETHOD(Object arg0) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_ADDRESSING_RESPONSES_NOSUCHMETHOD(arg0));
    }

    public static Localizable localizableRUNTIME_MODELER_WSFEATURE_ILLEGAL_FTRCONSTRUCTOR(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.wsfeature.illegal.ftrconstructor", new Object[]{arg0, arg1});
    }

    public static String RUNTIME_MODELER_WSFEATURE_ILLEGAL_FTRCONSTRUCTOR(Object arg0, Object arg1) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_WSFEATURE_ILLEGAL_FTRCONSTRUCTOR(arg0, arg1));
    }

    public static Localizable localizableRUNTIME_MODELER_WEBMETHOD_MUST_BE_PUBLIC(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.webmethod.must.be.public", new Object[]{arg0});
    }

    public static String RUNTIME_MODELER_WEBMETHOD_MUST_BE_PUBLIC(Object arg0) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_WEBMETHOD_MUST_BE_PUBLIC(arg0));
    }

    public static Localizable localizableRUNTIME_MODELER_ONEWAY_OPERATION_NO_OUT_PARAMETERS(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.oneway.operation.no.out.parameters", new Object[]{arg0, arg1});
    }

    public static String RUNTIME_MODELER_ONEWAY_OPERATION_NO_OUT_PARAMETERS(Object arg0, Object arg1) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_ONEWAY_OPERATION_NO_OUT_PARAMETERS(arg0, arg1));
    }

    public static Localizable localizableRUNTIME_MODELER_ENDPOINT_INTERFACE_NO_WEBSERVICE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.endpoint.interface.no.webservice", new Object[]{arg0});
    }

    public static String RUNTIME_MODELER_ENDPOINT_INTERFACE_NO_WEBSERVICE(Object arg0) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_ENDPOINT_INTERFACE_NO_WEBSERVICE(arg0));
    }

    public static Localizable localizableRUNTIME_MODELER_WSFEATURE_NO_FTRCONSTRUCTOR(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.wsfeature.no.ftrconstructor", new Object[]{arg0, arg1});
    }

    public static String RUNTIME_MODELER_WSFEATURE_NO_FTRCONSTRUCTOR(Object arg0, Object arg1) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_WSFEATURE_NO_FTRCONSTRUCTOR(arg0, arg1));
    }

    public static Localizable localizableRUNTIME_MODELER_MTOM_CONFLICT(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.mtom.conflict", new Object[]{arg0, arg1});
    }

    public static String RUNTIME_MODELER_MTOM_CONFLICT(Object arg0, Object arg1) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_MTOM_CONFLICT(arg0, arg1));
    }

    public static Localizable localizableRUNTIME_MODELER_CLASS_NOT_FOUND(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.class.not.found", new Object[]{arg0});
    }

    public static String RUNTIME_MODELER_CLASS_NOT_FOUND(Object arg0) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_CLASS_NOT_FOUND(arg0));
    }

    public static Localizable localizableRUNTIME_MODELER_WSFEATURE_MORETHANONE_FTRCONSTRUCTOR(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.wsfeature.morethanone.ftrconstructor", new Object[]{arg0, arg1});
    }

    public static String RUNTIME_MODELER_WSFEATURE_MORETHANONE_FTRCONSTRUCTOR(Object arg0, Object arg1) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_WSFEATURE_MORETHANONE_FTRCONSTRUCTOR(arg0, arg1));
    }

    public static Localizable localizableRUNTIME_MODELER_FEATURE_CONFLICT(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.feature.conflict", new Object[]{arg0, arg1});
    }

    public static String RUNTIME_MODELER_FEATURE_CONFLICT(Object arg0, Object arg1) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_FEATURE_CONFLICT(arg0, arg1));
    }

    public static Localizable localizableRUNTIME_MODELER_WEBMETHOD_MUST_BE_NONSTATICFINAL(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.webmethod.must.be.nonstaticfinal", new Object[]{arg0});
    }

    public static String RUNTIME_MODELER_WEBMETHOD_MUST_BE_NONSTATICFINAL(Object arg0) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_WEBMETHOD_MUST_BE_NONSTATICFINAL(arg0));
    }

    public static Localizable localizableRUNTIME_MODELER_CANNOT_GET_SERVICE_NAME_FROM_INTERFACE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.cannot.get.serviceName.from.interface", new Object[]{arg0});
    }

    public static String RUNTIME_MODELER_CANNOT_GET_SERVICE_NAME_FROM_INTERFACE(Object arg0) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_CANNOT_GET_SERVICE_NAME_FROM_INTERFACE(arg0));
    }

    public static Localizable localizableNOT_A_VALID_BARE_METHOD(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("not.a.valid.bare.method", new Object[]{arg0, arg1});
    }

    public static String NOT_A_VALID_BARE_METHOD(Object arg0, Object arg1) {
        return LOCALIZER.localize(ModelerMessages.localizableNOT_A_VALID_BARE_METHOD(arg0, arg1));
    }

    public static Localizable localizableRUNTIME_MODELER_PORTNAME_SERVICENAME_NAMESPACE_MISMATCH(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("runtime.modeler.portname.servicename.namespace.mismatch", new Object[]{arg0, arg1});
    }

    public static String RUNTIME_MODELER_PORTNAME_SERVICENAME_NAMESPACE_MISMATCH(Object arg0, Object arg1) {
        return LOCALIZER.localize(ModelerMessages.localizableRUNTIME_MODELER_PORTNAME_SERVICENAME_NAMESPACE_MISMATCH(arg0, arg1));
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(ModelerMessages.BUNDLE_NAME, locale);
        }
    }
}

