/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.jwt.httpclient;

import com.atlassian.jwt.CanonicalHttpRequest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CanonicalRequestUtil {
    public static String toVerboseString(CanonicalHttpRequest request) {
        return new ToStringBuilder((Object)request, ToStringStyle.SHORT_PREFIX_STYLE).append("method", (Object)request.getMethod()).append("relativePath", (Object)request.getRelativePath()).append("parameterMap", (Object)CanonicalRequestUtil.mapToString(request.getParameterMap())).toString();
    }

    private static String mapToString(Map<String, String[]> parameterMap) {
        StringBuilder sb = new StringBuilder().append('[');
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            sb.append(entry.getKey()).append(" -> ");
            String[] value = entry.getValue();
            if (value != null) {
                sb.append("(");
                CanonicalRequestUtil.appendTo(sb, Arrays.asList(value), ",");
                sb.append(")");
            }
            sb.append(',');
        }
        return sb.append(']').toString();
    }

    private static StringBuilder appendTo(StringBuilder appendable, Iterable<?> parts, CharSequence separator) {
        Iterator<?> iterator = parts.iterator();
        if (iterator.hasNext()) {
            appendable.append(CanonicalRequestUtil.toString(iterator.next()));
            while (iterator.hasNext()) {
                appendable.append(separator);
                appendable.append(CanonicalRequestUtil.toString(iterator.next()));
            }
        }
        return appendable;
    }

    private static CharSequence toString(Object part) {
        return part instanceof CharSequence ? (CharSequence)part : part.toString();
    }
}

