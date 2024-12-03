/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.c14n.implementations;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.helper.C14nHelper;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.apache.xml.security.c14n.implementations.NameSpaceSymbTable;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class Canonicalizer20010315Excl
extends CanonicalizerBase {
    private SortedSet<String> inclusiveNSSet = Collections.emptySortedSet();
    private boolean propagateDefaultNamespace = false;

    public Canonicalizer20010315Excl(boolean includeComments) {
        super(includeComments);
    }

    @Override
    public void engineCanonicalizeSubTree(Node rootNode, OutputStream writer) throws CanonicalizationException {
        this.engineCanonicalizeSubTree(rootNode, "", null, writer);
    }

    @Override
    public void engineCanonicalizeSubTree(Node rootNode, String inclusiveNamespaces, OutputStream writer) throws CanonicalizationException {
        this.engineCanonicalizeSubTree(rootNode, inclusiveNamespaces, null, writer);
    }

    @Override
    public void engineCanonicalizeSubTree(Node rootNode, String inclusiveNamespaces, boolean propagateDefaultNamespace, OutputStream writer) throws CanonicalizationException {
        this.propagateDefaultNamespace = propagateDefaultNamespace;
        this.engineCanonicalizeSubTree(rootNode, inclusiveNamespaces, null, writer);
    }

    public void engineCanonicalizeSubTree(Node rootNode, String inclusiveNamespaces, Node excl, OutputStream writer) throws CanonicalizationException {
        this.inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);
        super.engineCanonicalizeSubTree(rootNode, excl, writer);
    }

    public void engineCanonicalize(XMLSignatureInput rootNode, String inclusiveNamespaces, OutputStream writer, boolean secureValidation) throws CanonicalizationException {
        this.inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);
        super.engineCanonicalize(rootNode, writer, secureValidation);
    }

    @Override
    public void engineCanonicalizeXPathNodeSet(Set<Node> xpathNodeSet, String inclusiveNamespaces, OutputStream writer) throws CanonicalizationException {
        this.inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);
        super.engineCanonicalizeXPathNodeSet(xpathNodeSet, writer);
    }

    @Override
    protected void outputAttributesSubtree(Element element, NameSpaceSymbTable ns, Map<String, byte[]> cache, OutputStream writer) throws CanonicalizationException, DOMException, IOException {
        TreeSet<Attr> result = new TreeSet<Attr>(COMPARE);
        TreeSet<String> visiblyUtilized = new TreeSet<String>();
        if (!this.inclusiveNSSet.isEmpty()) {
            visiblyUtilized.addAll(this.inclusiveNSSet);
        }
        if (element.hasAttributes()) {
            NamedNodeMap attrs = element.getAttributes();
            int attrsLength = attrs.getLength();
            for (int i = 0; i < attrsLength; ++i) {
                Attr attribute = (Attr)attrs.item(i);
                String NName = attribute.getLocalName();
                String NNodeValue = attribute.getNodeValue();
                if (!"http://www.w3.org/2000/xmlns/".equals(attribute.getNamespaceURI())) {
                    String prefix = attribute.getPrefix();
                    if (prefix != null && !prefix.equals("xml") && !prefix.equals("xmlns")) {
                        visiblyUtilized.add(prefix);
                    }
                    result.add(attribute);
                    continue;
                }
                if ("xml".equals(NName) && "http://www.w3.org/XML/1998/namespace".equals(NNodeValue) || !ns.addMapping(NName, NNodeValue, attribute) || !C14nHelper.namespaceIsRelative(NNodeValue)) continue;
                Object[] exArgs = new Object[]{element.getTagName(), NName, attribute.getNodeValue()};
                throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", exArgs);
            }
        }
        if (this.propagateDefaultNamespace && ns.getLevel() == 1 && this.inclusiveNSSet.contains("xmlns") && ns.getMappingWithoutRendered("xmlns") == null) {
            ns.removeMapping("xmlns");
            ns.addMapping("xmlns", "", this.getNullNode(element.getOwnerDocument()));
        }
        String prefix = null;
        prefix = element.getNamespaceURI() != null && element.getPrefix() != null && element.getPrefix().length() != 0 ? element.getPrefix() : "xmlns";
        visiblyUtilized.add(prefix);
        for (String s : visiblyUtilized) {
            Attr key = ns.getMapping(s);
            if (key == null) continue;
            result.add(key);
        }
        for (Attr attr : result) {
            Canonicalizer20010315Excl.outputAttrToWriter(attr.getNodeName(), attr.getNodeValue(), writer, cache);
        }
    }

    @Override
    protected void outputAttributes(Element element, NameSpaceSymbTable ns, Map<String, byte[]> cache, OutputStream writer) throws CanonicalizationException, DOMException, IOException {
        boolean isOutputElement;
        TreeSet<Attr> result = new TreeSet<Attr>(COMPARE);
        TreeSet<String> visiblyUtilized = null;
        boolean bl = isOutputElement = this.isVisibleDO(element, ns.getLevel()) == 1;
        if (isOutputElement) {
            visiblyUtilized = new TreeSet<String>();
            if (!this.inclusiveNSSet.isEmpty()) {
                visiblyUtilized.addAll(this.inclusiveNSSet);
            }
        }
        if (element.hasAttributes()) {
            NamedNodeMap attrs = element.getAttributes();
            int attrsLength = attrs.getLength();
            for (int i = 0; i < attrsLength; ++i) {
                Node n;
                Attr attribute = (Attr)attrs.item(i);
                String NName = attribute.getLocalName();
                String NNodeValue = attribute.getNodeValue();
                if (!"http://www.w3.org/2000/xmlns/".equals(attribute.getNamespaceURI())) {
                    if (!this.isVisible(attribute) || !isOutputElement) continue;
                    String prefix = attribute.getPrefix();
                    if (prefix != null && !prefix.equals("xml") && !prefix.equals("xmlns")) {
                        visiblyUtilized.add(prefix);
                    }
                    result.add(attribute);
                    continue;
                }
                if (isOutputElement && !this.isVisible(attribute) && !"xmlns".equals(NName)) {
                    ns.removeMappingIfNotRender(NName);
                    continue;
                }
                if (!isOutputElement && this.isVisible(attribute) && this.inclusiveNSSet.contains(NName) && !ns.removeMappingIfRender(NName) && (n = ns.addMappingAndRender(NName, NNodeValue, attribute)) != null) {
                    result.add((Attr)n);
                    if (C14nHelper.namespaceIsRelative(attribute)) {
                        Object[] exArgs = new Object[]{element.getTagName(), NName, attribute.getNodeValue()};
                        throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", exArgs);
                    }
                }
                if (!ns.addMapping(NName, NNodeValue, attribute) || !C14nHelper.namespaceIsRelative(NNodeValue)) continue;
                Object[] exArgs = new Object[]{element.getTagName(), NName, attribute.getNodeValue()};
                throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", exArgs);
            }
        }
        if (isOutputElement) {
            Attr xmlns = element.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
            if (xmlns != null && !this.isVisible(xmlns)) {
                ns.addMapping("xmlns", "", this.getNullNode(xmlns.getOwnerDocument()));
            }
            String prefix = null;
            prefix = element.getNamespaceURI() != null && element.getPrefix() != null && element.getPrefix().length() != 0 ? element.getPrefix() : "xmlns";
            visiblyUtilized.add(prefix);
            for (String s : visiblyUtilized) {
                Attr key = ns.getMapping(s);
                if (key == null) continue;
                result.add(key);
            }
        }
        for (Attr attr : result) {
            Canonicalizer20010315Excl.outputAttrToWriter(attr.getNodeName(), attr.getNodeValue(), writer, cache);
        }
    }

    @Override
    protected void circumventBugIfNeeded(XMLSignatureInput input) throws XMLParserException, IOException {
        if (!input.isNeedsToBeExpanded() || this.inclusiveNSSet.isEmpty()) {
            return;
        }
        Document doc = null;
        doc = input.getSubNode() != null ? XMLUtils.getOwnerDocument(input.getSubNode()) : XMLUtils.getOwnerDocument(input.getNodeSet());
        XMLUtils.circumventBug2650(doc);
    }
}

