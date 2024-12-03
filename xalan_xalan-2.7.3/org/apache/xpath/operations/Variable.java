/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.operations;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.axes.PathComponent;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;

public class Variable
extends Expression
implements PathComponent {
    static final long serialVersionUID = -4334975375609297049L;
    private boolean m_fixUpWasCalled = false;
    protected QName m_qname;
    protected int m_index;
    protected boolean m_isGlobal = false;
    static final String PSUEDOVARNAMESPACE = "http://xml.apache.org/xalan/psuedovar";

    public void setIndex(int index) {
        this.m_index = index;
    }

    public int getIndex() {
        return this.m_index;
    }

    public void setIsGlobal(boolean isGlobal) {
        this.m_isGlobal = isGlobal;
    }

    public boolean getGlobal() {
        return this.m_isGlobal;
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        this.m_fixUpWasCalled = true;
        int sz = vars.size();
        for (int i = vars.size() - 1; i >= 0; --i) {
            QName qn = (QName)vars.elementAt(i);
            if (!qn.equals(this.m_qname)) continue;
            if (i < globalsSize) {
                this.m_isGlobal = true;
                this.m_index = i;
            } else {
                this.m_index = i - globalsSize;
            }
            return;
        }
        String msg = XSLMessages.createXPATHMessage("ER_COULD_NOT_FIND_VAR", new Object[]{this.m_qname.toString()});
        TransformerException te = new TransformerException(msg, this);
        throw new WrappedRuntimeException(te);
    }

    public void setQName(QName qname) {
        this.m_qname = qname;
    }

    public QName getQName() {
        return this.m_qname;
    }

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        return this.execute(xctxt, false);
    }

    @Override
    public XObject execute(XPathContext xctxt, boolean destructiveOK) throws TransformerException {
        PrefixResolver xprefixResolver = xctxt.getNamespaceContext();
        XObject result = this.m_fixUpWasCalled ? (this.m_isGlobal ? xctxt.getVarStack().getGlobalVariable(xctxt, this.m_index, destructiveOK) : xctxt.getVarStack().getLocalVariable(xctxt, this.m_index, destructiveOK)) : xctxt.getVarStack().getVariableOrParam(xctxt, this.m_qname);
        if (null == result) {
            this.warn(xctxt, "WG_ILLEGAL_VARIABLE_REFERENCE", new Object[]{this.m_qname.getLocalPart()});
            result = new XNodeSet(xctxt.getDTMManager());
        }
        return result;
    }

    public ElemVariable getElemVariable() {
        ElemVariable vvar = null;
        ExpressionNode owner = this.getExpressionOwner();
        if (null != owner && owner instanceof ElemTemplateElement) {
            ElemTemplateElement prev = (ElemTemplateElement)owner;
            if (!(prev instanceof Stylesheet)) {
                while (prev != null && !(prev.getParentNode() instanceof Stylesheet)) {
                    ElemTemplateElement savedprev = prev;
                    while (null != (prev = prev.getPreviousSiblingElem())) {
                        if (!(prev instanceof ElemVariable)) continue;
                        vvar = (ElemVariable)prev;
                        if (vvar.getName().equals(this.m_qname)) {
                            return vvar;
                        }
                        vvar = null;
                    }
                    prev = savedprev.getParentElem();
                }
            }
            if (prev != null) {
                vvar = prev.getStylesheetRoot().getVariableOrParamComposed(this.m_qname);
            }
        }
        return vvar;
    }

    @Override
    public boolean isStableNumber() {
        return true;
    }

    @Override
    public int getAnalysisBits() {
        Expression expr;
        XPath xpath;
        ElemVariable vvar = this.getElemVariable();
        if (null != vvar && null != (xpath = vvar.getSelect()) && null != (expr = xpath.getExpression()) && expr instanceof PathComponent) {
            return ((PathComponent)((Object)expr)).getAnalysisBits();
        }
        return 0x4000000;
    }

    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
        visitor.visitVariableRef(owner, this);
    }

    @Override
    public boolean deepEquals(Expression expr) {
        if (!this.isSameClass(expr)) {
            return false;
        }
        if (!this.m_qname.equals(((Variable)expr).m_qname)) {
            return false;
        }
        return this.getElemVariable() == ((Variable)expr).getElemVariable();
    }

    public boolean isPsuedoVarRef() {
        String ns = this.m_qname.getNamespaceURI();
        return null != ns && ns.equals(PSUEDOVARNAMESPACE) && this.m_qname.getLocalName().startsWith("#");
    }
}

