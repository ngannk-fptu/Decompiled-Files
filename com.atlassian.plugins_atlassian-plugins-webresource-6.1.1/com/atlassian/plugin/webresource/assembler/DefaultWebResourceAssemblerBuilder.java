/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerBuilder
 *  com.atlassian.webresource.api.prebake.Coordinate
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.plugin.webresource.assembler.DefaultWebResourceAssembler;
import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.prebake.PrebakeWebResourceAssembler;
import com.atlassian.plugin.webresource.prebake.PrebakeWebResourceAssemblerBuilder;
import com.atlassian.plugin.webresource.util.TimeSpan;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerBuilder;
import com.atlassian.webresource.api.prebake.Coordinate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class DefaultWebResourceAssemblerBuilder
implements PrebakeWebResourceAssemblerBuilder {
    private final Globals globals;
    private Optional<Boolean> isSuperBatchingEnabled = Optional.empty();
    private Optional<Boolean> isSyncBatchingEnabled = Optional.empty();
    private Optional<Boolean> isAutoIncludeFrontendRuntimeEnabled = Optional.empty();
    private Optional<TimeSpan> deadline = Optional.empty();
    private Optional<Coordinate> coord = Optional.empty();

    public DefaultWebResourceAssemblerBuilder(Globals globals) {
        this.globals = globals;
    }

    @Override
    public PrebakeWebResourceAssemblerBuilder withCoordinate(Coordinate coord) {
        this.coord = Optional.of(coord);
        return this;
    }

    @Override
    public PrebakeWebResourceAssembler build() {
        UrlBuildingStrategy urlStrat = UrlBuildingStrategy.from(this.coord);
        boolean autoIncludeFrontendRuntime = this.isAutoIncludeFrontendRuntimeEnabled.orElse(true);
        RequestState requestState = new RequestState(this.globals, urlStrat, autoIncludeFrontendRuntime);
        DefaultWebResourceAssembler assembler = new DefaultWebResourceAssembler(requestState, this.globals);
        this.deadline.ifPresent(timeSpan -> requestState.setBigPipeDeadline(System.currentTimeMillis() + timeSpan.toMillis()));
        this.isSyncBatchingEnabled.ifPresent(requestState::setSyncbatchEnabled);
        this.isSuperBatchingEnabled.ifPresent(requestState.getSuperbatchConfiguration()::setEnabled);
        return assembler;
    }

    @Deprecated
    public WebResourceAssemblerBuilder includeSuperbatchResources(boolean include) {
        this.isSuperBatchingEnabled = Optional.of(include);
        return this;
    }

    public WebResourceAssemblerBuilder includeSyncbatchResources(boolean include) {
        this.isSyncBatchingEnabled = Optional.of(include);
        return this;
    }

    public WebResourceAssemblerBuilder autoIncludeFrontendRuntime(boolean include) {
        this.isAutoIncludeFrontendRuntimeEnabled = Optional.of(include);
        return this;
    }

    public WebResourceAssemblerBuilder asyncDataDeadline(long deadline, TimeUnit timeunit) {
        this.deadline = Optional.of(new TimeSpan(deadline, timeunit));
        return this;
    }
}

