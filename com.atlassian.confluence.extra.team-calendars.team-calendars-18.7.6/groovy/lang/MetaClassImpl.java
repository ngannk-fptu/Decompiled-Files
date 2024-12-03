/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.AdaptingMetaClass;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovySystem;
import groovy.lang.MetaArrayLengthProperty;
import groovy.lang.MetaBeanProperty;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import groovy.lang.MetaMethod;
import groovy.lang.MetaProperty;
import groovy.lang.MissingFieldException;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import groovy.lang.MutableMetaClass;
import groovy.lang.ReadOnlyPropertyException;
import groovy.lang.Script;
import groovy.lang.Tuple;
import groovy.lang.Tuple2;
import groovyjarjarasm.asm.ClassVisitor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.CachedConstructor;
import org.codehaus.groovy.reflection.CachedField;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.reflection.ParameterTypes;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.reflection.android.AndroidSupport;
import org.codehaus.groovy.runtime.ConvertedClosure;
import org.codehaus.groovy.runtime.CurriedClosure;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.ExceptionUtils;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.GroovyCategorySupport;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.InvokerInvocationException;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.runtime.MethodClosure;
import org.codehaus.groovy.runtime.callsite.AbstractCallSite;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.ConstructorSite;
import org.codehaus.groovy.runtime.callsite.MetaClassConstructorSite;
import org.codehaus.groovy.runtime.callsite.PogoMetaClassSite;
import org.codehaus.groovy.runtime.callsite.PogoMetaMethodSite;
import org.codehaus.groovy.runtime.callsite.PojoMetaClassSite;
import org.codehaus.groovy.runtime.callsite.PojoMetaMethodSite;
import org.codehaus.groovy.runtime.callsite.StaticMetaClassSite;
import org.codehaus.groovy.runtime.callsite.StaticMetaMethodSite;
import org.codehaus.groovy.runtime.metaclass.ClosureMetaMethod;
import org.codehaus.groovy.runtime.metaclass.MetaClassRegistryImpl;
import org.codehaus.groovy.runtime.metaclass.MetaMethodIndex;
import org.codehaus.groovy.runtime.metaclass.MethodMetaProperty;
import org.codehaus.groovy.runtime.metaclass.MethodSelectionException;
import org.codehaus.groovy.runtime.metaclass.MissingMethodExceptionNoStack;
import org.codehaus.groovy.runtime.metaclass.MissingMethodExecutionFailed;
import org.codehaus.groovy.runtime.metaclass.MissingPropertyExceptionNoStack;
import org.codehaus.groovy.runtime.metaclass.MixinInstanceMetaMethod;
import org.codehaus.groovy.runtime.metaclass.MultipleSetterProperty;
import org.codehaus.groovy.runtime.metaclass.NewInstanceMetaMethod;
import org.codehaus.groovy.runtime.metaclass.NewMetaMethod;
import org.codehaus.groovy.runtime.metaclass.NewStaticMetaMethod;
import org.codehaus.groovy.runtime.metaclass.TransformMetaMethod;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.NumberMathModificationInfo;
import org.codehaus.groovy.runtime.wrappers.Wrapper;
import org.codehaus.groovy.util.ComplexKeyHashMap;
import org.codehaus.groovy.util.FastArray;
import org.codehaus.groovy.util.SingleKeyHashMap;

