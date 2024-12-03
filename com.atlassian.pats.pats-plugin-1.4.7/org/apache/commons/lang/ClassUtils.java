/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;

public class ClassUtils {
    public static final char PACKAGE_SEPARATOR_CHAR = '.';
    public static final String PACKAGE_SEPARATOR = String.valueOf('.');
    public static final char INNER_CLASS_SEPARATOR_CHAR = '$';
    public static final String INNER_CLASS_SEPARATOR = String.valueOf('$');
    private static final Map primitiveWrapperMap = new HashMap();
    private static final Map wrapperPrimitiveMap;
    private static final Map abbreviationMap;
    private static final Map reverseAbbreviationMap;
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class class$java$lang$Byte;
    static /* synthetic */ Class class$java$lang$Character;
    static /* synthetic */ Class class$java$lang$Short;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$java$lang$Long;
    static /* synthetic */ Class class$java$lang$Double;
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class class$org$apache$commons$lang$ClassUtils;

    private static void addAbbreviation(String primitive, String abbreviation) {
        abbreviationMap.put(primitive, abbreviation);
        reverseAbbreviationMap.put(abbreviation, primitive);
    }

    public static String getShortClassName(Object object, String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return ClassUtils.getShortClassName(object.getClass());
    }

    public static String getShortClassName(Class cls) {
        if (cls == null) {
            return "";
        }
        return ClassUtils.getShortClassName(cls.getName());
    }

