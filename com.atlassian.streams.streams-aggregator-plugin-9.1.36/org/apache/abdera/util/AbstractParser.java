/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import javax.xml.stream.XMLStreamReader;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractParser
implements Parser {
    protected Abdera abdera;
    protected ParserOptions options;

    protected AbstractParser() {
        this(new Abdera());
    }

    protected AbstractParser(Abdera abdera) {
        this.abdera = abdera;
    }

    public Abdera getAbdera() {
        return this.abdera;
    }

    public void setAbdera(Abdera abdera) {
        this.abdera = abdera;
    }

    public Factory getFactory() {
        return this.getAbdera().getFactory();
    }

    @Override
    public <T extends Element> Document<T> parse(InputStream in) throws ParseException {
        return this.parse(in, null, this.getDefaultParserOptions());
    }

    @Override
    public <T extends Element> Document<T> parse(XMLStreamReader reader) throws ParseException {
        return this.parse(reader, null, this.getDefaultParserOptions());
    }

    @Override
    public <T extends Element> Document<T> parse(InputStream in, String base) throws ParseException {
        return this.parse(in, base, this.getDefaultParserOptions());
    }

    @Override
    public <T extends Element> Document<T> parse(InputStream in, ParserOptions options) throws ParseException {
        return this.parse(in, null, options);
    }

    @Override
    public <T extends Element> Document<T> parse(InputStream in, String base, ParserOptions options) throws ParseException {
        return this.parse(new InputStreamReader(in), base, options);
    }

    @Override
    public <T extends Element> Document<T> parse(Reader in) throws ParseException {
        return this.parse(in, null, this.getDefaultParserOptions());
    }

    @Override
    public <T extends Element> Document<T> parse(Reader in, String base) throws ParseException {
        return this.parse(in, base, this.getDefaultParserOptions());
    }

    @Override
    public <T extends Element> Document<T> parse(Reader in, ParserOptions options) throws ParseException {
        return this.parse(in, null, options);
    }

    @Override
    public <T extends Element> Document<T> parse(ReadableByteChannel buf, ParserOptions options) throws ParseException {
        return this.parse(buf, null, options);
    }

    @Override
    public <T extends Element> Document<T> parse(ReadableByteChannel buf, String base, ParserOptions options) throws ParseException {
        String charset = options.getCharset();
        return this.parse(Channels.newReader(buf, charset != null ? charset : "utf-8"), base, options);
    }

    @Override
    public <T extends Element> Document<T> parse(ReadableByteChannel buf, String base) throws ParseException {
        return this.parse(buf, base, this.getDefaultParserOptions());
    }

    @Override
    public <T extends Element> Document<T> parse(ReadableByteChannel buf) throws ParseException {
        return this.parse(buf, null, this.getDefaultParserOptions());
    }

    @Override
    public synchronized ParserOptions getDefaultParserOptions() {
        if (this.options == null) {
            this.options = this.initDefaultParserOptions();
        }
        try {
            return (ParserOptions)this.options.clone();
        }
        catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
    }

    protected abstract ParserOptions initDefaultParserOptions();

    @Override
    public synchronized Parser setDefaultParserOptions(ParserOptions options) {
        try {
            this.options = options != null ? (ParserOptions)options.clone() : this.initDefaultParserOptions();
            return this;
        }
        catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
    }
}

