/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.functions;

import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionDef1Arg;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

public class FuncQname
extends FunctionDef1Arg {
    static final long serialVersionUID = -1532307875532617380L;

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        DTM dtm;
        String qname;
        int context = this.getArg0AsNode(xctxt);
        XString val = -1 != context ? (null == (qname = (dtm = xctxt.getDTM(context)).getNodeNameX(context)) ? XString.EMPTYSTRING : new XString(qname)) : XString.EMPTYSTRING;
        return val;
    }
}

