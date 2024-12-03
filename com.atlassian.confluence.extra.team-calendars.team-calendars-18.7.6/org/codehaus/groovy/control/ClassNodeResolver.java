/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import groovy.lang.GroovyClassLoader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.Verifier;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;

public class ClassNodeResolver {
    private Map<String, ClassNode> cachedClasses = new HashMap<String, ClassNode>();
    protected static final ClassNode NO_CLASS = new ClassNode("NO_CLASS", 1, ClassHelper.OBJECT_TYPE){

        @Override
        public void setRedirect(ClassNode cn) {
            throw new GroovyBugError("This is a dummy class node only! Never use it for real classes.");
        }
    };

    public LookupResult resolveName(String name, CompilationUnit compilationUnit) {
        ClassNode res = this.getFromClassCache(name);
        if (res == NO_CLASS) {
            return null;
        }
        if (res != null) {
            return new LookupResult(null, res);
        }
        LookupResult lr = this.findClassNode(name, compilationUnit);
        if (lr != null) {
            if (lr.isClassNode()) {
                this.cacheClass(name, lr.getClassNode());
            }
            return lr;
        }
        this.cacheClass(name, NO_CLASS);
        return null;
    }

    public void cacheClass(String name, ClassNode res) {
        this.cachedClasses.put(name, res);
    }

    public ClassNode getFromClassCache(String name) {
        ClassNode cached = this.cachedClasses.get(name);
        return cached;
    }

    public LookupResult findClassNode(String name, CompilationUnit compilationUnit) {
        return this.tryAsLoaderClassOrScript(name, compilationUnit);
    }

    private LookupResult tryAsLoaderClassOrScript(String name, CompilationUnit compilationUnit) {
        Class cls;
        GroovyClassLoader loader = compilationUnit.getClassLoader();
        try {
            cls = loader.loadClass(name, false, true);
        }
        catch (ClassNotFoundException cnfe) {
            LookupResult lr = ClassNodeResolver.tryAsScript(name, compilationUnit, null);
            return lr;
        }
        catch (CompilationFailedException cfe) {
            throw new GroovyBugError("The lookup for " + name + " caused a failed compilaton. There should not have been any compilation from this call.", cfe);
        }
        if (cls == null) {
            return null;
        }
        if (cls.getClassLoader() != loader) {
            return ClassNodeResolver.tryAsScript(name, compilationUnit, cls);
        }
        ClassNode cn = ClassHelper.make(cls);
        return new LookupResult(null, cn);
    }

    private static LookupResult tryAsScript(String name, CompilationUnit compilationUnit, Class oldClass) {
        LookupResult lr = null;
        if (oldClass != null) {
            ClassNode cn = ClassHelper.make(oldClass);
            lr = new LookupResult(null, cn);
        }
        if (name.startsWith("java.")) {
            return lr;
        }
        if (name.indexOf(36) != -1) {
            return lr;
        }
        GroovyClassLoader gcl = compilationUnit.getClassLoader();
        URL url = null;
        try {
            url = gcl.getResourceLoader().loadGroovySource(name);
        }
        catch (MalformedURLException malformedURLException) {
            // empty catch block
        }
        if (url != null && (oldClass == null || ClassNodeResolver.isSourceNewer(url, oldClass))) {
            SourceUnit su = compilationUnit.addSource(url);
            return new LookupResult(su, null);
        }
        return lr;
    }

    private static long getTimeStamp(Class cls) {
        return Verifier.getTimestamp(cls);
    }

    private static boolean isSourceNewer(URL source, Class cls) {
        try {
            long lastMod;
            if (source.getProtocol().equals("file")) {
                String path = source.getPath().replace('/', File.separatorChar).replace('|', ':');
                File file = new File(path);
                lastMod = file.lastModified();
            } else {
                URLConnection conn = source.openConnection();
                lastMod = conn.getLastModified();
                conn.getInputStream().close();
            }
            return lastMod > ClassNodeResolver.getTimeStamp(cls);
        }
        catch (IOException e) {
            return false;
        }
    }

    public static class LookupResult {
        private SourceUnit su;
        private ClassNode cn;

        public LookupResult(SourceUnit su, ClassNode cn) {
            this.su = su;
            this.cn = cn;
            if (su == null && cn == null) {
                throw new IllegalArgumentException("Either the SourceUnit or the ClassNode must not be null.");
            }
            if (su != null && cn != null) {
                throw new IllegalArgumentException("SourceUnit and ClassNode cannot be set at the same time.");
            }
        }

        public boolean isClassNode() {
            return this.cn != null;
        }

        public boolean isSourceUnit() {
            return this.su != null;
        }

        public SourceUnit getSourceUnit() {
            return this.su;
        }

        public ClassNode getClassNode() {
            return this.cn;
        }
    }
}

