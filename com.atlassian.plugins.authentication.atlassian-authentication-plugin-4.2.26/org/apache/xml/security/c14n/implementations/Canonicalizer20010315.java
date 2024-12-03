/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.c14n.implementations;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.helper.C14nHelper;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.apache.xml.security.c14n.implementations.NameSpaceSymbTable;
import org.apache.xml.security.c14n.implementations.XmlAttrStack;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class Canonicalizer20010315
extends CanonicalizerBase {
    private boolean firstCall = true;
    private final XmlAttrStack xmlattrStack;
    private final boolean c14n11;

    public Canonicalizer20010315(boolean includeComments) {
        this(includeComments, false);
    }

    public Canonicalizer20010315(boolean includeComments, boolean c14n11) {
        super(includeComments);
        this.xmlattrStack = new XmlAttrStack(c14n11);
        this.c14n11 = c14n11;
    }

    @Override
    public void engineCanonicalizeXPathNodeSet(Set<Node> xpathNodeSet, String inclusiveNamespaces, OutputStream writer) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }

    @Override
    public void engineCanonicalizeSubTree(Node rootNode, String inclusiveNamespaces, OutputStream writer) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }

    @Override
    public void engineCanonicalizeSubTree(Node rootNode, String inclusiveNamespaces, boolean propagateDefaultNamespace, OutputStream writer) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }

    @Override
    protected void outputAttributesSubtree(Element element, NameSpaceSymbTable ns, Map<String, byte[]> cache, OutputStream writer) throws CanonicalizationException, DOMException, IOException {
        if (!element.hasAttributes() && !this.firstCall) {
            return;
        }
        TreeSet<Attr> result = new TreeSet<Attr>(COMPARE);
        if (element.hasAttributes()) {
            NamedNodeMap attrs = element.getAttributes();
            int attrsLength = attrs.getLength();
            for (int i = 0; i < attrsLength; ++i) {
                Node n;
                Attr attribute = (Attr)attrs.item(i);
                String NUri = attribute.getNamespaceURI();
                String NName = attribute.getLocalName();
                String NValue = attribute.getValue();
                if (!"http://www.w3.org/2000/xmlns/".equals(NUri)) {
                    result.add(attribute);
                    continue;
                }
                if ("xml".equals(NName) && "http://www.w3.org/XML/1998/namespace".equals(NValue) || (n = ns.addMappingAndRender(NName, NValue, attribute)) == null) continue;
                result.add((Attr)n);
                if (!C14nHelper.namespaceIsRelative(attribute)) continue;
                Object[] exArgs = new Object[]{element.getTagName(), NName, attribute.getNodeValue()};
                throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", exArgs);
            }
        }
        if (this.firstCall) {
            ns.getUnrenderedNodes(result);
            this.xmlattrStack.getXmlnsAttr(result);
            this.firstCall = false;
        }
        for (Attr attr : result) {
            Canonicalizer20010315.outputAttrToWriter(attr.getNodeName(), attr.getNodeValue(), writer, cache);
        }
    }

    @Override
    protected void outputAttributes(Element element, NameSpaceSymbTable ns, Map<String, byte[]> cache, OutputStream writer) throws CanonicalizationException, DOMException, IOException {
        this.xmlattrStack.push(ns.getLevel());
        boolean isRealVisible = this.isVisibleDO(element, ns.getLevel()) == 1;
        TreeSet<Attr> result = new TreeSet<Attr>(COMPARE);
        if (element.hasAttributes()) {
            NamedNodeMap attrs = element.getAttributes();
            int attrsLength = attrs.getLength();
            for (int i = 0; i < attrsLength; ++i) {
                Attr attribute = (Attr)attrs.item(i);
                String NUri = attribute.getNamespaceURI();
                String NName = attribute.getLocalName();
                String NValue = attribute.getValue();
                if (!"http://www.w3.org/2000/xmlns/".equals(NUri)) {
                    if ("http://www.w3.org/XML/1998/namespace".equals(NUri)) {
                        if (this.c14n11 && "id".equals(NName)) {
                            if (!isRealVisible) continue;
                            result.add(attribute);
                            continue;
                        }
                        this.xmlattrStack.addXmlnsAttr(attribute);
                        continue;
                    }
                    if (!isRealVisible) continue;
                    result.add(attribute);
                    continue;
                }
                if ("xml".equals(NName) && "http://www.w3.org/XML/1998/namespace".equals(NValue)) continue;
                if (this.isVisible(attribute)) {
                    Node n;
                    if (!isRealVisible && ns.removeMappingIfRender(NName) || (n = ns.addMappingAndRender(NName, NValue, attribute)) == null) continue;
                    result.add((Attr)n);
                    if (!C14nHelper.namespaceIsRelative(attribute)) continue;
                    Object[] exArgs = new Object[]{element.getTagName(), NName, attribute.getNodeValue()};
                    throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", exArgs);
                }
                if (isRealVisible && !"xmlns".equals(NName)) {
                    ns.removeMapping(NName);
                    continue;
                }
                ns.addMapping(NName, NValue, attribute);
            }
        }
        if (isRealVisible) {
            Attr xmlns = element.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
            Node n = null;
            if (xmlns == null) {
                n = ns.getMapping("xmlns");
            } else if (!this.isVisible(xmlns)) {
                n = ns.addMappingAndRender("xmlns", "", this.getNullNode(xmlns.getOwnerDocument()));
            }
            if (n != null) {
                result.add((Attr)n);
            }
            this.xmlattrStack.getXmlnsAttr(result);
            ns.getUnrenderedNodes(result);
        }
        for (Attr attr : result) {
            Canonicalizer20010315.outputAttrToWriter(attr.getNodeName(), attr.getNodeValue(), writer, cache);
        }
    }

    @Override
    protected void circumventBugIfNeeded(XMLSignatureInput input) throws XMLParserException, IOException {
        if (!input.isNeedsToBeExpanded()) {
            return;
        }
        Document doc = null;
        doc = input.getSubNode() != null ? XMLUtils.getOwnerDocument(input.getSubNode()) : XMLUtils.getOwnerDocument(input.getNodeSet());
        XMLUtils.circumventBug2650(doc);
    }

    @Override
    protected void handleParent(Element e, NameSpaceSymbTable ns) {
        if (!e.hasAttributes() && e.getNamespaceURI() == null) {
            return;
        }
        this.xmlattrStack.push(-1);
        NamedNodeMap attrs = e.getAttributes();
        int attrsLength = attrs.getLength();
        for (int i = 0; i < attrsLength; ++i) {
            Attr attribute = (Attr)attrs.item(i);
            String NName = attribute.getLocalName();
            String NValue = attribute.getNodeValue();
            if ("http://www.w3.org/2000/xmlns/".equals(attribute.getNamespaceURI())) {
                if ("xml".equals(NName) && "http://www.w3.org/XML/1998/namespace".equals(NValue)) continue;
                ns.addMapping(NName, NValue, attribute);
                continue;
            }
            if (!"http://www.w3.org/XML/1998/namespace".equals(attribute.getNamespaceURI()) || this.c14n11 && "id".equals(NName)) continue;
            this.xmlattrStack.addXmlnsAttr(attribute);
        }
        if (e.getNamespaceURI() != null) {
            String Name;
            String NName = e.getPrefix();
            String NValue = e.getNamespaceURI();
            if (NName == null || NName.isEmpty()) {
                NName = "xmlns";
                Name = "xmlns";
            } else {
                Name = "xmlns:" + NName;
            }
            Attr n = e.getOwnerDocument().createAttributeNS("http://www.w3.org/2000/xmlns/", Name);
            n.setValue(NValue);
            ns.addMapping(NName, NValue, n);
        }
    }
}

