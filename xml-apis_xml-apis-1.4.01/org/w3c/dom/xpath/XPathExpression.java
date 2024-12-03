/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.xpath;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathException;

public interface XPathExpression {
    public Object evaluate(Node var1, short var2, Object var3) throws XPathException, DOMException;
}

