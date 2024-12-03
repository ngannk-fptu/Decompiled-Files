/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser.impl;

import java.util.HashMap;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ResolvableSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.impl.ReadOnlyClassToSerializerMap;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SerializerCache {
    private HashMap<TypeKey, JsonSerializer<Object>> _sharedMap = new HashMap(64);
    private ReadOnlyClassToSerializerMap _readOnlyMap = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ReadOnlyClassToSerializerMap getReadOnlyLookupMap() {
        ReadOnlyClassToSerializerMap m;
        SerializerCache serializerCache = this;
        synchronized (serializerCache) {
            m = this._readOnlyMap;
            if (m == null) {
                this._readOnlyMap = m = ReadOnlyClassToSerializerMap.from(this._sharedMap);
            }
        }
        return m.instance();
    }

    public synchronized int size() {
        return this._sharedMap.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JsonSerializer<Object> untypedValueSerializer(Class<?> type) {
        SerializerCache serializerCache = this;
        synchronized (serializerCache) {
            return this._sharedMap.get(new TypeKey(type, false));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JsonSerializer<Object> untypedValueSerializer(JavaType type) {
        SerializerCache serializerCache = this;
        synchronized (serializerCache) {
            return this._sharedMap.get(new TypeKey(type, false));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JsonSerializer<Object> typedValueSerializer(JavaType type) {
        SerializerCache serializerCache = this;
        synchronized (serializerCache) {
            return this._sharedMap.get(new TypeKey(type, true));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JsonSerializer<Object> typedValueSerializer(Class<?> cls) {
        SerializerCache serializerCache = this;
        synchronized (serializerCache) {
            return this._sharedMap.get(new TypeKey(cls, true));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addTypedSerializer(JavaType type, JsonSerializer<Object> ser) {
        SerializerCache serializerCache = this;
        synchronized (serializerCache) {
            if (this._sharedMap.put(new TypeKey(type, true), ser) == null) {
                this._readOnlyMap = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addTypedSerializer(Class<?> cls, JsonSerializer<Object> ser) {
        SerializerCache serializerCache = this;
        synchronized (serializerCache) {
            if (this._sharedMap.put(new TypeKey(cls, true), ser) == null) {
                this._readOnlyMap = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addAndResolveNonTypedSerializer(Class<?> type, JsonSerializer<Object> ser, SerializerProvider provider) throws JsonMappingException {
        SerializerCache serializerCache = this;
        synchronized (serializerCache) {
            if (this._sharedMap.put(new TypeKey(type, false), ser) == null) {
                this._readOnlyMap = null;
            }
            if (ser instanceof ResolvableSerializer) {
                ((ResolvableSerializer)((Object)ser)).resolve(provider);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addAndResolveNonTypedSerializer(JavaType type, JsonSerializer<Object> ser, SerializerProvider provider) throws JsonMappingException {
        SerializerCache serializerCache = this;
        synchronized (serializerCache) {
            if (this._sharedMap.put(new TypeKey(type, false), ser) == null) {
                this._readOnlyMap = null;
            }
            if (ser instanceof ResolvableSerializer) {
                ((ResolvableSerializer)((Object)ser)).resolve(provider);
            }
        }
    }

    public synchronized void flush() {
        this._sharedMap.clear();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class TypeKey {
        protected int _hashCode;
        protected Class<?> _class;
        protected JavaType _type;
        protected boolean _isTyped;

        public TypeKey(Class<?> key, boolean typed) {
            this._class = key;
            this._type = null;
            this._isTyped = typed;
            this._hashCode = TypeKey.hash(key, typed);
        }

        public TypeKey(JavaType key, boolean typed) {
            this._type = key;
            this._class = null;
            this._isTyped = typed;
            this._hashCode = TypeKey.hash(key, typed);
        }

        private static final int hash(Class<?> cls, boolean typed) {
            int hash = cls.getName().hashCode();
            if (typed) {
                ++hash;
            }
            return hash;
        }

        private static final int hash(JavaType type, boolean typed) {
            int hash = type.hashCode() - 1;
            if (typed) {
                --hash;
            }
            return hash;
        }

        public void resetTyped(Class<?> cls) {
            this._type = null;
            this._class = cls;
            this._isTyped = true;
            this._hashCode = TypeKey.hash(cls, true);
        }

        public void resetUntyped(Class<?> cls) {
            this._type = null;
            this._class = cls;
            this._isTyped = false;
            this._hashCode = TypeKey.hash(cls, false);
        }

        public void resetTyped(JavaType type) {
            this._type = type;
            this._class = null;
            this._isTyped = true;
            this._hashCode = TypeKey.hash(type, true);
        }

        public void resetUntyped(JavaType type) {
            this._type = type;
            this._class = null;
            this._isTyped = false;
            this._hashCode = TypeKey.hash(type, false);
        }

        public final int hashCode() {
            return this._hashCode;
        }

        public final String toString() {
            if (this._class != null) {
                return "{class: " + this._class.getName() + ", typed? " + this._isTyped + "}";
            }
            return "{type: " + this._type + ", typed? " + this._isTyped + "}";
        }

        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            TypeKey other = (TypeKey)o;
            if (other._isTyped == this._isTyped) {
                if (this._class != null) {
                    return other._class == this._class;
                }
                return this._type.equals(other._type);
            }
            return false;
        }
    }
}

