/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ValidatePluginLicenseResultRepresentation {
    @JsonProperty
    private final Collection<String> messages;
    @JsonProperty
    private final String type;

    @JsonCreator
    public ValidatePluginLicenseResultRepresentation(@JsonProperty(value="messages") Collection<String> messages, @JsonProperty(value="type") String type) {
        this.messages = Collections.unmodifiableList(new ArrayList<String>(messages));
        this.type = type;
    }

    public static ValidatePluginLicenseResultRepresentation success(Collection<String> messages) {
        return new ValidatePluginLicenseResultRepresentation(messages, "success");
    }

    public static ValidatePluginLicenseResultRepresentation error(Collection<String> messages) {
        return new ValidatePluginLicenseResultRepresentation(messages, "error");
    }

    public static ValidatePluginLicenseResultRepresentation warning(Collection<String> messages) {
        return new ValidatePluginLicenseResultRepresentation(messages, "warning");
    }

    public static ValidatePluginLicenseResultRepresentation debug(Collection<String> messages) {
        return new ValidatePluginLicenseResultRepresentation(messages, "debug");
    }

    public Collection<String> getMessages() {
        return this.messages;
    }

    public String getType() {
        return this.type;
    }
}

