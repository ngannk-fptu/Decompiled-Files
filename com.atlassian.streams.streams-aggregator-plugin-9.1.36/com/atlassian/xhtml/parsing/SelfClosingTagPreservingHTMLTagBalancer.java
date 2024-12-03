/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.xhtml.parsing;

import org.htmlunit.cyberneko.CustomizableHTMLTagBalancer;
import org.htmlunit.cyberneko.HTMLConfiguration;
import org.htmlunit.cyberneko.xerces.xni.Augmentations;
import org.htmlunit.cyberneko.xerces.xni.QName;
import org.htmlunit.cyberneko.xerces.xni.XMLAttributes;
import org.htmlunit.cyberneko.xerces.xni.XNIException;

public class SelfClosingTagPreservingHTMLTagBalancer
extends CustomizableHTMLTagBalancer {
    public SelfClosingTagPreservingHTMLTagBalancer(HTMLConfiguration htmlConfiguration) {
        super(htmlConfiguration);
    }

    @Override
    public void emptyElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        super.emptyElement(element, attrs, augs);
    }
}

