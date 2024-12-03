/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cluster.monitoring.spi.ClusterMonitoring
 *  com.atlassian.cluster.monitoring.spi.model.NodeIdentifier
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.google.common.collect.ImmutableMap
 *  com.sun.jersey.spi.container.ResourceFilters
 *  io.atlassian.fugue.Either
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cluster.monitoring.rest;

import com.atlassian.cluster.monitoring.descriptor.MonitoringDataSupplierModuleDescriptor;
import com.atlassian.cluster.monitoring.rest.DataSupplier;
import com.atlassian.cluster.monitoring.spi.ClusterMonitoring;
import com.atlassian.cluster.monitoring.spi.model.NodeIdentifier;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.spi.container.ResourceFilters;
import io.atlassian.fugue.Either;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="cluster")
@WebSudoRequired
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@ResourceFilters(value={SysadminOnlyResourceFilter.class})
public class ClusterMonitoringResource {
    private static final Logger log = LoggerFactory.getLogger(ClusterMonitoringResource.class);
    private static final String SERVER_ERROR_MESSAGE = "com.atlassian.monitoring.error";
    private final ClusterMonitoring clusterMonitoring;
    private final PluginAccessor pluginAccessor;
    private final I18nResolver i18nResolver;

    public ClusterMonitoringResource(@ComponentImport ClusterMonitoring clusterMonitoring, @ComponentImport PluginAccessor pluginAccessor, @ComponentImport I18nResolver i18nResolver) {
        this.clusterMonitoring = Objects.requireNonNull(clusterMonitoring);
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
        this.i18nResolver = Objects.requireNonNull(i18nResolver);
    }

    @GET
    @Path(value="nodes")
    public Response getNodes() {
        return ClusterMonitoringResource.convertEitherToResponse(this.clusterMonitoring.getNodes());
    }

    @PUT
    @Path(value="enable-clustering")
    public Response enableClustering() {
        return Response.ok((Object)this.clusterMonitoring.enableClustering()).build();
    }

    @GET
    @Path(value="current-node")
    public Response getCurrentNode() {
        return ClusterMonitoringResource.convertEitherToResponse(this.clusterMonitoring.getCurrentNode());
    }

    @GET
    @Path(value="suppliers")
    public Collection<DataSupplier> getDataSuppliers() {
        return this.monitoringModuleDescriptors().stream().map(this.asDataSupplier()).collect(Collectors.toList());
    }

    @GET
    @Path(value="suppliers/data/{pluginKey}/{moduleKey}/{nodeId}")
    public Response getDataProviderInformationForNode(@PathParam(value="pluginKey") String pluginKey, @PathParam(value="moduleKey") String moduleKey, @PathParam(value="nodeId") NodeIdentifier nodeId) {
        ModuleCompleteKey key = new ModuleCompleteKey(pluginKey, moduleKey);
        Either eitherData = this.clusterMonitoring.getData(key, nodeId);
        if (eitherData.isLeft()) {
            log.warn("Error received when querying remote node [{}]: ", (Object)nodeId, eitherData.left().get());
            String notScaryError = this.i18nResolver.getText(SERVER_ERROR_MESSAGE, new Serializable[]{nodeId});
            return Response.serverError().entity((Object)notScaryError).build();
        }
        MonitoringDataSupplierModuleDescriptor moduleDescriptor = (MonitoringDataSupplierModuleDescriptor)this.pluginAccessor.getEnabledPluginModule(key.getCompleteKey());
        DataSupplier dataSupplier = this.asDataSupplier().apply(moduleDescriptor);
        return Response.ok((Object)ImmutableMap.of((Object)"supplier", (Object)dataSupplier, (Object)"data", (Object)eitherData.right().get())).build();
    }

    private List<MonitoringDataSupplierModuleDescriptor> monitoringModuleDescriptors() {
        return this.pluginAccessor.getEnabledModuleDescriptorsByClass(MonitoringDataSupplierModuleDescriptor.class).stream().sorted((o1, o2) -> o2.getPriority() - o1.getPriority()).collect(Collectors.toList());
    }

    private Function<MonitoringDataSupplierModuleDescriptor, DataSupplier> asDataSupplier() {
        return descriptor -> {
            String i18nKey = descriptor.getI18nNameKey();
            String i18nLabel = this.i18nResolver.getText(i18nKey);
            return new DataSupplier(new ModuleCompleteKey(descriptor.getCompleteKey()), i18nKey, i18nLabel);
        };
    }

    static Response convertEitherToResponse(Either either) {
        return either.isLeft() ? Response.serverError().entity(either.left().get()).build() : Response.ok((Object)either.right().get()).build();
    }
}

