/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.LinkedCaseInsensitiveMap
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.socket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;

public class WebSocketExtension {
    private final String name;
    private final Map<String, String> parameters;

    public WebSocketExtension(String name) {
        this(name, null);
    }

    public WebSocketExtension(String name, @Nullable Map<String, String> parameters) {
        Assert.hasLength((String)name, (String)"Extension name must not be empty");
        this.name = name;
        if (!CollectionUtils.isEmpty(parameters)) {
            LinkedCaseInsensitiveMap map = new LinkedCaseInsensitiveMap(parameters.size(), Locale.ENGLISH);
            map.putAll(parameters);
            this.parameters = Collections.unmodifiableMap(map);
        } else {
            this.parameters = Collections.emptyMap();
        }
    }

    public String getName() {
        return this.name;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !WebSocketExtension.class.isAssignableFrom(other.getClass())) {
            return false;
        }
        WebSocketExtension otherExt = (WebSocketExtension)other;
        return this.name.equals(otherExt.name) && this.parameters.equals(otherExt.parameters);
    }

    public int hashCode() {
        return this.name.hashCode() * 31 + this.parameters.hashCode();
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(this.name);
        this.parameters.forEach((key, value) -> str.append(';').append((String)key).append('=').append((String)value));
        return str.toString();
    }

    public static List<WebSocketExtension> parseExtensions(String extensions) {
        if (StringUtils.hasText((String)extensions)) {
            String[] tokens = StringUtils.tokenizeToStringArray((String)extensions, (String)",");
            ArrayList<WebSocketExtension> result = new ArrayList<WebSocketExtension>(tokens.length);
            for (String token : tokens) {
                result.add(WebSocketExtension.parseExtension(token));
            }
            return result;
        }
        return Collections.emptyList();
    }

    private static WebSocketExtension parseExtension(String extension) {
        if (extension.contains(",")) {
            throw new IllegalArgumentException("Expected single extension value: [" + extension + "]");
        }
        String[] parts = StringUtils.tokenizeToStringArray((String)extension, (String)";");
        String name = parts[0].trim();
        LinkedHashMap parameters = null;
        if (parts.length > 1) {
            parameters = CollectionUtils.newLinkedHashMap((int)(parts.length - 1));
            for (int i = 1; i < parts.length; ++i) {
                String parameter = parts[i];
                int eqIndex = parameter.indexOf(61);
                if (eqIndex == -1) continue;
                String attribute = parameter.substring(0, eqIndex);
                String value = parameter.substring(eqIndex + 1);
                parameters.put(attribute, value);
            }
        }
        return new WebSocketExtension(name, parameters);
    }
}

