/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyResourceLoader;
import groovy.lang.Script;
import groovy.util.ResourceConnector;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.ClassNodeResolver;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.tools.gse.DependencyTracker;
import org.codehaus.groovy.tools.gse.StringSetMap;

public class GroovyScriptEngine
implements ResourceConnector {
    private static final ClassLoader CL_STUB = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

        @Override
        public ClassLoader run() {
            return new ClassLoader(){};
        }
    });
    private static final URL[] EMPTY_URL_ARRAY = new URL[0];
    private static WeakReference<ThreadLocal<LocalData>> localData = new WeakReference<Object>(null);
    private URL[] roots;
    private ResourceConnector rc;
    private final ClassLoader parentLoader;
    private GroovyClassLoader groovyLoader;
    private final Map<String, ScriptCacheEntry> scriptCache = new ConcurrentHashMap<String, ScriptCacheEntry>();
    private CompilerConfiguration config = new CompilerConfiguration(CompilerConfiguration.DEFAULT);

    private static synchronized ThreadLocal<LocalData> getLocalData() {
        ThreadLocal<LocalData> local = (ThreadLocal<LocalData>)localData.get();
        if (local != null) {
            return local;
        }
        local = new ThreadLocal<LocalData>();
        localData = new WeakReference(local);
        return local;
    }

    public static void main(String[] urls) throws Exception {
        GroovyScriptEngine gse = new GroovyScriptEngine(urls);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("groovy> ");
            String line = br.readLine();
            if (line == null || line.equals("quit")) break;
            try {
                System.out.println(gse.run(line, new Binding()));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private GroovyClassLoader initGroovyLoader() {
        GroovyClassLoader groovyClassLoader = AccessController.doPrivileged(new PrivilegedAction<ScriptClassLoader>(){

            @Override
            public ScriptClassLoader run() {
                if (GroovyScriptEngine.this.parentLoader instanceof GroovyClassLoader) {
                    return new ScriptClassLoader((GroovyClassLoader)GroovyScriptEngine.this.parentLoader);
                }
                return new ScriptClassLoader(GroovyScriptEngine.this.parentLoader, GroovyScriptEngine.this.config);
            }
        });
        for (URL root : this.roots) {
            groovyClassLoader.addURL(root);
        }
        return groovyClassLoader;
    }

    @Override
    public URLConnection getResourceConnection(String resourceName) throws ResourceException {
        URLConnection groovyScriptConn = null;
        ResourceException se = null;
        for (URL root : this.roots) {
            String message;
            URL scriptURL = null;
            try {
                scriptURL = new URL(root, resourceName);
                groovyScriptConn = GroovyScriptEngine.openConnection(scriptURL);
                break;
            }
            catch (MalformedURLException e) {
                message = "Malformed URL: " + root + ", " + resourceName;
                if (se == null) {
                    se = new ResourceException(message);
                    continue;
                }
                se = new ResourceException(message, se);
            }
            catch (IOException e1) {
                message = "Cannot open URL: " + root + resourceName;
                groovyScriptConn = null;
                se = se == null ? new ResourceException(message) : new ResourceException(message, se);
            }
        }
        if (se == null) {
            se = new ResourceException("No resource for " + resourceName + " was found");
        }
        if (groovyScriptConn == null) {
            throw se;
        }
        return groovyScriptConn;
    }

    private static URLConnection openConnection(URL scriptURL) throws IOException {
        URLConnection urlConnection = scriptURL.openConnection();
        GroovyScriptEngine.verifyInputStream(urlConnection);
        return scriptURL.openConnection();
    }

    private static void forceClose(URLConnection urlConnection) {
        if (urlConnection != null) {
            try {
                GroovyScriptEngine.verifyInputStream(urlConnection);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private static void verifyInputStream(URLConnection urlConnection) throws IOException {
        InputStream in = null;
        try {
            in = urlConnection.getInputStream();
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    private GroovyScriptEngine(URL[] roots, ClassLoader parent, ResourceConnector rc) {
        this.config.setSourceEncoding("UTF-8");
        if (roots == null) {
            roots = EMPTY_URL_ARRAY;
        }
        this.roots = roots;
        if (rc == null) {
            rc = this;
        }
        this.rc = rc;
        if (parent == CL_STUB) {
            parent = this.getClass().getClassLoader();
        }
        this.parentLoader = parent;
        this.groovyLoader = this.initGroovyLoader();
    }

    public GroovyScriptEngine(URL[] roots) {
        this(roots, CL_STUB, null);
    }

    public GroovyScriptEngine(URL[] roots, ClassLoader parentClassLoader) {
        this(roots, parentClassLoader, null);
    }

    public GroovyScriptEngine(String[] urls) throws IOException {
        this(GroovyScriptEngine.createRoots(urls), CL_STUB, null);
    }

    private static URL[] createRoots(String[] urls) throws MalformedURLException {
        if (urls == null) {
            return null;
        }
        URL[] roots = new URL[urls.length];
        for (int i = 0; i < roots.length; ++i) {
            roots[i] = urls[i].contains("://") ? new URL(urls[i]) : new File(urls[i]).toURI().toURL();
        }
        return roots;
    }

    public GroovyScriptEngine(String[] urls, ClassLoader parentClassLoader) throws IOException {
        this(GroovyScriptEngine.createRoots(urls), parentClassLoader, null);
    }

    public GroovyScriptEngine(String url) throws IOException {
        this(new String[]{url});
    }

    public GroovyScriptEngine(String url, ClassLoader parentClassLoader) throws IOException {
        this(new String[]{url}, parentClassLoader);
    }

    public GroovyScriptEngine(ResourceConnector rc) {
        this(null, CL_STUB, rc);
    }

    public GroovyScriptEngine(ResourceConnector rc, ClassLoader parentClassLoader) {
        this(null, parentClassLoader, rc);
    }

    public ClassLoader getParentClassLoader() {
        return this.parentLoader;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Class loadScriptByName(String scriptName) throws ResourceException, ScriptException {
        Class clazz;
        block6: {
            URLConnection conn = this.rc.getResourceConnection(scriptName);
            String path = conn.getURL().toExternalForm();
            ScriptCacheEntry entry = this.scriptCache.get(path);
            clazz = null;
            if (entry != null) {
                clazz = entry.scriptClass;
            }
            try {
                if (!this.isSourceNewer(entry)) break block6;
                try {
                    String encoding = conn.getContentEncoding() != null ? conn.getContentEncoding() : this.config.getSourceEncoding();
                    String content = IOGroovyMethods.getText(conn.getInputStream(), encoding);
                    clazz = this.groovyLoader.parseClass(content, path);
                }
                catch (IOException e) {
                    throw new ResourceException(e);
                }
            }
            finally {
                GroovyScriptEngine.forceClose(conn);
            }
        }
        return clazz;
    }

    public String run(String scriptName, String argument) throws ResourceException, ScriptException {
        Binding binding = new Binding();
        binding.setVariable("arg", argument);
        Object result = this.run(scriptName, binding);
        return result == null ? "" : result.toString();
    }

    public Object run(String scriptName, Binding binding) throws ResourceException, ScriptException {
        return this.createScript(scriptName, binding).run();
    }

    public Script createScript(String scriptName, Binding binding) throws ResourceException, ScriptException {
        return InvokerHelper.createScript(this.loadScriptByName(scriptName), binding);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private long getLastModified(String scriptName) throws ResourceException {
        URLConnection conn = this.rc.getResourceConnection(scriptName);
        long lastMod = 0L;
        try {
            lastMod = conn.getLastModified();
        }
        finally {
            GroovyScriptEngine.forceClose(conn);
        }
        return lastMod;
    }

    protected boolean isSourceNewer(ScriptCacheEntry entry) throws ResourceException {
        if (entry == null) {
            return true;
        }
        long mainEntryLastCheck = entry.lastCheck;
        long now = 0L;
        boolean returnValue = false;
        for (String scriptName : entry.dependencies) {
            long nextSourceCheck;
            ScriptCacheEntry depEntry = this.scriptCache.get(scriptName);
            if (depEntry.sourceNewer) {
                return true;
            }
            if (mainEntryLastCheck < depEntry.lastModified) {
                returnValue = true;
                continue;
            }
            if (now == 0L) {
                now = this.getCurrentTime();
            }
            if ((nextSourceCheck = depEntry.lastCheck + (long)this.config.getMinimumRecompilationInterval()) > now) continue;
            long lastMod = this.getLastModified(scriptName);
            if (depEntry.lastModified < lastMod) {
                depEntry = new ScriptCacheEntry(depEntry, lastMod, true);
                this.scriptCache.put(scriptName, depEntry);
                returnValue = true;
                continue;
            }
            depEntry = new ScriptCacheEntry(depEntry, now, false);
            this.scriptCache.put(scriptName, depEntry);
        }
        return returnValue;
    }

    public GroovyClassLoader getGroovyClassLoader() {
        return this.groovyLoader;
    }

    public CompilerConfiguration getConfig() {
        return this.config;
    }

    public void setConfig(CompilerConfiguration config) {
        if (config == null) {
            throw new NullPointerException("configuration cannot be null");
        }
        this.config = config;
        this.groovyLoader = this.initGroovyLoader();
    }

    protected long getCurrentTime() {
        return System.currentTimeMillis();
    }

    private class ScriptClassLoader
    extends GroovyClassLoader {
        public ScriptClassLoader(GroovyClassLoader loader) {
            super(loader);
        }

        public ScriptClassLoader(ClassLoader loader, CompilerConfiguration config) {
            super(loader, config, false);
            this.setResLoader();
        }

        private void setResLoader() {
            final GroovyResourceLoader rl = this.getResourceLoader();
            this.setResourceLoader(new GroovyResourceLoader(){

                @Override
                public URL loadGroovySource(String className) throws MalformedURLException {
                    for (String extension : GroovyScriptEngine.this.getConfig().getScriptExtensions()) {
                        String filename = className.replace('.', File.separatorChar) + "." + extension;
                        try {
                            URLConnection dependentScriptConn = GroovyScriptEngine.this.rc.getResourceConnection(filename);
                            return dependentScriptConn.getURL();
                        }
                        catch (ResourceException resourceException) {
                        }
                    }
                    return rl.loadGroovySource(className);
                }
            });
        }

        @Override
        protected CompilationUnit createCompilationUnit(CompilerConfiguration configuration, CodeSource source) {
            CompilationUnit cu = super.createCompilationUnit(configuration, source);
            LocalData local = (LocalData)GroovyScriptEngine.getLocalData().get();
            local.cu = cu;
            final StringSetMap cache = local.dependencyCache;
            final Map<String, String> precompiledEntries = local.precompiledEntries;
            Iterator iterator = cache.get(".").iterator();
            while (iterator.hasNext()) {
                String depSourcePath = (String)iterator.next();
                try {
                    cache.get(depSourcePath);
                    cu.addSource(GroovyScriptEngine.this.getResourceConnection(depSourcePath).getURL());
                }
                catch (ResourceException resourceException) {}
            }
            cache.clear();
            cu.addPhaseOperation(new CompilationUnit.PrimaryClassNodeOperation(){

                @Override
                public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
                    if (classNode instanceof InnerClassNode) {
                        return;
                    }
                    DependencyTracker dt = new DependencyTracker(source, cache, precompiledEntries);
                    dt.visitClass(classNode);
                }
            }, 7);
            cu.setClassNodeResolver(new ClassNodeResolver(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 * Enabled aggressive block sorting
                 * Enabled unnecessary exception pruning
                 * Enabled aggressive exception aggregation
                 */
                @Override
                public ClassNodeResolver.LookupResult findClassNode(String origName, CompilationUnit compilationUnit) {
                    CompilerConfiguration cc = compilationUnit.getConfiguration();
                    String name = origName.replace('.', '/');
                    Iterator<String> iterator = cc.getScriptExtensions().iterator();
                    while (iterator.hasNext()) {
                        String ext = iterator.next();
                        try {
                            String finalName = name + "." + ext;
                            URLConnection conn = GroovyScriptEngine.this.rc.getResourceConnection(finalName);
                            URL url = conn.getURL();
                            String path = url.toExternalForm();
                            ScriptCacheEntry entry = (ScriptCacheEntry)GroovyScriptEngine.this.scriptCache.get(path);
                            Class clazz = null;
                            if (entry != null) {
                                clazz = entry.scriptClass;
                            }
                            if (GroovyScriptEngine.this.isSourceNewer(entry)) {
                                try {
                                    SourceUnit su = compilationUnit.addSource(url);
                                    ClassNodeResolver.LookupResult lookupResult = new ClassNodeResolver.LookupResult(su, null);
                                    return lookupResult;
                                }
                                finally {
                                    GroovyScriptEngine.forceClose(conn);
                                }
                            }
                            precompiledEntries.put(origName, path);
                            if (clazz == null) continue;
                            ClassNode cn = new ClassNode(clazz);
                            return new ClassNodeResolver.LookupResult(null, cn);
                        }
                        catch (ResourceException resourceException) {
                        }
                    }
                    return super.findClassNode(origName, compilationUnit);
                }
            });
            return cu;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Class parseClass(GroovyCodeSource codeSource, boolean shouldCacheSource) throws CompilationFailedException {
            Map map = this.sourceCache;
            synchronized (map) {
                return this.doParseClass(codeSource);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Class<?> doParseClass(GroovyCodeSource codeSource) {
            ThreadLocal localTh = GroovyScriptEngine.getLocalData();
            LocalData localData = new LocalData();
            localTh.set(localData);
            StringSetMap cache = localData.dependencyCache;
            Class answer = null;
            try {
                this.updateLocalDependencyCache(codeSource, localData);
                answer = super.parseClass(codeSource, false);
                this.updateScriptCache(localData);
            }
            finally {
                cache.clear();
                localTh.remove();
            }
            return answer;
        }

        private void updateLocalDependencyCache(GroovyCodeSource codeSource, LocalData localData) {
            ScriptCacheEntry origEntry = (ScriptCacheEntry)GroovyScriptEngine.this.scriptCache.get(codeSource.getName());
            Set origDep = null;
            if (origEntry != null) {
                origDep = origEntry.dependencies;
            }
            if (origDep != null) {
                HashSet<String> newDep = new HashSet<String>(origDep.size());
                for (String depName : origDep) {
                    ScriptCacheEntry dep = (ScriptCacheEntry)GroovyScriptEngine.this.scriptCache.get(depName);
                    try {
                        if (origEntry != dep && !GroovyScriptEngine.this.isSourceNewer(dep)) continue;
                        newDep.add(depName);
                    }
                    catch (ResourceException resourceException) {}
                }
                StringSetMap cache = localData.dependencyCache;
                cache.put(".", newDep);
            }
        }

        private void updateScriptCache(LocalData localData) {
            StringSetMap cache = localData.dependencyCache;
            cache.makeTransitiveHull();
            long time = GroovyScriptEngine.this.getCurrentTime();
            HashSet<String> entryNames = new HashSet<String>();
            for (Map.Entry entry : cache.entrySet()) {
                long lastModified;
                String entryName;
                String className = (String)entry.getKey();
                Class clazz = this.getClassCacheEntry(className);
                if (clazz == null || entryNames.contains(entryName = this.getPath(clazz, localData.precompiledEntries))) continue;
                entryNames.add(entryName);
                Set<String> value = this.convertToPaths((Set)entry.getValue(), localData.precompiledEntries);
                try {
                    lastModified = GroovyScriptEngine.this.getLastModified(entryName);
                }
                catch (ResourceException e) {
                    lastModified = time;
                }
                ScriptCacheEntry cacheEntry = new ScriptCacheEntry(clazz, lastModified, time, value, false);
                GroovyScriptEngine.this.scriptCache.put(entryName, cacheEntry);
            }
        }

        private String getPath(Class clazz, Map<String, String> precompiledEntries) {
            CompilationUnit cu = ((LocalData)GroovyScriptEngine.getLocalData().get()).cu;
            String name = clazz.getName();
            ClassNode classNode = cu.getClassNode(name);
            if (classNode == null) {
                String path = precompiledEntries.get(name);
                if (path == null) {
                    throw new GroovyBugError("Precompiled class " + name + " should be available in precompiled entries map, but was not.");
                }
                return path;
            }
            return classNode.getModule().getContext().getName();
        }

        private Set<String> convertToPaths(Set<String> orig, Map<String, String> precompiledEntries) {
            HashSet<String> ret = new HashSet<String>();
            for (String className : orig) {
                Class clazz = this.getClassCacheEntry(className);
                if (clazz == null) continue;
                ret.add(this.getPath(clazz, precompiledEntries));
            }
            return ret;
        }
    }

    private static class ScriptCacheEntry {
        private final Class scriptClass;
        private final long lastModified;
        private final long lastCheck;
        private final Set<String> dependencies;
        private final boolean sourceNewer;

        public ScriptCacheEntry(Class clazz, long modified, long lastCheck, Set<String> depend, boolean sourceNewer) {
            this.scriptClass = clazz;
            this.lastModified = modified;
            this.lastCheck = lastCheck;
            this.dependencies = depend;
            this.sourceNewer = sourceNewer;
        }

        public ScriptCacheEntry(ScriptCacheEntry old, long lastCheck, boolean sourceNewer) {
            this(old.scriptClass, old.lastModified, lastCheck, old.dependencies, sourceNewer);
        }
    }

    private static class LocalData {
        CompilationUnit cu;
        StringSetMap dependencyCache = new StringSetMap();
        Map<String, String> precompiledEntries = new HashMap<String, String>();

        private LocalData() {
        }
    }
}

