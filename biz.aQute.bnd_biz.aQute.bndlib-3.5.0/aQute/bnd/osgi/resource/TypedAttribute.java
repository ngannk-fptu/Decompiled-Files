/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Version
 */
package aQute.bnd.osgi.resource;

import aQute.bnd.version.Version;
import java.util.Collection;

public class TypedAttribute {
    public final String value;
    public final String type;

    public TypedAttribute(String type, String value) {
        this.type = "String".equals(type) ? null : type;
        this.value = value;
    }

    public static TypedAttribute getTypedAttribute(Object value) {
        if (value instanceof Collection) {
            Collection c = (Collection)value;
            if (c.isEmpty()) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            String del = "";
            String subType = null;
            for (Object v : c) {
                if (subType == null) {
                    subType = TypedAttribute.getType(v);
                }
                sb.append(del).append(TypedAttribute.escape(v.toString()));
                del = ",";
            }
            if (subType == null) {
                subType = "String";
            }
            return new TypedAttribute("List<" + subType + ">", sb.toString());
        }
        if (value.getClass().isArray()) {
            Object[] array = (Object[])value;
            if (array.length == 0) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            String del = "";
            String subType = null;
            for (Object v : array) {
                if (subType == null) {
                    subType = TypedAttribute.getType(v);
                }
                sb.append(del).append(TypedAttribute.escape(v.toString()));
                del = ",";
            }
            if (subType == null) {
                subType = "String";
            }
            return new TypedAttribute("List<" + subType + ">", sb.toString());
        }
        return new TypedAttribute(TypedAttribute.getType(value), value.toString());
    }

    private static Object escape(String v) {
        StringBuilder sb = new StringBuilder();
        block4: for (int i = 0; i < v.length(); ++i) {
            char c = v.charAt(i);
            switch (c) {
                case '\\': {
                    sb.append("\\\\");
                    continue block4;
                }
                case ',': {
                    sb.append("\\,");
                }
                default: {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    private static String getType(Object value) {
        if (value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte) {
            return "Long";
        }
        if (value instanceof Double || value instanceof Float) {
            return "Double";
        }
        if (value instanceof Version || value instanceof org.osgi.framework.Version) {
            return "Version";
        }
        return "String";
    }
}

