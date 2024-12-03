/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.jettison.AbstractXMLInputFactory;
import org.w3c.dom.Document;

public class AbstractDOMDocumentParser {
    private AbstractXMLInputFactory inputFactory;

    protected AbstractDOMDocumentParser(AbstractXMLInputFactory inputFactory) {
        this.inputFactory = inputFactory;
    }

    public Document parse(InputStream input) throws IOException {
        try {
            XMLStreamReader streamReader = this.inputFactory.createXMLStreamReader(input);
            XMLInputFactory readerFactory = XMLInputFactory.newInstance();
            XMLEventReader eventReader = readerFactory.createXMLEventReader(streamReader);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(baos);
            eventWriter.add(eventReader);
            eventWriter.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            return this.getDocumentBuilder().parse(bais);
        }
        catch (Exception ex) {
            IOException ioex = new IOException("Cannot parse input stream");
            ioex.initCause(ex);
            throw ioex;
        }
    }

    private DocumentBuilder getDocumentBuilder() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder;
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException("Failed to create DocumentBuilder", e);
        }
    }
}

