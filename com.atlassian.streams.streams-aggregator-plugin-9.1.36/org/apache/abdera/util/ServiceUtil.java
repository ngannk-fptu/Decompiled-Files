/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserFactory;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.Discover;
import org.apache.abdera.writer.StreamWriter;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.abdera.xpath.XPath;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ServiceUtil
implements Constants {
    ServiceUtil() {
    }

    public static XPath newXPathInstance(Abdera abdera) {
        return (XPath)Discover.locate("org.apache.abdera.xpath.XPath", abdera.getConfiguration().getConfigurationOption("org.apache.abdera.xpath.XPath", "org.apache.abdera.parser.stax.FOMXPath"), abdera);
    }

    public static Parser newParserInstance(Abdera abdera) {
        return (Parser)Discover.locate("org.apache.abdera.parser.Parser", abdera.getConfiguration().getConfigurationOption("org.apache.abdera.parser.Parser", "org.apache.abdera.parser.stax.FOMParser"), abdera);
    }

    public static Factory newFactoryInstance(Abdera abdera) {
        return (Factory)Discover.locate("org.apache.abdera.factory.Factory", abdera.getConfiguration().getConfigurationOption("org.apache.abdera.factory.Factory", "org.apache.abdera.parser.stax.FOMFactory"), abdera);
    }

    public static ParserFactory newParserFactoryInstance(Abdera abdera) {
        return (ParserFactory)Discover.locate("org.apache.abdera.parser.ParserFactory", abdera.getConfiguration().getConfigurationOption("org.apache.abdera.parser.ParserFactory", "org.apache.abdera.parser.stax.FOMParserFactory"), abdera);
    }

    public static WriterFactory newWriterFactoryInstance(Abdera abdera) {
        return (WriterFactory)Discover.locate("org.apache.abdera.writer.WriterFactory", abdera.getConfiguration().getConfigurationOption("org.apache.abdera.writer.WriterFactory", "org.apache.abdera.parser.stax.FOMWriterFactory"), abdera);
    }

    public static Writer newWriterInstance(Abdera abdera) {
        return (Writer)Discover.locate("org.apache.abdera.writer.Writer", abdera.getConfiguration().getConfigurationOption("org.apache.abdera.writer.Writer", "org.apache.abdera.parser.stax.FOMWriter"), abdera);
    }

    public static StreamWriter newStreamWriterInstance(Abdera abdera) {
        return (StreamWriter)Discover.locate("org.apache.abdera.writer.StreamWriter", abdera.getConfiguration().getConfigurationOption("org.apache.abdera.writer.StreamWriter", "org.apache.abdera.parser.stax.StaxStreamWriter"), abdera);
    }

    protected static synchronized List<ExtensionFactory> loadExtensionFactories() {
        ArrayList<ExtensionFactory> list = new ArrayList<ExtensionFactory>();
        Iterable<ExtensionFactory> factories = Discover.locate("org.apache.abdera.factory.ExtensionFactory", new Object[0]);
        for (ExtensionFactory factory : factories) {
            list.add(factory);
        }
        return list;
    }

    public static synchronized <T> Iterable<T> loadimpls(String sid) {
        return Discover.locate(sid, new Object[0]);
    }

    public static synchronized <T> Iterable<T> loadimpls(String sid, boolean classesonly) {
        return Discover.locate(sid, classesonly);
    }

    public static Object newInstance(String id, String _default, Abdera abdera) {
        return Discover.locate(id, _default, abdera);
    }

    public static Object newInstance(String id, String _default, Object ... args) {
        return Discover.locate(id, _default, args);
    }
}

