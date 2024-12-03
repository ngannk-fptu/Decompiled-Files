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

public final class XmlmessageMessages {
    private static final String BUNDLE_NAME = "com.sun.xml.ws.resources.xmlmessage";
    private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory("com.sun.xml.ws.resources.xmlmessage", (LocalizableMessageFactory.ResourceBundleSupplier)new BundleSupplier());
    private static final Localizer LOCALIZER = new Localizer();

    public static Localizable localizableXML_INVALID_CONTENT_TYPE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("xml.invalid.content-type", new Object[]{arg0});
    }

    public static String XML_INVALID_CONTENT_TYPE(Object arg0) {
        return LOCALIZER.localize(XmlmessageMessages.localizableXML_INVALID_CONTENT_TYPE(arg0));
    }

    public static Localizable localizableXML_GET_SOURCE_ERR() {
        return MESSAGE_FACTORY.getMessage("xml.get.source.err", new Object[0]);
    }

    public static String XML_GET_SOURCE_ERR() {
        return LOCALIZER.localize(XmlmessageMessages.localizableXML_GET_SOURCE_ERR());
    }

    public static Localizable localizableXML_UNKNOWN_CONTENT_TYPE() {
        return MESSAGE_FACTORY.getMessage("xml.unknown.Content-Type", new Object[0]);
    }

    public static String XML_UNKNOWN_CONTENT_TYPE() {
        return LOCALIZER.localize(XmlmessageMessages.localizableXML_UNKNOWN_CONTENT_TYPE());
    }

    public static Localizable localizableXML_SET_PAYLOAD_ERR() {
        return MESSAGE_FACTORY.getMessage("xml.set.payload.err", new Object[0]);
    }

    public static String XML_SET_PAYLOAD_ERR() {
        return LOCALIZER.localize(XmlmessageMessages.localizableXML_SET_PAYLOAD_ERR());
    }

    public static Localizable localizableXML_ROOT_PART_INVALID_CONTENT_TYPE(Object arg0) {
        return MESSAGE_FACTORY.getMessage("xml.root.part.invalid.Content-Type", new Object[]{arg0});
    }

    public static String XML_ROOT_PART_INVALID_CONTENT_TYPE(Object arg0) {
        return LOCALIZER.localize(XmlmessageMessages.localizableXML_ROOT_PART_INVALID_CONTENT_TYPE(arg0));
    }

    public static Localizable localizableXML_GET_DS_ERR() {
        return MESSAGE_FACTORY.getMessage("xml.get.ds.err", new Object[0]);
    }

    public static String XML_GET_DS_ERR() {
        return LOCALIZER.localize(XmlmessageMessages.localizableXML_GET_DS_ERR());
    }

    public static Localizable localizableXML_CANNOT_INTERNALIZE_MESSAGE() {
        return MESSAGE_FACTORY.getMessage("xml.cannot.internalize.message", new Object[0]);
    }

    public static String XML_CANNOT_INTERNALIZE_MESSAGE() {
        return LOCALIZER.localize(XmlmessageMessages.localizableXML_CANNOT_INTERNALIZE_MESSAGE());
    }

    public static Localizable localizableXML_CONTENT_TYPE_PARSE_ERR() {
        return MESSAGE_FACTORY.getMessage("xml.Content-Type.parse.err", new Object[0]);
    }

    public static String XML_CONTENT_TYPE_PARSE_ERR() {
        return LOCALIZER.localize(XmlmessageMessages.localizableXML_CONTENT_TYPE_PARSE_ERR());
    }

    public static Localizable localizableXML_NULL_HEADERS() {
        return MESSAGE_FACTORY.getMessage("xml.null.headers", new Object[0]);
    }

    public static String XML_NULL_HEADERS() {
        return LOCALIZER.localize(XmlmessageMessages.localizableXML_NULL_HEADERS());
    }

    public static Localizable localizableXML_NO_CONTENT_TYPE() {
        return MESSAGE_FACTORY.getMessage("xml.no.Content-Type", new Object[0]);
    }

    public static String XML_NO_CONTENT_TYPE() {
        return LOCALIZER.localize(XmlmessageMessages.localizableXML_NO_CONTENT_TYPE());
    }

    public static Localizable localizableXML_CONTENT_TYPE_MUSTBE_MULTIPART() {
        return MESSAGE_FACTORY.getMessage("xml.content-type.mustbe.multipart", new Object[0]);
    }

    public static String XML_CONTENT_TYPE_MUSTBE_MULTIPART() {
        return LOCALIZER.localize(XmlmessageMessages.localizableXML_CONTENT_TYPE_MUSTBE_MULTIPART());
    }

    private static class BundleSupplier
    implements LocalizableMessageFactory.ResourceBundleSupplier {
        private BundleSupplier() {
        }

        public ResourceBundle getResourceBundle(Locale locale) {
            return ResourceBundle.getBundle(XmlmessageMessages.BUNDLE_NAME, locale);
        }
    }
}

