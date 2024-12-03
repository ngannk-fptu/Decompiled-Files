/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public final class UserSettingsRepresentation {
    @JsonProperty
    private final boolean emailDisabled;

    @JsonCreator
    public UserSettingsRepresentation(@JsonProperty(value="emailDisabled") boolean emailDisabled) {
        this.emailDisabled = emailDisabled;
    }

    public boolean isEmailDisabled() {
        return this.emailDisabled;
    }

    public static enum Settings {
        EMAIL_DISABLED("emailDisabled");

        private String key;

        private Settings(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }
}

