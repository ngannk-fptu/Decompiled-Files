/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.data.restricted;

import com.google.common.collect.Lists;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.FloatData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.NullData;
import com.google.template.soy.data.restricted.StringData;
import java.util.ArrayList;
import java.util.List;

public abstract class CollectionData
extends SoyData {
    public void put(Object ... data) {
        if (data.length % 2 != 0) {
            throw new SoyDataException("Varargs to put(...) must have an even number of arguments (key-value pairs).");
        }
        for (int i = 0; i < data.length; i += 2) {
            try {
                this.put((String)data[i], SoyData.createFromExistingData(data[i + 1]));
                continue;
            }
            catch (ClassCastException cce) {
                throw new SoyDataException("Attempting to add a mapping containing a non-string key (key type " + data[i].getClass().getName() + ").");
            }
        }
    }

    public void put(String keyStr, SoyData value) {
        List<String> keys = CollectionData.split(keyStr, '.');
        int numKeys = keys.size();
        CollectionData collectionData = this;
        for (int i = 0; i <= numKeys - 2; ++i) {
            SoyData nextSoyData = collectionData.getSingle(keys.get(i));
            if (nextSoyData != null && !(nextSoyData instanceof CollectionData)) {
                throw new SoyDataException("Failed to evaluate key string \"" + keyStr + "\" for put().");
            }
            CollectionData nextCollectionData = (CollectionData)nextSoyData;
            if (nextCollectionData == null) {
                nextCollectionData = Character.isDigit(keys.get(i + 1).charAt(0)) ? new SoyListData() : new SoyMapData();
                collectionData.putSingle(keys.get(i), nextCollectionData);
            }
            collectionData = nextCollectionData;
        }
        collectionData.putSingle(keys.get(numKeys - 1), CollectionData.ensureValidValue(value));
    }

    public void put(String keyStr, boolean value) {
        this.put(keyStr, BooleanData.forValue(value));
    }

    public void put(String keyStr, int value) {
        this.put(keyStr, IntegerData.forValue(value));
    }

    public void put(String keyStr, long value) {
        this.put(keyStr, IntegerData.forValue(value));
    }

    public void put(String keyStr, double value) {
        this.put(keyStr, FloatData.forValue(value));
    }

    public void put(String keyStr, String value) {
        this.put(keyStr, StringData.forValue(value));
    }

    public void remove(String keyStr) {
        List<String> keys = CollectionData.split(keyStr, '.');
        int numKeys = keys.size();
        CollectionData collectionData = this;
        for (int i = 0; i <= numKeys - 2; ++i) {
            SoyData soyData = collectionData.getSingle(keys.get(i));
            if (soyData == null || !(soyData instanceof CollectionData)) {
                return;
            }
            collectionData = (CollectionData)soyData;
        }
        collectionData.removeSingle(keys.get(numKeys - 1));
    }

    public SoyData get(String keyStr) {
        List<String> keys = CollectionData.split(keyStr, '.');
        int numKeys = keys.size();
        CollectionData collectionData = this;
        for (int i = 0; i <= numKeys - 2; ++i) {
            SoyData soyData = collectionData.getSingle(keys.get(i));
            if (soyData == null || !(soyData instanceof CollectionData)) {
                return null;
            }
            collectionData = (CollectionData)soyData;
        }
        return collectionData.getSingle(keys.get(numKeys - 1));
    }

    public SoyMapData getMapData(String keyStr) {
        return (SoyMapData)this.get(keyStr);
    }

    public SoyListData getListData(String keyStr) {
        return (SoyListData)this.get(keyStr);
    }

    public boolean getBoolean(String keyStr) {
        SoyData valueData = this.get(keyStr);
        if (valueData == null) {
            throw new IllegalArgumentException("Missing key: " + keyStr);
        }
        return valueData.booleanValue();
    }

    public int getInteger(String keyStr) {
        SoyData valueData = this.get(keyStr);
        if (valueData == null) {
            throw new IllegalArgumentException("Missing key: " + keyStr);
        }
        return valueData.integerValue();
    }

    public long getLong(String keyStr) {
        SoyData valueData = this.get(keyStr);
        if (valueData == null) {
            throw new IllegalArgumentException("Missing key: " + keyStr);
        }
        return valueData.longValue();
    }

    public double getFloat(String keyStr) {
        SoyData valueData = this.get(keyStr);
        if (valueData == null) {
            throw new IllegalArgumentException("Missing key: " + keyStr);
        }
        return valueData.floatValue();
    }

    public String getString(String keyStr) {
        SoyData valueData = this.get(keyStr);
        if (valueData == null) {
            throw new IllegalArgumentException("Missing key: " + keyStr);
        }
        return valueData.stringValue();
    }

    public abstract void putSingle(String var1, SoyData var2);

    public abstract void removeSingle(String var1);

    public abstract SoyData getSingle(String var1);

    protected static SoyData ensureValidValue(SoyData value) {
        return value != null ? value : NullData.INSTANCE;
    }

    private static List<String> split(String str, char delim) {
        ArrayList result = Lists.newArrayList();
        int currPartStart = 0;
        while (true) {
            int currPartEnd;
            if ((currPartEnd = str.indexOf(delim, currPartStart)) == -1) break;
            result.add(str.substring(currPartStart, currPartEnd));
            currPartStart = currPartEnd + 1;
        }
        result.add(str.substring(currPartStart));
        return result;
    }
}

