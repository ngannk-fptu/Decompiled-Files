/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.catalog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RELAXCatalog {
    private Map grammars_ = new HashMap();

    public RELAXCatalog() throws ParserConfigurationException, SAXException, IOException {
        this("http://www.iso-relax.org/catalog");
    }

    public RELAXCatalog(String rootURI) throws ParserConfigurationException, SAXException, IOException {
        String catalogFile = rootURI + "/catalog.xml";
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(catalogFile, (DefaultHandler)new CatalogHandler());
    }

    public InputSource getGrammar(String uri) {
        String location = (String)this.grammars_.get(uri);
        if (location == null) {
            return null;
        }
        return new InputSource(location);
    }

    class CatalogHandler
    extends DefaultHandler {
        CatalogHandler() {
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
            String uri = atts.getValue("uri");
            String grammar = atts.getValue("grammar");
            RELAXCatalog.this.grammars_.put(uri, grammar);
        }
    }
}