    public static String getShortClassName(String className) {
        int lastDotIdx;
        if (className == null) {
            return "";
        }
        if (className.length() == 0) {
            return "";
        }
        StrBuilder arrayPrefix = new StrBuilder();
        if (className.startsWith("[")) {
            while (className.charAt(0) == '[') {
                className = className.substring(1);
                arrayPrefix.append("[]");
            }
            if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
                className = className.substring(1, className.length() - 1);
            }
        }
        if (reverseAbbreviationMap.containsKey(className)) {
            className = (String)reverseAbbreviationMap.get(className);
        }
        int innerIdx = className.indexOf(36, (lastDotIdx = className.lastIndexOf(46)) == -1 ? 0 : lastDotIdx + 1);
        String out = className.substring(lastDotIdx + 1);
        if (innerIdx != -1) {
            out = out.replace('$', '.');
        }
        return out + arrayPrefix;
    }

    public static String getPackageName(Object object, String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return ClassUtils.getPackageName(object.getClass());
    }

    public static String getPackageName(Class cls) {
        if (cls == null) {
            return "";
        }
        return ClassUtils.getPackageName(cls.getName());
    }

    public static String getPackageName(String className) {
        int i;
        if (className == null || className.length() == 0) {
            return "";
        }
        while (className.charAt(0) == '[') {
            className = className.substring(1);
        }
        if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
            className = className.substring(1);
        }
        if ((i = className.lastIndexOf(46)) == -1) {
            return "";
        }
        return className.substring(0, i);
    }

    public static List getAllSuperclasses(Class cls) {
        if (cls == null) {
            return null;
        }
        ArrayList classes = new ArrayList();
        for (Class superclass = cls.getSuperclass(); superclass != null; superclass = superclass.getSuperclass()) {
            classes.add(superclass);
        }
        return classes;
    }

    public static List getAllInterfaces(Class cls) {
        if (cls == null) {
            return null;
        }
        ArrayList interfacesFound = new ArrayList();
        ClassUtils.getAllInterfaces(cls, interfacesFound);
        return interfacesFound;
    }

    private static void getAllInterfaces(Class cls, List interfacesFound) {
        while (cls != null) {
            Class<?>[] interfaces = cls.getInterfaces();
            for (int i = 0; i < interfaces.length; ++i) {
                if (interfacesFound.contains(interfaces[i])) continue;
                interfacesFound.add(interfaces[i]);
                ClassUtils.getAllInterfaces(interfaces[i], interfacesFound);
            }
            cls = cls.getSuperclass();
        }
    }

    public static List convertClassNamesToClasses(List classNames) {
        if (classNames == null) {
            return null;
        }
        ArrayList classes = new ArrayList(classNames.size());
        Iterator it = classNames.iterator();
        while (it.hasNext()) {
            String className = (String)it.next();
            try {
                classes.add(Class.forName(className));
            }
            catch (Exception ex) {
                classes.add(null);
            }
        }
        return classes;
    }

    public static List convertClassesToClassNames(List classes) {
        if (classes == null) {
            return null;
        }
        ArrayList<String> classNames = new ArrayList<String>(classes.size());
        Iterator it = classes.iterator();
        while (it.hasNext()) {
            Class cls = (Class)it.next();
            if (cls == null) {
                classNames.add(null);
                continue;
            }
            classNames.add(cls.getName());
        }
        return classNames;
    }

    public static boolean isAssignable(Class[] classArray, Class[] toClassArray) {
        return ClassUtils.isAssignable(classArray, toClassArray, false);
    }

    public static boolean isAssignable(Class[] classArray, Class[] toClassArray, boolean autoboxing) {
        if (!ArrayUtils.isSameLength(classArray, toClassArray)) {
            return false;
        }
        if (classArray == null) {
            classArray = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        if (toClassArray == null) {
            toClassArray = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        for (int i = 0; i < classArray.length; ++i) {
            if (ClassUtils.isAssignable(classArray[i], toClassArray[i], autoboxing)) continue;
            return false;
        }
        return true;
    }

    public static boolean isAssignable(Class cls, Class toClass) {
        return ClassUtils.isAssignable(cls, toClass, false);
    }

    public static boolean isAssignable(Class cls, Class toClass, boolean autoboxing) {
        if (toClass == null) {
            return false;
        }
        if (cls == null) {
            return !toClass.isPrimitive();
        }
        if (autoboxing) {
            if (cls.isPrimitive() && !toClass.isPrimitive() && (cls = ClassUtils.primitiveToWrapper(cls)) == null) {
                return false;
            }
            if (toClass.isPrimitive() && !cls.isPrimitive() && (cls = ClassUtils.wrapperToPrimitive(cls)) == null) {
                return false;
            }
        }
        if (cls.equals(toClass)) {
            return true;
        }
        if (cls.isPrimitive()) {
            if (!toClass.isPrimitive()) {
                return false;
            }
            if (Integer.TYPE.equals(cls)) {
                return Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            }
            if (Long.TYPE.equals(cls)) {
                return Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            }
            if (Boolean.TYPE.equals(cls)) {
                return false;
            }
            if (Double.TYPE.equals(cls)) {
                return false;
            }
            if (Float.TYPE.equals(cls)) {
                return Double.TYPE.equals(toClass);
            }
            if (Character.TYPE.equals(cls)) {
                return Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            }
            if (Short.TYPE.equals(cls)) {
                return Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            }
            if (Byte.TYPE.equals(cls)) {
                return Short.TYPE.equals(toClass) || Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            }
            return false;
        }
        return toClass.isAssignableFrom(cls);
    }

    public static Class primitiveToWrapper(Class cls) {
        Class convertedClass = cls;
        if (cls != null && cls.isPrimitive()) {
            convertedClass = (Class)primitiveWrapperMap.get(cls);
        }
        return convertedClass;
    }

    public static Class[] primitivesToWrappers(Class[] classes) {
        if (classes == null) {
            return null;
        }
        if (classes.length == 0) {
            return classes;
        }
        Class[] convertedClasses = new Class[classes.length];
        for (int i = 0; i < classes.length; ++i) {
            convertedClasses[i] = ClassUtils.primitiveToWrapper(classes[i]);
        }
        return convertedClasses;
    }

    public static Class wrapperToPrimitive(Class cls) {
        return (Class)wrapperPrimitiveMap.get(cls);
    }

    public static Class[] wrappersToPrimitives(Class[] classes) {
        if (classes == null) {
            return null;
        }
        if (classes.length == 0) {
            return classes;
        }
        Class[] convertedClasses = new Class[classes.length];
        for (int i = 0; i < classes.length; ++i) {
            convertedClasses[i] = ClassUtils.wrapperToPrimitive(classes[i]);
        }
        return convertedClasses;
    }

    public static boolean isInnerClass(Class cls) {
        if (cls == null) {
            return false;
        }
        return cls.getName().indexOf(36) >= 0;
    }

    public static Class getClass(ClassLoader classLoader, String className, boolean initialize) throws ClassNotFoundException {
        try {
            Class<?> clazz;
            if (abbreviationMap.containsKey(className)) {
                String clsName = "[" + abbreviationMap.get(className);
                clazz = Class.forName(clsName, initialize, classLoader).getComponentType();
            } else {
                clazz = Class.forName(ClassUtils.toCanonicalName(className), initialize, classLoader);
            }
            return clazz;
        }
        catch (ClassNotFoundException ex) {
            int lastDotIndex = className.lastIndexOf(46);
            if (lastDotIndex != -1) {
                try {
                    return ClassUtils.getClass(classLoader, className.substring(0, lastDotIndex) + '$' + className.substring(lastDotIndex + 1), initialize);
                }
                catch (ClassNotFoundException ex2) {
                    // empty catch block
                }
            }
            throw ex;
        }
    }

    public static Class getClass(ClassLoader classLoader, String className) throws ClassNotFoundException {
        return ClassUtils.getClass(classLoader, className, true);
    }

    public static Class getClass(String className) throws ClassNotFoundException {
        return ClassUtils.getClass(className, true);
    }

    public static Class getClass(String className, boolean initialize) throws ClassNotFoundException {
        ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
        ClassLoader loader = contextCL == null ? (class$org$apache$commons$lang$ClassUtils == null ? (class$org$apache$commons$lang$ClassUtils = ClassUtils.class$("org.apache.commons.lang.ClassUtils")) : class$org$apache$commons$lang$ClassUtils).getClassLoader() : contextCL;
        return ClassUtils.getClass(loader, className, initialize);
    }

    public static Method getPublicMethod(Class cls, String methodName, Class[] parameterTypes) throws SecurityException, NoSuchMethodException {
        Method declaredMethod = cls.getMethod(methodName, parameterTypes);
        if (Modifier.isPublic(declaredMethod.getDeclaringClass().getModifiers())) {
            return declaredMethod;
        }
        ArrayList candidateClasses = new ArrayList();
        candidateClasses.addAll(ClassUtils.getAllInterfaces(cls));
        candidateClasses.addAll(ClassUtils.getAllSuperclasses(cls));
        Iterator it = candidateClasses.iterator();
        while (it.hasNext()) {
            Method candidateMethod;
            Class candidateClass = (Class)it.next();
            if (!Modifier.isPublic(candidateClass.getModifiers())) continue;
            try {
                candidateMethod = candidateClass.getMethod(methodName, parameterTypes);
            }
            catch (NoSuchMethodException ex) {
                continue;
            }
            if (!Modifier.isPublic(candidateMethod.getDeclaringClass().getModifiers())) continue;
            return candidateMethod;
        }
        throw new NoSuchMethodException("Can't find a public method for " + methodName + " " + ArrayUtils.toString(parameterTypes));
    }

    private static String toCanonicalName(String className) {
        if ((className = StringUtils.deleteWhitespace(className)) == null) {
            throw new NullArgumentException("className");
        }
        if (className.endsWith("[]")) {
            StrBuilder classNameBuffer = new StrBuilder();
            while (className.endsWith("[]")) {
                className = className.substring(0, className.length() - 2);
                classNameBuffer.append("[");
            }
            String abbreviation = (String)abbreviationMap.get(className);
            if (abbreviation != null) {
                classNameBuffer.append(abbreviation);
            } else {
                classNameBuffer.append("L").append(className).append(";");
            }
            className = classNameBuffer.toString();
        }
        return className;
    }

    public static Class[] toClass(Object[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        Class[] classes = new Class[array.length];
        for (int i = 0; i < array.length; ++i) {
            classes[i] = array[i] == null ? null : array[i].getClass();
        }
        return classes;
    }

    public static String getShortCanonicalName(Object object, String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return ClassUtils.getShortCanonicalName(object.getClass().getName());
    }

    public static String getShortCanonicalName(Class cls) {
        if (cls == null) {
            return "";
        }
        return ClassUtils.getShortCanonicalName(cls.getName());
    }

    public static String getShortCanonicalName(String canonicalName) {
        return ClassUtils.getShortClassName(ClassUtils.getCanonicalName(canonicalName));
    }

    public static String getPackageCanonicalName(Object object, String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return ClassUtils.getPackageCanonicalName(object.getClass().getName());
    }

    public static String getPackageCanonicalName(Class cls) {
        if (cls == null) {
            return "";
        }
        return ClassUtils.getPackageCanonicalName(cls.getName());
    }

    public static String getPackageCanonicalName(String canonicalName) {
        return ClassUtils.getPackageName(ClassUtils.getCanonicalName(canonicalName));
    }

    private static String getCanonicalName(String className) {
        if ((className = StringUtils.deleteWhitespace(className)) == null) {
            return null;
        }
        int dim = 0;
        while (className.startsWith("[")) {
            ++dim;
            className = className.substring(1);
        }
        if (dim < 1) {
            return className;
        }
        if (className.startsWith("L")) {
            className = className.substring(1, className.endsWith(";") ? className.length() - 1 : className.length());
        } else if (className.length() > 0) {
            className = (String)reverseAbbreviationMap.get(className.substring(0, 1));
        }
        StrBuilder canonicalClassNameBuffer = new StrBuilder(className);
        for (int i = 0; i < dim; ++i) {
            canonicalClassNameBuffer.append("[]");
        }
        return canonicalClassNameBuffer.toString();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        primitiveWrapperMap.put(Boolean.TYPE, class$java$lang$Boolean == null ? (class$java$lang$Boolean = ClassUtils.class$("java.lang.Boolean")) : class$java$lang$Boolean);
        primitiveWrapperMap.put(Byte.TYPE, class$java$lang$Byte == null ? (class$java$lang$Byte = ClassUtils.class$("java.lang.Byte")) : class$java$lang$Byte);
        primitiveWrapperMap.put(Character.TYPE, class$java$lang$Character == null ? (class$java$lang$Character = ClassUtils.class$("java.lang.Character")) : class$java$lang$Character);
        primitiveWrapperMap.put(Short.TYPE, class$java$lang$Short == null ? (class$java$lang$Short = ClassUtils.class$("java.lang.Short")) : class$java$lang$Short);
        primitiveWrapperMap.put(Integer.TYPE, class$java$lang$Integer == null ? (class$java$lang$Integer = ClassUtils.class$("java.lang.Integer")) : class$java$lang$Integer);
        primitiveWrapperMap.put(Long.TYPE, class$java$lang$Long == null ? (class$java$lang$Long = ClassUtils.class$("java.lang.Long")) : class$java$lang$Long);
        primitiveWrapperMap.put(Double.TYPE, class$java$lang$Double == null ? (class$java$lang$Double = ClassUtils.class$("java.lang.Double")) : class$java$lang$Double);
        primitiveWrapperMap.put(Float.TYPE, class$java$lang$Float == null ? (class$java$lang$Float = ClassUtils.class$("java.lang.Float")) : class$java$lang$Float);
        primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
        wrapperPrimitiveMap = new HashMap();
        Iterator it = primitiveWrapperMap.keySet().iterator();
        while (it.hasNext()) {
            Class wrapperClass;
            Class primitiveClass = (Class)it.next();
            if (primitiveClass.equals(wrapperClass = (Class)primitiveWrapperMap.get(primitiveClass))) continue;
            wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
        }
        abbreviationMap = new HashMap();
        reverseAbbreviationMap = new HashMap();
        ClassUtils.addAbbreviation("int", "I");
        ClassUtils.addAbbreviation("boolean", "Z");
        ClassUtils.addAbbreviation("float", "F");
        ClassUtils.addAbbreviation("long", "J");
        ClassUtils.addAbbreviation("short", "S");
        ClassUtils.addAbbreviation("byte", "B");
        ClassUtils.addAbbreviation("double", "D");
        ClassUtils.addAbbreviation("char", "C");
    }
}

