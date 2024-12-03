/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.util;

import java.util.StringJoiner;
import org.apache.avro.Schema;

public class SchemaUtil {
    private SchemaUtil() {
    }

    public static String describe(Schema schema) {
        if (schema == null) {
            return "unknown";
        }
        switch (schema.getType()) {
            case UNION: {
                StringJoiner csv = new StringJoiner(", ");
                for (Schema branch : schema.getTypes()) {
                    csv.add(SchemaUtil.describe(branch));
                }
                return "[" + csv + "]";
            }
            case MAP: {
                return "Map<String, " + SchemaUtil.describe(schema.getValueType()) + ">";
            }
            case ARRAY: {
                return "List<" + SchemaUtil.describe(schema.getElementType()) + ">";
            }
        }
        return schema.getName();
    }

    public static String describe(Object datum) {
        if (datum == null) {
            return "null";
        }
        return datum + " (a " + datum.getClass().getName() + ")";
    }
}

