/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.thready.manager;

import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public interface ThreadDiagnosticsConfigurationManager {
    public Configuration getConfiguration();

    public void setConfiguration(Configuration var1);

    public boolean isThreadNameAttributesEnabled();

    public static class Configuration
    implements Serializable {
        private final boolean enabled;

        @JsonCreator
        public Configuration(@JsonProperty(value="enabled") boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isEnabled() {
            return this.enabled;
        }
    }
}

