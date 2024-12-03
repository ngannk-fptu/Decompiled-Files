/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.json;

import aQute.lib.json.Decoder;
import aQute.lib.json.Encoder;
import aQute.lib.json.Handler;
import aQute.lib.json.StringHandler;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class MapHandler
extends Handler {
    final Class<?> rawClass;
    final Type keyType;
    final Type valueType;

    MapHandler(Class<?> rawClass, Type keyType, Type valueType) {
        if (rawClass != Map.class) {
            ParameterizedType type = this.findAncestor(rawClass, Map.class);
            this.keyType = keyType == Object.class ? this.resolve(type.getActualTypeArguments()[0]) : keyType;
            this.valueType = valueType == Object.class ? this.resolve(type.getActualTypeArguments()[1]) : valueType;
        } else {
            this.keyType = keyType;
            this.valueType = valueType;
        }
        if (rawClass.isInterface()) {
            if (rawClass.isAssignableFrom(LinkedHashMap.class)) {
                rawClass = LinkedHashMap.class;
            } else if (rawClass.isAssignableFrom(TreeMap.class)) {
                rawClass = TreeMap.class;
            } else if (rawClass.isAssignableFrom(Hashtable.class)) {
                rawClass = Hashtable.class;
            } else if (rawClass.isAssignableFrom(HashMap.class)) {
                rawClass = HashMap.class;
            } else if (rawClass.isAssignableFrom(Dictionary.class)) {
                rawClass = Hashtable.class;
            } else {
                throw new IllegalArgumentException("Unknown map interface: " + rawClass);
            }
        }
        this.rawClass = rawClass;
    }

    private Type resolve(Type type) {
        if (type instanceof TypeVariable) {
            TypeVariable tv = (TypeVariable)type;
            Type[] bounds = tv.getBounds();
            return this.resolve(bounds[bounds.length - 1]);
        }
        return type;
    }

    private ParameterizedType findAncestor(Class<?> start, Class<?> target) {
        if (start == null || start == Object.class) {
            return null;
        }
        for (Type type : start.getGenericInterfaces()) {
            if (!(type instanceof ParameterizedType) || ((ParameterizedType)type).getRawType() != target) continue;
            return (ParameterizedType)type;
        }
        for (Type type : start.getInterfaces()) {
            ParameterizedType ancestor = this.findAncestor((Class<?>)type, target);
            if (ancestor == null) continue;
            return ancestor;
        }
        return this.findAncestor(start.getSuperclass(), target);
    }

    @Override
    public void encode(Encoder app, Object object, Map<Object, Type> visited) throws IOException, Exception {
        Map map = (Map)object;
        app.append("{");
        String del = "";
        for (Map.Entry e : map.entrySet()) {
            try {
                app.append(del);
                String key = e.getKey() != null && (this.keyType == String.class || this.keyType == Object.class) ? e.getKey().toString() : app.codec.enc().put(e.getKey()).toString();
                StringHandler.string(app, key);
                app.append(":");
                app.encode(e.getValue(), this.valueType, visited);
                del = ",";
            }
            catch (Exception ee) {
                throw new IllegalArgumentException("[\"" + e.getKey() + "\"]", ee);
            }
        }
        app.append("}");
    }

    @Override
    public Object decodeObject(Decoder r) throws Exception {
        assert (r.current() == 123);
        Map map = (Map)this.rawClass.getConstructor(new Class[0]).newInstance(new Object[0]);
        int c = r.next();
        while ("[{\"-0123456789tfn".indexOf(c) >= 0) {
            Object key = r.codec.parseString(r);
            if (this.keyType != null && this.keyType != Object.class) {
                Handler h = r.codec.getHandler(this.keyType, null);
                key = h.decode(r, (String)key);
            }
            if ((c = r.skipWs()) != 58) {
                throw new IllegalArgumentException("Expected ':' but got " + (char)c);
            }
            c = r.next();
            Object value = r.codec.decode(this.valueType, r);
            if (value != null || !r.codec.ignorenull) {
                map.put(key, value);
            }
            if ((c = r.skipWs()) == 125) break;
            if (c == 44) {
                c = r.next();
                continue;
            }
            throw new IllegalArgumentException("Invalid character in parsing list, expected } or , but found " + (char)c);
        }
        assert (r.current() == 125);
        r.read();
        return map;
    }
}

