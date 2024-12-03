/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.reflection;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import nonapi.io.github.classgraph.concurrency.SingletonMap;
import nonapi.io.github.classgraph.utils.LogNode;

abstract class ReflectionDriver {
    private final SingletonMap<Class<?>, ClassMemberCache, Exception> classToClassMemberCache = new SingletonMap<Class<?>, ClassMemberCache, Exception>(){

        @Override
        public ClassMemberCache newInstance(Class<?> cls, LogNode log) throws Exception, InterruptedException {
            return new ClassMemberCache(cls);
        }
    };
    private static Method isAccessibleMethod;
    private static Method canAccessMethod;

    ReflectionDriver() {
    }

    abstract Class<?> findClass(String var1) throws Exception;

    abstract Method[] getDeclaredMethods(Class<?> var1) throws Exception;

    abstract <T> Constructor<T>[] getDeclaredConstructors(Class<T> var1) throws Exception;

    abstract Field[] getDeclaredFields(Class<?> var1) throws Exception;

    abstract Object getField(Object var1, Field var2) throws Exception;

    abstract void setField(Object var1, Field var2, Object var3) throws Exception;

    abstract Object getStaticField(Field var1) throws Exception;

    abstract void setStaticField(Field var1, Object var2) throws Exception;

    abstract Object invokeMethod(Object var1, Method var2, Object ... var3) throws Exception;

    abstract Object invokeStaticMethod(Method var1, Object ... var2) throws Exception;

    abstract boolean makeAccessible(Object var1, AccessibleObject var2);

    boolean isAccessible(Object instance, AccessibleObject fieldOrMethod) {
        if (canAccessMethod != null) {
            try {
                return (Boolean)canAccessMethod.invoke((Object)fieldOrMethod, instance);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (isAccessibleMethod != null) {
            try {
                return (Boolean)isAccessibleMethod.invoke((Object)fieldOrMethod, new Object[0]);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return false;
    }

    protected Field findField(Class<?> cls, Object obj, String fieldName) throws Exception {
        Field field = (Field)this.classToClassMemberCache.get(cls, null).fieldNameToField.get(fieldName);
        if (field != null) {
            if (!this.isAccessible(obj, field)) {
                this.makeAccessible(obj, field);
            }
            return field;
        }
        throw new NoSuchFieldException("Could not find field " + cls.getName() + "." + fieldName);
    }

    protected Field findStaticField(Class<?> cls, String fieldName) throws Exception {
        return this.findField(cls, null, fieldName);
    }

    protected Field findInstanceField(Object obj, String fieldName) throws Exception {
        if (obj == null) {
            throw new IllegalArgumentException("obj cannot be null");
        }
        return this.findField(obj.getClass(), obj, fieldName);
    }

    protected Method findMethod(Class<?> cls, Object obj, String methodName, Class<?> ... paramTypes) throws Exception {
        List methodsForName = (List)this.classToClassMemberCache.get(cls, null).methodNameToMethods.get(methodName);
        if (methodsForName != null) {
            boolean found = false;
            for (Method method : methodsForName) {
                if (!Arrays.equals(method.getParameterTypes(), paramTypes)) continue;
                found = true;
                if (!this.isAccessible(obj, method)) continue;
                return method;
            }
            if (found) {
                for (Method method : methodsForName) {
                    if (!Arrays.equals(method.getParameterTypes(), paramTypes) || !this.makeAccessible(obj, method)) continue;
                    return method;
                }
            }
            throw new NoSuchMethodException("Could not make method accessible: " + cls.getName() + "." + methodName);
        }
        throw new NoSuchMethodException("Could not find method " + cls.getName() + "." + methodName);
    }

    protected Method findStaticMethod(Class<?> cls, String methodName, Class<?> ... paramTypes) throws Exception {
        return this.findMethod(cls, null, methodName, paramTypes);
    }

    protected Method findInstanceMethod(Object obj, String methodName, Class<?> ... paramTypes) throws Exception {
        if (obj == null) {
            throw new IllegalArgumentException("obj cannot be null");
        }
        return this.findMethod(obj.getClass(), obj, methodName, paramTypes);
    }

    static {
        try {
            isAccessibleMethod = AccessibleObject.class.getDeclaredMethod("isAccessible", new Class[0]);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            canAccessMethod = AccessibleObject.class.getDeclaredMethod("canAccess", Object.class);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public class ClassMemberCache {
        private final Map<String, List<Method>> methodNameToMethods = new HashMap<String, List<Method>>();
        private final Map<String, Field> fieldNameToField = new HashMap<String, Field>();

        private ClassMemberCache(Class<?> cls) throws Exception {
            HashSet<AnnotatedElement> visited = new HashSet<AnnotatedElement>();
            LinkedList<AnnotatedElement> interfaceQueue = new LinkedList<AnnotatedElement>();
            for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
                try {
                    for (GenericDeclaration genericDeclaration : ReflectionDriver.this.getDeclaredMethods(c)) {
                        this.cacheMethod((Method)genericDeclaration);
                    }
                    for (AnnotatedElement annotatedElement : ReflectionDriver.this.getDeclaredFields(c)) {
                        this.cacheField((Field)annotatedElement);
                    }
                    if (c.isInterface() && visited.add(c)) {
                        interfaceQueue.add(c);
                    }
                    for (AnnotatedElement annotatedElement : c.getInterfaces()) {
                        if (!visited.add(annotatedElement)) continue;
                        interfaceQueue.add(annotatedElement);
                    }
                    continue;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            while (!interfaceQueue.isEmpty()) {
                Class iface = (Class)interfaceQueue.remove();
                try {
                    for (AnnotatedElement annotatedElement : ReflectionDriver.this.getDeclaredMethods(iface)) {
                        this.cacheMethod((Method)annotatedElement);
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                for (AnnotatedElement annotatedElement : iface.getInterfaces()) {
                    if (!visited.add(annotatedElement)) continue;
                    interfaceQueue.add(annotatedElement);
                }
            }
        }

        private void cacheMethod(Method method) {
            List<Method> methodsForName = this.methodNameToMethods.get(method.getName());
            if (methodsForName == null) {
                methodsForName = new ArrayList<Method>();
                this.methodNameToMethods.put(method.getName(), methodsForName);
            }
            methodsForName.add(method);
        }

        private void cacheField(Field field) {
            if (!this.fieldNameToField.containsKey(field.getName())) {
                this.fieldNameToField.put(field.getName(), field);
            }
        }
    }
}

