/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.templates;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemCallTemplate;
import org.apache.xalan.templates.ElemParam;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemWithParam;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.TemplateList;
import org.apache.xalan.transformer.StackGuard;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.IntStack;
import org.apache.xml.utils.QName;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ElemApplyTemplates
extends ElemCallTemplate {
    static final long serialVersionUID = 2903125371542621004L;
    private QName m_mode = null;
    private boolean m_isDefaultTemplate = false;

    public void setMode(QName mode) {
        this.m_mode = mode;
    }

    public QName getMode() {
        return this.m_mode;
    }

    public void setIsDefaultTemplate(boolean b) {
        this.m_isDefaultTemplate = b;
    }

    @Override
    public int getXSLToken() {
        return 50;
    }

    @Override
    public void compose(StylesheetRoot sroot) throws TransformerException {
        super.compose(sroot);
    }

    @Override
    public String getNodeName() {
        return "apply-templates";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
        transformer.pushCurrentTemplateRuleIsNull(false);
        boolean pushMode = false;
        try {
            QName mode = transformer.getMode();
            if (!this.m_isDefaultTemplate && (null == mode && null != this.m_mode || null != mode && !mode.equals(this.m_mode))) {
                pushMode = true;
                transformer.pushMode(this.m_mode);
            }
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireTraceEvent(this);
            }
            this.transformSelectedNodes(transformer);
        }
        finally {
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireTraceEndEvent(this);
            }
            if (pushMode) {
                transformer.popMode();
            }
            transformer.popCurrentTemplateRuleIsNull();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void transformSelectedNodes(TransformerImpl transformer) throws TransformerException {
        XPathContext xctxt = transformer.getXPathContext();
        int sourceNode = xctxt.getCurrentNode();
        DTMIterator sourceNodes = this.m_selectExpression.asIterator(xctxt, sourceNode);
        VariableStack vars = xctxt.getVarStack();
        int nParams = this.getParamElemCount();
        int thisframe = vars.getStackFrame();
        StackGuard guard = transformer.getStackGuard();
        boolean check = guard.getRecursionLimit() > -1;
        boolean pushContextNodeListFlag = false;
        try {
            int child;
            Vector keys;
            xctxt.pushCurrentNode(-1);
            xctxt.pushCurrentExpressionNode(-1);
            xctxt.pushSAXLocatorNull();
            transformer.pushElemTemplateElement(null);
            Vector vector = keys = this.m_sortElems == null ? null : transformer.processSortKeys(this, sourceNode);
            if (null != keys) {
                sourceNodes = this.sortNodes(xctxt, keys, sourceNodes);
            }
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireSelectedEvent(sourceNode, this, "select", new XPath(this.m_selectExpression), new XNodeSet(sourceNodes));
            }
            SerializationHandler rth = transformer.getSerializationHandler();
            StylesheetRoot sroot = transformer.getStylesheet();
            TemplateList tl = sroot.getTemplateListComposed();
            boolean quiet = transformer.getQuietConflictWarnings();
            DTM dtm = xctxt.getDTM(sourceNode);
            int argsFrame = -1;
            if (nParams > 0) {
                argsFrame = vars.link(nParams);
                vars.setStackFrame(thisframe);
                for (int i = 0; i < nParams; ++i) {
                    ElemWithParam ewp = this.m_paramElems[i];
                    if (transformer.getDebug()) {
                        transformer.getTraceManager().fireTraceEvent(ewp);
                    }
                    XObject obj = ewp.getValue(transformer, sourceNode);
                    if (transformer.getDebug()) {
                        transformer.getTraceManager().fireTraceEndEvent(ewp);
                    }
                    vars.setLocalVariable(i, obj, argsFrame);
                }
                vars.setStackFrame(argsFrame);
            }
            xctxt.pushContextNodeList(sourceNodes);
            pushContextNodeListFlag = true;
            IntStack currentNodes = xctxt.getCurrentNodeStack();
            IntStack currentExpressionNodes = xctxt.getCurrentExpressionNodeStack();
            while (-1 != (child = sourceNodes.nextNode())) {
                int currentFrameBottom;
                ElemTemplate template;
                block38: {
                    block37: {
                        currentNodes.setTop(child);
                        currentExpressionNodes.setTop(child);
                        if (xctxt.getDTM(child) != dtm) {
                            dtm = xctxt.getDTM(child);
                        }
                        int exNodeType = dtm.getExpandedTypeID(child);
                        short nodeType = dtm.getNodeType(child);
                        QName mode = transformer.getMode();
                        template = tl.getTemplateFast(xctxt, child, exNodeType, mode, -1, quiet, dtm);
                        if (null != template) break block37;
                        switch (nodeType) {
                            case 1: 
                            case 11: {
                                template = sroot.getDefaultRule();
                                break block38;
                            }
                            case 2: 
                            case 3: 
                            case 4: {
                                transformer.pushPairCurrentMatched(sroot.getDefaultTextRule(), child);
                                transformer.setCurrentElement(sroot.getDefaultTextRule());
                                dtm.dispatchCharactersEvents(child, (ContentHandler)rth, false);
                                transformer.popCurrentMatched();
                                break;
                            }
                            case 9: {
                                template = sroot.getDefaultRootRule();
                                break block38;
                            }
                        }
                        continue;
                    }
                    transformer.setCurrentElement(template);
                }
                transformer.pushPairCurrentMatched(template, child);
                if (check) {
                    guard.checkForInfinateLoop();
                }
                if (template.m_frameSize > 0) {
                    xctxt.pushRTFContext();
                    currentFrameBottom = vars.getStackFrame();
                    vars.link(template.m_frameSize);
                    if (template.m_inArgsSize > 0) {
                        int paramIndex = 0;
                        for (ElemTemplateElement elem = template.getFirstChildElem(); null != elem && 41 == elem.getXSLToken(); elem = elem.getNextSiblingElem()) {
                            int i;
                            ElemParam ep = (ElemParam)elem;
                            for (i = 0; i < nParams; ++i) {
                                ElemWithParam ewp = this.m_paramElems[i];
                                if (ewp.m_qnameID != ep.m_qnameID) continue;
                                XObject obj = vars.getLocalVariable(i, argsFrame);
                                vars.setLocalVariable(paramIndex, obj);
                                break;
                            }
                            if (i == nParams) {
                                vars.setLocalVariable(paramIndex, null);
                            }
                            ++paramIndex;
                        }
                    }
                } else {
                    currentFrameBottom = 0;
                }
                if (transformer.getDebug()) {
                    transformer.getTraceManager().fireTraceEvent(template);
                }
                ElemTemplateElement t = template.m_firstChild;
                while (t != null) {
                    xctxt.setSAXLocator(t);
                    try {
                        transformer.pushElemTemplateElement(t);
                        t.execute(transformer);
                    }
                    finally {
                        transformer.popElemTemplateElement();
                    }
                    t = t.m_nextSibling;
                }
                if (transformer.getDebug()) {
                    transformer.getTraceManager().fireTraceEndEvent(template);
                }
                if (template.m_frameSize > 0) {
                    vars.unlink(currentFrameBottom);
                    xctxt.popRTFContext();
                }
                transformer.popCurrentMatched();
            }
        }
        catch (SAXException se) {
            transformer.getErrorListener().fatalError(new TransformerException(se));
        }
        finally {
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireSelectedEndEvent(sourceNode, this, "select", new XPath(this.m_selectExpression), new XNodeSet(sourceNodes));
            }
            if (nParams > 0) {
                vars.unlink(thisframe);
            }
            xctxt.popSAXLocator();
            if (pushContextNodeListFlag) {
                xctxt.popContextNodeList();
            }
            transformer.popElemTemplateElement();
            xctxt.popCurrentExpressionNode();
            xctxt.popCurrentNode();
            sourceNodes.detach();
        }
    }
}

