/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.jfr.domain;

import com.atlassian.troubleshooting.jfr.util.JfrConditionUtils;
import javax.annotation.Nullable;
import jdk.jfr.FlightRecorder;
import org.codehaus.jackson.annotate.JsonProperty;

public class JfrCapabilities {
    @JsonProperty
    private final boolean available = this.isJfrAvailable();
    @JsonProperty
    private final boolean initialized = this.isJfrInitialized();
    @JsonProperty
    private final boolean apiEnabled;
    @JsonProperty
    private final String nodeId;

    public JfrCapabilities(boolean apiEnabled, @Nullable String nodeId) {
        this.apiEnabled = apiEnabled;
        this.nodeId = nodeId;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public boolean isApiEnabled() {
        return this.apiEnabled;
    }

    private boolean isJfrInitialized() {
        try {
            return FlightRecorder.isInitialized();
        }
        catch (NoClassDefFoundError err) {
            return false;
        }
    }

    private boolean isJfrAvailable() {
        try {
            return FlightRecorder.isAvailable() && JfrConditionUtils.isJavaVersionSupported();
        }
        catch (NoClassDefFoundError err) {
            return false;
        }
    }

    @Nullable
    public String getNodeId() {
        return this.nodeId;
    }
}

