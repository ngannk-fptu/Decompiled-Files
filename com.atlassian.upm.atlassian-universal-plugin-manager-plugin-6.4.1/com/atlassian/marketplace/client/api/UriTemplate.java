/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.util.UriBuilder;
import com.google.common.collect.ImmutableMap;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UriTemplate {
    private static final Pattern SIMPLE_PLACEHOLDER_REGEX = Pattern.compile("\\{([^}]*)\\}");
    private final String value;

    public static UriTemplate create(String value) {
        return new UriTemplate(value);
    }

    private UriTemplate(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public URI resolve(Map<String, String> params) {
        StringBuilder b = new StringBuilder();
        int pos = 0;
        Matcher m = SIMPLE_PLACEHOLDER_REGEX.matcher(this.value);
        ImmutableMap.Builder query = ImmutableMap.builder();
        while (m.find()) {
            b.append(this.value, pos, m.start());
            String name = m.group(1);
            if (name.startsWith("?")) {
                for (String subName : name.substring(1).split(",")) {
                    String realName;
                    String string = realName = subName.endsWith("*") ? subName.substring(0, subName.length() - 1) : subName;
                    if (!params.containsKey(realName)) continue;
                    query.put((Object)realName, (Object)params.get(realName));
                }
            } else {
                String value = params.getOrDefault(name, "");
                try {
                    b.append(URLEncoder.encode(value, "UTF-8"));
                }
                catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
            pos = m.end();
        }
        b.append(this.value.substring(pos));
        UriBuilder ub = UriBuilder.fromUri(b.toString());
        for (Map.Entry e : query.build().entrySet()) {
            ub.queryParam((String)e.getKey(), e.getValue());
        }
        return ub.build();
    }

    public boolean equals(Object other) {
        return other instanceof UriTemplate && ((UriTemplate)other).value.equals(this.value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }
}

