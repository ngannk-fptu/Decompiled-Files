/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.NamespaceMappings
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.serialize;

import javax.xml.transform.TransformerException;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.serializer.NamespaceMappings;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class SerializerUtils {
    public static void addAttribute(SerializationHandler handler, int attr) throws TransformerException {
        TransformerImpl transformer = (TransformerImpl)handler.getTransformer();
        DTM dtm = transformer.getXPathContext().getDTM(attr);
        if (SerializerUtils.isDefinedNSDecl(handler, attr, dtm)) {
            return;
        }
        String ns = dtm.getNamespaceURI(attr);
        if (ns == null) {
            ns = "";
        }
        try {
            handler.addAttribute(ns, dtm.getLocalName(attr), dtm.getNodeName(attr), "CDATA", dtm.getNodeValue(attr), false);
        }
        catch (SAXException sAXException) {
            // empty catch block
        }
    }

    public static void addAttributes(SerializationHandler handler, int src) throws TransformerException {
        TransformerImpl transformer = (TransformerImpl)handler.getTransformer();
        DTM dtm = transformer.getXPathContext().getDTM(src);
        int node = dtm.getFirstAttribute(src);
        while (-1 != node) {
            SerializerUtils.addAttribute(handler, node);
            node = dtm.getNextAttribute(node);
        }
    }

    public static void outputResultTreeFragment(SerializationHandler handler, XObject obj, XPathContext support) throws SAXException {
        int doc = obj.rtf();
        DTM dtm = support.getDTM(doc);
        if (null != dtm) {
            int n = dtm.getFirstChild(doc);
            while (-1 != n) {
                handler.flushPending();
                if (dtm.getNodeType(n) == 1 && dtm.getNamespaceURI(n) == null) {
                    handler.startPrefixMapping("", "");
                }
                dtm.dispatchToEvents(n, (ContentHandler)handler);
                n = dtm.getNextSibling(n);
            }
        }
    }

    public static void processNSDecls(SerializationHandler handler, int src, int type, DTM dtm) throws TransformerException {
        try {
            if (type == 1) {
                int namespace = dtm.getFirstNamespaceNode(src, true);
                while (-1 != namespace) {
                    String prefix = dtm.getNodeNameX(namespace);
                    String desturi = handler.getNamespaceURIFromPrefix(prefix);
                    String srcURI = dtm.getNodeValue(namespace);
                    if (!srcURI.equalsIgnoreCase(desturi)) {
                        handler.startPrefixMapping(prefix, srcURI, false);
                    }
                    namespace = dtm.getNextNamespaceNode(src, namespace, true);
                }
            } else if (type == 13) {
                String prefix = dtm.getNodeNameX(src);
                String desturi = handler.getNamespaceURIFromPrefix(prefix);
                String srcURI = dtm.getNodeValue(src);
                if (!srcURI.equalsIgnoreCase(desturi)) {
                    handler.startPrefixMapping(prefix, srcURI, false);
                }
            }
        }
        catch (SAXException se) {
            throw new TransformerException(se);
        }
    }

    public static boolean isDefinedNSDecl(SerializationHandler serializer, int attr, DTM dtm) {
        String prefix;
        String uri;
        return 13 == dtm.getNodeType(attr) && null != (uri = serializer.getNamespaceURIFromPrefix(prefix = dtm.getNodeNameX(attr))) && uri.equals(dtm.getStringValue(attr));
    }

    public static void ensureNamespaceDeclDeclared(SerializationHandler handler, DTM dtm, int namespace) throws SAXException {
        String foundURI;
        NamespaceMappings ns;
        String uri = dtm.getNodeValue(namespace);
        String prefix = dtm.getNodeNameX(namespace);
        if (!(uri == null || uri.length() <= 0 || null == prefix || (ns = handler.getNamespaceMappings()) == null || null != (foundURI = ns.lookupNamespace(prefix)) && foundURI.equals(uri))) {
            handler.startPrefixMapping(prefix, uri, false);
        }
    }
}

