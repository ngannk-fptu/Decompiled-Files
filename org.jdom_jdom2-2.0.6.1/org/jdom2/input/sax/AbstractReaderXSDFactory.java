/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input.sax;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.jdom2.JDOMException;
import org.jdom2.input.sax.AbstractReaderSchemaFactory;
import org.xml.sax.SAXException;

public class AbstractReaderXSDFactory
extends AbstractReaderSchemaFactory {
    private static final Schema getSchemaFromString(SchemaFactoryProvider sfp, String ... systemID) throws JDOMException {
        if (systemID == null) {
            throw new NullPointerException("Cannot specify a null input array");
        }
        if (systemID.length == 0) {
            throw new IllegalArgumentException("You need at least one XSD source for an XML Schema validator");
        }
        Source[] urls = new Source[systemID.length];
        for (int i = 0; i < systemID.length; ++i) {
            if (systemID[i] == null) {
                throw new NullPointerException("Cannot specify a null SystemID");
            }
            urls[i] = new StreamSource(systemID[i]);
        }
        return AbstractReaderXSDFactory.getSchemaFromSource(sfp, urls);
    }

    private static final Schema getSchemaFromFile(SchemaFactoryProvider sfp, File ... systemID) throws JDOMException {
        if (systemID == null) {
            throw new NullPointerException("Cannot specify a null input array");
        }
        if (systemID.length == 0) {
            throw new IllegalArgumentException("You need at least one XSD source for an XML Schema validator");
        }
        Source[] sources = new Source[systemID.length];
        for (int i = 0; i < systemID.length; ++i) {
            if (systemID[i] == null) {
                throw new NullPointerException("Cannot specify a null SystemID");
            }
            sources[i] = new StreamSource(systemID[i]);
        }
        return AbstractReaderXSDFactory.getSchemaFromSource(sfp, sources);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final Schema getSchemaFromURL(SchemaFactoryProvider sfp, URL ... systemID) throws JDOMException {
        if (systemID == null) {
            throw new NullPointerException("Cannot specify a null input array");
        }
        if (systemID.length == 0) {
            throw new IllegalArgumentException("You need at least one XSD source for an XML Schema validator");
        }
        InputStream[] streams = new InputStream[systemID.length];
        try {
            Source[] sources = new Source[systemID.length];
            for (int i = 0; i < systemID.length; ++i) {
                if (systemID[i] == null) {
                    throw new NullPointerException("Cannot specify a null SystemID");
                }
                InputStream is = null;
                try {
                    is = systemID[i].openStream();
                }
                catch (IOException e) {
                    throw new JDOMException("Unable to read Schema URL " + systemID[i].toString(), e);
                }
                streams[i] = is;
                sources[i] = new StreamSource(is, systemID[i].toString());
            }
            Schema schema = AbstractReaderXSDFactory.getSchemaFromSource(sfp, sources);
            return schema;
        }
        finally {
            for (InputStream is : streams) {
                if (is == null) continue;
                try {
                    is.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    private static final Schema getSchemaFromSource(SchemaFactoryProvider sfp, Source ... sources) throws JDOMException {
        if (sources == null) {
            throw new NullPointerException("Cannot specify a null input array");
        }
        if (sources.length == 0) {
            throw new IllegalArgumentException("You need at least one XSD Source for an XML Schema validator");
        }
        try {
            SchemaFactory sfac = sfp.getSchemaFactory();
            if (sfac == null) {
                throw new JDOMException("Unable to create XSDSchema validator.");
            }
            return sfac.newSchema(sources);
        }
        catch (SAXException e) {
            String msg = Arrays.toString(sources);
            throw new JDOMException("Unable to create a Schema for Sources " + msg, e);
        }
    }

    public AbstractReaderXSDFactory(SAXParserFactory fac, SchemaFactoryProvider sfp, String ... systemid) throws JDOMException {
        super(fac, AbstractReaderXSDFactory.getSchemaFromString(sfp, systemid));
    }

    public AbstractReaderXSDFactory(SAXParserFactory fac, SchemaFactoryProvider sfp, URL ... systemid) throws JDOMException {
        super(fac, AbstractReaderXSDFactory.getSchemaFromURL(sfp, systemid));
    }

    public AbstractReaderXSDFactory(SAXParserFactory fac, SchemaFactoryProvider sfp, File ... systemid) throws JDOMException {
        super(fac, AbstractReaderXSDFactory.getSchemaFromFile(sfp, systemid));
    }

    public AbstractReaderXSDFactory(SAXParserFactory fac, SchemaFactoryProvider sfp, Source ... sources) throws JDOMException {
        super(fac, AbstractReaderXSDFactory.getSchemaFromSource(sfp, sources));
    }

    protected static interface SchemaFactoryProvider {
        public SchemaFactory getSchemaFactory();
    }
}

