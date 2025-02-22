/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.trax;

import java.io.IOException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXResult;
import org.apache.xalan.xsltc.trax.TransformerHandlerImpl;
import org.apache.xalan.xsltc.trax.TransformerImpl;
import org.apache.xml.utils.XMLReaderManager;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

public class TrAXFilter
extends XMLFilterImpl {
    private Templates _templates;
    private TransformerImpl _transformer;
    private TransformerHandlerImpl _transformerHandler;

    public TrAXFilter(Templates templates) throws TransformerConfigurationException {
        this._templates = templates;
        this._transformer = (TransformerImpl)templates.newTransformer();
        this._transformerHandler = new TransformerHandlerImpl(this._transformer);
    }

    public Transformer getTransformer() {
        return this._transformer;
    }

    private void createParent() throws SAXException {
        XMLReader parent = null;
        try {
            SAXParserFactory pfactory = SAXParserFactory.newInstance();
            pfactory.setNamespaceAware(true);
            if (this._transformer.isSecureProcessing()) {
                try {
                    pfactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                }
                catch (SAXException sAXException) {
                    // empty catch block
                }
            }
            SAXParser saxparser = pfactory.newSAXParser();
            parent = saxparser.getXMLReader();
        }
        catch (ParserConfigurationException e) {
            throw new SAXException(e);
        }
        catch (FactoryConfigurationError e) {
            throw new SAXException(e.toString());
        }
        if (parent == null) {
            parent = XMLReaderFactory.createXMLReader();
        }
        this.setParent(parent);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void parse(InputSource input) throws SAXException, IOException {
        XMLReader managedReader = null;
        try {
            if (this.getParent() == null) {
                try {
                    managedReader = XMLReaderManager.getInstance().getXMLReader();
                    this.setParent(managedReader);
                }
                catch (SAXException e) {
                    throw new SAXException(e.toString());
                }
            }
            this.getParent().parse(input);
        }
        finally {
            if (managedReader != null) {
                XMLReaderManager.getInstance().releaseXMLReader(managedReader);
            }
        }
    }

    @Override
    public void parse(String systemId) throws SAXException, IOException {
        this.parse(new InputSource(systemId));
    }

    @Override
    public void setContentHandler(ContentHandler handler) {
        this._transformerHandler.setResult(new SAXResult(handler));
        if (this.getParent() == null) {
            try {
                this.createParent();
            }
            catch (SAXException e) {
                return;
            }
        }
        this.getParent().setContentHandler(this._transformerHandler);
    }

    public void setErrorListener(ErrorListener handler) {
    }
}

