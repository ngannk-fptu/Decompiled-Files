/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.plugins.emailgateway;

import com.atlassian.confluence.plugins.emailgateway.api.EmailBodyType;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Map;

public class EmailBody {
    private EmailBodyType type;
    private String content;
    private Map<String, ? extends Serializable> context;

    public EmailBody(EmailBodyType type, String content) {
        this.type = type;
        this.content = content;
        this.context = ImmutableMap.of();
    }

    public EmailBody(EmailBodyType type, String content, Map<String, ? extends Serializable> context) {
        this(type, content);
        this.context = ImmutableMap.copyOf(context);
    }

    public EmailBodyType getType() {
        return this.type;
    }

    public String getContent() {
        return this.content;
    }

    public Map<String, ? extends Serializable> getContext() {
        return this.context;
    }
}

