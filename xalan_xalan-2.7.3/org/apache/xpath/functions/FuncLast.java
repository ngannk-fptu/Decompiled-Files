/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.functions;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SubContextList;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.functions.Function;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

public class FuncLast
extends Function {
    static final long serialVersionUID = 9205812403085432943L;
    private boolean m_isTopLevel;

    @Override
    public void postCompileStep(Compiler compiler) {
        this.m_isTopLevel = compiler.getLocationPathDepth() == -1;
    }

    public int getCountOfContextNodeList(XPathContext xctxt) throws TransformerException {
        SubContextList iter;
        SubContextList subContextList = iter = this.m_isTopLevel ? null : xctxt.getSubContextList();
        if (null != iter) {
            return iter.getLastPos(xctxt);
        }
        DTMIterator cnl = xctxt.getContextNodeList();
        int count = null != cnl ? cnl.getLength() : 0;
        return count;
    }

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        XNumber xnum = new XNumber(this.getCountOfContextNodeList(xctxt));
        return xnum;
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
    }
}

