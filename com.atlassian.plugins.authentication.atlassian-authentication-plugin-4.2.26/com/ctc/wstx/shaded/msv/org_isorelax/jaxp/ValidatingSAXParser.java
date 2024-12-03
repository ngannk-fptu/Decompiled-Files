/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.jaxp;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

class ValidatingSAXParser
extends SAXParser {
    protected SAXParser _WrappedParser;
    protected Verifier _Verifier;

    protected ValidatingSAXParser(SAXParser saxparser, Verifier verifier) {
        this._WrappedParser = saxparser;
        this._Verifier = verifier;
    }

    public Parser getParser() {
        throw new UnsupportedOperationException("getParser() method is not supported. Use getXMLReader().");
    }

    public XMLReader getXMLReader() throws SAXException {
        VerifierFilter verifierfilter = this._Verifier.getVerifierFilter();
        verifierfilter.setParent(this._WrappedParser.getXMLReader());
        return verifierfilter;
    }

    public boolean isNamespaceAware() {
        return this._WrappedParser.isNamespaceAware();
    }

    public boolean isValidating() {
        return true;
    }

    public void setProperty(String s, Object obj) throws SAXNotRecognizedException, SAXNotSupportedException {
        this._WrappedParser.setProperty(s, obj);
    }

    public Object getProperty(String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        return this._WrappedParser.getProperty(s);
    }

    public void parse(File file, HandlerBase handlerbase) {
        throw new UnsupportedOperationException("SAX1 features are not supported");
    }

    public void parse(InputSource inputsource, HandlerBase handlerbase) {
        throw new UnsupportedOperationException("SAX1 features are not supported");
    }

    public void parse(InputStream inputstream, HandlerBase handlerbase) {
        throw new UnsupportedOperationException("SAX1 features are not supported");
    }

    public void parse(InputStream inputstream, HandlerBase handlerbase, String s) {
        throw new UnsupportedOperationException("SAX1 features are not supported");
    }

    public void parse(String s, HandlerBase handlerbase) {
        throw new UnsupportedOperationException("SAX1 features are not supported");
    }

    public void parse(File file, DefaultHandler defaulthandler) throws SAXException, IOException {
        XMLReader xmlreader = this.getXMLReader();
        InputSource inputsource = new InputSource(new FileInputStream(file));
        xmlreader.setContentHandler(defaulthandler);
        xmlreader.parse(inputsource);
    }

    public void parse(InputSource inputsource, DefaultHandler defaulthandler) throws SAXException, IOException {
        XMLReader xmlreader = this.getXMLReader();
        xmlreader.setContentHandler(defaulthandler);
        xmlreader.parse(inputsource);
    }

    public void parse(InputStream inputstream, DefaultHandler defaulthandler) throws SAXException, IOException {
        XMLReader xmlreader = this.getXMLReader();
        InputSource inputsource = new InputSource(inputstream);
        xmlreader.setContentHandler(defaulthandler);
        xmlreader.parse(inputsource);
    }

    public void parse(InputStream inputstream, DefaultHandler defaulthandler, String s) throws SAXException, IOException {
        XMLReader xmlreader = this.getXMLReader();
        InputSource inputsource = new InputSource(inputstream);
        inputsource.setSystemId(s);
        xmlreader.setContentHandler(defaulthandler);
        xmlreader.parse(inputsource);
    }

    public void parse(String s, DefaultHandler defaulthandler) throws SAXException, IOException {
        XMLReader xmlreader = this.getXMLReader();
        InputSource inputsource = new InputSource(s);
        xmlreader.setContentHandler(defaulthandler);
        xmlreader.parse(inputsource);
    }
}

