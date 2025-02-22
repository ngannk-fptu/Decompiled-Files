/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

public class XUnresolvedVariable
extends XObject {
    static final long serialVersionUID = -256779804767950188L;
    private transient int m_context;
    private transient TransformerImpl m_transformer;
    private transient int m_varStackPos = -1;
    private transient int m_varStackContext;
    private boolean m_isGlobal;
    private transient boolean m_doneEval = true;

    public XUnresolvedVariable(ElemVariable obj, int sourceNode, TransformerImpl transformer, int varStackPos, int varStackContext, boolean isGlobal) {
        super(obj);
        this.m_context = sourceNode;
        this.m_transformer = transformer;
        this.m_varStackPos = varStackPos;
        this.m_varStackContext = varStackContext;
        this.m_isGlobal = isGlobal;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        if (!this.m_doneEval) {
            this.m_transformer.getMsgMgr().error(xctxt.getSAXLocator(), "ER_REFERENCING_ITSELF", new Object[]{((ElemVariable)this.object()).getName().getLocalName()});
        }
        VariableStack vars = xctxt.getVarStack();
        int currentFrame = vars.getStackFrame();
        ElemVariable velem = (ElemVariable)this.m_obj;
        try {
            this.m_doneEval = false;
            if (-1 != velem.m_frameSize) {
                vars.link(velem.m_frameSize);
            }
            XObject var = velem.getValue(this.m_transformer, this.m_context);
            this.m_doneEval = true;
            XObject xObject = var;
            return xObject;
        }
        finally {
            if (-1 != velem.m_frameSize) {
                vars.unlink(currentFrame);
            }
        }
    }

    public void setVarStackPos(int top) {
        this.m_varStackPos = top;
    }

    public void setVarStackContext(int bottom) {
        this.m_varStackContext = bottom;
    }

    @Override
    public int getType() {
        return 600;
    }

    @Override
    public String getTypeString() {
        return "XUnresolvedVariable (" + this.object().getClass().getName() + ")";
    }
}

