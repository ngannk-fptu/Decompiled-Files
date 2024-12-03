/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.json;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jettison.JSONSequenceTooLargeException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONString;
import org.codehaus.jettison.json.JSONTokener;

public class JSONObject
implements Serializable {
    static final int DEFAULT_RECURSION_DEPTH_LIMIT = 500;
    static int RECURSION_DEPTH_LIMIT = 500;
    private LinkedHashMap<Object, Object> myHashMap;
    private boolean dropRootElement;
    private List ignoredElements;
    private boolean writeNullAsString = true;
    private boolean escapeForwardSlashAlways = true;
    public static final Object NULL = new Null();
    public static final Object EXPLICIT_NULL = new Null(true);

    public JSONObject() {
        this(false, null, true, true);
    }

    public JSONObject(List ignoredElements) {
        this(false, ignoredElements, true, true);
    }

    public JSONObject(boolean dropRootElement, List ignoredElements, boolean writeNullAsString, boolean escapeForwardSlash) {
        this.myHashMap = new LinkedHashMap();
        this.dropRootElement = dropRootElement;
        this.ignoredElements = ignoredElements;
        this.writeNullAsString = writeNullAsString;
        this.escapeForwardSlashAlways = escapeForwardSlash;
    }

    public JSONObject(JSONObject jo, String[] sa) throws JSONException {
        this();
        for (int i = 0; i < sa.length; ++i) {
            this.putOpt(sa[i], jo.opt(sa[i]));
        }
    }

    public JSONObject(JSONTokener x) throws JSONException {
        this();
        if (x.nextClean() != '{') {
            throw x.syntaxError("A JSONObject text must begin with '{'");
        }
        block9: while (true) {
            char c = x.nextClean();
            switch (c) {
                case '\u0000': {
                    throw x.syntaxError("A JSONObject text must end with '}'");
                }
                case '}': {
                    return;
                }
                case '{': {
                    throw x.syntaxError("Expected a key");
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
                throw x.syntaxError("Expected a ':' after a key");
            }
            this.doPut(key, x.nextValue(), x.getThreshold(), true);
            switch (x.nextClean()) {
                case ',': 
                case ';': {
                    if (x.nextClean() == '}') {
                        return;
                    }
                    x.back();
                    continue block9;
                }
                case '}': {
                    return;
                }
            }
            break;
        }
        throw x.syntaxError("Expected a ',' or '}'");
    }

    public JSONObject(Map map) throws JSONException {
        this(map, 0);
    }

    private JSONObject(Map map, int recursionDepth) throws JSONException {
        if (recursionDepth > RECURSION_DEPTH_LIMIT) {
            throw new JSONException("JSONObject has reached recursion depth limit of " + RECURSION_DEPTH_LIMIT);
        }
        this.myHashMap = map == null ? new LinkedHashMap() : new LinkedHashMap(map);
        for (Map.Entry<Object, Object> entry : this.myHashMap.entrySet()) {
            Object v = entry.getValue();
            if (v instanceof Collection) {
                this.myHashMap.put(entry.getKey(), new JSONArray((Collection)v));
            }
            if (!(v instanceof Map) || v == map) continue;
            this.myHashMap.put(entry.getKey(), new JSONObject((Map)v, recursionDepth + 1));
        }
    }

    public JSONObject(Object object, String[] names) {
        this();
        Class<?> c = object.getClass();
        for (int i = 0; i < names.length; ++i) {
            try {
                String name = names[i];
                Field field = c.getField(name);
                Object value = field.get(object);
                this.put(name, value);
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public JSONObject(String string) throws JSONException {
        this(new JSONTokener(string));
    }

    public JSONObject accumulate(String key, Object value) throws JSONException {
        JSONObject.testValidity(value);
        Object o = this.opt(key);
        if (o == null) {
            this.put(key, value);
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
        } else {
            if (!(o instanceof JSONArray)) {
                throw new JSONException("JSONObject[" + key + "] is not a JSONArray.");
            }
            ((JSONArray)o).put(value);
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
        return this.doGetBoolean(key, this.get(key));
    }

    private boolean doGetBoolean(String key, Object o) throws JSONException {
        if (o.equals(Boolean.FALSE) || o instanceof String && ((String)o).equalsIgnoreCase("false")) {
            return false;
        }
        if (o.equals(Boolean.TRUE) || o instanceof String && ((String)o).equalsIgnoreCase("true")) {
            return true;
        }
        throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a Boolean.");
    }

    public double getDouble(String key) throws JSONException {
        return this.doGetDouble(key, this.get(key));
    }

    private double doGetDouble(String key, Object o) throws JSONException {
        try {
            return o instanceof Number ? ((Number)o).doubleValue() : Double.valueOf((String)o).doubleValue();
        }
        catch (Exception e) {
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a number.");
        }
    }

    public int getInt(String key) throws JSONException {
        return this.doGetInt(key, this.get(key));
    }

    private int doGetInt(String key, Object o) throws JSONException {
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
        return this.doGetLong(key, this.get(key));
    }

    private long doGetLong(String key, Object o) throws JSONException {
        return o instanceof String ? Long.parseLong((String)o) : (o instanceof Number ? ((Number)o).longValue() : (long)this.getDouble(key));
    }

    public String getString(String key) throws JSONException {
        return this.get(key).toString();
    }

    public boolean has(String key) {
        return this.myHashMap.containsKey(key);
    }

    public boolean isNull(String key) {
        return NULL.equals(this.opt(key)) || EXPLICIT_NULL.equals(this.opt(key));
    }

    public Iterator keys() {
        return this.myHashMap.keySet().iterator();
    }

    public int length() {
        return this.myHashMap.size();
    }

    public JSONArray names() {
        JSONArray ja = new JSONArray();
        Iterator keys = this.keys();
        while (keys.hasNext()) {
            ja.put(keys.next());
        }
        return ja.length() == 0 ? null : ja;
    }

    public static String numberToString(Number n) throws JSONException {
        if (n == null) {
            throw new JSONException("Null pointer");
        }
        JSONObject.testValidity(n);
        String s = n.toString();
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

    public Object opt(String key) {
        return key == null ? null : this.myHashMap.get(key);
    }

    public boolean optBoolean(String key) {
        return this.optBoolean(key, false);
    }

    public boolean optBoolean(String key, boolean defaultValue) {
        Object o = this.opt(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return this.doGetBoolean(key, o);
        }
        catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    public JSONObject put(String key, Collection value) throws JSONException {
        this.put(key, new JSONArray(value));
        return this;
    }

    public double optDouble(String key) {
        return this.optDouble(key, Double.NaN);
    }

    public double optDouble(String key, double defaultValue) {
        Object o = this.opt(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return this.doGetDouble(key, o);
        }
        catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    public int optInt(String key) {
        return this.optInt(key, 0);
    }

    public int optInt(String key, int defaultValue) {
        Object o = this.opt(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return this.doGetInt(key, o);
        }
        catch (JSONException ex) {
            throw new RuntimeException(ex);
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
        Object o = this.opt(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return this.doGetLong(key, o);
        }
        catch (JSONException ex) {
            throw new RuntimeException(ex);
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
        this.put(key, (Object)value);
        return this;
    }

    public JSONObject put(String key, long value) throws JSONException {
        this.put(key, (Object)value);
        return this;
    }

    public JSONObject put(String key, Map value) throws JSONException {
        this.put(key, new JSONObject(value));
        return this;
    }

    public JSONObject put(String key, Object value) throws JSONException {
        return this.doPut(key, value, -1, false);
    }

    protected JSONObject doPut(String key, Object value, int threshold, boolean checkExistingValue) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        if (value != null) {
            JSONObject.testValidity(value);
            if (!checkExistingValue || !this.myHashMap.containsKey(key)) {
                this.myHashMap.put(key, value);
                if (threshold > 0 && this.myHashMap.size() >= threshold) {
                    throw new JSONSequenceTooLargeException("Threshold has been exceeded");
                }
            } else {
                JSONArray array = null;
                Object existingValue = this.myHashMap.get(key);
                if (existingValue instanceof JSONArray) {
                    array = (JSONArray)existingValue;
                } else {
                    array = new JSONArray(Collections.singletonList(existingValue));
                    this.myHashMap.put(key, array);
                }
                array.put(value);
            }
        } else if (!this.writeNullAsString) {
            this.myHashMap.put(key, null);
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
        return JSONObject.quote(string, true);
    }

    public static String quote(String string, boolean escapeForwardSlashAlways) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }
        char c = '\u0000';
        int len = string.length();
        StringBuilder sb = new StringBuilder(len + 4);
        sb.append('\"');
        block12: for (int i = 0; i < len; ++i) {
            c = string.charAt(i);
            switch (c) {
                case '\\': {
                    sb.append("\\\\");
                    continue block12;
                }
                case '\"': {
                    sb.append("\\\"");
                    continue block12;
                }
                case '/': {
                    if (escapeForwardSlashAlways || i > 0 && string.charAt(i - 1) == '<') {
                        sb.append('\\');
                    }
                    sb.append(c);
                    continue block12;
                }
                case '\b': {
                    sb.append("\\b");
                    continue block12;
                }
                case '\t': {
                    sb.append("\\t");
                    continue block12;
                }
                case '\n': {
                    sb.append("\\n");
                    continue block12;
                }
                case '\f': {
                    sb.append("\\f");
                    continue block12;
                }
                case '\r': {
                    sb.append("\\r");
                    continue block12;
                }
                case '\u2028': {
                    sb.append("\\u2028");
                    continue block12;
                }
                case '\u2029': {
                    sb.append("\\u2029");
                    continue block12;
                }
                default: {
                    if (c < ' ') {
                        String t = "000" + Integer.toHexString(c);
                        sb.append("\\u" + t.substring(t.length() - 4));
                        continue block12;
                    }
                    sb.append(c);
                }
            }
        }
        sb.append('\"');
        return sb.toString();
    }

    public Object remove(String key) {
        return this.myHashMap.remove(key);
    }

    static void testValidity(Object o) throws JSONException {
        if (o != null) {
            if (o instanceof Double) {
                if (((Double)o).isInfinite() || ((Double)o).isNaN()) {
                    throw new JSONException("JSON does not allow non-finite numbers");
                }
            } else if (o instanceof Float && (((Float)o).isInfinite() || ((Float)o).isNaN())) {
                throw new JSONException("JSON does not allow non-finite numbers.");
            }
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

    public int hashCode() {
        return this.myHashMap.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof JSONObject) {
            return this.myHashMap.equals(((JSONObject)obj).myHashMap);
        }
        return false;
    }

    public String toString() {
        try {
            Iterator keys = this.keys();
            StringBuilder sb = new StringBuilder("{");
            while (keys.hasNext()) {
                if (sb.length() > 1) {
                    sb.append(',');
                }
                Object o = keys.next();
                sb.append(JSONObject.quote(o.toString(), this.escapeForwardSlashAlways));
                sb.append(':');
                sb.append(JSONObject.valueToString(this.myHashMap.get(o), this.escapeForwardSlashAlways));
            }
            sb.append('}');
            return sb.toString();
        }
        catch (Exception e) {
            return null;
        }
    }

    public String toString(int indentFactor) throws JSONException {
        return this.toString(indentFactor, 0);
    }

    String toString(int indentFactor, int indent) throws JSONException {
        int i;
        int n = this.length();
        if (n == 0) {
            return "{}";
        }
        Iterator keys = this.keys();
        StringBuilder sb = new StringBuilder("{");
        int newindent = indent + indentFactor;
        while (keys.hasNext()) {
            Object o = keys.next();
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
            sb.append(JSONObject.valueToString(this.myHashMap.get(o), indentFactor, newindent, this.escapeForwardSlashAlways));
        }
        if (sb.length() > 1) {
            sb.append('\n');
            for (i = 0; i < indent; ++i) {
                sb.append(' ');
            }
        }
        sb.append('}');
        return sb.toString();
    }

    static String valueToString(Object value, boolean escapeForwardSlash) throws JSONException {
        if (value == null || value.equals(null)) {
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
            if (o != null) {
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
        return JSONObject.quote(value.toString(), escapeForwardSlash);
    }

    static String valueToString(Object value, int indentFactor, int indent, boolean escapeForwardSlash) throws JSONException {
        if (value == null || value.equals(null)) {
            return "null";
        }
        try {
            if (value instanceof JSONString) {
                return ((JSONString)value).toJSONString();
            }
        }
        catch (Exception exception) {
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
        return JSONObject.quote(value.toString(), escapeForwardSlash);
    }

    public static void setGlobalRecursionDepthLimit(int newRecursionDepthLimit) {
        RECURSION_DEPTH_LIMIT = newRecursionDepthLimit;
    }

    @Deprecated
    public void setRecursionDepthLimit(int newRecursionDepthLimit) {
        RECURSION_DEPTH_LIMIT = newRecursionDepthLimit;
    }

    public static int getGlobalRecursionDepthLimit() {
        return RECURSION_DEPTH_LIMIT;
    }

    @Deprecated
    public int getRecursionDepthLimit() {
        return RECURSION_DEPTH_LIMIT;
    }

    public Writer write(Writer writer) throws JSONException {
        try {
            int hashMapSize = this.myHashMap.size();
            boolean dropObjectKeyName = false;
            if (hashMapSize == 1) {
                boolean bl = dropObjectKeyName = this.dropRootElement || this.ignoredElements != null && this.ignoredElements.contains(this.keys().next());
            }
            if (!dropObjectKeyName) {
                writer.write(123);
            }
            boolean b = false;
            Iterator keys = this.keys();
            while (keys.hasNext()) {
                if (b) {
                    writer.write(44);
                    b = false;
                }
                String k = keys.next().toString();
                Object v = this.myHashMap.get(k);
                boolean mayBeDropSimpleElement = false;
                if (!dropObjectKeyName) {
                    boolean bl = mayBeDropSimpleElement = hashMapSize > 1 && this.ignoredElements != null && this.ignoredElements.contains(k);
                    if (!mayBeDropSimpleElement) {
                        writer.write(JSONObject.quote(k, this.escapeForwardSlashAlways));
                        writer.write(58);
                    }
                }
                if (v instanceof JSONObject) {
                    ((JSONObject)v).write(writer);
                } else if (v instanceof JSONArray) {
                    ((JSONArray)v).write(writer);
                } else if (!mayBeDropSimpleElement) {
                    writer.write(JSONObject.valueToString(v, this.escapeForwardSlashAlways));
                }
                if (mayBeDropSimpleElement) continue;
                b = true;
            }
            if (!dropObjectKeyName) {
                writer.write(125);
            }
            return writer;
        }
        catch (IOException e) {
            throw new JSONException(e);
        }
    }

    public boolean isEscapeForwardSlashAlways() {
        return this.escapeForwardSlashAlways;
    }

    public void setEscapeForwardSlashAlways(boolean escapeForwardSlashAlways) {
        this.escapeForwardSlashAlways = escapeForwardSlashAlways;
    }

    public Map toMap() {
        return Collections.unmodifiableMap(this.myHashMap);
    }

    private static final class Null {
        boolean explicitNull;

        public Null() {
        }

        public Null(boolean explicitNull) {
            this.explicitNull = explicitNull;
        }

        protected final Object clone() {
            return this;
        }

        public boolean equals(Object object) {
            return object == null || object == this;
        }

        public String toString() {
            return this.isExplicitNull() ? null : "null";
        }

        public boolean isExplicitNull() {
            return this.explicitNull;
        }
    }
}

