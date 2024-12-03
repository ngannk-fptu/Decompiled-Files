/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.operations;

import javax.xml.transform.TransformerException;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.UnaryOperation;

public class String
extends UnaryOperation {
    static final long serialVersionUID = 2973374377453022888L;

    @Override
    public XObject operate(XObject right) throws TransformerException {
        return (XString)right.xstr();
    }
}

