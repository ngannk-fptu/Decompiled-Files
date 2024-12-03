/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.mozilla.javascript.AccessorSlot;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.ConstProperties;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Delegator;
import org.mozilla.javascript.ExternalArrayData;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.LambdaSlot;
import org.mozilla.javascript.LazilyLoadedCtor;
import org.mozilla.javascript.LazyLoadSlot;
import org.mozilla.javascript.MemberBox;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ObjToIntMap;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Slot;
import org.mozilla.javascript.SlotMapContainer;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolScriptable;
import org.mozilla.javascript.ThreadSafeSlotMapContainer;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;
import org.mozilla.javascript.annotations.JSSetter;
import org.mozilla.javascript.annotations.JSStaticFunction;
import org.mozilla.javascript.debug.DebuggableObject;

public abstract class ScriptableObject
implements Scriptable,
SymbolScriptable,
Serializable,
DebuggableObject,
ConstProperties {
    private static final long serialVersionUID = 2829861078851942586L;
    public static final int EMPTY = 0;
    public static final int READONLY = 1;
    public static final int DONTENUM = 2;
    public static final int PERMANENT = 4;
    public static final int UNINITIALIZED_CONST = 8;
    public static final int CONST = 13;
    private Scriptable prototypeObject;
    private Scriptable parentScopeObject;
    private transient SlotMapContainer slotMap;
    private transient ExternalArrayData externalData;
    private volatile Map<Object, Object> associatedValues;
    private boolean isExtensible = true;
    private boolean isSealed = false;
    private static final Method GET_ARRAY_LENGTH;
    private static final Comparator<Object> KEY_COMPARATOR;

    protected static ScriptableObject buildDataDescriptor(Scriptable scope, Object value, int attributes) {
        NativeObject desc = new NativeObject();
        ScriptRuntime.setBuiltinProtoAndParent(desc, scope, TopLevel.Builtins.Object);
        desc.defineProperty("value", value, 0);
        desc.setCommonDescriptorProperties(attributes, true);
        return desc;
    }

    protected void setCommonDescriptorProperties(int attributes, boolean defineWritable) {
        if (defineWritable) {
            this.defineProperty("writable", (Object)((attributes & 1) == 0 ? 1 : 0), 0);
        }
        this.defineProperty("enumerable", (Object)((attributes & 2) == 0 ? 1 : 0), 0);
        this.defineProperty("configurable", (Object)((attributes & 4) == 0 ? 1 : 0), 0);
    }

    static void checkValidAttributes(int attributes) {
        int mask = 15;
        if ((attributes & 0xFFFFFFF0) != 0) {
            throw new IllegalArgumentException(String.valueOf(attributes));
        }
    }

    private static SlotMapContainer createSlotMap(int initialSize) {
        Context cx = Context.getCurrentContext();
        if (cx != null && cx.hasFeature(17)) {
            return new ThreadSafeSlotMapContainer(initialSize);
        }
        return new SlotMapContainer(initialSize);
    }

    public ScriptableObject() {
        this.slotMap = ScriptableObject.createSlotMap(0);
    }

    public ScriptableObject(Scriptable scope, Scriptable prototype) {
        if (scope == null) {
            throw new IllegalArgumentException();
        }
        this.parentScopeObject = scope;
        this.prototypeObject = prototype;
        this.slotMap = ScriptableObject.createSlotMap(0);
    }

    public String getTypeOf() {
        return this.avoidObjectDetection() ? "undefined" : "object";
    }

    @Override
    public abstract String getClassName();

    @Override
    public boolean has(String name, Scriptable start) {
        return null != this.slotMap.query(name, 0);
    }

    @Override
    public boolean has(int index, Scriptable start) {
        if (this.externalData != null) {
            return index < this.externalData.getArrayLength();
        }
        return null != this.slotMap.query(null, index);
    }

    @Override
    public boolean has(Symbol key, Scriptable start) {
        return null != this.slotMap.query(key, 0);
    }

    @Override
    public Object get(String name, Scriptable start) {
        Slot slot = this.slotMap.query(name, 0);
        if (slot == null) {
            return Scriptable.NOT_FOUND;
        }
        return slot.getValue(start);
    }

    @Override
    public Object get(int index, Scriptable start) {
        if (this.externalData != null) {
            if (index < this.externalData.getArrayLength()) {
                return this.externalData.getArrayElement(index);
            }
            return Scriptable.NOT_FOUND;
        }
        Slot slot = this.slotMap.query(null, index);
        if (slot == null) {
            return Scriptable.NOT_FOUND;
        }
        return slot.getValue(start);
    }

    @Override
    public Object get(Symbol key, Scriptable start) {
        Slot slot = this.slotMap.query(key, 0);
        if (slot == null) {
            return Scriptable.NOT_FOUND;
        }
        return slot.getValue(start);
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        if (this.putImpl(name, 0, start, value)) {
            return;
        }
        if (start == this) {
            throw Kit.codeBug();
        }
        start.put(name, start, value);
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        if (this.externalData != null) {
            if (index >= this.externalData.getArrayLength()) {
                throw new JavaScriptException(ScriptRuntime.newNativeError(Context.getCurrentContext(), this, TopLevel.NativeErrors.RangeError, new Object[]{"External array index out of bounds "}), null, 0);
            }
            this.externalData.setArrayElement(index, value);
            return;
        }
        if (this.putImpl(null, index, start, value)) {
            return;
        }
        if (start == this) {
            throw Kit.codeBug();
        }
        start.put(index, start, value);
    }

    @Override
    public void put(Symbol key, Scriptable start, Object value) {
        if (this.putImpl(key, 0, start, value)) {
            return;
        }
        if (start == this) {
            throw Kit.codeBug();
        }
        ScriptableObject.ensureSymbolScriptable(start).put(key, start, value);
    }

    @Override
    public void delete(String name) {
        this.checkNotSealed(name, 0);
        this.slotMap.remove(name, 0);
    }

    @Override
    public void delete(int index) {
        this.checkNotSealed(null, index);
        this.slotMap.remove(null, index);
    }

    @Override
    public void delete(Symbol key) {
        this.checkNotSealed(key, 0);
        this.slotMap.remove(key, 0);
    }

    @Override
    public void putConst(String name, Scriptable start, Object value) {
        if (this.putConstImpl(name, 0, start, value, 1)) {
            return;
        }
        if (start == this) {
            throw Kit.codeBug();
        }
        if (start instanceof ConstProperties) {
            ((ConstProperties)((Object)start)).putConst(name, start, value);
        } else {
            start.put(name, start, value);
        }
    }

    @Override
    public void defineConst(String name, Scriptable start) {
        if (this.putConstImpl(name, 0, start, Undefined.instance, 8)) {
            return;
        }
        if (start == this) {
            throw Kit.codeBug();
        }
        if (start instanceof ConstProperties) {
            ((ConstProperties)((Object)start)).defineConst(name, start);
        }
    }

    @Override
    public boolean isConst(String name) {
        Slot slot = this.slotMap.query(name, 0);
        if (slot == null) {
            return false;
        }
        return (slot.getAttributes() & 5) == 5;
    }

    @Deprecated
    public final int getAttributes(String name, Scriptable start) {
        return this.getAttributes(name);
    }

    @Deprecated
    public final int getAttributes(int index, Scriptable start) {
        return this.getAttributes(index);
    }

    @Deprecated
    public final void setAttributes(String name, Scriptable start, int attributes) {
        this.setAttributes(name, attributes);
    }

    @Deprecated
    public void setAttributes(int index, Scriptable start, int attributes) {
        this.setAttributes(index, attributes);
    }

    public int getAttributes(String name) {
        return this.getAttributeSlot(name, 0).getAttributes();
    }

    public int getAttributes(int index) {
        return this.getAttributeSlot(null, index).getAttributes();
    }

    public int getAttributes(Symbol sym) {
        return this.getAttributeSlot(sym).getAttributes();
    }

    public void setAttributes(String name, int attributes) {
        this.checkNotSealed(name, 0);
        Slot attrSlot = this.slotMap.modify(name, 0, 0);
        attrSlot.setAttributes(attributes);
    }

    public void setAttributes(int index, int attributes) {
        this.checkNotSealed(null, index);
        Slot attrSlot = this.slotMap.modify(null, index, 0);
        attrSlot.setAttributes(attributes);
    }

    public void setAttributes(Symbol key, int attributes) {
        this.checkNotSealed(key, 0);
        Slot attrSlot = this.slotMap.modify(key, 0, 0);
        attrSlot.setAttributes(attributes);
    }

    public void setGetterOrSetter(String name, int index, Callable getterOrSetter, boolean isSetter) {
        AccessorSlot aSlot;
        Slot slot;
        if (name != null && index != 0) {
            throw new IllegalArgumentException(name);
        }
        this.checkNotSealed(name, index);
        if (this.isExtensible()) {
            slot = this.slotMap.modify(name, index, 0);
            if (slot instanceof AccessorSlot) {
                aSlot = (AccessorSlot)slot;
            } else {
                aSlot = new AccessorSlot(slot);
                this.slotMap.replace(slot, aSlot);
            }
        } else {
            slot = this.slotMap.query(name, index);
            if (slot instanceof AccessorSlot) {
                aSlot = (AccessorSlot)slot;
            } else {
                return;
            }
        }
        int attributes = aSlot.getAttributes();
        if ((attributes & 1) != 0) {
            throw Context.reportRuntimeErrorById("msg.modify.readonly", name);
        }
        if (isSetter) {
            aSlot.setter = getterOrSetter instanceof Function ? new AccessorSlot.FunctionSetter(getterOrSetter) : null;
        } else {
            aSlot.getter = getterOrSetter instanceof Function ? new AccessorSlot.FunctionGetter(getterOrSetter) : null;
        }
        aSlot.value = Undefined.instance;
    }

    public Object getGetterOrSetter(String name, int index, Scriptable scope, boolean isSetter) {
        if (name != null && index != 0) {
            throw new IllegalArgumentException(name);
        }
        Slot slot = this.slotMap.query(name, index);
        if (slot == null) {
            return null;
        }
        Function getterOrSetter = isSetter ? slot.getSetterFunction(name, scope) : slot.getGetterFunction(name, scope);
        return getterOrSetter == null ? Undefined.instance : getterOrSetter;
    }

    @Deprecated
    public Object getGetterOrSetter(String name, int index, boolean isSetter) {
        return this.getGetterOrSetter(name, index, this, isSetter);
    }

    protected boolean isGetterOrSetter(String name, int index, boolean setter) {
        Slot slot = this.slotMap.query(name, index);
        return slot != null && slot.isSetterSlot();
    }

    void addLazilyInitializedValue(String name, int index, LazilyLoadedCtor init, int attributes) {
        LazyLoadSlot lslot;
        if (name != null && index != 0) {
            throw new IllegalArgumentException(name);
        }
        this.checkNotSealed(name, index);
        Slot slot = this.slotMap.modify(name, index, 0);
        if (slot instanceof LazyLoadSlot) {
            lslot = (LazyLoadSlot)slot;
        } else {
            lslot = new LazyLoadSlot(slot);
            this.slotMap.replace(slot, lslot);
        }
        lslot.setAttributes(attributes);
        lslot.value = init;
    }

    public void setExternalArrayData(ExternalArrayData array) {
        this.externalData = array;
        if (array == null) {
            this.delete("length");
        } else {
            this.defineProperty("length", null, GET_ARRAY_LENGTH, null, 3);
        }
    }

    public ExternalArrayData getExternalArrayData() {
        return this.externalData;
    }

    public Object getExternalArrayLength() {
        return this.externalData == null ? 0 : this.externalData.getArrayLength();
    }

    @Override
    public Scriptable getPrototype() {
        return this.prototypeObject;
    }

    @Override
    public void setPrototype(Scriptable m) {
        this.prototypeObject = m;
    }

    @Override
    public Scriptable getParentScope() {
        return this.parentScopeObject;
    }

    @Override
    public void setParentScope(Scriptable m) {
        this.parentScopeObject = m;
    }

    @Override
    public Object[] getIds() {
        return this.getIds(false, false);
    }

    @Override
    public Object[] getAllIds() {
        return this.getIds(true, false);
    }

    @Override
    public Object getDefaultValue(Class<?> typeHint) {
        return ScriptableObject.getDefaultValue(this, typeHint);
    }

    public static Object getDefaultValue(Scriptable object, Class<?> typeHint) {
        Context cx = null;
        for (int i = 0; i < 2; ++i) {
            Object u;
            boolean tryToString = typeHint == ScriptRuntime.StringClass ? i == 0 : i == 1;
            String methodName = tryToString ? "toString" : "valueOf";
            Object v = ScriptableObject.getProperty(object, methodName);
            if (!(v instanceof Function)) continue;
            Function fun = (Function)v;
            if (cx == null) {
                cx = Context.getContext();
            }
            if ((v = fun.call(cx, fun.getParentScope(), object, ScriptRuntime.emptyArgs)) == null) continue;
            if (!(v instanceof Scriptable)) {
                return v;
            }
            if (typeHint == ScriptRuntime.ScriptableClass || typeHint == ScriptRuntime.FunctionClass) {
                return v;
            }
            if (!tryToString || !(v instanceof Wrapper) || !((u = ((Wrapper)v).unwrap()) instanceof String)) continue;
            return u;
        }
        String arg = typeHint == null ? "undefined" : typeHint.getName();
        throw ScriptRuntime.typeErrorById("msg.default.value", arg);
    }

    @Override
    public boolean hasInstance(Scriptable instance) {
        return ScriptRuntime.jsDelegatesTo(instance, this);
    }

    public boolean avoidObjectDetection() {
        return false;
    }

    protected Object equivalentValues(Object value) {
        return this == value ? Boolean.TRUE : Scriptable.NOT_FOUND;
    }

    public static <T extends Scriptable> void defineClass(Scriptable scope, Class<T> clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        ScriptableObject.defineClass(scope, clazz, false, false);
    }

    public static <T extends Scriptable> void defineClass(Scriptable scope, Class<T> clazz, boolean sealed) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        ScriptableObject.defineClass(scope, clazz, sealed, false);
    }

    public static <T extends Scriptable> String defineClass(Scriptable scope, Class<T> clazz, boolean sealed, boolean mapInheritance) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        BaseFunction ctor = ScriptableObject.buildClassCtor(scope, clazz, sealed, mapInheritance);
        if (ctor == null) {
            return null;
        }
        String name = ctor.getClassPrototype().getClassName();
        ScriptableObject.defineProperty(scope, name, ctor, 2);
        return name;
    }

    static <T extends Scriptable> BaseFunction buildClassCtor(Scriptable scope, Class<T> clazz, boolean sealed, boolean mapInheritance) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        FunctionObject ctor;
        Class<T> superScriptable;
        String name;
        Class<T> superClass;
        Object existingProto;
        AccessibleObject[] methods = FunctionObject.getMethodList(clazz);
        for (int i = 0; i < methods.length; ++i) {
            Method method = methods[i];
            if (!method.getName().equals("init")) continue;
            Class<?>[] parmTypes = method.getParameterTypes();
            if (parmTypes.length == 3 && parmTypes[0] == ScriptRuntime.ContextClass && parmTypes[1] == ScriptRuntime.ScriptableClass && parmTypes[2] == Boolean.TYPE && Modifier.isStatic(method.getModifiers())) {
                Object[] args = new Object[]{Context.getContext(), scope, sealed ? Boolean.TRUE : Boolean.FALSE};
                method.invoke(null, args);
                return null;
            }
            if (parmTypes.length != 1 || parmTypes[0] != ScriptRuntime.ScriptableClass || !Modifier.isStatic(method.getModifiers())) continue;
            Object[] args = new Object[]{scope};
            method.invoke(null, args);
            return null;
        }
        AccessibleObject[] ctors = clazz.getConstructors();
        Constructor<?> protoCtor = null;
        for (int i = 0; i < ctors.length; ++i) {
            if (ctors[i].getParameterTypes().length != 0) continue;
            protoCtor = ctors[i];
            break;
        }
        if (protoCtor == null) {
            throw Context.reportRuntimeErrorById("msg.zero.arg.ctor", clazz.getName());
        }
        Scriptable proto = (Scriptable)protoCtor.newInstance(ScriptRuntime.emptyArgs);
        String className = proto.getClassName();
        Object existing = ScriptableObject.getProperty(ScriptableObject.getTopLevelScope(scope), className);
        if (existing instanceof BaseFunction && (existingProto = ((BaseFunction)existing).getPrototypeProperty()) != null && clazz.equals(existingProto.getClass())) {
            return (BaseFunction)existing;
        }
        Scriptable superProto = null;
        if (mapInheritance && ScriptRuntime.ScriptableClass.isAssignableFrom(superClass = clazz.getSuperclass()) && !Modifier.isAbstract(superClass.getModifiers()) && (name = ScriptableObject.defineClass(scope, superScriptable = ScriptableObject.extendsScriptable(superClass), sealed, mapInheritance)) != null) {
            superProto = ScriptableObject.getClassPrototype(scope, name);
        }
        if (superProto == null) {
            superProto = ScriptableObject.getObjectPrototype(scope);
        }
        proto.setPrototype(superProto);
        String functionPrefix = "jsFunction_";
        String staticFunctionPrefix = "jsStaticFunction_";
        String getterPrefix = "jsGet_";
        String setterPrefix = "jsSet_";
        String ctorName = "jsConstructor";
        Object ctorMember = ScriptableObject.findAnnotatedMember(methods, JSConstructor.class);
        if (ctorMember == null) {
            ctorMember = ScriptableObject.findAnnotatedMember(ctors, JSConstructor.class);
        }
        if (ctorMember == null) {
            ctorMember = FunctionObject.findSingleMethod((Method[])methods, "jsConstructor");
        }
        if (ctorMember == null) {
            if (ctors.length == 1) {
                ctorMember = ctors[0];
            } else if (ctors.length == 2) {
                if (((Constructor)ctors[0]).getParameterTypes().length == 0) {
                    ctorMember = ctors[1];
                } else if (((Constructor)ctors[1]).getParameterTypes().length == 0) {
                    ctorMember = ctors[0];
                }
            }
            if (ctorMember == null) {
                throw Context.reportRuntimeErrorById("msg.ctor.multiple.parms", clazz.getName());
            }
        }
        if ((ctor = new FunctionObject(className, (Member)ctorMember, scope)).isVarArgsMethod()) {
            throw Context.reportRuntimeErrorById("msg.varargs.ctor", ctorMember.getName());
        }
        ctor.initAsConstructor(scope, proto);
        AccessibleObject finishInit = null;
        HashSet<String> staticNames = new HashSet<String>();
        HashSet instanceNames = new HashSet();
        for (AccessibleObject method : methods) {
            String propName;
            boolean isStatic;
            HashSet<String> names;
            Class<?>[] parmTypes;
            if (method == ctorMember) continue;
            String name2 = ((Method)method).getName();
            if (name2.equals("finishInit") && (parmTypes = ((Method)method).getParameterTypes()).length == 3 && parmTypes[0] == ScriptRuntime.ScriptableClass && parmTypes[1] == FunctionObject.class && parmTypes[2] == ScriptRuntime.ScriptableClass && Modifier.isStatic(((Method)method).getModifiers())) {
                finishInit = method;
                continue;
            }
            if (name2.indexOf(36) != -1 || name2.equals("jsConstructor")) continue;
            Annotation annotation = null;
            String prefix = null;
            if (method.isAnnotationPresent(JSFunction.class)) {
                annotation = ((Method)method).getAnnotation(JSFunction.class);
            } else if (method.isAnnotationPresent(JSStaticFunction.class)) {
                annotation = ((Method)method).getAnnotation(JSStaticFunction.class);
            } else if (method.isAnnotationPresent(JSGetter.class)) {
                annotation = ((Method)method).getAnnotation(JSGetter.class);
            } else if (method.isAnnotationPresent(JSSetter.class)) continue;
            if (annotation == null) {
                if (name2.startsWith("jsFunction_")) {
                    prefix = "jsFunction_";
                } else if (name2.startsWith("jsStaticFunction_")) {
                    prefix = "jsStaticFunction_";
                } else {
                    if (!name2.startsWith("jsGet_")) continue;
                    prefix = "jsGet_";
                }
            }
            if ((names = (isStatic = annotation instanceof JSStaticFunction || prefix == "jsStaticFunction_") ? staticNames : instanceNames).contains(propName = ScriptableObject.getPropertyName(name2, prefix, annotation))) {
                throw Context.reportRuntimeErrorById("duplicate.defineClass.name", name2, propName);
            }
            names.add(propName);
            name2 = propName;
            if (annotation instanceof JSGetter || prefix == "jsGet_") {
                if (!(proto instanceof ScriptableObject)) {
                    throw Context.reportRuntimeErrorById("msg.extend.scriptable", proto.getClass().toString(), name2);
                }
                Method setter = ScriptableObject.findSetterMethod((Method[])methods, name2, "jsSet_");
                int attr = 6 | (setter != null ? 0 : 1);
                ((ScriptableObject)proto).defineProperty(name2, null, (Method)method, setter, attr);
                continue;
            }
            if (isStatic && !Modifier.isStatic(((Method)method).getModifiers())) {
                throw Context.reportRuntimeError("jsStaticFunction must be used with static method.");
            }
            FunctionObject f = new FunctionObject(name2, (Member)((Object)method), proto);
            if (f.isVarArgsConstructor()) {
                throw Context.reportRuntimeErrorById("msg.varargs.fun", ctorMember.getName());
            }
            ScriptableObject.defineProperty(isStatic ? ctor : proto, name2, f, 2);
            if (!sealed) continue;
            f.sealObject();
        }
        if (finishInit != null) {
            Object[] finishArgs = new Object[]{scope, ctor, proto};
            finishInit.invoke(null, finishArgs);
        }
        if (sealed) {
            ctor.sealObject();
            if (proto instanceof ScriptableObject) {
                ((ScriptableObject)proto).sealObject();
            }
        }
        return ctor;
    }

    private static Member findAnnotatedMember(AccessibleObject[] members, Class<? extends Annotation> annotation) {
        for (AccessibleObject member : members) {
            if (!member.isAnnotationPresent(annotation)) continue;
            return (Member)((Object)member);
        }
        return null;
    }

    private static Method findSetterMethod(Method[] methods, String name, String prefix) {
        String newStyleName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        for (Method method : methods) {
            JSSetter annotation = method.getAnnotation(JSSetter.class);
            if (annotation == null || !name.equals(annotation.value()) && (!"".equals(annotation.value()) || !newStyleName.equals(method.getName()))) continue;
            return method;
        }
        String oldStyleName = prefix + name;
        for (Method method : methods) {
            if (!oldStyleName.equals(method.getName())) continue;
            return method;
        }
        return null;
    }

    private static String getPropertyName(String methodName, String prefix, Annotation annotation) {
        if (prefix != null) {
            return methodName.substring(prefix.length());
        }
        String propName = null;
        if (annotation instanceof JSGetter) {
            propName = ((JSGetter)annotation).value();
            if ((propName == null || propName.length() == 0) && methodName.length() > 3 && methodName.startsWith("get") && Character.isUpperCase((propName = methodName.substring(3)).charAt(0))) {
                if (propName.length() == 1) {
                    propName = propName.toLowerCase();
                } else if (!Character.isUpperCase(propName.charAt(1))) {
                    propName = Character.toLowerCase(propName.charAt(0)) + propName.substring(1);
                }
            }
        } else if (annotation instanceof JSFunction) {
            propName = ((JSFunction)annotation).value();
        } else if (annotation instanceof JSStaticFunction) {
            propName = ((JSStaticFunction)annotation).value();
        }
        if (propName == null || propName.length() == 0) {
            propName = methodName;
        }
        return propName;
    }

    private static <T extends Scriptable> Class<T> extendsScriptable(Class<?> c) {
        if (ScriptRuntime.ScriptableClass.isAssignableFrom(c)) {
            return c;
        }
        return null;
    }

    public void defineProperty(String propertyName, Object value, int attributes) {
        this.checkNotSealed(propertyName, 0);
        this.put(propertyName, (Scriptable)this, value);
        this.setAttributes(propertyName, attributes);
    }

    public void defineProperty(Symbol key, Object value, int attributes) {
        this.checkNotSealed(key, 0);
        this.put(key, (Scriptable)this, value);
        this.setAttributes(key, attributes);
    }

    public static void defineProperty(Scriptable destination, String propertyName, Object value, int attributes) {
        if (!(destination instanceof ScriptableObject)) {
            destination.put(propertyName, destination, value);
            return;
        }
        ScriptableObject so = (ScriptableObject)destination;
        so.defineProperty(propertyName, value, attributes);
    }

    public static void defineConstProperty(Scriptable destination, String propertyName) {
        if (destination instanceof ConstProperties) {
            ConstProperties cp = (ConstProperties)((Object)destination);
            cp.defineConst(propertyName, destination);
        } else {
            ScriptableObject.defineProperty(destination, propertyName, Undefined.instance, 13);
        }
    }

    public void defineProperty(String propertyName, Class<?> clazz, int attributes) {
        int length = propertyName.length();
        if (length == 0) {
            throw new IllegalArgumentException();
        }
        char[] buf = new char[3 + length];
        propertyName.getChars(0, length, buf, 3);
        buf[3] = Character.toUpperCase(buf[3]);
        buf[0] = 103;
        buf[1] = 101;
        buf[2] = 116;
        String getterName = new String(buf);
        buf[0] = 115;
        String setterName = new String(buf);
        Method[] methods = FunctionObject.getMethodList(clazz);
        Method getter = FunctionObject.findSingleMethod(methods, getterName);
        Method setter = FunctionObject.findSingleMethod(methods, setterName);
        if (setter == null) {
            attributes |= 1;
        }
        this.defineProperty(propertyName, null, getter, setter == null ? null : setter, attributes);
    }

    public void defineProperty(String propertyName, Object delegateTo, Method getter, Method setter, int attributes) {
        AccessorSlot aSlot;
        Slot slot;
        MemberBox getterBox = null;
        if (getter != null) {
            boolean delegatedForm;
            getterBox = new MemberBox(getter);
            if (!Modifier.isStatic(getter.getModifiers())) {
                delegatedForm = delegateTo != null;
                getterBox.delegateTo = delegateTo;
            } else {
                delegatedForm = true;
                getterBox.delegateTo = Void.TYPE;
            }
            String errorId = null;
            Class<?>[] parmTypes = getter.getParameterTypes();
            if (parmTypes.length == 0) {
                if (delegatedForm) {
                    errorId = "msg.obj.getter.parms";
                }
            } else if (parmTypes.length == 1) {
                Class<?> argType = parmTypes[0];
                if (argType != ScriptRuntime.ScriptableClass && argType != ScriptRuntime.ScriptableObjectClass) {
                    errorId = "msg.bad.getter.parms";
                } else if (!delegatedForm) {
                    errorId = "msg.bad.getter.parms";
                }
            } else {
                errorId = "msg.bad.getter.parms";
            }
            if (errorId != null) {
                throw Context.reportRuntimeErrorById(errorId, getter.toString());
            }
        }
        MemberBox setterBox = null;
        if (setter != null) {
            boolean delegatedForm;
            if (setter.getReturnType() != Void.TYPE) {
                throw Context.reportRuntimeErrorById("msg.setter.return", setter.toString());
            }
            setterBox = new MemberBox(setter);
            if (!Modifier.isStatic(setter.getModifiers())) {
                delegatedForm = delegateTo != null;
                setterBox.delegateTo = delegateTo;
            } else {
                delegatedForm = true;
                setterBox.delegateTo = Void.TYPE;
            }
            String errorId = null;
            Class<?>[] parmTypes = setter.getParameterTypes();
            if (parmTypes.length == 1) {
                if (delegatedForm) {
                    errorId = "msg.setter2.expected";
                }
            } else if (parmTypes.length == 2) {
                Class<?> argType = parmTypes[0];
                if (argType != ScriptRuntime.ScriptableClass && argType != ScriptRuntime.ScriptableObjectClass) {
                    errorId = "msg.setter2.parms";
                } else if (!delegatedForm) {
                    errorId = "msg.setter1.parms";
                }
            } else {
                errorId = "msg.setter.parms";
            }
            if (errorId != null) {
                throw Context.reportRuntimeErrorById(errorId, setter.toString());
            }
        }
        if ((slot = this.slotMap.modify(propertyName, 0, 0)) instanceof AccessorSlot) {
            aSlot = (AccessorSlot)slot;
        } else {
            aSlot = new AccessorSlot(slot);
            this.slotMap.replace(slot, aSlot);
        }
        aSlot.setAttributes(attributes);
        if (getterBox != null) {
            aSlot.getter = new AccessorSlot.MemberBoxGetter(getterBox);
        }
        if (setterBox != null) {
            aSlot.setter = new AccessorSlot.MemberBoxSetter(setterBox);
        }
    }

    public void defineOwnProperties(Context cx, ScriptableObject props) {
        int i;
        Object[] ids = props.getIds(false, true);
        ScriptableObject[] descs = new ScriptableObject[ids.length];
        int len = ids.length;
        for (i = 0; i < len; ++i) {
            Object descObj = ScriptRuntime.getObjectElem(props, ids[i], cx);
            ScriptableObject desc = ScriptableObject.ensureScriptableObject(descObj);
            this.checkPropertyDefinition(desc);
            descs[i] = desc;
        }
        len = ids.length;
        for (i = 0; i < len; ++i) {
            this.defineOwnProperty(cx, ids[i], descs[i]);
        }
    }

    public void defineOwnProperty(Context cx, Object id, ScriptableObject desc) {
        this.checkPropertyDefinition(desc);
        this.defineOwnProperty(cx, id, desc, true);
    }

    protected void defineOwnProperty(Context cx, Object id, ScriptableObject desc, boolean checkValid) {
        int attributes;
        boolean isNew;
        Object key = null;
        int index = 0;
        if (id instanceof Symbol) {
            key = id;
        } else {
            ScriptRuntime.StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, id);
            if (s.stringId == null) {
                index = s.index;
            } else {
                key = s.stringId;
            }
        }
        Slot slot = this.slotMap.query(key, index);
        boolean bl = isNew = slot == null;
        if (checkValid) {
            ScriptableObject current = slot == null ? null : slot.getPropertyDescriptor(cx, this);
            this.checkPropertyChange(id, current, desc);
        }
        boolean isAccessor = this.isAccessorDescriptor(desc);
        if (slot == null) {
            slot = this.slotMap.modify(key, index, 0);
            attributes = this.applyDescriptorToAttributeBitset(7, desc);
        } else {
            attributes = this.applyDescriptorToAttributeBitset(slot.getAttributes(), desc);
        }
        if (isAccessor) {
            Object setter;
            AccessorSlot fslot;
            if (slot instanceof AccessorSlot) {
                fslot = (AccessorSlot)slot;
            } else {
                fslot = new AccessorSlot(slot);
                this.slotMap.replace(slot, fslot);
            }
            Object getter = ScriptableObject.getProperty((Scriptable)desc, "get");
            if (getter != NOT_FOUND) {
                fslot.getter = new AccessorSlot.FunctionGetter(getter);
            }
            if ((setter = ScriptableObject.getProperty((Scriptable)desc, "set")) != NOT_FOUND) {
                fslot.setter = new AccessorSlot.FunctionSetter(setter);
            }
            fslot.value = Undefined.instance;
            fslot.setAttributes(attributes);
        } else {
            Object value;
            if (!slot.isValueSlot() && this.isDataDescriptor(desc)) {
                Slot newSlot = new Slot(slot);
                this.slotMap.replace(slot, newSlot);
                slot = newSlot;
            }
            if ((value = ScriptableObject.getProperty((Scriptable)desc, "value")) != NOT_FOUND) {
                slot.value = value;
            } else if (isNew) {
                slot.value = Undefined.instance;
            }
            slot.setAttributes(attributes);
        }
    }

    public void defineProperty(String name, Supplier<Object> getter, Consumer<Object> setter, int attributes) {
        LambdaSlot lSlot;
        Slot slot = this.slotMap.modify(name, 0, attributes);
        if (slot instanceof LambdaSlot) {
            lSlot = (LambdaSlot)slot;
        } else {
            lSlot = new LambdaSlot(slot);
            this.slotMap.replace(slot, lSlot);
        }
        lSlot.getter = getter;
        lSlot.setter = setter;
        this.setAttributes(name, attributes);
    }

    protected void checkPropertyDefinition(ScriptableObject desc) {
        Object getter = ScriptableObject.getProperty((Scriptable)desc, "get");
        if (getter != NOT_FOUND && getter != Undefined.instance && !(getter instanceof Callable)) {
            throw ScriptRuntime.notFunctionError(getter);
        }
        Object setter = ScriptableObject.getProperty((Scriptable)desc, "set");
        if (setter != NOT_FOUND && setter != Undefined.instance && !(setter instanceof Callable)) {
            throw ScriptRuntime.notFunctionError(setter);
        }
        if (this.isDataDescriptor(desc) && this.isAccessorDescriptor(desc)) {
            throw ScriptRuntime.typeErrorById("msg.both.data.and.accessor.desc", new Object[0]);
        }
    }

    protected void checkPropertyChange(Object id, ScriptableObject current, ScriptableObject desc) {
        if (current == null) {
            if (!this.isExtensible()) {
                throw ScriptRuntime.typeErrorById("msg.not.extensible", new Object[0]);
            }
        } else if (ScriptableObject.isFalse(current.get("configurable", (Scriptable)current))) {
            if (ScriptableObject.isTrue(ScriptableObject.getProperty((Scriptable)desc, "configurable"))) {
                throw ScriptRuntime.typeErrorById("msg.change.configurable.false.to.true", id);
            }
            if (ScriptableObject.isTrue(current.get("enumerable", (Scriptable)current)) != ScriptableObject.isTrue(ScriptableObject.getProperty((Scriptable)desc, "enumerable"))) {
                throw ScriptRuntime.typeErrorById("msg.change.enumerable.with.configurable.false", id);
            }
            boolean isData = this.isDataDescriptor(desc);
            boolean isAccessor = this.isAccessorDescriptor(desc);
            if (isData || isAccessor) {
                if (isData && this.isDataDescriptor(current)) {
                    if (ScriptableObject.isFalse(current.get("writable", (Scriptable)current))) {
                        if (ScriptableObject.isTrue(ScriptableObject.getProperty((Scriptable)desc, "writable"))) {
                            throw ScriptRuntime.typeErrorById("msg.change.writable.false.to.true.with.configurable.false", id);
                        }
                        if (!this.sameValue(ScriptableObject.getProperty((Scriptable)desc, "value"), current.get("value", (Scriptable)current))) {
                            throw ScriptRuntime.typeErrorById("msg.change.value.with.writable.false", id);
                        }
                    }
                } else if (isAccessor && this.isAccessorDescriptor(current)) {
                    if (!this.sameValue(ScriptableObject.getProperty((Scriptable)desc, "set"), current.get("set", (Scriptable)current))) {
                        throw ScriptRuntime.typeErrorById("msg.change.setter.with.configurable.false", id);
                    }
                    if (!this.sameValue(ScriptableObject.getProperty((Scriptable)desc, "get"), current.get("get", (Scriptable)current))) {
                        throw ScriptRuntime.typeErrorById("msg.change.getter.with.configurable.false", id);
                    }
                } else {
                    if (this.isDataDescriptor(current)) {
                        throw ScriptRuntime.typeErrorById("msg.change.property.data.to.accessor.with.configurable.false", id);
                    }
                    throw ScriptRuntime.typeErrorById("msg.change.property.accessor.to.data.with.configurable.false", id);
                }
            }
        }
    }

    protected static boolean isTrue(Object value) {
        return value != NOT_FOUND && ScriptRuntime.toBoolean(value);
    }

    protected static boolean isFalse(Object value) {
        return !ScriptableObject.isTrue(value);
    }

    protected boolean sameValue(Object newValue, Object currentValue) {
        if (newValue == NOT_FOUND) {
            return true;
        }
        if (currentValue == NOT_FOUND) {
            currentValue = Undefined.instance;
        }
        if (currentValue instanceof Number && newValue instanceof Number) {
            double d1 = ((Number)currentValue).doubleValue();
            double d2 = ((Number)newValue).doubleValue();
            if (Double.isNaN(d1) && Double.isNaN(d2)) {
                return true;
            }
            if (d1 == 0.0 && Double.doubleToLongBits(d1) != Double.doubleToLongBits(d2)) {
                return false;
            }
        }
        return ScriptRuntime.shallowEq(currentValue, newValue);
    }

    protected int applyDescriptorToAttributeBitset(int attributes, ScriptableObject desc) {
        Object configurable;
        Object writable;
        Object enumerable = ScriptableObject.getProperty((Scriptable)desc, "enumerable");
        if (enumerable != NOT_FOUND) {
            int n = attributes = ScriptRuntime.toBoolean(enumerable) ? attributes & 0xFFFFFFFD : attributes | 2;
        }
        if ((writable = ScriptableObject.getProperty((Scriptable)desc, "writable")) != NOT_FOUND) {
            int n = attributes = ScriptRuntime.toBoolean(writable) ? attributes & 0xFFFFFFFE : attributes | 1;
        }
        if ((configurable = ScriptableObject.getProperty((Scriptable)desc, "configurable")) != NOT_FOUND) {
            attributes = ScriptRuntime.toBoolean(configurable) ? attributes & 0xFFFFFFFB : attributes | 4;
        }
        return attributes;
    }

    protected boolean isDataDescriptor(ScriptableObject desc) {
        return ScriptableObject.hasProperty((Scriptable)desc, "value") || ScriptableObject.hasProperty((Scriptable)desc, "writable");
    }

    protected boolean isAccessorDescriptor(ScriptableObject desc) {
        return ScriptableObject.hasProperty((Scriptable)desc, "get") || ScriptableObject.hasProperty((Scriptable)desc, "set");
    }

    protected boolean isGenericDescriptor(ScriptableObject desc) {
        return !this.isDataDescriptor(desc) && !this.isAccessorDescriptor(desc);
    }

    protected static Scriptable ensureScriptable(Object arg) {
        if (!(arg instanceof Scriptable)) {
            throw ScriptRuntime.typeErrorById("msg.arg.not.object", ScriptRuntime.typeof(arg));
        }
        return (Scriptable)arg;
    }

    protected static SymbolScriptable ensureSymbolScriptable(Object arg) {
        if (!(arg instanceof SymbolScriptable)) {
            throw ScriptRuntime.typeErrorById("msg.object.not.symbolscriptable", ScriptRuntime.typeof(arg));
        }
        return (SymbolScriptable)arg;
    }

    protected static ScriptableObject ensureScriptableObject(Object arg) {
        if (arg instanceof ScriptableObject) {
            return (ScriptableObject)arg;
        }
        if (arg instanceof Delegator) {
            return (ScriptableObject)((Delegator)arg).getDelegee();
        }
        throw ScriptRuntime.typeErrorById("msg.arg.not.object", ScriptRuntime.typeof(arg));
    }

    public void defineFunctionProperties(String[] names, Class<?> clazz, int attributes) {
        Method[] methods = FunctionObject.getMethodList(clazz);
        for (int i = 0; i < names.length; ++i) {
            String name = names[i];
            Method m = FunctionObject.findSingleMethod(methods, name);
            if (m == null) {
                throw Context.reportRuntimeErrorById("msg.method.not.found", name, clazz.getName());
            }
            FunctionObject f = new FunctionObject(name, m, this);
            this.defineProperty(name, (Object)f, attributes);
        }
    }

    public static Scriptable getObjectPrototype(Scriptable scope) {
        return TopLevel.getBuiltinPrototype(ScriptableObject.getTopLevelScope(scope), TopLevel.Builtins.Object);
    }

    public static Scriptable getFunctionPrototype(Scriptable scope) {
        return TopLevel.getBuiltinPrototype(ScriptableObject.getTopLevelScope(scope), TopLevel.Builtins.Function);
    }

    public static Scriptable getGeneratorFunctionPrototype(Scriptable scope) {
        return TopLevel.getBuiltinPrototype(ScriptableObject.getTopLevelScope(scope), TopLevel.Builtins.GeneratorFunction);
    }

    public static Scriptable getArrayPrototype(Scriptable scope) {
        return TopLevel.getBuiltinPrototype(ScriptableObject.getTopLevelScope(scope), TopLevel.Builtins.Array);
    }

    public static Scriptable getClassPrototype(Scriptable scope, String className) {
        Object proto;
        Object ctor = ScriptableObject.getProperty(scope = ScriptableObject.getTopLevelScope(scope), className);
        if (ctor instanceof BaseFunction) {
            proto = ((BaseFunction)ctor).getPrototypeProperty();
        } else if (ctor instanceof Scriptable) {
            Scriptable ctorObj = (Scriptable)ctor;
            proto = ctorObj.get("prototype", ctorObj);
        } else {
            return null;
        }
        if (proto instanceof Scriptable) {
            return (Scriptable)proto;
        }
        return null;
    }

    public static Scriptable getTopLevelScope(Scriptable obj) {
        Scriptable parent;
        while ((parent = obj.getParentScope()) != null) {
            obj = parent;
        }
        return obj;
    }

    public boolean isExtensible() {
        return this.isExtensible;
    }

    public void preventExtensions() {
        this.isExtensible = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sealObject() {
        if (!this.isSealed) {
            long stamp = this.slotMap.readLock();
            try {
                for (Slot slot : this.slotMap) {
                    Object value = slot.value;
                    if (!(value instanceof LazilyLoadedCtor)) continue;
                    LazilyLoadedCtor initializer = (LazilyLoadedCtor)value;
                    try {
                        initializer.init();
                    }
                    finally {
                        slot.value = initializer.getValue();
                    }
                }
                this.isSealed = true;
            }
            finally {
                this.slotMap.unlockRead(stamp);
            }
        }
    }

    public final boolean isSealed() {
        return this.isSealed;
    }

    private void checkNotSealed(Object key, int index) {
        if (!this.isSealed()) {
            return;
        }
        String str = key != null ? key.toString() : Integer.toString(index);
        throw Context.reportRuntimeErrorById("msg.modify.sealed", str);
    }

    public static Object getProperty(Scriptable obj, String name) {
        Object result;
        Scriptable start = obj;
        while ((result = obj.get(name, start)) == Scriptable.NOT_FOUND && (obj = obj.getPrototype()) != null) {
        }
        return result;
    }

    public static Object getProperty(Scriptable obj, Symbol key) {
        Object result;
        Scriptable start = obj;
        while ((result = ScriptableObject.ensureSymbolScriptable(obj).get(key, start)) == Scriptable.NOT_FOUND && (obj = obj.getPrototype()) != null) {
        }
        return result;
    }

    public static <T> T getTypedProperty(Scriptable s, int index, Class<T> type) {
        Object val = ScriptableObject.getProperty(s, index);
        if (val == Scriptable.NOT_FOUND) {
            val = null;
        }
        return type.cast(Context.jsToJava(val, type));
    }

    public static Object getProperty(Scriptable obj, int index) {
        Object result;
        Scriptable start = obj;
        while ((result = obj.get(index, start)) == Scriptable.NOT_FOUND && (obj = obj.getPrototype()) != null) {
        }
        return result;
    }

    public static <T> T getTypedProperty(Scriptable s, String name, Class<T> type) {
        Object val = ScriptableObject.getProperty(s, name);
        if (val == Scriptable.NOT_FOUND) {
            val = null;
        }
        return type.cast(Context.jsToJava(val, type));
    }

    public static boolean hasProperty(Scriptable obj, String name) {
        return null != ScriptableObject.getBase(obj, name);
    }

    public static void redefineProperty(Scriptable obj, String name, boolean isConst) {
        ConstProperties cp;
        Scriptable base = ScriptableObject.getBase(obj, name);
        if (base == null) {
            return;
        }
        if (base instanceof ConstProperties && (cp = (ConstProperties)((Object)base)).isConst(name)) {
            throw ScriptRuntime.typeErrorById("msg.const.redecl", name);
        }
        if (isConst) {
            throw ScriptRuntime.typeErrorById("msg.var.redecl", name);
        }
    }

    public static boolean hasProperty(Scriptable obj, int index) {
        return null != ScriptableObject.getBase(obj, index);
    }

    public static boolean hasProperty(Scriptable obj, Symbol key) {
        return null != ScriptableObject.getBase(obj, key);
    }

    public static void putProperty(Scriptable obj, String name, Object value) {
        Scriptable base = ScriptableObject.getBase(obj, name);
        if (base == null) {
            base = obj;
        }
        base.put(name, obj, value);
    }

    public static void putProperty(Scriptable obj, Symbol key, Object value) {
        Scriptable base = ScriptableObject.getBase(obj, key);
        if (base == null) {
            base = obj;
        }
        ScriptableObject.ensureSymbolScriptable(base).put(key, obj, value);
    }

    public static void putConstProperty(Scriptable obj, String name, Object value) {
        Scriptable base = ScriptableObject.getBase(obj, name);
        if (base == null) {
            base = obj;
        }
        if (base instanceof ConstProperties) {
            ((ConstProperties)((Object)base)).putConst(name, obj, value);
        }
    }

    public static void putProperty(Scriptable obj, int index, Object value) {
        Scriptable base = ScriptableObject.getBase(obj, index);
        if (base == null) {
            base = obj;
        }
        base.put(index, obj, value);
    }

    public static boolean deleteProperty(Scriptable obj, String name) {
        Scriptable base = ScriptableObject.getBase(obj, name);
        if (base == null) {
            return true;
        }
        base.delete(name);
        return !base.has(name, obj);
    }

    public static boolean deleteProperty(Scriptable obj, int index) {
        Scriptable base = ScriptableObject.getBase(obj, index);
        if (base == null) {
            return true;
        }
        base.delete(index);
        return !base.has(index, obj);
    }

    public static Object[] getPropertyIds(Scriptable obj) {
        if (obj == null) {
            return ScriptRuntime.emptyArgs;
        }
        Object[] result = obj.getIds();
        ObjToIntMap map = null;
        while ((obj = obj.getPrototype()) != null) {
            int i;
            Object[] ids = obj.getIds();
            if (ids.length == 0) continue;
            if (map == null) {
                if (result.length == 0) {
                    result = ids;
                    continue;
                }
                map = new ObjToIntMap(result.length + ids.length);
                for (i = 0; i != result.length; ++i) {
                    map.intern(result[i]);
                }
                result = null;
            }
            for (i = 0; i != ids.length; ++i) {
                map.intern(ids[i]);
            }
        }
        if (map != null) {
            result = map.getKeys();
        }
        return result;
    }

    public static Object callMethod(Scriptable obj, String methodName, Object[] args) {
        return ScriptableObject.callMethod(null, obj, methodName, args);
    }

    public static Object callMethod(Context cx, Scriptable obj, String methodName, Object[] args) {
        Object funObj = ScriptableObject.getProperty(obj, methodName);
        if (!(funObj instanceof Function)) {
            throw ScriptRuntime.notFunctionError(obj, methodName);
        }
        Function fun = (Function)funObj;
        Scriptable scope = ScriptableObject.getTopLevelScope(obj);
        if (cx != null) {
            return fun.call(cx, scope, obj, args);
        }
        return Context.call(null, fun, scope, obj, args);
    }

    private static Scriptable getBase(Scriptable obj, String name) {
        while (!obj.has(name, obj) && (obj = obj.getPrototype()) != null) {
        }
        return obj;
    }

    private static Scriptable getBase(Scriptable obj, int index) {
        while (!obj.has(index, obj) && (obj = obj.getPrototype()) != null) {
        }
        return obj;
    }

    private static Scriptable getBase(Scriptable obj, Symbol key) {
        while (!ScriptableObject.ensureSymbolScriptable(obj).has(key, obj) && (obj = obj.getPrototype()) != null) {
        }
        return obj;
    }

    public final Object getAssociatedValue(Object key) {
        Map<Object, Object> h = this.associatedValues;
        if (h == null) {
            return null;
        }
        return h.get(key);
    }

    public static Object getTopScopeValue(Scriptable scope, Object key) {
        scope = ScriptableObject.getTopLevelScope(scope);
        do {
            ScriptableObject so;
            Object value;
            if (!(scope instanceof ScriptableObject) || (value = (so = (ScriptableObject)scope).getAssociatedValue(key)) == null) continue;
            return value;
        } while ((scope = scope.getPrototype()) != null);
        return null;
    }

    public final synchronized Object associateValue(Object key, Object value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        Map<Object, Object> h = this.associatedValues;
        if (h == null) {
            this.associatedValues = h = new HashMap<Object, Object>();
        }
        return Kit.initHash(h, key, value);
    }

    private boolean putImpl(Object key, int index, Scriptable start, Object value) {
        Slot slot;
        if (this != start) {
            slot = this.slotMap.query(key, index);
            if (!this.isExtensible && (slot == null || !(slot instanceof AccessorSlot) && (slot.getAttributes() & 1) != 0) && Context.isCurrentContextStrict()) {
                throw ScriptRuntime.typeErrorById("msg.not.extensible", new Object[0]);
            }
            if (slot == null) {
                return false;
            }
        } else if (!this.isExtensible) {
            slot = this.slotMap.query(key, index);
            if ((slot == null || !(slot instanceof AccessorSlot) && (slot.getAttributes() & 1) != 0) && Context.isCurrentContextStrict()) {
                throw ScriptRuntime.typeErrorById("msg.not.extensible", new Object[0]);
            }
            if (slot == null) {
                return true;
            }
        } else {
            if (this.isSealed) {
                this.checkNotSealed(key, index);
            }
            slot = this.slotMap.modify(key, index, 0);
        }
        return slot.setValue(value, this, start);
    }

    private boolean putConstImpl(String name, int index, Scriptable start, Object value, int constFlag) {
        Slot slot;
        Context cx;
        assert (constFlag != 0);
        if (!this.isExtensible && (cx = Context.getContext()).isStrictMode()) {
            throw ScriptRuntime.typeErrorById("msg.not.extensible", new Object[0]);
        }
        if (this != start) {
            slot = this.slotMap.query(name, index);
            if (slot == null) {
                return false;
            }
        } else if (!this.isExtensible()) {
            slot = this.slotMap.query(name, index);
            if (slot == null) {
                return true;
            }
        } else {
            this.checkNotSealed(name, index);
            slot = this.slotMap.modify(name, index, 13);
            int attr = slot.getAttributes();
            if ((attr & 1) == 0) {
                throw Context.reportRuntimeErrorById("msg.var.redecl", name);
            }
            if ((attr & 8) != 0) {
                slot.value = value;
                if (constFlag != 8) {
                    slot.setAttributes(attr & 0xFFFFFFF7);
                }
            }
            return true;
        }
        return slot.setValue(value, this, start);
    }

    private Slot getAttributeSlot(String name, int index) {
        Slot slot = this.slotMap.query(name, index);
        if (slot == null) {
            String str = name != null ? name : Integer.toString(index);
            throw Context.reportRuntimeErrorById("msg.prop.not.found", str);
        }
        return slot;
    }

    private Slot getAttributeSlot(Symbol key) {
        Slot slot = this.slotMap.query(key, 0);
        if (slot == null) {
            throw Context.reportRuntimeErrorById("msg.prop.not.found", key);
        }
        return slot;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Object[] getIds(boolean getNonEnumerable, boolean getSymbols) {
        Object[] result;
        Object[] a;
        int externalLen;
        int n = externalLen = this.externalData == null ? 0 : this.externalData.getArrayLength();
        if (externalLen == 0) {
            a = ScriptRuntime.emptyArgs;
        } else {
            a = new Object[externalLen];
            for (int i = 0; i < externalLen; ++i) {
                a[i] = i;
            }
        }
        if (this.slotMap.isEmpty()) {
            return a;
        }
        int c = externalLen;
        long stamp = this.slotMap.readLock();
        try {
            for (Slot slot : this.slotMap) {
                if (!getNonEnumerable && (slot.getAttributes() & 2) != 0 || !getSymbols && slot.name instanceof Symbol) continue;
                if (c == externalLen) {
                    Object[] oldA = a;
                    a = new Object[this.slotMap.dirtySize() + externalLen];
                    if (oldA != null) {
                        System.arraycopy(oldA, 0, a, 0, externalLen);
                    }
                }
                a[c++] = slot.name != null ? slot.name : Integer.valueOf(slot.indexOrHash);
            }
        }
        finally {
            this.slotMap.unlockRead(stamp);
        }
        if (c == a.length + externalLen) {
            result = a;
        } else {
            result = new Object[c];
            System.arraycopy(a, 0, result, 0, c);
        }
        Context cx = Context.getCurrentContext();
        if (cx != null && cx.hasFeature(16)) {
            Arrays.sort(result, KEY_COMPARATOR);
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        long stamp = this.slotMap.readLock();
        try {
            int objectsCount = this.slotMap.dirtySize();
            if (objectsCount == 0) {
                out.writeInt(0);
            } else {
                out.writeInt(objectsCount);
                for (Slot slot : this.slotMap) {
                    out.writeObject(slot);
                }
            }
        }
        finally {
            this.slotMap.unlockRead(stamp);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int tableSize = in.readInt();
        this.slotMap = ScriptableObject.createSlotMap(tableSize);
        for (int i = 0; i < tableSize; ++i) {
            Slot slot = (Slot)in.readObject();
            this.slotMap.add(slot);
        }
    }

    protected ScriptableObject getOwnPropertyDescriptor(Context cx, Object id) {
        Slot slot = this.querySlot(cx, id);
        if (slot == null) {
            return null;
        }
        Scriptable scope = this.getParentScope();
        return slot.getPropertyDescriptor(cx, scope == null ? this : scope);
    }

    protected Slot querySlot(Context cx, Object id) {
        if (id instanceof Symbol) {
            return this.slotMap.query(id, 0);
        }
        ScriptRuntime.StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, id);
        if (s.stringId == null) {
            return this.slotMap.query(null, s.index);
        }
        return this.slotMap.query(s.stringId, 0);
    }

    public int size() {
        return this.slotMap.size();
    }

    public boolean isEmpty() {
        return this.slotMap.isEmpty();
    }

    public Object get(Object key) {
        Object value = null;
        if (key instanceof String) {
            value = this.get((String)key, (Scriptable)this);
        } else if (key instanceof Symbol) {
            value = this.get((Symbol)key, (Scriptable)this);
        } else if (key instanceof Number) {
            value = this.get(((Number)key).intValue(), (Scriptable)this);
        }
        if (value == Scriptable.NOT_FOUND || value == Undefined.instance) {
            return null;
        }
        if (value instanceof Wrapper) {
            return ((Wrapper)value).unwrap();
        }
        return value;
    }

    static {
        try {
            GET_ARRAY_LENGTH = ScriptableObject.class.getMethod("getExternalArrayLength", new Class[0]);
        }
        catch (NoSuchMethodException nsm) {
            throw new RuntimeException(nsm);
        }
        KEY_COMPARATOR = new KeyComparator();
    }

    public static final class KeyComparator
    implements Comparator<Object>,
    Serializable {
        private static final long serialVersionUID = 6411335891523988149L;

        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof Integer) {
                if (o2 instanceof Integer) {
                    return ((Integer)o1).compareTo((Integer)o2);
                }
                return -1;
            }
            if (o2 instanceof Integer) {
                return 1;
            }
            return 0;
        }
    }
}

