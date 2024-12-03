/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.ivy.Ivy
 *  org.apache.ivy.core.cache.ResolutionCacheManager
 *  org.apache.ivy.core.event.IvyListener
 *  org.apache.ivy.core.event.download.PrepareDownloadEvent
 *  org.apache.ivy.core.event.resolve.StartResolveEvent
 *  org.apache.ivy.core.module.descriptor.Configuration
 *  org.apache.ivy.core.module.descriptor.DefaultDependencyArtifactDescriptor
 *  org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor
 *  org.apache.ivy.core.module.descriptor.DefaultExcludeRule
 *  org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor
 *  org.apache.ivy.core.module.id.ArtifactId
 *  org.apache.ivy.core.module.id.ModuleId
 *  org.apache.ivy.core.module.id.ModuleRevisionId
 *  org.apache.ivy.core.report.ArtifactDownloadReport
 *  org.apache.ivy.core.report.ResolveReport
 *  org.apache.ivy.core.resolve.ResolveOptions
 *  org.apache.ivy.core.settings.IvySettings
 *  org.apache.ivy.plugins.matcher.ExactPatternMatcher
 *  org.apache.ivy.plugins.matcher.PatternMatcher
 *  org.apache.ivy.plugins.resolver.ChainResolver
 *  org.apache.ivy.plugins.resolver.IBiblioResolver
 *  org.apache.ivy.util.DefaultMessageLogger
 *  org.apache.ivy.util.Message
 */
package groovy.grape;

