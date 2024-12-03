/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.webresource.models.Requestable
 *  com.atlassian.plugin.webresource.models.WebResourceContextKey
 *  com.atlassian.plugin.webresource.models.WebResourceKey
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  javax.annotation.Nullable
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 */
package com.atlassian.webresource.plugin.rest.one.zero;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugin.webresource.models.Requestable;
import com.atlassian.plugin.webresource.models.WebResourceContextKey;
import com.atlassian.plugin.webresource.models.WebResourceKey;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import com.atlassian.webresource.plugin.async.AsyncWebResourceLoader;
import com.atlassian.webresource.plugin.rest.one.zero.model.ResolveResourcesJson;
import com.atlassian.webresource.plugin.rest.one.zero.model.ResourcesAndData;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@AnonymousAllowed
@OpenAPIDefinition(info=@Info(title="Web Resource Manager", version="1.0", description="This is a draft of the proposed APIs to support retrieving of all forms of resources."))
@Path(value="resources")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class PhasesUnawareResources {
    private final AsyncWebResourceLoader asyncWebResourceLoader;

    public PhasesUnawareResources(AsyncWebResourceLoader asyncWebResourceLoader) {
        this.asyncWebResourceLoader = asyncWebResourceLoader;
    }

    @VisibleForTesting
    protected static <T extends Requestable> Set<T> mapsStringsToRequestables(Collection<String> rawStrings, Function<String, T> constructor) {
        return rawStrings.stream().map(constructor).collect(Collectors.toSet());
    }

    @VisibleForTesting
    protected static <T extends Requestable> Map<ResourcePhase, Set<T>> withDefaultPhase(Set<T> requestables) {
        return requestables.isEmpty() ? Collections.emptyMap() : Collections.singletonMap(ResourcePhase.defaultPhase(), requestables);
    }

    @Deprecated
    @GET
    @Produces(value={"application/json"})
    @Operation(summary="Retrieve resolved resources", tags={"resources"})
    @ApiResponse(responseCode="200", description="Successful operation", content={@Content(schema=@Schema(implementation=ResourcesAndData.class))})
    public ResourcesAndData get(@Parameter(description="Comma-separated list of phase aware WebResources wanted. Phases are prepended, followed by by a semi-colon to separate it from the key of the WebResource", example="interactive;com.atlassian:web-resource-key-1,render;com.atlassian:web-resource-key-2", style=ParameterStyle.FORM, explode=Explode.FALSE) @QueryParam(value="r") String webResources, @Parameter(description="Comma-separated list of phase aware WebResourceContexts wanted. Phases are prepended, followed by by a semi-colon to separate it from the key of the WebResourceContext", example="interactive;com.atlassian:context-key-1,render;com.atlassian:context-key-2", style=ParameterStyle.FORM, explode=Explode.FALSE) @QueryParam(value="c") String contexts, @Parameter(description="Comma-separated list of WebResources not wanted.", example="com.atlassian:excluded-web-resource-key-1,com.atlassian:excluded-web-resource-key-2", style=ParameterStyle.FORM, explode=Explode.FALSE) @QueryParam(value="xr") String excludeResources, @Parameter(description="Comma-separated list of WebResourceContexts not wanted.", example="com.atlassian:excluded-context-key-1,com.atlassian:excluded-context-key-2", style=ParameterStyle.FORM, explode=Explode.FALSE) @QueryParam(value="xc") String excludeContexts) throws IOException {
        return new ResourcesAndData(this.asyncWebResourceLoader.resolve(PhasesUnawareResources.withDefaultPhase(PhasesUnawareResources.mapsStringsToRequestables(PhasesUnawareResources.splitQueryParam(webResources), WebResourceKey::new)), PhasesUnawareResources.withDefaultPhase(PhasesUnawareResources.mapsStringsToRequestables(PhasesUnawareResources.splitQueryParam(contexts), WebResourceContextKey::new)), PhasesUnawareResources.mapsStringsToRequestables(PhasesUnawareResources.splitQueryParam(excludeResources), WebResourceKey::new), PhasesUnawareResources.mapsStringsToRequestables(PhasesUnawareResources.splitQueryParam(excludeContexts), WebResourceContextKey::new)));
    }

    @POST
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    @Operation(summary="Retrieve resolved resources", tags={"resources"})
    @ApiResponse(responseCode="200", description="Successful operation", content={@Content(schema=@Schema(implementation=ResourcesAndData.class))})
    public ResourcesAndData post(@Parameter(required=true) ResolveResourcesJson request) throws IOException {
        return new ResourcesAndData(this.asyncWebResourceLoader.resolve(PhasesUnawareResources.withDefaultPhase(PhasesUnawareResources.mapsStringsToRequestables(request.getResources(), WebResourceKey::new)), PhasesUnawareResources.withDefaultPhase(PhasesUnawareResources.mapsStringsToRequestables(request.getContexts(), WebResourceContextKey::new)), PhasesUnawareResources.mapsStringsToRequestables(request.getExcludeResources(), WebResourceKey::new), PhasesUnawareResources.mapsStringsToRequestables(request.getExcludeContexts(), WebResourceContextKey::new)));
    }

    private static List<String> splitQueryParam(@Nullable String queryParam) {
        return queryParam == null || queryParam.isEmpty() ? Collections.emptyList() : Arrays.asList(queryParam.split(","));
    }
}

