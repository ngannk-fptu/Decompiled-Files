/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input.sax;

import org.jdom2.JDOMException;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XMLReaderSAX2Factory
implements XMLReaderJDOMFactory {
    private final boolean validate;
    private final String saxdriver;

    public XMLReaderSAX2Factory(boolean validate) {
        this(validate, null);
    }

    public XMLReaderSAX2Factory(boolean validate, String saxdriver) {
        this.validate = validate;
        this.saxdriver = saxdriver;
    }

    public XMLReader createXMLReader() throws JDOMException {
        try {
            XMLReader reader = this.saxdriver == null ? XMLReaderFactory.createXMLReader() : XMLReaderFactory.createXMLReader(this.saxdriver);
            reader.setFeature("http://xml.org/sax/features/validation", this.validate);
            reader.setFeature("http://xml.org/sax/features/namespaces", true);
            reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            return reader;
        }
        catch (SAXException e) {
            throw new JDOMException("Unable to create SAX2 XMLReader.", e);
        }
    }

    public String getDriverClassName() {
        return this.saxdriver;
    }

    public boolean isValidating() {
        return this.validate;
    }
}

