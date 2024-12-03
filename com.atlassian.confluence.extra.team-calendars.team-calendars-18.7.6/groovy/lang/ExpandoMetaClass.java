/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.AdaptingMetaClass;
import groovy.lang.Closure;
import groovy.lang.ExpandoMetaClassCreationHandle;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovySystem;
import groovy.lang.MetaBeanProperty;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaClassRegistry;
import groovy.lang.MetaMethod;
import groovy.lang.MetaProperty;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.MixinInMetaClass;
import org.codehaus.groovy.runtime.DefaultCachedMethodKey;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.runtime.MethodKey;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.ConstructorMetaMethodSite;
import org.codehaus.groovy.runtime.callsite.PogoMetaClassSite;
import org.codehaus.groovy.runtime.callsite.PojoMetaClassSite;
import org.codehaus.groovy.runtime.callsite.StaticMetaClassSite;
import org.codehaus.groovy.runtime.metaclass.ClosureMetaMethod;
import org.codehaus.groovy.runtime.metaclass.ClosureStaticMetaMethod;
import org.codehaus.groovy.runtime.metaclass.DefaultMetaClassInfo;
import org.codehaus.groovy.runtime.metaclass.MethodSelectionException;
import org.codehaus.groovy.runtime.metaclass.MixedInMetaClass;
import org.codehaus.groovy.runtime.metaclass.MixinInstanceMetaMethod;
import org.codehaus.groovy.runtime.metaclass.OwnedMetaClass;
import org.codehaus.groovy.runtime.metaclass.ThreadManagedMetaBeanProperty;
import org.codehaus.groovy.util.FastArray;

