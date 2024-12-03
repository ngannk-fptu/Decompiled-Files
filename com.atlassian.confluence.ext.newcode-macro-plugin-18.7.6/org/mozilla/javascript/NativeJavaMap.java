/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ES6Iterator;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.Undefined;

public class NativeJavaMap
extends NativeJavaObject {
    private static final long serialVersionUID = -3786257752907047381L;
    private Map<Object, Object> map;
    private static Callable symbol_iterator = (cx, scope, thisObj, args) -> {
        if (!(thisObj instanceof NativeJavaMap)) {
            throw ScriptRuntime.typeErrorById("msg.incompat.call", SymbolKey.ITERATOR);
        }
        return new NativeJavaMapIterator(scope, ((NativeJavaMap)thisObj).map);
    };

    static void init(ScriptableObject scope, boolean sealed) {
        NativeJavaMapIterator.init(scope, sealed);
    }

    public NativeJavaMap(Scriptable scope, Object map) {
        super(scope, map, map.getClass());
        assert (map instanceof Map);
        this.map = (Map)map;
    }

    @Override
    public String getClassName() {
        return "JavaMap";
    }

    @Override
    public boolean has(String name, Scriptable start) {
        Context cx = Context.getCurrentContext();
        if (cx != null && cx.hasFeature(21) && this.map.containsKey(name)) {
            return true;
        }
        return super.has(name, start);
    }

    @Override
    public boolean has(int index, Scriptable start) {
        Context cx = Context.getCurrentContext();
        if (cx != null && cx.hasFeature(21) && this.map.containsKey(index)) {
            return true;
        }
        return super.has(index, start);
    }

    @Override
    public boolean has(Symbol key, Scriptable start) {
        return SymbolKey.ITERATOR.equals(key);
    }

    @Override
    public Object get(String name, Scriptable start) {
        Context cx = Context.getCurrentContext();
        if (cx != null && cx.hasFeature(21) && this.map.containsKey(name)) {
            Object obj = this.map.get(name);
            return cx.getWrapFactory().wrap(cx, this, obj, obj == null ? null : obj.getClass());
        }
        return super.get(name, start);
    }

    @Override
    public Object get(int index, Scriptable start) {
        Context cx = Context.getCurrentContext();
        if (cx != null && cx.hasFeature(21) && this.map.containsKey(index)) {
            Object obj = this.map.get(index);
            return cx.getWrapFactory().wrap(cx, this, obj, obj == null ? null : obj.getClass());
        }
        return super.get(index, start);
    }

    @Override
    public Object get(Symbol key, Scriptable start) {
        if (SymbolKey.ITERATOR.equals(key)) {
            return symbol_iterator;
        }
        return super.get(key, start);
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        Context cx = Context.getCurrentContext();
        if (cx != null && cx.hasFeature(21)) {
            this.map.put(name, Context.jsToJava(value, Object.class));
        } else {
            super.put(name, start, value);
        }
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        Context cx = Context.getContext();
        if (cx != null && cx.hasFeature(21)) {
            this.map.put(index, Context.jsToJava(value, Object.class));
        } else {
            super.put(index, start, value);
        }
    }

    @Override
    public Object[] getIds() {
        Context cx = Context.getCurrentContext();
        if (cx != null && cx.hasFeature(21)) {
            ArrayList<Object> ids = new ArrayList<Object>(this.map.size());
            for (Object key : this.map.keySet()) {
                if (key instanceof Integer) {
                    ids.add(key);
                    continue;
                }
                ids.add(ScriptRuntime.toString(key));
            }
            return ids.toArray();
        }
        return super.getIds();
    }

    private static final class NativeJavaMapIterator
    extends ES6Iterator {
        private static final long serialVersionUID = 1L;
        private static final String ITERATOR_TAG = "JavaMapIterator";
        private Iterator<Map.Entry<Object, Object>> iterator;

        static void init(ScriptableObject scope, boolean sealed) {
            ES6Iterator.init(scope, sealed, new NativeJavaMapIterator(), ITERATOR_TAG);
        }

        private NativeJavaMapIterator() {
        }

        NativeJavaMapIterator(Scriptable scope, Map<Object, Object> map) {
            super(scope, ITERATOR_TAG);
            this.iterator = map.entrySet().iterator();
        }

        @Override
        public String getClassName() {
            return "Java Map Iterator";
        }

        @Override
        protected boolean isDone(Context cx, Scriptable scope) {
            return !this.iterator.hasNext();
        }

        @Override
        protected Object nextValue(Context cx, Scriptable scope) {
            if (!this.iterator.hasNext()) {
                return cx.newArray(scope, new Object[]{Undefined.instance, Undefined.instance});
            }
            Map.Entry<Object, Object> e = this.iterator.next();
            return cx.newArray(scope, new Object[]{e.getKey(), e.getValue()});
        }

        @Override
        protected String getTag() {
            return ITERATOR_TAG;
        }
    }
}

