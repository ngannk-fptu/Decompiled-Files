/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.upm.api.util.Option;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class PurchasedPluginUpdateResultRepresentation {
    @JsonProperty
    private final String message;
    @JsonProperty
    private final String type;

    @JsonCreator
    PurchasedPluginUpdateResultRepresentation(@JsonProperty(value="message") String message, @JsonProperty(value="type") String type) {
        this.message = message;
        this.type = type;
    }

    public static PurchasedPluginUpdateResultRepresentation success(Option<String> message) {
        return new PurchasedPluginUpdateResultRepresentation(message.getOrElse((String)null), "success");
    }

    public static PurchasedPluginUpdateResultRepresentation error(String message) {
        return new PurchasedPluginUpdateResultRepresentation(message, "error");
    }

    public static PurchasedPluginUpdateResultRepresentation warning(String message) {
        return new PurchasedPluginUpdateResultRepresentation(message, "warning");
    }

    public String getMessage() {
        return this.message;
    }

    public String getType() {
        return this.type;
    }
}

