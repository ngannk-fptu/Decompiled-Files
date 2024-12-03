/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONAwareEx;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;
import net.minidev.json.writer.JsonReaderI;

public class JSONNavi<T> {
    private JsonReaderI<? super T> mapper;
    private T root;
    private Stack<Object> stack = new Stack();
    private Stack<Object> path = new Stack();
    private Object current;
    private boolean failure = false;
    private String failureMessage;
    private boolean readonly = false;
    private Object missingKey = null;
    private static final JSONStyle ERROR_COMPRESS = new JSONStyle(2);

    public static JSONNavi<JSONAwareEx> newInstance() {
        return new JSONNavi<JSONAwareEx>(JSONValue.defaultReader.DEFAULT_ORDERED);
    }

    public static JSONNavi<JSONObject> newInstanceObject() {
        JSONNavi<JSONObject> o = new JSONNavi<JSONObject>(JSONValue.defaultReader.getMapper(JSONObject.class));
        o.object();
        return o;
    }

    public static JSONNavi<JSONArray> newInstanceArray() {
        JSONNavi<JSONArray> o = new JSONNavi<JSONArray>(JSONValue.defaultReader.getMapper(JSONArray.class));
        o.array();
        return o;
    }

    public JSONNavi(JsonReaderI<? super T> mapper) {
        this.mapper = mapper;
    }

    public JSONNavi(String json) {
        this.root = JSONValue.parse(json);
        this.current = this.root;
        this.readonly = true;
    }

    public JSONNavi(String json, JsonReaderI<T> mapper) {
        this.root = JSONValue.parse(json, mapper);
        this.mapper = mapper;
        this.current = this.root;
        this.readonly = true;
    }

    public JSONNavi(String json, Class<T> mapTo) {
        this.root = JSONValue.parse(json, mapTo);
        this.mapper = JSONValue.defaultReader.getMapper(mapTo);
        this.current = this.root;
        this.readonly = true;
    }

    public JSONNavi<T> root() {
        this.current = this.root;
        this.stack.clear();
        this.path.clear();
        this.failure = false;
        this.missingKey = null;
        this.failureMessage = null;
        return this;
    }

    public boolean hasFailure() {
        return this.failure;
    }

    public Object getCurrentObject() {
        return this.current;
    }

    public Collection<String> getKeys() {
        if (this.current instanceof Map) {
            return ((Map)this.current).keySet();
        }
        return null;
    }

    public int getSize() {
        if (this.current == null) {
            return 0;
        }
        if (this.isArray()) {
            return ((List)this.current).size();
        }
        if (this.isObject()) {
            return ((Map)this.current).size();
        }
        return 1;
    }

    public String getString(String key) {
        String v = null;
        if (!this.hasKey(key)) {
            return v;
        }
        this.at(key);
        v = this.asString();
        this.up();
        return v;
    }

    public int getInt(String key) {
        int v = 0;
        if (!this.hasKey(key)) {
            return v;
        }
        this.at(key);
        v = this.asInt();
        this.up();
        return v;
    }

    public Integer getInteger(String key) {
        Integer v = null;
        if (!this.hasKey(key)) {
            return v;
        }
        this.at(key);
        v = this.asIntegerObj();
        this.up();
        return v;
    }

    public double getDouble(String key) {
        double v = 0.0;
        if (!this.hasKey(key)) {
            return v;
        }
        this.at(key);
        v = this.asDouble();
        this.up();
        return v;
    }

    public boolean hasKey(String key) {
        if (!this.isObject()) {
            return false;
        }
        return this.o(this.current).containsKey(key);
    }

    public JSONNavi<?> at(String key) {
        if (this.failure) {
            return this;
        }
        if (!this.isObject()) {
            this.object();
        }
        if (!(this.current instanceof Map)) {
            return this.failure("current node is not an Object", key);
        }
        if (!this.o(this.current).containsKey(key)) {
            if (this.readonly) {
                return this.failure("current Object have no key named " + key, key);
            }
            this.stack.add(this.current);
            this.path.add(key);
            this.current = null;
            this.missingKey = key;
            return this;
        }
        Object next = this.o(this.current).get(key);
        this.stack.add(this.current);
        this.path.add(key);
        this.current = next;
        return this;
    }

    public Object get(String key) {
        if (this.failure) {
            return this;
        }
        if (!this.isObject()) {
            this.object();
        }
        if (!(this.current instanceof Map)) {
            return this.failure("current node is not an Object", key);
        }
        return this.o(this.current).get(key);
    }

