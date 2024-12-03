/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.xsltc.trax;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.TransformerHandler;
import org.apache.xalan.xsltc.StripFilter;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.dom.DOMWSFilter;
import org.apache.xalan.xsltc.dom.SAXImpl;
import org.apache.xalan.xsltc.dom.XSLTCDTMManager;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xalan.xsltc.trax.TransformerImpl;
import org.apache.xml.serializer.SerializationHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class TransformerHandlerImpl
implements TransformerHandler,
DeclHandler {
    private TransformerImpl _transformer;
    private AbstractTranslet _translet = null;
    private String _systemId;
    private SAXImpl _dom = null;
    private ContentHandler _handler = null;
    private LexicalHandler _lexHandler = null;
    private DTDHandler _dtdHandler = null;
    private DeclHandler _declHandler = null;
    private Result _result = null;
    private Locator _locator = null;
    private boolean _done = false;
    private boolean _isIdentity = false;

    public TransformerHandlerImpl(TransformerImpl transformer) {
        this._transformer = transformer;
        if (transformer.isIdentity()) {
            this._handler = new DefaultHandler();
            this._isIdentity = true;
        } else {
            this._translet = this._transformer.getTranslet();
        }
    }

    @Override
    public String getSystemId() {
        return this._systemId;
    }

    @Override
    public void setSystemId(String id) {
        this._systemId = id;
    }

    @Override
    public Transformer getTransformer() {
        return this._transformer;
    }

    @Override
    public void setResult(Result result) throws IllegalArgumentException {
        this._result = result;
        if (null == result) {
            ErrorMsg err = new ErrorMsg("ER_RESULT_NULL");
            throw new IllegalArgumentException(err.toString());
        }
        if (this._isIdentity) {
            try {
                SerializationHandler outputHandler = this._transformer.getOutputHandler(result);
                this._transformer.transferOutputProperties(outputHandler);
                this._handler = outputHandler;
                this._lexHandler = outputHandler;
            }
            catch (TransformerException e) {
                this._result = null;
            }
        } else if (this._done) {
            try {
                this._transformer.setDOM(this._dom);
                this._transformer.transform(null, this._result);
            }
            catch (TransformerException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this._handler.characters(ch, start, length);
    }

    @Override
    public void startDocument() throws SAXException {
        if (this._result == null) {
            ErrorMsg err = new ErrorMsg("JAXP_SET_RESULT_ERR");
            throw new SAXException(err.toString());
        }
        if (!this._isIdentity) {
            boolean hasIdCall = this._translet != null ? this._translet.hasIdCall() : false;
            XSLTCDTMManager dtmManager = null;
            try {
                dtmManager = (XSLTCDTMManager)this._transformer.getTransformerFactory().getDTMManagerClass().newInstance();
            }
            catch (Exception e) {
                throw new SAXException(e);
            }
            DOMWSFilter wsFilter = this._translet != null && this._translet instanceof StripFilter ? new DOMWSFilter(this._translet) : null;
            this._dom = (SAXImpl)dtmManager.getDTM(null, false, wsFilter, true, false, hasIdCall);
            this._handler = this._dom.getBuilder();
            this._lexHandler = (LexicalHandler)((Object)this._handler);
            this._dtdHandler = (DTDHandler)((Object)this._handler);
            this._declHandler = (DeclHandler)((Object)this._handler);
            this._dom.setDocumentURI(this._systemId);
            if (this._locator != null) {
                this._handler.setDocumentLocator(this._locator);
            }
        }
        this._handler.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        this._handler.endDocument();
        if (!this._isIdentity) {
            if (this._result != null) {
                try {
                    this._transformer.setDOM(this._dom);
                    this._transformer.transform(null, this._result);
                }
                catch (TransformerException e) {
                    throw new SAXException(e);
                }
            }
            this._done = true;
            this._transformer.setDOM(this._dom);
        }
        if (this._isIdentity && this._result instanceof DOMResult) {
            ((DOMResult)this._result).setNode(this._transformer.getTransletOutputHandlerFactory().getNode());
        }
    }

    @Override
    public void startElement(String uri, String localName, String qname, Attributes attributes) throws SAXException {
        this._handler.startElement(uri, localName, qname, attributes);
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qname) throws SAXException {
        this._handler.endElement(namespaceURI, localName, qname);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        this._handler.processingInstruction(target, data);
    }

    @Override
    public void startCDATA() throws SAXException {
        if (this._lexHandler != null) {
            this._lexHandler.startCDATA();
        }
    }

    @Override
    public void endCDATA() throws SAXException {
        if (this._lexHandler != null) {
            this._lexHandler.endCDATA();
        }
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        if (this._lexHandler != null) {
            this._lexHandler.comment(ch, start, length);
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this._handler.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this._locator = locator;
        if (this._handler != null) {
            this._handler.setDocumentLocator(locator);
        }
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        this._handler.skippedEntity(name);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this._handler.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        this._handler.endPrefixMapping(prefix);
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        if (this._lexHandler != null) {
            this._lexHandler.startDTD(name, publicId, systemId);
        }
    }

    @Override
    public void endDTD() throws SAXException {
        if (this._lexHandler != null) {
            this._lexHandler.endDTD();
        }
    }

    @Override
    public void startEntity(String name) throws SAXException {
        if (this._lexHandler != null) {
            this._lexHandler.startEntity(name);
        }
    }

    @Override
    public void endEntity(String name) throws SAXException {
        if (this._lexHandler != null) {
            this._lexHandler.endEntity(name);
        }
    }

    @Override
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
        if (this._dtdHandler != null) {
            this._dtdHandler.unparsedEntityDecl(name, publicId, systemId, notationName);
        }
    }

    @Override
    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
        if (this._dtdHandler != null) {
            this._dtdHandler.notationDecl(name, publicId, systemId);
        }
    }

    @Override
    public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) throws SAXException {
        if (this._declHandler != null) {
            this._declHandler.attributeDecl(eName, aName, type, valueDefault, value);
        }
    }

    @Override
    public void elementDecl(String name, String model) throws SAXException {
        if (this._declHandler != null) {
            this._declHandler.elementDecl(name, model);
        }
    }

    @Override
    public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
        if (this._declHandler != null) {
            this._declHandler.externalEntityDecl(name, publicId, systemId);
        }
    }

    @Override
    public void internalEntityDecl(String name, String value) throws SAXException {
        if (this._declHandler != null) {
            this._declHandler.internalEntityDecl(name, value);
        }
    }
}

