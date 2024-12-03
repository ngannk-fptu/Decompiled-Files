/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang.StringUtils
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.stp.action;

import com.atlassian.troubleshooting.stp.action.Message;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ParametersAreNonnullByDefault
public class DefaultMessage
implements Message,
Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final String body;
    private final String url;

    public DefaultMessage(String name, String body) {
        this(name, body, null);
    }

    @JsonCreator
    public DefaultMessage(@JsonProperty(value="name") String name, @JsonProperty(value="body") String body, @JsonProperty(value="url") @Nullable String url) {
        this.name = Objects.requireNonNull(name);
        this.body = Objects.requireNonNull(body);
        this.url = url;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @HtmlSafe
    public String getBody() {
        return this.body;
    }

    @Override
    public String getURL() {
        return this.url;
    }

    @Override
    public boolean hasURL() {
        return !StringUtils.isEmpty((String)this.url);
    }

    public String toString() {
        return this.name + ":" + this.body;
    }
}

