/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.tools;

import com.sun.xml.fastinfoset.Decoder;
import com.sun.xml.fastinfoset.tools.SAXEventSerializer;
import com.sun.xml.fastinfoset.tools.TransformInputOutput;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.jvnet.fastinfoset.FastInfosetSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent
extends TransformInputOutput {
    @Override
    public void parse(InputStream document, OutputStream events, String workingDirectory) throws Exception {
        if (!document.markSupported()) {
            document = new BufferedInputStream(document);
        }
        document.mark(4);
        boolean isFastInfosetDocument = Decoder.isFastInfosetDocument(document);
        document.reset();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMResult dr = new DOMResult();
        if (isFastInfosetDocument) {
            t.transform(new FastInfosetSource(document), dr);
        } else if (workingDirectory != null) {
            SAXParser parser = this.getParser();
            XMLReader reader = parser.getXMLReader();
            reader.setEntityResolver(FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent.createRelativePathResolver(workingDirectory));
            SAXSource source = new SAXSource(reader, new InputSource(document));
            t.transform(source, dr);
        } else {
            t.transform(new StreamSource(document), dr);
        }
        SAXEventSerializer ses = new SAXEventSerializer(events);
        t.transform(new DOMSource(dr.getNode()), new SAXResult(ses));
    }

    @Override
    public void parse(InputStream document, OutputStream events) throws Exception {
        this.parse(document, events, null);
    }

    private SAXParser getParser() {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        try {
            return saxParserFactory.newSAXParser();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent p = new FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent();
        p.parse(args);
    }
}

