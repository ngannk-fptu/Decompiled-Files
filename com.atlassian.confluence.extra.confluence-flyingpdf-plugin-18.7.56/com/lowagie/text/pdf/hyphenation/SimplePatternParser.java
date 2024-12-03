/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.hyphenation;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.hyphenation.Hyphen;
import com.lowagie.text.pdf.hyphenation.PatternConsumer;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.lowagie.text.xml.simpleparser.SimpleXMLParser;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class SimplePatternParser
implements SimpleXMLDocHandler,
PatternConsumer {
    int currElement;
    PatternConsumer consumer;
    StringBuffer token = new StringBuffer();
    List<Object> exception;
    char hyphenChar = (char)45;
    SimpleXMLParser parser;
    static final int ELEM_CLASSES = 1;
    static final int ELEM_EXCEPTIONS = 2;
    static final int ELEM_PATTERNS = 3;
    static final int ELEM_HYPHEN = 4;

    public void parse(InputStream stream, PatternConsumer consumer) {
        this.consumer = consumer;
        try {
            SimpleXMLParser.parse((SimpleXMLDocHandler)this, stream);
        }
        catch (IOException e) {
            throw new ExceptionConverter(e);
        }
        finally {
            try {
                stream.close();
            }
            catch (Exception exception) {}
        }
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

    protected List<Object> normalizeException(List<Object> ex) {
        ArrayList<Object> res = new ArrayList<Object>();
        for (Object item : ex) {
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

    protected String getExceptionWord(List<Object> ex) {
        StringBuilder res = new StringBuilder();
        for (Object item : ex) {
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
    public void endDocument() {
    }

    @Override
    public void endElement(String tag) {
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
                    this.consumer.addException(this.getExceptionWord(this.exception), (ArrayList)((ArrayList)this.exception).clone());
                    break;
                }
                case 3: {
                    this.consumer.addPattern(SimplePatternParser.getPattern(word), SimplePatternParser.getInterletterValues(word));
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
    public void startDocument() {
    }

    @Override
    @Deprecated
    public void startElement(String tag, HashMap h) {
        this.startElement(tag, (Map<String, String>)h);
    }

    @Override
    public void startElement(String tag, Map<String, String> h) {
        switch (tag) {
            case "hyphen-char": {
                String hh = h.get("value");
                if (hh == null || hh.length() != 1) break;
                this.hyphenChar = hh.charAt(0);
                break;
            }
            case "classes": {
                this.currElement = 1;
                break;
            }
            case "patterns": {
                this.currElement = 3;
                break;
            }
            case "exceptions": {
                this.currElement = 2;
                this.exception = new ArrayList<Object>();
                break;
            }
            case "hyphen": {
                if (this.token.length() > 0) {
                    this.exception.add(this.token.toString());
                }
                this.exception.add(new Hyphen(h.get("pre"), h.get("no"), h.get("post")));
                this.currElement = 4;
            }
        }
        this.token.setLength(0);
    }

    @Override
    public void text(String str) {
        StringTokenizer tk = new StringTokenizer(str);
        while (tk.hasMoreTokens()) {
            String word = tk.nextToken();
            switch (this.currElement) {
                case 1: {
                    this.consumer.addClass(word);
                    break;
                }
                case 2: {
                    this.exception.add(word);
                    this.exception = this.normalizeException(this.exception);
                    this.consumer.addException(this.getExceptionWord(this.exception), (ArrayList)((ArrayList)this.exception).clone());
                    this.exception.clear();
                    break;
                }
                case 3: {
                    this.consumer.addPattern(SimplePatternParser.getPattern(word), SimplePatternParser.getInterletterValues(word));
                }
            }
        }
    }

    @Override
    public void addClass(String c) {
        System.out.println("class: " + c);
    }

    @Override
    public void addException(String w, ArrayList e) {
        System.out.println("exception: " + w + " : " + e.toString());
    }

    @Override
    public void addPattern(String p, String v) {
        System.out.println("pattern: " + p + " : " + v);
    }

    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                SimplePatternParser pp = new SimplePatternParser();
                pp.parse(new FileInputStream(args[0]), pp);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

