/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.webdav;

import java.util.HashMap;
import java.util.Map;
import javax.jcr.PropertyType;

public class JcrValueType {
    private static final String VALUE_CONTENT_TYPE_FRAGMENT = "jcr-value/";
    private static final Map<String, Integer> TYPE_LOOKUP = new HashMap<String, Integer>();

    public static String contentTypeFromType(int propertyType) {
        return VALUE_CONTENT_TYPE_FRAGMENT + PropertyType.nameFromValue(propertyType).toLowerCase();
    }

    public static int typeFromContentType(String contentType) {
        if (contentType != null) {
            String ct;
            int pos = contentType.indexOf(59);
            String string = ct = pos == -1 ? contentType : contentType.substring(0, pos);
            if (TYPE_LOOKUP.containsKey(ct)) {
                return TYPE_LOOKUP.get(ct);
            }
        }
        return 0;
    }

    static {
        TYPE_LOOKUP.put(JcrValueType.contentTypeFromType(2), 2);
        TYPE_LOOKUP.put(JcrValueType.contentTypeFromType(6), 6);
        TYPE_LOOKUP.put(JcrValueType.contentTypeFromType(5), 5);
        TYPE_LOOKUP.put(JcrValueType.contentTypeFromType(12), 12);
        TYPE_LOOKUP.put(JcrValueType.contentTypeFromType(4), 4);
        TYPE_LOOKUP.put(JcrValueType.contentTypeFromType(3), 3);
        TYPE_LOOKUP.put(JcrValueType.contentTypeFromType(7), 7);
        TYPE_LOOKUP.put(JcrValueType.contentTypeFromType(8), 8);
        TYPE_LOOKUP.put(JcrValueType.contentTypeFromType(9), 9);
        TYPE_LOOKUP.put(JcrValueType.contentTypeFromType(1), 1);
        TYPE_LOOKUP.put(JcrValueType.contentTypeFromType(0), 0);
        TYPE_LOOKUP.put(JcrValueType.contentTypeFromType(11), 11);
        TYPE_LOOKUP.put(JcrValueType.contentTypeFromType(10), 10);
    }
}

