/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Document
 *  org.jdom.JDOMException
 *  org.jdom.input.DOMBuilder
 *  org.jdom.input.JDOMParseException
 */
package com.sun.syndication.io;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.ParsingFeedException;
import com.sun.syndication.io.SAXBuilder;
import com.sun.syndication.io.WireFeedParser;
import com.sun.syndication.io.impl.FeedParsers;
import com.sun.syndication.io.impl.XmlFixerReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.jdom.input.JDOMParseException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class WireFeedInput {
    private static Map clMap = new WeakHashMap();
    private static final InputSource EMPTY_INPUTSOURCE = new InputSource(new ByteArrayInputStream(new byte[0]));
    private static final EntityResolver RESOLVER = new EmptyEntityResolver();
    private boolean _validate = false;
    private boolean _xmlHealerOn = true;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static FeedParsers getFeedParsers() {
        Class clazz = WireFeedInput.class;
        synchronized (clazz) {
            FeedParsers parsers = (FeedParsers)clMap.get(Thread.currentThread().getContextClassLoader());
            if (parsers == null) {
                parsers = new FeedParsers();
                clMap.put(Thread.currentThread().getContextClassLoader(), parsers);
            }
            return parsers;
        }
    }

    public static List getSupportedFeedTypes() {
        return WireFeedInput.getFeedParsers().getSupportedFeedTypes();
    }

    public WireFeedInput() {
        this(false);
    }

    public WireFeedInput(boolean validate) {
    }

    public void setXmlHealerOn(boolean heals) {
        this._xmlHealerOn = heals;
    }

    public boolean getXmlHealerOn() {
        return this._xmlHealerOn;
    }

    public WireFeed build(File file) throws FileNotFoundException, IOException, IllegalArgumentException, FeedException {
        Reader reader = new FileReader(file);
        if (this._xmlHealerOn) {
            reader = new XmlFixerReader(reader);
        }
        WireFeed feed = this.build(reader);
        reader.close();
        return feed;
    }

    public WireFeed build(Reader reader) throws IllegalArgumentException, FeedException {
        SAXBuilder saxBuilder = this.createSAXBuilder();
        try {
            if (this._xmlHealerOn) {
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
        return parser.parse(document, this._validate);
    }

    protected SAXBuilder createSAXBuilder() {
        SAXBuilder saxBuilder = new SAXBuilder(this._validate);
        saxBuilder.setEntityResolver(RESOLVER);
        try {
            XMLReader parser = saxBuilder.createParser();
            try {
                parser.setFeature("http://xml.org/sax/features/external-general-entities", false);
                saxBuilder.setFeature("http://xml.org/sax/features/external-general-entities", false);
            }
            catch (SAXNotRecognizedException e) {
            }
            catch (SAXNotSupportedException e) {
                // empty catch block
            }
            try {
                parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                saxBuilder.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            }
            catch (SAXNotRecognizedException e) {
            }
            catch (SAXNotSupportedException e) {
                // empty catch block
            }
            try {
                parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            }
            catch (SAXNotRecognizedException e) {
            }
            catch (SAXNotSupportedException sAXNotSupportedException) {}
        }
        catch (JDOMException e) {
            throw new IllegalStateException("JDOM could not create a SAX parser");
        }
        saxBuilder.setExpandEntities(false);
        return saxBuilder;
    }

    private static class EmptyEntityResolver
    implements EntityResolver {
        private EmptyEntityResolver() {
        }

        public InputSource resolveEntity(String publicId, String systemId) {
            if (systemId != null && systemId.endsWith(".dtd")) {
                return EMPTY_INPUTSOURCE;
            }
            return null;
        }
    }
}

