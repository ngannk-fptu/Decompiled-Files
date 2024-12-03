/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class VendorFeedbackRepresentation {
    @JsonProperty
    private final String reasonCode;
    @JsonProperty
    private final String message;
    @JsonProperty
    private final String type;
    @JsonProperty
    private final String pluginVersion;
    @JsonProperty
    private final String email;
    @JsonProperty
    private final String fullName;
    public static final String DISABLE_TYPE = "disable";
    public static final String UNINSTALL_TYPE = "uninstall";

    @JsonCreator
    public VendorFeedbackRepresentation(@JsonProperty(value="reasonCode") String reasonCode, @JsonProperty(value="message") String message, @JsonProperty(value="type") String type, @JsonProperty(value="pluginVersion") String pluginVersion, @JsonProperty(value="email") String email, @JsonProperty(value="fullName") String fullName) {
        this.reasonCode = Objects.requireNonNull(reasonCode);
        this.message = Objects.requireNonNull(message);
        this.type = Objects.requireNonNull(type);
        this.pluginVersion = Objects.requireNonNull(pluginVersion);
        this.fullName = fullName;
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public String getReasonCode() {
        return this.reasonCode;
    }

    public String getMessage() {
        return this.message;
    }

    public String getType() {
        return this.type;
    }

    public String getPluginVersion() {
        return this.pluginVersion;
    }

    public String getFullName() {
        return this.fullName;
    }
}

