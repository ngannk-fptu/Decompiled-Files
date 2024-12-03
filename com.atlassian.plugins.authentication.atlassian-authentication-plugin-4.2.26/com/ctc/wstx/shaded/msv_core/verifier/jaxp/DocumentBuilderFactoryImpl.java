/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.jaxp;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactory;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.TheFactoryImpl;
import com.ctc.wstx.shaded.msv_core.verifier.jaxp.DocumentBuilderImpl;
import java.io.File;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DocumentBuilderFactoryImpl
extends DocumentBuilderFactory {
    private final DocumentBuilderFactory core;
    private final VerifierFactory jarvFactory;
    private Schema schema;

    public DocumentBuilderFactoryImpl() {
        this(DocumentBuilderFactory.newInstance());
    }

    public DocumentBuilderFactoryImpl(DocumentBuilderFactory _factory) {
        this(_factory, null);
    }

    public DocumentBuilderFactoryImpl(DocumentBuilderFactory _factory, Schema _schema) {
        this.core = _factory;
        this.jarvFactory = new TheFactoryImpl();
        this.schema = _schema;
    }

    public Object getAttribute(String name) {
        if (name.equals("http://www.sun.com/xmlns/msv/features/panicMode")) {
            try {
                return this.jarvFactory.isFeature(name) ? Boolean.TRUE : Boolean.FALSE;
            }
            catch (SAXException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        return this.core.getAttribute(name);
    }

    public void setAttribute(String name, Object value) {
        if (name.equals("http://www.sun.com/xmlns/msv/features/panicMode")) {
            try {
                this.jarvFactory.setFeature(name, (Boolean)value);
            }
            catch (SAXException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        if ("http://www.sun.com/xml/msv/schema".equals(name)) {
            try {
                if (value instanceof String) {
                    this.schema = this.jarvFactory.compileSchema((String)value);
                    return;
                }
                if (value instanceof File) {
                    this.schema = this.jarvFactory.compileSchema((File)value);
                    return;
                }
                if (value instanceof InputSource) {
                    this.schema = this.jarvFactory.compileSchema((InputSource)value);
                    return;
                }
                if (value instanceof InputStream) {
                    this.schema = this.jarvFactory.compileSchema((InputStream)value);
                    return;
                }
                if (value instanceof Schema) {
                    this.schema = (Schema)value;
                    return;
                }
                throw new IllegalArgumentException("unrecognized value type: " + value.getClass().getName());
            }
            catch (Exception e) {
                throw new IllegalArgumentException(e.toString());
            }
        }
        this.core.setAttribute(name, value);
    }

    public boolean isCoalescing() {
        return this.core.isCoalescing();
    }

    public boolean isExpandEntityReference() {
        return this.core.isExpandEntityReferences();
    }

    public boolean isIgnoringComments() {
        return this.core.isIgnoringComments();
    }

    public boolean isIgnoringElementContentWhitespace() {
        return this.core.isIgnoringElementContentWhitespace();
    }

    public boolean isNamespaceAware() {
        return this.core.isNamespaceAware();
    }

    public boolean isValidating() {
        return this.core.isValidating();
    }

    public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        if (this.schema == null) {
            return this.core.newDocumentBuilder();
        }
        return new DocumentBuilderImpl(this.core.newDocumentBuilder(), this.schema);
    }

    public void setCoalescing(boolean newVal) {
        this.core.setCoalescing(newVal);
    }

    public void setExpandEntityReference(boolean newVal) {
        this.core.setExpandEntityReferences(newVal);
    }

    public void setIgnoringComments(boolean newVal) {
        this.core.setIgnoringComments(newVal);
    }

    public void setIgnoringElementContentWhitespace(boolean newVal) {
        this.core.setIgnoringElementContentWhitespace(newVal);
    }

    public void setNamespaceAware(boolean newVal) {
        this.core.setNamespaceAware(newVal);
    }

    public void setValidating(boolean newVal) {
        this.core.setValidating(newVal);
    }

    public boolean getFeature(String name) {
        throw new UnsupportedOperationException();
    }

    public void setFeature(String name, boolean value) {
        throw new UnsupportedOperationException();
    }
}

