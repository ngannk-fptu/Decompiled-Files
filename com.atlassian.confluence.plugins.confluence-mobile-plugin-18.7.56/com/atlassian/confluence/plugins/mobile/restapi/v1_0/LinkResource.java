/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.restapi.v1_0;

import com.atlassian.confluence.plugins.mobile.dto.LinkExtractorDto;
import com.atlassian.confluence.plugins.mobile.service.LinkExtractorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Tag(name="Link API", description="Contains all operations related to links")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/linkextractor")
@Component
public class LinkResource {
    private final LinkExtractorService linkExtractorService;

    @Autowired
    public LinkResource(LinkExtractorService linkExtractorService) {
        this.linkExtractorService = linkExtractorService;
    }

    @Operation(summary="Get link details", description="Get the page and comment Ids from a confluence URL", responses={@ApiResponse(responseCode="200", description="Page and comment Ids", content={@Content(schema=@Schema(implementation=LinkExtractorDto.class))})})
    @GET
    public LinkExtractorDto getLinkExtractor(@QueryParam(value="link") String link) {
        return this.linkExtractorService.extractor(link);
    }
}

