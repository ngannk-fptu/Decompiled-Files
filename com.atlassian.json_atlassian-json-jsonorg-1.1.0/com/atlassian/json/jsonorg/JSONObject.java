/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.json.jsonorg;

import com.atlassian.annotations.PublicApi;
import com.atlassian.json.jsonorg.JSONArray;
import com.atlassian.json.jsonorg.JSONElement;
import com.atlassian.json.jsonorg.JSONException;
import com.atlassian.json.jsonorg.JSONString;
import com.atlassian.json.jsonorg.JSONTokener;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@PublicApi
public class JSONObject
extends JSONElement
implements Iterable<String> {
    private final Map<String, Object> map = new LinkedHashMap<String, Object>();
    public static final Null NULL = Null.access$000();

    public JSONObject() {
    }

    public JSONObject(JSONObject jo, String[] names) throws JSONException {
        for (String name : names) {
            this.putOpt(name, jo.opt(name));
        }
    }

    public JSONObject(JSONTokener x) throws JSONException {
        if (x.nextClean() != '{') {
            x.syntaxError("A JSONObject text must begin with '{'");
        }
        block8: while (true) {
            char c = x.nextClean();
            switch (c) {
                case '\u0000': {
                    x.syntaxError("A JSONObject text must end with '}'");
                    return;
                }
                case '}': {
                    return;
                }
            }
            x.back();
            String key = x.nextValue().toString();
            c = x.nextClean();
            if (c == '=') {
                if (x.next() != '>') {
                    x.back();
                }
            } else if (c != ':') {
                x.syntaxError("Expected a ':' after a key");
            }
            this.put(key, x.nextValue());
            switch (x.nextClean()) {
                case ',': 
                case ';': {
                    if (x.nextClean() == '}') {
                        return;
                    }
                    x.back();
                    continue block8;
                }
                case '}': {
                    return;
                }
            }
            x.syntaxError("Expected a ',' or '}'");
        }
    }

    public JSONObject(Map<String, Object> map) {
        if (map != null) {
            this.map.putAll(map);
        }
    }

    public JSONObject(Object bean) {
        Method[] methods;
        Class<?> klass = bean.getClass();
        for (Method method : methods = klass.getMethods()) {
            try {
                String name = method.getName();
                String key = "";
                if (name.startsWith("get")) {
                    key = name.substring(3);
                } else if (name.startsWith("is")) {
                    key = name.substring(2);
                }
                if (key.length() <= 0 || !Character.isUpperCase(key.charAt(0)) || method.getParameterTypes().length != 0) continue;
                if (key.length() == 1) {
                    key = key.toLowerCase();
                } else if (!Character.isUpperCase(key.charAt(1))) {
                    key = key.substring(0, 1).toLowerCase() + key.substring(1);
                }
                this.put(key, method.invoke(bean, new Object[0]));
            }
            catch (JSONException jSONException) {
            }
            catch (RuntimeException runtimeException) {
            }
            catch (InvocationTargetException invocationTargetException) {
            }
            catch (IllegalAccessException illegalAccessException) {
                // empty catch block
            }
        }
    }

    public JSONObject(Object object, String[] names) {
        Class<?> c = object.getClass();
        for (String name : names) {
            try {
                Field field = c.getField(name);
                Object value = field.get(object);
                this.put(name, value);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public JSONObject(String source) throws JSONException {
        this(new JSONTokener(source));
    }

    public JSONObject accumulate(String key, Object value) throws JSONException {
        JSONObject.testValidity(value);
        Object o = this.opt(key);
        if (o == null) {
            this.put(key, value instanceof JSONArray ? new JSONArray().put(value) : value);
        } else if (o instanceof JSONArray) {
            ((JSONArray)o).put(value);
        } else {
            this.put(key, new JSONArray().put(o).put(value));
        }
        return this;
    }

    public JSONObject append(String key, Object value) throws JSONException {
        JSONObject.testValidity(value);
        Object o = this.opt(key);
        if (o == null) {
            this.put(key, new JSONArray().put(value));
        } else if (o instanceof JSONArray) {
            this.put(key, ((JSONArray)o).put(value));
        } else {
            throw new JSONException("JSONObject[" + key + "] is not a JSONArray.");
        }
        return this;
    }

    public static String doubleToString(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            return "null";
        }
        String s = Double.toString(d);
        if (s.indexOf(46) > 0 && s.indexOf(101) < 0 && s.indexOf(69) < 0) {
            while (s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.endsWith(".")) {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }

    public Object get(String key) throws JSONException {
        Object o = this.opt(key);
        if (o == null) {
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] not found.");
        }
        return o;
    }

    public boolean getBoolean(String key) throws JSONException {
        Object o = this.get(key);
        if (o.equals(Boolean.FALSE) || o instanceof String && ((String)o).equalsIgnoreCase("false")) {
            return false;
        }
        if (o.equals(Boolean.TRUE) || o instanceof String && ((String)o).equalsIgnoreCase("true")) {
            return true;
        }
        throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a Boolean.");
    }

    public double getDouble(String key) throws JSONException {
        Object o = this.get(key);
        try {
            return o instanceof Number ? ((Number)o).doubleValue() : Double.valueOf((String)o).doubleValue();
        }
        catch (Exception e) {
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a number.");
        }
    }

    public int getInt(String key) throws JSONException {
        Object o = this.get(key);
        return o instanceof Number ? ((Number)o).intValue() : (int)this.getDouble(key);
    }

    public JSONArray getJSONArray(String key) throws JSONException {
        Object o = this.get(key);
        if (o instanceof JSONArray) {
            return (JSONArray)o;
        }
        throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a JSONArray.");
    }

    public JSONObject getJSONObject(String key) throws JSONException {
        Object o = this.get(key);
        if (o instanceof JSONObject) {
            return (JSONObject)o;
        }
        throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a JSONObject.");
    }

    public long getLong(String key) throws JSONException {
        Object o = this.get(key);
        return o instanceof Number ? ((Number)o).longValue() : (long)this.getDouble(key);
    }

    public static String[] getNames(JSONObject jo) {
        int length = jo.length();
        if (length == 0) {
            return null;
        }
        String[] names = new String[length];
        int j = 0;
        for (String name : jo.keySet()) {
            names[j++] = name;
        }
        return names;
    }

    public static String[] getNames(Object object) {
        if (object == null) {
            return null;
        }
        Class<?> klass = object.getClass();
        Field[] fields = klass.getFields();
        int length = fields.length;
        if (length == 0) {
            return null;
        }
        String[] names = new String[length];
        for (int i = 0; i < length; ++i) {
            names[i] = fields[i].getName();
        }
        return names;
    }

    public String getString(String key) throws JSONException {
        return this.get(key).toString();
    }

    public boolean has(String key) {
        return this.map.containsKey(key);
    }

    public boolean isNull(String key) {
        return NULL.isNull(this.opt(key));
    }

    public Iterator<String> keys() {
        return this.keySet().iterator();
    }

    Set<String> keySet() {
        return this.map.keySet();
    }

    public int length() {
        return this.map.size();
    }

    public JSONArray names() {
        JSONArray ja = new JSONArray();
        for (String key : this.keySet()) {
            ja.put(key);
        }
        return ja.length() == 0 ? null : ja;
    }

    public static String numberToString(Number n) throws JSONException {
        if (n == null) {
            throw new JSONException("Null pointer");
        }
        JSONObject.testValidity(n);
        return n.toString();
    }

    public Object opt(String key) {
        return key == null ? null : this.map.get(key);
    }

    public boolean optBoolean(String key) {
        return this.optBoolean(key, false);
    }

    public boolean optBoolean(String key, boolean defaultValue) {
        try {
            return this.getBoolean(key);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public JSONObject put(String key, Collection<Object> values) throws JSONException {
        this.put(key, new JSONArray(values));
        return this;
    }

    public double optDouble(String key) {
        return this.optDouble(key, Double.NaN);
    }

    public double optDouble(String key, double defaultValue) {
        try {
            Object o = this.opt(key);
            return o instanceof Number ? ((Number)o).doubleValue() : Double.parseDouble((String)o);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public int optInt(String key) {
        return this.optInt(key, 0);
    }

    public int optInt(String key, int defaultValue) {
        try {
            return this.getInt(key);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public JSONArray optJSONArray(String key) {
        Object o = this.opt(key);
        return o instanceof JSONArray ? (JSONArray)o : null;
    }

    public JSONObject optJSONObject(String key) {
        Object o = this.opt(key);
        return o instanceof JSONObject ? (JSONObject)o : null;
    }

    public long optLong(String key) {
        return this.optLong(key, 0L);
    }

    public long optLong(String key, long defaultValue) {
        try {
            return this.getLong(key);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public String optString(String key) {
        return this.optString(key, "");
    }

    public String optString(String key, String defaultValue) {
        Object o = this.opt(key);
        return o != null ? o.toString() : defaultValue;
    }

    public JSONObject put(String key, boolean value) throws JSONException {
        this.put(key, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    public JSONObject put(String key, double value) throws JSONException {
        this.put(key, new Double(value));
        return this;
    }

    public JSONObject put(String key, int value) throws JSONException {
        this.put(key, new Integer(value));
        return this;
    }

    public JSONObject put(String key, long value) throws JSONException {
        this.put(key, new Long(value));
        return this;
    }

    public JSONObject put(String key, Map<String, Object> value) throws JSONException {
        this.put(key, new JSONObject(value));
        return this;
    }

    public JSONObject put(String key, Object value) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        if (value != null) {
            JSONObject.testValidity(value);
            this.map.put(key, value);
        } else {
            this.remove(key);
        }
        return this;
    }

    public JSONObject putOpt(String key, Object value) throws JSONException {
        if (key != null && value != null) {
            this.put(key, value);
        }
        return this;
    }

    public static String quote(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }
        char c = '\u0000';
        int len = string.length();
        StringBuilder sb = new StringBuilder(len + 4);
        sb.append('\"');
        char[] chars = new char[len];
        string.getChars(0, len, chars, 0);
        block9: for (int i = 0; i < len; ++i) {
            char b = c;
            c = chars[i];
            switch (c) {
                case '\"': 
                case '\\': {
                    sb.append('\\');
                    sb.append(c);
                    continue block9;
                }
                case '/': {
                    if (b == '<') {
                        sb.append('\\');
                    }
                    sb.append(c);
                    continue block9;
                }
                case '\b': {
                    sb.append("\\b");
                    continue block9;
                }
                case '\t': {
                    sb.append("\\t");
                    continue block9;
                }
                case '\n': {
                    sb.append("\\n");
                    continue block9;
                }
                case '\f': {
                    sb.append("\\f");
                    continue block9;
                }
                case '\r': {
                    sb.append("\\r");
                    continue block9;
                }
                default: {
                    if (c < ' ' || c >= '\u0080' && c < '\u00a0' || c >= '\u2000' && c < '\u2100') {
                        String t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                        continue block9;
                    }
                    sb.append(c);
                }
            }
        }
        sb.append('\"');
        return sb.toString();
    }

    public Object remove(String key) {
        return this.map.remove(key);
    }

    @Override
    public Iterator<String> iterator() {
        return this.map.keySet().iterator();
    }

    static void testValidity(Object o) throws JSONException {
        if (o != null && (o instanceof Double ? ((Double)o).isInfinite() || ((Double)o).isNaN() : o instanceof Float && (((Float)o).isInfinite() || ((Float)o).isNaN()))) {
            throw new JSONException("JSON does not allow non-finite numbers.");
        }
    }

    public JSONArray toJSONArray(JSONArray names) throws JSONException {
        if (names == null || names.length() == 0) {
            return null;
        }
        JSONArray ja = new JSONArray();
        for (int i = 0; i < names.length(); ++i) {
            ja.put(this.opt(names.getString(i)));
        }
        return ja;
    }

    public String toString() {
        try {
            StringBuilder sb = new StringBuilder("{");
            for (String o : this.keySet()) {
                if (sb.length() > 1) {
                    sb.append(',');
                }
                sb.append(JSONObject.quote(o));
                sb.append(':');
                sb.append(JSONObject.valueToString(this.map.get(o)));
            }
            sb.append('}');
            return sb.toString();
        }
        catch (Exception e) {
            return "";
        }
    }

    public String toString(int indentFactor) throws JSONException {
        return this.toString(indentFactor, 0);
    }

    String toString(int indentFactor, int indent) throws JSONException {
        int n = this.length();
        if (n == 0) {
            return "{}";
        }
        Iterator<String> keys = this.keys();
        StringBuilder sb = new StringBuilder("{");
        int newindent = indent + indentFactor;
        if (n == 1) {
            String o = keys.next();
            sb.append(JSONObject.quote(o.toString()));
            sb.append(": ");
            sb.append(JSONObject.valueToString(this.map.get(o), indentFactor, indent));
        } else {
            int i;
            while (keys.hasNext()) {
                String o = keys.next();
                if (sb.length() > 1) {
                    sb.append(",\n");
                } else {
                    sb.append('\n');
                }
                for (i = 0; i < newindent; ++i) {
                    sb.append(' ');
                }
                sb.append(JSONObject.quote(o.toString()));
                sb.append(": ");
                sb.append(JSONObject.valueToString(this.map.get(o), indentFactor, newindent));
            }
            if (sb.length() > 1) {
                sb.append('\n');
                for (i = 0; i < indent; ++i) {
                    sb.append(' ');
                }
            }
        }
        sb.append('}');
        return sb.toString();
    }

    static String valueToString(Object value) throws JSONException {
        if (NULL.isNull(value)) {
            return "null";
        }
        if (value instanceof JSONString) {
            String o;
            try {
                o = ((JSONString)value).toJSONString();
            }
            catch (Exception e) {
                throw new JSONException(e);
            }
            if (o instanceof String) {
                return o;
            }
            throw new JSONException("Bad value from toJSONString: " + o);
        }
        if (value instanceof Number) {
            return JSONObject.numberToString((Number)value);
        }
        if (value instanceof Boolean || value instanceof JSONObject || value instanceof JSONArray) {
            return value.toString();
        }
        if (value instanceof Map) {
            JSONObject result = new JSONObject((Map)Map.class.cast(value));
            return result.toString();
        }
        if (value instanceof Collection) {
            JSONArray result = new JSONArray((Collection)Collection.class.cast(value));
            return result.toString();
        }
        if (value.getClass().isArray()) {
            return new JSONArray(value).toString();
        }
        return JSONObject.quote(value.toString());
    }

    static String valueToString(Object value, int indentFactor, int indent) throws JSONException {
        JSONElement result;
        if (value == null || value instanceof Null) {
            return "null";
        }
        try {
            String o;
            if (value instanceof JSONString && (o = ((JSONString)value).toJSONString()) instanceof String) {
                return o;
            }
        }
        catch (Exception o) {
            // empty catch block
        }
        if (value instanceof Number) {
            return JSONObject.numberToString((Number)value);
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof JSONObject) {
            return ((JSONObject)value).toString(indentFactor, indent);
        }
        if (value instanceof JSONArray) {
            return ((JSONArray)value).toString(indentFactor, indent);
        }
        if (value instanceof Map) {
            result = new JSONObject((Map)Map.class.cast(value));
            return ((JSONObject)result).toString(indentFactor, indent);
        }
        if (value instanceof Collection) {
            result = new JSONArray((Collection)Collection.class.cast(value));
            return ((JSONArray)result).toString(indentFactor, indent);
        }
        if (value.getClass().isArray()) {
            return new JSONArray(value).toString(indentFactor, indent);
        }
        return JSONObject.quote(value.toString());
    }

    @Override
    public Writer write(Writer writer) throws JSONException {
        try {
            boolean b = false;
            Iterator<String> keys = this.keys();
            writer.write(123);
            while (keys.hasNext()) {
                if (b) {
                    writer.write(44);
                }
                String k = keys.next();
                writer.write(JSONObject.quote(k));
                writer.write(58);
                Object v = this.map.get(k);
                if (v instanceof JSONObject) {
                    ((JSONObject)v).write(writer);
                } else if (v instanceof JSONArray) {
                    ((JSONArray)v).write(writer);
                } else {
                    writer.write(JSONObject.valueToString(v));
                }
                b = true;
            }
            writer.write(125);
            return writer;
        }
        catch (IOException e) {
            throw new JSONException(e);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JSONObject that = (JSONObject)o;
        return this.map.equals(that.map);
    }

    public int hashCode() {
        return this.map.hashCode();
    }

    @Override
    public boolean isJSONObject() {
        return true;
    }

    @Override
    public boolean isJSONArray() {
        return false;
    }

    @Override
    public JSONObject getAsJSONObject() {
        return this;
    }

    @Override
    public JSONArray getAsJSONArray() {
        throw new IllegalStateException("I am a JSONObject");
    }

    public static final class Null {
        private static final Null INSTANCE = new Null();

        Null() {
        }

        boolean isNull(Object value) {
            return value == null || this.equals(value);
        }

        public boolean equals(Object o) {
            return this == o || o != null && this.getClass() == o.getClass();
        }

        public int hashCode() {
            return 0;
        }

        public String toString() {
            return "null";
        }

        static /* synthetic */ Null access$000() {
            return INSTANCE;
        }
    }
}

