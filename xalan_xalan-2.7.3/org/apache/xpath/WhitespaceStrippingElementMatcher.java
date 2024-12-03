/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath;

import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathContext;
import org.w3c.dom.Element;

public interface WhitespaceStrippingElementMatcher {
    public boolean shouldStripWhiteSpace(XPathContext var1, Element var2) throws TransformerException;

    public boolean canStripWhiteSpace();
}

