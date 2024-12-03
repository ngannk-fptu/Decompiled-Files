/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.webresource.impl;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.webresource.assembler.DefaultWebResourceSet;
import com.atlassian.plugin.webresource.bigpipe.BigPipe;
import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.SuperbatchConfiguration;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.Snapshot;
import com.atlassian.plugin.webresource.models.RawRequest;
import com.atlassian.plugin.webresource.models.Requestable;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class RequestState {
    private final BigPipe bigPipe;
    private final Globals globals;
    private final RawRequest rawRequest;
    private final RequestCache requestCache;
    private final UrlBuildingStrategy urlBuildingStrategy;
    private volatile long bigPipDeadline;
    private final SuperbatchConfiguration superbatchConfiguration;
    private boolean syncbatchEnabled = true;
    private final boolean autoIncludeFrontendRuntime;
    private final Set<String> excludedResolvedCache;

    public RequestState(Globals globals, UrlBuildingStrategy urlBuildingStrategy) {
        this(globals, urlBuildingStrategy, true);
    }

    public RequestState(Globals globals, UrlBuildingStrategy urlBuildingStrategy, boolean autoIncludeFrontendRuntime) {
        this(new RequestCache(globals), globals, urlBuildingStrategy, new RawRequest(), System.currentTimeMillis() + globals.getConfig().getDefaultBigPipeDeadline().toMillis(), new SuperbatchConfiguration(globals.getConfig().isSuperBatchingEnabled()), globals.getConfig().isSyncBatchingEnabled(), autoIncludeFrontendRuntime);
    }

    public RequestState(RequestState other) {
        this(other.requestCache, other.globals, other.urlBuildingStrategy, other.rawRequest, other.bigPipDeadline, other.superbatchConfiguration, other.syncbatchEnabled, other.autoIncludeFrontendRuntime);
    }

    private RequestState(RequestCache requestCache, Globals globals, UrlBuildingStrategy urlBuildingStrategy, RawRequest rawRequest, long bigPipDeadline, SuperbatchConfiguration superbatchConfiguration, boolean syncbatchEnabled, boolean autoIncludeFrontendRuntime) {
        this.requestCache = requestCache;
        this.globals = globals;
        this.urlBuildingStrategy = urlBuildingStrategy;
        this.rawRequest = rawRequest.deepClone();
        this.bigPipDeadline = bigPipDeadline;
        this.bigPipe = new BigPipe();
        this.superbatchConfiguration = superbatchConfiguration;
        this.syncbatchEnabled = syncbatchEnabled;
        this.excludedResolvedCache = new HashSet<String>();
        this.autoIncludeFrontendRuntime = autoIncludeFrontendRuntime;
    }

    public void clearIncludedAndUpdateExcluded(@Nonnull ResourcePhase resourcePhase, @Nonnull Set<String> excludedResolved) {
        this.rawRequest.setPhaseCompleted(resourcePhase);
        this.excludedResolvedCache.addAll(excludedResolved);
    }

    @Nonnull
    public RequestState deepClone() {
        return new RequestState(this);
    }

    @Nonnull
    public BigPipe getBigPipe() {
        return this.bigPipe;
    }

    public long getBigPipeDeadline() {
        return this.bigPipDeadline;
    }

    public void setBigPipeDeadline(long deadline) {
        this.bigPipDeadline = deadline;
    }

    @Nonnull
    public RequestCache getRequestCache() {
        return this.requestCache;
    }

    @Nonnull
    @Deprecated
    public LinkedHashSet<String> getExcluded() {
        return this.rawRequest.getExcludedAsLooseType();
    }

    @Nonnull
    public Set<String> getExcludedData() {
        return this.rawRequest.getExcludedData();
    }

    @Nonnull
    @Deprecated
    public LinkedHashSet<String> getIncluded() {
        return this.rawRequest.getIncludedAsLooseType();
    }

    @Nonnull
    @Deprecated
    public LinkedHashSet<String> getIncluded(@Nonnull ResourcePhase phaseType) {
        return this.rawRequest.getIncluded(phaseType).stream().map(Requestable::toLooseType).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Nonnull
    public LinkedHashMap<String, Jsonable> getIncludedData() {
        return this.rawRequest.getIncludedData();
    }

    @Nonnull
    public LinkedHashMap<String, Jsonable> getIncludedData(@Nonnull ResourcePhase resourcePhase) {
        return this.rawRequest.getIncludedData(resourcePhase);
    }

    @Nonnull
    public Set<String> getExcludedResolved() {
        return ImmutableSet.copyOf(this.excludedResolvedCache);
    }

    @Nonnull
    public Globals getGlobals() {
        return this.globals;
    }

    public RawRequest getRawRequest() {
        return this.rawRequest;
    }

    @Nonnull
    public Snapshot getSnapshot() {
        return this.requestCache.getSnapshot();
    }

    public boolean isAutoIncludeFrontendRuntimeEnabled() {
        return this.autoIncludeFrontendRuntime;
    }

    @Deprecated
    @Nonnull
    public Optional<DefaultWebResourceSet> getSyncResourceSet() {
        return Optional.ofNullable(null);
    }

    @Deprecated
    public void setSyncResourceSet(@Nullable DefaultWebResourceSet set) {
        this.setSyncbatchEnabled(set != null);
    }

    public UrlBuildingStrategy getUrlStrategy() {
        return this.urlBuildingStrategy;
    }

    @Deprecated
    public void markSyncResourcesAsWritten() {
    }

    public SuperbatchConfiguration getSuperbatchConfiguration() {
        return this.superbatchConfiguration;
    }

    public void setSyncbatchEnabled(boolean enabled) {
        this.syncbatchEnabled = enabled;
    }

    public boolean isSyncbatchEnabled() {
        return this.syncbatchEnabled;
    }

    public String toString() {
        return '[' + StringUtils.join(this.rawRequest.getIncludedAsLooseType(), (String)", ") + "] - [" + StringUtils.join(this.rawRequest.getExcludedAsLooseType(), (String)", ") + ']';
    }
}

