/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XSLTVisitor;
import org.apache.xpath.XPath;

public class ElemWhen
extends ElemTemplateElement {
    static final long serialVersionUID = 5984065730262071360L;
    private XPath m_test;

    public void setTest(XPath v) {
        this.m_test = v;
    }

    public XPath getTest() {
        return this.m_test;
    }

    @Override
    public int getXSLToken() {
        return 38;
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
    public String getNodeName() {
        return "when";
    }

    @Override
    protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs) {
        if (callAttrs) {
            this.m_test.getExpression().callVisitors(this.m_test, visitor);
        }
        super.callChildVisitors(visitor, callAttrs);
    }
}

