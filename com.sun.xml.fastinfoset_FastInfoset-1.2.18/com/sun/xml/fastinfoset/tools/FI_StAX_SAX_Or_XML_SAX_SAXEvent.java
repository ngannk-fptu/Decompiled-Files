/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.tools;

import com.sun.xml.fastinfoset.Decoder;
import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.fastinfoset.tools.SAXEventSerializer;
import com.sun.xml.fastinfoset.tools.StAX2SAXReader;
import com.sun.xml.fastinfoset.tools.TransformInputOutput;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;

public class FI_StAX_SAX_Or_XML_SAX_SAXEvent
extends TransformInputOutput {
    @Override
    public void parse(InputStream document, OutputStream events) throws Exception {
        if (!document.markSupported()) {
            document = new BufferedInputStream(document);
        }
        document.mark(4);
        boolean isFastInfosetDocument = Decoder.isFastInfosetDocument(document);
        document.reset();
        if (isFastInfosetDocument) {
            StAXDocumentParser parser = new StAXDocumentParser();
            parser.setInputStream(document);
            SAXEventSerializer ses = new SAXEventSerializer(events);
            StAX2SAXReader reader = new StAX2SAXReader(parser, ses);
            reader.setLexicalHandler(ses);
            reader.adapt();
        } else {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setNamespaceAware(true);
            SAXParser parser = parserFactory.newSAXParser();
            SAXEventSerializer ses = new SAXEventSerializer(events);
            parser.setProperty("http://xml.org/sax/properties/lexical-handler", ses);
            parser.parse(document, (DefaultHandler)ses);
        }
    }

    public static void main(String[] args) throws Exception {
        FI_StAX_SAX_Or_XML_SAX_SAXEvent p = new FI_StAX_SAX_Or_XML_SAX_SAXEvent();
        p.parse(args);
    }
}

