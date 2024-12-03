/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.json;

import aQute.lib.json.Decoder;
import aQute.lib.json.Encoder;
import aQute.lib.json.Handler;
import aQute.lib.json.JSONCodec;
import aQute.lib.json.StringHandler;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectHandler
extends Handler {
    final Class rawClass;
    final Field[] fields;
    final Type[] types;
    final Object[] defaults;
    final Field extra;

    ObjectHandler(JSONCodec codec, Class<?> c) throws Exception {
        this.rawClass = c;
        ArrayList<Field> fields = new ArrayList<Field>();
        for (Field f : c.getFields()) {
            if (Modifier.isStatic(f.getModifiers())) continue;
            fields.add(f);
        }
        this.fields = fields.toArray(new Field[0]);
        Arrays.sort(this.fields, new Comparator<Field>(){

            @Override
            public int compare(Field o1, Field o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        this.types = new Type[this.fields.length];
        this.defaults = new Object[this.fields.length];
        Field x = null;
        for (int i = 0; i < this.fields.length; ++i) {
            if (this.fields[i].getName().equals("__extra")) {
                x = this.fields[i];
            }
            this.types[i] = this.fields[i].getGenericType();
        }
        this.extra = x != null && Map.class.isAssignableFrom(x.getType()) ? x : null;
        try {
            Object template = c.getConstructor(new Class[0]).newInstance(new Object[0]);
            for (int i = 0; i < this.fields.length; ++i) {
                this.defaults[i] = this.fields[i].get(template);
            }
        }
        catch (Exception e) {
            // empty catch block
        }
    }

    @Override
    public void encode(Encoder app, Object object, Map<Object, Type> visited) throws Exception {
        app.append("{");
        app.indent();
        String del = "";
        for (int i = 0; i < this.fields.length; ++i) {
            try {
                if (this.fields[i].getName().startsWith("__")) continue;
                Object value = this.fields[i].get(object);
                if (!app.writeDefaults && (value == this.defaults[i] || value != null && value.equals(this.defaults[i]))) continue;
                app.append(del);
                StringHandler.string(app, this.fields[i].getName());
                app.append(":");
                app.encode(value, this.types[i], visited);
                del = ",";
                continue;
            }
            catch (Exception e) {
                throw new IllegalArgumentException(this.fields[i].getName() + ":", e);
            }
        }
        app.undent();
        app.append("}");
    }

    @Override
    public Object decodeObject(Decoder r) throws Exception {
        assert (r.current() == 123);
        Object targetObject = this.rawClass.getConstructor(new Class[0]).newInstance(new Object[0]);
        int c = r.next();
        while ("[{\"-0123456789tfn".indexOf(c) >= 0) {
            Object value;
            String key = r.codec.parseString(r);
            c = r.skipWs();
            if (c != 58) {
                throw new IllegalArgumentException("Expected ':' but got " + (char)c);
            }
            c = r.next();
            Field f = this.getField(key);
            if (f != null) {
                value = r.codec.decode(f.getGenericType(), r);
                if (value != null || !r.codec.ignorenull) {
                    if (Modifier.isFinal(f.getModifiers())) {
                        throw new IllegalArgumentException("Field " + f + " is final");
                    }
                    f.set(targetObject, value);
                }
            } else if (this.extra == null) {
                if (r.strict) {
                    throw new IllegalArgumentException("No such field " + key);
                }
                value = r.codec.decode(null, r);
                r.getExtra().put(this.rawClass.getName() + "." + key, value);
            } else {
                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>)this.extra.get(targetObject);
                if (map == null) {
                    map = new LinkedHashMap<String, Object>();
                    this.extra.set(targetObject, map);
                }
                Object value2 = r.codec.decode(null, r);
                map.put(key, value2);
            }
            c = r.skipWs();
            if (c == 125) break;
            if (c == 44) {
                c = r.next();
                continue;
            }
            throw new IllegalArgumentException("Invalid character in parsing object, expected } or , but found " + (char)c);
        }
        assert (r.current() == 125);
        r.read();
        return targetObject;
    }

    private Field getField(String key) {
        for (int i = 0; i < this.fields.length; ++i) {
            int n = key.compareTo(this.fields[i].getName());
            if (n == 0) {
                return this.fields[i];
            }
            if (n >= 0) continue;
            return null;
        }
        return null;
    }
}

