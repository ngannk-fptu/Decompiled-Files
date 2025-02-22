/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.functions;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SubContextList;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.functions.Function;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

public class FuncPosition
extends Function {
    static final long serialVersionUID = -9092846348197271582L;
    private boolean m_isTopLevel;

    @Override
    public void postCompileStep(Compiler compiler) {
        this.m_isTopLevel = compiler.getLocationPathDepth() == -1;
    }

    public int getPositionInContextNodeList(XPathContext xctxt) {
        SubContextList iter;
        SubContextList subContextList = iter = this.m_isTopLevel ? null : xctxt.getSubContextList();
        if (null != iter) {
            int prox = iter.getProximityPosition(xctxt);
            return prox;
        }
        DTMIterator cnl = xctxt.getContextNodeList();
        if (null != cnl) {
            int n = cnl.getCurrentNode();
            if (n == -1) {
                if (cnl.getCurrentPos() == 0) {
                    return 0;
                }
                try {
                    cnl = cnl.cloneWithReset();
                }
                catch (CloneNotSupportedException cnse) {
                    throw new WrappedRuntimeException(cnse);
                }
                int currentNode = xctxt.getContextNode();
                while (-1 != (n = cnl.nextNode()) && n != currentNode) {
                }
            }
            return cnl.getCurrentPos();
        }
        return -1;
    }

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        double pos = this.getPositionInContextNodeList(xctxt);
        return new XNumber(pos);
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
    }
}

