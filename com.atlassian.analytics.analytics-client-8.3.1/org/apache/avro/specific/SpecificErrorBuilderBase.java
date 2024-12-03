/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.specific;

import java.lang.reflect.Constructor;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.Schema;
import org.apache.avro.data.ErrorBuilder;
import org.apache.avro.data.RecordBuilderBase;
import org.apache.avro.generic.GenericData;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificExceptionBase;

public abstract class SpecificErrorBuilderBase<T extends SpecificExceptionBase>
extends RecordBuilderBase<T>
implements ErrorBuilder<T> {
    private Constructor<T> errorConstructor;
    private Object value;
    private boolean hasValue;
    private Throwable cause;
    private boolean hasCause;

    protected SpecificErrorBuilderBase(Schema schema) {
        super(schema, (GenericData)SpecificData.get());
    }

    protected SpecificErrorBuilderBase(Schema schema, SpecificData model) {
        super(schema, (GenericData)model);
    }

    protected SpecificErrorBuilderBase(SpecificErrorBuilderBase<T> other) {
        super(other, (GenericData)SpecificData.get());
        this.errorConstructor = other.errorConstructor;
        this.value = other.value;
        this.hasValue = other.hasValue;
        this.cause = other.cause;
        this.hasCause = other.hasCause;
    }

    protected SpecificErrorBuilderBase(T other) {
        super(((SpecificExceptionBase)other).getSchema(), (GenericData)SpecificData.get());
        Throwable otherCause;
        Object otherValue = ((AvroRemoteException)other).getValue();
        if (otherValue != null) {
            this.setValue(otherValue);
        }
        if ((otherCause = ((Throwable)other).getCause()) != null) {
            this.setCause(otherCause);
        }
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public SpecificErrorBuilderBase<T> setValue(Object value) {
        this.value = value;
        this.hasValue = true;
        return this;
    }

    @Override
    public boolean hasValue() {
        return this.hasValue;
    }

    @Override
    public SpecificErrorBuilderBase<T> clearValue() {
        this.value = null;
        this.hasValue = false;
        return this;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }

    @Override
    public SpecificErrorBuilderBase<T> setCause(Throwable cause) {
        this.cause = cause;
        this.hasCause = true;
        return this;
    }

    @Override
    public boolean hasCause() {
        return this.hasCause;
    }

    @Override
    public SpecificErrorBuilderBase<T> clearCause() {
        this.cause = null;
        this.hasCause = false;
        return this;
    }
}

