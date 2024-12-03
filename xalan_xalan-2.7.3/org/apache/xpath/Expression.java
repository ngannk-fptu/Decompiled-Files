/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath;

import java.io.Serializable;
import java.util.Vector;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitable;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class Expression
implements Serializable,
ExpressionNode,
XPathVisitable {
    static final long serialVersionUID = 565665869777906902L;
    private ExpressionNode m_parent;

    public boolean canTraverseOutsideSubtree() {
        return false;
    }

    public XObject execute(XPathContext xctxt, int currentNode) throws TransformerException {
        return this.execute(xctxt);
    }

    public XObject execute(XPathContext xctxt, int currentNode, DTM dtm, int expType) throws TransformerException {
        return this.execute(xctxt);
    }

    public abstract XObject execute(XPathContext var1) throws TransformerException;

    public XObject execute(XPathContext xctxt, boolean destructiveOK) throws TransformerException {
        return this.execute(xctxt);
    }

    public double num(XPathContext xctxt) throws TransformerException {
        return this.execute(xctxt).num();
    }

    public boolean bool(XPathContext xctxt) throws TransformerException {
        return this.execute(xctxt).bool();
    }

    public XMLString xstr(XPathContext xctxt) throws TransformerException {
        return this.execute(xctxt).xstr();
    }

    public boolean isNodesetExpr() {
        return false;
    }

    public int asNode(XPathContext xctxt) throws TransformerException {
        DTMIterator iter = this.execute(xctxt).iter();
        return iter.nextNode();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DTMIterator asIterator(XPathContext xctxt, int contextNode) throws TransformerException {
        try {
            xctxt.pushCurrentNodeAndExpression(contextNode, contextNode);
            DTMIterator dTMIterator = this.execute(xctxt).iter();
            return dTMIterator;
        }
        finally {
            xctxt.popCurrentNodeAndExpression();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DTMIterator asIteratorRaw(XPathContext xctxt, int contextNode) throws TransformerException {
        try {
            xctxt.pushCurrentNodeAndExpression(contextNode, contextNode);
            XNodeSet nodeset = (XNodeSet)this.execute(xctxt);
            DTMIterator dTMIterator = nodeset.iterRaw();
            return dTMIterator;
        }
        finally {
            xctxt.popCurrentNodeAndExpression();
        }
    }

    public void executeCharsToContentHandler(XPathContext xctxt, ContentHandler handler) throws TransformerException, SAXException {
        XObject obj = this.execute(xctxt);
        obj.dispatchCharactersEvents(handler);
        obj.detach();
    }

    public boolean isStableNumber() {
        return false;
    }

    public abstract void fixupVariables(Vector var1, int var2);

    public abstract boolean deepEquals(Expression var1);

    protected final boolean isSameClass(Expression expr) {
        if (null == expr) {
            return false;
        }
        return this.getClass() == expr.getClass();
    }

    public void warn(XPathContext xctxt, String msg, Object[] args) throws TransformerException {
        String fmsg = XSLMessages.createXPATHWarning(msg, args);
        if (null != xctxt) {
            ErrorListener eh = xctxt.getErrorListener();
            eh.warning(new TransformerException(fmsg, xctxt.getSAXLocator()));
        }
    }

    public void assertion(boolean b, String msg) {
        if (!b) {
            String fMsg = XSLMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[]{msg});
            throw new RuntimeException(fMsg);
        }
    }

    public void error(XPathContext xctxt, String msg, Object[] args) throws TransformerException {
        String fmsg = XSLMessages.createXPATHMessage(msg, args);
        if (null != xctxt) {
            ErrorListener eh = xctxt.getErrorListener();
            TransformerException te = new TransformerException(fmsg, this);
            eh.fatalError(te);
        }
    }

    public ExpressionNode getExpressionOwner() {
        ExpressionNode parent;
        for (parent = this.exprGetParent(); null != parent && parent instanceof Expression; parent = parent.exprGetParent()) {
        }
        return parent;
    }

    @Override
    public void exprSetParent(ExpressionNode n) {
        this.assertion(n != this, "Can not parent an expression to itself!");
        this.m_parent = n;
    }

    @Override
    public ExpressionNode exprGetParent() {
        return this.m_parent;
    }

    @Override
    public void exprAddChild(ExpressionNode n, int i) {
        this.assertion(false, "exprAddChild method not implemented!");
    }

    @Override
    public ExpressionNode exprGetChild(int i) {
        return null;
    }

    @Override
    public int exprGetNumChildren() {
        return 0;
    }

    @Override
    public String getPublicId() {
        if (null == this.m_parent) {
            return null;
        }
        return this.m_parent.getPublicId();
    }

    @Override
    public String getSystemId() {
        if (null == this.m_parent) {
            return null;
        }
        return this.m_parent.getSystemId();
    }

    @Override
    public int getLineNumber() {
        if (null == this.m_parent) {
            return 0;
        }
        return this.m_parent.getLineNumber();
    }

    @Override
    public int getColumnNumber() {
        if (null == this.m_parent) {
            return 0;
        }
        return this.m_parent.getColumnNumber();
    }
}

