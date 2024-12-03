/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.plugin.webresource.models;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.webresource.models.Requestable;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.builder.ToStringBuilder;

public final class RawRequest {
    private static final String RESOURCE_PHASE_TYPE_MANDATORY_MESSAGE = "The resource resourcePhase type is mandatory for the resource inclusion.";
    private final Map<ResourcePhase, Deque<Requestable>> includedResourcesByType;
    private final Map<ResourcePhase, LinkedHashMap<String, Jsonable>> includedDataByType;
    private final LinkedHashSet<Requestable> excludedResources;
    private final Set<String> excludedData;

    public RawRequest() {
        this.excludedResources = new LinkedHashSet();
        this.excludedData = new LinkedHashSet<String>();
        this.includedResourcesByType = new EnumMap<ResourcePhase, Deque<Requestable>>(ResourcePhase.class);
        this.includedDataByType = new EnumMap<ResourcePhase, LinkedHashMap<String, Jsonable>>(ResourcePhase.class);
        Arrays.stream(ResourcePhase.values()).forEach(type -> {
            Deque cfr_ignored_0 = this.includedResourcesByType.put((ResourcePhase)type, new ArrayDeque());
        });
        Arrays.stream(ResourcePhase.values()).forEach(type -> this.includedDataByType.put((ResourcePhase)type, new LinkedHashMap()));
    }

    private RawRequest(@Nonnull RawRequest other) {
        Objects.requireNonNull(other, "The raw request is mandatory for the cloning.");
        this.excludedResources = new LinkedHashSet<Requestable>(other.excludedResources);
        this.excludedData = new LinkedHashSet<String>(other.excludedData);
        this.includedResourcesByType = other.includedResourcesByType.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new ArrayDeque((Collection)entry.getValue())));
        this.includedDataByType = other.includedDataByType.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new LinkedHashMap((Map)entry.getValue())));
    }

    public boolean hasAny(ResourcePhase phase) {
        return !this.includedResourcesByType.get(phase).isEmpty() || !this.includedDataByType.get(phase).isEmpty();
    }

    public void clearExcluded() {
        this.excludedResources.clear();
    }

    public void setPhaseCompleted(@Nonnull ResourcePhase resourcePhase) {
        Deque<Requestable> alreadyIncludedResources = this.includedResourcesByType.get(resourcePhase);
        this.exclude(alreadyIncludedResources);
        alreadyIncludedResources.clear();
        LinkedHashMap<String, Jsonable> alreadyIncludedData = this.includedDataByType.get(resourcePhase);
        this.excludedData.addAll(alreadyIncludedData.keySet());
        alreadyIncludedData.clear();
    }

    @Nonnull
    public RawRequest deepClone() {
        return new RawRequest(this);
    }

    public boolean exclude(@Nonnull Requestable requestableToExclude) {
        Objects.requireNonNull(requestableToExclude, "The requestable resource is mandatory for the exclusion.");
        return this.excludedResources.add(requestableToExclude);
    }

    public void exclude(@Nullable Collection<Requestable> resources) {
        Optional.ofNullable(resources).orElseGet(LinkedHashSet::new).forEach(this::exclude);
    }

    public boolean include(@Nonnull Requestable resource) {
        return this.include(ResourcePhase.defaultPhase(), resource);
    }

    public void include(@Nullable Collection<Requestable> resources) {
        Optional.ofNullable(resources).orElseGet(LinkedHashSet::new).forEach(this::include);
    }

    public void include(@Nonnull ResourcePhase resourcePhase, @Nullable Collection<Requestable> resources) {
        Optional.ofNullable(resources).orElseGet(LinkedHashSet::new).forEach(resource -> this.include(resourcePhase, (Requestable)resource));
    }

    public boolean include(@Nonnull RawRequest request) {
        return this.include(ResourcePhase.defaultPhase(), request);
    }

    public boolean include(@Nonnull ResourcePhase resourcePhase, @Nonnull Requestable resource) {
        Objects.requireNonNull(resource, "The resourceToInclude resource is mandatory for the inclusion.");
        Objects.requireNonNull(resourcePhase, RESOURCE_PHASE_TYPE_MANDATORY_MESSAGE);
        return this.includedResourcesByType.get(resourcePhase).add(resource);
    }

    public boolean include(@Nonnull ResourcePhase resourcePhase, @Nonnull RawRequest requestToInclude) {
        Objects.requireNonNull(requestToInclude, "The request is mandatory for the inclusion.");
        Objects.requireNonNull(resourcePhase, RESOURCE_PHASE_TYPE_MANDATORY_MESSAGE);
        boolean wereIncludedResourcesAdded = this.includedResourcesByType.get(resourcePhase).addAll(requestToInclude.getIncluded());
        boolean wereExcludedResourcesAdded = this.excludedResources.addAll(requestToInclude.getExcluded());
        return wereIncludedResourcesAdded && wereExcludedResourcesAdded;
    }

    public void includeFirst(@Nonnull ResourcePhase resourcePhase, @Nonnull Requestable resource) {
        Objects.requireNonNull(resource, "The resourceToInclude resource is mandatory for the inclusion.");
        Objects.requireNonNull(resourcePhase, RESOURCE_PHASE_TYPE_MANDATORY_MESSAGE);
        this.includedResourcesByType.get(resourcePhase).addFirst(resource);
    }

    public void includeFirst(@Nonnull Requestable resource) {
        this.includeFirst(ResourcePhase.defaultPhase(), resource);
    }

    @Nonnull
    public LinkedHashSet<Requestable> getIncluded() {
        return this.includedResourcesByType.values().stream().flatMap(Collection::stream).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Nonnull
    @Deprecated
    public LinkedHashSet<String> getIncludedAsLooseType() {
        return this.includedResourcesByType.values().stream().flatMap(Collection::stream).map(Requestable::toLooseType).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Nonnull
    public LinkedHashSet<Requestable> getIncluded(@Nonnull ResourcePhase phaseType) {
        Objects.requireNonNull(phaseType, "The resource phase type is mandatory to retrieve the resources included.");
        return new LinkedHashSet<Requestable>((Collection)this.includedResourcesByType.get(phaseType));
    }

    @Nonnull
    public LinkedHashMap<String, Jsonable> getIncludedData(@Nonnull ResourcePhase phaseType) {
        Objects.requireNonNull(phaseType, "The resource phase type is mandatory to retrieve the resources data included.");
        return this.includedDataByType.get(phaseType);
    }

    @Nonnull
    public LinkedHashMap<String, Jsonable> getIncludedData() {
        return this.includedDataByType.values().stream().flatMap(data -> data.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (data1, data2) -> data1, LinkedHashMap::new));
    }

    @Nonnull
    public LinkedHashSet<Requestable> getExcluded() {
        return new LinkedHashSet<Requestable>(this.excludedResources);
    }

    @Nonnull
    @Deprecated
    public LinkedHashSet<String> getExcludedAsLooseType() {
        return this.excludedResources.stream().map(Requestable::toLooseType).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Nonnull
    public Set<String> getExcludedData() {
        return this.excludedData;
    }

    @Deprecated
    public void removeExcluded(@Nonnull Requestable resource) {
        this.excludedResources.remove(resource);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }
}

