/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext.stax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.EntityDeclaration;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecCharacters;
import org.apache.xml.security.stax.ext.stax.XMLSecComment;
import org.apache.xml.security.stax.ext.stax.XMLSecDTD;
import org.apache.xml.security.stax.ext.stax.XMLSecEndDocument;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEntityDeclaration;
import org.apache.xml.security.stax.ext.stax.XMLSecEntityReference;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;
import org.apache.xml.security.stax.ext.stax.XMLSecProcessingInstruction;
import org.apache.xml.security.stax.ext.stax.XMLSecStartDocument;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.stax.XMLSecAttributeImpl;
import org.apache.xml.security.stax.impl.stax.XMLSecCharactersImpl;
import org.apache.xml.security.stax.impl.stax.XMLSecCommentImpl;
import org.apache.xml.security.stax.impl.stax.XMLSecDTDImpl;
import org.apache.xml.security.stax.impl.stax.XMLSecEndDocumentImpl;
import org.apache.xml.security.stax.impl.stax.XMLSecEndElementImpl;
import org.apache.xml.security.stax.impl.stax.XMLSecEntityDeclarationImpl;
import org.apache.xml.security.stax.impl.stax.XMLSecEntityReferenceImpl;
import org.apache.xml.security.stax.impl.stax.XMLSecNamespaceImpl;
import org.apache.xml.security.stax.impl.stax.XMLSecProcessingInstructionImpl;
import org.apache.xml.security.stax.impl.stax.XMLSecStartDocumentImpl;
import org.apache.xml.security.stax.impl.stax.XMLSecStartElementImpl;

public final class XMLSecEventFactory {
    private XMLSecEventFactory() {
    }

    public static XMLSecEvent allocate(XMLStreamReader xmlStreamReader, XMLSecStartElement parentXMLSecStartElement) throws XMLStreamException {
        switch (xmlStreamReader.getEventType()) {
            case 1: {
                ArrayList<XMLSecAttribute> comparableAttributes = null;
                int attributeCount = xmlStreamReader.getAttributeCount();
                if (attributeCount > 0) {
                    comparableAttributes = new ArrayList<XMLSecAttribute>(attributeCount);
                    for (int i = 0; i < attributeCount; ++i) {
                        comparableAttributes.add(XMLSecEventFactory.createXMLSecAttribute(xmlStreamReader.getAttributeName(i), xmlStreamReader.getAttributeValue(i)));
                    }
                }
                ArrayList<XMLSecNamespace> comparableNamespaces = null;
                int namespaceCount = xmlStreamReader.getNamespaceCount();
                if (namespaceCount > 0) {
                    comparableNamespaces = new ArrayList<XMLSecNamespace>(namespaceCount);
                    for (int i = 0; i < namespaceCount; ++i) {
                        comparableNamespaces.add(XMLSecNamespaceImpl.getInstance(xmlStreamReader.getNamespacePrefix(i), xmlStreamReader.getNamespaceURI(i)));
                    }
                }
                return new XMLSecStartElementImpl(xmlStreamReader.getName(), comparableAttributes, comparableNamespaces, parentXMLSecStartElement);
            }
            case 2: {
                return new XMLSecEndElementImpl(xmlStreamReader.getName(), parentXMLSecStartElement);
            }
            case 3: {
                return new XMLSecProcessingInstructionImpl(xmlStreamReader.getPITarget(), xmlStreamReader.getPIData(), parentXMLSecStartElement);
            }
            case 4: {
                char[] text = new char[xmlStreamReader.getTextLength()];
                xmlStreamReader.getTextCharacters(0, text, 0, xmlStreamReader.getTextLength());
                return new XMLSecCharactersImpl(text, false, false, xmlStreamReader.isWhiteSpace(), parentXMLSecStartElement);
            }
            case 5: {
                return new XMLSecCommentImpl(xmlStreamReader.getText(), parentXMLSecStartElement);
            }
            case 6: {
                return new XMLSecCharactersImpl(xmlStreamReader.getText(), false, true, xmlStreamReader.isWhiteSpace(), parentXMLSecStartElement);
            }
            case 7: {
                String systemId = xmlStreamReader.getLocation() != null ? xmlStreamReader.getLocation().getSystemId() : null;
                return new XMLSecStartDocumentImpl(systemId, xmlStreamReader.getCharacterEncodingScheme(), xmlStreamReader.standaloneSet() ? Boolean.valueOf(xmlStreamReader.isStandalone()) : null, xmlStreamReader.getVersion());
            }
            case 8: {
                return new XMLSecEndDocumentImpl();
            }
            case 9: {
                return new XMLSecEntityReferenceImpl(xmlStreamReader.getLocalName(), null, parentXMLSecStartElement);
            }
            case 10: {
                throw new UnsupportedOperationException("Attribute event not supported");
            }
            case 11: {
                return new XMLSecDTDImpl(xmlStreamReader.getText(), parentXMLSecStartElement);
            }
            case 12: {
                return new XMLSecCharactersImpl(xmlStreamReader.getText(), false, false, xmlStreamReader.isWhiteSpace(), parentXMLSecStartElement);
            }
            case 13: {
                throw new UnsupportedOperationException("Namespace event not supported");
            }
            case 14: {
                throw new UnsupportedOperationException("NotationDeclaration event not supported");
            }
            case 15: {
                throw new UnsupportedOperationException("Entity declaration event not supported");
            }
        }
        throw new IllegalArgumentException("Unknown XML event occurred");
    }

