/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.app.consent;

import java.io.Serializable;
import lombok.Generated;

public class NotConsentedApp
implements Serializable {
    private static final long serialVersionUID = -619727760354202163L;
    public final String key;
    public final String name;

    @Generated
    NotConsentedApp(String key, String name) {
        this.key = key;
        this.name = name;
    }

    @Generated
    public static NotConsentedAppBuilder builder() {
        return new NotConsentedAppBuilder();
    }

    @Generated
    public static class NotConsentedAppBuilder {
        @Generated
        private String key;
        @Generated
        private String name;

        @Generated
        NotConsentedAppBuilder() {
        }

        @Generated
        public NotConsentedAppBuilder key(String key) {
            this.key = key;
            return this;
        }

        @Generated
        public NotConsentedAppBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Generated
        public NotConsentedApp build() {
            return new NotConsentedApp(this.key, this.name);
        }

        @Generated
        public String toString() {
            return "NotConsentedApp.NotConsentedAppBuilder(key=" + this.key + ", name=" + this.name + ")";
        }
    }
}

