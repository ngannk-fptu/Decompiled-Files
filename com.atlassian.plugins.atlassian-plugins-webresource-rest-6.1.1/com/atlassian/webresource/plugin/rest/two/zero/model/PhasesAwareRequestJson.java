/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonPropertyOrder
 */
package com.atlassian.webresource.plugin.rest.two.zero.model;

import com.atlassian.annotations.VisibleForTesting;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonPropertyOrder(alphabetic=true)
public class PhasesAwareRequestJson {
    private static final String RESOURCES_CANNOT_BE_NULL_MSG = " resource cannot be null";
    @VisibleForTesting
    public static final String REQUIRE_RESOURCES_NULL_MSG = "'require' phase resource cannot be null";
    @VisibleForTesting
    public static final String INTERACTION_RESOURCES_NULL_MSG = "'interaction' phase resource cannot be null";
    @VisibleForTesting
    public static final String EXCLUDE_RESOURCES_NULL_MSG = "'exclude' resource cannot be null";
    @Nonnull
    @JsonProperty(value="require")
    private final Collection<String> require;
    @Nonnull
    @JsonProperty(value="interaction")
    private final Collection<String> requireForInteraction;
    @Nonnull
    @JsonProperty(value="exclude")
    private final Collection<String> exclude;

    public PhasesAwareRequestJson() {
        this(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    @JsonCreator
    public PhasesAwareRequestJson(@JsonProperty(value="require") @Nonnull Collection<String> require, @JsonProperty(value="interaction") @Nonnull Collection<String> requireForInteraction, @JsonProperty(value="exclude") @Nonnull Collection<String> exclude) {
        this.require = Objects.requireNonNull(require, REQUIRE_RESOURCES_NULL_MSG);
        this.requireForInteraction = Objects.requireNonNull(requireForInteraction, INTERACTION_RESOURCES_NULL_MSG);
        this.exclude = Objects.requireNonNull(exclude, EXCLUDE_RESOURCES_NULL_MSG);
    }

    @Nonnull
    public Collection<String> getRequire() {
        return this.require;
    }

    @Nonnull
    public Collection<String> getRequireForInteraction() {
        return this.requireForInteraction;
    }

    @Nonnull
    public Collection<String> getExclude() {
        return this.exclude;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof PhasesAwareRequestJson) {
            PhasesAwareRequestJson otherPhasesAwareRequestJson = (PhasesAwareRequestJson)other;
            return Objects.equals(this.require, otherPhasesAwareRequestJson.require) && Objects.equals(this.requireForInteraction, otherPhasesAwareRequestJson.requireForInteraction) && Objects.equals(this.exclude, otherPhasesAwareRequestJson.exclude);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.require, this.requireForInteraction, this.exclude);
    }
}

