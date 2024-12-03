/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.core.BugException;
import freemarker.core._DelayedConversionToString;
import freemarker.core._DelayedJQuote;
import freemarker.core._TemplateModelException;
import freemarker.ext.beans.CallableMemberDescriptor;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class _MethodUtil {
    private _MethodUtil() {
    }

    public static int isMoreOrSameSpecificParameterType(Class specific, Class generic, boolean bugfixed, int ifHigherThan) {
        if (ifHigherThan >= 4) {
            return 0;
        }
        if (generic.isAssignableFrom(specific)) {
            return generic == specific ? 1 : 4;
        }
        boolean specificIsPrim = specific.isPrimitive();
        boolean genericIsPrim = generic.isPrimitive();
        if (specificIsPrim) {
            if (genericIsPrim) {
                if (ifHigherThan >= 3) {
                    return 0;
                }
                return _MethodUtil.isWideningPrimitiveNumberConversion(specific, generic) ? 3 : 0;
            }
            if (bugfixed) {
                Class specificAsBoxed = ClassUtil.primitiveClassToBoxingClass(specific);
                if (specificAsBoxed == generic) {
                    return 2;
                }
                if (generic.isAssignableFrom(specificAsBoxed)) {
                    return 4;
                }
                if (ifHigherThan >= 3) {
                    return 0;
                }
                if (Number.class.isAssignableFrom(specificAsBoxed) && Number.class.isAssignableFrom(generic)) {
                    return _MethodUtil.isWideningBoxedNumberConversion(specificAsBoxed, generic) ? 3 : 0;
                }
                return 0;
            }
            return 0;
        }
        if (ifHigherThan >= 3) {
            return 0;
        }
        if (bugfixed && !genericIsPrim && Number.class.isAssignableFrom(specific) && Number.class.isAssignableFrom(generic)) {
            return _MethodUtil.isWideningBoxedNumberConversion(specific, generic) ? 3 : 0;
        }
        return 0;
    }

    private static boolean isWideningPrimitiveNumberConversion(Class source, Class target) {
        if (target == Short.TYPE && source == Byte.TYPE) {
            return true;
        }
        if (target == Integer.TYPE && (source == Short.TYPE || source == Byte.TYPE)) {
            return true;
        }
        if (target == Long.TYPE && (source == Integer.TYPE || source == Short.TYPE || source == Byte.TYPE)) {
            return true;
        }
        if (target == Float.TYPE && (source == Long.TYPE || source == Integer.TYPE || source == Short.TYPE || source == Byte.TYPE)) {
            return true;
        }
        return target == Double.TYPE && (source == Float.TYPE || source == Long.TYPE || source == Integer.TYPE || source == Short.TYPE || source == Byte.TYPE);
    }

    private static boolean isWideningBoxedNumberConversion(Class source, Class target) {
        if (target == Short.class && source == Byte.class) {
            return true;
        }
        if (target == Integer.class && (source == Short.class || source == Byte.class)) {
            return true;
        }
        if (target == Long.class && (source == Integer.class || source == Short.class || source == Byte.class)) {
            return true;
        }
        if (target == Float.class && (source == Long.class || source == Integer.class || source == Short.class || source == Byte.class)) {
            return true;
        }
        return target == Double.class && (source == Float.class || source == Long.class || source == Integer.class || source == Short.class || source == Byte.class);
    }

    public static Set getAssignables(Class c1, Class c2) {
        HashSet s = new HashSet();
        _MethodUtil.collectAssignables(c1, c2, s);
        return s;
    }

    private static void collectAssignables(Class c1, Class c2, Set s) {
        Class sc;
        if (c1.isAssignableFrom(c2)) {
            s.add(c1);
        }
        if ((sc = c1.getSuperclass()) != null) {
            _MethodUtil.collectAssignables(sc, c2, s);
        }
        Class<?>[] itf = c1.getInterfaces();
        for (int i = 0; i < itf.length; ++i) {
            _MethodUtil.collectAssignables(itf[i], c2, s);
        }
    }

    public static Class[] getParameterTypes(Member member) {
        if (member instanceof Method) {
            return ((Method)member).getParameterTypes();
        }
        if (member instanceof Constructor) {
            return ((Constructor)member).getParameterTypes();
        }
        throw new IllegalArgumentException("\"member\" must be Method or Constructor");
    }

    public static boolean isVarargs(Member member) {
        if (member instanceof Method) {
            return ((Method)member).isVarArgs();
        }
        if (member instanceof Constructor) {
            return ((Constructor)member).isVarArgs();
        }
        throw new BugException();
    }

    public static String toString(Member member) {
        String className;
        if (!(member instanceof Method) && !(member instanceof Constructor)) {
            throw new IllegalArgumentException("\"member\" must be a Method or Constructor");
        }
        StringBuilder sb = new StringBuilder();
        if ((member.getModifiers() & 8) != 0) {
            sb.append("static ");
        }
        if ((className = ClassUtil.getShortClassName(member.getDeclaringClass())) != null) {
            sb.append(className);
            sb.append('.');
        }
        sb.append(member.getName());
        sb.append('(');
        Class[] paramTypes = _MethodUtil.getParameterTypes(member);
        for (int i = 0; i < paramTypes.length; ++i) {
            if (i != 0) {
                sb.append(", ");
            }
            String paramTypeDecl = ClassUtil.getShortClassName(paramTypes[i]);
            if (i == paramTypes.length - 1 && paramTypeDecl.endsWith("[]") && _MethodUtil.isVarargs(member)) {
                sb.append(paramTypeDecl.substring(0, paramTypeDecl.length() - 2));
                sb.append("...");
                continue;
            }
            sb.append(paramTypeDecl);
        }
        sb.append(')');
        return sb.toString();
    }

    public static Object[] invocationErrorMessageStart(Member member) {
        return _MethodUtil.invocationErrorMessageStart(member, member instanceof Constructor);
    }

    private static Object[] invocationErrorMessageStart(Object member, boolean isConstructor) {
        return new Object[]{"Java ", isConstructor ? "constructor " : "method ", new _DelayedJQuote(member)};
    }

    public static TemplateModelException newInvocationTemplateModelException(Object object, Member member, Throwable e) {
        return _MethodUtil.newInvocationTemplateModelException(object, member, (member.getModifiers() & 8) != 0, member instanceof Constructor, e);
    }

    public static TemplateModelException newInvocationTemplateModelException(Object object, CallableMemberDescriptor callableMemberDescriptor, Throwable e) {
        return _MethodUtil.newInvocationTemplateModelException(object, new _DelayedConversionToString(callableMemberDescriptor){

            @Override
            protected String doConversion(Object callableMemberDescriptor) {
                return ((CallableMemberDescriptor)callableMemberDescriptor).getDeclaration();
            }
        }, callableMemberDescriptor.isStatic(), callableMemberDescriptor.isConstructor(), e);
    }

    private static TemplateModelException newInvocationTemplateModelException(Object parentObject, Object member, boolean isStatic, boolean isConstructor, Throwable e) {
        Object[] objectArray;
        Throwable cause;
        while (e instanceof InvocationTargetException && (cause = ((InvocationTargetException)e).getTargetException()) != null) {
            e = cause;
        }
        Object[] objectArray2 = new Object[4];
        objectArray2[0] = _MethodUtil.invocationErrorMessageStart(member, isConstructor);
        objectArray2[1] = " threw an exception";
        if (isStatic || isConstructor) {
            objectArray = "";
        } else {
            Object[] objectArray3 = new Object[4];
            objectArray3[0] = " when invoked on ";
            objectArray3[1] = parentObject.getClass();
            objectArray3[2] = " object ";
            objectArray = objectArray3;
            objectArray3[3] = new _DelayedJQuote(parentObject);
        }
        objectArray2[2] = objectArray;
        objectArray2[3] = "; see cause exception in the Java stack trace.";
        return new _TemplateModelException(e, objectArray2);
    }

    public static String getBeanPropertyNameFromReaderMethodName(String name, Class<?> returnType) {
        int start;
        if (name.startsWith("get")) {
            start = 3;
        } else if (returnType == Boolean.TYPE && name.startsWith("is")) {
            start = 2;
        } else {
            return null;
        }
        int ln = name.length();
        if (start == ln) {
            return null;
        }
        char c1 = name.charAt(start);
        return start + 1 < ln && Character.isUpperCase(name.charAt(start + 1)) && Character.isUpperCase(c1) ? name.substring(start) : new StringBuilder(ln - start).append(Character.toLowerCase(c1)).append(name, start + 1, ln).toString();
    }

    public static <T extends Annotation> T getInheritableAnnotation(Class<?> contextClass, Method method, Class<T> annotationClass) {
        T result = method.getAnnotation(annotationClass);
        if (result != null) {
            return result;
        }
        return _MethodUtil.getInheritableMethodAnnotation(contextClass, method.getName(), method.getParameterTypes(), true, annotationClass);
    }

    private static <T extends Annotation> T getInheritableMethodAnnotation(Class<?> contextClass, String methodName, Class<?>[] methodParamTypes, boolean skipCheckingDirectMethod, Class<T> annotationClass) {
        if (!skipCheckingDirectMethod) {
            T result;
            Object similarMethod;
            try {
                similarMethod = contextClass.getMethod(methodName, methodParamTypes);
            }
            catch (NoSuchMethodException e) {
                similarMethod = null;
            }
            if (similarMethod != null && (result = ((Method)similarMethod).getAnnotation(annotationClass)) != null) {
                return result;
            }
        }
        for (Class<?> anInterface : contextClass.getInterfaces()) {
            T result;
            Method similarInterfaceMethod;
            if (anInterface.getName().startsWith("java.")) continue;
            try {
                similarInterfaceMethod = anInterface.getMethod(methodName, methodParamTypes);
            }
            catch (NoSuchMethodException e) {
                similarInterfaceMethod = null;
            }
            if (similarInterfaceMethod == null || (result = similarInterfaceMethod.getAnnotation(annotationClass)) == null) continue;
            return result;
        }
        Class<?> superClass = contextClass.getSuperclass();
        if (superClass == Object.class || superClass == null) {
            return null;
        }
        return _MethodUtil.getInheritableMethodAnnotation(superClass, methodName, methodParamTypes, false, annotationClass);
    }

    public static <T extends Annotation> T getInheritableAnnotation(Class<?> contextClass, Constructor<?> constructor, Class<T> annotationClass) {
        Object result = constructor.getAnnotation(annotationClass);
        if (result != null) {
            return (T)result;
        }
        Class<?>[] paramTypes = constructor.getParameterTypes();
        do {
            if ((contextClass = contextClass.getSuperclass()) == Object.class || contextClass == null) {
                return null;
            }
            try {
                constructor = contextClass.getConstructor(paramTypes);
            }
            catch (NoSuchMethodException e) {
                constructor = null;
            }
        } while (constructor == null || (result = constructor.getAnnotation(annotationClass)) == null);
        return (T)result;
    }

    public static <T extends Annotation> T getInheritableAnnotation(Class<?> contextClass, Field field, Class<T> annotationClass) {
        T result = field.getAnnotation(annotationClass);
        if (result != null) {
            return result;
        }
        return _MethodUtil.getInheritableFieldAnnotation(contextClass, field.getName(), true, annotationClass);
    }

    private static <T extends Annotation> T getInheritableFieldAnnotation(Class<?> contextClass, String fieldName, boolean skipCheckingDirectField, Class<T> annotationClass) {
        if (!skipCheckingDirectField) {
            T result;
            Object similarField;
            try {
                similarField = contextClass.getField(fieldName);
            }
            catch (NoSuchFieldException e) {
                similarField = null;
            }
            if (similarField != null && (result = ((Field)similarField).getAnnotation(annotationClass)) != null) {
                return result;
            }
        }
        for (Class<?> anInterface : contextClass.getInterfaces()) {
            T result;
            Field similarInterfaceField;
            if (anInterface.getName().startsWith("java.")) continue;
            try {
                similarInterfaceField = anInterface.getField(fieldName);
            }
            catch (NoSuchFieldException e) {
                similarInterfaceField = null;
            }
            if (similarInterfaceField == null || (result = similarInterfaceField.getAnnotation(annotationClass)) == null) continue;
            return result;
        }
        Class<?> superClass = contextClass.getSuperclass();
        if (superClass == Object.class || superClass == null) {
            return null;
        }
        return _MethodUtil.getInheritableFieldAnnotation(superClass, fieldName, false, annotationClass);
    }

    public static Method getMethodWithClosestNonSubReturnType(Class<?> returnType, Collection<Method> methods) {
        for (Method method : methods) {
            if (method.getReturnType() != returnType) continue;
            return method;
        }
        if (returnType == Object.class || returnType.isPrimitive()) {
            return null;
        }
        for (Class<?> superClass = returnType.getSuperclass(); superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
            for (Method method : methods) {
                if (method.getReturnType() != superClass) continue;
                return method;
            }
        }
        Method method = _MethodUtil.getMethodWithClosestNonSubInterfaceReturnType(returnType, methods);
        if (method != null) {
            return method;
        }
        for (Method method2 : methods) {
            if (method2.getReturnType() != Object.class) continue;
            return method2;
        }
        return null;
    }

    private static Method getMethodWithClosestNonSubInterfaceReturnType(Class<?> returnType, Collection<Method> methods) {
        HashSet nullResultReturnTypeInterfaces = new HashSet();
        do {
            Method result;
            if ((result = _MethodUtil.getMethodWithClosestNonSubInterfaceReturnType(returnType, methods, nullResultReturnTypeInterfaces)) == null) continue;
            return result;
        } while ((returnType = returnType.getSuperclass()) != null);
        return null;
    }

    private static Method getMethodWithClosestNonSubInterfaceReturnType(Class<?> returnType, Collection<Method> methods, Set<Class<?>> nullResultReturnTypeInterfaces) {
        boolean returnTypeIsInterface = returnType.isInterface();
        if (returnTypeIsInterface) {
            if (nullResultReturnTypeInterfaces.contains(returnType)) {
                return null;
            }
            for (Method method : methods) {
                if (method.getReturnType() != returnType) continue;
                return method;
            }
        }
        for (Class<?> subInterface : returnType.getInterfaces()) {
            Method result = _MethodUtil.getMethodWithClosestNonSubInterfaceReturnType(subInterface, methods, nullResultReturnTypeInterfaces);
            if (result == null) continue;
            return result;
        }
        if (returnTypeIsInterface) {
            nullResultReturnTypeInterfaces.add(returnType);
        }
        return null;
    }
}

