/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.OgnlContext
 *  ognl.TypeConverter
 */
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProviderFactory;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.XWorkTypeConverterWrapper;
import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import ognl.OgnlContext;

public abstract class DefaultTypeConverter
implements TypeConverter {
    protected static final String MILLISECOND_FORMAT = ".SSS";
    private static final String NULL_STRING = "null";
    private static final Map<Class<?>, Object> baseTypeDefaults;
    private Container container;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    public Object convertValue(Map<String, Object> context, Object value, Class toType) {
        return this.convertValue(value, toType);
    }

    @Override
    public Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName, Object value, Class toType) {
        return this.convertValue(context, value, toType);
    }

    public TypeConverter getTypeConverter(Map<String, Object> context) {
        ognl.TypeConverter converter = null;
        if (context instanceof OgnlContext) {
            converter = ((OgnlContext)context).getTypeConverter();
        }
        if (converter != null) {
            if (converter instanceof TypeConverter) {
                return (TypeConverter)converter;
            }
            return new XWorkTypeConverterWrapper(converter);
        }
        return null;
    }

    public Object convertValue(Object value, Class toType) {
        Enum<?> result = null;
        if (value != null) {
            if (value.getClass().isArray() && toType.isArray()) {
                Class<?> componentType = toType.getComponentType();
                result = Array.newInstance(componentType, Array.getLength(value));
                int icount = Array.getLength(value);
                for (int i = 0; i < icount; ++i) {
                    Array.set(result, i, this.convertValue(Array.get(value, i), componentType));
                }
            } else {
                if (toType == Integer.class || toType == Integer.TYPE) {
                    result = (int)DefaultTypeConverter.longValue(value);
                }
                if (toType == Double.class || toType == Double.TYPE) {
                    result = DefaultTypeConverter.doubleValue(value);
                }
                if (toType == Boolean.class || toType == Boolean.TYPE) {
                    Enum<?> enum_ = result = DefaultTypeConverter.booleanValue(value) ? Boolean.TRUE : Boolean.FALSE;
                }
                if (toType == Byte.class || toType == Byte.TYPE) {
                    result = (byte)DefaultTypeConverter.longValue(value);
                }
                if (toType == Character.class || toType == Character.TYPE) {
                    result = Character.valueOf((char)DefaultTypeConverter.longValue(value));
                }
                if (toType == Short.class || toType == Short.TYPE) {
                    result = (short)DefaultTypeConverter.longValue(value);
                }
                if (toType == Long.class || toType == Long.TYPE) {
                    result = DefaultTypeConverter.longValue(value);
                }
                if (toType == Float.class || toType == Float.TYPE) {
                    result = new Float(DefaultTypeConverter.doubleValue(value));
                }
                if (toType == BigInteger.class) {
                    result = DefaultTypeConverter.bigIntValue(value);
                }
                if (toType == BigDecimal.class) {
                    result = DefaultTypeConverter.bigDecValue(value);
                }
                if (toType == String.class) {
                    result = DefaultTypeConverter.stringValue(value);
                }
                if (Enum.class.isAssignableFrom(toType)) {
                    result = this.enumValue(toType, value);
                }
            }
        } else {
            result = baseTypeDefaults.get(toType);
        }
        return result;
    }

    public static boolean booleanValue(Object value) {
        if (value == null) {
            return false;
        }
        Class<?> c = value.getClass();
        if (c == Boolean.class) {
            return (Boolean)value;
        }
        if (c == Character.class) {
            return ((Character)value).charValue() != '\u0000';
        }
        if (value instanceof Number) {
            return ((Number)value).doubleValue() != 0.0;
        }
        return true;
    }

    public Enum<?> enumValue(Class toClass, Object o) {
        Enum<?> result = null;
        if (o == null) {
            result = null;
        } else if (o instanceof String[]) {
            result = (Enum<?>)Enum.valueOf(toClass, ((String[])o)[0]);
        } else if (o instanceof String) {
            result = (Enum<?>)Enum.valueOf(toClass, (String)o);
        }
        return result;
    }

    public static long longValue(Object value) {
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
        return Long.parseLong(DefaultTypeConverter.stringValue(value, true));
    }

    public static double doubleValue(Object value) {
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
        String s = DefaultTypeConverter.stringValue(value, true);
        return s.length() == 0 ? 0.0 : Double.parseDouble(s);
    }

    public static BigInteger bigIntValue(Object value) {
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
        return new BigInteger(DefaultTypeConverter.stringValue(value, true));
    }

    public static BigDecimal bigDecValue(Object value) {
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
        if (c.getSuperclass() == Number.class) {
            return BigDecimal.valueOf(((Number)value).doubleValue());
        }
        if (c == Boolean.class) {
            return BigDecimal.valueOf((Boolean)value != false ? 1L : 0L);
        }
        if (c == Character.class) {
            return BigDecimal.valueOf(((Character)value).charValue());
        }
        return new BigDecimal(DefaultTypeConverter.stringValue(value, true));
    }

    public static String stringValue(Object value, boolean trim) {
        String result;
        if (value == null) {
            result = NULL_STRING;
        } else {
            result = value.toString();
            if (trim) {
                result = result.trim();
            }
        }
        return result;
    }

    public static String stringValue(Object value) {
        return DefaultTypeConverter.stringValue(value, false);
    }

    protected Locale getLocale(Map<String, Object> context) {
        Locale locale = null;
        if (context != null) {
            locale = ActionContext.of(context).getLocale();
        }
        if (locale == null) {
            LocaleProviderFactory localeProviderFactory = this.container.getInstance(LocaleProviderFactory.class);
            locale = localeProviderFactory.createLocaleProvider().getLocale();
        }
        return locale;
    }

    static {
        HashMap<Class<Comparable<Boolean>>, Comparable<Boolean>> map = new HashMap<Class<Comparable<Boolean>>, Comparable<Boolean>>();
        map.put(Boolean.TYPE, Boolean.FALSE);
        map.put(Byte.TYPE, Byte.valueOf((byte)0));
        map.put(Short.TYPE, Short.valueOf((short)0));
        map.put(Character.TYPE, new Character('\u0000'));
        map.put(Integer.TYPE, Integer.valueOf(0));
        map.put(Long.TYPE, Long.valueOf(0L));
        map.put(Float.TYPE, new Float(0.0f));
        map.put(Double.TYPE, new Double(0.0));
        map.put(BigInteger.class, BigInteger.ZERO);
        map.put(BigDecimal.class, BigDecimal.ZERO);
        baseTypeDefaults = Collections.unmodifiableMap(map);
    }
}

