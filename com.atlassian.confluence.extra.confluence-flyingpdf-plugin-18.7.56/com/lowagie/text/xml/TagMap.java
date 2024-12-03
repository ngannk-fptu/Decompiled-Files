/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.xml;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.xml.XmlPeer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

public class TagMap
extends HashMap<String, XmlPeer> {
    private static final long serialVersionUID = -6809383366554350820L;

    public TagMap(String tagfile) {
        try {
            this.init(TagMap.class.getClassLoader().getResourceAsStream(tagfile));
        }
        catch (Exception e) {
            try {
                this.init(new FileInputStream(tagfile));
            }
            catch (FileNotFoundException fnfe) {
                throw new ExceptionConverter(fnfe);
            }
        }
    }

    public TagMap(InputStream in) {
        this.init(in);
    }

    protected void init(InputStream in) {
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(new InputSource(in), (DefaultHandler)new AttributeHandler((Map<String, XmlPeer>)this));
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    class AttributeHandler
    extends DefaultHandler {
        public static final String TAG = "tag";
        public static final String ATTRIBUTE = "attribute";
        public static final String NAME = "name";
        public static final String ALIAS = "alias";
        public static final String VALUE = "value";
        public static final String CONTENT = "content";
        private Map<String, XmlPeer> tagMap;
        private XmlPeer currentPeer;

        @Deprecated
        public AttributeHandler(HashMap tagMap) {
            this.tagMap = tagMap;
        }

        public AttributeHandler(Map<String, XmlPeer> tagMap) {
            this.tagMap = tagMap;
        }

        @Override
        public void startElement(String uri, String lname, String tag, Attributes attrs) {
            String name = attrs.getValue(NAME);
            String alias = attrs.getValue(ALIAS);
            String value = attrs.getValue(VALUE);
            if (name != null) {
                if (TAG.equals(tag)) {
                    this.currentPeer = new XmlPeer(name, alias);
                } else if (ATTRIBUTE.equals(tag)) {
                    if (alias != null) {
                        this.currentPeer.addAlias(name, alias);
                    }
                    if (value != null) {
                        this.currentPeer.addValue(name, value);
                    }
                }
            }
            if ((value = attrs.getValue(CONTENT)) != null) {
                this.currentPeer.setContent(value);
            }
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) {
        }

        @Override
        public void characters(char[] ch, int start, int length) {
        }

        @Override
        public void endElement(String uri, String lname, String tag) {
            if (TAG.equals(tag)) {
                this.tagMap.put(this.currentPeer.getAlias(), this.currentPeer);
            }
        }
    }
}

