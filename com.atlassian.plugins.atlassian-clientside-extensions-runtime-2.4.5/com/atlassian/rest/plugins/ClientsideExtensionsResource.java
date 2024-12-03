/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.api.DynamicWebInterfaceManager
 *  com.atlassian.plugin.web.api.WebItem
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.rest.annotation.ResponseType
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.annotation.Nonnull
 *  javax.inject.Inject
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 */
package com.atlassian.rest.plugins;

import com.atlassian.ozymandias.SafePluginPointAccess;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.api.DynamicWebInterfaceManager;
import com.atlassian.plugin.web.api.WebItem;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.rest.annotation.ResponseType;
import com.atlassian.rest.plugins.ClientsideExtensionsAssetsDto;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="client-plugins")
@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class ClientsideExtensionsResource {
    @ComponentImport
    private final ApplicationProperties applicationProperties;
    @ComponentImport
    private final DynamicWebInterfaceManager manager;

    @Inject
    public ClientsideExtensionsResource(ApplicationProperties applicationProperties, DynamicWebInterfaceManager manager) {
        this.applicationProperties = applicationProperties;
        this.manager = manager;
    }

    @GET
    @Path(value="/items")
    @ResponseType(value=ClientsideExtensionsAssetsDto.class)
    public Response getWebItems(@QueryParam(value="location") String location, @Nonnull @QueryParam(value="key") List<String> keys) {
        String contextPath = this.applicationProperties.getBaseUrl(UrlMode.RELATIVE);
        List<WebItem> webItemsBySection = StreamSupport.stream(ClientsideExtensionsResource.getWebItemsBySection(this.manager, location).spliterator(), false).collect(Collectors.toList());
        if (keys.isEmpty()) {
            return ClientsideExtensionsResource.toResponse(webItemsBySection, contextPath);
        }
        return ClientsideExtensionsResource.toResponse(ClientsideExtensionsResource.getWebItemsByKey(webItemsBySection, keys), contextPath);
    }

    private static Map<String, Object> getContext() {
        return new HashMap<String, Object>();
    }

    private static List<WebItem> getWebItemsByKey(List<WebItem> items, List<String> keys) {
        return items.stream().filter(webItem -> {
            String webItemKey = webItem.getCompleteKey();
            return keys.stream().anyMatch(key -> key.equals(webItemKey));
        }).collect(Collectors.toList());
    }

    private static Iterable<WebItem> getWebItemsBySection(DynamicWebInterfaceManager manager, String section) {
        return (Iterable)SafePluginPointAccess.call(() -> manager.getDisplayableWebItems(section, ClientsideExtensionsResource.getContext())).getOrElse(Collections.emptyList());
    }

    private static Response toResponse(List<WebItem> items, @Nonnull String contextPath) {
        return Response.ok((Object)new ClientsideExtensionsAssetsDto(items, contextPath)).build();
    }
}

