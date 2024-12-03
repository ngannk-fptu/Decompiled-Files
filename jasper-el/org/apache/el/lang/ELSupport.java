/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELException
 */
package org.apache.el.lang;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessController;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.el.ELContext;
import javax.el.ELException;
import org.apache.el.lang.ELArithmetic;
import org.apache.el.util.MessageFactory;

public class ELSupport {
    private static final Long ZERO = 0L;
    protected static final boolean COERCE_TO_ZERO;

    public static final int compare(ELContext ctx, Object obj0, Object obj1) throws ELException {
        if (obj0 == obj1 || ELSupport.equals(ctx, obj0, obj1)) {
            return 0;
        }
        if (ELSupport.isBigDecimalOp(obj0, obj1)) {
            BigDecimal bd0 = (BigDecimal)ELSupport.coerceToNumber(ctx, obj0, BigDecimal.class);
            BigDecimal bd1 = (BigDecimal)ELSupport.coerceToNumber(ctx, obj1, BigDecimal.class);
            return bd0.compareTo(bd1);
        }
        if (ELSupport.isDoubleOp(obj0, obj1)) {
            Double d0 = (Double)ELSupport.coerceToNumber(ctx, obj0, Double.class);
            Double d1 = (Double)ELSupport.coerceToNumber(ctx, obj1, Double.class);
            return d0.compareTo(d1);
        }
        if (ELSupport.isBigIntegerOp(obj0, obj1)) {
            BigInteger bi0 = (BigInteger)ELSupport.coerceToNumber(ctx, obj0, BigInteger.class);
            BigInteger bi1 = (BigInteger)ELSupport.coerceToNumber(ctx, obj1, BigInteger.class);
            return bi0.compareTo(bi1);
        }
        if (ELSupport.isLongOp(obj0, obj1)) {
            Long l0 = (Long)ELSupport.coerceToNumber(ctx, obj0, Long.class);
            Long l1 = (Long)ELSupport.coerceToNumber(ctx, obj1, Long.class);
            return l0.compareTo(l1);
        }
        if (obj0 instanceof String || obj1 instanceof String) {
            return ELSupport.coerceToString(ctx, obj0).compareTo(ELSupport.coerceToString(ctx, obj1));
        }
        if (obj0 instanceof Comparable) {
            Comparable comparable = (Comparable)obj0;
            return obj1 != null ? comparable.compareTo(obj1) : 1;
        }
        if (obj1 instanceof Comparable) {
            Comparable comparable = (Comparable)obj1;
            return obj0 != null ? -comparable.compareTo(obj0) : -1;
        }
        throw new ELException(MessageFactory.get("error.compare", obj0, obj1));
    }

