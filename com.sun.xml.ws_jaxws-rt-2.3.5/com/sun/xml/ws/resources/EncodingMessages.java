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

public final class EncodingMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.encoding";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.encoding", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableEXCEPTION_NOTFOUND(Object arg0) {
        return MESSAGE_FACTORY.getMessage("exception.notfound", new Object[]{arg0});
    }

    public static String EXCEPTION_NOTFOUND(Object arg0) {
        return LOCALIZER.localize(EncodingMessages.localizableEXCEPTION_NOTFOUND(arg0));
    }

    public static Localizable localizableXSD_UNKNOWN_PREFIX(Object arg0) {
        return MESSAGE_FACTORY.getMessage("xsd.unknownPrefix", new Object[]{arg0});
    }

    public static String XSD_UNKNOWN_PREFIX(Object arg0) {
        return LOCALIZER.localize(EncodingMessages.localizableXSD_UNKNOWN_PREFIX(arg0));
    }

    public static Localizable localizableNESTED_ENCODING_ERROR(Object arg0) {
        return MESSAGE_FACTORY.getMessage("nestedEncodingError", new Object[]{arg0});
    }

    public static String NESTED_ENCODING_ERROR(Object arg0) {
        return LOCALIZER.localize(EncodingMessages.localizableNESTED_ENCODING_ERROR(arg0));
    }

    public static Localizable localizableNESTED_DESERIALIZATION_ERROR(Object arg0) {
        return MESSAGE_FACTORY.getMessage("nestedDeserializationError", new Object[]{arg0});
    }

    public static String NESTED_DESERIALIZATION_ERROR(Object arg0) {
        return LOCALIZER.localize(EncodingMessages.localizableNESTED_DESERIALIZATION_ERROR(arg0));
    }

    public static Localizable localizableXSD_UNEXPECTED_ELEMENT_NAME(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("xsd.unexpectedElementName", new Object[]{arg0, arg1});
    }

    public static String XSD_UNEXPECTED_ELEMENT_NAME(Object arg0, Object arg1) {
        return LOCALIZER.localize(EncodingMessages.localizableXSD_UNEXPECTED_ELEMENT_NAME(arg0, arg1));
    }

    public static Localizable localizableFAILED_TO_READ_RESPONSE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("failed.to.read.response", new Object[]{arg0});
    }

    public static String FAILED_TO_READ_RESPONSE(Object arg0) {
        return LOCALIZER.localize(EncodingMessages.localizableFAILED_TO_READ_RESPONSE(arg0));
    }

    public static Localizable localizableNESTED_SERIALIZATION_ERROR(Object arg0) {
        return MESSAGE_FACTORY.getMessage("nestedSerializationError", new Object[]{arg0});
    }

    public static String NESTED_SERIALIZATION_ERROR(Object arg0) {
        return LOCALIZER.localize(EncodingMessages.localizableNESTED_SERIALIZATION_ERROR(arg0));
    }

    public static Localizable localizableNO_SUCH_CONTENT_ID(Object arg0) {
        return MESSAGE_FACTORY.getMessage("noSuchContentId", new Object[]{arg0});
    }

    public static String NO_SUCH_CONTENT_ID(Object arg0) {
        return LOCALIZER.localize(EncodingMessages.localizableNO_SUCH_CONTENT_ID(arg0));
    }

    public static Localizable localizableEXCEPTION_INCORRECT_TYPE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("exception.incorrectType", new Object[]{arg0});
    }

    public static String EXCEPTION_INCORRECT_TYPE(Object arg0) {
        return LOCALIZER.localize(EncodingMessages.localizableEXCEPTION_INCORRECT_TYPE(arg0));
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(EncodingMessages.BUNDLE_NAME, locale);
        }
    }
}

