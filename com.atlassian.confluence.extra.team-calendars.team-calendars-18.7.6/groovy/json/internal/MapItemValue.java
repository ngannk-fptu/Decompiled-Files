/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.internal.Exceptions;
import groovy.json.internal.Value;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapItemValue
implements Map.Entry<String, Value> {
    final Value name;
    final Value value;
    private String key = null;
    private static final boolean internKeys = Boolean.parseBoolean(System.getProperty("groovy.json.implementation.internKeys", "false"));
    protected static ConcurrentHashMap<String, String> internedKeysCache;

    public MapItemValue(Value name, Value value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getKey() {
        if (this.key == null) {
            if (internKeys) {
                this.key = this.name.toString();
                String keyPrime = internedKeysCache.get(this.key);
                if (keyPrime == null) {
                    this.key = this.key.intern();
                    internedKeysCache.put(this.key, this.key);
                } else {
                    this.key = keyPrime;
                }
            } else {
                this.key = this.name.toString();
            }
        }
        return this.key;
    }

    @Override
    public Value getValue() {
        return this.value;
    }

    @Override
    public Value setValue(Value value) {
        Exceptions.die("not that kind of Entry");
        return null;
    }

    static {
        if (internKeys) {
            internedKeysCache = new ConcurrentHashMap();
        }
    }
}

