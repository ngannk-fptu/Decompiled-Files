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

public final class StreamingMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.streaming";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.streaming", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableXMLRECORDER_RECORDING_ENDED() {
        return MESSAGE_FACTORY.getMessage("xmlrecorder.recording.ended", new Object[0]);
    }

    public static String XMLRECORDER_RECORDING_ENDED() {
        return LOCALIZER.localize(StreamingMessages.localizableXMLRECORDER_RECORDING_ENDED());
    }

    public static Localizable localizableXMLREADER_NESTED_ERROR(Object arg0) {
        return MESSAGE_FACTORY.getMessage("xmlreader.nestedError", new Object[]{arg0});
    }

    public static String XMLREADER_NESTED_ERROR(Object arg0) {
        return LOCALIZER.localize(StreamingMessages.localizableXMLREADER_NESTED_ERROR(arg0));
    }

    public static Localizable localizableWOODSTOX_CANT_LOAD(Object arg0) {
        return MESSAGE_FACTORY.getMessage("woodstox.cant.load", new Object[]{arg0});
    }

    public static String WOODSTOX_CANT_LOAD(Object arg0) {
        return LOCALIZER.localize(StreamingMessages.localizableWOODSTOX_CANT_LOAD(arg0));
    }

    public static Localizable localizableSOURCEREADER_INVALID_SOURCE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("sourcereader.invalidSource", new Object[]{arg0});
    }

    public static String SOURCEREADER_INVALID_SOURCE(Object arg0) {
        return LOCALIZER.localize(StreamingMessages.localizableSOURCEREADER_INVALID_SOURCE(arg0));
    }

    public static Localizable localizableXMLWRITER_NO_PREFIX_FOR_URI(Object arg0) {
        return MESSAGE_FACTORY.getMessage("xmlwriter.noPrefixForURI", new Object[]{arg0});
    }

    public static String XMLWRITER_NO_PREFIX_FOR_URI(Object arg0) {
        return LOCALIZER.localize(StreamingMessages.localizableXMLWRITER_NO_PREFIX_FOR_URI(arg0));
    }

    public static Localizable localizableSTREAMING_PARSE_EXCEPTION(Object arg0) {
        return MESSAGE_FACTORY.getMessage("streaming.parseException", new Object[]{arg0});
    }

    public static String STREAMING_PARSE_EXCEPTION(Object arg0) {
        return LOCALIZER.localize(StreamingMessages.localizableSTREAMING_PARSE_EXCEPTION(arg0));
    }

    public static Localizable localizableXMLREADER_ILLEGAL_STATE_ENCOUNTERED(Object arg0) {
        return MESSAGE_FACTORY.getMessage("xmlreader.illegalStateEncountered", new Object[]{arg0});
    }

    public static String XMLREADER_ILLEGAL_STATE_ENCOUNTERED(Object arg0) {
        return LOCALIZER.localize(StreamingMessages.localizableXMLREADER_ILLEGAL_STATE_ENCOUNTERED(arg0));
    }

    public static Localizable localizableSTAX_CANT_CREATE() {
        return MESSAGE_FACTORY.getMessage("stax.cantCreate", new Object[0]);
    }

    public static String STAX_CANT_CREATE() {
        return LOCALIZER.localize(StreamingMessages.localizableSTAX_CANT_CREATE());
    }

    public static Localizable localizableSTAXREADER_XMLSTREAMEXCEPTION(Object arg0) {
        return MESSAGE_FACTORY.getMessage("staxreader.xmlstreamexception", new Object[]{arg0});
    }

    public static String STAXREADER_XMLSTREAMEXCEPTION(Object arg0) {
        return LOCALIZER.localize(StreamingMessages.localizableSTAXREADER_XMLSTREAMEXCEPTION(arg0));
    }

    public static Localizable localizableXMLREADER_UNEXPECTED_STATE(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("xmlreader.unexpectedState", new Object[]{arg0, arg1});
    }

    public static String XMLREADER_UNEXPECTED_STATE(Object arg0, Object arg1) {
        return LOCALIZER.localize(StreamingMessages.localizableXMLREADER_UNEXPECTED_STATE(arg0, arg1));
    }

    public static Localizable localizableFASTINFOSET_EXCEPTION() {
        return MESSAGE_FACTORY.getMessage("fastinfoset.exception", new Object[0]);
    }

    public static String FASTINFOSET_EXCEPTION() {
        return LOCALIZER.localize(StreamingMessages.localizableFASTINFOSET_EXCEPTION());
    }

    public static Localizable localizableXMLWRITER_NESTED_ERROR(Object arg0) {
        return MESSAGE_FACTORY.getMessage("xmlwriter.nestedError", new Object[]{arg0});
    }

    public static String XMLWRITER_NESTED_ERROR(Object arg0) {
        return LOCALIZER.localize(StreamingMessages.localizableXMLWRITER_NESTED_ERROR(arg0));
    }

    public static Localizable localizableXMLREADER_PARSE_EXCEPTION(Object arg0) {
        return MESSAGE_FACTORY.getMessage("xmlreader.parseException", new Object[]{arg0});
    }

    public static String XMLREADER_PARSE_EXCEPTION(Object arg0) {
        return LOCALIZER.localize(StreamingMessages.localizableXMLREADER_PARSE_EXCEPTION(arg0));
    }

    public static Localizable localizableXMLREADER_UNEXPECTED_STATE_MESSAGE(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("xmlreader.unexpectedState.message", new Object[]{arg0, arg1, arg2});
    }

    public static String XMLREADER_UNEXPECTED_STATE_MESSAGE(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(StreamingMessages.localizableXMLREADER_UNEXPECTED_STATE_MESSAGE(arg0, arg1, arg2));
    }

    public static Localizable localizableXMLREADER_UNEXPECTED_STATE_TAG(Object arg0, Object arg1) {
        return MESSAGE_FACTORY.getMessage("xmlreader.unexpectedState.tag", new Object[]{arg0, arg1});
    }

    public static String XMLREADER_UNEXPECTED_STATE_TAG(Object arg0, Object arg1) {
        return LOCALIZER.localize(StreamingMessages.localizableXMLREADER_UNEXPECTED_STATE_TAG(arg0, arg1));
    }

    public static Localizable localizableFASTINFOSET_NO_IMPLEMENTATION() {
        return MESSAGE_FACTORY.getMessage("fastinfoset.noImplementation", new Object[0]);
    }

    public static String FASTINFOSET_NO_IMPLEMENTATION() {
        return LOCALIZER.localize(StreamingMessages.localizableFASTINFOSET_NO_IMPLEMENTATION());
    }

    public static Localizable localizableINVALID_PROPERTY_VALUE_INTEGER(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("invalid.property.value.integer", new Object[]{arg0, arg1, arg2});
    }

    public static String INVALID_PROPERTY_VALUE_INTEGER(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(StreamingMessages.localizableINVALID_PROPERTY_VALUE_INTEGER(arg0, arg1, arg2));
    }

    public static Localizable localizableXMLREADER_IO_EXCEPTION(Object arg0) {
        return MESSAGE_FACTORY.getMessage("xmlreader.ioException", new Object[]{arg0});
    }

    public static String XMLREADER_IO_EXCEPTION(Object arg0) {
        return LOCALIZER.localize(StreamingMessages.localizableXMLREADER_IO_EXCEPTION(arg0));
    }

    public static Localizable localizableFASTINFOSET_DECODING_NOT_ACCEPTED() {
        return MESSAGE_FACTORY.getMessage("fastinfoset.decodingNotAccepted", new Object[0]);
    }

    public static String FASTINFOSET_DECODING_NOT_ACCEPTED() {
        return LOCALIZER.localize(StreamingMessages.localizableFASTINFOSET_DECODING_NOT_ACCEPTED());
    }

    public static Localizable localizableINVALID_PROPERTY_VALUE_LONG(Object arg0, Object arg1, Object arg2) {
        return MESSAGE_FACTORY.getMessage("invalid.property.value.long", new Object[]{arg0, arg1, arg2});
    }

    public static String INVALID_PROPERTY_VALUE_LONG(Object arg0, Object arg1, Object arg2) {
        return LOCALIZER.localize(StreamingMessages.localizableINVALID_PROPERTY_VALUE_LONG(arg0, arg1, arg2));
    }

    public static Localizable localizableXMLREADER_UNEXPECTED_CHARACTER_CONTENT(Object arg0) {
        return MESSAGE_FACTORY.getMessage("xmlreader.unexpectedCharacterContent", new Object[]{arg0});
    }

    public static String XMLREADER_UNEXPECTED_CHARACTER_CONTENT(Object arg0) {
        return LOCALIZER.localize(StreamingMessages.localizableXMLREADER_UNEXPECTED_CHARACTER_CONTENT(arg0));
    }

    public static Localizable localizableSTREAMING_IO_EXCEPTION(Object arg0) {
        return MESSAGE_FACTORY.getMessage("streaming.ioException", new Object[]{arg0});
    }

    public static String STREAMING_IO_EXCEPTION(Object arg0) {
        return LOCALIZER.localize(StreamingMessages.localizableSTREAMING_IO_EXCEPTION(arg0));
    }

    public static Localizable localizableXMLWRITER_IO_EXCEPTION(Object arg0) {
        return MESSAGE_FACTORY.getMessage("xmlwriter.ioException", new Object[]{arg0});
    }

    public static String XMLWRITER_IO_EXCEPTION(Object arg0) {
        return LOCALIZER.localize(StreamingMessages.localizableXMLWRITER_IO_EXCEPTION(arg0));
    }

    public static Localizable localizableFASTINFOSET_ENABLED() {
        return MESSAGE_FACTORY.getMessage("fastinfoset.enabled", new Object[0]);
    }

    public static String FASTINFOSET_ENABLED() {
        return LOCALIZER.localize(StreamingMessages.localizableFASTINFOSET_ENABLED());
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(StreamingMessages.BUNDLE_NAME, locale);
        }
    }
}

