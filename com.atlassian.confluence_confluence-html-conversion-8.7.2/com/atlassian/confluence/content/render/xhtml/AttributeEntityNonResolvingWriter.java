/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.QName
 *  org.apache.xerces.xni.XMLAttributes
 *  org.apache.xerces.xni.XNIException
 *  org.cyberneko.html.filters.Writer
 */
package com.atlassian.confluence.content.render.xhtml;

import java.io.Writer;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XNIException;

class AttributeEntityNonResolvingWriter
extends org.cyberneko.html.filters.Writer {
    public AttributeEntityNonResolvingWriter(Writer writer, String encoding) {
        super(writer, encoding);
    }

    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        this.fSeenRootElement = true;
        this.printStartElement(element, attributes, true);
    }

    protected void printStartElement(QName element, XMLAttributes attributes) {
        this.printStartElement(element, attributes, false);
    }

    protected void printStartElement(QName element, XMLAttributes attributes, boolean emptyElement) {
        int contentIndex = -1;
        String originalContent = null;
        if (element.rawname.toLowerCase().equals("meta")) {
            String httpEquiv = null;
            int length = attributes.getLength();
            for (int i = 0; i < length; ++i) {
                String aname = attributes.getQName(i).toLowerCase();
                if (aname.equals("http-equiv")) {
                    httpEquiv = attributes.getValue(i);
                    continue;
                }
                if (!aname.equals("content")) continue;
                contentIndex = i;
            }
            if (httpEquiv != null && httpEquiv.toLowerCase().equals("content-type")) {
                this.fSeenHttpEquiv = true;
                Object content = null;
                if (contentIndex != -1) {
                    originalContent = attributes.getValue(contentIndex);
                    content = originalContent.toLowerCase();
                }
                if (content != null) {
                    int charsetIndex = ((String)content).indexOf("charset=");
                    content = charsetIndex != -1 ? ((String)content).substring(0, charsetIndex + 8) : (String)content + ";charset=";
                    content = (String)content + this.fEncoding;
                    attributes.setValue(contentIndex, (String)content);
                }
            }
        }
        this.fPrinter.print('<');
        this.fPrinter.print(element.rawname);
        int attrCount = attributes != null ? attributes.getLength() : 0;
        for (int i = 0; i < attrCount; ++i) {
            String aname = attributes.getQName(i);
            String avalue = attributes.getNonNormalizedValue(i);
            this.fPrinter.print(' ');
            this.fPrinter.print(aname);
            this.fPrinter.print("=\"");
            this.printAttributeValue(avalue);
            this.fPrinter.print('\"');
        }
        if (emptyElement) {
            this.fPrinter.print('/');
        }
        this.fPrinter.print('>');
        this.fPrinter.flush();
        if (contentIndex != -1) {
            attributes.setValue(contentIndex, originalContent);
        }
    }
}

