/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.tools;

import com.sun.xml.fastinfoset.Decoder;
import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import com.sun.xml.fastinfoset.tools.SAXEventSerializer;
import com.sun.xml.fastinfoset.tools.TransformInputOutput;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class FI_SAX_Or_XML_SAX_SAXEvent
extends TransformInputOutput {
    @Override
    public void parse(InputStream document, OutputStream events, String workingDirectory) throws Exception {
        if (!document.markSupported()) {
            document = new BufferedInputStream(document);
        }
        document.mark(4);
        boolean isFastInfosetDocument = Decoder.isFastInfosetDocument(document);
        document.reset();
        if (isFastInfosetDocument) {
            SAXDocumentParser parser = new SAXDocumentParser();
            SAXEventSerializer ses = new SAXEventSerializer(events);
            parser.setContentHandler(ses);
            parser.setProperty("http://xml.org/sax/properties/lexical-handler", ses);
            parser.parse(document);
        } else {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setNamespaceAware(true);
            SAXParser parser = parserFactory.newSAXParser();
            SAXEventSerializer ses = new SAXEventSerializer(events);
            XMLReader reader = parser.getXMLReader();
            reader.setProperty("http://xml.org/sax/properties/lexical-handler", ses);
            reader.setContentHandler(ses);
            if (workingDirectory != null) {
                reader.setEntityResolver(FI_SAX_Or_XML_SAX_SAXEvent.createRelativePathResolver(workingDirectory));
            }
            reader.parse(new InputSource(document));
        }
    }

    @Override
    public void parse(InputStream document, OutputStream events) throws Exception {
        this.parse(document, events, null);
    }

    public static void main(String[] args) throws Exception {
        FI_SAX_Or_XML_SAX_SAXEvent p = new FI_SAX_Or_XML_SAX_SAXEvent();
        p.parse(args);
    }
}

