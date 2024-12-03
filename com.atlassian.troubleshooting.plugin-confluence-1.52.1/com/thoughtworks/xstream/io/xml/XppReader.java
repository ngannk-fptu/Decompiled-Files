/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.AbstractPullReader;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import java.io.IOException;
import java.io.Reader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XppReader
extends AbstractPullReader {
    private final XmlPullParser parser;
    private final Reader reader;

    public XppReader(Reader reader, XmlPullParser parser) {
        this(reader, parser, new XmlFriendlyNameCoder());
    }

    public XppReader(Reader reader, XmlPullParser parser, NameCoder nameCoder) {
        super(nameCoder);
        this.parser = parser;
        this.reader = reader;
        try {
            parser.setInput(this.reader);
        }
        catch (XmlPullParserException e) {
            throw new StreamException(e);
        }
        this.moveDown();
    }

    public XppReader(Reader reader) {
        this(reader, new XmlFriendlyReplacer());
    }

    public XppReader(Reader reader, XmlFriendlyReplacer replacer) {
        super(replacer);
        try {
            this.parser = this.createParser();
            this.reader = reader;
            this.parser.setInput(this.reader);
            this.moveDown();
        }
        catch (XmlPullParserException e) {
            throw new StreamException(e);
        }
    }

    protected XmlPullParser createParser() {
        ReflectiveOperationException exception = null;
        try {
            return (XmlPullParser)Class.forName("org.xmlpull.mxp1.MXParser", true, XmlPullParser.class.getClassLoader()).newInstance();
        }
        catch (InstantiationException e) {
            exception = e;
        }
        catch (IllegalAccessException e) {
            exception = e;
        }
        catch (ClassNotFoundException e) {
            exception = e;
        }
        throw new StreamException("Cannot create Xpp3 parser instance.", exception);
    }

    protected int pullNextEvent() {
        try {
            switch (this.parser.next()) {
                case 0: 
                case 2: {
                    return 1;
                }
                case 1: 
                case 3: {
                    return 2;
                }
                case 4: {
                    return 3;
                }
                case 9: {
                    return 4;
                }
            }
            return 0;
        }
        catch (XmlPullParserException e) {
            throw new StreamException(e);
        }
        catch (IOException e) {
            throw new StreamException(e);
        }
    }

    protected String pullElementName() {
        return this.parser.getName();
    }

    protected String pullText() {
        return this.parser.getText();
    }

    public String getAttribute(String name) {
        return this.parser.getAttributeValue(null, this.encodeAttribute(name));
    }

    public String getAttribute(int index) {
        return this.parser.getAttributeValue(index);
    }

    public int getAttributeCount() {
        return this.parser.getAttributeCount();
    }

    public String getAttributeName(int index) {
        return this.decodeAttribute(this.parser.getAttributeName(index));
    }

    public void appendErrors(ErrorWriter errorWriter) {
        errorWriter.add("line number", String.valueOf(this.parser.getLineNumber()));
    }

    public void close() {
        try {
            this.reader.close();
        }
        catch (IOException e) {
            throw new StreamException(e);
        }
    }
}

