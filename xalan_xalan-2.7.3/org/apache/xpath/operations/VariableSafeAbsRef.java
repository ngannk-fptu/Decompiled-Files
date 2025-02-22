/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.operations;

import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Variable;

public class VariableSafeAbsRef
extends Variable {
    static final long serialVersionUID = -9174661990819967452L;

    @Override
    public XObject execute(XPathContext xctxt, boolean destructiveOK) throws TransformerException {
        XNodeSet xns = (XNodeSet)super.execute(xctxt, destructiveOK);
        DTMManager dtmMgr = xctxt.getDTMManager();
        int context = xctxt.getContextNode();
        if (dtmMgr.getDTM(xns.getRoot()).getDocument() != dtmMgr.getDTM(context).getDocument()) {
            Expression expr = (Expression)((Object)xns.getContainedIter());
            xns = (XNodeSet)expr.asIterator(xctxt, context);
        }
        return xns;
    }
}

