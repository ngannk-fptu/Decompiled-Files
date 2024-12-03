/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyResourceLoader;
import groovyjarjarasm.asm.ClassVisitor;
import groovyjarjarasm.asm.ClassWriter;
import groovyjarjarasm.asm.Opcodes;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.classgen.Verifier;
import org.codehaus.groovy.control.BytecodeProcessor;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.IOGroovyMethods;

public class GroovyClassLoader
extends URLClassLoader {
    private static final URL[] EMPTY_URL_ARRAY = new URL[0];
    protected final Map<String, Class> classCache = new HashMap<String, Class>();
    protected final Map<String, Class> sourceCache = new HashMap<String, Class>();
    private final CompilerConfiguration config;
    private Boolean recompile;
    private static int scriptNameCounter = 1000000;
    private GroovyResourceLoader resourceLoader = new GroovyResourceLoader(){

        @Override
        public URL loadGroovySource(final String filename) throws MalformedURLException {
            return AccessController.doPrivileged(new PrivilegedAction<URL>(){

                @Override
                public URL run() {
                    for (String extension : GroovyClassLoader.this.config.getScriptExtensions()) {
                        try {
                            URL ret = GroovyClassLoader.this.getSourceFile(filename, extension);
                            if (ret == null) continue;
                            return ret;
                        }
                        catch (Throwable throwable) {
                        }
                    }
                    return null;
                }
            });
        }
    };

    public GroovyClassLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public GroovyClassLoader(ClassLoader loader) {
        this(loader, null);
    }

    public GroovyClassLoader(GroovyClassLoader parent) {
        this(parent, parent.config, false);
    }

    public GroovyClassLoader(ClassLoader parent, CompilerConfiguration config, boolean useConfigurationClasspath) {
        super(EMPTY_URL_ARRAY, parent);
        if (config == null) {
            config = CompilerConfiguration.DEFAULT;
        }
        this.config = config;
        if (useConfigurationClasspath) {
            for (String path : config.getClasspath()) {
                this.addClasspath(path);
            }
        }
    }

    public GroovyClassLoader(ClassLoader loader, CompilerConfiguration config) {
        this(loader, config, true);
    }

    public void setResourceLoader(GroovyResourceLoader resourceLoader) {
        if (resourceLoader == null) {
            throw new IllegalArgumentException("Resource loader must not be null!");
        }
        this.resourceLoader = resourceLoader;
    }

    public GroovyResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

    public Class defineClass(ClassNode classNode, String file, String newCodeBase) {
        CodeSource codeSource = null;
        try {
            codeSource = new CodeSource(new URL("file", "", newCodeBase), (Certificate[])null);
        }
        catch (MalformedURLException malformedURLException) {
            // empty catch block
        }
        CompilationUnit unit = this.createCompilationUnit(this.config, codeSource);
        ClassCollector collector = this.createCollector(unit, classNode.getModule().getContext());
        try {
            unit.addClassNode(classNode);
            unit.setClassgenCallback(collector);
            unit.compile(7);
            this.definePackageInternal(collector.generatedClass.getName());
            return collector.generatedClass;
        }
        catch (CompilationFailedException e) {
            throw new RuntimeException(e);
        }
    }

    public Class parseClass(File file) throws CompilationFailedException, IOException {
        return this.parseClass(new GroovyCodeSource(file, this.config.getSourceEncoding()));
    }

    public Class parseClass(final String text, final String fileName) throws CompilationFailedException {
        GroovyCodeSource gcs = AccessController.doPrivileged(new PrivilegedAction<GroovyCodeSource>(){

            @Override
            public GroovyCodeSource run() {
                return new GroovyCodeSource(text, fileName, "/groovy/script");
            }
        });
        gcs.setCachable(false);
        return this.parseClass(gcs);
    }

    public Class parseClass(String text) throws CompilationFailedException {
        return this.parseClass(text, "script" + System.currentTimeMillis() + Math.abs(text.hashCode()) + ".groovy");
    }

    public synchronized String generateScriptName() {
        return "script" + ++scriptNameCounter + ".groovy";
    }

    @Deprecated
    public Class parseClass(final InputStream in, final String fileName) throws CompilationFailedException {
        GroovyCodeSource gcs = AccessController.doPrivileged(new PrivilegedAction<GroovyCodeSource>(){

            @Override
            public GroovyCodeSource run() {
                try {
                    String scriptText = GroovyClassLoader.this.config.getSourceEncoding() != null ? IOGroovyMethods.getText(in, GroovyClassLoader.this.config.getSourceEncoding()) : IOGroovyMethods.getText(in);
                    return new GroovyCodeSource(scriptText, fileName, "/groovy/script");
                }
                catch (IOException e) {
                    throw new RuntimeException("Impossible to read the content of the input stream for file named: " + fileName, e);
                }
            }
        });
        return this.parseClass(gcs);
    }

    public Class parseClass(GroovyCodeSource codeSource) throws CompilationFailedException {
        return this.parseClass(codeSource, codeSource.isCachable());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Class parseClass(GroovyCodeSource codeSource, boolean shouldCacheSource) throws CompilationFailedException {
        Map<String, Class> map = this.sourceCache;
        synchronized (map) {
            Class answer = this.sourceCache.get(codeSource.getName());
            if (answer != null) {
                return answer;
            }
            answer = this.doParseClass(codeSource);
            if (shouldCacheSource) {
                this.sourceCache.put(codeSource.getName(), answer);
            }
            return answer;
        }
    }

    private Class doParseClass(GroovyCodeSource codeSource) {
        URL url;
        GroovyClassLoader.validate(codeSource);
        CompilationUnit unit = this.createCompilationUnit(this.config, codeSource.getCodeSource());
        if (this.recompile != null && this.recompile.booleanValue() || this.recompile == null && this.config.getRecompileGroovySource()) {
            unit.addFirstPhaseOperation(TimestampAdder.INSTANCE, CompilePhase.CLASS_GENERATION.getPhaseNumber());
        }
        SourceUnit su = null;
        File file = codeSource.getFile();
        su = file != null ? unit.addSource(file) : ((url = codeSource.getURL()) != null ? unit.addSource(url) : unit.addSource(codeSource.getName(), codeSource.getScriptText()));
        ClassCollector collector = this.createCollector(unit, su);
        unit.setClassgenCallback(collector);
        int goalPhase = 7;
        if (this.config != null && this.config.getTargetDirectory() != null) {
            goalPhase = 8;
        }
        unit.compile(goalPhase);
        Class answer = collector.generatedClass;
        String mainClass = su.getAST().getMainClassName();
        for (Object o : collector.getLoadedClasses()) {
            Class clazz = (Class)o;
            String clazzName = clazz.getName();
            this.definePackageInternal(clazzName);
            this.setClassCacheEntry(clazz);
            if (!clazzName.equals(mainClass)) continue;
            answer = clazz;
        }
        return answer;
    }

    private static void validate(GroovyCodeSource codeSource) {
        if (codeSource.getFile() == null && codeSource.getScriptText() == null) {
            throw new IllegalArgumentException("Script text to compile cannot be null!");
        }
    }

    private void definePackageInternal(String className) {
        String pkgName;
        Package pkg;
        int i = className.lastIndexOf(46);
        if (i != -1 && (pkg = this.getPackage(pkgName = className.substring(0, i))) == null) {
            this.definePackage(pkgName, null, null, null, null, null, null, null);
        }
    }

    protected String[] getClassPath() {
        URL[] urls = this.getURLs();
        String[] ret = new String[urls.length];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = urls[i].getFile();
        }
        return ret;
    }

    @Override
    protected PermissionCollection getPermissions(CodeSource codeSource) {
        PermissionCollection perms;
        try {
            try {
                perms = super.getPermissions(codeSource);
            }
            catch (SecurityException e) {
                perms = new Permissions();
            }
            ProtectionDomain myDomain = AccessController.doPrivileged(new PrivilegedAction<ProtectionDomain>(){

                @Override
                public ProtectionDomain run() {
                    return this.getClass().getProtectionDomain();
                }
            });
            PermissionCollection myPerms = myDomain.getPermissions();
            if (myPerms != null) {
                Enumeration<Permission> elements = myPerms.elements();
                while (elements.hasMoreElements()) {
                    perms.add(elements.nextElement());
                }
            }
        }
        catch (Throwable e) {
            perms = new Permissions();
        }
        perms.setReadOnly();
        return perms;
    }

    protected CompilationUnit createCompilationUnit(CompilerConfiguration config, CodeSource source) {
        return new CompilationUnit(config, source, this);
    }

    protected ClassCollector createCollector(CompilationUnit unit, SourceUnit su) {
        InnerLoader loader = AccessController.doPrivileged(new PrivilegedAction<InnerLoader>(){

            @Override
            public InnerLoader run() {
                return new InnerLoader(GroovyClassLoader.this);
            }
        });
        return new ClassCollector(loader, unit, su);
    }

    public Class defineClass(String name, byte[] b) {
        return super.defineClass(name, b, 0, b.length);
    }

    public Class loadClass(String name, boolean lookupScriptFiles, boolean preferClassOverScript) throws ClassNotFoundException, CompilationFailedException {
        return this.loadClass(name, lookupScriptFiles, preferClassOverScript, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Class getClassCacheEntry(String name) {
        if (name == null) {
            return null;
        }
        Map<String, Class> map = this.classCache;
        synchronized (map) {
            return this.classCache.get(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void setClassCacheEntry(Class cls) {
        Map<String, Class> map = this.classCache;
        synchronized (map) {
            this.classCache.put(cls.getName(), cls);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void removeClassCacheEntry(String name) {
        Map<String, Class> map = this.classCache;
        synchronized (map) {
            this.classCache.remove(name);
        }
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    protected boolean isRecompilable(Class cls) {
        if (cls == null) {
            return true;
        }
        if (cls.getClassLoader() == this) {
            return false;
        }
        if (this.recompile == null && !this.config.getRecompileGroovySource()) {
            return false;
        }
        if (this.recompile != null && !this.recompile.booleanValue()) {
            return false;
        }
        if (!GroovyObject.class.isAssignableFrom(cls)) {
            return false;
        }
        long timestamp = this.getTimeStamp(cls);
        return timestamp != Long.MAX_VALUE;
    }

    public void setShouldRecompile(Boolean mode) {
        this.recompile = mode;
    }

    public Boolean isShouldRecompile() {
        return this.recompile;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Class loadClass(String name, boolean lookupScriptFiles, boolean preferClassOverScript, boolean resolve) throws ClassNotFoundException, CompilationFailedException {
        String className;
        int i2;
        Class cls = this.getClassCacheEntry(name);
        boolean recompile = this.isRecompilable(cls);
        if (!recompile) {
            return cls;
        }
        ClassNotFoundException last = null;
        try {
            Class<?> parentClassLoaderClass = super.loadClass(name, resolve);
            if (cls != parentClassLoaderClass) {
                return parentClassLoaderClass;
            }
        }
        catch (ClassNotFoundException cnfe) {
            last = cnfe;
        }
        catch (NoClassDefFoundError ncdfe) {
            if (ncdfe.getMessage().indexOf("wrong name") > 0) {
                last = new ClassNotFoundException(name);
            }
            throw ncdfe;
        }
        SecurityManager sm = System.getSecurityManager();
        if (sm != null && (i2 = (className = name.replace('/', '.')).lastIndexOf(46)) != -1 && !className.startsWith("sun.reflect.")) {
            sm.checkPackageAccess(className.substring(0, i2));
        }
        if (cls != null && preferClassOverScript) {
            return cls;
        }
        if (lookupScriptFiles) {
            try {
                Class classCacheEntry = this.getClassCacheEntry(name);
                if (classCacheEntry != cls) {
                    Class i2 = classCacheEntry;
                    return i2;
                }
                URL source = this.resourceLoader.loadGroovySource(name);
                Class oldClass = cls;
                cls = null;
                cls = this.recompile(source, name, oldClass);
            }
            catch (IOException ioe) {
                last = new ClassNotFoundException("IOException while opening groovy source: " + name, ioe);
            }
            finally {
                if (cls == null) {
                    this.removeClassCacheEntry(name);
                } else {
                    this.setClassCacheEntry(cls);
                }
            }
        }
        if (cls == null) {
            if (last == null) {
                throw new AssertionError(true);
            }
            throw last;
        }
        return cls;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Class recompile(URL source, String className, Class oldClass) throws CompilationFailedException, IOException {
        if (source != null && (oldClass != null && this.isSourceNewer(source, oldClass) || oldClass == null)) {
            Map<String, Class> map = this.sourceCache;
            synchronized (map) {
                String name = source.toExternalForm();
                this.sourceCache.remove(name);
                if (GroovyClassLoader.isFile(source)) {
                    try {
                        return this.parseClass(new GroovyCodeSource(new File(source.toURI()), this.config.getSourceEncoding()));
                    }
                    catch (URISyntaxException uRISyntaxException) {
                        // empty catch block
                    }
                }
                return this.parseClass(source.openStream(), name);
            }
        }
        return oldClass;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.loadClass(name, false);
    }

    protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return this.loadClass(name, true, true, resolve);
    }

    protected long getTimeStamp(Class cls) {
        return Verifier.getTimestamp(cls);
    }

    private static String decodeFileName(String fileName) {
        String decodedFile = fileName;
        try {
            decodedFile = URLDecoder.decode(fileName, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            System.err.println("Encountered an invalid encoding scheme when trying to use URLDecoder.decode() inside of the GroovyClassLoader.decodeFileName() method.  Returning the unencoded URL.");
            System.err.println("Please note that if you encounter this error and you have spaces in your directory you will run into issues.  Refer to GROOVY-1787 for description of this bug.");
        }
        return decodedFile;
    }

    private static boolean isFile(URL ret) {
        return ret != null && ret.getProtocol().equals("file");
    }

    private static File getFileForUrl(URL ret, String filename) {
        String fileWithoutPackage = filename;
        if (fileWithoutPackage.indexOf(47) != -1) {
            int index = fileWithoutPackage.lastIndexOf(47);
            fileWithoutPackage = fileWithoutPackage.substring(index + 1);
        }
        return GroovyClassLoader.fileReallyExists(ret, fileWithoutPackage);
    }

    private static File fileReallyExists(URL ret, String fileWithoutPackage) {
        File file;
        File path;
        try {
            path = new File(ret.toURI());
        }
        catch (URISyntaxException e) {
            path = new File(GroovyClassLoader.decodeFileName(ret.getFile()));
        }
        path = path.getParentFile();
        if (path.exists() && path.isDirectory() && (file = new File(path, fileWithoutPackage)).exists()) {
            File parent = file.getParentFile();
            for (String child : parent.list()) {
                if (!child.equals(fileWithoutPackage)) continue;
                return file;
            }
        }
        return null;
    }

    private URL getSourceFile(String name, String extension) {
        String filename = name.replace('.', '/') + "." + extension;
        URL ret = this.getResource(filename);
        if (GroovyClassLoader.isFile(ret) && GroovyClassLoader.getFileForUrl(ret, filename) == null) {
            return null;
        }
        return ret;
    }

    protected boolean isSourceNewer(URL source, Class cls) throws IOException {
        long lastMod;
        if (GroovyClassLoader.isFile(source)) {
            String path = source.getPath().replace('/', File.separatorChar).replace('|', ':');
            File file = new File(path);
            lastMod = file.lastModified();
        } else {
            URLConnection conn = source.openConnection();
            lastMod = conn.getLastModified();
            conn.getInputStream().close();
        }
        long classTime = this.getTimeStamp(cls);
        return classTime + (long)this.config.getMinimumRecompilationInterval() < lastMod;
    }

    public void addClasspath(final String path) {
        AccessController.doPrivileged(new PrivilegedAction<Void>(){

            @Override
            public Void run() {
                URL[] urls;
                URI newURI;
                try {
                    newURI = new URI(path);
                    newURI.toURL();
                }
                catch (URISyntaxException e) {
                    newURI = new File(path).toURI();
                }
                catch (MalformedURLException e) {
                    newURI = new File(path).toURI();
                }
                catch (IllegalArgumentException e) {
                    newURI = new File(path).toURI();
                }
                for (URL url : urls = GroovyClassLoader.this.getURLs()) {
                    try {
                        if (!newURI.equals(url.toURI())) continue;
                        return null;
                    }
                    catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    GroovyClassLoader.this.addURL(newURI.toURL());
                }
                catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Class[] getLoadedClasses() {
        Map<String, Class> map = this.classCache;
        synchronized (map) {
            Collection<Class> values = this.classCache.values();
            return values.toArray(new Class[values.size()]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearCache() {
        Map<String, Class> map = this.classCache;
        synchronized (map) {
            this.classCache.clear();
        }
        map = this.sourceCache;
        synchronized (map) {
            this.sourceCache.clear();
        }
    }

    private static class TimestampAdder
    extends CompilationUnit.PrimaryClassNodeOperation
    implements Opcodes {
        private static final TimestampAdder INSTANCE = new TimestampAdder();

        private TimestampAdder() {
        }

        protected void addTimeStamp(ClassNode node) {
            if (node.getDeclaredField("__timeStamp") == null) {
                FieldNode timeTagField = new FieldNode("__timeStamp", 4105, ClassHelper.long_TYPE, node, new ConstantExpression(System.currentTimeMillis()));
                timeTagField.setSynthetic(true);
                node.addField(timeTagField);
                timeTagField = new FieldNode("__timeStamp__239_neverHappen" + String.valueOf(System.currentTimeMillis()), 4105, ClassHelper.long_TYPE, node, new ConstantExpression(0L));
                timeTagField.setSynthetic(true);
                node.addField(timeTagField);
            }
        }

        @Override
        public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
            if ((classNode.getModifiers() & 0x200) > 0) {
                return;
            }
            if (!(classNode instanceof InnerClassNode)) {
                this.addTimeStamp(classNode);
            }
        }
    }

    public static class ClassCollector
    extends CompilationUnit.ClassgenCallback {
        private Class generatedClass;
        private final GroovyClassLoader cl;
        private final SourceUnit su;
        private final CompilationUnit unit;
        private final Collection<Class> loadedClasses;

        protected ClassCollector(InnerLoader cl, CompilationUnit unit, SourceUnit su) {
            this.cl = cl;
            this.unit = unit;
            this.loadedClasses = new ArrayList<Class>();
            this.su = su;
        }

        public GroovyClassLoader getDefiningClassLoader() {
            return this.cl;
        }

        protected Class createClass(byte[] code, ClassNode classNode) {
            BytecodeProcessor bytecodePostprocessor = this.unit.getConfiguration().getBytecodePostprocessor();
            byte[] fcode = code;
            if (bytecodePostprocessor != null) {
                fcode = bytecodePostprocessor.processBytecode(classNode.getName(), fcode);
            }
            GroovyClassLoader cl = this.getDefiningClassLoader();
            Class theClass = cl.defineClass(classNode.getName(), fcode, 0, fcode.length, this.unit.getAST().getCodeSource());
            this.loadedClasses.add(theClass);
            if (this.generatedClass == null) {
                ModuleNode mn = classNode.getModule();
                SourceUnit msu = null;
                if (mn != null) {
                    msu = mn.getContext();
                }
                ClassNode main = null;
                if (mn != null) {
                    main = mn.getClasses().get(0);
                }
                if (msu == this.su && main == classNode) {
                    this.generatedClass = theClass;
                }
            }
            return theClass;
        }

        protected Class onClassNode(ClassWriter classWriter, ClassNode classNode) {
            byte[] code = classWriter.toByteArray();
            return this.createClass(code, classNode);
        }

        @Override
        public void call(ClassVisitor classWriter, ClassNode classNode) {
            this.onClassNode((ClassWriter)classWriter, classNode);
        }

        public Collection getLoadedClasses() {
            return this.loadedClasses;
        }
    }

    public static class InnerLoader
    extends GroovyClassLoader {
        private final GroovyClassLoader delegate;
        private final long timeStamp;

        public InnerLoader(GroovyClassLoader delegate) {
            super(delegate);
            this.delegate = delegate;
            this.timeStamp = System.currentTimeMillis();
        }

        @Override
        public void addClasspath(String path) {
            this.delegate.addClasspath(path);
        }

        @Override
        public void clearCache() {
            this.delegate.clearCache();
        }

        @Override
        public URL findResource(String name) {
            return this.delegate.findResource(name);
        }

        public Enumeration findResources(String name) throws IOException {
            return this.delegate.findResources(name);
        }

        @Override
        public Class[] getLoadedClasses() {
            return this.delegate.getLoadedClasses();
        }

        @Override
        public URL getResource(String name) {
            return this.delegate.getResource(name);
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            return this.delegate.getResourceAsStream(name);
        }

        @Override
        public GroovyResourceLoader getResourceLoader() {
            return this.delegate.getResourceLoader();
        }

        @Override
        public URL[] getURLs() {
            return this.delegate.getURLs();
        }

        @Override
        public Class loadClass(String name, boolean lookupScriptFiles, boolean preferClassOverScript, boolean resolve) throws ClassNotFoundException, CompilationFailedException {
            Class<?> c = this.findLoadedClass(name);
            if (c != null) {
                return c;
            }
            return this.delegate.loadClass(name, lookupScriptFiles, preferClassOverScript, resolve);
        }

        @Override
        public Class parseClass(GroovyCodeSource codeSource, boolean shouldCache) throws CompilationFailedException {
            return this.delegate.parseClass(codeSource, shouldCache);
        }

        @Override
        public void setResourceLoader(GroovyResourceLoader resourceLoader) {
            this.delegate.setResourceLoader(resourceLoader);
        }

        @Override
        public void addURL(URL url) {
            this.delegate.addURL(url);
        }

        public long getTimeStamp() {
            return this.timeStamp;
        }
    }
}

