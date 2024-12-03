/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.JsonToken
 *  com.fasterxml.jackson.core.StreamReadCapability
 */
package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@JacksonStdImpl
final class UntypedObjectDeserializerNR
extends StdDeserializer<Object> {
    private static final long serialVersionUID = 1L;
    protected static final Object[] NO_OBJECTS = new Object[0];
    public static final UntypedObjectDeserializerNR std = new UntypedObjectDeserializerNR();
    protected final boolean _nonMerging;

    public UntypedObjectDeserializerNR() {
        this(false);
    }

    protected UntypedObjectDeserializerNR(boolean nonMerging) {
        super(Object.class);
        this._nonMerging = nonMerging;
    }

    public static UntypedObjectDeserializerNR instance(boolean nonMerging) {
        if (nonMerging) {
            return new UntypedObjectDeserializerNR(true);
        }
        return std;
    }

    @Override
    public LogicalType logicalType() {
        return LogicalType.Untyped;
    }

    @Override
    public Boolean supportsUpdate(DeserializationConfig config) {
        return this._nonMerging ? Boolean.FALSE : null;
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.currentTokenId()) {
            case 1: {
                return this._deserializeNR(p, ctxt, Scope.rootObjectScope(ctxt.isEnabled(StreamReadCapability.DUPLICATE_PROPERTIES)));
            }
            case 2: {
                return Scope.emptyMap();
            }
            case 5: {
                return this._deserializeObjectAtName(p, ctxt);
            }
            case 3: {
                return this._deserializeNR(p, ctxt, Scope.rootArrayScope());
            }
            case 6: {
                return p.getText();
            }
            case 7: {
                if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
                    return this._coerceIntegral(p, ctxt);
                }
                return p.getNumberValue();
            }
            case 8: {
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return p.getDecimalValue();
                }
                return p.getNumberValue();
            }
            case 9: {
                return Boolean.TRUE;
            }
            case 10: {
                return Boolean.FALSE;
            }
            case 11: {
                return null;
            }
            case 12: {
                return p.getEmbeddedObject();
            }
        }
        return ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
    }

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        switch (p.currentTokenId()) {
            case 1: 
            case 3: 
            case 5: {
                return typeDeserializer.deserializeTypedFromAny(p, ctxt);
            }
        }
        return this._deserializeAnyScalar(p, ctxt, p.currentTokenId());
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt, Object intoValue) throws IOException {
        if (this._nonMerging) {
            return this.deserialize(p, ctxt);
        }
        switch (p.currentTokenId()) {
            case 2: 
            case 4: {
                return intoValue;
            }
            case 1: {
                JsonToken t = p.nextToken();
                if (t == JsonToken.END_OBJECT) {
                    return intoValue;
                }
            }
            case 5: {
                if (!(intoValue instanceof Map)) break;
                Map m = (Map)intoValue;
                String key = p.currentName();
                do {
                    p.nextToken();
                    Object old = m.get(key);
                    Object newV = old != null ? this.deserialize(p, ctxt, old) : this.deserialize(p, ctxt);
                    if (newV == old) continue;
                    m.put(key, newV);
                } while ((key = p.nextFieldName()) != null);
                return intoValue;
            }
            case 3: {
                JsonToken t = p.nextToken();
                if (t == JsonToken.END_ARRAY) {
                    return intoValue;
                }
                if (!(intoValue instanceof Collection)) break;
                Collection c = (Collection)intoValue;
                do {
                    c.add(this.deserialize(p, ctxt));
                } while (p.nextToken() != JsonToken.END_ARRAY);
                return intoValue;
            }
        }
        return this.deserialize(p, ctxt);
    }

    private Object _deserializeObjectAtName(JsonParser p, DeserializationContext ctxt) throws IOException {
        Scope rootObject = Scope.rootObjectScope(ctxt.isEnabled(StreamReadCapability.DUPLICATE_PROPERTIES));
        String key = p.currentName();
        while (key != null) {
            Object value;
            JsonToken t = p.nextToken();
            if (t == null) {
                t = JsonToken.NOT_AVAILABLE;
            }
            switch (t.id()) {
                case 1: {
                    value = this._deserializeNR(p, ctxt, rootObject.childObject());
                    break;
                }
                case 2: {
                    return rootObject.finishRootObject();
                }
                case 3: {
                    value = this._deserializeNR(p, ctxt, rootObject.childArray());
                    break;
                }
                default: {
                    value = this._deserializeAnyScalar(p, ctxt, t.id());
                }
            }
            rootObject.putValue(key, value);
            key = p.nextFieldName();
        }
        return rootObject.finishRootObject();
    }

    private Object _deserializeNR(JsonParser p, DeserializationContext ctxt, Scope rootScope) throws IOException {
        boolean intCoercions = ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS);
        boolean useJavaArray = ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY);
        Scope currScope = rootScope;
        block23: while (true) {
            Object value;
            block30: {
                if (!currScope.isObject()) break block30;
                String propName = p.nextFieldName();
                while (propName != null) {
                    block31: {
                        JsonToken t = p.nextToken();
                        if (t == null) {
                            t = JsonToken.NOT_AVAILABLE;
                        }
                        switch (t.id()) {
                            case 1: {
                                currScope = currScope.childObject(propName);
                                break block31;
                            }
                            case 3: {
                                currScope = currScope.childArray(propName);
                                continue block23;
                            }
                            case 6: {
                                value = p.getText();
                                break;
                            }
                            case 7: {
                                value = intCoercions ? this._coerceIntegral(p, ctxt) : p.getNumberValue();
                                break;
                            }
                            case 8: {
                                value = ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS) ? p.getDecimalValue() : p.getNumberValue();
                                break;
                            }
                            case 9: {
                                value = Boolean.TRUE;
                                break;
                            }
                            case 10: {
                                value = Boolean.FALSE;
                                break;
                            }
                            case 11: {
                                value = null;
                                break;
                            }
                            case 12: {
                                value = p.getEmbeddedObject();
                                break;
                            }
                            default: {
                                return ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
                            }
                        }
                        currScope.putValue(propName, value);
                    }
                    propName = p.nextFieldName();
                }
                if (currScope == rootScope) {
                    return currScope.finishRootObject();
                }
                currScope = currScope.finishBranchObject();
                continue;
            }
            while (true) {
                JsonToken t;
                if ((t = p.nextToken()) == null) {
                    t = JsonToken.NOT_AVAILABLE;
                }
                switch (t.id()) {
                    case 1: {
                        currScope = currScope.childObject();
                        continue block23;
                    }
                    case 3: {
                        currScope = currScope.childArray();
                        continue block23;
                    }
                    case 4: {
                        if (currScope == rootScope) {
                            return currScope.finishRootArray(useJavaArray);
                        }
                        currScope = currScope.finishBranchArray(useJavaArray);
                        continue block23;
                    }
                    case 6: {
                        value = p.getText();
                        break;
                    }
                    case 7: {
                        value = intCoercions ? this._coerceIntegral(p, ctxt) : p.getNumberValue();
                        break;
                    }
                    case 8: {
                        value = ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS) ? p.getDecimalValue() : p.getNumberValue();
                        break;
                    }
                    case 9: {
                        value = Boolean.TRUE;
                        break;
                    }
                    case 10: {
                        value = Boolean.FALSE;
                        break;
                    }
                    case 11: {
                        value = null;
                        break;
                    }
                    case 12: {
                        value = p.getEmbeddedObject();
                        break;
                    }
                    default: {
                        return ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
                    }
                }
                currScope.addValue(value);
            }
            break;
        }
    }

    private Object _deserializeAnyScalar(JsonParser p, DeserializationContext ctxt, int tokenType) throws IOException {
        switch (tokenType) {
            case 6: {
                return p.getText();
            }
            case 7: {
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                    return p.getBigIntegerValue();
                }
                return p.getNumberValue();
            }
            case 8: {
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return p.getDecimalValue();
                }
                return p.getNumberValue();
            }
            case 9: {
                return Boolean.TRUE;
            }
            case 10: {
                return Boolean.FALSE;
            }
            case 12: {
                return p.getEmbeddedObject();
            }
            case 11: {
                return null;
            }
        }
        return ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
    }

    protected Object _mapObjectWithDups(JsonParser p, DeserializationContext ctxt, Map<String, Object> result, String initialKey, Object oldValue, Object newValue, String nextKey) throws IOException {
        boolean squashDups = ctxt.isEnabled(StreamReadCapability.DUPLICATE_PROPERTIES);
        if (squashDups) {
            this._squashDups(result, initialKey, oldValue, newValue);
        }
        while (nextKey != null) {
            p.nextToken();
            newValue = this.deserialize(p, ctxt);
            oldValue = result.put(nextKey, newValue);
            if (oldValue != null && squashDups) {
                this._squashDups(result, nextKey, oldValue, newValue);
            }
            nextKey = p.nextFieldName();
        }
        return result;
    }

    private void _squashDups(Map<String, Object> result, String key, Object oldValue, Object newValue) {
        if (oldValue instanceof List) {
            ((List)oldValue).add(newValue);
            result.put(key, oldValue);
        } else {
            ArrayList<Object> l = new ArrayList<Object>();
            l.add(oldValue);
            l.add(newValue);
            result.put(key, l);
        }
    }

    private static final class Scope {
        private final Scope _parent;
        private Scope _child;
        private boolean _isObject;
        private boolean _squashDups;
        private String _deferredKey;
        private Map<String, Object> _map;
        private List<Object> _list;

        private Scope(Scope p) {
            this._parent = p;
            this._isObject = false;
            this._squashDups = false;
        }

        private Scope(Scope p, boolean isObject, boolean squashDups) {
            this._parent = p;
            this._isObject = isObject;
            this._squashDups = squashDups;
        }

        public static Scope rootObjectScope(boolean squashDups) {
            return new Scope(null, true, squashDups);
        }

        public static Scope rootArrayScope() {
            return new Scope(null);
        }

        private Scope resetAsArray() {
            this._isObject = false;
            return this;
        }

        private Scope resetAsObject(boolean squashDups) {
            this._isObject = true;
            this._squashDups = squashDups;
            return this;
        }

        public Scope childObject() {
            if (this._child == null) {
                return new Scope(this, true, this._squashDups);
            }
            return this._child.resetAsObject(this._squashDups);
        }

        public Scope childObject(String deferredKey) {
            this._deferredKey = deferredKey;
            if (this._child == null) {
                return new Scope(this, true, this._squashDups);
            }
            return this._child.resetAsObject(this._squashDups);
        }

        public Scope childArray() {
            if (this._child == null) {
                return new Scope(this);
            }
            return this._child.resetAsArray();
        }

        public Scope childArray(String deferredKey) {
            this._deferredKey = deferredKey;
            if (this._child == null) {
                return new Scope(this);
            }
            return this._child.resetAsArray();
        }

        public boolean isObject() {
            return this._isObject;
        }

        public void putValue(String key, Object value) {
            if (this._squashDups) {
                this._putValueHandleDups(key, value);
                return;
            }
            if (this._map == null) {
                this._map = new LinkedHashMap<String, Object>();
            }
            this._map.put(key, value);
        }

        public Scope putDeferredValue(Object value) {
            String key = Objects.requireNonNull(this._deferredKey);
            this._deferredKey = null;
            if (this._squashDups) {
                this._putValueHandleDups(key, value);
                return this;
            }
            if (this._map == null) {
                this._map = new LinkedHashMap<String, Object>();
            }
            this._map.put(key, value);
            return this;
        }

        public void addValue(Object value) {
            if (this._list == null) {
                this._list = new ArrayList<Object>();
            }
            this._list.add(value);
        }

        public Object finishRootObject() {
            if (this._map == null) {
                return Scope.emptyMap();
            }
            return this._map;
        }

        public Scope finishBranchObject() {
            Map<String, Object> value;
            if (this._map == null) {
                value = new LinkedHashMap<String, Object>();
            } else {
                value = this._map;
                this._map = null;
            }
            if (this._parent.isObject()) {
                return this._parent.putDeferredValue(value);
            }
            this._parent.addValue(value);
            return this._parent;
        }

        public Object finishRootArray(boolean asJavaArray) {
            if (this._list == null) {
                if (asJavaArray) {
                    return NO_OBJECTS;
                }
                return Scope.emptyList();
            }
            if (asJavaArray) {
                return this._list.toArray(NO_OBJECTS);
            }
            return this._list;
        }

        public Scope finishBranchArray(boolean asJavaArray) {
            Object value;
            if (this._list == null) {
                value = asJavaArray ? NO_OBJECTS : Scope.emptyList();
            } else {
                value = asJavaArray ? this._list.toArray(NO_OBJECTS) : this._list;
                this._list = null;
            }
            if (this._parent.isObject()) {
                return this._parent.putDeferredValue(value);
            }
            this._parent.addValue(value);
            return this._parent;
        }

        private void _putValueHandleDups(String key, Object newValue) {
            if (this._map == null) {
                this._map = new LinkedHashMap<String, Object>();
                this._map.put(key, newValue);
                return;
            }
            Object old = this._map.put(key, newValue);
            if (old != null) {
                if (old instanceof List) {
                    ((List)old).add(newValue);
                    this._map.put(key, old);
                } else {
                    ArrayList<Object> l = new ArrayList<Object>();
                    l.add(old);
                    l.add(newValue);
                    this._map.put(key, l);
                }
            }
        }

        public static Map<String, Object> emptyMap() {
            return new LinkedHashMap<String, Object>(2);
        }

        public static List<Object> emptyList() {
            return new ArrayList<Object>(2);
        }
    }
}

