/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.dsig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.poi.poifs.crypt.dsig.SignatureInfo;
import org.apache.poi.poifs.crypt.dsig.SignatureMarshalListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeIterator;

public class SignatureMarshalDefaultListener
implements SignatureMarshalListener {
    private static final String OBJECT_TAG = "Object";
    private static final Set<String> IGNORE_NS = new HashSet<String>(Arrays.asList(null, "http://www.w3.org/2000/xmlns/", "http://www.w3.org/2000/09/xmldsig#"));
    private static final List<String> DIRECT_NS = Arrays.asList("http://schemas.openxmlformats.org/package/2006/digital-signature", "http://schemas.microsoft.com/office/2006/digsig");

    @Override
    public void handleElement(SignatureInfo signatureInfo, Document doc, EventTarget target, EventListener parentListener) {
        DocumentTraversal traversal = (DocumentTraversal)((Object)doc);
        Map<String, String> prefixCfg = signatureInfo.getSignatureConfig().getNamespacePrefixes();
        HashMap prefixUsed = new HashMap();
        SignatureMarshalDefaultListener.forEachElement(doc.getElementsByTagName(OBJECT_TAG), o -> SignatureMarshalDefaultListener.forEachElement(o.getChildNodes(), c -> {
            this.getAllNamespaces(traversal, (Element)c, prefixCfg, prefixUsed);
            prefixUsed.forEach((ns, prefix) -> SignatureMarshalDefaultListener.setXmlns(c, prefix, ns));
        }));
    }

    private static void forEachElement(NodeList nl, Consumer<Element> consumer) {
        int len = nl.getLength();
        for (int i = 0; i < len; ++i) {
            Node n = nl.item(i);
            if (!(n instanceof Element)) continue;
            consumer.accept((Element)n);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void getAllNamespaces(DocumentTraversal traversal, Element objNode, Map<String, String> prefixCfg, Map<String, String> prefixUsed) {
        prefixUsed.clear();
        NodeIterator iter = traversal.createNodeIterator(objNode, 1, null, false);
        try {
            Element node;
            while ((node = (Element)iter.nextNode()) != null) {
                this.setPrefix(node, prefixCfg, prefixUsed);
                NamedNodeMap nnm = node.getAttributes();
                int nnmLen = nnm.getLength();
                for (int j = 0; j < nnmLen; ++j) {
                    this.setPrefix(nnm.item(j), prefixCfg, prefixUsed);
                }
            }
        }
        finally {
            iter.detach();
        }
    }

    private void setPrefix(Node node, Map<String, String> prefixCfg, Map<String, String> prefixUsed) {
        String ns = node.getNamespaceURI();
        String prefix = prefixCfg.get(ns);
        if (IGNORE_NS.contains(ns)) {
            return;
        }
        if (prefix != null) {
            node.setPrefix(prefix);
        }
        if (DIRECT_NS.contains(ns)) {
            SignatureMarshalDefaultListener.setXmlns(node, prefix, ns);
        } else {
            prefixUsed.put(ns, prefix);
        }
    }

    private static void setXmlns(Node node, String prefix, String ns) {
        if (node instanceof Element && !ns.equals(node.getParentNode().getNamespaceURI())) {
            ((Element)node).setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns" + (prefix == null ? "" : ":" + prefix), ns);
        }
    }
}

