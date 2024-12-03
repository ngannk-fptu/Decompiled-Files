/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.json.jsonorg;

import com.atlassian.annotations.PublicApi;
import com.atlassian.json.jsonorg.JSONElement;
import com.atlassian.json.jsonorg.JSONException;
import com.atlassian.json.jsonorg.JSONObject;
import com.atlassian.json.jsonorg.JSONTokener;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@PublicApi
public class JSONArray
extends JSONElement
implements Iterable<Object> {
    private final List<Object> list = new ArrayList<Object>();

    public JSONArray() {
    }

    public JSONArray(JSONTokener x) throws JSONException {
        char nextChar;
        if (x.nextClean() != '[') {
            x.syntaxError("A JSONArray text must start with '['");
        }
        if ((nextChar = x.nextClean()) == '\u0000') {
            x.syntaxError("Expected a ',' or ']'");
        }
        if (nextChar != ']') {
            x.back();
            block5: while (true) {
                if (x.nextClean() == ',') {
                    x.back();
                    this.list.add(null);
                } else {
                    x.back();
                    this.list.add(x.nextValue());
                }
                switch (x.nextClean()) {
                    case '\u0000': {
                        x.syntaxError("Expected a ',' or ']'");
                    }
                    case ',': 
                    case ';': {
                        nextChar = x.nextClean();
                        if (nextChar == '\u0000') {
                            x.syntaxError("Expected a ',' or ']'");
                        }
                        if (nextChar == ']') {
                            return;
                        }
                        x.back();
                        continue block5;
                    }
                    case ']': {
                        return;
                    }
                }
                x.syntaxError("Expected a ',' or ']'");
            }
        }
    }

    public JSONArray(String source) throws JSONException {
        this(new JSONTokener(source));
    }

    public JSONArray(Collection<? extends Object> collection) {
        if (collection != null) {
            this.list.addAll(collection);
        }
    }

    public JSONArray(Object array) throws JSONException {
        this();
        if (array.getClass().isArray()) {
            int length = Array.getLength(array);
            for (int i = 0; i < length; ++i) {
                this.put(Array.get(array, i));
            }
        } else {
            throw new JSONException("JSONArray initial value should be a string or collection or array.");
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
        return JSONObject.NULL.isNull(this.opt(index));
    }

    public String join(String separator) throws JSONException {
        int len = this.length();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; ++i) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(JSONObject.valueToString(this.list.get(i)));
        }
        return sb.toString();
    }

    public int length() {
        return this.list.size();
    }

    public Object opt(int index) {
        return index < 0 || index >= this.length() ? null : this.list.get(index);
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

    public JSONArray put(Collection<Object> value) {
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
        this.put(new Integer(value));
        return this;
    }

    public JSONArray put(long value) {
        this.put(new Long(value));
        return this;
    }

    public JSONArray put(Map<String, Object> value) {
        this.put(new JSONObject(value));
        return this;
    }

    public JSONArray put(Object value) {
        this.list.add(value);
        return this;
    }

    public JSONArray put(int index, boolean value) throws JSONException {
        this.put(index, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    public JSONArray put(int index, Collection<? extends Object> value) throws JSONException {
        this.put(index, new JSONArray(value));
        return this;
    }

    public JSONArray put(int index, double value) throws JSONException {
        this.put(index, new Double(value));
        return this;
    }

    public JSONArray put(int index, int value) throws JSONException {
        this.put(index, new Integer(value));
        return this;
    }

    public JSONArray put(int index, long value) throws JSONException {
        this.put(index, new Long(value));
        return this;
    }

    public JSONArray put(int index, Map<String, Object> value) throws JSONException {
        this.put(index, new JSONObject(value));
        return this;
    }

    public JSONArray put(int index, Object value) throws JSONException {
        JSONObject.testValidity(value);
        if (index < 0) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        if (index < this.length()) {
            this.list.set(index, value);
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

    @Override
    public Iterator<Object> iterator() {
        return this.list.iterator();
    }

    public Iterable<JSONObject> objects() {
        return new IterableCopyOf<JSONObject>(){

            @Override
            boolean instanceOf(Object obj) {
                return obj instanceof JSONObject;
            }
        }.iterable();
    }

    public Iterable<JSONArray> arrays() {
        return new IterableCopyOf<JSONArray>(){

            @Override
            boolean instanceOf(Object obj) {
                return obj instanceof JSONArray;
            }
        }.iterable();
    }

    public Iterable<String> strings() {
        return new IterableCopyOf<String>(){

            @Override
            boolean instanceOf(Object obj) {
                return obj instanceof String;
            }
        }.iterable();
    }

    public Iterable<Boolean> booleans() {
        return new IterableCopyOf<Boolean>(){

            @Override
            boolean instanceOf(Object obj) {
                return obj instanceof Boolean;
            }
        }.iterable();
    }

    public Iterable<Integer> integers() {
        return new IterableCopyOf<Integer>(){

            @Override
            boolean instanceOf(Object obj) {
                return obj instanceof Number;
            }

            @Override
            Integer transform(Object obj) {
                return ((Number)obj).intValue();
            }
        }.iterable();
    }

    public Iterable<Long> longs() {
        return new IterableCopyOf<Long>(){

            @Override
            boolean instanceOf(Object obj) {
                return obj instanceof Number;
            }

            @Override
            Long transform(Object obj) {
                return ((Number)obj).longValue();
            }
        }.iterable();
    }

    public Iterable<Double> doubles() {
        return new IterableCopyOf<Double>(){

            @Override
            boolean instanceOf(Object obj) {
                return obj instanceof Number;
            }

            @Override
            Double transform(Object obj) {
                return ((Number)obj).doubleValue();
            }
        }.iterable();
    }

    public String toString() {
        try {
            return '[' + this.join(",") + ']';
        }
        catch (Exception e) {
            return "";
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
        StringBuffer sb = new StringBuffer("[");
        if (len == 1) {
            sb.append(JSONObject.valueToString(this.list.get(0), indentFactor, indent));
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
                sb.append(JSONObject.valueToString(this.list.get(i), indentFactor, newindent));
            }
            sb.append('\n');
            for (i = 0; i < indent; ++i) {
                sb.append(' ');
            }
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
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
                if ((v = this.list.get(i)) instanceof JSONObject) {
                    ((JSONObject)v).write(writer);
                } else if (v instanceof JSONArray) {
                    ((JSONArray)v).write(writer);
                } else {
                    writer.write(JSONObject.valueToString(v));
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

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JSONArray jsonArray = (JSONArray)o;
        return this.list.equals(jsonArray.list);
    }

    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    public boolean isJSONObject() {
        return false;
    }

    @Override
    public boolean isJSONArray() {
        return true;
    }

    @Override
    public JSONObject getAsJSONObject() {
        throw new IllegalStateException("I am a JSONArray");
    }

    @Override
    public JSONArray getAsJSONArray() {
        return this;
    }

    private abstract class IterableCopyOf<E> {
        private IterableCopyOf() {
        }

        abstract boolean instanceOf(Object var1);

        E transform(Object obj) {
            return (E)obj;
        }

        Iterable<E> iterable() {
            final ArrayList<E> copyOf = new ArrayList<E>(JSONArray.this.list.size());
            for (Object obj : JSONArray.this.list) {
                if (!this.instanceOf(obj)) continue;
                copyOf.add(this.transform(obj));
            }
            return new Iterable<E>(){
                Iterator<E> iterator;
                {
                    this.iterator = copyOf.iterator();
                }

                @Override
                public Iterator<E> iterator() {
                    return new Iterator<E>(){

                        @Override
                        public boolean hasNext() {
                            return iterator.hasNext();
                        }

                        @Override
                        public E next() {
                            return iterator.next();
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            };
        }
    }
}

