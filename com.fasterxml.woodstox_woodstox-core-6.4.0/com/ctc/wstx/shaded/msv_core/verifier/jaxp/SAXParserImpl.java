/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.jaxp;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactory;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFilter;
import java.io.File;
import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderAdapter;

class SAXParserImpl
extends SAXParser {
    private final SAXParser core;
    private Verifier verifier;
    private final VerifierFactory factory;

    SAXParserImpl(SAXParser core, VerifierFactory _jarvFactory, Verifier _verifier) {
        this.core = core;
        this.factory = _jarvFactory;
        this.verifier = _verifier;
    }

    public Parser getParser() throws SAXException {
        return new XMLReaderAdapter(this.getXMLReader());
    }

    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.core.getProperty(name);
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://www.sun.com/xml/msv/schema".equals(name)) {
            try {
                if (value instanceof String) {
                    this.verifier = this.factory.newVerifier((String)value);
                    return;
                }
                if (value instanceof File) {
                    this.verifier = this.factory.newVerifier((File)value);
                    return;
                }
                if (value instanceof InputSource) {
                    this.verifier = this.factory.newVerifier((InputSource)value);
                    return;
                }
                if (value instanceof InputStream) {
                    this.verifier = this.factory.newVerifier((InputStream)value);
                    return;
                }
                if (value instanceof Schema) {
                    this.verifier = ((Schema)value).newVerifier();
                    return;
                }
                throw new SAXNotSupportedException("unrecognized value type: " + value.getClass().getName());
            }
            catch (Exception e) {
                throw new SAXNotRecognizedException(e.toString());
            }
        }
        this.core.setProperty(name, value);
    }

    public XMLReader getXMLReader() throws SAXException {
        XMLReader reader = this.core.getXMLReader();
        if (this.verifier == null) {
            return reader;
        }
        VerifierFilter filter = this.verifier.getVerifierFilter();
        filter.setParent(reader);
        return filter;
    }

    public boolean isNamespaceAware() {
        return this.core.isNamespaceAware();
    }

    public boolean isValidating() {
        return this.core.isValidating();
    }
}