    public static XMLSecStartElement createXmlSecStartElement(QName name, List<XMLSecAttribute> attributes, List<XMLSecNamespace> namespaces) {
        return new XMLSecStartElementImpl(name, attributes, namespaces);
    }

    public static XMLSecStartElement createXmlSecStartElement(QName name, Collection<XMLSecAttribute> attributes, Collection<XMLSecNamespace> namespaces) {
        return new XMLSecStartElementImpl(name, attributes, namespaces);
    }

    public static XMLSecEndElement createXmlSecEndElement(QName name) {
        return new XMLSecEndElementImpl(name, null);
    }

    public static XMLSecStartDocument createXmlSecStartDocument(String systemId, String characterEncodingScheme, Boolean standAlone, String version) {
        return new XMLSecStartDocumentImpl(systemId, characterEncodingScheme, standAlone, version);
    }

    public static XMLSecEndDocument createXMLSecEndDocument() {
        return new XMLSecEndDocumentImpl();
    }

    public static XMLSecCharacters createXmlSecCharacters(String data) {
        return new XMLSecCharactersImpl(data, false, false, false, null);
    }

    public static XMLSecCharacters createXmlSecCharacters(char[] text) {
        return new XMLSecCharactersImpl(text, false, false, false, null);
    }

    public static XMLSecCharacters createXmlSecCharacters(char[] text, int off, int len) {
        return new XMLSecCharactersImpl(Arrays.copyOfRange(text, off, off + len), false, false, false, null);
    }

    public static XMLSecComment createXMLSecComment(String data) {
        return new XMLSecCommentImpl(data, null);
    }

    public static XMLSecProcessingInstruction createXMLSecProcessingInstruction(String target, String data) {
        return new XMLSecProcessingInstructionImpl(target, data, null);
    }

    public static XMLSecCharacters createXMLSecCData(String data) {
        return new XMLSecCharactersImpl(data, true, false, false, null);
    }

    public static XMLSecDTD createXMLSecDTD(String dtd) {
        return new XMLSecDTDImpl(dtd, null);
    }

    public static XMLSecEntityReference createXMLSecEntityReference(String name, EntityDeclaration entityDeclaration) {
        return new XMLSecEntityReferenceImpl(name, entityDeclaration, null);
    }

    public static XMLSecEntityDeclaration createXmlSecEntityDeclaration(String name) {
        return new XMLSecEntityDeclarationImpl(name);
    }

    public static XMLSecAttribute createXMLSecAttribute(QName name, String value) {
        return new XMLSecAttributeImpl(name, value);
    }

    public static XMLSecNamespace createXMLSecNamespace(String prefix, String uri) {
        return XMLSecNamespaceImpl.getInstance(prefix, uri);
    }
}

