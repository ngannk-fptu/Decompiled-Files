/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserFactory;
import org.apache.abdera.util.AbderaConfiguration;
import org.apache.abdera.util.Configuration;
import org.apache.abdera.writer.StreamWriter;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.abdera.xpath.XPath;

public class Abdera {
    private static Abdera instance;
    private final Configuration config;
    private Factory factory;
    private Parser parser;
    private XPath xpath;
    private ParserFactory parserFactory;
    private WriterFactory writerFactory;
    private Writer writer;

    public static synchronized Abdera getInstance() {
        if (instance == null) {
            instance = new Abdera();
        }
        return instance;
    }

    public Abdera() {
        this(AbderaConfiguration.getDefault());
    }

    public Abdera(Configuration config) {
        this.config = config;
        IRI.preinit();
    }

    public Feed newFeed() {
        return this.getFactory().newFeed();
    }

    public Entry newEntry() {
        return this.getFactory().newEntry();
    }

    public Service newService() {
        return this.getFactory().newService();
    }

    public Categories newCategories() {
        return this.getFactory().newCategories();
    }

    public Configuration getConfiguration() {
        return this.config;
    }

    public synchronized Factory getFactory() {
        if (this.factory == null) {
            this.factory = this.newFactory();
        }
        return this.factory;
    }

    public synchronized Parser getParser() {
        if (this.parser == null) {
            this.parser = this.newParser();
        }
        return this.parser;
    }

    public synchronized XPath getXPath() {
        if (this.xpath == null) {
            this.xpath = this.newXPath();
        }
        return this.xpath;
    }

    public synchronized ParserFactory getParserFactory() {
        if (this.parserFactory == null) {
            this.parserFactory = this.newParserFactory();
        }
        return this.parserFactory;
    }

    public synchronized WriterFactory getWriterFactory() {
        if (this.writerFactory == null) {
            this.writerFactory = this.newWriterFactory();
        }
        return this.writerFactory;
    }

    public synchronized Writer getWriter() {
        if (this.writer == null) {
            this.writer = this.newWriter();
        }
        return this.writer;
    }

    private Factory newFactory() {
        return this.config.newFactoryInstance(this);
    }

    private Parser newParser() {
        return this.config.newParserInstance(this);
    }

    private XPath newXPath() {
        return this.config.newXPathInstance(this);
    }

    private ParserFactory newParserFactory() {
        return this.config.newParserFactoryInstance(this);
    }

    private WriterFactory newWriterFactory() {
        return this.config.newWriterFactoryInstance(this);
    }

    private Writer newWriter() {
        return this.config.newWriterInstance(this);
    }

    public StreamWriter newStreamWriter() {
        return this.config.newStreamWriterInstance(this);
    }

    public static Factory getNewFactory() {
        return new Abdera().newFactory();
    }

    public static Parser getNewParser() {
        return new Abdera().newParser();
    }

    public static XPath getNewXPath() {
        return new Abdera().newXPath();
    }

    public static ParserFactory getNewParserFactory() {
        return new Abdera().newParserFactory();
    }

    public static WriterFactory getNewWriterFactory() {
        return new Abdera().newWriterFactory();
    }

    public static Writer getNewWriter() {
        return new Abdera().newWriter();
    }

    public static StreamWriter getNewStreamWriter() {
        return new Abdera().newStreamWriter();
    }
}

