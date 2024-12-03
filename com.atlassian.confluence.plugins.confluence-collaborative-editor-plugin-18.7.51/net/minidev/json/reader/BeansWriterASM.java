/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.reader;

import java.io.IOException;
import net.minidev.asm.Accessor;
import net.minidev.asm.BeansAccess;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONUtil;
import net.minidev.json.reader.JsonWriterI;

public class BeansWriterASM
implements JsonWriterI<Object> {
    @Override
    public <E> void writeJSONString(E value, Appendable out, JSONStyle compression) throws IOException {
        Class<?> cls = value.getClass();
        boolean needSep = false;
        BeansAccess<?> fields = BeansAccess.get(cls, JSONUtil.JSON_SMART_FIELD_FILTER);
        out.append('{');
        for (Accessor field : fields.getAccessors()) {
            Object v = fields.get(value, field.getIndex());
            if (v == null && compression.ignoreNull()) continue;
            if (needSep) {
                out.append(',');
            } else {
                needSep = true;
            }
            String key = field.getName();
            JSONObject.writeJSONKV(key, v, out, compression);
        }
        out.append('}');
    }
}

