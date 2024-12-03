/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.html;

import com.lowagie.text.DocListener;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.html.SAXmyHtmlHandler;
import com.lowagie.text.xml.XmlParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HtmlParser
extends XmlParser {
    public static void parse(DocListener document, InputSource is) {
        HtmlParser parser = new HtmlParser();
        parser.go(document, is);
    }

    public static void parse(DocListener document, String file) {
        HtmlParser parser = new HtmlParser();
        parser.go(document, file);
    }

    public static void parse(DocListener document, InputStream is) {
        HtmlParser parser = new HtmlParser();
        parser.go(document, new InputSource(is));
    }

    public static void parse(DocListener document, Reader is) {
        HtmlParser parser = new HtmlParser();
        parser.go(document, new InputSource(is));
    }

    @Override
    public void go(DocListener document, InputSource is) {
        try {
            this.parser.parse(is, (DefaultHandler)new SAXmyHtmlHandler(document));
        }
        catch (IOException | SAXException se) {
            throw new ExceptionConverter(se);
        }
    }

    @Override
    public void go(DocListener document, String file) {
        try {
            this.parser.parse(file, (DefaultHandler)new SAXmyHtmlHandler(document));
        }
        catch (IOException | SAXException se) {
            throw new ExceptionConverter(se);
        }
    }

    public void go(DocListener document, InputStream is) {
        try {
            this.parser.parse(new InputSource(is), (DefaultHandler)new SAXmyHtmlHandler(document));
        }
        catch (IOException | SAXException se) {
            throw new ExceptionConverter(se);
        }
    }

    public void go(DocListener document, Reader is) {
        try {
            this.parser.parse(new InputSource(is), (DefaultHandler)new SAXmyHtmlHandler(document));
        }
        catch (IOException | SAXException se) {
            throw new ExceptionConverter(se);
        }
    }
}

