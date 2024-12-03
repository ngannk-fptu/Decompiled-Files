/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonPropertyOrder
 */
package com.atlassian.webresource.plugin.rest.two.zero.model;

import com.atlassian.webresource.plugin.rest.two.zero.model.UrlFetchableResourcesWithDataJson;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonPropertyOrder(alphabetic=true)
public final class PhasesAwareResourcesResponseJson {
    private final UrlFetchableResourcesWithDataJson require;
    private final UrlFetchableResourcesWithDataJson interaction;

    @JsonCreator
    public PhasesAwareResourcesResponseJson(@Nonnull @JsonProperty(value="interaction") UrlFetchableResourcesWithDataJson interaction, @Nonnull @JsonProperty(value="require") UrlFetchableResourcesWithDataJson require) {
        this.require = Objects.requireNonNull(require, "The required resources are mandatory.");
        this.interaction = Objects.requireNonNull(interaction, "The resources for interaction are mandatory.");
    }

    @Nonnull
    @JsonProperty(value="require")
    public UrlFetchableResourcesWithDataJson getRequire() {
        return this.require;
    }

    @Nonnull
    @JsonProperty(value="interaction")
    public UrlFetchableResourcesWithDataJson getInteraction() {
        return this.interaction;
    }

    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject instanceof PhasesAwareResourcesResponseJson) {
            PhasesAwareResourcesResponseJson other = (PhasesAwareResourcesResponseJson)otherObject;
            return this.require.equals(other.require) && this.interaction.equals(other.interaction);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.require, this.interaction);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.JSON_STYLE);
    }
}

