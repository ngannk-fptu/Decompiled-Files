/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.AVT;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.XML11Char;
import org.apache.xpath.XPathContext;
import org.xml.sax.SAXException;

public class ElemPI
extends ElemTemplateElement {
    static final long serialVersionUID = 5621976448020889825L;
    private AVT m_name_atv = null;

    public void setName(AVT v) {
        this.m_name_atv = v;
    }

    public AVT getName() {
        return this.m_name_atv;
    }

    @Override
    public void compose(StylesheetRoot sroot) throws TransformerException {
        super.compose(sroot);
        Vector vnames = sroot.getComposeState().getVariableNames();
        if (null != this.m_name_atv) {
            this.m_name_atv.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
        }
    }

    @Override
    public int getXSLToken() {
        return 58;
    }

    @Override
    public String getNodeName() {
        return "processing-instruction";
    }

    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
        String piName;
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEvent(this);
        }
        XPathContext xctxt = transformer.getXPathContext();
        int sourceNode = xctxt.getCurrentNode();
        String string = piName = this.m_name_atv == null ? null : this.m_name_atv.evaluate(xctxt, sourceNode, this);
        if (piName == null) {
            return;
        }
        if (piName.equalsIgnoreCase("xml")) {
            transformer.getMsgMgr().warn(this, "WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML", new Object[]{"name", piName});
            return;
        }
        if (!this.m_name_atv.isSimple() && !XML11Char.isXML11ValidNCName(piName)) {
            transformer.getMsgMgr().warn(this, "WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME", new Object[]{"name", piName});
            return;
        }
        String data = transformer.transformToString(this);
        try {
            transformer.getResultTreeHandler().processingInstruction(piName, data);
        }
        catch (SAXException se) {
            throw new TransformerException(se);
        }
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEndEvent(this);
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

