/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.deser.SettableBeanProperty;
import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.map.deser.impl.CreatorProperty;
import org.codehaus.jackson.map.introspect.AnnotatedWithParams;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StdValueInstantiator
extends ValueInstantiator {
    protected final String _valueTypeDesc;
    protected final boolean _cfgEmptyStringsAsObjects;
    protected AnnotatedWithParams _defaultCreator;
    protected CreatorProperty[] _constructorArguments;
    protected AnnotatedWithParams _withArgsCreator;
    protected JavaType _delegateType;
    protected AnnotatedWithParams _delegateCreator;
    protected AnnotatedWithParams _fromStringCreator;
    protected AnnotatedWithParams _fromIntCreator;
    protected AnnotatedWithParams _fromLongCreator;
    protected AnnotatedWithParams _fromDoubleCreator;
    protected AnnotatedWithParams _fromBooleanCreator;

    public StdValueInstantiator(DeserializationConfig config, Class<?> valueType) {
        this._cfgEmptyStringsAsObjects = config == null ? false : config.isEnabled(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        this._valueTypeDesc = valueType == null ? "UNKNOWN TYPE" : valueType.getName();
    }

    public StdValueInstantiator(DeserializationConfig config, JavaType valueType) {
        this._cfgEmptyStringsAsObjects = config == null ? false : config.isEnabled(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        this._valueTypeDesc = valueType == null ? "UNKNOWN TYPE" : valueType.toString();
    }

    protected StdValueInstantiator(StdValueInstantiator src) {
        this._cfgEmptyStringsAsObjects = src._cfgEmptyStringsAsObjects;
        this._valueTypeDesc = src._valueTypeDesc;
        this._defaultCreator = src._defaultCreator;
        this._constructorArguments = src._constructorArguments;
        this._withArgsCreator = src._withArgsCreator;
        this._delegateType = src._delegateType;
        this._delegateCreator = src._delegateCreator;
        this._fromStringCreator = src._fromStringCreator;
        this._fromIntCreator = src._fromIntCreator;
        this._fromLongCreator = src._fromLongCreator;
        this._fromDoubleCreator = src._fromDoubleCreator;
        this._fromBooleanCreator = src._fromBooleanCreator;
    }

    public void configureFromObjectSettings(AnnotatedWithParams defaultCreator, AnnotatedWithParams delegateCreator, JavaType delegateType, AnnotatedWithParams withArgsCreator, CreatorProperty[] constructorArgs) {
        this._defaultCreator = defaultCreator;
        this._delegateCreator = delegateCreator;
        this._delegateType = delegateType;
        this._withArgsCreator = withArgsCreator;
        this._constructorArguments = constructorArgs;
    }

    public void configureFromStringCreator(AnnotatedWithParams creator) {
        this._fromStringCreator = creator;
    }

    public void configureFromIntCreator(AnnotatedWithParams creator) {
        this._fromIntCreator = creator;
    }

    public void configureFromLongCreator(AnnotatedWithParams creator) {
        this._fromLongCreator = creator;
    }

    public void configureFromDoubleCreator(AnnotatedWithParams creator) {
        this._fromDoubleCreator = creator;
    }

    public void configureFromBooleanCreator(AnnotatedWithParams creator) {
        this._fromBooleanCreator = creator;
    }

    @Override
    public String getValueTypeDesc() {
        return this._valueTypeDesc;
    }

    @Override
    public boolean canCreateFromString() {
        return this._fromStringCreator != null;
    }

    @Override
    public boolean canCreateFromInt() {
        return this._fromIntCreator != null;
    }

    @Override
    public boolean canCreateFromLong() {
        return this._fromLongCreator != null;
    }

    @Override
    public boolean canCreateFromDouble() {
        return this._fromDoubleCreator != null;
    }

    @Override
    public boolean canCreateFromBoolean() {
        return this._fromBooleanCreator != null;
    }

    @Override
    public boolean canCreateUsingDefault() {
        return this._defaultCreator != null;
    }

    @Override
    public boolean canCreateFromObjectWith() {
        return this._withArgsCreator != null;
    }

    @Override
    public JavaType getDelegateType() {
        return this._delegateType;
    }

    @Override
    public SettableBeanProperty[] getFromObjectArguments() {
        return this._constructorArguments;
    }

    @Override
    public Object createUsingDefault() throws IOException, JsonProcessingException {
        if (this._defaultCreator == null) {
            throw new IllegalStateException("No default constructor for " + this.getValueTypeDesc());
        }
        try {
            return this._defaultCreator.call();
        }
        catch (ExceptionInInitializerError e) {
            throw this.wrapException(e);
        }
        catch (Exception e) {
            throw this.wrapException(e);
        }
    }

    @Override
    public Object createFromObjectWith(Object[] args) throws IOException, JsonProcessingException {
        if (this._withArgsCreator == null) {
            throw new IllegalStateException("No with-args constructor for " + this.getValueTypeDesc());
        }
        try {
            return this._withArgsCreator.call(args);
        }
        catch (ExceptionInInitializerError e) {
            throw this.wrapException(e);
        }
        catch (Exception e) {
            throw this.wrapException(e);
        }
    }

    @Override
    public Object createUsingDelegate(Object delegate) throws IOException, JsonProcessingException {
        if (this._delegateCreator == null) {
            throw new IllegalStateException("No delegate constructor for " + this.getValueTypeDesc());
        }
        try {
            return this._delegateCreator.call1(delegate);
        }
        catch (ExceptionInInitializerError e) {
            throw this.wrapException(e);
        }
        catch (Exception e) {
            throw this.wrapException(e);
        }
    }

    @Override
    public Object createFromString(String value) throws IOException, JsonProcessingException {
        if (this._fromStringCreator != null) {
            try {
                return this._fromStringCreator.call1(value);
            }
            catch (Exception e) {
                throw this.wrapException(e);
            }
        }
        return this._createFromStringFallbacks(value);
    }

    @Override
    public Object createFromInt(int value) throws IOException, JsonProcessingException {
        try {
            if (this._fromIntCreator != null) {
                return this._fromIntCreator.call1(value);
            }
            if (this._fromLongCreator != null) {
                return this._fromLongCreator.call1(value);
            }
        }
        catch (Exception e) {
            throw this.wrapException(e);
        }
        throw new JsonMappingException("Can not instantiate value of type " + this.getValueTypeDesc() + " from JSON integral number; no single-int-arg constructor/factory method");
    }

    @Override
    public Object createFromLong(long value) throws IOException, JsonProcessingException {
        try {
            if (this._fromLongCreator != null) {
                return this._fromLongCreator.call1(value);
            }
        }
        catch (Exception e) {
            throw this.wrapException(e);
        }
        throw new JsonMappingException("Can not instantiate value of type " + this.getValueTypeDesc() + " from JSON long integral number; no single-long-arg constructor/factory method");
    }

    @Override
    public Object createFromDouble(double value) throws IOException, JsonProcessingException {
        try {
            if (this._fromDoubleCreator != null) {
                return this._fromDoubleCreator.call1(value);
            }
        }
        catch (Exception e) {
            throw this.wrapException(e);
        }
        throw new JsonMappingException("Can not instantiate value of type " + this.getValueTypeDesc() + " from JSON floating-point number; no one-double/Double-arg constructor/factory method");
    }

    @Override
    public Object createFromBoolean(boolean value) throws IOException, JsonProcessingException {
        try {
            if (this._fromBooleanCreator != null) {
                return this._fromBooleanCreator.call1(value);
            }
        }
        catch (Exception e) {
            throw this.wrapException(e);
        }
        throw new JsonMappingException("Can not instantiate value of type " + this.getValueTypeDesc() + " from JSON boolean value; no single-boolean/Boolean-arg constructor/factory method");
    }

    @Override
    public AnnotatedWithParams getDelegateCreator() {
        return this._delegateCreator;
    }

    @Override
    public AnnotatedWithParams getDefaultCreator() {
        return this._defaultCreator;
    }

    @Override
    public AnnotatedWithParams getWithArgsCreator() {
        return this._withArgsCreator;
    }

    protected Object _createFromStringFallbacks(String value) throws IOException, JsonProcessingException {
        if (this._fromBooleanCreator != null) {
            String str = value.trim();
            if ("true".equals(str)) {
                return this.createFromBoolean(true);
            }
            if ("false".equals(str)) {
                return this.createFromBoolean(false);
            }
        }
        if (this._cfgEmptyStringsAsObjects && value.length() == 0) {
            return null;
        }
        throw new JsonMappingException("Can not instantiate value of type " + this.getValueTypeDesc() + " from JSON String; no single-String constructor/factory method");
    }

    protected JsonMappingException wrapException(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return new JsonMappingException("Instantiation of " + this.getValueTypeDesc() + " value failed: " + t.getMessage(), t);
    }
}

