/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemWhen;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

public class ElemChoose
extends ElemTemplateElement {
    static final long serialVersionUID = -3070117361903102033L;

    @Override
    public int getXSLToken() {
        return 37;
    }

    @Override
    public String getNodeName() {
        return "choose";
    }

    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEvent(this);
        }
        boolean found = false;
        for (ElemTemplateElement childElem = this.getFirstChildElem(); childElem != null; childElem = childElem.getNextSiblingElem()) {
            int type = childElem.getXSLToken();
            if (38 == type) {
                found = true;
                ElemWhen when = (ElemWhen)childElem;
                XPathContext xctxt = transformer.getXPathContext();
                int sourceNode = xctxt.getCurrentNode();
                if (transformer.getDebug()) {
                    XObject test = when.getTest().execute(xctxt, sourceNode, (PrefixResolver)when);
                    if (transformer.getDebug()) {
                        transformer.getTraceManager().fireSelectedEvent(sourceNode, when, "test", when.getTest(), test);
                    }
                    if (!test.bool()) continue;
                    transformer.getTraceManager().fireTraceEvent(when);
                    transformer.executeChildTemplates((ElemTemplateElement)when, true);
                    transformer.getTraceManager().fireTraceEndEvent(when);
                    return;
                }
                if (!when.getTest().bool(xctxt, sourceNode, when)) continue;
                transformer.executeChildTemplates((ElemTemplateElement)when, true);
                return;
            }
            if (39 != type) continue;
            found = true;
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireTraceEvent(childElem);
            }
            transformer.executeChildTemplates(childElem, true);
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireTraceEndEvent(childElem);
            }
            return;
        }
        if (!found) {
            transformer.getMsgMgr().error(this, "ER_CHOOSE_REQUIRES_WHEN");
        }
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEndEvent(this);
        }
    }

    @Override
    public ElemTemplateElement appendChild(ElemTemplateElement newChild) {
        int type = newChild.getXSLToken();
        switch (type) {
            case 38: 
            case 39: {
                break;
            }
            default: {
                this.error("ER_CANNOT_ADD", new Object[]{newChild.getNodeName(), this.getNodeName()});
            }
        }
        return super.appendChild(newChild);
    }

    @Override
    public boolean canAcceptVariables() {
        return false;
    }
}

