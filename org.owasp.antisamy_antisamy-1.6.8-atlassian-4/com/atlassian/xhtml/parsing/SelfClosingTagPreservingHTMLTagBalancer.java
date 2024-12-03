/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sourceforge.htmlunit.cyberneko.HTMLConfiguration
 *  net.sourceforge.htmlunit.cyberneko.HTMLElements$Element
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.QName
 *  org.apache.xerces.xni.XMLAttributes
 *  org.apache.xerces.xni.XNIException
 */
package com.atlassian.xhtml.parsing;

import net.sourceforge.htmlunit.cyberneko.CustomizableHTMLTagBalancer;
import net.sourceforge.htmlunit.cyberneko.HTMLConfiguration;
import net.sourceforge.htmlunit.cyberneko.HTMLElements;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XNIException;

public class SelfClosingTagPreservingHTMLTagBalancer
extends CustomizableHTMLTagBalancer {
    public SelfClosingTagPreservingHTMLTagBalancer(HTMLConfiguration htmlConfiguration) {
        super(htmlConfiguration);
    }

    public void emptyElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        if (augs == null) {
            this.startElement(element, attrs, augs);
            HTMLElements.Element elem = this.getElement(element);
            if (elem.isEmpty() || this.fAllowSelfclosingTags || elem.code == 134 || elem.code == 56 && this.fAllowSelfclosingIframe) {
                this.endElement(element, augs);
            }
        } else {
            this.fDocumentHandler.emptyElement(element, attrs, augs);
        }
    }
}

