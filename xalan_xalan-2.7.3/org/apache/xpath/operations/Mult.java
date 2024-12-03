/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.operations;

import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Operation;

public class Mult
extends Operation {
    static final long serialVersionUID = -4956770147013414675L;

    @Override
    public XObject operate(XObject left, XObject right) throws TransformerException {
        return new XNumber(left.num() * right.num());
    }

    @Override
    public double num(XPathContext xctxt) throws TransformerException {
        return this.m_left.num(xctxt) * this.m_right.num(xctxt);
    }
}

