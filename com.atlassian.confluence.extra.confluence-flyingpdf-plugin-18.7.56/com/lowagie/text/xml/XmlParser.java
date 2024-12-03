/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.xml;

import com.lowagie.text.DocListener;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.xml.SAXiTextHandler;
import com.lowagie.text.xml.SAXmyHandler;
import com.lowagie.text.xml.TagMap;
import com.lowagie.text.xml.XmlPeer;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlParser {
    protected SAXParser parser;

    public XmlParser() {
        try {
            this.parser = SAXParserFactory.newInstance().newSAXParser();
        }
        catch (ParserConfigurationException | SAXException pce) {
            throw new ExceptionConverter(pce);
        }
    }

    public void go(DocListener document, InputSource is) {
        try {
            this.parser.parse(is, new SAXiTextHandler(document));
        }
        catch (IOException | SAXException se) {
            throw new ExceptionConverter(se);
        }
    }

    public void go(DocListener document, InputSource is, String tagmap) {
        try {
            this.parser.parse(is, (DefaultHandler)new SAXmyHandler(document, new TagMap(tagmap)));
        }
        catch (IOException | SAXException se) {
            throw new ExceptionConverter(se);
        }
    }

    public void go(DocListener document, InputSource is, InputStream tagmap) {
        try {
            this.parser.parse(is, (DefaultHandler)new SAXmyHandler(document, new TagMap(tagmap)));
        }
        catch (IOException | SAXException se) {
            throw new ExceptionConverter(se);
        }
    }

    public void go(DocListener document, InputSource is, Map<String, XmlPeer> tagmap) {
        try {
            this.parser.parse(is, (DefaultHandler)new SAXmyHandler(document, tagmap));
        }
        catch (IOException | SAXException se) {
            throw new ExceptionConverter(se);
        }
    }

    public void go(DocListener document, String file) {
        try {
            this.parser.parse(file, new SAXiTextHandler(document));
        }
        catch (IOException | SAXException se) {
            throw new ExceptionConverter(se);
        }
    }

    public void go(DocListener document, String file, String tagmap) {
        try {
            this.parser.parse(file, (DefaultHandler)new SAXmyHandler(document, new TagMap(tagmap)));
        }
        catch (IOException | SAXException se) {
            throw new ExceptionConverter(se);
        }
    }

    public void go(DocListener document, String file, Map<String, XmlPeer> tagmap) {
        try {
            this.parser.parse(file, (DefaultHandler)new SAXmyHandler(document, tagmap));
        }
        catch (IOException | SAXException se) {
            throw new ExceptionConverter(se);
        }
    }

    public static void parse(DocListener document, InputSource is) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(document, is);
    }

    public static void parse(DocListener document, InputSource is, String tagmap) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(document, is, tagmap);
    }

    public static void parse(DocListener document, InputSource is, Map<String, XmlPeer> tagmap) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(document, is, tagmap);
    }

    public static void parse(DocListener document, String file) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(document, file);
    }

    public static void parse(DocListener document, String file, String tagmap) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(document, file, tagmap);
    }

    public static void parse(DocListener document, String file, Map<String, XmlPeer> tagmap) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(document, file, tagmap);
    }

    public static void parse(DocListener document, InputStream is) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(document, new InputSource(is));
    }

    public static void parse(DocListener document, InputStream is, String tagmap) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(document, new InputSource(is), tagmap);
    }

    public static void parse(DocListener document, InputStream is, Map<String, XmlPeer> tagmap) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(document, new InputSource(is), tagmap);
    }

    public static void parse(DocListener document, Reader is) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(document, new InputSource(is));
    }

    public static void parse(DocListener document, Reader is, String tagmap) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(document, new InputSource(is), tagmap);
    }

    public static void parse(DocListener document, Reader is, Map<String, XmlPeer> tagmap) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(document, new InputSource(is), tagmap);
    }
}

