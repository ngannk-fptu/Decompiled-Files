/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.operations;

import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.UnaryOperation;

public class Neg
extends UnaryOperation {
    static final long serialVersionUID = -6280607702375702291L;

    @Override
    public XObject operate(XObject right) throws TransformerException {
        return new XNumber(-right.num());
    }

    @Override
    public double num(XPathContext xctxt) throws TransformerException {
        return -this.m_right.num(xctxt);
    }
}

