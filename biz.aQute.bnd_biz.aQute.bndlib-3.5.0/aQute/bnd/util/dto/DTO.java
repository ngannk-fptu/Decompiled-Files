/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.util.dto;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class DTO {
    public String toString() {
        return DTO.appendValue(new StringBuilder(), new IdentityHashMap<Object, String>(), "#", this).toString();
    }

    private static StringBuilder appendDTO(StringBuilder result, Map<Object, String> objectRefs, String refpath, DTO dto) {
        result.append("{");
        String delim = "";
        for (Field field : dto.getClass().getFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            result.append(delim);
            String name = field.getName();
            DTO.appendString(result, name);
            result.append(":");
            Object value = null;
            try {
                value = field.get(dto);
            }
            catch (IllegalAccessException e) {
                // empty catch block
            }
            DTO.appendValue(result, objectRefs, refpath + "/" + name, value);
            delim = ", ";
        }
        result.append("}");
        return result;
    }

    private static StringBuilder appendValue(StringBuilder result, Map<Object, String> objectRefs, String refpath, Object value) {
        if (value == null) {
            return result.append("null");
        }
        if (value instanceof String || value instanceof Character) {
            return DTO.appendString(result, DTO.compress(value.toString()));
        }
        if (value instanceof Number || value instanceof Boolean) {
            return result.append(value.toString());
        }
        String path = objectRefs.get(value);
        if (path != null) {
            result.append("{\"$ref\":");
            DTO.appendString(result, path);
            result.append("}");
            return result;
        }
        objectRefs.put(value, refpath);
        if (value instanceof DTO) {
            return DTO.appendDTO(result, objectRefs, refpath, (DTO)value);
        }
        if (value instanceof Map) {
            return DTO.appendMap(result, objectRefs, refpath, (Map)value);
        }
        if (value instanceof List || value instanceof Set) {
            return DTO.appendIterable(result, objectRefs, refpath, (Iterable)value);
        }
        if (value.getClass().isArray()) {
            return DTO.appendArray(result, objectRefs, refpath, value);
        }
        return DTO.appendString(result, DTO.compress(value.toString()));
    }

    private static StringBuilder appendArray(StringBuilder result, Map<Object, String> objectRefs, String refpath, Object array) {
        result.append("[");
        int length = Array.getLength(array);
        for (int i = 0; i < length; ++i) {
            if (i > 0) {
                result.append(",");
            }
            DTO.appendValue(result, objectRefs, refpath + "/" + i, Array.get(array, i));
        }
        result.append("]");
        return result;
    }

    private static StringBuilder appendIterable(StringBuilder result, Map<Object, String> objectRefs, String refpath, Iterable<?> iterable) {
        result.append("[");
        int i = 0;
        for (Object item : iterable) {
            if (i > 0) {
                result.append(",");
            }
            DTO.appendValue(result, objectRefs, refpath + "/" + i, item);
            ++i;
        }
        result.append("]");
        return result;
    }

    private static StringBuilder appendMap(StringBuilder result, Map<Object, String> objectRefs, String refpath, Map<?, ?> map) {
        result.append("{");
        String delim = "";
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            result.append(delim);
            String name = String.valueOf(entry.getKey());
            DTO.appendString(result, name);
            result.append(":");
            Object value = entry.getValue();
            DTO.appendValue(result, objectRefs, refpath + "/" + name, value);
            delim = ", ";
        }
        result.append("}");
        return result;
    }

    private static StringBuilder appendString(StringBuilder result, CharSequence string) {
        result.append("\"");
        int i = result.length();
        result.append(string);
        while (i < result.length()) {
            char c = result.charAt(i);
            if (c == '\"' || c == '\\') {
                result.insert(i, '\\');
                i += 2;
                continue;
            }
            if (c < ' ') {
                result.insert(i + 1, Integer.toHexString(c | 0x10000));
                result.replace(i, i + 2, "\\u");
                i += 6;
                continue;
            }
            ++i;
        }
        result.append("\"");
        return result;
    }

    private static CharSequence compress(CharSequence in) {
        int length = in.length();
        if (length <= 21) {
            return in;
        }
        StringBuilder result = new StringBuilder(21);
        result.append(in, 0, 9);
        result.append("...");
        result.append(in, length - 9, length);
        return result;
    }
}

