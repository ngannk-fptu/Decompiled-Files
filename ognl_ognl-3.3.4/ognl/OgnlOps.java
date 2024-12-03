/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Enumeration;
import ognl.ElementsAccessor;
import ognl.NumericTypes;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.enhance.UnsupportedCompilationException;

public abstract class OgnlOps
implements NumericTypes {
    public static int compareWithConversion(Object v1, Object v2) {
        int result;
        if (v1 == v2) {
            result = 0;
        } else {
            int t1 = OgnlOps.getNumericType(v1);
            int t2 = OgnlOps.getNumericType(v2);
            int type = OgnlOps.getNumericType(t1, t2, true);
            switch (type) {
                case 6: {
                    result = OgnlOps.bigIntValue(v1).compareTo(OgnlOps.bigIntValue(v2));
                    break;
                }
                case 9: {
                    result = OgnlOps.bigDecValue(v1).compareTo(OgnlOps.bigDecValue(v2));
                    break;
                }
                case 10: {
                    if (t1 == 10 && t2 == 10) {
                        if (v1 instanceof Comparable && v1.getClass().isAssignableFrom(v2.getClass())) {
                            result = ((Comparable)v1).compareTo(v2);
                            break;
                        }
                        if (v1 instanceof Enum && v2 instanceof Enum && (v1.getClass() == v2.getClass() || ((Enum)v1).getDeclaringClass() == ((Enum)v2).getDeclaringClass())) {
                            result = ((Enum)v1).compareTo((Enum)v2);
                            break;
                        }
                        throw new IllegalArgumentException("invalid comparison: " + v1.getClass().getName() + " and " + v2.getClass().getName());
                    }
                }
                case 7: 
                case 8: {
                    double dv1 = OgnlOps.doubleValue(v1);
                    double dv2 = OgnlOps.doubleValue(v2);
                    return dv1 == dv2 ? 0 : (dv1 < dv2 ? -1 : 1);
                }
                default: {
                    long lv1 = OgnlOps.longValue(v1);
                    long lv2 = OgnlOps.longValue(v2);
                    return lv1 == lv2 ? 0 : (lv1 < lv2 ? -1 : 1);
                }
            }
        }
        return result;
    }

    public static boolean isEqual(Object object1, Object object2) {
        boolean result = false;
        if (object1 == object2) {
            result = true;
        } else if (object1 != null && object2 != null) {
            if (object1.getClass().isArray()) {
                if (object2.getClass().isArray() && object2.getClass() == object1.getClass()) {
                    boolean bl = result = Array.getLength(object1) == Array.getLength(object2);
                    if (result) {
                        int icount = Array.getLength(object1);
                        for (int i = 0; result && i < icount; ++i) {
                            result = OgnlOps.isEqual(Array.get(object1, i), Array.get(object2, i));
                        }
                    }
                }
            } else {
                int t1 = OgnlOps.getNumericType(object1);
                int t2 = OgnlOps.getNumericType(object2);
                result = t1 == 10 && t2 == 10 && (!(object1 instanceof Comparable) || !(object2 instanceof Comparable)) ? object1.equals(object2) : OgnlOps.compareWithConversion(object1, object2) == 0;
            }
        }
        return result;
    }

    public static boolean booleanValue(boolean value) {
        return value;
    }

    public static boolean booleanValue(int value) {
        return value > 0;
    }

    public static boolean booleanValue(float value) {
        return value > 0.0f;
    }

    public static boolean booleanValue(long value) {
        return value > 0L;
    }

    public static boolean booleanValue(double value) {
        return value > 0.0;
    }

    public static boolean booleanValue(Object value) {
        if (value == null) {
            return false;
        }
        Class<?> c = value.getClass();
        if (c == Boolean.class) {
            return (Boolean)value;
        }
        if (c == String.class) {
            return Boolean.parseBoolean(String.valueOf(value));
        }
        if (c == Character.class) {
            return ((Character)value).charValue() != '\u0000';
        }
        if (value instanceof Number) {
            return ((Number)value).doubleValue() != 0.0;
        }
        return true;
    }

    public static long longValue(Object value) throws NumberFormatException {
        if (value == null) {
            return 0L;
        }
        Class<?> c = value.getClass();
        if (c.getSuperclass() == Number.class) {
            return ((Number)value).longValue();
        }
        if (c == Boolean.class) {
            return (Boolean)value != false ? 1L : 0L;
        }
        if (c == Character.class) {
            return ((Character)value).charValue();
        }
        return Long.parseLong(OgnlOps.stringValue(value, true));
    }

    public static double doubleValue(Object value) throws NumberFormatException {
        if (value == null) {
            return 0.0;
        }
        Class<?> c = value.getClass();
        if (c.getSuperclass() == Number.class) {
            return ((Number)value).doubleValue();
        }
        if (c == Boolean.class) {
            return (Boolean)value != false ? 1.0 : 0.0;
        }
        if (c == Character.class) {
            return ((Character)value).charValue();
        }
        String s = OgnlOps.stringValue(value, true);
        return s.length() == 0 ? 0.0 : Double.parseDouble(s);
    }

    public static BigInteger bigIntValue(Object value) throws NumberFormatException {
        if (value == null) {
            return BigInteger.valueOf(0L);
        }
        Class<?> c = value.getClass();
        if (c == BigInteger.class) {
            return (BigInteger)value;
        }
        if (c == BigDecimal.class) {
            return ((BigDecimal)value).toBigInteger();
        }
        if (c.getSuperclass() == Number.class) {
            return BigInteger.valueOf(((Number)value).longValue());
        }
        if (c == Boolean.class) {
            return BigInteger.valueOf((Boolean)value != false ? 1L : 0L);
        }
        if (c == Character.class) {
            return BigInteger.valueOf(((Character)value).charValue());
        }
        return new BigInteger(OgnlOps.stringValue(value, true));
    }

    public static BigDecimal bigDecValue(Object value) throws NumberFormatException {
        if (value == null) {
            return BigDecimal.valueOf(0L);
        }
        Class<?> c = value.getClass();
        if (c == BigDecimal.class) {
            return (BigDecimal)value;
        }
        if (c == BigInteger.class) {
            return new BigDecimal((BigInteger)value);
        }
        if (c == Boolean.class) {
            return BigDecimal.valueOf((Boolean)value != false ? 1L : 0L);
        }
        if (c == Character.class) {
            return BigDecimal.valueOf(((Character)value).charValue());
        }
        return new BigDecimal(OgnlOps.stringValue(value, true));
    }

    public static String stringValue(Object value, boolean trim) {
        String result;
        if (value == null) {
            result = OgnlRuntime.NULL_STRING;
        } else {
            result = value.toString();
            if (trim) {
                result = result.trim();
            }
        }
        return result;
    }

    public static String stringValue(Object value) {
        return OgnlOps.stringValue(value, false);
    }

    public static int getNumericType(Object value) {
        if (value != null) {
            Class<?> c = value.getClass();
            if (c == Integer.class) {
                return 4;
            }
            if (c == Double.class) {
                return 8;
            }
            if (c == Boolean.class) {
                return 0;
            }
            if (c == Byte.class) {
                return 1;
            }
            if (c == Character.class) {
                return 2;
            }
            if (c == Short.class) {
                return 3;
            }
            if (c == Long.class) {
                return 5;
            }
            if (c == Float.class) {
                return 7;
            }
            if (c == BigInteger.class) {
                return 6;
            }
            if (c == BigDecimal.class) {
                return 9;
            }
        }
        return 10;
    }

    public static Object toArray(char value, Class toType) {
        return OgnlOps.toArray(new Character(value), toType);
    }

    public static Object toArray(byte value, Class toType) {
        return OgnlOps.toArray(new Byte(value), toType);
    }

    public static Object toArray(int value, Class toType) {
        return OgnlOps.toArray(new Integer(value), toType);
    }

    public static Object toArray(long value, Class toType) {
        return OgnlOps.toArray(new Long(value), toType);
    }

    public static Object toArray(float value, Class toType) {
        return OgnlOps.toArray(new Float(value), toType);
    }

    public static Object toArray(double value, Class toType) {
        return OgnlOps.toArray(new Double(value), toType);
    }

    public static Object toArray(boolean value, Class toType) {
        return OgnlOps.toArray(new Boolean(value), toType);
    }

    public static Object convertValue(char value, Class toType) {
        return OgnlOps.convertValue(new Character(value), toType);
    }

    public static Object convertValue(byte value, Class toType) {
        return OgnlOps.convertValue(new Byte(value), toType);
    }

    public static Object convertValue(int value, Class toType) {
        return OgnlOps.convertValue(new Integer(value), toType);
    }

    public static Object convertValue(long value, Class toType) {
        return OgnlOps.convertValue(new Long(value), toType);
    }

    public static Object convertValue(float value, Class toType) {
        return OgnlOps.convertValue(new Float(value), toType);
    }

    public static Object convertValue(double value, Class toType) {
        return OgnlOps.convertValue(new Double(value), toType);
    }

    public static Object convertValue(boolean value, Class toType) {
        return OgnlOps.convertValue(new Boolean(value), toType);
    }

    public static Object convertValue(char value, Class toType, boolean preventNull) {
        return OgnlOps.convertValue(new Character(value), toType, preventNull);
    }

    public static Object convertValue(byte value, Class toType, boolean preventNull) {
        return OgnlOps.convertValue(new Byte(value), toType, preventNull);
    }

    public static Object convertValue(int value, Class toType, boolean preventNull) {
        return OgnlOps.convertValue(new Integer(value), toType, preventNull);
    }

    public static Object convertValue(long value, Class toType, boolean preventNull) {
        return OgnlOps.convertValue(new Long(value), toType, preventNull);
    }

    public static Object convertValue(float value, Class toType, boolean preventNull) {
        return OgnlOps.convertValue(new Float(value), toType, preventNull);
    }

    public static Object convertValue(double value, Class toType, boolean preventNull) {
        return OgnlOps.convertValue(new Double(value), toType, preventNull);
    }

    public static Object convertValue(boolean value, Class toType, boolean preventNull) {
        return OgnlOps.convertValue(new Boolean(value), toType, preventNull);
    }

    public static Object toArray(char value, Class toType, boolean preventNull) {
        return OgnlOps.toArray(new Character(value), toType, preventNull);
    }

    public static Object toArray(byte value, Class toType, boolean preventNull) {
        return OgnlOps.toArray(new Byte(value), toType, preventNull);
    }

    public static Object toArray(int value, Class toType, boolean preventNull) {
        return OgnlOps.toArray(new Integer(value), toType, preventNull);
    }

    public static Object toArray(long value, Class toType, boolean preventNull) {
        return OgnlOps.toArray(new Long(value), toType, preventNull);
    }

    public static Object toArray(float value, Class toType, boolean preventNull) {
        return OgnlOps.toArray(new Float(value), toType, preventNull);
    }

    public static Object toArray(double value, Class toType, boolean preventNull) {
        return OgnlOps.toArray(new Double(value), toType, preventNull);
    }

    public static Object toArray(boolean value, Class toType, boolean preventNull) {
        return OgnlOps.toArray(new Boolean(value), toType, preventNull);
    }

    public static Object convertValue(Object value, Class toType) {
        return OgnlOps.convertValue(value, toType, false);
    }

    public static Object toArray(Object value, Class toType) {
        return OgnlOps.toArray(value, toType, false);
    }

    public static Object toArray(Object value, Class toType, boolean preventNulls) {
        if (value == null) {
            return null;
        }
        Object result = null;
        if (value.getClass().isArray() && toType.isAssignableFrom(value.getClass().getComponentType())) {
            return value;
        }
        if (!value.getClass().isArray()) {
            if (toType == Character.TYPE) {
                return OgnlOps.stringValue(value).toCharArray();
            }
            if (value instanceof Collection) {
                return ((Collection)value).toArray((Object[])Array.newInstance(toType, 0));
            }
            Object arr = Array.newInstance(toType, 1);
            Array.set(arr, 0, OgnlOps.convertValue(value, toType, preventNulls));
            return arr;
        }
        result = Array.newInstance(toType, Array.getLength(value));
        int icount = Array.getLength(value);
        for (int i = 0; i < icount; ++i) {
            Array.set(result, i, OgnlOps.convertValue(Array.get(value, i), toType));
        }
        if (result == null && preventNulls) {
            return value;
        }
        return result;
    }

    public static Object convertValue(Object value, Class toType, boolean preventNulls) {
        Object result = null;
        if (value != null && toType.isAssignableFrom(value.getClass())) {
            return value;
        }
        if (value != null) {
            if (value.getClass().isArray() && toType.isArray()) {
                Class<?> componentType = toType.getComponentType();
                result = Array.newInstance(componentType, Array.getLength(value));
                int icount = Array.getLength(value);
                for (int i = 0; i < icount; ++i) {
                    Array.set(result, i, OgnlOps.convertValue(Array.get(value, i), componentType));
                }
            } else {
                if (value.getClass().isArray() && !toType.isArray()) {
                    return OgnlOps.convertValue(Array.get(value, 0), toType);
                }
                if (!value.getClass().isArray() && toType.isArray()) {
                    if (toType.getComponentType() == Character.TYPE) {
                        result = OgnlOps.stringValue(value).toCharArray();
                    } else if (toType.getComponentType() == Object.class) {
                        if (value instanceof Collection) {
                            Collection vc = (Collection)value;
                            return vc.toArray(new Object[0]);
                        }
                        return new Object[]{value};
                    }
                } else {
                    if (toType == Integer.class || toType == Integer.TYPE) {
                        result = new Integer((int)OgnlOps.longValue(value));
                    }
                    if (toType == Double.class || toType == Double.TYPE) {
                        result = new Double(OgnlOps.doubleValue(value));
                    }
                    if (toType == Boolean.class || toType == Boolean.TYPE) {
                        Object object = result = OgnlOps.booleanValue(value) ? Boolean.TRUE : Boolean.FALSE;
                    }
                    if (toType == Byte.class || toType == Byte.TYPE) {
                        result = new Byte((byte)OgnlOps.longValue(value));
                    }
                    if (toType == Character.class || toType == Character.TYPE) {
                        result = new Character((char)OgnlOps.longValue(value));
                    }
                    if (toType == Short.class || toType == Short.TYPE) {
                        result = new Short((short)OgnlOps.longValue(value));
                    }
                    if (toType == Long.class || toType == Long.TYPE) {
                        result = new Long(OgnlOps.longValue(value));
                    }
                    if (toType == Float.class || toType == Float.TYPE) {
                        result = new Float(OgnlOps.doubleValue(value));
                    }
                    if (toType == BigInteger.class) {
                        result = OgnlOps.bigIntValue(value);
                    }
                    if (toType == BigDecimal.class) {
                        result = OgnlOps.bigDecValue(value);
                    }
                    if (toType == String.class) {
                        result = OgnlOps.stringValue(value);
                    }
                }
            }
        } else if (toType.isPrimitive()) {
            result = OgnlRuntime.getPrimitiveDefaultValue(toType);
        } else if (preventNulls && toType == Boolean.class) {
            result = Boolean.FALSE;
        } else if (preventNulls && Number.class.isAssignableFrom(toType)) {
            result = OgnlRuntime.getNumericDefaultValue(toType);
        }
        if (result == null && preventNulls) {
            return value;
        }
        if (value != null && result == null) {
            throw new IllegalArgumentException("Unable to convert type " + value.getClass().getName() + " of " + value + " to type of " + toType.getName());
        }
        return result;
    }

    public static int getIntValue(Object value) {
        try {
            if (value == null) {
                return -1;
            }
            if (Number.class.isInstance(value)) {
                return ((Number)value).intValue();
            }
            String str = String.class.isInstance(value) ? (String)value : value.toString();
            return Integer.parseInt(str);
        }
        catch (Throwable t) {
            throw new RuntimeException("Error converting " + value + " to integer:", t);
        }
    }

    public static int getNumericType(Object v1, Object v2) {
        return OgnlOps.getNumericType(v1, v2, false);
    }

    public static int getNumericType(int t1, int t2, boolean canBeNonNumeric) {
        if (t1 == t2) {
            return t1;
        }
        if (canBeNonNumeric && (t1 == 10 || t2 == 10 || t1 == 2 || t2 == 2)) {
            return 10;
        }
        if (t1 == 10) {
            t1 = 8;
        }
        if (t2 == 10) {
            t2 = 8;
        }
        if (t1 >= 7) {
            if (t2 >= 7) {
                return Math.max(t1, t2);
            }
            if (t2 < 4) {
                return t1;
            }
            if (t2 == 6) {
                return 9;
            }
            return Math.max(8, t1);
        }
        if (t2 >= 7) {
            if (t1 < 4) {
                return t2;
            }
            if (t1 == 6) {
                return 9;
            }
            return Math.max(8, t2);
        }
        return Math.max(t1, t2);
    }

    public static int getNumericType(Object v1, Object v2, boolean canBeNonNumeric) {
        return OgnlOps.getNumericType(OgnlOps.getNumericType(v1), OgnlOps.getNumericType(v2), canBeNonNumeric);
    }

    public static Number newInteger(int type, long value) {
        switch (type) {
            case 0: 
            case 2: 
            case 4: {
                return new Integer((int)value);
            }
            case 7: {
                if ((long)((float)value) == value) {
                    return new Float(value);
                }
            }
            case 8: {
                if ((long)((double)value) == value) {
                    return new Double(value);
                }
            }
            case 5: {
                return new Long(value);
            }
            case 1: {
                return new Byte((byte)value);
            }
            case 3: {
                return new Short((short)value);
            }
        }
        return BigInteger.valueOf(value);
    }

    public static Number newReal(int type, double value) {
        if (type == 7) {
            return new Float((float)value);
        }
        return new Double(value);
    }

    public static Object binaryOr(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1, v2);
        if (type == 6 || type == 9) {
            return OgnlOps.bigIntValue(v1).or(OgnlOps.bigIntValue(v2));
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) | OgnlOps.longValue(v2));
    }

    public static Object binaryXor(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1, v2);
        if (type == 6 || type == 9) {
            return OgnlOps.bigIntValue(v1).xor(OgnlOps.bigIntValue(v2));
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) ^ OgnlOps.longValue(v2));
    }

    public static Object binaryAnd(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1, v2);
        if (type == 6 || type == 9) {
            return OgnlOps.bigIntValue(v1).and(OgnlOps.bigIntValue(v2));
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) & OgnlOps.longValue(v2));
    }

    public static boolean equal(Object v1, Object v2) {
        if (v1 == null) {
            return v2 == null;
        }
        return v1 == v2 || OgnlOps.isEqual(v1, v2);
    }

    public static boolean less(Object v1, Object v2) {
        return OgnlOps.compareWithConversion(v1, v2) < 0;
    }

    public static boolean greater(Object v1, Object v2) {
        return OgnlOps.compareWithConversion(v1, v2) > 0;
    }

    public static boolean in(Object v1, Object v2) throws OgnlException {
        if (v2 == null) {
            return false;
        }
        ElementsAccessor elementsAccessor = OgnlRuntime.getElementsAccessor(OgnlRuntime.getTargetClass(v2));
        Enumeration e = elementsAccessor.getElements(v2);
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if (!OgnlOps.equal(v1, o)) continue;
            return true;
        }
        return false;
    }

    public static Object shiftLeft(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1);
        if (type == 6 || type == 9) {
            return OgnlOps.bigIntValue(v1).shiftLeft((int)OgnlOps.longValue(v2));
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) << (int)OgnlOps.longValue(v2));
    }

    public static Object shiftRight(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1);
        if (type == 6 || type == 9) {
            return OgnlOps.bigIntValue(v1).shiftRight((int)OgnlOps.longValue(v2));
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) >> (int)OgnlOps.longValue(v2));
    }

    public static Object unsignedShiftRight(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1);
        if (type == 6 || type == 9) {
            return OgnlOps.bigIntValue(v1).shiftRight((int)OgnlOps.longValue(v2));
        }
        if (type <= 4) {
            return OgnlOps.newInteger(4, (int)OgnlOps.longValue(v1) >>> (int)OgnlOps.longValue(v2));
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) >>> (int)OgnlOps.longValue(v2));
    }

    public static Object add(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1, v2, true);
        switch (type) {
            case 6: {
                return OgnlOps.bigIntValue(v1).add(OgnlOps.bigIntValue(v2));
            }
            case 9: {
                return OgnlOps.bigDecValue(v1).add(OgnlOps.bigDecValue(v2));
            }
            case 7: 
            case 8: {
                return OgnlOps.newReal(type, OgnlOps.doubleValue(v1) + OgnlOps.doubleValue(v2));
            }
            case 10: {
                int t1 = OgnlOps.getNumericType(v1);
                int t2 = OgnlOps.getNumericType(v2);
                if (t1 != 10 && v2 == null || t2 != 10 && v1 == null) {
                    throw new NullPointerException("Can't add values " + v1 + " , " + v2);
                }
                return OgnlOps.stringValue(v1) + OgnlOps.stringValue(v2);
            }
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) + OgnlOps.longValue(v2));
    }

    public static Object subtract(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1, v2);
        switch (type) {
            case 6: {
                return OgnlOps.bigIntValue(v1).subtract(OgnlOps.bigIntValue(v2));
            }
            case 9: {
                return OgnlOps.bigDecValue(v1).subtract(OgnlOps.bigDecValue(v2));
            }
            case 7: 
            case 8: {
                return OgnlOps.newReal(type, OgnlOps.doubleValue(v1) - OgnlOps.doubleValue(v2));
            }
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) - OgnlOps.longValue(v2));
    }

    public static Object multiply(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1, v2);
        switch (type) {
            case 6: {
                return OgnlOps.bigIntValue(v1).multiply(OgnlOps.bigIntValue(v2));
            }
            case 9: {
                return OgnlOps.bigDecValue(v1).multiply(OgnlOps.bigDecValue(v2));
            }
            case 7: 
            case 8: {
                return OgnlOps.newReal(type, OgnlOps.doubleValue(v1) * OgnlOps.doubleValue(v2));
            }
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) * OgnlOps.longValue(v2));
    }

    public static Object divide(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1, v2);
        switch (type) {
            case 6: {
                return OgnlOps.bigIntValue(v1).divide(OgnlOps.bigIntValue(v2));
            }
            case 9: {
                return OgnlOps.bigDecValue(v1).divide(OgnlOps.bigDecValue(v2), 6);
            }
            case 7: 
            case 8: {
                return OgnlOps.newReal(type, OgnlOps.doubleValue(v1) / OgnlOps.doubleValue(v2));
            }
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) / OgnlOps.longValue(v2));
    }

    public static Object remainder(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1, v2);
        switch (type) {
            case 6: 
            case 9: {
                return OgnlOps.bigIntValue(v1).remainder(OgnlOps.bigIntValue(v2));
            }
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) % OgnlOps.longValue(v2));
    }

    public static Object negate(Object value) {
        int type = OgnlOps.getNumericType(value);
        switch (type) {
            case 6: {
                return OgnlOps.bigIntValue(value).negate();
            }
            case 9: {
                return OgnlOps.bigDecValue(value).negate();
            }
            case 7: 
            case 8: {
                return OgnlOps.newReal(type, -OgnlOps.doubleValue(value));
            }
        }
        return OgnlOps.newInteger(type, -OgnlOps.longValue(value));
    }

    public static Object bitNegate(Object value) {
        int type = OgnlOps.getNumericType(value);
        switch (type) {
            case 6: 
            case 9: {
                return OgnlOps.bigIntValue(value).not();
            }
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(value) ^ 0xFFFFFFFFFFFFFFFFL);
    }

    public static String getEscapeString(String value) {
        StringBuffer result = new StringBuffer();
        int icount = value.length();
        for (int i = 0; i < icount; ++i) {
            result.append(OgnlOps.getEscapedChar(value.charAt(i)));
        }
        return new String(result);
    }

    public static String getEscapedChar(char ch) {
        String result;
        switch (ch) {
            case '\b': {
                result = "\b";
                break;
            }
            case '\t': {
                result = "\\t";
                break;
            }
            case '\n': {
                result = "\\n";
                break;
            }
            case '\f': {
                result = "\\f";
                break;
            }
            case '\r': {
                result = "\\r";
                break;
            }
            case '\"': {
                result = "\\\"";
                break;
            }
            case '\'': {
                result = "\\'";
                break;
            }
            case '\\': {
                result = "\\\\";
                break;
            }
            default: {
                if (Character.isISOControl(ch)) {
                    String hc = Integer.toString(ch, 16);
                    int hcl = hc.length();
                    result = "\\u";
                    if (hcl < 4) {
                        result = hcl == 3 ? result + "0" : (hcl == 2 ? result + "00" : result + "000");
                    }
                    result = result + hc;
                    break;
                }
                result = new String(ch + "");
            }
        }
        return result;
    }

    public static Object returnValue(Object ignore, Object returnValue) {
        return returnValue;
    }

    public static RuntimeException castToRuntime(Throwable t) {
        if (RuntimeException.class.isInstance(t)) {
            return (RuntimeException)t;
        }
        if (OgnlException.class.isInstance(t)) {
            throw new UnsupportedCompilationException("Error evluating expression: " + t.getMessage(), t);
        }
        return new RuntimeException(t);
    }
}

