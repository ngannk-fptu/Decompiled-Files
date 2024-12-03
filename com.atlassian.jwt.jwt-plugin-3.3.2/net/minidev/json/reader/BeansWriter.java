/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.reader;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONUtil;
import net.minidev.json.reader.JsonWriter;
import net.minidev.json.reader.JsonWriterI;

public class BeansWriter
implements JsonWriterI<Object> {
    @Override
    public <E> void writeJSONString(E value, Appendable out, JSONStyle compression) throws IOException {
        try {
            boolean needSep = false;
            compression.objectStart(out);
            for (Class<?> nextClass = value.getClass(); nextClass != Object.class; nextClass = nextClass.getSuperclass()) {
                Field[] fields;
                for (Field field : fields = nextClass.getDeclaredFields()) {
                    int m = field.getModifiers();
                    if ((m & 0x98) > 0) continue;
                    Object v = null;
                    if ((m & 1) > 0) {
                        v = field.get(value);
                    } else {
                        Class<?> c2;
                        String g = JSONUtil.getGetterName(field.getName());
                        Method mtd = null;
                        try {
                            mtd = nextClass.getDeclaredMethod(g, new Class[0]);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        if (mtd == null && ((c2 = field.getType()) == Boolean.TYPE || c2 == Boolean.class)) {
                            g = JSONUtil.getIsName(field.getName());
                            mtd = nextClass.getDeclaredMethod(g, new Class[0]);
                        }
                        if (mtd == null) continue;
                        v = mtd.invoke(value, new Object[0]);
                    }
                    if (v == null && compression.ignoreNull()) continue;
                    if (needSep) {
                        compression.objectNext(out);
                    } else {
                        needSep = true;
                    }
                    String key = field.getName();
                    JsonWriter.writeJSONKV(key, v, out, compression);
                }
            }
            compression.objectStop(out);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

