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

public final class PolicyMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.policy";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.policy", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableWSP_1013_EXCEPTION_WHEN_READING_POLICY_ELEMENT(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_1013_EXCEPTION_WHEN_READING_POLICY_ELEMENT", new Object[]{arg0});
    }

    public static String WSP_1013_EXCEPTION_WHEN_READING_POLICY_ELEMENT(Object arg0) {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1013_EXCEPTION_WHEN_READING_POLICY_ELEMENT(arg0));
    }

    public static Localizable localizableWSP_1001_XML_EXCEPTION_WHEN_PROCESSING_POLICY_REFERENCE() {
        return MESSAGE_FACTORY.getMessage("WSP_1001_XML_EXCEPTION_WHEN_PROCESSING_POLICY_REFERENCE", new Object[0]);
    }

    public static String WSP_1001_XML_EXCEPTION_WHEN_PROCESSING_POLICY_REFERENCE() {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1001_XML_EXCEPTION_WHEN_PROCESSING_POLICY_REFERENCE());
    }

    public static Localizable localizableWSP_1014_CAN_NOT_FIND_POLICY(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_1014_CAN_NOT_FIND_POLICY", new Object[]{arg0});
    }

    public static String WSP_1014_CAN_NOT_FIND_POLICY(Object arg0) {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1014_CAN_NOT_FIND_POLICY(arg0));
    }

    public static Localizable localizableWSP_1009_NOT_MARSHALLING_ANY_POLICIES_POLICY_MAP_IS_NULL() {
        return MESSAGE_FACTORY.getMessage("WSP_1009_NOT_MARSHALLING_ANY_POLICIES_POLICY_MAP_IS_NULL", new Object[0]);
    }

    public static String WSP_1009_NOT_MARSHALLING_ANY_POLICIES_POLICY_MAP_IS_NULL() {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1009_NOT_MARSHALLING_ANY_POLICIES_POLICY_MAP_IS_NULL());
    }

    public static Localizable localizableWSP_1011_FAILED_TO_RETRIEVE_EFFECTIVE_POLICY_FOR_SUBJECT(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_1011_FAILED_TO_RETRIEVE_EFFECTIVE_POLICY_FOR_SUBJECT", new Object[]{arg0});
    }

    public static String WSP_1011_FAILED_TO_RETRIEVE_EFFECTIVE_POLICY_FOR_SUBJECT(Object arg0) {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1011_FAILED_TO_RETRIEVE_EFFECTIVE_POLICY_FOR_SUBJECT(arg0));
    }

    public static Localizable localizableWSP_1016_POLICY_ID_NULL_OR_DUPLICATE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_1016_POLICY_ID_NULL_OR_DUPLICATE", new Object[]{arg0});
    }

    public static String WSP_1016_POLICY_ID_NULL_OR_DUPLICATE(Object arg0) {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1016_POLICY_ID_NULL_OR_DUPLICATE(arg0));
    }

    public static Localizable localizableWSP_1021_FAULT_NOT_BOUND(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_1021_FAULT_NOT_BOUND", new Object[]{arg0});
    }

    public static String WSP_1021_FAULT_NOT_BOUND(Object arg0) {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1021_FAULT_NOT_BOUND(arg0));
    }

    public static Localizable localizableWSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("WSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME", new Object[]{arg0, arg1});
    }

    public static String WSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(Object arg0, Object arg1) {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(arg0, arg1));
    }

    public static Localizable localizableWSP_1019_CREATE_EMPTY_POLICY_MAP() {
        return MESSAGE_FACTORY.getMessage("WSP_1019_CREATE_EMPTY_POLICY_MAP", new Object[0]);
    }

    public static String WSP_1019_CREATE_EMPTY_POLICY_MAP() {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1019_CREATE_EMPTY_POLICY_MAP());
    }

    public static Localizable localizableWSP_1020_DUPLICATE_ID(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_1020_DUPLICATE_ID", new Object[]{arg0});
    }

    public static String WSP_1020_DUPLICATE_ID(Object arg0) {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1020_DUPLICATE_ID(arg0));
    }

    public static Localizable localizableWSP_1002_UNABLE_TO_MARSHALL_POLICY_OR_POLICY_REFERENCE() {
        return MESSAGE_FACTORY.getMessage("WSP_1002_UNABLE_TO_MARSHALL_POLICY_OR_POLICY_REFERENCE", new Object[0]);
    }

    public static String WSP_1002_UNABLE_TO_MARSHALL_POLICY_OR_POLICY_REFERENCE() {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1002_UNABLE_TO_MARSHALL_POLICY_OR_POLICY_REFERENCE());
    }

    public static Localizable localizableWSP_1008_NOT_MARSHALLING_WSDL_SUBJ_NULL(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_1008_NOT_MARSHALLING_WSDL_SUBJ_NULL", new Object[]{arg0});
    }

    public static String WSP_1008_NOT_MARSHALLING_WSDL_SUBJ_NULL(Object arg0) {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1008_NOT_MARSHALLING_WSDL_SUBJ_NULL(arg0));
    }

    public static Localizable localizableWSP_1017_MAP_UPDATE_FAILED() {
        return MESSAGE_FACTORY.getMessage("WSP_1017_MAP_UPDATE_FAILED", new Object[0]);
    }

    public static String WSP_1017_MAP_UPDATE_FAILED() {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1017_MAP_UPDATE_FAILED());
    }

    public static Localizable localizableWSP_1018_FAILED_TO_MARSHALL_POLICY(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_1018_FAILED_TO_MARSHALL_POLICY", new Object[]{arg0});
    }

    public static String WSP_1018_FAILED_TO_MARSHALL_POLICY(Object arg0) {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1018_FAILED_TO_MARSHALL_POLICY(arg0));
    }

    public static Localizable localizableWSP_1004_POLICY_URIS_CAN_NOT_BE_NULL() {
        return MESSAGE_FACTORY.getMessage("WSP_1004_POLICY_URIS_CAN_NOT_BE_NULL", new Object[0]);
    }

    public static String WSP_1004_POLICY_URIS_CAN_NOT_BE_NULL() {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1004_POLICY_URIS_CAN_NOT_BE_NULL());
    }

    public static Localizable localizableWSP_1010_NO_POLICIES_DEFINED() {
        return MESSAGE_FACTORY.getMessage("WSP_1010_NO_POLICIES_DEFINED", new Object[0]);
    }

    public static String WSP_1010_NO_POLICIES_DEFINED() {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1010_NO_POLICIES_DEFINED());
    }

    public static Localizable localizableWSP_1012_FAILED_CONFIGURE_WSDL_MODEL() {
        return MESSAGE_FACTORY.getMessage("WSP_1012_FAILED_CONFIGURE_WSDL_MODEL", new Object[0]);
    }

    public static String WSP_1012_FAILED_CONFIGURE_WSDL_MODEL() {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1012_FAILED_CONFIGURE_WSDL_MODEL());
    }

    public static Localizable localizableWSP_1015_SERVER_SIDE_ASSERTION_VALIDATION_FAILED(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("WSP_1015_SERVER_SIDE_ASSERTION_VALIDATION_FAILED", new Object[]{arg0, arg1});
    }

    public static String WSP_1015_SERVER_SIDE_ASSERTION_VALIDATION_FAILED(Object arg0, Object arg1) {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1015_SERVER_SIDE_ASSERTION_VALIDATION_FAILED(arg0, arg1));
    }

    public static Localizable localizableWSP_1005_POLICY_REFERENCE_DOES_NOT_EXIST(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_1005_POLICY_REFERENCE_DOES_NOT_EXIST", new Object[]{arg0});
    }

    public static String WSP_1005_POLICY_REFERENCE_DOES_NOT_EXIST(Object arg0) {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1005_POLICY_REFERENCE_DOES_NOT_EXIST(arg0));
    }

    public static Localizable localizableWSP_1006_POLICY_MAP_EXTENDER_CAN_NOT_BE_NULL() {
        return MESSAGE_FACTORY.getMessage("WSP_1006_POLICY_MAP_EXTENDER_CAN_NOT_BE_NULL", new Object[0]);
    }

    public static String WSP_1006_POLICY_MAP_EXTENDER_CAN_NOT_BE_NULL() {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1006_POLICY_MAP_EXTENDER_CAN_NOT_BE_NULL());
    }

    public static Localizable localizableWSP_1007_POLICY_EXCEPTION_WHILE_FINISHING_PARSING_WSDL() {
        return MESSAGE_FACTORY.getMessage("WSP_1007_POLICY_EXCEPTION_WHILE_FINISHING_PARSING_WSDL", new Object[0]);
    }

    public static String WSP_1007_POLICY_EXCEPTION_WHILE_FINISHING_PARSING_WSDL() {
        return LOCALIZER.localize(PolicyMessages.localizableWSP_1007_POLICY_EXCEPTION_WHILE_FINISHING_PARSING_WSDL());
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(PolicyMessages.BUNDLE_NAME, locale);
        }
    }
}

