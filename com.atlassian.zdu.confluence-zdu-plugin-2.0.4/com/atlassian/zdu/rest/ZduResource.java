/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.sun.jersey.api.NotFoundException
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 */
package com.atlassian.zdu.rest;

import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.atlassian.zdu.api.ZduService;
import com.atlassian.zdu.rest.dto.Cluster;
import com.atlassian.zdu.rest.dto.ClusterStateResponse;
import com.atlassian.zdu.rest.dto.NodeInfoDTO;
import com.atlassian.zdu.rest.filter.IsClusteredFilter;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.spi.container.ResourceFilters;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@OpenAPIDefinition(info=@Info(title="Rolling Upgrades", description="Provides Rolling Upgrade capabilities.", version="1.0"), servers={@Server(url="/rest/zdu", description="The Zero Downtime/Rolling Upgrade REST resource for the server")})
@Path(value="/")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
@ResourceFilters(value={SysadminOnlyResourceFilter.class, IsClusteredFilter.class})
@Named
public class ZduResource {
    private final ZduService zduService;

    @Inject
    public ZduResource(ZduService zduService) {
        this.zduService = zduService;
    }

    @GET
    @Path(value="/cluster")
    @Operation(summary="Gets the Cluster overview", description="Gets an overview of a Cluster including its current state and composition of Nodes.", tags={"cluster"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Returns full JSON representation of the cluster", content={@Content(schema=@Schema(implementation=Cluster.class))}), @ApiResponse(responseCode="401", description="Returned if user is not authenticated"), @ApiResponse(responseCode="403", description="Returned if the calling user does not have permission to view the content")})
    public Cluster getCluster() {
        return this.zduService.getCluster();
    }

    @GET
    @Path(value="/state")
    @Operation(summary="Gets the Cluster State", description="Gets the State of the Cluster and the responding Node's information.", tags={"cluster"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Returns full JSON representation of cluster state", content={@Content(schema=@Schema(implementation=ClusterStateResponse.class))}), @ApiResponse(responseCode="401", description="Returned if user is not authenticated"), @ApiResponse(responseCode="403", description="Returned if the calling user does not have permission to view the content")})
    public ClusterStateResponse getState() {
        return this.zduService.getClusterStateResponse();
    }

    @GET
    @Path(value="/nodes/{nodeId}")
    @Operation(summary="Gets the Node's overview", description="Gets the requested Node's information.", tags={"zdu"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Returns full JSON representation of the cluster node", content={@Content(schema=@Schema(implementation=NodeInfoDTO.class))}), @ApiResponse(responseCode="401", description="Returned if user is not authenticated"), @ApiResponse(responseCode="403", description="Returned if the calling user does not have permission to view the content"), @ApiResponse(responseCode="404", description="Returned if there is no content with the given id")})
    @Parameter(name="nodeId", required=true, example="abc", description="The id of the Node to retrieve information from.")
    public NodeInfoDTO getNodeById(@PathParam(value="nodeId") String nodeId) {
        return this.zduService.getNode(nodeId).orElseThrow(NotFoundException::new);
    }

    @POST
    @Path(value="/start")
    @Operation(summary="Start ZDU upgrade", description="Enables Upgrading of individual Nodes within the Cluster, allowing a heterogeneous Cluster formation.", tags={"zdu"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Returns full JSON representation of the cluster", content={@Content(schema=@Schema(implementation=Cluster.class))}), @ApiResponse(responseCode="401", description="Returned if user is not authenticated"), @ApiResponse(responseCode="403", description="Returned if the calling user does not have permission to view the content"), @ApiResponse(responseCode="409", description="Returned if the cluster is not in a valid state")})
    public Cluster startUpgrade() {
        return this.zduService.startZdu();
    }

    @POST
    @Path(value="/retryUpgrade")
    @Operation(summary="Retry ZDU upgrade", description="Reruns any incomplete finalization Upgrade Tasks.", tags={"zdu"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Returns full JSON representation of the cluster", content={@Content(schema=@Schema(implementation=Cluster.class))}), @ApiResponse(responseCode="401", description="Returned if user is not authenticated"), @ApiResponse(responseCode="403", description="Returned if the calling user does not have permission to view the content"), @ApiResponse(responseCode="409", description="Returned if the cluster is not in a valid state")})
    public Cluster retryFinalization() {
        return this.zduService.retryFinalization();
    }

    @POST
    @Path(value="/cancel")
    @Operation(summary="Cancel ZDU upgrade", description="Prohibits the Upgrading of individual Nodes within the Cluster. All Nodes need to be on the same version before performing this request.", tags={"zdu"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Returns full JSON representation of the cluster", content={@Content(schema=@Schema(implementation=Cluster.class))}), @ApiResponse(responseCode="401", description="Returned if user is not authenticated"), @ApiResponse(responseCode="403", description="Returned if the calling user does not have permission to view the content"), @ApiResponse(responseCode="409", description="Returned if the cluster is not in a valid state")})
    public Cluster cancelUpgrade() {
        return this.zduService.cancelZdu();
    }

    @POST
    @Path(value="/approve")
    @Operation(summary="Approve the ZDU upgrade", description="Finalizes the ZDU upgrade and runs specific tasks such as cleanup scripts.", tags={"zdu"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Returns full JSON representation of the cluster", content={@Content(schema=@Schema(implementation=Cluster.class))}), @ApiResponse(responseCode="401", description="Returned if user is not authenticated"), @ApiResponse(responseCode="403", description="Returned if the calling user does not have permission to view the content"), @ApiResponse(responseCode="409", description="Returned if the cluster is not in a valid state"), @ApiResponse(responseCode="500", description="Internal Error")})
    public Cluster approveUpgrade() {
        return this.zduService.finalizeZdu();
    }
}

