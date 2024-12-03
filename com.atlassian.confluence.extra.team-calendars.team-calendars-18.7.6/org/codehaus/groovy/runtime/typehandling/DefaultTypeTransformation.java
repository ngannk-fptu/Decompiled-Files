/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.typehandling;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.GroovyRuntimeException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.reflection.stdclasses.CachedSAMClass;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.InvokerInvocationException;
import org.codehaus.groovy.runtime.IteratorClosureAdapter;
import org.codehaus.groovy.runtime.MethodClosure;
import org.codehaus.groovy.runtime.NullObject;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.GroovyCastException;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class DefaultTypeTransformation {
    protected static final Object[] EMPTY_ARGUMENTS = new Object[0];
    protected static final BigInteger ONE_NEG = new BigInteger("-1");
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    public static byte byteUnbox(Object value) {
        Number n = DefaultTypeTransformation.castToNumber(value, Byte.TYPE);
        return n.byteValue();
    }

    public static char charUnbox(Object value) {
        return ShortTypeHandling.castToChar(value).charValue();
    }

    public static short shortUnbox(Object value) {
        Number n = DefaultTypeTransformation.castToNumber(value, Short.TYPE);
        return n.shortValue();
    }

    public static int intUnbox(Object value) {
        Number n = DefaultTypeTransformation.castToNumber(value, Integer.TYPE);
        return n.intValue();
    }

    public static boolean booleanUnbox(Object value) {
        return DefaultTypeTransformation.castToBoolean(value);
    }

    public static long longUnbox(Object value) {
        Number n = DefaultTypeTransformation.castToNumber(value, Long.TYPE);
        return n.longValue();
    }

    public static float floatUnbox(Object value) {
        Number n = DefaultTypeTransformation.castToNumber(value, Float.TYPE);
        return n.floatValue();
    }

    public static double doubleUnbox(Object value) {
        Number n = DefaultTypeTransformation.castToNumber(value, Double.TYPE);
        return n.doubleValue();
    }

    @Deprecated
    public static Object box(boolean value) {
        return value ? Boolean.TRUE : Boolean.FALSE;
    }

    @Deprecated
    public static Object box(byte value) {
        return value;
    }

    @Deprecated
    public static Object box(char value) {
        return Character.valueOf(value);
    }

    @Deprecated
    public static Object box(short value) {
        return value;
    }

    @Deprecated
    public static Object box(int value) {
        return value;
    }

    @Deprecated
    public static Object box(long value) {
        return value;
    }

    @Deprecated
    public static Object box(float value) {
        return Float.valueOf(value);
    }

    @Deprecated
    public static Object box(double value) {
        return value;
    }

    public static Number castToNumber(Object object) {
        return DefaultTypeTransformation.castToNumber(object, Number.class);
    }

    public static Number castToNumber(Object object, Class type) {
        if (object instanceof Number) {
            return (Number)object;
        }
        if (object instanceof Character) {
            return (int)((Character)object).charValue();
        }
        if (object instanceof GString) {
            String c = ((GString)object).toString();
            if (c.length() == 1) {
                return (int)c.charAt(0);
            }
            throw new GroovyCastException((Object)c, type);
        }
        if (object instanceof String) {
            String c = (String)object;
            if (c.length() == 1) {
                return (int)c.charAt(0);
            }
            throw new GroovyCastException((Object)c, type);
        }
        throw new GroovyCastException(object, type);
    }

    public static boolean castToBoolean(Object object) {
        if (object == null) {
            return false;
        }
        if (object.getClass() == Boolean.class) {
            return (Boolean)object;
        }
        return (Boolean)InvokerHelper.invokeMethod(object, "asBoolean", InvokerHelper.EMPTY_ARGS);
    }

    @Deprecated
    public static char castToChar(Object object) {
        if (object instanceof Character) {
            return ((Character)object).charValue();
        }
        if (object instanceof Number) {
            Number value = (Number)object;
            return (char)value.intValue();
        }
        String text = object.toString();
        if (text.length() == 1) {
            return text.charAt(0);
        }
        throw new GroovyCastException((Object)text, Character.TYPE);
    }

    public static Object castToType(Object object, Class type) {
        if (object == null) {
            return null;
        }
        if (type == Object.class) {
            return object;
        }
        Class<?> aClass = object.getClass();
        if (type == aClass) {
            return object;
        }
        if (type.isAssignableFrom(aClass)) {
            return object;
        }
        if (ReflectionCache.isArray(type)) {
            return DefaultTypeTransformation.asArray(object, type);
        }
        if (type.isEnum()) {
            return ShortTypeHandling.castToEnum(object, type);
        }
        if (Collection.class.isAssignableFrom(type)) {
            return DefaultTypeTransformation.continueCastOnCollection(object, type);
        }
        if (type == String.class) {
            return ShortTypeHandling.castToString(object);
        }
        if (type == Character.class) {
            return ShortTypeHandling.castToChar(object);
        }
        if (type == Boolean.class) {
            return DefaultTypeTransformation.castToBoolean(object);
        }
        if (type == Class.class) {
            return ShortTypeHandling.castToClass(object);
        }
        if (type.isPrimitive()) {
            return DefaultTypeTransformation.castToPrimitive(object, type);
        }
        return DefaultTypeTransformation.continueCastOnNumber(object, type);
    }

    private static Object continueCastOnCollection(Object object, Class type) {
        int modifiers = type.getModifiers();
        if (object instanceof Collection && type.isAssignableFrom(LinkedHashSet.class) && (type == LinkedHashSet.class || Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers))) {
            return new LinkedHashSet((Collection)object);
        }
        if (object.getClass().isArray()) {
            Collection<Object> answer;
            if (type.isAssignableFrom(ArrayList.class) && (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers))) {
                answer = new ArrayList();
            } else if (type.isAssignableFrom(LinkedHashSet.class) && (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers))) {
                answer = new LinkedHashSet();
            } else {
                try {
                    answer = (Collection)type.newInstance();
                }
                catch (Exception e) {
                    throw new GroovyCastException("Could not instantiate instance of: " + type.getName() + ". Reason: " + e);
                }
            }
            int length = Array.getLength(object);
            for (int i = 0; i < length; ++i) {
                Object element = Array.get(object, i);
                answer.add(element);
            }
            return answer;
        }
        return DefaultTypeTransformation.continueCastOnNumber(object, type);
    }

    private static Object continueCastOnNumber(Object object, Class type) {
        if (Number.class.isAssignableFrom(type)) {
            Number n = DefaultTypeTransformation.castToNumber(object, type);
            if (type == Byte.class) {
                return n.byteValue();
            }
            if (type == Character.class) {
                return Character.valueOf((char)n.intValue());
            }
            if (type == Short.class) {
                return n.shortValue();
            }
            if (type == Integer.class) {
                return n.intValue();
            }
            if (type == Long.class) {
                return n.longValue();
            }
            if (type == Float.class) {
                return Float.valueOf(n.floatValue());
            }
            if (type == Double.class) {
                Double answer = n.doubleValue();
                if (!(n instanceof Double || answer != Double.NEGATIVE_INFINITY && answer != Double.POSITIVE_INFINITY)) {
                    throw new GroovyRuntimeException("Automatic coercion of " + n.getClass().getName() + " value " + n + " to double failed.  Value is out of range.");
                }
                return answer;
            }
            if (type == BigDecimal.class) {
                if (n instanceof Float || n instanceof Double) {
                    return new BigDecimal(n.doubleValue());
                }
                return new BigDecimal(n.toString());
            }
            if (type == BigInteger.class) {
                if (object instanceof Float || object instanceof Double) {
                    BigDecimal bd = new BigDecimal(n.doubleValue());
                    return bd.toBigInteger();
                }
                if (object instanceof BigDecimal) {
                    return ((BigDecimal)object).toBigInteger();
                }
                return new BigInteger(n.toString());
            }
        }
        return DefaultTypeTransformation.continueCastOnSAM(object, type);
    }

    private static Object castToPrimitive(Object object, Class type) {
        if (type == Boolean.TYPE) {
            return DefaultTypeTransformation.booleanUnbox(object);
        }
        if (type == Byte.TYPE) {
            return DefaultTypeTransformation.byteUnbox(object);
        }
        if (type == Character.TYPE) {
            return Character.valueOf(DefaultTypeTransformation.charUnbox(object));
        }
        if (type == Short.TYPE) {
            return DefaultTypeTransformation.shortUnbox(object);
        }
        if (type == Integer.TYPE) {
            return DefaultTypeTransformation.intUnbox(object);
        }
        if (type == Long.TYPE) {
            return DefaultTypeTransformation.longUnbox(object);
        }
        if (type == Float.TYPE) {
            return Float.valueOf(DefaultTypeTransformation.floatUnbox(object));
        }
        if (type == Double.TYPE) {
            Double answer = new Double(DefaultTypeTransformation.doubleUnbox(object));
            if (!(object instanceof Double || answer != Double.NEGATIVE_INFINITY && answer != Double.POSITIVE_INFINITY)) {
                throw new GroovyRuntimeException("Automatic coercion of " + object.getClass().getName() + " value " + object + " to double failed.  Value is out of range.");
            }
            return answer;
        }
        throw new GroovyCastException(object, type);
    }

    private static Object continueCastOnSAM(Object object, Class type) {
        Method m;
        if (object instanceof Closure && (m = CachedSAMClass.getSAMMethod(type)) != null) {
            return CachedSAMClass.coerceToSAM((Closure)object, m, type, type.isInterface());
        }
        Object[] args = null;
        if (object instanceof Collection) {
            Collection collection = (Collection)object;
            args = collection.toArray();
        } else if (object instanceof Object[]) {
            args = (Object[])object;
        } else if (object instanceof Map) {
            args = new Object[]{object};
        }
        Exception nested = null;
        if (args != null) {
            try {
                return InvokerHelper.invokeConstructorOf(type, (Object)args);
            }
            catch (InvokerInvocationException iie) {
                throw iie;
            }
            catch (GroovyRuntimeException e) {
                if (e.getMessage().contains("Could not find matching constructor for")) {
                    try {
                        return InvokerHelper.invokeConstructorOf(type, object);
                    }
                    catch (InvokerInvocationException iie) {
                        throw iie;
                    }
                    catch (Exception ex) {
                        nested = e;
                    }
                } else {
                    nested = e;
                }
            }
            catch (Exception e) {
                nested = e;
            }
        }
        GroovyCastException gce = nested != null ? new GroovyCastException(object, type, nested) : new GroovyCastException(object, type);
        throw gce;
    }

    public static Object asArray(Object object, Class type) {
        if (type.isAssignableFrom(object.getClass())) {
            return object;
        }
        Collection list = DefaultTypeTransformation.asCollection(object);
        int size = list.size();
        Class<?> elementType = type.getComponentType();
        Object array = Array.newInstance(elementType, size);
        int idx = 0;
        for (Object element : list) {
            Array.set(array, idx, DefaultTypeTransformation.castToType(element, elementType));
            ++idx;
        }
        return array;
    }

    public static <T> Collection<T> asCollection(T[] value) {
        return DefaultTypeTransformation.arrayAsCollection(value);
    }

    public static Collection asCollection(Object value) {
        if (value == null) {
            return Collections.EMPTY_LIST;
        }
        if (value instanceof Collection) {
            return (Collection)value;
        }
        if (value instanceof Map) {
            Map map = (Map)value;
            return map.entrySet();
        }
        if (value.getClass().isArray()) {
            return DefaultTypeTransformation.arrayAsCollection(value);
        }
        if (value instanceof MethodClosure) {
            MethodClosure method = (MethodClosure)value;
            IteratorClosureAdapter adapter = new IteratorClosureAdapter(method.getDelegate());
            method.call((Object)adapter);
            return adapter.asList();
        }
        if (value instanceof String) {
            return StringGroovyMethods.toList((String)value);
        }
        if (value instanceof GString) {
            return StringGroovyMethods.toList(value.toString());
        }
        if (value instanceof File) {
            try {
                return ResourceGroovyMethods.readLines((File)value);
            }
            catch (IOException e) {
                throw new GroovyRuntimeException("Error reading file: " + value, e);
            }
        }
        if (value instanceof Class && ((Class)value).isEnum()) {
            Object[] values = (Object[])InvokerHelper.invokeMethod(value, "values", EMPTY_OBJECT_ARRAY);
            return Arrays.asList(values);
        }
        return Collections.singletonList(value);
    }

    public static Collection arrayAsCollection(Object value) {
        if (value.getClass().getComponentType().isPrimitive()) {
            return DefaultTypeTransformation.primitiveArrayToList(value);
        }
        return DefaultTypeTransformation.arrayAsCollection((Object[])value);
    }

    public static <T> Collection<T> arrayAsCollection(T[] value) {
        return Arrays.asList(value);
    }

    @Deprecated
    public static boolean isEnumSubclass(Object value) {
        if (value instanceof Class) {
            for (Class superclass = ((Class)value).getSuperclass(); superclass != null; superclass = superclass.getSuperclass()) {
                if (!superclass.getName().equals("java.lang.Enum")) continue;
                return true;
            }
        }
        return false;
    }

    public static List primitiveArrayToList(Object array) {
        int size = Array.getLength(array);
        ArrayList<Object> list = new ArrayList<Object>(size);
        for (int i = 0; i < size; ++i) {
            Object item = Array.get(array, i);
            if (item != null && item.getClass().isArray() && item.getClass().getComponentType().isPrimitive()) {
                item = DefaultTypeTransformation.primitiveArrayToList(item);
            }
            list.add(item);
        }
        return list;
    }

    public static Object[] primitiveArrayBox(Object array) {
        int size = Array.getLength(array);
        Object[] ret = (Object[])Array.newInstance(ReflectionCache.autoboxType(array.getClass().getComponentType()), size);
        for (int i = 0; i < size; ++i) {
            ret[i] = Array.get(array, i);
        }
        return ret;
    }

    public static int compareTo(Object left, Object right) {
        return DefaultTypeTransformation.compareToWithEqualityCheck(left, right, false);
    }

    private static int compareToWithEqualityCheck(Object left, Object right, boolean equalityCheckOnly) {
        block21: {
            if (left == right) {
                return 0;
            }
            if (left == null) {
                return -1;
            }
            if (right == null) {
                return 1;
            }
            if (left instanceof Comparable) {
                if (left instanceof Number) {
                    if (right instanceof Character || right instanceof Number) {
                        return DefaultGroovyMethods.compareTo((Number)left, DefaultTypeTransformation.castToNumber(right));
                    }
                    if (DefaultTypeTransformation.isValidCharacterString(right)) {
                        return DefaultGroovyMethods.compareTo((Number)left, ShortTypeHandling.castToChar(right));
                    }
                } else if (left instanceof Character) {
                    if (DefaultTypeTransformation.isValidCharacterString(right)) {
                        return DefaultGroovyMethods.compareTo((Character)left, ShortTypeHandling.castToChar(right));
                    }
                    if (right instanceof Number) {
                        return DefaultGroovyMethods.compareTo((Character)left, (Number)right);
                    }
                } else if (right instanceof Number) {
                    if (DefaultTypeTransformation.isValidCharacterString(left)) {
                        return DefaultGroovyMethods.compareTo(ShortTypeHandling.castToChar(left), (Number)right);
                    }
                } else {
                    if (left instanceof String && right instanceof Character) {
                        return ((String)left).compareTo(right.toString());
                    }
                    if (left instanceof String && right instanceof GString) {
                        return ((String)left).compareTo(right.toString());
                    }
                }
                if (!equalityCheckOnly || left.getClass().isAssignableFrom(right.getClass()) || right.getClass() != Object.class && right.getClass().isAssignableFrom(left.getClass()) || left instanceof GString && right instanceof String) {
                    Comparable comparable = (Comparable)left;
                    try {
                        return comparable.compareTo(right);
                    }
                    catch (ClassCastException cce) {
                        if (equalityCheckOnly) break block21;
                        throw cce;
                    }
                }
            }
        }
        if (equalityCheckOnly) {
            return -1;
        }
        throw new GroovyRuntimeException(MessageFormat.format("Cannot compare {0} with value ''{1}'' and {2} with value ''{3}''", left.getClass().getName(), left, right.getClass().getName(), right));
    }

    public static boolean compareEqual(Object left, Object right) {
        if (left == right) {
            return true;
        }
        if (left == null) {
            return right instanceof NullObject;
        }
        if (right == null) {
            return left instanceof NullObject;
        }
        if (left instanceof Comparable) {
            return DefaultTypeTransformation.compareToWithEqualityCheck(left, right, true) == 0;
        }
        Class<?> leftClass = left.getClass();
        Class<?> rightClass = right.getClass();
        if (leftClass.isArray() && rightClass.isArray()) {
            return DefaultTypeTransformation.compareArrayEqual(left, right);
        }
        if (leftClass.isArray() && leftClass.getComponentType().isPrimitive()) {
            left = DefaultTypeTransformation.primitiveArrayToList(left);
        }
        if (rightClass.isArray() && rightClass.getComponentType().isPrimitive()) {
            right = DefaultTypeTransformation.primitiveArrayToList(right);
        }
        if (left instanceof Object[] && right instanceof List) {
            return DefaultGroovyMethods.equals((Object[])left, (List)right);
        }
        if (left instanceof List && right instanceof Object[]) {
            return DefaultGroovyMethods.equals((List)left, (Object[])right);
        }
        if (left instanceof List && right instanceof List) {
            return DefaultGroovyMethods.equals((List)left, (List)right);
        }
        if (left instanceof Map.Entry && right instanceof Map.Entry) {
            Object v2;
            Object v1;
            Object k2;
            Object k1 = ((Map.Entry)left).getKey();
            return (k1 == (k2 = ((Map.Entry)right).getKey()) || k1 != null && k1.equals(k2)) && ((v1 = ((Map.Entry)left).getValue()) == (v2 = ((Map.Entry)right).getValue()) || v1 != null && DefaultTypeTransformation.compareEqual(v1, v2));
        }
        return (Boolean)InvokerHelper.invokeMethod(left, "equals", right);
    }

    public static boolean compareArrayEqual(Object left, Object right) {
        if (left == null) {
            return right == null;
        }
        if (right == null) {
            return false;
        }
        if (Array.getLength(left) != Array.getLength(right)) {
            return false;
        }
        for (int i = 0; i < Array.getLength(left); ++i) {
            Object r;
            Object l = Array.get(left, i);
            if (DefaultTypeTransformation.compareEqual(l, r = Array.get(right, i))) continue;
            return false;
        }
        return true;
    }

    private static boolean isValidCharacterString(Object value) {
        String s;
        return value instanceof String && (s = (String)value).length() == 1;
    }

    @Deprecated
    public static int[] convertToIntArray(Object a) {
        int[] ans = null;
        if (a.getClass().getName().equals("[I")) {
            ans = (int[])a;
        } else {
            Object[] ia = (Object[])a;
            ans = new int[ia.length];
            for (int i = 0; i < ia.length; ++i) {
                if (ia[i] == null) continue;
                ans[i] = ((Number)ia[i]).intValue();
            }
        }
        return ans;
    }

    @Deprecated
    public static boolean[] convertToBooleanArray(Object a) {
        boolean[] ans = null;
        if (a instanceof boolean[]) {
            ans = (boolean[])a;
        } else {
            Object[] ia = (Object[])a;
            ans = new boolean[ia.length];
            for (int i = 0; i < ia.length; ++i) {
                if (ia[i] == null) continue;
                ans[i] = (Boolean)ia[i];
            }
        }
        return ans;
    }

    @Deprecated
    public static byte[] convertToByteArray(Object a) {
        byte[] ans = null;
        if (a instanceof byte[]) {
            ans = (byte[])a;
        } else {
            Object[] ia = (Object[])a;
            ans = new byte[ia.length];
            for (int i = 0; i < ia.length; ++i) {
                if (ia[i] == null) continue;
                ans[i] = ((Number)ia[i]).byteValue();
            }
        }
        return ans;
    }

    @Deprecated
    public static short[] convertToShortArray(Object a) {
        short[] ans = null;
        if (a instanceof short[]) {
            ans = (short[])a;
        } else {
            Object[] ia = (Object[])a;
            ans = new short[ia.length];
            for (int i = 0; i < ia.length; ++i) {
                ans[i] = ((Number)ia[i]).shortValue();
            }
        }
        return ans;
    }

    @Deprecated
    public static char[] convertToCharArray(Object a) {
        char[] ans = null;
        if (a instanceof char[]) {
            ans = (char[])a;
        } else {
            Object[] ia = (Object[])a;
            ans = new char[ia.length];
            for (int i = 0; i < ia.length; ++i) {
                if (ia[i] == null) continue;
                ans[i] = ((Character)ia[i]).charValue();
            }
        }
        return ans;
    }

    @Deprecated
    public static long[] convertToLongArray(Object a) {
        long[] ans = null;
        if (a instanceof long[]) {
            ans = (long[])a;
        } else {
            Object[] ia = (Object[])a;
            ans = new long[ia.length];
            for (int i = 0; i < ia.length; ++i) {
                if (ia[i] == null) continue;
                ans[i] = ((Number)ia[i]).longValue();
            }
        }
        return ans;
    }

    @Deprecated
    public static float[] convertToFloatArray(Object a) {
        float[] ans = null;
        if (a instanceof float[]) {
            ans = (float[])a;
        } else {
            Object[] ia = (Object[])a;
            ans = new float[ia.length];
            for (int i = 0; i < ia.length; ++i) {
                if (ia[i] == null) continue;
                ans[i] = ((Number)ia[i]).floatValue();
            }
        }
        return ans;
    }

    @Deprecated
    public static double[] convertToDoubleArray(Object a) {
        double[] ans = null;
        if (a instanceof double[]) {
            ans = (double[])a;
        } else {
            Object[] ia = (Object[])a;
            ans = new double[ia.length];
            for (int i = 0; i < ia.length; ++i) {
                if (ia[i] == null) continue;
                ans[i] = ((Number)ia[i]).doubleValue();
            }
        }
        return ans;
    }

    @Deprecated
    public static Object convertToPrimitiveArray(Object a, Class type) {
        if (type == Byte.TYPE) {
            return DefaultTypeTransformation.convertToByteArray(a);
        }
        if (type == Boolean.TYPE) {
            return DefaultTypeTransformation.convertToBooleanArray(a);
        }
        if (type == Short.TYPE) {
            return DefaultTypeTransformation.convertToShortArray(a);
        }
        if (type == Character.TYPE) {
            return DefaultTypeTransformation.convertToCharArray(a);
        }
        if (type == Integer.TYPE) {
            return DefaultTypeTransformation.convertToIntArray(a);
        }
        if (type == Long.TYPE) {
            return DefaultTypeTransformation.convertToLongArray(a);
        }
        if (type == Float.TYPE) {
            return DefaultTypeTransformation.convertToFloatArray(a);
        }
        if (type == Double.TYPE) {
            return DefaultTypeTransformation.convertToDoubleArray(a);
        }
        return a;
    }

    @Deprecated
    public static Character getCharFromSizeOneString(Object value) {
        if (value instanceof GString) {
            value = value.toString();
        }
        if (value instanceof String) {
            String s = (String)value;
            if (s.length() != 1) {
                throw new IllegalArgumentException("String of length 1 expected but got a bigger one");
            }
            return Character.valueOf(s.charAt(0));
        }
        return (Character)value;
    }

    public static Object castToVargsArray(Object[] origin, int firstVargsPos, Class<?> arrayType) {
        Class<?> componentType = arrayType.getComponentType();
        if (firstVargsPos >= origin.length) {
            return Array.newInstance(componentType, 0);
        }
        int length = origin.length - firstVargsPos;
        if (length == 1 && arrayType.isInstance(origin[firstVargsPos])) {
            return origin[firstVargsPos];
        }
        Object newArray = Array.newInstance(componentType, length);
        for (int i = 0; i < length; ++i) {
            Object convertedValue = DefaultTypeTransformation.castToType(origin[firstVargsPos + i], componentType);
            Array.set(newArray, i, convertedValue);
        }
        return newArray;
    }
}

