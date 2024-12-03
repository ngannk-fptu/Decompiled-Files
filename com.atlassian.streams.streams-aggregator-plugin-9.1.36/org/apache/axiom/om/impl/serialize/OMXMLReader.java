/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.om.impl.serialize;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.activation.DataHandler;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.serialize.ContentHandlerWriter;
import org.apache.axiom.util.base64.Base64EncodingWriterOutputStream;
import org.apache.axiom.util.sax.AbstractXMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class OMXMLReader
extends AbstractXMLReader {
    private final OMContainer root;
    private final AttributesAdapter attributesAdapter = new AttributesAdapter();

    public OMXMLReader(OMContainer root) {
        this.root = root;
    }

    public void parse(InputSource input) throws IOException, SAXException {
        this.parse();
    }

    public void parse(String systemId) throws IOException, SAXException {
        this.parse();
    }

    private void parse() throws SAXException {
        if (this.root instanceof OMDocument) {
            this.generateEvents((OMDocument)this.root);
        } else {
            OMElement element = (OMElement)this.root;
            this.contentHandler.startDocument();
            this.generateParentPrefixMappingEvents(element, true);
            this.generateEvents(element);
            this.generateParentPrefixMappingEvents(element, false);
            this.contentHandler.endDocument();
        }
    }

    private void generateEvents(OMDocument document) throws SAXException {
        this.contentHandler.startDocument();
        this.generateEventsForChildren(document);
        this.contentHandler.endDocument();
    }

    private void generatePrefixMappingEvents(OMNamespace ns, boolean start) throws SAXException {
        String prefix = ns.getPrefix();
        if (prefix != null) {
            if (start) {
                this.contentHandler.startPrefixMapping(prefix, ns.getNamespaceURI());
            } else {
                this.contentHandler.endPrefixMapping(prefix);
            }
        }
    }

    private void generatePrefixMappingEvents(OMElement omElement, boolean start) throws SAXException {
        Iterator it = omElement.getAllDeclaredNamespaces();
        while (it.hasNext()) {
            this.generatePrefixMappingEvents((OMNamespace)it.next(), start);
        }
    }

    private void generateParentPrefixMappingEvents(OMElement omElement, boolean start) throws SAXException {
        if (!(omElement.getParent() instanceof OMElement)) {
            return;
        }
        HashSet<String> seenPrefixes = new HashSet<String>();
        Iterator it = omElement.getAllDeclaredNamespaces();
        while (it.hasNext()) {
            seenPrefixes.add(((OMNamespace)it.next()).getPrefix());
        }
        OMElement current = omElement;
        OMContainer parent;
        block1: while ((parent = current.getParent()) instanceof OMElement) {
            current = (OMElement)parent;
            Iterator it2 = current.getAllDeclaredNamespaces();
            while (true) {
                if (!it2.hasNext()) continue block1;
                OMNamespace ns = (OMNamespace)it2.next();
                if (!seenPrefixes.add(ns.getPrefix())) continue;
                this.generatePrefixMappingEvents(ns, start);
            }
            break;
        }
        return;
    }

    private void generateEvents(OMElement omElement) throws SAXException {
        String prefix;
        String uri;
        this.generatePrefixMappingEvents(omElement, true);
        OMNamespace omNamespace = omElement.getNamespace();
        if (omNamespace != null) {
            uri = omNamespace.getNamespaceURI();
            prefix = omNamespace.getPrefix();
        } else {
            uri = "";
            prefix = null;
        }
        String localName = omElement.getLocalName();
        String qName = prefix == null || prefix.length() == 0 ? localName : prefix + ":" + localName;
        this.attributesAdapter.setAttributes(omElement);
        this.contentHandler.startElement(uri, localName, qName, this.attributesAdapter);
        this.generateEventsForChildren(omElement);
        this.contentHandler.endElement(uri, localName, qName);
        this.generatePrefixMappingEvents(omElement, false);
    }

    private void generateEventsForChildren(OMContainer parent) throws SAXException {
        Iterator it = parent.getChildren();
        block10: while (it.hasNext()) {
            OMNode node = (OMNode)it.next();
            switch (node.getType()) {
                case 11: {
                    if (this.lexicalHandler == null) continue block10;
                    OMDocType doctype = (OMDocType)node;
                    this.lexicalHandler.startDTD(doctype.getRootName(), doctype.getPublicId(), doctype.getSystemId());
                    this.lexicalHandler.endDTD();
                    continue block10;
                }
                case 1: {
                    this.generateEvents((OMElement)node);
                    continue block10;
                }
                case 4: {
                    this.generateEvents((OMText)node, false);
                    continue block10;
                }
                case 6: {
                    this.generateEvents((OMText)node, true);
                    continue block10;
                }
                case 12: {
                    if (this.lexicalHandler != null) {
                        this.lexicalHandler.startCDATA();
                    }
                    this.generateEvents((OMText)node, false);
                    if (this.lexicalHandler == null) continue block10;
                    this.lexicalHandler.endCDATA();
                    continue block10;
                }
                case 5: {
                    if (this.lexicalHandler == null) continue block10;
                    char[] ch = ((OMComment)node).getValue().toCharArray();
                    this.lexicalHandler.comment(ch, 0, ch.length);
                    continue block10;
                }
                case 3: {
                    OMProcessingInstruction pi = (OMProcessingInstruction)node;
                    this.contentHandler.processingInstruction(pi.getTarget(), pi.getValue());
                    continue block10;
                }
                case 9: {
                    this.contentHandler.skippedEntity(((OMEntityReference)node).getName());
                    continue block10;
                }
            }
            throw new IllegalStateException("Unrecognized node type " + node.getType());
        }
    }

    private void generateEvents(OMText omText, boolean space) throws SAXException {
        if (omText.isBinary()) {
            DataHandler dh = (DataHandler)omText.getDataHandler();
            Base64EncodingWriterOutputStream out = new Base64EncodingWriterOutputStream(new ContentHandlerWriter(this.contentHandler));
            try {
                dh.writeTo((OutputStream)out);
                out.complete();
            }
            catch (IOException ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof SAXException) {
                    throw (SAXException)ex.getCause();
                }
                throw new SAXException(ex);
            }
        } else {
            char[] ch = omText.getTextCharacters();
            if (space) {
                this.contentHandler.ignorableWhitespace(ch, 0, ch.length);
            } else {
                this.contentHandler.characters(ch, 0, ch.length);
            }
        }
    }

    protected static class AttributesAdapter
    implements Attributes {
        private List attributes = new ArrayList(5);

        protected AttributesAdapter() {
        }

        public void setAttributes(OMElement element) {
            this.attributes.clear();
            Iterator it = element.getAllAttributes();
            while (it.hasNext()) {
                this.attributes.add(it.next());
            }
        }

        public int getLength() {
            return this.attributes.size();
        }

        public int getIndex(String qName) {
            int len = this.attributes.size();
            for (int i = 0; i < len; ++i) {
                if (!this.getQName(i).equals(qName)) continue;
                return i;
            }
            return -1;
        }

        public int getIndex(String uri, String localName) {
            int len = this.attributes.size();
            for (int i = 0; i < len; ++i) {
                if (!this.getURI(i).equals(uri) || !this.getLocalName(i).equals(localName)) continue;
                return i;
            }
            return -1;
        }

        public String getLocalName(int index) {
            return ((OMAttribute)this.attributes.get(index)).getLocalName();
        }

        public String getQName(int index) {
            OMAttribute attribute = (OMAttribute)this.attributes.get(index);
            OMNamespace ns = attribute.getNamespace();
            if (ns == null) {
                return attribute.getLocalName();
            }
            String prefix = ns.getPrefix();
            if (prefix == null || prefix.length() == 0) {
                return attribute.getLocalName();
            }
            return ns.getPrefix() + ":" + attribute.getLocalName();
        }

        public String getType(int index) {
            return ((OMAttribute)this.attributes.get(index)).getAttributeType();
        }

        public String getType(String qName) {
            int index = this.getIndex(qName);
            return index == -1 ? null : this.getType(index);
        }

        public String getType(String uri, String localName) {
            int index = this.getIndex(uri, localName);
            return index == -1 ? null : this.getType(index);
        }

        public String getURI(int index) {
            OMNamespace ns = ((OMAttribute)this.attributes.get(index)).getNamespace();
            return ns == null ? "" : ns.getNamespaceURI();
        }

        public String getValue(int index) {
            return ((OMAttribute)this.attributes.get(index)).getAttributeValue();
        }

        public String getValue(String qName) {
            int index = this.getIndex(qName);
            return index == -1 ? null : this.getValue(index);
        }

        public String getValue(String uri, String localName) {
            int index = this.getIndex(uri, localName);
            return index == -1 ? null : this.getValue(index);
        }
    }
}

