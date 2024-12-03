/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.saxon.Configuration
 *  net.sf.saxon.dom.DOMNodeWrapper
 *  net.sf.saxon.om.Item
 *  net.sf.saxon.om.NodeInfo
 *  net.sf.saxon.om.Sequence
 *  net.sf.saxon.om.SequenceTool
 *  net.sf.saxon.sxpath.IndependentContext
 *  net.sf.saxon.sxpath.XPathDynamicContext
 *  net.sf.saxon.sxpath.XPathEvaluator
 *  net.sf.saxon.sxpath.XPathExpression
 *  net.sf.saxon.sxpath.XPathStaticContext
 *  net.sf.saxon.sxpath.XPathVariable
 *  net.sf.saxon.tree.wrapper.VirtualNode
 *  net.sf.saxon.value.DateTimeValue
 *  net.sf.saxon.value.GDateValue
 */
package org.apache.xmlbeans.impl.xpath.saxon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import net.sf.saxon.Configuration;
import net.sf.saxon.dom.DOMNodeWrapper;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceTool;
import net.sf.saxon.sxpath.IndependentContext;
import net.sf.saxon.sxpath.XPathDynamicContext;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.sxpath.XPathStaticContext;
import net.sf.saxon.sxpath.XPathVariable;
import net.sf.saxon.tree.wrapper.VirtualNode;
import net.sf.saxon.value.DateTimeValue;
import net.sf.saxon.value.GDateValue;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.store.Cur;
import org.apache.xmlbeans.impl.xpath.Path;
import org.apache.xmlbeans.impl.xpath.XPathEngine;
import org.apache.xmlbeans.impl.xpath.saxon.SaxonXPathEngine;
import org.w3c.dom.Node;

public class SaxonXPath
implements Path {
    private final Map<String, String> namespaceMap = new HashMap<String, String>();
    private String path;
    private String contextVar;
    private String defaultNS;

    public SaxonXPath(String path, String contextVar, Map<String, String> namespaceMap) {
        this.path = path;
        this.contextVar = contextVar;
        this.defaultNS = namespaceMap.get("$xmlbeans!default_uri");
        this.namespaceMap.putAll(namespaceMap);
        this.namespaceMap.remove("$xmlbeans!default_uri");
    }

    @Override
    public XPathEngine execute(Cur c, XmlOptions options) {
        return new SaxonXPathEngine(this, c);
    }

    public List selectNodes(Object node) {
        try {
            Node contextNode = (Node)node;
            Configuration config = new Configuration();
            IndependentContext sc = new IndependentContext(config);
            if (this.defaultNS != null) {
                sc.setDefaultElementNamespace(this.defaultNS);
            }
            this.namespaceMap.forEach((arg_0, arg_1) -> ((IndependentContext)sc).declareNamespace(arg_0, arg_1));
            NodeInfo contextItem = config.unravel((Source)new DOMSource(contextNode));
            XPathEvaluator xpe = new XPathEvaluator(config);
            xpe.setStaticContext((XPathStaticContext)sc);
            XPathVariable thisVar = sc.declareVariable("", this.contextVar);
            XPathExpression xpath = xpe.createExpression(this.path);
            XPathDynamicContext dc = xpath.createDynamicContext(null);
            dc.setContextItem((Item)contextItem);
            dc.setVariable(thisVar, (Sequence)contextItem);
            List saxonNodes = xpath.evaluate(dc);
            ArrayList<Object> retNodes = new ArrayList<Object>(saxonNodes.size());
            for (Item o : saxonNodes) {
                if (o instanceof DOMNodeWrapper) {
                    Node n = SaxonXPath.getUnderlyingNode((VirtualNode)((DOMNodeWrapper)o));
                    retNodes.add(n);
                    continue;
                }
                if (o instanceof NodeInfo) {
                    retNodes.add(o.getStringValue());
                    continue;
                }
                if (o instanceof GDateValue) {
                    retNodes.add(o);
                    continue;
                }
                if (o instanceof DateTimeValue) {
                    retNodes.add(o);
                    continue;
                }
                retNodes.add(SequenceTool.convertToJava((Item)o));
            }
            return retNodes;
        }
        catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public List selectPath(Object node) {
        return this.selectNodes(node);
    }

    private static Node getUnderlyingNode(VirtualNode v) {
        Object o = v;
        while (o instanceof VirtualNode) {
            o = o.getUnderlyingNode();
        }
        return (Node)o;
    }
}

