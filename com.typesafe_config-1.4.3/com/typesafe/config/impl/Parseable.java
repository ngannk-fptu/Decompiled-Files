/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigIncludeContext;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigParseable;
import com.typesafe.config.ConfigSyntax;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.impl.AbstractConfigNode;
import com.typesafe.config.impl.AbstractConfigObject;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigDocumentParser;
import com.typesafe.config.impl.ConfigImpl;
import com.typesafe.config.impl.ConfigImplUtil;
import com.typesafe.config.impl.ConfigNodeObject;
import com.typesafe.config.impl.ConfigNodeRoot;
import com.typesafe.config.impl.ConfigParser;
import com.typesafe.config.impl.PropertiesParser;
import com.typesafe.config.impl.SimpleConfigDocument;
import com.typesafe.config.impl.SimpleConfigObject;
import com.typesafe.config.impl.SimpleConfigOrigin;
import com.typesafe.config.impl.SimpleIncludeContext;
import com.typesafe.config.impl.SimpleIncluder;
import com.typesafe.config.impl.Token;
import com.typesafe.config.impl.Tokenizer;
import com.typesafe.config.parser.ConfigDocument;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

public abstract class Parseable
implements ConfigParseable {
    private ConfigIncludeContext includeContext;
    private ConfigParseOptions initialOptions;
    private ConfigOrigin initialOrigin;
    private static final ThreadLocal<LinkedList<Parseable>> parseStack = new ThreadLocal<LinkedList<Parseable>>(){

        @Override
        protected LinkedList<Parseable> initialValue() {
            return new LinkedList<Parseable>();
        }
    };
    private static final int MAX_INCLUDE_DEPTH = 50;
    private static final String jsonContentType = "application/json";
    private static final String propertiesContentType = "text/x-java-properties";
    private static final String hoconContentType = "application/hocon";

    protected Parseable() {
    }

    private ConfigParseOptions fixupOptions(ConfigParseOptions baseOptions) {
        ConfigSyntax syntax = baseOptions.getSyntax();
        if (syntax == null) {
            syntax = this.guessSyntax();
        }
        if (syntax == null) {
            syntax = ConfigSyntax.CONF;
        }
        ConfigParseOptions modified = baseOptions.setSyntax(syntax);
        modified = modified.appendIncluder(ConfigImpl.defaultIncluder());
        modified = modified.setIncluder(SimpleIncluder.makeFull(modified.getIncluder()));
        return modified;
    }

    protected void postConstruct(ConfigParseOptions baseOptions) {
        this.initialOptions = this.fixupOptions(baseOptions);
        this.includeContext = new SimpleIncludeContext(this);
        this.initialOrigin = this.initialOptions.getOriginDescription() != null ? SimpleConfigOrigin.newSimple(this.initialOptions.getOriginDescription()) : this.createOrigin();
    }

    protected abstract Reader reader() throws IOException;

    protected Reader reader(ConfigParseOptions options) throws IOException {
        return this.reader();
    }

    protected static void trace(String message) {
        if (ConfigImpl.traceLoadsEnabled()) {
            ConfigImpl.trace(message);
        }
    }

    ConfigSyntax guessSyntax() {
        return null;
    }

    ConfigSyntax contentType() {
        return null;
    }

    ConfigParseable relativeTo(String filename) {
        String resource = filename;
        if (filename.startsWith("/")) {
            resource = filename.substring(1);
        }
        return Parseable.newResources(resource, this.options().setOriginDescription(null));
    }

    ConfigIncludeContext includeContext() {
        return this.includeContext;
    }

    static AbstractConfigObject forceParsedToObject(ConfigValue value) {
        if (value instanceof AbstractConfigObject) {
            return (AbstractConfigObject)value;
        }
        throw new ConfigException.WrongType(value.origin(), "", "object at file root", value.valueType().name());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ConfigObject parse(ConfigParseOptions baseOptions) {
        LinkedList<Parseable> stack = parseStack.get();
        if (stack.size() >= 50) {
            throw new ConfigException.Parse(this.initialOrigin, "include statements nested more than 50 times, you probably have a cycle in your includes. Trace: " + stack);
        }
        stack.addFirst(this);
        try {
            AbstractConfigObject abstractConfigObject = Parseable.forceParsedToObject(this.parseValue(baseOptions));
            return abstractConfigObject;
        }
        finally {
            stack.removeFirst();
            if (stack.isEmpty()) {
                parseStack.remove();
            }
        }
    }

    final AbstractConfigValue parseValue(ConfigParseOptions baseOptions) {
        ConfigParseOptions options = this.fixupOptions(baseOptions);
        ConfigOrigin origin = options.getOriginDescription() != null ? SimpleConfigOrigin.newSimple(options.getOriginDescription()) : this.initialOrigin;
        return this.parseValue(origin, options);
    }

    private final AbstractConfigValue parseValue(ConfigOrigin origin, ConfigParseOptions finalOptions) {
        try {
            return this.rawParseValue(origin, finalOptions);
        }
        catch (IOException e) {
            if (finalOptions.getAllowMissing()) {
                Parseable.trace(e.getMessage() + ". Allowing Missing File, this can be turned off by setting ConfigParseOptions.allowMissing = false");
                return SimpleConfigObject.emptyMissing(origin);
            }
            Parseable.trace("exception loading " + origin.description() + ": " + e.getClass().getName() + ": " + e.getMessage());
            throw new ConfigException.IO(origin, e.getClass().getName() + ": " + e.getMessage(), e);
        }
    }

    final ConfigDocument parseDocument(ConfigParseOptions baseOptions) {
        ConfigParseOptions options = this.fixupOptions(baseOptions);
        ConfigOrigin origin = options.getOriginDescription() != null ? SimpleConfigOrigin.newSimple(options.getOriginDescription()) : this.initialOrigin;
        return this.parseDocument(origin, options);
    }

    private final ConfigDocument parseDocument(ConfigOrigin origin, ConfigParseOptions finalOptions) {
        try {
            return this.rawParseDocument(origin, finalOptions);
        }
        catch (IOException e) {
            if (finalOptions.getAllowMissing()) {
                ArrayList<AbstractConfigNode> children = new ArrayList<AbstractConfigNode>();
                children.add(new ConfigNodeObject(new ArrayList<AbstractConfigNode>()));
                return new SimpleConfigDocument(new ConfigNodeRoot(children, origin), finalOptions);
            }
            Parseable.trace("exception loading " + origin.description() + ": " + e.getClass().getName() + ": " + e.getMessage());
            throw new ConfigException.IO(origin, e.getClass().getName() + ": " + e.getMessage(), e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected AbstractConfigValue rawParseValue(ConfigOrigin origin, ConfigParseOptions finalOptions) throws IOException {
        ConfigParseOptions optionsWithContentType;
        Reader reader = this.reader(finalOptions);
        ConfigSyntax contentType = this.contentType();
        if (contentType != null) {
            if (ConfigImpl.traceLoadsEnabled() && finalOptions.getSyntax() != null) {
                Parseable.trace("Overriding syntax " + (Object)((Object)finalOptions.getSyntax()) + " with Content-Type which specified " + (Object)((Object)contentType));
            }
            optionsWithContentType = finalOptions.setSyntax(contentType);
        } else {
            optionsWithContentType = finalOptions;
        }
        try {
            AbstractConfigValue abstractConfigValue = this.rawParseValue(reader, origin, optionsWithContentType);
            return abstractConfigValue;
        }
        finally {
            reader.close();
        }
    }

    private AbstractConfigValue rawParseValue(Reader reader, ConfigOrigin origin, ConfigParseOptions finalOptions) throws IOException {
        if (finalOptions.getSyntax() == ConfigSyntax.PROPERTIES) {
            return PropertiesParser.parse(reader, origin);
        }
        Iterator<Token> tokens = Tokenizer.tokenize(origin, reader, finalOptions.getSyntax());
        ConfigNodeRoot document = ConfigDocumentParser.parse(tokens, origin, finalOptions);
        return ConfigParser.parse(document, origin, finalOptions, this.includeContext());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ConfigDocument rawParseDocument(ConfigOrigin origin, ConfigParseOptions finalOptions) throws IOException {
        ConfigParseOptions optionsWithContentType;
        Reader reader = this.reader(finalOptions);
        ConfigSyntax contentType = this.contentType();
        if (contentType != null) {
            if (ConfigImpl.traceLoadsEnabled() && finalOptions.getSyntax() != null) {
                Parseable.trace("Overriding syntax " + (Object)((Object)finalOptions.getSyntax()) + " with Content-Type which specified " + (Object)((Object)contentType));
            }
            optionsWithContentType = finalOptions.setSyntax(contentType);
        } else {
            optionsWithContentType = finalOptions;
        }
        try {
            ConfigDocument configDocument = this.rawParseDocument(reader, origin, optionsWithContentType);
            return configDocument;
        }
        finally {
            reader.close();
        }
    }

    private ConfigDocument rawParseDocument(Reader reader, ConfigOrigin origin, ConfigParseOptions finalOptions) throws IOException {
        Iterator<Token> tokens = Tokenizer.tokenize(origin, reader, finalOptions.getSyntax());
        return new SimpleConfigDocument(ConfigDocumentParser.parse(tokens, origin, finalOptions), finalOptions);
    }

    public ConfigObject parse() {
        return Parseable.forceParsedToObject(this.parseValue(this.options()));
    }

    public ConfigDocument parseConfigDocument() {
        return this.parseDocument(this.options());
    }

    AbstractConfigValue parseValue() {
        return this.parseValue(this.options());
    }

    @Override
    public final ConfigOrigin origin() {
        return this.initialOrigin;
    }

    protected abstract ConfigOrigin createOrigin();

    @Override
    public ConfigParseOptions options() {
        return this.initialOptions;
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    private static Reader readerFromStream(InputStream input) {
        return Parseable.readerFromStream(input, "UTF-8");
    }

    private static Reader readerFromStream(InputStream input, String encoding) {
        try {
            InputStreamReader reader = new InputStreamReader(input, encoding);
            return new BufferedReader(reader);
        }
        catch (UnsupportedEncodingException e) {
            throw new ConfigException.BugOrBroken("Java runtime does not support UTF-8", e);
        }
    }

    private static Reader doNotClose(Reader input) {
        return new FilterReader(input){

            @Override
            public void close() {
            }
        };
    }

    static URL relativeTo(URL url, String filename) {
        if (new File(filename).isAbsolute()) {
            return null;
        }
        try {
            URI siblingURI = url.toURI();
            URI relative = new URI(filename);
            URL resolved = siblingURI.resolve(relative).toURL();
            return resolved;
        }
        catch (MalformedURLException e) {
            return null;
        }
        catch (URISyntaxException e) {
            return null;
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    static File relativeTo(File file, String filename) {
        File child = new File(filename);
        if (child.isAbsolute()) {
            return null;
        }
        File parent = file.getParentFile();
        if (parent == null) {
            return null;
        }
        return new File(parent, filename);
    }

    public static Parseable newNotFound(String whatNotFound, String message, ConfigParseOptions options) {
        return new ParseableNotFound(whatNotFound, message, options);
    }

    public static Parseable newReader(Reader reader, ConfigParseOptions options) {
        return new ParseableReader(Parseable.doNotClose(reader), options);
    }

    public static Parseable newString(String input, ConfigParseOptions options) {
        return new ParseableString(input, options);
    }

    public static Parseable newURL(URL input, ConfigParseOptions options) {
        if (input.getProtocol().equals("file")) {
            return Parseable.newFile(ConfigImplUtil.urlToFile(input), options);
        }
        return new ParseableURL(input, options);
    }

    public static Parseable newFile(File input, ConfigParseOptions options) {
        return new ParseableFile(input, options);
    }

    private static Parseable newResourceURL(URL input, ConfigParseOptions options, String resource, Relativizer relativizer) {
        return new ParseableResourceURL(input, options, resource, relativizer);
    }

    public static Parseable newResources(Class<?> klass, String resource, ConfigParseOptions options) {
        return Parseable.newResources(Parseable.convertResourceName(klass, resource), options.setClassLoader(klass.getClassLoader()));
    }

    private static String convertResourceName(Class<?> klass, String resource) {
        if (resource.startsWith("/")) {
            return resource.substring(1);
        }
        String className = klass.getName();
        int i = className.lastIndexOf(46);
        if (i < 0) {
            return resource;
        }
        String packageName = className.substring(0, i);
        String packagePath = packageName.replace('.', '/');
        return packagePath + "/" + resource;
    }

    public static Parseable newResources(String resource, ConfigParseOptions options) {
        if (options.getClassLoader() == null) {
            throw new ConfigException.BugOrBroken("null class loader; pass in a class loader or use Thread.currentThread().setContextClassLoader()");
        }
        return new ParseableResources(resource, options);
    }

    public static Parseable newProperties(Properties properties, ConfigParseOptions options) {
        return new ParseableProperties(properties, options);
    }

    private static final class ParseableProperties
    extends Parseable {
        private final Properties props;

        ParseableProperties(Properties props, ConfigParseOptions options) {
            this.props = props;
            this.postConstruct(options);
        }

        @Override
        protected Reader reader() throws IOException {
            throw new ConfigException.BugOrBroken("reader() should not be called on props");
        }

        @Override
        protected AbstractConfigObject rawParseValue(ConfigOrigin origin, ConfigParseOptions finalOptions) {
            if (ConfigImpl.traceLoadsEnabled()) {
                ParseableProperties.trace("Loading config from properties " + this.props);
            }
            return PropertiesParser.fromProperties(origin, this.props);
        }

        @Override
        ConfigSyntax guessSyntax() {
            return ConfigSyntax.PROPERTIES;
        }

        @Override
        protected ConfigOrigin createOrigin() {
            return SimpleConfigOrigin.newSimple("properties");
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + this.props.size() + " props)";
        }
    }

    private static final class ParseableResources
    extends Parseable
    implements Relativizer {
        private final String resource;

        ParseableResources(String resource, ConfigParseOptions options) {
            this.resource = resource;
            this.postConstruct(options);
        }

        @Override
        protected Reader reader() throws IOException {
            throw new ConfigException.BugOrBroken("reader() should not be called on resources");
        }

        @Override
        protected AbstractConfigObject rawParseValue(ConfigOrigin origin, ConfigParseOptions finalOptions) throws IOException {
            ClassLoader loader = finalOptions.getClassLoader();
            if (loader == null) {
                throw new ConfigException.BugOrBroken("null class loader; pass in a class loader or use Thread.currentThread().setContextClassLoader()");
            }
            Enumeration<URL> e = loader.getResources(this.resource);
            if (!e.hasMoreElements()) {
                if (ConfigImpl.traceLoadsEnabled()) {
                    ParseableResources.trace("Loading config from class loader " + loader + " but there were no resources called " + this.resource);
                }
                throw new IOException("resource not found on classpath: " + this.resource);
            }
            AbstractConfigObject merged = SimpleConfigObject.empty(origin);
            while (e.hasMoreElements()) {
                URL url = e.nextElement();
                if (ConfigImpl.traceLoadsEnabled()) {
                    ParseableResources.trace("Loading config from resource '" + this.resource + "' URL " + url.toExternalForm() + " from class loader " + loader);
                }
                Parseable element = Parseable.newResourceURL(url, finalOptions, this.resource, this);
                AbstractConfigValue v = element.parseValue();
                merged = merged.withFallback(v);
            }
            return merged;
        }

        @Override
        ConfigSyntax guessSyntax() {
            return ConfigImplUtil.syntaxFromExtension(this.resource);
        }

        static String parent(String resource) {
            int i = resource.lastIndexOf(47);
            if (i < 0) {
                return null;
            }
            return resource.substring(0, i);
        }

        @Override
        public ConfigParseable relativeTo(String sibling) {
            if (sibling.startsWith("/")) {
                return ParseableResources.newResources(sibling.substring(1), this.options().setOriginDescription(null));
            }
            String parent = ParseableResources.parent(this.resource);
            if (parent == null) {
                return ParseableResources.newResources(sibling, this.options().setOriginDescription(null));
            }
            return ParseableResources.newResources(parent + "/" + sibling, this.options().setOriginDescription(null));
        }

        @Override
        protected ConfigOrigin createOrigin() {
            return SimpleConfigOrigin.newResource(this.resource);
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + this.resource + ")";
        }
    }

    private static final class ParseableResourceURL
    extends ParseableURL {
        private final Relativizer relativizer;
        private final String resource;

        ParseableResourceURL(URL input, ConfigParseOptions options, String resource, Relativizer relativizer) {
            super(input);
            this.relativizer = relativizer;
            this.resource = resource;
            this.postConstruct(options);
        }

        @Override
        protected ConfigOrigin createOrigin() {
            return SimpleConfigOrigin.newResource(this.resource, this.input);
        }

        @Override
        ConfigParseable relativeTo(String filename) {
            return this.relativizer.relativeTo(filename);
        }
    }

    private static final class ParseableFile
    extends Parseable {
        private final File input;

        ParseableFile(File input, ConfigParseOptions options) {
            this.input = input;
            this.postConstruct(options);
        }

        @Override
        protected Reader reader() throws IOException {
            if (ConfigImpl.traceLoadsEnabled()) {
                ParseableFile.trace("Loading config from a file: " + this.input);
            }
            FileInputStream stream = new FileInputStream(this.input);
            return Parseable.readerFromStream(stream);
        }

        @Override
        ConfigSyntax guessSyntax() {
            return ConfigImplUtil.syntaxFromExtension(this.input.getName());
        }

        @Override
        ConfigParseable relativeTo(String filename) {
            File sibling = new File(filename).isAbsolute() ? new File(filename) : ParseableFile.relativeTo(this.input, filename);
            if (sibling == null) {
                return null;
            }
            if (sibling.exists()) {
                ParseableFile.trace(sibling + " exists, so loading it as a file");
                return ParseableFile.newFile(sibling, this.options().setOriginDescription(null));
            }
            ParseableFile.trace(sibling + " does not exist, so trying it as a classpath resource");
            return super.relativeTo(filename);
        }

        @Override
        protected ConfigOrigin createOrigin() {
            return SimpleConfigOrigin.newFile(this.input.getPath());
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + this.input.getPath() + ")";
        }
    }

    private static class ParseableURL
    extends Parseable {
        protected final URL input;
        private String contentType = null;

        protected ParseableURL(URL input) {
            this.input = input;
        }

        ParseableURL(URL input, ConfigParseOptions options) {
            this(input);
            this.postConstruct(options);
        }

        @Override
        protected Reader reader() throws IOException {
            throw new ConfigException.BugOrBroken("reader() without options should not be called on ParseableURL");
        }

        private static String acceptContentType(ConfigParseOptions options) {
            if (options.getSyntax() == null) {
                return null;
            }
            switch (options.getSyntax()) {
                case JSON: {
                    return Parseable.jsonContentType;
                }
                case CONF: {
                    return Parseable.hoconContentType;
                }
                case PROPERTIES: {
                    return Parseable.propertiesContentType;
                }
            }
            return null;
        }

        @Override
        protected Reader reader(ConfigParseOptions options) throws IOException {
            try {
                if (ConfigImpl.traceLoadsEnabled()) {
                    ParseableURL.trace("Loading config from a URL: " + this.input.toExternalForm());
                }
                URLConnection connection = this.input.openConnection();
                String acceptContent = ParseableURL.acceptContentType(options);
                if (acceptContent != null) {
                    connection.setRequestProperty("Accept", acceptContent);
                }
                connection.connect();
                this.contentType = connection.getContentType();
                if (this.contentType != null) {
                    if (ConfigImpl.traceLoadsEnabled()) {
                        ParseableURL.trace("URL sets Content-Type: '" + this.contentType + "'");
                    }
                    this.contentType = this.contentType.trim();
                    int semi = this.contentType.indexOf(59);
                    if (semi >= 0) {
                        this.contentType = this.contentType.substring(0, semi);
                    }
                }
                InputStream stream = connection.getInputStream();
                return Parseable.readerFromStream(stream);
            }
            catch (FileNotFoundException fnf) {
                throw fnf;
            }
            catch (IOException e) {
                throw new ConfigException.BugOrBroken("Cannot load config from URL: " + this.input.toExternalForm(), e);
            }
        }

        @Override
        ConfigSyntax guessSyntax() {
            return ConfigImplUtil.syntaxFromExtension(this.input.getPath());
        }

        @Override
        ConfigSyntax contentType() {
            if (this.contentType != null) {
                if (this.contentType.equals(Parseable.jsonContentType)) {
                    return ConfigSyntax.JSON;
                }
                if (this.contentType.equals(Parseable.propertiesContentType)) {
                    return ConfigSyntax.PROPERTIES;
                }
                if (this.contentType.equals(Parseable.hoconContentType)) {
                    return ConfigSyntax.CONF;
                }
                if (ConfigImpl.traceLoadsEnabled()) {
                    ParseableURL.trace("'" + this.contentType + "' isn't a known content type");
                }
                return null;
            }
            return null;
        }

        @Override
        ConfigParseable relativeTo(String filename) {
            URL url = ParseableURL.relativeTo(this.input, filename);
            if (url == null) {
                return null;
            }
            return ParseableURL.newURL(url, this.options().setOriginDescription(null));
        }

        @Override
        protected ConfigOrigin createOrigin() {
            return SimpleConfigOrigin.newURL(this.input);
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + this.input.toExternalForm() + ")";
        }
    }

    private static final class ParseableString
    extends Parseable {
        private final String input;

        ParseableString(String input, ConfigParseOptions options) {
            this.input = input;
            this.postConstruct(options);
        }

        @Override
        protected Reader reader() {
            if (ConfigImpl.traceLoadsEnabled()) {
                ParseableString.trace("Loading config from a String " + this.input);
            }
            return new StringReader(this.input);
        }

        @Override
        protected ConfigOrigin createOrigin() {
            return SimpleConfigOrigin.newSimple("String");
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + this.input + ")";
        }
    }

    private static final class ParseableReader
    extends Parseable {
        private final Reader reader;

        ParseableReader(Reader reader, ConfigParseOptions options) {
            this.reader = reader;
            this.postConstruct(options);
        }

        @Override
        protected Reader reader() {
            if (ConfigImpl.traceLoadsEnabled()) {
                ParseableReader.trace("Loading config from reader " + this.reader);
            }
            return this.reader;
        }

        @Override
        protected ConfigOrigin createOrigin() {
            return SimpleConfigOrigin.newSimple("Reader");
        }
    }

    private static final class ParseableNotFound
    extends Parseable {
        private final String what;
        private final String message;

        ParseableNotFound(String what, String message, ConfigParseOptions options) {
            this.what = what;
            this.message = message;
            this.postConstruct(options);
        }

        @Override
        protected Reader reader() throws IOException {
            throw new FileNotFoundException(this.message);
        }

        @Override
        protected ConfigOrigin createOrigin() {
            return SimpleConfigOrigin.newSimple(this.what);
        }
    }

    protected static interface Relativizer {
        public ConfigParseable relativeTo(String var1);
    }
}

