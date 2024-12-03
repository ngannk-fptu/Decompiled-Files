/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import org.w3c.dom.Document;
import org.xhtmlrenderer.resource.AbstractResource;
import org.xhtmlrenderer.resource.FSEntityResolver;
import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.XRLog;
import org.xhtmlrenderer.util.XRRuntimeException;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

public class XMLResource
extends AbstractResource {
    private Document document;
    private static final XMLResourceBuilder XML_RESOURCE_BUILDER = new XMLResourceBuilder();
    private static boolean useConfiguredParser = true;

    private XMLResource(InputStream stream) {
        super(stream);
    }

    private XMLResource(InputSource source) {
        super(source);
    }

    public static XMLResource load(InputStream stream) {
        return XML_RESOURCE_BUILDER.createXMLResource(new XMLResource(stream));
    }

    public static XMLResource load(InputSource source) {
        return XML_RESOURCE_BUILDER.createXMLResource(new XMLResource(source));
    }

    public static XMLResource load(Reader reader) {
        return XML_RESOURCE_BUILDER.createXMLResource(new XMLResource(new InputSource(reader)));
    }

    public static XMLResource load(Source source) {
        return XML_RESOURCE_BUILDER.createXMLResource(source);
    }

    public Document getDocument() {
        return this.document;
    }

    void setDocument(Document document) {
        this.document = document;
    }

    public static final XMLReader newXMLReader() {
        String xmlReaderClass;
        XMLReader xmlReader;
        block12: {
            xmlReader = null;
            xmlReaderClass = Configuration.valueFor("xr.load.xml-reader");
            try {
                if (xmlReaderClass == null || xmlReaderClass.toLowerCase().equals("default") || !useConfiguredParser) break block12;
                try {
                    Class.forName(xmlReaderClass);
                }
                catch (Exception ex) {
                    useConfiguredParser = false;
                    XRLog.load(Level.WARNING, "The XMLReader class you specified as a configuration property could not be found. Class.forName() failed on " + xmlReaderClass + ". Please check classpath. Use value 'default' in FS configuration if necessary. Will now try JDK default.");
                }
                if (useConfiguredParser) {
                    xmlReader = XMLReaderFactory.createXMLReader(xmlReaderClass);
                }
            }
            catch (Exception ex) {
                XRLog.load(Level.WARNING, "Could not instantiate custom XMLReader class for XML parsing: " + xmlReaderClass + ". Please check classpath. Use value 'default' in FS configuration if necessary. Will now try JDK default.", ex);
            }
        }
        if (xmlReader == null) {
            try {
                xmlReader = XMLReaderFactory.createXMLReader();
                xmlReaderClass = "{JDK default}";
            }
            catch (Exception ex) {
                XRLog.general(ex.getMessage());
            }
        }
        if (xmlReader == null) {
            try {
                XRLog.load(Level.WARNING, "falling back on the default parser");
                SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                xmlReader = parser.getXMLReader();
                xmlReaderClass = "SAXParserFactory default";
            }
            catch (Exception ex) {
                XRLog.general(ex.getMessage());
            }
        }
        if (xmlReader == null) {
            throw new XRRuntimeException("Could not instantiate any SAX 2 parser, including JDK default. The name of the class to use should have been read from the org.xml.sax.driver System property, which is set to: ");
        }
        XRLog.load("SAX XMLReader in use (parser): " + xmlReader.getClass().getName());
        return xmlReader;
    }

    private static abstract class ObjectPool<T> {
        private final Queue<Reference<T>> pool;

        ObjectPool(int capacity) {
            this.pool = new ArrayBlockingQueue<Reference<T>>(capacity);
        }

        protected abstract T newValue();

        T get() {
            T obj = null;
            Reference<T> ref = this.pool.poll();
            if (ref != null) {
                obj = ref.get();
            }
            if (obj == null) {
                obj = this.newValue();
            }
            return obj;
        }

        void release(T obj) {
            this.pool.offer(new SoftReference<T>(obj));
        }
    }

    private static class IdentityTransformerPool
    extends ObjectPool<Transformer> {
        private final TransformerFactory traxFactory;

        IdentityTransformerPool() {
            this(Configuration.valueAsInt("xr.load.parser-pool-capacity", 3));
        }

        IdentityTransformerPool(int capacity) {
            super(capacity);
            TransformerFactory tf = TransformerFactory.newInstance();
            try {
                tf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            }
            catch (TransformerConfigurationException e) {
                XRLog.init(Level.WARNING, "Problem configuring TrAX factory", e);
            }
            this.traxFactory = tf;
        }

        @Override
        protected Transformer newValue() {
            try {
                return this.traxFactory.newTransformer();
            }
            catch (TransformerConfigurationException ex) {
                throw new XRRuntimeException("Failed on configuring TrAX transformer.", ex);
            }
        }
    }

    private static class WhitespacePreservingFilter
    extends XMLFilterImpl
    implements EntityResolver2 {
        WhitespacePreservingFilter(XMLReader parent) {
            super(parent);
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            this.getContentHandler().characters(ch, start, length);
        }

        @Override
        public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
            EntityResolver resolver = this.getEntityResolver();
            if (resolver instanceof EntityResolver2) {
                return ((EntityResolver2)resolver).getExternalSubset(name, baseURI);
            }
            return null;
        }

        @Override
        public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
            EntityResolver resolver = this.getEntityResolver();
            if (resolver instanceof EntityResolver2) {
                return ((EntityResolver2)resolver).resolveEntity(name, publicId, baseURI, systemId);
            }
            return this.resolveEntity(publicId, systemId);
        }
    }

    private static class XMLReaderPool
    extends ObjectPool<XMLReader> {
        private final boolean preserveElementContentWhitespace = Configuration.isFalse("xr.load.ignore-element-content-whitespace", true);

        XMLReaderPool() {
            this(Configuration.valueAsInt("xr.load.parser-pool-capacity", 3));
        }

        XMLReaderPool(int capacity) {
            super(capacity);
        }

        @Override
        protected XMLReader newValue() {
            XMLReader xmlReader = XMLResource.newXMLReader();
            if (this.preserveElementContentWhitespace) {
                xmlReader = new WhitespacePreservingFilter(xmlReader);
            }
            this.addHandlers(xmlReader);
            this.setParserFeatures(xmlReader);
            return xmlReader;
        }

        private void addHandlers(XMLReader xmlReader) {
            xmlReader.setEntityResolver(FSEntityResolver.instance());
            xmlReader.setErrorHandler(new ErrorHandler(){

                @Override
                public void error(SAXParseException ex) {
                    XRLog.load(ex.getMessage());
                }

                @Override
                public void fatalError(SAXParseException ex) {
                    XRLog.load(ex.getMessage());
                }

                @Override
                public void warning(SAXParseException ex) {
                    XRLog.load(ex.getMessage());
                }
            });
        }

        private void setParserFeatures(XMLReader xmlReader) {
            try {
                xmlReader.setFeature("http://xml.org/sax/features/validation", false);
                xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
            }
            catch (SAXException s) {
                XRLog.load(Level.WARNING, "Could not set validation/namespace features for XML parser,exception thrown.", s);
            }
            if (Configuration.isFalse("xr.load.configure-features", false)) {
                XRLog.load(Level.FINE, "SAX Parser: by request, not changing any parser features.");
                return;
            }
            this.setFeature(xmlReader, "http://xml.org/sax/features/validation", "xr.load.validation");
            this.setFeature(xmlReader, "http://xml.org/sax/features/string-interning", "xr.load.string-interning");
            this.setFeature(xmlReader, "http://xml.org/sax/features/namespaces", "xr.load.namespaces");
            this.setFeature(xmlReader, "http://xml.org/sax/features/namespace-prefixes", "xr.load.namespace-prefixes");
            this.setFeature(xmlReader, "http://xml.org/sax/features/use-entity-resolver2", true);
            this.setFeature(xmlReader, "http://xml.org/sax/features/xmlns-uris", true);
        }

        private void setFeature(XMLReader xmlReader, String featureUri, String configName) {
            this.setFeature(xmlReader, featureUri, Configuration.isTrue(configName, false));
        }

        private void setFeature(XMLReader xmlReader, String featureUri, boolean value) {
            try {
                xmlReader.setFeature(featureUri, value);
                XRLog.load(Level.FINE, "SAX Parser feature: " + featureUri.substring(featureUri.lastIndexOf("/")) + " set to " + xmlReader.getFeature(featureUri));
            }
            catch (SAXNotSupportedException ex) {
                XRLog.load(Level.WARNING, "SAX feature not supported on this XMLReader: " + featureUri);
            }
            catch (SAXNotRecognizedException ex) {
                XRLog.load(Level.WARNING, "SAX feature not recognized on this XMLReader: " + featureUri + ". Feature may be properly named, but not recognized by this parser.");
            }
        }
    }

    private static class XMLResourceBuilder {
        private final XMLReaderPool parserPool = new XMLReaderPool();
        private final IdentityTransformerPool traxPool = new IdentityTransformerPool();

        private XMLResourceBuilder() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        XMLResource createXMLResource(XMLResource target) {
            Document document;
            long st = System.currentTimeMillis();
            XMLReader xmlReader = (XMLReader)this.parserPool.get();
            try {
                document = this.transform(new SAXSource(xmlReader, target.getResourceInputSource()));
            }
            finally {
                this.parserPool.release(xmlReader);
            }
            long end = System.currentTimeMillis();
            target.setElapsedLoadTime(end - st);
            XRLog.load("Loaded document in ~" + target.getElapsedLoadTime() + "ms");
            target.setDocument(document);
            return target;
        }

        public XMLResource createXMLResource(Source source) {
            long st = System.currentTimeMillis();
            Document document = this.transform(source);
            long end = System.currentTimeMillis();
            XMLResource target = new XMLResource(null);
            target.setElapsedLoadTime(end - st);
            XRLog.load("Loaded document in ~" + target.getElapsedLoadTime() + "ms");
            target.setDocument(document);
            return target;
        }

        private Document transform(Source source) {
            DOMResult result = new DOMResult();
            Transformer idTransform = (Transformer)this.traxPool.get();
            try {
                idTransform.transform(source, result);
            }
            catch (Exception ex) {
                throw new XRRuntimeException("Can't load the XML resource (using TrAX transformer). " + ex.getMessage(), ex);
            }
            finally {
                this.traxPool.release(idTransform);
            }
            return (Document)result.getNode();
        }
    }
}

