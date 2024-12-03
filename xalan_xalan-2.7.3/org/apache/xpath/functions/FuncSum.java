/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.functions;

import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

public class FuncSum
extends FunctionOneArg {
    static final long serialVersionUID = -2719049259574677519L;

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        int pos;
        DTMIterator nodes = this.m_arg0.asIterator(xctxt, xctxt.getCurrentNode());
        double sum = 0.0;
        while (-1 != (pos = nodes.nextNode())) {
            DTM dtm = nodes.getDTM(pos);
            XMLString s = dtm.getStringValue(pos);
            if (null == s) continue;
            sum += s.toDouble();
        }
        nodes.detach();
        return new XNumber(sum);
    }
}