    public Object get(int index) {
        if (this.failure) {
            return this;
        }
        if (!this.isArray()) {
            this.array();
        }
        if (!(this.current instanceof List)) {
            return this.failure("current node is not an List", index);
        }
        return this.a(this.current).get(index);
    }

    public JSONNavi<T> set(String key, String value) {
        this.object();
        if (this.failure) {
            return this;
        }
        this.o(this.current).put(key, value);
        return this;
    }

    public JSONNavi<T> set(String key, Number value) {
        this.object();
        if (this.failure) {
            return this;
        }
        this.o(this.current).put(key, value);
        return this;
    }

    public JSONNavi<T> set(String key, long value) {
        return this.set(key, (Number)value);
    }

    public JSONNavi<T> set(String key, int value) {
        return this.set(key, (Number)value);
    }

    public JSONNavi<T> set(String key, double value) {
        return this.set(key, (Number)value);
    }

    public JSONNavi<T> set(String key, float value) {
        return this.set(key, Float.valueOf(value));
    }

    public JSONNavi<T> add(Object ... values) {
        this.array();
        if (this.failure) {
            return this;
        }
        List<Object> list = this.a(this.current);
        for (Object o : values) {
            list.add(o);
        }
        return this;
    }

    public String asString() {
        if (this.current == null) {
            return null;
        }
        if (this.current instanceof String) {
            return (String)this.current;
        }
        return this.current.toString();
    }

    public double asDouble() {
        if (this.current instanceof Number) {
            return ((Number)this.current).doubleValue();
        }
        return Double.NaN;
    }

    public Double asDoubleObj() {
        if (this.current == null) {
            return null;
        }
        if (this.current instanceof Number) {
            if (this.current instanceof Double) {
                return (Double)this.current;
            }
            return ((Number)this.current).doubleValue();
        }
        return Double.NaN;
    }

    public float asFloat() {
        if (this.current instanceof Number) {
            return ((Number)this.current).floatValue();
        }
        return Float.NaN;
    }

    public Float asFloatObj() {
        if (this.current == null) {
            return null;
        }
        if (this.current instanceof Number) {
            if (this.current instanceof Float) {
                return (Float)this.current;
            }
            return Float.valueOf(((Number)this.current).floatValue());
        }
        return Float.valueOf(Float.NaN);
    }

    public int asInt() {
        if (this.current instanceof Number) {
            return ((Number)this.current).intValue();
        }
        return 0;
    }

    public Integer asIntegerObj() {
        if (this.current == null) {
            return null;
        }
        if (this.current instanceof Number) {
            Long l;
            if (this.current instanceof Integer) {
                return (Integer)this.current;
            }
            if (this.current instanceof Long && (l = (Long)this.current) == (long)l.intValue()) {
                return l.intValue();
            }
            return null;
        }
        return null;
    }

    public long asLong() {
        if (this.current instanceof Number) {
            return ((Number)this.current).longValue();
        }
        return 0L;
    }

    public Long asLongObj() {
        if (this.current == null) {
            return null;
        }
        if (this.current instanceof Number) {
            if (this.current instanceof Long) {
                return (Long)this.current;
            }
            if (this.current instanceof Integer) {
                return ((Number)this.current).longValue();
            }
            return null;
        }
        return null;
    }

    public boolean asBoolean() {
        if (this.current instanceof Boolean) {
            return (Boolean)this.current;
        }
        return false;
    }

    public Boolean asBooleanObj() {
        if (this.current == null) {
            return null;
        }
        if (this.current instanceof Boolean) {
            return (Boolean)this.current;
        }
        return null;
    }

    public JSONNavi<T> object() {
        if (this.failure) {
            return this;
        }
        if (this.current == null && this.readonly) {
            this.failure("Can not create Object child in readonly", null);
        }
        if (this.current != null) {
            if (this.isObject()) {
                return this;
            }
            if (this.isArray()) {
                this.failure("can not use Object feature on Array.", null);
            }
            this.failure("Can not use current position as Object", null);
        } else {
            this.current = this.mapper.createObject();
        }
        if (this.root == null) {
            this.root = this.current;
        } else {
            this.store();
        }
        return this;
    }

