/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.compound.hyphenation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.SAXParserFactory;
import org.apache.lucene.analysis.compound.hyphenation.Hyphen;
import org.apache.lucene.analysis.compound.hyphenation.PatternConsumer;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class PatternParser
extends DefaultHandler {
    XMLReader parser;
    int currElement;
    PatternConsumer consumer;
    StringBuilder token = new StringBuilder();
    ArrayList<Object> exception;
    char hyphenChar;
    String errMsg;
    static final int ELEM_CLASSES = 1;
    static final int ELEM_EXCEPTIONS = 2;
    static final int ELEM_PATTERNS = 3;
    static final int ELEM_HYPHEN = 4;

    public PatternParser() {
        this.parser = PatternParser.createParser();
        this.parser.setContentHandler(this);
        this.parser.setErrorHandler(this);
        this.parser.setEntityResolver(this);
        this.hyphenChar = (char)45;
    }

    public PatternParser(PatternConsumer consumer) {
        this();
        this.consumer = consumer;
    }

    public void setConsumer(PatternConsumer consumer) {
        this.consumer = consumer;
    }

    public void parse(String filename) throws IOException {
        this.parse(new InputSource(filename));
    }

    public void parse(File file) throws IOException {
        InputSource src = new InputSource(file.toURI().toASCIIString());
        this.parse(src);
    }

    public void parse(InputSource source) throws IOException {
        try {
            this.parser.parse(source);
        }
        catch (SAXException e) {
            throw new IOException(e);
        }
    }

    static XMLReader createParser() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            return factory.newSAXParser().getXMLReader();
        }
        catch (Exception e) {
            throw new RuntimeException("Couldn't create XMLReader: " + e.getMessage());
        }
    }

    protected String readToken(StringBuffer chars) {
        int countr;
        int i;
        boolean space = false;
        for (i = 0; i < chars.length() && Character.isWhitespace(chars.charAt(i)); ++i) {
            space = true;
        }
        if (space) {
            for (countr = i; countr < chars.length(); ++countr) {
                chars.setCharAt(countr - i, chars.charAt(countr));
            }
            chars.setLength(chars.length() - i);
            if (this.token.length() > 0) {
                String word = this.token.toString();
                this.token.setLength(0);
                return word;
            }
        }
        space = false;
        for (i = 0; i < chars.length(); ++i) {
            if (!Character.isWhitespace(chars.charAt(i))) continue;
            space = true;
            break;
        }
        this.token.append(chars.toString().substring(0, i));
        for (countr = i; countr < chars.length(); ++countr) {
            chars.setCharAt(countr - i, chars.charAt(countr));
        }
        chars.setLength(chars.length() - i);
        if (space) {
            String word = this.token.toString();
            this.token.setLength(0);
            return word;
        }
        this.token.append(chars);
        return null;
    }

    protected static String getPattern(String word) {
        StringBuilder pat = new StringBuilder();
        int len = word.length();
        for (int i = 0; i < len; ++i) {
            if (Character.isDigit(word.charAt(i))) continue;
            pat.append(word.charAt(i));
        }
        return pat.toString();
    }

    protected ArrayList<Object> normalizeException(ArrayList<?> ex) {
        ArrayList<Object> res = new ArrayList<Object>();
        for (int i = 0; i < ex.size(); ++i) {
            Object item = ex.get(i);
            if (item instanceof String) {
                String str = (String)item;
                StringBuilder buf = new StringBuilder();
                for (int j = 0; j < str.length(); ++j) {
                    char c = str.charAt(j);
                    if (c != this.hyphenChar) {
                        buf.append(c);
                        continue;
                    }
                    res.add(buf.toString());
                    buf.setLength(0);
                    char[] h = new char[]{this.hyphenChar};
                    res.add(new Hyphen(new String(h), null, null));
                }
                if (buf.length() <= 0) continue;
                res.add(buf.toString());
                continue;
            }
            res.add(item);
        }
        return res;
    }

    protected String getExceptionWord(ArrayList<?> ex) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < ex.size(); ++i) {
            Object item = ex.get(i);
            if (item instanceof String) {
                res.append((String)item);
                continue;
            }
            if (((Hyphen)item).noBreak == null) continue;
            res.append(((Hyphen)item).noBreak);
        }
        return res.toString();
    }

    protected static String getInterletterValues(String pat) {
        StringBuilder il = new StringBuilder();
        String word = pat + "a";
        int len = word.length();
        for (int i = 0; i < len; ++i) {
            char c = word.charAt(i);
            if (Character.isDigit(c)) {
                il.append(c);
                ++i;
                continue;
            }
            il.append('0');
        }
        return il.toString();
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        if (systemId != null && systemId.matches("(?i).*\\bhyphenation.dtd\\b.*") || "hyphenation-info".equals(publicId)) {
            return new InputSource(this.getClass().getResource("hyphenation.dtd").toExternalForm());
        }
        return null;
    }

    @Override
    public void startElement(String uri, String local, String raw, Attributes attrs) {
        if (local.equals("hyphen-char")) {
            String h = attrs.getValue("value");
            if (h != null && h.length() == 1) {
                this.hyphenChar = h.charAt(0);
            }
        } else if (local.equals("classes")) {
            this.currElement = 1;
        } else if (local.equals("patterns")) {
            this.currElement = 3;
        } else if (local.equals("exceptions")) {
            this.currElement = 2;
            this.exception = new ArrayList();
        } else if (local.equals("hyphen")) {
            if (this.token.length() > 0) {
                this.exception.add(this.token.toString());
            }
            this.exception.add(new Hyphen(attrs.getValue("pre"), attrs.getValue("no"), attrs.getValue("post")));
            this.currElement = 4;
        }
        this.token.setLength(0);
    }

    @Override
    public void endElement(String uri, String local, String raw) {
        if (this.token.length() > 0) {
            String word = this.token.toString();
            switch (this.currElement) {
                case 1: {
                    this.consumer.addClass(word);
                    break;
                }
                case 2: {
                    this.exception.add(word);
                    this.exception = this.normalizeException(this.exception);
                    this.consumer.addException(this.getExceptionWord(this.exception), (ArrayList)this.exception.clone());
                    break;
                }
                case 3: {
                    this.consumer.addPattern(PatternParser.getPattern(word), PatternParser.getInterletterValues(word));
                    break;
                }
            }
            if (this.currElement != 4) {
                this.token.setLength(0);
            }
        }
        this.currElement = this.currElement == 4 ? 2 : 0;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        StringBuffer chars = new StringBuffer(length);
        chars.append(ch, start, length);
        String word = this.readToken(chars);
        while (word != null) {
            switch (this.currElement) {
                case 1: {
                    this.consumer.addClass(word);
                    break;
                }
                case 2: {
                    this.exception.add(word);
                    this.exception = this.normalizeException(this.exception);
                    this.consumer.addException(this.getExceptionWord(this.exception), (ArrayList)this.exception.clone());
                    this.exception.clear();
                    break;
                }
                case 3: {
                    this.consumer.addPattern(PatternParser.getPattern(word), PatternParser.getInterletterValues(word));
                }
            }
            word = this.readToken(chars);
        }
    }

    private String getLocationString(SAXParseException ex) {
        StringBuilder str = new StringBuilder();
        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf(47);
            if (index != -1) {
                systemId = systemId.substring(index + 1);
            }
            str.append(systemId);
        }
        str.append(':');
        str.append(ex.getLineNumber());
        str.append(':');
        str.append(ex.getColumnNumber());
        return str.toString();
    }
}

