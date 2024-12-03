/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.QName
 *  org.apache.xerces.xni.XMLAttributes
 *  org.apache.xerces.xni.XNIException
 *  org.cyberneko.html.filters.DefaultFilter
 */
package com.atlassian.confluence.content.render.xhtml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.filters.DefaultFilter;

public class XmlAttributeEncodingFilter
extends DefaultFilter {
    private static final Pattern ENTITY_PATTERN = Pattern.compile("\\&#?\\w+;");

    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        XmlAttributeEncodingFilter.handleElementAttributes(attributes);
        super.startElement(element, attributes, augs);
    }

    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        XmlAttributeEncodingFilter.handleElementAttributes(attributes);
        super.emptyElement(element, attributes, augs);
    }

    private static void handleElementAttributes(XMLAttributes attributes) {
        if (attributes.getLength() > 0) {
            for (int i = 0; i < attributes.getLength(); ++i) {
                String value = attributes.getNonNormalizedValue(i);
                if (!StringUtils.isNotBlank((CharSequence)value)) continue;
                attributes.setValue(i, XmlAttributeEncodingFilter.encode(value));
            }
        }
    }

    private static String encode(String value) {
        StringBuilder encodedValue = new StringBuilder();
        Matcher matcher = ENTITY_PATTERN.matcher(value);
        int previousMatchPosition = 0;
        while (matcher.find()) {
            encodedValue.append(value.substring(previousMatchPosition, matcher.start()).replaceAll("&", "&amp;"));
            encodedValue.append(matcher.group());
            previousMatchPosition = matcher.end();
        }
        if (previousMatchPosition < value.length()) {
            encodedValue.append(value.substring(previousMatchPosition).replaceAll("&", "&amp;"));
        }
        return StringUtils.replaceEach((String)encodedValue.toString(), (String[])new String[]{"<", ">"}, (String[])new String[]{"&lt;", "&gt;"});
    }
}

