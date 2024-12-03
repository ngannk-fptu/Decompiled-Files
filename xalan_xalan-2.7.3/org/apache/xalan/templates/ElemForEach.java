/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemSort;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XSLTVisitor;
import org.apache.xalan.transformer.NodeSorter;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.IntStack;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;

public class ElemForEach
extends ElemTemplateElement
implements ExpressionOwner {
    static final long serialVersionUID = 6018140636363583690L;
    static final boolean DEBUG = false;
    public boolean m_doc_cache_off = false;
    protected Expression m_selectExpression = null;
    protected XPath m_xpath = null;
    protected Vector m_sortElems = null;

    public void setSelect(XPath xpath) {
        this.m_selectExpression = xpath.getExpression();
        this.m_xpath = xpath;
    }

    public Expression getSelect() {
        return this.m_selectExpression;
    }

    @Override
    public void compose(StylesheetRoot sroot) throws TransformerException {
        super.compose(sroot);
        int length = this.getSortElemCount();
        for (int i = 0; i < length; ++i) {
            this.getSortElem(i).compose(sroot);
        }
        Vector vnames = sroot.getComposeState().getVariableNames();
        if (null != this.m_selectExpression) {
            this.m_selectExpression.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
        } else {
            this.m_selectExpression = this.getStylesheetRoot().m_selectDefault.getExpression();
        }
    }

    @Override
    public void endCompose(StylesheetRoot sroot) throws TransformerException {
        int length = this.getSortElemCount();
        for (int i = 0; i < length; ++i) {
            this.getSortElem(i).endCompose(sroot);
        }
        super.endCompose(sroot);
    }

    public int getSortElemCount() {
        return this.m_sortElems == null ? 0 : this.m_sortElems.size();
    }

    public ElemSort getSortElem(int i) {
        return (ElemSort)this.m_sortElems.elementAt(i);
    }

    public void setSortElem(ElemSort sortElem) {
        if (null == this.m_sortElems) {
            this.m_sortElems = new Vector();
        }
        this.m_sortElems.addElement(sortElem);
    }

    @Override
    public int getXSLToken() {
        return 28;
    }

    @Override
    public String getNodeName() {
        return "for-each";
    }

    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
        transformer.pushCurrentTemplateRuleIsNull(true);
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEvent(this);
        }
        try {
            this.transformSelectedNodes(transformer);
        }
        finally {
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireTraceEndEvent(this);
            }
            transformer.popCurrentTemplateRuleIsNull();
        }
    }

    protected ElemTemplateElement getTemplateMatch() {
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DTMIterator sortNodes(XPathContext xctxt, Vector keys, DTMIterator sourceNodes) throws TransformerException {
        NodeSorter sorter = new NodeSorter(xctxt);
        sourceNodes.setShouldCacheNodes(true);
        sourceNodes.runTo(-1);
        xctxt.pushContextNodeList(sourceNodes);
        try {
            sorter.sort(sourceNodes, keys, xctxt);
            sourceNodes.setCurrentPos(0);
        }
        finally {
            xctxt.popContextNodeList();
        }
        return sourceNodes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void transformSelectedNodes(TransformerImpl transformer) throws TransformerException {
        XPathContext xctxt = transformer.getXPathContext();
        int sourceNode = xctxt.getCurrentNode();
        DTMIterator sourceNodes = this.m_selectExpression.asIterator(xctxt, sourceNode);
        try {
            int child;
            Vector keys;
            Vector vector = keys = this.m_sortElems == null ? null : transformer.processSortKeys(this, sourceNode);
            if (null != keys) {
                sourceNodes = this.sortNodes(xctxt, keys, sourceNodes);
            }
            if (transformer.getDebug()) {
                Expression expr = this.m_xpath.getExpression();
                XObject xObject = expr.execute(xctxt);
                int current = xctxt.getCurrentNode();
                transformer.getTraceManager().fireSelectedEvent(current, this, "select", this.m_xpath, xObject);
            }
            xctxt.pushCurrentNode(-1);
            IntStack currentNodes = xctxt.getCurrentNodeStack();
            xctxt.pushCurrentExpressionNode(-1);
            IntStack currentExpressionNodes = xctxt.getCurrentExpressionNodeStack();
            xctxt.pushSAXLocatorNull();
            xctxt.pushContextNodeList(sourceNodes);
            transformer.pushElemTemplateElement(null);
            DTM dtm = xctxt.getDTM(sourceNode);
            int docID = sourceNode & 0xFFFF0000;
            while (-1 != (child = sourceNodes.nextNode())) {
                currentNodes.setTop(child);
                currentExpressionNodes.setTop(child);
                if ((child & 0xFFFF0000) != docID) {
                    dtm = xctxt.getDTM(child);
                    docID = child & 0xFFFF0000;
                }
                short nodeType = dtm.getNodeType(child);
                if (transformer.getDebug()) {
                    transformer.getTraceManager().fireTraceEvent(this);
                }
                ElemTemplateElement t = this.m_firstChild;
                while (t != null) {
                    xctxt.setSAXLocator(t);
                    transformer.setCurrentElement(t);
                    t.execute(transformer);
                    t = t.m_nextSibling;
                }
                if (transformer.getDebug()) {
                    transformer.setCurrentElement(null);
                    transformer.getTraceManager().fireTraceEndEvent(this);
                }
                if (!this.m_doc_cache_off) continue;
                xctxt.getSourceTreeManager().removeDocumentFromCache(dtm.getDocument());
                xctxt.release(dtm, false);
            }
        }
        finally {
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireSelectedEndEvent(sourceNode, this, "select", new XPath(this.m_selectExpression), new XNodeSet(sourceNodes));
            }
            xctxt.popSAXLocator();
            xctxt.popContextNodeList();
            transformer.popElemTemplateElement();
            xctxt.popCurrentExpressionNode();
            xctxt.popCurrentNode();
            sourceNodes.detach();
        }
    }

    @Override
    public ElemTemplateElement appendChild(ElemTemplateElement newChild) {
        int type = newChild.getXSLToken();
        if (64 == type) {
            this.setSortElem((ElemSort)newChild);
            return newChild;
        }
        return super.appendChild(newChild);
    }

    @Override
    public void callChildVisitors(XSLTVisitor visitor, boolean callAttributes) {
        if (callAttributes && null != this.m_selectExpression) {
            this.m_selectExpression.callVisitors(this, visitor);
        }
        int length = this.getSortElemCount();
        for (int i = 0; i < length; ++i) {
            this.getSortElem(i).callVisitors(visitor);
        }
        super.callChildVisitors(visitor, callAttributes);
    }

    @Override
    public Expression getExpression() {
        return this.m_selectExpression;
    }

    @Override
    public void setExpression(Expression exp) {
        exp.exprSetParent(this);
        this.m_selectExpression = exp;
    }

    private void readObject(ObjectInputStream os) throws IOException, ClassNotFoundException {
        os.defaultReadObject();
        this.m_xpath = null;
    }
}

