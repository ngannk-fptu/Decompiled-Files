/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.objects;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.ref.DTMNodeIterator;
import org.apache.xml.dtm.ref.DTMNodeList;
import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.axes.NodeSequence;
import org.apache.xpath.objects.Comparator;
import org.apache.xpath.objects.EqualComparator;
import org.apache.xpath.objects.GreaterThanComparator;
import org.apache.xpath.objects.GreaterThanOrEqualComparator;
import org.apache.xpath.objects.LessThanComparator;
import org.apache.xpath.objects.LessThanOrEqualComparator;
import org.apache.xpath.objects.NotEqualComparator;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class XNodeSet
extends NodeSequence {
    static final long serialVersionUID = 1916026368035639667L;
    static final LessThanComparator S_LT = new LessThanComparator();
    static final LessThanOrEqualComparator S_LTE = new LessThanOrEqualComparator();
    static final GreaterThanComparator S_GT = new GreaterThanComparator();
    static final GreaterThanOrEqualComparator S_GTE = new GreaterThanOrEqualComparator();
    static final EqualComparator S_EQ = new EqualComparator();
    static final NotEqualComparator S_NEQ = new NotEqualComparator();

    protected XNodeSet() {
    }

    public XNodeSet(DTMIterator val) {
        if (val instanceof XNodeSet) {
            XNodeSet nodeSet = (XNodeSet)val;
            this.setIter(nodeSet.m_iter);
            this.m_dtmMgr = nodeSet.m_dtmMgr;
            this.m_last = nodeSet.m_last;
            if (!nodeSet.hasCache()) {
                nodeSet.setShouldCacheNodes(true);
            }
            this.setObject(nodeSet.getIteratorCache());
        } else {
            this.setIter(val);
        }
    }

    public XNodeSet(XNodeSet val) {
        this.setIter(val.m_iter);
        this.m_dtmMgr = val.m_dtmMgr;
        this.m_last = val.m_last;
        if (!val.hasCache()) {
            val.setShouldCacheNodes(true);
        }
        this.setObject(val.m_obj);
    }

    public XNodeSet(DTMManager dtmMgr) {
        this(-1, dtmMgr);
    }

    public XNodeSet(int n, DTMManager dtmMgr) {
        super(new NodeSetDTM(dtmMgr));
        this.m_dtmMgr = dtmMgr;
        if (-1 != n) {
            ((NodeSetDTM)this.m_obj).addNode(n);
            this.m_last = 1;
        } else {
            this.m_last = 0;
        }
    }

    @Override
    public int getType() {
        return 4;
    }

    @Override
    public String getTypeString() {
        return "#NODESET";
    }

    public double getNumberFromNode(int n) {
        XMLString xstr = this.m_dtmMgr.getDTM(n).getStringValue(n);
        return xstr.toDouble();
    }

    @Override
    public double num() {
        int node = this.item(0);
        return node != -1 ? this.getNumberFromNode(node) : Double.NaN;
    }

    @Override
    public double numWithSideEffects() {
        int node = this.nextNode();
        return node != -1 ? this.getNumberFromNode(node) : Double.NaN;
    }

    @Override
    public boolean bool() {
        return this.item(0) != -1;
    }

    @Override
    public boolean boolWithSideEffects() {
        return this.nextNode() != -1;
    }

    public XMLString getStringFromNode(int n) {
        if (-1 != n) {
            return this.m_dtmMgr.getDTM(n).getStringValue(n);
        }
        return XString.EMPTYSTRING;
    }

    @Override
    public void dispatchCharactersEvents(ContentHandler ch) throws SAXException {
        int node = this.item(0);
        if (node != -1) {
            this.m_dtmMgr.getDTM(node).dispatchCharactersEvents(node, ch, false);
        }
    }

    @Override
    public XMLString xstr() {
        int node = this.item(0);
        return node != -1 ? this.getStringFromNode(node) : XString.EMPTYSTRING;
    }

    @Override
    public void appendToFsb(FastStringBuffer fsb) {
        XString xstring = (XString)this.xstr();
        xstring.appendToFsb(fsb);
    }

    @Override
    public String str() {
        int node = this.item(0);
        return node != -1 ? this.getStringFromNode(node).toString() : "";
    }

    @Override
    public Object object() {
        if (null == this.m_obj) {
            return this;
        }
        return this.m_obj;
    }

    @Override
    public NodeIterator nodeset() throws TransformerException {
        return new DTMNodeIterator(this.iter());
    }

    @Override
    public NodeList nodelist() throws TransformerException {
        DTMNodeList nodelist = new DTMNodeList(this);
        XNodeSet clone = (XNodeSet)nodelist.getDTMIterator();
        this.SetVector(clone.getVector());
        return nodelist;
    }

    public DTMIterator iterRaw() {
        return this;
    }

    public void release(DTMIterator iter) {
    }

    @Override
    public DTMIterator iter() {
        try {
            if (this.hasCache()) {
                return this.cloneWithReset();
            }
            return this;
        }
        catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse.getMessage());
        }
    }

    @Override
    public XObject getFresh() {
        try {
            if (this.hasCache()) {
                return (XObject)((Object)this.cloneWithReset());
            }
            return this;
        }
        catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse.getMessage());
        }
    }

    @Override
    public NodeSetDTM mutableNodeset() {
        NodeSetDTM mnl;
        if (this.m_obj instanceof NodeSetDTM) {
            mnl = (NodeSetDTM)this.m_obj;
        } else {
            mnl = new NodeSetDTM(this.iter());
            this.setObject(mnl);
            this.setCurrentPos(0);
        }
        return mnl;
    }

    public boolean compare(XObject obj2, Comparator comparator) throws TransformerException {
        boolean result = false;
        int type = obj2.getType();
        if (4 == type) {
            int node1;
            DTMIterator list1 = this.iterRaw();
            DTMIterator list2 = ((XNodeSet)obj2).iterRaw();
            Vector<XMLString> node2Strings = null;
            block0: while (-1 != (node1 = list1.nextNode())) {
                XMLString s1 = this.getStringFromNode(node1);
                if (null == node2Strings) {
                    int node2;
                    while (-1 != (node2 = list2.nextNode())) {
                        XMLString s2 = this.getStringFromNode(node2);
                        if (comparator.compareStrings(s1, s2)) {
                            result = true;
                            continue block0;
                        }
                        if (null == node2Strings) {
                            node2Strings = new Vector<XMLString>();
                        }
                        node2Strings.addElement(s2);
                    }
                    continue;
                }
                int n = node2Strings.size();
                for (int i = 0; i < n; ++i) {
                    if (!comparator.compareStrings(s1, (XMLString)node2Strings.elementAt(i))) continue;
                    result = true;
                    continue block0;
                }
            }
            list1.reset();
            list2.reset();
        } else if (1 == type) {
            double num1 = this.bool() ? 1.0 : 0.0;
            double num2 = obj2.num();
            result = comparator.compareNumbers(num1, num2);
        } else if (2 == type) {
            int node;
            DTMIterator list1 = this.iterRaw();
            double num2 = obj2.num();
            while (-1 != (node = list1.nextNode())) {
                double num1 = this.getNumberFromNode(node);
                if (!comparator.compareNumbers(num1, num2)) continue;
                result = true;
                break;
            }
            list1.reset();
        } else if (5 == type) {
            int node;
            XMLString s2 = obj2.xstr();
            DTMIterator list1 = this.iterRaw();
            while (-1 != (node = list1.nextNode())) {
                XMLString s1 = this.getStringFromNode(node);
                if (!comparator.compareStrings(s1, s2)) continue;
                result = true;
                break;
            }
            list1.reset();
        } else if (3 == type) {
            int node;
            XMLString s2 = obj2.xstr();
            DTMIterator list1 = this.iterRaw();
            while (-1 != (node = list1.nextNode())) {
                XMLString s1 = this.getStringFromNode(node);
                if (!comparator.compareStrings(s1, s2)) continue;
                result = true;
                break;
            }
            list1.reset();
        } else {
            result = comparator.compareNumbers(this.num(), obj2.num());
        }
        return result;
    }

    @Override
    public boolean lessThan(XObject obj2) throws TransformerException {
        return this.compare(obj2, S_LT);
    }

    @Override
    public boolean lessThanOrEqual(XObject obj2) throws TransformerException {
        return this.compare(obj2, S_LTE);
    }

    @Override
    public boolean greaterThan(XObject obj2) throws TransformerException {
        return this.compare(obj2, S_GT);
    }

    @Override
    public boolean greaterThanOrEqual(XObject obj2) throws TransformerException {
        return this.compare(obj2, S_GTE);
    }

    @Override
    public boolean equals(XObject obj2) {
        try {
            return this.compare(obj2, S_EQ);
        }
        catch (TransformerException te) {
            throw new WrappedRuntimeException(te);
        }
    }

    @Override
    public boolean notEquals(XObject obj2) throws TransformerException {
        return this.compare(obj2, S_NEQ);
    }
}

