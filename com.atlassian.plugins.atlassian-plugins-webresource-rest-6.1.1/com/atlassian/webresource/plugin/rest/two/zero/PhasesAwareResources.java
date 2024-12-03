/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.models.WebResourceContextKey
 *  com.atlassian.plugin.webresource.models.WebResourceKey
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  javax.annotation.Nonnull
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 */
package com.atlassian.webresource.plugin.rest.two.zero;

import com.atlassian.plugin.webresource.models.WebResourceContextKey;
import com.atlassian.plugin.webresource.models.WebResourceKey;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import com.atlassian.webresource.plugin.async.AsyncWebResourceLoader;
import com.atlassian.webresource.plugin.async.model.ResourcesAndData;
import com.atlassian.webresource.plugin.rest.two.zero.model.PhasesAwareRequestJson;
import com.atlassian.webresource.plugin.rest.two.zero.model.PhasesAwareResourcesResponseJson;
import com.atlassian.webresource.plugin.rest.two.zero.util.PhasesAwareResourcesModelMapperUtil;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@AnonymousAllowed
@OpenAPIDefinition(info=@Info(title="Web Resource Manager", version="2.0", description="REST API for retrieving web resources with phases"))
@Path(value="resources")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
public class PhasesAwareResources {
    private final AsyncWebResourceLoader asyncWebResourceLoader;

    public PhasesAwareResources(@Nonnull AsyncWebResourceLoader asyncWebResourceLoader) {
        this.asyncWebResourceLoader = asyncWebResourceLoader;
    }

    @POST
    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    @Operation(summary="Retrieve resolved resources", tags={"resources"})
    @ApiResponse(responseCode="200", description="Successful operation", content={@Content(schema=@Schema(implementation=PhasesAwareResourcesResponseJson.class))})
    public PhasesAwareResourcesResponseJson post(@Parameter(required=true) PhasesAwareRequestJson request) throws IOException {
        Map<ResourcePhase, Set<WebResourceKey>> phasesAwareIncludedWebResources = PhasesAwareResourcesModelMapperUtil.byPhase(PhasesAwareResourcesModelMapperUtil.transformStringsToWebResourceKeysSet(request.getRequire()), PhasesAwareResourcesModelMapperUtil.transformStringsToWebResourceKeysSet(request.getRequireForInteraction()));
        Map<ResourcePhase, Set<WebResourceContextKey>> phasesAwareIncludedWebResourceContexts = PhasesAwareResourcesModelMapperUtil.byPhase(PhasesAwareResourcesModelMapperUtil.transformStringsToWebResourceContextKeysSet(request.getRequire()), PhasesAwareResourcesModelMapperUtil.transformStringsToWebResourceContextKeysSet(request.getRequireForInteraction()));
        Set<WebResourceKey> excludeWebResources = PhasesAwareResourcesModelMapperUtil.transformStringsToWebResourceKeysSet(request.getExclude());
        Set<WebResourceContextKey> excludeWebResourceContexts = PhasesAwareResourcesModelMapperUtil.transformStringsToWebResourceContextKeysSet(request.getExclude());
        ResourcesAndData resolvedResourcesAndData = this.asyncWebResourceLoader.resolve(phasesAwareIncludedWebResources, phasesAwareIncludedWebResourceContexts, excludeWebResources, excludeWebResourceContexts);
        return new PhasesAwareResourcesResponseJson(PhasesAwareResourcesModelMapperUtil.transformOutputShapeToUrlFetchableResourcesWithDataJson(resolvedResourcesAndData.getInteraction()), PhasesAwareResourcesModelMapperUtil.transformOutputShapeToUrlFetchableResourcesWithDataJson(resolvedResourcesAndData.getRequire()));
    }
}

