/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.operations;

import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Operation;

public class Equals
extends Operation {
    static final long serialVersionUID = -2658315633903426134L;

    @Override
    public XObject operate(XObject left, XObject right) throws TransformerException {
        return left.equals(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }

    @Override
    public boolean bool(XPathContext xctxt) throws TransformerException {
        XObject right;
        XObject left = this.m_left.execute(xctxt, true);
        boolean result = left.equals(right = this.m_right.execute(xctxt, true));
        left.detach();
        right.detach();
        return result;
    }
}

