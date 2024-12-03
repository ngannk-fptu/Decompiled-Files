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
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.apache.xml.security.c14n.implementations.NameSpaceSymbTable;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

public class CanonicalizerPhysical
extends CanonicalizerBase {
    public CanonicalizerPhysical() {
        super(true);
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
        if (element.hasAttributes()) {
            TreeSet<Attr> result = new TreeSet<Attr>(COMPARE);
            NamedNodeMap attrs = element.getAttributes();
            int attrsLength = attrs.getLength();
            for (int i = 0; i < attrsLength; ++i) {
                Attr attribute = (Attr)attrs.item(i);
                result.add(attribute);
            }
            for (Attr attr : result) {
                CanonicalizerPhysical.outputAttrToWriter(attr.getNodeName(), attr.getNodeValue(), writer, cache);
            }
        }
    }

    @Override
    protected void outputAttributes(Element element, NameSpaceSymbTable ns, Map<String, byte[]> cache, OutputStream writer) throws CanonicalizationException, DOMException, IOException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }

    @Override
    protected void circumventBugIfNeeded(XMLSignatureInput input) throws XMLParserException, IOException {
    }

    @Override
    protected void handleParent(Element e, NameSpaceSymbTable ns) {
    }

    @Override
    public final String engineGetURI() {
        return "http://santuario.apache.org/c14n/physical";
    }

    @Override
    protected void outputPItoWriter(ProcessingInstruction currentPI, OutputStream writer, int position) throws IOException {
        super.outputPItoWriter(currentPI, writer, 0);
    }

    @Override
    protected void outputCommentToWriter(Comment currentComment, OutputStream writer, int position) throws IOException {
        super.outputCommentToWriter(currentComment, writer, 0);
    }
}

