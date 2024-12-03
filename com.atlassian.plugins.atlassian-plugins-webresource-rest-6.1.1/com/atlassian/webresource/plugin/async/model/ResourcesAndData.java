/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.webresource.api.assembler.WebResource
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  com.atlassian.webresource.api.data.PluginDataResource
 *  javax.annotation.Nonnull
 */
package com.atlassian.webresource.plugin.async.model;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.webresource.api.assembler.WebResource;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import com.atlassian.webresource.api.data.PluginDataResource;
import com.atlassian.webresource.plugin.async.model.OutputShape;
import com.atlassian.webresource.plugin.async.model.ResourceTypeAndUrl;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;

public class ResourcesAndData {
    private final OutputShape require;
    private final OutputShape interaction;

    public ResourcesAndData(@Nonnull OutputShape require, @Nonnull OutputShape interaction) {
        this.require = Objects.requireNonNull(require, "The required resources are mandatory.");
        this.interaction = Objects.requireNonNull(interaction, "The resources for interaction are mandatory.");
    }

    public ResourcesAndData(@Nonnull Iterable<WebResource> resources) {
        this(ResourcesAndData.buildOutputShape(resources, ResourcePhase.REQUIRE), ResourcesAndData.buildOutputShape(resources, ResourcePhase.INTERACTION));
    }

    @Nonnull
    public OutputShape getRequire() {
        return this.require;
    }

    @Nonnull
    public Collection<ResourceTypeAndUrl> getRequireResources() {
        return new ArrayList<ResourceTypeAndUrl>(this.require.getResources());
    }

    @Nonnull
    public Map<String, String> getRequiredResourcesUnparsedData() {
        return new HashMap<String, String>(this.require.getUnparsedData());
    }

    @Nonnull
    public Map<String, String> getRequireResourcesUnparsedErrors() {
        return new HashMap<String, String>(this.require.getUnparsedErrors());
    }

    @Nonnull
    public Collection<ResourceTypeAndUrl> getResourcesForInteration() {
        return new ArrayList<ResourceTypeAndUrl>(this.interaction.getResources());
    }

    @Nonnull
    public Map<String, String> getResourcesForInterationUnparsedData() {
        return new HashMap<String, String>(this.interaction.getUnparsedData());
    }

    @Nonnull
    public Map<String, String> getResourcesForInterationUnparsedErrors() {
        return new HashMap<String, String>(this.interaction.getUnparsedErrors());
    }

    @Nonnull
    public OutputShape getInteraction() {
        return this.interaction;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ResourcesAndData) {
            ResourcesAndData otherResourcesAndData = (ResourcesAndData)other;
            return this.require.equals(otherResourcesAndData.require) && this.interaction.equals(otherResourcesAndData.interaction);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.require, this.interaction);
    }

    public String toString() {
        return "ResourcesAndData{ requirePhase=" + this.require + ", interactionPhase=" + this.interaction + " }";
    }

    public void merge(@Nonnull Iterable<WebResource> resources) {
        Objects.requireNonNull(resources, "The resorces are mandatory for the merge action.");
        this.merge(new ResourcesAndData(resources));
    }

    private void merge(ResourcesAndData resourcesAndData) {
        Objects.requireNonNull(resourcesAndData, "The resorces and data are mandatory for the merge action.");
        this.require.merge(resourcesAndData.require);
        this.interaction.merge(resourcesAndData.interaction);
    }

    private static OutputShape buildOutputShape(Iterable<WebResource> resources, ResourcePhase resourcePhase) {
        Objects.requireNonNull(resources, "The resources are mandatory to perform the conversion to OutputShape.");
        Map<String, String> data = StreamSupport.stream(resources.spliterator(), false).filter(resource -> resourcePhase == resource.getResourcePhase()).filter(PluginDataResource.class::isInstance).map(PluginDataResource.class::cast).filter(resource -> resource.getData().isPresent()).collect(Collectors.toMap(PluginDataResource::getKey, resource -> ResourcesAndData.jsonToString(resource.getJsonable())));
        Map<String, String> errors = StreamSupport.stream(resources.spliterator(), false).filter(resource -> resourcePhase == resource.getResourcePhase()).filter(PluginDataResource.class::isInstance).map(PluginDataResource.class::cast).filter(resource -> !resource.getData().isPresent()).collect(Collectors.toMap(PluginDataResource::getKey, resource -> ResourcesAndData.jsonToString(resource.getJsonable())));
        Collection resourcesTypesAndUrls = StreamSupport.stream(resources.spliterator(), false).filter(resource -> resourcePhase == resource.getResourcePhase()).filter(PluginUrlResource.class::isInstance).map(PluginUrlResource.class::cast).map(ResourceTypeAndUrl::new).collect(Collectors.toCollection(ArrayList::new));
        return new OutputShape(resourcesTypesAndUrls, data, errors);
    }

    private static String jsonToString(Jsonable jsonable) {
        try {
            StringWriter out = new StringWriter();
            jsonable.write((Writer)out);
            return out.toString();
        }
        catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }
}

