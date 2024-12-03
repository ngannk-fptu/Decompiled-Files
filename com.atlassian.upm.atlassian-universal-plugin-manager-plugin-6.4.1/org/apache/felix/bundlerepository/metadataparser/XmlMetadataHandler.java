/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository.metadataparser;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.felix.bundlerepository.Logger;
import org.apache.felix.bundlerepository.metadataparser.MetadataHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XmlMetadataHandler
extends MetadataHandler {
    public XmlMetadataHandler(Logger logger) {
        super(logger);
    }

    public void parse(InputStream istream) throws ParserConfigurationException, IOException, SAXException {
        ContentHandler contenthandler = (ContentHandler)((Object)this.m_handler);
        InputSource is = new InputSource(istream);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(false);
        SAXParser saxParser = spf.newSAXParser();
        XMLReader xmlReader = null;
        xmlReader = saxParser.getXMLReader();
        xmlReader.setContentHandler(contenthandler);
        xmlReader.parse(is);
    }
}

