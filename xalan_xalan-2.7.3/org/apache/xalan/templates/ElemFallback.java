/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.transformer.TransformerImpl;

public class ElemFallback
extends ElemTemplateElement {
    static final long serialVersionUID = 1782962139867340703L;

    @Override
    public int getXSLToken() {
        return 57;
    }

    @Override
    public String getNodeName() {
        return "fallback";
    }

    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
    }

    public void executeFallback(TransformerImpl transformer) throws TransformerException {
        int parentElemType = this.m_parentNode.getXSLToken();
        if (79 == parentElemType || -1 == parentElemType) {
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireTraceEvent(this);
            }
            transformer.executeChildTemplates((ElemTemplateElement)this, true);
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireTraceEndEvent(this);
            }
        } else {
            System.out.println("Error!  parent of xsl:fallback must be an extension or unknown element!");
        }
    }
}

