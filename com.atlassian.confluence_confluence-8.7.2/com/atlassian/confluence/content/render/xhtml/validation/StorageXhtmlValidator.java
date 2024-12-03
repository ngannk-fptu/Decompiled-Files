/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ClassLoaderUtils
 */
package com.atlassian.confluence.content.render.xhtml.validation;

import com.atlassian.confluence.content.render.xhtml.Namespace;
import com.atlassian.confluence.content.render.xhtml.XhtmlConstants;
import com.atlassian.confluence.content.render.xhtml.validation.ValidationException;
import com.atlassian.confluence.content.render.xhtml.validation.XmlValidator;
import com.atlassian.core.util.ClassLoaderUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class StorageXhtmlValidator
implements XmlValidator {
    private static final SchemaFactory SCHEMA_FACTORY = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
    private static final Schema HYBRID_SCHEMA;

    @Override
    public void validate(String xml) throws ValidationException {
        Validator validator = HYBRID_SCHEMA.newValidator();
        try {
            validator.validate(new SAXSource(new InputSource(this.getXhtmlDocument(xml))));
        }
        catch (SAXException e) {
            throw new ValidationException(xml, e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Reader getXhtmlDocument(String xmlFragment) {
        StringBuilder namespaces = new StringBuilder();
        for (Namespace namespace : XhtmlConstants.STORAGE_NAMESPACES) {
            namespaces.append(namespaces.length() > 0 ? " " : "").append("xmlns").append((String)(namespace.isDefaultNamespace() ? "" : ":" + namespace.getPrefix())).append("=\"").append(namespace.getUri()).append("\"");
        }
        return new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//ENTITIES Latin 1 for XHTML//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent\">\n<html " + namespaces + ">\n<head>\n    <title></title>\n</head>\n<body>" + xmlFragment + "</body>\n</html>");
    }

    static {
        SCHEMA_FACTORY.setResourceResolver(new ClasspathResourceResolver());
        try {
            HYBRID_SCHEMA = SCHEMA_FACTORY.newSchema(new StreamSource(ClassLoaderUtils.getResourceAsStream((String)"xhtml/xhtml11.xsd", StorageXhtmlValidator.class)));
        }
        catch (SAXException e) {
            throw new RuntimeException("Error parsing default schema", e);
        }
    }

    private static class DefaultLSInput
    implements LSInput {
        private final String publicId;
        private final String systemId;
        private final InputStream inputStream;

        public DefaultLSInput(String publicId, String systemId, InputStream inputStream) {
            this.publicId = publicId;
            this.systemId = systemId;
            this.inputStream = inputStream;
        }

        @Override
        public InputStream getByteStream() {
            return this.inputStream;
        }

        @Override
        public String getSystemId() {
            return this.systemId;
        }

        @Override
        public String getPublicId() {
            return this.publicId;
        }

        @Override
        public String getStringData() {
            return null;
        }

        @Override
        public Reader getCharacterStream() {
            return null;
        }

        @Override
        public void setCharacterStream(Reader characterStream) {
        }

        @Override
        public void setByteStream(InputStream byteStream) {
        }

        @Override
        public void setStringData(String stringData) {
        }

        @Override
        public void setSystemId(String systemId) {
        }

        @Override
        public void setPublicId(String publicId) {
        }

        @Override
        public String getBaseURI() {
            return null;
        }

        @Override
        public void setBaseURI(String baseURI) {
        }

        @Override
        public String getEncoding() {
            return null;
        }

        @Override
        public void setEncoding(String encoding) {
        }

        @Override
        public boolean getCertifiedText() {
            return false;
        }

        @Override
        public void setCertifiedText(boolean certifiedText) {
        }
    }

    private static class ClasspathResourceResolver
    implements LSResourceResolver {
        private Set<String> includedResources = new HashSet<String>();

        private ClasspathResourceResolver() {
        }

        @Override
        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
            DefaultLSInput result = null;
            if (!"http://www.w3.org/XML/1998/namespace".equals(namespaceURI) && !this.includedResources.contains(systemId)) {
                result = new DefaultLSInput(publicId, systemId, ClassLoaderUtils.getResourceAsStream((String)("xhtml/" + systemId), StorageXhtmlValidator.class));
                this.includedResources.add(systemId);
            }
            return result;
        }
    }
}

