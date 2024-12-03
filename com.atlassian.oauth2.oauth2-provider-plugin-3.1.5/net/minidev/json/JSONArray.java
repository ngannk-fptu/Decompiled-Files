/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minidev.json.JSONAwareEx;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStreamAwareEx;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;
import net.minidev.json.reader.JsonWriter;

public class JSONArray
extends ArrayList<Object>
implements List<Object>,
JSONAwareEx,
JSONStreamAwareEx {
    private static final long serialVersionUID = 9106884089231309568L;

    public JSONArray() {
    }

    public JSONArray(int initialCapacity) {
        super(initialCapacity);
    }

    public static String toJSONString(List<? extends Object> list) {
        return JSONArray.toJSONString(list, JSONValue.COMPRESSION);
    }

    public static String toJSONString(List<? extends Object> list, JSONStyle compression) {
        StringBuilder sb = new StringBuilder();
        try {
            JSONArray.writeJSONString(list, sb, compression);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return sb.toString();
    }

    public static void writeJSONString(Iterable<? extends Object> list, Appendable out, JSONStyle compression) throws IOException {
        if (list == null) {
            out.append("null");
            return;
        }
        JsonWriter.JSONIterableWriter.writeJSONString(list, out, compression);
    }

    public static void writeJSONString(List<? extends Object> list, Appendable out) throws IOException {
        JSONArray.writeJSONString(list, out, JSONValue.COMPRESSION);
    }

    public JSONArray appendElement(Object element) {
        this.add(element);
        return this;
    }

    public void merge(Object o2) {
        JSONObject.merge(this, o2);
    }

    @Override
    public String toJSONString() {
        return JSONArray.toJSONString(this, JSONValue.COMPRESSION);
    }

    @Override
    public String toJSONString(JSONStyle compression) {
        return JSONArray.toJSONString(this, compression);
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }

    public String toString(JSONStyle compression) {
        return this.toJSONString(compression);
    }

    @Override
    public void writeJSONString(Appendable out) throws IOException {
        JSONArray.writeJSONString(this, out, JSONValue.COMPRESSION);
    }

    @Override
    public void writeJSONString(Appendable out, JSONStyle compression) throws IOException {
        JSONArray.writeJSONString(this, out, compression);
    }
}

