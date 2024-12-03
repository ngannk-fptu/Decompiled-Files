/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.fugue.Pair
 *  com.atlassian.mywork.model.Registration
 *  com.atlassian.mywork.rest.CacheControl
 *  com.atlassian.mywork.rest.JsonConfig
 *  com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.google.common.collect.Lists
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 */
package com.atlassian.mywork.host.rest;

import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import com.atlassian.mywork.host.service.LocalRegistrationService;
import com.atlassian.mywork.model.Registration;
import com.atlassian.mywork.rest.CacheControl;
import com.atlassian.mywork.rest.JsonConfig;
import com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess;
import com.atlassian.sal.api.message.LocaleResolver;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path(value="configuration")
@Produces(value={"application/json"})
@UnlicensedSiteAccess
public class ConfigurationResource {
    private final LocalRegistrationService registrationService;
    private final LocaleResolver localeResolver;

    public ConfigurationResource(LocalRegistrationService registrationService, LocaleResolver localeResolver) {
        this.registrationService = registrationService;
        this.localeResolver = localeResolver;
    }

    @GET
    public Response getConfiguration(@Context HttpServletRequest request) {
        Option<Pair<List<Registration>, Date>> all = this.registrationService.getAll(new Date(request.getDateHeader("If-Modified-Since")));
        if (all.isEmpty()) {
            return Response.notModified().build();
        }
        List registrations = (List)((Pair)all.get()).left();
        ArrayList configs = Lists.newArrayListWithExpectedSize((int)registrations.size());
        for (Registration registration : registrations) {
            configs.add(this.createConfig(registration, request));
        }
        return Response.ok((Object)configs).cacheControl(CacheControl.forever()).build();
    }

    private JsonConfig createConfig(Registration registration, HttpServletRequest request) {
        return new JsonConfig(registration, this.localeResolver.getLocale(request));
    }
}

