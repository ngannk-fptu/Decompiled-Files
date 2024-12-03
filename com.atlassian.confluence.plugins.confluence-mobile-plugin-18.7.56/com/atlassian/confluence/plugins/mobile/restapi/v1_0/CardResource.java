/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.restapi.v1_0;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.plugins.mobile.model.card.Card;
import com.atlassian.confluence.plugins.mobile.service.CardService;
import com.atlassian.confluence.plugins.mobile.service.impl.CardServiceImpl;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Tag(name="Activity API", description="Contains all operations related to Activities")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/card")
@Component
public class CardResource {
    private final CardService cardService;

    @Autowired
    public CardResource(CardService cardService) {
        this.cardService = cardService;
    }

    @Operation(summary="Get activities", description="Gets a list of activities by a space key", responses={@ApiResponse(responseCode="200", description="List of Activities", content={@Content(array=@ArraySchema(schema=@Schema(implementation=CardServiceImpl.CardPageResponse.class)))})})
    @GET
    public PageResponse<Card> getCards(@QueryParam(value="expand") String expand, @QueryParam(value="spaceKey") String spaceKey, @QueryParam(value="start") @DefaultValue(value="0") int start, @QueryParam(value="limit") @DefaultValue(value="50") int limit) {
        return this.cardService.find().expand(new Expansions(ExpansionsParser.parse((String)expand))).spaceKey(spaceKey).fetchMany((PageRequest)new SimplePageRequest(start, limit));
    }
}

