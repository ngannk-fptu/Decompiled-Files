/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  org.codehaus.jackson.JsonParser
 */
package com.sun.jersey.json.impl.reader;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.json.impl.DefaultJaxbXmlDocumentStructure;
import com.sun.jersey.json.impl.JaxbXmlDocumentStructure;
import com.sun.jersey.json.impl.reader.JsonXmlEvent;
import com.sun.jersey.json.impl.reader.XmlEventProvider;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.jackson.JsonParser;

public class NaturalNotationEventProvider
extends XmlEventProvider {
    private final boolean attrsWithPrefix;
    private JaxbXmlDocumentStructure documentStructure;

    public NaturalNotationEventProvider(JsonParser parser, JSONConfiguration configuration, String rootName, JAXBContext jaxbContext, Class<?> expectedType) throws XMLStreamException {
        super(parser, configuration, rootName);
        this.documentStructure = DefaultJaxbXmlDocumentStructure.getXmlDocumentStructure(jaxbContext, expectedType, true);
        this.attrsWithPrefix = configuration.isUsingPrefixesAtNaturalAttributes();
    }

    private QName getFieldQName(String jsonFieldName, boolean isAttribute) {
        QName result;
        QName qName = result = isAttribute ? this.documentStructure.getExpectedAttributesMap().get(jsonFieldName) : this.documentStructure.getExpectedElementsMap().get(jsonFieldName);
        if (isAttribute && "type".equals(jsonFieldName)) {
            result = new QName("http://www.w3.org/2001/XMLSchema-instance", "type");
        }
        return result == null ? new QName(jsonFieldName) : result;
    }

    @Override
    protected String getAttributeName(String jsonFieldName) {
        return this.attrsWithPrefix ? super.getAttributeName(jsonFieldName) : jsonFieldName;
    }

    @Override
    protected QName getAttributeQName(String jsonFieldName) {
        return this.getFieldQName(this.getAttributeName(jsonFieldName), true);
    }

    @Override
    protected QName getElementQName(String jsonFieldName) {
        return this.getFieldQName(jsonFieldName, false);
    }

    @Override
    protected boolean isAttribute(String jsonFieldName) {
        String attributeName = this.getAttributeName(jsonFieldName);
        return !"$".equals(attributeName) && (!this.documentStructure.canHandleAttributes() ? !this.documentStructure.getExpectedElementsMap().containsKey(attributeName) : this.documentStructure.getExpectedAttributesMap().containsKey(attributeName)) || !jsonFieldName.equals(attributeName);
    }

    @Override
    protected JsonXmlEvent createEndElementEvent(QName elementName, Location location) {
        this.documentStructure.endElement(elementName);
        return super.createEndElementEvent(elementName, location);
    }

    @Override
    protected JsonXmlEvent createStartElementEvent(QName elementName, Location location) {
        this.documentStructure.startElement(elementName);
        return super.createStartElementEvent(elementName, location);
    }
}

