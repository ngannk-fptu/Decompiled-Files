/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.parser.NamedParser;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserFactory;
import org.apache.abdera.writer.NamedWriter;
import org.apache.abdera.writer.StreamWriter;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.abdera.xpath.XPath;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Configuration
extends Cloneable,
Serializable {
    public String getConfigurationOption(String var1);

    public String getConfigurationOption(String var1, String var2);

    public Factory newFactoryInstance(Abdera var1);

    public Parser newParserInstance(Abdera var1);

    public XPath newXPathInstance(Abdera var1);

    public ParserFactory newParserFactoryInstance(Abdera var1);

    public WriterFactory newWriterFactoryInstance(Abdera var1);

    public Writer newWriterInstance(Abdera var1);

    public StreamWriter newStreamWriterInstance(Abdera var1);

    public Map<String, NamedParser> getNamedParsers();

    public Map<String, NamedWriter> getNamedWriters();

    public Map<String, Class<? extends StreamWriter>> getStreamWriters();

    public List<ExtensionFactory> getExtensionFactories();

    public Object clone();

    public Configuration addNamedParser(NamedParser var1);

    public Configuration addNamedWriter(NamedWriter var1);

    public Configuration addExtensionFactory(ExtensionFactory var1);

    public Configuration addStreamWriter(Class<? extends StreamWriter> var1);
}

