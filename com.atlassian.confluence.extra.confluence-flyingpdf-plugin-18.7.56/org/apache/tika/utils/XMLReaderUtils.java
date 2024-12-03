/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.ParseContext;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XMLReaderUtils
implements Serializable {
    private static final long serialVersionUID = 6110455808615143122L;
    private static final Logger LOG = Logger.getLogger(XMLReaderUtils.class.getName());
    private static final String XERCES_SECURITY_MANAGER = "org.apache.xerces.util.SecurityManager";
    private static final String XERCES_SECURITY_MANAGER_PROPERTY = "http://apache.org/xml/properties/security-manager";
    private static final ContentHandler IGNORING_CONTENT_HANDLER = new DefaultHandler();
    private static final DTDHandler IGNORING_DTD_HANDLER = new DTDHandler(){

        @Override
        public void notationDecl(String name, String publicId, String systemId) throws SAXException {
        }

        @Override
        public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
        }
    };
    private static final ErrorHandler IGNORING_ERROR_HANDLER = new ErrorHandler(){

        @Override
        public void warning(SAXParseException exception) throws SAXException {
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
        }
    };
    public static final int DEFAULT_POOL_SIZE = 10;
    private static int POOL_SIZE = 10;
    private static long LAST_LOG = -1L;
    private static final String JAXP_ENTITY_EXPANSION_LIMIT_KEY = "jdk.xml.entityExpansionLimit";
    public static final int DEFAULT_MAX_ENTITY_EXPANSIONS = 20;
    private static volatile int MAX_ENTITY_EXPANSIONS = XMLReaderUtils.determineMaxEntityExpansions();
    private static final ReentrantReadWriteLock SAX_READ_WRITE_LOCK = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock DOM_READ_WRITE_LOCK = new ReentrantReadWriteLock();
    private static ArrayBlockingQueue<PoolSAXParser> SAX_PARSERS = new ArrayBlockingQueue(POOL_SIZE);
    private static ArrayBlockingQueue<PoolDOMBuilder> DOM_BUILDERS = new ArrayBlockingQueue(POOL_SIZE);
    private static final AtomicInteger POOL_GENERATION = new AtomicInteger();
    private static final EntityResolver IGNORING_SAX_ENTITY_RESOLVER;
    private static final XMLResolver IGNORING_STAX_ENTITY_RESOLVER;

    private static int determineMaxEntityExpansions() {
        String expansionLimit = System.getProperty(JAXP_ENTITY_EXPANSION_LIMIT_KEY);
        if (expansionLimit != null) {
            try {
                return Integer.parseInt(expansionLimit);
            }
            catch (NumberFormatException e) {
                LOG.log(Level.WARNING, "Couldn't parse an integer for the entity expansion limit:" + expansionLimit + "; backing off to default: " + 20);
            }
        }
        return 20;
    }

    public static void setMaxEntityExpansions(int maxEntityExpansions) {
        MAX_ENTITY_EXPANSIONS = maxEntityExpansions;
    }

    public static XMLReader getXMLReader() throws TikaException {
        XMLReader reader;
        try {
            reader = XMLReaderUtils.getSAXParser().getXMLReader();
        }
        catch (SAXException e) {
            throw new TikaException("Unable to create an XMLReader", e);
        }
        reader.setEntityResolver(IGNORING_SAX_ENTITY_RESOLVER);
        return reader;
    }

    public static SAXParser getSAXParser() throws TikaException {
        try {
            SAXParser parser = XMLReaderUtils.getSAXParserFactory().newSAXParser();
            XMLReaderUtils.trySetXercesSecurityManager(parser);
            return parser;
        }
        catch (ParserConfigurationException e) {
            throw new TikaException("Unable to configure a SAX parser", e);
        }
        catch (SAXException e) {
            throw new TikaException("Unable to create a SAX parser", e);
        }
    }

    public static SAXParserFactory getSAXParserFactory() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        XMLReaderUtils.trySetSAXFeature(factory, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        XMLReaderUtils.trySetSAXFeature(factory, "http://xml.org/sax/features/external-general-entities", false);
        XMLReaderUtils.trySetSAXFeature(factory, "http://xml.org/sax/features/external-parameter-entities", false);
        XMLReaderUtils.trySetSAXFeature(factory, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        XMLReaderUtils.trySetSAXFeature(factory, "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        return factory;
    }

    public static DocumentBuilderFactory getDocumentBuilderFactory() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(false);
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        XMLReaderUtils.trySetSAXFeature(factory, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        XMLReaderUtils.trySetSAXFeature(factory, "http://xml.org/sax/features/external-general-entities", false);
        XMLReaderUtils.trySetSAXFeature(factory, "http://xml.org/sax/features/external-parameter-entities", false);
        XMLReaderUtils.trySetSAXFeature(factory, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        XMLReaderUtils.trySetSAXFeature(factory, "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        XMLReaderUtils.trySetXercesSecurityManager(factory);
        return factory;
    }

    public static DocumentBuilder getDocumentBuilder() throws TikaException {
        try {
            DocumentBuilderFactory documentBuilderFactory = XMLReaderUtils.getDocumentBuilderFactory();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver(IGNORING_SAX_ENTITY_RESOLVER);
            documentBuilder.setErrorHandler(null);
            return documentBuilder;
        }
        catch (ParserConfigurationException e) {
            throw new TikaException("XML parser not available", e);
        }
    }

    public static XMLInputFactory getXMLInputFactory() {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        XMLReaderUtils.tryToSetStaxProperty(factory, "javax.xml.stream.isNamespaceAware", true);
        XMLReaderUtils.tryToSetStaxProperty(factory, "javax.xml.stream.isValidating", false);
        factory.setXMLResolver(IGNORING_STAX_ENTITY_RESOLVER);
        XMLReaderUtils.trySetStaxSecurityManager(factory);
        return factory;
    }

    private static void trySetTransformerAttribute(TransformerFactory transformerFactory, String attribute, String value) {
        try {
            transformerFactory.setAttribute(attribute, value);
        }
        catch (SecurityException e) {
            throw e;
        }
        catch (Exception e) {
            LOG.log(Level.WARNING, "Transformer Attribute unsupported: " + attribute, e);
        }
        catch (AbstractMethodError ame) {
            LOG.log(Level.WARNING, attribute, ame);
        }
    }

    private static void trySetSAXFeature(SAXParserFactory saxParserFactory, String feature, boolean enabled) {
        try {
            saxParserFactory.setFeature(feature, enabled);
        }
        catch (SecurityException e) {
            throw e;
        }
        catch (Exception e) {
            LOG.log(Level.WARNING, "SAX Feature unsupported: " + feature, e);
        }
        catch (AbstractMethodError ame) {
            LOG.log(Level.WARNING, "Cannot set SAX feature because outdated XML parser in classpath: " + feature, ame);
        }
    }

    private static void trySetSAXFeature(DocumentBuilderFactory documentBuilderFactory, String feature, boolean enabled) {
        try {
            documentBuilderFactory.setFeature(feature, enabled);
        }
        catch (Exception e) {
            LOG.log(Level.WARNING, "SAX Feature unsupported: " + feature, e);
        }
        catch (AbstractMethodError ame) {
            LOG.log(Level.WARNING, "Cannot set SAX feature because outdated XML parser in classpath: " + feature, ame);
        }
    }

    private static void tryToSetStaxProperty(XMLInputFactory factory, String key, boolean value) {
        try {
            factory.setProperty(key, value);
        }
        catch (IllegalArgumentException e) {
            LOG.log(Level.WARNING, "StAX Feature unsupported: " + key, e);
        }
    }

    public static Transformer getTransformer() throws TikaException {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            XMLReaderUtils.trySetTransformerAttribute(transformerFactory, "http://javax.xml.XMLConstants/property/accessExternalDTD", "");
            XMLReaderUtils.trySetTransformerAttribute(transformerFactory, "http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
            return transformerFactory.newTransformer();
        }
        catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
            throw new TikaException("Transformer not available", e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Document buildDOM(InputStream is, ParseContext context) throws TikaException, IOException, SAXException {
        DocumentBuilder builder = context.get(DocumentBuilder.class);
        PoolDOMBuilder poolBuilder = null;
        if (builder == null) {
            poolBuilder = XMLReaderUtils.acquireDOMBuilder();
            builder = poolBuilder.getDocumentBuilder();
        }
        try {
            Document document = builder.parse(is);
            return document;
        }
        finally {
            if (poolBuilder != null) {
                XMLReaderUtils.releaseDOMBuilder(poolBuilder);
            }
        }
    }

    public static Document buildDOM(Path path) throws TikaException, IOException, SAXException {
        try (InputStream is = Files.newInputStream(path, new OpenOption[0]);){
            Document document = XMLReaderUtils.buildDOM(is);
            return document;
        }
    }

    public static Document buildDOM(String uriString) throws TikaException, IOException, SAXException {
        PoolDOMBuilder builder = XMLReaderUtils.acquireDOMBuilder();
        try {
            Document document = builder.getDocumentBuilder().parse(uriString);
            return document;
        }
        finally {
            XMLReaderUtils.releaseDOMBuilder(builder);
        }
    }

    public static Document buildDOM(InputStream is) throws TikaException, IOException, SAXException {
        PoolDOMBuilder builder = XMLReaderUtils.acquireDOMBuilder();
        try {
            Document document = builder.getDocumentBuilder().parse(is);
            return document;
        }
        finally {
            XMLReaderUtils.releaseDOMBuilder(builder);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void parseSAX(InputStream is, DefaultHandler contentHandler, ParseContext context) throws TikaException, IOException, SAXException {
        SAXParser saxParser = context.get(SAXParser.class);
        PoolSAXParser poolSAXParser = null;
        if (saxParser == null) {
            poolSAXParser = XMLReaderUtils.acquireSAXParser();
            saxParser = poolSAXParser.getSAXParser();
        }
        try {
            saxParser.parse(is, contentHandler);
        }
        finally {
            if (poolSAXParser != null) {
                XMLReaderUtils.releaseParser(poolSAXParser);
            }
        }
    }

    private static PoolDOMBuilder acquireDOMBuilder() throws TikaException {
        int waiting = 0;
        long lastWarn = -1L;
        do {
            PoolDOMBuilder builder = null;
            DOM_READ_WRITE_LOCK.readLock().lock();
            try {
                builder = DOM_BUILDERS.poll(100L, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                throw new TikaException("interrupted while waiting for DOMBuilder", e);
            }
            finally {
                DOM_READ_WRITE_LOCK.readLock().unlock();
            }
            if (builder != null) {
                return builder;
            }
            if (lastWarn >= 0L && System.currentTimeMillis() - lastWarn <= 1000L) continue;
            LOG.log(Level.WARNING, "Contention waiting for a DOMParser. Consider increasing the XMLReaderUtils.POOL_SIZE");
            lastWarn = System.currentTimeMillis();
        } while (++waiting <= 3000);
        XMLReaderUtils.setPoolSize(POOL_SIZE);
        throw new TikaException("Waited more than 5 minutes for a DocumentBuilder; This could indicate that a parser has not correctly released its DocumentBuilder. Please report this to the Tika team: dev@tika.apache.org");
    }

    private static void releaseDOMBuilder(PoolDOMBuilder builder) {
        if (builder.getPoolGeneration() != POOL_GENERATION.get()) {
            return;
        }
        try {
            builder.reset();
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            // empty catch block
        }
        DOM_READ_WRITE_LOCK.readLock().lock();
        try {
            boolean success = DOM_BUILDERS.offer(builder);
            if (!success) {
                LOG.warning("DocumentBuilder not taken back into pool.  If you haven't resized the pool, this could be a sign that there are more calls to 'acquire' than to 'release'");
            }
        }
        finally {
            DOM_READ_WRITE_LOCK.readLock().unlock();
        }
    }

    private static PoolSAXParser acquireSAXParser() throws TikaException {
        int waiting = 0;
        long lastWarn = -1L;
        do {
            PoolSAXParser parser = null;
            SAX_READ_WRITE_LOCK.readLock().lock();
            try {
                parser = SAX_PARSERS.poll(100L, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                throw new TikaException("interrupted while waiting for SAXParser", e);
            }
            finally {
                SAX_READ_WRITE_LOCK.readLock().unlock();
            }
            if (parser != null) {
                return parser;
            }
            if (lastWarn >= 0L && System.currentTimeMillis() - lastWarn <= 1000L) continue;
            LOG.warning("Contention waiting for a SAXParser. Consider increasing the XMLReaderUtils.POOL_SIZE");
            lastWarn = System.currentTimeMillis();
        } while (++waiting <= 3000);
        XMLReaderUtils.setPoolSize(POOL_SIZE);
        throw new TikaException("Waited more than 5 minutes for a SAXParser; This could indicate that a parser has not correctly released its SAXParser. Please report this to the Tika team: dev@tika.apache.org");
    }

    private static void releaseParser(PoolSAXParser parser) {
        try {
            parser.reset();
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            // empty catch block
        }
        if (parser.getGeneration() != POOL_GENERATION.get()) {
            return;
        }
        SAX_READ_WRITE_LOCK.readLock().lock();
        try {
            boolean success = SAX_PARSERS.offer(parser);
            if (!success) {
                LOG.warning("SAXParser not taken back into pool.  If you haven't resized the pool, this could be a sign that there are more calls to 'acquire' than to 'release'");
            }
        }
        finally {
            SAX_READ_WRITE_LOCK.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void setPoolSize(int poolSize) throws TikaException {
        SAX_READ_WRITE_LOCK.writeLock().lock();
        try {
            for (PoolSAXParser parser : SAX_PARSERS) {
                parser.reset();
            }
            SAX_PARSERS.clear();
            SAX_PARSERS = new ArrayBlockingQueue(poolSize);
            int generation = POOL_GENERATION.incrementAndGet();
            for (int i = 0; i < poolSize; ++i) {
                try {
                    SAX_PARSERS.offer(XMLReaderUtils.buildPoolParser(generation, XMLReaderUtils.getSAXParserFactory().newSAXParser()));
                    continue;
                }
                catch (ParserConfigurationException | SAXException e) {
                    throw new TikaException("problem creating sax parser", e);
                }
            }
        }
        finally {
            SAX_READ_WRITE_LOCK.writeLock().unlock();
        }
        DOM_READ_WRITE_LOCK.writeLock().lock();
        try {
            DOM_BUILDERS.clear();
            DOM_BUILDERS = new ArrayBlockingQueue(poolSize);
            for (int i = 0; i < poolSize; ++i) {
                DOM_BUILDERS.offer(new PoolDOMBuilder(POOL_GENERATION.get(), XMLReaderUtils.getDocumentBuilder()));
            }
        }
        finally {
            DOM_READ_WRITE_LOCK.writeLock().unlock();
        }
        POOL_SIZE = poolSize;
    }

    private static void trySetXercesSecurityManager(DocumentBuilderFactory factory) {
        block6: {
            for (String securityManagerClassName : new String[]{XERCES_SECURITY_MANAGER}) {
                try {
                    Object mgr = Class.forName(securityManagerClassName).newInstance();
                    Method setLimit = mgr.getClass().getMethod("setEntityExpansionLimit", Integer.TYPE);
                    setLimit.invoke(mgr, MAX_ENTITY_EXPANSIONS);
                    factory.setAttribute(XERCES_SECURITY_MANAGER_PROPERTY, mgr);
                    return;
                }
                catch (ClassNotFoundException mgr) {
                }
                catch (Throwable e) {
                    if (System.currentTimeMillis() <= LAST_LOG + TimeUnit.MINUTES.toMillis(5L)) continue;
                    LOG.log(Level.WARNING, "SAX Security Manager could not be setup [log suppressed for 5 minutes]", e);
                    LAST_LOG = System.currentTimeMillis();
                }
            }
            try {
                factory.setAttribute("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", MAX_ENTITY_EXPANSIONS);
            }
            catch (IllegalArgumentException e) {
                if (System.currentTimeMillis() <= LAST_LOG + TimeUnit.MINUTES.toMillis(5L)) break block6;
                LOG.log(Level.WARNING, "SAX Security Manager could not be setup [log suppressed for 5 minutes]", e);
                LAST_LOG = System.currentTimeMillis();
            }
        }
    }

    private static void trySetXercesSecurityManager(SAXParser parser) {
        block6: {
            for (String securityManagerClassName : new String[]{XERCES_SECURITY_MANAGER}) {
                try {
                    Object mgr = Class.forName(securityManagerClassName).newInstance();
                    Method setLimit = mgr.getClass().getMethod("setEntityExpansionLimit", Integer.TYPE);
                    setLimit.invoke(mgr, MAX_ENTITY_EXPANSIONS);
                    parser.setProperty(XERCES_SECURITY_MANAGER_PROPERTY, mgr);
                    return;
                }
                catch (ClassNotFoundException mgr) {
                }
                catch (Throwable e) {
                    if (System.currentTimeMillis() <= LAST_LOG + TimeUnit.MINUTES.toMillis(5L)) continue;
                    LOG.log(Level.WARNING, "SAX Security Manager could not be setup [log suppressed for 5 minutes]", e);
                    LAST_LOG = System.currentTimeMillis();
                }
            }
            try {
                parser.setProperty("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", MAX_ENTITY_EXPANSIONS);
            }
            catch (SAXException e) {
                if (System.currentTimeMillis() <= LAST_LOG + TimeUnit.MINUTES.toMillis(5L)) break block6;
                LOG.log(Level.WARNING, "SAX Security Manager could not be setup [log suppressed for 5 minutes]", e);
                LAST_LOG = System.currentTimeMillis();
            }
        }
    }

    private static void trySetStaxSecurityManager(XMLInputFactory inputFactory) {
        block2: {
            try {
                inputFactory.setProperty("com.ctc.wstx.maxEntityCount", MAX_ENTITY_EXPANSIONS);
            }
            catch (IllegalArgumentException e) {
                if (System.currentTimeMillis() <= LAST_LOG + TimeUnit.MINUTES.toMillis(5L)) break block2;
                LOG.log(Level.WARNING, "SAX Security Manager could not be setup [log suppressed for 5 minutes]", e);
                LAST_LOG = System.currentTimeMillis();
            }
        }
    }

    public static int getPoolSize() {
        return POOL_SIZE;
    }

    public static int getMaxEntityExpansions() {
        return MAX_ENTITY_EXPANSIONS;
    }

    public static String getAttrValue(String localName, Attributes atts) {
        for (int i = 0; i < atts.getLength(); ++i) {
            if (!localName.equals(atts.getLocalName(i))) continue;
            return atts.getValue(i);
        }
        return null;
    }

    private static PoolSAXParser buildPoolParser(int generation, SAXParser parser) {
        boolean canSetJaxPEntity;
        boolean hasSecurityManager;
        boolean canReset;
        block13: {
            block12: {
                canReset = false;
                try {
                    parser.reset();
                    canReset = true;
                }
                catch (UnsupportedOperationException e) {
                    canReset = false;
                }
                hasSecurityManager = false;
                try {
                    Object mgr = Class.forName(XERCES_SECURITY_MANAGER).newInstance();
                    Method setLimit = mgr.getClass().getMethod("setEntityExpansionLimit", Integer.TYPE);
                    setLimit.invoke(mgr, MAX_ENTITY_EXPANSIONS);
                    parser.setProperty(XERCES_SECURITY_MANAGER_PROPERTY, mgr);
                    hasSecurityManager = true;
                }
                catch (SecurityException e) {
                    throw e;
                }
                catch (ClassNotFoundException e) {
                }
                catch (Throwable e) {
                    if (System.currentTimeMillis() <= LAST_LOG + TimeUnit.MINUTES.toMillis(5L)) break block12;
                    LOG.log(Level.WARNING, "SAX Security Manager could not be setup [log suppressed for 5 minutes]", e);
                    LAST_LOG = System.currentTimeMillis();
                }
            }
            canSetJaxPEntity = false;
            if (!hasSecurityManager) {
                try {
                    parser.setProperty("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", MAX_ENTITY_EXPANSIONS);
                    canSetJaxPEntity = true;
                }
                catch (SAXException e) {
                    if (System.currentTimeMillis() <= LAST_LOG + TimeUnit.MINUTES.toMillis(5L)) break block13;
                    LOG.log(Level.WARNING, "SAX Security Manager could not be setup [log suppressed for 5 minutes]", e);
                    LAST_LOG = System.currentTimeMillis();
                }
            }
        }
        if (!canReset && hasSecurityManager) {
            return new XercesPoolSAXParser(generation, parser);
        }
        if (canReset && hasSecurityManager) {
            return new Xerces2PoolSAXParser(generation, parser);
        }
        if (canReset && !hasSecurityManager && canSetJaxPEntity) {
            return new BuiltInPoolSAXParser(generation, parser);
        }
        return new UnrecognizedPoolSAXParser(generation, parser);
    }

    private static void clearReader(XMLReader reader) {
        if (reader == null) {
            return;
        }
        reader.setContentHandler(IGNORING_CONTENT_HANDLER);
        reader.setDTDHandler(IGNORING_DTD_HANDLER);
        reader.setEntityResolver(IGNORING_SAX_ENTITY_RESOLVER);
        reader.setErrorHandler(IGNORING_ERROR_HANDLER);
    }

    static {
        try {
            XMLReaderUtils.setPoolSize(POOL_SIZE);
        }
        catch (TikaException e) {
            throw new RuntimeException("problem initializing SAXParser and DOMBuilder pools", e);
        }
        IGNORING_SAX_ENTITY_RESOLVER = new EntityResolver(){

            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                return new InputSource(new StringReader(""));
            }
        };
        IGNORING_STAX_ENTITY_RESOLVER = new XMLResolver(){

            @Override
            public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException {
                return "";
            }
        };
    }

    private static class UnrecognizedPoolSAXParser
    extends PoolSAXParser {
        public UnrecognizedPoolSAXParser(int generation, SAXParser parser) {
            super(generation, parser);
        }

        @Override
        void reset() {
            try {
                this.saxParser.reset();
            }
            catch (UnsupportedOperationException unsupportedOperationException) {
                // empty catch block
            }
            try {
                XMLReader reader = this.saxParser.getXMLReader();
                XMLReaderUtils.clearReader(reader);
            }
            catch (SAXException sAXException) {
                // empty catch block
            }
            XMLReaderUtils.trySetXercesSecurityManager(this.saxParser);
        }
    }

    private static class BuiltInPoolSAXParser
    extends PoolSAXParser {
        public BuiltInPoolSAXParser(int generation, SAXParser parser) {
            super(generation, parser);
        }

        @Override
        void reset() {
            this.saxParser.reset();
            try {
                XMLReader reader = this.saxParser.getXMLReader();
                XMLReaderUtils.clearReader(reader);
            }
            catch (SAXException sAXException) {
                // empty catch block
            }
        }
    }

    private static class Xerces2PoolSAXParser
    extends PoolSAXParser {
        public Xerces2PoolSAXParser(int generation, SAXParser parser) {
            super(generation, parser);
        }

        @Override
        void reset() {
            try {
                Object object = this.saxParser.getProperty(XMLReaderUtils.XERCES_SECURITY_MANAGER_PROPERTY);
                this.saxParser.reset();
                this.saxParser.setProperty(XMLReaderUtils.XERCES_SECURITY_MANAGER_PROPERTY, object);
            }
            catch (SAXException e) {
                LOG.log(Level.WARNING, "problem resetting sax parser", e);
            }
            try {
                XMLReader reader = this.saxParser.getXMLReader();
                XMLReaderUtils.clearReader(reader);
            }
            catch (SAXException sAXException) {
                // empty catch block
            }
        }
    }

    private static class XercesPoolSAXParser
    extends PoolSAXParser {
        public XercesPoolSAXParser(int generation, SAXParser parser) {
            super(generation, parser);
        }

        @Override
        public void reset() {
            try {
                XMLReader reader = this.saxParser.getXMLReader();
                XMLReaderUtils.clearReader(reader);
            }
            catch (SAXException sAXException) {
                // empty catch block
            }
        }
    }

    private static abstract class PoolSAXParser {
        final int poolGeneration;
        final SAXParser saxParser;

        PoolSAXParser(int poolGeneration, SAXParser saxParser) {
            this.poolGeneration = poolGeneration;
            this.saxParser = saxParser;
        }

        abstract void reset();

        public int getGeneration() {
            return this.poolGeneration;
        }

        public SAXParser getSAXParser() {
            return this.saxParser;
        }
    }

    private static class PoolDOMBuilder {
        private final int poolGeneration;
        private final DocumentBuilder documentBuilder;

        PoolDOMBuilder(int poolGeneration, DocumentBuilder documentBuilder) {
            this.poolGeneration = poolGeneration;
            this.documentBuilder = documentBuilder;
        }

        public int getPoolGeneration() {
            return this.poolGeneration;
        }

        public DocumentBuilder getDocumentBuilder() {
            return this.documentBuilder;
        }

        public void reset() {
            this.documentBuilder.reset();
            this.documentBuilder.setEntityResolver(IGNORING_SAX_ENTITY_RESOLVER);
            this.documentBuilder.setErrorHandler(null);
        }
    }
}

