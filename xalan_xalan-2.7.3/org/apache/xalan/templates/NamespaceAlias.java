/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;

public class NamespaceAlias
extends ElemTemplateElement {
    static final long serialVersionUID = 456173966637810718L;
    private String m_StylesheetPrefix;
    private String m_StylesheetNamespace;
    private String m_ResultPrefix;
    private String m_ResultNamespace;

    public NamespaceAlias(int docOrderNumber) {
        this.m_docOrderNumber = docOrderNumber;
    }

    public void setStylesheetPrefix(String v) {
        this.m_StylesheetPrefix = v;
    }

    public String getStylesheetPrefix() {
        return this.m_StylesheetPrefix;
    }

    public void setStylesheetNamespace(String v) {
        this.m_StylesheetNamespace = v;
    }

    public String getStylesheetNamespace() {
        return this.m_StylesheetNamespace;
    }

    public void setResultPrefix(String v) {
        this.m_ResultPrefix = v;
    }

    public String getResultPrefix() {
        return this.m_ResultPrefix;
    }

    public void setResultNamespace(String v) {
        this.m_ResultNamespace = v;
    }

    public String getResultNamespace() {
        return this.m_ResultNamespace;
    }

    @Override
    public void recompose(StylesheetRoot root) {
        root.recomposeNamespaceAliases(this);
    }
}

