/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.QName
 *  org.apache.xerces.xni.XMLAttributes
 *  org.apache.xerces.xni.XMLString
 *  org.apache.xerces.xni.XNIException
 *  org.cyberneko.html.filters.DefaultFilter
 */
package com.atlassian.confluence.content.render.xhtml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.filters.DefaultFilter;

class ScriptWrappingFilter
extends DefaultFilter {
    private static final Pattern CDATA_START = Pattern.compile("(\\s*//)?\\s*<!\\[CDATA\\[");
    private boolean startScriptElement = false;
    private boolean scriptEndWrapRequired = false;

    ScriptWrappingFilter() {
    }

    public void endElement(QName element, Augmentations augs) throws XNIException {
        if (ScriptWrappingFilter.isScript(element)) {
            if (this.scriptEndWrapRequired) {
                this.endScriptWrapping(augs);
            }
            this.setNoWrappingRequired();
        }
        super.endElement(element, augs);
    }

    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        if (ScriptWrappingFilter.isScript(element)) {
            this.startScriptElement = true;
        }
        super.startElement(element, attributes, augs);
    }

    public void characters(XMLString text, Augmentations augs) throws XNIException {
        if (this.startScriptElement) {
            Matcher matcher = CDATA_START.matcher(text.toString());
            if (matcher.find() && matcher.start() == 0) {
                this.setNoWrappingRequired();
            } else {
                this.startScriptWrapping(augs);
            }
        }
        super.characters(text, augs);
    }

    public void startCDATA(Augmentations augs) throws XNIException {
        if (this.startScriptElement) {
            this.setNoWrappingRequired();
        }
        super.startCDATA(augs);
    }

    private static boolean isScript(QName element) {
        return "script".equalsIgnoreCase(element.localpart);
    }

    private void startScriptWrapping(Augmentations augs) {
        super.characters(new XMLString(new char[]{'/', '/', '<', '!', '[', 'C', 'D', 'A', 'T', 'A', '[', '\n'}, 0, 12), augs);
        this.scriptEndWrapRequired = true;
    }

    private void endScriptWrapping(Augmentations augs) {
        super.characters(new XMLString(new char[]{'\n', '/', '/', ']', ']', '>', '\n'}, 0, 7), augs);
    }

    private void setNoWrappingRequired() {
        this.startScriptElement = false;
        this.scriptEndWrapRequired = false;
    }
}

