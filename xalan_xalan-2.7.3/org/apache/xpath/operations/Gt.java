/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.operations;

import javax.xml.transform.TransformerException;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Operation;

public class Gt
extends Operation {
    static final long serialVersionUID = 8927078751014375950L;

    @Override
    public XObject operate(XObject left, XObject right) throws TransformerException {
        return left.greaterThan(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
}

