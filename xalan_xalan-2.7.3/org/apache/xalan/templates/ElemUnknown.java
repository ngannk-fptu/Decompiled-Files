/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemFallback;
import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.transformer.TransformerImpl;

public class ElemUnknown
extends ElemLiteralResult {
    static final long serialVersionUID = -4573981712648730168L;

    @Override
    public int getXSLToken() {
        return -1;
    }

    private void executeFallbacks(TransformerImpl transformer) throws TransformerException {
        ElemTemplateElement child = this.m_firstChild;
        while (child != null) {
            if (child.getXSLToken() == 57) {
                try {
                    transformer.pushElemTemplateElement(child);
                    ((ElemFallback)child).executeFallback(transformer);
                }
                finally {
                    transformer.popElemTemplateElement();
                }
            }
            child = child.m_nextSibling;
        }
    }

    private boolean hasFallbackChildren() {
        ElemTemplateElement child = this.m_firstChild;
        while (child != null) {
            if (child.getXSLToken() == 57) {
                return true;
            }
            child = child.m_nextSibling;
        }
        return false;
    }

    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEvent(this);
        }
        try {
            if (this.hasFallbackChildren()) {
                this.executeFallbacks(transformer);
            }
        }
        catch (TransformerException e) {
            transformer.getErrorListener().fatalError(e);
        }
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEndEvent(this);
        }
    }
}