public class ExpandoMetaClass
extends MetaClassImpl
implements GroovyObject {
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
    private static final String META_CLASS = "metaClass";
    private static final String CLASS = "class";
    private static final String META_METHODS = "metaMethods";
    private static final String METHODS = "methods";
    private static final String PROPERTIES = "properties";
    public static final String STATIC_QUALIFIER = "static";
    public static final String CONSTRUCTOR = "constructor";
    private static final String CLASS_PROPERTY = "class";
    private static final String META_CLASS_PROPERTY = "metaClass";
    private static final String GROOVY_CONSTRUCTOR = "<init>";
    private MetaClass myMetaClass;
    private boolean initialized;
    private volatile boolean modified;
    private boolean initCalled;
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock readLock = this.rwl.readLock();
    private final Lock writeLock = this.rwl.writeLock();
    private final boolean allowChangesAfterInit;
    public boolean inRegistry;
    private final Set<MetaMethod> inheritedMetaMethods = new HashSet<MetaMethod>();
    private final Map<String, MetaProperty> beanPropertyCache = new ConcurrentHashMap<String, MetaProperty>(16, 0.75f, 1);
    private final Map<String, MetaProperty> staticBeanPropertyCache = new ConcurrentHashMap<String, MetaProperty>(16, 0.75f, 1);
    private final Map<MethodKey, MetaMethod> expandoMethods = new ConcurrentHashMap<MethodKey, MetaMethod>(16, 0.75f, 1);
    private final ConcurrentHashMap expandoSubclassMethods = new ConcurrentHashMap(16, 0.75f, 1);
    private final Map<String, MetaProperty> expandoProperties = new ConcurrentHashMap<String, MetaProperty>(16, 0.75f, 1);
    private ClosureStaticMetaMethod invokeStaticMethodMethod;
    private final Set<MixinInMetaClass> mixinClasses = new LinkedHashSet<MixinInMetaClass>();

    public Collection getExpandoSubclassMethods() {
        return this.expandoSubclassMethods.values();
    }

    public ExpandoMetaClass(Class theClass, boolean register, boolean allowChangesAfterInit, MetaMethod[] add) {
        this(GroovySystem.getMetaClassRegistry(), theClass, register, allowChangesAfterInit, add);
    }

    public ExpandoMetaClass(MetaClassRegistry registry, Class theClass, boolean register, boolean allowChangesAfterInit, MetaMethod[] add) {
        super(registry, theClass, add);
        this.myMetaClass = InvokerHelper.getMetaClass(this.getClass());
        this.inRegistry = register;
        this.allowChangesAfterInit = allowChangesAfterInit;
    }

    public ExpandoMetaClass(Class theClass) {
        this(theClass, false, false, null);
    }

    public ExpandoMetaClass(Class theClass, MetaMethod[] add) {
        this(theClass, false, false, add);
    }

    public ExpandoMetaClass(Class theClass, boolean register) {
        this(theClass, register, false, null);
    }

    public ExpandoMetaClass(Class theClass, boolean register, MetaMethod[] add) {
        this(theClass, register, false, add);
    }

    public ExpandoMetaClass(Class theClass, boolean register, boolean allowChangesAfterInit) {
        this(theClass, register, allowChangesAfterInit, null);
    }

    @Override
    public MetaMethod findMixinMethod(String methodName, Class[] arguments) {
        for (MixinInMetaClass mixin : this.mixinClasses) {
            MetaMethod noParam;
            MetaMethod metaMethod;
            CachedClass mixinClass = mixin.getMixinClass();
            MetaClass metaClass = mixinClass.classInfo.getMetaClassForClass();
            if (metaClass == null) {
                metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(mixinClass.getTheClass());
            }
            if ((metaMethod = metaClass.pickMethod(methodName, arguments)) == null && metaClass instanceof MetaClassImpl) {
                MetaClassImpl mc = (MetaClassImpl)metaClass;
                for (CachedClass cl = mc.getTheCachedClass().getCachedSuperClass(); cl != null && (metaMethod = mc.getMethodWithoutCaching(cl.getTheClass(), methodName, arguments, false)) == null; cl = cl.getCachedSuperClass()) {
                }
            }
            if (metaMethod == null) continue;
            MixinInstanceMetaMethod method = new MixinInstanceMetaMethod(metaMethod, mixin);
            if (method.getParameterTypes().length == 1 && !method.getParameterTypes()[0].isPrimitive && (noParam = this.pickMethod(methodName, EMPTY_CLASS_ARRAY)) == null && arguments.length != 0) {
                try {
                    this.findMixinMethod(methodName, EMPTY_CLASS_ARRAY);
                }
                catch (MethodSelectionException methodSelectionException) {
                    // empty catch block
                }
            }
            this.registerInstanceMethod(method);
            return method;
        }
        return null;
    }

    @Override
    protected void onInvokeMethodFoundInHierarchy(MetaMethod method) {
        this.invokeMethodMethod = method;
    }

    @Override
    protected void onSuperMethodFoundInHierarchy(MetaMethod method) {
        this.addSuperMethodIfNotOverridden(method);
    }

    @Override
    protected void onSuperPropertyFoundInHierarchy(MetaBeanProperty property) {
        this.addMetaBeanProperty(property);
    }

    @Override
    protected void onSetPropertyFoundInHierarchy(MetaMethod method) {
        this.setPropertyMethod = method;
    }

    @Override
    protected void onGetPropertyFoundInHierarchy(MetaMethod method) {
        this.getPropertyMethod = method;
    }

    @Override
    public boolean isModified() {
        return this.modified;
    }

    public void registerSubclassInstanceMethod(String name, Class klazz, Closure closure) {
        List<MetaMethod> list = ClosureMetaMethod.createMethodList(name, klazz, closure);
        for (MetaMethod metaMethod : list) {
            this.registerSubclassInstanceMethod(metaMethod);
        }
    }

    public void registerSubclassInstanceMethod(MetaMethod metaMethod) {
        this.modified = true;
        String name = metaMethod.getName();
        Object methodOrList = this.expandoSubclassMethods.get(name);
        if (methodOrList == null) {
            this.expandoSubclassMethods.put(name, metaMethod);
        } else if (methodOrList instanceof MetaMethod) {
            FastArray arr = new FastArray(2);
            arr.add(methodOrList);
            arr.add(metaMethod);
            this.expandoSubclassMethods.put(name, arr);
        } else {
            ((FastArray)methodOrList).add(metaMethod);
        }
    }

    public void addMixinClass(MixinInMetaClass mixin) {
        this.mixinClasses.add(mixin);
    }

    public Object castToMixedType(Object obj, Class type) {
        for (MixinInMetaClass mixin : this.mixinClasses) {
            if (!type.isAssignableFrom(mixin.getMixinClass().getTheClass())) continue;
            return mixin.getMixinInstance(obj);
        }
        return null;
    }

    public static void enableGlobally() {
        DefaultMetaClassInfo.setWithoutCustomMetaclassCreationHandle(false);
        ExpandoMetaClassCreationHandle.enable();
    }

    public static void disableGlobally() {
        DefaultMetaClassInfo.setWithoutCustomMetaclassCreationHandle(true);
        ExpandoMetaClassCreationHandle.disable();
    }

    @Override
    public void initialize() {
        try {
            this.writeLock.lock();
            if (!this.isInitialized()) {
                super.initialize();
                this.setInitialized(true);
                this.initCalled = true;
            }
        }
        finally {
            this.readLock.lock();
            this.writeLock.unlock();
            this.readLock.unlock();
        }
    }

    @Override
    protected boolean isInitialized() {
        try {
            this.readLock.lock();
            boolean bl = this.initialized;
            return bl;
        }
        finally {
            this.readLock.unlock();
        }
    }

    protected void setInitialized(boolean b) {
        this.initialized = b;
    }

    private void addSuperMethodIfNotOverridden(final MetaMethod metaMethodFromSuper) {
        this.performOperationOnMetaClass(new Callable(){

            @Override
            public void call() {
                MetaMethod existing = null;
                try {
                    existing = ExpandoMetaClass.this.pickMethod(metaMethodFromSuper.getName(), metaMethodFromSuper.getNativeParameterTypes());
                }
                catch (GroovyRuntimeException groovyRuntimeException) {
                    // empty catch block
                }
                if (existing == null) {
                    this.addMethodWithKey(metaMethodFromSuper);
                } else {
                    boolean isGroovyMethod = ExpandoMetaClass.this.getMetaMethods().contains(existing);
                    if (isGroovyMethod) {
                        this.addMethodWithKey(metaMethodFromSuper);
                    } else if (ExpandoMetaClass.this.inheritedMetaMethods.contains(existing)) {
                        ExpandoMetaClass.this.inheritedMetaMethods.remove(existing);
                        this.addMethodWithKey(metaMethodFromSuper);
                    }
                }
            }

            private void addMethodWithKey(MetaMethod metaMethodFromSuper2) {
                ExpandoMetaClass.this.inheritedMetaMethods.add(metaMethodFromSuper2);
                if (metaMethodFromSuper2 instanceof ClosureMetaMethod) {
                    ClosureMetaMethod closureMethod = (ClosureMetaMethod)metaMethodFromSuper2;
                    String name = metaMethodFromSuper2.getName();
                    Class declaringClass = metaMethodFromSuper2.getDeclaringClass().getTheClass();
                    ClosureMetaMethod localMethod = ClosureMetaMethod.copy(closureMethod);
                    ExpandoMetaClass.this.addMetaMethod(localMethod);
                    DefaultCachedMethodKey key = new DefaultCachedMethodKey(declaringClass, name, localMethod.getParameterTypes(), false);
                    ExpandoMetaClass.this.checkIfGroovyObjectMethod(localMethod);
                    ExpandoMetaClass.this.expandoMethods.put(key, localMethod);
                }
            }
        });
    }

    @Override
    public Object invokeConstructor(Object[] arguments) {
        Class[] argClasses = MetaClassHelper.convertToTypeArray(arguments);
        MetaMethod method = this.pickMethod(GROOVY_CONSTRUCTOR, argClasses);
        if (method != null && method.getParameterTypes().length == arguments.length) {
            return method.invoke(this.theClass, arguments);
        }
        return super.invokeConstructor(arguments);
    }

    @Override
    public MetaClass getMetaClass() {
        return this.myMetaClass;
    }

    @Override
    public Object getProperty(String property) {
        if (ExpandoMetaClass.isValidExpandoProperty(property)) {
            if (property.equals(STATIC_QUALIFIER)) {
                return new ExpandoMetaProperty(property, true);
            }
            if (property.equals(CONSTRUCTOR)) {
                return new ExpandoMetaConstructor();
            }
            if (this.myMetaClass.hasProperty(this, property) == null) {
                return new ExpandoMetaProperty(property);
            }
            return this.myMetaClass.getProperty(this, property);
        }
        return this.myMetaClass.getProperty(this, property);
    }

    public static boolean isValidExpandoProperty(String property) {
        return !property.equals("metaClass") && !property.equals("class") && !property.equals(META_METHODS) && !property.equals(METHODS) && !property.equals(PROPERTIES);
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        Object[] objectArray;
        if (args instanceof Object[]) {
            objectArray = (Object[])args;
        } else {
            Object[] objectArray2 = new Object[1];
            objectArray = objectArray2;
            objectArray2[0] = args;
        }
        Object[] argsArr = objectArray;
        MetaMethod metaMethod = this.myMetaClass.getMetaMethod(name, argsArr);
        if (metaMethod != null) {
            return metaMethod.doMethodInvoke(this, argsArr);
        }
        if (argsArr.length == 2 && argsArr[0] instanceof Class && argsArr[1] instanceof Closure) {
            if (argsArr[0] == this.theClass) {
                this.registerInstanceMethod(name, (Closure)argsArr[1]);
            } else {
                this.registerSubclassInstanceMethod(name, (Class)argsArr[0], (Closure)argsArr[1]);
            }
            return null;
        }
        if (argsArr.length == 1 && argsArr[0] instanceof Closure) {
            this.registerInstanceMethod(name, (Closure)argsArr[0]);
            return null;
        }
        throw new MissingMethodException(name, this.getClass(), argsArr);
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.myMetaClass = metaClass;
    }

    @Override
    public void setProperty(String property, Object newValue) {
        if (newValue instanceof Closure) {
            if (property.equals(CONSTRUCTOR)) {
                property = GROOVY_CONSTRUCTOR;
            }
            Closure callable = (Closure)newValue;
            List<MetaMethod> list = ClosureMetaMethod.createMethodList(property, this.theClass, callable);
            for (MetaMethod method : list) {
                this.registerInstanceMethod(method);
            }
        } else {
            this.registerBeanProperty(property, newValue);
        }
    }

    public ExpandoMetaClass define(Closure closure) {
        DefiningClosure definer = new DefiningClosure();
        Object delegate = closure.getDelegate();
        closure.setDelegate(definer);
        closure.setResolveStrategy(3);
        closure.call((Object)null);
        closure.setDelegate(delegate);
        closure.setResolveStrategy(1);
        definer.definition = false;
        return this;
    }

    protected synchronized void performOperationOnMetaClass(Callable c) {
        try {
            this.writeLock.lock();
            if (this.allowChangesAfterInit) {
                this.setInitialized(false);
            }
            c.call();
        }
        finally {
            if (this.initCalled) {
                this.setInitialized(true);
            }
            this.readLock.lock();
            this.writeLock.unlock();
            this.readLock.unlock();
        }
    }

    @Override
    protected void checkInitalised() {
        try {
            this.readLock.lock();
            super.checkInitalised();
        }
        finally {
            this.readLock.unlock();
        }
    }

    public void registerBeanProperty(final String property, final Object newValue) {
        this.performOperationOnMetaClass(new Callable(){

            @Override
            public void call() {
                Class<Object> type = newValue == null ? Object.class : newValue.getClass();
                MetaBeanProperty mbp = newValue instanceof MetaBeanProperty ? (MetaBeanProperty)newValue : new ThreadManagedMetaBeanProperty(ExpandoMetaClass.this.theClass, property, type, newValue);
                MetaMethod getter = mbp.getGetter();
                DefaultCachedMethodKey getterKey = new DefaultCachedMethodKey(ExpandoMetaClass.this.theClass, getter.getName(), CachedClass.EMPTY_ARRAY, false);
                MetaMethod setter = mbp.getSetter();
                DefaultCachedMethodKey setterKey = new DefaultCachedMethodKey(ExpandoMetaClass.this.theClass, setter.getName(), setter.getParameterTypes(), false);
                ExpandoMetaClass.this.addMetaMethod(getter);
                ExpandoMetaClass.this.addMetaMethod(setter);
                ExpandoMetaClass.this.expandoMethods.put(setterKey, setter);
                ExpandoMetaClass.this.expandoMethods.put(getterKey, getter);
                ExpandoMetaClass.this.expandoProperties.put(mbp.getName(), mbp);
                ExpandoMetaClass.this.addMetaBeanProperty(mbp);
                ExpandoMetaClass.this.performRegistryCallbacks();
            }
        });
    }

    public void registerInstanceMethod(final MetaMethod metaMethod) {
        final boolean inited = this.initCalled;
        this.performOperationOnMetaClass(new Callable(){

            @Override
            public void call() {
                String methodName = metaMethod.getName();
                ExpandoMetaClass.this.checkIfGroovyObjectMethod(metaMethod);
                DefaultCachedMethodKey key = new DefaultCachedMethodKey(ExpandoMetaClass.this.theClass, methodName, metaMethod.getParameterTypes(), false);
                if (ExpandoMetaClass.this.isInitialized()) {
                    throw new RuntimeException("Already initialized, cannot add new method: " + metaMethod);
                }
                ExpandoMetaClass.this.addMetaMethodToIndex(metaMethod, ExpandoMetaClass.this.metaMethodIndex.getHeader(ExpandoMetaClass.this.theClass));
                ExpandoMetaClass.this.dropMethodCache(methodName);
                ExpandoMetaClass.this.expandoMethods.put(key, metaMethod);
                if (inited && ExpandoMetaClass.this.isGetter(methodName, metaMethod.getParameterTypes())) {
                    String propertyName = ExpandoMetaClass.this.getPropertyForGetter(methodName);
                    ExpandoMetaClass.this.registerBeanPropertyForMethod(metaMethod, propertyName, true, false);
                } else if (inited && ExpandoMetaClass.this.isSetter(methodName, metaMethod.getParameterTypes())) {
                    String propertyName = ExpandoMetaClass.this.getPropertyForSetter(methodName);
                    ExpandoMetaClass.this.registerBeanPropertyForMethod(metaMethod, propertyName, false, false);
                }
                ExpandoMetaClass.this.performRegistryCallbacks();
            }
        });
    }

    public void registerInstanceMethod(String name, Closure closure) {
        List<MetaMethod> list = ClosureMetaMethod.createMethodList(name, this.theClass, closure);
        for (MetaMethod method : list) {
            this.registerInstanceMethod(method);
        }
    }

    @Override
    public List<MetaMethod> getMethods() {
        ArrayList<MetaMethod> methodList = new ArrayList<MetaMethod>();
        methodList.addAll(this.expandoMethods.values());
        methodList.addAll(super.getMethods());
        return methodList;
    }

    @Override
    public List<MetaProperty> getProperties() {
        ArrayList<MetaProperty> propertyList = new ArrayList<MetaProperty>();
        propertyList.addAll(super.getProperties());
        return propertyList;
    }

    private void performRegistryCallbacks() {
        MetaClassRegistry registry = GroovySystem.getMetaClassRegistry();
        this.incVersion();
        if (!this.modified) {
            this.modified = true;
            if (this.inRegistry) {
                MetaClass currMetaClass = registry.getMetaClass(this.theClass);
                if (!(currMetaClass instanceof ExpandoMetaClass) && currMetaClass instanceof AdaptingMetaClass) {
                    ((AdaptingMetaClass)currMetaClass).setAdaptee(this);
                } else {
                    registry.setMetaClass(this.theClass, this);
                }
            }
        }
    }

    private void registerBeanPropertyForMethod(MetaMethod metaMethod, String propertyName, boolean getter, boolean isStatic) {
        boolean staticProp;
        MetaProperty metaProperty;
        Map<String, MetaProperty> propertyCache = isStatic ? this.staticBeanPropertyCache : this.beanPropertyCache;
        MetaBeanProperty beanProperty = (MetaBeanProperty)propertyCache.get(propertyName);
        if (beanProperty == null && (metaProperty = super.getMetaProperty(propertyName)) instanceof MetaBeanProperty && isStatic == (staticProp = Modifier.isStatic(metaProperty.getModifiers()))) {
            beanProperty = (MetaBeanProperty)metaProperty;
        }
        if (beanProperty == null) {
            beanProperty = getter ? new MetaBeanProperty(propertyName, Object.class, metaMethod, null) : new MetaBeanProperty(propertyName, Object.class, null, metaMethod);
            propertyCache.put(propertyName, beanProperty);
        } else if (getter) {
            MetaMethod setterMethod = beanProperty.getSetter();
            Class type = setterMethod != null ? setterMethod.getParameterTypes()[0].getTheClass() : Object.class;
            beanProperty = new MetaBeanProperty(propertyName, type, metaMethod, setterMethod);
            propertyCache.put(propertyName, beanProperty);
        } else {
            MetaMethod getterMethod = beanProperty.getGetter();
            beanProperty = new MetaBeanProperty(propertyName, metaMethod.getParameterTypes()[0].getTheClass(), getterMethod, metaMethod);
            propertyCache.put(propertyName, beanProperty);
        }
        this.expandoProperties.put(beanProperty.getName(), beanProperty);
        this.addMetaBeanProperty(beanProperty);
    }

    protected void registerStaticMethod(String name, Closure callable) {
        this.registerStaticMethod(name, callable, null);
    }

    protected void registerStaticMethod(final String name, final Closure callable, final Class[] paramTypes) {
        this.performOperationOnMetaClass(new Callable(){

            @Override
            public void call() {
                String methodName = name.equals("methodMissing") ? "$static_methodMissing" : (name.equals("propertyMissing") ? "$static_propertyMissing" : name);
                ClosureStaticMetaMethod metaMethod = null;
                metaMethod = paramTypes != null ? new ClosureStaticMetaMethod(methodName, ExpandoMetaClass.this.theClass, callable, paramTypes) : new ClosureStaticMetaMethod(methodName, ExpandoMetaClass.this.theClass, callable);
                if (methodName.equals("invokeMethod") && callable.getParameterTypes().length == 2) {
                    ExpandoMetaClass.this.invokeStaticMethodMethod = metaMethod;
                } else {
                    if (methodName.equals("methodMissing")) {
                        methodName = "$static_methodMissing";
                    }
                    DefaultCachedMethodKey key = new DefaultCachedMethodKey(ExpandoMetaClass.this.theClass, methodName, metaMethod.getParameterTypes(), false);
                    ExpandoMetaClass.this.addMetaMethod(metaMethod);
                    ExpandoMetaClass.this.dropStaticMethodCache(methodName);
                    if (ExpandoMetaClass.this.isGetter(methodName, metaMethod.getParameterTypes())) {
                        String propertyName = ExpandoMetaClass.this.getPropertyForGetter(methodName);
                        ExpandoMetaClass.this.registerBeanPropertyForMethod(metaMethod, propertyName, true, true);
                    } else if (ExpandoMetaClass.this.isSetter(methodName, metaMethod.getParameterTypes())) {
                        String propertyName = ExpandoMetaClass.this.getPropertyForSetter(methodName);
                        ExpandoMetaClass.this.registerBeanPropertyForMethod(metaMethod, propertyName, false, true);
                    }
                    ExpandoMetaClass.this.performRegistryCallbacks();
                    ExpandoMetaClass.this.expandoMethods.put(key, metaMethod);
                }
            }
        });
    }

    @Override
    protected Object getSubclassMetaMethods(String methodName) {
        if (!this.isModified()) {
            return null;
        }
        return this.expandoSubclassMethods.get(methodName);
    }

    public Class getJavaClass() {
        return this.theClass;
    }

    public void refreshInheritedMethods(Set modifiedSuperExpandos) {
        for (ExpandoMetaClass superExpando : modifiedSuperExpandos) {
            if (superExpando == this) continue;
            this.refreshInheritedMethods(superExpando);
        }
    }

    private void refreshInheritedMethods(ExpandoMetaClass superExpando) {
        List<MetaMethod> metaMethods = superExpando.getExpandoMethods();
        for (MetaMethod metaMethod : metaMethods) {
            if (metaMethod.isStatic()) {
                if (superExpando.getTheClass() != this.getTheClass()) continue;
                this.registerStaticMethod(metaMethod.getName(), (Closure)((ClosureStaticMetaMethod)metaMethod).getClosure().clone());
                continue;
            }
            this.addSuperMethodIfNotOverridden(metaMethod);
        }
        Collection<MetaProperty> metaProperties = superExpando.getExpandoProperties();
        for (MetaProperty metaProperty : metaProperties) {
            MetaBeanProperty property = (MetaBeanProperty)metaProperty;
            this.expandoProperties.put(property.getName(), property);
            this.addMetaBeanProperty(property);
        }
    }

    public List<MetaMethod> getExpandoMethods() {
        return Collections.unmodifiableList(DefaultGroovyMethods.toList(this.expandoMethods.values()));
    }

    public Collection<MetaProperty> getExpandoProperties() {
        return Collections.unmodifiableCollection(this.expandoProperties.values());
    }

    @Override
    public Object invokeMethod(Class sender, Object object, String methodName, Object[] originalArguments, boolean isCallToSuper, boolean fromInsideClass) {
        if (this.invokeMethodMethod != null) {
            MetaClassHelper.unwrap(originalArguments);
            return this.invokeMethodMethod.invoke(object, new Object[]{methodName, originalArguments});
        }
        return super.invokeMethod(sender, object, methodName, originalArguments, isCallToSuper, fromInsideClass);
    }

    @Override
    public Object invokeStaticMethod(Object object, String methodName, Object[] arguments) {
        if (this.invokeStaticMethodMethod != null) {
            MetaClassHelper.unwrap(arguments);
            return this.invokeStaticMethodMethod.invoke(object, new Object[]{methodName, arguments});
        }
        return super.invokeStaticMethod(object, methodName, arguments);
    }

    @Override
    public Object getProperty(Class sender, Object object, String name, boolean useSuper, boolean fromInsideClass) {
        if (this.hasOverrideGetProperty(name) && this.getJavaClass().isInstance(object)) {
            return this.getPropertyMethod.invoke(object, new Object[]{name});
        }
        if ("mixedIn".equals(name)) {
            return new MixedInAccessor(object, this.mixinClasses);
        }
        return super.getProperty(sender, object, name, useSuper, fromInsideClass);
    }

    @Override
    public Object getProperty(Object object, String name) {
        if (this.hasOverrideGetProperty(name) && this.getJavaClass().isInstance(object)) {
            return this.getPropertyMethod.invoke(object, new Object[]{name});
        }
        return super.getProperty(object, name);
    }

    private boolean hasOverrideGetProperty(String name) {
        return this.getPropertyMethod != null && !name.equals("metaClass") && !name.equals("class");
    }

    @Override
    public void setProperty(Class sender, Object object, String name, Object newValue, boolean useSuper, boolean fromInsideClass) {
        if (this.setPropertyMethod != null && !name.equals("metaClass") && this.getJavaClass().isInstance(object)) {
            this.setPropertyMethod.invoke(object, new Object[]{name, newValue});
            return;
        }
        super.setProperty(sender, object, name, newValue, useSuper, fromInsideClass);
    }

    @Override
    public MetaProperty getMetaProperty(String name) {
        MetaProperty mp = this.expandoProperties.get(name);
        if (mp != null) {
            return mp;
        }
        return super.getMetaProperty(name);
    }

    public boolean hasMetaProperty(String name) {
        return this.getMetaProperty(name) != null;
    }

    public boolean hasMetaMethod(String name, Class[] args) {
        return super.pickMethod(name, args) != null;
    }

    private static boolean isPropertyName(String name) {
        return name.length() > 0 && Character.isUpperCase(name.charAt(0)) || name.length() > 1 && Character.isUpperCase(name.charAt(1));
    }

    private boolean isGetter(String name, CachedClass[] args) {
        if (name == null || name.length() == 0 || args == null) {
            return false;
        }
        if (args.length != 0) {
            return false;
        }
        if (name.startsWith("get")) {
            name = name.substring(3);
            return ExpandoMetaClass.isPropertyName(name);
        }
        if (name.startsWith("is")) {
            name = name.substring(2);
            return ExpandoMetaClass.isPropertyName(name);
        }
        return false;
    }

    private String getPropertyForGetter(String getterName) {
        if (getterName == null || getterName.length() == 0) {
            return null;
        }
        if (getterName.startsWith("get")) {
            String prop = getterName.substring(3);
            return MetaClassHelper.convertPropertyName(prop);
        }
        if (getterName.startsWith("is")) {
            String prop = getterName.substring(2);
            return MetaClassHelper.convertPropertyName(prop);
        }
        return null;
    }

    public String getPropertyForSetter(String setterName) {
        if (setterName == null || setterName.length() == 0) {
            return null;
        }
        if (setterName.startsWith("set")) {
            String prop = setterName.substring(3);
            return MetaClassHelper.convertPropertyName(prop);
        }
        return null;
    }

    public boolean isSetter(String name, CachedClass[] args) {
        if (name == null || name.length() == 0 || args == null) {
            return false;
        }
        if (name.startsWith("set")) {
            if (args.length != 1) {
                return false;
            }
            name = name.substring(3);
            return ExpandoMetaClass.isPropertyName(name);
        }
        return false;
    }

    @Override
    public CallSite createPojoCallSite(CallSite site, Object receiver, Object[] args) {
        if (this.invokeMethodMethod != null) {
            return new PojoMetaClassSite(site, this);
        }
        return super.createPojoCallSite(site, receiver, args);
    }

    @Override
    public CallSite createStaticSite(CallSite site, Object[] args) {
        if (this.invokeStaticMethodMethod != null) {
            return new StaticMetaClassSite(site, this);
        }
        return super.createStaticSite(site, args);
    }

    @Override
    public boolean hasCustomStaticInvokeMethod() {
        return this.invokeStaticMethodMethod != null;
    }

    @Override
    public CallSite createPogoCallSite(CallSite site, Object[] args) {
        if (this.invokeMethodMethod != null) {
            return new PogoMetaClassSite(site, this);
        }
        return super.createPogoCallSite(site, args);
    }

    public CallSite createPogoCallCurrentSite(CallSite site, Class sender, String name, Object[] args) {
        if (this.invokeMethodMethod != null) {
            return new PogoMetaClassSite(site, this);
        }
        return super.createPogoCallCurrentSite(site, sender, args);
    }

    @Override
    public MetaMethod retrieveConstructor(Object[] args) {
        Class[] params = MetaClassHelper.convertToTypeArray(args);
        MetaMethod method = this.pickMethod(GROOVY_CONSTRUCTOR, params);
        if (method != null) {
            return method;
        }
        return super.retrieveConstructor(args);
    }

    @Override
    public CallSite createConstructorSite(CallSite site, Object[] args) {
        Class[] params = MetaClassHelper.convertToTypeArray(args);
        MetaMethod method = this.pickMethod(GROOVY_CONSTRUCTOR, params);
        if (method != null && method.getParameterTypes().length == args.length && method.getDeclaringClass().getTheClass().equals(this.getTheClass())) {
            return new ConstructorMetaMethodSite(site, this, method, params);
        }
        return super.createConstructorSite(site, args);
    }

    private static class MixedInAccessor {
        private final Object object;
        private final Set<MixinInMetaClass> mixinClasses;

        public MixedInAccessor(Object object, Set<MixinInMetaClass> mixinClasses) {
            this.object = object;
            this.mixinClasses = mixinClasses;
        }

        public Object getAt(Class key) {
            if (key.isAssignableFrom(this.object.getClass())) {
                return new GroovyObjectSupport(){
                    {
                        MetaClass ownMetaClass = InvokerHelper.getMetaClass(MixedInAccessor.this.object.getClass());
                        this.setMetaClass(new OwnedMetaClass(ownMetaClass){

                            @Override
                            protected Object getOwner() {
                                return MixedInAccessor.this.object;
                            }

                            @Override
                            protected MetaClass getOwnerMetaClass(Object owner) {
                                return this.getAdaptee();
                            }
                        });
                    }
                };
            }
            for (final MixinInMetaClass mixin : this.mixinClasses) {
                if (!key.isAssignableFrom(mixin.getMixinClass().getTheClass())) continue;
                return new GroovyObjectSupport(){
                    {
                        final Object mixedInInstance = mixin.getMixinInstance(MixedInAccessor.this.object);
                        this.setMetaClass(new OwnedMetaClass(InvokerHelper.getMetaClass(mixedInInstance)){

                            @Override
                            protected Object getOwner() {
                                return mixedInInstance;
                            }

                            @Override
                            protected MetaClass getOwnerMetaClass(Object owner) {
                                return ((MixedInMetaClass)this.getAdaptee()).getAdaptee();
                            }
                        });
                    }
                };
            }
            throw new RuntimeException("Class " + key + " isn't mixed in " + this.object.getClass());
        }

        public void putAt(Class key, Object value) {
            for (MixinInMetaClass mixin : this.mixinClasses) {
                if (mixin.getMixinClass().getTheClass() != key) continue;
                mixin.setMixinInstance(this.object, value);
                return;
            }
            throw new RuntimeException("Class " + key + " isn't mixed in " + this.object.getClass());
        }
    }

    private class StaticDefiningClosure
    extends ExpandoMetaProperty {
        protected StaticDefiningClosure() {
            super(ExpandoMetaClass.STATIC_QUALIFIER, true);
        }

        @Override
        public Object invokeMethod(String name, Object obj) {
            Object[] objectArray;
            Object[] args;
            if (obj instanceof Object[] && (args = (Object[])obj).length == 1 && args[0] instanceof Closure) {
                ExpandoMetaClass.this.registerStaticMethod(name, (Closure)args[0]);
                return null;
            }
            Class<?> clazz = this.getClass();
            if (obj instanceof Object[]) {
                objectArray = (Object[])obj;
            } else {
                Object[] objectArray2 = new Object[1];
                objectArray = objectArray2;
                objectArray2[0] = obj;
            }
            throw new MissingMethodException(name, clazz, objectArray);
        }
    }

    private class DefiningClosure
    extends GroovyObjectSupport {
        boolean definition = true;

        private DefiningClosure() {
        }

        public void mixin(Class category) {
            this.mixin(Collections.singletonList(category));
        }

        public void mixin(List categories) {
            DefaultGroovyMethods.mixin((MetaClass)ExpandoMetaClass.this, (List<Class>)categories);
        }

        public void mixin(Class[] categories) {
            DefaultGroovyMethods.mixin((MetaClass)ExpandoMetaClass.this, categories);
        }

        public void define(Class subClass, Closure closure) {
            SubClassDefiningClosure definer = new SubClassDefiningClosure(subClass);
            closure.setDelegate(definer);
            closure.setResolveStrategy(1);
            closure.call((Object)null);
        }

        @Override
        public Object invokeMethod(String name, Object obj) {
            try {
                return this.getMetaClass().invokeMethod((Object)this, name, obj);
            }
            catch (MissingMethodException mme) {
                if (obj instanceof Object[]) {
                    if (ExpandoMetaClass.STATIC_QUALIFIER.equals(name)) {
                        StaticDefiningClosure staticDef = new StaticDefiningClosure();
                        Closure c = (Closure)((Object[])obj)[0];
                        c.setDelegate(staticDef);
                        c.setResolveStrategy(3);
                        c.call((Object)null);
                        return null;
                    }
                    Object[] args = (Object[])obj;
                    if (args.length == 1 && args[0] instanceof Closure) {
                        ExpandoMetaClass.this.registerInstanceMethod(name, (Closure)args[0]);
                    } else if (args.length == 2 && args[0] instanceof Class && args[1] instanceof Closure) {
                        ExpandoMetaClass.this.registerSubclassInstanceMethod(name, (Class)args[0], (Closure)args[1]);
                    } else {
                        ExpandoMetaClass.this.setProperty(name, ((Object[])obj)[0]);
                    }
                    return null;
                }
                throw mme;
            }
        }

        @Override
        public void setProperty(String property, Object newValue) {
            ExpandoMetaClass.this.setProperty(property, newValue);
        }

        @Override
        public Object getProperty(String property) {
            if (ExpandoMetaClass.STATIC_QUALIFIER.equals(property)) {
                return new StaticDefiningClosure();
            }
            if (this.definition) {
                return new ExpandoMetaProperty(property);
            }
            throw new MissingPropertyException(property, this.getClass());
        }
    }

    private class SubClassDefiningClosure
    extends GroovyObjectSupport {
        private final Class klazz;

        public SubClassDefiningClosure(Class klazz) {
            this.klazz = klazz;
        }

        @Override
        public Object invokeMethod(String name, Object obj) {
            Object[] args;
            if (obj instanceof Object[] && (args = (Object[])obj).length == 1 && args[0] instanceof Closure) {
                ExpandoMetaClass.this.registerSubclassInstanceMethod(name, this.klazz, (Closure)args[0]);
                return null;
            }
            throw new MissingMethodException(name, this.getClass(), new Object[]{obj});
        }
    }

    protected class ExpandoMetaConstructor
    extends GroovyObjectSupport {
        protected ExpandoMetaConstructor() {
        }

        public Object leftShift(Closure c) {
            if (c != null) {
                List<MetaMethod> list = ClosureMetaMethod.createMethodList(ExpandoMetaClass.GROOVY_CONSTRUCTOR, ExpandoMetaClass.this.theClass, c);
                for (MetaMethod method : list) {
                    Class[] paramTypes = method.getNativeParameterTypes();
                    Constructor ctor = ExpandoMetaClass.this.retrieveConstructor(paramTypes);
                    if (ctor != null) {
                        throw new GroovyRuntimeException("Cannot add new constructor for arguments [" + DefaultGroovyMethods.inspect(paramTypes) + "]. It already exists!");
                    }
                    ExpandoMetaClass.this.registerInstanceMethod(method);
                }
            }
            return this;
        }
    }

    protected class ExpandoMetaProperty
    extends GroovyObjectSupport {
        protected String propertyName;
        protected boolean isStatic;

        protected ExpandoMetaProperty(String name) {
            this(name, false);
        }

        protected ExpandoMetaProperty(String name, boolean isStatic) {
            this.propertyName = name;
            this.isStatic = isStatic;
        }

        public String getPropertyName() {
            return this.propertyName;
        }

        public boolean isStatic() {
            return this.isStatic;
        }

        public Object leftShift(Object arg) {
            this.registerIfClosure(arg, false);
            return this;
        }

        private void registerIfClosure(Object arg, boolean replace) {
            if (arg instanceof Closure) {
                Closure callable = (Closure)arg;
                List<MetaMethod> list = ClosureMetaMethod.createMethodList(this.propertyName, ExpandoMetaClass.this.theClass, callable);
                if (list.isEmpty() && this.isStatic) {
                    Class[] paramTypes = callable.getParameterTypes();
                    this.registerStatic(callable, replace, paramTypes);
                    return;
                }
                for (MetaMethod method : list) {
                    Class[] paramTypes = method.getNativeParameterTypes();
                    if (this.isStatic) {
                        this.registerStatic(callable, replace, paramTypes);
                        continue;
                    }
                    this.registerInstance(method, replace, paramTypes);
                }
            }
        }

        private void registerStatic(Closure callable, boolean replace, Class[] paramTypes) {
            Method foundMethod = this.checkIfMethodExists(ExpandoMetaClass.this.theClass, this.propertyName, paramTypes, true);
            if (foundMethod != null && !replace) {
                throw new GroovyRuntimeException("Cannot add new static method [" + this.propertyName + "] for arguments [" + DefaultGroovyMethods.inspect(paramTypes) + "]. It already exists!");
            }
            ExpandoMetaClass.this.registerStaticMethod(this.propertyName, callable, paramTypes);
        }

        private void registerInstance(MetaMethod method, boolean replace, Class[] paramTypes) {
            Method foundMethod = this.checkIfMethodExists(ExpandoMetaClass.this.theClass, this.propertyName, paramTypes, false);
            if (foundMethod != null && !replace) {
                throw new GroovyRuntimeException("Cannot add new method [" + this.propertyName + "] for arguments [" + DefaultGroovyMethods.inspect(paramTypes) + "]. It already exists!");
            }
            ExpandoMetaClass.this.registerInstanceMethod(method);
        }

        private Method checkIfMethodExists(Class methodClass, String methodName, Class[] paramTypes, boolean staticMethod) {
            Method[] methods;
            Method foundMethod = null;
            for (Method method : methods = methodClass.getMethods()) {
                if (!method.getName().equals(methodName) || Modifier.isStatic(method.getModifiers()) != staticMethod || !MetaClassHelper.parametersAreCompatible(paramTypes, method.getParameterTypes())) continue;
                foundMethod = method;
                break;
            }
            return foundMethod;
        }

        @Override
        public Object getProperty(String property) {
            this.propertyName = property;
            return this;
        }

        @Override
        public void setProperty(String property, Object newValue) {
            this.propertyName = property;
            this.registerIfClosure(newValue, true);
        }
    }

    private static interface Callable {
        public void call();
    }
}