import groovy.grape.GrapeEngine;
import groovy.grape.IvyGrabRecord;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import groovy.lang.Reference;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.cache.ResolutionCacheManager;
import org.apache.ivy.core.event.IvyListener;
import org.apache.ivy.core.event.download.PrepareDownloadEvent;
import org.apache.ivy.core.event.resolve.StartResolveEvent;
import org.apache.ivy.core.module.descriptor.Configuration;
import org.apache.ivy.core.module.descriptor.DefaultDependencyArtifactDescriptor;
import org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor;
import org.apache.ivy.core.module.descriptor.DefaultExcludeRule;
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor;
import org.apache.ivy.core.module.id.ArtifactId;
import org.apache.ivy.core.module.id.ModuleId;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ArtifactDownloadReport;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.matcher.ExactPatternMatcher;
import org.apache.ivy.plugins.matcher.PatternMatcher;
import org.apache.ivy.plugins.resolver.ChainResolver;
import org.apache.ivy.plugins.resolver.IBiblioResolver;
import org.apache.ivy.util.DefaultMessageLogger;
import org.apache.ivy.util.Message;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.reflection.ReflectionUtils;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.metaclass.MetaClassRegistryImpl;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class GrapeIvy
implements GrapeEngine,
GroovyObject {
    private static final int DEFAULT_DEPTH = 3;
    private final Object exclusiveGrabArgs;
    private boolean enableGrapes;
    private Ivy ivyInstance;
    private Set<String> resolvedDependencies;
    private Set<String> downloadedArtifacts;
    private Map<ClassLoader, Set<IvyGrabRecord>> loadedDeps;
    private Set<IvyGrabRecord> grabRecordsForCurrDependencies;
    private IvySettings settings;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public GrapeIvy() {
        boolean bl;
        MetaClass metaClass;
        Object object;
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        this.exclusiveGrabArgs = object = callSiteArray[0].call(ScriptBytecodeAdapter.createList(new Object[]{ScriptBytecodeAdapter.createList(new Object[]{"group", "groupId", "organisation", "organization", "org"}), ScriptBytecodeAdapter.createList(new Object[]{"module", "artifactId", "artifact"}), ScriptBytecodeAdapter.createList(new Object[]{"version", "revision", "rev"}), ScriptBytecodeAdapter.createList(new Object[]{"conf", "scope", "configuration"})}), ScriptBytecodeAdapter.createMap(new Object[0]), new _closure1(this, this));
        Object object2 = callSiteArray[1].callConstructor(WeakHashMap.class);
        this.loadedDeps = (Map)ScriptBytecodeAdapter.castToType(object2, Map.class);
        Object object3 = callSiteArray[2].callConstructor(LinkedHashSet.class);
        this.grabRecordsForCurrDependencies = (Set)ScriptBytecodeAdapter.castToType(object3, Set.class);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        if (this.enableGrapes) {
            return;
        }
        Object object4 = callSiteArray[3].callConstructor(DefaultMessageLogger.class, ScriptBytecodeAdapter.createPojoWrapper(DefaultTypeTransformation.intUnbox(ScriptBytecodeAdapter.asType(callSiteArray[4].call(System.class, "ivy.message.logger.level", "-1"), Integer.TYPE)), Integer.TYPE));
        ScriptBytecodeAdapter.setProperty(object4, null, Message.class, "defaultLogger");
        Object object5 = callSiteArray[5].callConstructor(IvySettings.class);
        this.settings = (IvySettings)ScriptBytecodeAdapter.castToType(object5, IvySettings.class);
        Object grapeConfig = null;
        if (BytecodeInterface8.disabledStandardMetaClass()) {
            Object object6;
            grapeConfig = object6 = callSiteArray[6].callCurrent(this);
        } else {
            File file = this.getLocalGrapeConfig();
            grapeConfig = file;
        }
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[7].call(grapeConfig))) {
            Object object7;
            grapeConfig = object7 = callSiteArray[8].call(GrapeIvy.class, "defaultGrapeConfig.xml");
        }
        try {
            callSiteArray[9].call((Object)this.settings, grapeConfig);
        }
        catch (ParseException ex) {
            Object object8;
            Object configLocation = grapeConfig instanceof File ? callSiteArray[10].callGetProperty(grapeConfig) : callSiteArray[11].call(grapeConfig);
            callSiteArray[12].call(callSiteArray[13].callGetProperty(System.class), callSiteArray[14].call((Object)new GStringImpl(new Object[]{configLocation}, new String[]{"Local Ivy config file '", "' appears corrupt - ignoring it and using default config instead\nError was: "}), callSiteArray[15].callGetProperty(ex)));
            grapeConfig = object8 = callSiteArray[16].call(GrapeIvy.class, "defaultGrapeConfig.xml");
            callSiteArray[17].call((Object)this.settings, grapeConfig);
        }
        if (BytecodeInterface8.disabledStandardMetaClass()) {
            Object object9 = callSiteArray[18].callCurrent(this);
            ScriptBytecodeAdapter.setProperty(object9, null, this.settings, "defaultCache");
        } else {
            File file = this.getGrapeCacheDir();
            ScriptBytecodeAdapter.setProperty(file, null, this.settings, "defaultCache");
        }
        callSiteArray[19].call(this.settings, "ivy.default.configuration.m2compatible", "true");
        Object object10 = callSiteArray[20].call(Ivy.class, this.settings);
        this.ivyInstance = (Ivy)ScriptBytecodeAdapter.castToType(object10, Ivy.class);
        List list = ScriptBytecodeAdapter.createList(new Object[0]);
        this.resolvedDependencies = (Set)ScriptBytecodeAdapter.castToType(list, Set.class);
        List list2 = ScriptBytecodeAdapter.createList(new Object[0]);
        this.downloadedArtifacts = (Set)ScriptBytecodeAdapter.castToType(list2, Set.class);
        this.enableGrapes = bl = true;
    }

    public File getGroovyRoot() {
        Object object;
        Object object2;
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        String root = ShortTypeHandling.castToString(callSiteArray[21].call(System.class, "groovy.root"));
        Object groovyRoot = null;
        groovyRoot = ScriptBytecodeAdapter.compareEqual(root, null) ? (object2 = callSiteArray[22].callConstructor(File.class, callSiteArray[23].call(System.class, "user.home"), ".groovy")) : (object = callSiteArray[24].callConstructor(File.class, root));
        try {
            Object object3;
            groovyRoot = object3 = callSiteArray[25].callGetProperty(groovyRoot);
        }
        catch (IOException e) {
        }
        return (File)ScriptBytecodeAdapter.castToType(groovyRoot, File.class);
    }

    public File getLocalGrapeConfig() {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        String grapeConfig = ShortTypeHandling.castToString(callSiteArray[26].call(System.class, "grape.config"));
        if (DefaultTypeTransformation.booleanUnbox(grapeConfig)) {
            return (File)ScriptBytecodeAdapter.castToType(callSiteArray[27].callConstructor(File.class, grapeConfig), File.class);
        }
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return (File)ScriptBytecodeAdapter.castToType(callSiteArray[28].callConstructor(File.class, callSiteArray[29].callCurrent(this), "grapeConfig.xml"), File.class);
        }
        return (File)ScriptBytecodeAdapter.castToType(callSiteArray[30].callConstructor(File.class, this.getGrapeDir(), "grapeConfig.xml"), File.class);
    }

    public File getGrapeDir() {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        String root = ShortTypeHandling.castToString(callSiteArray[31].call(System.class, "grape.root"));
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual(root, null)) {
                return (File)ScriptBytecodeAdapter.castToType(callSiteArray[32].callCurrent(this), File.class);
            }
        } else if (ScriptBytecodeAdapter.compareEqual(root, null)) {
            return this.getGroovyRoot();
        }
        File grapeRoot = (File)ScriptBytecodeAdapter.castToType(callSiteArray[33].callConstructor(File.class, root), File.class);
        try {
            Object object = callSiteArray[34].callGetProperty(grapeRoot);
            grapeRoot = (File)ScriptBytecodeAdapter.castToType(object, File.class);
        }
        catch (IOException e) {
        }
        return grapeRoot;
    }

    public File getGrapeCacheDir() {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        File cache = null;
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[35].callConstructor(File.class, callSiteArray[36].callCurrent(this), "grapes");
            cache = (File)ScriptBytecodeAdapter.castToType(object, File.class);
        } else {
            Object object = callSiteArray[37].callConstructor(File.class, this.getGrapeDir(), "grapes");
            cache = (File)ScriptBytecodeAdapter.castToType(object, File.class);
        }
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[38].call(cache))) {
            callSiteArray[39].call(cache);
        } else if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[40].call(cache))) {
            throw (Throwable)callSiteArray[41].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{cache}, new String[]{"The grape cache dir ", " is not a directory"}));
        }
        return cache;
    }

    public Object chooseClassLoader(Map args) {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        Object loader = callSiteArray[42].callGetProperty(args);
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[43].callCurrent((GroovyObject)this, loader))) {
            Object object;
            Object object2;
            Object object3 = callSiteArray[45].callGetPropertySafe(callSiteArray[46].callGetProperty(args));
            loader = object2 = callSiteArray[44].callGetPropertySafe(DefaultTypeTransformation.booleanUnbox(object3) ? object3 : callSiteArray[47].call(ReflectionUtils.class, DefaultTypeTransformation.booleanUnbox(object = callSiteArray[48].callGetProperty(args)) ? object : Integer.valueOf(1)));
            while (DefaultTypeTransformation.booleanUnbox(loader) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[49].callCurrent((GroovyObject)this, loader))) {
                Object object4;
                loader = object4 = callSiteArray[50].callGetProperty(loader);
            }
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[51].callCurrent((GroovyObject)this, loader))) {
                throw (Throwable)callSiteArray[52].callConstructor(RuntimeException.class, "No suitable ClassLoader found for grab");
            }
        }
        return loader;
    }

    private boolean isValidTargetClassLoader(Object loader) {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        return DefaultTypeTransformation.booleanUnbox(callSiteArray[53].callCurrent((GroovyObject)this, callSiteArray[54].callGetPropertySafe(loader)));
    }

    private boolean isValidTargetClassLoaderClass(Class loaderClass) {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return ScriptBytecodeAdapter.compareNotEqual(loaderClass, null) && (ScriptBytecodeAdapter.compareEqual(callSiteArray[55].callGetProperty(loaderClass), "groovy.lang.GroovyClassLoader") || ScriptBytecodeAdapter.compareEqual(callSiteArray[56].callGetProperty(loaderClass), "org.codehaus.groovy.tools.RootLoader") || DefaultTypeTransformation.booleanUnbox(callSiteArray[57].callCurrent((GroovyObject)this, callSiteArray[58].callGetProperty(loaderClass))));
        }
        return ScriptBytecodeAdapter.compareNotEqual(loaderClass, null) && (ScriptBytecodeAdapter.compareEqual(callSiteArray[59].callGetProperty(loaderClass), "groovy.lang.GroovyClassLoader") || ScriptBytecodeAdapter.compareEqual(callSiteArray[60].callGetProperty(loaderClass), "org.codehaus.groovy.tools.RootLoader") || DefaultTypeTransformation.booleanUnbox(callSiteArray[61].callCurrent((GroovyObject)this, callSiteArray[62].callGetProperty(loaderClass))));
    }

    public IvyGrabRecord createGrabRecord(Map deps) {
        Object object;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        Object object6;
        Object object7;
        Object object8;
        Object object9;
        Object object10;
        Object object11;
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        Object object12 = callSiteArray[63].callGetProperty(deps);
        String module = ShortTypeHandling.castToString(DefaultTypeTransformation.booleanUnbox(object12) ? object12 : (DefaultTypeTransformation.booleanUnbox(object11 = callSiteArray[64].callGetProperty(deps)) ? object11 : callSiteArray[65].callGetProperty(deps)));
        if (!DefaultTypeTransformation.booleanUnbox(module)) {
            throw (Throwable)callSiteArray[66].callConstructor(RuntimeException.class, "grab requires at least a module: or artifactId: or artifact: argument");
        }
        Object object13 = callSiteArray[67].callGetProperty(deps);
        String groupId = ShortTypeHandling.castToString(DefaultTypeTransformation.booleanUnbox(object13) ? object13 : (DefaultTypeTransformation.booleanUnbox(object10 = callSiteArray[68].callGetProperty(deps)) ? object10 : (DefaultTypeTransformation.booleanUnbox(object9 = callSiteArray[69].callGetProperty(deps)) ? object9 : (DefaultTypeTransformation.booleanUnbox(object8 = callSiteArray[70].callGetProperty(deps)) ? object8 : (DefaultTypeTransformation.booleanUnbox(object7 = callSiteArray[71].callGetProperty(deps)) ? object7 : "")))));
        Object object14 = callSiteArray[72].callGetProperty(deps);
        String ext = ShortTypeHandling.castToString(DefaultTypeTransformation.booleanUnbox(object14) ? object14 : (DefaultTypeTransformation.booleanUnbox(object6 = callSiteArray[73].callGetProperty(deps)) ? object6 : ""));
        Object object15 = callSiteArray[74].callGetProperty(deps);
        String type = ShortTypeHandling.castToString(DefaultTypeTransformation.booleanUnbox(object15) ? object15 : "");
        Object object16 = callSiteArray[75].callGetProperty(deps);
        String version = ShortTypeHandling.castToString(DefaultTypeTransformation.booleanUnbox(object16) ? object16 : (DefaultTypeTransformation.booleanUnbox(object5 = callSiteArray[76].callGetProperty(deps)) ? object5 : (DefaultTypeTransformation.booleanUnbox(object4 = callSiteArray[77].callGetProperty(deps)) ? object4 : "*")));
        if (ScriptBytecodeAdapter.compareEqual("*", version)) {
            String string;
            version = string = "latest.default";
        }
        ModuleRevisionId mrid = (ModuleRevisionId)ScriptBytecodeAdapter.castToType(callSiteArray[78].call(ModuleRevisionId.class, groupId, module, version), ModuleRevisionId.class);
        boolean force = DefaultTypeTransformation.booleanUnbox(DefaultTypeTransformation.booleanUnbox(callSiteArray[79].call((Object)deps, "force")) ? callSiteArray[80].callGetProperty(deps) : Boolean.valueOf(true));
        boolean changing = DefaultTypeTransformation.booleanUnbox(DefaultTypeTransformation.booleanUnbox(callSiteArray[81].call((Object)deps, "changing")) ? callSiteArray[82].callGetProperty(deps) : Boolean.valueOf(false));
        boolean transitive = DefaultTypeTransformation.booleanUnbox(DefaultTypeTransformation.booleanUnbox(callSiteArray[83].call((Object)deps, "transitive")) ? callSiteArray[84].callGetProperty(deps) : Boolean.valueOf(true));
        Object object17 = callSiteArray[85].callGetProperty(deps);
        Object conf = DefaultTypeTransformation.booleanUnbox(object17) ? object17 : (DefaultTypeTransformation.booleanUnbox(object3 = callSiteArray[86].callGetProperty(deps)) ? object3 : (DefaultTypeTransformation.booleanUnbox(object2 = callSiteArray[87].callGetProperty(deps)) ? object2 : ScriptBytecodeAdapter.createList(new Object[]{"default"})));
        if (conf instanceof String) {
            Object object18;
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[88].call(conf, "[")) && DefaultTypeTransformation.booleanUnbox(callSiteArray[89].call(conf, "]"))) {
                Object object19;
                conf = object19 = callSiteArray[90].call(conf, ScriptBytecodeAdapter.createRange(1, -2, true));
            }
            conf = object18 = callSiteArray[91].call(callSiteArray[92].call(conf, ","));
        }
        Object classifier = DefaultTypeTransformation.booleanUnbox(object = callSiteArray[93].callGetProperty(deps)) ? object : null;
        return (IvyGrabRecord)ScriptBytecodeAdapter.castToType(callSiteArray[94].callConstructor(IvyGrabRecord.class, ScriptBytecodeAdapter.createMap(new Object[]{"mrid", mrid, "conf", conf, "changing", changing, "transitive", transitive, "force", force, "classifier", classifier, "ext", ext, "type", type})), IvyGrabRecord.class);
    }

    @Override
    public Object grab(String endorsedModule) {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        return callSiteArray[95].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"group", "groovy.endorsed", "module", endorsedModule, "version", callSiteArray[96].callGetProperty(GroovySystem.class)}));
    }

    @Override
    public Object grab(Map args) {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[97].callGetProperty(args);
            Object object2 = DefaultTypeTransformation.booleanUnbox(object) ? object : callSiteArray[98].call((Object)DEFAULT_DEPTH, 1);
            ScriptBytecodeAdapter.setProperty(object2, null, args, "calleeDepth");
        } else {
            Object object = callSiteArray[99].callGetProperty(args);
            Object object3 = DefaultTypeTransformation.booleanUnbox(object) ? object : Integer.valueOf(DEFAULT_DEPTH + 1);
            ScriptBytecodeAdapter.setProperty(object3, null, args, "calleeDepth");
        }
        return callSiteArray[100].callCurrent(this, args, args);
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Object grab(Map args, Map ... dependencies) {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        ClassLoader loader = null;
        callSiteArray[101].call(this.grabRecordsForCurrDependencies);
        try {
            Object[] objectArray = new Object[6];
            objectArray[0] = "classLoader";
            objectArray[1] = callSiteArray[103].call((Object)args, "classLoader");
            objectArray[2] = "refObject";
            objectArray[3] = callSiteArray[104].call((Object)args, "refObject");
            objectArray[4] = "calleeDepth";
            Object object = callSiteArray[105].callGetProperty(args);
            objectArray[5] = DefaultTypeTransformation.booleanUnbox(object) ? object : Integer.valueOf(DEFAULT_DEPTH);
            Object object2 = callSiteArray[102].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(objectArray));
            loader = (ClassLoader)ScriptBytecodeAdapter.castToType(object2, ClassLoader.class);
            if (!DefaultTypeTransformation.booleanUnbox(loader)) {
                return null;
            }
            boolean bl = false;
            if (bl) {
                return null;
            }
        }
        catch (Exception e) {
            Set grabRecordsForCurrLoader = (Set)ScriptBytecodeAdapter.castToType(callSiteArray[114].callCurrent((GroovyObject)this, loader), Set.class);
            callSiteArray[115].call((Object)grabRecordsForCurrLoader, this.grabRecordsForCurrDependencies);
            callSiteArray[116].call(this.grabRecordsForCurrDependencies);
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[117].callGetProperty(args))) throw (Throwable)e;
            Exception exception = e;
            return exception;
        }
        {
            Object uris = callSiteArray[106].callCurrent(this, loader, args, dependencies);
            URI uri = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[107].call(uris), Iterator.class);
            while (iterator.hasNext()) {
                uri = (URI)ScriptBytecodeAdapter.castToType(iterator.next(), URI.class);
                callSiteArray[108].call((Object)loader, callSiteArray[109].call(uri));
            }
            URI uri2 = null;
            Iterator iterator2 = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[110].call(uris), Iterator.class);
            while (iterator2.hasNext()) {
                uri2 = (URI)ScriptBytecodeAdapter.castToType(iterator2.next(), URI.class);
                File file = (File)ScriptBytecodeAdapter.castToType(callSiteArray[111].callConstructor(File.class, uri2), File.class);
                callSiteArray[112].callCurrent(this, loader, file);
                callSiteArray[113].callCurrent(this, loader, file);
            }
            return null;
        }
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Object processCategoryMethods(ClassLoader loader, File file) {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[118].call(callSiteArray[119].call(callSiteArray[120].callGetProperty(file)), ".jar"))) {
            return null;
        }
        Object mcRegistry = callSiteArray[121].callGetProperty(GroovySystem.class);
        if (!(mcRegistry instanceof MetaClassRegistryImpl)) {
            return null;
        }
        try {
            JarFile jar = (JarFile)ScriptBytecodeAdapter.castToType(callSiteArray[122].callConstructor(JarFile.class, file), JarFile.class);
            Object entry = callSiteArray[123].call((Object)jar, callSiteArray[124].callGetProperty(MetaClassRegistryImpl.class));
            if (!DefaultTypeTransformation.booleanUnbox(entry)) return null;
            Properties props = (Properties)ScriptBytecodeAdapter.castToType(callSiteArray[125].callConstructor(Properties.class), Properties.class);
            callSiteArray[126].call((Object)props, callSiteArray[127].call((Object)jar, entry));
            Map metaMethods = (Map)ScriptBytecodeAdapter.castToType(callSiteArray[128].callConstructor(HashMap.class), Map.class);
            callSiteArray[129].call(mcRegistry, props, loader, metaMethods);
            public class _processCategoryMethods_closure2
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _processCategoryMethods_closure2(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _processCategoryMethods_closure2.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(CachedClass c, List<MetaMethod> methods) {
                    Reference<CachedClass> c2 = new Reference<CachedClass>(c);
                    CallSite[] callSiteArray = _processCategoryMethods_closure2.$getCallSiteArray();
                    Reference<Set> classesToBeUpdated = new Reference<Set>((Set)ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.createList(new Object[]{c2.get()}), Set.class));
                    public class _closure18
                    extends Closure
                    implements GeneratedClosure {
                        private /* synthetic */ Reference c;
                        private /* synthetic */ Reference classesToBeUpdated;
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure18(Object _outerInstance, Object _thisObject, Reference c, Reference classesToBeUpdated) {
                            Reference reference;
                            Reference reference2;
                            CallSite[] callSiteArray = _closure18.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                            this.c = reference2 = c;
                            this.classesToBeUpdated = reference = classesToBeUpdated;
                        }

                        public Object doCall(ClassInfo info) {
                            CallSite[] callSiteArray = _closure18.$getCallSiteArray();
                            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(callSiteArray[1].callGetProperty(this.c.get()), callSiteArray[2].callGetProperty(callSiteArray[3].callGetProperty(info))))) {
                                return callSiteArray[4].call(this.classesToBeUpdated.get(), callSiteArray[5].callGetProperty(info));
                            }
                            return null;
                        }

                        public Object call(ClassInfo info) {
                            CallSite[] callSiteArray = _closure18.$getCallSiteArray();
                            return callSiteArray[6].callCurrent((GroovyObject)this, info);
                        }

                        public CachedClass getC() {
                            CallSite[] callSiteArray = _closure18.$getCallSiteArray();
                            return (CachedClass)ScriptBytecodeAdapter.castToType(this.c.get(), CachedClass.class);
                        }

                        public Set getClassesToBeUpdated() {
                            CallSite[] callSiteArray = _closure18.$getCallSiteArray();
                            return (Set)ScriptBytecodeAdapter.castToType(this.classesToBeUpdated.get(), Set.class);
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _closure18.class) {
                                return ScriptBytecodeAdapter.initMetaClass(this);
                            }
                            ClassInfo classInfo = $staticClassInfo;
                            if (classInfo == null) {
                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                            }
                            return classInfo.getMetaClass();
                        }

                        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                            stringArray[0] = "isAssignableFrom";
                            stringArray[1] = "theClass";
                            stringArray[2] = "theClass";
                            stringArray[3] = "cachedClass";
                            stringArray[4] = "leftShift";
                            stringArray[5] = "cachedClass";
                            stringArray[6] = "doCall";
                        }

                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                            String[] stringArray = new String[7];
                            _closure18.$createCallSiteArray_1(stringArray);
                            return new CallSiteArray(_closure18.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _closure18.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    callSiteArray[0].call(ClassInfo.class, new _closure18(this, this.getThisObject(), c2, classesToBeUpdated));
                    return ScriptBytecodeAdapter.invokeMethodNSpreadSafe(_processCategoryMethods_closure2.class, classesToBeUpdated.get(), "addNewMopMethods", new Object[]{methods});
                }

                public Object call(CachedClass c, List<MetaMethod> methods) {
                    Reference<CachedClass> c2 = new Reference<CachedClass>(c);
                    CallSite[] callSiteArray = _processCategoryMethods_closure2.$getCallSiteArray();
                    return callSiteArray[1].callCurrent(this, c2.get(), methods);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _processCategoryMethods_closure2.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "onAllClassInfo";
                    stringArray[1] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[2];
                    _processCategoryMethods_closure2.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_processCategoryMethods_closure2.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _processCategoryMethods_closure2.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            return callSiteArray[130].call((Object)metaMethods, new _processCategoryMethods_closure2(this, this));
        }
        catch (ZipException zipException) {
            throw (Throwable)callSiteArray[131].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{file}, new String[]{"Grape could not load jar '", "'"}), zipException);
        }
        catch (Throwable throwable) {
            throw throwable;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void processOtherServices(ClassLoader loader, File f) {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            try {
                ZipFile zf = (ZipFile)ScriptBytecodeAdapter.castToType(callSiteArray[132].callConstructor(ZipFile.class, f), ZipFile.class);
                ZipEntry serializedCategoryMethods = (ZipEntry)ScriptBytecodeAdapter.castToType(callSiteArray[133].call((Object)zf, "META-INF/services/org.codehaus.groovy.runtime.SerializedCategoryMethods"), ZipEntry.class);
                if (ScriptBytecodeAdapter.compareNotEqual(serializedCategoryMethods, null)) {
                    callSiteArray[134].callCurrent((GroovyObject)this, callSiteArray[135].call((Object)zf, serializedCategoryMethods));
                }
                ZipEntry pluginRunners = (ZipEntry)ScriptBytecodeAdapter.castToType(callSiteArray[136].call((Object)zf, "META-INF/services/org.codehaus.groovy.plugins.Runners"), ZipEntry.class);
                if (!ScriptBytecodeAdapter.compareNotEqual(pluginRunners, null)) return;
                callSiteArray[137].callCurrent(this, callSiteArray[138].call((Object)zf, pluginRunners), callSiteArray[139].call(f), loader);
                return;
            }
            catch (ZipException ignore) {
            }
            return;
        }
        try {
            ZipFile zf = (ZipFile)ScriptBytecodeAdapter.castToType(callSiteArray[140].callConstructor(ZipFile.class, f), ZipFile.class);
            ZipEntry serializedCategoryMethods = (ZipEntry)ScriptBytecodeAdapter.castToType(callSiteArray[141].call((Object)zf, "META-INF/services/org.codehaus.groovy.runtime.SerializedCategoryMethods"), ZipEntry.class);
            if (ScriptBytecodeAdapter.compareNotEqual(serializedCategoryMethods, null)) {
                callSiteArray[142].callCurrent((GroovyObject)this, callSiteArray[143].call((Object)zf, serializedCategoryMethods));
            }
            ZipEntry pluginRunners = (ZipEntry)ScriptBytecodeAdapter.castToType(callSiteArray[144].call((Object)zf, "META-INF/services/org.codehaus.groovy.plugins.Runners"), ZipEntry.class);
            if (!ScriptBytecodeAdapter.compareNotEqual(pluginRunners, null)) return;
            callSiteArray[145].callCurrent(this, callSiteArray[146].call((Object)zf, pluginRunners), callSiteArray[147].call(f), loader);
            return;
        }
        catch (ZipException ignore) {
        }
        return;
    }

    public void processSerializedCategoryMethods(InputStream is) {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        public class _processSerializedCategoryMethods_closure3
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _processSerializedCategoryMethods_closure3(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _processSerializedCategoryMethods_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _processSerializedCategoryMethods_closure3.$getCallSiteArray();
                return callSiteArray[0].callCurrent((GroovyObject)this, callSiteArray[1].call(it));
            }

            public Object doCall() {
                CallSite[] callSiteArray = _processSerializedCategoryMethods_closure3.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _processSerializedCategoryMethods_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "println";
                stringArray[1] = "trim";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _processSerializedCategoryMethods_closure3.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_processSerializedCategoryMethods_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _processSerializedCategoryMethods_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[148].call(callSiteArray[149].call(callSiteArray[150].callGetProperty(is)), new _processSerializedCategoryMethods_closure3(this, this));
    }

    /*
     * WARNING - void declaration
     */
    public void processRunners(InputStream is, String name, ClassLoader loader) {
        void var3_3;
        Reference<String> name2 = new Reference<String>(name);
        Reference<void> loader2 = new Reference<void>(var3_3);
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        public class _processRunners_closure4
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _processRunners_closure4(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _processRunners_closure4.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _processRunners_closure4.$getCallSiteArray();
                if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return !DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(it)) && ScriptBytecodeAdapter.compareNotEqual(callSiteArray[1].call(it, 0), "#");
                }
                return !DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(it)) && ScriptBytecodeAdapter.compareNotEqual(callSiteArray[3].call(it, 0), "#");
            }

            public Object doCall() {
                CallSite[] callSiteArray = _processRunners_closure4.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _processRunners_closure4.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "isEmpty";
                stringArray[1] = "getAt";
                stringArray[2] = "isEmpty";
                stringArray[3] = "getAt";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
                _processRunners_closure4.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_processRunners_closure4.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _processRunners_closure4.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        public class _processRunners_closure5
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference name;
            private /* synthetic */ Reference loader;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _processRunners_closure5(Object _outerInstance, Object _thisObject, Reference name, Reference loader) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _processRunners_closure5.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.name = reference2 = name;
                this.loader = reference = loader;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _processRunners_closure5.$getCallSiteArray();
                Object object = callSiteArray[0].call(callSiteArray[1].call(this.loader.get(), it));
                callSiteArray[2].call(callSiteArray[3].callGetProperty(GroovySystem.class), this.name.get(), object);
                Object object2 = object;
                try {
                    return object2;
                }
                catch (Exception ex) {
                    throw (Throwable)callSiteArray[4].callConstructor(IllegalStateException.class, callSiteArray[5].call(callSiteArray[6].call((Object)"Error registering runner class '", it), "'"), ex);
                }
            }

            public String getName() {
                CallSite[] callSiteArray = _processRunners_closure5.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.name.get());
            }

            public ClassLoader getLoader() {
                CallSite[] callSiteArray = _processRunners_closure5.$getCallSiteArray();
                return (ClassLoader)ScriptBytecodeAdapter.castToType(this.loader.get(), ClassLoader.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _processRunners_closure5.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _processRunners_closure5.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "newInstance";
                stringArray[1] = "loadClass";
                stringArray[2] = "putAt";
                stringArray[3] = "RUNNER_REGISTRY";
                stringArray[4] = "<$constructor$>";
                stringArray[5] = "plus";
                stringArray[6] = "plus";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[7];
                _processRunners_closure5.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_processRunners_closure5.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _processRunners_closure5.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[151].call(callSiteArray[152].call(ScriptBytecodeAdapter.invokeMethod0SpreadSafe(GrapeIvy.class, callSiteArray[153].call(callSiteArray[154].callGetProperty(is)), "trim"), new _processRunners_closure4(this, this)), new _processRunners_closure5(this, this, name2, loader2));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public ResolveReport getDependencies(Map args, IvyGrabRecord ... grabRecords) {
        Object object;
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        ResolutionCacheManager cacheManager = (ResolutionCacheManager)ScriptBytecodeAdapter.castToType(callSiteArray[155].callGetProperty(this.ivyInstance), ResolutionCacheManager.class);
        Object millis = callSiteArray[156].call(System.class);
        Object md = callSiteArray[157].callConstructor(DefaultModuleDescriptor.class, callSiteArray[158].call(ModuleRevisionId.class, "caller", "all-caller", callSiteArray[159].call((Object)"working", callSiteArray[160].call(callSiteArray[161].call(millis), ScriptBytecodeAdapter.createRange(-2, -1, true)))), "integration", null, true);
        callSiteArray[162].call(md, callSiteArray[163].callConstructor(Configuration.class, "default"));
        callSiteArray[164].call(md, millis);
        callSiteArray[165].callCurrent(this, args, md);
        Reference<Object> grabRecord = new Reference<Object>(null);
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[166].call(grabRecords), Iterator.class);
        while (iterator.hasNext()) {
            grabRecord.set(((IvyGrabRecord)ScriptBytecodeAdapter.castToType(iterator.next(), IvyGrabRecord.class)));
            Object object2 = callSiteArray[167].callGroovyObjectGetProperty(grabRecord.get());
            Object conf = DefaultTypeTransformation.booleanUnbox(object2) ? object2 : ScriptBytecodeAdapter.createList(new Object[]{"*"});
            public class _getDependencies_closure6
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference grabRecord;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDependencies_closure6(Object _outerInstance, Object _thisObject, Reference grabRecord) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDependencies_closure6.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.grabRecord = reference = grabRecord;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDependencies_closure6.$getCallSiteArray();
                    return callSiteArray[0].call(callSiteArray[1].callGetProperty(it), callSiteArray[2].callGroovyObjectGetProperty(this.grabRecord.get()));
                }

                public IvyGrabRecord getGrabRecord() {
                    CallSite[] callSiteArray = _getDependencies_closure6.$getCallSiteArray();
                    return (IvyGrabRecord)ScriptBytecodeAdapter.castToType(this.grabRecord.get(), IvyGrabRecord.class);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDependencies_closure6.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDependencies_closure6.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "equals";
                    stringArray[1] = "dependencyRevisionId";
                    stringArray[2] = "mrid";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[3];
                    _getDependencies_closure6.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_getDependencies_closure6.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDependencies_closure6.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            Reference<DefaultDependencyDescriptor> dd = new Reference<DefaultDependencyDescriptor>((DefaultDependencyDescriptor)ScriptBytecodeAdapter.castToType(callSiteArray[168].call(callSiteArray[169].callGetProperty(md), new _getDependencies_closure6(this, this, grabRecord)), DefaultDependencyDescriptor.class));
            if (DefaultTypeTransformation.booleanUnbox(dd.get())) {
                callSiteArray[170].callCurrent(this, dd.get(), grabRecord.get(), conf);
                continue;
            }
            Object object3 = callSiteArray[171].callConstructor((Object)DefaultDependencyDescriptor.class, ArrayUtil.createArray(md, callSiteArray[172].callGroovyObjectGetProperty(grabRecord.get()), callSiteArray[173].callGroovyObjectGetProperty(grabRecord.get()), callSiteArray[174].callGroovyObjectGetProperty(grabRecord.get()), callSiteArray[175].callGroovyObjectGetProperty(grabRecord.get())));
            dd.set((DefaultDependencyDescriptor)ScriptBytecodeAdapter.castToType(object3, DefaultDependencyDescriptor.class));
            public class _getDependencies_closure7
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference dd;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDependencies_closure7(Object _outerInstance, Object _thisObject, Reference dd) {
                    Reference reference;
                    CallSite[] callSiteArray = _getDependencies_closure7.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.dd = reference = dd;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getDependencies_closure7.$getCallSiteArray();
                    return callSiteArray[0].call(this.dd.get(), "default", it);
                }

                public DefaultDependencyDescriptor getDd() {
                    CallSite[] callSiteArray = _getDependencies_closure7.$getCallSiteArray();
                    return (DefaultDependencyDescriptor)ScriptBytecodeAdapter.castToType(this.dd.get(), DefaultDependencyDescriptor.class);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getDependencies_closure7.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDependencies_closure7.class) {
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
                    stringArray[0] = "addDependencyConfiguration";
                    return new CallSiteArray(_getDependencies_closure7.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDependencies_closure7.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[176].call(conf, new _getDependencies_closure7(this, this, dd));
            callSiteArray[177].callCurrent(this, dd.get(), grabRecord.get(), conf);
            callSiteArray[178].call(md, dd.get());
        }
        ResolveOptions resolveOptions = (ResolveOptions)ScriptBytecodeAdapter.castToType(callSiteArray[179].call(callSiteArray[180].call(callSiteArray[181].call(callSiteArray[182].callConstructor(ResolveOptions.class), ScriptBytecodeAdapter.createPojoWrapper((String[])ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[]{"default"}), String[].class), String[].class)), false), DefaultTypeTransformation.booleanUnbox(callSiteArray[183].call((Object)args, "validate")) ? callSiteArray[184].callGetProperty(args) : Boolean.valueOf(false)), ResolveOptions.class);
        String string = DefaultTypeTransformation.booleanUnbox(callSiteArray[185].callGetProperty(args)) ? "downloadGrapes" : "cachedGrapes";
        ScriptBytecodeAdapter.setProperty(string, null, callSiteArray[186].callGetProperty(this.ivyInstance), "defaultResolver");
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[187].callGetProperty(args))) {
            callSiteArray[188].call(callSiteArray[189].callGetProperty(this.ivyInstance), "ivy.checksums", "");
        }
        int reportDownloads = 0;
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            boolean bl = ScriptBytecodeAdapter.compareEqual(callSiteArray[190].call(System.class, "groovy.grape.report.downloads", "false"), "true");
            reportDownloads = bl ? 1 : 0;
        } else {
            boolean bl = ScriptBytecodeAdapter.compareEqual(callSiteArray[191].call(System.class, "groovy.grape.report.downloads", "false"), "true");
            reportDownloads = bl ? 1 : 0;
        }
        if (reportDownloads != 0) {
            public class _getDependencies_closure8
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getDependencies_closure8(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _getDependencies_closure8.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object ivyEvent) {
                    CallSite[] callSiteArray = _getDependencies_closure8.$getCallSiteArray();
                    Object object = ivyEvent;
                    if (ScriptBytecodeAdapter.isCase(object, StartResolveEvent.class)) {
                        public class _closure19
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure19(Object _outerInstance, Object _thisObject) {
                                CallSite[] callSiteArray = _closure19.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure19.$getCallSiteArray();
                                Object name = callSiteArray[0].call(it);
                                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call(callSiteArray[2].callGroovyObjectGetProperty(this), name))) {
                                    callSiteArray[3].call(callSiteArray[4].callGroovyObjectGetProperty(this), name);
                                    return callSiteArray[5].call(callSiteArray[6].callGetProperty(System.class), callSiteArray[7].call((Object)"Resolving ", name));
                                }
                                return null;
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure19.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }

                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                stringArray[0] = "toString";
                                stringArray[1] = "contains";
                                stringArray[2] = "resolvedDependencies";
                                stringArray[3] = "leftShift";
                                stringArray[4] = "resolvedDependencies";
                                stringArray[5] = "println";
                                stringArray[6] = "err";
                                stringArray[7] = "plus";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[8];
                                _closure19.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure19.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure19.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        return callSiteArray[0].call(callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(ivyEvent)), new _closure19(this, this.getThisObject()));
                    }
                    if (ScriptBytecodeAdapter.isCase(object, PrepareDownloadEvent.class)) {
                        public class _closure20
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure20(Object _outerInstance, Object _thisObject) {
                                CallSite[] callSiteArray = _closure20.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure20.$getCallSiteArray();
                                Object name = callSiteArray[0].call(it);
                                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call(callSiteArray[2].callGroovyObjectGetProperty(this), name))) {
                                    callSiteArray[3].call(callSiteArray[4].callGroovyObjectGetProperty(this), name);
                                    return callSiteArray[5].call(callSiteArray[6].callGetProperty(System.class), callSiteArray[7].call((Object)"Preparing to download artifact ", name));
                                }
                                return null;
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure20.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }

                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                stringArray[0] = "toString";
                                stringArray[1] = "contains";
                                stringArray[2] = "downloadedArtifacts";
                                stringArray[3] = "leftShift";
                                stringArray[4] = "downloadedArtifacts";
                                stringArray[5] = "println";
                                stringArray[6] = "err";
                                stringArray[7] = "plus";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[8];
                                _closure20.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure20.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure20.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        return callSiteArray[3].call(callSiteArray[4].callGetProperty(ivyEvent), new _closure20(this, this.getThisObject()));
                    }
                    return null;
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getDependencies_closure8.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "each";
                    stringArray[1] = "dependencies";
                    stringArray[2] = "moduleDescriptor";
                    stringArray[3] = "each";
                    stringArray[4] = "artifacts";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[5];
                    _getDependencies_closure8.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_getDependencies_closure8.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getDependencies_closure8.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[192].call(callSiteArray[193].callGetProperty(this.ivyInstance), ScriptBytecodeAdapter.createPojoWrapper((IvyListener)ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createMap(new Object[]{"progress", new _getDependencies_closure8(this, this)}), IvyListener.class), IvyListener.class));
        }
        ResolveReport report = null;
        int attempt = 8;
        while (true) {
            Object object4 = callSiteArray[194].call(this.ivyInstance, md, resolveOptions);
            report = (ResolveReport)ScriptBytecodeAdapter.castToType(object4, ResolveReport.class);
            try {
            }
            catch (IOException ioe) {
                int n = attempt;
                attempt = DefaultTypeTransformation.intUnbox(callSiteArray[195].call(n));
                if (!(n != 0)) throw (Throwable)callSiteArray[199].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{callSiteArray[200].callGetProperty(ioe)}, new String[]{"Error grabbing grapes -- ", ""}));
                if (reportDownloads != 0) {
                    callSiteArray[196].call(callSiteArray[197].callGetProperty(System.class), "Grab Error: retrying...");
                }
                callSiteArray[198].callCurrent((GroovyObject)this, attempt > 4 ? 350 : 1000);
                continue;
            }
            break;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[201].call(report))) {
            throw (Throwable)callSiteArray[202].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{callSiteArray[203].callGetProperty(report)}, new String[]{"Error grabbing Grapes -- ", ""}));
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[204].callGetProperty(report)) && reportDownloads != 0) {
            callSiteArray[205].call(callSiteArray[206].callGetProperty(System.class), new GStringImpl(new Object[]{callSiteArray[207].call(callSiteArray[208].callGetProperty(report), 10), callSiteArray[209].callGetProperty(report), callSiteArray[210].call(ScriptBytecodeAdapter.invokeMethod0SpreadSafe(GrapeIvy.class, callSiteArray[211].callGetProperty(report), "toString"), "\n  ")}, new String[]{"Downloaded ", " Kbytes in ", "ms:\n  ", ""}));
        }
        md = object = callSiteArray[212].callGetProperty(report);
        if (!(!DefaultTypeTransformation.booleanUnbox(callSiteArray[213].callGetProperty(args)))) return report;
        callSiteArray[214].call(callSiteArray[215].call((Object)cacheManager, callSiteArray[216].callGetProperty(md)));
        callSiteArray[217].call(callSiteArray[218].call((Object)cacheManager, callSiteArray[219].callGetProperty(md)));
        return report;
    }

    private void createAndAddDependencyArtifactDescriptor(DefaultDependencyDescriptor dd, IvyGrabRecord grabRecord, List<String> conf) {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[220].call(conf, 0), "optional") || DefaultTypeTransformation.booleanUnbox(callSiteArray[221].callGroovyObjectGetProperty(grabRecord))) {
            Object object = callSiteArray[225].callGroovyObjectGetProperty(grabRecord);
            Object object2 = DefaultTypeTransformation.booleanUnbox(object) ? object : "jar";
            Object object3 = callSiteArray[226].callGroovyObjectGetProperty(grabRecord);
            Reference<Object> dad = new Reference<Object>(callSiteArray[222].callConstructor((Object)DefaultDependencyArtifactDescriptor.class, ArrayUtil.createArray(dd, callSiteArray[223].callGetProperty(callSiteArray[224].callGroovyObjectGetProperty(grabRecord)), object2, DefaultTypeTransformation.booleanUnbox(object3) ? object3 : "jar", null, DefaultTypeTransformation.booleanUnbox(callSiteArray[227].callGroovyObjectGetProperty(grabRecord)) ? ScriptBytecodeAdapter.createMap(new Object[]{"classifier", callSiteArray[228].callGroovyObjectGetProperty(grabRecord)}) : null)));
            public class _createAndAddDependencyArtifactDescriptor_closure9
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference dad;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _createAndAddDependencyArtifactDescriptor_closure9(Object _outerInstance, Object _thisObject, Reference dad) {
                    Reference reference;
                    CallSite[] callSiteArray = _createAndAddDependencyArtifactDescriptor_closure9.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.dad = reference = dad;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _createAndAddDependencyArtifactDescriptor_closure9.$getCallSiteArray();
                    return callSiteArray[0].call(this.dad.get(), it);
                }

                public Object getDad() {
                    CallSite[] callSiteArray = _createAndAddDependencyArtifactDescriptor_closure9.$getCallSiteArray();
                    return this.dad.get();
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _createAndAddDependencyArtifactDescriptor_closure9.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _createAndAddDependencyArtifactDescriptor_closure9.class) {
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
                    stringArray[0] = "addConfiguration";
                    return new CallSiteArray(_createAndAddDependencyArtifactDescriptor_closure9.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _createAndAddDependencyArtifactDescriptor_closure9.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[229].call(conf, new _createAndAddDependencyArtifactDescriptor_closure9(this, this, dad));
            callSiteArray[230].call(dd, "default", dad.get());
        }
    }

    /*
     * WARNING - void declaration
     */
    public void uninstallArtifact(String group, String module, String rev) {
        void var3_3;
        void var2_2;
        Reference<String> group2 = new Reference<String>(group);
        Reference<void> module2 = new Reference<void>(var2_2);
        Reference<void> rev2 = new Reference<void>(var3_3);
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        Reference<Pattern> ivyFilePattern = new Reference<Pattern>((Pattern)ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.bitwiseNegate("ivy-(.*)\\.xml"), Pattern.class));
        public class _uninstallArtifact_closure10
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference group;
            private /* synthetic */ Reference module;
            private /* synthetic */ Reference ivyFilePattern;
            private /* synthetic */ Reference rev;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _uninstallArtifact_closure10(Object _outerInstance, Object _thisObject, Reference group, Reference module, Reference ivyFilePattern, Reference rev) {
                Reference reference;
                Reference reference2;
                Reference reference3;
                Reference reference4;
                CallSite[] callSiteArray = _uninstallArtifact_closure10.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.group = reference4 = group;
                this.module = reference3 = module;
                this.ivyFilePattern = reference2 = ivyFilePattern;
                this.rev = reference = rev;
            }

            public Object doCall(File groupDir) {
                CallSite[] callSiteArray = _uninstallArtifact_closure10.$getCallSiteArray();
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGetProperty(groupDir), this.group.get())) {
                    public class _closure21
                    extends Closure
                    implements GeneratedClosure {
                        private /* synthetic */ Reference module;
                        private /* synthetic */ Reference ivyFilePattern;
                        private /* synthetic */ Reference rev;
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure21(Object _outerInstance, Object _thisObject, Reference module, Reference ivyFilePattern, Reference rev) {
                            Reference reference;
                            Reference reference2;
                            Reference reference3;
                            CallSite[] callSiteArray = _closure21.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                            this.module = reference3 = module;
                            this.ivyFilePattern = reference2 = ivyFilePattern;
                            this.rev = reference = rev;
                        }

                        public Object doCall(File moduleDir) {
                            Reference<File> moduleDir2 = new Reference<File>(moduleDir);
                            CallSite[] callSiteArray = _closure21.$getCallSiteArray();
                            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGetProperty(moduleDir2.get()), this.module.get())) {
                                public class _closure22
                                extends Closure
                                implements GeneratedClosure {
                                    private /* synthetic */ Reference ivyFilePattern;
                                    private /* synthetic */ Reference rev;
                                    private /* synthetic */ Reference moduleDir;
                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                    public static transient /* synthetic */ boolean __$stMC;
                                    private static /* synthetic */ SoftReference $callSiteArray;

                                    public _closure22(Object _outerInstance, Object _thisObject, Reference ivyFilePattern, Reference rev, Reference moduleDir) {
                                        Reference reference;
                                        Reference reference2;
                                        Reference reference3;
                                        CallSite[] callSiteArray = _closure22.$getCallSiteArray();
                                        super(_outerInstance, _thisObject);
                                        this.ivyFilePattern = reference3 = ivyFilePattern;
                                        this.rev = reference2 = rev;
                                        this.moduleDir = reference = moduleDir;
                                    }

                                    public Object doCall(File ivyFile) {
                                        CallSite[] callSiteArray = _closure22.$getCallSiteArray();
                                        Object m = callSiteArray[0].call(this.ivyFilePattern.get(), callSiteArray[1].callGetProperty(ivyFile));
                                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(m)) && ScriptBytecodeAdapter.compareEqual(callSiteArray[3].call(m, 1), this.rev.get())) {
                                            Object jardir = callSiteArray[4].callConstructor(File.class, this.moduleDir.get(), "jars");
                                            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call(jardir))) {
                                                return null;
                                            }
                                            Object dbf = callSiteArray[6].call(DocumentBuilderFactory.class);
                                            Object db = callSiteArray[7].call(dbf);
                                            Object root = callSiteArray[8].callGetProperty(callSiteArray[9].call(db, ivyFile));
                                            Object publis = callSiteArray[10].call(root, "publications");
                                            if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                                                int i = 0;
                                                while (ScriptBytecodeAdapter.compareLessThan(i, callSiteArray[11].callGetProperty(publis))) {
                                                    Object artifacts = callSiteArray[12].call(callSiteArray[13].call(publis, i), "artifact");
                                                    int j = 0;
                                                    while (ScriptBytecodeAdapter.compareLessThan(j, callSiteArray[14].callGetProperty(artifacts))) {
                                                        Object artifact = callSiteArray[15].call(artifacts, j);
                                                        Object attrs = callSiteArray[16].callGetProperty(artifact);
                                                        Object name = callSiteArray[17].call(callSiteArray[18].call(callSiteArray[19].call(attrs, "name")), new GStringImpl(new Object[]{this.rev.get()}, new String[]{"-", ""}));
                                                        Object classifier = callSiteArray[20].callSafe(callSiteArray[21].call(attrs, "m", "classifier"));
                                                        if (DefaultTypeTransformation.booleanUnbox(classifier)) {
                                                            name = callSiteArray[22].call(name, new GStringImpl(new Object[]{classifier}, new String[]{"-", ""}));
                                                        }
                                                        name = callSiteArray[23].call(name, new GStringImpl(new Object[]{callSiteArray[24].call(callSiteArray[25].call(attrs, "ext"))}, new String[]{".", ""}));
                                                        Object jarfile = callSiteArray[26].callConstructor(File.class, jardir, name);
                                                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[27].call(jarfile))) {
                                                            callSiteArray[28].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[29].callGetProperty(jarfile)}, new String[]{"Deleting ", ""}));
                                                            callSiteArray[30].call(jarfile);
                                                        }
                                                        int n = j;
                                                        j = DefaultTypeTransformation.intUnbox(callSiteArray[31].call(n));
                                                    }
                                                    int n = i;
                                                    i = DefaultTypeTransformation.intUnbox(callSiteArray[32].call(n));
                                                }
                                            } else {
                                                int i = 0;
                                                while (ScriptBytecodeAdapter.compareLessThan(i, callSiteArray[33].callGetProperty(publis))) {
                                                    Object artifacts = callSiteArray[34].call(callSiteArray[35].call(publis, i), "artifact");
                                                    int j = 0;
                                                    while (ScriptBytecodeAdapter.compareLessThan(j, callSiteArray[36].callGetProperty(artifacts))) {
                                                        Object artifact = callSiteArray[37].call(artifacts, j);
                                                        Object attrs = callSiteArray[38].callGetProperty(artifact);
                                                        Object name = callSiteArray[39].call(callSiteArray[40].call(callSiteArray[41].call(attrs, "name")), new GStringImpl(new Object[]{this.rev.get()}, new String[]{"-", ""}));
                                                        Object classifier = callSiteArray[42].callSafe(callSiteArray[43].call(attrs, "m", "classifier"));
                                                        if (DefaultTypeTransformation.booleanUnbox(classifier)) {
                                                            name = callSiteArray[44].call(name, new GStringImpl(new Object[]{classifier}, new String[]{"-", ""}));
                                                        }
                                                        name = callSiteArray[45].call(name, new GStringImpl(new Object[]{callSiteArray[46].call(callSiteArray[47].call(attrs, "ext"))}, new String[]{".", ""}));
                                                        Object jarfile = callSiteArray[48].callConstructor(File.class, jardir, name);
                                                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[49].call(jarfile))) {
                                                            callSiteArray[50].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[51].callGetProperty(jarfile)}, new String[]{"Deleting ", ""}));
                                                            callSiteArray[52].call(jarfile);
                                                        }
                                                        int n = j;
                                                        int cfr_ignored_0 = n + 1;
                                                    }
                                                    int n = i;
                                                    int cfr_ignored_1 = n + 1;
                                                }
                                            }
                                            return callSiteArray[53].call(ivyFile);
                                        }
                                        return null;
                                    }

                                    public Object call(File ivyFile) {
                                        CallSite[] callSiteArray = _closure22.$getCallSiteArray();
                                        return callSiteArray[54].callCurrent((GroovyObject)this, ivyFile);
                                    }

                                    public Pattern getIvyFilePattern() {
                                        CallSite[] callSiteArray = _closure22.$getCallSiteArray();
                                        return (Pattern)ScriptBytecodeAdapter.castToType(this.ivyFilePattern.get(), Pattern.class);
                                    }

                                    public String getRev() {
                                        CallSite[] callSiteArray = _closure22.$getCallSiteArray();
                                        return ShortTypeHandling.castToString(this.rev.get());
                                    }

                                    public File getModuleDir() {
                                        CallSite[] callSiteArray = _closure22.$getCallSiteArray();
                                        return (File)ScriptBytecodeAdapter.castToType(this.moduleDir.get(), File.class);
                                    }

                                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                        if (this.getClass() != _closure22.class) {
                                            return ScriptBytecodeAdapter.initMetaClass(this);
                                        }
                                        ClassInfo classInfo = $staticClassInfo;
                                        if (classInfo == null) {
                                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                        }
                                        return classInfo.getMetaClass();
                                    }

                                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                        stringArray[0] = "matcher";
                                        stringArray[1] = "name";
                                        stringArray[2] = "matches";
                                        stringArray[3] = "group";
                                        stringArray[4] = "<$constructor$>";
                                        stringArray[5] = "exists";
                                        stringArray[6] = "newInstance";
                                        stringArray[7] = "newDocumentBuilder";
                                        stringArray[8] = "documentElement";
                                        stringArray[9] = "parse";
                                        stringArray[10] = "getElementsByTagName";
                                        stringArray[11] = "length";
                                        stringArray[12] = "getElementsByTagName";
                                        stringArray[13] = "item";
                                        stringArray[14] = "length";
                                        stringArray[15] = "item";
                                        stringArray[16] = "attributes";
                                        stringArray[17] = "plus";
                                        stringArray[18] = "getTextContent";
                                        stringArray[19] = "getNamedItem";
                                        stringArray[20] = "getTextContent";
                                        stringArray[21] = "getNamedItemNS";
                                        stringArray[22] = "plus";
                                        stringArray[23] = "plus";
                                        stringArray[24] = "getTextContent";
                                        stringArray[25] = "getNamedItem";
                                        stringArray[26] = "<$constructor$>";
                                        stringArray[27] = "exists";
                                        stringArray[28] = "println";
                                        stringArray[29] = "name";
                                        stringArray[30] = "delete";
                                        stringArray[31] = "next";
                                        stringArray[32] = "next";
                                        stringArray[33] = "length";
                                        stringArray[34] = "getElementsByTagName";
                                        stringArray[35] = "item";
                                        stringArray[36] = "length";
                                        stringArray[37] = "item";
                                        stringArray[38] = "attributes";
                                        stringArray[39] = "plus";
                                        stringArray[40] = "getTextContent";
                                        stringArray[41] = "getNamedItem";
                                        stringArray[42] = "getTextContent";
                                        stringArray[43] = "getNamedItemNS";
                                        stringArray[44] = "plus";
                                        stringArray[45] = "plus";
                                        stringArray[46] = "getTextContent";
                                        stringArray[47] = "getNamedItem";
                                        stringArray[48] = "<$constructor$>";
                                        stringArray[49] = "exists";
                                        stringArray[50] = "println";
                                        stringArray[51] = "name";
                                        stringArray[52] = "delete";
                                        stringArray[53] = "delete";
                                        stringArray[54] = "doCall";
                                    }

                                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                        String[] stringArray = new String[55];
                                        _closure22.$createCallSiteArray_1(stringArray);
                                        return new CallSiteArray(_closure22.class, stringArray);
                                    }

                                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                        CallSiteArray callSiteArray;
                                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                            callSiteArray = _closure22.$createCallSiteArray();
                                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                        }
                                        return callSiteArray.array;
                                    }
                                }
                                return callSiteArray[1].call(moduleDir2.get(), this.ivyFilePattern.get(), new _closure22(this, this.getThisObject(), this.ivyFilePattern, this.rev, moduleDir2));
                            }
                            return null;
                        }

                        public Object call(File moduleDir) {
                            Reference<File> moduleDir2 = new Reference<File>(moduleDir);
                            CallSite[] callSiteArray = _closure21.$getCallSiteArray();
                            return callSiteArray[2].callCurrent((GroovyObject)this, moduleDir2.get());
                        }

                        public String getModule() {
                            CallSite[] callSiteArray = _closure21.$getCallSiteArray();
                            return ShortTypeHandling.castToString(this.module.get());
                        }

                        public Pattern getIvyFilePattern() {
                            CallSite[] callSiteArray = _closure21.$getCallSiteArray();
                            return (Pattern)ScriptBytecodeAdapter.castToType(this.ivyFilePattern.get(), Pattern.class);
                        }

                        public String getRev() {
                            CallSite[] callSiteArray = _closure21.$getCallSiteArray();
                            return ShortTypeHandling.castToString(this.rev.get());
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _closure21.class) {
                                return ScriptBytecodeAdapter.initMetaClass(this);
                            }
                            ClassInfo classInfo = $staticClassInfo;
                            if (classInfo == null) {
                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                            }
                            return classInfo.getMetaClass();
                        }

                        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                            stringArray[0] = "name";
                            stringArray[1] = "eachFileMatch";
                            stringArray[2] = "doCall";
                        }

                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                            String[] stringArray = new String[3];
                            _closure21.$createCallSiteArray_1(stringArray);
                            return new CallSiteArray(_closure21.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _closure21.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    return callSiteArray[1].call((Object)groupDir, new _closure21(this, this.getThisObject(), this.module, this.ivyFilePattern, this.rev));
                }
                return null;
            }

            public Object call(File groupDir) {
                CallSite[] callSiteArray = _uninstallArtifact_closure10.$getCallSiteArray();
                return callSiteArray[2].callCurrent((GroovyObject)this, groupDir);
            }

            public String getGroup() {
                CallSite[] callSiteArray = _uninstallArtifact_closure10.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.group.get());
            }

            public String getModule() {
                CallSite[] callSiteArray = _uninstallArtifact_closure10.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.module.get());
            }

            public Pattern getIvyFilePattern() {
                CallSite[] callSiteArray = _uninstallArtifact_closure10.$getCallSiteArray();
                return (Pattern)ScriptBytecodeAdapter.castToType(this.ivyFilePattern.get(), Pattern.class);
            }

            public String getRev() {
                CallSite[] callSiteArray = _uninstallArtifact_closure10.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.rev.get());
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _uninstallArtifact_closure10.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "name";
                stringArray[1] = "eachDir";
                stringArray[2] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                _uninstallArtifact_closure10.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_uninstallArtifact_closure10.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _uninstallArtifact_closure10.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[231].call(callSiteArray[232].callGroovyObjectGetProperty(this), new _uninstallArtifact_closure10(this, this, group2, module2, ivyFilePattern, rev2));
    }

    private Object addExcludesIfNeeded(Map args, DefaultModuleDescriptor md) {
        Reference<DefaultModuleDescriptor> md2 = new Reference<DefaultModuleDescriptor>(md);
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[233].call((Object)args, "excludes"))) {
            return null;
        }
        public class _addExcludesIfNeeded_closure11
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference md;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _addExcludesIfNeeded_closure11(Object _outerInstance, Object _thisObject, Reference md) {
                Reference reference;
                CallSite[] callSiteArray = _addExcludesIfNeeded_closure11.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.md = reference = md;
            }

            public Object doCall(Object map) {
                CallSite[] callSiteArray = _addExcludesIfNeeded_closure11.$getCallSiteArray();
                Object excludeRule = callSiteArray[0].callConstructor(DefaultExcludeRule.class, callSiteArray[1].callConstructor(ArtifactId.class, callSiteArray[2].callConstructor(ModuleId.class, callSiteArray[3].callGetProperty(map), callSiteArray[4].callGetProperty(map)), callSiteArray[5].callGetProperty(PatternMatcher.class), callSiteArray[6].callGetProperty(PatternMatcher.class), callSiteArray[7].callGetProperty(PatternMatcher.class)), callSiteArray[8].callGetProperty(ExactPatternMatcher.class), null);
                callSiteArray[9].call(excludeRule, "default");
                return callSiteArray[10].call(this.md.get(), excludeRule);
            }

            public DefaultModuleDescriptor getMd() {
                CallSite[] callSiteArray = _addExcludesIfNeeded_closure11.$getCallSiteArray();
                return (DefaultModuleDescriptor)ScriptBytecodeAdapter.castToType(this.md.get(), DefaultModuleDescriptor.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _addExcludesIfNeeded_closure11.class) {
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
                stringArray[1] = "<$constructor$>";
                stringArray[2] = "<$constructor$>";
                stringArray[3] = "group";
                stringArray[4] = "module";
                stringArray[5] = "ANY_EXPRESSION";
                stringArray[6] = "ANY_EXPRESSION";
                stringArray[7] = "ANY_EXPRESSION";
                stringArray[8] = "INSTANCE";
                stringArray[9] = "addConfiguration";
                stringArray[10] = "addExcludeRule";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[11];
                _addExcludesIfNeeded_closure11.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_addExcludesIfNeeded_closure11.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _addExcludesIfNeeded_closure11.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return callSiteArray[234].call(callSiteArray[235].callGetProperty(args), new _addExcludesIfNeeded_closure11(this, this, md2));
    }

    @Override
    public Map<String, Map<String, List<String>>> enumerateGrapes() {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        Reference<Map> bunches = new Reference<Map>(ScriptBytecodeAdapter.createMap(new Object[0]));
        Reference<Pattern> ivyFilePattern = new Reference<Pattern>((Pattern)ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.bitwiseNegate("ivy-(.*)\\.xml"), Pattern.class));
        public class _enumerateGrapes_closure12
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference bunches;
            private /* synthetic */ Reference ivyFilePattern;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _enumerateGrapes_closure12(Object _outerInstance, Object _thisObject, Reference bunches, Reference ivyFilePattern) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _enumerateGrapes_closure12.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.bunches = reference2 = bunches;
                this.ivyFilePattern = reference = ivyFilePattern;
            }

            public Object doCall(File groupDir) {
                CallSite[] callSiteArray = _enumerateGrapes_closure12.$getCallSiteArray();
                Reference<Map> grapes = new Reference<Map>(ScriptBytecodeAdapter.createMap(new Object[0]));
                Map map = grapes.get();
                callSiteArray[0].call(this.bunches.get(), callSiteArray[1].callGetProperty(groupDir), map);
                public class _closure23
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference ivyFilePattern;
                    private /* synthetic */ Reference grapes;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure23(Object _outerInstance, Object _thisObject, Reference ivyFilePattern, Reference grapes) {
                        Reference reference;
                        Reference reference2;
                        CallSite[] callSiteArray = _closure23.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.ivyFilePattern = reference2 = ivyFilePattern;
                        this.grapes = reference = grapes;
                    }

                    public Object doCall(File moduleDir) {
                        CallSite[] callSiteArray = _closure23.$getCallSiteArray();
                        Reference<List> versions = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[0]));
                        public class _closure24
                        extends Closure
                        implements GeneratedClosure {
                            private /* synthetic */ Reference ivyFilePattern;
                            private /* synthetic */ Reference versions;
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure24(Object _outerInstance, Object _thisObject, Reference ivyFilePattern, Reference versions) {
                                Reference reference;
                                Reference reference2;
                                CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                                this.ivyFilePattern = reference2 = ivyFilePattern;
                                this.versions = reference = versions;
                            }

                            public Object doCall(File ivyFile) {
                                CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                                Object m = callSiteArray[0].call(this.ivyFilePattern.get(), callSiteArray[1].callGetProperty(ivyFile));
                                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(m))) {
                                    Object object = callSiteArray[3].call(this.versions.get(), callSiteArray[4].call(m, 1));
                                    this.versions.set(object);
                                    return object;
                                }
                                return null;
                            }

                            public Object call(File ivyFile) {
                                CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                                return callSiteArray[5].callCurrent((GroovyObject)this, ivyFile);
                            }

                            public Pattern getIvyFilePattern() {
                                CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                                return (Pattern)ScriptBytecodeAdapter.castToType(this.ivyFilePattern.get(), Pattern.class);
                            }

                            public Object getVersions() {
                                CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                                return this.versions.get();
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure24.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }

                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                stringArray[0] = "matcher";
                                stringArray[1] = "name";
                                stringArray[2] = "matches";
                                stringArray[3] = "plus";
                                stringArray[4] = "group";
                                stringArray[5] = "doCall";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[6];
                                _closure24.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure24.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure24.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        callSiteArray[0].call(moduleDir, this.ivyFilePattern.get(), new _closure24(this, this.getThisObject(), this.ivyFilePattern, versions));
                        List list = versions.get();
                        callSiteArray[1].call(this.grapes.get(), callSiteArray[2].callGetProperty(moduleDir), list);
                        return list;
                    }

                    public Object call(File moduleDir) {
                        CallSite[] callSiteArray = _closure23.$getCallSiteArray();
                        return callSiteArray[3].callCurrent((GroovyObject)this, moduleDir);
                    }

                    public Pattern getIvyFilePattern() {
                        CallSite[] callSiteArray = _closure23.$getCallSiteArray();
                        return (Pattern)ScriptBytecodeAdapter.castToType(this.ivyFilePattern.get(), Pattern.class);
                    }

                    public Map getGrapes() {
                        CallSite[] callSiteArray = _closure23.$getCallSiteArray();
                        return (Map)ScriptBytecodeAdapter.castToType(this.grapes.get(), Map.class);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure23.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "eachFileMatch";
                        stringArray[1] = "putAt";
                        stringArray[2] = "name";
                        stringArray[3] = "doCall";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[4];
                        _closure23.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_closure23.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure23.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                return callSiteArray[2].call((Object)groupDir, new _closure23(this, this.getThisObject(), this.ivyFilePattern, grapes));
            }

            public Object call(File groupDir) {
                CallSite[] callSiteArray = _enumerateGrapes_closure12.$getCallSiteArray();
                return callSiteArray[3].callCurrent((GroovyObject)this, groupDir);
            }

            public Map getBunches() {
                CallSite[] callSiteArray = _enumerateGrapes_closure12.$getCallSiteArray();
                return (Map)ScriptBytecodeAdapter.castToType(this.bunches.get(), Map.class);
            }

            public Pattern getIvyFilePattern() {
                CallSite[] callSiteArray = _enumerateGrapes_closure12.$getCallSiteArray();
                return (Pattern)ScriptBytecodeAdapter.castToType(this.ivyFilePattern.get(), Pattern.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _enumerateGrapes_closure12.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "putAt";
                stringArray[1] = "name";
                stringArray[2] = "eachDir";
                stringArray[3] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
                _enumerateGrapes_closure12.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_enumerateGrapes_closure12.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _enumerateGrapes_closure12.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[236].call(callSiteArray[237].callGroovyObjectGetProperty(this), new _enumerateGrapes_closure12(this, this, bunches, ivyFilePattern));
        return bunches.get();
    }

    @Override
    public URI[] resolve(Map args, Map ... dependencies) {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        return (URI[])ScriptBytecodeAdapter.castToType(callSiteArray[238].callCurrent(this, args, null, dependencies), URI[].class);
    }

    @Override
    public URI[] resolve(Map args, List depsInfo, Map ... dependencies) {
        Object object;
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        Object loader = callSiteArray[239].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"classLoader", callSiteArray[240].call((Object)args, "classLoader"), "refObject", callSiteArray[241].call((Object)args, "refObject"), "calleeDepth", DefaultTypeTransformation.booleanUnbox(object = callSiteArray[242].callGetProperty(args)) ? object : Integer.valueOf(DEFAULT_DEPTH)}));
        if (!DefaultTypeTransformation.booleanUnbox(loader)) {
            return (URI[])ScriptBytecodeAdapter.castToType(null, URI[].class);
        }
        return (URI[])ScriptBytecodeAdapter.castToType(callSiteArray[243].callCurrent(this, loader, args, depsInfo, dependencies), URI[].class);
    }

    public URI[] resolve(ClassLoader loader, Map args, Map ... dependencies) {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        return (URI[])ScriptBytecodeAdapter.castToType(callSiteArray[244].callCurrent(this, loader, args, null, dependencies), URI[].class);
    }

    public URI[] resolve(ClassLoader loader, Map args, List depsInfo, Map ... dependencies) {
        Reference<List> depsInfo2 = new Reference<List>(depsInfo);
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        Reference<Set> keys = new Reference<Set>((Set)ScriptBytecodeAdapter.castToType(callSiteArray[245].call(args), Set.class));
        public class _resolve_closure13
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference keys;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _resolve_closure13(Object _outerInstance, Object _thisObject, Reference keys) {
                Reference reference;
                CallSite[] callSiteArray = _resolve_closure13.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.keys = reference = keys;
            }

            public Object doCall(Object a) {
                CallSite[] callSiteArray = _resolve_closure13.$getCallSiteArray();
                Set badArgs = (Set)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), a), Set.class);
                if (DefaultTypeTransformation.booleanUnbox(badArgs) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call((Object)badArgs, this.keys.get()))) {
                    throw (Throwable)callSiteArray[3].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{callSiteArray[4].call(callSiteArray[5].call(this.keys.get(), badArgs), a)}, new String[]{"Mutually exclusive arguments passed into grab: ", ""}));
                }
                return null;
            }

            public Set getKeys() {
                CallSite[] callSiteArray = _resolve_closure13.$getCallSiteArray();
                return (Set)ScriptBytecodeAdapter.castToType(this.keys.get(), Set.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _resolve_closure13.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "getAt";
                stringArray[1] = "exclusiveGrabArgs";
                stringArray[2] = "disjoint";
                stringArray[3] = "<$constructor$>";
                stringArray[4] = "plus";
                stringArray[5] = "intersect";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[6];
                _resolve_closure13.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_resolve_closure13.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _resolve_closure13.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[246].call((Object)keys.get(), new _resolve_closure13(this, this, keys));
        if (!this.enableGrapes) {
            return (URI[])ScriptBytecodeAdapter.castToType(null, URI[].class);
        }
        int populateDepsInfo = 0;
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(depsInfo2.get(), null);
            populateDepsInfo = bl ? 1 : 0;
        } else {
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(depsInfo2.get(), null);
            populateDepsInfo = bl ? 1 : 0;
        }
        Reference<Set> localDeps = new Reference<Set>((Set)ScriptBytecodeAdapter.castToType(callSiteArray[247].callCurrent((GroovyObject)this, loader), Set.class));
        public class _resolve_closure14
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference localDeps;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _resolve_closure14(Object _outerInstance, Object _thisObject, Reference localDeps) {
                Reference reference;
                CallSite[] callSiteArray = _resolve_closure14.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.localDeps = reference = localDeps;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _resolve_closure14.$getCallSiteArray();
                IvyGrabRecord igr = (IvyGrabRecord)ScriptBytecodeAdapter.castToType(callSiteArray[0].callCurrent((GroovyObject)this, it), IvyGrabRecord.class);
                callSiteArray[1].call(callSiteArray[2].callGroovyObjectGetProperty(this), igr);
                return callSiteArray[3].call(this.localDeps.get(), igr);
            }

            public Set getLocalDeps() {
                CallSite[] callSiteArray = _resolve_closure14.$getCallSiteArray();
                return (Set)ScriptBytecodeAdapter.castToType(this.localDeps.get(), Set.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _resolve_closure14.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _resolve_closure14.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "createGrabRecord";
                stringArray[1] = "add";
                stringArray[2] = "grabRecordsForCurrDependencies";
                stringArray[3] = "add";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
                _resolve_closure14.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_resolve_closure14.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _resolve_closure14.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[248].call((Object)dependencies, new _resolve_closure14(this, this, localDeps));
        ResolveReport report = null;
        try {
            Object object = callSiteArray[249].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.despreadList(new Object[]{args}, new Object[]{callSiteArray[250].call(callSiteArray[251].call(localDeps.get()))}, new int[]{1}));
            report = (ResolveReport)ScriptBytecodeAdapter.castToType(object, ResolveReport.class);
        }
        catch (Exception e) {
            callSiteArray[252].call((Object)localDeps.get(), this.grabRecordsForCurrDependencies);
            callSiteArray[253].call(this.grabRecordsForCurrDependencies);
            throw (Throwable)e;
        }
        List results = ScriptBytecodeAdapter.createList(new Object[0]);
        ArtifactDownloadReport adl = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[254].call(callSiteArray[255].callGetProperty(report)), Iterator.class);
        while (iterator.hasNext()) {
            adl = (ArtifactDownloadReport)ScriptBytecodeAdapter.castToType(iterator.next(), ArtifactDownloadReport.class);
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[256].callGetProperty(adl))) continue;
            results = (List)ScriptBytecodeAdapter.castToType(callSiteArray[257].call((Object)results, callSiteArray[258].call(callSiteArray[259].callGetProperty(adl))), List.class);
        }
        if (populateDepsInfo != 0) {
            Object deps = callSiteArray[260].callGetProperty(report);
            public class _resolve_closure15
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference depsInfo;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _resolve_closure15(Object _outerInstance, Object _thisObject, Reference depsInfo) {
                    Reference reference;
                    CallSite[] callSiteArray = _resolve_closure15.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.depsInfo = reference = depsInfo;
                }

                public Object doCall(Object depNode) {
                    CallSite[] callSiteArray = _resolve_closure15.$getCallSiteArray();
                    Object id = callSiteArray[0].callGetProperty(depNode);
                    return callSiteArray[1].call(this.depsInfo.get(), ScriptBytecodeAdapter.createMap(new Object[]{"group", callSiteArray[2].callGetProperty(id), "module", callSiteArray[3].callGetProperty(id), "revision", callSiteArray[4].callGetProperty(id)}));
                }

                public List getDepsInfo() {
                    CallSite[] callSiteArray = _resolve_closure15.$getCallSiteArray();
                    return (List)ScriptBytecodeAdapter.castToType(this.depsInfo.get(), List.class);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _resolve_closure15.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "id";
                    stringArray[1] = "leftShift";
                    stringArray[2] = "organisation";
                    stringArray[3] = "name";
                    stringArray[4] = "revision";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[5];
                    _resolve_closure15.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_resolve_closure15.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _resolve_closure15.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[261].call(deps, new _resolve_closure15(this, this, depsInfo2));
        }
        return (URI[])ScriptBytecodeAdapter.asType(results, URI[].class);
    }

    private Set<IvyGrabRecord> getLoadedDepsForLoader(ClassLoader loader) {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        Set localDeps = (Set)ScriptBytecodeAdapter.castToType(callSiteArray[262].call(this.loadedDeps, loader), Set.class);
        if (ScriptBytecodeAdapter.compareEqual(localDeps, null)) {
            Object object = callSiteArray[263].callConstructor(LinkedHashSet.class);
            localDeps = (Set)ScriptBytecodeAdapter.castToType(object, Set.class);
            callSiteArray[264].call(this.loadedDeps, loader, localDeps);
        }
        return localDeps;
    }

    @Override
    public Map[] listDependencies(ClassLoader classLoader) {
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[265].call(this.loadedDeps, classLoader))) {
            Reference<List> results = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[0]));
            public class _listDependencies_closure16
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference results;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _listDependencies_closure16(Object _outerInstance, Object _thisObject, Reference results) {
                    Reference reference;
                    CallSite[] callSiteArray = _listDependencies_closure16.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.results = reference = results;
                }

                public Object doCall(IvyGrabRecord grabbed) {
                    CallSite[] callSiteArray = _listDependencies_closure16.$getCallSiteArray();
                    Map dep = ScriptBytecodeAdapter.createMap(new Object[]{"group", callSiteArray[0].callGetProperty(callSiteArray[1].callGroovyObjectGetProperty(grabbed)), "module", callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(grabbed)), "version", callSiteArray[4].callGetProperty(callSiteArray[5].callGroovyObjectGetProperty(grabbed))});
                    if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[6].callGroovyObjectGetProperty(grabbed), ScriptBytecodeAdapter.createList(new Object[]{"default"}))) {
                        Object object = callSiteArray[7].callGroovyObjectGetProperty(grabbed);
                        ScriptBytecodeAdapter.setProperty(object, null, dep, "conf");
                    }
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[8].callGroovyObjectGetProperty(grabbed))) {
                        Object object = callSiteArray[9].callGroovyObjectGetProperty(grabbed);
                        ScriptBytecodeAdapter.setProperty(object, null, dep, "changing");
                    }
                    if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[10].callGroovyObjectGetProperty(grabbed))) {
                        Object object = callSiteArray[11].callGroovyObjectGetProperty(grabbed);
                        ScriptBytecodeAdapter.setProperty(object, null, dep, "transitive");
                    }
                    if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[12].callGroovyObjectGetProperty(grabbed))) {
                        Object object = callSiteArray[13].callGroovyObjectGetProperty(grabbed);
                        ScriptBytecodeAdapter.setProperty(object, null, dep, "force");
                    }
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[14].callGroovyObjectGetProperty(grabbed))) {
                        Object object = callSiteArray[15].callGroovyObjectGetProperty(grabbed);
                        ScriptBytecodeAdapter.setProperty(object, null, dep, "classifier");
                    }
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[16].callGroovyObjectGetProperty(grabbed))) {
                        Object object = callSiteArray[17].callGroovyObjectGetProperty(grabbed);
                        ScriptBytecodeAdapter.setProperty(object, null, dep, "ext");
                    }
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[18].callGroovyObjectGetProperty(grabbed))) {
                        Object object = callSiteArray[19].callGroovyObjectGetProperty(grabbed);
                        ScriptBytecodeAdapter.setProperty(object, null, dep, "type");
                    }
                    return callSiteArray[20].call(this.results.get(), dep);
                }

                public Object call(IvyGrabRecord grabbed) {
                    CallSite[] callSiteArray = _listDependencies_closure16.$getCallSiteArray();
                    return callSiteArray[21].callCurrent((GroovyObject)this, grabbed);
                }

                public List getResults() {
                    CallSite[] callSiteArray = _listDependencies_closure16.$getCallSiteArray();
                    return (List)ScriptBytecodeAdapter.castToType(this.results.get(), List.class);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _listDependencies_closure16.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "organisation";
                    stringArray[1] = "mrid";
                    stringArray[2] = "name";
                    stringArray[3] = "mrid";
                    stringArray[4] = "revision";
                    stringArray[5] = "mrid";
                    stringArray[6] = "conf";
                    stringArray[7] = "conf";
                    stringArray[8] = "changing";
                    stringArray[9] = "changing";
                    stringArray[10] = "transitive";
                    stringArray[11] = "transitive";
                    stringArray[12] = "force";
                    stringArray[13] = "force";
                    stringArray[14] = "classifier";
                    stringArray[15] = "classifier";
                    stringArray[16] = "ext";
                    stringArray[17] = "ext";
                    stringArray[18] = "type";
                    stringArray[19] = "type";
                    stringArray[20] = "leftShift";
                    stringArray[21] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[22];
                    _listDependencies_closure16.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_listDependencies_closure16.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _listDependencies_closure16.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[266].call(callSiteArray[267].call(this.loadedDeps, classLoader), new _listDependencies_closure16(this, this, results));
            return (Map[])ScriptBytecodeAdapter.castToType(results.get(), Map[].class);
        }
        return (Map[])ScriptBytecodeAdapter.castToType(null, Map[].class);
    }

    @Override
    public void addResolver(Map<String, Object> args) {
        Object object;
        CallSite[] callSiteArray = GrapeIvy.$getCallSiteArray();
        ChainResolver chainResolver = (ChainResolver)ScriptBytecodeAdapter.castToType(callSiteArray[268].call((Object)this.settings, "downloadGrapes"), ChainResolver.class);
        IBiblioResolver resolver = (IBiblioResolver)ScriptBytecodeAdapter.castToType(callSiteArray[269].callConstructor(IBiblioResolver.class, ScriptBytecodeAdapter.createMap(new Object[]{"name", callSiteArray[270].callGetProperty(args), "root", callSiteArray[271].callGetProperty(args), "m2compatible", DefaultTypeTransformation.booleanUnbox(object = callSiteArray[272].callGetProperty(args)) ? object : Boolean.valueOf(true), "settings", this.settings})), IBiblioResolver.class);
        callSiteArray[273].call((Object)chainResolver, resolver);
        Object object2 = callSiteArray[274].call(Ivy.class, this.settings);
        this.ivyInstance = (Ivy)ScriptBytecodeAdapter.castToType(object2, Ivy.class);
        List list = ScriptBytecodeAdapter.createList(new Object[0]);
        this.resolvedDependencies = (Set)ScriptBytecodeAdapter.castToType(list, Set.class);
        List list2 = ScriptBytecodeAdapter.createList(new Object[0]);
        this.downloadedArtifacts = (Set)ScriptBytecodeAdapter.castToType(list2, Set.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != GrapeIvy.class) {
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

    public static int getDEFAULT_DEPTH() {
        return DEFAULT_DEPTH;
    }

    public boolean getEnableGrapes() {
        return this.enableGrapes;
    }

    public boolean isEnableGrapes() {
        return this.enableGrapes;
    }

    public void setEnableGrapes(boolean bl) {
        this.enableGrapes = bl;
    }

    public Ivy getIvyInstance() {
        return this.ivyInstance;
    }

    public void setIvyInstance(Ivy ivy) {
        this.ivyInstance = ivy;
    }

    public Set<String> getResolvedDependencies() {
        return this.resolvedDependencies;
    }

    public void setResolvedDependencies(Set<String> set) {
        this.resolvedDependencies = set;
    }

    public Set<String> getDownloadedArtifacts() {
        return this.downloadedArtifacts;
    }

    public void setDownloadedArtifacts(Set<String> set) {
        this.downloadedArtifacts = set;
    }

    public Map<ClassLoader, Set<IvyGrabRecord>> getLoadedDeps() {
        return this.loadedDeps;
    }

    public void setLoadedDeps(Map<ClassLoader, Set<IvyGrabRecord>> map) {
        this.loadedDeps = map;
    }

    public Set<IvyGrabRecord> getGrabRecordsForCurrDependencies() {
        return this.grabRecordsForCurrDependencies;
    }

    public void setGrabRecordsForCurrDependencies(Set<IvyGrabRecord> set) {
        this.grabRecordsForCurrDependencies = set;
    }

    public IvySettings getSettings() {
        return this.settings;
    }

    public void setSettings(IvySettings ivySettings) {
        this.settings = ivySettings;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "inject";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "getProperty";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "getLocalGrapeConfig";
        stringArray[7] = "exists";
        stringArray[8] = "getResource";
        stringArray[9] = "load";
        stringArray[10] = "canonicalPath";
        stringArray[11] = "toString";
        stringArray[12] = "println";
        stringArray[13] = "err";
        stringArray[14] = "plus";
        stringArray[15] = "message";
        stringArray[16] = "getResource";
        stringArray[17] = "load";
        stringArray[18] = "getGrapeCacheDir";
        stringArray[19] = "setVariable";
        stringArray[20] = "newInstance";
        stringArray[21] = "getProperty";
        stringArray[22] = "<$constructor$>";
        stringArray[23] = "getProperty";
        stringArray[24] = "<$constructor$>";
        stringArray[25] = "canonicalFile";
        stringArray[26] = "getProperty";
        stringArray[27] = "<$constructor$>";
        stringArray[28] = "<$constructor$>";
        stringArray[29] = "getGrapeDir";
        stringArray[30] = "<$constructor$>";
        stringArray[31] = "getProperty";
        stringArray[32] = "getGroovyRoot";
        stringArray[33] = "<$constructor$>";
        stringArray[34] = "canonicalFile";
        stringArray[35] = "<$constructor$>";
        stringArray[36] = "getGrapeDir";
        stringArray[37] = "<$constructor$>";
        stringArray[38] = "exists";
        stringArray[39] = "mkdirs";
        stringArray[40] = "isDirectory";
        stringArray[41] = "<$constructor$>";
        stringArray[42] = "classLoader";
        stringArray[43] = "isValidTargetClassLoader";
        stringArray[44] = "classLoader";
        stringArray[45] = "class";
        stringArray[46] = "refObject";
        stringArray[47] = "getCallingClass";
        stringArray[48] = "calleeDepth";
        stringArray[49] = "isValidTargetClassLoader";
        stringArray[50] = "parent";
        stringArray[51] = "isValidTargetClassLoader";
        stringArray[52] = "<$constructor$>";
        stringArray[53] = "isValidTargetClassLoaderClass";
        stringArray[54] = "class";
        stringArray[55] = "name";
        stringArray[56] = "name";
        stringArray[57] = "isValidTargetClassLoaderClass";
        stringArray[58] = "superclass";
        stringArray[59] = "name";
        stringArray[60] = "name";
        stringArray[61] = "isValidTargetClassLoaderClass";
        stringArray[62] = "superclass";
        stringArray[63] = "module";
        stringArray[64] = "artifactId";
        stringArray[65] = "artifact";
        stringArray[66] = "<$constructor$>";
        stringArray[67] = "group";
        stringArray[68] = "groupId";
        stringArray[69] = "organisation";
        stringArray[70] = "organization";
        stringArray[71] = "org";
        stringArray[72] = "ext";
        stringArray[73] = "type";
        stringArray[74] = "type";
        stringArray[75] = "version";
        stringArray[76] = "revision";
        stringArray[77] = "rev";
        stringArray[78] = "newInstance";
        stringArray[79] = "containsKey";
        stringArray[80] = "force";
        stringArray[81] = "containsKey";
        stringArray[82] = "changing";
        stringArray[83] = "containsKey";
        stringArray[84] = "transitive";
        stringArray[85] = "conf";
        stringArray[86] = "scope";
        stringArray[87] = "configuration";
        stringArray[88] = "startsWith";
        stringArray[89] = "endsWith";
        stringArray[90] = "getAt";
        stringArray[91] = "toList";
        stringArray[92] = "split";
        stringArray[93] = "classifier";
        stringArray[94] = "<$constructor$>";
        stringArray[95] = "grab";
        stringArray[96] = "version";
        stringArray[97] = "calleeDepth";
        stringArray[98] = "plus";
        stringArray[99] = "calleeDepth";
        stringArray[100] = "grab";
        stringArray[101] = "clear";
        stringArray[102] = "chooseClassLoader";
        stringArray[103] = "remove";
        stringArray[104] = "remove";
        stringArray[105] = "calleeDepth";
        stringArray[106] = "resolve";
        stringArray[107] = "iterator";
        stringArray[108] = "addURL";
        stringArray[109] = "toURL";
        stringArray[110] = "iterator";
        stringArray[111] = "<$constructor$>";
        stringArray[112] = "processCategoryMethods";
        stringArray[113] = "processOtherServices";
        stringArray[114] = "getLoadedDepsForLoader";
        stringArray[115] = "removeAll";
        stringArray[116] = "clear";
        stringArray[117] = "noExceptions";
        stringArray[118] = "endsWith";
        stringArray[119] = "toLowerCase";
        stringArray[120] = "name";
        stringArray[121] = "metaClassRegistry";
        stringArray[122] = "<$constructor$>";
        stringArray[123] = "getEntry";
        stringArray[124] = "MODULE_META_INF_FILE";
        stringArray[125] = "<$constructor$>";
        stringArray[126] = "load";
        stringArray[127] = "getInputStream";
        stringArray[128] = "<$constructor$>";
        stringArray[129] = "registerExtensionModuleFromProperties";
        stringArray[130] = "each";
        stringArray[131] = "<$constructor$>";
        stringArray[132] = "<$constructor$>";
        stringArray[133] = "getEntry";
        stringArray[134] = "processSerializedCategoryMethods";
        stringArray[135] = "getInputStream";
        stringArray[136] = "getEntry";
        stringArray[137] = "processRunners";
        stringArray[138] = "getInputStream";
        stringArray[139] = "getName";
        stringArray[140] = "<$constructor$>";
        stringArray[141] = "getEntry";
        stringArray[142] = "processSerializedCategoryMethods";
        stringArray[143] = "getInputStream";
        stringArray[144] = "getEntry";
        stringArray[145] = "processRunners";
        stringArray[146] = "getInputStream";
        stringArray[147] = "getName";
        stringArray[148] = "each";
        stringArray[149] = "readLines";
        stringArray[150] = "text";
        stringArray[151] = "each";
        stringArray[152] = "findAll";
        stringArray[153] = "readLines";
        stringArray[154] = "text";
        stringArray[155] = "resolutionCacheManager";
        stringArray[156] = "currentTimeMillis";
        stringArray[157] = "<$constructor$>";
        stringArray[158] = "newInstance";
        stringArray[159] = "plus";
        stringArray[160] = "getAt";
        stringArray[161] = "toString";
        stringArray[162] = "addConfiguration";
        stringArray[163] = "<$constructor$>";
        stringArray[164] = "setLastModified";
        stringArray[165] = "addExcludesIfNeeded";
        stringArray[166] = "iterator";
        stringArray[167] = "conf";
        stringArray[168] = "find";
        stringArray[169] = "dependencies";
        stringArray[170] = "createAndAddDependencyArtifactDescriptor";
        stringArray[171] = "<$constructor$>";
        stringArray[172] = "mrid";
        stringArray[173] = "force";
        stringArray[174] = "changing";
        stringArray[175] = "transitive";
        stringArray[176] = "each";
        stringArray[177] = "createAndAddDependencyArtifactDescriptor";
        stringArray[178] = "addDependency";
        stringArray[179] = "setValidate";
        stringArray[180] = "setOutputReport";
        stringArray[181] = "setConfs";
        stringArray[182] = "<$constructor$>";
        stringArray[183] = "containsKey";
        stringArray[184] = "validate";
        stringArray[185] = "autoDownload";
        stringArray[186] = "settings";
        stringArray[187] = "disableChecksums";
        stringArray[188] = "setVariable";
        stringArray[189] = "settings";
        stringArray[190] = "getProperty";
        stringArray[191] = "getProperty";
        stringArray[192] = "addIvyListener";
        stringArray[193] = "eventManager";
        stringArray[194] = "resolve";
        stringArray[195] = "previous";
        stringArray[196] = "println";
        stringArray[197] = "err";
        stringArray[198] = "sleep";
        stringArray[199] = "<$constructor$>";
        stringArray[200] = "message";
        stringArray[201] = "hasError";
        stringArray[202] = "<$constructor$>";
        stringArray[203] = "allProblemMessages";
        stringArray[204] = "downloadSize";
        stringArray[205] = "println";
        stringArray[206] = "err";
        stringArray[207] = "rightShift";
        stringArray[208] = "downloadSize";
        stringArray[209] = "downloadTime";
        stringArray[210] = "join";
        stringArray[211] = "allArtifactsReports";
        stringArray[212] = "moduleDescriptor";
        stringArray[213] = "preserveFiles";
        stringArray[214] = "delete";
        stringArray[215] = "getResolvedIvyFileInCache";
        stringArray[216] = "moduleRevisionId";
        stringArray[217] = "delete";
        stringArray[218] = "getResolvedIvyPropertiesInCache";
        stringArray[219] = "moduleRevisionId";
        stringArray[220] = "getAt";
        stringArray[221] = "classifier";
        stringArray[222] = "<$constructor$>";
        stringArray[223] = "name";
        stringArray[224] = "mrid";
        stringArray[225] = "type";
        stringArray[226] = "ext";
        stringArray[227] = "classifier";
        stringArray[228] = "classifier";
        stringArray[229] = "each";
        stringArray[230] = "addDependencyArtifact";
        stringArray[231] = "eachDir";
        stringArray[232] = "grapeCacheDir";
        stringArray[233] = "containsKey";
        stringArray[234] = "each";
        stringArray[235] = "excludes";
        stringArray[236] = "eachDir";
        stringArray[237] = "grapeCacheDir";
        stringArray[238] = "resolve";
        stringArray[239] = "chooseClassLoader";
        stringArray[240] = "remove";
        stringArray[241] = "remove";
        stringArray[242] = "calleeDepth";
        stringArray[243] = "resolve";
        stringArray[244] = "resolve";
        stringArray[245] = "keySet";
        stringArray[246] = "each";
        stringArray[247] = "getLoadedDepsForLoader";
        stringArray[248] = "each";
        stringArray[249] = "getDependencies";
        stringArray[250] = "reverse";
        stringArray[251] = "asList";
        stringArray[252] = "removeAll";
        stringArray[253] = "clear";
        stringArray[254] = "iterator";
        stringArray[255] = "allArtifactsReports";
        stringArray[256] = "localFile";
        stringArray[257] = "plus";
        stringArray[258] = "toURI";
        stringArray[259] = "localFile";
        stringArray[260] = "dependencies";
        stringArray[261] = "each";
        stringArray[262] = "get";
        stringArray[263] = "<$constructor$>";
        stringArray[264] = "put";
        stringArray[265] = "containsKey";
        stringArray[266] = "each";
        stringArray[267] = "getAt";
        stringArray[268] = "getResolver";
        stringArray[269] = "<$constructor$>";
        stringArray[270] = "name";
        stringArray[271] = "root";
        stringArray[272] = "m2Compatible";
        stringArray[273] = "add";
        stringArray[274] = "newInstance";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[275];
        GrapeIvy.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(GrapeIvy.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = GrapeIvy.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }

    public class _closure1
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        public _closure1(Object _outerInstance, Object _thisObject) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            super(_outerInstance, _thisObject);
        }

        /*
         * WARNING - void declaration
         */
        public Object doCall(Object m, Object g) {
            void var2_2;
            Reference<Object> m2 = new Reference<Object>(m);
            Reference<void> g2 = new Reference<void>(var2_2);
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            public class _closure17
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference m;
                private /* synthetic */ Reference g;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure17(Object _outerInstance, Object _thisObject, Reference m, Reference g) {
                    Reference reference;
                    Reference reference2;
                    CallSite[] callSiteArray = _closure17.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.m = reference2 = m;
                    this.g = reference = g;
                }

                public Object doCall(Object a) {
                    CallSite[] callSiteArray = _closure17.$getCallSiteArray();
                    Set set = (Set)ScriptBytecodeAdapter.asType(callSiteArray[0].call(this.g.get(), a), Set.class);
                    callSiteArray[1].call(this.m.get(), a, set);
                    return set;
                }

                public Object getM() {
                    CallSite[] callSiteArray = _closure17.$getCallSiteArray();
                    return this.m.get();
                }

                public Object getG() {
                    CallSite[] callSiteArray = _closure17.$getCallSiteArray();
                    return this.g.get();
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _closure17.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "minus";
                    stringArray[1] = "putAt";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[2];
                    _closure17.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_closure17.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _closure17.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[0].call((Object)g2.get(), new _closure17(this, this.getThisObject(), m2, g2));
            return m2.get();
        }

        /*
         * WARNING - void declaration
         */
        public Object call(Object m, Object g) {
            void var2_2;
            Reference<Object> m2 = new Reference<Object>(m);
            Reference<void> g2 = new Reference<void>(var2_2);
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            return callSiteArray[1].callCurrent(this, m2.get(), g2.get());
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != _closure1.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "each";
            stringArray[1] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[2];
            _closure1.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(_closure1.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = _closure1.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }
}

