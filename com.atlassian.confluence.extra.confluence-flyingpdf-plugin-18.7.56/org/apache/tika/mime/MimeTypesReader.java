/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.mime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import org.apache.tika.exception.TikaException;
import org.apache.tika.mime.AndClause;
import org.apache.tika.mime.Clause;
import org.apache.tika.mime.Magic;
import org.apache.tika.mime.MagicMatch;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.mime.MimeTypesReaderMetKeys;
import org.apache.tika.mime.MinShouldMatchClause;
import org.apache.tika.mime.OrClause;
import org.apache.tika.utils.XMLReaderUtils;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MimeTypesReader
extends DefaultHandler
implements MimeTypesReaderMetKeys {
    private static int POOL_SIZE = 10;
    private static final ReentrantReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock();
    private static ArrayBlockingQueue<SAXParser> SAX_PARSERS = new ArrayBlockingQueue(POOL_SIZE);
    private static Logger LOG = Logger.getLogger(MimeTypesReader.class.getName());
    protected final MimeTypes types;
    protected MimeType type = null;
    protected int priority;
    protected StringBuilder characters = null;
    private ClauseRecord current = new ClauseRecord(null);

    protected MimeTypesReader(MimeTypes types) {
        this.types = types;
    }

    public void read(InputStream stream) throws IOException, MimeTypeException {
        SAXParser parser = null;
        try {
            parser = MimeTypesReader.acquireSAXParser();
            parser.parse(stream, (DefaultHandler)this);
        }
        catch (TikaException e) {
            throw new MimeTypeException("Unable to create an XML parser", e);
        }
        catch (SAXException e) {
            throw new MimeTypeException("Invalid type configuration", e);
        }
        finally {
            if (parser != null) {
                MimeTypesReader.releaseParser(parser);
            }
        }
    }

    public void read(Document document) throws MimeTypeException {
        try {
            Transformer transformer = XMLReaderUtils.getTransformer();
            transformer.transform(new DOMSource(document), new SAXResult(this));
        }
        catch (TransformerException | TikaException e) {
            throw new MimeTypeException("Failed to parse type registry", e);
        }
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        return new InputSource(new ByteArrayInputStream(new byte[0]));
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (this.type == null) {
            if ("mime-type".equals(qName)) {
                String name = attributes.getValue("type");
                String interpretedAttr = attributes.getValue("interpreted");
                boolean interpreted = "true".equals(interpretedAttr);
                try {
                    this.type = this.types.forName(name);
                    this.type.setInterpreted(interpreted);
                }
                catch (MimeTypeException e) {
                    this.handleMimeError(name, e, qName, attributes);
                }
            }
        } else if ("alias".equals(qName)) {
            String alias = attributes.getValue("type");
            this.types.addAlias(this.type, MediaType.parse(alias));
        } else if ("sub-class-of".equals(qName)) {
            String parent = attributes.getValue("type");
            this.types.setSuperType(this.type, MediaType.parse(parent));
        } else if ("acronym".equals(qName) || "_comment".equals(qName) || "tika:link".equals(qName) || "tika:uti".equals(qName)) {
            this.characters = new StringBuilder();
        } else if ("glob".equals(qName)) {
            String pattern = attributes.getValue("pattern");
            String isRegex = attributes.getValue("isregex");
            if (pattern != null) {
                try {
                    this.types.addPattern(this.type, pattern, Boolean.valueOf(isRegex));
                }
                catch (MimeTypeException e) {
                    this.handleGlobError(this.type, pattern, e, qName, attributes);
                }
            }
        } else if ("root-XML".equals(qName)) {
            String namespace = attributes.getValue("namespaceURI");
            String name = attributes.getValue("localName");
            this.type.addRootXML(namespace, name);
        } else if ("match".equals(qName)) {
            if (attributes.getValue("minShouldMatch") != null) {
                this.current = new ClauseRecord(new MinShouldMatchVal(Integer.parseInt(attributes.getValue("minShouldMatch"))));
            } else {
                String kind = attributes.getValue("type");
                String offset = attributes.getValue("offset");
                String value = attributes.getValue("value");
                String mask = attributes.getValue("mask");
                if (kind == null) {
                    kind = "string";
                }
                this.current = new ClauseRecord(new MagicMatch(this.type.getType(), kind, offset, value, mask));
            }
        } else if ("magic".equals(qName)) {
            String value = attributes.getValue("priority");
            this.priority = value != null && value.length() > 0 ? Integer.parseInt(value) : 50;
            this.current = new ClauseRecord(null);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (this.type != null) {
            if ("mime-type".equals(qName)) {
                this.type = null;
            } else if ("_comment".equals(qName)) {
                this.type.setDescription(this.characters.toString().trim());
                this.characters = null;
            } else if ("acronym".equals(qName)) {
                this.type.setAcronym(this.characters.toString().trim());
                this.characters = null;
            } else if ("tika:uti".equals(qName)) {
                this.type.setUniformTypeIdentifier(this.characters.toString().trim());
                this.characters = null;
            } else if ("tika:link".equals(qName)) {
                try {
                    this.type.addLink(new URI(this.characters.toString().trim()));
                }
                catch (URISyntaxException e) {
                    throw new IllegalArgumentException("unable to parse link: " + this.characters, e);
                }
                this.characters = null;
            } else if ("match".equals(qName)) {
                this.current.stop();
            } else if ("magic".equals(qName)) {
                for (Clause clause : this.current.getClauses()) {
                    this.type.addMagic(new Magic(this.type, this.priority, clause));
                }
                this.current = null;
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (this.characters != null) {
            this.characters.append(ch, start, length);
        }
    }

    protected void handleMimeError(String input, MimeTypeException ex, String qName, Attributes attributes) throws SAXException {
        throw new SAXException(ex);
    }

    protected void handleGlobError(MimeType type, String pattern, MimeTypeException ex, String qName, Attributes attributes) throws SAXException {
        throw new SAXException(ex);
    }

    private static SAXParser acquireSAXParser() throws TikaException {
        SAXParser parser;
        do {
            parser = null;
            try {
                READ_WRITE_LOCK.readLock().lock();
                parser = SAX_PARSERS.poll(10L, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                throw new TikaException("interrupted while waiting for SAXParser", e);
            }
            finally {
                READ_WRITE_LOCK.readLock().unlock();
            }
        } while (parser == null);
        return parser;
    }

    private static void releaseParser(SAXParser parser) {
        try {
            parser.reset();
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            // empty catch block
        }
        try {
            READ_WRITE_LOCK.readLock().lock();
            SAX_PARSERS.offer(parser);
        }
        finally {
            READ_WRITE_LOCK.readLock().unlock();
        }
    }

    public static void setPoolSize(int poolSize) throws TikaException {
        try {
            READ_WRITE_LOCK.writeLock().lock();
            SAX_PARSERS = new ArrayBlockingQueue(poolSize);
            for (int i = 0; i < poolSize; ++i) {
                SAX_PARSERS.offer(MimeTypesReader.newSAXParser());
            }
            POOL_SIZE = poolSize;
        }
        finally {
            READ_WRITE_LOCK.writeLock().unlock();
        }
    }

    private static SAXParser newSAXParser() throws TikaException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(false);
        try {
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        }
        catch (ParserConfigurationException | SAXException e) {
            LOG.log(Level.WARNING, "can't set secure parsing feature on SAXParserFactory: " + factory.getClass() + ". User assumes responsibility for consequences.");
        }
        try {
            return factory.newSAXParser();
        }
        catch (ParserConfigurationException | SAXException e) {
            throw new TikaException("can't create saxparser", e);
        }
    }

    static {
        try {
            MimeTypesReader.setPoolSize(POOL_SIZE);
        }
        catch (TikaException e) {
            throw new RuntimeException("problem initializing SAXParser pool", e);
        }
    }

    private static class MinShouldMatchVal
    implements Clause {
        private final int val;

        MinShouldMatchVal(int val) {
            this.val = val;
        }

        int getVal() {
            return this.val;
        }

        @Override
        public boolean eval(byte[] data) {
            throw new IllegalStateException("This should never be used on this placeholder class");
        }

        @Override
        public int size() {
            return 0;
        }
    }

    private class ClauseRecord {
        private ClauseRecord parent;
        private Clause clause;
        private List<Clause> subclauses = null;

        public ClauseRecord(Clause clause) {
            this.parent = MimeTypesReader.this.current;
            this.clause = clause;
        }

        public void stop() {
            if (this.clause instanceof MinShouldMatchVal) {
                this.clause = new MinShouldMatchClause(((MinShouldMatchVal)this.clause).getVal(), this.subclauses);
            } else if (this.subclauses != null) {
                Clause subclause = this.subclauses.size() == 1 ? this.subclauses.get(0) : new OrClause(this.subclauses);
                this.clause = new AndClause(this.clause, subclause);
            }
            if (this.parent.subclauses == null) {
                this.parent.subclauses = Collections.singletonList(this.clause);
            } else {
                if (this.parent.subclauses.size() == 1) {
                    this.parent.subclauses = new ArrayList<Clause>(this.parent.subclauses);
                }
                this.parent.subclauses.add(this.clause);
            }
            MimeTypesReader.this.current = ((MimeTypesReader)MimeTypesReader.this).current.parent;
        }

        public List<Clause> getClauses() {
            return this.subclauses;
        }
    }
}

