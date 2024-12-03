/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.internal.integration.jira.rest;

import com.atlassian.internal.integration.jira.InternalJiraService;
import com.atlassian.internal.integration.jira.autocomplete.AutoCompleteItem;
import com.atlassian.internal.integration.jira.autocomplete.RestAutoCompleteContext;
import com.atlassian.internal.integration.jira.rest.RestUtils;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.sun.jersey.spi.resource.Singleton;
import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json;charset=UTF-8"})
@Path(value="/fields")
@Singleton
public class JiraFieldsResource {
    private final InternalJiraService jiraService;

    public JiraFieldsResource(InternalJiraService jiraService) {
        this.jiraService = jiraService;
    }

    @POST
    @Path(value="autocomplete")
    public Response getAutoCompleteData(RestAutoCompleteContext context) {
        Collection<AutoCompleteItem> items = this.jiraService.getAutoCompleteItems(context);
        return RestUtils.ok(items).build();
    }
}

