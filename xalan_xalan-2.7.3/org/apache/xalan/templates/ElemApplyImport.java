/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.transformer.TransformerImpl;

public class ElemApplyImport
extends ElemTemplateElement {
    static final long serialVersionUID = 3764728663373024038L;

    @Override
    public int getXSLToken() {
        return 72;
    }

    @Override
    public String getNodeName() {
        return "apply-imports";
    }

    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
        int sourceNode;
        if (transformer.currentTemplateRuleIsNull()) {
            transformer.getMsgMgr().error(this, "ER_NO_APPLY_IMPORT_IN_FOR_EACH");
        }
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEvent(this);
        }
        if (-1 != (sourceNode = transformer.getXPathContext().getCurrentNode())) {
            ElemTemplate matchTemplate = transformer.getMatchedTemplate();
            transformer.applyTemplateToNode(this, matchTemplate, sourceNode);
        } else {
            transformer.getMsgMgr().error(this, "ER_NULL_SOURCENODE_APPLYIMPORTS");
        }
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEndEvent(this);
        }
    }

    @Override
    public ElemTemplateElement appendChild(ElemTemplateElement newChild) {
        this.error("ER_CANNOT_ADD", new Object[]{newChild.getNodeName(), this.getNodeName()});
        return null;
    }
}

