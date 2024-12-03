/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.c14n.implementations;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.CanonicalizerSpi;
import org.apache.xml.security.c14n.helper.AttrCompare;
import org.apache.xml.security.c14n.implementations.NameSpaceSymbTable;
import org.apache.xml.security.c14n.implementations.UtfHelpper;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.signature.NodeFilter;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

public abstract class CanonicalizerBase
extends CanonicalizerSpi {
    public static final String XML = "xml";
    public static final String XMLNS = "xmlns";
    public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
    public static final String XML_LANG_URI = "http://www.w3.org/XML/1998/namespace";
    protected static final AttrCompare COMPARE = new AttrCompare();
    protected static final int NODE_BEFORE_DOCUMENT_ELEMENT = -1;
    protected static final int NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT = 0;
    protected static final int NODE_AFTER_DOCUMENT_ELEMENT = 1;
    private static final byte[] END_PI = new byte[]{63, 62};
    private static final byte[] BEGIN_PI = new byte[]{60, 63};
    private static final byte[] END_COMM = new byte[]{45, 45, 62};
    private static final byte[] BEGIN_COMM = new byte[]{60, 33, 45, 45};
    private static final byte[] XA = new byte[]{38, 35, 120, 65, 59};
    private static final byte[] X9 = new byte[]{38, 35, 120, 57, 59};
    private static final byte[] QUOT = new byte[]{38, 113, 117, 111, 116, 59};
    private static final byte[] XD = new byte[]{38, 35, 120, 68, 59};
    private static final byte[] GT = new byte[]{38, 103, 116, 59};
    private static final byte[] LT = new byte[]{38, 108, 116, 59};
    private static final byte[] END_TAG = new byte[]{60, 47};
    private static final byte[] AMP = new byte[]{38, 97, 109, 112, 59};
    private static final byte[] EQUALS_STR = new byte[]{61, 34};
    private boolean includeComments;
    private List<NodeFilter> nodeFilter;
    private Set<Node> xpathNodeSet;
    private Attr nullNode;

    protected CanonicalizerBase(boolean includeComments) {
        this.includeComments = includeComments;
    }

    @Override
    public void engineCanonicalizeSubTree(Node rootNode, OutputStream writer) throws CanonicalizationException {
        this.engineCanonicalizeSubTree(rootNode, (Node)null, writer);
    }

    @Override
    public void engineCanonicalizeXPathNodeSet(Set<Node> xpathNodeSet, OutputStream writer) throws CanonicalizationException {
        this.xpathNodeSet = xpathNodeSet;
        this.engineCanonicalizeXPathNodeSetInternal(XMLUtils.getOwnerDocument(this.xpathNodeSet), writer);
    }

    public void engineCanonicalize(XMLSignatureInput input, OutputStream writer, boolean secureValidation) throws CanonicalizationException {
        try {
            if (input.isExcludeComments()) {
                this.includeComments = false;
            }
            if (input.isOctetStream()) {
                this.engineCanonicalize(input.getBytes(), writer, secureValidation);
            } else if (input.isElement()) {
                this.engineCanonicalizeSubTree(input.getSubNode(), input.getExcludeNode(), writer);
            } else if (input.isNodeSet()) {
                this.nodeFilter = input.getNodeFilters();
                this.circumventBugIfNeeded(input);
                if (input.getSubNode() != null) {
                    this.engineCanonicalizeXPathNodeSetInternal(input.getSubNode(), writer);
                } else {
                    this.engineCanonicalizeXPathNodeSet(input.getNodeSet(), writer);
                }
            }
        }
        catch (IOException | XMLParserException ex) {
            throw new CanonicalizationException(ex);
        }
    }

    protected void engineCanonicalizeSubTree(Node rootNode, Node excludeNode, OutputStream writer) throws CanonicalizationException {
        try {
            NameSpaceSymbTable ns = new NameSpaceSymbTable();
            int nodeLevel = -1;
            if (rootNode != null && 1 == rootNode.getNodeType()) {
                this.getParentNameSpaces((Element)rootNode, ns);
                nodeLevel = 0;
            }
            this.canonicalizeSubTree(rootNode, ns, rootNode, nodeLevel, excludeNode, writer);
            writer.flush();
        }
        catch (UnsupportedEncodingException ex) {
            throw new CanonicalizationException(ex);
        }
        catch (IOException ex) {
            throw new CanonicalizationException(ex);
        }
    }

    private void canonicalizeSubTree(Node currentNode, NameSpaceSymbTable ns, Node endnode, int documentLevel, Node excludeNode, OutputStream writer) throws CanonicalizationException, IOException {
        if (currentNode == null || this.isVisibleInt(currentNode) == -1) {
            return;
        }
        Node sibling = null;
        Node parentNode = null;
        HashMap<String, byte[]> cache = new HashMap<String, byte[]>();
        while (true) {
            switch (currentNode.getNodeType()) {
                case 2: 
                case 6: 
                case 12: {
                    throw new CanonicalizationException("empty", new Object[]{"illegal node type during traversal"});
                }
                case 9: 
                case 11: {
                    ns.outputNodePush();
                    sibling = currentNode.getFirstChild();
                    break;
                }
                case 8: {
                    if (!this.includeComments) break;
                    this.outputCommentToWriter((Comment)currentNode, writer, documentLevel);
                    break;
                }
                case 7: {
                    this.outputPItoWriter((ProcessingInstruction)currentNode, writer, documentLevel);
                    break;
                }
                case 3: 
                case 4: {
                    CanonicalizerBase.outputTextToWriter(currentNode.getNodeValue(), writer);
                    break;
                }
                case 1: {
                    documentLevel = 0;
                    if (currentNode == excludeNode) break;
                    Element currentElement = (Element)currentNode;
                    ns.outputNodePush();
                    writer.write(60);
                    String name = currentElement.getTagName();
                    UtfHelpper.writeByte(name, writer, cache);
                    this.outputAttributesSubtree(currentElement, ns, cache, writer);
                    writer.write(62);
                    sibling = currentNode.getFirstChild();
                    if (sibling == null) {
                        writer.write((byte[])END_TAG.clone());
                        UtfHelpper.writeStringToUtf8(name, writer);
                        writer.write(62);
                        ns.outputNodePop();
                        if (parentNode == null) break;
                        sibling = currentNode.getNextSibling();
                        break;
                    }
                    parentNode = currentElement;
                    break;
                }
            }
            while (sibling == null && parentNode != null) {
                writer.write((byte[])END_TAG.clone());
                UtfHelpper.writeByte(((Element)parentNode).getTagName(), writer, cache);
                writer.write(62);
                ns.outputNodePop();
                if (parentNode == endnode) {
                    return;
                }
                sibling = parentNode.getNextSibling();
                if ((parentNode = parentNode.getParentNode()) != null && 1 == parentNode.getNodeType()) continue;
                documentLevel = 1;
                parentNode = null;
            }
            if (sibling == null) {
                return;
            }
            currentNode = sibling;
            sibling = currentNode.getNextSibling();
        }
    }

    private void engineCanonicalizeXPathNodeSetInternal(Node doc, OutputStream writer) throws CanonicalizationException {
        try {
            this.canonicalizeXPathNodeSet(doc, doc, writer);
            writer.flush();
        }
        catch (IOException ex) {
            throw new CanonicalizationException(ex);
        }
    }

    private void canonicalizeXPathNodeSet(Node currentNode, Node endnode, OutputStream writer) throws CanonicalizationException, IOException {
        if (this.isVisibleInt(currentNode) == -1) {
            return;
        }
        boolean currentNodeIsVisible = false;
        NameSpaceSymbTable ns = new NameSpaceSymbTable();
        if (currentNode != null && 1 == currentNode.getNodeType()) {
            this.getParentNameSpaces((Element)currentNode, ns);
        }
        if (currentNode == null) {
            return;
        }
        Node sibling = null;
        Node parentNode = null;
        int documentLevel = -1;
        HashMap<String, byte[]> cache = new HashMap<String, byte[]>();
        while (true) {
            switch (currentNode.getNodeType()) {
                case 2: 
                case 6: 
                case 12: {
                    throw new CanonicalizationException("empty", new Object[]{"illegal node type during traversal"});
                }
                case 9: 
                case 11: {
                    ns.outputNodePush();
                    sibling = currentNode.getFirstChild();
                    break;
                }
                case 8: {
                    if (!this.includeComments || this.isVisibleDO(currentNode, ns.getLevel()) != 1) break;
                    this.outputCommentToWriter((Comment)currentNode, writer, documentLevel);
                    break;
                }
                case 7: {
                    if (!this.isVisible(currentNode)) break;
                    this.outputPItoWriter((ProcessingInstruction)currentNode, writer, documentLevel);
                    break;
                }
                case 3: 
                case 4: {
                    if (!this.isVisible(currentNode)) break;
                    CanonicalizerBase.outputTextToWriter(currentNode.getNodeValue(), writer);
                    for (Node nextSibling = currentNode.getNextSibling(); nextSibling != null && (nextSibling.getNodeType() == 3 || nextSibling.getNodeType() == 4); nextSibling = nextSibling.getNextSibling()) {
                        CanonicalizerBase.outputTextToWriter(nextSibling.getNodeValue(), writer);
                        currentNode = nextSibling;
                        sibling = currentNode.getNextSibling();
                    }
                    break;
                }
                case 1: {
                    documentLevel = 0;
                    Element currentElement = (Element)currentNode;
                    String name = null;
                    int i = this.isVisibleDO(currentNode, ns.getLevel());
                    if (i == -1) {
                        sibling = currentNode.getNextSibling();
                        break;
                    }
                    boolean bl = currentNodeIsVisible = i == 1;
                    if (currentNodeIsVisible) {
                        ns.outputNodePush();
                        writer.write(60);
                        name = currentElement.getTagName();
                        UtfHelpper.writeByte(name, writer, cache);
                    } else {
                        ns.push();
                    }
                    this.outputAttributes(currentElement, ns, cache, writer);
                    if (currentNodeIsVisible) {
                        writer.write(62);
                    }
                    if ((sibling = currentNode.getFirstChild()) == null) {
                        if (currentNodeIsVisible) {
                            writer.write((byte[])END_TAG.clone());
                            UtfHelpper.writeByte(name, writer, cache);
                            writer.write(62);
                            ns.outputNodePop();
                        } else {
                            ns.pop();
                        }
                        if (parentNode == null) break;
                        sibling = currentNode.getNextSibling();
                        break;
                    }
                    parentNode = currentElement;
                    break;
                }
            }
            while (sibling == null && parentNode != null) {
                if (this.isVisible(parentNode)) {
                    writer.write((byte[])END_TAG.clone());
                    UtfHelpper.writeByte(((Element)parentNode).getTagName(), writer, cache);
                    writer.write(62);
                    ns.outputNodePop();
                } else {
                    ns.pop();
                }
                if (parentNode == endnode) {
                    return;
                }
                sibling = parentNode.getNextSibling();
                if ((parentNode = parentNode.getParentNode()) != null && 1 == parentNode.getNodeType()) continue;
                parentNode = null;
                documentLevel = 1;
            }
            if (sibling == null) {
                return;
            }
            currentNode = sibling;
            sibling = currentNode.getNextSibling();
        }
    }

    protected int isVisibleDO(Node currentNode, int level) {
        if (this.nodeFilter != null) {
            Iterator<NodeFilter> it = this.nodeFilter.iterator();
            while (it.hasNext()) {
                int i = it.next().isNodeIncludeDO(currentNode, level);
                if (i == 1) continue;
                return i;
            }
        }
        if (this.xpathNodeSet != null && !this.xpathNodeSet.contains(currentNode)) {
            return 0;
        }
        return 1;
    }

    protected int isVisibleInt(Node currentNode) {
        if (this.nodeFilter != null) {
            Iterator<NodeFilter> it = this.nodeFilter.iterator();
            while (it.hasNext()) {
                int i = it.next().isNodeInclude(currentNode);
                if (i == 1) continue;
                return i;
            }
        }
        if (this.xpathNodeSet != null && !this.xpathNodeSet.contains(currentNode)) {
            return 0;
        }
        return 1;
    }

    protected boolean isVisible(Node currentNode) {
        if (this.nodeFilter != null) {
            Iterator<NodeFilter> it = this.nodeFilter.iterator();
            while (it.hasNext()) {
                if (it.next().isNodeInclude(currentNode) == 1) continue;
                return false;
            }
        }
        return this.xpathNodeSet == null || this.xpathNodeSet.contains(currentNode);
    }

    protected void handleParent(Element e, NameSpaceSymbTable ns) {
        if (!e.hasAttributes() && e.getNamespaceURI() == null) {
            return;
        }
        NamedNodeMap attrs = e.getAttributes();
        int attrsLength = attrs.getLength();
        for (int i = 0; i < attrsLength; ++i) {
            Attr attribute = (Attr)attrs.item(i);
            String NName = attribute.getLocalName();
            String NValue = attribute.getNodeValue();
            if (!XMLNS_URI.equals(attribute.getNamespaceURI()) || XML.equals(NName) && XML_LANG_URI.equals(NValue)) continue;
            ns.addMapping(NName, NValue, attribute);
        }
        if (e.getNamespaceURI() != null) {
            String Name;
            String NName = e.getPrefix();
            String NValue = e.getNamespaceURI();
            if (NName == null || NName.isEmpty()) {
                NName = XMLNS;
                Name = XMLNS;
            } else {
                Name = "xmlns:" + NName;
            }
            Attr n = e.getOwnerDocument().createAttributeNS(XMLNS_URI, Name);
            n.setValue(NValue);
            ns.addMapping(NName, NValue, n);
        }
    }

    private void getParentNameSpaces(Element el, NameSpaceSymbTable ns) {
        Node n1 = el.getParentNode();
        if (n1 == null || 1 != n1.getNodeType()) {
            return;
        }
        ArrayList<Element> parents = new ArrayList<Element>();
        for (Node parent = n1; parent != null && 1 == parent.getNodeType(); parent = parent.getParentNode()) {
            parents.add((Element)parent);
        }
        ListIterator it = parents.listIterator(parents.size());
        while (it.hasPrevious()) {
            Element ele = (Element)it.previous();
            this.handleParent(ele, ns);
        }
        parents.clear();
        Attr nsprefix = ns.getMappingWithoutRendered(XMLNS);
        if (nsprefix != null && nsprefix.getValue().length() == 0) {
            ns.addMappingAndRender(XMLNS, "", this.getNullNode(nsprefix.getOwnerDocument()));
        }
    }

    abstract void outputAttributes(Element var1, NameSpaceSymbTable var2, Map<String, byte[]> var3, OutputStream var4) throws CanonicalizationException, DOMException, IOException;

    abstract void outputAttributesSubtree(Element var1, NameSpaceSymbTable var2, Map<String, byte[]> var3, OutputStream var4) throws CanonicalizationException, DOMException, IOException;

    abstract void circumventBugIfNeeded(XMLSignatureInput var1) throws XMLParserException, IOException;

    protected static final void outputAttrToWriter(String name, String value, OutputStream writer, Map<String, byte[]> cache) throws IOException {
        writer.write(32);
        UtfHelpper.writeByte(name, writer, cache);
        writer.write((byte[])EQUALS_STR.clone());
        int length = value.length();
        int i = 0;
        block8: while (i < length) {
            byte[] toWrite;
            int c = value.codePointAt(i);
            i += Character.charCount(c);
            switch (c) {
                case 38: {
                    toWrite = (byte[])AMP.clone();
                    break;
                }
                case 60: {
                    toWrite = (byte[])LT.clone();
                    break;
                }
                case 34: {
                    toWrite = (byte[])QUOT.clone();
                    break;
                }
                case 9: {
                    toWrite = (byte[])X9.clone();
                    break;
                }
                case 10: {
                    toWrite = (byte[])XA.clone();
                    break;
                }
                case 13: {
                    toWrite = (byte[])XD.clone();
                    break;
                }
                default: {
                    if (c < 128) {
                        writer.write(c);
                        continue block8;
                    }
                    UtfHelpper.writeCodePointToUtf8(c, writer);
                    continue block8;
                }
            }
            writer.write(toWrite);
        }
        writer.write(34);
    }

    protected void outputPItoWriter(ProcessingInstruction currentPI, OutputStream writer, int position) throws IOException {
        int c;
        if (position == 1) {
            writer.write(10);
        }
        writer.write((byte[])BEGIN_PI.clone());
        String target = currentPI.getTarget();
        int length = target.length();
        for (int i = 0; i < length; i += Character.charCount(c)) {
            c = target.codePointAt(i);
            if (c == 13) {
                writer.write((byte[])XD.clone());
                continue;
            }
            if (c < 128) {
                writer.write(c);
                continue;
            }
            UtfHelpper.writeCodePointToUtf8(c, writer);
        }
        String data = currentPI.getData();
        length = data.length();
        if (length > 0) {
            int c2;
            writer.write(32);
            for (int i = 0; i < length; i += Character.charCount(c2)) {
                c2 = data.codePointAt(i);
                if (c2 == 13) {
                    writer.write((byte[])XD.clone());
                    continue;
                }
                UtfHelpper.writeCodePointToUtf8(c2, writer);
            }
        }
        writer.write((byte[])END_PI.clone());
        if (position == -1) {
            writer.write(10);
        }
    }

    protected void outputCommentToWriter(Comment currentComment, OutputStream writer, int position) throws IOException {
        int c;
        if (position == 1) {
            writer.write(10);
        }
        writer.write((byte[])BEGIN_COMM.clone());
        String data = currentComment.getData();
        int length = data.length();
        for (int i = 0; i < length; i += Character.charCount(c)) {
            c = data.codePointAt(i);
            if (c == 13) {
                writer.write((byte[])XD.clone());
                continue;
            }
            if (c < 128) {
                writer.write(c);
                continue;
            }
            UtfHelpper.writeCodePointToUtf8(c, writer);
        }
        writer.write((byte[])END_COMM.clone());
        if (position == -1) {
            writer.write(10);
        }
    }

    private static final void outputTextToWriter(String text, OutputStream writer) throws IOException {
        int length = text.length();
        int i = 0;
        block6: while (i < length) {
            byte[] toWrite;
            int c = text.codePointAt(i);
            i += Character.charCount(c);
            switch (c) {
                case 38: {
                    toWrite = (byte[])AMP.clone();
                    break;
                }
                case 60: {
                    toWrite = (byte[])LT.clone();
                    break;
                }
                case 62: {
                    toWrite = (byte[])GT.clone();
                    break;
                }
                case 13: {
                    toWrite = (byte[])XD.clone();
                    break;
                }
                default: {
                    if (c < 128) {
                        writer.write(c);
                        continue block6;
                    }
                    UtfHelpper.writeCodePointToUtf8(c, writer);
                    continue block6;
                }
            }
            writer.write(toWrite);
        }
    }

    protected Attr getNullNode(Document ownerDocument) {
        if (this.nullNode == null) {
            try {
                this.nullNode = ownerDocument.createAttributeNS(XMLNS_URI, XMLNS);
                this.nullNode.setValue("");
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to create nullNode: " + e);
            }
        }
        return this.nullNode;
    }
}

