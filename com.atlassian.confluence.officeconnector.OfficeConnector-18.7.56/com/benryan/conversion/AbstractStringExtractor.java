/*
 * Decompiled with CFR 0.152.
 */
package com.benryan.conversion;

import com.benryan.conversion.StringExtractor;
import java.io.StringWriter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public abstract class AbstractStringExtractor
implements StringExtractor {
    @Override
    public String extract(InputSource inputSource) {
        try {
            XMLFilterImpl filter = new XMLFilterImpl(this.getNonValidatingXmlReader()){

                @Override
                public void setContentHandler(ContentHandler handler) {
                    super.setContentHandler(AbstractStringExtractor.this.wrapContentHandler(handler));
                }
            };
            SAXSource source = new SAXSource(filter, inputSource);
            StringWriter output = new StringWriter();
            StreamResult result = new StreamResult(output);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("method", this.getTransformerMethod());
            transformer.transform(source, result);
            String bodyAsString = output.toString();
            return bodyAsString;
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        catch (SAXException e) {
            throw new RuntimeException(e);
        }
        catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private XMLReader getNonValidatingXmlReader() throws ParserConfigurationException, SAXException {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setValidating(false);
        saxParserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        saxParserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        SAXParser parser = saxParserFactory.newSAXParser();
        XMLReader xmlReader = parser.getXMLReader();
        xmlReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        xmlReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        return xmlReader;
    }

    protected String getTransformerMethod() {
        return "xml";
    }

    protected abstract ContentHandler wrapContentHandler(ContentHandler var1);
}

