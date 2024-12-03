/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input.sax;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.jdom2.JDOMException;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XMLReaderJAXPFactory
implements XMLReaderJDOMFactory {
    private final SAXParserFactory instance;
    private final boolean validating;

    public XMLReaderJAXPFactory(String factoryClassName, ClassLoader classLoader, boolean dtdvalidate) {
        this.instance = SAXParserFactory.newInstance(factoryClassName, classLoader);
        this.instance.setNamespaceAware(true);
        this.instance.setValidating(dtdvalidate);
        this.validating = dtdvalidate;
    }

    public XMLReader createXMLReader() throws JDOMException {
        try {
            return this.instance.newSAXParser().getXMLReader();
        }
        catch (SAXException e) {
            throw new JDOMException("Unable to create a new XMLReader instance", e);
        }
        catch (ParserConfigurationException e) {
            throw new JDOMException("Unable to create a new XMLReader instance", e);
        }
    }

    public boolean isValidating() {
        return this.validating;
    }
}

