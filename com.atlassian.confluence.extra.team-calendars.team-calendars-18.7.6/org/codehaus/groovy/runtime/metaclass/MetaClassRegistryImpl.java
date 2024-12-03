/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import groovy.lang.ExpandoMetaClass;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import groovy.lang.MetaClassRegistryChangeEvent;
import groovy.lang.MetaClassRegistryChangeEventListener;
import groovy.lang.MetaMethod;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.DefaultGroovyStaticMethods;
import org.codehaus.groovy.runtime.m12n.ExtensionModule;
import org.codehaus.groovy.runtime.m12n.ExtensionModuleRegistry;
import org.codehaus.groovy.runtime.m12n.ExtensionModuleScanner;
import org.codehaus.groovy.runtime.metaclass.DefaultMetaClassInfo;
import org.codehaus.groovy.runtime.metaclass.NewInstanceMetaMethod;
import org.codehaus.groovy.runtime.metaclass.NewMetaMethod;
import org.codehaus.groovy.runtime.metaclass.NewStaticMetaMethod;
import org.codehaus.groovy.util.FastArray;
import org.codehaus.groovy.util.ManagedConcurrentLinkedQueue;
import org.codehaus.groovy.util.ReferenceBundle;
import org.codehaus.groovy.vmplugin.VMPluginFactory;

