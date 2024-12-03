/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.tools;

import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.fastinfoset.tools.SAX2StAXWriter;
import com.sun.xml.fastinfoset.tools.TransformInputOutput;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class XML_SAX_StAX_FI
extends TransformInputOutput {
    @Override
    public void parse(InputStream xml, OutputStream finf, String workingDirectory) throws Exception {
        StAXDocumentSerializer documentSerializer = new StAXDocumentSerializer();
        documentSerializer.setOutputStream(finf);
        SAX2StAXWriter saxTostax = new SAX2StAXWriter(documentSerializer);
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        SAXParser saxParser = saxParserFactory.newSAXParser();
        XMLReader reader = saxParser.getXMLReader();
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", saxTostax);
        reader.setContentHandler(saxTostax);
        if (workingDirectory != null) {
            reader.setEntityResolver(XML_SAX_StAX_FI.createRelativePathResolver(workingDirectory));
        }
        reader.parse(new InputSource(xml));
        xml.close();
        finf.close();
    }

    @Override
    public void parse(InputStream xml, OutputStream finf) throws Exception {
        this.parse(xml, finf, null);
    }

    public static void main(String[] args) throws Exception {
        XML_SAX_StAX_FI s = new XML_SAX_StAX_FI();
        s.parse(args);
    }
}

