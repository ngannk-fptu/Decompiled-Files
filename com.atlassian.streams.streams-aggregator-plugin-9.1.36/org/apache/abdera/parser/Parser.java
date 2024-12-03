/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser;

import java.io.InputStream;
import java.io.Reader;
import java.nio.channels.ReadableByteChannel;
import javax.xml.stream.XMLStreamReader;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.ParserOptions;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Parser {
    public <T extends Element> Document<T> parse(InputStream var1) throws ParseException;

    public <T extends Element> Document<T> parse(XMLStreamReader var1) throws ParseException;

    public <T extends Element> Document<T> parse(InputStream var1, String var2) throws ParseException;

    public <T extends Element> Document<T> parse(InputStream var1, ParserOptions var2) throws ParseException;

    public <T extends Element> Document<T> parse(InputStream var1, String var2, ParserOptions var3) throws ParseException;

    public <T extends Element> Document<T> parse(Reader var1) throws ParseException;

    public <T extends Element> Document<T> parse(Reader var1, String var2) throws ParseException;

    public <T extends Element> Document<T> parse(Reader var1, ParserOptions var2) throws ParseException;

    public <T extends Element> Document<T> parse(Reader var1, String var2, ParserOptions var3) throws ParseException;

    public <T extends Element> Document<T> parse(ReadableByteChannel var1) throws ParseException;

    public <T extends Element> Document<T> parse(ReadableByteChannel var1, String var2) throws ParseException;

    public <T extends Element> Document<T> parse(ReadableByteChannel var1, String var2, ParserOptions var3) throws ParseException;

    public <T extends Element> Document<T> parse(XMLStreamReader var1, String var2, ParserOptions var3) throws ParseException;

    public <T extends Element> Document<T> parse(ReadableByteChannel var1, ParserOptions var2) throws ParseException;

    public ParserOptions getDefaultParserOptions();

    public Parser setDefaultParserOptions(ParserOptions var1);
}

