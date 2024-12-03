/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.objects;

import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.ref.DTMNodeIterator;
import org.apache.xml.dtm.ref.DTMNodeList;
import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.RTFIterator;
import org.apache.xpath.objects.DTMXRTreeFrag;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.w3c.dom.NodeList;

public class XRTreeFrag
extends XObject
implements Cloneable {
    static final long serialVersionUID = -3201553822254911567L;
    private DTMXRTreeFrag m_DTMXRTreeFrag;
    private int m_dtmRoot = -1;
    protected boolean m_allowRelease = false;
    private XMLString m_xmlStr = null;

    public XRTreeFrag(int root, XPathContext xctxt, ExpressionNode parent) {
        super(null);
        this.exprSetParent(parent);
        this.initDTM(root, xctxt);
    }

    public XRTreeFrag(int root, XPathContext xctxt) {
        super(null);
        this.initDTM(root, xctxt);
    }

    private final void initDTM(int root, XPathContext xctxt) {
        this.m_dtmRoot = root;
        DTM dtm = xctxt.getDTM(root);
        if (dtm != null) {
            this.m_DTMXRTreeFrag = xctxt.getDTMXRTreeFrag(xctxt.getDTMIdentity(dtm));
        }
    }

    @Override
    public Object object() {
        if (this.m_DTMXRTreeFrag.getXPathContext() != null) {
            return new DTMNodeIterator(new NodeSetDTM(this.m_dtmRoot, this.m_DTMXRTreeFrag.getXPathContext().getDTMManager()));
        }
        return super.object();
    }

    public XRTreeFrag(Expression expr) {
        super(expr);
    }

    @Override
    public void allowDetachToRelease(boolean allowRelease) {
        this.m_allowRelease = allowRelease;
    }

    @Override
    public void detach() {
        if (this.m_allowRelease) {
            this.m_DTMXRTreeFrag.destruct();
            this.setObject(null);
        }
    }

    @Override
    public int getType() {
        return 5;
    }

    @Override
    public String getTypeString() {
        return "#RTREEFRAG";
    }

    @Override
    public double num() throws TransformerException {
        XMLString s = this.xstr();
        return s.toDouble();
    }

    @Override
    public boolean bool() {
        return true;
    }

    @Override
    public XMLString xstr() {
        if (null == this.m_xmlStr) {
            this.m_xmlStr = this.m_DTMXRTreeFrag.getDTM().getStringValue(this.m_dtmRoot);
        }
        return this.m_xmlStr;
    }

    @Override
    public void appendToFsb(FastStringBuffer fsb) {
        XString xstring = (XString)this.xstr();
        xstring.appendToFsb(fsb);
    }

    @Override
    public String str() {
        String str = this.m_DTMXRTreeFrag.getDTM().getStringValue(this.m_dtmRoot).toString();
        return null == str ? "" : str;
    }

    @Override
    public int rtf() {
        return this.m_dtmRoot;
    }

    public DTMIterator asNodeIterator() {
        return new RTFIterator(this.m_dtmRoot, this.m_DTMXRTreeFrag.getXPathContext().getDTMManager());
    }

    public NodeList convertToNodeset() {
        if (this.m_obj instanceof NodeList) {
            return (NodeList)this.m_obj;
        }
        return new DTMNodeList(this.asNodeIterator());
    }

    @Override
    public boolean equals(XObject obj2) {
        try {
            if (4 == obj2.getType()) {
                return obj2.equals(this);
            }
            if (1 == obj2.getType()) {
                return this.bool() == obj2.bool();
            }
            if (2 == obj2.getType()) {
                return this.num() == obj2.num();
            }
            if (4 == obj2.getType()) {
                return this.xstr().equals(obj2.xstr());
            }
            if (3 == obj2.getType()) {
                return this.xstr().equals(obj2.xstr());
            }
            if (5 == obj2.getType()) {
                return this.xstr().equals(obj2.xstr());
            }
            return super.equals(obj2);
        }
        catch (TransformerException te) {
            throw new WrappedRuntimeException(te);
        }
    }
}

