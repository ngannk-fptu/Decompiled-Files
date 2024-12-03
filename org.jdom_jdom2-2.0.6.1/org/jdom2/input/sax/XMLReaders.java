/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input.sax;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.jdom2.JDOMException;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum XMLReaders implements XMLReaderJDOMFactory
{
    NONVALIDATING(0),
    DTDVALIDATING(1),
    XSDVALIDATING(2);

    private final int singletonID;

    private XMLReaders(int singletonID) {
        this.singletonID = singletonID;
    }

    private FactorySupplier getSupplier() {
        switch (this.singletonID) {
            case 0: {
                return NONSingleton.INSTANCE;
            }
            case 1: {
                return DTDSingleton.INSTANCE;
            }
            case 2: {
                return XSDSingleton.INSTANCE;
            }
        }
        throw new IllegalStateException("Unknown singletonID: " + this.singletonID);
    }

    @Override
    public XMLReader createXMLReader() throws JDOMException {
        try {
            FactorySupplier supplier = this.getSupplier();
            return supplier.supply().newSAXParser().getXMLReader();
        }
        catch (SAXException e) {
            throw new JDOMException("Unable to create a new XMLReader instance", e);
        }
        catch (ParserConfigurationException e) {
            throw new JDOMException("Unable to create a new XMLReader instance", e);
        }
        catch (Exception e) {
            throw new JDOMException("It was not possible to configure a suitable XMLReader to support " + this, e);
        }
    }

    @Override
    public boolean isValidating() {
        return this.getSupplier().validates();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum XSDSingleton implements FactorySupplier
    {
        INSTANCE;

        private final Exception failcause;
        private final SAXParserFactory factory;

        private XSDSingleton() {
            SAXParserFactory fac = SAXParserFactory.newInstance();
            Exception problem = null;
            fac.setNamespaceAware(true);
            fac.setValidating(false);
            try {
                SchemaFactory sfac = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
                Schema schema = sfac.newSchema();
                fac.setSchema(schema);
            }
            catch (SAXException se) {
                fac = null;
                problem = se;
            }
            catch (IllegalArgumentException iae) {
                fac = null;
                problem = iae;
            }
            catch (UnsupportedOperationException uoe) {
                fac = null;
                problem = uoe;
            }
            this.factory = fac;
            this.failcause = problem;
        }

        @Override
        public SAXParserFactory supply() throws Exception {
            if (this.factory == null) {
                throw this.failcause;
            }
            return this.factory;
        }

        @Override
        public boolean validates() {
            return true;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum DTDSingleton implements FactorySupplier
    {
        INSTANCE;

        private final SAXParserFactory factory;

        private DTDSingleton() {
            SAXParserFactory fac = SAXParserFactory.newInstance();
            fac.setNamespaceAware(true);
            fac.setValidating(true);
            this.factory = fac;
        }

        @Override
        public SAXParserFactory supply() throws Exception {
            return this.factory;
        }

        @Override
        public boolean validates() {
            return true;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum NONSingleton implements FactorySupplier
    {
        INSTANCE;

        private final SAXParserFactory factory;

        private NONSingleton() {
            SAXParserFactory fac = SAXParserFactory.newInstance();
            fac.setNamespaceAware(true);
            fac.setValidating(false);
            this.factory = fac;
        }

        @Override
        public SAXParserFactory supply() throws Exception {
            return this.factory;
        }

        @Override
        public boolean validates() {
            return false;
        }
    }

    private static interface FactorySupplier {
        public SAXParserFactory supply() throws Exception;

        public boolean validates();
    }
}

