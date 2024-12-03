/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webresource.plugin.async.model;

import com.atlassian.webresource.plugin.async.model.ResourceTypeAndUrl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class OutputShape {
    private final Collection<ResourceTypeAndUrl> resources;
    private final Map<String, String> unparsedData;
    private final Map<String, String> unparsedErrors;

    public OutputShape(@Nonnull Collection<? extends ResourceTypeAndUrl> resources, @Nonnull Map<String, String> unparsedData, @Nonnull Map<String, String> unparsedErrors) {
        Objects.requireNonNull(resources, "The resources and urls are mandatory.");
        Objects.requireNonNull(unparsedData, "The unparsed data is mandatory.");
        Objects.requireNonNull(unparsedErrors, "The unparse error is mandatory.");
        this.resources = new ArrayList<ResourceTypeAndUrl>(resources);
        this.unparsedData = new HashMap<String, String>(unparsedData);
        this.unparsedErrors = new HashMap<String, String>(unparsedErrors);
    }

    @Nonnull
    public Collection<ResourceTypeAndUrl> getResources() {
        return new ArrayList<ResourceTypeAndUrl>(this.resources);
    }

    @Nonnull
    public Map<String, String> getUnparsedData() {
        return new HashMap<String, String>(this.unparsedData);
    }

    @Nonnull
    public Map<String, String> getUnparsedErrors() {
        return new HashMap<String, String>(this.unparsedErrors);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof OutputShape) {
            OutputShape otherOutputShape = (OutputShape)other;
            return this.resources.equals(otherOutputShape.resources) && this.unparsedData.equals(otherOutputShape.unparsedData) && this.unparsedErrors.equals(otherOutputShape.unparsedErrors);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.resources, this.unparsedData, this.unparsedErrors);
    }

    public String toString() {
        return "OutputShape{resources=" + this.resources.stream().map(ResourceTypeAndUrl::getKey).collect(Collectors.joining("|")) + ", unparsedData=" + this.unparsedData.keySet() + ", unparsedErrors=" + this.unparsedErrors.keySet() + '}';
    }

    public void merge(@Nonnull OutputShape outputShape) {
        this.resources.addAll(outputShape.resources);
        this.unparsedData.putAll(outputShape.unparsedData);
        this.unparsedErrors.putAll(outputShape.unparsedErrors);
    }
}