public class MetaClassRegistryImpl
implements MetaClassRegistry {
    @Deprecated
    public static final String MODULE_META_INF_FILE = "META-INF/services/org.codehaus.groovy.runtime.ExtensionModule";
    private final boolean useAccessible;
    private final FastArray instanceMethods = new FastArray();
    private final FastArray staticMethods = new FastArray();
    private final LinkedList<MetaClassRegistryChangeEventListener> changeListenerList = new LinkedList();
    private final LinkedList<MetaClassRegistryChangeEventListener> nonRemoveableChangeListenerList = new LinkedList();
    private final ManagedConcurrentLinkedQueue<MetaClass> metaClassInfo = new ManagedConcurrentLinkedQueue(ReferenceBundle.getWeakBundle());
    private final ExtensionModuleRegistry moduleRegistry = new ExtensionModuleRegistry();
    public static final int LOAD_DEFAULT = 0;
    public static final int DONT_LOAD_DEFAULT = 1;
    private static MetaClassRegistry instanceInclude;
    private static MetaClassRegistry instanceExclude;
    private volatile MetaClassRegistry.MetaClassCreationHandle metaClassCreationHandle = new MetaClassRegistry.MetaClassCreationHandle();

    public MetaClassRegistryImpl() {
        this(0, true);
    }

    public MetaClassRegistryImpl(int loadDefault) {
        this(loadDefault, true);
    }

    public MetaClassRegistryImpl(boolean useAccessible) {
        this(0, useAccessible);
    }

    public MetaClassRegistryImpl(int loadDefault, boolean useAccessible) {
        this.useAccessible = useAccessible;
        if (loadDefault == 0) {
            Class[] staticPluginDGMs;
            Class[] pluginDGMs;
            HashMap<CachedClass, List<MetaMethod>> map = new HashMap<CachedClass, List<MetaMethod>>();
            this.registerMethods(null, true, true, map);
            Class[] additionals = DefaultGroovyMethods.ADDITIONAL_CLASSES;
            for (int i = 0; i != additionals.length; ++i) {
                this.createMetaMethodFromClass(map, additionals[i]);
            }
            for (Class plugin : pluginDGMs = VMPluginFactory.getPlugin().getPluginDefaultGroovyMethods()) {
                this.registerMethods(plugin, false, true, map);
            }
            this.registerMethods(DefaultGroovyStaticMethods.class, false, false, map);
            for (Class plugin : staticPluginDGMs = VMPluginFactory.getPlugin().getPluginStaticGroovyMethods()) {
                this.registerMethods(plugin, false, false, map);
            }
            ExtensionModuleScanner scanner = new ExtensionModuleScanner(new DefaultModuleListener(map), this.getClass().getClassLoader());
            scanner.scanClasspathModules();
            MetaClassRegistryImpl.refreshMopMethods(map);
        }
        this.installMetaClassCreationHandle();
        MetaClass emcMetaClass = this.metaClassCreationHandle.create(ExpandoMetaClass.class, this);
        emcMetaClass.initialize();
        ClassInfo.getClassInfo(ExpandoMetaClass.class).setStrongMetaClass(emcMetaClass);
        this.addNonRemovableMetaClassRegistryChangeEventListener(new MetaClassRegistryChangeEventListener(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void updateConstantMetaClass(MetaClassRegistryChangeEvent cmcu) {
                ManagedConcurrentLinkedQueue managedConcurrentLinkedQueue = MetaClassRegistryImpl.this.metaClassInfo;
                synchronized (managedConcurrentLinkedQueue) {
                    MetaClassRegistryImpl.this.metaClassInfo.add(cmcu.getNewMetaClass());
                    DefaultMetaClassInfo.getNewConstantMetaClassVersioning();
                    Class c = cmcu.getClassToUpdate();
                    DefaultMetaClassInfo.setPrimitiveMeta(c, cmcu.getNewMetaClass() == null);
                    try {
                        Field sdyn = c.getDeclaredField("__$stMC");
                        sdyn.setBoolean(null, cmcu.getNewMetaClass() != null);
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
            }
        });
    }

    private static void refreshMopMethods(Map<CachedClass, List<MetaMethod>> map) {
        for (Map.Entry<CachedClass, List<MetaMethod>> e : map.entrySet()) {
            CachedClass cls = e.getKey();
            cls.setNewMopMethods(e.getValue());
        }
    }

    public void registerExtensionModuleFromProperties(Properties properties, ClassLoader classLoader, Map<CachedClass, List<MetaMethod>> map) {
        ExtensionModuleScanner scanner = new ExtensionModuleScanner(new DefaultModuleListener(map), classLoader);
        scanner.scanExtensionModuleFromProperties(properties);
    }

    public ExtensionModuleRegistry getModuleRegistry() {
        return this.moduleRegistry;
    }

    private void installMetaClassCreationHandle() {
        try {
            Class<?> customMetaClassHandle = Class.forName("groovy.runtime.metaclass.CustomMetaClassCreationHandle");
            Constructor<?> customMetaClassHandleConstructor = customMetaClassHandle.getConstructor(new Class[0]);
            this.metaClassCreationHandle = (MetaClassRegistry.MetaClassCreationHandle)customMetaClassHandleConstructor.newInstance(new Object[0]);
        }
        catch (ClassNotFoundException e) {
            this.metaClassCreationHandle = new MetaClassRegistry.MetaClassCreationHandle();
        }
        catch (Exception e) {
            throw new GroovyRuntimeException("Could not instantiate custom Metaclass creation handle: " + e, e);
        }
    }

    private void registerMethods(Class theClass, boolean useMethodWrapper, boolean useInstanceMethods, Map<CachedClass, List<MetaMethod>> map) {
        if (useMethodWrapper) {
            try {
                List<GeneratedMetaMethod.DgmMethodRecord> records = GeneratedMetaMethod.DgmMethodRecord.loadDgmInfo();
                for (GeneratedMetaMethod.DgmMethodRecord record : records) {
                    Class[] newParams = new Class[record.parameters.length - 1];
                    System.arraycopy(record.parameters, 1, newParams, 0, record.parameters.length - 1);
                    GeneratedMetaMethod.Proxy method = new GeneratedMetaMethod.Proxy(record.className, record.methodName, ReflectionCache.getCachedClass(record.parameters[0]), record.returnType, newParams);
                    CachedClass declClass = ((MetaMethod)method).getDeclaringClass();
                    List<MetaMethod> arr = map.get(declClass);
                    if (arr == null) {
                        arr = new ArrayList<MetaMethod>(4);
                        map.put(declClass, arr);
                    }
                    arr.add(method);
                    this.instanceMethods.add(method);
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            CachedMethod[] methods;
            for (CachedMethod method : methods = ReflectionCache.getCachedClass(theClass).getMethods()) {
                NewMetaMethod metaMethod;
                CachedClass[] paramTypes;
                int mod = method.getModifiers();
                if (!Modifier.isStatic(mod) || !Modifier.isPublic(mod) || method.getCachedMethod().getAnnotation(Deprecated.class) != null || (paramTypes = method.getParameterTypes()).length <= 0) continue;
                List<MetaMethod> arr = map.get(paramTypes[0]);
                if (arr == null) {
                    arr = new ArrayList<MetaMethod>(4);
                    map.put(paramTypes[0], arr);
                }
                if (useInstanceMethods) {
                    metaMethod = new NewInstanceMetaMethod(method);
                    arr.add(metaMethod);
                    this.instanceMethods.add(metaMethod);
                    continue;
                }
                metaMethod = new NewStaticMetaMethod(method);
                arr.add(metaMethod);
                this.staticMethods.add(metaMethod);
            }
        }
    }

    private void createMetaMethodFromClass(Map<CachedClass, List<MetaMethod>> map, Class aClass) {
        try {
            MetaMethod method = (MetaMethod)aClass.newInstance();
            CachedClass declClass = method.getDeclaringClass();
            List<MetaMethod> arr = map.get(declClass);
            if (arr == null) {
                arr = new ArrayList<MetaMethod>(4);
                map.put(declClass, arr);
            }
            arr.add(method);
            this.instanceMethods.add(method);
        }
        catch (InstantiationException instantiationException) {
        }
        catch (IllegalAccessException illegalAccessException) {
            // empty catch block
        }
    }

    @Override
    public final MetaClass getMetaClass(Class theClass) {
        return ClassInfo.getClassInfo(theClass).getMetaClass();
    }

    public MetaClass getMetaClass(Object obj) {
        return ClassInfo.getClassInfo(obj.getClass()).getMetaClass(obj);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setMetaClass(Class theClass, MetaClass oldMc, MetaClass newMc) {
        ClassInfo info = ClassInfo.getClassInfo(theClass);
        MetaClass mc = null;
        info.lock();
        try {
            mc = info.getStrongMetaClass();
            info.setStrongMetaClass(newMc);
        }
        finally {
            info.unlock();
        }
        if (oldMc == null && mc != newMc || oldMc != null && mc != newMc && mc != oldMc) {
            this.fireConstantMetaClassUpdate(null, theClass, mc, newMc);
        }
    }

    @Override
    public void removeMetaClass(Class theClass) {
        this.setMetaClass(theClass, null, null);
    }

    @Override
    public void setMetaClass(Class theClass, MetaClass theMetaClass) {
        this.setMetaClass(theClass, null, theMetaClass);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMetaClass(Object obj, MetaClass theMetaClass) {
        Class<?> theClass = obj.getClass();
        ClassInfo info = ClassInfo.getClassInfo(theClass);
        MetaClass oldMC = null;
        info.lock();
        try {
            oldMC = info.getPerInstanceMetaClass(obj);
            info.setPerInstanceMetaClass(obj, theMetaClass);
        }
        finally {
            info.unlock();
        }
        this.fireConstantMetaClassUpdate(obj, theClass, oldMC, theMetaClass);
    }

    public boolean useAccessible() {
        return this.useAccessible;
    }

    @Override
    public MetaClassRegistry.MetaClassCreationHandle getMetaClassCreationHandler() {
        return this.metaClassCreationHandle;
    }

    @Override
    public void setMetaClassCreationHandle(MetaClassRegistry.MetaClassCreationHandle handle) {
        if (handle == null) {
            throw new IllegalArgumentException("Cannot set MetaClassCreationHandle to null value!");
        }
        ClassInfo.clearModifiedExpandos();
        handle.setDisableCustomMetaClassLookup(this.metaClassCreationHandle.isDisableCustomMetaClassLookup());
        this.metaClassCreationHandle = handle;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addMetaClassRegistryChangeEventListener(MetaClassRegistryChangeEventListener listener) {
        LinkedList<MetaClassRegistryChangeEventListener> linkedList = this.changeListenerList;
        synchronized (linkedList) {
            this.changeListenerList.add(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addNonRemovableMetaClassRegistryChangeEventListener(MetaClassRegistryChangeEventListener listener) {
        LinkedList<MetaClassRegistryChangeEventListener> linkedList = this.changeListenerList;
        synchronized (linkedList) {
            this.nonRemoveableChangeListenerList.add(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMetaClassRegistryChangeEventListener(MetaClassRegistryChangeEventListener listener) {
        LinkedList<MetaClassRegistryChangeEventListener> linkedList = this.changeListenerList;
        synchronized (linkedList) {
            this.changeListenerList.remove(listener);
        }
    }

    protected void fireConstantMetaClassUpdate(Object obj, Class c, MetaClass oldMC, MetaClass newMc) {
        MetaClassRegistryChangeEventListener[] listener = this.getMetaClassRegistryChangeEventListeners();
        MetaClassRegistryChangeEvent cmcu = new MetaClassRegistryChangeEvent(this, obj, c, oldMC, newMc);
        for (int i = 0; i < listener.length; ++i) {
            listener[i].updateConstantMetaClass(cmcu);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MetaClassRegistryChangeEventListener[] getMetaClassRegistryChangeEventListeners() {
        LinkedList<MetaClassRegistryChangeEventListener> linkedList = this.changeListenerList;
        synchronized (linkedList) {
            ArrayList<MetaClassRegistryChangeEventListener> ret = new ArrayList<MetaClassRegistryChangeEventListener>(this.changeListenerList.size() + this.nonRemoveableChangeListenerList.size());
            ret.addAll(this.nonRemoveableChangeListenerList);
            ret.addAll(this.changeListenerList);
            return ret.toArray(new MetaClassRegistryChangeEventListener[ret.size()]);
        }
    }

    public static synchronized MetaClassRegistry getInstance(int includeExtension) {
        if (includeExtension != 1) {
            if (instanceInclude == null) {
                instanceInclude = new MetaClassRegistryImpl();
            }
            return instanceInclude;
        }
        if (instanceExclude == null) {
            instanceExclude = new MetaClassRegistryImpl(1);
        }
        return instanceExclude;
    }

    public FastArray getInstanceMethods() {
        return this.instanceMethods;
    }

    public FastArray getStaticMethods() {
        return this.staticMethods;
    }

    @Override
    public Iterator iterator() {
        final MetaClass[] refs = this.metaClassInfo.toArray((MetaClass[])new MetaClass[0]);
        return new Iterator(){
            private int index = 0;
            private MetaClass currentMeta;
            private boolean hasNextCalled = false;
            private boolean hasNext = false;

            @Override
            public boolean hasNext() {
                if (this.hasNextCalled) {
                    return this.hasNext;
                }
                this.hasNextCalled = true;
                if (this.index < refs.length) {
                    this.hasNext = true;
                    this.currentMeta = refs[this.index];
                    ++this.index;
                } else {
                    this.hasNext = false;
                }
                return this.hasNext;
            }

            private void ensureNext() {
                this.hasNext();
                this.hasNextCalled = false;
            }

            public Object next() {
                this.ensureNext();
                return this.currentMeta;
            }

            @Override
            public void remove() {
                this.ensureNext();
                MetaClassRegistryImpl.this.setMetaClass(this.currentMeta.getTheClass(), this.currentMeta, null);
                this.currentMeta = null;
            }
        };
    }

    private class DefaultModuleListener
    implements ExtensionModuleScanner.ExtensionModuleListener {
        private final Map<CachedClass, List<MetaMethod>> map;

        public DefaultModuleListener(Map<CachedClass, List<MetaMethod>> map) {
            this.map = map;
        }

        @Override
        public void onModule(ExtensionModule module) {
            if (MetaClassRegistryImpl.this.moduleRegistry.hasModule(module.getName())) {
                ExtensionModule loadedModule = MetaClassRegistryImpl.this.moduleRegistry.getModule(module.getName());
                if (loadedModule.getVersion().equals(module.getVersion())) {
                    return;
                }
                throw new GroovyRuntimeException("Conflicting module versions. Module [" + module.getName() + " is loaded in version " + loadedModule.getVersion() + " and you are trying to load version " + module.getVersion());
            }
            MetaClassRegistryImpl.this.moduleRegistry.addModule(module);
            List<MetaMethod> metaMethods = module.getMetaMethods();
            for (MetaMethod metaMethod : metaMethods) {
                CachedClass cachedClass = metaMethod.getDeclaringClass();
                List<MetaMethod> methods = this.map.get(cachedClass);
                if (methods == null) {
                    methods = new ArrayList<MetaMethod>(4);
                    this.map.put(cachedClass, methods);
                }
                methods.add(metaMethod);
                if (metaMethod.isStatic()) {
                    MetaClassRegistryImpl.this.staticMethods.add(metaMethod);
                    continue;
                }
                MetaClassRegistryImpl.this.instanceMethods.add(metaMethod);
            }
        }
    }
}

