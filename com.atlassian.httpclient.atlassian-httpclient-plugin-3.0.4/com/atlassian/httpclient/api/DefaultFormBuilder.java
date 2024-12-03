/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package com.atlassian.httpclient.api;

import com.atlassian.httpclient.api.EntityBuilder;
import com.atlassian.httpclient.api.FormBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

final class DefaultFormBuilder
implements FormBuilder {
    private Map<String, List<String>> parameters = Maps.newLinkedHashMap();

    DefaultFormBuilder() {
    }

    @Override
    public FormBuilder addParam(String name) {
        return this.addParam(name, null);
    }

    @Override
    public FormBuilder addParam(String name, String value) {
        LinkedList values = this.parameters.get(name);
        if (values == null) {
            values = Lists.newLinkedList();
            this.parameters.put(name, values);
        }
        values.add(value);
        return this;
    }

    @Override
    public FormBuilder setParam(String name, List<String> values) {
        this.parameters.put(name, Lists.newLinkedList(values));
        return this;
    }

    @Override
    public EntityBuilder.Entity build() {
        StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, List<String>> entry : this.parameters.entrySet()) {
            String name = this.encode(entry.getKey());
            List<String> values = entry.getValue();
            for (String value : values) {
                if (first) {
                    first = false;
                } else {
                    buf.append("&");
                }
                buf.append(name);
                if (value == null) continue;
                buf.append("=");
                buf.append(this.encode(value));
            }
        }
        final byte[] bytes = buf.toString().getBytes(Charset.forName("UTF-8"));
        return new EntityBuilder.Entity(){

            @Override
            public Map<String, String> getHeaders() {
                return ImmutableMap.of((Object)"Content-Type", (Object)"application/x-www-form-urlencoded; charset=UTF-8");
            }

            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(bytes);
            }

            public String toString() {
                return new String(bytes, Charset.forName("UTF-8"));
            }
        };
    }

    private String encode(String str) {
        try {
            str = URLEncoder.encode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return str;
    }
}

