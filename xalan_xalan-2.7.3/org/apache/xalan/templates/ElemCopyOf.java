/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;
import org.apache.xalan.serialize.SerializerUtils;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XSLTVisitor;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.TreeWalker2Result;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.ref.DTMTreeWalker;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.xml.sax.SAXException;

public class ElemCopyOf
extends ElemTemplateElement {
    static final long serialVersionUID = -7433828829497411127L;
    public XPath m_selectExpression = null;

    public void setSelect(XPath expr) {
        this.m_selectExpression = expr;
    }

    public XPath getSelect() {
        return this.m_selectExpression;
    }

    @Override
    public void compose(StylesheetRoot sroot) throws TransformerException {
        super.compose(sroot);
        StylesheetRoot.ComposeState cstate = sroot.getComposeState();
        this.m_selectExpression.fixupVariables(cstate.getVariableNames(), cstate.getGlobalsSize());
    }

    @Override
    public int getXSLToken() {
        return 74;
    }

    @Override
    public String getNodeName() {
        return "copy-of";
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEvent(this);
        }
        try {
            XPathContext xctxt = transformer.getXPathContext();
            int sourceNode = xctxt.getCurrentNode();
            XObject value = this.m_selectExpression.execute(xctxt, sourceNode, (PrefixResolver)this);
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireSelectedEvent(sourceNode, this, "select", this.m_selectExpression, value);
            }
            SerializationHandler handler = transformer.getSerializationHandler();
            if (null == value) return;
            int type = value.getType();
            switch (type) {
                case 1: 
                case 2: 
                case 3: {
                    String s = value.str();
                    handler.characters(s.toCharArray(), 0, s.length());
                    return;
                }
                case 4: {
                    int pos;
                    DTMIterator nl = value.iter();
                    TreeWalker2Result tw = new TreeWalker2Result(transformer, handler);
                    while (-1 != (pos = nl.nextNode())) {
                        DTM dtm = xctxt.getDTMManager().getDTM(pos);
                        short t = dtm.getNodeType(pos);
                        if (t == 9) {
                            int child = dtm.getFirstChild(pos);
                            while (child != -1) {
                                ((DTMTreeWalker)tw).traverse(child);
                                child = dtm.getNextSibling(child);
                            }
                            continue;
                        }
                        if (t == 2) {
                            SerializerUtils.addAttribute(handler, pos);
                            continue;
                        }
                        ((DTMTreeWalker)tw).traverse(pos);
                    }
                    return;
                }
                case 5: {
                    SerializerUtils.outputResultTreeFragment(handler, value, transformer.getXPathContext());
                    return;
                }
                default: {
                    String s = value.str();
                    handler.characters(s.toCharArray(), 0, s.length());
                    return;
                }
            }
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

