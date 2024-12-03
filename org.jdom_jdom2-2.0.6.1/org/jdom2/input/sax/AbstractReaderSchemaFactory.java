/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input.sax;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import org.jdom2.JDOMException;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public abstract class AbstractReaderSchemaFactory
implements XMLReaderJDOMFactory {
    private final SAXParserFactory saxfac;

    public AbstractReaderSchemaFactory(SAXParserFactory fac, Schema schema) {
        if (schema == null) {
            throw new NullPointerException("Cannot create a SchemaXMLReaderFactory with a null schema");
        }
        this.saxfac = fac;
        if (this.saxfac != null) {
            this.saxfac.setNamespaceAware(true);
            this.saxfac.setValidating(false);
            this.saxfac.setSchema(schema);
        }
    }

    public XMLReader createXMLReader() throws JDOMException {
        if (this.saxfac == null) {
            throw new JDOMException("It was not possible to configure a suitable XMLReader to support " + this);
        }
        try {
            return this.saxfac.newSAXParser().getXMLReader();
        }
        catch (SAXException e) {
            throw new JDOMException("Could not create a new Schema-Validating XMLReader.", e);
        }
        catch (ParserConfigurationException e) {
            throw new JDOMException("Could not create a new Schema-Validating XMLReader.", e);
        }
    }

    public boolean isValidating() {
        return true;
    }
}

