/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XSLTVisitor;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.objects.XString;

public class ElemWithParam
extends ElemTemplateElement {
    static final long serialVersionUID = -1070355175864326257L;
    int m_index;
    private XPath m_selectPattern = null;
    private QName m_qname = null;
    int m_qnameID;

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

    @Override
    public int getXSLToken() {
        return 2;
    }

    @Override
    public String getNodeName() {
        return "with-param";
    }

    @Override
    public void compose(StylesheetRoot sroot) throws TransformerException {
        XPath newSelect;
        if (null == this.m_selectPattern && sroot.getOptimizer() && null != (newSelect = ElemVariable.rewriteChildToExpression(this))) {
            this.m_selectPattern = newSelect;
        }
        this.m_qnameID = sroot.getComposeState().getQNameID(this.m_qname);
        super.compose(sroot);
        Vector vnames = sroot.getComposeState().getVariableNames();
        if (null != this.m_selectPattern) {
            this.m_selectPattern.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
        }
    }

    @Override
    public void setParentElem(ElemTemplateElement p) {
        super.setParentElem(p);
        p.m_hasVariableDecl = true;
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
                int df = transformer.transformToRTF(this);
                var = new XRTreeFrag(df, xctxt, this);
            }
        }
        finally {
            xctxt.popCurrentNode();
        }
        return var;
    }

    @Override
    protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs) {
        if (callAttrs && null != this.m_selectPattern) {
            this.m_selectPattern.getExpression().callVisitors(this.m_selectPattern, visitor);
        }
        super.callChildVisitors(visitor, callAttrs);
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

