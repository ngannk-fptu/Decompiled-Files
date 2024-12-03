/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.vault.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class VaultMount {
    private final String type;
    @Nullable
    private final String description;
    private final Map<String, Object> config;
    private final Map<String, String> options;

    VaultMount(@JsonProperty(value="type") String type, @Nullable @JsonProperty(value="description") String description, @Nullable @JsonProperty(value="config") Map<String, Object> config, @Nullable @JsonProperty(value="options") Map<String, String> options) {
        this.type = type;
        this.description = description;
        this.config = config != null ? config : Collections.emptyMap();
        this.options = options != null ? options : Collections.emptyMap();
    }

    public static VaultMount create(String type) {
        return VaultMount.builder().type(type).build();
    }

    public static VaultMountBuilder builder() {
        return new VaultMountBuilder();
    }

    public String getType() {
        return this.type;
    }

    @Nullable
    public String getDescription() {
        return this.description;
    }

    @Nullable
    public Map<String, Object> getConfig() {
        return this.config;
    }

    @Nullable
    public Map<String, String> getOptions() {
        return this.options;
    }

    public static class VaultMountBuilder {
        @Nullable
        private String type;
        @Nullable
        private String description;
        private Map<String, Object> config = Collections.emptyMap();
        private Map<String, String> options = Collections.emptyMap();

        VaultMountBuilder() {
        }

        public VaultMountBuilder type(String type) {
            Assert.hasText((String)type, (String)"Type must not be empty or null");
            this.type = type;
            return this;
        }

        public VaultMountBuilder description(String description) {
            this.description = description;
            return this;
        }

        public VaultMountBuilder config(Map<String, Object> config) {
            Assert.notNull(config, (String)"Configuration map must not be null");
            this.config = config;
            return this;
        }

        public VaultMountBuilder options(Map<String, String> options) {
            Assert.notNull(options, (String)"Options map must not be null");
            this.options = options;
            return this;
        }

        public VaultMount build() {
            Assert.notNull((Object)this.type, (String)"Type must not be null");
            Assert.hasText((String)this.type, (String)"Type must not be empty or null");
            return new VaultMount(this.type, this.description, this.config, this.options);
        }
    }
}

