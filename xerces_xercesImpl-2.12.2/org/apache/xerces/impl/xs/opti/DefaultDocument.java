/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.opti;

import org.apache.xerces.impl.xs.opti.NodeImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class DefaultDocument
extends NodeImpl
implements Document {
    private String fDocumentURI = null;

    public DefaultDocument() {
        this.nodeType = (short)9;
    }

    @Override
    public String getNodeName() {
        return "#document";
    }

    @Override
    public DocumentType getDoctype() {
        return null;
    }

    @Override
    public DOMImplementation getImplementation() {
        return null;
    }

    @Override
    public Element getDocumentElement() {
        return null;
    }

    @Override
    public NodeList getElementsByTagName(String string) {
        return null;
    }

    @Override
    public NodeList getElementsByTagNameNS(String string, String string2) {
        return null;
    }

    @Override
    public Element getElementById(String string) {
        return null;
    }

    @Override
    public Node importNode(Node node, boolean bl) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Element createElement(String string) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public DocumentFragment createDocumentFragment() {
        return null;
    }

    @Override
    public Text createTextNode(String string) {
        return null;
    }

    @Override
    public Comment createComment(String string) {
        return null;
    }

    @Override
    public CDATASection createCDATASection(String string) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String string, String string2) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Attr createAttribute(String string) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public EntityReference createEntityReference(String string) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Element createElementNS(String string, String string2) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Attr createAttributeNS(String string, String string2) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public String getInputEncoding() {
        return null;
    }

    @Override
    public String getXmlEncoding() {
        return null;
    }

    @Override
    public boolean getXmlStandalone() {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public void setXmlStandalone(boolean bl) {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public String getXmlVersion() {
        return null;
    }

    @Override
    public void setXmlVersion(String string) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public boolean getStrictErrorChecking() {
        return false;
    }

    @Override
    public void setStrictErrorChecking(boolean bl) {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public String getDocumentURI() {
        return this.fDocumentURI;
    }

    @Override
    public void setDocumentURI(String string) {
        this.fDocumentURI = string;
    }

    @Override
    public Node adoptNode(Node node) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public void normalizeDocument() {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public DOMConfiguration getDomConfig() {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Node renameNode(Node node, String string, String string2) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }
}

