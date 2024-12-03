/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.templates;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XSLTVisitor;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ElemValueOf
extends ElemTemplateElement {
    static final long serialVersionUID = 3490728458007586786L;
    private XPath m_selectExpression = null;
    private boolean m_isDot = false;
    private boolean m_disableOutputEscaping = false;

    public void setSelect(XPath v) {
        if (null != v) {
            String s = v.getPatternString();
            this.m_isDot = null != s && s.equals(".");
        }
        this.m_selectExpression = v;
    }

    public XPath getSelect() {
        return this.m_selectExpression;
    }

    public void setDisableOutputEscaping(boolean v) {
        this.m_disableOutputEscaping = v;
    }

    public boolean getDisableOutputEscaping() {
        return this.m_disableOutputEscaping;
    }

    @Override
    public int getXSLToken() {
        return 30;
    }

    @Override
    public void compose(StylesheetRoot sroot) throws TransformerException {
        super.compose(sroot);
        Vector vnames = sroot.getComposeState().getVariableNames();
        if (null != this.m_selectExpression) {
            this.m_selectExpression.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
        }
    }

    @Override
    public String getNodeName() {
        return "value-of";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
        XPathContext xctxt = transformer.getXPathContext();
        SerializationHandler rth = transformer.getResultTreeHandler();
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEvent(this);
        }
        try {
            xctxt.pushNamespaceContext(this);
            int current = xctxt.getCurrentNode();
            xctxt.pushCurrentNodeAndExpression(current, current);
            if (this.m_disableOutputEscaping) {
                rth.processingInstruction("javax.xml.transform.disable-output-escaping", "");
            }
            try {
                Expression expr = this.m_selectExpression.getExpression();
                if (transformer.getDebug()) {
                    XObject obj = expr.execute(xctxt);
                    transformer.getTraceManager().fireSelectedEvent(current, this, "select", this.m_selectExpression, obj);
                    obj.dispatchCharactersEvents((ContentHandler)rth);
                } else {
                    expr.executeCharsToContentHandler(xctxt, (ContentHandler)rth);
                }
            }
            finally {
                if (this.m_disableOutputEscaping) {
                    rth.processingInstruction("javax.xml.transform.enable-output-escaping", "");
                }
                xctxt.popNamespaceContext();
                xctxt.popCurrentNodeAndExpression();
            }
        }
        catch (SAXException se) {
            throw new TransformerException(se);
        }
        catch (RuntimeException re) {
            TransformerException te = new TransformerException(re);
            te.setLocator(this);
            throw te;
        }
        finally {
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireTraceEndEvent(this);
            }
        }
    }

    @Override
    public ElemTemplateElement appendChild(ElemTemplateElement newChild) {
        this.error("ER_CANNOT_ADD", new Object[]{newChild.getNodeName(), this.getNodeName()});
        return null;
    }

    @Override
    protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs) {
        if (callAttrs) {
            this.m_selectExpression.getExpression().callVisitors(this.m_selectExpression, visitor);
        }
        super.callChildVisitors(visitor, callAttrs);
    }
}

