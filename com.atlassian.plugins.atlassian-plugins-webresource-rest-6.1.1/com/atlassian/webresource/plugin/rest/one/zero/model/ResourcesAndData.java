/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.PluginCssResource
 *  com.atlassian.webresource.api.assembler.resource.PluginJsResource
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonPropertyOrder
 */
package com.atlassian.webresource.plugin.rest.one.zero.model;

import com.atlassian.webresource.api.assembler.resource.PluginCssResource;
import com.atlassian.webresource.api.assembler.resource.PluginJsResource;
import com.atlassian.webresource.plugin.async.model.ResourceTypeAndUrl;
import com.atlassian.webresource.plugin.rest.one.zero.model.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonPropertyOrder(alphabetic=true)
public class ResourcesAndData {
    @Nonnull
    public final List<Resource> resources;
    @Nonnull
    public final Map<String, String> unparsedData;
    @Nonnull
    public final Map<String, String> unparsedErrors;

    public ResourcesAndData(@Nonnull com.atlassian.webresource.plugin.async.model.ResourcesAndData resourcesAndData) {
        ResourcesAndData convertedResourcesAndData = ResourcesAndData.convert(resourcesAndData);
        this.resources = convertedResourcesAndData.resources;
        this.unparsedData = convertedResourcesAndData.unparsedData;
        this.unparsedErrors = convertedResourcesAndData.unparsedErrors;
    }

    @JsonCreator
    public ResourcesAndData(@Nonnull @JsonProperty(value="resources") List<Resource> resources, @Nonnull @JsonProperty(value="unparsedData") Map<String, String> unparsedData, @Nonnull @JsonProperty(value="unparsedErrors") Map<String, String> unparsedErrors) {
        this.resources = resources;
        this.unparsedData = new HashMap<String, String>(unparsedData);
        this.unparsedErrors = new HashMap<String, String>(unparsedErrors);
    }

    @Nonnull
    @JsonProperty(value="resources")
    public List<Resource> getResources() {
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

    private static ResourcesAndData convert(com.atlassian.webresource.plugin.async.model.ResourcesAndData resourcesAndData) {
        Iterable resources = Stream.of(resourcesAndData.getRequireResources(), resourcesAndData.getResourcesForInteration()).flatMap(Collection::stream).map(ResourceTypeAndUrl::getPluginUrlResource).filter(resource -> resource instanceof PluginJsResource || resource instanceof PluginCssResource).map(webResource -> {
            if (webResource instanceof PluginJsResource) {
                return new Resource((PluginJsResource)webResource);
            }
            return new Resource((PluginCssResource)webResource);
        }).collect(Collectors.toCollection(ArrayList::new));
        Map<String, String> unparsedData = Stream.of(resourcesAndData.getRequiredResourcesUnparsedData(), resourcesAndData.getResourcesForInterationUnparsedData()).flatMap(data -> data.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<String, String> unparsedErrors = Stream.of(resourcesAndData.getRequireResourcesUnparsedErrors(), resourcesAndData.getResourcesForInterationUnparsedErrors()).flatMap(data -> data.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new ResourcesAndData(StreamSupport.stream(resources.spliterator(), false).collect(Collectors.toList()), unparsedData, unparsedErrors);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ResourcesAndData) {
            ResourcesAndData otherResourcesAndData = (ResourcesAndData)other;
            return Objects.equals(this.resources, otherResourcesAndData.resources) && Objects.equals(this.unparsedData, otherResourcesAndData.unparsedData) && Objects.equals(this.unparsedErrors, otherResourcesAndData.unparsedErrors);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.resources, this.unparsedData, this.unparsedErrors);
    }

    public String toString() {
        return "ResourcesAndData{resources=" + this.resources + ", unparsedData=" + this.unparsedData + ", unparsedErrors=" + this.unparsedErrors + '}';
    }
}

