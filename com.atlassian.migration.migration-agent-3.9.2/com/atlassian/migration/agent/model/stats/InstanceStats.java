/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.model.stats;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ParametersAreNonnullByDefault
public final class InstanceStats {
    @JsonProperty
    private final String version;
    @JsonProperty
    private final String buildNumber;
    @JsonProperty
    private final int numberOfUsers;
    @JsonProperty
    private final int numberOfGroups;
    @JsonProperty
    private final int numberOfSpaces;

    @JsonCreator
    private InstanceStats(@JsonProperty(value="version") String version, @JsonProperty(value="buildNumber") String buildNumber, @JsonProperty(value="numberOfUsers") int numberOfUsers, @JsonProperty(value="numberOfGroups") int numberOfGroups, @JsonProperty(value="numberOfSpaces") int numberOfSpaces) {
        this.version = version;
        this.buildNumber = buildNumber;
        this.numberOfUsers = numberOfUsers;
        this.numberOfGroups = numberOfGroups;
        this.numberOfSpaces = numberOfSpaces;
    }

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    public String getVersion() {
        return this.version;
    }

    public String getBuildNumber() {
        return this.buildNumber;
    }

    public int getNumberOfUsers() {
        return this.numberOfUsers;
    }

    public int getNumberOfGroups() {
        return this.numberOfGroups;
    }

    public int getNumberOfSpaces() {
        return this.numberOfSpaces;
    }

    public static final class Builder {
        private String version;
        private String buildNumber;
        private int numberOfUsers;
        private int numberOfGroups;
        private int numberOfSpaces;

        private Builder() {
        }

        @Nonnull
        public Builder version(String version) {
            this.version = version;
            return this;
        }

        @Nonnull
        public Builder buildNumber(String buildNumber) {
            this.buildNumber = buildNumber;
            return this;
        }

        @Nonnull
        public Builder numberOfUsers(int numberOfUsers) {
            this.numberOfUsers = numberOfUsers;
            return this;
        }

        @Nonnull
        public Builder numberOfGroups(int numberOfGroups) {
            this.numberOfGroups = numberOfGroups;
            return this;
        }

        @Nonnull
        public Builder numberOfSpaces(int numberOfSpaces) {
            this.numberOfSpaces = numberOfSpaces;
            return this;
        }

        @Nonnull
        public InstanceStats build() {
            return new InstanceStats(this.version, this.buildNumber, this.numberOfUsers, this.numberOfGroups, this.numberOfSpaces);
        }
    }
}