    public static final boolean equals(ELContext ctx, Object obj0, Object obj1) throws ELException {
        if (obj0 == obj1) {
            return true;
        }
        if (obj0 == null || obj1 == null) {
            return false;
        }
        if (ELSupport.isBigDecimalOp(obj0, obj1)) {
            BigDecimal bd0 = (BigDecimal)ELSupport.coerceToNumber(ctx, obj0, BigDecimal.class);
            BigDecimal bd1 = (BigDecimal)ELSupport.coerceToNumber(ctx, obj1, BigDecimal.class);
            return bd0.equals(bd1);
        }
        if (ELSupport.isDoubleOp(obj0, obj1)) {
            Double d0 = (Double)ELSupport.coerceToNumber(ctx, obj0, Double.class);
            Double d1 = (Double)ELSupport.coerceToNumber(ctx, obj1, Double.class);
            return d0.equals(d1);
        }
        if (ELSupport.isBigIntegerOp(obj0, obj1)) {
            BigInteger bi0 = (BigInteger)ELSupport.coerceToNumber(ctx, obj0, BigInteger.class);
            BigInteger bi1 = (BigInteger)ELSupport.coerceToNumber(ctx, obj1, BigInteger.class);
            return bi0.equals(bi1);
        }
        if (ELSupport.isLongOp(obj0, obj1)) {
            Long l0 = (Long)ELSupport.coerceToNumber(ctx, obj0, Long.class);
            Long l1 = (Long)ELSupport.coerceToNumber(ctx, obj1, Long.class);
            return l0.equals(l1);
        }
        if (obj0 instanceof Boolean || obj1 instanceof Boolean) {
            return ELSupport.coerceToBoolean(ctx, obj0, false).equals(ELSupport.coerceToBoolean(ctx, obj1, false));
        }
        if (obj0.getClass().isEnum()) {
            return obj0.equals(ELSupport.coerceToEnum(ctx, obj1, obj0.getClass()));
        }
        if (obj1.getClass().isEnum()) {
            return obj1.equals(ELSupport.coerceToEnum(ctx, obj0, obj1.getClass()));
        }
        if (obj0 instanceof String || obj1 instanceof String) {
            int lexCompare = ELSupport.coerceToString(ctx, obj0).compareTo(ELSupport.coerceToString(ctx, obj1));
            return lexCompare == 0;
        }
        return obj0.equals(obj1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final Enum<?> coerceToEnum(ELContext ctx, Object obj, Class type) {
        Object result;
        if (ctx != null) {
            boolean originalIsPropertyResolved = ctx.isPropertyResolved();
            try {
                Object result2 = ctx.getELResolver().convertToType(ctx, obj, type);
                if (ctx.isPropertyResolved()) {
                    Enum enum_ = (Enum)result2;
                    return enum_;
                }
            }
            finally {
                ctx.setPropertyResolved(originalIsPropertyResolved);
            }
        }
        if (obj == null || "".equals(obj)) {
            return null;
        }
        if (type.isAssignableFrom(obj.getClass())) {
            return (Enum)obj;
        }
        if (!(obj instanceof String)) {
            throw new ELException(MessageFactory.get("error.convert", obj, obj.getClass(), type));
        }
        try {
            result = Enum.valueOf(type, (String)obj);
        }
        catch (IllegalArgumentException iae) {
            throw new ELException(MessageFactory.get("error.convert", obj, obj.getClass(), type));
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final Boolean coerceToBoolean(ELContext ctx, Object obj, boolean primitive) throws ELException {
        if (ctx != null) {
            boolean originalIsPropertyResolved = ctx.isPropertyResolved();
            try {
                Object result = ctx.getELResolver().convertToType(ctx, obj, Boolean.class);
                if (ctx.isPropertyResolved()) {
                    Boolean bl = (Boolean)result;
                    return bl;
                }
            }
            finally {
                ctx.setPropertyResolved(originalIsPropertyResolved);
            }
        }
        if (!COERCE_TO_ZERO && !primitive && obj == null) {
            return null;
        }
        if (obj == null || "".equals(obj)) {
            return Boolean.FALSE;
        }
        if (obj instanceof Boolean) {
            return (Boolean)obj;
        }
        if (obj instanceof String) {
            return Boolean.valueOf((String)obj);
        }
        throw new ELException(MessageFactory.get("error.convert", obj, obj.getClass(), Boolean.class));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Character coerceToCharacter(ELContext ctx, Object obj) throws ELException {
        if (ctx != null) {
            boolean originalIsPropertyResolved = ctx.isPropertyResolved();
            try {
                Object result = ctx.getELResolver().convertToType(ctx, obj, Character.class);
                if (ctx.isPropertyResolved()) {
                    Character c = (Character)result;
                    return c;
                }
            }
            finally {
                ctx.setPropertyResolved(originalIsPropertyResolved);
            }
        }
        if (obj == null || "".equals(obj)) {
            return Character.valueOf('\u0000');
        }
        if (obj instanceof String) {
            return Character.valueOf(((String)obj).charAt(0));
        }
        if (ELArithmetic.isNumber(obj)) {
            return Character.valueOf((char)((Number)obj).shortValue());
        }
        Class<?> objType = obj.getClass();
        if (obj instanceof Character) {
            return (Character)obj;
        }
        throw new ELException(MessageFactory.get("error.convert", obj, objType, Character.class));
    }

    protected static final Number coerceToNumber(Number number, Class<?> type) throws ELException {
        if (Long.TYPE == type || Long.class.equals(type)) {
            return number.longValue();
        }
        if (Double.TYPE == type || Double.class.equals(type)) {
            return number.doubleValue();
        }
        if (Integer.TYPE == type || Integer.class.equals(type)) {
            return number.intValue();
        }
        if (BigInteger.class.equals(type)) {
            if (number instanceof BigDecimal) {
                return ((BigDecimal)number).toBigInteger();
            }
            if (number instanceof BigInteger) {
                return number;
            }
            return BigInteger.valueOf(number.longValue());
        }
        if (BigDecimal.class.equals(type)) {
            if (number instanceof BigDecimal) {
                return number;
            }
            if (number instanceof BigInteger) {
                return new BigDecimal((BigInteger)number);
            }
            return new BigDecimal(number.doubleValue());
        }
        if (Byte.TYPE == type || Byte.class.equals(type)) {
            return number.byteValue();
        }
        if (Short.TYPE == type || Short.class.equals(type)) {
            return number.shortValue();
        }
        if (Float.TYPE == type || Float.class.equals(type)) {
            return Float.valueOf(number.floatValue());
        }
        if (Number.class.equals(type)) {
            return number;
        }
        throw new ELException(MessageFactory.get("error.convert", number, number.getClass(), type));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final Number coerceToNumber(ELContext ctx, Object obj, Class<?> type) throws ELException {
        if (ctx != null) {
            boolean originalIsPropertyResolved = ctx.isPropertyResolved();
            try {
                Object result = ctx.getELResolver().convertToType(ctx, obj, type);
                if (ctx.isPropertyResolved()) {
                    Number number = (Number)result;
                    return number;
                }
            }
            finally {
                ctx.setPropertyResolved(originalIsPropertyResolved);
            }
        }
        if (!COERCE_TO_ZERO && obj == null && !type.isPrimitive()) {
            return null;
        }
        if (obj == null || "".equals(obj)) {
            return ELSupport.coerceToNumber(ZERO, type);
        }
        if (obj instanceof String) {
            return ELSupport.coerceToNumber((String)obj, type);
        }
        if (ELArithmetic.isNumber(obj)) {
            return ELSupport.coerceToNumber((Number)obj, type);
        }
        if (obj instanceof Character) {
            return ELSupport.coerceToNumber((short)((Character)obj).charValue(), type);
        }
        throw new ELException(MessageFactory.get("error.convert", obj, obj.getClass(), type));
    }

    protected static final Number coerceToNumber(String val, Class<?> type) throws ELException {
        if (Long.TYPE == type || Long.class.equals(type)) {
            try {
                return Long.valueOf(val);
            }
            catch (NumberFormatException nfe) {
                throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
            }
        }
        if (Integer.TYPE == type || Integer.class.equals(type)) {
            try {
                return Integer.valueOf(val);
            }
            catch (NumberFormatException nfe) {
                throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
            }
        }
        if (Double.TYPE == type || Double.class.equals(type)) {
            try {
                return Double.valueOf(val);
            }
            catch (NumberFormatException nfe) {
                throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
            }
        }
        if (BigInteger.class.equals(type)) {
            try {
                return new BigInteger(val);
            }
            catch (NumberFormatException nfe) {
                throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
            }
        }
        if (BigDecimal.class.equals(type)) {
            try {
                return new BigDecimal(val);
            }
            catch (NumberFormatException nfe) {
                throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
            }
        }
        if (Byte.TYPE == type || Byte.class.equals(type)) {
            try {
                return Byte.valueOf(val);
            }
            catch (NumberFormatException nfe) {
                throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
            }
        }
        if (Short.TYPE == type || Short.class.equals(type)) {
            try {
                return Short.valueOf(val);
            }
            catch (NumberFormatException nfe) {
                throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
            }
        }
        if (Float.TYPE == type || Float.class.equals(type)) {
            try {
                return Float.valueOf(val);
            }
            catch (NumberFormatException nfe) {
                throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
            }
        }
        throw new ELException(MessageFactory.get("error.convert", val, String.class, type));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final String coerceToString(ELContext ctx, Object obj) {
        if (ctx != null) {
            boolean originalIsPropertyResolved = ctx.isPropertyResolved();
            try {
                Object result = ctx.getELResolver().convertToType(ctx, obj, String.class);
                if (ctx.isPropertyResolved()) {
                    String string = (String)result;
                    return string;
                }
            }
            finally {
                ctx.setPropertyResolved(originalIsPropertyResolved);
            }
        }
        if (obj == null) {
            return "";
        }
        if (obj instanceof String) {
            return (String)obj;
        }
        if (obj instanceof Enum) {
            return ((Enum)obj).name();
        }
        return obj.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final Object coerceToType(ELContext ctx, Object obj, Class<?> type) throws ELException {
        if (ctx != null) {
            boolean originalIsPropertyResolved = ctx.isPropertyResolved();
            try {
                Object result = ctx.getELResolver().convertToType(ctx, obj, type);
                if (ctx.isPropertyResolved()) {
                    Object object = result;
                    return object;
                }
            }
            finally {
                ctx.setPropertyResolved(originalIsPropertyResolved);
            }
        }
        if (type == null || Object.class.equals(type) || obj != null && type.isAssignableFrom(obj.getClass())) {
            return obj;
        }
        if (!(COERCE_TO_ZERO || obj != null || type.isPrimitive() || String.class.isAssignableFrom(type))) {
            return null;
        }
        if (String.class.equals(type)) {
            return ELSupport.coerceToString(ctx, obj);
        }
        if (ELArithmetic.isNumberType(type)) {
            return ELSupport.coerceToNumber(ctx, obj, type);
        }
        if (Character.class.equals(type) || Character.TYPE == type) {
            return ELSupport.coerceToCharacter(ctx, obj);
        }
        if (Boolean.class.equals(type) || Boolean.TYPE == type) {
            return ELSupport.coerceToBoolean(ctx, obj, Boolean.TYPE == type);
        }
        if (type.isEnum()) {
            return ELSupport.coerceToEnum(ctx, obj, type);
        }
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            String str = (String)obj;
            PropertyEditor editor = PropertyEditorManager.findEditor(type);
            if (editor == null) {
                if (str.isEmpty()) {
                    return null;
                }
                throw new ELException(MessageFactory.get("error.convert", obj, obj.getClass(), type));
            }
            try {
                editor.setAsText(str);
                return editor.getValue();
            }
            catch (RuntimeException e) {
                if (str.isEmpty()) {
                    return null;
                }
                throw new ELException(MessageFactory.get("error.convert", obj, obj.getClass(), type), (Throwable)e);
            }
        }
        if (obj instanceof Set && type == Map.class && ((Set)obj).isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        if (type.isArray() && obj.getClass().isArray()) {
            return ELSupport.coerceToArray(ctx, obj, type);
        }
        throw new ELException(MessageFactory.get("error.convert", obj, obj.getClass(), type));
    }

    private static Object coerceToArray(ELContext ctx, Object obj, Class<?> type) {
        int size = Array.getLength(obj);
        Class<?> componentType = type.getComponentType();
        Object result = Array.newInstance(componentType, size);
        for (int i = 0; i < size; ++i) {
            Array.set(result, i, ELSupport.coerceToType(ctx, Array.get(obj, i), componentType));
        }
        return result;
    }

    public static final boolean isBigDecimalOp(Object obj0, Object obj1) {
        return obj0 instanceof BigDecimal || obj1 instanceof BigDecimal;
    }

    public static final boolean isBigIntegerOp(Object obj0, Object obj1) {
        return obj0 instanceof BigInteger || obj1 instanceof BigInteger;
    }

    public static final boolean isDoubleOp(Object obj0, Object obj1) {
        return obj0 instanceof Double || obj1 instanceof Double || obj0 instanceof Float || obj1 instanceof Float;
    }

    public static final boolean isLongOp(Object obj0, Object obj1) {
        return obj0 instanceof Long || obj1 instanceof Long || obj0 instanceof Integer || obj1 instanceof Integer || obj0 instanceof Character || obj1 instanceof Character || obj0 instanceof Short || obj1 instanceof Short || obj0 instanceof Byte || obj1 instanceof Byte;
    }

    public static final boolean isStringFloat(String str) {
        int len = str.length();
        if (len > 1) {
            for (int i = 0; i < len; ++i) {
                switch (str.charAt(i)) {
                    case 'E': {
                        return true;
                    }
                    case 'e': {
                        return true;
                    }
                    case '.': {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    static {
        String coerceToZeroStr = System.getSecurityManager() != null ? AccessController.doPrivileged(() -> System.getProperty("org.apache.el.parser.COERCE_TO_ZERO", "false")) : System.getProperty("org.apache.el.parser.COERCE_TO_ZERO", "false");
        COERCE_TO_ZERO = Boolean.parseBoolean(coerceToZeroStr);
    }
}

