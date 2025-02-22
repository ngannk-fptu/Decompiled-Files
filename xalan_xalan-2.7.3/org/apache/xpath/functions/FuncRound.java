/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.functions;

import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

public class FuncRound
extends FunctionOneArg {
    static final long serialVersionUID = -7970583902573826611L;

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        XObject obj = this.m_arg0.execute(xctxt);
        double val = obj.num();
        if (val >= -0.5 && val < 0.0) {
            return new XNumber(-0.0);
        }
        if (val == 0.0) {
            return new XNumber(val);
        }
        return new XNumber(Math.floor(val + 0.5));
    }
}

