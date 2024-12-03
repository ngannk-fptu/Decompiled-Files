/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.transformer;

import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.ObjectStack;

public class StackGuard {
    private int m_recursionLimit = -1;
    TransformerImpl m_transformer;

    public int getRecursionLimit() {
        return this.m_recursionLimit;
    }

    public void setRecursionLimit(int limit) {
        this.m_recursionLimit = limit;
    }

    public StackGuard(TransformerImpl transformerImpl) {
        this.m_transformer = transformerImpl;
    }

    public int countLikeTemplates(ElemTemplate templ, int pos) {
        ObjectStack elems = this.m_transformer.getCurrentTemplateElements();
        int count = 1;
        for (int i = pos - 1; i >= 0; --i) {
            if ((ElemTemplateElement)elems.elementAt(i) != templ) continue;
            ++count;
        }
        return count;
    }

    private ElemTemplate getNextMatchOrNamedTemplate(int pos) {
        ObjectStack elems = this.m_transformer.getCurrentTemplateElements();
        for (int i = pos; i >= 0; --i) {
            ElemTemplateElement elem = (ElemTemplateElement)elems.elementAt(i);
            if (null == elem || elem.getXSLToken() != 19) continue;
            return (ElemTemplate)elem;
        }
        return null;
    }

    public void checkForInfinateLoop() throws TransformerException {
        ElemTemplate template;
        int nTemplates = this.m_transformer.getCurrentTemplateElementsCount();
        if (nTemplates < this.m_recursionLimit) {
            return;
        }
        if (this.m_recursionLimit <= 0) {
            return;
        }
        for (int i = nTemplates - 1; i >= this.m_recursionLimit && null != (template = this.getNextMatchOrNamedTemplate(i)); --i) {
            int loopCount = this.countLikeTemplates(template, i);
            if (loopCount < this.m_recursionLimit) continue;
            String idIs = XSLMessages.createMessage(null != template.getName() ? "nameIs" : "matchPatternIs", null);
            Object[] msgArgs = new Object[]{new Integer(loopCount), idIs, null != template.getName() ? template.getName().toString() : template.getMatch().getPatternString()};
            String msg = XSLMessages.createMessage("recursionTooDeep", msgArgs);
            throw new TransformerException(msg);
        }
    }
}

