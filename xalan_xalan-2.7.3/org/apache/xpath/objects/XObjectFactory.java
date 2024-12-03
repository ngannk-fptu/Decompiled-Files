/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.objects;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.OneStepIterator;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

public class XObjectFactory {
    public static XObject create(Object val) {
        XObject result = val instanceof XObject ? (XObject)val : (val instanceof String ? new XString((String)val) : (val instanceof Boolean ? new XBoolean((Boolean)val) : (val instanceof Double ? new XNumber((Double)val) : new XObject(val))));
        return result;
    }

    public static XObject create(Object val, XPathContext xctxt) {
        XObject result;
        if (val instanceof XObject) {
            result = (XObject)val;
        } else if (val instanceof String) {
            result = new XString((String)val);
        } else if (val instanceof Boolean) {
            result = new XBoolean((Boolean)val);
        } else if (val instanceof Number) {
            result = new XNumber((Number)val);
        } else if (val instanceof DTM) {
            DTM dtm = (DTM)val;
            try {
                int dtmRoot = dtm.getDocument();
                DTMAxisIterator iter = dtm.getAxisIterator(13);
                iter.setStartNode(dtmRoot);
                OneStepIterator iterator = new OneStepIterator(iter, 13);
                iterator.setRoot(dtmRoot, xctxt);
                result = new XNodeSet(iterator);
            }
            catch (Exception ex) {
                throw new WrappedRuntimeException(ex);
            }
        } else if (val instanceof DTMAxisIterator) {
            DTMAxisIterator iter = (DTMAxisIterator)val;
            try {
                OneStepIterator iterator = new OneStepIterator(iter, 13);
                iterator.setRoot(iter.getStartNode(), xctxt);
                result = new XNodeSet(iterator);
            }
            catch (Exception ex) {
                throw new WrappedRuntimeException(ex);
            }
        } else {
            result = val instanceof DTMIterator ? new XNodeSet((DTMIterator)val) : (val instanceof Node ? new XNodeSetForDOM((Node)val, (DTMManager)xctxt) : (val instanceof NodeList ? new XNodeSetForDOM((NodeList)val, xctxt) : (val instanceof NodeIterator ? new XNodeSetForDOM((NodeIterator)val, xctxt) : new XObject(val))));
        }
        return result;
    }
}

