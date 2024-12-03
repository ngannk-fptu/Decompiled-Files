/*
 * Decompiled with CFR 0.152.
 */
package groovy.json;

import groovy.json.JsonDelegate;
import groovy.json.JsonException;
import groovy.json.JsonOutput;
import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.Writable;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonBuilder
extends GroovyObjectSupport
implements Writable {
    private Object content;

    public JsonBuilder() {
    }

    public JsonBuilder(Object content) {
        this.content = content;
    }

    public Object getContent() {
        return this.content;
    }

    public Object call(Map m) {
        this.content = m;
        return this.content;
    }

    public Object call(List l) {
        this.content = l;
        return this.content;
    }

    public Object call(Object ... args) {
        ArrayList<Object> listContent = new ArrayList<Object>();
        for (Object it : args) {
            listContent.add(it);
        }
        this.content = listContent;
        return this.content;
    }

    public Object call(Iterable coll, Closure c) {
        ArrayList<Map<String, Object>> listContent = new ArrayList<Map<String, Object>>();
        if (coll != null) {
            for (Object it : coll) {
                listContent.add(JsonDelegate.curryDelegateAndGetContent(c, it));
            }
        }
        this.content = listContent;
        return this.content;
    }

    public Object call(Collection coll, Closure c) {
        return this.call((Iterable)coll, c);
    }

    public Object call(Closure c) {
        this.content = JsonDelegate.cloneDelegateAndGetContent(c);
        return this.content;
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        if (args != null && Object[].class.isAssignableFrom(args.getClass())) {
            Object[] arr = (Object[])args;
            if (arr.length == 0) {
                return this.setAndGetContent(name, new HashMap());
            }
            if (arr.length == 1) {
                if (arr[0] instanceof Closure) {
                    return this.setAndGetContent(name, JsonDelegate.cloneDelegateAndGetContent((Closure)arr[0]));
                }
                if (arr[0] instanceof Map) {
                    return this.setAndGetContent(name, arr[0]);
                }
            } else if (arr.length == 2) {
                Object first = arr[0];
                Object second = arr[1];
                if (second instanceof Closure) {
                    Closure closure = (Closure)second;
                    if (first instanceof Map) {
                        LinkedHashMap<String, Object> subMap = new LinkedHashMap<String, Object>();
                        subMap.putAll((Map)first);
                        subMap.putAll(JsonDelegate.cloneDelegateAndGetContent(closure));
                        return this.setAndGetContent(name, subMap);
                    }
                    if (first instanceof Iterable) {
                        List<Map<String, Object>> list = JsonBuilder.collectContentForEachEntry((Iterable)first, closure);
                        return this.setAndGetContent(name, list);
                    }
                    if (first != null && first.getClass().isArray()) {
                        List<Object> coll = Arrays.asList((Object[])first);
                        List<Map<String, Object>> list = JsonBuilder.collectContentForEachEntry(coll, closure);
                        return this.setAndGetContent(name, list);
                    }
                }
            }
            throw new JsonException("Expected no arguments, a single map, a single closure, or a map and closure as arguments.");
        }
        return this.setAndGetContent(name, new HashMap());
    }

    private static List<Map<String, Object>> collectContentForEachEntry(Iterable coll, Closure closure) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Object it : coll) {
            list.add(JsonDelegate.curryDelegateAndGetContent(closure, it));
        }
        return list;
    }

    private Object setAndGetContent(String name, Object value) {
        LinkedHashMap<String, Object> contentMap = new LinkedHashMap<String, Object>();
        contentMap.put(name, value);
        this.content = contentMap;
        return this.content;
    }

    public String toString() {
        return JsonOutput.toJson(this.content);
    }

    public String toPrettyString() {
        return JsonOutput.prettyPrint(this.toString());
    }

    @Override
    public Writer writeTo(Writer out) throws IOException {
        return out.append(this.toString());
    }
}

