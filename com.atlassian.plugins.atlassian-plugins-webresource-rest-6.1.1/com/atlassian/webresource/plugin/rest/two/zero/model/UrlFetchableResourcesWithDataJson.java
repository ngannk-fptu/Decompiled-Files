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

import com.atlassian.webresource.plugin.rest.two.zero.model.UrlFetchableResourceJson;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonPropertyOrder(alphabetic=true)
public class UrlFetchableResourcesWithDataJson {
    private final Collection<UrlFetchableResourceJson> resources;
    private final Map<String, String> unparsedData;
    private final Map<String, String> unparsedErrors;

    @JsonCreator
    public UrlFetchableResourcesWithDataJson(@Nonnull @JsonProperty(value="resources") Collection<? extends UrlFetchableResourceJson> resources, @Nonnull @JsonProperty(value="unparsedData") Map<String, String> unparsedData, @Nonnull @JsonProperty(value="unparsedErrors") Map<String, String> unparsedErrors) {
        Objects.requireNonNull(resources, "The resources and urls are mandatory.");
        Objects.requireNonNull(unparsedData, "The unparsed data is mandatory.");
        Objects.requireNonNull(unparsedErrors, "The unparsed error is mandatory.");
        this.resources = new ArrayList<UrlFetchableResourceJson>(resources);
        this.unparsedData = new HashMap<String, String>(unparsedData);
        this.unparsedErrors = new HashMap<String, String>(unparsedErrors);
    }

    @Nonnull
    @JsonProperty(value="resources")
    public Collection<UrlFetchableResourceJson> getResources() {
        return this.resources;
    }

    @Nonnull
    @JsonProperty(value="unparsedData")
    public Map<String, String> getUnparsedData() {
        return this.unparsedData;
    }

    @Nonnull
    @JsonProperty(value="unparsedErrors")
    public Map<String, String> getUnparsedErrors() {
        return this.unparsedErrors;
    }

    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject instanceof UrlFetchableResourcesWithDataJson) {
            UrlFetchableResourcesWithDataJson other = (UrlFetchableResourcesWithDataJson)otherObject;
            return this.resources.equals(other.resources) && this.unparsedData.equals(other.unparsedData) && this.unparsedErrors.equals(other.unparsedErrors);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.resources, this.unparsedData, this.unparsedErrors);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.JSON_STYLE);
    }
}

