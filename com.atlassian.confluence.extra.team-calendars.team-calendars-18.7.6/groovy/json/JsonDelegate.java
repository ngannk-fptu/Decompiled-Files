/*
 * Decompiled with CFR 0.152.
 */
package groovy.json;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonDelegate
extends GroovyObjectSupport {
    private Map<String, Object> content = new LinkedHashMap<String, Object>();

    @Override
    public Object invokeMethod(String name, Object args) {
        List<Object> val = null;
        if (args != null && Object[].class.isAssignableFrom(args.getClass())) {
            Object[] arr = (Object[])args;
            if (arr.length == 1) {
                val = arr[0];
            } else if (JsonDelegate.isIterableOrArrayAndClosure(arr)) {
                Closure closure = (Closure)arr[1];
                Iterator iterator = arr[0] instanceof Iterable ? ((Iterable)arr[0]).iterator() : Arrays.asList((Object[])arr[0]).iterator();
                ArrayList<Object> list = new ArrayList<Object>();
                while (iterator.hasNext()) {
                    list.add(JsonDelegate.curryDelegateAndGetContent(closure, iterator.next()));
                }
                val = list;
            } else {
                val = Arrays.asList(arr);
            }
        }
        this.content.put(name, val);
        return val;
    }

    private static boolean isIterableOrArrayAndClosure(Object[] args) {
        if (args.length != 2 || !(args[1] instanceof Closure)) {
            return false;
        }
        return args[0] instanceof Iterable || args[0] != null && args[0].getClass().isArray();
    }

    public static Map<String, Object> cloneDelegateAndGetContent(Closure<?> c) {
        JsonDelegate delegate = new JsonDelegate();
        Closure cloned = (Closure)c.clone();
        cloned.setDelegate(delegate);
        cloned.setResolveStrategy(1);
        cloned.call();
        return delegate.getContent();
    }

    public static Map<String, Object> curryDelegateAndGetContent(Closure<?> c, Object o) {
        JsonDelegate delegate = new JsonDelegate();
        Closure<?> curried = c.curry(o);
        curried.setDelegate(delegate);
        curried.setResolveStrategy(1);
        curried.call();
        return delegate.getContent();
    }

    public Map<String, Object> getContent() {
        return this.content;
    }
}

