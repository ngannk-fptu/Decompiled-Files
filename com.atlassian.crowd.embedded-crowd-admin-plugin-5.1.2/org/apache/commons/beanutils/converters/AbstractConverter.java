/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.beanutils.converters;

import java.lang.reflect.Array;
import java.util.Collection;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractConverter
implements Converter {
    private static final String DEFAULT_CONFIG_MSG = "(N.B. Converters can be configured to use default values to avoid throwing exceptions)";
    private static final String PACKAGE = "org.apache.commons.beanutils.converters.";
    private transient Log log;
    private boolean useDefault = false;
    private Object defaultValue = null;

    public AbstractConverter() {
    }

    public AbstractConverter(Object defaultValue) {
        this.setDefaultValue(defaultValue);
    }

    public boolean isUseDefault() {
        return this.useDefault;
    }

    @Override
    public <T> T convert(Class<T> type, Object value) {
        if (type == null) {
            return this.convertToDefaultType(type, value);
        }
        Class<?> sourceType = value == null ? null : value.getClass();
        Class<T> targetType = ConvertUtils.primitiveToWrapper(type);
        if (this.log().isDebugEnabled()) {
            this.log().debug((Object)("Converting" + (value == null ? "" : " '" + this.toString(sourceType) + "'") + " value '" + value + "' to type '" + this.toString(targetType) + "'"));
        }
        if ((value = this.convertArray(value)) == null) {
            return this.handleMissing(targetType);
        }
        sourceType = value.getClass();
        try {
            if (targetType.equals(String.class)) {
                return targetType.cast(this.convertToString(value));
            }
            if (targetType.equals(sourceType)) {
                if (this.log().isDebugEnabled()) {
                    this.log().debug((Object)("    No conversion required, value is already a " + this.toString(targetType)));
                }
                return targetType.cast(value);
            }
            T result = this.convertToType(targetType, value);
            if (this.log().isDebugEnabled()) {
                this.log().debug((Object)("    Converted to " + this.toString(targetType) + " value '" + result + "'"));
            }
            return targetType.cast(result);
        }
        catch (Throwable t) {
            return this.handleError(targetType, value, t);
        }
    }

    protected String convertToString(Object value) throws Throwable {
        return value.toString();
    }

    protected abstract <T> T convertToType(Class<T> var1, Object var2) throws Throwable;

    protected Object convertArray(Object value) {
        if (value == null) {
            return null;
        }
        if (value.getClass().isArray()) {
            if (Array.getLength(value) > 0) {
                return Array.get(value, 0);
            }
            return null;
        }
        if (value instanceof Collection) {
            Collection collection = (Collection)value;
            if (collection.size() > 0) {
                return collection.iterator().next();
            }
            return null;
        }
        return value;
    }

    protected <T> T handleError(Class<T> type, Object value, Throwable cause) {
        if (this.log().isDebugEnabled()) {
            if (cause instanceof ConversionException) {
                this.log().debug((Object)("    Conversion threw ConversionException: " + cause.getMessage()));
            } else {
                this.log().debug((Object)("    Conversion threw " + cause));
            }
        }
        if (this.useDefault) {
            return this.handleMissing(type);
        }
        ConversionException cex = null;
        if (cause instanceof ConversionException) {
            cex = (ConversionException)cause;
            if (this.log().isDebugEnabled()) {
                this.log().debug((Object)("    Re-throwing ConversionException: " + cex.getMessage()));
                this.log().debug((Object)"    (N.B. Converters can be configured to use default values to avoid throwing exceptions)");
            }
        } else {
            String msg = "Error converting from '" + this.toString(value.getClass()) + "' to '" + this.toString(type) + "' " + cause.getMessage();
            cex = new ConversionException(msg, cause);
            if (this.log().isDebugEnabled()) {
                this.log().debug((Object)("    Throwing ConversionException: " + msg));
                this.log().debug((Object)"    (N.B. Converters can be configured to use default values to avoid throwing exceptions)");
            }
            BeanUtils.initCause(cex, cause);
        }
        throw cex;
    }

    protected <T> T handleMissing(Class<T> type) {
        if (this.useDefault || type.equals(String.class)) {
            Object value = this.getDefault(type);
            if (this.useDefault && value != null && !type.equals(value.getClass())) {
                try {
                    value = this.convertToType(type, this.defaultValue);
                }
                catch (Throwable t) {
                    throw new ConversionException("Default conversion to " + this.toString(type) + " failed.", t);
                }
            }
            if (this.log().isDebugEnabled()) {
                this.log().debug((Object)("    Using default " + (value == null ? "" : this.toString(value.getClass()) + " ") + "value '" + this.defaultValue + "'"));
            }
            return type.cast(value);
        }
        ConversionException cex = new ConversionException("No value specified for '" + this.toString(type) + "'");
        if (this.log().isDebugEnabled()) {
            this.log().debug((Object)("    Throwing ConversionException: " + cex.getMessage()));
            this.log().debug((Object)"    (N.B. Converters can be configured to use default values to avoid throwing exceptions)");
        }
        throw cex;
    }

    protected void setDefaultValue(Object defaultValue) {
        this.useDefault = false;
        if (this.log().isDebugEnabled()) {
            this.log().debug((Object)("Setting default value: " + defaultValue));
        }
        this.defaultValue = defaultValue == null ? null : this.convert(this.getDefaultType(), defaultValue);
        this.useDefault = true;
    }

    protected abstract Class<?> getDefaultType();

    protected Object getDefault(Class<?> type) {
        if (type.equals(String.class)) {
            return null;
        }
        return this.defaultValue;
    }

    public String toString() {
        return this.toString(this.getClass()) + "[UseDefault=" + this.useDefault + "]";
    }

    Log log() {
        if (this.log == null) {
            this.log = LogFactory.getLog(this.getClass());
        }
        return this.log;
    }

    String toString(Class<?> type) {
        String typeName = null;
        if (type == null) {
            typeName = "null";
        } else if (type.isArray()) {
            Class<?> elementType = type.getComponentType();
            int count = 1;
            while (elementType.isArray()) {
                elementType = elementType.getComponentType();
                ++count;
            }
            typeName = elementType.getName();
            for (int i = 0; i < count; ++i) {
                typeName = typeName + "[]";
            }
        } else {
            typeName = type.getName();
        }
        if (typeName.startsWith("java.lang.") || typeName.startsWith("java.util.") || typeName.startsWith("java.math.")) {
            typeName = typeName.substring("java.lang.".length());
        } else if (typeName.startsWith(PACKAGE)) {
            typeName = typeName.substring(PACKAGE.length());
        }
        return typeName;
    }

    private <T> T convertToDefaultType(Class<T> targetClass, Object value) {
        Object result = this.convert(this.getDefaultType(), value);
        return (T)result;
    }

    protected ConversionException conversionException(Class<?> type, Object value) {
        return new ConversionException("Can't convert value '" + value + "' to type " + type);
    }
}

