/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.app.reliability;

import java.io.Serializable;
import lombok.Generated;

public class NotReliableApp
implements Serializable {
    private static final long serialVersionUID = -5483472557881869087L;
    public final String key;
    public final String name;

    @Generated
    NotReliableApp(String key, String name) {
        this.key = key;
        this.name = name;
    }

    @Generated
    public static NotReliableAppBuilder builder() {
        return new NotReliableAppBuilder();
    }

    @Generated
    public static class NotReliableAppBuilder {
        @Generated
        private String key;
        @Generated
        private String name;

        @Generated
        NotReliableAppBuilder() {
        }

        @Generated
        public NotReliableAppBuilder key(String key) {
            this.key = key;
            return this;
        }

        @Generated
        public NotReliableAppBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Generated
        public NotReliableApp build() {
            return new NotReliableApp(this.key, this.name);
        }

        @Generated
        public String toString() {
            return "NotReliableApp.NotReliableAppBuilder(key=" + this.key + ", name=" + this.name + ")";
        }
    }
}

