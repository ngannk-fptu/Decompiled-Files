/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.QName
 *  org.apache.xerces.xni.XMLAttributes
 *  org.apache.xerces.xni.XMLString
 *  org.apache.xerces.xni.XNIException
 *  org.apache.xerces.xni.parser.XMLComponentManager
 *  org.apache.xerces.xni.parser.XMLConfigurationException
 *  org.cyberneko.html.filters.DefaultFilter
 */
package com.atlassian.confluence.extra.flyingpdf.html;

import com.atlassian.confluence.extra.flyingpdf.html.LinkFixer;
import java.util.Stack;
import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.cyberneko.html.filters.DefaultFilter;

public class ConfluenceHtmlToXmlFilter
extends DefaultFilter {
    private Stack<QName> styleStack = new Stack();
    private StringBuffer collectedStyles;
    private LinkFixer linkFixer;
    private boolean insideStyle = false;

    public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
        this.collectedStyles = (StringBuffer)componentManager.getProperty("http://atlassian.com/html/properties/stylecollector");
        this.linkFixer = (LinkFixer)componentManager.getProperty("http://atlassian.com/html/properties/linkfixer");
        super.reset(componentManager);
    }

    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        element.localpart = element.localpart == null ? null : element.localpart.toLowerCase();
        element.prefix = element.prefix == null ? null : element.prefix.toLowerCase();
        element.uri = element.uri == null ? null : element.uri.toLowerCase();
        String string = element.rawname = element.rawname == null ? null : element.rawname.toLowerCase();
        if ("img".equals(element.localpart)) {
            String attrName = "border";
            String formatStr = "border-style:solid;border-width:%1$s;";
            this.moveAttributeToStyle(attributes, attrName, formatStr);
            this.moveAttributeToStyle(attributes, "height", "height:%1$s;");
            this.moveAttributeToStyle(attributes, "width", "width:%1$s;");
        }
        super.emptyElement(element, attributes, augs);
    }

    public void endElement(QName element, Augmentations augs) throws XNIException {
        element.localpart = element.localpart == null ? null : element.localpart.toLowerCase();
        element.prefix = element.prefix == null ? null : element.prefix.toLowerCase();
        element.uri = element.uri == null ? null : element.uri.toLowerCase();
        String string = element.rawname = element.rawname == null ? null : element.rawname.toLowerCase();
        if ("style".equals(element.localpart)) {
            if (!this.styleStack.isEmpty()) {
                this.styleStack.pop();
            }
            this.insideStyle = !this.styleStack.isEmpty();
        }
        super.endElement(element, augs);
    }

    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        String newHref;
        String href;
        int hrefIdx;
        element.localpart = element.localpart == null ? null : element.localpart.toLowerCase();
        element.prefix = element.prefix == null ? null : element.prefix.toLowerCase();
        element.uri = element.uri == null ? null : element.uri.toLowerCase();
        String string = element.rawname = element.rawname == null ? null : element.rawname.toLowerCase();
        if ("font".equals(element.localpart)) {
            String attrName = "color";
            this.moveAttributeToStyle(attributes, attrName, "color:%1$s;");
        } else if (element.localpart.equals("style")) {
            String id = attributes.getValue("id");
            if (id == null || !id.equals("confluence.flyingpdf.styleId")) {
                this.styleStack.push(element);
                this.insideStyle = true;
            }
        } else if (element.localpart.equals("a") && (hrefIdx = attributes.getIndex("href")) != -1 && !StringUtils.isBlank((CharSequence)(href = attributes.getValue(hrefIdx))) && (newHref = this.linkFixer.convertLink(href)) != null) {
            attributes.setValue(hrefIdx, newHref);
        }
        super.startElement(element, attributes, augs);
    }

    private void moveAttributeToStyle(XMLAttributes attributes, String attrName, String formatStr) {
        String attrValue = attributes.getValue(attrName);
        if (!StringUtils.isEmpty((CharSequence)attrValue)) {
            Object newStyle = String.format(formatStr, attrValue);
            int styleIdx = attributes.getIndex("style");
            if (styleIdx != -1) {
                String style = attributes.getValue(styleIdx);
                newStyle = (String)newStyle + style;
                attributes.setValue(styleIdx, (String)newStyle);
            } else {
                attributes.addAttribute(new QName(null, "style", "style", null), "CDATA", (String)newStyle);
            }
        }
    }

    public void characters(XMLString text, Augmentations augs) throws XNIException {
        if (this.insideStyle) {
            this.collectedStyles.append(text.toString()).append("\r\n");
        } else {
            super.characters(text, augs);
        }
    }

    public StringBuffer getCollectedStyles() {
        return this.collectedStyles;
    }
}

