/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.osgi;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.About;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Domain;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Macro;
import aQute.bnd.osgi.OSInformation;
import aQute.bnd.osgi.Verifier;
import aQute.bnd.service.Plugin;
import aQute.bnd.service.Registry;
import aQute.bnd.service.RegistryDonePlugin;
import aQute.bnd.service.RegistryPlugin;
import aQute.bnd.service.url.URLConnectionHandler;
import aQute.bnd.version.Version;
import aQute.bnd.version.VersionRange;
import aQute.lib.collections.ExtList;
import aQute.lib.collections.SortedList;
import aQute.lib.exceptions.Exceptions;
import aQute.lib.hex.Hex;
import aQute.lib.io.IO;
import aQute.lib.strings.Strings;
import aQute.lib.utf8properties.UTF8Properties;
import aQute.libg.cryptography.SHA1;
import aQute.libg.generics.Create;
import aQute.libg.reporter.ReporterAdapter;
import aQute.libg.slf4j.GradleLogging;
import aQute.service.reporter.Report;
import aQute.service.reporter.Reporter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Processor
extends Domain
implements Reporter,
Registry,
Constants,
Closeable {
    private static final Logger logger = LoggerFactory.getLogger(Processor.class);
    public static Reporter log;
    static final int BUFFER_SIZE = 4096;
    static Pattern PACKAGES_IGNORED;
    static ThreadLocal<Processor> current;
    private static final ScheduledExecutorService sheduledExecutor;
    private static final ExecutorService executor;
    static Random random;
    public static final String LIST_SPLITTER = "\\s*,\\s*";
    final List<String> errors = new ArrayList<String>();
    final List<String> warnings = new ArrayList<String>();
    final Set<Object> basicPlugins = new HashSet<Object>();
    private final Set<Closeable> toBeClosed = new HashSet<Closeable>();
    private Set<Object> plugins;
    boolean pedantic;
    boolean trace;
    boolean exceptions;
    boolean fileMustExist = true;
    private File base = new File("").getAbsoluteFile();
    private URI baseURI = this.base.toURI();
    Properties properties;
    String profile;
    private Macro replacer;
    private long lastModified;
    private File propertiesFile;
    private boolean fixup = true;
    long modified;
    Processor parent;
    List<File> included;
    CL pluginLoader;
    Collection<String> filter;
    HashSet<String> missingCommand;
    Boolean strict;
    boolean fixupMessages;
    static String _uri;
    static String _fileuri;
    List<Report.Location> locations = new ArrayList<Report.Location>();
    Version upto = null;

    public Processor() {
        this.properties = new UTF8Properties();
    }

    public Processor(Properties parent) {
        this.properties = new UTF8Properties(parent);
    }

    public Processor(Processor processor) {
        this(processor.getProperties0());
        this.parent = processor;
    }

    public Processor(Properties props, boolean copy) {
        this.properties = copy ? new UTF8Properties(props) : props;
    }

    public void setParent(Processor processor) {
        this.parent = processor;
        UTF8Properties updated = new UTF8Properties(processor.getProperties0());
        updated.putAll((Map<?, ?>)this.getProperties0());
        this.properties = updated;
    }

    public Processor getParent() {
        return this.parent;
    }

    public Processor getTop() {
        if (this.parent == null) {
            return this;
        }
        return this.parent.getTop();
    }

    public void getInfo(Reporter processor, String prefix) {
        if (prefix == null) {
            prefix = this.getBase() + " :";
        }
        if (this.isFailOk()) {
            this.addAll(this.warnings, processor.getErrors(), prefix, processor);
        } else {
            this.addAll(this.errors, processor.getErrors(), prefix, processor);
        }
        this.addAll(this.warnings, processor.getWarnings(), prefix, processor);
        processor.getErrors().clear();
        processor.getWarnings().clear();
    }

    public void getInfo(Reporter processor) {
        this.getInfo(processor, "");
    }

    private void addAll(List<String> to, List<String> from, String prefix, Reporter reporter) {
        try {
            for (String message : from) {
                String newMessage = prefix.isEmpty() ? message : prefix + message;
                to.add(newMessage);
                Report.Location location = reporter.getLocation(message);
                if (location == null) continue;
                Reporter.SetLocation newer = this.location(newMessage);
                for (Field f : newer.getClass().getFields()) {
                    if ("message".equals(f.getName())) continue;
                    f.set(newer, f.get(location));
                }
            }
        }
        catch (Exception e) {
            throw Exceptions.duck(e);
        }
    }

    private Processor current() {
        Processor p = current.get();
        if (p == null) {
            return this;
        }
        return p;
    }

    @Override
    public Reporter.SetLocation warning(String string, Object ... args) {
        this.fixupMessages = false;
        Processor p = this.current();
        String s = Processor.formatArrays(string, args);
        if (!p.warnings.contains(s)) {
            p.warnings.add(s);
        }
        p.signal();
        return this.location(s);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Reporter.SetLocation error(String string, Object ... args) {
        this.fixupMessages = false;
        Processor p = this.current();
        try {
            if (p.isFailOk()) {
                Reporter.SetLocation setLocation = p.warning(string, args);
                return setLocation;
            }
            String s = Processor.formatArrays(string, args);
            if (!p.errors.contains(s)) {
                p.errors.add(s);
            }
            Reporter.SetLocation setLocation = this.location(s);
            return setLocation;
        }
        finally {
            p.signal();
        }
    }

    @Override
    @Deprecated
    public void progress(float progress, String format, Object ... args) {
        Logger l = this.getLogger();
        if (l.isInfoEnabled(GradleLogging.LIFECYCLE)) {
            String message = Processor.formatArrays(format, args);
            if (progress > 0.0f) {
                l.info(GradleLogging.LIFECYCLE, "[{}] {}", (Object)((int)progress), (Object)message);
            } else {
                l.info(GradleLogging.LIFECYCLE, "{}", (Object)message);
            }
        }
    }

    public void progress(String format, Object ... args) {
        this.progress(-1.0f, format, args);
    }

    public Reporter.SetLocation error(String format, Throwable t, Object ... args) {
        return this.exception(t, format, args);
    }

    @Override
    public Reporter.SetLocation exception(Throwable t, String format, Object ... args) {
        Processor p = this.current();
        if (p.trace) {
            p.getLogger().info("Reported exception", t);
        } else {
            p.getLogger().debug("Reported exception", t);
        }
        if (p.exceptions) {
            this.printExceptionSummary(t, System.err);
        }
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        String s = Processor.formatArrays("Exception: %s", Exceptions.toString(t));
        if (p.isFailOk()) {
            p.warnings.add(s);
        } else {
            p.errors.add(s);
        }
        return this.error(format, args);
    }

    public int printExceptionSummary(Throwable e, PrintStream out) {
        if (e == null) {
            return 0;
        }
        int count = 10;
        int n = this.printExceptionSummary(e.getCause(), out);
        if (n == 0) {
            out.println("Root cause: " + e.getMessage() + "   :" + e.getClass().getName());
            count = Integer.MAX_VALUE;
        } else {
            out.println("Rethrown from: " + e.toString());
        }
        out.println();
        this.printStackTrace(e, count, out);
        System.err.println();
        return n + 1;
    }

    public void printStackTrace(Throwable e, int count, PrintStream out) {
        e.printStackTrace(out);
    }

    public void signal() {
    }

    @Override
    public List<String> getWarnings() {
        this.fixupMessages();
        return this.warnings;
    }

    @Override
    public List<String> getErrors() {
        this.fixupMessages();
        return this.errors;
    }

    public static Parameters parseHeader(String value, Processor logger) {
        return new Parameters(value, logger);
    }

    public Parameters parseHeader(String value) {
        return new Parameters(value, this);
    }

    public void addClose(Closeable jar) {
        assert (jar != null);
        this.toBeClosed.add(jar);
    }

    public void removeClose(Closeable jar) {
        assert (jar != null);
        this.toBeClosed.remove(jar);
    }

    @Override
    public boolean isPedantic() {
        return this.current().pedantic;
    }

    public void setPedantic(boolean pedantic) {
        this.pedantic = pedantic;
    }

    public void use(Processor reporter) {
        this.setPedantic(reporter.isPedantic());
        this.setTrace(reporter.isTrace());
        this.setExceptions(reporter.isExceptions());
        this.setFailOk(reporter.isFailOk());
    }

    public static File getFile(File base, String file) {
        return IO.getFile(base, file);
    }

    public File getFile(String file) {
        return Processor.getFile(this.base, file);
    }

    @Override
    public <T> List<T> getPlugins(Class<T> clazz) {
        ArrayList<T> l = new ArrayList<T>();
        Set<Object> all = this.getPlugins();
        for (Object plugin : all) {
            if (!clazz.isInstance(plugin)) continue;
            l.add(clazz.cast(plugin));
        }
        return l;
    }

    @Override
    public <T> T getPlugin(Class<T> clazz) {
        Set<Object> all = this.getPlugins();
        for (Object plugin : all) {
            if (!clazz.isInstance(plugin)) continue;
            return clazz.cast(plugin);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<Object> getPlugins() {
        Set<Object> p;
        Processor processor = this;
        synchronized (processor) {
            p = this.plugins;
            if (p != null) {
                return p;
            }
            this.plugins = p = new CopyOnWriteArraySet<Object>();
            this.missingCommand = new HashSet();
        }
        String spe = this.getProperty("-plugin");
        if ("none".equals(spe)) {
            return p;
        }
        p.add(this);
        this.setTypeSpecificPlugins(p);
        if (this.parent != null) {
            p.addAll(this.parent.getPlugins());
        }
        spe = this.mergeLocalProperties("-plugin");
        String pluginPath = this.mergeProperties("-pluginpath");
        this.loadPlugins(p, spe, pluginPath);
        this.addExtensions(p);
        for (RegistryDonePlugin rdp : this.getPlugins(RegistryDonePlugin.class)) {
            try {
                rdp.done();
            }
            catch (Exception e) {
                this.error("Calling done on %s, gives an exception %s", rdp, e);
            }
        }
        return p;
    }

    protected void addExtensions(Set<Object> p) {
    }

    protected void loadPlugins(Set<Object> instances, String pluginString, String pluginPathString) {
        Attrs attrs;
        String className;
        Parameters plugins = new Parameters(pluginString, this);
        CL loader = this.getLoader();
        for (Map.Entry<String, Attrs> entry : plugins.entrySet()) {
            String key = Processor.removeDuplicateMarker(entry.getKey());
            String path = entry.getValue().get("path:");
            if (path == null) continue;
            String[] parts = path.split(LIST_SPLITTER);
            try {
                for (String p : parts) {
                    File f = this.getFile(p).getAbsoluteFile();
                    loader.add(f.toURI().toURL());
                }
            }
            catch (Exception e) {
                this.error("Problem adding path %s to loader for plugin %s. Exception: (%s)", path, key, e);
            }
        }
        HashSet<String> loaded = new HashSet<String>();
        for (Map.Entry<String, Attrs> entry : plugins.entrySet()) {
            className = Processor.removeDuplicateMarker(entry.getKey());
            attrs = entry.getValue();
            logger.debug("Trying pre-plugin {}", (Object)className);
            Object plugin = this.loadPlugin(this.getClass().getClassLoader(), attrs, className, true);
            if (plugin == null) continue;
            loaded.add(entry.getKey());
            instances.add(plugin);
        }
        plugins.keySet().removeAll(loaded);
        this.loadPluginPath(instances, pluginPathString, loader);
        for (Map.Entry<String, Attrs> entry : plugins.entrySet()) {
            className = Processor.removeDuplicateMarker(entry.getKey());
            attrs = entry.getValue();
            logger.debug("Loading secondary plugin {}", (Object)className);
            String commands = attrs.get("command:");
            Object plugin = this.loadPlugin(loader, attrs, className, commands != null);
            if (plugin != null) {
                instances.add(plugin);
                continue;
            }
            if (commands == null) {
                this.error("Cannot load the plugin %s", className);
                continue;
            }
            Collection<String> cs = Processor.split(commands);
            this.missingCommand.addAll(cs);
        }
    }

    private void loadPluginPath(Set<Object> instances, String pluginPath, CL loader) {
        Parameters pluginpath = new Parameters(pluginPath, this);
        for (Map.Entry<String, Attrs> entry : pluginpath.entrySet()) {
            File f;
            block11: {
                f = this.getFile(entry.getKey()).getAbsoluteFile();
                if (!f.isFile()) {
                    String url = entry.getValue().get("url");
                    if (url != null) {
                        try {
                            logger.debug("downloading {} to {}", (Object)url, (Object)f.getAbsoluteFile());
                            URL u = new URL(url);
                            URLConnection connection = u.openConnection();
                            for (Object plugin : instances) {
                                URLConnectionHandler handler;
                                if (!(plugin instanceof URLConnectionHandler) || !(handler = (URLConnectionHandler)plugin).matches(u)) continue;
                                handler.handle(connection);
                            }
                            IO.mkdirs(f.getParentFile());
                            IO.copy(connection.getInputStream(), f);
                            String digest = entry.getValue().get("sha1");
                            if (digest == null) break block11;
                            if (Hex.isHex(digest.trim())) {
                                byte[] filesha1;
                                byte[] sha1 = Hex.toByteArray(digest);
                                if (!Arrays.equals(sha1, filesha1 = SHA1.digest(f).digest())) {
                                    this.error("Plugin path: %s, specified url %s and a sha1 but the file does not match the sha", entry.getKey(), url);
                                }
                            } else {
                                this.error("Plugin path: %s, specified url %s and a sha1 '%s' but this is not a hexadecimal", entry.getKey(), url, digest);
                            }
                            break block11;
                        }
                        catch (Exception e) {
                            this.error("Failed to download plugin %s from %s, error %s", entry.getKey(), url, e);
                            continue;
                        }
                    }
                    this.error("No such file %s from %s and no 'url' attribute on the path so it can be downloaded", entry.getKey(), this);
                    continue;
                }
            }
            logger.debug("Adding {} to loader for plugins", (Object)f);
            try {
                loader.add(f.toURI().toURL());
            }
            catch (MalformedURLException e) {}
        }
    }

    private Object loadPlugin(ClassLoader loader, Attrs attrs, String className, boolean ignoreError) {
        try {
            Class<?> c = loader.loadClass(className);
            Object plugin = c.getConstructor(new Class[0]).newInstance(new Object[0]);
            this.customize(plugin, attrs);
            if (plugin instanceof Closeable) {
                this.addClose((Closeable)plugin);
            }
            return plugin;
        }
        catch (NoClassDefFoundError e) {
            if (!ignoreError) {
                this.exception(e, "Failed to load plugin %s;%s, error: %s ", className, attrs, e);
            }
        }
        catch (ClassNotFoundException e) {
            if (!ignoreError) {
                this.exception(e, "Failed to load plugin %s;%s, error: %s ", className, attrs, e);
            }
        }
        catch (Exception e) {
            this.exception(e, "Unexpected error loading plugin %s-%s: %s", className, attrs, e);
        }
        return null;
    }

    protected void setTypeSpecificPlugins(Set<Object> list) {
        list.add(Processor.getExecutor());
        list.add(random);
        list.addAll(this.basicPlugins);
    }

    protected <T> T customize(T plugin, Attrs map) {
        if (plugin instanceof Plugin) {
            ((Plugin)plugin).setReporter(this);
            try {
                if (map == null) {
                    map = Attrs.EMPTY_ATTRS;
                }
                ((Plugin)plugin).setProperties(map);
            }
            catch (Exception e) {
                this.error("While setting properties %s on plugin %s, %s", map, plugin, e);
            }
        }
        if (plugin instanceof RegistryPlugin) {
            ((RegistryPlugin)plugin).setRegistry(this);
        }
        return plugin;
    }

    @Override
    public boolean isFailOk() {
        String v = this.getProperty("-failok", null);
        return v != null && v.equalsIgnoreCase("true");
    }

    public File getBase() {
        return this.base;
    }

    public URI getBaseURI() {
        return this.baseURI;
    }

    public void setBase(File base) {
        this.base = base;
        this.baseURI = base == null ? null : base.toURI();
    }

    public void clear() {
        this.errors.clear();
        this.warnings.clear();
        this.locations.clear();
        this.fixupMessages = false;
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    @Deprecated
    public void trace(String msg, Object ... parms) {
        Processor p = this.current();
        Logger l = p.getLogger();
        if (p.trace) {
            if (l.isInfoEnabled()) {
                l.info("{}", (Object)Processor.formatArrays(msg, parms));
            }
        } else if (l.isDebugEnabled()) {
            l.debug("{}", (Object)Processor.formatArrays(msg, parms));
        }
    }

    public <T> List<T> newList() {
        return new ArrayList();
    }

    public <T> Set<T> newSet() {
        return new TreeSet();
    }

    public static <K, V> Map<K, V> newMap() {
        return new LinkedHashMap();
    }

    public static <K, V> Map<K, V> newHashMap() {
        return new LinkedHashMap();
    }

    public <T> List<T> newList(Collection<T> t) {
        return new ArrayList<T>(t);
    }

    public <T> Set<T> newSet(Collection<T> t) {
        return new TreeSet<T>(t);
    }

    public <K, V> Map<K, V> newMap(Map<K, V> t) {
        return new LinkedHashMap<K, V>(t);
    }

    @Override
    public void close() throws IOException {
        for (Closeable c : this.toBeClosed) {
            IO.close(c);
        }
        if (this.pluginLoader != null) {
            this.pluginLoader.closex();
        }
        this.toBeClosed.clear();
    }

    public String _basedir(String[] args) {
        if (this.base == null) {
            throw new IllegalArgumentException("No base dir set");
        }
        return this.base.getAbsolutePath();
    }

    public String _propertiesname(String[] args) {
        if (args.length > 1) {
            this.error("propertiesname does not take arguments", new Object[0]);
            return null;
        }
        File pf = this.getPropertiesFile();
        if (pf == null) {
            return "";
        }
        return pf.getName();
    }

    public String _propertiesdir(String[] args) {
        if (args.length > 1) {
            this.error("propertiesdir does not take arguments", new Object[0]);
            return null;
        }
        File pf = this.getPropertiesFile();
        if (pf == null) {
            return "";
        }
        return pf.getParentFile().getAbsolutePath();
    }

    public String _uri(String[] args) throws Exception {
        Macro.verifyCommand(args, _uri, null, 2, 3);
        URI uri = new URI(args[1]);
        if (!uri.isAbsolute() || uri.getScheme().equals("file")) {
            URI base;
            if (args.length > 2) {
                base = new URI(args[2]);
            } else {
                base = this.getBaseURI();
                if (base == null) {
                    throw new IllegalArgumentException("No base dir set");
                }
            }
            uri = base.resolve(uri.getSchemeSpecificPart());
        }
        return uri.toString();
    }

    public String _fileuri(String[] args) throws Exception {
        Macro.verifyCommand(args, _fileuri, null, 2, 2);
        File f = IO.getFile(this.getBase(), args[1]).getCanonicalFile();
        return f.toURI().toString();
    }

    public Properties getProperties() {
        if (this.fixup) {
            this.fixup = false;
            this.begin();
        }
        this.fixupMessages = false;
        return this.getProperties0();
    }

    private Properties getProperties0() {
        return this.properties;
    }

    public String getProperty(String key) {
        return this.getProperty(key, null);
    }

    public void mergeProperties(File file, boolean override) {
        if (file.isFile()) {
            try {
                Properties properties = this.loadProperties(file);
                this.mergeProperties(properties, override);
            }
            catch (Exception e) {
                this.error("Error loading properties file: %s", file);
            }
        } else if (!file.exists()) {
            this.error("Properties file does not exist: %s", file);
        } else {
            this.error("Properties file must a file, not a directory: %s", file);
        }
    }

    public void mergeProperties(Properties properties, boolean override) {
        Enumeration<?> e = properties.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            String value = properties.getProperty(key);
            if (!override && this.getProperties().containsKey(key)) continue;
            this.setProperty(key, value);
        }
    }

    public void setProperties(Properties properties) {
        this.doIncludes(this.getBase(), properties);
        this.getProperties0().putAll((Map<?, ?>)properties);
        this.mergeProperties("-init");
        this.getProperties0().remove("-init");
    }

    public void setProperties(File base, Properties properties) {
        this.doIncludes(base, properties);
        this.getProperties0().putAll((Map<?, ?>)properties);
    }

    public void addProperties(File file) throws Exception {
        this.addIncluded(file);
        Properties p = this.loadProperties(file);
        this.setProperties(p);
    }

    public void addProperties(Map<?, ?> properties) {
        for (Map.Entry<?, ?> entry : properties.entrySet()) {
            this.setProperty(entry.getKey().toString(), entry.getValue() + "");
        }
    }

    public synchronized void addIncluded(File file) {
        if (this.included == null) {
            this.included = new ArrayList<File>();
        }
        this.included.add(file);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doIncludes(File ubase, Properties p) {
        String includes = p.getProperty("-include");
        if (includes != null) {
            includes = this.getReplacer().process(includes);
            p.remove("-include");
            Set<String> clauses = new Parameters(includes, this).keySet();
            for (String value : clauses) {
                boolean fileMustExist = true;
                boolean overwrite = true;
                while (true) {
                    if (value.startsWith("-")) {
                        fileMustExist = false;
                        value = value.substring(1).trim();
                        continue;
                    }
                    if (!value.startsWith("~")) break;
                    overwrite = false;
                    value = value.substring(1).trim();
                }
                try {
                    File file = Processor.getFile(ubase, value).getAbsoluteFile();
                    if (!file.isFile()) {
                        try {
                            URL url = new URL(value);
                            int n = value.lastIndexOf(46);
                            String ext = ".jar";
                            if (n >= 0) {
                                ext = value.substring(n);
                            }
                            File tmp = File.createTempFile("url", ext);
                            try {
                                IO.copy(url.openStream(), tmp);
                                this.doIncludeFile(tmp, overwrite, p);
                            }
                            finally {
                                IO.delete(tmp);
                            }
                        }
                        catch (MalformedURLException mue) {
                            if (!fileMustExist) continue;
                            this.error("Included file %s %s", file, file.isDirectory() ? "is directory" : "does not exist");
                        }
                        catch (Exception e) {
                            if (!fileMustExist) continue;
                            this.exception(e, "Error in processing included URL: %s", value);
                        }
                        continue;
                    }
                    this.doIncludeFile(file, overwrite, p);
                }
                catch (Exception e) {
                    if (!fileMustExist) continue;
                    this.exception(e, "Error in processing included file: %s", value);
                }
            }
        }
    }

    public void doIncludeFile(File file, boolean overwrite, Properties target) throws Exception {
        this.doIncludeFile(file, overwrite, target, null);
    }

    public void doIncludeFile(File file, boolean overwrite, Properties target, String extensionName) throws Exception {
        if (this.included != null && this.included.contains(file)) {
            this.error("Cyclic or multiple include of %s", file);
        } else {
            Properties sub;
            this.addIncluded(file);
            this.updateModified(file.lastModified(), file.toString());
            if (file.getName().toLowerCase().endsWith(".mf")) {
                try (InputStream in = IO.stream(file);){
                    sub = Processor.getManifestAsProperties(in);
                }
            } else {
                sub = this.loadProperties(file);
            }
            this.doIncludes(file.getParentFile(), sub);
            for (Map.Entry<Object, Object> entry : sub.entrySet()) {
                String extensionKey;
                String key = (String)entry.getKey();
                String value = (String)entry.getValue();
                if (overwrite || !target.containsKey(key)) {
                    target.setProperty(key, value);
                    continue;
                }
                if (extensionName == null || target.containsKey(extensionKey = extensionName + "." + key)) continue;
                target.setProperty(extensionKey, value);
            }
        }
    }

    public void unsetProperty(String string) {
        this.getProperties().remove(string);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean refresh() {
        Processor processor = this;
        synchronized (processor) {
            this.plugins = null;
        }
        if (this.propertiesFile == null) {
            return false;
        }
        boolean changed = this.updateModified(this.propertiesFile.lastModified(), "properties file");
        if (this.included != null) {
            for (File file : this.included) {
                if (changed) break;
                changed |= !file.exists() || this.updateModified(file.lastModified(), "include file: " + file);
            }
        }
        this.profile = this.getProperty("-profile");
        if (changed) {
            this.forceRefresh();
            return true;
        }
        return false;
    }

    boolean isStrict() {
        if (this.strict == null) {
            this.strict = Processor.isTrue(this.getProperty("-strict"));
        }
        return this.strict;
    }

    public void forceRefresh() {
        this.included = null;
        Processor p = this.getParent();
        this.properties = p != null ? new UTF8Properties(p.getProperties0()) : new UTF8Properties();
        this.setProperties(this.propertiesFile, this.base);
        this.propertiesChanged();
    }

    public void propertiesChanged() {
    }

    public void setProperties(File propertiesFile) throws IOException {
        propertiesFile = propertiesFile.getAbsoluteFile();
        this.setProperties(propertiesFile, propertiesFile.getParentFile());
    }

    public void setProperties(File propertiesFile, File base) {
        this.propertiesFile = propertiesFile.getAbsoluteFile();
        this.setBase(base);
        try {
            if (propertiesFile.isFile()) {
                long modified = propertiesFile.lastModified();
                if (modified > System.currentTimeMillis() + 100L) {
                    System.err.println("Huh? This is in the future " + propertiesFile);
                    this.modified = System.currentTimeMillis();
                } else {
                    this.modified = modified;
                }
                this.included = null;
                Properties p = this.loadProperties(propertiesFile);
                this.setProperties(p);
            } else if (this.fileMustExist) {
                this.error("No such properties file: %s", propertiesFile);
            }
        }
        catch (IOException e) {
            this.error("Could not load properties %s", propertiesFile);
        }
    }

    protected void begin() {
        if (Processor.isTrue(this.getProperty("-pedantic"))) {
            this.setPedantic(true);
        }
    }

    public static boolean isTrue(String value) {
        if (value == null) {
            return false;
        }
        if ((value = value.trim()).isEmpty()) {
            return false;
        }
        if (value.startsWith("!")) {
            if (value.equals("!")) {
                return false;
            }
            return !Processor.isTrue(value.substring(1));
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        if ("off".equalsIgnoreCase(value)) {
            return false;
        }
        return !"not".equalsIgnoreCase(value);
    }

    public String getUnprocessedProperty(String key, String deflt) {
        return this.getProperties().getProperty(key, deflt);
    }

    public String getProperty(String key, String deflt) {
        return this.getProperty(key, deflt, ",");
    }

    public String getProperty(String key, String deflt, String separator) {
        return this.getProperty(key, deflt, separator, true);
    }

    private String getProperty(String key, String deflt, String separator, boolean inherit) {
        Instruction ins = new Instruction(key);
        if (!ins.isLiteral()) {
            return this.getWildcardProperty(deflt, separator, inherit, ins);
        }
        Processor source = this;
        return this.getLiteralProperty(key, deflt, source, inherit);
    }

    private String getWildcardProperty(String deflt, String separator, boolean inherit, Instruction ins) {
        SortedList<String> sortedList = SortedList.fromIterator(this.iterator(inherit));
        StringBuilder sb = new StringBuilder();
        String del = "";
        for (String k : sortedList) {
            String v;
            if (!ins.matches(k) || (v = this.getLiteralProperty(k, null, this, inherit)) == null || v.isEmpty()) continue;
            sb.append(del);
            del = separator;
            sb.append(v);
        }
        if (sb.length() == 0) {
            return deflt;
        }
        return sb.toString();
    }

    private String getLiteralProperty(String key, String deflt, Processor source, boolean inherit) {
        String value = null;
        if (this.filter != null && this.filter.contains(key)) {
            Object raw = this.getProperties().get(key);
            if (raw != null) {
                if (raw instanceof String) {
                    value = (String)raw;
                } else {
                    this.warning("Key '%s' has a non-String value: %s:%s", key, raw == null ? "" : raw.getClass().getName(), raw);
                }
            }
        } else {
            while (source != null) {
                Object raw = source.getProperties().get(key);
                if (raw != null) {
                    if (raw instanceof String) {
                        value = (String)raw;
                        break;
                    }
                    this.warning("Key '%s' has a non-String value: %s:%s", key, raw == null ? "" : raw.getClass().getName(), raw);
                    break;
                }
                if (!inherit) break;
                source = source.getParent();
            }
            if (value == null) {
                value = this.getReplacer().getMacro(key, null);
            }
        }
        if (value != null) {
            return this.getReplacer().process(value, source);
        }
        if (deflt != null) {
            return this.getReplacer().process(deflt, this);
        }
        return null;
    }

    public Properties loadProperties(File file) throws IOException {
        this.updateModified(file.lastModified(), "Properties file: " + file);
        UTF8Properties p = this.loadProperties0(file);
        return p;
    }

    UTF8Properties loadProperties0(File file) throws IOException {
        String name = file.toURI().getPath();
        int n = name.lastIndexOf(47);
        if (n > 0) {
            name = name.substring(0, n);
        }
        if (name.length() == 0) {
            name = ".";
        }
        try {
            UTF8Properties p = new UTF8Properties();
            p.load(file, this);
            return p.replaceAll("\\$\\{\\.\\}", Matcher.quoteReplacement(name));
        }
        catch (Exception e) {
            this.error("Error during loading properties file: %s, error: %s", name, e);
            return new UTF8Properties();
        }
    }

    public static Properties replaceAll(Properties p, String pattern, String replacement) {
        UTF8Properties result = new UTF8Properties();
        Pattern regex = Pattern.compile(pattern);
        for (Map.Entry<Object, Object> entry : p.entrySet()) {
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            value = regex.matcher(value).replaceAll(replacement);
            result.put(key, value);
        }
        return result;
    }

    public static String printClauses(Map<?, ? extends Map<?, ?>> exports) throws IOException {
        return Processor.printClauses(exports, false);
    }

    public static String printClauses(Map<?, ? extends Map<?, ?>> exports, boolean checkMultipleVersions) throws IOException {
        StringBuilder sb = new StringBuilder();
        String del = "";
        for (Map.Entry<?, Map<?, ?>> entry : exports.entrySet()) {
            String name = entry.getKey().toString();
            Map<?, ?> clause = entry.getValue();
            String outname = Processor.removeDuplicateMarker(name);
            sb.append(del);
            sb.append(outname);
            Processor.printClause(clause, sb);
            del = ",";
        }
        return sb.toString();
    }

    public static void printClause(Map<?, ?> map, StringBuilder sb) throws IOException {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            if (key.equals("-internal-source:") || key.equals("-internal-exported:") || key.equals("-noimport:") || key.equals("provide:") || key.equals("-split-package:") || key.equals("from:")) continue;
            String value = ((String)entry.getValue()).trim();
            sb.append(";");
            sb.append(key);
            sb.append("=");
            Processor.quote(sb, value);
        }
    }

    public static boolean quote(Appendable sb, String value) throws IOException {
        return OSGiHeader.quote(sb, value);
    }

    public Macro getReplacer() {
        if (this.replacer == null) {
            this.replacer = new Macro(this, this.getMacroDomains());
            return this.replacer;
        }
        return this.replacer;
    }

    protected Object[] getMacroDomains() {
        return new Object[0];
    }

    public Properties getFlattenedProperties() {
        return this.getReplacer().getFlattenedProperties();
    }

    public Properties getFlattenedProperties(boolean ignoreInstructions) {
        return this.getReplacer().getFlattenedProperties(ignoreInstructions);
    }

    public Set<String> getPropertyKeys(boolean inherit) {
        Set<Object> result = this.parent == null || !inherit ? Create.set() : this.parent.getPropertyKeys(inherit);
        for (Object o : this.getProperties0().keySet()) {
            result.add(o.toString());
        }
        return result;
    }

    public boolean updateModified(long time, String reason) {
        if (time > this.lastModified) {
            this.lastModified = time;
            return true;
        }
        return false;
    }

    public long lastModified() {
        return this.lastModified;
    }

    public void setProperty(String key, String value) {
        for (int i = 0; i < headers.length; ++i) {
            if (!headers[i].equalsIgnoreCase(key)) continue;
            key = headers[i];
            break;
        }
        this.getProperties().put(key, value);
    }

    public static Properties getManifestAsProperties(InputStream in) throws IOException {
        UTF8Properties p = new UTF8Properties();
        Manifest manifest = new Manifest(in);
        for (Attributes.Name name : manifest.getMainAttributes().keySet()) {
            String value = manifest.getMainAttributes().getValue(name);
            p.put(name.toString(), value);
        }
        return p;
    }

    public File getPropertiesFile() {
        return this.propertiesFile;
    }

    public void setFileMustExist(boolean mustexist) {
        this.fileMustExist = mustexist;
    }

    public static String read(InputStream in) throws Exception {
        return IO.collect(in, StandardCharsets.UTF_8);
    }

    public static String join(Collection<?> list, String delimeter) {
        return Processor.join(delimeter, list);
    }

    public static String join(String delimeter, Collection<?> ... list) {
        StringBuilder sb = new StringBuilder();
        String del = "";
        if (list != null) {
            for (Collection<?> l : list) {
                for (Object item : l) {
                    sb.append(del);
                    sb.append(item);
                    del = delimeter;
                }
            }
        }
        return sb.toString();
    }

    public static String join(Object[] list, String delimeter) {
        if (list == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String del = "";
        for (Object item : list) {
            sb.append(del);
            sb.append(item);
            del = delimeter;
        }
        return sb.toString();
    }

    public static String join(Collection<?> ... list) {
        return Processor.join(",", list);
    }

    public static <T> String join(T[] list) {
        return Processor.join(list, ",");
    }

    public static void split(String s, Collection<String> set) {
        String[] elements;
        for (String element : elements = s.trim().split(LIST_SPLITTER)) {
            if (element.length() <= 0) continue;
            set.add(element);
        }
    }

    public static Collection<String> split(String s) {
        return Processor.split(s, LIST_SPLITTER);
    }

    public static Collection<String> split(String s, String splitter) {
        if (s != null) {
            s = s.trim();
        }
        if (s == null || s.trim().length() == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(s.split(splitter));
    }

    public static String merge(String ... strings) {
        ArrayList<String> result = new ArrayList<String>();
        for (String s : strings) {
            if (s == null) continue;
            Processor.split(s, result);
        }
        return Processor.join(result);
    }

    public boolean isExceptions() {
        return this.exceptions;
    }

    public void setExceptions(boolean exceptions) {
        this.exceptions = exceptions;
    }

    public String normalize(String f) {
        if (f.startsWith(this.base.getAbsolutePath() + "/")) {
            return f.substring(this.base.getAbsolutePath().length() + 1);
        }
        return f;
    }

    public String normalize(File f) {
        return this.normalize(f.getAbsolutePath());
    }

    public static String removeDuplicateMarker(String key) {
        int i;
        for (i = key.length() - 1; i >= 0 && key.charAt(i) == '~'; --i) {
        }
        return key.substring(0, i + 1);
    }

    public static boolean isDuplicate(String name) {
        return name.length() > 0 && name.charAt(name.length() - 1) == '~';
    }

    public void setTrace(boolean x) {
        this.trace = x;
    }

    protected CL getLoader() {
        if (this.pluginLoader == null) {
            this.pluginLoader = new CL(this);
        }
        return this.pluginLoader;
    }

    public boolean exists() {
        return this.base != null && this.base.isDirectory() && this.propertiesFile != null && this.propertiesFile.isFile();
    }

    @Override
    public boolean isOk() {
        return this.isFailOk() || this.getErrors().size() == 0;
    }

    private void fixupMessages() {
        if (this.fixupMessages) {
            return;
        }
        this.fixupMessages = true;
        Parameters fixup = this.getMergedParameters("-fixupmessages");
        if (fixup.isEmpty()) {
            return;
        }
        Instructions instrs = new Instructions(fixup);
        this.doFixup(instrs, this.errors, this.warnings, "error");
        this.doFixup(instrs, this.warnings, this.errors, "warning");
    }

    private void doFixup(Instructions instrs, List<String> messages, List<String> other, String type) {
        for (int i = 0; i < messages.size(); ++i) {
            Attrs attrs;
            String restrict;
            String message = messages.get(i);
            Instruction matcher = instrs.finder(message);
            if (matcher == null || matcher.isNegated() || (restrict = (attrs = instrs.get(matcher)).get("restrict:")) != null && !restrict.equals(type)) continue;
            String replace = attrs.get("replace:");
            if (replace != null) {
                logger.debug("replacing {} with {}", (Object)message, (Object)replace);
                this.setProperty("@", message);
                message = this.getReplacer().process(replace);
                messages.set(i, message);
                this.unsetProperty("@");
            }
            String is = attrs.get("is:");
            if (attrs.isEmpty() || "ignore".equals(is)) {
                messages.remove(i--);
                continue;
            }
            if (is == null || type.equals(is)) continue;
            messages.remove(i--);
            other.add(message);
        }
    }

    public boolean check(String ... pattern) throws IOException {
        Set missed = Create.set();
        if (pattern != null) {
            for (String p : pattern) {
                boolean match = false;
                Pattern pat = Pattern.compile(p);
                Iterator<String> i = this.errors.iterator();
                while (i.hasNext()) {
                    if (!pat.matcher(i.next()).find()) continue;
                    i.remove();
                    match = true;
                }
                i = this.warnings.iterator();
                while (i.hasNext()) {
                    if (!pat.matcher(i.next()).find()) continue;
                    i.remove();
                    match = true;
                }
                if (match) continue;
                missed.add(p);
            }
        }
        if (missed.isEmpty() && this.isPerfect()) {
            return true;
        }
        if (!missed.isEmpty()) {
            System.err.println("Missed the following patterns in the warnings or errors: " + missed);
        }
        this.report(System.err);
        return false;
    }

    protected void report(Appendable out) throws IOException {
        int i;
        if (this.errors.size() > 0) {
            out.append(String.format("-----------------%nErrors%n", new Object[0]));
            for (i = 0; i < this.errors.size(); ++i) {
                out.append(String.format("%03d: %s%n", i, this.errors.get(i)));
            }
        }
        if (this.warnings.size() > 0) {
            out.append(String.format("-----------------%nWarnings%n", new Object[0]));
            for (i = 0; i < this.warnings.size(); ++i) {
                out.append(String.format("%03d: %s%n", i, this.warnings.get(i)));
            }
        }
    }

    public boolean isPerfect() {
        return this.getErrors().size() == 0 && this.getWarnings().size() == 0;
    }

    public void setForceLocal(Collection<String> local) {
        this.filter = local;
    }

    public boolean isMissingPlugin(String name) {
        this.getPlugins();
        return this.missingCommand != null && this.missingCommand.contains(name);
    }

    public static String appendPath(String ... parts) {
        StringBuilder sb = new StringBuilder();
        boolean lastSlash = true;
        for (String part : parts) {
            for (int i = 0; i < part.length(); ++i) {
                char c = part.charAt(i);
                if (c == '/') {
                    if (!lastSlash) {
                        sb.append('/');
                    }
                    lastSlash = true;
                    continue;
                }
                sb.append(c);
                lastSlash = false;
            }
            if (lastSlash || sb.length() <= 0) continue;
            sb.append('/');
            lastSlash = true;
        }
        if (lastSlash && sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static Attrs doAttrbutes(Object[] attrs, Clazz clazz, Macro macro) {
        Attrs map = new Attrs();
        if (attrs == null || attrs.length == 0) {
            return map;
        }
        for (Object a : attrs) {
            String attr = (String)a;
            int n = attr.indexOf(61);
            if (n <= 0) {
                throw new IllegalArgumentException(Processor.formatArrays("Invalid attribute on package-info.java in %s , %s. Must be <key>=<name> ", clazz, attr));
            }
            map.put(attr.substring(0, n), macro.process(attr.substring(n + 1)));
        }
        return map;
    }

    public static String formatArrays(String string, Object ... parms) {
        return Strings.format(string, parms);
    }

    public static Object makePrintable(Object object) {
        if (object == null) {
            return null;
        }
        if (object.getClass().isArray()) {
            return Arrays.toString(Processor.makePrintableArray(object));
        }
        return object;
    }

    private static Object[] makePrintableArray(Object array) {
        int length = Array.getLength(array);
        Object[] output = new Object[length];
        for (int i = 0; i < length; ++i) {
            output[i] = Processor.makePrintable(Array.get(array, i));
        }
        return output;
    }

    public static String append(String ... strings) {
        List result = Create.list();
        for (String s : strings) {
            result.addAll(Processor.split(s));
        }
        return Processor.join(result);
    }

    public synchronized Class<?> getClass(String type, File jar) throws Exception {
        CL cl = this.getLoader();
        cl.add(jar.toURI().toURL());
        return cl.loadClass(type);
    }

    public boolean isTrace() {
        return this.current().trace;
    }

    public static long getDuration(String tm, long dflt) {
        if (tm == null) {
            return dflt;
        }
        tm = tm.toUpperCase();
        TimeUnit unit = TimeUnit.MILLISECONDS;
        Matcher m = Pattern.compile("\\s*(\\d+)\\s*(NANOSECONDS|MICROSECONDS|MILLISECONDS|SECONDS|MINUTES|HOURS|DAYS)?").matcher(tm);
        if (m.matches()) {
            long duration = Long.parseLong(tm);
            String u = m.group(2);
            if (u != null) {
                unit = TimeUnit.valueOf(u);
            }
            duration = TimeUnit.MILLISECONDS.convert(duration, unit);
            return duration;
        }
        return dflt;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String _random(String[] args) {
        int numchars = 8;
        if (args.length > 1) {
            try {
                numchars = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid character count parameter in ${random} macro.");
            }
        }
        Class<Processor> e = Processor.class;
        synchronized (Processor.class) {
            if (random == null) {
                random = new Random();
            }
            // ** MonitorExit[e] (shouldn't be in output)
            char[] letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
            char[] alphanums = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
            char[] array = new char[numchars];
            for (int i = 0; i < numchars; ++i) {
                char c = i == 0 ? letters[random.nextInt(letters.length)] : alphanums[random.nextInt(alphanums.length)];
                array[i] = c;
            }
            return new String(array);
        }
    }

    public String _native_capability(String ... args) throws Exception {
        return OSInformation.getNativeCapabilityClause(this, args);
    }

    protected Processor beginHandleErrors(String message) {
        logger.debug("begin {}", (Object)message);
        Processor previous = current.get();
        current.set(this);
        return previous;
    }

    protected void endHandleErrors(Processor previous) {
        logger.debug("end");
        current.set(previous);
    }

    public static Executor getExecutor() {
        return executor;
    }

    public static ScheduledExecutorService getScheduledExecutor() {
        return sheduledExecutor;
    }

    public synchronized void addBasicPlugin(Object plugin) {
        this.basicPlugins.add(plugin);
        Set<Object> p = this.plugins;
        if (p != null) {
            p.add(plugin);
        }
    }

    public synchronized void removeBasicPlugin(Object plugin) {
        this.basicPlugins.remove(plugin);
        Set<Object> p = this.plugins;
        if (p != null) {
            p.remove(plugin);
        }
    }

    public List<File> getIncluded() {
        return this.included;
    }

    @Override
    public String get(String key) {
        return this.getProperty(key);
    }

    @Override
    public String get(String key, String deflt) {
        return this.getProperty(key, deflt);
    }

    @Override
    public void set(String key, String value) {
        this.getProperties().setProperty(key, value);
    }

    @Override
    public Iterator<String> iterator() {
        return this.iterator(true);
    }

    private Iterator<String> iterator(boolean inherit) {
        Set<String> keys = this.getPropertyKeys(inherit);
        final Iterator<String> it = keys.iterator();
        return new Iterator<String>(){
            String current;

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public String next() {
                this.current = (String)it.next();
                return this.current;
            }

            @Override
            public void remove() {
                Processor.this.getProperties().remove(this.current);
            }
        };
    }

    public Set<String> keySet() {
        return this.getPropertyKeys(true);
    }

    public String toString() {
        try {
            StringBuilder sb = new StringBuilder();
            this.report(sb);
            return sb.toString();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String replaceExtension(String s, String extension, String newExtension) {
        if (s.endsWith(extension)) {
            s = s.substring(0, s.length() - extension.length());
        }
        return s + newExtension;
    }

    private Reporter.SetLocation location(String s) {
        SetLocationImpl loc = new SetLocationImpl(s);
        this.locations.add(loc);
        return loc;
    }

    @Override
    public Report.Location getLocation(String msg) {
        for (Report.Location l : this.locations) {
            if (l.message == null || !l.message.equals(msg)) continue;
            return l;
        }
        return null;
    }

    public FileLine getHeader(String header) throws Exception {
        return this.getHeader(Pattern.compile("^[ \t]*" + Pattern.quote(header), 10));
    }

    public static Pattern toFullHeaderPattern(String header) {
        StringBuilder sb = new StringBuilder();
        sb.append("^[ \t]*(").append(header).append(")(\\.[^\\s:=]*)?[ \t]*[ \t:=][ \t]*");
        sb.append("[^\\\\\n\r]*(\\\\\n[^\\\\\n\r]*)*");
        try {
            return Pattern.compile(sb.toString(), 10);
        }
        catch (Exception e) {
            return Pattern.compile("^[ \t]*" + Pattern.quote(header), 10);
        }
    }

    public FileLine getHeader(Pattern header) throws Exception {
        return this.getHeader(header, null);
    }

    public FileLine getHeader(String header, String clause) throws Exception {
        return this.getHeader(Processor.toFullHeaderPattern(header), clause == null ? null : Pattern.compile(Pattern.quote(clause)));
    }

    public FileLine getHeader(Pattern header, Pattern clause) throws Exception {
        FileLine fl = this.getHeader0(header, clause);
        if (fl != null) {
            return fl;
        }
        Processor rover = this;
        while (rover.getPropertiesFile() == null) {
            if (rover.parent == null) {
                return new FileLine(new File("ANONYMOUS"), 0, 0);
            }
            rover = rover.parent;
        }
        return new FileLine(rover.getPropertiesFile(), 0, 0);
    }

    private FileLine getHeader0(Pattern header, Pattern clause) throws Exception {
        FileLine fl;
        File f = this.getPropertiesFile();
        if (f != null) {
            fl = Processor.findHeader(f, header, clause);
            if (fl != null) {
                return fl;
            }
            List<File> result = this.getIncluded();
            if (result != null) {
                ExtList<File> reversed = new ExtList<File>((Collection<File>)result);
                Collections.reverse(reversed);
                for (File included : reversed) {
                    fl = Processor.findHeader(included, header);
                    if (fl == null) continue;
                    return fl;
                }
            }
        }
        if (this.getParent() != null && (fl = this.getParent().getHeader(header, clause)) != null) {
            return fl;
        }
        if (f == null && this.parent != null) {
            f = this.parent.getPropertiesFile();
        }
        if (f == null) {
            return null;
        }
        return new FileLine(f, 0, 0);
    }

    public static FileLine findHeader(File f, String header) throws IOException {
        return Processor.findHeader(f, Pattern.compile("^[ \t]*" + Pattern.quote(header), 10));
    }

    public static FileLine findHeader(File f, Pattern header) throws IOException {
        return Processor.findHeader(f, header, null);
    }

    public static FileLine findHeader(File f, Pattern header, Pattern clause) throws IOException {
        if (f.isFile()) {
            String s = IO.collect(f);
            Matcher matcher = header.matcher(s);
            while (matcher.find()) {
                FileLine fl = new FileLine();
                fl.file = f;
                fl.start = matcher.start();
                fl.end = matcher.end();
                fl.length = fl.end - fl.start;
                fl.line = Processor.getLine(s, fl.start);
                if (clause != null) {
                    Matcher mclause = clause.matcher(s);
                    mclause.region(fl.start, fl.end);
                    if (!mclause.find()) continue;
                    fl.start = mclause.start();
                    fl.end = mclause.end();
                }
                return fl;
            }
        }
        return null;
    }

    public static int getLine(String s, int index) {
        int n = 0;
        while (--index > 0) {
            char c = s.charAt(index);
            if (c != '\n') continue;
            ++n;
        }
        return n;
    }

    public boolean since(Version introduced) {
        if (this.upto == null) {
            String uptov = this.getProperty("-upto");
            if (uptov == null) {
                this.upto = Version.HIGHEST;
                return true;
            }
            if (!Version.VERSION.matcher(uptov).matches()) {
                this.error("The %s given version is not a version: %s", "-upto", uptov);
                this.upto = Version.HIGHEST;
                return true;
            }
            this.upto = new Version(uptov);
        }
        return this.upto.compareTo(introduced) >= 0;
    }

    public void report(Map<String, Object> table) throws Exception {
        table.put("Included Files", this.getIncluded());
        table.put("Base", this.getBase());
        table.put("Properties", this.getProperties0().entrySet());
    }

    public boolean is(String propertyName) {
        return Processor.isTrue(this.getProperty(propertyName));
    }

    public String mergeProperties(String key) {
        return this.mergeProperties(key, ",");
    }

    public String mergeLocalProperties(String key) {
        if (this.since(About._3_3)) {
            return this.getProperty(this.makeWildcard(key), null, ",", false);
        }
        return this.mergeProperties(key);
    }

    public String mergeProperties(String key, String separator) {
        if (this.since(About._2_4)) {
            return this.getProperty(this.makeWildcard(key), null, separator, true);
        }
        return this.getProperty(key);
    }

    private String makeWildcard(String key) {
        return key + "|" + key + ".*";
    }

    public Parameters getMergedParameters(String key) {
        return new Parameters(this.mergeProperties(key), this);
    }

    public <T> T[] concat(Class<T> type, T[] prefix, T suffix) {
        Object[] result = (Object[])Array.newInstance(type, (prefix != null ? prefix.length : 0) + 1);
        if (result.length > 1) {
            System.arraycopy(prefix, 0, result, 0, result.length - 1);
        }
        result[result.length - 1] = suffix;
        return result;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Jar getJarFromName(String name, String from) {
        File file = new File(name);
        if (!file.isAbsolute()) {
            file = new File(this.getBase(), name);
        }
        if (file.exists()) {
            try {
                Jar jar2 = new Jar(file);
                this.addClose(jar2);
                return jar2;
            }
            catch (Exception e) {
                this.error("Exception in parsing jar file for %s: %s %s", from, name, e);
            }
        }
        try {
            URL url = new URL(name);
            URLConnection connection = url.openConnection();
            try (InputStream in = connection.getInputStream();){
                long lastModified = connection.getLastModified();
                if (lastModified == 0L) {
                    lastModified = System.currentTimeMillis();
                }
                Jar jar3 = new Jar(this.fileName(url.getPath()), in, lastModified);
                this.addClose(jar3);
                Jar jar = jar3;
                return jar;
            }
        }
        catch (IOException ee) {
            return null;
        }
    }

    private String fileName(String path) {
        int n = path.lastIndexOf(47);
        if (n > 0) {
            return path.substring(n + 1);
        }
        return path;
    }

    public String _thisfile(String[] args) {
        if (this.propertiesFile == null) {
            this.error("${thisfile} executed on a processor without a properties file", new Object[0]);
            return null;
        }
        return this.propertiesFile.getAbsolutePath().replaceAll("\\\\", "/");
    }

    public void getSettings(Processor p) {
        this.trace = p.isTrace();
        this.pedantic = p.isPedantic();
        this.exceptions = p.isExceptions();
    }

    public String _frange(String[] args) {
        VersionRange vr;
        boolean isProvider;
        if (args.length < 2 || args.length > 3) {
            this.error("Invalid filter range, 2 or 3 args ${frange;<version>[;true|false]}", new Object[0]);
            return null;
        }
        String v = args[1];
        boolean bl = isProvider = args.length == 3 && Processor.isTrue(args[2]);
        if (Verifier.isVersion(v)) {
            Version l = new Version(v);
            Version h = isProvider ? new Version(l.getMajor(), l.getMinor() + 1, 0) : new Version(l.getMajor() + 1, 0, 0);
            vr = new VersionRange(true, l, h, false);
        } else if (Verifier.isVersionRange(v)) {
            vr = new VersionRange(v);
        } else {
            this.error("The _frange parameter %s is neither a version nor a version range", v);
            return null;
        }
        return vr.toFilter();
    }

    public String _findfile(String[] args) {
        File f = this.getFile(args[1]);
        ArrayList<String> files = new ArrayList<String>();
        this.tree(files, f, "", new Instruction(args[2]));
        return Processor.join(files);
    }

    void tree(List<String> list, File current, String path, Instruction instr) {
        String[] subs;
        if (path.length() > 0) {
            path = path + "/";
        }
        if ((subs = current.list()) != null) {
            for (String sub : subs) {
                File f = new File(current, sub);
                if (f.isFile()) {
                    if (!instr.matches(sub) || instr.isNegated()) continue;
                    list.add(path + sub);
                    continue;
                }
                this.tree(list, f, path + sub, instr);
            }
        }
    }

    static {
        ReporterAdapter reporterAdapter = new ReporterAdapter(System.out);
        reporterAdapter.setTrace(true);
        reporterAdapter.setExceptions(true);
        reporterAdapter.setPedantic(true);
        log = reporterAdapter;
        PACKAGES_IGNORED = Pattern.compile("(java\\.lang\\.reflect|sun\\.reflect).*");
        current = new ThreadLocal();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        executor = new ThreadPoolExecutor(0, 64, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory, new RejectedExecutionHandler(){

            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                if (executor.isShutdown()) {
                    return;
                }
                try {
                    r.run();
                }
                catch (Throwable t) {
                    try {
                        Thread thread = Thread.currentThread();
                        thread.getUncaughtExceptionHandler().uncaughtException(thread, t);
                    }
                    catch (Throwable for_real) {
                        // empty catch block
                    }
                }
            }
        });
        sheduledExecutor = new ScheduledThreadPoolExecutor(4, threadFactory);
        random = new Random();
        _uri = "${uri;<uri>[;<baseuri>]}, Resolve the uri against the baseuri. baseuri defaults to the processor base.";
        _fileuri = "${fileuri;<path>}, Return a file uri for the specified path. Relative paths are resolved against the processor base.";
    }

    static class SetLocationImpl
    extends Report.Location
    implements Reporter.SetLocation {
        public SetLocationImpl(String s) {
            this.message = s;
        }

        @Override
        public Reporter.SetLocation file(String file) {
            this.file = file;
            return this;
        }

        @Override
        public Reporter.SetLocation header(String header) {
            this.header = header;
            return this;
        }

        @Override
        public Reporter.SetLocation context(String context) {
            this.context = context;
            return this;
        }

        @Override
        public Reporter.SetLocation method(String methodName) {
            this.methodName = methodName;
            return this;
        }

        @Override
        public Reporter.SetLocation line(int n) {
            this.line = n;
            return this;
        }

        @Override
        public Reporter.SetLocation reference(String reference) {
            this.reference = reference;
            return this;
        }

        @Override
        public Reporter.SetLocation details(Object details) {
            this.details = details;
            return this;
        }

        @Override
        public Report.Location location() {
            return this;
        }

        @Override
        public Reporter.SetLocation length(int length) {
            this.length = length;
            return this;
        }
    }

    public static class CL
    extends URLClassLoader {
        CL(Processor p) {
            super(new URL[0], p.getClass().getClassLoader());
        }

        void closex() {
            Class<URLClassLoader> clazz = URLClassLoader.class;
            try {
                clazz.getMethod("close", new Class[0]).invoke((Object)this, new Object[0]);
                return;
            }
            catch (Exception e) {
                try {
                    Field ucpField = clazz.getDeclaredField("ucp");
                    ucpField.setAccessible(true);
                    Object cp = ucpField.get(this);
                    Field loadersField = cp.getClass().getDeclaredField("loaders");
                    loadersField.setAccessible(true);
                    Collection loaders = (Collection)loadersField.get(cp);
                    for (Object loader : loaders) {
                        try {
                            Field loaderField = loader.getClass().getDeclaredField("jar");
                            loaderField.setAccessible(true);
                            JarFile jarFile = (JarFile)loaderField.get(loader);
                            jarFile.close();
                        }
                        catch (Throwable t) {}
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                return;
            }
        }

        void add(URL url) {
            URL[] urls;
            for (URL u : urls = this.getURLs()) {
                if (!u.equals(url)) continue;
                return;
            }
            super.addURL(url);
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            try {
                Class<?> c = super.loadClass(name);
                return c;
            }
            catch (Throwable t) {
                StringBuilder sb = new StringBuilder();
                sb.append(name);
                sb.append(" not found, parent: ");
                sb.append(this.getParent());
                sb.append(" urls:");
                sb.append(Arrays.toString(this.getURLs()));
                sb.append(" exception:");
                sb.append(Exceptions.toString(t));
                throw new ClassNotFoundException(sb.toString(), t);
            }
        }
    }

    public static class FileLine {
        public static final FileLine DUMMY = new FileLine(null, 0, 0);
        public File file;
        public int line;
        public int length;
        public int start;
        public int end;

        public FileLine() {
        }

        public FileLine(File file, int line, int length) {
            this.file = file;
            this.line = line;
            this.length = length;
        }

        public void set(Reporter.SetLocation sl) {
            sl.file(this.file.getAbsolutePath());
            sl.line(this.line);
            sl.length(this.length);
        }
    }
}

