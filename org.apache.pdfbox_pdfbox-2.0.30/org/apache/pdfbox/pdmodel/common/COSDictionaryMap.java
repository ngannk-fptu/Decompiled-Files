/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class COSDictionaryMap<K, V>
implements Map<K, V> {
    private final COSDictionary map;
    private final Map<K, V> actuals;

    public COSDictionaryMap(Map<K, V> actualsMap, COSDictionary dicMap) {
        this.actuals = actualsMap;
        this.map = dicMap;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.actuals.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.actuals.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return this.actuals.get(key);
    }

    @Override
    public V put(K key, V value) {
        COSObjectable object = (COSObjectable)value;
        this.map.setItem(COSName.getPDFName((String)key), object.getCOSObject());
        return this.actuals.put(key, value);
    }

    @Override
    public V remove(Object key) {
        this.map.removeItem(COSName.getPDFName((String)key));
        return this.actuals.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> t) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void clear() {
        this.map.clear();
        this.actuals.clear();
    }

    @Override
    public Set<K> keySet() {
        return this.actuals.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.actuals.values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(this.actuals.entrySet());
    }

    @Override
    public boolean equals(Object o) {
        boolean retval = false;
        if (o instanceof COSDictionaryMap) {
            COSDictionaryMap other = (COSDictionaryMap)o;
            retval = other.map.equals(this.map);
        }
        return retval;
    }

    public String toString() {
        return this.actuals.toString();
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }

    public static COSDictionary convert(Map<String, ?> someMap) {
        COSDictionary dic = new COSDictionary();
        for (Map.Entry<String, ?> entry : someMap.entrySet()) {
            String name = entry.getKey();
            COSObjectable object = (COSObjectable)entry.getValue();
            dic.setItem(COSName.getPDFName(name), object.getCOSObject());
        }
        return dic;
    }

    public static COSDictionaryMap<String, Object> convertBasicTypesToMap(COSDictionary map) throws IOException {
        COSDictionaryMap retval = null;
        if (map != null) {
            HashMap<String, Object> actualMap = new HashMap<String, Object>();
            for (COSName key : map.keySet()) {
                COSBase cosObj = map.getDictionaryObject(key);
                Object actualObject = null;
                if (cosObj instanceof COSString) {
                    actualObject = ((COSString)cosObj).getString();
                } else if (cosObj instanceof COSInteger) {
                    actualObject = ((COSInteger)cosObj).intValue();
                } else if (cosObj instanceof COSName) {
                    actualObject = ((COSName)cosObj).getName();
                } else if (cosObj instanceof COSFloat) {
                    actualObject = Float.valueOf(((COSFloat)cosObj).floatValue());
                } else if (cosObj instanceof COSBoolean) {
                    actualObject = ((COSBoolean)cosObj).getValue() ? Boolean.TRUE : Boolean.FALSE;
                } else {
                    throw new IOException("Error:unknown type of object to convert:" + cosObj);
                }
                actualMap.put(key.getName(), actualObject);
            }
            retval = new COSDictionaryMap(actualMap, map);
        }
        return retval;
    }
}

