/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Filter
 *  org.osgi.framework.FrameworkUtil
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.ServiceReference
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.util;

import java.nio.CharBuffer;
import java.util.Collection;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public abstract class OsgiFilterUtils {
    private static final char FILTER_BEGIN = '(';
    private static final char FILTER_END = ')';
    private static final String FILTER_AND_CONSTRAINT = "(&";
    private static final String EQUALS = "=";

    public static String unifyFilter(String clazz, String filter) {
        return OsgiFilterUtils.unifyFilter(new String[]{clazz}, filter);
    }

    public static String unifyFilter(Class<?> clazz, String filter) {
        if (clazz != null) {
            return OsgiFilterUtils.unifyFilter(clazz.getName(), filter);
        }
        return OsgiFilterUtils.unifyFilter((String)null, filter);
    }

    public static String unifyFilter(Class<?>[] classes, String filter) {
        if (ObjectUtils.isEmpty((Object[])classes)) {
            return OsgiFilterUtils.unifyFilter(new String[0], filter);
        }
        String[] classNames = new String[classes.length];
        for (int i = 0; i < classNames.length; ++i) {
            if (classes[i] == null) continue;
            classNames[i] = classes[i].getName();
        }
        return OsgiFilterUtils.unifyFilter(classNames, filter);
    }

    public static String unifyFilter(String[] classes, String filter) {
        return OsgiFilterUtils.unifyFilter("objectClass", classes, filter);
    }

    public static String unifyFilter(String key, String[] items, String filter) {
        boolean moreThenOneClass;
        boolean filterHasText = StringUtils.hasText((String)filter);
        if (items == null) {
            items = new String[]{};
        }
        int itemName = items.length;
        for (int i = 0; i < items.length; ++i) {
            if (items[i] != null) continue;
            --itemName;
        }
        if (itemName == 0) {
            if (filterHasText) {
                return filter;
            }
            throw new IllegalArgumentException("at least one parameter has to be not-null");
        }
        Assert.hasText((String)key, (String)"key is required");
        if (filterHasText && (filter.charAt(0) != '(' || filter.charAt(filter.length() - 1) != ')')) {
            throw new IllegalArgumentException("invalid filter: " + filter);
        }
        StringBuilder buffer = new StringBuilder();
        if (filterHasText) {
            buffer.append(FILTER_AND_CONSTRAINT);
        }
        boolean bl = moreThenOneClass = itemName > 1;
        if (moreThenOneClass) {
            buffer.append(FILTER_AND_CONSTRAINT);
        }
        for (int i = 0; i < items.length; ++i) {
            if (items[i] == null) continue;
            buffer.append('(');
            buffer.append(key);
            buffer.append(EQUALS);
            buffer.append(items[i]);
            buffer.append(')');
        }
        if (moreThenOneClass) {
            buffer.append(')');
        }
        if (filterHasText) {
            buffer.append(filter);
            buffer.append(')');
        }
        return buffer.toString();
    }

    public static boolean isValidFilter(String filter) {
        try {
            OsgiFilterUtils.createFilter(filter);
            return true;
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static Filter createFilter(String filter) {
        Assert.hasText((String)filter, (String)"invalid filter");
        try {
            return FrameworkUtil.createFilter((String)filter);
        }
        catch (InvalidSyntaxException ise) {
            throw (RuntimeException)new IllegalArgumentException("invalid filter: " + ise.getFilter()).initCause(ise);
        }
    }

    public static String getFilter(ServiceReference reference) {
        String[] propertyKeys = reference.getPropertyKeys();
        StringBuilder sb = new StringBuilder(propertyKeys.length << 3);
        sb.append(FILTER_AND_CONSTRAINT);
        for (String key : propertyKeys) {
            if ("service.id".equals(key)) continue;
            Object value = reference.getProperty(key);
            Class<?> cl = value.getClass();
            if (cl.isArray()) {
                Object[] array = ObjectUtils.toObjectArray((Object)value);
                for (Object item : array) {
                    sb.append("(");
                    sb.append(OsgiFilterUtils.escapeFilterCharacters(key));
                    sb.append(EQUALS);
                    sb.append(OsgiFilterUtils.escapeFilterCharacters(String.valueOf(item)));
                    sb.append(")");
                }
                continue;
            }
            if (Collection.class.isAssignableFrom(cl)) {
                Collection c = (Collection)value;
                for (Object item : c) {
                    sb.append("(");
                    sb.append(OsgiFilterUtils.escapeFilterCharacters(key));
                    sb.append(EQUALS);
                    sb.append(OsgiFilterUtils.escapeFilterCharacters(String.valueOf(item)));
                    sb.append(")");
                }
                continue;
            }
            sb.append("(");
            sb.append(OsgiFilterUtils.escapeFilterCharacters(key));
            sb.append(EQUALS);
            sb.append(OsgiFilterUtils.escapeFilterCharacters(String.valueOf(value)));
            sb.append(")");
        }
        sb.append(")");
        return sb.toString();
    }

    private static String escapeFilterCharacters(String value) {
        CharBuffer buffer = CharBuffer.allocate(value.length() * 2);
        for (char c : value.toCharArray()) {
            switch (c) {
                case '*': {
                    buffer.append('\\');
                    break;
                }
                case '\\': {
                    buffer.append('\\');
                    break;
                }
                case '(': {
                    buffer.append('\\');
                    break;
                }
                case ')': {
                    buffer.append('\\');
                }
            }
            buffer.append(c);
        }
        return buffer.flip().toString();
    }
}

