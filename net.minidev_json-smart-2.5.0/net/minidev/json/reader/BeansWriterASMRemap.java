/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.asm.Accessor
 *  net.minidev.asm.BeansAccess
 *  net.minidev.asm.FieldFilter
 */
package net.minidev.json.reader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.minidev.asm.Accessor;
import net.minidev.asm.BeansAccess;
import net.minidev.asm.FieldFilter;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONUtil;
import net.minidev.json.reader.JsonWriterI;

public class BeansWriterASMRemap
implements JsonWriterI<Object> {
    private Map<String, String> rename = new HashMap<String, String>();

    public void renameField(String source, String dest) {
        this.rename.put(source, dest);
    }

    private String rename(String key) {
        String k2 = this.rename.get(key);
        if (k2 != null) {
            return k2;
        }
        return key;
    }

    @Override
    public <E> void writeJSONString(E value, Appendable out, JSONStyle compression) throws IOException {
        Class<?> cls = value.getClass();
        boolean needSep = false;
        BeansAccess fields = BeansAccess.get(cls, (FieldFilter)JSONUtil.JSON_SMART_FIELD_FILTER);
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
            key = this.rename(key);
            JSONObject.writeJSONKV(key, v, out, compression);
        }
        out.append('}');
    }
}

