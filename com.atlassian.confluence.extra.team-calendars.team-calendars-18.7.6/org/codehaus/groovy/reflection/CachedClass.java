/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection;

import groovy.lang.Closure;
import groovy.lang.ExpandoMetaClass;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaMethod;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.reflection.CachedConstructor;
import org.codehaus.groovy.reflection.CachedField;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.callsite.CallSiteClassLoader;
import org.codehaus.groovy.runtime.metaclass.ClosureMetaClass;
import org.codehaus.groovy.util.FastArray;
import org.codehaus.groovy.util.LazyReference;
import org.codehaus.groovy.util.ReferenceBundle;

public class CachedClass {
    private static final Method[] EMPTY_METHOD_ARRAY = new Method[0];
    private final Class cachedClass;
    public ClassInfo classInfo;
    private static ReferenceBundle softBundle = ReferenceBundle.getSoftBundle();
    private final LazyReference<CachedField[]> fields = new LazyReference<CachedField[]>(softBundle){

        @Override
        public CachedField[] initValue() {
            Field[] declaredFields = AccessController.doPrivileged(new PrivilegedAction<Field[]>(){

                @Override
                public Field[] run() {
                    AccessibleObject[] df = CachedClass.this.getTheClass().getDeclaredFields();
                    df = (Field[])CachedClass.makeAccessible(df);
                    return df;
                }
            });
            CachedField[] fields = new CachedField[declaredFields.length];
            for (int i = 0; i != fields.length; ++i) {
                fields[i] = new CachedField(declaredFields[i]);
            }
            return fields;
        }
    };
    private LazyReference<CachedConstructor[]> constructors = new LazyReference<CachedConstructor[]>(softBundle){

        @Override
        public CachedConstructor[] initValue() {
            Constructor[] declaredConstructors = (Constructor[])AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    return CachedClass.this.getTheClass().getDeclaredConstructors();
                }
            });
            CachedConstructor[] constructors = new CachedConstructor[declaredConstructors.length];
            for (int i = 0; i != constructors.length; ++i) {
                constructors[i] = new CachedConstructor(CachedClass.this, declaredConstructors[i]);
            }
            return constructors;
        }
    };
    private final LazyReference<CachedMethod[]> methods = new LazyReference<CachedMethod[]>(softBundle){

        @Override
        public CachedMethod[] initValue() {
            Method[] declaredMethods = (Method[])AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    try {
                        AccessibleObject[] dm = CachedClass.this.getTheClass().getDeclaredMethods();
                        dm = (Method[])CachedClass.makeAccessible(dm);
                        return dm;
                    }
                    catch (Throwable e) {
                        return EMPTY_METHOD_ARRAY;
                    }
                }
            });
            ArrayList<CachedMethod> methods = new ArrayList<CachedMethod>(declaredMethods.length);
            ArrayList<CachedMethod> mopMethods = new ArrayList<CachedMethod>(declaredMethods.length);
            for (int i = 0; i != declaredMethods.length; ++i) {
                CachedMethod cachedMethod = new CachedMethod(CachedClass.this, declaredMethods[i]);
                String name = cachedMethod.getName();
                if (declaredMethods[i].isBridge() || name.indexOf(43) >= 0) continue;
                if (name.startsWith("this$") || name.startsWith("super$")) {
                    mopMethods.add(cachedMethod);
                    continue;
                }
                methods.add(cachedMethod);
            }
            Object[] resMethods = methods.toArray(new CachedMethod[methods.size()]);
            Arrays.sort(resMethods);
            CachedClass superClass = CachedClass.this.getCachedSuperClass();
            if (superClass != null) {
                superClass.getMethods();
                CachedMethod[] superMopMethods = superClass.mopMethods;
                for (int i = 0; i != superMopMethods.length; ++i) {
                    mopMethods.add(superMopMethods[i]);
                }
            }
            CachedClass.this.mopMethods = mopMethods.toArray(new CachedMethod[mopMethods.size()]);
            Arrays.sort(CachedClass.this.mopMethods, CachedMethodComparatorByName.INSTANCE);
            return resMethods;
        }
    };
    private LazyReference<CachedClass> cachedSuperClass = new LazyReference<CachedClass>(softBundle){

        @Override
        public CachedClass initValue() {
            if (!CachedClass.this.isArray) {
                return ReflectionCache.getCachedClass(CachedClass.this.getTheClass().getSuperclass());
            }
            if (CachedClass.this.cachedClass.getComponentType().isPrimitive() || CachedClass.this.cachedClass.getComponentType() == Object.class) {
                return ReflectionCache.OBJECT_CLASS;
            }
            return ReflectionCache.OBJECT_ARRAY_CLASS;
        }
    };
    private final LazyReference<CallSiteClassLoader> callSiteClassLoader = new LazyReference<CallSiteClassLoader>(softBundle){

        @Override
        public CallSiteClassLoader initValue() {
            return AccessController.doPrivileged(new PrivilegedAction<CallSiteClassLoader>(){

                @Override
                public CallSiteClassLoader run() {
                    return new CallSiteClassLoader(CachedClass.this.cachedClass);
                }
            });
        }
    };
    private final LazyReference<LinkedList<ClassInfo>> hierarchy = new LazyReference<LinkedList<ClassInfo>>(softBundle){

        @Override
        public LinkedList<ClassInfo> initValue() {
            LinkedHashSet<ClassInfo> res = new LinkedHashSet<ClassInfo>();
            res.add(CachedClass.this.classInfo);
            for (CachedClass iface : CachedClass.this.getDeclaredInterfaces()) {
                res.addAll(iface.getHierarchy());
            }
            CachedClass superClass = CachedClass.this.getCachedSuperClass();
            if (superClass != null) {
                res.addAll(superClass.getHierarchy());
            }
            if (CachedClass.this.isInterface) {
                res.add(ReflectionCache.OBJECT_CLASS.classInfo);
            }
            return new LinkedList<ClassInfo>(res);
        }
    };
    static final MetaMethod[] EMPTY = new MetaMethod[0];
    int hashCode;
    public CachedMethod[] mopMethods;
    public static final CachedClass[] EMPTY_ARRAY = new CachedClass[0];
    private final LazyReference<Set<CachedClass>> declaredInterfaces = new LazyReference<Set<CachedClass>>(softBundle){

        @Override
        public Set<CachedClass> initValue() {
            Class<?>[] classes;
            HashSet<CachedClass> res = new HashSet<CachedClass>(0);
            for (Class<?> cls : classes = CachedClass.this.getTheClass().getInterfaces()) {
                res.add(ReflectionCache.getCachedClass(cls));
            }
            return res;
        }
    };
    private final LazyReference<Set<CachedClass>> interfaces = new LazyReference<Set<CachedClass>>(softBundle){

        @Override
        public Set<CachedClass> initValue() {
            Class<?>[] classes;
            HashSet<CachedClass> res = new HashSet<CachedClass>(0);
            if (CachedClass.this.getTheClass().isInterface()) {
                res.add(CachedClass.this);
            }
            for (Class<?> cls : classes = CachedClass.this.getTheClass().getInterfaces()) {
                CachedClass aClass = ReflectionCache.getCachedClass(cls);
                if (res.contains(aClass)) continue;
                res.addAll(aClass.getInterfaces());
            }
            CachedClass superClass = CachedClass.this.getCachedSuperClass();
            if (superClass != null) {
                res.addAll(superClass.getInterfaces());
            }
            return res;
        }
    };
    public final boolean isArray;
    public final boolean isPrimitive;
    public final int modifiers;
    int distance = -1;
    public final boolean isInterface;
    public final boolean isNumber;

    private static AccessibleObject[] makeAccessible(AccessibleObject[] aoa) {
        try {
            AccessibleObject.setAccessible(aoa, true);
            return aoa;
        }
        catch (Throwable outer) {
            ArrayList<AccessibleObject> ret = new ArrayList<AccessibleObject>(aoa.length);
            for (AccessibleObject ao : aoa) {
                try {
                    ao.setAccessible(true);
                    ret.add(ao);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            return ret.toArray((AccessibleObject[])Array.newInstance(aoa.getClass().getComponentType(), ret.size()));
        }
    }

    public CachedClass(Class klazz, ClassInfo classInfo) {
        this.cachedClass = klazz;
        this.classInfo = classInfo;
        this.isArray = klazz.isArray();
        this.isPrimitive = klazz.isPrimitive();
        this.modifiers = klazz.getModifiers();
        this.isInterface = klazz.isInterface();
        this.isNumber = Number.class.isAssignableFrom(klazz);
        for (CachedClass inf : this.getInterfaces()) {
            ReflectionCache.isAssignableFrom(klazz, inf.cachedClass);
        }
        for (CachedClass cur = this; cur != null; cur = cur.getCachedSuperClass()) {
            ReflectionCache.setAssignableFrom(cur.cachedClass, klazz);
        }
    }

    public CachedClass getCachedSuperClass() {
        return this.cachedSuperClass.get();
    }

    public Set<CachedClass> getInterfaces() {
        return this.interfaces.get();
    }

    public Set<CachedClass> getDeclaredInterfaces() {
        return this.declaredInterfaces.get();
    }

    public CachedMethod[] getMethods() {
        return this.methods.get();
    }

    public CachedField[] getFields() {
        return this.fields.get();
    }

    public CachedConstructor[] getConstructors() {
        return this.constructors.get();
    }

    public CachedMethod searchMethods(String name, CachedClass[] parameterTypes) {
        CachedMethod[] methods = this.getMethods();
        CachedMethod res = null;
        for (CachedMethod m : methods) {
            if (!m.getName().equals(name) || !ReflectionCache.arrayContentsEq(parameterTypes, m.getParameterTypes()) || res != null && !res.getReturnType().isAssignableFrom(m.getReturnType())) continue;
            res = m;
        }
        return res;
    }

    public int getModifiers() {
        return this.modifiers;
    }

    public Object coerceArgument(Object argument) {
        return argument;
    }

    public int getSuperClassDistance() {
        if (this.distance >= 0) {
            return this.distance;
        }
        int distance = 0;
        for (Class klazz = this.getTheClass(); klazz != null; klazz = klazz.getSuperclass()) {
            ++distance;
        }
        this.distance = distance;
        return distance;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = super.hashCode();
            if (this.hashCode == 0) {
                this.hashCode = -889274690;
            }
        }
        return this.hashCode;
    }

    public boolean isPrimitive() {
        return this.isPrimitive;
    }

    public boolean isVoid() {
        return this.getTheClass() == Void.TYPE;
    }

    public boolean isInterface() {
        return this.isInterface;
    }

    public String getName() {
        return this.getTheClass().getName();
    }

    public String getTypeDescription() {
        return BytecodeHelper.getTypeDescription(this.getTheClass());
    }

    public final Class getTheClass() {
        return this.cachedClass;
    }

    public MetaMethod[] getNewMetaMethods() {
        ArrayList<MetaMethod> arr = new ArrayList<MetaMethod>();
        arr.addAll(Arrays.asList(this.classInfo.newMetaMethods));
        MetaClass metaClass = this.classInfo.getStrongMetaClass();
        if (metaClass != null && metaClass instanceof ExpandoMetaClass) {
            arr.addAll(((ExpandoMetaClass)metaClass).getExpandoMethods());
        }
        if (this.isInterface) {
            MetaClass mc = ReflectionCache.OBJECT_CLASS.classInfo.getStrongMetaClass();
            this.addSubclassExpandos(arr, mc);
        } else {
            for (CachedClass cls = this; cls != null; cls = cls.getCachedSuperClass()) {
                MetaClass mc = cls.classInfo.getStrongMetaClass();
                this.addSubclassExpandos(arr, mc);
            }
        }
        for (CachedClass inf : this.getInterfaces()) {
            MetaClass mc = inf.classInfo.getStrongMetaClass();
            this.addSubclassExpandos(arr, mc);
        }
        return arr.toArray(new MetaMethod[arr.size()]);
    }

    private void addSubclassExpandos(List<MetaMethod> arr, MetaClass mc) {
        if (mc != null && mc instanceof ExpandoMetaClass) {
            ExpandoMetaClass emc = (ExpandoMetaClass)mc;
            for (Object mm : emc.getExpandoSubclassMethods()) {
                if (mm instanceof MetaMethod) {
                    MetaMethod method = (MetaMethod)mm;
                    if (method.getDeclaringClass() != this) continue;
                    arr.add(method);
                    continue;
                }
                FastArray farr = (FastArray)mm;
                for (int i = 0; i != farr.size; ++i) {
                    MetaMethod method = (MetaMethod)farr.get(i);
                    if (method.getDeclaringClass() != this) continue;
                    arr.add(method);
                }
            }
        }
    }

    public void setNewMopMethods(List<MetaMethod> arr) {
        MetaClass metaClass = this.classInfo.getStrongMetaClass();
        if (metaClass != null) {
            if (metaClass.getClass() == MetaClassImpl.class) {
                this.classInfo.setStrongMetaClass(null);
                this.updateSetNewMopMethods(arr);
                this.classInfo.setStrongMetaClass(new MetaClassImpl(metaClass.getTheClass()));
                return;
            }
            if (metaClass.getClass() == ExpandoMetaClass.class) {
                this.classInfo.setStrongMetaClass(null);
                this.updateSetNewMopMethods(arr);
                ExpandoMetaClass newEmc = new ExpandoMetaClass(metaClass.getTheClass());
                newEmc.initialize();
                this.classInfo.setStrongMetaClass(newEmc);
                return;
            }
            throw new GroovyRuntimeException("Can't add methods to class " + this.getTheClass().getName() + ". Strong custom meta class already set.");
        }
        this.classInfo.setWeakMetaClass(null);
        this.updateSetNewMopMethods(arr);
    }

    private void updateSetNewMopMethods(List<MetaMethod> arr) {
        if (arr != null) {
            MetaMethod[] metaMethods = arr.toArray(new MetaMethod[arr.size()]);
            this.classInfo.dgmMetaMethods = metaMethods;
            this.classInfo.newMetaMethods = metaMethods;
        } else {
            this.classInfo.newMetaMethods = this.classInfo.dgmMetaMethods;
        }
    }

    public void addNewMopMethods(List<MetaMethod> arr) {
        MetaClass metaClass = this.classInfo.getStrongMetaClass();
        if (metaClass != null) {
            if (metaClass.getClass() == MetaClassImpl.class) {
                this.classInfo.setStrongMetaClass(null);
                ArrayList<MetaMethod> res = new ArrayList<MetaMethod>();
                Collections.addAll(res, this.classInfo.newMetaMethods);
                res.addAll(arr);
                this.updateSetNewMopMethods(res);
                MetaClassImpl answer = new MetaClassImpl(((MetaClassImpl)metaClass).getRegistry(), metaClass.getTheClass());
                answer.initialize();
                this.classInfo.setStrongMetaClass(answer);
                return;
            }
            if (metaClass.getClass() == ExpandoMetaClass.class) {
                ExpandoMetaClass emc = (ExpandoMetaClass)metaClass;
                this.classInfo.setStrongMetaClass(null);
                this.updateAddNewMopMethods(arr);
                ExpandoMetaClass newEmc = new ExpandoMetaClass(metaClass.getTheClass());
                for (MetaMethod mm : emc.getExpandoMethods()) {
                    newEmc.registerInstanceMethod(mm);
                }
                newEmc.initialize();
                this.classInfo.setStrongMetaClass(newEmc);
                return;
            }
            throw new GroovyRuntimeException("Can't add methods to class " + this.getTheClass().getName() + ". Strong custom meta class already set.");
        }
        this.classInfo.setWeakMetaClass(null);
        this.updateAddNewMopMethods(arr);
    }

    private void updateAddNewMopMethods(List<MetaMethod> arr) {
        ArrayList<MetaMethod> res = new ArrayList<MetaMethod>();
        res.addAll(Arrays.asList(this.classInfo.newMetaMethods));
        res.addAll(arr);
        this.classInfo.newMetaMethods = res.toArray(new MetaMethod[res.size()]);
        Class theClass = this.classInfo.getCachedClass().getTheClass();
        if (theClass == Closure.class || theClass == Class.class) {
            ClosureMetaClass.resetCachedMetaClasses();
        }
    }

    public boolean isAssignableFrom(Class argument) {
        return argument == null || ReflectionCache.isAssignableFrom(this.getTheClass(), argument);
    }

    public boolean isDirectlyAssignable(Object argument) {
        return ReflectionCache.isAssignableFrom(this.getTheClass(), argument.getClass());
    }

    public CallSiteClassLoader getCallSiteLoader() {
        return this.callSiteClassLoader.get();
    }

    public Collection<ClassInfo> getHierarchy() {
        return this.hierarchy.get();
    }

    public String toString() {
        return this.cachedClass.toString();
    }

    public CachedClass getCachedClass() {
        return this;
    }

    public static class CachedMethodComparatorWithString
    implements Comparator {
        public static final Comparator INSTANCE = new CachedMethodComparatorWithString();

        public int compare(Object o1, Object o2) {
            if (o1 instanceof CachedMethod) {
                return ((CachedMethod)o1).getName().compareTo((String)o2);
            }
            return ((String)o1).compareTo(((CachedMethod)o2).getName());
        }
    }

    public static class CachedMethodComparatorByName
    implements Comparator {
        public static final Comparator INSTANCE = new CachedMethodComparatorByName();

        public int compare(Object o1, Object o2) {
            return ((CachedMethod)o1).getName().compareTo(((CachedMethod)o2).getName());
        }
    }
}

