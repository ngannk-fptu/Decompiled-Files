/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.xpath;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathException;
import org.w3c.dom.xpath.XPathExpression;
import org.w3c.dom.xpath.XPathNSResolver;

public interface XPathEvaluator {
    public XPathExpression createExpression(String var1, XPathNSResolver var2) throws XPathException, DOMException;

    public XPathNSResolver createNSResolver(Node var1);

    public Object evaluate(String var1, Node var2, XPathNSResolver var3, short var4, Object var5) throws XPathException, DOMException;
}

