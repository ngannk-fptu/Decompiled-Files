/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.extensions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.xml.transform.TransformerException;
import org.apache.xalan.serialize.SerializerUtils;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.transformer.ClonerToResultTree;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.QName;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.DescendantIterator;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.axes.OneStepIterator;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.objects.XString;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

public class XSLProcessorContext {
    private TransformerImpl transformer;
    private Stylesheet stylesheetTree;
    private DTM sourceTree;
    private int sourceNode;
    private QName mode;

    public XSLProcessorContext(TransformerImpl transformer, Stylesheet stylesheetTree) {
        this.transformer = transformer;
        this.stylesheetTree = stylesheetTree;
        XPathContext xctxt = transformer.getXPathContext();
        this.mode = transformer.getMode();
        this.sourceNode = xctxt.getCurrentNode();
        this.sourceTree = xctxt.getDTM(this.sourceNode);
    }

    public TransformerImpl getTransformer() {
        return this.transformer;
    }

    public Stylesheet getStylesheet() {
        return this.stylesheetTree;
    }

    public Node getSourceTree() {
        return this.sourceTree.getNode(this.sourceTree.getDocumentRoot(this.sourceNode));
    }

    public Node getContextNode() {
        return this.sourceTree.getNode(this.sourceNode);
    }

    public QName getMode() {
        return this.mode;
    }

    public void outputToResultTree(Stylesheet stylesheetTree, Object obj) throws TransformerException, MalformedURLException, FileNotFoundException, IOException {
        try {
            LocPathIterator iterator;
            XObject value;
            SerializationHandler rtreeHandler = this.transformer.getResultTreeHandler();
            XPathContext xctxt = this.transformer.getXPathContext();
            if (obj instanceof XObject) {
                value = (XObject)obj;
            } else if (obj instanceof String) {
                value = new XString((String)obj);
            } else if (obj instanceof Boolean) {
                value = new XBoolean((boolean)((Boolean)obj));
            } else if (obj instanceof Double) {
                value = new XNumber((Double)obj);
            } else if (obj instanceof DocumentFragment) {
                int handle = xctxt.getDTMHandleFromNode((DocumentFragment)obj);
                value = new XRTreeFrag(handle, xctxt);
            } else if (obj instanceof DTM) {
                DTM dtm = (DTM)obj;
                iterator = new DescendantIterator();
                iterator.setRoot(dtm.getDocument(), xctxt);
                value = new XNodeSet(iterator);
            } else if (obj instanceof DTMAxisIterator) {
                DTMAxisIterator iter = (DTMAxisIterator)obj;
                iterator = new OneStepIterator(iter, -1);
                value = new XNodeSet(iterator);
            } else {
                value = obj instanceof DTMIterator ? new XNodeSet((DTMIterator)obj) : (obj instanceof NodeIterator ? new XNodeSet(new NodeSetDTM((NodeIterator)obj, xctxt)) : (obj instanceof Node ? new XNodeSet(xctxt.getDTMHandleFromNode((Node)obj), xctxt.getDTMManager()) : new XString(obj.toString())));
            }
            int type = value.getType();
            switch (type) {
                case 1: 
                case 2: 
                case 3: {
                    String s = value.str();
                    rtreeHandler.characters(s.toCharArray(), 0, s.length());
                    break;
                }
                case 4: {
                    int pos;
                    DTMIterator nl = value.iter();
                    while (-1 != (pos = nl.nextNode())) {
                        DTM dtm = nl.getDTM(pos);
                        int top = pos;
                        while (-1 != pos) {
                            rtreeHandler.flushPending();
                            ClonerToResultTree.cloneToResultTree(pos, dtm.getNodeType(pos), dtm, rtreeHandler, true);
                            int nextNode = dtm.getFirstChild(pos);
                            while (-1 == nextNode) {
                                if (1 == dtm.getNodeType(pos)) {
                                    rtreeHandler.endElement("", "", dtm.getNodeName(pos));
                                }
                                if (top == pos) break;
                                nextNode = dtm.getNextSibling(pos);
                                if (-1 != nextNode || top != (pos = dtm.getParent(pos))) continue;
                                if (1 == dtm.getNodeType(pos)) {
                                    rtreeHandler.endElement("", "", dtm.getNodeName(pos));
                                }
                                nextNode = -1;
                                break;
                            }
                            pos = nextNode;
                        }
                    }
                    break;
                }
                case 5: {
                    SerializerUtils.outputResultTreeFragment(rtreeHandler, value, this.transformer.getXPathContext());
                }
            }
        }
        catch (SAXException se) {
            throw new TransformerException(se);
        }
    }
}

