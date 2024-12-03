/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import org.apache.xalan.templates.ElemTemplateElement;

public class ElemText
extends ElemTemplateElement {
    static final long serialVersionUID = 1383140876182316711L;
    private boolean m_disableOutputEscaping = false;

    public void setDisableOutputEscaping(boolean v) {
        this.m_disableOutputEscaping = v;
    }

    public boolean getDisableOutputEscaping() {
        return this.m_disableOutputEscaping;
    }

    @Override
    public int getXSLToken() {
        return 42;
    }

    @Override
    public String getNodeName() {
        return "text";
    }

    @Override
    public ElemTemplateElement appendChild(ElemTemplateElement newChild) {
        int type = newChild.getXSLToken();
        switch (type) {
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

