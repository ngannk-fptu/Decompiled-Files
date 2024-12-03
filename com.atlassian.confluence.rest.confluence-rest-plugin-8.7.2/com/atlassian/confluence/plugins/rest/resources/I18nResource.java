/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.util.i18n.PluginI18NResource
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.servlet.util.LastModifiedHandler
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.rest.resources;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.rest.resources.AbstractResource;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.PluginI18NResource;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.servlet.util.LastModifiedHandler;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.user.User;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/i18n")
@AnonymousAllowed
public class I18nResource
extends AbstractResource {
    private static final Logger log = LoggerFactory.getLogger(I18nResource.class);
    private final PluginAccessor pluginAccessor;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final HttpContext httpContext;

    private I18nResource() {
        this.pluginAccessor = null;
        this.localeManager = null;
        this.i18NBeanFactory = null;
        this.httpContext = null;
    }

    public I18nResource(UserAccessor userAccessor, PluginAccessor pluginAccessor, LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, HttpContext httpContext, SpacePermissionManager spacePermissionManager) {
        super(userAccessor, spacePermissionManager);
        this.pluginAccessor = pluginAccessor;
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.httpContext = httpContext;
    }

    @GET
    @Produces(value={"application/xml", "application/json"})
    public Response get(@QueryParam(value="pluginKeys") Set<String> pluginKeys, @QueryParam(value="rawValue") @DefaultValue(value="false") boolean rawValue) {
        if (pluginKeys == null || pluginKeys.isEmpty()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        if (pluginKeys.size() == 1) {
            return this.getI18n(pluginKeys.iterator().next(), rawValue);
        }
        HashMap<String, String> i18nProperties = new HashMap<String, String>();
        I18NBean i18NBean = this.getI18nBean();
        for (String pluginKey : pluginKeys) {
            Plugin plugin = this.pluginAccessor.getEnabledPlugin(pluginKey);
            if (plugin == null) {
                log.error("Skipping plugin. Cannot find plugin to get i18n properties for: " + pluginKey);
                continue;
            }
            try {
                this.loadI18nProperties(plugin, i18nProperties, i18NBean, rawValue);
            }
            catch (IOException e) {
                log.error("Skipping plugin. Error loading plugin i18n properties: " + pluginKey, (Throwable)e);
            }
        }
        return Response.ok(i18nProperties).build();
    }

    @GET
    @Produces(value={"application/json"})
    @Path(value="/{key}")
    public Response getI18n(@PathParam(value="key") String pluginKey, @QueryParam(value="rawValue") @DefaultValue(value="false") boolean rawValue) {
        Plugin plugin = this.pluginAccessor.getEnabledPlugin(pluginKey);
        if (plugin == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        if (this.httpContext != null && this.httpContext.getResponse() != null) {
            if (LastModifiedHandler.checkRequest((HttpServletRequest)this.httpContext.getRequest(), (HttpServletResponse)this.httpContext.getResponse(), (Date)plugin.getDateLoaded())) {
                return Response.status((Response.Status)Response.Status.NOT_MODIFIED).build();
            }
        } else {
            log.error("\n\nhttpcontext null: " + (this.httpContext == null));
            if (this.httpContext != null) {
                log.error("response null: " + (this.httpContext.getResponse() == null) + "\n\n");
            }
        }
        HashMap<String, String> i18nProperties = new HashMap<String, String>();
        I18NBean i18NBean = this.getI18nBean();
        try {
            this.loadI18nProperties(plugin, i18nProperties, i18NBean, rawValue);
        }
        catch (IOException e) {
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(i18nProperties).build();
    }

    private I18NBean getI18nBean() {
        User user = this.getCurrentUser();
        Locale locale = this.localeManager.getLocale(user);
        return this.i18NBeanFactory.getI18NBean(locale);
    }

    private void loadI18nProperties(Plugin plugin, Map<String, String> i18nProperties, I18NBean i18NBean, boolean rawValue) throws IOException {
        for (ResourceDescriptor resourceDescriptor : plugin.getResourceDescriptors()) {
            if (!"i18n".equals(resourceDescriptor.getType())) continue;
            PluginI18NResource resource = new PluginI18NResource(plugin, resourceDescriptor);
            Iterator<String> iterator = resource.getBundle().keySet().iterator();
            while (iterator.hasNext()) {
                String o;
                String keyStr = o = iterator.next();
                i18nProperties.put(keyStr, i18NBean.getText(keyStr, null, rawValue));
            }
        }
    }
}

