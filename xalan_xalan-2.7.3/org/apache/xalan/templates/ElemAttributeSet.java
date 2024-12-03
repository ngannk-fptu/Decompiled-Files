/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.ElemAttribute;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemUse;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.QName;

public class ElemAttributeSet
extends ElemUse {
    static final long serialVersionUID = -426740318278164496L;
    public QName m_qname = null;

    public void setName(QName name) {
        this.m_qname = name;
    }

    public QName getName() {
        return this.m_qname;
    }

    @Override
    public int getXSLToken() {
        return 40;
    }

    @Override
    public String getNodeName() {
        return "attribute-set";
    }

    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEvent(this);
        }
        if (transformer.isRecursiveAttrSet(this)) {
            throw new TransformerException(XSLMessages.createMessage("ER_XSLATTRSET_USED_ITSELF", new Object[]{this.m_qname.getLocalPart()}));
        }
        transformer.pushElemAttributeSet(this);
        super.execute(transformer);
        for (ElemAttribute attr = (ElemAttribute)this.getFirstChildElem(); null != attr; attr = (ElemAttribute)attr.getNextSiblingElem()) {
            attr.execute(transformer);
        }
        transformer.popElemAttributeSet();
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEndEvent(this);
        }
    }

    public ElemTemplateElement appendChildElem(ElemTemplateElement newChild) {
        int type = newChild.getXSLToken();
        switch (type) {
            case 48: {
                break;
            }
            default: {
                this.error("ER_CANNOT_ADD", new Object[]{newChild.getNodeName(), this.getNodeName()});
            }
        }
        return super.appendChild(newChild);
    }

    @Override
    public void recompose(StylesheetRoot root) {
        root.recomposeAttributeSets(this);
    }
}

