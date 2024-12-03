/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.apache.jackrabbit.commons.xml.DefaultContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ParsingContentHandler
extends DefaultContentHandler {
    private static final SAXParserFactory SAX_PARSER_FACTORY = SAXParserFactory.newInstance();

    public ParsingContentHandler(ContentHandler handler) {
        super(handler);
    }

    public void parse(InputStream in) throws IOException, SAXException {
        try {
            SAX_PARSER_FACTORY.newSAXParser().parse(new InputSource(in), (DefaultHandler)this);
        }
        catch (ParserConfigurationException e) {
            throw new SAXException("SAX parser configuration error", e);
        }
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        return new InputSource(new ByteArrayInputStream(new byte[0]));
    }

    static {
        SAX_PARSER_FACTORY.setNamespaceAware(true);
    }
}

