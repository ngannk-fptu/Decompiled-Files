/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.json;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;
import java.util.Map;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONTokener;

public class JSONArray
implements Serializable {
    private ArrayList myArrayList;
    private boolean escapeForwardSlashAlways = true;

    public boolean isEscapeForwardSlashAlways() {
        return this.escapeForwardSlashAlways;
    }

    public void setEscapeForwardSlashAlways(boolean escapeForwardSlashAlways) {
        this.escapeForwardSlashAlways = escapeForwardSlashAlways;
    }

    public JSONArray() {
        this.myArrayList = new ArrayList();
    }

    public JSONArray(int capacity) {
        this.myArrayList = new ArrayList(capacity);
    }

    public JSONArray(JSONTokener x) throws JSONException {
        this();
        if (x.nextClean() != '[') {
            throw x.syntaxError("A JSONArray text must start with '['");
        }
        char c = x.nextClean();
        if (c == '\u0000') {
            throw x.syntaxError("JSONArray text must end with ']'");
        }
        if (c == ',') {
            throw x.syntaxError("JSONArray text has a trailing ','");
        }
        if (c == ']') {
            return;
        }
        x.back();
        block4: while (true) {
            if (x.nextClean() == ',') {
                if (c == '[') {
                    throw x.syntaxError("JSONArray text has a trailing ','");
                }
                x.back();
                this.myArrayList.add(null);
            } else {
                x.back();
                this.myArrayList.add(x.nextValue());
            }
            switch (x.nextClean()) {
                case ',': 
                case ';': {
                    char nextClean = x.nextClean();
                    if (nextClean == '\u0000') {
                        throw x.syntaxError("JSONArray text has a trailing ','");
                    }
                    if (nextClean == ']') {
                        return;
                    }
                    x.back();
                    continue block4;
                }
                case ']': {
                    return;
                }
            }
            break;
        }
        throw x.syntaxError("Expected a ',' or ']'");
    }

    public JSONArray(String string) throws JSONException {
        this(new JSONTokener(string));
    }

    public JSONArray(Collection collection) throws JSONException {
        this(collection, 0);
    }

    private JSONArray(Collection collection, int recursionDepth) throws JSONException {
        if (recursionDepth > JSONObject.getGlobalRecursionDepthLimit()) {
            throw new JSONException("JSONArray has reached recursion depth limit of " + JSONObject.getGlobalRecursionDepthLimit());
        }
        this.myArrayList = collection == null ? new ArrayList() : new ArrayList(collection);
        ListIterator<Serializable> iter = this.myArrayList.listIterator();
        while (iter.hasNext()) {
            Object e = iter.next();
            if (e instanceof Collection) {
                iter.set(new JSONArray((Collection)e, recursionDepth + 1));
            }
            if (!(e instanceof Map)) continue;
            iter.set(new JSONObject((Map)e));
        }
    }

    public Object get(int index) throws JSONException {
        Object o = this.opt(index);
        if (o == null) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        return o;
    }

    public boolean getBoolean(int index) throws JSONException {
        Object o = this.get(index);
        if (o.equals(Boolean.FALSE) || o instanceof String && ((String)o).equalsIgnoreCase("false")) {
            return false;
        }
        if (o.equals(Boolean.TRUE) || o instanceof String && ((String)o).equalsIgnoreCase("true")) {
            return true;
        }
        throw new JSONException("JSONArray[" + index + "] is not a Boolean.");
    }

    public double getDouble(int index) throws JSONException {
        Object o = this.get(index);
        try {
            return o instanceof Number ? ((Number)o).doubleValue() : Double.valueOf((String)o).doubleValue();
        }
        catch (Exception e) {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }

    public int getInt(int index) throws JSONException {
        Object o = this.get(index);
        return o instanceof Number ? ((Number)o).intValue() : (int)this.getDouble(index);
    }

    public JSONArray getJSONArray(int index) throws JSONException {
        Object o = this.get(index);
        if (o instanceof JSONArray) {
            return (JSONArray)o;
        }
        throw new JSONException("JSONArray[" + index + "] is not a JSONArray.");
    }

    public JSONObject getJSONObject(int index) throws JSONException {
        Object o = this.get(index);
        if (o instanceof JSONObject) {
            return (JSONObject)o;
        }
        throw new JSONException("JSONArray[" + index + "] is not a JSONObject.");
    }

    public long getLong(int index) throws JSONException {
        Object o = this.get(index);
        return o instanceof Number ? ((Number)o).longValue() : (long)this.getDouble(index);
    }

    public String getString(int index) throws JSONException {
        return this.get(index).toString();
    }

    public boolean isNull(int index) {
        return JSONObject.NULL.equals(this.opt(index));
    }

    public String join(String separator) throws JSONException {
        int len = this.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; ++i) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(JSONObject.valueToString(this.myArrayList.get(i), this.escapeForwardSlashAlways));
        }
        return sb.toString();
    }

    public int length() {
        return this.myArrayList.size();
    }

    public Object opt(int index) {
        return index < 0 || index >= this.length() ? null : this.myArrayList.get(index);
    }

    public boolean optBoolean(int index) {
        return this.optBoolean(index, false);
    }

    public boolean optBoolean(int index, boolean defaultValue) {
        try {
            return this.getBoolean(index);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public double optDouble(int index) {
        return this.optDouble(index, Double.NaN);
    }

    public double optDouble(int index, double defaultValue) {
        try {
            return this.getDouble(index);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public int optInt(int index) {
        return this.optInt(index, 0);
    }

    public int optInt(int index, int defaultValue) {
        try {
            return this.getInt(index);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public JSONArray optJSONArray(int index) {
        Object o = this.opt(index);
        return o instanceof JSONArray ? (JSONArray)o : null;
    }

    public JSONObject optJSONObject(int index) {
        Object o = this.opt(index);
        return o instanceof JSONObject ? (JSONObject)o : null;
    }

    public long optLong(int index) {
        return this.optLong(index, 0L);
    }

    public long optLong(int index, long defaultValue) {
        try {
            return this.getLong(index);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public String optString(int index) {
        return this.optString(index, "");
    }

    public String optString(int index, String defaultValue) {
        Object o = this.opt(index);
        return o != null ? o.toString() : defaultValue;
    }

    public JSONArray put(boolean value) {
        this.put(value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    public JSONArray put(Collection value) throws JSONException {
        this.put(new JSONArray(value));
        return this;
    }

    public JSONArray put(double value) throws JSONException {
        Double d = new Double(value);
        JSONObject.testValidity(d);
        this.put(d);
        return this;
    }

    public JSONArray put(int value) {
        this.put((Object)value);
        return this;
    }

    public JSONArray put(long value) {
        this.put((Object)value);
        return this;
    }

    public JSONArray put(Map value) throws JSONException {
        this.put(new JSONObject(value));
        return this;
    }

    public JSONArray put(Object value) {
        this.myArrayList.add(value);
        return this;
    }

    public JSONArray remove(Object value) {
        this.myArrayList.remove(value);
        return this;
    }

    public JSONArray put(int index, boolean value) throws JSONException {
        this.put(index, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    public JSONArray put(int index, Collection value) throws JSONException {
        this.put(index, new JSONArray(value));
        return this;
    }

    public JSONArray put(int index, double value) throws JSONException {
        this.put(index, new Double(value));
        return this;
    }

    public JSONArray put(int index, int value) throws JSONException {
        this.put(index, (Object)value);
        return this;
    }

    public JSONArray put(int index, long value) throws JSONException {
        this.put(index, (Object)value);
        return this;
    }

    public JSONArray put(int index, Map value) throws JSONException {
        this.put(index, new JSONObject(value));
        return this;
    }

    public JSONArray put(int index, Object value) throws JSONException {
        JSONObject.testValidity(value);
        if (index < 0) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        if (index < this.length()) {
            this.myArrayList.set(index, value);
        } else {
            while (index != this.length()) {
                this.put(JSONObject.NULL);
            }
            this.put(value);
        }
        return this;
    }

    public JSONObject toJSONObject(JSONArray names) throws JSONException {
        if (names == null || names.length() == 0 || this.length() == 0) {
            return null;
        }
        JSONObject jo = new JSONObject();
        for (int i = 0; i < names.length(); ++i) {
            jo.put(names.getString(i), this.opt(i));
        }
        return jo;
    }

    public String toString() {
        try {
            return '[' + this.join(",") + ']';
        }
        catch (Exception e) {
            return null;
        }
    }

    public String toString(int indentFactor) throws JSONException {
        return this.toString(indentFactor, 0);
    }

    String toString(int indentFactor, int indent) throws JSONException {
        int len = this.length();
        if (len == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        if (len == 1) {
            sb.append(JSONObject.valueToString(this.myArrayList.get(0), indentFactor, indent, this.escapeForwardSlashAlways));
        } else {
            int i;
            int newindent = indent + indentFactor;
            sb.append('\n');
            for (i = 0; i < len; ++i) {
                if (i > 0) {
                    sb.append(",\n");
                }
                for (int j = 0; j < newindent; ++j) {
                    sb.append(' ');
                }
                sb.append(JSONObject.valueToString(this.myArrayList.get(i), indentFactor, newindent, this.escapeForwardSlashAlways));
            }
            sb.append('\n');
            for (i = 0; i < indent; ++i) {
                sb.append(' ');
            }
        }
        sb.append(']');
        return sb.toString();
    }

    public int hashCode() {
        return this.myArrayList.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof JSONArray) {
            return this.myArrayList.equals(((JSONArray)obj).myArrayList);
        }
        return false;
    }

    public Writer write(Writer writer) throws JSONException {
        try {
            boolean b = false;
            int len = this.length();
            writer.write(91);
            for (int i = 0; i < len; ++i) {
                Object v;
                if (b) {
                    writer.write(44);
                }
                if ((v = this.myArrayList.get(i)) instanceof JSONObject) {
                    ((JSONObject)v).write(writer);
                } else if (v instanceof JSONArray) {
                    ((JSONArray)v).write(writer);
                } else {
                    writer.write(JSONObject.valueToString(v, this.escapeForwardSlashAlways));
                }
                b = true;
            }
            writer.write(93);
            return writer;
        }
        catch (IOException e) {
            throw new JSONException(e);
        }
    }
}

