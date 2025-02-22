/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;

public class KeyDeclaration
extends ElemTemplateElement {
    static final long serialVersionUID = 7724030248631137918L;
    private QName m_name;
    private XPath m_matchPattern = null;
    private XPath m_use;

    public KeyDeclaration(Stylesheet parentNode, int docOrderNumber) {
        this.m_parentNode = parentNode;
        this.setUid(docOrderNumber);
    }

    public void setName(QName name) {
        this.m_name = name;
    }

    public QName getName() {
        return this.m_name;
    }

    @Override
    public String getNodeName() {
        return "key";
    }

    public void setMatch(XPath v) {
        this.m_matchPattern = v;
    }

    public XPath getMatch() {
        return this.m_matchPattern;
    }

    public void setUse(XPath v) {
        this.m_use = v;
    }

    public XPath getUse() {
        return this.m_use;
    }

    @Override
    public int getXSLToken() {
        return 31;
    }

    @Override
    public void compose(StylesheetRoot sroot) throws TransformerException {
        super.compose(sroot);
        Vector vnames = sroot.getComposeState().getVariableNames();
        if (null != this.m_matchPattern) {
            this.m_matchPattern.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
        }
        if (null != this.m_use) {
            this.m_use.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
        }
    }

    @Override
    public void recompose(StylesheetRoot root) {
        root.recomposeKeys(this);
    }
}

