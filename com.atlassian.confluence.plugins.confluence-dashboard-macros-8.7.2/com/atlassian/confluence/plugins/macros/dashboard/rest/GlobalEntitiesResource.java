/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.confluence.setup.velocity.ConfluenceStaticContextItemProvider
 *  com.atlassian.confluence.themes.GlobalHelper
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.Maps
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.macros.dashboard.rest;

import com.atlassian.confluence.plugins.macros.dashboard.GlobalEntitiesContextProvider;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.setup.velocity.ConfluenceStaticContextItemProvider;
import com.atlassian.confluence.themes.GlobalHelper;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@AnonymousAllowed
@Path(value="/global-entities")
public class GlobalEntitiesResource {
    public static final String TEMPLATE_PATH = "com/atlassian/confluence/plugins/macros/dashboard/";
    private final TemplateRenderer templateRenderer;
    private final GlobalEntitiesContextProvider globalEntitiesContextProvider;
    private final ConfluenceStaticContextItemProvider confluenceStaticContextItemProvider;
    private final DarkFeaturesManager darkFeaturesManager;

    public GlobalEntitiesResource(@ComponentImport TemplateRenderer templateRenderer, GlobalEntitiesContextProvider globalEntitiesContextProvider, @ComponentImport DarkFeaturesManager darkFeaturesManager) {
        this.templateRenderer = templateRenderer;
        this.globalEntitiesContextProvider = globalEntitiesContextProvider;
        this.darkFeaturesManager = darkFeaturesManager;
        this.confluenceStaticContextItemProvider = new ConfluenceStaticContextItemProvider();
    }

    @GET
    @Path(value="spaces")
    public Response spaces(@Context HttpServletRequest httpRequest) throws IOException {
        Map<String, Object> context = this.buildInitialContext(httpRequest);
        this.globalEntitiesContextProvider.injectSpaceTabContext(AuthenticatedUserThreadLocal.get(), context);
        return Response.ok((Object)this.renderEntitiesTemplate(context, "spaces")).header("Content-Type", (Object)"text/html; charset=UTF-8").build();
    }

    @GET
    @Path(value="pages")
    public Response pages(@Context HttpServletRequest httpRequest) throws IOException {
        Map<String, Object> context = this.buildInitialContext(httpRequest);
        this.globalEntitiesContextProvider.injectPagesTabContext(context);
        return Response.ok((Object)this.renderEntitiesTemplate(context, "pages")).header("Content-Type", (Object)"text/html; charset=UTF-8").build();
    }

    @GET
    @Path(value="network")
    public Response network(@Context HttpServletRequest httpRequest) {
        Map<String, Object> context = this.buildInitialContext(httpRequest);
        return Response.ok((Object)this.renderEntitiesTemplate(context, "network")).header("Content-Type", (Object)"text/html; charset=UTF-8").build();
    }

    private Map<String, Object> buildInitialContext(HttpServletRequest httpRequest) {
        HashMap context = Maps.newHashMap();
        context.putAll(this.confluenceStaticContextItemProvider.getContextMap());
        context.put("helper", new GlobalHelper());
        context.put("req", httpRequest);
        context.put("darkFeatures", this.darkFeaturesManager.getDarkFeatures());
        context.put("remoteUser", AuthenticatedUserThreadLocal.get());
        return context;
    }

    private String renderEntitiesTemplate(Map<String, Object> context, String entityName) {
        try {
            StringWriter stringWriter = new StringWriter();
            this.templateRenderer.render(TEMPLATE_PATH + entityName + "-entities.vm", context, (Writer)stringWriter);
            return stringWriter.toString();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

