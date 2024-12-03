/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.util;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import groovy.lang.IntRange;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.regex.Pattern;
import java.util.zip.ZipException;
import javax.swing.JFrame;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.util.CachedPackage;
import org.codehaus.groovy.tools.shell.util.Logger;
import org.codehaus.groovy.tools.shell.util.PackageHelper;
import org.codehaus.groovy.tools.shell.util.Preferences;

public class PackageHelperImpl
implements PreferenceChangeListener,
PackageHelper,
GroovyObject {
    public static final Pattern NAME_PATTERN;
    private static final String CLASS_SUFFIX = ".class";
    protected static final Logger LOG;
    private Map<String, CachedPackage> rootPackages;
    private final ClassLoader groovyClassLoader;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public PackageHelperImpl(ClassLoader groovyClassLoader) {
        MetaClass metaClass;
        CallSite[] callSiteArray = PackageHelperImpl.$getCallSiteArray();
        Object var3_3 = null;
        this.rootPackages = (Map)ScriptBytecodeAdapter.castToType(var3_3, Map.class);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        ClassLoader classLoader = groovyClassLoader;
        this.groovyClassLoader = (ClassLoader)ScriptBytecodeAdapter.castToType(classLoader, ClassLoader.class);
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(Boolean.class, callSiteArray[1].call(Preferences.class, callSiteArray[2].callGroovyObjectGetProperty(this))))) {
            Object object = callSiteArray[3].callStatic(PackageHelperImpl.class, groovyClassLoader);
            this.rootPackages = (Map)ScriptBytecodeAdapter.castToType(object, Map.class);
        }
        callSiteArray[4].call(Preferences.class, this);
    }

    public PackageHelperImpl() {
        CallSite[] callSiteArray = PackageHelperImpl.$getCallSiteArray();
        this(null);
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent evt) {
        CallSite[] callSiteArray = PackageHelperImpl.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[5].callGetProperty(evt), callSiteArray[6].callGroovyObjectGetProperty(this))) {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[7].call(Boolean.class, callSiteArray[8].call(evt)))) {
                Object var3_3 = null;
                this.rootPackages = (Map)ScriptBytecodeAdapter.castToType(var3_3, Map.class);
            } else if (ScriptBytecodeAdapter.compareEqual(this.rootPackages, null)) {
                Object object = callSiteArray[9].callStatic(PackageHelperImpl.class, this.groovyClassLoader);
                this.rootPackages = (Map)ScriptBytecodeAdapter.castToType(object, Map.class);
            }
        }
    }

    public static Map<String, CachedPackage> initializePackages(ClassLoader groovyClassLoader) throws IOException {
        CallSite[] callSiteArray = PackageHelperImpl.$getCallSiteArray();
        Map rootPackages = (Map)ScriptBytecodeAdapter.castToType(callSiteArray[10].callConstructor(HashMap.class), Map.class);
        Reference<Set> urls = new Reference<Set>((Set)ScriptBytecodeAdapter.castToType(callSiteArray[11].callConstructor(HashSet.class), Set.class));
        ClassLoader loader = groovyClassLoader;
        while (ScriptBytecodeAdapter.compareNotEqual(loader, null)) {
            if (!(loader instanceof URLClassLoader)) {
                callSiteArray[12].call((Object)LOG, callSiteArray[13].call((Object)"Ignoring classloader for completion: ", loader));
            } else {
                callSiteArray[14].call((Object)urls.get(), callSiteArray[15].callGetProperty((URLClassLoader)ScriptBytecodeAdapter.castToType(loader, URLClassLoader.class)));
            }
            Object object = callSiteArray[16].callGetProperty(loader);
            loader = (ClassLoader)ScriptBytecodeAdapter.castToType(object, ClassLoader.class);
        }
        Class[] systemClasses = (Class[])ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[]{String.class, JFrame.class, GroovyObject.class}), Class[].class);
        Reference<Boolean> jigsaw = new Reference<Boolean>(false);
        public class _initializePackages_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference urls;
            private /* synthetic */ Reference jigsaw;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _initializePackages_closure1(Object _outerInstance, Object _thisObject, Reference urls, Reference jigsaw) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _initializePackages_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.urls = reference2 = urls;
                this.jigsaw = reference = jigsaw;
            }

            public Object doCall(Class systemClass) {
                CallSite[] callSiteArray = _initializePackages_closure1.$getCallSiteArray();
                String classfileName = ShortTypeHandling.castToString(callSiteArray[0].call(callSiteArray[1].call(callSiteArray[2].callGetProperty(systemClass), ".", "/"), PackageHelperImpl.CLASS_SUFFIX));
                URL classURL = (URL)ScriptBytecodeAdapter.castToType(callSiteArray[3].call((Object)systemClass, classfileName), URL.class);
                if (ScriptBytecodeAdapter.compareEqual(classURL, null)) {
                    Object object = callSiteArray[4].call(callSiteArray[5].callGetProperty(callSiteArray[6].call(Thread.class)), classfileName);
                    classURL = (URL)ScriptBytecodeAdapter.castToType(object, URL.class);
                }
                if (ScriptBytecodeAdapter.compareNotEqual(classURL, null)) {
                    URLConnection uc = (URLConnection)ScriptBytecodeAdapter.castToType(callSiteArray[7].call(classURL), URLConnection.class);
                    if (uc instanceof JarURLConnection) {
                        return callSiteArray[8].call(this.urls.get(), callSiteArray[9].call((JarURLConnection)ScriptBytecodeAdapter.castToType(uc, JarURLConnection.class)));
                    }
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[10].call(callSiteArray[11].call(callSiteArray[12].call(uc)), "JavaRuntimeURLConnection"))) {
                        boolean bl = true;
                        this.jigsaw.set(bl);
                        return bl;
                    }
                    String filepath = ShortTypeHandling.castToString(callSiteArray[13].call(classURL));
                    String rootFolder = ShortTypeHandling.castToString(callSiteArray[14].call(filepath, 0, callSiteArray[15].call(callSiteArray[16].call(callSiteArray[17].call(filepath), callSiteArray[18].call(classfileName)), 1)));
                    return callSiteArray[19].call(this.urls.get(), callSiteArray[20].callConstructor(URL.class, rootFolder));
                }
                return null;
            }

            public Object call(Class systemClass) {
                CallSite[] callSiteArray = _initializePackages_closure1.$getCallSiteArray();
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return callSiteArray[21].callCurrent((GroovyObject)this, systemClass);
                }
                return this.doCall(systemClass);
            }

            public Set getUrls() {
                CallSite[] callSiteArray = _initializePackages_closure1.$getCallSiteArray();
                return (Set)ScriptBytecodeAdapter.castToType(this.urls.get(), Set.class);
            }

            public Boolean getJigsaw() {
                CallSite[] callSiteArray = _initializePackages_closure1.$getCallSiteArray();
                return (Boolean)ScriptBytecodeAdapter.castToType(this.jigsaw.get(), Boolean.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _initializePackages_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "plus";
                stringArray[1] = "replace";
                stringArray[2] = "name";
                stringArray[3] = "getResource";
                stringArray[4] = "getResource";
                stringArray[5] = "contextClassLoader";
                stringArray[6] = "currentThread";
                stringArray[7] = "openConnection";
                stringArray[8] = "add";
                stringArray[9] = "getJarFileURL";
                stringArray[10] = "equals";
                stringArray[11] = "getSimpleName";
                stringArray[12] = "getClass";
                stringArray[13] = "toExternalForm";
                stringArray[14] = "substring";
                stringArray[15] = "minus";
                stringArray[16] = "minus";
                stringArray[17] = "length";
                stringArray[18] = "length";
                stringArray[19] = "add";
                stringArray[20] = "<$constructor$>";
                stringArray[21] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[22];
                _initializePackages_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_initializePackages_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _initializePackages_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[17].call((Object)systemClasses, new _initializePackages_closure1(PackageHelperImpl.class, PackageHelperImpl.class, urls, jigsaw));
        URL url = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[18].call(urls.get()), Iterator.class);
        while (iterator.hasNext()) {
            url = (URL)ScriptBytecodeAdapter.castToType(iterator.next(), URL.class);
            Collection packageNames = (Collection)ScriptBytecodeAdapter.castToType(callSiteArray[19].callStatic(PackageHelperImpl.class, url), Collection.class);
            if (!DefaultTypeTransformation.booleanUnbox(packageNames)) continue;
            callSiteArray[20].callStatic(PackageHelperImpl.class, packageNames, url, rootPackages);
        }
        if (DefaultTypeTransformation.booleanUnbox(jigsaw.get())) {
            URL jigsawURL = (URL)ScriptBytecodeAdapter.castToType(callSiteArray[21].call(callSiteArray[22].call(URI.class, "jrt:/")), URL.class);
            public class _initializePackages_closure2
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _initializePackages_closure2(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _initializePackages_closure2.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object isPackage, Object name) {
                    CallSite[] callSiteArray = _initializePackages_closure2.$getCallSiteArray();
                    return DefaultTypeTransformation.booleanUnbox(isPackage) && DefaultTypeTransformation.booleanUnbox(name);
                }

                public Object call(Object isPackage, Object name) {
                    CallSite[] callSiteArray = _initializePackages_closure2.$getCallSiteArray();
                    return callSiteArray[0].callCurrent(this, isPackage, name);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _initializePackages_closure2.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "doCall";
                    return new CallSiteArray(_initializePackages_closure2.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _initializePackages_closure2.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            Set jigsawPackages = (Set)ScriptBytecodeAdapter.castToType(callSiteArray[23].callStatic(PackageHelperImpl.class, jigsawURL, new _initializePackages_closure2(PackageHelperImpl.class, PackageHelperImpl.class)), Set.class);
            callSiteArray[24].callStatic(PackageHelperImpl.class, jigsawPackages, jigsawURL, rootPackages);
        }
        return rootPackages;
    }

    private static Set<String> getPackagesAndClassesFromJigsaw(URL jigsawURL, Closure<Boolean> predicate) {
        CallSite[] callSiteArray = PackageHelperImpl.$getCallSiteArray();
        Object shell = callSiteArray[25].callConstructor(GroovyShell.class);
        callSiteArray[26].call(shell, "predicate", predicate);
        String jigsawURLString = ShortTypeHandling.castToString(callSiteArray[27].call(jigsawURL));
        callSiteArray[28].call(shell, "jigsawURLString", jigsawURLString);
        callSiteArray[29].call(shell, "import java.nio.file.*\n\ndef fs = FileSystems.newFileSystem(URI.create(jigsawURLString), [:])\n\nresult = [] as Set\n\ndef filterPackageName(Path path) {\n    def elems = \"$path\".split('/')\n\n    if (elems && elems.length > 2) {\n        // remove e.g. 'modules/java.base/\n        elems = elems[2..<elems.length]\n\n        def name = elems.join('.')\n        if (predicate(true, name)) {\n            result << name\n        }\n    }\n}\n\ndef filterClassName(Path path) {\n    def elems = \"$path\".split('/')\n\n    if (elems && elems.length > 2) {\n        // remove e.g. 'modules/java.base/\n        elems = elems[2..<elems.length]\n\n        def name = elems.join('.')\n        if (name.endsWith('.class')) {\n            name = name.substring(0, name.lastIndexOf('.'))\n            if (predicate(false, name)) {\n                result << name\n            }\n        }\n    }\n}\n\n// walk each file and directory, possibly storing directories as packages, and files as classes\nFiles.walkFileTree(fs.getPath('modules'),\n        [preVisitDirectory: { dir, attrs -> filterPackageName(dir); FileVisitResult.CONTINUE },\n         visitFile: { file, attrs -> filterClassName(file); FileVisitResult.CONTINUE}\n        ]\n            as SimpleFileVisitor)\n");
        Set jigsawPackages = (Set)ScriptBytecodeAdapter.castToType(callSiteArray[30].call(shell, "result"), Set.class);
        return jigsawPackages;
    }

    /*
     * WARNING - void declaration
     */
    public static Object mergeNewPackages(Collection<String> packageNames, URL url, Map<String, CachedPackage> rootPackages) {
        void var2_2;
        Reference<URL> url2 = new Reference<URL>(url);
        Reference<void> rootPackages2 = new Reference<void>(var2_2);
        CallSite[] callSiteArray = PackageHelperImpl.$getCallSiteArray();
        Reference<Object> tokenizer = new Reference<Object>(null);
        StringTokenizer cfr_ignored_0 = tokenizer.get();
        public class _mergeNewPackages_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference tokenizer;
            private /* synthetic */ Reference rootPackages;
            private /* synthetic */ Reference url;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _mergeNewPackages_closure3(Object _outerInstance, Object _thisObject, Reference tokenizer, Reference rootPackages, Reference url) {
                Reference reference;
                Reference reference2;
                Reference reference3;
                CallSite[] callSiteArray = _mergeNewPackages_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.tokenizer = reference3 = tokenizer;
                this.rootPackages = reference2 = rootPackages;
                this.url = reference = url;
            }

            public Object doCall(String packname) {
                CachedPackage cachedPackage;
                CallSite[] callSiteArray = _mergeNewPackages_closure3.$getCallSiteArray();
                Object object = callSiteArray[0].callConstructor(StringTokenizer.class, packname, ".");
                this.tokenizer.set((StringTokenizer)ScriptBytecodeAdapter.castToType(object, StringTokenizer.class));
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call(this.tokenizer.get()))) {
                    return null;
                }
                String rootname = ShortTypeHandling.castToString(callSiteArray[2].call(this.tokenizer.get()));
                CachedPackage cp = null;
                CachedPackage childp = null;
                cp = cachedPackage = (CachedPackage)ScriptBytecodeAdapter.asType(callSiteArray[3].call(this.rootPackages.get(), rootname, null), CachedPackage.class);
                if (ScriptBytecodeAdapter.compareEqual(cp, null)) {
                    Object object2 = callSiteArray[4].callConstructor(CachedPackage.class, rootname, ScriptBytecodeAdapter.createPojoWrapper((Set)ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[]{this.url.get()}), Set.class), Set.class));
                    cp = (CachedPackage)ScriptBytecodeAdapter.castToType(object2, CachedPackage.class);
                    callSiteArray[5].call(this.rootPackages.get(), rootname, cp);
                }
                while (DefaultTypeTransformation.booleanUnbox(callSiteArray[6].call(this.tokenizer.get()))) {
                    CachedPackage cachedPackage2;
                    CachedPackage cachedPackage3;
                    String packbasename = ShortTypeHandling.castToString(callSiteArray[7].call(this.tokenizer.get()));
                    if (ScriptBytecodeAdapter.compareEqual(callSiteArray[8].callGetProperty(cp), null)) {
                        Object object3 = callSiteArray[9].callConstructor(HashMap.class, 1);
                        ScriptBytecodeAdapter.setProperty(object3, null, cp, "childPackages");
                    }
                    childp = cachedPackage3 = (CachedPackage)ScriptBytecodeAdapter.asType(callSiteArray[10].call(callSiteArray[11].callGetProperty(cp), packbasename, null), CachedPackage.class);
                    if (ScriptBytecodeAdapter.compareEqual(childp, null)) {
                        Set urllist = (Set)ScriptBytecodeAdapter.castToType(callSiteArray[12].callConstructor(HashSet.class, 1), Set.class);
                        callSiteArray[13].call((Object)urllist, this.url.get());
                        Object object4 = callSiteArray[14].callConstructor(CachedPackage.class, packbasename, urllist);
                        childp = (CachedPackage)ScriptBytecodeAdapter.castToType(object4, CachedPackage.class);
                        callSiteArray[15].call(callSiteArray[16].callGetProperty(cp), packbasename, childp);
                    } else {
                        callSiteArray[17].call(callSiteArray[18].callGetProperty(childp), this.url.get());
                    }
                    cp = cachedPackage2 = childp;
                }
                return null;
            }

            public Object call(String packname) {
                CallSite[] callSiteArray = _mergeNewPackages_closure3.$getCallSiteArray();
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return callSiteArray[19].callCurrent((GroovyObject)this, packname);
                }
                return this.doCall(packname);
            }

            public StringTokenizer getTokenizer() {
                CallSite[] callSiteArray = _mergeNewPackages_closure3.$getCallSiteArray();
                return (StringTokenizer)ScriptBytecodeAdapter.castToType(this.tokenizer.get(), StringTokenizer.class);
            }

            public Map getRootPackages() {
                CallSite[] callSiteArray = _mergeNewPackages_closure3.$getCallSiteArray();
                return (Map)ScriptBytecodeAdapter.castToType(this.rootPackages.get(), Map.class);
            }

            public URL getUrl() {
                CallSite[] callSiteArray = _mergeNewPackages_closure3.$getCallSiteArray();
                return (URL)ScriptBytecodeAdapter.castToType(this.url.get(), URL.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _mergeNewPackages_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "<$constructor$>";
                stringArray[1] = "hasMoreTokens";
                stringArray[2] = "nextToken";
                stringArray[3] = "get";
                stringArray[4] = "<$constructor$>";
                stringArray[5] = "put";
                stringArray[6] = "hasMoreTokens";
                stringArray[7] = "nextToken";
                stringArray[8] = "childPackages";
                stringArray[9] = "<$constructor$>";
                stringArray[10] = "get";
                stringArray[11] = "childPackages";
                stringArray[12] = "<$constructor$>";
                stringArray[13] = "add";
                stringArray[14] = "<$constructor$>";
                stringArray[15] = "put";
                stringArray[16] = "childPackages";
                stringArray[17] = "add";
                stringArray[18] = "sources";
                stringArray[19] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[20];
                _mergeNewPackages_closure3.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_mergeNewPackages_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _mergeNewPackages_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return callSiteArray[31].call(packageNames, new _mergeNewPackages_closure3(PackageHelperImpl.class, PackageHelperImpl.class, tokenizer, rootPackages2, url2));
    }

    public static Collection<String> getPackageNames(URL url) {
        CallSite[] callSiteArray = PackageHelperImpl.$getCallSiteArray();
        String path = ShortTypeHandling.castToString(callSiteArray[32].call(URLDecoder.class, callSiteArray[33].call(url), "UTF-8"));
        File urlfile = (File)ScriptBytecodeAdapter.castToType(callSiteArray[34].callConstructor(File.class, path), File.class);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[35].call(urlfile))) {
            Set packnames = (Set)ScriptBytecodeAdapter.castToType(callSiteArray[36].callConstructor(HashSet.class), Set.class);
            callSiteArray[37].callStatic(PackageHelperImpl.class, urlfile, "", packnames);
            return packnames;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[38].call(callSiteArray[39].callGetProperty(urlfile), ".jar"))) {
            JarFile jf = (JarFile)ScriptBytecodeAdapter.castToType(callSiteArray[40].callConstructor(JarFile.class, urlfile), JarFile.class);
            Collection collection = (Collection)ScriptBytecodeAdapter.castToType(callSiteArray[41].callStatic(PackageHelperImpl.class, jf), Collection.class);
            try {
                return collection;
            }
            catch (ZipException ze) {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[42].callGetProperty(LOG))) {
                    callSiteArray[43].call(ze);
                }
                callSiteArray[44].call((Object)LOG, new GStringImpl(new Object[]{callSiteArray[45].call(url), callSiteArray[46].call(ze)}, new String[]{"Error opening zipfile : '", "',  ", ""}));
            }
            catch (FileNotFoundException fnfe) {
                callSiteArray[47].call((Object)LOG, new GStringImpl(new Object[]{callSiteArray[48].call(url), callSiteArray[49].call(fnfe)}, new String[]{"Error opening file : '", "',  ", ""}));
            }
        }
        return ScriptBytecodeAdapter.createList(new Object[0]);
    }

    public static Collection<String> collectPackageNamesFromFolderRecursive(File directory, String prefix, Set<String> packnames) {
        CallSite[] callSiteArray = PackageHelperImpl.$getCallSiteArray();
        Object[] files = (File[])ScriptBytecodeAdapter.castToType(callSiteArray[50].call(directory), File[].class);
        boolean packageAdded = false;
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            int i = 0;
            while (ScriptBytecodeAdapter.compareNotEqual(files, null) && ScriptBytecodeAdapter.compareLessThan(i, callSiteArray[51].callGetProperty(files))) {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[52].call(callSiteArray[53].call((Object)files, i)))) {
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[54].call(callSiteArray[55].callGetProperty(callSiteArray[56].call((Object)files, i)), "."))) {
                        return (Collection)ScriptBytecodeAdapter.castToType(null, Collection.class);
                    }
                    String optionalDot = DefaultTypeTransformation.booleanUnbox(prefix) ? "." : "";
                    callSiteArray[57].callStatic(PackageHelperImpl.class, callSiteArray[58].call((Object)files, i), callSiteArray[59].call(callSiteArray[60].call((Object)prefix, optionalDot), callSiteArray[61].callGetProperty(callSiteArray[62].call((Object)files, i))), packnames);
                } else if (!packageAdded && DefaultTypeTransformation.booleanUnbox(callSiteArray[63].call(callSiteArray[64].callGetProperty(callSiteArray[65].call((Object)files, i)), CLASS_SUFFIX))) {
                    boolean bl;
                    packageAdded = bl = true;
                    if (DefaultTypeTransformation.booleanUnbox(prefix)) {
                        callSiteArray[66].call(packnames, prefix);
                    }
                }
                int n = i;
                i = DefaultTypeTransformation.intUnbox(callSiteArray[67].call(n));
            }
        } else {
            int i = 0;
            while (ScriptBytecodeAdapter.compareNotEqual(files, null) && ScriptBytecodeAdapter.compareLessThan(i, callSiteArray[68].callGetProperty(files))) {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[69].call(BytecodeInterface8.objectArrayGet(files, i)))) {
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[70].call(callSiteArray[71].callGetProperty(BytecodeInterface8.objectArrayGet(files, i)), "."))) {
                        return (Collection)ScriptBytecodeAdapter.castToType(null, Collection.class);
                    }
                    String optionalDot = DefaultTypeTransformation.booleanUnbox(prefix) ? "." : "";
                    callSiteArray[72].callStatic(PackageHelperImpl.class, BytecodeInterface8.objectArrayGet(files, i), callSiteArray[73].call(callSiteArray[74].call((Object)prefix, optionalDot), callSiteArray[75].callGetProperty(BytecodeInterface8.objectArrayGet(files, i))), packnames);
                } else if (!packageAdded && DefaultTypeTransformation.booleanUnbox(callSiteArray[76].call(callSiteArray[77].callGetProperty(BytecodeInterface8.objectArrayGet(files, i)), CLASS_SUFFIX))) {
                    boolean bl;
                    packageAdded = bl = true;
                    if (DefaultTypeTransformation.booleanUnbox(prefix)) {
                        callSiteArray[78].call(packnames, prefix);
                    }
                }
                int n = i;
                int cfr_ignored_0 = n + 1;
            }
        }
        return (Collection)ScriptBytecodeAdapter.castToType(null, Collection.class);
    }

    public static Collection<String> getPackageNamesFromJar(JarFile jf) {
        CallSite[] callSiteArray = PackageHelperImpl.$getCallSiteArray();
        Set packnames = (Set)ScriptBytecodeAdapter.castToType(callSiteArray[79].callConstructor(HashSet.class), Set.class);
        Enumeration e = (Enumeration)ScriptBytecodeAdapter.castToType(callSiteArray[80].call(jf), Enumeration.class);
        while (DefaultTypeTransformation.booleanUnbox(callSiteArray[81].call(e))) {
            JarEntry entry = (JarEntry)ScriptBytecodeAdapter.castToType(callSiteArray[82].call(e), JarEntry.class);
            if (ScriptBytecodeAdapter.compareEqual(entry, null)) continue;
            String name = ShortTypeHandling.castToString(callSiteArray[83].callGetProperty(entry));
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[84].call((Object)name, CLASS_SUFFIX))) continue;
            String fullname = ShortTypeHandling.castToString(callSiteArray[85].call(callSiteArray[86].call(name, "/", "."), 0, callSiteArray[87].call(callSiteArray[88].call(name), callSiteArray[89].call(CLASS_SUFFIX))));
            if (!ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[90].call((Object)fullname, "."), -1)) continue;
            callSiteArray[91].call((Object)packnames, callSiteArray[92].call(fullname, 0, callSiteArray[93].call((Object)fullname, ".")));
        }
        return packnames;
    }

    @Override
    public Set<String> getContents(String packagename) {
        String string;
        String string2;
        if (!DefaultTypeTransformation.booleanUnbox(this.rootPackages)) {
            return (Set)ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[0]), Set.class);
        }
        if (!DefaultTypeTransformation.booleanUnbox(packagename)) {
            public class _getContents_closure4
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;

                public _getContents_closure4(Object _outerInstance, Object _thisObject) {
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(String key, CachedPackage v) {
                    return key;
                }

                public Object call(String key, CachedPackage v) {
                    return this.doCall(key, v);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getContents_closure4.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }
            }
            return (Set)ScriptBytecodeAdapter.asType(DefaultGroovyMethods.collect(this.rootPackages, new _getContents_closure4(this, this)), Set.class);
        }
        String sanitizedPackageName = null;
        sanitizedPackageName = packagename.endsWith(".*") ? (string2 = StringGroovyMethods.getAt(packagename, new IntRange(true, 0, -3))) : (string = packagename);
        StringTokenizer tokenizer = new StringTokenizer(sanitizedPackageName, ".");
        CachedPackage cp = (CachedPackage)ScriptBytecodeAdapter.castToType(this.rootPackages.get(tokenizer.nextToken()), CachedPackage.class);
        while (cp != null && tokenizer.hasMoreTokens()) {
            CachedPackage cachedPackage;
            String token = tokenizer.nextToken();
            if (cp.getChildPackages() == null) {
                return (Set)ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[0]), Set.class);
            }
            cp = cachedPackage = cp.getChildPackages().get(token);
        }
        if (cp == null) {
            return (Set)ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[0]), Set.class);
        }
        TreeSet<String> children = new TreeSet<String>();
        if (DefaultTypeTransformation.booleanUnbox(cp.getChildPackages())) {
            public class _getContents_closure5
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;

                public _getContents_closure5(Object _outerInstance, Object _thisObject) {
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(String key, CachedPackage v) {
                    return key;
                }

                public Object call(String key, CachedPackage v) {
                    return this.doCall(key, v);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getContents_closure5.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }
            }
            children.addAll(DefaultGroovyMethods.collect(cp.getChildPackages(), new _getContents_closure5(this, this)));
        }
        if (cp.isChecked() && !cp.isContainsClasses()) {
            return children;
        }
        Set<String> classnames = PackageHelperImpl.getClassnames(cp.getSources(), sanitizedPackageName);
        boolean bl = true;
        cp.setChecked(bl);
        if (DefaultTypeTransformation.booleanUnbox(classnames)) {
            boolean bl2 = true;
            cp.setContainsClasses(bl2);
            children.addAll(classnames);
        }
        return children;
    }

    public static Set<String> getClassnames(Set<URL> urls, String packagename) {
        Reference<String> packagename2 = new Reference<String>(packagename);
        TreeSet<String> classes = new TreeSet<String>();
        String pathname = packagename2.get().replace(".", "/");
        Iterator<URL> it = urls.iterator();
        while (it.hasNext()) {
            URL url = it.next();
            if (ScriptBytecodeAdapter.compareEqual(url.getProtocol(), "jrt")) {
                public class _getClassnames_closure6
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference packagename;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;

                    public _getClassnames_closure6(Object _outerInstance, Object _thisObject, Reference packagename) {
                        super(_outerInstance, _thisObject);
                        Reference reference;
                        this.packagename = reference = packagename;
                    }

                    public Object doCall(boolean isPackage, String name) {
                        return !isPackage && name.startsWith(ShortTypeHandling.castToString(this.packagename.get()));
                    }

                    public Object call(boolean isPackage, String name) {
                        return this.doCall(isPackage, name);
                    }

                    public String getPackagename() {
                        return ShortTypeHandling.castToString(this.packagename.get());
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _getClassnames_closure6.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }
                }
                public class _getClassnames_closure7
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference packagename;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;

                    public _getClassnames_closure7(Object _outerInstance, Object _thisObject, Reference packagename) {
                        super(_outerInstance, _thisObject);
                        Reference reference;
                        this.packagename = reference = packagename;
                    }

                    public Object doCall(Object it) {
                        return StringGroovyMethods.minus((CharSequence)ScriptBytecodeAdapter.castToType(it, CharSequence.class), (Object)new GStringImpl(new Object[]{this.packagename.get()}, new String[]{"", "."}));
                    }

                    public String getPackagename() {
                        return ShortTypeHandling.castToString(this.packagename.get());
                    }

                    public Object call(Object args) {
                        return this.doCall(args);
                    }

                    @Override
                    public Object call() {
                        return this.doCall(null);
                    }

                    public Object doCall() {
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _getClassnames_closure7.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }
                }
                DefaultGroovyMethods.collect(PackageHelperImpl.getPackagesAndClassesFromJigsaw(url, new _getClassnames_closure6(PackageHelperImpl.class, PackageHelperImpl.class, packagename2)), classes, new _getClassnames_closure7(PackageHelperImpl.class, PackageHelperImpl.class, packagename2));
                continue;
            }
            File file = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
            if (file == null) continue;
            if (file.isDirectory()) {
                File packFolder = new File(file, pathname);
                if (!packFolder.isDirectory()) continue;
                Object[] files = packFolder.listFiles();
                int i = 0;
                while (files != null && i < files.length) {
                    if (((File)BytecodeInterface8.objectArrayGet(files, i)).isFile()) {
                        String filename = ((File)BytecodeInterface8.objectArrayGet(files, i)).getName();
                        if (filename.endsWith(CLASS_SUFFIX)) {
                            String name = filename.substring(0, filename.length() - CLASS_SUFFIX.length());
                            if (!(!StringGroovyMethods.matches((CharSequence)name, NAME_PATTERN))) {
                                classes.add(name);
                            }
                        }
                    }
                    int n = i;
                    int cfr_ignored_0 = n + 1;
                }
                continue;
            }
            if (!file.toString().endsWith(".jar")) continue;
            JarFile jf = new JarFile(file);
            try {
                Enumeration<JarEntry> e = jf.entries();
                while (e.hasMoreElements()) {
                    String string;
                    JarEntry entry = e.nextElement();
                    if (entry == null) continue;
                    String name = entry.getName();
                    if (!name.endsWith(CLASS_SUFFIX)) continue;
                    int lastslash = name.lastIndexOf("/");
                    if (ScriptBytecodeAdapter.compareEqual(lastslash, -1) || ScriptBytecodeAdapter.compareNotEqual(name.substring(0, lastslash), pathname)) continue;
                    name = string = name.substring(lastslash + 1, name.length() - CLASS_SUFFIX.length());
                    if (!StringGroovyMethods.matches((CharSequence)name, NAME_PATTERN)) continue;
                    classes.add(name);
                }
            }
            finally {
                jf.close();
            }
        }
        return classes;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != PackageHelperImpl.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    static {
        Object object = ScriptBytecodeAdapter.bitwiseNegate("^[A-Z][^.$_]+$");
        NAME_PATTERN = (Pattern)ScriptBytecodeAdapter.castToType(object, Pattern.class);
        Object object2 = PackageHelperImpl.$getCallSiteArray()[94].call(Logger.class, PackageHelperImpl.class);
        LOG = (Logger)ScriptBytecodeAdapter.castToType(object2, Logger.class);
    }

    public Map<String, CachedPackage> getRootPackages() {
        return this.rootPackages;
    }

    public void setRootPackages(Map<String, CachedPackage> map) {
        this.rootPackages = map;
    }

    public final ClassLoader getGroovyClassLoader() {
        return this.groovyClassLoader;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "valueOf";
        stringArray[1] = "get";
        stringArray[2] = "IMPORT_COMPLETION_PREFERENCE_KEY";
        stringArray[3] = "initializePackages";
        stringArray[4] = "addChangeListener";
        stringArray[5] = "key";
        stringArray[6] = "IMPORT_COMPLETION_PREFERENCE_KEY";
        stringArray[7] = "valueOf";
        stringArray[8] = "getNewValue";
        stringArray[9] = "initializePackages";
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "<$constructor$>";
        stringArray[12] = "debug";
        stringArray[13] = "plus";
        stringArray[14] = "addAll";
        stringArray[15] = "URLs";
        stringArray[16] = "parent";
        stringArray[17] = "each";
        stringArray[18] = "iterator";
        stringArray[19] = "getPackageNames";
        stringArray[20] = "mergeNewPackages";
        stringArray[21] = "toURL";
        stringArray[22] = "create";
        stringArray[23] = "getPackagesAndClassesFromJigsaw";
        stringArray[24] = "mergeNewPackages";
        stringArray[25] = "<$constructor$>";
        stringArray[26] = "setProperty";
        stringArray[27] = "toString";
        stringArray[28] = "setProperty";
        stringArray[29] = "evaluate";
        stringArray[30] = "getProperty";
        stringArray[31] = "each";
        stringArray[32] = "decode";
        stringArray[33] = "getFile";
        stringArray[34] = "<$constructor$>";
        stringArray[35] = "isDirectory";
        stringArray[36] = "<$constructor$>";
        stringArray[37] = "collectPackageNamesFromFolderRecursive";
        stringArray[38] = "endsWith";
        stringArray[39] = "path";
        stringArray[40] = "<$constructor$>";
        stringArray[41] = "getPackageNamesFromJar";
        stringArray[42] = "debugEnabled";
        stringArray[43] = "printStackTrace";
        stringArray[44] = "debug";
        stringArray[45] = "getFile";
        stringArray[46] = "toString";
        stringArray[47] = "debug";
        stringArray[48] = "getFile";
        stringArray[49] = "toString";
        stringArray[50] = "listFiles";
        stringArray[51] = "length";
        stringArray[52] = "isDirectory";
        stringArray[53] = "getAt";
        stringArray[54] = "startsWith";
        stringArray[55] = "name";
        stringArray[56] = "getAt";
        stringArray[57] = "collectPackageNamesFromFolderRecursive";
        stringArray[58] = "getAt";
        stringArray[59] = "plus";
        stringArray[60] = "plus";
        stringArray[61] = "name";
        stringArray[62] = "getAt";
        stringArray[63] = "endsWith";
        stringArray[64] = "name";
        stringArray[65] = "getAt";
        stringArray[66] = "add";
        stringArray[67] = "next";
        stringArray[68] = "length";
        stringArray[69] = "isDirectory";
        stringArray[70] = "startsWith";
        stringArray[71] = "name";
        stringArray[72] = "collectPackageNamesFromFolderRecursive";
        stringArray[73] = "plus";
        stringArray[74] = "plus";
        stringArray[75] = "name";
        stringArray[76] = "endsWith";
        stringArray[77] = "name";
        stringArray[78] = "add";
        stringArray[79] = "<$constructor$>";
        stringArray[80] = "entries";
        stringArray[81] = "hasMoreElements";
        stringArray[82] = "nextElement";
        stringArray[83] = "name";
        stringArray[84] = "endsWith";
        stringArray[85] = "substring";
        stringArray[86] = "replace";
        stringArray[87] = "minus";
        stringArray[88] = "length";
        stringArray[89] = "length";
        stringArray[90] = "lastIndexOf";
        stringArray[91] = "add";
        stringArray[92] = "substring";
        stringArray[93] = "lastIndexOf";
        stringArray[94] = "create";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[95];
        PackageHelperImpl.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(PackageHelperImpl.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = PackageHelperImpl.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

