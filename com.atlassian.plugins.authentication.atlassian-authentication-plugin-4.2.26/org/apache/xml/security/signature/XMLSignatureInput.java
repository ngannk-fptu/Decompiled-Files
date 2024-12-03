/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.signature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.implementations.Canonicalizer11_OmitComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315OmitComments;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.signature.NodeFilter;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.signature.XMLSignatureInputDebugger;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XMLSignatureInput {
    private InputStream inputOctetStreamProxy;
    private Set<Node> inputNodeSet;
    private Node subNode;
    private Node excludeNode;
    private boolean excludeComments = false;
    private boolean isNodeSet = false;
    private byte[] bytes;
    private boolean secureValidation = true;
    private String mimeType;
    private String sourceURI;
    private List<NodeFilter> nodeFilters = new ArrayList<NodeFilter>();
    private boolean needsToBeExpanded = false;
    private OutputStream outputStream;
    private String preCalculatedDigest;

    public XMLSignatureInput(byte[] inputOctets) {
        this.bytes = inputOctets;
    }

    public XMLSignatureInput(InputStream inputOctetStream) {
        this.inputOctetStreamProxy = inputOctetStream;
    }

    public XMLSignatureInput(Node rootNode) {
        this.subNode = rootNode;
    }

    public XMLSignatureInput(Set<Node> inputNodeSet) {
        this.inputNodeSet = inputNodeSet;
    }

    public XMLSignatureInput(String preCalculatedDigest) {
        this.preCalculatedDigest = preCalculatedDigest;
    }

    public boolean isNeedsToBeExpanded() {
        return this.needsToBeExpanded;
    }

    public void setNeedsToBeExpanded(boolean needsToBeExpanded) {
        this.needsToBeExpanded = needsToBeExpanded;
    }

    public Set<Node> getNodeSet() throws XMLParserException, IOException {
        return this.getNodeSet(false);
    }

    public Set<Node> getInputNodeSet() {
        return this.inputNodeSet;
    }

    public Set<Node> getNodeSet(boolean circumvent) throws XMLParserException, IOException {
        if (this.inputNodeSet != null) {
            return this.inputNodeSet;
        }
        if (this.inputOctetStreamProxy == null && this.subNode != null) {
            if (circumvent) {
                XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(this.subNode));
            }
            this.inputNodeSet = new LinkedHashSet<Node>();
            XMLUtils.getSet(this.subNode, this.inputNodeSet, this.excludeNode, this.excludeComments);
            return this.inputNodeSet;
        }
        if (this.isOctetStream()) {
            this.convertToNodes();
            LinkedHashSet<Node> result = new LinkedHashSet<Node>();
            XMLUtils.getSet(this.subNode, result, null, false);
            return result;
        }
        throw new RuntimeException("getNodeSet() called but no input data present");
    }

    public InputStream getOctetStream() throws IOException {
        if (this.inputOctetStreamProxy != null) {
            return this.inputOctetStreamProxy;
        }
        if (this.bytes != null) {
            this.inputOctetStreamProxy = new ByteArrayInputStream(this.bytes);
            return this.inputOctetStreamProxy;
        }
        return null;
    }

    public InputStream getOctetStreamReal() {
        return this.inputOctetStreamProxy;
    }

    public byte[] getBytes() throws IOException, CanonicalizationException {
        byte[] inputBytes = this.getBytesFromInputStream();
        if (inputBytes != null) {
            return inputBytes;
        }
        if (this.isOctetStream() || this.isElement() || this.isNodeSet()) {
            Canonicalizer20010315OmitComments c14nizer = new Canonicalizer20010315OmitComments();
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
                c14nizer.engineCanonicalize(this, (OutputStream)baos, this.secureValidation);
                this.bytes = baos.toByteArray();
            }
        }
        return this.bytes;
    }

    public boolean isNodeSet() {
        return this.inputOctetStreamProxy == null && this.inputNodeSet != null || this.isNodeSet;
    }

    public boolean isElement() {
        return this.inputOctetStreamProxy == null && this.subNode != null && this.inputNodeSet == null && !this.isNodeSet;
    }

    public boolean isOctetStream() {
        return (this.inputOctetStreamProxy != null || this.bytes != null) && this.inputNodeSet == null && this.subNode == null;
    }

    public boolean isOutputStreamSet() {
        return this.outputStream != null;
    }

    public boolean isByteArray() {
        return this.bytes != null && this.inputNodeSet == null && this.subNode == null;
    }

    public boolean isPreCalculatedDigest() {
        return this.preCalculatedDigest != null;
    }

    public boolean isInitialized() {
        return this.isOctetStream() || this.isNodeSet();
    }

    public String getMIMEType() {
        return this.mimeType;
    }

    public void setMIMEType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSourceURI() {
        return this.sourceURI;
    }

    public void setSourceURI(String sourceURI) {
        this.sourceURI = sourceURI;
    }

    public String toString() {
        if (this.isNodeSet()) {
            return "XMLSignatureInput/NodeSet/" + this.inputNodeSet.size() + " nodes/" + this.getSourceURI();
        }
        if (this.isElement()) {
            return "XMLSignatureInput/Element/" + this.subNode + " exclude " + this.excludeNode + " comments:" + this.excludeComments + "/" + this.getSourceURI();
        }
        try {
            byte[] bytes = this.getBytes();
            return "XMLSignatureInput/OctetStream/" + (bytes != null ? bytes.length : 0) + " octets/" + this.getSourceURI();
        }
        catch (IOException | CanonicalizationException ex) {
            return "XMLSignatureInput/OctetStream//" + this.getSourceURI();
        }
    }

    public String getHTMLRepresentation() throws XMLSignatureException {
        XMLSignatureInputDebugger db = new XMLSignatureInputDebugger(this);
        return db.getHTMLRepresentation();
    }

    public String getHTMLRepresentation(Set<String> inclusiveNamespaces) throws XMLSignatureException {
        XMLSignatureInputDebugger db = new XMLSignatureInputDebugger(this, inclusiveNamespaces);
        return db.getHTMLRepresentation();
    }

    public Node getExcludeNode() {
        return this.excludeNode;
    }

    public void setExcludeNode(Node excludeNode) {
        this.excludeNode = excludeNode;
    }

    public Node getSubNode() {
        return this.subNode;
    }

    public boolean isExcludeComments() {
        return this.excludeComments;
    }

    public void setExcludeComments(boolean excludeComments) {
        this.excludeComments = excludeComments;
    }

    public void updateOutputStream(OutputStream diOs) throws CanonicalizationException, IOException {
        this.updateOutputStream(diOs, false);
    }

    public void updateOutputStream(OutputStream diOs, boolean c14n11) throws CanonicalizationException, IOException {
        if (diOs == this.outputStream) {
            return;
        }
        if (this.bytes != null) {
            diOs.write(this.bytes);
        } else if (this.inputOctetStreamProxy == null) {
            Canonicalizer20010315 c14nizer = null;
            c14nizer = c14n11 ? new Canonicalizer11_OmitComments() : new Canonicalizer20010315OmitComments();
            c14nizer.engineCanonicalize(this, diOs, this.secureValidation);
        } else {
            byte[] buffer = new byte[4096];
            int bytesread = 0;
            try {
                while ((bytesread = this.inputOctetStreamProxy.read(buffer)) != -1) {
                    diOs.write(buffer, 0, bytesread);
                }
            }
            catch (IOException ex) {
                this.inputOctetStreamProxy.close();
                throw ex;
            }
        }
    }

    public void setOutputStream(OutputStream os) {
        this.outputStream = os;
    }

    private byte[] getBytesFromInputStream() throws IOException {
        if (this.bytes != null) {
            return this.bytes;
        }
        if (this.inputOctetStreamProxy == null) {
            return null;
        }
        try {
            this.bytes = JavaUtils.getBytesFromStream(this.inputOctetStreamProxy);
        }
        finally {
            this.inputOctetStreamProxy.close();
        }
        return this.bytes;
    }

    public void addNodeFilter(NodeFilter filter) throws XMLParserException, IOException {
        if (this.isOctetStream()) {
            this.convertToNodes();
        }
        this.nodeFilters.add(filter);
    }

    public List<NodeFilter> getNodeFilters() {
        return this.nodeFilters;
    }

    public void setNodeSet(boolean b) {
        this.isNodeSet = b;
    }

    private void convertToNodes() throws XMLParserException, IOException {
        try {
            Document doc = XMLUtils.read(this.getOctetStream(), this.secureValidation);
            this.subNode = doc;
        }
        finally {
            if (this.inputOctetStreamProxy != null) {
                this.inputOctetStreamProxy.close();
            }
            this.inputOctetStreamProxy = null;
            this.bytes = null;
        }
    }

    public boolean isSecureValidation() {
        return this.secureValidation;
    }

    public void setSecureValidation(boolean secureValidation) {
        this.secureValidation = secureValidation;
    }

    public String getPreCalculatedDigest() {
        return this.preCalculatedDigest;
    }
}

