/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import javax.xml.parsers.SAXParserFactory;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.DocumentSplitter;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class SplitterTest {
    public static void main(String[] args) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        XMLReader reader = factory.newSAXParser().getXMLReader();
        reader.setErrorHandler(new ErrorHandler(){

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void warning(SAXParseException exception) throws SAXException {
                throw exception;
            }
        });
        DocumentSplitter splitter = new DocumentSplitter();
        reader.setContentHandler(splitter);
        reader.parse(args[0]);
        for (Document doc : splitter.getDocuments()) {
            System.out.println(doc.getDocumentElement());
        }
    }
}

