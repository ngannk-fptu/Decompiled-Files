/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;

@JacksonStdImpl
public class StdValueInstantiator
extends ValueInstantiator
implements Serializable {
    private static final long serialVersionUID = 1L;
    protected final String _valueTypeDesc;
    protected final Class<?> _valueClass;
    protected AnnotatedWithParams _defaultCreator;
    protected AnnotatedWithParams _withArgsCreator;
    protected SettableBeanProperty[] _constructorArguments;
    protected JavaType _delegateType;
    protected AnnotatedWithParams _delegateCreator;
    protected SettableBeanProperty[] _delegateArguments;
    protected JavaType _arrayDelegateType;
    protected AnnotatedWithParams _arrayDelegateCreator;
    protected SettableBeanProperty[] _arrayDelegateArguments;
    protected AnnotatedWithParams _fromStringCreator;
    protected AnnotatedWithParams _fromIntCreator;
    protected AnnotatedWithParams _fromLongCreator;
    protected AnnotatedWithParams _fromBigIntegerCreator;
    protected AnnotatedWithParams _fromDoubleCreator;
    protected AnnotatedWithParams _fromBigDecimalCreator;
    protected AnnotatedWithParams _fromBooleanCreator;

    @Deprecated
    public StdValueInstantiator(DeserializationConfig config, Class<?> valueType) {
        this._valueTypeDesc = ClassUtil.nameOf(valueType);
        this._valueClass = valueType == null ? Object.class : valueType;
    }

    public StdValueInstantiator(DeserializationConfig config, JavaType valueType) {
        this._valueTypeDesc = valueType == null ? "UNKNOWN TYPE" : valueType.toString();
        this._valueClass = valueType == null ? Object.class : valueType.getRawClass();
    }

    protected StdValueInstantiator(StdValueInstantiator src) {
        this._valueTypeDesc = src._valueTypeDesc;
        this._valueClass = src._valueClass;
        this._defaultCreator = src._defaultCreator;
        this._constructorArguments = src._constructorArguments;
        this._withArgsCreator = src._withArgsCreator;
        this._delegateType = src._delegateType;
        this._delegateCreator = src._delegateCreator;
        this._delegateArguments = src._delegateArguments;
        this._arrayDelegateType = src._arrayDelegateType;
        this._arrayDelegateCreator = src._arrayDelegateCreator;
        this._arrayDelegateArguments = src._arrayDelegateArguments;
        this._fromStringCreator = src._fromStringCreator;
        this._fromIntCreator = src._fromIntCreator;
        this._fromLongCreator = src._fromLongCreator;
        this._fromBigIntegerCreator = src._fromBigIntegerCreator;
        this._fromDoubleCreator = src._fromDoubleCreator;
        this._fromBigDecimalCreator = src._fromBigDecimalCreator;
        this._fromBooleanCreator = src._fromBooleanCreator;
    }

    public void configureFromObjectSettings(AnnotatedWithParams defaultCreator, AnnotatedWithParams delegateCreator, JavaType delegateType, SettableBeanProperty[] delegateArgs, AnnotatedWithParams withArgsCreator, SettableBeanProperty[] constructorArgs) {
        this._defaultCreator = defaultCreator;
        this._delegateCreator = delegateCreator;
        this._delegateType = delegateType;
        this._delegateArguments = delegateArgs;
        this._withArgsCreator = withArgsCreator;
        this._constructorArguments = constructorArgs;
    }

    public void configureFromArraySettings(AnnotatedWithParams arrayDelegateCreator, JavaType arrayDelegateType, SettableBeanProperty[] arrayDelegateArgs) {
        this._arrayDelegateCreator = arrayDelegateCreator;
        this._arrayDelegateType = arrayDelegateType;
        this._arrayDelegateArguments = arrayDelegateArgs;
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

    public void configureFromBigIntegerCreator(AnnotatedWithParams creator) {
        this._fromBigIntegerCreator = creator;
    }

    public void configureFromDoubleCreator(AnnotatedWithParams creator) {
        this._fromDoubleCreator = creator;
    }

    public void configureFromBigDecimalCreator(AnnotatedWithParams creator) {
        this._fromBigDecimalCreator = creator;
    }

    public void configureFromBooleanCreator(AnnotatedWithParams creator) {
        this._fromBooleanCreator = creator;
    }

    @Override
    public String getValueTypeDesc() {
        return this._valueTypeDesc;
    }

    @Override
    public Class<?> getValueClass() {
        return this._valueClass;
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
    public boolean canCreateFromBigInteger() {
        return this._fromBigIntegerCreator != null;
    }

    @Override
    public boolean canCreateFromDouble() {
        return this._fromDoubleCreator != null;
    }

    @Override
    public boolean canCreateFromBigDecimal() {
        return this._fromBigDecimalCreator != null;
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
    public boolean canCreateUsingDelegate() {
        return this._delegateType != null;
    }

    @Override
    public boolean canCreateUsingArrayDelegate() {
        return this._arrayDelegateType != null;
    }

    @Override
    public boolean canCreateFromObjectWith() {
        return this._withArgsCreator != null;
    }

    @Override
    public boolean canInstantiate() {
        return this.canCreateUsingDefault() || this.canCreateUsingDelegate() || this.canCreateUsingArrayDelegate() || this.canCreateFromObjectWith() || this.canCreateFromString() || this.canCreateFromInt() || this.canCreateFromLong() || this.canCreateFromDouble() || this.canCreateFromBoolean();
    }

    @Override
    public JavaType getDelegateType(DeserializationConfig config) {
        return this._delegateType;
    }

    @Override
    public JavaType getArrayDelegateType(DeserializationConfig config) {
        return this._arrayDelegateType;
    }

    @Override
    public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
        return this._constructorArguments;
    }

    @Override
    public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
        if (this._defaultCreator == null) {
            return super.createUsingDefault(ctxt);
        }
        try {
            return this._defaultCreator.call();
        }
        catch (Exception e) {
            return ctxt.handleInstantiationProblem(this._valueClass, null, (Throwable)((Object)this.rewrapCtorProblem(ctxt, e)));
        }
    }

    @Override
    public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
        if (this._withArgsCreator == null) {
            return super.createFromObjectWith(ctxt, args);
        }
        try {
            return this._withArgsCreator.call(args);
        }
        catch (Exception e) {
            return ctxt.handleInstantiationProblem(this._valueClass, args, (Throwable)((Object)this.rewrapCtorProblem(ctxt, e)));
        }
    }

    @Override
    public Object createUsingDefaultOrWithoutArguments(DeserializationContext ctxt) throws IOException {
        if (this._defaultCreator != null) {
            return this.createUsingDefault(ctxt);
        }
        if (this._withArgsCreator != null) {
            return this.createFromObjectWith(ctxt, new Object[this._constructorArguments.length]);
        }
        return super.createUsingDefaultOrWithoutArguments(ctxt);
    }

    @Override
    public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
        if (this._delegateCreator == null && this._arrayDelegateCreator != null) {
            return this._createUsingDelegate(this._arrayDelegateCreator, this._arrayDelegateArguments, ctxt, delegate);
        }
        return this._createUsingDelegate(this._delegateCreator, this._delegateArguments, ctxt, delegate);
    }

    @Override
    public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
        if (this._arrayDelegateCreator == null && this._delegateCreator != null) {
            return this.createUsingDelegate(ctxt, delegate);
        }
        return this._createUsingDelegate(this._arrayDelegateCreator, this._arrayDelegateArguments, ctxt, delegate);
    }

    @Override
    public Object createFromString(DeserializationContext ctxt, String value) throws IOException {
        if (this._fromStringCreator != null) {
            try {
                return this._fromStringCreator.call1(value);
            }
            catch (Exception t) {
                return ctxt.handleInstantiationProblem(this._fromStringCreator.getDeclaringClass(), value, (Throwable)((Object)this.rewrapCtorProblem(ctxt, t)));
            }
        }
        return super.createFromString(ctxt, value);
    }

    @Override
    public Object createFromInt(DeserializationContext ctxt, int value) throws IOException {
        if (this._fromIntCreator != null) {
            Integer arg = value;
            try {
                return this._fromIntCreator.call1(arg);
            }
            catch (Exception t0) {
                return ctxt.handleInstantiationProblem(this._fromIntCreator.getDeclaringClass(), arg, (Throwable)((Object)this.rewrapCtorProblem(ctxt, t0)));
            }
        }
        if (this._fromLongCreator != null) {
            Long arg = value;
            try {
                return this._fromLongCreator.call1(arg);
            }
            catch (Exception t0) {
                return ctxt.handleInstantiationProblem(this._fromLongCreator.getDeclaringClass(), arg, (Throwable)((Object)this.rewrapCtorProblem(ctxt, t0)));
            }
        }
        if (this._fromBigIntegerCreator != null) {
            BigInteger arg = BigInteger.valueOf(value);
            try {
                return this._fromBigIntegerCreator.call1(arg);
            }
            catch (Exception t0) {
                return ctxt.handleInstantiationProblem(this._fromBigIntegerCreator.getDeclaringClass(), arg, (Throwable)((Object)this.rewrapCtorProblem(ctxt, t0)));
            }
        }
        return super.createFromInt(ctxt, value);
    }

    @Override
    public Object createFromLong(DeserializationContext ctxt, long value) throws IOException {
        if (this._fromLongCreator != null) {
            Long arg = value;
            try {
                return this._fromLongCreator.call1(arg);
            }
            catch (Exception t0) {
                return ctxt.handleInstantiationProblem(this._fromLongCreator.getDeclaringClass(), arg, (Throwable)((Object)this.rewrapCtorProblem(ctxt, t0)));
            }
        }
        if (this._fromBigIntegerCreator != null) {
            BigInteger arg = BigInteger.valueOf(value);
            try {
                return this._fromBigIntegerCreator.call1(arg);
            }
            catch (Exception t0) {
                return ctxt.handleInstantiationProblem(this._fromBigIntegerCreator.getDeclaringClass(), arg, (Throwable)((Object)this.rewrapCtorProblem(ctxt, t0)));
            }
        }
        return super.createFromLong(ctxt, value);
    }

    @Override
    public Object createFromBigInteger(DeserializationContext ctxt, BigInteger value) throws IOException {
        if (this._fromBigIntegerCreator != null) {
            try {
                return this._fromBigIntegerCreator.call1(value);
            }
            catch (Exception t) {
                return ctxt.handleInstantiationProblem(this._fromBigIntegerCreator.getDeclaringClass(), value, (Throwable)((Object)this.rewrapCtorProblem(ctxt, t)));
            }
        }
        return super.createFromBigInteger(ctxt, value);
    }

    @Override
    public Object createFromDouble(DeserializationContext ctxt, double value) throws IOException {
        if (this._fromDoubleCreator != null) {
            Double arg = value;
            try {
                return this._fromDoubleCreator.call1(arg);
            }
            catch (Exception t0) {
                return ctxt.handleInstantiationProblem(this._fromDoubleCreator.getDeclaringClass(), arg, (Throwable)((Object)this.rewrapCtorProblem(ctxt, t0)));
            }
        }
        if (this._fromBigDecimalCreator != null) {
            BigDecimal arg = BigDecimal.valueOf(value);
            try {
                return this._fromBigDecimalCreator.call1(arg);
            }
            catch (Exception t0) {
                return ctxt.handleInstantiationProblem(this._fromBigDecimalCreator.getDeclaringClass(), arg, (Throwable)((Object)this.rewrapCtorProblem(ctxt, t0)));
            }
        }
        return super.createFromDouble(ctxt, value);
    }

    @Override
    public Object createFromBigDecimal(DeserializationContext ctxt, BigDecimal value) throws IOException {
        Double dbl;
        if (this._fromBigDecimalCreator != null) {
            try {
                return this._fromBigDecimalCreator.call1(value);
            }
            catch (Exception t) {
                return ctxt.handleInstantiationProblem(this._fromBigDecimalCreator.getDeclaringClass(), value, (Throwable)((Object)this.rewrapCtorProblem(ctxt, t)));
            }
        }
        if (this._fromDoubleCreator != null && (dbl = StdValueInstantiator.tryConvertToDouble(value)) != null) {
            try {
                return this._fromDoubleCreator.call1(dbl);
            }
            catch (Exception t0) {
                return ctxt.handleInstantiationProblem(this._fromDoubleCreator.getDeclaringClass(), dbl, (Throwable)((Object)this.rewrapCtorProblem(ctxt, t0)));
            }
        }
        return super.createFromBigDecimal(ctxt, value);
    }

    static Double tryConvertToDouble(BigDecimal value) {
        double doubleValue = value.doubleValue();
        return Double.isInfinite(doubleValue) ? null : Double.valueOf(doubleValue);
    }

    @Override
    public Object createFromBoolean(DeserializationContext ctxt, boolean value) throws IOException {
        if (this._fromBooleanCreator == null) {
            return super.createFromBoolean(ctxt, value);
        }
        Boolean arg = value;
        try {
            return this._fromBooleanCreator.call1(arg);
        }
        catch (Exception t0) {
            return ctxt.handleInstantiationProblem(this._fromBooleanCreator.getDeclaringClass(), arg, (Throwable)((Object)this.rewrapCtorProblem(ctxt, t0)));
        }
    }

    @Override
    public AnnotatedWithParams getDelegateCreator() {
        return this._delegateCreator;
    }

    @Override
    public AnnotatedWithParams getArrayDelegateCreator() {
        return this._arrayDelegateCreator;
    }

    @Override
    public AnnotatedWithParams getDefaultCreator() {
        return this._defaultCreator;
    }

    @Override
    public AnnotatedWithParams getWithArgsCreator() {
        return this._withArgsCreator;
    }

    @Deprecated
    protected JsonMappingException wrapException(Throwable t) {
        for (Throwable curr = t; curr != null; curr = curr.getCause()) {
            if (!(curr instanceof JsonMappingException)) continue;
            return (JsonMappingException)((Object)curr);
        }
        return new JsonMappingException(null, "Instantiation of " + this.getValueTypeDesc() + " value failed: " + ClassUtil.exceptionMessage(t), t);
    }

    @Deprecated
    protected JsonMappingException unwrapAndWrapException(DeserializationContext ctxt, Throwable t) {
        for (Throwable curr = t; curr != null; curr = curr.getCause()) {
            if (!(curr instanceof JsonMappingException)) continue;
            return (JsonMappingException)((Object)curr);
        }
        return ctxt.instantiationException(this.getValueClass(), t);
    }

    protected JsonMappingException wrapAsJsonMappingException(DeserializationContext ctxt, Throwable t) {
        if (t instanceof JsonMappingException) {
            return (JsonMappingException)((Object)t);
        }
        return ctxt.instantiationException(this.getValueClass(), t);
    }

    protected JsonMappingException rewrapCtorProblem(DeserializationContext ctxt, Throwable t) {
        Throwable cause;
        if ((t instanceof ExceptionInInitializerError || t instanceof InvocationTargetException) && (cause = t.getCause()) != null) {
            t = cause;
        }
        return this.wrapAsJsonMappingException(ctxt, t);
    }

    private Object _createUsingDelegate(AnnotatedWithParams delegateCreator, SettableBeanProperty[] delegateArguments, DeserializationContext ctxt, Object delegate) throws IOException {
        if (delegateCreator == null) {
            throw new IllegalStateException("No delegate constructor for " + this.getValueTypeDesc());
        }
        try {
            if (delegateArguments == null) {
                return delegateCreator.call1(delegate);
            }
            int len = delegateArguments.length;
            Object[] args = new Object[len];
            for (int i = 0; i < len; ++i) {
                SettableBeanProperty prop = delegateArguments[i];
                args[i] = prop == null ? delegate : ctxt.findInjectableValue(prop.getInjectableValueId(), prop, null);
            }
            return delegateCreator.call(args);
        }
        catch (Exception t) {
            throw this.rewrapCtorProblem(ctxt, t);
        }
    }
}

