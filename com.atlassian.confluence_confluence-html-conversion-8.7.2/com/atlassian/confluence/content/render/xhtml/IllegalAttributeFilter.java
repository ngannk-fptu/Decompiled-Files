/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ctc.wstx.io.WstxInputData
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.QName
 *  org.apache.xerces.xni.XMLAttributes
 *  org.apache.xerces.xni.XNIException
 *  org.cyberneko.html.filters.DefaultFilter
 */
package com.atlassian.confluence.content.render.xhtml;

import com.ctc.wstx.io.WstxInputData;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.filters.DefaultFilter;

public class IllegalAttributeFilter
extends DefaultFilter {
    private static final boolean XML_11 = false;
    private static final boolean NS_AWARE = false;

    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        super.startElement(element, this.filter(attributes), augs);
    }

    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        super.emptyElement(element, this.filter(attributes), augs);
    }

    private XMLAttributes filter(XMLAttributes attributes) {
        int i = 0;
        while (i < attributes.getLength()) {
            String localName = attributes.getLocalName(i);
            if (localName.length() > 0 && WstxInputData.findIllegalNameChar((String)localName, (boolean)false, (boolean)false) != -1) {
                attributes.removeAttributeAt(i);
                continue;
            }
            ++i;
        }
        return attributes;
    }
}

