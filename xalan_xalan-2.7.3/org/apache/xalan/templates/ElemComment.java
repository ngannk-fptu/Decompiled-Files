/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.transformer.TransformerImpl;
import org.xml.sax.SAXException;

public class ElemComment
extends ElemTemplateElement {
    static final long serialVersionUID = -8813199122875770142L;

    @Override
    public int getXSLToken() {
        return 59;
    }

    @Override
    public String getNodeName() {
        return "comment";
    }

    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEvent(this);
        }
        try {
            String data = transformer.transformToString(this);
            transformer.getResultTreeHandler().comment(data);
        }
        catch (SAXException se) {
            throw new TransformerException(se);
        }
        finally {
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireTraceEndEvent(this);
            }
        }
    }

    @Override
    public ElemTemplateElement appendChild(ElemTemplateElement newChild) {
        int type = newChild.getXSLToken();
        switch (type) {
            case 9: 
            case 17: 
            case 28: 
            case 30: 
            case 35: 
            case 36: 
            case 37: 
            case 42: 
            case 50: 
            case 72: 
            case 73: 
            case 74: 
            case 75: 
            case 78: {
                break;
            }
            default: {
                this.error("ER_CANNOT_ADD", new Object[]{newChild.getNodeName(), this.getNodeName()});
            }
        }
        return super.appendChild(newChild);
    }
}

