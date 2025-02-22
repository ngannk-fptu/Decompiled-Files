/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.asm.$ClassVisitor;
import com.google.inject.internal.asm.$Label;
import com.google.inject.internal.asm.$Type;
import com.google.inject.internal.cglib.core.$AbstractClassGenerator;
import com.google.inject.internal.cglib.core.$ClassEmitter;
import com.google.inject.internal.cglib.core.$CodeEmitter;
import com.google.inject.internal.cglib.core.$CodeGenerationException;
import com.google.inject.internal.cglib.core.$CollectionUtils;
import com.google.inject.internal.cglib.core.$Constants;
import com.google.inject.internal.cglib.core.$DuplicatesPredicate;
import com.google.inject.internal.cglib.core.$EmitUtils;
import com.google.inject.internal.cglib.core.$KeyFactory;
import com.google.inject.internal.cglib.core.$Local;
import com.google.inject.internal.cglib.core.$MethodInfo;
import com.google.inject.internal.cglib.core.$MethodInfoTransformer;
import com.google.inject.internal.cglib.core.$MethodWrapper;
import com.google.inject.internal.cglib.core.$ObjectSwitchCallback;
import com.google.inject.internal.cglib.core.$ProcessSwitchCallback;
import com.google.inject.internal.cglib.core.$ReflectUtils;
import com.google.inject.internal.cglib.core.$RejectModifierPredicate;
import com.google.inject.internal.cglib.core.$Signature;
import com.google.inject.internal.cglib.core.$Transformer;
import com.google.inject.internal.cglib.core.$TypeUtils;
import com.google.inject.internal.cglib.core.$VisibilityPredicate;
import com.google.inject.internal.cglib.proxy.$Callback;
import com.google.inject.internal.cglib.proxy.$CallbackFilter;
import com.google.inject.internal.cglib.proxy.$CallbackGenerator;
import com.google.inject.internal.cglib.proxy.$CallbackInfo;
import com.google.inject.internal.cglib.proxy.$Factory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class $Enhancer
extends $AbstractClassGenerator {
    private static final $CallbackFilter ALL_ZERO = new $CallbackFilter(){

        public int accept(Method method) {
            return 0;
        }
    };
    private static final $AbstractClassGenerator.Source SOURCE = new $AbstractClassGenerator.Source((class$net$sf$cglib$proxy$Enhancer == null ? (class$net$sf$cglib$proxy$Enhancer = $Enhancer.class$("com.google.inject.internal.cglib.proxy.$Enhancer")) : class$net$sf$cglib$proxy$Enhancer).getName());
    private static final EnhancerKey KEY_FACTORY = (EnhancerKey)((Object)$KeyFactory.create(class$net$sf$cglib$proxy$Enhancer$EnhancerKey == null ? (class$net$sf$cglib$proxy$Enhancer$EnhancerKey = $Enhancer.class$("com.google.inject.internal.cglib.proxy.$Enhancer$EnhancerKey")) : class$net$sf$cglib$proxy$Enhancer$EnhancerKey));
    private static final String BOUND_FIELD = "CGLIB$BOUND";
    private static final String THREAD_CALLBACKS_FIELD = "CGLIB$THREAD_CALLBACKS";
    private static final String STATIC_CALLBACKS_FIELD = "CGLIB$STATIC_CALLBACKS";
    private static final String SET_THREAD_CALLBACKS_NAME = "CGLIB$SET_THREAD_CALLBACKS";
    private static final String SET_STATIC_CALLBACKS_NAME = "CGLIB$SET_STATIC_CALLBACKS";
    private static final String CONSTRUCTED_FIELD = "CGLIB$CONSTRUCTED";
    private static final $Type FACTORY = $TypeUtils.parseType("com.google.inject.internal.cglib.proxy.$Factory");
    private static final $Type ILLEGAL_STATE_EXCEPTION = $TypeUtils.parseType("IllegalStateException");
    private static final $Type ILLEGAL_ARGUMENT_EXCEPTION = $TypeUtils.parseType("IllegalArgumentException");
    private static final $Type THREAD_LOCAL = $TypeUtils.parseType("ThreadLocal");
    private static final $Type CALLBACK = $TypeUtils.parseType("com.google.inject.internal.cglib.proxy.$Callback");
    private static final $Type CALLBACK_ARRAY = $Type.getType(array$Lnet$sf$cglib$proxy$Callback == null ? (array$Lnet$sf$cglib$proxy$Callback = $Enhancer.class$("[Lcom.google.inject.internal.cglib.proxy.$Callback;")) : array$Lnet$sf$cglib$proxy$Callback);
    private static final $Signature CSTRUCT_NULL = $TypeUtils.parseConstructor("");
    private static final $Signature SET_THREAD_CALLBACKS = new $Signature("CGLIB$SET_THREAD_CALLBACKS", $Type.VOID_TYPE, new $Type[]{CALLBACK_ARRAY});
    private static final $Signature SET_STATIC_CALLBACKS = new $Signature("CGLIB$SET_STATIC_CALLBACKS", $Type.VOID_TYPE, new $Type[]{CALLBACK_ARRAY});
    private static final $Signature NEW_INSTANCE = new $Signature("newInstance", $Constants.TYPE_OBJECT, new $Type[]{CALLBACK_ARRAY});
    private static final $Signature MULTIARG_NEW_INSTANCE = new $Signature("newInstance", $Constants.TYPE_OBJECT, new $Type[]{$Constants.TYPE_CLASS_ARRAY, $Constants.TYPE_OBJECT_ARRAY, CALLBACK_ARRAY});
    private static final $Signature SINGLE_NEW_INSTANCE = new $Signature("newInstance", $Constants.TYPE_OBJECT, new $Type[]{CALLBACK});
    private static final $Signature SET_CALLBACK = new $Signature("setCallback", $Type.VOID_TYPE, new $Type[]{$Type.INT_TYPE, CALLBACK});
    private static final $Signature GET_CALLBACK = new $Signature("getCallback", CALLBACK, new $Type[]{$Type.INT_TYPE});
    private static final $Signature SET_CALLBACKS = new $Signature("setCallbacks", $Type.VOID_TYPE, new $Type[]{CALLBACK_ARRAY});
    private static final $Signature GET_CALLBACKS = new $Signature("getCallbacks", CALLBACK_ARRAY, new $Type[0]);
    private static final $Signature THREAD_LOCAL_GET = $TypeUtils.parseSignature("Object get()");
    private static final $Signature THREAD_LOCAL_SET = $TypeUtils.parseSignature("void set(Object)");
    private static final $Signature BIND_CALLBACKS = $TypeUtils.parseSignature("void CGLIB$BIND_CALLBACKS(Object)");
    private Class[] interfaces;
    private $CallbackFilter filter;
    private $Callback[] callbacks;
    private $Type[] callbackTypes;
    private boolean classOnly;
    private Class superclass;
    private Class[] argumentTypes;
    private Object[] arguments;
    private boolean useFactory = true;
    private Long serialVersionUID;
    private boolean interceptDuringConstruction = true;
    static /* synthetic */ Class class$net$sf$cglib$proxy$Enhancer;
    static /* synthetic */ Class class$net$sf$cglib$proxy$Enhancer$EnhancerKey;
    static /* synthetic */ Class array$Lnet$sf$cglib$proxy$Callback;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$net$sf$cglib$proxy$Factory;

    public $Enhancer() {
        super(SOURCE);
    }

    public void setSuperclass(Class superclass) {
        if (superclass != null && superclass.isInterface()) {
            this.setInterfaces(new Class[]{superclass});
        } else {
            this.superclass = superclass != null && superclass.equals(class$java$lang$Object == null ? (class$java$lang$Object = $Enhancer.class$("java.lang.Object")) : class$java$lang$Object) ? null : superclass;
        }
    }

    public void setInterfaces(Class[] interfaces) {
        this.interfaces = interfaces;
    }

    public void setCallbackFilter($CallbackFilter filter) {
        this.filter = filter;
    }

    public void setCallback($Callback callback) {
        this.setCallbacks(new $Callback[]{callback});
    }

    public void setCallbacks($Callback[] callbacks) {
        if (callbacks != null && callbacks.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty");
        }
        this.callbacks = callbacks;
    }

    public void setUseFactory(boolean useFactory) {
        this.useFactory = useFactory;
    }

    public void setInterceptDuringConstruction(boolean interceptDuringConstruction) {
        this.interceptDuringConstruction = interceptDuringConstruction;
    }

    public void setCallbackType(Class callbackType) {
        this.setCallbackTypes(new Class[]{callbackType});
    }

    public void setCallbackTypes(Class[] callbackTypes) {
        if (callbackTypes != null && callbackTypes.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty");
        }
        this.callbackTypes = $CallbackInfo.determineTypes(callbackTypes);
    }

    public Object create() {
        this.classOnly = false;
        this.argumentTypes = null;
        return this.createHelper();
    }

    public Object create(Class[] argumentTypes, Object[] arguments) {
        this.classOnly = false;
        if (argumentTypes == null || arguments == null || argumentTypes.length != arguments.length) {
            throw new IllegalArgumentException("Arguments must be non-null and of equal length");
        }
        this.argumentTypes = argumentTypes;
        this.arguments = arguments;
        return this.createHelper();
    }

    public Class createClass() {
        this.classOnly = true;
        return (Class)this.createHelper();
    }

    public void setSerialVersionUID(Long sUID) {
        this.serialVersionUID = sUID;
    }

    private void validate() {
        if (this.classOnly ^ this.callbacks == null) {
            if (this.classOnly) {
                throw new IllegalStateException("createClass does not accept callbacks");
            }
            throw new IllegalStateException("Callbacks are required");
        }
        if (this.classOnly && this.callbackTypes == null) {
            throw new IllegalStateException("Callback types are required");
        }
        if (this.callbacks != null && this.callbackTypes != null) {
            if (this.callbacks.length != this.callbackTypes.length) {
                throw new IllegalStateException("Lengths of callback and callback types array must be the same");
            }
            $Type[] check = $CallbackInfo.determineTypes(this.callbacks);
            for (int i = 0; i < check.length; ++i) {
                if (check[i].equals(this.callbackTypes[i])) continue;
                throw new IllegalStateException("Callback " + check[i] + " is not assignable to " + this.callbackTypes[i]);
            }
        } else if (this.callbacks != null) {
            this.callbackTypes = $CallbackInfo.determineTypes(this.callbacks);
        }
        if (this.filter == null) {
            if (this.callbackTypes.length > 1) {
                throw new IllegalStateException("Multiple callback types possible but no filter specified");
            }
            this.filter = ALL_ZERO;
        }
        if (this.interfaces != null) {
            for (int i = 0; i < this.interfaces.length; ++i) {
                if (this.interfaces[i] == null) {
                    throw new IllegalStateException("Interfaces cannot be null");
                }
                if (this.interfaces[i].isInterface()) continue;
                throw new IllegalStateException(this.interfaces[i] + " is not an interface");
            }
        }
    }

    private Object createHelper() {
        this.validate();
        if (this.superclass != null) {
            this.setNamePrefix(this.superclass.getName());
        } else if (this.interfaces != null) {
            this.setNamePrefix(this.interfaces[$ReflectUtils.findPackageProtected(this.interfaces)].getName());
        }
        return super.create(KEY_FACTORY.newInstance(this.superclass != null ? this.superclass.getName() : null, $ReflectUtils.getNames(this.interfaces), this.filter, this.callbackTypes, this.useFactory, this.interceptDuringConstruction, this.serialVersionUID));
    }

    protected ClassLoader getDefaultClassLoader() {
        if (this.superclass != null) {
            return this.superclass.getClassLoader();
        }
        if (this.interfaces != null) {
            return this.interfaces[0].getClassLoader();
        }
        return null;
    }

    private $Signature rename($Signature sig, int index) {
        return new $Signature("CGLIB$" + sig.getName() + "$" + index, sig.getDescriptor());
    }

    public static void getMethods(Class superclass, Class[] interfaces, List methods) {
        $Enhancer.getMethods(superclass, interfaces, methods, null, null);
    }

    private static void getMethods(Class superclass, Class[] interfaces, List methods, List interfaceMethods, Set forcePublic) {
        List target;
        $ReflectUtils.addAllMethods(superclass, methods);
        List list = target = interfaceMethods != null ? interfaceMethods : methods;
        if (interfaces != null) {
            for (int i = 0; i < interfaces.length; ++i) {
                if (interfaces[i] == (class$net$sf$cglib$proxy$Factory == null ? $Enhancer.class$("com.google.inject.internal.cglib.proxy.$Factory") : class$net$sf$cglib$proxy$Factory)) continue;
                $ReflectUtils.addAllMethods(interfaces[i], target);
            }
        }
        if (interfaceMethods != null) {
            if (forcePublic != null) {
                forcePublic.addAll($MethodWrapper.createSet(interfaceMethods));
            }
            methods.addAll(interfaceMethods);
        }
        $CollectionUtils.filter(methods, new $RejectModifierPredicate(8));
        $CollectionUtils.filter(methods, new $VisibilityPredicate(superclass, true));
        $CollectionUtils.filter(methods, new $DuplicatesPredicate());
        $CollectionUtils.filter(methods, new $RejectModifierPredicate(16));
    }

    public void generateClass($ClassVisitor v) throws Exception {
        Class sc;
        Class clazz = this.superclass == null ? (class$java$lang$Object == null ? (class$java$lang$Object = $Enhancer.class$("java.lang.Object")) : class$java$lang$Object) : (sc = this.superclass);
        if ($TypeUtils.isFinal(sc.getModifiers())) {
            throw new IllegalArgumentException("Cannot subclass final class " + sc);
        }
        ArrayList constructors = new ArrayList(Arrays.asList(sc.getDeclaredConstructors()));
        this.filterConstructors(sc, constructors);
        ArrayList actualMethods = new ArrayList();
        ArrayList interfaceMethods = new ArrayList();
        final HashSet forcePublic = new HashSet();
        $Enhancer.getMethods(sc, this.interfaces, actualMethods, interfaceMethods, forcePublic);
        List methods = $CollectionUtils.transform(actualMethods, new $Transformer(){

            public Object transform(Object value) {
                Method method = (Method)value;
                int modifiers = 0x10 | method.getModifiers() & 0xFFFFFBFF & 0xFFFFFEFF & 0xFFFFFFDF;
                if (forcePublic.contains($MethodWrapper.create(method))) {
                    modifiers = modifiers & 0xFFFFFFFB | 1;
                }
                return $ReflectUtils.getMethodInfo(method, modifiers);
            }
        });
        $ClassEmitter e = new $ClassEmitter(v);
        e.begin_class(46, 1, this.getClassName(), $Type.getType(sc), this.useFactory ? $TypeUtils.add($TypeUtils.getTypes(this.interfaces), FACTORY) : $TypeUtils.getTypes(this.interfaces), "<generated>");
        List constructorInfo = $CollectionUtils.transform(constructors, $MethodInfoTransformer.getInstance());
        e.declare_field(2, BOUND_FIELD, $Type.BOOLEAN_TYPE, null);
        if (!this.interceptDuringConstruction) {
            e.declare_field(2, CONSTRUCTED_FIELD, $Type.BOOLEAN_TYPE, null);
        }
        e.declare_field(26, THREAD_CALLBACKS_FIELD, THREAD_LOCAL, null);
        e.declare_field(26, STATIC_CALLBACKS_FIELD, CALLBACK_ARRAY, null);
        if (this.serialVersionUID != null) {
            e.declare_field(26, "serialVersionUID", $Type.LONG_TYPE, this.serialVersionUID);
        }
        for (int i = 0; i < this.callbackTypes.length; ++i) {
            e.declare_field(2, $Enhancer.getCallbackField(i), this.callbackTypes[i], null);
        }
        this.emitMethods(e, methods, actualMethods);
        this.emitConstructors(e, constructorInfo);
        this.emitSetThreadCallbacks(e);
        this.emitSetStaticCallbacks(e);
        this.emitBindCallbacks(e);
        if (this.useFactory) {
            int[] keys = this.getCallbackKeys();
            this.emitNewInstanceCallbacks(e);
            this.emitNewInstanceCallback(e);
            this.emitNewInstanceMultiarg(e, constructorInfo);
            this.emitGetCallback(e, keys);
            this.emitSetCallback(e, keys);
            this.emitGetCallbacks(e);
            this.emitSetCallbacks(e);
        }
        e.end_class();
    }

    protected void filterConstructors(Class sc, List constructors) {
        $CollectionUtils.filter(constructors, new $VisibilityPredicate(sc, true));
        if (constructors.size() == 0) {
            throw new IllegalArgumentException("No visible constructors in " + sc);
        }
    }

    protected Object firstInstance(Class type) throws Exception {
        if (this.classOnly) {
            return type;
        }
        return this.createUsingReflection(type);
    }

    protected Object nextInstance(Object instance) {
        Class<?> protoclass;
        Class<?> clazz = protoclass = instance instanceof Class ? (Class<?>)instance : instance.getClass();
        if (this.classOnly) {
            return protoclass;
        }
        if (instance instanceof $Factory) {
            if (this.argumentTypes != null) {
                return (($Factory)instance).newInstance(this.argumentTypes, this.arguments, this.callbacks);
            }
            return (($Factory)instance).newInstance(this.callbacks);
        }
        return this.createUsingReflection(protoclass);
    }

    public static void registerCallbacks(Class generatedClass, $Callback[] callbacks) {
        $Enhancer.setThreadCallbacks(generatedClass, callbacks);
    }

    public static void registerStaticCallbacks(Class generatedClass, $Callback[] callbacks) {
        $Enhancer.setCallbacksHelper(generatedClass, callbacks, SET_STATIC_CALLBACKS_NAME);
    }

    public static boolean isEnhanced(Class type) {
        try {
            $Enhancer.getCallbacksSetter(type, SET_THREAD_CALLBACKS_NAME);
            return true;
        }
        catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static void setThreadCallbacks(Class type, $Callback[] callbacks) {
        $Enhancer.setCallbacksHelper(type, callbacks, SET_THREAD_CALLBACKS_NAME);
    }

    private static void setCallbacksHelper(Class type, $Callback[] callbacks, String methodName) {
        try {
            Method setter = $Enhancer.getCallbacksSetter(type, methodName);
            setter.invoke(null, new Object[]{callbacks});
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(type + " is not an enhanced class");
        }
        catch (IllegalAccessException e) {
            throw new $CodeGenerationException(e);
        }
        catch (InvocationTargetException e) {
            throw new $CodeGenerationException(e);
        }
    }

    private static Method getCallbacksSetter(Class type, String methodName) throws NoSuchMethodException {
        return type.getDeclaredMethod(methodName, array$Lnet$sf$cglib$proxy$Callback == null ? (array$Lnet$sf$cglib$proxy$Callback = $Enhancer.class$("[Lcom.google.inject.internal.cglib.proxy.$Callback;")) : array$Lnet$sf$cglib$proxy$Callback);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object createUsingReflection(Class type) {
        $Enhancer.setThreadCallbacks(type, this.callbacks);
        try {
            if (this.argumentTypes != null) {
                Object object = $ReflectUtils.newInstance(type, this.argumentTypes, this.arguments);
                return object;
            }
            Object object = $ReflectUtils.newInstance(type);
            return object;
        }
        finally {
            $Enhancer.setThreadCallbacks(type, null);
        }
    }

    public static Object create(Class type, $Callback callback) {
        $Enhancer e = new $Enhancer();
        e.setSuperclass(type);
        e.setCallback(callback);
        return e.create();
    }

    public static Object create(Class superclass, Class[] interfaces, $Callback callback) {
        $Enhancer e = new $Enhancer();
        e.setSuperclass(superclass);
        e.setInterfaces(interfaces);
        e.setCallback(callback);
        return e.create();
    }

    public static Object create(Class superclass, Class[] interfaces, $CallbackFilter filter, $Callback[] callbacks) {
        $Enhancer e = new $Enhancer();
        e.setSuperclass(superclass);
        e.setInterfaces(interfaces);
        e.setCallbackFilter(filter);
        e.setCallbacks(callbacks);
        return e.create();
    }

    private void emitConstructors($ClassEmitter ce, List constructors) {
        boolean seenNull = false;
        Iterator it = constructors.iterator();
        while (it.hasNext()) {
            $MethodInfo constructor = ($MethodInfo)it.next();
            $CodeEmitter e = $EmitUtils.begin_method(ce, constructor, 1);
            e.load_this();
            e.dup();
            e.load_args();
            $Signature sig = constructor.getSignature();
            seenNull = seenNull || sig.getDescriptor().equals("()V");
            e.super_invoke_constructor(sig);
            e.invoke_static_this(BIND_CALLBACKS);
            if (!this.interceptDuringConstruction) {
                e.load_this();
                e.push(1);
                e.putfield(CONSTRUCTED_FIELD);
            }
            e.return_value();
            e.end_method();
        }
        if (!this.classOnly && !seenNull && this.arguments == null) {
            throw new IllegalArgumentException("Superclass has no null constructors but no arguments were given");
        }
    }

    private int[] getCallbackKeys() {
        int[] keys = new int[this.callbackTypes.length];
        for (int i = 0; i < this.callbackTypes.length; ++i) {
            keys[i] = i;
        }
        return keys;
    }

    private void emitGetCallback($ClassEmitter ce, int[] keys) {
        final $CodeEmitter e = ce.begin_method(1, GET_CALLBACK, null);
        e.load_this();
        e.invoke_static_this(BIND_CALLBACKS);
        e.load_this();
        e.load_arg(0);
        e.process_switch(keys, new $ProcessSwitchCallback(){

            public void processCase(int key, $Label end) {
                e.getfield($Enhancer.getCallbackField(key));
                e.goTo(end);
            }

            public void processDefault() {
                e.pop();
                e.aconst_null();
            }
        });
        e.return_value();
        e.end_method();
    }

    private void emitSetCallback($ClassEmitter ce, int[] keys) {
        final $CodeEmitter e = ce.begin_method(1, SET_CALLBACK, null);
        e.load_arg(0);
        e.process_switch(keys, new $ProcessSwitchCallback(){

            public void processCase(int key, $Label end) {
                e.load_this();
                e.load_arg(1);
                e.checkcast($Enhancer.this.callbackTypes[key]);
                e.putfield($Enhancer.getCallbackField(key));
                e.goTo(end);
            }

            public void processDefault() {
            }
        });
        e.return_value();
        e.end_method();
    }

    private void emitSetCallbacks($ClassEmitter ce) {
        $CodeEmitter e = ce.begin_method(1, SET_CALLBACKS, null);
        e.load_this();
        e.load_arg(0);
        for (int i = 0; i < this.callbackTypes.length; ++i) {
            e.dup2();
            e.aaload(i);
            e.checkcast(this.callbackTypes[i]);
            e.putfield($Enhancer.getCallbackField(i));
        }
        e.return_value();
        e.end_method();
    }

    private void emitGetCallbacks($ClassEmitter ce) {
        $CodeEmitter e = ce.begin_method(1, GET_CALLBACKS, null);
        e.load_this();
        e.invoke_static_this(BIND_CALLBACKS);
        e.load_this();
        e.push(this.callbackTypes.length);
        e.newarray(CALLBACK);
        for (int i = 0; i < this.callbackTypes.length; ++i) {
            e.dup();
            e.push(i);
            e.load_this();
            e.getfield($Enhancer.getCallbackField(i));
            e.aastore();
        }
        e.return_value();
        e.end_method();
    }

    private void emitNewInstanceCallbacks($ClassEmitter ce) {
        $CodeEmitter e = ce.begin_method(1, NEW_INSTANCE, null);
        e.load_arg(0);
        e.invoke_static_this(SET_THREAD_CALLBACKS);
        this.emitCommonNewInstance(e);
    }

    private void emitCommonNewInstance($CodeEmitter e) {
        e.new_instance_this();
        e.dup();
        e.invoke_constructor_this();
        e.aconst_null();
        e.invoke_static_this(SET_THREAD_CALLBACKS);
        e.return_value();
        e.end_method();
    }

    private void emitNewInstanceCallback($ClassEmitter ce) {
        $CodeEmitter e = ce.begin_method(1, SINGLE_NEW_INSTANCE, null);
        switch (this.callbackTypes.length) {
            case 0: {
                break;
            }
            case 1: {
                e.push(1);
                e.newarray(CALLBACK);
                e.dup();
                e.push(0);
                e.load_arg(0);
                e.aastore();
                e.invoke_static_this(SET_THREAD_CALLBACKS);
                break;
            }
            default: {
                e.throw_exception(ILLEGAL_STATE_EXCEPTION, "More than one callback object required");
            }
        }
        this.emitCommonNewInstance(e);
    }

    private void emitNewInstanceMultiarg($ClassEmitter ce, List constructors) {
        final $CodeEmitter e = ce.begin_method(1, MULTIARG_NEW_INSTANCE, null);
        e.load_arg(2);
        e.invoke_static_this(SET_THREAD_CALLBACKS);
        e.new_instance_this();
        e.dup();
        e.load_arg(0);
        $EmitUtils.constructor_switch(e, constructors, new $ObjectSwitchCallback(){

            public void processCase(Object key, $Label end) {
                $MethodInfo constructor = ($MethodInfo)key;
                $Type[] types = constructor.getSignature().getArgumentTypes();
                for (int i = 0; i < types.length; ++i) {
                    e.load_arg(1);
                    e.push(i);
                    e.aaload();
                    e.unbox(types[i]);
                }
                e.invoke_constructor_this(constructor.getSignature());
                e.goTo(end);
            }

            public void processDefault() {
                e.throw_exception(ILLEGAL_ARGUMENT_EXCEPTION, "Constructor not found");
            }
        });
        e.aconst_null();
        e.invoke_static_this(SET_THREAD_CALLBACKS);
        e.return_value();
        e.end_method();
    }

    private void emitMethods($ClassEmitter ce, List methods, List actualMethods) {
        Iterator it2;
        $CallbackGenerator[] generators = $CallbackInfo.getGenerators(this.callbackTypes);
        HashMap<$CallbackGenerator, ArrayList<$MethodInfo>> groups = new HashMap<$CallbackGenerator, ArrayList<$MethodInfo>>();
        final HashMap<$MethodInfo, Integer> indexes = new HashMap<$MethodInfo, Integer>();
        final HashMap<$MethodInfo, Integer> originalModifiers = new HashMap<$MethodInfo, Integer>();
        final Map positions = $CollectionUtils.getIndexMap(methods);
        Iterator it1 = methods.iterator();
        Iterator iterator = it2 = actualMethods != null ? actualMethods.iterator() : null;
        while (it1.hasNext()) {
            $MethodInfo method = ($MethodInfo)it1.next();
            Method actualMethod = it2 != null ? (Method)it2.next() : null;
            int index = this.filter.accept(actualMethod);
            if (index >= this.callbackTypes.length) {
                throw new IllegalArgumentException("Callback filter returned an index that is too large: " + index);
            }
            originalModifiers.put(method, new Integer(actualMethod != null ? actualMethod.getModifiers() : method.getModifiers()));
            indexes.put(method, new Integer(index));
            ArrayList<$MethodInfo> group = (ArrayList<$MethodInfo>)groups.get(generators[index]);
            if (group == null) {
                group = new ArrayList<$MethodInfo>(methods.size());
                groups.put(generators[index], group);
            }
            group.add(method);
        }
        HashSet<$CallbackGenerator> seenGen = new HashSet<$CallbackGenerator>();
        $CodeEmitter se = ce.getStaticHook();
        se.new_instance(THREAD_LOCAL);
        se.dup();
        se.invoke_constructor(THREAD_LOCAL, CSTRUCT_NULL);
        se.putfield(THREAD_CALLBACKS_FIELD);
        Object[] state = new Object[1];
        $CallbackGenerator.Context context = new $CallbackGenerator.Context(){

            public ClassLoader getClassLoader() {
                return $Enhancer.this.getClassLoader();
            }

            public int getOriginalModifiers($MethodInfo method) {
                return (Integer)originalModifiers.get(method);
            }

            public int getIndex($MethodInfo method) {
                return (Integer)indexes.get(method);
            }

            public void emitCallback($CodeEmitter e, int index) {
                $Enhancer.this.emitCurrentCallback(e, index);
            }

            public $Signature getImplSignature($MethodInfo method) {
                return $Enhancer.this.rename(method.getSignature(), (Integer)positions.get(method));
            }

            public $CodeEmitter beginMethod($ClassEmitter ce, $MethodInfo method) {
                $CodeEmitter e = $EmitUtils.begin_method(ce, method);
                if (!$Enhancer.this.interceptDuringConstruction && !$TypeUtils.isAbstract(method.getModifiers())) {
                    $Label constructed = e.make_label();
                    e.load_this();
                    e.getfield($Enhancer.CONSTRUCTED_FIELD);
                    e.if_jump(154, constructed);
                    e.load_this();
                    e.load_args();
                    e.super_invoke();
                    e.return_value();
                    e.mark(constructed);
                }
                return e;
            }
        };
        for (int i = 0; i < this.callbackTypes.length; ++i) {
            $CallbackGenerator gen = generators[i];
            if (seenGen.contains(gen)) continue;
            seenGen.add(gen);
            List fmethods = (List)groups.get(gen);
            if (fmethods == null) continue;
            try {
                gen.generate(ce, context, fmethods);
                gen.generateStatic(se, context, fmethods);
                continue;
            }
            catch (RuntimeException x) {
                throw x;
            }
            catch (Exception x) {
                throw new $CodeGenerationException(x);
            }
        }
        se.return_value();
        se.end_method();
    }

    private void emitSetThreadCallbacks($ClassEmitter ce) {
        $CodeEmitter e = ce.begin_method(9, SET_THREAD_CALLBACKS, null);
        e.getfield(THREAD_CALLBACKS_FIELD);
        e.load_arg(0);
        e.invoke_virtual(THREAD_LOCAL, THREAD_LOCAL_SET);
        e.return_value();
        e.end_method();
    }

    private void emitSetStaticCallbacks($ClassEmitter ce) {
        $CodeEmitter e = ce.begin_method(9, SET_STATIC_CALLBACKS, null);
        e.load_arg(0);
        e.putfield(STATIC_CALLBACKS_FIELD);
        e.return_value();
        e.end_method();
    }

    private void emitCurrentCallback($CodeEmitter e, int index) {
        e.load_this();
        e.getfield($Enhancer.getCallbackField(index));
        e.dup();
        $Label end = e.make_label();
        e.ifnonnull(end);
        e.pop();
        e.load_this();
        e.invoke_static_this(BIND_CALLBACKS);
        e.load_this();
        e.getfield($Enhancer.getCallbackField(index));
        e.mark(end);
    }

    private void emitBindCallbacks($ClassEmitter ce) {
        $CodeEmitter e = ce.begin_method(26, BIND_CALLBACKS, null);
        $Local me = e.make_local();
        e.load_arg(0);
        e.checkcast_this();
        e.store_local(me);
        $Label end = e.make_label();
        e.load_local(me);
        e.getfield(BOUND_FIELD);
        e.if_jump(154, end);
        e.load_local(me);
        e.push(1);
        e.putfield(BOUND_FIELD);
        e.getfield(THREAD_CALLBACKS_FIELD);
        e.invoke_virtual(THREAD_LOCAL, THREAD_LOCAL_GET);
        e.dup();
        $Label found_callback = e.make_label();
        e.ifnonnull(found_callback);
        e.pop();
        e.getfield(STATIC_CALLBACKS_FIELD);
        e.dup();
        e.ifnonnull(found_callback);
        e.pop();
        e.goTo(end);
        e.mark(found_callback);
        e.checkcast(CALLBACK_ARRAY);
        e.load_local(me);
        e.swap();
        for (int i = this.callbackTypes.length - 1; i >= 0; --i) {
            if (i != 0) {
                e.dup2();
            }
            e.aaload(i);
            e.checkcast(this.callbackTypes[i]);
            e.putfield($Enhancer.getCallbackField(i));
        }
        e.mark(end);
        e.return_value();
        e.end_method();
    }

    private static String getCallbackField(int index) {
        return "CGLIB$CALLBACK_" + index;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public static interface EnhancerKey {
        public Object newInstance(String var1, String[] var2, $CallbackFilter var3, $Type[] var4, boolean var5, boolean var6, Long var7);
    }
}

