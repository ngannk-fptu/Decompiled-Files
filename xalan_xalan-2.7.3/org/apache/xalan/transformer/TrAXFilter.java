/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.transformer;

import java.io.IOException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.transformer.TransformerImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

public class TrAXFilter
extends XMLFilterImpl {
    private Templates m_templates;
    private TransformerImpl m_transformer;

    public TrAXFilter(Templates templates) throws TransformerConfigurationException {
        this.m_templates = templates;
        this.m_transformer = (TransformerImpl)templates.newTransformer();
    }

    public TransformerImpl getTransformer() {
        return this.m_transformer;
    }

    @Override
    public void setParent(XMLReader parent) {
        super.setParent(parent);
        if (null != parent.getContentHandler()) {
            this.setContentHandler(parent.getContentHandler());
        }
        this.setupParse();
    }

    @Override
    public void parse(InputSource input) throws SAXException, IOException {
        if (null == this.getParent()) {
            XMLReader reader = null;
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);
                if (this.m_transformer.getStylesheet().isSecureProcessing()) {
                    try {
                        factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                    }
                    catch (SAXException sAXException) {
                        // empty catch block
                    }
                }
                SAXParser jaxpParser = factory.newSAXParser();
                reader = jaxpParser.getXMLReader();
            }
            catch (ParserConfigurationException ex) {
                throw new SAXException(ex);
            }
            catch (FactoryConfigurationError ex1) {
                throw new SAXException(ex1.toString());
            }
            catch (NoSuchMethodError ex1) {
            }
            catch (AbstractMethodError ex1) {
                // empty catch block
            }
            XMLReader parent = reader == null ? XMLReaderFactory.createXMLReader() : reader;
            try {
                parent.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            }
            catch (SAXException sAXException) {
                // empty catch block
            }
            this.setParent(parent);
        } else {
            this.setupParse();
        }
        if (null == this.m_transformer.getContentHandler()) {
            throw new SAXException(XSLMessages.createMessage("ER_CANNOT_CALL_PARSE", null));
        }
        this.getParent().parse(input);
        Exception e = this.m_transformer.getExceptionThrown();
        if (null != e) {
            if (e instanceof SAXException) {
                throw (SAXException)e;
            }
            throw new SAXException(e);
        }
    }

    @Override
    public void parse(String systemId) throws SAXException, IOException {
        this.parse(new InputSource(systemId));
    }

    private void setupParse() {
        XMLReader p = this.getParent();
        if (p == null) {
            throw new NullPointerException(XSLMessages.createMessage("ER_NO_PARENT_FOR_FILTER", null));
        }
        ContentHandler ch = this.m_transformer.getInputContentHandler();
        p.setContentHandler(ch);
        p.setEntityResolver(this);
        p.setDTDHandler(this);
        p.setErrorHandler(this);
    }

    @Override
    public void setContentHandler(ContentHandler handler) {
        this.m_transformer.setContentHandler(handler);
    }

    public void setErrorListener(ErrorListener handler) {
        this.m_transformer.setErrorListener(handler);
    }
}

