/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input.sax;

import java.io.File;
import java.net.URL;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.validation.SchemaFactory;
import org.jdom2.JDOMException;
import org.jdom2.input.sax.AbstractReaderXSDFactory;

public class XMLReaderXSDFactory
extends AbstractReaderXSDFactory {
    private static final AbstractReaderXSDFactory.SchemaFactoryProvider xsdschemas = new AbstractReaderXSDFactory.SchemaFactoryProvider(){
        private final ThreadLocal<SchemaFactory> schemafactl = new ThreadLocal();

        public SchemaFactory getSchemaFactory() {
            SchemaFactory sfac = this.schemafactl.get();
            if (sfac == null) {
                sfac = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
                this.schemafactl.set(sfac);
            }
            return sfac;
        }
    };

    public XMLReaderXSDFactory(String ... systemid) throws JDOMException {
        super(SAXParserFactory.newInstance(), xsdschemas, systemid);
    }

    public XMLReaderXSDFactory(String factoryClassName, ClassLoader classloader, String ... systemid) throws JDOMException {
        super(SAXParserFactory.newInstance(factoryClassName, classloader), xsdschemas, systemid);
    }

    public XMLReaderXSDFactory(URL ... systemid) throws JDOMException {
        super(SAXParserFactory.newInstance(), xsdschemas, systemid);
    }

    public XMLReaderXSDFactory(String factoryClassName, ClassLoader classloader, URL ... systemid) throws JDOMException {
        super(SAXParserFactory.newInstance(factoryClassName, classloader), xsdschemas, systemid);
    }

    public XMLReaderXSDFactory(File ... systemid) throws JDOMException {
        super(SAXParserFactory.newInstance(), xsdschemas, systemid);
    }

    public XMLReaderXSDFactory(String factoryClassName, ClassLoader classloader, File ... systemid) throws JDOMException {
        super(SAXParserFactory.newInstance(factoryClassName, classloader), xsdschemas, systemid);
    }

    public XMLReaderXSDFactory(Source ... sources) throws JDOMException {
        super(SAXParserFactory.newInstance(), xsdschemas, sources);
    }

    public XMLReaderXSDFactory(String factoryClassName, ClassLoader classloader, Source ... sources) throws JDOMException {
        super(SAXParserFactory.newInstance(factoryClassName, classloader), xsdschemas, sources);
    }
}

