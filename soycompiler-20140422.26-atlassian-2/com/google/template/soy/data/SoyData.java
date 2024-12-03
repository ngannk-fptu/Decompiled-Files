/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.data;

import com.google.template.soy.data.SoyAbstractValue;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.FloatData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.NullData;
import com.google.template.soy.data.restricted.StringData;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class SoyData
extends SoyAbstractValue {
    public static SoyData createFromExistingData(Object obj) {
        if (obj == null) {
            return NullData.INSTANCE;
        }
        if (obj instanceof SoyData) {
            return (SoyData)obj;
        }
        if (obj instanceof String) {
            return StringData.forValue((String)obj);
        }
        if (obj instanceof Boolean) {
            return BooleanData.forValue((Boolean)obj);
        }
        if (obj instanceof Integer) {
            return IntegerData.forValue(((Integer)obj).intValue());
        }
        if (obj instanceof Long) {
            return IntegerData.forValue((Long)obj);
        }
        if (obj instanceof Map) {
            Map objCast = (Map)obj;
            return new SoyMapData(objCast);
        }
        if (obj instanceof Iterable) {
            return new SoyListData((Iterable)obj);
        }
        if (obj instanceof Double) {
            return FloatData.forValue((Double)obj);
        }
        if (obj instanceof Float) {
            return FloatData.forValue(((Float)obj).floatValue());
        }
        if (obj instanceof Future) {
            try {
                return SoyData.createFromExistingData(((Future)obj).get());
            }
            catch (InterruptedException e) {
                throw new SoyDataException("Encountered InterruptedException when resolving Future object.", e);
            }
            catch (ExecutionException e) {
                throw new SoyDataException("Encountered ExecutionException when resolving Future object.", e);
            }
        }
        throw new SoyDataException("Attempting to convert unrecognized object to Soy data (object type " + obj.getClass().getName() + ").");
    }

    @Override
    public boolean equals(SoyValue other) {
        return this.equals((Object)other);
    }

    public abstract boolean equals(Object var1);

    @Override
    public boolean coerceToBoolean() {
        return this.toBoolean();
    }

    @Deprecated
    public abstract boolean toBoolean();

    @Override
    public String coerceToString() {
        return this.toString();
    }

    public abstract String toString();
}

