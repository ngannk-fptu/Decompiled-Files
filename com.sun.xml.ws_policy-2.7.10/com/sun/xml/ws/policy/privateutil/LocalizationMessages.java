/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.localization.Localizable
 *  com.sun.istack.localization.LocalizableMessageFactory
 *  com.sun.istack.localization.LocalizableMessageFactory$ResourceBundleSupplier
 *  com.sun.istack.localization.Localizer
 */
package com.sun.xml.ws.policy.privateutil;

import com.sun.istack.localization.Localizable;
import com.sun.istack.localization.LocalizableMessageFactory;
import com.sun.istack.localization.Localizer;
import java.util.Locale;
import java.util.ResourceBundle;

public final class LocalizationMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.policy.privateutil.Localization";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.policy.privateutil.Localization", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableWSP_0038_POLICY_TO_ATTACH_MUST_NOT_BE_NULL() {
        return MESSAGE_FACTORY.getMessage("WSP_0038_POLICY_TO_ATTACH_MUST_NOT_BE_NULL", new Object[0]);
    }

    public static String WSP_0038_POLICY_TO_ATTACH_MUST_NOT_BE_NULL() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0038_POLICY_TO_ATTACH_MUST_NOT_BE_NULL());
    }

    public static Localizable localizableWSP_0075_PROBLEMATIC_ASSERTION_STATE(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("WSP_0075_PROBLEMATIC_ASSERTION_STATE", new Object[]{arg0, arg1});
    }

    public static String WSP_0075_PROBLEMATIC_ASSERTION_STATE(Object arg0, Object arg1) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0075_PROBLEMATIC_ASSERTION_STATE(arg0, arg1));
    }

    public static Localizable localizableWSP_0041_POLICY_REFERENCE_NODE_FOUND_WITH_NO_POLICY_REFERENCE_IN_IT() {
        return MESSAGE_FACTORY.getMessage("WSP_0041_POLICY_REFERENCE_NODE_FOUND_WITH_NO_POLICY_REFERENCE_IN_IT", new Object[0]);
    }

    public static String WSP_0041_POLICY_REFERENCE_NODE_FOUND_WITH_NO_POLICY_REFERENCE_IN_IT() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0041_POLICY_REFERENCE_NODE_FOUND_WITH_NO_POLICY_REFERENCE_IN_IT());
    }

    public static Localizable localizableWSP_0012_UNABLE_TO_UNMARSHALL_POLICY_MALFORMED_URI() {
        return MESSAGE_FACTORY.getMessage("WSP_0012_UNABLE_TO_UNMARSHALL_POLICY_MALFORMED_URI", new Object[0]);
    }

    public static String WSP_0012_UNABLE_TO_UNMARSHALL_POLICY_MALFORMED_URI() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0012_UNABLE_TO_UNMARSHALL_POLICY_MALFORMED_URI());
    }

    public static Localizable localizableWSP_0044_POLICY_MAP_MUTATOR_ALREADY_CONNECTED() {
        return MESSAGE_FACTORY.getMessage("WSP_0044_POLICY_MAP_MUTATOR_ALREADY_CONNECTED", new Object[0]);
    }

    public static String WSP_0044_POLICY_MAP_MUTATOR_ALREADY_CONNECTED() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0044_POLICY_MAP_MUTATOR_ALREADY_CONNECTED());
    }

    public static Localizable localizableWSP_0042_POLICY_REFERENCE_NODE_EXPECTED_INSTEAD_OF(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0042_POLICY_REFERENCE_NODE_EXPECTED_INSTEAD_OF", new Object[]{arg0});
    }

    public static String WSP_0042_POLICY_REFERENCE_NODE_EXPECTED_INSTEAD_OF(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0042_POLICY_REFERENCE_NODE_EXPECTED_INSTEAD_OF(arg0));
    }

    public static Localizable localizableWSP_0076_NO_SERVICE_PROVIDERS_FOUND(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0076_NO_SERVICE_PROVIDERS_FOUND", new Object[]{arg0});
    }

    public static String WSP_0076_NO_SERVICE_PROVIDERS_FOUND(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0076_NO_SERVICE_PROVIDERS_FOUND(arg0));
    }

    public static Localizable localizableWSP_0008_UNEXPECTED_CHILD_MODEL_TYPE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0008_UNEXPECTED_CHILD_MODEL_TYPE", new Object[]{arg0});
    }

    public static String WSP_0008_UNEXPECTED_CHILD_MODEL_TYPE(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0008_UNEXPECTED_CHILD_MODEL_TYPE(arg0));
    }

    public static Localizable localizableWSP_0085_MESSAGE_FAULT_NO_NAME() {
        return MESSAGE_FACTORY.getMessage("WSP_0085_MESSAGE_FAULT_NO_NAME", new Object[0]);
    }

    public static String WSP_0085_MESSAGE_FAULT_NO_NAME() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0085_MESSAGE_FAULT_NO_NAME());
    }

    public static Localizable localizableWSP_0017_UNABLE_TO_ACCESS_POLICY_SOURCE_MODEL_PLUS_REASON(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("WSP_0017_UNABLE_TO_ACCESS_POLICY_SOURCE_MODEL_PLUS_REASON", new Object[]{arg0, arg1});
    }

    public static String WSP_0017_UNABLE_TO_ACCESS_POLICY_SOURCE_MODEL_PLUS_REASON(Object arg0, Object arg1) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0017_UNABLE_TO_ACCESS_POLICY_SOURCE_MODEL_PLUS_REASON(arg0, arg1));
    }

    public static Localizable localizableWSP_0006_UNEXPECTED_MULTIPLE_POLICY_NODES() {
        return MESSAGE_FACTORY.getMessage("WSP_0006_UNEXPECTED_MULTIPLE_POLICY_NODES", new Object[0]);
    }

    public static String WSP_0006_UNEXPECTED_MULTIPLE_POLICY_NODES() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0006_UNEXPECTED_MULTIPLE_POLICY_NODES());
    }

    public static Localizable localizableWSP_0091_END_ELEMENT_NO_MATCH(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("WSP_0091_END_ELEMENT_NO_MATCH", new Object[]{arg0, arg1, arg2});
    }

    public static String WSP_0091_END_ELEMENT_NO_MATCH(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0091_END_ELEMENT_NO_MATCH(arg0, arg1, arg2));
    }

    public static Localizable localizableWSP_0050_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_POLICY_REFERENCE_NODE_TYPE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0050_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_POLICY_REFERENCE_NODE_TYPE", new Object[]{arg0});
    }

    public static String WSP_0050_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_POLICY_REFERENCE_NODE_TYPE(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0050_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_POLICY_REFERENCE_NODE_TYPE(arg0));
    }

    public static Localizable localizableWSP_0004_UNEXPECTED_VISIBILITY_ATTR_VALUE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0004_UNEXPECTED_VISIBILITY_ATTR_VALUE", new Object[]{arg0});
    }

    public static String WSP_0004_UNEXPECTED_VISIBILITY_ATTR_VALUE(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0004_UNEXPECTED_VISIBILITY_ATTR_VALUE(arg0));
    }

    public static Localizable localizableWSP_0047_POLICY_IS_NULL_RETURNING() {
        return MESSAGE_FACTORY.getMessage("WSP_0047_POLICY_IS_NULL_RETURNING", new Object[0]);
    }

    public static String WSP_0047_POLICY_IS_NULL_RETURNING() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0047_POLICY_IS_NULL_RETURNING());
    }

    public static Localizable localizableWSP_0035_RECONFIGURE_ALTERNATIVES(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0035_RECONFIGURE_ALTERNATIVES", new Object[]{arg0});
    }

    public static String WSP_0035_RECONFIGURE_ALTERNATIVES(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0035_RECONFIGURE_ALTERNATIVES(arg0));
    }

    public static Localizable localizableWSP_0048_POLICY_ELEMENT_EXPECTED_FIRST() {
        return MESSAGE_FACTORY.getMessage("WSP_0048_POLICY_ELEMENT_EXPECTED_FIRST", new Object[0]);
    }

    public static String WSP_0048_POLICY_ELEMENT_EXPECTED_FIRST() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0048_POLICY_ELEMENT_EXPECTED_FIRST());
    }

    public static Localizable localizableWSP_0064_INITIAL_POLICY_COLLECTION_MUST_NOT_BE_EMPTY() {
        return MESSAGE_FACTORY.getMessage("WSP_0064_INITIAL_POLICY_COLLECTION_MUST_NOT_BE_EMPTY", new Object[0]);
    }

    public static String WSP_0064_INITIAL_POLICY_COLLECTION_MUST_NOT_BE_EMPTY() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0064_INITIAL_POLICY_COLLECTION_MUST_NOT_BE_EMPTY());
    }

    public static Localizable localizableWSP_0083_MESSAGE_TYPE_NULL() {
        return MESSAGE_FACTORY.getMessage("WSP_0083_MESSAGE_TYPE_NULL", new Object[0]);
    }

    public static String WSP_0083_MESSAGE_TYPE_NULL() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0083_MESSAGE_TYPE_NULL());
    }

    public static Localizable localizableWSP_0013_UNABLE_TO_SET_PARENT_MODEL_ON_ROOT() {
        return MESSAGE_FACTORY.getMessage("WSP_0013_UNABLE_TO_SET_PARENT_MODEL_ON_ROOT", new Object[0]);
    }

    public static String WSP_0013_UNABLE_TO_SET_PARENT_MODEL_ON_ROOT() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0013_UNABLE_TO_SET_PARENT_MODEL_ON_ROOT());
    }

    public static Localizable localizableWSP_0005_UNEXPECTED_POLICY_ELEMENT_FOUND_IN_ASSERTION_PARAM(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0005_UNEXPECTED_POLICY_ELEMENT_FOUND_IN_ASSERTION_PARAM", new Object[]{arg0});
    }

    public static String WSP_0005_UNEXPECTED_POLICY_ELEMENT_FOUND_IN_ASSERTION_PARAM(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0005_UNEXPECTED_POLICY_ELEMENT_FOUND_IN_ASSERTION_PARAM(arg0));
    }

    public static Localizable localizableWSP_0089_EXPECTED_ELEMENT(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("WSP_0089_EXPECTED_ELEMENT", new Object[]{arg0, arg1, arg2});
    }

    public static String WSP_0089_EXPECTED_ELEMENT(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0089_EXPECTED_ELEMENT(arg0, arg1, arg2));
    }

    public static Localizable localizableWSP_0002_UNRECOGNIZED_SCOPE_TYPE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0002_UNRECOGNIZED_SCOPE_TYPE", new Object[]{arg0});
    }

    public static String WSP_0002_UNRECOGNIZED_SCOPE_TYPE(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0002_UNRECOGNIZED_SCOPE_TYPE(arg0));
    }

    public static Localizable localizableWSP_0090_UNEXPECTED_ELEMENT(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("WSP_0090_UNEXPECTED_ELEMENT", new Object[]{arg0, arg1});
    }

    public static String WSP_0090_UNEXPECTED_ELEMENT(Object arg0, Object arg1) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0090_UNEXPECTED_ELEMENT(arg0, arg1));
    }

    public static Localizable localizableWSP_0073_CREATE_CHILD_NODE_OPERATION_NOT_SUPPORTED(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("WSP_0073_CREATE_CHILD_NODE_OPERATION_NOT_SUPPORTED", new Object[]{arg0, arg1});
    }

    public static String WSP_0073_CREATE_CHILD_NODE_OPERATION_NOT_SUPPORTED(Object arg0, Object arg1) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0073_CREATE_CHILD_NODE_OPERATION_NOT_SUPPORTED(arg0, arg1));
    }

    public static Localizable localizableWSP_0026_SINGLE_EMPTY_ALTERNATIVE_COMBINATION_CREATED() {
        return MESSAGE_FACTORY.getMessage("WSP_0026_SINGLE_EMPTY_ALTERNATIVE_COMBINATION_CREATED", new Object[0]);
    }

    public static String WSP_0026_SINGLE_EMPTY_ALTERNATIVE_COMBINATION_CREATED() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0026_SINGLE_EMPTY_ALTERNATIVE_COMBINATION_CREATED());
    }

    public static Localizable localizableWSP_0040_POLICY_REFERENCE_URI_ATTR_NOT_FOUND() {
        return MESSAGE_FACTORY.getMessage("WSP_0040_POLICY_REFERENCE_URI_ATTR_NOT_FOUND", new Object[0]);
    }

    public static String WSP_0040_POLICY_REFERENCE_URI_ATTR_NOT_FOUND() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0040_POLICY_REFERENCE_URI_ATTR_NOT_FOUND());
    }

    public static Localizable localizableWSP_0045_POLICY_MAP_KEY_MUST_NOT_BE_NULL() {
        return MESSAGE_FACTORY.getMessage("WSP_0045_POLICY_MAP_KEY_MUST_NOT_BE_NULL", new Object[0]);
    }

    public static String WSP_0045_POLICY_MAP_KEY_MUST_NOT_BE_NULL() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0045_POLICY_MAP_KEY_MUST_NOT_BE_NULL());
    }

    public static Localizable localizableWSP_0014_UNABLE_TO_INSTANTIATE_READER_FOR_STORAGE() {
        return MESSAGE_FACTORY.getMessage("WSP_0014_UNABLE_TO_INSTANTIATE_READER_FOR_STORAGE", new Object[0]);
    }

    public static String WSP_0014_UNABLE_TO_INSTANTIATE_READER_FOR_STORAGE() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0014_UNABLE_TO_INSTANTIATE_READER_FOR_STORAGE());
    }

    public static Localizable localizableWSP_0015_UNABLE_TO_INSTANTIATE_DIGEST_ALG_URI_FIELD() {
        return MESSAGE_FACTORY.getMessage("WSP_0015_UNABLE_TO_INSTANTIATE_DIGEST_ALG_URI_FIELD", new Object[0]);
    }

    public static String WSP_0015_UNABLE_TO_INSTANTIATE_DIGEST_ALG_URI_FIELD() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0015_UNABLE_TO_INSTANTIATE_DIGEST_ALG_URI_FIELD());
    }

    public static Localizable localizableWSP_0087_UNKNOWN_EVENT(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0087_UNKNOWN_EVENT", new Object[]{arg0});
    }

    public static String WSP_0087_UNKNOWN_EVENT(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0087_UNKNOWN_EVENT(arg0));
    }

    public static Localizable localizableWSP_0027_SERVICE_PROVIDER_NOT_FOUND(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0027_SERVICE_PROVIDER_NOT_FOUND", new Object[]{arg0});
    }

    public static String WSP_0027_SERVICE_PROVIDER_NOT_FOUND(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0027_SERVICE_PROVIDER_NOT_FOUND(arg0));
    }

    public static Localizable localizableWSP_0066_ILLEGAL_PROVIDER_CLASSNAME(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0066_ILLEGAL_PROVIDER_CLASSNAME", new Object[]{arg0});
    }

    public static String WSP_0066_ILLEGAL_PROVIDER_CLASSNAME(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0066_ILLEGAL_PROVIDER_CLASSNAME(arg0));
    }

    public static Localizable localizableWSP_0084_MESSAGE_TYPE_NO_MESSAGE() {
        return MESSAGE_FACTORY.getMessage("WSP_0084_MESSAGE_TYPE_NO_MESSAGE", new Object[0]);
    }

    public static String WSP_0084_MESSAGE_TYPE_NO_MESSAGE() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0084_MESSAGE_TYPE_NO_MESSAGE());
    }

    public static Localizable localizableWSP_0018_UNABLE_TO_ACCESS_POLICY_SOURCE_MODEL(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0018_UNABLE_TO_ACCESS_POLICY_SOURCE_MODEL", new Object[]{arg0});
    }

    public static String WSP_0018_UNABLE_TO_ACCESS_POLICY_SOURCE_MODEL(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0018_UNABLE_TO_ACCESS_POLICY_SOURCE_MODEL(arg0));
    }

    public static Localizable localizableWSP_0007_UNEXPECTED_MODEL_NODE_TYPE_FOUND(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0007_UNEXPECTED_MODEL_NODE_TYPE_FOUND", new Object[]{arg0});
    }

    public static String WSP_0007_UNEXPECTED_MODEL_NODE_TYPE_FOUND(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0007_UNEXPECTED_MODEL_NODE_TYPE_FOUND(arg0));
    }

    public static Localizable localizableWSP_0003_UNMARSHALLING_FAILED_END_TAG_DOES_NOT_MATCH(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("WSP_0003_UNMARSHALLING_FAILED_END_TAG_DOES_NOT_MATCH", new Object[]{arg0, arg1});
    }

    public static String WSP_0003_UNMARSHALLING_FAILED_END_TAG_DOES_NOT_MATCH(Object arg0, Object arg1) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0003_UNMARSHALLING_FAILED_END_TAG_DOES_NOT_MATCH(arg0, arg1));
    }

    public static Localizable localizableWSP_0052_NUMBER_OF_ALTERNATIVE_COMBINATIONS_CREATED(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0052_NUMBER_OF_ALTERNATIVE_COMBINATIONS_CREATED", new Object[]{arg0});
    }

    public static String WSP_0052_NUMBER_OF_ALTERNATIVE_COMBINATIONS_CREATED(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0052_NUMBER_OF_ALTERNATIVE_COMBINATIONS_CREATED(arg0));
    }

    public static Localizable localizableWSP_0029_SERVICE_PORT_OPERATION_PARAM_MUST_NOT_BE_NULL(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("WSP_0029_SERVICE_PORT_OPERATION_PARAM_MUST_NOT_BE_NULL", new Object[]{arg0, arg1, arg2});
    }

    public static String WSP_0029_SERVICE_PORT_OPERATION_PARAM_MUST_NOT_BE_NULL(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0029_SERVICE_PORT_OPERATION_PARAM_MUST_NOT_BE_NULL(arg0, arg1, arg2));
    }

    public static Localizable localizableWSP_0094_INVALID_URN() {
        return MESSAGE_FACTORY.getMessage("WSP_0094_INVALID_URN", new Object[0]);
    }

    public static String WSP_0094_INVALID_URN() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0094_INVALID_URN());
    }

    public static Localizable localizableWSP_0009_UNEXPECTED_CDATA_ON_SOURCE_MODEL_NODE(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("WSP_0009_UNEXPECTED_CDATA_ON_SOURCE_MODEL_NODE", new Object[]{arg0, arg1});
    }

    public static String WSP_0009_UNEXPECTED_CDATA_ON_SOURCE_MODEL_NODE(Object arg0, Object arg1) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0009_UNEXPECTED_CDATA_ON_SOURCE_MODEL_NODE(arg0, arg1));
    }

    public static Localizable localizableWSP_0093_INVALID_URI(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("WSP_0093_INVALID_URI", new Object[]{arg0, arg1});
    }

    public static String WSP_0093_INVALID_URI(Object arg0, Object arg1) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0093_INVALID_URI(arg0, arg1));
    }

    public static Localizable localizableWSP_0010_UNEXPANDED_POLICY_REFERENCE_NODE_FOUND_REFERENCING(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0010_UNEXPANDED_POLICY_REFERENCE_NODE_FOUND_REFERENCING", new Object[]{arg0});
    }

    public static String WSP_0010_UNEXPANDED_POLICY_REFERENCE_NODE_FOUND_REFERENCING(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0010_UNEXPANDED_POLICY_REFERENCE_NODE_FOUND_REFERENCING(arg0));
    }

    public static Localizable localizableWSP_0049_PARENT_MODEL_CAN_NOT_BE_CHANGED() {
        return MESSAGE_FACTORY.getMessage("WSP_0049_PARENT_MODEL_CAN_NOT_BE_CHANGED", new Object[0]);
    }

    public static String WSP_0049_PARENT_MODEL_CAN_NOT_BE_CHANGED() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0049_PARENT_MODEL_CAN_NOT_BE_CHANGED());
    }

    public static Localizable localizableWSP_0043_POLICY_MODEL_TRANSLATION_ERROR_INPUT_PARAM_NULL() {
        return MESSAGE_FACTORY.getMessage("WSP_0043_POLICY_MODEL_TRANSLATION_ERROR_INPUT_PARAM_NULL", new Object[0]);
    }

    public static String WSP_0043_POLICY_MODEL_TRANSLATION_ERROR_INPUT_PARAM_NULL() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0043_POLICY_MODEL_TRANSLATION_ERROR_INPUT_PARAM_NULL());
    }

    public static Localizable localizableWSP_0059_MULTIPLE_ATTRS_WITH_SAME_NAME_DETECTED_FOR_ASSERTION(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("WSP_0059_MULTIPLE_ATTRS_WITH_SAME_NAME_DETECTED_FOR_ASSERTION", new Object[]{arg0, arg1});
    }

    public static String WSP_0059_MULTIPLE_ATTRS_WITH_SAME_NAME_DETECTED_FOR_ASSERTION(Object arg0, Object arg1) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0059_MULTIPLE_ATTRS_WITH_SAME_NAME_DETECTED_FOR_ASSERTION(arg0, arg1));
    }

    public static Localizable localizableWSP_0070_ERROR_REGISTERING_ASSERTION_CREATOR(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0070_ERROR_REGISTERING_ASSERTION_CREATOR", new Object[]{arg0});
    }

    public static String WSP_0070_ERROR_REGISTERING_ASSERTION_CREATOR(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0070_ERROR_REGISTERING_ASSERTION_CREATOR(arg0));
    }

    public static Localizable localizableWSP_0016_UNABLE_TO_CLONE_POLICY_SOURCE_MODEL() {
        return MESSAGE_FACTORY.getMessage("WSP_0016_UNABLE_TO_CLONE_POLICY_SOURCE_MODEL", new Object[0]);
    }

    public static String WSP_0016_UNABLE_TO_CLONE_POLICY_SOURCE_MODEL() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0016_UNABLE_TO_CLONE_POLICY_SOURCE_MODEL());
    }

    public static Localizable localizableWSP_0065_INCONSISTENCY_IN_POLICY_SOURCE_MODEL(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0065_INCONSISTENCY_IN_POLICY_SOURCE_MODEL", new Object[]{arg0});
    }

    public static String WSP_0065_INCONSISTENCY_IN_POLICY_SOURCE_MODEL(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0065_INCONSISTENCY_IN_POLICY_SOURCE_MODEL(arg0));
    }

    public static Localizable localizableWSP_0037_PRIVATE_CONSTRUCTOR_DOES_NOT_TAKE_NULL() {
        return MESSAGE_FACTORY.getMessage("WSP_0037_PRIVATE_CONSTRUCTOR_DOES_NOT_TAKE_NULL", new Object[0]);
    }

    public static String WSP_0037_PRIVATE_CONSTRUCTOR_DOES_NOT_TAKE_NULL() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0037_PRIVATE_CONSTRUCTOR_DOES_NOT_TAKE_NULL());
    }

    public static Localizable localizableWSP_0061_METHOD_INVOCATION_FAILED(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("WSP_0061_METHOD_INVOCATION_FAILED", new Object[]{arg0, arg1, arg2});
    }

    public static String WSP_0061_METHOD_INVOCATION_FAILED(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0061_METHOD_INVOCATION_FAILED(arg0, arg1, arg2));
    }

    public static Localizable localizableWSP_0025_SPI_FAIL_SERVICE_MSG(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("WSP_0025_SPI_FAIL_SERVICE_MSG", new Object[]{arg0, arg1});
    }

    public static String WSP_0025_SPI_FAIL_SERVICE_MSG(Object arg0, Object arg1) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0025_SPI_FAIL_SERVICE_MSG(arg0, arg1));
    }

    public static Localizable localizableWSP_0032_SERVICE_CAN_NOT_BE_NULL() {
        return MESSAGE_FACTORY.getMessage("WSP_0032_SERVICE_CAN_NOT_BE_NULL", new Object[0]);
    }

    public static String WSP_0032_SERVICE_CAN_NOT_BE_NULL() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0032_SERVICE_CAN_NOT_BE_NULL());
    }

    public static Localizable localizableWSP_0001_UNSUPPORTED_MODEL_NODE_TYPE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0001_UNSUPPORTED_MODEL_NODE_TYPE", new Object[]{arg0});
    }

    public static String WSP_0001_UNSUPPORTED_MODEL_NODE_TYPE(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0001_UNSUPPORTED_MODEL_NODE_TYPE(arg0));
    }

    public static Localizable localizableWSP_0071_ERROR_MULTIPLE_ASSERTION_CREATORS_FOR_NAMESPACE(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("WSP_0071_ERROR_MULTIPLE_ASSERTION_CREATORS_FOR_NAMESPACE", new Object[]{arg0, arg1, arg2});
    }

    public static String WSP_0071_ERROR_MULTIPLE_ASSERTION_CREATORS_FOR_NAMESPACE(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0071_ERROR_MULTIPLE_ASSERTION_CREATORS_FOR_NAMESPACE(arg0, arg1, arg2));
    }

    public static Localizable localizableWSP_0020_SUBJECT_PARAM_MUST_NOT_BE_NULL() {
        return MESSAGE_FACTORY.getMessage("WSP_0020_SUBJECT_PARAM_MUST_NOT_BE_NULL", new Object[0]);
    }

    public static String WSP_0020_SUBJECT_PARAM_MUST_NOT_BE_NULL() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0020_SUBJECT_PARAM_MUST_NOT_BE_NULL());
    }

    public static Localizable localizableWSP_0031_SERVICE_PARAM_MUST_NOT_BE_NULL() {
        return MESSAGE_FACTORY.getMessage("WSP_0031_SERVICE_PARAM_MUST_NOT_BE_NULL", new Object[0]);
    }

    public static String WSP_0031_SERVICE_PARAM_MUST_NOT_BE_NULL() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0031_SERVICE_PARAM_MUST_NOT_BE_NULL());
    }

    public static Localizable localizableWSP_0011_UNABLE_TO_UNMARSHALL_POLICY_XML_ELEM_EXPECTED() {
        return MESSAGE_FACTORY.getMessage("WSP_0011_UNABLE_TO_UNMARSHALL_POLICY_XML_ELEM_EXPECTED", new Object[0]);
    }

    public static String WSP_0011_UNABLE_TO_UNMARSHALL_POLICY_XML_ELEM_EXPECTED() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0011_UNABLE_TO_UNMARSHALL_POLICY_XML_ELEM_EXPECTED());
    }

    public static Localizable localizableWSP_0086_FAILED_CREATE_READER(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0086_FAILED_CREATE_READER", new Object[]{arg0});
    }

    public static String WSP_0086_FAILED_CREATE_READER(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0086_FAILED_CREATE_READER(arg0));
    }

    public static Localizable localizableWSP_0063_ERROR_WHILE_CONSTRUCTING_EXCEPTION(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0063_ERROR_WHILE_CONSTRUCTING_EXCEPTION", new Object[]{arg0});
    }

    public static String WSP_0063_ERROR_WHILE_CONSTRUCTING_EXCEPTION(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0063_ERROR_WHILE_CONSTRUCTING_EXCEPTION(arg0));
    }

    public static Localizable localizableWSP_0039_POLICY_SRC_MODEL_INPUT_PARAMETER_MUST_NOT_BE_NULL() {
        return MESSAGE_FACTORY.getMessage("WSP_0039_POLICY_SRC_MODEL_INPUT_PARAMETER_MUST_NOT_BE_NULL", new Object[0]);
    }

    public static String WSP_0039_POLICY_SRC_MODEL_INPUT_PARAMETER_MUST_NOT_BE_NULL() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0039_POLICY_SRC_MODEL_INPUT_PARAMETER_MUST_NOT_BE_NULL());
    }

    public static Localizable localizableWSP_0046_POLICY_MAP_KEY_HANDLER_NOT_SET() {
        return MESSAGE_FACTORY.getMessage("WSP_0046_POLICY_MAP_KEY_HANDLER_NOT_SET", new Object[0]);
    }

    public static String WSP_0046_POLICY_MAP_KEY_HANDLER_NOT_SET() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0046_POLICY_MAP_KEY_HANDLER_NOT_SET());
    }

    public static Localizable localizableWSP_0055_NO_ALTERNATIVE_COMBINATIONS_CREATED() {
        return MESSAGE_FACTORY.getMessage("WSP_0055_NO_ALTERNATIVE_COMBINATIONS_CREATED", new Object[0]);
    }

    public static String WSP_0055_NO_ALTERNATIVE_COMBINATIONS_CREATED() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0055_NO_ALTERNATIVE_COMBINATIONS_CREATED());
    }

    public static Localizable localizableWSP_0062_INPUT_PARAMS_MUST_NOT_BE_NULL() {
        return MESSAGE_FACTORY.getMessage("WSP_0062_INPUT_PARAMS_MUST_NOT_BE_NULL", new Object[0]);
    }

    public static String WSP_0062_INPUT_PARAMS_MUST_NOT_BE_NULL() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0062_INPUT_PARAMS_MUST_NOT_BE_NULL());
    }

    public static Localizable localizableWSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0079_ERROR_WHILE_RFC2396_UNESCAPING", new Object[]{arg0});
    }

    public static String WSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(arg0));
    }

    public static Localizable localizableWSP_0023_UNEXPECTED_ERROR_WHILE_CLOSING_RESOURCE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0023_UNEXPECTED_ERROR_WHILE_CLOSING_RESOURCE", new Object[]{arg0});
    }

    public static String WSP_0023_UNEXPECTED_ERROR_WHILE_CLOSING_RESOURCE(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0023_UNEXPECTED_ERROR_WHILE_CLOSING_RESOURCE(arg0));
    }

    public static Localizable localizableWSP_0069_EXCEPTION_WHILE_RETRIEVING_EFFECTIVE_POLICY_FOR_KEY(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0069_EXCEPTION_WHILE_RETRIEVING_EFFECTIVE_POLICY_FOR_KEY", new Object[]{arg0});
    }

    public static String WSP_0069_EXCEPTION_WHILE_RETRIEVING_EFFECTIVE_POLICY_FOR_KEY(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0069_EXCEPTION_WHILE_RETRIEVING_EFFECTIVE_POLICY_FOR_KEY(arg0));
    }

    public static Localizable localizableWSP_0057_N_ALTERNATIVE_COMBINATIONS_M_POLICY_ALTERNATIVES_CREATED(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("WSP_0057_N_ALTERNATIVE_COMBINATIONS_M_POLICY_ALTERNATIVES_CREATED", new Object[]{arg0, arg1});
    }

    public static String WSP_0057_N_ALTERNATIVE_COMBINATIONS_M_POLICY_ALTERNATIVES_CREATED(Object arg0, Object arg1) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0057_N_ALTERNATIVE_COMBINATIONS_M_POLICY_ALTERNATIVES_CREATED(arg0, arg1));
    }

    public static Localizable localizableWSP_0022_STORAGE_TYPE_NOT_SUPPORTED(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0022_STORAGE_TYPE_NOT_SUPPORTED", new Object[]{arg0});
    }

    public static String WSP_0022_STORAGE_TYPE_NOT_SUPPORTED(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0022_STORAGE_TYPE_NOT_SUPPORTED(arg0));
    }

    public static Localizable localizableWSP_0056_NEITHER_NULL_NOR_EMPTY_POLICY_COLLECTION_EXPECTED() {
        return MESSAGE_FACTORY.getMessage("WSP_0056_NEITHER_NULL_NOR_EMPTY_POLICY_COLLECTION_EXPECTED", new Object[0]);
    }

    public static String WSP_0056_NEITHER_NULL_NOR_EMPTY_POLICY_COLLECTION_EXPECTED() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0056_NEITHER_NULL_NOR_EMPTY_POLICY_COLLECTION_EXPECTED());
    }

    public static Localizable localizableWSP_0095_INVALID_BOOLEAN_VALUE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0095_INVALID_BOOLEAN_VALUE", new Object[]{arg0});
    }

    public static String WSP_0095_INVALID_BOOLEAN_VALUE(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0095_INVALID_BOOLEAN_VALUE(arg0));
    }

    public static Localizable localizableWSP_0019_SUBOPTIMAL_ALTERNATIVE_SELECTED(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0019_SUBOPTIMAL_ALTERNATIVE_SELECTED", new Object[]{arg0});
    }

    public static String WSP_0019_SUBOPTIMAL_ALTERNATIVE_SELECTED(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0019_SUBOPTIMAL_ALTERNATIVE_SELECTED(arg0));
    }

    public static Localizable localizableWSP_0053_INVALID_CLIENT_SIDE_ALTERNATIVE() {
        return MESSAGE_FACTORY.getMessage("WSP_0053_INVALID_CLIENT_SIDE_ALTERNATIVE", new Object[0]);
    }

    public static String WSP_0053_INVALID_CLIENT_SIDE_ALTERNATIVE() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0053_INVALID_CLIENT_SIDE_ALTERNATIVE());
    }

    public static Localizable localizableWSP_0024_SPI_FAIL_SERVICE_URL_LINE_MSG(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("WSP_0024_SPI_FAIL_SERVICE_URL_LINE_MSG", new Object[]{arg0, arg1, arg2});
    }

    public static String WSP_0024_SPI_FAIL_SERVICE_URL_LINE_MSG(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0024_SPI_FAIL_SERVICE_URL_LINE_MSG(arg0, arg1, arg2));
    }

    public static Localizable localizableWSP_0092_CHARACTER_DATA_UNEXPECTED(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("WSP_0092_CHARACTER_DATA_UNEXPECTED", new Object[]{arg0, arg1, arg2});
    }

    public static String WSP_0092_CHARACTER_DATA_UNEXPECTED(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0092_CHARACTER_DATA_UNEXPECTED(arg0, arg1, arg2));
    }

    public static Localizable localizableWSP_0028_SERVICE_PROVIDER_COULD_NOT_BE_INSTANTIATED(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0028_SERVICE_PROVIDER_COULD_NOT_BE_INSTANTIATED", new Object[]{arg0});
    }

    public static String WSP_0028_SERVICE_PROVIDER_COULD_NOT_BE_INSTANTIATED(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0028_SERVICE_PROVIDER_COULD_NOT_BE_INSTANTIATED(arg0));
    }

    public static Localizable localizableWSP_0096_ERROR_WHILE_COMBINE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0096_ERROR_WHILE_COMBINE", new Object[]{arg0});
    }

    public static String WSP_0096_ERROR_WHILE_COMBINE(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0096_ERROR_WHILE_COMBINE(arg0));
    }

    public static Localizable localizableWSP_0034_REMOVE_OPERATION_NOT_SUPPORTED() {
        return MESSAGE_FACTORY.getMessage("WSP_0034_REMOVE_OPERATION_NOT_SUPPORTED", new Object[0]);
    }

    public static String WSP_0034_REMOVE_OPERATION_NOT_SUPPORTED() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0034_REMOVE_OPERATION_NOT_SUPPORTED());
    }

    public static Localizable localizableWSP_0030_SERVICE_PORT_OPERATION_FAULT_MSG_PARAM_MUST_NOT_BE_NULL(Object arg0, Object arg1, Object arg2, Object arg3) {
        return MESSAGE_FACTORY.getMessage("WSP_0030_SERVICE_PORT_OPERATION_FAULT_MSG_PARAM_MUST_NOT_BE_NULL", new Object[]{arg0, arg1, arg2, arg3});
    }

    public static String WSP_0030_SERVICE_PORT_OPERATION_FAULT_MSG_PARAM_MUST_NOT_BE_NULL(Object arg0, Object arg1, Object arg2, Object arg3) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0030_SERVICE_PORT_OPERATION_FAULT_MSG_PARAM_MUST_NOT_BE_NULL(arg0, arg1, arg2, arg3));
    }

    public static Localizable localizableWSP_0067_ILLEGAL_CFG_FILE_SYNTAX() {
        return MESSAGE_FACTORY.getMessage("WSP_0067_ILLEGAL_CFG_FILE_SYNTAX", new Object[0]);
    }

    public static String WSP_0067_ILLEGAL_CFG_FILE_SYNTAX() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0067_ILLEGAL_CFG_FILE_SYNTAX());
    }

    public static Localizable localizableWSP_0021_SUBJECT_AND_POLICY_PARAM_MUST_NOT_BE_NULL(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("WSP_0021_SUBJECT_AND_POLICY_PARAM_MUST_NOT_BE_NULL", new Object[]{arg0, arg1});
    }

    public static String WSP_0021_SUBJECT_AND_POLICY_PARAM_MUST_NOT_BE_NULL(Object arg0, Object arg1) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0021_SUBJECT_AND_POLICY_PARAM_MUST_NOT_BE_NULL(arg0, arg1));
    }

    public static Localizable localizableWSP_0051_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_ASSERTION_RELATED_NODE_TYPE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0051_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_ASSERTION_RELATED_NODE_TYPE", new Object[]{arg0});
    }

    public static String WSP_0051_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_ASSERTION_RELATED_NODE_TYPE(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0051_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_ASSERTION_RELATED_NODE_TYPE(arg0));
    }

    public static Localizable localizableWSP_0080_IMPLEMENTATION_EXPECTED_NOT_NULL() {
        return MESSAGE_FACTORY.getMessage("WSP_0080_IMPLEMENTATION_EXPECTED_NOT_NULL", new Object[0]);
    }

    public static String WSP_0080_IMPLEMENTATION_EXPECTED_NOT_NULL() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0080_IMPLEMENTATION_EXPECTED_NOT_NULL());
    }

    public static Localizable localizableWSP_0078_ASSERTION_CREATOR_DISCOVERED(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("WSP_0078_ASSERTION_CREATOR_DISCOVERED", new Object[]{arg0, arg1});
    }

    public static String WSP_0078_ASSERTION_CREATOR_DISCOVERED(Object arg0, Object arg1) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0078_ASSERTION_CREATOR_DISCOVERED(arg0, arg1));
    }

    public static Localizable localizableWSP_0088_FAILED_PARSE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0088_FAILED_PARSE", new Object[]{arg0});
    }

    public static String WSP_0088_FAILED_PARSE(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0088_FAILED_PARSE(arg0));
    }

    public static Localizable localizableWSP_0058_MULTIPLE_POLICY_IDS_NOT_ALLOWED() {
        return MESSAGE_FACTORY.getMessage("WSP_0058_MULTIPLE_POLICY_IDS_NOT_ALLOWED", new Object[0]);
    }

    public static String WSP_0058_MULTIPLE_POLICY_IDS_NOT_ALLOWED() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0058_MULTIPLE_POLICY_IDS_NOT_ALLOWED());
    }

    public static Localizable localizableWSP_0068_FAILED_TO_UNMARSHALL_POLICY_EXPRESSION() {
        return MESSAGE_FACTORY.getMessage("WSP_0068_FAILED_TO_UNMARSHALL_POLICY_EXPRESSION", new Object[0]);
    }

    public static String WSP_0068_FAILED_TO_UNMARSHALL_POLICY_EXPRESSION() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0068_FAILED_TO_UNMARSHALL_POLICY_EXPRESSION());
    }

    public static Localizable localizableWSP_0060_POLICY_ELEMENT_TYPE_UNKNOWN(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0060_POLICY_ELEMENT_TYPE_UNKNOWN", new Object[]{arg0});
    }

    public static String WSP_0060_POLICY_ELEMENT_TYPE_UNKNOWN(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0060_POLICY_ELEMENT_TYPE_UNKNOWN(arg0));
    }

    public static Localizable localizableWSP_0077_ASSERTION_CREATOR_DOES_NOT_SUPPORT_ANY_URI(Object arg0) {
        return MESSAGE_FACTORY.getMessage("WSP_0077_ASSERTION_CREATOR_DOES_NOT_SUPPORT_ANY_URI", new Object[]{arg0});
    }

    public static String WSP_0077_ASSERTION_CREATOR_DOES_NOT_SUPPORT_ANY_URI(Object arg0) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0077_ASSERTION_CREATOR_DOES_NOT_SUPPORT_ANY_URI(arg0));
    }

    public static Localizable localizableWSP_0081_UNABLE_TO_INSERT_CHILD(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("WSP_0081_UNABLE_TO_INSERT_CHILD", new Object[]{arg0, arg1});
    }

    public static String WSP_0081_UNABLE_TO_INSERT_CHILD(Object arg0, Object arg1) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0081_UNABLE_TO_INSERT_CHILD(arg0, arg1));
    }

    public static Localizable localizableWSP_0082_NO_SUBJECT_TYPE() {
        return MESSAGE_FACTORY.getMessage("WSP_0082_NO_SUBJECT_TYPE", new Object[0]);
    }

    public static String WSP_0082_NO_SUBJECT_TYPE() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0082_NO_SUBJECT_TYPE());
    }

    public static Localizable localizableWSP_0054_NO_MORE_ELEMS_IN_POLICY_MAP() {
        return MESSAGE_FACTORY.getMessage("WSP_0054_NO_MORE_ELEMS_IN_POLICY_MAP", new Object[0]);
    }

    public static String WSP_0054_NO_MORE_ELEMS_IN_POLICY_MAP() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0054_NO_MORE_ELEMS_IN_POLICY_MAP());
    }

    public static Localizable localizableWSP_0036_PRIVATE_METHOD_DOES_NOT_ACCEPT_NULL_OR_EMPTY_COLLECTION() {
        return MESSAGE_FACTORY.getMessage("WSP_0036_PRIVATE_METHOD_DOES_NOT_ACCEPT_NULL_OR_EMPTY_COLLECTION", new Object[0]);
    }

    public static String WSP_0036_PRIVATE_METHOD_DOES_NOT_ACCEPT_NULL_OR_EMPTY_COLLECTION() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0036_PRIVATE_METHOD_DOES_NOT_ACCEPT_NULL_OR_EMPTY_COLLECTION());
    }

    public static Localizable localizableWSP_0074_CANNOT_CREATE_ASSERTION_BAD_TYPE(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("WSP_0074_CANNOT_CREATE_ASSERTION_BAD_TYPE", new Object[]{arg0, arg1, arg2});
    }

    public static String WSP_0074_CANNOT_CREATE_ASSERTION_BAD_TYPE(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0074_CANNOT_CREATE_ASSERTION_BAD_TYPE(arg0, arg1, arg2));
    }

    public static Localizable localizableWSP_0072_DIGEST_MUST_NOT_BE_NULL_WHEN_ALG_DEFINED() {
        return MESSAGE_FACTORY.getMessage("WSP_0072_DIGEST_MUST_NOT_BE_NULL_WHEN_ALG_DEFINED", new Object[0]);
    }

    public static String WSP_0072_DIGEST_MUST_NOT_BE_NULL_WHEN_ALG_DEFINED() {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0072_DIGEST_MUST_NOT_BE_NULL_WHEN_ALG_DEFINED());
    }

    public static Localizable localizableWSP_0033_SERVICE_AND_PORT_PARAM_MUST_NOT_BE_NULL(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("WSP_0033_SERVICE_AND_PORT_PARAM_MUST_NOT_BE_NULL", new Object[]{arg0, arg1});
    }

    public static String WSP_0033_SERVICE_AND_PORT_PARAM_MUST_NOT_BE_NULL(Object arg0, Object arg1) {
        return LOCALIZER.localize(LocalizationMessages.localizableWSP_0033_SERVICE_AND_PORT_PARAM_MUST_NOT_BE_NULL(arg0, arg1));
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(LocalizationMessages.BUNDLE_NAME, locale);
        }
    }
}

