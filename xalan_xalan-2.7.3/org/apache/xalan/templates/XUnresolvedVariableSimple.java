/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

public class XUnresolvedVariableSimple
extends XObject {
    static final long serialVersionUID = -1224413807443958985L;

    public XUnresolvedVariableSimple(ElemVariable obj) {
        super(obj);
    }

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        Expression expr = ((ElemVariable)this.m_obj).getSelect().getExpression();
        XObject xobj = expr.execute(xctxt);
        xobj.allowDetachToRelease(false);
        return xobj;
    }

    @Override
    public int getType() {
        return 600;
    }

    @Override
    public String getTypeString() {
        return "XUnresolvedVariableSimple (" + this.object().getClass().getName() + ")";
    }
}

