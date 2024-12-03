/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Document
 *  org.jdom2.JDOMException
 *  org.jdom2.input.DOMBuilder
 *  org.jdom2.input.JDOMParseException
 *  org.jdom2.input.sax.XMLReaderJDOMFactory
 *  org.jdom2.input.sax.XMLReaders
 */
package com.rometools.rome.io;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.impl.ConfigurableClassLoader;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.ParsingFeedException;
import com.rometools.rome.io.SAXBuilder;
import com.rometools.rome.io.WireFeedParser;
import com.rometools.rome.io.impl.FeedParsers;
import com.rometools.rome.io.impl.XmlFixerReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.DOMBuilder;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaders;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class WireFeedInput {
    private static final InputSource EMPTY_INPUTSOURCE = new InputSource(new ByteArrayInputStream(new byte[0]));
    private static final EntityResolver RESOLVER = new EmptyEntityResolver();
    private static Map<ClassLoader, FeedParsers> clMap = new WeakHashMap<ClassLoader, FeedParsers>();
    private final boolean validate;
    private final Locale locale;
    private boolean xmlHealerOn = true;
    private boolean allowDoctypes = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static FeedParsers getFeedParsers() {
        Class<WireFeedInput> clazz = WireFeedInput.class;
        synchronized (WireFeedInput.class) {
            ClassLoader classLoader = ConfigurableClassLoader.INSTANCE.getClassLoader();
            FeedParsers parsers = clMap.get(classLoader);
            if (parsers == null) {
                parsers = new FeedParsers();
                clMap.put(classLoader, parsers);
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return parsers;
        }
    }

    public static List<String> getSupportedFeedTypes() {
        return WireFeedInput.getFeedParsers().getSupportedFeedTypes();
    }

    public WireFeedInput() {
        this(false, Locale.US);
    }

    public WireFeedInput(boolean validate, Locale locale) {
        this.validate = false;
        this.locale = locale;
    }

    public void setXmlHealerOn(boolean heals) {
        this.xmlHealerOn = heals;
    }

    public boolean getXmlHealerOn() {
        return this.xmlHealerOn;
    }

    public boolean isAllowDoctypes() {
        return this.allowDoctypes;
    }

    public void setAllowDoctypes(boolean allowDoctypes) {
        this.allowDoctypes = allowDoctypes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public WireFeed build(File file) throws FileNotFoundException, IOException, IllegalArgumentException, FeedException {
        WireFeed feed;
        Reader reader = new FileReader(file);
        try {
            if (this.xmlHealerOn) {
                reader = new XmlFixerReader(reader);
            }
            feed = this.build(reader);
        }
        finally {
            reader.close();
        }
        return feed;
    }

    public WireFeed build(Reader reader) throws IllegalArgumentException, FeedException {
        SAXBuilder saxBuilder = this.createSAXBuilder();
        try {
            if (this.xmlHealerOn) {
                reader = new XmlFixerReader(reader);
            }
            Document document = saxBuilder.build(reader);
            return this.build(document);
        }
        catch (JDOMParseException ex) {
            throw new ParsingFeedException("Invalid XML: " + ex.getMessage(), ex);
        }
        catch (IllegalArgumentException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new ParsingFeedException("Invalid XML", ex);
        }
    }

    public WireFeed build(InputSource is) throws IllegalArgumentException, FeedException {
        SAXBuilder saxBuilder = this.createSAXBuilder();
        try {
            Document document = saxBuilder.build(is);
            return this.build(document);
        }
        catch (JDOMParseException ex) {
            throw new ParsingFeedException("Invalid XML: " + ex.getMessage(), ex);
        }
        catch (IllegalArgumentException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new ParsingFeedException("Invalid XML", ex);
        }
    }

    public WireFeed build(org.w3c.dom.Document document) throws IllegalArgumentException, FeedException {
        DOMBuilder domBuilder = new DOMBuilder();
        try {
            Document jdomDoc = domBuilder.build(document);
            return this.build(jdomDoc);
        }
        catch (IllegalArgumentException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new ParsingFeedException("Invalid XML", ex);
        }
    }

    public WireFeed build(Document document) throws IllegalArgumentException, FeedException {
        WireFeedParser parser = WireFeedInput.getFeedParsers().getParserFor(document);
        if (parser == null) {
            throw new IllegalArgumentException("Invalid document");
        }
        return parser.parse(document, this.validate, this.locale);
    }

    protected SAXBuilder createSAXBuilder() {
        SAXBuilder saxBuilder = this.validate ? new SAXBuilder((XMLReaderJDOMFactory)XMLReaders.DTDVALIDATING) : new SAXBuilder((XMLReaderJDOMFactory)XMLReaders.NONVALIDATING);
        saxBuilder.setEntityResolver(RESOLVER);
        try {
            XMLReader parser = saxBuilder.createParser();
            this.setFeature(saxBuilder, parser, "http://xml.org/sax/features/external-general-entities", false);
            this.setFeature(saxBuilder, parser, "http://xml.org/sax/features/external-parameter-entities", false);
            this.setFeature(saxBuilder, parser, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            if (!this.allowDoctypes) {
                this.setFeature(saxBuilder, parser, "http://apache.org/xml/features/disallow-doctype-decl", true);
            }
        }
        catch (JDOMException e) {
            throw new IllegalStateException("JDOM could not create a SAX parser", e);
        }
        saxBuilder.setExpandEntities(false);
        return saxBuilder;
    }

    private void setFeature(SAXBuilder saxBuilder, XMLReader parser, String feature, boolean value) {
        if (this.isFeatureSupported(parser, feature, value)) {
            saxBuilder.setFeature(feature, value);
        }
    }

    private boolean isFeatureSupported(XMLReader parser, String feature, boolean value) {
        try {
            parser.setFeature(feature, value);
            return true;
        }
        catch (SAXNotRecognizedException e) {
            return false;
        }
        catch (SAXNotSupportedException e) {
            return false;
        }
    }

    private static class EmptyEntityResolver
    implements EntityResolver {
        private EmptyEntityResolver() {
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) {
            if (systemId != null && systemId.endsWith(".dtd")) {
                return EMPTY_INPUTSOURCE;
            }
            return null;
        }
    }
}

