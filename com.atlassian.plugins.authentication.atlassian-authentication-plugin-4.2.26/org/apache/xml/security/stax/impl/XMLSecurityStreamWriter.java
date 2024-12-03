/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;

public class XMLSecurityStreamWriter
implements XMLStreamWriter {
    private final OutputProcessorChain outputProcessorChain;
    private Element elementStack;
    private Element openStartElement;
    private NSContext namespaceContext = new NSContext(null);
    private boolean endDocumentWritten = false;
    private boolean haveToWriteEndElement = false;
    private SecurePart signEntireRequestPart;
    private SecurePart encryptEntireRequestPart;

    public XMLSecurityStreamWriter(OutputProcessorChain outputProcessorChain) {
        this.outputProcessorChain = outputProcessorChain;
    }

    private void chainProcessEvent(XMLSecEvent xmlSecEvent) throws XMLStreamException {
        try {
            this.outputProcessorChain.reset();
            this.outputProcessorChain.processEvent(xmlSecEvent);
        }
        catch (XMLSecurityException e) {
            throw new XMLStreamException(e);
        }
        catch (XMLStreamException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("Trying to declare prefix xmlns (illegal as per NS 1.1 #4)")) {
                throw new XMLStreamException("If you hit this exception this most probably meansyou are using the javax.xml.transform.stax.StAXResult. Don't use it. It is buggy as hell.", e);
            }
            throw e;
        }
    }

    private void outputOpenStartElement() throws XMLStreamException {
        if (this.openStartElement != null) {
            this.chainProcessEvent(XMLSecEventFactory.createXmlSecStartElement(this.openStartElement.getQName(), this.openStartElement.getAttributes(), this.openStartElement.getNamespaces()));
            this.openStartElement = null;
        }
        if (this.haveToWriteEndElement) {
            this.haveToWriteEndElement = false;
            this.writeEndElement();
        }
    }

    private String getNamespacePrefix(String namespaceURI) {
        if (this.elementStack == null) {
            return this.namespaceContext.getPrefix(namespaceURI);
        }
        return this.elementStack.getNamespaceContext().getPrefix(namespaceURI);
    }

    @Override
    public void writeStartElement(String localName) throws XMLStreamException {
        this.writeStartElement("", localName, "");
    }

    @Override
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        this.writeStartElement(this.getNamespacePrefix(namespaceURI), localName, namespaceURI);
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        Element element;
        this.outputOpenStartElement();
        if (this.elementStack == null) {
            element = new Element(this.elementStack, this.namespaceContext, namespaceURI, localName, prefix);
            if (this.signEntireRequestPart != null) {
                this.signEntireRequestPart.setName(new QName(namespaceURI, localName, prefix));
                this.outputProcessorChain.getSecurityContext().putAsMap("signatureParts", this.signEntireRequestPart.getName(), this.signEntireRequestPart);
            }
            if (this.encryptEntireRequestPart != null) {
                this.encryptEntireRequestPart.setName(new QName(namespaceURI, localName, prefix));
                this.outputProcessorChain.getSecurityContext().putAsMap("encryptionParts", this.encryptEntireRequestPart.getName(), this.encryptEntireRequestPart);
            }
        } else {
            element = new Element(this.elementStack, namespaceURI, localName, prefix);
        }
        this.elementStack = element;
        this.openStartElement = element;
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException {
        this.writeEmptyElement("", localName, "");
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        this.writeEmptyElement(this.getNamespacePrefix(namespaceURI), localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.writeStartElement(prefix, localName, namespaceURI);
        this.openStartElement.setEmptyElement(true);
        this.haveToWriteEndElement = true;
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        this.outputOpenStartElement();
        Element element = this.elementStack;
        this.elementStack = this.elementStack.getParentElement();
        this.chainProcessEvent(XMLSecEventFactory.createXmlSecEndElement(element.getQName()));
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        if (!this.endDocumentWritten) {
            this.outputOpenStartElement();
            while (this.elementStack != null) {
                Element element = this.elementStack;
                this.elementStack = element.getParentElement();
                this.chainProcessEvent(XMLSecEventFactory.createXmlSecEndElement(element.getQName()));
            }
            this.chainProcessEvent(XMLSecEventFactory.createXMLSecEndDocument());
            this.endDocumentWritten = true;
        }
    }

    @Override
    public void close() throws XMLStreamException {
        try {
            this.writeEndDocument();
            this.outputProcessorChain.reset();
            this.outputProcessorChain.doFinal();
        }
        catch (XMLSecurityException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void flush() throws XMLStreamException {
    }

    @Override
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        this.writeAttribute("", "", localName, value);
    }

    @Override
    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        this.writeAttribute(this.getNamespacePrefix(namespaceURI), namespaceURI, localName, value);
    }

    @Override
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        if (this.openStartElement == null) {
            throw new XMLStreamException("No open start element.");
        }
        this.openStartElement.addAttribute(XMLSecEventFactory.createXMLSecAttribute(new QName(namespaceURI, localName, prefix), value));
    }

    @Override
    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        if (this.openStartElement == null) {
            throw new XMLStreamException("No open start element.");
        }
        this.openStartElement.addNamespace(XMLSecEventFactory.createXMLSecNamespace(prefix, namespaceURI));
    }

    @Override
    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        if (this.openStartElement == null) {
            throw new XMLStreamException("No open start element.");
        }
        if (this.openStartElement.getElementPrefix().equals("")) {
            this.openStartElement.setElementNamespace(namespaceURI);
            this.openStartElement.setElementPrefix("");
        }
        this.openStartElement.addNamespace(XMLSecEventFactory.createXMLSecNamespace("", namespaceURI));
    }

    @Override
    public void writeComment(String data) throws XMLStreamException {
        this.outputOpenStartElement();
        this.chainProcessEvent(XMLSecEventFactory.createXMLSecComment(data));
    }

    @Override
    public void writeProcessingInstruction(String target) throws XMLStreamException {
        this.writeProcessingInstruction(target, "");
    }

    @Override
    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        this.outputOpenStartElement();
        this.chainProcessEvent(XMLSecEventFactory.createXMLSecProcessingInstruction(target, data));
    }

    @Override
    public void writeCData(String data) throws XMLStreamException {
        this.outputOpenStartElement();
        this.chainProcessEvent(XMLSecEventFactory.createXMLSecCData(data));
    }

    @Override
    public void writeDTD(String dtd) throws XMLStreamException {
        if (this.elementStack != null) {
            throw new XMLStreamException("Not in proLOG");
        }
        this.chainProcessEvent(XMLSecEventFactory.createXMLSecDTD(dtd));
    }

    @Override
    public void writeEntityRef(String name) throws XMLStreamException {
        this.outputOpenStartElement();
        this.chainProcessEvent(XMLSecEventFactory.createXMLSecEntityReference(name, XMLSecEventFactory.createXmlSecEntityDeclaration(name)));
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        this.writeStartDocument(null, null);
    }

    @Override
    public void writeStartDocument(String version) throws XMLStreamException {
        this.writeStartDocument(null, version);
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        this.chainProcessEvent(XMLSecEventFactory.createXmlSecStartDocument(null, encoding, null, version));
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        this.outputOpenStartElement();
        this.chainProcessEvent(XMLSecEventFactory.createXmlSecCharacters(text));
    }

    @Override
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        this.outputOpenStartElement();
        this.chainProcessEvent(XMLSecEventFactory.createXmlSecCharacters(text, start, len));
    }

    @Override
    public String getPrefix(String uri) throws XMLStreamException {
        return this.getNamespacePrefix(uri);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        if (this.elementStack == null) {
            this.namespaceContext.add(prefix, uri);
        } else {
            this.elementStack.getNamespaceContext().add(prefix, uri);
        }
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        if (this.elementStack == null) {
            this.namespaceContext.add("", uri);
        } else {
            this.elementStack.getNamespaceContext().add("", uri);
        }
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        if (context == null) {
            throw new NullPointerException("context must not be null");
        }
        this.namespaceContext = new NSContext(context);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        if (this.elementStack == null) {
            return this.namespaceContext;
        }
        return this.elementStack.getNamespaceContext();
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        throw new IllegalArgumentException("Properties not supported");
    }

    public SecurePart getSignEntireRequestPart() {
        return this.signEntireRequestPart;
    }

    public void setSignEntireRequestPart(SecurePart signEntireRequestPart) {
        this.signEntireRequestPart = signEntireRequestPart;
    }

    public SecurePart getEncryptEntireRequestPart() {
        return this.encryptEntireRequestPart;
    }

    public void setEncryptEntireRequestPart(SecurePart encryptEntireRequestPart) {
        this.encryptEntireRequestPart = encryptEntireRequestPart;
    }

    private static class NSContext
    implements NamespaceContext {
        private NamespaceContext parentNamespaceContext;
        private List<String> prefixNsList = Collections.emptyList();

        NSContext(NamespaceContext parentNamespaceContext) {
            this.parentNamespaceContext = parentNamespaceContext;
        }

        @Override
        public String getNamespaceURI(String prefix) {
            for (int i = 0; i < this.prefixNsList.size(); i += 2) {
                String s = this.prefixNsList.get(i);
                if (!s.equals(prefix)) continue;
                return this.prefixNsList.get(i + 1);
            }
            if (this.parentNamespaceContext != null) {
                return this.parentNamespaceContext.getNamespaceURI(prefix);
            }
            return null;
        }

        @Override
        public String getPrefix(String namespaceURI) {
            for (int i = 1; i < this.prefixNsList.size(); i += 2) {
                String s = this.prefixNsList.get(i);
                if (!s.equals(namespaceURI)) continue;
                return this.prefixNsList.get(i - 1);
            }
            if (this.parentNamespaceContext != null) {
                return this.parentNamespaceContext.getPrefix(namespaceURI);
            }
            return null;
        }

        public Iterator getPrefixes(String namespaceURI) {
            ArrayList<String> prefixes = new ArrayList<String>(1);
            for (int i = 1; i < this.prefixNsList.size(); i += 2) {
                String s = this.prefixNsList.get(i);
                if (!s.equals(namespaceURI)) continue;
                prefixes.add(this.prefixNsList.get(i - 1));
            }
            if (this.parentNamespaceContext != null) {
                Iterator<String> parentPrefixes = this.parentNamespaceContext.getPrefixes(namespaceURI);
                while (parentPrefixes.hasNext()) {
                    prefixes.add(parentPrefixes.next());
                }
            }
            return prefixes.iterator();
        }

        private void add(String prefix, String namespace) {
            if (this.prefixNsList == Collections.emptyList()) {
                this.prefixNsList = new ArrayList<String>(1);
            }
            this.prefixNsList.add(prefix);
            this.prefixNsList.add(namespace);
        }
    }

    private static class Element {
        private Element parentElement;
        private QName qName;
        private String elementName;
        private String elementNamespace;
        private String elementPrefix;
        private boolean emptyElement;
        private List<XMLSecNamespace> namespaces = Collections.emptyList();
        private List<XMLSecAttribute> attributes = Collections.emptyList();
        private NSContext namespaceContext;

        public Element(Element parentElement, String elementNamespace, String elementName, String elementPrefix) {
            this(parentElement, null, elementNamespace, elementName, elementPrefix);
        }

        public Element(Element parentElement, NSContext namespaceContext, String elementNamespace, String elementName, String elementPrefix) {
            this.parentElement = parentElement;
            this.namespaceContext = namespaceContext;
            this.elementName = elementName;
            this.setElementNamespace(elementNamespace);
            this.setElementPrefix(elementPrefix);
        }

        private Element getParentElement() {
            return this.parentElement;
        }

        private void setEmptyElement(boolean emptyElement) {
            this.emptyElement = emptyElement;
        }

        private String getElementName() {
            return this.elementName;
        }

        private String getElementNamespace() {
            return this.elementNamespace;
        }

        private void setElementNamespace(String elementNamespace) {
            this.elementNamespace = elementNamespace == null ? "" : elementNamespace;
            this.qName = null;
        }

        private String getElementPrefix() {
            return this.elementPrefix;
        }

        private void setElementPrefix(String elementPrefix) {
            this.elementPrefix = elementPrefix == null ? "" : elementPrefix;
            this.qName = null;
        }

        private List<XMLSecNamespace> getNamespaces() {
            return this.namespaces;
        }

        private void addNamespace(XMLSecNamespace namespace) {
            if (this.namespaces == Collections.emptyList()) {
                this.namespaces = new ArrayList<XMLSecNamespace>(1);
            }
            this.namespaces.add(namespace);
            this.getNamespaceContext().add(namespace.getPrefix(), namespace.getNamespaceURI());
        }

        private List<XMLSecAttribute> getAttributes() {
            return this.attributes;
        }

        private void addAttribute(XMLSecAttribute attribute) {
            if (this.attributes == Collections.emptyList()) {
                this.attributes = new ArrayList<XMLSecAttribute>(1);
            }
            this.attributes.add(attribute);
        }

        private NSContext getNamespaceContext() {
            if (this.namespaceContext == null) {
                this.namespaceContext = this.emptyElement && this.parentElement != null ? this.parentElement.getNamespaceContext() : (this.parentElement != null ? new NSContext(this.parentElement.getNamespaceContext()) : new NSContext(null));
            }
            return this.namespaceContext;
        }

        private QName getQName() {
            if (this.qName == null) {
                this.qName = new QName(this.getElementNamespace(), this.getElementName(), this.getElementPrefix());
            }
            return this.qName;
        }
    }
}

