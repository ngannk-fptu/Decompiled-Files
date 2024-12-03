/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.abdera.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.factory.StreamBuilder;
import org.apache.abdera.i18n.text.Localizer;
import org.apache.abdera.parser.NamedParser;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserFactory;
import org.apache.abdera.util.Configuration;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.Discover;
import org.apache.abdera.writer.NamedWriter;
import org.apache.abdera.writer.StreamWriter;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.abdera.xpath.XPath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AbderaConfiguration
implements Constants,
Configuration {
    private static final long serialVersionUID = 7460203853824337559L;
    private static final Log log = LogFactory.getLog(AbderaConfiguration.class);
    private final ResourceBundle bundle;
    private final List<ExtensionFactory> factories;
    private final Map<String, NamedWriter> writers;
    private final Map<String, Class<? extends StreamWriter>> streamwriters;
    private final Map<String, NamedParser> parsers;

    public static synchronized Configuration getDefault() {
        AbderaConfiguration instance = null;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("abdera");
            instance = new AbderaConfiguration(bundle);
        }
        catch (Exception e) {
            instance = new AbderaConfiguration();
        }
        return instance;
    }

    private static ResourceBundle getBundle(Locale locale) {
        ResourceBundle bundle = null;
        try {
            bundle = ResourceBundle.getBundle("abdera", locale, Thread.currentThread().getContextClassLoader());
        }
        catch (Exception exception) {
            // empty catch block
        }
        return bundle;
    }

    public AbderaConfiguration() {
        this(null);
    }

    protected AbderaConfiguration(ResourceBundle bundle) {
        this.bundle = bundle != null ? bundle : AbderaConfiguration.getBundle(Locale.getDefault());
        this.factories = AbderaConfiguration.loadExtensionFactories();
        this.writers = this.initNamedWriters();
        this.parsers = this.initNamedParsers();
        this.streamwriters = this.initStreamWriters();
    }

    private static synchronized List<ExtensionFactory> loadExtensionFactories() {
        ArrayList<ExtensionFactory> list = new ArrayList<ExtensionFactory>();
        Iterable<ExtensionFactory> factories = Discover.locate("org.apache.abdera.factory.ExtensionFactory", new Object[0]);
        for (ExtensionFactory factory : factories) {
            list.add(factory);
        }
        return list;
    }

    private ResourceBundle getBundle() {
        return this.bundle;
    }

    @Override
    public String getConfigurationOption(String id) {
        String option = System.getProperty(id);
        if (option == null) {
            try {
                ResourceBundle bundle = this.getBundle();
                if (bundle != null) {
                    option = bundle.getString(id);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return option;
    }

    @Override
    public String getConfigurationOption(String id, String _default) {
        String value = this.getConfigurationOption(id);
        return value != null ? value : _default;
    }

    @Override
    public AbderaConfiguration addExtensionFactory(ExtensionFactory factory) {
        List<ExtensionFactory> factories = this.getExtensionFactories();
        if (!factories.contains(factory)) {
            factories.add(factory);
        } else {
            log.warn((Object)("These extensions are already registered: " + factory.getNamespaces()));
        }
        return this;
    }

    @Override
    public List<ExtensionFactory> getExtensionFactories() {
        return this.factories;
    }

    @Override
    public AbderaConfiguration addNamedWriter(NamedWriter writer) {
        Map<String, NamedWriter> writers = this.getNamedWriters();
        if (!writers.containsKey(writer.getName())) {
            writers.put(writer.getName(), writer);
        } else {
            log.warn((Object)("The NamedWriter is already registered: " + writer.getName()));
        }
        return this;
    }

    private Map<String, NamedWriter> initNamedWriters() {
        Map<String, NamedWriter> writers = null;
        Iterable<NamedWriter> _writers = Discover.locate("org.apache.abdera.writer.NamedWriter", new Object[0]);
        writers = Collections.synchronizedMap(new HashMap());
        for (NamedWriter writer : _writers) {
            writers.put(writer.getName().toLowerCase(), writer);
        }
        return writers;
    }

    private Map<String, Class<? extends StreamWriter>> initStreamWriters() {
        Map<String, Class<? extends StreamWriter>> writers = null;
        Iterable<Class> _writers = Discover.locate("org.apache.abdera.writer.StreamWriter", true);
        writers = Collections.synchronizedMap(new HashMap());
        for (Class writer : _writers) {
            String name = AbderaConfiguration.getName(writer);
            if (name == null) continue;
            writers.put(name.toLowerCase(), writer);
        }
        writers.put("fom", StreamBuilder.class);
        return writers;
    }

    private static String getName(Class<? extends StreamWriter> sw) {
        String name = null;
        try {
            Field field = sw.getField("NAME");
            if (Modifier.isStatic(field.getModifiers())) {
                name = (String)field.get(null);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return name;
    }

    @Override
    public Map<String, NamedWriter> getNamedWriters() {
        return this.writers;
    }

    @Override
    public Map<String, Class<? extends StreamWriter>> getStreamWriters() {
        return this.streamwriters;
    }

    @Override
    public AbderaConfiguration addNamedParser(NamedParser parser) {
        Map<String, NamedParser> parsers = this.getNamedParsers();
        if (!parsers.containsKey(parser.getName())) {
            parsers.put(parser.getName(), parser);
        } else {
            log.warn((Object)("The NamedParser is already registered: " + parser.getName()));
        }
        return this;
    }

    @Override
    public AbderaConfiguration addStreamWriter(Class<? extends StreamWriter> sw) {
        String swName;
        Map<String, Class<? extends StreamWriter>> streamWriters = this.getStreamWriters();
        if (!streamWriters.containsKey(swName = AbderaConfiguration.getName(sw))) {
            streamWriters.put(swName, sw);
        } else {
            log.warn((Object)("The StreamWriter is already registered: " + swName));
        }
        return this;
    }

    private Map<String, NamedParser> initNamedParsers() {
        Map<String, NamedParser> parsers = null;
        Iterable<NamedParser> _parsers = Discover.locate("org.apache.abdera.parser.NamedParser", new Object[0]);
        parsers = Collections.synchronizedMap(new HashMap());
        for (NamedParser parser : _parsers) {
            parsers.put(parser.getName().toLowerCase(), parser);
        }
        return parsers;
    }

    @Override
    public Map<String, NamedParser> getNamedParsers() {
        return this.parsers;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Factory newFactoryInstance(Abdera abdera) {
        return (Factory)Discover.locate("org.apache.abdera.factory.Factory", abdera.getConfiguration().getConfigurationOption("org.apache.abdera.factory.Factory", "org.apache.abdera.parser.stax.FOMFactory"), abdera);
    }

    @Override
    public Parser newParserInstance(Abdera abdera) {
        return (Parser)Discover.locate("org.apache.abdera.parser.Parser", abdera.getConfiguration().getConfigurationOption("org.apache.abdera.parser.Parser", "org.apache.abdera.parser.stax.FOMParser"), abdera);
    }

    @Override
    public XPath newXPathInstance(Abdera abdera) {
        try {
            return (XPath)Discover.locate("org.apache.abdera.xpath.XPath", abdera.getConfiguration().getConfigurationOption("org.apache.abdera.xpath.XPath", "org.apache.abdera.parser.stax.FOMXPath"), abdera);
        }
        catch (Throwable n) {
            throw this.throwex("IMPLEMENTATION.NOT.AVAILABLE", "XPath", n);
        }
    }

    @Override
    public ParserFactory newParserFactoryInstance(Abdera abdera) {
        try {
            return (ParserFactory)Discover.locate("org.apache.abdera.parser.ParserFactory", abdera.getConfiguration().getConfigurationOption("org.apache.abdera.parser.ParserFactory", "org.apache.abdera.parser.stax.FOMParserFactory"), abdera);
        }
        catch (Throwable n) {
            throw this.throwex("IMPLEMENTATION.NOT.AVAILABLE", "Parser", n);
        }
    }

    @Override
    public WriterFactory newWriterFactoryInstance(Abdera abdera) {
        try {
            return (WriterFactory)Discover.locate("org.apache.abdera.writer.WriterFactory", abdera.getConfiguration().getConfigurationOption("org.apache.abdera.writer.WriterFactory", "org.apache.abdera.parser.stax.FOMWriterFactory"), abdera);
        }
        catch (Throwable n) {
            throw this.throwex("IMPLEMENTATION.NOT.AVAILABLE", "WriterFactory", n);
        }
    }

    @Override
    public Writer newWriterInstance(Abdera abdera) {
        try {
            return (Writer)Discover.locate("org.apache.abdera.writer.Writer", abdera.getConfiguration().getConfigurationOption("org.apache.abdera.writer.Writer", "org.apache.abdera.parser.stax.FOMWriter"), abdera);
        }
        catch (Throwable n) {
            throw this.throwex("IMPLEMENTATION.NOT.AVAILABLE", "Writer", n);
        }
    }

    @Override
    public StreamWriter newStreamWriterInstance(Abdera abdera) {
        try {
            return (StreamWriter)Discover.locate("org.apache.abdera.writer.StreamWriter", abdera.getConfiguration().getConfigurationOption("org.apache.abdera.writer.StreamWriter", "org.apache.abdera.parser.stax.StaxStreamWriter"), abdera);
        }
        catch (Throwable n) {
            throw this.throwex("IMPLEMENTATION.NOT.AVAILABLE", "StreamWriter", n);
        }
    }

    private RuntimeException throwex(String id, String arg, Throwable t) {
        return new RuntimeException(Localizer.sprintf(id, arg), t);
    }
}

