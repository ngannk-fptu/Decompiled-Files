/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.Converter
 *  org.apache.commons.beanutils.converters.BooleanConverter
 *  org.apache.commons.beanutils.converters.StringConverter
 */
package org.apache.velocity.tools.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.StringConverter;
import org.apache.velocity.tools.ClassUtils;
import org.apache.velocity.tools.ConversionUtils;
import org.apache.velocity.tools.config.ConfigurationException;
import org.apache.velocity.tools.config.NullKeyException;

public class Data
implements Comparable<Data> {
    protected static final Type DEFAULT_TYPE = Type.AUTO;
    private String key;
    private String typeValue;
    private Object value;
    private boolean isList;
    private Class target;
    private Converter converter;

    public Data() {
        this.setType(DEFAULT_TYPE);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setClassname(String classname) {
        try {
            this.setTargetClass(ClassUtils.getClass(classname));
        }
        catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException("Class " + classname + " could not be found.", cnfe);
        }
    }

    public void setClass(String classname) {
        this.setClassname(classname);
    }

    protected void setType(Type type) {
        this.isList = type.isList();
        if (!type.isCustom()) {
            this.typeValue = type.value();
            this.target = type.getTarget();
            this.converter = type.getConverter();
        } else if (type.isList()) {
            this.typeValue = type.value();
            this.target = type.getTarget();
        }
    }

    public void setType(String t) {
        this.typeValue = t;
        Type type = Type.get(this.typeValue);
        if (type != null) {
            this.setType(type);
        }
    }

    public void setTargetClass(Class clazz) {
        this.target = clazz;
    }

    public void setConverter(Class clazz) {
        try {
            this.convertWith((Converter)clazz.newInstance());
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Class " + clazz + " is not a valid " + Converter.class, e);
        }
    }

    public void setConverter(String classname) {
        try {
            this.convertWith((Converter)ClassUtils.getInstance(classname));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Class " + classname + " is not a valid " + Converter.class, e);
        }
    }

    public void convertWith(Converter converter) {
        this.converter = converter;
    }

    public String getKey() {
        return this.key;
    }

    public String getType() {
        return this.typeValue;
    }

    public Object getValue() {
        return this.value;
    }

    public Class getTargetClass() {
        return this.target;
    }

    public Converter getConverter() {
        return this.converter;
    }

    public Object getConvertedValue() {
        return this.convert(this.value);
    }

    public void validate() {
        if (this.getKey() == null) {
            throw new NullKeyException(this);
        }
        if (this.getValue() == null) {
            throw new ConfigurationException(this, "No value has been set for '" + this.getKey() + '\'');
        }
        if (this.converter != null) {
            try {
                if (this.getConvertedValue() == null && this.getValue() != null) {
                    throw new ConfigurationException(this, "Conversion of " + this.getValue() + " for '" + this.getKey() + "' failed and returned null");
                }
            }
            catch (Throwable t) {
                throw new ConfigurationException(this, t);
            }
        }
    }

    @Override
    public int compareTo(Data datum) {
        if (this.getKey() == null && datum.getKey() == null) {
            return 0;
        }
        if (this.getKey() == null) {
            return -1;
        }
        if (datum.getKey() == null) {
            return 1;
        }
        return this.getKey().compareTo(datum.getKey());
    }

    public int hashCode() {
        if (this.getKey() == null) {
            return super.hashCode();
        }
        return this.getKey().hashCode();
    }

    public boolean equals(Object obj) {
        if (this.getKey() == null || !(obj instanceof Data)) {
            return super.equals(obj);
        }
        return this.getKey().equals(((Data)obj).getKey());
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("Data '");
        out.append(this.key);
        out.append('\'');
        out.append(" -");
        out.append(this.typeValue);
        out.append("-> ");
        out.append(this.value);
        return out.toString();
    }

    protected Object convert(Object value) {
        if (this.isList) {
            return this.convertList(value);
        }
        if (this.converter == null) {
            return value;
        }
        return this.convertValue(value);
    }

    private Object convertValue(Object value) {
        return this.converter.convert(this.target, value);
    }

    private List convertList(Object val) {
        String value = (String)val;
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        List<String> list = Arrays.asList(value.split(","));
        if (this.converter == null || this.target.equals(String.class)) {
            return list;
        }
        ArrayList<Object> convertedList = new ArrayList<Object>();
        for (String item : list) {
            convertedList.add(this.convertValue(item));
        }
        return convertedList;
    }

    protected static class NumberConverter
    implements Converter {
        protected NumberConverter() {
        }

        public Object convert(Class type, Object obj) {
            Number num = ConversionUtils.toNumber(obj, "default", Locale.US);
            if (num == null) {
                throw new IllegalArgumentException("Could not convert " + obj + " to a number");
            }
            if (obj.toString().indexOf(46) < 0) {
                num = num.doubleValue() > 2.147483647E9 || num.doubleValue() < -2.147483648E9 ? (Number)num.longValue() : (Number)num.intValue();
            }
            return num;
        }
    }

    protected static class AutoConverter
    implements Converter {
        protected AutoConverter() {
        }

        public Object convert(Class type, Object obj) {
            if (obj instanceof String) {
                try {
                    return this.convert((String)obj);
                }
                catch (Exception e) {
                    return obj;
                }
            }
            return obj;
        }

        public Object convert(String value) {
            if (value.matches("true|false|yes|no|y|n|on|off")) {
                return Type.BOOLEAN.getConverter().convert(Boolean.class, (Object)value);
            }
            if (value.matches("-?[0-9]+(\\.[0-9]+)?")) {
                return Type.NUMBER.getConverter().convert(Number.class, (Object)value);
            }
            if (value.matches("(\\w+\\.)+\\w+")) {
                return Type.FIELD.getConverter().convert(Object.class, (Object)value);
            }
            return value;
        }
    }

    protected static class FieldConverter
    implements Converter {
        protected FieldConverter() {
        }

        public Object convert(Class type, Object value) {
            String fieldpath = (String)value;
            try {
                return ClassUtils.getFieldValue(fieldpath);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Could not retrieve value for field at " + fieldpath, e);
            }
        }
    }

    protected static enum Type {
        AUTO(Object.class, new AutoConverter()),
        BOOLEAN(Boolean.class, (Converter)new BooleanConverter()),
        CUSTOM(null, null),
        FIELD(Object.class, new FieldConverter()),
        NUMBER(Number.class, new NumberConverter()),
        STRING(String.class, (Converter)new StringConverter()),
        LIST(List.class, null),
        LIST_AUTO(List.class, AUTO.getConverter()),
        LIST_BOOLEAN(List.class, BOOLEAN.getConverter()),
        LIST_FIELD(List.class, FIELD.getConverter()),
        LIST_NUMBER(List.class, NUMBER.getConverter()),
        LIST_STRING(List.class, STRING.getConverter());

        private Class target;
        private Converter converter;

        private Type(Class t, Converter c) {
            this.target = t;
            this.converter = c;
        }

        public boolean isCustom() {
            return this.converter == null;
        }

        public boolean isList() {
            return this.target == List.class;
        }

        public Class getTarget() {
            return this.target;
        }

        public Converter getConverter() {
            return this.converter;
        }

        public String value() {
            return this.name().replace('_', '.').toLowerCase();
        }

        public static Type get(String type) {
            if (type == null || type.length() == 0) {
                return CUSTOM;
            }
            return Type.valueOf(type.replace('.', '_').toUpperCase());
        }
    }
}