public class MetaClassImpl
implements MetaClass,
MutableMetaClass {
    private static final String CLOSURE_CALL_METHOD = "call";
    private static final String CLOSURE_DO_CALL_METHOD = "doCall";
    protected static final String STATIC_METHOD_MISSING = "$static_methodMissing";
    protected static final String STATIC_PROPERTY_MISSING = "$static_propertyMissing";
    protected static final String METHOD_MISSING = "methodMissing";
    protected static final String PROPERTY_MISSING = "propertyMissing";
    private static final String GET_PROPERTY_METHOD = "getProperty";
    private static final String SET_PROPERTY_METHOD = "setProperty";
    protected static final String INVOKE_METHOD_METHOD = "invokeMethod";
    private static final Class[] METHOD_MISSING_ARGS = new Class[]{String.class, Object.class};
    private static final Class[] GETTER_MISSING_ARGS = new Class[]{String.class};
    private static final Class[] SETTER_MISSING_ARGS = METHOD_MISSING_ARGS;
    private static final Comparator<CachedClass> CACHED_CLASS_NAME_COMPARATOR = new Comparator<CachedClass>(){

        @Override
        public int compare(CachedClass o1, CachedClass o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
    protected final Class theClass;
    protected final CachedClass theCachedClass;
    private static final MetaMethod[] EMPTY = new MetaMethod[0];
    protected MetaMethod getPropertyMethod;
    protected MetaMethod invokeMethodMethod;
    protected MetaMethod setPropertyMethod;
    protected MetaClassRegistry registry;
    protected final boolean isGroovyObject;
    protected final boolean isMap;
    private ClassNode classNode;
    private final Index classPropertyIndex = new MethodIndex();
    private Index classPropertyIndexForSuper = new MethodIndex();
    private final SingleKeyHashMap staticPropertyIndex = new SingleKeyHashMap();
    private final Map<String, MetaMethod> listeners = new HashMap<String, MetaMethod>();
    private FastArray constructors;
    private final List<MetaMethod> allMethods = new ArrayList<MetaMethod>();
    private volatile boolean initialized;
    private final MetaProperty arrayLengthProperty = new MetaArrayLengthProperty();
    private static final MetaMethod AMBIGUOUS_LISTENER_METHOD = new DummyMetaMethod();
    public static final Object[] EMPTY_ARGUMENTS = new Object[0];
    private final Set<MetaMethod> newGroovyMethodsSet = new HashSet<MetaMethod>();
    private MetaMethod genericGetMethod;
    private MetaMethod genericSetMethod;
    private MetaMethod propertyMissingGet;
    private MetaMethod propertyMissingSet;
    private MetaMethod methodMissing;
    private MetaMethodIndex.Header mainClassMethodHeader;
    protected final MetaMethodIndex metaMethodIndex;
    private final MetaMethod[] myNewMetaMethods;
    private final MetaMethod[] additionalMetaMethods;
    private static final ConcurrentMap<String, String> PROP_NAMES = new ConcurrentHashMap<String, String>(1024);
    private static final SingleKeyHashMap.Copier NAME_INDEX_COPIER = new SingleKeyHashMap.Copier(){

        @Override
        public Object copy(Object value) {
            if (value instanceof FastArray) {
                return ((FastArray)value).copy();
            }
            return value;
        }
    };
    private static final SingleKeyHashMap.Copier METHOD_INDEX_COPIER = new SingleKeyHashMap.Copier(){

        @Override
        public Object copy(Object value) {
            return SingleKeyHashMap.copy(new SingleKeyHashMap(false), (SingleKeyHashMap)value, NAME_INDEX_COPIER);
        }
    };

    public final CachedClass getTheCachedClass() {
        return this.theCachedClass;
    }

    public MetaClassImpl(Class theClass, MetaMethod[] add) {
        this.theClass = theClass;
        this.theCachedClass = ReflectionCache.getCachedClass(theClass);
        this.isGroovyObject = GroovyObject.class.isAssignableFrom(theClass);
        this.isMap = Map.class.isAssignableFrom(theClass);
        this.registry = GroovySystem.getMetaClassRegistry();
        this.metaMethodIndex = new MetaMethodIndex(this.theCachedClass);
        MetaMethod[] metaMethods = this.theCachedClass.getNewMetaMethods();
        if (add != null && add.length != 0) {
            ArrayList<MetaMethod> arr = new ArrayList<MetaMethod>();
            arr.addAll(Arrays.asList(metaMethods));
            arr.addAll(Arrays.asList(add));
            this.myNewMetaMethods = arr.toArray(new MetaMethod[arr.size()]);
            this.additionalMetaMethods = metaMethods;
        } else {
            this.myNewMetaMethods = metaMethods;
            this.additionalMetaMethods = EMPTY;
        }
    }

    public MetaClassImpl(Class theClass) {
        this(theClass, null);
    }

    public MetaClassImpl(MetaClassRegistry registry, Class theClass, MetaMethod[] add) {
        this(theClass, add);
        this.registry = registry;
        this.constructors = new FastArray(this.theCachedClass.getConstructors());
    }

    public MetaClassImpl(MetaClassRegistry registry, Class theClass) {
        this(registry, theClass, null);
    }

    public MetaClassRegistry getRegistry() {
        return this.registry;
    }

    public List respondsTo(Object obj, String name, Object[] argTypes) {
        Object[] classes = MetaClassHelper.castArgumentsToClassArray(argTypes);
        MetaMethod m = this.getMetaMethod(name, classes);
        if (m != null) {
            return Collections.singletonList(m);
        }
        return Collections.emptyList();
    }

    public List respondsTo(Object obj, String name) {
        Object o = this.getMethods(this.getTheClass(), name, false);
        if (o instanceof FastArray) {
            return ((FastArray)o).toList();
        }
        return Collections.singletonList(o);
    }

    @Override
    public MetaProperty hasProperty(Object obj, String name) {
        return this.getMetaProperty(name);
    }

    @Override
    public MetaProperty getMetaProperty(String name) {
        SingleKeyHashMap propertyMap = this.classPropertyIndex.getNotNull(this.theCachedClass);
        if (propertyMap.containsKey(name)) {
            return (MetaProperty)propertyMap.get(name);
        }
        if (this.staticPropertyIndex.containsKey(name)) {
            return (MetaProperty)this.staticPropertyIndex.get(name);
        }
        propertyMap = this.classPropertyIndexForSuper.getNotNull(this.theCachedClass);
        if (propertyMap.containsKey(name)) {
            return (MetaProperty)propertyMap.get(name);
        }
        for (CachedClass superClass = this.theCachedClass; superClass != null && superClass != ReflectionCache.OBJECT_CLASS; superClass = superClass.getCachedSuperClass()) {
            MetaBeanProperty property = this.findPropertyInClassHierarchy(name, superClass);
            if (property == null) continue;
            this.onSuperPropertyFoundInHierarchy(property);
            return property;
        }
        return null;
    }

    @Override
    public MetaMethod getStaticMetaMethod(String name, Object[] argTypes) {
        Class[] classes = MetaClassHelper.castArgumentsToClassArray(argTypes);
        return this.pickStaticMethod(name, classes);
    }

    @Override
    public MetaMethod getMetaMethod(String name, Object[] argTypes) {
        Class[] classes = MetaClassHelper.castArgumentsToClassArray(argTypes);
        return this.pickMethod(name, classes);
    }

    @Override
    public Class getTheClass() {
        return this.theClass;
    }

    public boolean isGroovyObject() {
        return this.isGroovyObject;
    }

    private void fillMethodIndex() {
        this.mainClassMethodHeader = this.metaMethodIndex.getHeader(this.theClass);
        LinkedList<CachedClass> superClasses = this.getSuperClasses();
        CachedClass firstGroovySuper = this.calcFirstGroovySuperClass(superClasses);
        Set<CachedClass> interfaces = this.theCachedClass.getInterfaces();
        this.addInterfaceMethods(interfaces);
        this.populateMethods(superClasses, firstGroovySuper);
        this.inheritInterfaceNewMetaMethods(interfaces);
        if (this.isGroovyObject) {
            this.metaMethodIndex.copyMethodsToSuper();
            this.connectMultimethods(superClasses, firstGroovySuper);
            this.removeMultimethodsOverloadedWithPrivateMethods();
            this.replaceWithMOPCalls(this.theCachedClass.mopMethods);
        }
    }

    private void populateMethods(LinkedList<CachedClass> superClasses, CachedClass firstGroovySuper) {
        CachedClass c;
        MetaMethodIndex.Header header = this.metaMethodIndex.getHeader(firstGroovySuper.getTheClass());
        Iterator iter = superClasses.iterator();
        while (iter.hasNext()) {
            MetaMethod[] cachedMethods1;
            c = (CachedClass)iter.next();
            CachedMethod[] cachedMethods = c.getMethods();
            for (CachedMethod metaMethod : cachedMethods) {
                this.addToAllMethodsIfPublic(metaMethod);
                if (metaMethod.isPrivate() && c != firstGroovySuper) continue;
                this.addMetaMethodToIndex(metaMethod, header);
            }
            MetaMethod[] metaMethodArray = cachedMethods1 = this.getNewMetaMethods(c);
            int n = metaMethodArray.length;
            for (int metaMethod = 0; metaMethod < n; ++metaMethod) {
                MetaMethod method = metaMethodArray[metaMethod];
                if (this.newGroovyMethodsSet.contains(method)) continue;
                this.newGroovyMethodsSet.add(method);
                this.addMetaMethodToIndex(method, header);
            }
            if (c != firstGroovySuper) continue;
            break;
        }
        MetaMethodIndex.Header last = header;
        while (iter.hasNext()) {
            c = (CachedClass)iter.next();
            header = this.metaMethodIndex.getHeader(c.getTheClass());
            if (last != null) {
                this.metaMethodIndex.copyNonPrivateMethods(last, header);
            }
            last = header;
            for (CachedMethod metaMethod : c.getMethods()) {
                this.addToAllMethodsIfPublic(metaMethod);
                this.addMetaMethodToIndex(metaMethod, header);
            }
            for (MetaMethod method : this.getNewMetaMethods(c)) {
                if (method.getName().equals("<init>") && !method.getDeclaringClass().equals(this.theCachedClass) || this.newGroovyMethodsSet.contains(method)) continue;
                this.newGroovyMethodsSet.add(method);
                this.addMetaMethodToIndex(method, header);
            }
        }
    }

    private MetaMethod[] getNewMetaMethods(CachedClass c) {
        if (this.theCachedClass != c) {
            return c.getNewMetaMethods();
        }
        return this.myNewMetaMethods;
    }

    private void addInterfaceMethods(Set<CachedClass> interfaces) {
        MetaMethodIndex.Header header = this.metaMethodIndex.getHeader(this.theClass);
        for (CachedClass c : interfaces) {
            CachedMethod[] m = c.getMethods();
            for (int i = 0; i != m.length; ++i) {
                CachedMethod method = m[i];
                this.addMetaMethodToIndex(method, header);
            }
        }
    }

    protected LinkedList<CachedClass> getSuperClasses() {
        LinkedList<CachedClass> superClasses = new LinkedList<CachedClass>();
        if (this.theClass.isInterface()) {
            superClasses.addFirst(ReflectionCache.OBJECT_CLASS);
        } else {
            for (CachedClass c = this.theCachedClass; c != null; c = c.getCachedSuperClass()) {
                superClasses.addFirst(c);
            }
            if (this.theCachedClass.isArray && this.theClass != Object[].class && !this.theClass.getComponentType().isPrimitive()) {
                superClasses.addFirst(ReflectionCache.OBJECT_ARRAY_CLASS);
            }
        }
        return superClasses;
    }

    private void removeMultimethodsOverloadedWithPrivateMethods() {
        MethodIndexAction mia = new MethodIndexAction(){

            @Override
            public boolean skipClass(Class clazz) {
                return clazz == MetaClassImpl.this.theClass;
            }

            @Override
            public void methodNameAction(Class clazz, MetaMethodIndex.Entry e) {
                if (e.methods == null) {
                    return;
                }
                boolean hasPrivate = false;
                if (e.methods instanceof FastArray) {
                    FastArray methods = (FastArray)e.methods;
                    int len = methods.size();
                    Object[] data = methods.getArray();
                    for (int i = 0; i != len; ++i) {
                        MetaMethod method = (MetaMethod)data[i];
                        if (!method.isPrivate() || clazz != method.getDeclaringClass().getTheClass()) continue;
                        hasPrivate = true;
                        break;
                    }
                } else {
                    MetaMethod method = (MetaMethod)e.methods;
                    if (method.isPrivate() && clazz == method.getDeclaringClass().getTheClass()) {
                        hasPrivate = true;
                    }
                }
                if (!hasPrivate) {
                    return;
                }
                Object o = e.methodsForSuper;
                e.methods = o instanceof FastArray ? ((FastArray)o).copy() : o;
            }
        };
        mia.iterate();
    }

    private void replaceWithMOPCalls(final CachedMethod[] mopMethods) {
        if (!this.isGroovyObject) {
            return;
        }
        class MOPIter
        extends MethodIndexAction {
            boolean useThis;

            MOPIter() {
            }

            @Override
            public void methodNameAction(Class clazz, MetaMethodIndex.Entry e) {
                block15: {
                    block13: {
                        int to;
                        int from;
                        block14: {
                            if (!this.useThis) break block13;
                            if (e.methods == null) {
                                return;
                            }
                            if (!(e.methods instanceof FastArray)) break block14;
                            FastArray methods = (FastArray)e.methods;
                            this.processFastArray(methods);
                            break block15;
                        }
                        MetaMethod method = (MetaMethod)e.methods;
                        if (method instanceof NewMetaMethod) {
                            return;
                        }
                        if (this.useThis ^ Modifier.isPrivate(method.getModifiers())) {
                            return;
                        }
                        String mopName = method.getMopName();
                        int index = Arrays.binarySearch(mopMethods, mopName, CachedClass.CachedMethodComparatorWithString.INSTANCE);
                        if (index < 0) break block15;
                        for (from = index; from > 0 && mopMethods[from - 1].getName().equals(mopName); --from) {
                        }
                        for (to = index; to < mopMethods.length - 1 && mopMethods[to + 1].getName().equals(mopName); ++to) {
                        }
                        int matchingMethod = MetaClassImpl.this.findMatchingMethod(mopMethods, from, to, method);
                        if (matchingMethod == -1) break block15;
                        e.methods = mopMethods[matchingMethod];
                        break block15;
                    }
                    if (e.methodsForSuper == null) {
                        return;
                    }
                    if (e.methodsForSuper instanceof FastArray) {
                        FastArray methods = (FastArray)e.methodsForSuper;
                        this.processFastArray(methods);
                    } else {
                        MetaMethod method = (MetaMethod)e.methodsForSuper;
                        if (method instanceof NewMetaMethod) {
                            return;
                        }
                        if (this.useThis ^ Modifier.isPrivate(method.getModifiers())) {
                            return;
                        }
                        String mopName = method.getMopName();
                        String[] decomposedMopName = this.decomposeMopName(mopName);
                        for (int distance = Integer.parseInt(decomposedMopName[1]); distance > 0; --distance) {
                            int to;
                            int from;
                            String fixedMopName = decomposedMopName[0] + distance + decomposedMopName[2];
                            int index = Arrays.binarySearch(mopMethods, fixedMopName, CachedClass.CachedMethodComparatorWithString.INSTANCE);
                            if (index < 0) continue;
                            for (from = index; from > 0 && mopMethods[from - 1].getName().equals(fixedMopName); --from) {
                            }
                            for (to = index; to < mopMethods.length - 1 && mopMethods[to + 1].getName().equals(fixedMopName); ++to) {
                            }
                            int matchingMethod = MetaClassImpl.this.findMatchingMethod(mopMethods, from, to, method);
                            if (matchingMethod == -1) continue;
                            e.methodsForSuper = mopMethods[matchingMethod];
                            distance = 0;
                        }
                    }
                }
            }

            private String[] decomposeMopName(String mopName) {
                int eidx;
                int idx = mopName.indexOf("$");
                if (idx > 0 && (eidx = mopName.indexOf("$", idx + 1)) > 0) {
                    return new String[]{mopName.substring(0, idx + 1), mopName.substring(idx + 1, eidx), mopName.substring(eidx)};
                }
                return new String[]{"", "0", mopName};
            }

            private void processFastArray(FastArray methods) {
                int len = methods.size();
                Object[] data = methods.getArray();
                for (int i = 0; i != len; ++i) {
                    int to;
                    int from;
                    String mopName;
                    int index;
                    boolean isPrivate;
                    MetaMethod method = (MetaMethod)data[i];
                    if (method instanceof NewMetaMethod || this.useThis ^ (isPrivate = Modifier.isPrivate(method.getModifiers())) || (index = Arrays.binarySearch(mopMethods, mopName = method.getMopName(), CachedClass.CachedMethodComparatorWithString.INSTANCE)) < 0) continue;
                    for (from = index; from > 0 && mopMethods[from - 1].getName().equals(mopName); --from) {
                    }
                    for (to = index; to < mopMethods.length - 1 && mopMethods[to + 1].getName().equals(mopName); ++to) {
                    }
                    int matchingMethod = MetaClassImpl.this.findMatchingMethod(mopMethods, from, to, method);
                    if (matchingMethod == -1) continue;
                    methods.set(i, mopMethods[matchingMethod]);
                }
            }
        }
        MOPIter iter = new MOPIter();
        iter.useThis = false;
        iter.iterate();
        iter.useThis = true;
        iter.iterate();
    }

    private void inheritInterfaceNewMetaMethods(Set<CachedClass> interfaces) {
        for (CachedClass cls : interfaces) {
            MetaMethod[] methods;
            for (MetaMethod method : methods = this.getNewMetaMethods(cls)) {
                if (!this.newGroovyMethodsSet.contains(method)) {
                    this.newGroovyMethodsSet.add(method);
                }
                this.addMetaMethodToIndex(method, this.mainClassMethodHeader);
            }
        }
    }

    private void connectMultimethods(List<CachedClass> superClasses, CachedClass firstGroovyClass) {
        superClasses = DefaultGroovyMethods.reverse(superClasses);
        MetaMethodIndex.Header last = null;
        for (CachedClass c : superClasses) {
            MetaMethodIndex.Header methodIndex = this.metaMethodIndex.getHeader(c.getTheClass());
            if (last != null) {
                this.metaMethodIndex.copyNonPrivateNonNewMetaMethods(last, methodIndex);
            }
            last = methodIndex;
            if (c != firstGroovyClass) continue;
            break;
        }
    }

    private CachedClass calcFirstGroovySuperClass(Collection superClasses) {
        if (this.theCachedClass.isInterface) {
            return ReflectionCache.OBJECT_CLASS;
        }
        CachedClass firstGroovy = null;
        Iterator iter = superClasses.iterator();
        while (iter.hasNext()) {
            CachedClass c = (CachedClass)iter.next();
            if (!GroovyObject.class.isAssignableFrom(c.getTheClass())) continue;
            firstGroovy = c;
            break;
        }
        if (firstGroovy == null) {
            firstGroovy = this.theCachedClass;
        } else if (firstGroovy.getTheClass() == GroovyObjectSupport.class && iter.hasNext() && (firstGroovy = (CachedClass)iter.next()).getTheClass() == Closure.class && iter.hasNext()) {
            firstGroovy = (CachedClass)iter.next();
        }
        return GroovyObject.class.isAssignableFrom(firstGroovy.getTheClass()) ? firstGroovy.getCachedSuperClass() : firstGroovy;
    }

    private Object getMethods(Class sender, String name, boolean isCallToSuper) {
        GroovyCategorySupport.CategoryMethodList used;
        MetaMethodIndex.Entry entry = this.metaMethodIndex.getMethods(sender, name);
        Object answer = entry == null ? FastArray.EMPTY_LIST : (isCallToSuper ? entry.methodsForSuper : entry.methods);
        if (answer == null) {
            answer = FastArray.EMPTY_LIST;
        }
        if (!isCallToSuper && (used = GroovyCategorySupport.getCategoryMethods(name)) != null) {
            FastArray arr;
            if (answer instanceof MetaMethod) {
                arr = new FastArray();
                arr.add(answer);
            } else {
                arr = ((FastArray)answer).copy();
            }
            for (MetaMethod element : used) {
                if (!element.getDeclaringClass().getTheClass().isAssignableFrom(sender)) continue;
                MetaClassImpl.filterMatchingMethodForCategory(arr, element);
            }
            answer = arr;
        }
        return answer;
    }

    private Object getStaticMethods(Class sender, String name) {
        MetaMethodIndex.Entry entry = this.metaMethodIndex.getMethods(sender, name);
        if (entry == null) {
            return FastArray.EMPTY_LIST;
        }
        Object answer = entry.staticMethods;
        if (answer == null) {
            return FastArray.EMPTY_LIST;
        }
        return answer;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void addNewInstanceMethod(Method method) {
        CachedMethod cachedMethod = CachedMethod.find(method);
        NewInstanceMetaMethod newMethod = new NewInstanceMetaMethod(cachedMethod);
        CachedClass declaringClass = newMethod.getDeclaringClass();
        this.addNewInstanceMethodToIndex(newMethod, this.metaMethodIndex.getHeader(declaringClass.getTheClass()));
    }

    private void addNewInstanceMethodToIndex(MetaMethod newMethod, MetaMethodIndex.Header header) {
        if (!this.newGroovyMethodsSet.contains(newMethod)) {
            this.newGroovyMethodsSet.add(newMethod);
            this.addMetaMethodToIndex(newMethod, header);
        }
    }

    @Override
    public void addNewStaticMethod(Method method) {
        CachedMethod cachedMethod = CachedMethod.find(method);
        NewStaticMetaMethod newMethod = new NewStaticMetaMethod(cachedMethod);
        CachedClass declaringClass = newMethod.getDeclaringClass();
        this.addNewStaticMethodToIndex(newMethod, this.metaMethodIndex.getHeader(declaringClass.getTheClass()));
    }

    private void addNewStaticMethodToIndex(MetaMethod newMethod, MetaMethodIndex.Header header) {
        if (!this.newGroovyMethodsSet.contains(newMethod)) {
            this.newGroovyMethodsSet.add(newMethod);
            this.addMetaMethodToIndex(newMethod, header);
        }
    }

    @Override
    public Object invokeMethod(Object object, String methodName, Object arguments) {
        if (arguments == null) {
            return this.invokeMethod(object, methodName, MetaClassHelper.EMPTY_ARRAY);
        }
        if (arguments instanceof Tuple) {
            Tuple tuple = (Tuple)arguments;
            return this.invokeMethod(object, methodName, tuple.toArray());
        }
        if (arguments instanceof Object[]) {
            return this.invokeMethod(object, methodName, (Object[])arguments);
        }
        return this.invokeMethod(object, methodName, new Object[]{arguments});
    }

    @Override
    public Object invokeMissingMethod(Object instance, String methodName, Object[] arguments) {
        return this.invokeMissingMethod(instance, methodName, arguments, null, false);
    }

    @Override
    public Object invokeMissingProperty(Object instance, String propertyName, Object optionalValue, boolean isGetter) {
        MetaProperty metaProperty;
        MetaMethod method;
        Class<?> theClass = instance instanceof Class ? (Class<?>)instance : instance.getClass();
        for (CachedClass superClass = this.theCachedClass; superClass != null && superClass != ReflectionCache.OBJECT_CLASS; superClass = superClass.getCachedSuperClass()) {
            MetaBeanProperty property = this.findPropertyInClassHierarchy(propertyName, superClass);
            if (property == null) continue;
            this.onSuperPropertyFoundInHierarchy(property);
            if (!isGetter) {
                property.setProperty(instance, optionalValue);
                return null;
            }
            return property.getProperty(instance);
        }
        if (isGetter) {
            Class[] getPropertyArgs = new Class[]{String.class};
            method = MetaClassImpl.findMethodInClassHierarchy(instance.getClass(), GET_PROPERTY_METHOD, getPropertyArgs, this);
            if (method != null && method instanceof ClosureMetaMethod) {
                this.onGetPropertyFoundInHierarchy(method);
                return method.invoke(instance, new Object[]{propertyName});
            }
        } else {
            Class[] setPropertyArgs = new Class[]{String.class, Object.class};
            method = MetaClassImpl.findMethodInClassHierarchy(instance.getClass(), SET_PROPERTY_METHOD, setPropertyArgs, this);
            if (method != null && method instanceof ClosureMetaMethod) {
                this.onSetPropertyFoundInHierarchy(method);
                return method.invoke(instance, new Object[]{propertyName, optionalValue});
            }
        }
        try {
            if (!(instance instanceof Class)) {
                if (isGetter) {
                    if (this.propertyMissingGet != null) {
                        return this.propertyMissingGet.invoke(instance, new Object[]{propertyName});
                    }
                } else if (this.propertyMissingSet != null) {
                    return this.propertyMissingSet.invoke(instance, new Object[]{propertyName, optionalValue});
                }
            }
        }
        catch (InvokerInvocationException iie) {
            boolean shouldHandle;
            boolean bl = shouldHandle = isGetter && this.propertyMissingGet != null;
            if (!shouldHandle) {
                boolean bl2 = shouldHandle = !isGetter && this.propertyMissingSet != null;
            }
            if (shouldHandle && iie.getCause() instanceof MissingPropertyException) {
                throw (MissingPropertyException)iie.getCause();
            }
            throw iie;
        }
        if (instance instanceof Class && theClass != Class.class && (metaProperty = InvokerHelper.getMetaClass(Class.class).hasProperty(instance, propertyName)) != null) {
            if (isGetter) {
                return metaProperty.getProperty(instance);
            }
            metaProperty.setProperty(instance, optionalValue);
            return null;
        }
        throw new MissingPropertyExceptionNoStack(propertyName, theClass);
    }

    private Object invokeMissingMethod(Object instance, String methodName, Object[] arguments, RuntimeException original, boolean isCallToSuper) {
        if (!isCallToSuper) {
            Class[] argClasses;
            MetaMethod method;
            Class instanceKlazz = instance.getClass();
            if (this.theClass != instanceKlazz && this.theClass.isAssignableFrom(instanceKlazz)) {
                instanceKlazz = this.theClass;
            }
            if ((method = this.findMixinMethod(methodName, argClasses = MetaClassHelper.castArgumentsToClassArray(arguments))) != null) {
                this.onMixinMethodFound(method);
                return method.invoke(instance, arguments);
            }
            method = MetaClassImpl.findMethodInClassHierarchy(instanceKlazz, methodName, argClasses, this);
            if (method != null) {
                this.onSuperMethodFoundInHierarchy(method);
                return method.invoke(instance, arguments);
            }
            Object[] invokeMethodArgs = new Class[]{String.class, Object[].class};
            method = MetaClassImpl.findMethodInClassHierarchy(instanceKlazz, INVOKE_METHOD_METHOD, (Class[])invokeMethodArgs, this);
            if (method != null && method instanceof ClosureMetaMethod) {
                this.onInvokeMethodFoundInHierarchy(method);
                return method.invoke(instance, invokeMethodArgs);
            }
        }
        if (this.methodMissing != null) {
            try {
                return this.methodMissing.invoke(instance, new Object[]{methodName, arguments});
            }
            catch (InvokerInvocationException iie) {
                if (this.methodMissing instanceof ClosureMetaMethod && iie.getCause() instanceof MissingMethodException) {
                    MissingMethodException mme = (MissingMethodException)iie.getCause();
                    throw new MissingMethodExecutionFailed(mme.getMethod(), mme.getClass(), mme.getArguments(), mme.isStatic(), mme);
                }
                throw iie;
            }
            catch (MissingMethodException mme) {
                if (this.methodMissing instanceof ClosureMetaMethod) {
                    throw new MissingMethodExecutionFailed(mme.getMethod(), mme.getClass(), mme.getArguments(), mme.isStatic(), mme);
                }
                throw mme;
            }
        }
        if (original != null) {
            throw original;
        }
        throw new MissingMethodExceptionNoStack(methodName, this.theClass, arguments, false);
    }

    protected void onSuperPropertyFoundInHierarchy(MetaBeanProperty property) {
    }

    protected void onMixinMethodFound(MetaMethod method) {
    }

    protected void onSuperMethodFoundInHierarchy(MetaMethod method) {
    }

    protected void onInvokeMethodFoundInHierarchy(MetaMethod method) {
    }

    protected void onSetPropertyFoundInHierarchy(MetaMethod method) {
    }

    protected void onGetPropertyFoundInHierarchy(MetaMethod method) {
    }

    protected Object invokeStaticMissingProperty(Object instance, String propertyName, Object optionalValue, boolean isGetter) {
        MetaClassImpl mc;
        MetaClass metaClass = mc = instance instanceof Class ? this.registry.getMetaClass((Class)instance) : this;
        if (isGetter) {
            MetaMethod propertyMissing = mc.getMetaMethod(STATIC_PROPERTY_MISSING, GETTER_MISSING_ARGS);
            if (propertyMissing != null) {
                return propertyMissing.invoke(instance, new Object[]{propertyName});
            }
        } else {
            MetaMethod propertyMissing = mc.getMetaMethod(STATIC_PROPERTY_MISSING, SETTER_MISSING_ARGS);
            if (propertyMissing != null) {
                return propertyMissing.invoke(instance, new Object[]{propertyName, optionalValue});
            }
        }
        if (instance instanceof Class) {
            throw new MissingPropertyException(propertyName, (Class)instance);
        }
        throw new MissingPropertyException(propertyName, this.theClass);
    }

    @Override
    public Object invokeMethod(Object object, String methodName, Object[] originalArguments) {
        return this.invokeMethod(this.theClass, object, methodName, originalArguments, false, false);
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public Object invokeMethod(Class sender, Object object, String methodName, Object[] originalArguments, boolean isCallToSuper, boolean fromInsideClass) {
        block39: {
            this.checkInitalised();
            if (object == null) {
                throw new NullPointerException("Cannot invoke method: " + methodName + " on null object");
            }
            arguments = originalArguments == null ? MetaClassImpl.EMPTY_ARGUMENTS : originalArguments;
            method = null;
            if ("call".equals(methodName) && object instanceof GeneratedClosure) {
                method = this.getMethodWithCaching(sender, "doCall", arguments, isCallToSuper);
            }
            if (method == null) {
                method = this.getMethodWithCaching(sender, methodName, arguments, isCallToSuper);
            }
            MetaClassHelper.unwrap(arguments);
            if (method == null) {
                method = this.tryListParamMetaMethod(sender, methodName, isCallToSuper, arguments);
            }
            if (!(isClosure = object instanceof Closure)) break block39;
            closure = (Closure)object;
            owner = closure.getOwner();
            if ("call".equals(methodName) || "doCall".equals(methodName)) {
                objectClass = object.getClass();
                if (objectClass == MethodClosure.class) {
                    mc = (MethodClosure)object;
                    methodName = mc.getMethod();
                    ownerClass = owner instanceof Class != false ? (Class<?>)owner : owner.getClass();
                    ownerMetaClass = this.registry.getMetaClass(ownerClass);
                    return ownerMetaClass.invokeMethod(ownerClass, owner, methodName, arguments, false, false);
                }
                if (objectClass == CurriedClosure.class) {
                    cc = (CurriedClosure)object;
                    curriedArguments = cc.getUncurriedArguments(arguments);
                    ownerClass = owner instanceof Class != false ? (Class<?>)owner : owner.getClass();
                    ownerMetaClass = this.registry.getMetaClass(ownerClass);
                    return ownerMetaClass.invokeMethod(owner, methodName, curriedArguments);
                }
                if (method == null) {
                    this.invokeMissingMethod(object, methodName, arguments);
                }
            }
            delegate = closure.getDelegate();
            isClosureNotOwner = owner != closure;
            resolveStrategy = closure.getResolveStrategy();
            argClasses = MetaClassHelper.convertToTypeArray(arguments);
            switch (resolveStrategy) {
                case 4: {
                    method = closure.getMetaClass().pickMethod(methodName, argClasses);
                    if (method == null) break;
                    return method.invoke(closure, arguments);
                }
                case 3: {
                    if (method != null || delegate == closure || delegate == null) break;
                    delegateMetaClass = this.lookupObjectMetaClass(delegate);
                    method = delegateMetaClass.pickMethod(methodName, argClasses);
                    if (method != null) {
                        return delegateMetaClass.invokeMethod(delegate, methodName, originalArguments);
                    }
                    if (delegate == closure || !(delegate instanceof GroovyObject)) break;
                    return MetaClassImpl.invokeMethodOnGroovyObject(methodName, originalArguments, delegate);
                }
                case 2: {
                    if (method != null || owner == closure) break;
                    ownerMetaClass = this.lookupObjectMetaClass(owner);
                    return ownerMetaClass.invokeMethod(owner, methodName, originalArguments);
                }
                case 1: {
                    if (method == null && delegate != closure && delegate != null && (method = (delegateMetaClass = this.lookupObjectMetaClass(delegate)).pickMethod(methodName, argClasses)) != null) {
                        return delegateMetaClass.invokeMethod(delegate, methodName, originalArguments);
                    }
                    if (method == null && owner != closure && (method = (ownerMetaClass = this.lookupObjectMetaClass(owner)).pickMethod(methodName, argClasses)) != null) {
                        return ownerMetaClass.invokeMethod(owner, methodName, originalArguments);
                    }
                    if (method != null || resolveStrategy == 4) break;
                    last = null;
                    if (delegate == closure || !(delegate instanceof GroovyObject)) ** GOTO lbl67
                    try {
                        return MetaClassImpl.invokeMethodOnGroovyObject(methodName, originalArguments, delegate);
                    }
                    catch (MissingMethodException mme) {
                        if (last != null) ** GOTO lbl67
                        last = mme;
                    }
lbl67:
                    // 3 sources

                    if (isClosureNotOwner && owner instanceof GroovyObject) {
                        try {
                            return MetaClassImpl.invokeMethodOnGroovyObject(methodName, originalArguments, owner);
                        }
                        catch (MissingMethodException mme) {
                            last = mme;
                        }
                    }
                    if (last == null) break;
                    return this.invokeMissingMethod(object, methodName, originalArguments, last, isCallToSuper);
                }
                default: {
                    if (method == null && owner != closure && (method = (ownerMetaClass = this.lookupObjectMetaClass(owner)).pickMethod(methodName, argClasses)) != null) {
                        return ownerMetaClass.invokeMethod(owner, methodName, originalArguments);
                    }
                    if (method == null && delegate != closure && delegate != null && (method = (delegateMetaClass = this.lookupObjectMetaClass(delegate)).pickMethod(methodName, argClasses)) != null) {
                        return delegateMetaClass.invokeMethod(delegate, methodName, originalArguments);
                    }
                    if (method != null || resolveStrategy == 4) break;
                    last = null;
                    if (isClosureNotOwner && owner instanceof GroovyObject) {
                        try {
                            return MetaClassImpl.invokeMethodOnGroovyObject(methodName, originalArguments, owner);
                        }
                        catch (MissingMethodException mme) {
                            if (methodName.equals(mme.getMethod())) {
                                if (last == null) {
                                    last = mme;
                                }
                            }
                            throw mme;
                        }
                        catch (InvokerInvocationException iie) {
                            if (iie.getCause() instanceof MissingMethodException) {
                                mme = (MissingMethodException)iie.getCause();
                                if (methodName.equals(mme.getMethod())) {
                                    if (last == null) {
                                        last = mme;
                                    }
                                }
                                throw iie;
                            }
                            throw iie;
                        }
                    }
                    if (delegate != closure && delegate instanceof GroovyObject) {
                        try {
                            return MetaClassImpl.invokeMethodOnGroovyObject(methodName, originalArguments, delegate);
                        }
                        catch (MissingMethodException mme) {
                            last = mme;
                        }
                        catch (InvokerInvocationException iie) {
                            if (iie.getCause() instanceof MissingMethodException) {
                                last = (MissingMethodException)iie.getCause();
                            }
                            throw iie;
                        }
                    }
                    if (last == null) break;
                    return this.invokeMissingMethod(object, methodName, originalArguments, last, isCallToSuper);
                }
            }
        }
        if (method != null) {
            return method.doMethodInvoke(object, arguments);
        }
        return this.invokePropertyOrMissing(object, methodName, originalArguments, fromInsideClass, isCallToSuper);
    }

    private MetaMethod tryListParamMetaMethod(Class sender, String methodName, boolean isCallToSuper, Object[] arguments) {
        MetaMethod method = null;
        if (arguments.length == 1 && arguments[0] instanceof List) {
            Object[] newArguments = ((List)arguments[0]).toArray();
            method = this.createTransformMetaMethod(this.getMethodWithCaching(sender, methodName, newArguments, isCallToSuper));
        }
        return method;
    }

    protected MetaMethod createTransformMetaMethod(MetaMethod method) {
        if (method == null) {
            return null;
        }
        return new TransformMetaMethod(method){

            @Override
            public Object invoke(Object object, Object[] arguments) {
                Object firstArgument = arguments[0];
                List list = (List)firstArgument;
                arguments = list.toArray();
                return super.invoke(object, arguments);
            }
        };
    }

    private Object invokePropertyOrMissing(Object object, String methodName, Object[] originalArguments, boolean fromInsideClass, boolean isCallToSuper) {
        Object bindingVar;
        Object value = null;
        MetaProperty metaProperty = this.getMetaProperty(methodName, false);
        if (metaProperty != null) {
            value = metaProperty.getProperty(object);
        } else if (object instanceof Map) {
            value = ((Map)object).get(methodName);
        }
        if (value instanceof Closure) {
            Closure closure = (Closure)value;
            MetaClass delegateMetaClass = closure.getMetaClass();
            return delegateMetaClass.invokeMethod(closure.getClass(), closure, CLOSURE_DO_CALL_METHOD, originalArguments, false, fromInsideClass);
        }
        if (object instanceof Script && (bindingVar = ((Script)object).getBinding().getVariables().get(methodName)) != null) {
            MetaClass bindingVarMC = ((MetaClassRegistryImpl)this.registry).getMetaClass(bindingVar);
            return bindingVarMC.invokeMethod(bindingVar, CLOSURE_CALL_METHOD, originalArguments);
        }
        return this.invokeMissingMethod(object, methodName, originalArguments, null, isCallToSuper);
    }

    private MetaClass lookupObjectMetaClass(Object object) {
        if (object instanceof GroovyObject) {
            GroovyObject go = (GroovyObject)object;
            return go.getMetaClass();
        }
        Class ownerClass = object.getClass();
        if (ownerClass == Class.class) {
            ownerClass = (Class)object;
        }
        MetaClass metaClass = this.registry.getMetaClass(ownerClass);
        return metaClass;
    }

    private static Object invokeMethodOnGroovyObject(String methodName, Object[] originalArguments, Object owner) {
        GroovyObject go = (GroovyObject)owner;
        return go.invokeMethod(methodName, originalArguments);
    }

    public MetaMethod getMethodWithCaching(Class sender, String methodName, Object[] arguments, boolean isCallToSuper) {
        if (!isCallToSuper && GroovyCategorySupport.hasCategoryInCurrentThread()) {
            return this.getMethodWithoutCaching(sender, methodName, MetaClassHelper.convertToTypeArray(arguments), isCallToSuper);
        }
        MetaMethodIndex.Entry e = this.metaMethodIndex.getMethods(sender, methodName);
        if (e == null) {
            return null;
        }
        return isCallToSuper ? this.getSuperMethodWithCaching(arguments, e) : this.getNormalMethodWithCaching(arguments, e);
    }

    private static boolean sameClasses(Class[] params, Class[] arguments) {
        if (params == null) {
            return false;
        }
        if (params.length != arguments.length) {
            return false;
        }
        for (int i = params.length - 1; i >= 0; --i) {
            Class arg = arguments[i];
            if (arg != null) {
                if (params[i] == arguments[i]) continue;
                return false;
            }
            return false;
        }
        return true;
    }

    private MetaMethod getMethodWithCachingInternal(Class sender, CallSite site, Class[] params) {
        if (GroovyCategorySupport.hasCategoryInCurrentThread()) {
            return this.getMethodWithoutCaching(sender, site.getName(), params, false);
        }
        MetaMethodIndex.Entry e = this.metaMethodIndex.getMethods(sender, site.getName());
        if (e == null) {
            return null;
        }
        Object methods = e.methods;
        if (methods == null) {
            return null;
        }
        MetaMethodIndex.CacheEntry cacheEntry = e.cachedMethod;
        if (cacheEntry != null && MetaClassImpl.sameClasses(cacheEntry.params, params)) {
            return cacheEntry.method;
        }
        e.cachedMethod = cacheEntry = new MetaMethodIndex.CacheEntry(params, (MetaMethod)this.chooseMethod(e.name, methods, params));
        return cacheEntry.method;
    }

    private MetaMethod getSuperMethodWithCaching(Object[] arguments, MetaMethodIndex.Entry e) {
        Class[] classes;
        MetaMethod method;
        if (e.methodsForSuper == null) {
            return null;
        }
        MetaMethodIndex.CacheEntry cacheEntry = e.cachedMethodForSuper;
        if (cacheEntry != null && MetaClassHelper.sameClasses(cacheEntry.params, arguments, e.methodsForSuper instanceof MetaMethod) && (method = cacheEntry.method) != null) {
            return method;
        }
        MetaMethod method2 = (MetaMethod)this.chooseMethod(e.name, e.methodsForSuper, classes = MetaClassHelper.convertToTypeArray(arguments));
        e.cachedMethodForSuper = cacheEntry = new MetaMethodIndex.CacheEntry(classes, method2.isAbstract() ? null : method2);
        return cacheEntry.method;
    }

    private MetaMethod getNormalMethodWithCaching(Object[] arguments, MetaMethodIndex.Entry e) {
        MetaMethod method;
        Object methods = e.methods;
        if (methods == null) {
            return null;
        }
        MetaMethodIndex.CacheEntry cacheEntry = e.cachedMethod;
        if (cacheEntry != null && MetaClassHelper.sameClasses(cacheEntry.params, arguments, methods instanceof MetaMethod) && (method = cacheEntry.method) != null) {
            return method;
        }
        Class[] classes = MetaClassHelper.convertToTypeArray(arguments);
        e.cachedMethod = cacheEntry = new MetaMethodIndex.CacheEntry(classes, (MetaMethod)this.chooseMethod(e.name, methods, classes));
        return cacheEntry.method;
    }

    public Constructor retrieveConstructor(Class[] arguments) {
        CachedConstructor constructor = (CachedConstructor)this.chooseMethod("<init>", this.constructors, arguments);
        if (constructor != null) {
            return constructor.cachedConstructor;
        }
        constructor = (CachedConstructor)this.chooseMethod("<init>", this.constructors, arguments);
        if (constructor != null) {
            return constructor.cachedConstructor;
        }
        return null;
    }

    public MetaMethod retrieveStaticMethod(String methodName, Object[] arguments) {
        MetaMethodIndex.Entry e = this.metaMethodIndex.getMethods(this.theClass, methodName);
        if (e != null) {
            MetaMethodIndex.CacheEntry cacheEntry = e.cachedStaticMethod;
            if (cacheEntry != null && MetaClassHelper.sameClasses(cacheEntry.params, arguments, e.staticMethods instanceof MetaMethod)) {
                return cacheEntry.method;
            }
            Class[] classes = MetaClassHelper.convertToTypeArray(arguments);
            e.cachedStaticMethod = cacheEntry = new MetaMethodIndex.CacheEntry(classes, this.pickStaticMethod(methodName, classes));
            return cacheEntry.method;
        }
        return this.pickStaticMethod(methodName, MetaClassHelper.convertToTypeArray(arguments));
    }

    public MetaMethod getMethodWithoutCaching(Class sender, String methodName, Class[] arguments, boolean isCallToSuper) {
        MetaMethod method = null;
        Object methods = this.getMethods(sender, methodName, isCallToSuper);
        if (methods != null) {
            method = (MetaMethod)this.chooseMethod(methodName, methods, arguments);
        }
        return method;
    }

    @Override
    public Object invokeStaticMethod(Object object, String methodName, Object[] arguments) {
        MetaMethod method;
        Class<?> sender;
        this.checkInitalised();
        Class<?> clazz = sender = object instanceof Class ? (Class<?>)object : object.getClass();
        if (sender != this.theClass) {
            MetaClass mc = this.registry.getMetaClass(sender);
            return mc.invokeStaticMethod(sender, methodName, arguments);
        }
        if (sender == Class.class) {
            return this.invokeMethod(object, methodName, arguments);
        }
        if (arguments == null) {
            arguments = EMPTY_ARGUMENTS;
        }
        if ((method = this.retrieveStaticMethod(methodName, arguments)) != null) {
            MetaClassHelper.unwrap(arguments);
            return method.doMethodInvoke(object, arguments);
        }
        Object prop = null;
        try {
            prop = this.getProperty(this.theClass, this.theClass, methodName, false, false);
        }
        catch (MissingPropertyException missingPropertyException) {
            // empty catch block
        }
        if (prop instanceof Closure) {
            return MetaClassImpl.invokeStaticClosureProperty(arguments, prop);
        }
        Object[] originalArguments = (Object[])arguments.clone();
        MetaClassHelper.unwrap(arguments);
        Object[] argClasses = MetaClassHelper.convertToTypeArray(arguments);
        for (Class<?> superClass = sender.getSuperclass(); superClass != Object.class && superClass != null; superClass = superClass.getSuperclass()) {
            MetaClass mc = this.registry.getMetaClass(superClass);
            method = mc.getStaticMetaMethod(methodName, argClasses);
            if (method != null) {
                return method.doMethodInvoke(object, arguments);
            }
            try {
                prop = mc.getProperty(superClass, superClass, methodName, false, false);
            }
            catch (MissingPropertyException missingPropertyException) {
                // empty catch block
            }
            if (!(prop instanceof Closure)) continue;
            return MetaClassImpl.invokeStaticClosureProperty(originalArguments, prop);
        }
        if (prop != null) {
            MetaClass propMC = this.registry.getMetaClass(prop.getClass());
            return propMC.invokeMethod(prop, CLOSURE_CALL_METHOD, arguments);
        }
        return this.invokeStaticMissingMethod(sender, methodName, arguments);
    }

    private static Object invokeStaticClosureProperty(Object[] originalArguments, Object prop) {
        Closure closure = (Closure)prop;
        MetaClass delegateMetaClass = closure.getMetaClass();
        return delegateMetaClass.invokeMethod(closure.getClass(), closure, CLOSURE_DO_CALL_METHOD, originalArguments, false, false);
    }

    private Object invokeStaticMissingMethod(Class sender, String methodName, Object[] arguments) {
        MetaMethod metaMethod = this.getStaticMetaMethod(STATIC_METHOD_MISSING, METHOD_MISSING_ARGS);
        if (metaMethod != null) {
            return metaMethod.invoke(sender, new Object[]{methodName, arguments});
        }
        throw new MissingMethodException(methodName, sender, arguments, true);
    }

    private MetaMethod pickStaticMethod(String methodName, Class[] arguments) {
        MetaMethod method = null;
        MethodSelectionException mse = null;
        Object methods = this.getStaticMethods(this.theClass, methodName);
        if (!(methods instanceof FastArray) || !((FastArray)methods).isEmpty()) {
            try {
                method = (MetaMethod)this.chooseMethod(methodName, methods, arguments);
            }
            catch (MethodSelectionException msex) {
                mse = msex;
            }
        }
        if (method == null && this.theClass != Class.class) {
            MetaClass classMetaClass = this.registry.getMetaClass(Class.class);
            method = classMetaClass.pickMethod(methodName, arguments);
        }
        if (method == null) {
            method = (MetaMethod)this.chooseMethod(methodName, methods, MetaClassHelper.convertToTypeArray(arguments));
        }
        if (method == null && mse != null) {
            throw mse;
        }
        return method;
    }

    @Override
    public Object invokeConstructor(Object[] arguments) {
        return this.invokeConstructor(this.theClass, arguments);
    }

    @Override
    public int selectConstructorAndTransformArguments(int numberOfConstructors, Object[] arguments) {
        if (numberOfConstructors == -1) {
            return this.selectConstructorAndTransformArguments1(arguments);
        }
        return this.selectConstructorAndTransformArguments0(numberOfConstructors, arguments);
    }

    private int selectConstructorAndTransformArguments0(int numberOfConstructors, Object[] arguments) {
        if (numberOfConstructors != this.constructors.size()) {
            throw new IncompatibleClassChangeError("the number of constructors during runtime and compile time for " + this.theClass.getName() + " do not match. Expected " + numberOfConstructors + " but got " + this.constructors.size());
        }
        CachedConstructor constructor = this.createCachedConstructor(arguments);
        ArrayList l = new ArrayList(this.constructors.toList());
        Comparator comp = new Comparator(){

            public int compare(Object arg0, Object arg1) {
                CachedConstructor c0 = (CachedConstructor)arg0;
                CachedConstructor c1 = (CachedConstructor)arg1;
                String descriptor0 = BytecodeHelper.getMethodDescriptor(Void.TYPE, c0.getNativeParameterTypes());
                String descriptor1 = BytecodeHelper.getMethodDescriptor(Void.TYPE, c1.getNativeParameterTypes());
                return descriptor0.compareTo(descriptor1);
            }
        };
        Collections.sort(l, comp);
        int found = -1;
        for (int i = 0; i < l.size(); ++i) {
            if (l.get(i) != constructor) continue;
            found = i;
            break;
        }
        return 0 | found << 8;
    }

    private CachedConstructor createCachedConstructor(Object[] arguments) {
        if (arguments == null) {
            arguments = EMPTY_ARGUMENTS;
        }
        Class[] argClasses = MetaClassHelper.convertToTypeArray(arguments);
        MetaClassHelper.unwrap(arguments);
        CachedConstructor constructor = (CachedConstructor)this.chooseMethod("<init>", this.constructors, argClasses);
        if (constructor == null) {
            constructor = (CachedConstructor)this.chooseMethod("<init>", this.constructors, argClasses);
        }
        if (constructor == null) {
            throw new GroovyRuntimeException("Could not find matching constructor for: " + this.theClass.getName() + "(" + InvokerHelper.toTypeString(arguments) + ")");
        }
        return constructor;
    }

    private int selectConstructorAndTransformArguments1(Object[] arguments) {
        CachedConstructor constructor = this.createCachedConstructor(arguments);
        String methodDescriptor = BytecodeHelper.getMethodDescriptor(Void.TYPE, constructor.getNativeParameterTypes());
        return BytecodeHelper.hashCode(methodDescriptor);
    }

    protected void checkInitalised() {
        if (!this.isInitialized()) {
            throw new IllegalStateException("initialize must be called for meta class of " + this.theClass + "(" + this.getClass() + ") to complete initialisation process before any invocation or field/property access can be done");
        }
    }

    public MetaMethod retrieveConstructor(Object[] arguments) {
        this.checkInitalised();
        if (arguments == null) {
            arguments = EMPTY_ARGUMENTS;
        }
        Class[] argClasses = MetaClassHelper.convertToTypeArray(arguments);
        MetaClassHelper.unwrap(arguments);
        Object res = this.chooseMethod("<init>", this.constructors, argClasses);
        if (res instanceof MetaMethod) {
            return (MetaMethod)res;
        }
        CachedConstructor constructor = (CachedConstructor)res;
        if (constructor != null) {
            return new MetaConstructor(constructor, false);
        }
        if (arguments.length == 1 && arguments[0] instanceof Map) {
            res = this.chooseMethod("<init>", this.constructors, MetaClassHelper.EMPTY_TYPE_ARRAY);
        } else if (arguments.length == 2 && arguments[1] instanceof Map && this.theClass.getEnclosingClass() != null && this.theClass.getEnclosingClass().isAssignableFrom(argClasses[0])) {
            res = this.chooseMethod("<init>", this.constructors, new Class[]{argClasses[0]});
        }
        if (res instanceof MetaMethod) {
            return (MetaMethod)res;
        }
        constructor = (CachedConstructor)res;
        if (constructor != null) {
            return new MetaConstructor(constructor, true);
        }
        return null;
    }

    private Object invokeConstructor(Class at, Object[] arguments) {
        Object firstArgument;
        this.checkInitalised();
        if (arguments == null) {
            arguments = EMPTY_ARGUMENTS;
        }
        Class[] argClasses = MetaClassHelper.convertToTypeArray(arguments);
        MetaClassHelper.unwrap(arguments);
        CachedConstructor constructor = (CachedConstructor)this.chooseMethod("<init>", this.constructors, argClasses);
        if (constructor != null) {
            return constructor.doConstructorInvoke(arguments);
        }
        if (arguments.length == 1 && (firstArgument = arguments[0]) instanceof Map && (constructor = (CachedConstructor)this.chooseMethod("<init>", this.constructors, MetaClassHelper.EMPTY_TYPE_ARRAY)) != null) {
            Object bean = constructor.doConstructorInvoke(MetaClassHelper.EMPTY_ARRAY);
            this.setProperties(bean, (Map)firstArgument);
            return bean;
        }
        throw new GroovyRuntimeException("Could not find matching constructor for: " + this.theClass.getName() + "(" + InvokerHelper.toTypeString(arguments) + ")");
    }

    public void setProperties(Object bean, Map map) {
        this.checkInitalised();
        for (Map.Entry entry : map.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            this.setProperty(bean, key, value);
        }
    }

    @Override
    public Object getProperty(Class sender, Object object, String name, boolean useSuper, boolean fromInsideClass) {
        boolean isStatic;
        boolean bl = isStatic = this.theClass != Class.class && object instanceof Class;
        if (isStatic && object != this.theClass) {
            MetaClass mc = this.registry.getMetaClass((Class)object);
            return mc.getProperty(sender, object, name, useSuper, false);
        }
        this.checkInitalised();
        if (!isStatic && this.isMap) {
            return ((Map)object).get(name);
        }
        Tuple2<MetaMethod, MetaProperty> methodAndProperty = this.createMetaMethodAndMetaProperty(sender, sender, name, useSuper, isStatic);
        MetaMethod method = methodAndProperty.getFirst();
        MetaProperty mp = methodAndProperty.getSecond();
        if (method == null && mp != null) {
            try {
                return mp.getProperty(object);
            }
            catch (IllegalArgumentException e) {
                mp = null;
            }
        }
        Object[] arguments = EMPTY_ARGUMENTS;
        if (method == null && !useSuper && !isStatic && GroovyCategorySupport.hasCategoryInCurrentThread() && (method = MetaClassImpl.getCategoryMethodGetter(sender, "get", true)) != null) {
            arguments = new Object[]{name};
        }
        if (method == null && this.genericGetMethod != null && (this.genericGetMethod.isStatic() || !isStatic)) {
            arguments = new Object[]{name};
            method = this.genericGetMethod;
        }
        if (method == null) {
            if (this.theClass != Class.class && object instanceof Class) {
                MetaClass mc = this.registry.getMetaClass(Class.class);
                return mc.getProperty(Class.class, object, name, useSuper, false);
            }
            if (object instanceof Collection) {
                return DefaultGroovyMethods.getAt((Collection)object, name);
            }
            if (object instanceof Object[]) {
                return DefaultGroovyMethods.getAt(Arrays.asList((Object[])object), name);
            }
            MetaMethod addListenerMethod = this.listeners.get(name);
            if (addListenerMethod != null) {
                return null;
            }
        } else {
            return method.doMethodInvoke(object, arguments);
        }
        if (isStatic || object instanceof Class) {
            return this.invokeStaticMissingProperty(object, name, null, true);
        }
        return this.invokeMissingProperty(object, name, null, true);
    }

    public MetaProperty getEffectiveGetMetaProperty(final Class sender, final Object object, String name, final boolean useSuper) {
        boolean isStatic;
        boolean bl = isStatic = this.theClass != Class.class && object instanceof Class;
        if (isStatic && object != this.theClass) {
            return new MetaProperty(name, Object.class){
                final MetaClass mc;
                {
                    super(name, type);
                    this.mc = MetaClassImpl.this.registry.getMetaClass((Class)object);
                }

                @Override
                public Object getProperty(Object object2) {
                    return this.mc.getProperty(sender, object2, this.name, useSuper, false);
                }

                @Override
                public void setProperty(Object object2, Object newValue) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        this.checkInitalised();
        if (!isStatic && this.isMap) {
            return new MetaProperty(name, Object.class){

                @Override
                public Object getProperty(Object object) {
                    return ((Map)object).get(this.name);
                }

                @Override
                public void setProperty(Object object, Object newValue) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        Tuple2<MetaMethod, MetaProperty> methodAndProperty = this.createMetaMethodAndMetaProperty(sender, this.theClass, name, useSuper, isStatic);
        MetaMethod method = methodAndProperty.getFirst();
        MetaProperty mp = methodAndProperty.getSecond();
        if (method != null) {
            return new MethodMetaProperty.GetBeanMethodMetaProperty(name, method);
        }
        if (mp != null) {
            return mp;
        }
        if (!useSuper && !isStatic && GroovyCategorySupport.hasCategoryInCurrentThread() && (method = MetaClassImpl.getCategoryMethodGetter(sender, "get", true)) != null) {
            return new MethodMetaProperty.GetMethodMetaProperty(name, method);
        }
        if (this.genericGetMethod != null && (this.genericGetMethod.isStatic() || !isStatic) && (method = this.genericGetMethod) != null) {
            return new MethodMetaProperty.GetMethodMetaProperty(name, method);
        }
        if (this.theClass != Class.class && object instanceof Class) {
            return new MetaProperty(name, Object.class){

                @Override
                public Object getProperty(Object object) {
                    MetaClass mc = MetaClassImpl.this.registry.getMetaClass(Class.class);
                    return mc.getProperty(Class.class, object, this.name, useSuper, false);
                }

                @Override
                public void setProperty(Object object, Object newValue) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        if (object instanceof Collection) {
            return new MetaProperty(name, Object.class){

                @Override
                public Object getProperty(Object object) {
                    return DefaultGroovyMethods.getAt((Collection)object, this.name);
                }

                @Override
                public void setProperty(Object object, Object newValue) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        if (object instanceof Object[]) {
            return new MetaProperty(name, Object.class){

                @Override
                public Object getProperty(Object object) {
                    return DefaultGroovyMethods.getAt(Arrays.asList((Object[])object), this.name);
                }

                @Override
                public void setProperty(Object object, Object newValue) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        MetaMethod addListenerMethod = this.listeners.get(name);
        if (addListenerMethod != null) {
            return new MetaProperty(name, Object.class){

                @Override
                public Object getProperty(Object object) {
                    return null;
                }

                @Override
                public void setProperty(Object object, Object newValue) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        if (isStatic || object instanceof Class) {
            return new MetaProperty(name, Object.class){

                @Override
                public Object getProperty(Object object) {
                    return MetaClassImpl.this.invokeStaticMissingProperty(object, this.name, null, true);
                }

                @Override
                public void setProperty(Object object, Object newValue) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return new MetaProperty(name, Object.class){

            @Override
            public Object getProperty(Object object) {
                return MetaClassImpl.this.invokeMissingProperty(object, this.name, null, true);
            }

            @Override
            public void setProperty(Object object, Object newValue) {
                throw new UnsupportedOperationException();
            }
        };
    }

    private Tuple2<MetaMethod, MetaProperty> createMetaMethodAndMetaProperty(Class senderForMP, Class senderForCMG, String name, boolean useSuper, boolean isStatic) {
        MetaMethod categoryMethod;
        String getterName;
        MetaMethod method = null;
        MetaProperty mp = this.getMetaProperty(senderForMP, name, useSuper, isStatic);
        if (mp != null && mp instanceof MetaBeanProperty) {
            MetaBeanProperty mbp = (MetaBeanProperty)mp;
            method = mbp.getGetter();
            mp = mbp.getField();
        }
        if (!useSuper && !isStatic && GroovyCategorySupport.hasCategoryInCurrentThread() && (getterName = GroovyCategorySupport.getPropertyCategoryGetterName(name)) != null && (categoryMethod = MetaClassImpl.getCategoryMethodGetter(senderForCMG, getterName, false)) != null) {
            method = categoryMethod;
        }
        return new Tuple2<Object, MetaProperty>(method, mp);
    }

    private static MetaMethod getCategoryMethodGetter(Class sender, String name, boolean useLongVersion) {
        GroovyCategorySupport.CategoryMethodList possibleGenericMethods = GroovyCategorySupport.getCategoryMethods(name);
        if (possibleGenericMethods != null) {
            for (MetaMethod mmethod : possibleGenericMethods) {
                if (!mmethod.getDeclaringClass().getTheClass().isAssignableFrom(sender)) continue;
                CachedClass[] paramTypes = mmethod.getParameterTypes();
                if (!(useLongVersion ? paramTypes.length == 1 && paramTypes[0].getTheClass() == String.class : paramTypes.length == 0)) continue;
                return mmethod;
            }
        }
        return null;
    }

    private static MetaMethod getCategoryMethodSetter(Class sender, String name, boolean useLongVersion) {
        GroovyCategorySupport.CategoryMethodList possibleGenericMethods = GroovyCategorySupport.getCategoryMethods(name);
        if (possibleGenericMethods != null) {
            for (MetaMethod mmethod : possibleGenericMethods) {
                if (!mmethod.getDeclaringClass().getTheClass().isAssignableFrom(sender)) continue;
                CachedClass[] paramTypes = mmethod.getParameterTypes();
                if (!(useLongVersion ? paramTypes.length == 2 && paramTypes[0].getTheClass() == String.class : paramTypes.length == 1)) continue;
                return mmethod;
            }
        }
        return null;
    }

    @Override
    public List<MetaProperty> getProperties() {
        this.checkInitalised();
        SingleKeyHashMap propertyMap = this.classPropertyIndex.getNullable(this.theCachedClass);
        if (propertyMap == null) {
            propertyMap = new SingleKeyHashMap();
        }
        ArrayList<MetaProperty> ret = new ArrayList<MetaProperty>(propertyMap.size());
        ComplexKeyHashMap.EntryIterator iter = propertyMap.getEntrySetIterator();
        while (iter.hasNext()) {
            MetaProperty element = (MetaProperty)((SingleKeyHashMap.Entry)iter.next()).value;
            if (element instanceof CachedField) continue;
            if (element instanceof MetaBeanProperty) {
                MetaBeanProperty mp = (MetaBeanProperty)element;
                boolean setter = true;
                boolean getter = true;
                if (mp.getGetter() == null || mp.getGetter() instanceof GeneratedMetaMethod || mp.getGetter() instanceof NewInstanceMetaMethod) {
                    getter = false;
                }
                if (mp.getSetter() == null || mp.getSetter() instanceof GeneratedMetaMethod || mp.getSetter() instanceof NewInstanceMetaMethod) {
                    setter = false;
                }
                if (!setter && !getter) continue;
            }
            ret.add(element);
        }
        return ret;
    }

    private static Object filterPropertyMethod(Object methodOrList, boolean isGetter, boolean booleanGetter) {
        MetaMethod element;
        Object ret = null;
        if (methodOrList instanceof MetaMethod) {
            MetaMethod element2 = (MetaMethod)methodOrList;
            int parameterCount = element2.getParameterTypes().length;
            if (!isGetter && parameterCount == 1) {
                ret = element2;
            }
            Class returnType = element2.getReturnType();
            if (isGetter && returnType != Void.class && returnType != Void.TYPE && (!booleanGetter || returnType == Boolean.class || returnType == Boolean.TYPE) && parameterCount == 0) {
                ret = element2;
            }
        }
        if (methodOrList instanceof FastArray) {
            FastArray methods = (FastArray)methodOrList;
            int len = methods.size();
            Object[] data = methods.getArray();
            for (int i = 0; i != len; ++i) {
                element = (MetaMethod)data[i];
                int parameterCount = element.getParameterTypes().length;
                if (!isGetter && parameterCount == 1) {
                    ret = MetaClassImpl.addElementToList(ret, element);
                }
                Class returnType = element.getReturnType();
                if (!isGetter || returnType == Void.class || returnType == Void.TYPE || parameterCount != 0) continue;
                ret = MetaClassImpl.addElementToList(ret, element);
            }
        }
        if (ret == null || ret instanceof MetaMethod || !isGetter) {
            return ret;
        }
        MetaMethod method = null;
        int distance = -1;
        for (Object o : (List)ret) {
            element = (MetaMethod)o;
            int localDistance = MetaClassImpl.distanceToObject(element.getReturnType());
            if (distance != -1 && distance <= localDistance) continue;
            distance = localDistance;
            method = element;
        }
        return method;
    }

    private static Object addElementToList(Object ret, MetaMethod element) {
        if (ret == null) {
            ret = element;
        } else if (ret instanceof List) {
            ((List)ret).add(element);
        } else {
            LinkedList<Cloneable> list = new LinkedList<Cloneable>();
            list.add(ret);
            list.add(element);
            ret = list;
        }
        return ret;
    }

    private static int distanceToObject(Class c) {
        int count = 0;
        while (c != null) {
            c = c.getSuperclass();
            ++count;
        }
        return count;
    }

    private void setupProperties(PropertyDescriptor[] propertyDescriptors) {
        if (this.theCachedClass.isInterface) {
            LinkedList<CachedClass> superClasses = new LinkedList<CachedClass>();
            superClasses.add(ReflectionCache.OBJECT_CLASS);
            Set<CachedClass> interfaces = this.theCachedClass.getInterfaces();
            LinkedList<CachedClass> superInterfaces = new LinkedList<CachedClass>(interfaces);
            if (superInterfaces.size() > 1) {
                Collections.sort(superInterfaces, CACHED_CLASS_NAME_COMPARATOR);
            }
            SingleKeyHashMap iPropertyIndex = this.classPropertyIndex.getNotNull(this.theCachedClass);
            for (CachedClass iclass : superInterfaces) {
                SingleKeyHashMap sPropertyIndex = this.classPropertyIndex.getNotNull(iclass);
                MetaClassImpl.copyNonPrivateFields(sPropertyIndex, iPropertyIndex);
                MetaClassImpl.addFields(iclass, iPropertyIndex);
            }
            MetaClassImpl.addFields(this.theCachedClass, iPropertyIndex);
            this.applyPropertyDescriptors(propertyDescriptors);
            this.applyStrayPropertyMethods(superClasses, this.classPropertyIndex, true);
            this.makeStaticPropertyIndex();
        } else {
            LinkedList<CachedClass> superClasses = this.getSuperClasses();
            LinkedList<CachedClass> interfaces = new LinkedList<CachedClass>(this.theCachedClass.getInterfaces());
            if (interfaces.size() > 1) {
                Collections.sort(interfaces, CACHED_CLASS_NAME_COMPARATOR);
            }
            if (this.theCachedClass.isArray) {
                SingleKeyHashMap map = new SingleKeyHashMap();
                map.put("length", this.arrayLengthProperty);
                this.classPropertyIndex.put(this.theCachedClass, map);
            }
            this.inheritStaticInterfaceFields(superClasses, new LinkedHashSet<CachedClass>(interfaces));
            this.inheritFields(superClasses);
            this.applyPropertyDescriptors(propertyDescriptors);
            this.applyStrayPropertyMethods(superClasses, this.classPropertyIndex, true);
            this.applyStrayPropertyMethods(superClasses, this.classPropertyIndexForSuper, false);
            this.copyClassPropertyIndexForSuper(this.classPropertyIndexForSuper);
            this.makeStaticPropertyIndex();
        }
    }

    private void makeStaticPropertyIndex() {
        SingleKeyHashMap propertyMap = this.classPropertyIndex.getNotNull(this.theCachedClass);
        ComplexKeyHashMap.EntryIterator iter = propertyMap.getEntrySetIterator();
        while (iter.hasNext()) {
            SingleKeyHashMap.Entry entry = (SingleKeyHashMap.Entry)iter.next();
            MetaProperty mp = (MetaProperty)entry.getValue();
            if (mp instanceof CachedField) {
                CachedField mfp = (CachedField)mp;
                if (!mfp.isStatic()) {
                    continue;
                }
            } else if (mp instanceof MetaBeanProperty) {
                MetaProperty result = MetaClassImpl.establishStaticMetaProperty(mp);
                if (result == null) continue;
                mp = result;
            } else {
                if (!(mp instanceof MultipleSetterProperty)) continue;
                MultipleSetterProperty msp = (MultipleSetterProperty)mp;
                mp = msp.createStaticVersion();
            }
            this.staticPropertyIndex.put(entry.getKey(), mp);
        }
    }

    private static MetaProperty establishStaticMetaProperty(MetaProperty mp) {
        boolean field;
        MetaBeanProperty mbp = (MetaBeanProperty)mp;
        MetaProperty result = null;
        MetaMethod getterMethod = mbp.getGetter();
        MetaMethod setterMethod = mbp.getSetter();
        CachedField metaField = mbp.getField();
        boolean getter = getterMethod == null || getterMethod.isStatic();
        boolean setter = setterMethod == null || setterMethod.isStatic();
        boolean bl = field = metaField == null || metaField.isStatic();
        if (!(getter || setter || field)) {
            return result;
        }
        String propertyName = mbp.getName();
        Class propertyType = mbp.getType();
        if (setter && getter) {
            result = field ? mbp : new MetaBeanProperty(propertyName, propertyType, getterMethod, setterMethod);
        } else if (getter && !setter) {
            if (getterMethod == null) {
                result = metaField;
            } else {
                MetaBeanProperty newmp = new MetaBeanProperty(propertyName, propertyType, getterMethod, null);
                if (field) {
                    newmp.setField(metaField);
                }
                result = newmp;
            }
        } else if (setter && !getter) {
            if (setterMethod == null) {
                result = metaField;
            } else {
                MetaBeanProperty newmp = new MetaBeanProperty(propertyName, propertyType, null, setterMethod);
                if (field) {
                    newmp.setField(metaField);
                }
                result = newmp;
            }
        } else {
            result = metaField;
        }
        return result;
    }

    private void copyClassPropertyIndexForSuper(Index dest) {
        ComplexKeyHashMap.EntryIterator iter = this.classPropertyIndex.getEntrySetIterator();
        while (iter.hasNext()) {
            SingleKeyHashMap.Entry entry = (SingleKeyHashMap.Entry)iter.next();
            SingleKeyHashMap newVal = new SingleKeyHashMap();
            dest.put((CachedClass)entry.getKey(), newVal);
        }
    }

    private void inheritStaticInterfaceFields(LinkedList superClasses, Set interfaces) {
        for (CachedClass iclass : interfaces) {
            SingleKeyHashMap iPropertyIndex = this.classPropertyIndex.getNotNull(iclass);
            MetaClassImpl.addFields(iclass, iPropertyIndex);
            for (CachedClass sclass : superClasses) {
                if (!iclass.getTheClass().isAssignableFrom(sclass.getTheClass())) continue;
                SingleKeyHashMap sPropertyIndex = this.classPropertyIndex.getNotNull(sclass);
                MetaClassImpl.copyNonPrivateFields(iPropertyIndex, sPropertyIndex);
            }
        }
    }

    private void inheritFields(LinkedList<CachedClass> superClasses) {
        SingleKeyHashMap last = null;
        for (CachedClass klass : superClasses) {
            SingleKeyHashMap propertyIndex = this.classPropertyIndex.getNotNull(klass);
            if (last != null) {
                MetaClassImpl.copyNonPrivateFields(last, propertyIndex, klass);
            }
            last = propertyIndex;
            MetaClassImpl.addFields(klass, propertyIndex);
        }
    }

    private static void addFields(CachedClass klass, SingleKeyHashMap propertyIndex) {
        CachedField[] fields;
        for (CachedField field : fields = klass.getFields()) {
            propertyIndex.put(field.getName(), field);
        }
    }

    private static void copyNonPrivateFields(SingleKeyHashMap from, SingleKeyHashMap to) {
        MetaClassImpl.copyNonPrivateFields(from, to, null);
    }

    private static void copyNonPrivateFields(SingleKeyHashMap from, SingleKeyHashMap to, CachedClass klass) {
        ComplexKeyHashMap.EntryIterator iter = from.getEntrySetIterator();
        while (iter.hasNext()) {
            SingleKeyHashMap.Entry entry = (SingleKeyHashMap.Entry)iter.next();
            CachedField mfp = (CachedField)entry.getValue();
            if (!MetaClassImpl.inheritedOrPublic(mfp) && !MetaClassImpl.packageLocal(mfp, klass)) continue;
            to.put(entry.getKey(), mfp);
        }
    }

    private static boolean inheritedOrPublic(CachedField mfp) {
        return Modifier.isPublic(mfp.getModifiers()) || Modifier.isProtected(mfp.getModifiers());
    }

    private static boolean packageLocal(CachedField mfp, CachedClass klass) {
        if ((mfp.getModifiers() & 7) != 0 || klass == null) {
            return false;
        }
        Package fieldPackage = mfp.field.getDeclaringClass().getPackage();
        Package classPackage = klass.getTheClass().getPackage();
        return fieldPackage == null && classPackage == null || fieldPackage != null && classPackage != null && fieldPackage.getName().equals(classPackage.getName());
    }

    private void applyStrayPropertyMethods(LinkedList<CachedClass> superClasses, Index classPropertyIndex, boolean isThis) {
        for (CachedClass klass : superClasses) {
            MetaMethodIndex.Header header = this.metaMethodIndex.getHeader(klass.getTheClass());
            SingleKeyHashMap propertyIndex = classPropertyIndex.getNotNull(klass);
            MetaMethodIndex.Entry e = header.head;
            while (e != null) {
                String methodName = e.name;
                if (methodName.length() >= 3 && (methodName.startsWith("is") || methodName.length() >= 4)) {
                    Object propertyMethods;
                    boolean isGetter = methodName.startsWith("get") || methodName.startsWith("is");
                    boolean isBooleanGetter = methodName.startsWith("is");
                    boolean isSetter = methodName.startsWith("set");
                    if ((isGetter || isSetter) && (propertyMethods = MetaClassImpl.filterPropertyMethod(isThis ? e.methods : e.methodsForSuper, isGetter, isBooleanGetter)) != null) {
                        String propName = MetaClassImpl.getPropName(methodName);
                        if (propertyMethods instanceof MetaMethod) {
                            MetaClassImpl.createMetaBeanProperty(propertyIndex, propName, isGetter, (MetaMethod)propertyMethods);
                        } else {
                            LinkedList methods = (LinkedList)propertyMethods;
                            for (MetaMethod m : methods) {
                                MetaClassImpl.createMetaBeanProperty(propertyIndex, propName, isGetter, m);
                            }
                        }
                    }
                }
                e = e.nextClassEntry;
            }
        }
    }

    private static String getPropName(String methodName) {
        String name = (String)PROP_NAMES.get(methodName);
        if (name == null) {
            String stripped = methodName.startsWith("is") ? methodName.substring(2) : methodName.substring(3);
            String propName = Introspector.decapitalize(stripped);
            PROP_NAMES.putIfAbsent(methodName, propName);
            name = (String)PROP_NAMES.get(methodName);
        }
        return name;
    }

    private static MetaProperty makeReplacementMetaProperty(MetaProperty mp, String propName, boolean isGetter, MetaMethod propertyMethod) {
        if (mp == null) {
            if (isGetter) {
                return new MetaBeanProperty(propName, propertyMethod.getReturnType(), propertyMethod, null);
            }
            return new MetaBeanProperty(propName, propertyMethod.getParameterTypes()[0].getTheClass(), null, propertyMethod);
        }
        if (mp instanceof CachedField) {
            CachedField mfp = (CachedField)mp;
            MetaBeanProperty mbp = new MetaBeanProperty(propName, mfp.getType(), isGetter ? propertyMethod : null, isGetter ? null : propertyMethod);
            mbp.setField(mfp);
            return mbp;
        }
        if (mp instanceof MultipleSetterProperty) {
            MultipleSetterProperty msp = (MultipleSetterProperty)mp;
            if (isGetter) {
                msp.setGetter(propertyMethod);
            }
            return msp;
        }
        if (mp instanceof MetaBeanProperty) {
            MetaBeanProperty mbp = (MetaBeanProperty)mp;
            if (isGetter) {
                mbp.setGetter(propertyMethod);
                return mbp;
            }
            if (mbp.getSetter() == null || mbp.getSetter() == propertyMethod) {
                mbp.setSetter(propertyMethod);
                return mbp;
            }
            MultipleSetterProperty msp = new MultipleSetterProperty(propName);
            msp.setField(mbp.getField());
            msp.setGetter(mbp.getGetter());
            return msp;
        }
        throw new GroovyBugError("unknown MetaProperty class used. Class is " + mp.getClass());
    }

    private static void createMetaBeanProperty(SingleKeyHashMap propertyIndex, String propName, boolean isGetter, MetaMethod propertyMethod) {
        MetaProperty mp = (MetaProperty)propertyIndex.get(propName);
        MetaProperty newMp = MetaClassImpl.makeReplacementMetaProperty(mp, propName, isGetter, propertyMethod);
        if (newMp != mp) {
            propertyIndex.put(propName, newMp);
        }
    }

    protected void applyPropertyDescriptors(PropertyDescriptor[] propertyDescriptors) {
        for (PropertyDescriptor pd : propertyDescriptors) {
            CachedMethod cachedSetter;
            CachedMethod cachedGetter;
            if (pd.getPropertyType() == null) continue;
            Method method = pd.getReadMethod();
            MetaMethod getter = method != null ? ((cachedGetter = CachedMethod.find(method)) == null ? null : this.findMethod(cachedGetter)) : null;
            method = pd.getWriteMethod();
            MetaMethod setter = method != null ? ((cachedSetter = CachedMethod.find(method)) == null ? null : this.findMethod(cachedSetter)) : null;
            MetaBeanProperty mp = new MetaBeanProperty(pd.getName(), pd.getPropertyType(), getter, setter);
            this.addMetaBeanProperty(mp);
        }
    }

    @Override
    public void addMetaBeanProperty(MetaBeanProperty mp) {
        MetaProperty staticProperty = MetaClassImpl.establishStaticMetaProperty(mp);
        if (staticProperty != null) {
            this.staticPropertyIndex.put(mp.getName(), mp);
        } else {
            SingleKeyHashMap propertyMap = this.classPropertyIndex.getNotNull(this.theCachedClass);
            MetaProperty old = (MetaProperty)propertyMap.get(mp.getName());
            if (old != null) {
                CachedField field = old instanceof MetaBeanProperty ? ((MetaBeanProperty)old).getField() : (old instanceof MultipleSetterProperty ? ((MultipleSetterProperty)old).getField() : (CachedField)old);
                mp.setField(field);
            }
            propertyMap.put(mp.getName(), mp);
        }
    }

    @Override
    public void setProperty(Class sender, Object object, String name, Object newValue, boolean useSuper, boolean fromInsideClass) {
        MetaMethod categoryMethod;
        String getterName;
        boolean isStatic;
        this.checkInitalised();
        boolean bl = isStatic = this.theClass != Class.class && object instanceof Class;
        if (isStatic && object != this.theClass) {
            MetaClass mc = this.registry.getMetaClass((Class)object);
            mc.getProperty(sender, object, name, useSuper, fromInsideClass);
            return;
        }
        if (newValue instanceof Wrapper) {
            newValue = ((Wrapper)newValue).unwrap();
        }
        MetaMethod method = null;
        Object[] arguments = null;
        MetaProperty mp = this.getMetaProperty(sender, name, useSuper, isStatic);
        MetaProperty field = null;
        if (mp != null) {
            if (mp instanceof MetaBeanProperty) {
                MetaBeanProperty mbp = (MetaBeanProperty)mp;
                method = mbp.getSetter();
                CachedField f = mbp.getField();
                if (method != null || f != null && !Modifier.isFinal(((MetaProperty)f).getModifiers())) {
                    arguments = new Object[]{newValue};
                    field = f;
                }
            } else {
                field = mp;
            }
        }
        if (!useSuper && !isStatic && GroovyCategorySupport.hasCategoryInCurrentThread() && name.length() > 0 && (getterName = GroovyCategorySupport.getPropertyCategorySetterName(name)) != null && (categoryMethod = MetaClassImpl.getCategoryMethodSetter(sender, getterName, false)) != null) {
            method = categoryMethod;
            arguments = new Object[]{newValue};
        }
        boolean ambiguousListener = false;
        if (method == null) {
            method = this.listeners.get(name);
            boolean bl2 = ambiguousListener = method == AMBIGUOUS_LISTENER_METHOD;
            if (method != null && !ambiguousListener && newValue instanceof Closure) {
                Object proxy = Proxy.newProxyInstance(this.theClass.getClassLoader(), new Class[]{method.getParameterTypes()[0].getTheClass()}, (InvocationHandler)new ConvertedClosure((Closure)newValue, name));
                arguments = new Object[]{proxy};
                newValue = proxy;
            } else {
                method = null;
            }
        }
        if (method == null && field != null) {
            if (Modifier.isFinal(field.getModifiers())) {
                if (!isStatic && this.isMap) {
                    ((Map)object).put(name, newValue);
                    return;
                }
                throw new ReadOnlyPropertyException(name, this.theClass);
            }
            if (!this.isMap || !MetaClassImpl.isPrivateOrPkgPrivate(field.getModifiers())) {
                field.setProperty(object, newValue);
                return;
            }
        }
        if (method == null && !useSuper && !isStatic && GroovyCategorySupport.hasCategoryInCurrentThread() && (method = MetaClassImpl.getCategoryMethodSetter(sender, "set", true)) != null) {
            arguments = new Object[]{name, newValue};
        }
        if (method == null && this.genericSetMethod != null && (this.genericSetMethod.isStatic() || !isStatic)) {
            arguments = new Object[]{name, newValue};
            method = this.genericSetMethod;
        }
        if (method != null) {
            if (arguments.length == 1) {
                arguments[0] = newValue = DefaultTypeTransformation.castToType(newValue, method.getParameterTypes()[0].getTheClass());
            } else {
                arguments[1] = newValue = DefaultTypeTransformation.castToType(newValue, method.getParameterTypes()[1].getTheClass());
            }
            method.doMethodInvoke(object, arguments);
            return;
        }
        if (method == null && !isStatic && this.isMap) {
            ((Map)object).put(name, newValue);
            return;
        }
        if (ambiguousListener) {
            throw new GroovyRuntimeException("There are multiple listeners for the property " + name + ". Please do not use the bean short form to access this listener.");
        }
        if (mp != null) {
            throw new ReadOnlyPropertyException(name, this.theClass);
        }
        if ((isStatic || object instanceof Class) && !"metaClass".equals(name)) {
            this.invokeStaticMissingProperty(object, name, newValue, false);
        } else {
            this.invokeMissingProperty(object, name, newValue, false);
        }
    }

    private static boolean isPrivateOrPkgPrivate(int mod) {
        return !Modifier.isProtected(mod) && !Modifier.isPublic(mod);
    }

    private MetaProperty getMetaProperty(Class _clazz, String name, boolean useSuper, boolean useStatic) {
        SingleKeyHashMap propertyMap;
        if (_clazz == this.theClass) {
            return this.getMetaProperty(name, useStatic);
        }
        CachedClass clazz = ReflectionCache.getCachedClass(_clazz);
        while ((propertyMap = useStatic ? this.staticPropertyIndex : (useSuper ? this.classPropertyIndexForSuper.getNullable(clazz) : this.classPropertyIndex.getNullable(clazz))) == null) {
            if (clazz != this.theCachedClass) {
                clazz = this.theCachedClass;
                continue;
            }
            return null;
        }
        return (MetaProperty)propertyMap.get(name);
    }

    private MetaProperty getMetaProperty(String name, boolean useStatic) {
        CachedClass clazz = this.theCachedClass;
        SingleKeyHashMap propertyMap = useStatic ? this.staticPropertyIndex : this.classPropertyIndex.getNullable(clazz);
        if (propertyMap == null) {
            return null;
        }
        return (MetaProperty)propertyMap.get(name);
    }

    @Override
    public Object getAttribute(Class sender, Object receiver, String messageName, boolean useSuper) {
        return this.getAttribute(receiver, messageName);
    }

    public Object getAttribute(Class sender, Object object, String attribute, boolean useSuper, boolean fromInsideClass) {
        boolean isStatic;
        this.checkInitalised();
        boolean bl = isStatic = this.theClass != Class.class && object instanceof Class;
        if (isStatic && object != this.theClass) {
            MetaClass mc = this.registry.getMetaClass((Class)object);
            return mc.getAttribute(sender, object, attribute, useSuper);
        }
        MetaProperty mp = this.getMetaProperty(sender, attribute, useSuper, isStatic);
        if (mp != null) {
            if (mp instanceof MetaBeanProperty) {
                MetaBeanProperty mbp = (MetaBeanProperty)mp;
                mp = mbp.getField();
            }
            try {
                if (mp != null) {
                    return mp.getProperty(object);
                }
            }
            catch (Exception e) {
                throw new GroovyRuntimeException("Cannot read field: " + attribute, e);
            }
        }
        throw new MissingFieldException(attribute, this.theClass);
    }

    @Override
    public void setAttribute(Class sender, Object object, String attribute, Object newValue, boolean useSuper, boolean fromInsideClass) {
        boolean isStatic;
        this.checkInitalised();
        boolean bl = isStatic = this.theClass != Class.class && object instanceof Class;
        if (isStatic && object != this.theClass) {
            MetaClass mc = this.registry.getMetaClass((Class)object);
            mc.setAttribute(sender, object, attribute, newValue, useSuper, fromInsideClass);
            return;
        }
        MetaProperty mp = this.getMetaProperty(sender, attribute, useSuper, isStatic);
        if (mp != null) {
            if (mp instanceof MetaBeanProperty) {
                MetaBeanProperty mbp = (MetaBeanProperty)mp;
                mp = mbp.getField();
            }
            if (mp != null) {
                mp.setProperty(object, newValue);
                return;
            }
        }
        throw new MissingFieldException(attribute, this.theClass);
    }

    @Override
    public ClassNode getClassNode() {
        if (this.classNode == null && GroovyObject.class.isAssignableFrom(this.theClass)) {
            String groovyFile = this.theClass.getName();
            int idx = groovyFile.indexOf(36);
            if (idx > 0) {
                groovyFile = groovyFile.substring(0, idx);
            }
            groovyFile = groovyFile.replace('.', '/') + ".groovy";
            URL url = this.theClass.getClassLoader().getResource(groovyFile);
            if (url == null) {
                url = Thread.currentThread().getContextClassLoader().getResource(groovyFile);
            }
            if (url != null) {
                try {
                    CompilationUnit.ClassgenCallback search = new CompilationUnit.ClassgenCallback(){

                        @Override
                        public void call(ClassVisitor writer, ClassNode node) {
                            if (node.getName().equals(MetaClassImpl.this.theClass.getName())) {
                                MetaClassImpl.this.classNode = node;
                            }
                        }
                    };
                    CompilationUnit unit = new CompilationUnit();
                    unit.setClassgenCallback(search);
                    unit.addSource(url);
                    unit.compile(7);
                }
                catch (Exception e) {
                    throw new GroovyRuntimeException("Exception thrown parsing: " + groovyFile + ". Reason: " + e, e);
                }
            }
        }
        return this.classNode;
    }

    public String toString() {
        return super.toString() + "[" + this.theClass + "]";
    }

    @Override
    public void addMetaMethod(MetaMethod method) {
        if (this.isInitialized()) {
            throw new RuntimeException("Already initialized, cannot add new method: " + method);
        }
        CachedClass declaringClass = method.getDeclaringClass();
        this.addMetaMethodToIndex(method, this.metaMethodIndex.getHeader(declaringClass.getTheClass()));
    }

    protected void addMetaMethodToIndex(MetaMethod method, MetaMethodIndex.Header header) {
        this.checkIfStdMethod(method);
        String name = method.getName();
        MetaMethodIndex.Entry e = this.metaMethodIndex.getOrPutMethods(name, header);
        if (method.isStatic()) {
            e.staticMethods = this.metaMethodIndex.addMethodToList(e.staticMethods, method);
        }
        e.methods = this.metaMethodIndex.addMethodToList(e.methods, method);
    }

    protected final void checkIfGroovyObjectMethod(MetaMethod metaMethod) {
        if (metaMethod instanceof ClosureMetaMethod || metaMethod instanceof MixinInstanceMetaMethod) {
            if (MetaClassImpl.isGetPropertyMethod(metaMethod)) {
                this.getPropertyMethod = metaMethod;
            } else if (MetaClassImpl.isInvokeMethod(metaMethod)) {
                this.invokeMethodMethod = metaMethod;
            } else if (MetaClassImpl.isSetPropertyMethod(metaMethod)) {
                this.setPropertyMethod = metaMethod;
            }
        }
    }

    private static boolean isSetPropertyMethod(MetaMethod metaMethod) {
        return SET_PROPERTY_METHOD.equals(metaMethod.getName()) && metaMethod.getParameterTypes().length == 2;
    }

    private static boolean isGetPropertyMethod(MetaMethod metaMethod) {
        return GET_PROPERTY_METHOD.equals(metaMethod.getName());
    }

    private static boolean isInvokeMethod(MetaMethod metaMethod) {
        return INVOKE_METHOD_METHOD.equals(metaMethod.getName()) && metaMethod.getParameterTypes().length == 2;
    }

    private void checkIfStdMethod(MetaMethod method) {
        CachedClass[] parameterTypes;
        this.checkIfGroovyObjectMethod(method);
        if (MetaClassImpl.isGenericGetMethod(method) && this.genericGetMethod == null) {
            this.genericGetMethod = method;
        } else if (MetaClassHelper.isGenericSetMethod(method) && this.genericSetMethod == null) {
            this.genericSetMethod = method;
        }
        if (method.getName().equals(PROPERTY_MISSING) && (parameterTypes = method.getParameterTypes()).length == 1) {
            this.propertyMissingGet = method;
        }
        if (this.propertyMissingSet == null && method.getName().equals(PROPERTY_MISSING) && (parameterTypes = method.getParameterTypes()).length == 2) {
            this.propertyMissingSet = method;
        }
        if (method.getName().equals(METHOD_MISSING) && (parameterTypes = method.getParameterTypes()).length == 2 && parameterTypes[0].getTheClass() == String.class && parameterTypes[1].getTheClass() == Object.class) {
            this.methodMissing = method;
        }
        if (this.theCachedClass.isNumber) {
            NumberMathModificationInfo.instance.checkIfStdMethod(method);
        }
    }

    protected boolean isInitialized() {
        return this.initialized;
    }

    private static Boolean getMatchKindForCategory(MetaMethod aMethod, MetaMethod categoryMethod) {
        Class categoryMethodClass;
        CachedClass[] params2;
        CachedClass[] params1 = aMethod.getParameterTypes();
        if (params1.length != (params2 = categoryMethod.getParameterTypes()).length) {
            return Boolean.FALSE;
        }
        for (int i = 0; i < params1.length; ++i) {
            if (params1[i] == params2[i]) continue;
            return Boolean.FALSE;
        }
        Class aMethodClass = aMethod.getDeclaringClass().getTheClass();
        if (aMethodClass == (categoryMethodClass = categoryMethod.getDeclaringClass().getTheClass())) {
            return Boolean.TRUE;
        }
        boolean match = aMethodClass.isAssignableFrom(categoryMethodClass);
        if (match) {
            return Boolean.TRUE;
        }
        return null;
    }

    private static void filterMatchingMethodForCategory(FastArray list, MetaMethod method) {
        int len = list.size();
        if (len == 0) {
            list.add(method);
            return;
        }
        Object[] data = list.getArray();
        for (int j = 0; j != len; ++j) {
            MetaMethod aMethod = (MetaMethod)data[j];
            Boolean match = MetaClassImpl.getMatchKindForCategory(aMethod, method);
            if (match == Boolean.TRUE) {
                list.set(j, method);
                return;
            }
            if (match != null) continue;
            return;
        }
        list.add(method);
    }

    private int findMatchingMethod(CachedMethod[] data, int from, int to, MetaMethod method) {
        for (int j = from; j <= to; ++j) {
            CachedClass[] params2;
            CachedMethod aMethod = data[j];
            CachedClass[] params1 = aMethod.getParameterTypes();
            if (params1.length != (params2 = method.getParameterTypes()).length) continue;
            boolean matches = true;
            for (int i = 0; i < params1.length; ++i) {
                if (params1[i] == params2[i]) continue;
                matches = false;
                break;
            }
            if (!matches) continue;
            return j;
        }
        return -1;
    }

    private MetaMethod findMethod(CachedMethod aMethod) {
        Object methods = this.getMethods(this.theClass, aMethod.getName(), false);
        if (methods instanceof FastArray) {
            FastArray m = (FastArray)methods;
            int len = m.size;
            Object[] data = m.getArray();
            for (int i = 0; i != len; ++i) {
                MetaMethod method = (MetaMethod)data[i];
                if (!method.isMethod(aMethod)) continue;
                return method;
            }
        } else {
            MetaMethod method = (MetaMethod)methods;
            if (method.getName().equals(aMethod.getName()) && method.getReturnType().equals(aMethod.getReturnType()) && MetaMethod.equal(method.getParameterTypes(), aMethod.getParameterTypes())) {
                return method;
            }
        }
        return aMethod;
    }

    protected Object chooseMethod(String methodName, Object methodOrList, Class[] arguments) {
        Object method = this.chooseMethodInternal(methodName, methodOrList, arguments);
        if (method instanceof GeneratedMetaMethod.Proxy) {
            return ((GeneratedMetaMethod.Proxy)method).proxy();
        }
        return method;
    }

    private Object chooseMethodInternal(String methodName, Object methodOrList, Class[] arguments) {
        if (methodOrList instanceof MetaMethod) {
            if (((ParameterTypes)methodOrList).isValidMethod(arguments)) {
                return methodOrList;
            }
            return null;
        }
        FastArray methods = (FastArray)methodOrList;
        if (methods == null) {
            return null;
        }
        int methodCount = methods.size();
        if (methodCount <= 0) {
            return null;
        }
        if (methodCount == 1) {
            Object method = methods.get(0);
            if (((ParameterTypes)method).isValidMethod(arguments)) {
                return method;
            }
            return null;
        }
        if (arguments != null && arguments.length != 0) {
            ArrayList matchingMethods = null;
            int len = methods.size;
            Object[] data = methods.getArray();
            for (int i = 0; i != len; ++i) {
                ArrayList method = data[i];
                if (!((ParameterTypes)((Object)method)).isValidMethod(arguments)) continue;
                if (matchingMethods == null) {
                    matchingMethods = method;
                    continue;
                }
                if (matchingMethods instanceof ArrayList) {
                    ((ArrayList)matchingMethods).add(method);
                    continue;
                }
                ArrayList arr = new ArrayList(4);
                arr.add(matchingMethods);
                arr.add(method);
                matchingMethods = arr;
            }
            if (matchingMethods == null) {
                return null;
            }
            if (!(matchingMethods instanceof ArrayList)) {
                return matchingMethods;
            }
            return this.chooseMostSpecificParams(methodName, matchingMethods, arguments);
        }
        Object answer = MetaClassHelper.chooseEmptyMethodParams(methods);
        if (answer != null) {
            return answer;
        }
        throw new MethodSelectionException(methodName, methods, arguments);
    }

    private Object chooseMostSpecificParams(String name, List matchingMethods, Class[] arguments) {
        return MetaClassImpl.doChooseMostSpecificParams(this.theClass.getName(), name, matchingMethods, arguments, false);
    }

    protected static Object doChooseMostSpecificParams(String theClassName, String name, List matchingMethods, Class[] arguments, boolean checkParametersCompatible) {
        long matchesDistance = -1L;
        LinkedList matches = new LinkedList();
        for (Object method : matchingMethods) {
            ParameterTypes parameterTypes = (ParameterTypes)method;
            if (checkParametersCompatible && !MetaClassHelper.parametersAreCompatible(arguments, parameterTypes.getNativeParameterTypes())) continue;
            long dist = MetaClassHelper.calculateParameterDistance(arguments, parameterTypes);
            if (dist == 0L) {
                return method;
            }
            matchesDistance = MetaClassImpl.handleMatches(matchesDistance, matches, method, dist);
        }
        int size = matches.size();
        if (1 == size) {
            return matches.getFirst();
        }
        if (0 == size) {
            return null;
        }
        throw new GroovyRuntimeException(MetaClassImpl.createErrorMessageForAmbiguity(theClassName, name, arguments, matches));
    }

    protected static String createErrorMessageForAmbiguity(String theClassName, String name, Class[] arguments, LinkedList matches) {
        StringBuilder msg = new StringBuilder("Ambiguous method overloading for method ");
        msg.append(theClassName).append("#").append(name).append(".\nCannot resolve which method to invoke for ").append(InvokerHelper.toString(arguments)).append(" due to overlapping prototypes between:");
        for (Object match : matches) {
            CachedClass[] types = ((ParameterTypes)match).getParameterTypes();
            msg.append("\n\t").append(InvokerHelper.toString(types));
        }
        return msg.toString();
    }

    protected static long handleMatches(long matchesDistance, LinkedList matches, Object method, long dist) {
        if (matches.isEmpty()) {
            matches.add(method);
            matchesDistance = dist;
        } else if (dist < matchesDistance) {
            matchesDistance = dist;
            matches.clear();
            matches.add(method);
        } else if (dist == matchesDistance) {
            matches.add(method);
        }
        return matchesDistance;
    }

    private static boolean isGenericGetMethod(MetaMethod method) {
        if (method.getName().equals("get")) {
            CachedClass[] parameterTypes = method.getParameterTypes();
            return parameterTypes.length == 1 && parameterTypes[0].getTheClass() == String.class;
        }
        return false;
    }

    @Override
    public synchronized void initialize() {
        if (!this.isInitialized()) {
            block3: {
                this.fillMethodIndex();
                try {
                    this.addProperties();
                }
                catch (Throwable e) {
                    if (AndroidSupport.isRunningAndroid()) break block3;
                    ExceptionUtils.sneakyThrow(e);
                }
            }
            this.initialized = true;
        }
    }

    private void addProperties() {
        EventSetDescriptor[] eventDescriptors;
        BeanInfo info;
        try {
            info = MetaClassImpl.isBeanDerivative(this.theClass) ? (BeanInfo)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws IntrospectionException {
                    return Introspector.getBeanInfo(MetaClassImpl.this.theClass, 3);
                }
            }) : (BeanInfo)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws IntrospectionException {
                    return Introspector.getBeanInfo(MetaClassImpl.this.theClass);
                }
            });
        }
        catch (PrivilegedActionException pae) {
            throw new GroovyRuntimeException("exception during bean introspection", pae.getException());
        }
        PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
        this.setupProperties(descriptors);
        for (EventSetDescriptor descriptor : eventDescriptors = info.getEventSetDescriptors()) {
            Method[] listenerMethods;
            for (Method listenerMethod : listenerMethods = descriptor.getListenerMethods()) {
                CachedMethod metaMethod = CachedMethod.find(descriptor.getAddListenerMethod());
                if (metaMethod == null) continue;
                this.addToAllMethodsIfPublic(metaMethod);
                String name = listenerMethod.getName();
                if (this.listeners.containsKey(name)) {
                    this.listeners.put(name, AMBIGUOUS_LISTENER_METHOD);
                    continue;
                }
                this.listeners.put(name, metaMethod);
            }
        }
    }

    private static boolean isBeanDerivative(Class theClass) {
        for (Class next = theClass; next != null; next = next.getSuperclass()) {
            if (!Arrays.asList(next.getInterfaces()).contains(BeanInfo.class)) continue;
            return true;
        }
        return false;
    }

    private void addToAllMethodsIfPublic(MetaMethod metaMethod) {
        if (Modifier.isPublic(metaMethod.getModifiers())) {
            this.allMethods.add(metaMethod);
        }
    }

    @Override
    public List<MetaMethod> getMethods() {
        return this.allMethods;
    }

    @Override
    public List<MetaMethod> getMetaMethods() {
        return new ArrayList<MetaMethod>(this.newGroovyMethodsSet);
    }

    protected void dropStaticMethodCache(String name) {
        this.metaMethodIndex.clearCaches(name);
    }

    protected void dropMethodCache(String name) {
        this.metaMethodIndex.clearCaches(name);
    }

    public CallSite createPojoCallSite(CallSite site, Object receiver, Object[] args) {
        if (!(this instanceof AdaptingMetaClass)) {
            Class[] params = MetaClassHelper.convertToTypeArray(args);
            MetaMethod metaMethod = this.getMethodWithCachingInternal(this.getTheClass(), site, params);
            if (metaMethod != null) {
                return PojoMetaMethodSite.createPojoMetaMethodSite(site, this, metaMethod, params, receiver, args);
            }
        }
        return new PojoMetaClassSite(site, this);
    }

    public CallSite createStaticSite(CallSite site, Object[] args) {
        if (!(this instanceof AdaptingMetaClass)) {
            Class[] params = MetaClassHelper.convertToTypeArray(args);
            MetaMethod metaMethod = this.retrieveStaticMethod(site.getName(), args);
            if (metaMethod != null) {
                return StaticMetaMethodSite.createStaticMetaMethodSite(site, this, metaMethod, params, args);
            }
        }
        return new StaticMetaClassSite(site, this);
    }

    public CallSite createPogoCallSite(CallSite site, Object[] args) {
        if (!GroovyCategorySupport.hasCategoryInCurrentThread() && !(this instanceof AdaptingMetaClass)) {
            MetaMethod metaMethod;
            Class[] params = MetaClassHelper.convertToTypeArray(args);
            CallSite tempSite = site;
            if (site.getName().equals(CLOSURE_CALL_METHOD) && GeneratedClosure.class.isAssignableFrom(this.theClass)) {
                tempSite = new AbstractCallSite(site.getArray(), site.getIndex(), CLOSURE_DO_CALL_METHOD);
            }
            if ((metaMethod = this.getMethodWithCachingInternal(this.theClass, tempSite, params)) != null) {
                return PogoMetaMethodSite.createPogoMetaMethodSite(site, this, metaMethod, params, args);
            }
        }
        return new PogoMetaClassSite(site, this);
    }

    public CallSite createPogoCallCurrentSite(CallSite site, Class sender, Object[] args) {
        Class[] params;
        MetaMethod metaMethod;
        if (!GroovyCategorySupport.hasCategoryInCurrentThread() && !(this instanceof AdaptingMetaClass) && (metaMethod = this.getMethodWithCachingInternal(sender, site, params = MetaClassHelper.convertToTypeArray(args))) != null) {
            return PogoMetaMethodSite.createPogoMetaMethodSite(site, this, metaMethod, params, args);
        }
        return new PogoMetaClassSite(site, this);
    }

    public CallSite createConstructorSite(CallSite site, Object[] args) {
        if (!(this instanceof AdaptingMetaClass)) {
            Class[] params = MetaClassHelper.convertToTypeArray(args);
            CachedConstructor constructor = (CachedConstructor)this.chooseMethod("<init>", this.constructors, params);
            if (constructor != null) {
                return ConstructorSite.createConstructorSite(site, this, constructor, params, args);
            }
            if (args.length == 1 && args[0] instanceof Map) {
                constructor = (CachedConstructor)this.chooseMethod("<init>", this.constructors, MetaClassHelper.EMPTY_TYPE_ARRAY);
                if (constructor != null) {
                    return new ConstructorSite.NoParamSite(site, this, constructor, params);
                }
            } else if (args.length == 2 && this.theClass.getEnclosingClass() != null && args[1] instanceof Map) {
                String enclosingInstanceParamType;
                Class<?> enclosingClass = this.theClass.getEnclosingClass();
                String string = enclosingInstanceParamType = args[0] != null ? args[0].getClass().getName() : "";
                if (enclosingClass.getName().equals(enclosingInstanceParamType) && (constructor = (CachedConstructor)this.chooseMethod("<init>", this.constructors, new Class[]{enclosingClass})) != null) {
                    return new ConstructorSite.NoParamSiteInnerClass(site, this, constructor, params);
                }
            }
        }
        return new MetaClassConstructorSite(site, this);
    }

    public ClassInfo getClassInfo() {
        return this.theCachedClass.classInfo;
    }

    public int getVersion() {
        return this.theCachedClass.classInfo.getVersion();
    }

    public void incVersion() {
        this.theCachedClass.classInfo.incVersion();
    }

    public MetaMethod[] getAdditionalMetaMethods() {
        return this.additionalMetaMethods;
    }

    protected MetaBeanProperty findPropertyInClassHierarchy(String propertyName, CachedClass theClass) {
        MetaBeanProperty property = null;
        if (theClass == null) {
            return null;
        }
        CachedClass superClass = theClass.getCachedSuperClass();
        if (superClass == null) {
            return null;
        }
        MetaClass metaClass = this.registry.getMetaClass(superClass.getTheClass());
        if (metaClass instanceof MutableMetaClass && (property = MetaClassImpl.getMetaPropertyFromMutableMetaClass(propertyName, metaClass)) == null) {
            if (superClass != ReflectionCache.OBJECT_CLASS) {
                property = this.findPropertyInClassHierarchy(propertyName, superClass);
            }
            if (property == null) {
                Class[] interfaces = theClass.getTheClass().getInterfaces();
                property = this.searchInterfacesForMetaProperty(propertyName, interfaces);
            }
        }
        return property;
    }

    private MetaBeanProperty searchInterfacesForMetaProperty(String propertyName, Class[] interfaces) {
        Class[] superInterfaces;
        Class anInterface;
        MetaClass metaClass;
        MetaBeanProperty property = null;
        Class[] classArray = interfaces;
        int n = classArray.length;
        for (int i = 0; !(i >= n || (metaClass = this.registry.getMetaClass(anInterface = classArray[i])) instanceof MutableMetaClass && (property = MetaClassImpl.getMetaPropertyFromMutableMetaClass(propertyName, metaClass)) != null || (superInterfaces = anInterface.getInterfaces()).length > 0 && (property = this.searchInterfacesForMetaProperty(propertyName, superInterfaces)) != null); ++i) {
        }
        return property;
    }

    private static MetaBeanProperty getMetaPropertyFromMutableMetaClass(String propertyName, MetaClass metaClass) {
        MetaProperty metaProperty;
        boolean isModified = ((MutableMetaClass)metaClass).isModified();
        if (isModified && (metaProperty = metaClass.getMetaProperty(propertyName)) instanceof MetaBeanProperty) {
            return (MetaBeanProperty)metaProperty;
        }
        return null;
    }

    protected MetaMethod findMixinMethod(String methodName, Class[] arguments) {
        return null;
    }

    protected static MetaMethod findMethodInClassHierarchy(Class instanceKlazz, String methodName, Class[] arguments, MetaClass metaClass) {
        MetaClass superMetaClass;
        if (metaClass instanceof MetaClassImpl) {
            boolean check = false;
            for (ClassInfo ci : ((MetaClassImpl)metaClass).theCachedClass.getHierarchy()) {
                MetaClass aClass = ci.getStrongMetaClass();
                if (!(aClass instanceof MutableMetaClass) || !((MutableMetaClass)aClass).isModified()) continue;
                check = true;
                break;
            }
            if (!check) {
                return null;
            }
        }
        MetaMethod method = null;
        Class<Object> superClass = metaClass.getTheClass().isArray() && !metaClass.getTheClass().getComponentType().isPrimitive() && metaClass.getTheClass().getComponentType() != Object.class ? Object[].class : metaClass.getTheClass().getSuperclass();
        if (superClass != null) {
            superMetaClass = GroovySystem.getMetaClassRegistry().getMetaClass(superClass);
            method = MetaClassImpl.findMethodInClassHierarchy(instanceKlazz, methodName, arguments, superMetaClass);
        } else if (metaClass.getTheClass().isInterface()) {
            superMetaClass = GroovySystem.getMetaClassRegistry().getMetaClass(Object.class);
            method = MetaClassImpl.findMethodInClassHierarchy(instanceKlazz, methodName, arguments, superMetaClass);
        }
        method = MetaClassImpl.findSubClassMethod(instanceKlazz, methodName, arguments, metaClass, method);
        MetaMethod infMethod = MetaClassImpl.searchInterfacesForMetaMethod(instanceKlazz, methodName, arguments, metaClass);
        if (infMethod != null) {
            method = method == null ? infMethod : MetaClassImpl.mostSpecific(method, infMethod, instanceKlazz);
        }
        method = MetaClassImpl.findOwnMethod(instanceKlazz, methodName, arguments, metaClass, method);
        return method;
    }

    private static MetaMethod findSubClassMethod(Class instanceKlazz, String methodName, Class[] arguments, MetaClass metaClass, MetaMethod method) {
        Object list;
        if (metaClass instanceof MetaClassImpl && (list = ((MetaClassImpl)metaClass).getSubclassMetaMethods(methodName)) != null) {
            if (list instanceof MetaMethod) {
                MetaMethod m = (MetaMethod)list;
                if (m.getDeclaringClass().getTheClass().isAssignableFrom(instanceKlazz) && m.isValidExactMethod(arguments)) {
                    method = method == null ? m : MetaClassImpl.mostSpecific(method, m, instanceKlazz);
                }
            } else {
                FastArray arr = (FastArray)list;
                for (int i = 0; i != arr.size(); ++i) {
                    MetaMethod m = (MetaMethod)arr.get(i);
                    if (!m.getDeclaringClass().getTheClass().isAssignableFrom(instanceKlazz) || !m.isValidExactMethod(arguments)) continue;
                    method = method == null ? m : MetaClassImpl.mostSpecific(method, m, instanceKlazz);
                }
            }
        }
        return method;
    }

    private static MetaMethod mostSpecific(MetaMethod method, MetaMethod newMethod, Class instanceKlazz) {
        Class newMethodC = newMethod.getDeclaringClass().getTheClass();
        Class methodC = method.getDeclaringClass().getTheClass();
        if (!newMethodC.isAssignableFrom(instanceKlazz)) {
            return method;
        }
        if (newMethodC == methodC) {
            return newMethod;
        }
        if (newMethodC.isAssignableFrom(methodC)) {
            return method;
        }
        if (methodC.isAssignableFrom(newMethodC)) {
            return newMethod;
        }
        return newMethod;
    }

    private static MetaMethod searchInterfacesForMetaMethod(Class instanceKlazz, String methodName, Class[] arguments, MetaClass metaClass) {
        Class<?>[] interfaces = metaClass.getTheClass().getInterfaces();
        MetaMethod method = null;
        for (Class<?> anInterface : interfaces) {
            MetaClass infMetaClass = GroovySystem.getMetaClassRegistry().getMetaClass(anInterface);
            MetaMethod infMethod = MetaClassImpl.searchInterfacesForMetaMethod(instanceKlazz, methodName, arguments, infMetaClass);
            if (infMethod == null) continue;
            method = method == null ? infMethod : MetaClassImpl.mostSpecific(method, infMethod, instanceKlazz);
        }
        method = MetaClassImpl.findSubClassMethod(instanceKlazz, methodName, arguments, metaClass, method);
        method = MetaClassImpl.findOwnMethod(instanceKlazz, methodName, arguments, metaClass, method);
        return method;
    }

    protected static MetaMethod findOwnMethod(Class instanceKlazz, String methodName, Class[] arguments, MetaClass metaClass, MetaMethod method) {
        if (instanceKlazz == metaClass.getTheClass()) {
            return method;
        }
        MetaMethod ownMethod = metaClass.pickMethod(methodName, arguments);
        if (ownMethod != null) {
            method = method == null ? ownMethod : MetaClassImpl.mostSpecific(method, ownMethod, instanceKlazz);
        }
        return method;
    }

    protected Object getSubclassMetaMethods(String methodName) {
        return null;
    }

    @Override
    public Object getProperty(Object object, String property) {
        return this.getProperty(this.theClass, object, property, false, false);
    }

    @Override
    public void setProperty(Object object, String property, Object newValue) {
        this.setProperty(this.theClass, object, property, newValue, false, false);
    }

    @Override
    public Object getAttribute(Object object, String attribute) {
        return this.getAttribute(this.theClass, object, attribute, false, false);
    }

    @Override
    public void setAttribute(Object object, String attribute, Object newValue) {
        this.setAttribute(this.theClass, object, attribute, newValue, false, false);
    }

    @Override
    public MetaMethod pickMethod(String methodName, Class[] arguments) {
        return this.getMethodWithoutCaching(this.theClass, methodName, arguments, false);
    }

    public boolean hasCustomInvokeMethod() {
        return this.invokeMethodMethod != null;
    }

    public boolean hasCustomStaticInvokeMethod() {
        return false;
    }

    protected void clearInvocationCaches() {
        this.metaMethodIndex.clearCaches();
    }

    private static class DummyMetaMethod
    extends MetaMethod {
        private DummyMetaMethod() {
        }

        @Override
        public int getModifiers() {
            return 0;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public Class getReturnType() {
            return null;
        }

        @Override
        public CachedClass getDeclaringClass() {
            return null;
        }

        public ParameterTypes getParamTypes() {
            return null;
        }

        @Override
        public Object invoke(Object object, Object[] arguments) {
            return null;
        }
    }

    public static class Index
    extends SingleKeyHashMap {
        public Index(int size) {
        }

        public Index() {
        }

        public Index(boolean size) {
            super(false);
        }

        public SingleKeyHashMap getNotNull(CachedClass key) {
            SingleKeyHashMap.Entry res = this.getOrPut(key);
            if (res.value == null) {
                res.value = new SingleKeyHashMap();
            }
            return (SingleKeyHashMap)res.value;
        }

        public void put(CachedClass key, SingleKeyHashMap value) {
            this.getOrPut((Object)key).value = value;
        }

        public SingleKeyHashMap getNullable(CachedClass clazz) {
            return (SingleKeyHashMap)this.get(clazz);
        }

        public boolean checkEquals(ComplexKeyHashMap.Entry e, Object key) {
            return ((SingleKeyHashMap.Entry)e).key.equals(key);
        }
    }

    class MethodIndex
    extends Index {
        public MethodIndex(boolean b) {
            super(false);
        }

        public MethodIndex(int size) {
            super(size);
        }

        public MethodIndex() {
        }

        MethodIndex copy() {
            return (MethodIndex)SingleKeyHashMap.copy(new MethodIndex(false), this, METHOD_INDEX_COPIER);
        }

        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    private abstract class MethodIndexAction {
        private MethodIndexAction() {
        }

        public void iterate() {
            ComplexKeyHashMap.Entry[] table = MetaClassImpl.this.metaMethodIndex.methodHeaders.getTable();
            int len = table.length;
            for (int i = 0; i != len; ++i) {
                SingleKeyHashMap.Entry classEntry = (SingleKeyHashMap.Entry)table[i];
                while (classEntry != null) {
                    Class clazz = (Class)classEntry.getKey();
                    if (!this.skipClass(clazz)) {
                        MetaMethodIndex.Header header = (MetaMethodIndex.Header)classEntry.getValue();
                        MetaMethodIndex.Entry nameEntry = header.head;
                        while (nameEntry != null) {
                            this.methodNameAction(clazz, nameEntry);
                            nameEntry = nameEntry.nextClassEntry;
                        }
                    }
                    classEntry = (SingleKeyHashMap.Entry)classEntry.next;
                }
            }
        }

        public abstract void methodNameAction(Class var1, MetaMethodIndex.Entry var2);

        public boolean skipClass(Class clazz) {
            return false;
        }
    }

    public static final class MetaConstructor
    extends MetaMethod {
        private final CachedConstructor cc;
        private final boolean beanConstructor;

        private MetaConstructor(CachedConstructor cc, boolean bean) {
            super(cc.getNativeParameterTypes());
            this.setParametersTypes(cc.getParameterTypes());
            this.cc = cc;
            this.beanConstructor = bean;
        }

        @Override
        public int getModifiers() {
            return this.cc.getModifiers();
        }

        @Override
        public String getName() {
            return "<init>";
        }

        @Override
        public Class getReturnType() {
            return this.cc.getCachedClass().getTheClass();
        }

        @Override
        public CachedClass getDeclaringClass() {
            return this.cc.getCachedClass();
        }

        @Override
        public Object invoke(Object object, Object[] arguments) {
            return this.cc.doConstructorInvoke(arguments);
        }

        public CachedConstructor getCachedConstrcutor() {
            return this.cc;
        }

        public boolean isBeanConstructor() {
            return this.beanConstructor;
        }
    }
}

