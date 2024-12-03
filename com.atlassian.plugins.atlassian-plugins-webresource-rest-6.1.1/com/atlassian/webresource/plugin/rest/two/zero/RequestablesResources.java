/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.webresource.plugin.rest.two.zero;

import com.atlassian.webresource.plugin.rest.two.zero.graph.RequestableGraph;
import com.atlassian.webresource.plugin.rest.two.zero.graph.RequestableGraphService;
import com.atlassian.webresource.plugin.rest.two.zero.model.ErrorResponseJson;
import com.atlassian.webresource.plugin.rest.two.zero.model.RequestableEdgeJson;
import com.atlassian.webresource.plugin.rest.two.zero.model.RequestableEdgeListResponseJson;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="requestables")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
public class RequestablesResources {
    private final RequestableGraphService requestableGraphService;

    @GET
    public Response getIndex() {
        return Response.ok().build();
    }

    public RequestablesResources(RequestableGraphService requestableGraphService) {
        this.requestableGraphService = requestableGraphService;
    }

    @Path(value="{requestable}/consumer-graph")
    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    @ApiResponses(value={@ApiResponse(responseCode="404", description="requestable not found"), @ApiResponse(responseCode="200", description="graph of all requestables that consume this one")})
    @GET
    public Response getSerializedConsumerGraph(@PathParam(value="requestable") String requestableId) {
        return this.responseOf(requestableId, this.requestableGraphService::getConsumerGraphById);
    }

    @Path(value="{requestable}/dependency-graph")
    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    @ApiResponses(value={@ApiResponse(responseCode="404", description="requestable not found"), @ApiResponse(responseCode="200", description="graph of all requestables that this one depends on")})
    @GET
    public Response getSerializedDependencyGraph(@PathParam(value="requestable") String requestableId) {
        return this.responseOf(requestableId, this.requestableGraphService::getDependencyGraphById);
    }

    private Response responseOf(String requestableId, Function<String, RequestableGraph> graphProducer) {
        if (this.requestableGraphService.hasById(requestableId)) {
            RequestableGraph graph = graphProducer.apply(requestableId);
            return Response.ok((Object)this.convertGraphEdgesToRequestableItems(graph)).build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)new ErrorResponseJson("Could not find `" + requestableId + "` in the graph.")).build();
    }

    private RequestableEdgeListResponseJson convertGraphEdgesToRequestableItems(RequestableGraph graph) {
        Collection items = StreamSupport.stream(graph.getEdges().spliterator(), false).map(edge -> new RequestableEdgeJson(edge.getSource(), edge.getTarget(), edge.getPhase())).collect(Collectors.toList());
        return new RequestableEdgeListResponseJson(items);
    }
}

