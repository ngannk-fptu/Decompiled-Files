/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 */
package com.google.template.soy.data;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyList;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.CollectionData;
import com.google.template.soy.data.restricted.FloatData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.StringData;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;

public final class SoyListData
extends CollectionData
implements Iterable<SoyData>,
SoyList {
    private final List<SoyData> list = Lists.newArrayList();

    public SoyListData() {
    }

    public SoyListData(Iterable<?> data) {
        this();
        this.add(data);
    }

    public SoyListData(Object ... values) {
        this(Arrays.asList(values));
    }

    public List<SoyData> asList() {
        return Collections.unmodifiableList(this.list);
    }

    @Override
    public String toString() {
        return "[" + Joiner.on((String)", ").join(this.list) + "]";
    }

    @Override
    @Deprecated
    public boolean toBoolean() {
        return true;
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public int length() {
        return this.list.size();
    }

    @Override
    public Iterator<SoyData> iterator() {
        return Collections.unmodifiableList(this.list).iterator();
    }

    private void add(Iterable<?> data) {
        for (Object el : data) {
            try {
                this.add(SoyData.createFromExistingData(el));
            }
            catch (SoyDataException sde) {
                sde.prependIndexToDataPath(this.list.size());
                throw sde;
            }
        }
    }

    public void add(Object ... values) {
        this.add(Arrays.asList(values));
    }

    public void add(SoyData value) {
        this.list.add(SoyListData.ensureValidValue(value));
    }

    public void add(boolean value) {
        this.add(BooleanData.forValue(value));
    }

    public void add(int value) {
        this.add(IntegerData.forValue(value));
    }

    public void add(long value) {
        this.add(IntegerData.forValue(value));
    }

    public void add(double value) {
        this.add(FloatData.forValue(value));
    }

    public void add(String value) {
        this.add(StringData.forValue(value));
    }

    public void set(int index, SoyData value) {
        if (index == this.list.size()) {
            this.list.add(SoyListData.ensureValidValue(value));
        } else {
            this.list.set(index, SoyListData.ensureValidValue(value));
        }
    }

    public void set(int index, boolean value) {
        this.set(index, BooleanData.forValue(value));
    }

    public void set(int index, int value) {
        this.set(index, IntegerData.forValue(value));
    }

    public void set(int index, double value) {
        this.set(index, FloatData.forValue(value));
    }

    public void set(int index, String value) {
        this.set(index, StringData.forValue(value));
    }

    public void remove(int index) {
        this.list.remove(index);
    }

    @Override
    public SoyData get(int index) {
        try {
            return this.list.get(index);
        }
        catch (IndexOutOfBoundsException ioobe) {
            return null;
        }
    }

    public SoyMapData getMapData(int index) {
        return (SoyMapData)this.get(index);
    }

    public SoyListData getListData(int index) {
        return (SoyListData)this.get(index);
    }

    public boolean getBoolean(int index) {
        return this.get(index).booleanValue();
    }

    public int getInteger(int index) {
        return this.get(index).integerValue();
    }

    public long getLong(int index) {
        return this.get(index).longValue();
    }

    public double getFloat(int index) {
        return this.get(index).floatValue();
    }

    public String getString(int index) {
        return this.get(index).stringValue();
    }

    @Override
    public void putSingle(String key, SoyData value) {
        this.set(Integer.parseInt(key), value);
    }

    @Override
    public void removeSingle(String key) {
        this.remove(Integer.parseInt(key));
    }

    @Override
    public SoyData getSingle(String key) {
        return this.get(Integer.parseInt(key));
    }

    @Override
    @Nonnull
    public List<? extends SoyValueProvider> asJavaList() {
        return this.asList();
    }

    @Override
    @Nonnull
    public List<? extends SoyValue> asResolvedJavaList() {
        return this.asList();
    }

    @Override
    public SoyValueProvider getProvider(int index) {
        return this.get(index);
    }

    @Override
    public int getItemCnt() {
        return this.length();
    }

    @Override
    @Nonnull
    public Iterable<? extends SoyValue> getItemKeys() {
        ImmutableList.Builder indexesBuilder = ImmutableList.builder();
        int n = this.length();
        for (int i = 0; i < n; ++i) {
            indexesBuilder.add((Object)IntegerData.forValue(i));
        }
        return indexesBuilder.build();
    }

    @Override
    public boolean hasItem(SoyValue key) {
        int index = this.getIntegerIndex(key);
        return 0 <= index && index < this.length();
    }

    @Override
    public SoyValue getItem(SoyValue key) {
        return this.get(this.getIntegerIndex(key));
    }

    @Override
    public SoyValueProvider getItemProvider(SoyValue key) {
        return this.get(this.getIntegerIndex(key));
    }

    private int getIntegerIndex(SoyValue key) {
        try {
            return ((IntegerData)key).integerValue();
        }
        catch (ClassCastException cce) {
            try {
                return Integer.parseInt(key.coerceToString());
            }
            catch (NumberFormatException nfe) {
                throw new SoyDataException("SoyList accessed with non-integer key (got key type " + key.getClass().getName() + ").");
            }
        }
    }
}

