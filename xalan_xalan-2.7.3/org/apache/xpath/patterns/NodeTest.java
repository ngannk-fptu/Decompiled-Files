/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.patterns;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

public class NodeTest
extends Expression {
    static final long serialVersionUID = -5736721866747906182L;
    public static final String WILD = "*";
    public static final String SUPPORTS_PRE_STRIPPING = "http://xml.apache.org/xpath/features/whitespace-pre-stripping";
    protected int m_whatToShow;
    public static final int SHOW_BYFUNCTION = 65536;
    String m_namespace;
    protected String m_name;
    XNumber m_score;
    public static final XNumber SCORE_NODETEST = new XNumber(-0.5);
    public static final XNumber SCORE_NSWILD = new XNumber(-0.25);
    public static final XNumber SCORE_QNAME = new XNumber(0.0);
    public static final XNumber SCORE_OTHER = new XNumber(0.5);
    public static final XNumber SCORE_NONE = new XNumber(Double.NEGATIVE_INFINITY);
    private boolean m_isTotallyWild;

    public int getWhatToShow() {
        return this.m_whatToShow;
    }

    public void setWhatToShow(int what) {
        this.m_whatToShow = what;
    }

    public String getNamespace() {
        return this.m_namespace;
    }

    public void setNamespace(String ns) {
        this.m_namespace = ns;
    }

    public String getLocalName() {
        return null == this.m_name ? "" : this.m_name;
    }

    public void setLocalName(String name) {
        this.m_name = name;
    }

    public NodeTest(int whatToShow, String namespace, String name) {
        this.initNodeTest(whatToShow, namespace, name);
    }

    public NodeTest(int whatToShow) {
        this.initNodeTest(whatToShow);
    }

    @Override
    public boolean deepEquals(Expression expr) {
        if (!this.isSameClass(expr)) {
            return false;
        }
        NodeTest nt = (NodeTest)expr;
        if (null != nt.m_name) {
            if (null == this.m_name) {
                return false;
            }
            if (!nt.m_name.equals(this.m_name)) {
                return false;
            }
        } else if (null != this.m_name) {
            return false;
        }
        if (null != nt.m_namespace) {
            if (null == this.m_namespace) {
                return false;
            }
            if (!nt.m_namespace.equals(this.m_namespace)) {
                return false;
            }
        } else if (null != this.m_namespace) {
            return false;
        }
        if (this.m_whatToShow != nt.m_whatToShow) {
            return false;
        }
        return this.m_isTotallyWild == nt.m_isTotallyWild;
    }

    public NodeTest() {
    }

    public void initNodeTest(int whatToShow) {
        this.m_whatToShow = whatToShow;
        this.calcScore();
    }

    public void initNodeTest(int whatToShow, String namespace, String name) {
        this.m_whatToShow = whatToShow;
        this.m_namespace = namespace;
        this.m_name = name;
        this.calcScore();
    }

    public XNumber getStaticScore() {
        return this.m_score;
    }

    public void setStaticScore(XNumber score) {
        this.m_score = score;
    }

    protected void calcScore() {
        this.m_score = this.m_namespace == null && this.m_name == null ? SCORE_NODETEST : ((this.m_namespace == WILD || this.m_namespace == null) && this.m_name == WILD ? SCORE_NODETEST : (this.m_namespace != WILD && this.m_name == WILD ? SCORE_NSWILD : SCORE_QNAME));
        this.m_isTotallyWild = this.m_namespace == null && this.m_name == WILD;
    }

    public double getDefaultScore() {
        return this.m_score.num();
    }

    public static int getNodeTypeTest(int whatToShow) {
        if (0 != (whatToShow & 1)) {
            return 1;
        }
        if (0 != (whatToShow & 2)) {
            return 2;
        }
        if (0 != (whatToShow & 4)) {
            return 3;
        }
        if (0 != (whatToShow & 0x100)) {
            return 9;
        }
        if (0 != (whatToShow & 0x400)) {
            return 11;
        }
        if (0 != (whatToShow & 0x1000)) {
            return 13;
        }
        if (0 != (whatToShow & 0x80)) {
            return 8;
        }
        if (0 != (whatToShow & 0x40)) {
            return 7;
        }
        if (0 != (whatToShow & 0x200)) {
            return 10;
        }
        if (0 != (whatToShow & 0x20)) {
            return 6;
        }
        if (0 != (whatToShow & 0x10)) {
            return 5;
        }
        if (0 != (whatToShow & 0x800)) {
            return 12;
        }
        if (0 != (whatToShow & 8)) {
            return 4;
        }
        return 0;
    }

    public static void debugWhatToShow(int whatToShow) {
        Vector<String> v = new Vector<String>();
        if (0 != (whatToShow & 2)) {
            v.addElement("SHOW_ATTRIBUTE");
        }
        if (0 != (whatToShow & 0x1000)) {
            v.addElement("SHOW_NAMESPACE");
        }
        if (0 != (whatToShow & 8)) {
            v.addElement("SHOW_CDATA_SECTION");
        }
        if (0 != (whatToShow & 0x80)) {
            v.addElement("SHOW_COMMENT");
        }
        if (0 != (whatToShow & 0x100)) {
            v.addElement("SHOW_DOCUMENT");
        }
        if (0 != (whatToShow & 0x400)) {
            v.addElement("SHOW_DOCUMENT_FRAGMENT");
        }
        if (0 != (whatToShow & 0x200)) {
            v.addElement("SHOW_DOCUMENT_TYPE");
        }
        if (0 != (whatToShow & 1)) {
            v.addElement("SHOW_ELEMENT");
        }
        if (0 != (whatToShow & 0x20)) {
            v.addElement("SHOW_ENTITY");
        }
        if (0 != (whatToShow & 0x10)) {
            v.addElement("SHOW_ENTITY_REFERENCE");
        }
        if (0 != (whatToShow & 0x800)) {
            v.addElement("SHOW_NOTATION");
        }
        if (0 != (whatToShow & 0x40)) {
            v.addElement("SHOW_PROCESSING_INSTRUCTION");
        }
        if (0 != (whatToShow & 4)) {
            v.addElement("SHOW_TEXT");
        }
        int n = v.size();
        for (int i = 0; i < n; ++i) {
            if (i > 0) {
                System.out.print(" | ");
            }
            System.out.print(v.elementAt(i));
        }
        if (0 == n) {
            System.out.print("empty whatToShow: " + whatToShow);
        }
        System.out.println();
    }

    private static final boolean subPartMatch(String p, String t) {
        return p == t || null != p && (t == WILD || p.equals(t));
    }

    private static final boolean subPartMatchNS(String p, String t) {
        return p == t || null != p && (p.length() > 0 ? t == WILD || p.equals(t) : null == t);
    }

    @Override
    public XObject execute(XPathContext xctxt, int context) throws TransformerException {
        DTM dtm = xctxt.getDTM(context);
        short nodeType = dtm.getNodeType(context);
        if (this.m_whatToShow == -1) {
            return this.m_score;
        }
        int nodeBit = this.m_whatToShow & 1 << nodeType - 1;
        switch (nodeBit) {
            case 256: 
            case 1024: {
                return SCORE_OTHER;
            }
            case 128: {
                return this.m_score;
            }
            case 4: 
            case 8: {
                return this.m_score;
            }
            case 64: {
                return NodeTest.subPartMatch(dtm.getNodeName(context), this.m_name) ? this.m_score : SCORE_NONE;
            }
            case 4096: {
                String ns = dtm.getLocalName(context);
                return NodeTest.subPartMatch(ns, this.m_name) ? this.m_score : SCORE_NONE;
            }
            case 1: 
            case 2: {
                return this.m_isTotallyWild || NodeTest.subPartMatchNS(dtm.getNamespaceURI(context), this.m_namespace) && NodeTest.subPartMatch(dtm.getLocalName(context), this.m_name) ? this.m_score : SCORE_NONE;
            }
        }
        return SCORE_NONE;
    }

    @Override
    public XObject execute(XPathContext xctxt, int context, DTM dtm, int expType) throws TransformerException {
        if (this.m_whatToShow == -1) {
            return this.m_score;
        }
        int nodeBit = this.m_whatToShow & 1 << dtm.getNodeType(context) - 1;
        switch (nodeBit) {
            case 256: 
            case 1024: {
                return SCORE_OTHER;
            }
            case 128: {
                return this.m_score;
            }
            case 4: 
            case 8: {
                return this.m_score;
            }
            case 64: {
                return NodeTest.subPartMatch(dtm.getNodeName(context), this.m_name) ? this.m_score : SCORE_NONE;
            }
            case 4096: {
                String ns = dtm.getLocalName(context);
                return NodeTest.subPartMatch(ns, this.m_name) ? this.m_score : SCORE_NONE;
            }
            case 1: 
            case 2: {
                return this.m_isTotallyWild || NodeTest.subPartMatchNS(dtm.getNamespaceURI(context), this.m_namespace) && NodeTest.subPartMatch(dtm.getLocalName(context), this.m_name) ? this.m_score : SCORE_NONE;
            }
        }
        return SCORE_NONE;
    }

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        return this.execute(xctxt, xctxt.getCurrentNode());
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
    }

    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
        this.assertion(false, "callVisitors should not be called for this object!!!");
    }
}

