/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.jfr.domain;

import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class JfrSettings {
    @JsonProperty
    private final boolean enabled;

    @JsonCreator
    public JfrSettings(@JsonProperty(value="enabled") String enabled) {
        this.enabled = Boolean.parseBoolean(enabled);
    }

    public JfrSettings(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        JfrSettings that = (JfrSettings)other;
        return this.enabled == that.enabled;
    }

    public int hashCode() {
        return Objects.hash(this.enabled);
    }
}

