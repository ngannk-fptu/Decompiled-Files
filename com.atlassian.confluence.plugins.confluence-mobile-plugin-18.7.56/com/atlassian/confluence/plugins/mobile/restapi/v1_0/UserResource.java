/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.restapi.v1_0;

import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.plugins.mobile.dto.UserDto;
import com.atlassian.confluence.plugins.mobile.service.MobileUserService;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Tag(name="User API", description="Contains all operations for users")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@AnonymousAllowed
@Path(value="/user")
@Component
public class UserResource {
    private final MobileUserService mobileUserService;

    @Autowired
    public UserResource(MobileUserService mobileUserService) {
        this.mobileUserService = mobileUserService;
    }

    @Operation(summary="Get current user", description="Gets the current logged-in user details", responses={@ApiResponse(responseCode="200", description="User details", content={@Content(schema=@Schema(implementation=UserDto.class))})})
    @GET
    @Path(value="/current")
    public UserDto getCurrentUser() {
        return this.mobileUserService.getCurrentUser();
    }

    @Operation(summary="Get users editing content", description="Gets users editing content by Id", responses={@ApiResponse(responseCode="200", description="Users editing", content={@Content(array=@ArraySchema(schema=@Schema(implementation=Person.class)))}), @ApiResponse(responseCode="404", description="Content doesn't exist"), @ApiResponse(responseCode="500", description="Collaboration not supported")})
    @GET
    @Path(value="/concurent-editing")
    public List<Person> getConcurrentEditingUsers(@QueryParam(value="contentId") Long contentId) {
        return this.mobileUserService.getConcurrentEditingUser(contentId);
    }
}

