/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XSLTVisitor;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

public class ElemIf
extends ElemTemplateElement {
    static final long serialVersionUID = 2158774632427453022L;
    private XPath m_test = null;

    public void setTest(XPath v) {
        this.m_test = v;
    }

    public XPath getTest() {
        return this.m_test;
    }

    @Override
    public void compose(StylesheetRoot sroot) throws TransformerException {
        super.compose(sroot);
        Vector vnames = sroot.getComposeState().getVariableNames();
        if (null != this.m_test) {
            this.m_test.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
        }
    }

    @Override
    public int getXSLToken() {
        return 36;
    }

    @Override
    public String getNodeName() {
        return "if";
    }

    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
        XPathContext xctxt = transformer.getXPathContext();
        int sourceNode = xctxt.getCurrentNode();
        if (transformer.getDebug()) {
            XObject test = this.m_test.execute(xctxt, sourceNode, (PrefixResolver)this);
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireSelectedEvent(sourceNode, this, "test", this.m_test, test);
            }
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireTraceEvent(this);
            }
            if (test.bool()) {
                transformer.executeChildTemplates((ElemTemplateElement)this, true);
            }
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireTraceEndEvent(this);
            }
        } else if (this.m_test.bool(xctxt, sourceNode, this)) {
            transformer.executeChildTemplates((ElemTemplateElement)this, true);
        }
    }

    @Override
    protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs) {
        if (callAttrs) {
            this.m_test.getExpression().callVisitors(this.m_test, visitor);
        }
        super.callChildVisitors(visitor, callAttrs);
    }
}

