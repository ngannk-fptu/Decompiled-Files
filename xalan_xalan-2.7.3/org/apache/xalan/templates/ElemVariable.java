/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemTextLiteral;
import org.apache.xalan.templates.ElemValueOf;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XSLTVisitor;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.objects.XRTreeFragSelectWrapper;
import org.apache.xpath.objects.XString;

public class ElemVariable
extends ElemTemplateElement {
    static final long serialVersionUID = 9111131075322790061L;
    protected int m_index;
    int m_frameSize = -1;
    private XPath m_selectPattern;
    protected QName m_qname;
    private boolean m_isTopLevel = false;

    public ElemVariable() {
    }

    public void setIndex(int index) {
        this.m_index = index;
    }

    public int getIndex() {
        return this.m_index;
    }

    public void setSelect(XPath v) {
        this.m_selectPattern = v;
    }

    public XPath getSelect() {
        return this.m_selectPattern;
    }

    public void setName(QName v) {
        this.m_qname = v;
    }

    public QName getName() {
        return this.m_qname;
    }

    public void setIsTopLevel(boolean v) {
        this.m_isTopLevel = v;
    }

    public boolean getIsTopLevel() {
        return this.m_isTopLevel;
    }

    @Override
    public int getXSLToken() {
        return 73;
    }

    @Override
    public String getNodeName() {
        return "variable";
    }

    public ElemVariable(ElemVariable param) throws TransformerException {
        this.m_selectPattern = param.m_selectPattern;
        this.m_qname = param.m_qname;
        this.m_isTopLevel = param.m_isTopLevel;
    }

    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEvent(this);
        }
        int sourceNode = transformer.getXPathContext().getCurrentNode();
        XObject var = this.getValue(transformer, sourceNode);
        transformer.getXPathContext().getVarStack().setLocalVariable(this.m_index, var);
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEndEvent(this);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public XObject getValue(TransformerImpl transformer, int sourceNode) throws TransformerException {
        XObject var;
        XPathContext xctxt = transformer.getXPathContext();
        xctxt.pushCurrentNode(sourceNode);
        try {
            if (null != this.m_selectPattern) {
                var = this.m_selectPattern.execute(xctxt, sourceNode, (PrefixResolver)this);
                var.allowDetachToRelease(false);
                if (transformer.getDebug()) {
                    transformer.getTraceManager().fireSelectedEvent(sourceNode, this, "select", this.m_selectPattern, var);
                }
            } else if (null == this.getFirstChildElem()) {
                var = XString.EMPTYSTRING;
            } else {
                int df = this.m_parentNode instanceof Stylesheet ? transformer.transformToGlobalRTF(this) : transformer.transformToRTF(this);
                var = new XRTreeFrag(df, xctxt, this);
            }
        }
        finally {
            xctxt.popCurrentNode();
        }
        return var;
    }

    @Override
    public void compose(StylesheetRoot sroot) throws TransformerException {
        XPath newSelect;
        if (null == this.m_selectPattern && sroot.getOptimizer() && null != (newSelect = ElemVariable.rewriteChildToExpression(this))) {
            this.m_selectPattern = newSelect;
        }
        StylesheetRoot.ComposeState cstate = sroot.getComposeState();
        Vector vnames = cstate.getVariableNames();
        if (null != this.m_selectPattern) {
            this.m_selectPattern.fixupVariables(vnames, cstate.getGlobalsSize());
        }
        if (!(this.m_parentNode instanceof Stylesheet) && this.m_qname != null) {
            this.m_index = cstate.addVariableName(this.m_qname) - cstate.getGlobalsSize();
        } else if (this.m_parentNode instanceof Stylesheet) {
            cstate.resetStackFrameSize();
        }
        super.compose(sroot);
    }

    @Override
    public void endCompose(StylesheetRoot sroot) throws TransformerException {
        super.endCompose(sroot);
        if (this.m_parentNode instanceof Stylesheet) {
            StylesheetRoot.ComposeState cstate = sroot.getComposeState();
            this.m_frameSize = cstate.getFrameSize();
            cstate.resetStackFrameSize();
        }
    }

    static XPath rewriteChildToExpression(ElemTemplateElement varElem) throws TransformerException {
        ElemTemplateElement t = varElem.getFirstChildElem();
        if (null != t && null == t.getNextSiblingElem()) {
            ElemTextLiteral lit;
            int etype = t.getXSLToken();
            if (30 == etype) {
                ElemValueOf valueof = (ElemValueOf)t;
                if (!valueof.getDisableOutputEscaping() && valueof.getDOMBackPointer() == null) {
                    varElem.m_firstChild = null;
                    return new XPath(new XRTreeFragSelectWrapper(valueof.getSelect().getExpression()));
                }
            } else if (78 == etype && !(lit = (ElemTextLiteral)t).getDisableOutputEscaping() && lit.getDOMBackPointer() == null) {
                String str = lit.getNodeValue();
                XString xstr = new XString(str);
                varElem.m_firstChild = null;
                return new XPath(new XRTreeFragSelectWrapper(xstr));
            }
        }
        return null;
    }

    @Override
    public void recompose(StylesheetRoot root) {
        root.recomposeVariables(this);
    }

    @Override
    public void setParentElem(ElemTemplateElement p) {
        super.setParentElem(p);
        p.m_hasVariableDecl = true;
    }

    @Override
    protected boolean accept(XSLTVisitor visitor) {
        return visitor.visitVariableOrParamDecl(this);
    }

    @Override
    protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs) {
        if (null != this.m_selectPattern) {
            this.m_selectPattern.getExpression().callVisitors(this.m_selectPattern, visitor);
        }
        super.callChildVisitors(visitor, callAttrs);
    }

    public boolean isPsuedoVar() {
        String ns = this.m_qname.getNamespaceURI();
        return null != ns && ns.equals("http://xml.apache.org/xalan/psuedovar") && this.m_qname.getLocalName().startsWith("#");
    }

    @Override
    public ElemTemplateElement appendChild(ElemTemplateElement elem) {
        if (this.m_selectPattern != null) {
            this.error("ER_CANT_HAVE_CONTENT_AND_SELECT", new Object[]{"xsl:" + this.getNodeName()});
            return null;
        }
        return super.appendChild(elem);
    }
}