    public JSONNavi<T> array() {
        if (this.failure) {
            return this;
        }
        if (this.current == null && this.readonly) {
            this.failure("Can not create Array child in readonly", null);
        }
        if (this.current != null) {
            if (this.isArray()) {
                return this;
            }
            if (this.isObject()) {
                this.failure("can not use Object feature on Array.", null);
            }
            this.failure("Can not use current position as Object", null);
        } else {
            this.current = this.mapper.createArray();
        }
        if (this.root == null) {
            this.root = this.current;
        } else {
            this.store();
        }
        return this;
    }

    public JSONNavi<T> set(Number num) {
        if (this.failure) {
            return this;
        }
        this.current = num;
        this.store();
        return this;
    }

    public JSONNavi<T> set(Boolean bool) {
        if (this.failure) {
            return this;
        }
        this.current = bool;
        this.store();
        return this;
    }

    public JSONNavi<T> set(String text) {
        if (this.failure) {
            return this;
        }
        this.current = text;
        this.store();
        return this;
    }

    public T getRoot() {
        return this.root;
    }

    private void store() {
        Object parent = this.stack.peek();
        if (this.isObject(parent)) {
            this.o(parent).put((String)this.missingKey, this.current);
        } else if (this.isArray(parent)) {
            int index = ((Number)this.missingKey).intValue();
            List<Object> lst = this.a(parent);
            while (lst.size() <= index) {
                lst.add(null);
            }
            lst.set(index, this.current);
        }
    }

    public boolean isArray() {
        return this.isArray(this.current);
    }

    public boolean isObject() {
        return this.isObject(this.current);
    }

    private boolean isArray(Object obj) {
        if (obj == null) {
            return false;
        }
        return obj instanceof List;
    }

    private boolean isObject(Object obj) {
        if (obj == null) {
            return false;
        }
        return obj instanceof Map;
    }

    private List<Object> a(Object obj) {
        return (List)obj;
    }

    private Map<String, Object> o(Object obj) {
        return (Map)obj;
    }

    public JSONNavi<?> at(int index) {
        if (this.failure) {
            return this;
        }
        if (!(this.current instanceof List)) {
            return this.failure("current node is not an Array", index);
        }
        List lst = (List)this.current;
        if (index < 0 && (index = lst.size() + index) < 0) {
            index = 0;
        }
        if (index >= lst.size()) {
            if (this.readonly) {
                return this.failure("Out of bound exception for index", index);
            }
            this.stack.add(this.current);
            this.path.add(index);
            this.current = null;
            this.missingKey = index;
            return this;
        }
        Object next = lst.get(index);
        this.stack.add(this.current);
        this.path.add(index);
        this.current = next;
        return this;
    }

    public JSONNavi<?> atNext() {
        if (this.failure) {
            return this;
        }
        if (!(this.current instanceof List)) {
            return this.failure("current node is not an Array", null);
        }
        List lst = (List)this.current;
        return this.at(lst.size());
    }

    public JSONNavi<?> up(int level) {
        while (level-- > 0 && this.stack.size() > 0) {
            this.current = this.stack.pop();
            this.path.pop();
        }
        return this;
    }

    public JSONNavi<?> up() {
        if (this.stack.size() > 0) {
            this.current = this.stack.pop();
            this.path.pop();
        }
        return this;
    }

    public String toString() {
        if (this.failure) {
            return JSONValue.toJSONString(this.failureMessage, ERROR_COMPRESS);
        }
        return JSONValue.toJSONString(this.root);
    }

    public String toString(JSONStyle compression) {
        if (this.failure) {
            return JSONValue.toJSONString(this.failureMessage, compression);
        }
        return JSONValue.toJSONString(this.root, compression);
    }

    private JSONNavi<?> failure(String err, Object jPathPostfix) {
        this.failure = true;
        StringBuilder sb = new StringBuilder();
        sb.append("Error: ");
        sb.append(err);
        sb.append(" at ");
        sb.append(this.getJPath());
        if (jPathPostfix != null) {
            if (jPathPostfix instanceof Integer) {
                sb.append('[').append(jPathPostfix).append(']');
            } else {
                sb.append('/').append(jPathPostfix);
            }
        }
        this.failureMessage = sb.toString();
        return this;
    }

    public String getJPath() {
        StringBuilder sb = new StringBuilder();
        for (Object e : this.path) {
            if (e instanceof String) {
                sb.append('/').append(e.toString());
                continue;
            }
            sb.append('[').append(e.toString()).append(']');
        }
        return sb.toString();
    }
}

