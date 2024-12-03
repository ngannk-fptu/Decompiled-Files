/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.PathSegment
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.upm.api.util.Either;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.core.rest.resources.RequestContext;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.pac.AddonMarketplaceQueries;
import com.atlassian.upm.pac.AvailableAddonWithVersion;
import com.atlassian.upm.request.PluginRequestStore;
import com.atlassian.upm.rest.UpmMarketplaceFilter;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.util.Iterator;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@Path(value="/available/{key}")
@WebSudoNotRequired
public class AvailablePluginOrPluginCollectionResource {
    private final UpmRepresentationFactory factory;
    private final PermissionEnforcer permissionEnforcer;
    private final PluginRequestStore pluginRequestStore;
    private final UserManager userManager;
    private final AddonMarketplaceQueries mpacQueries;

    public AvailablePluginOrPluginCollectionResource(UpmRepresentationFactory factory, PermissionEnforcer permissionEnforcer, PluginRequestStore pluginRequestStore, UserManager userManager, AddonMarketplaceQueries mpacQueries) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.factory = Objects.requireNonNull(factory, "factory");
        this.pluginRequestStore = Objects.requireNonNull(pluginRequestStore, "pluginRequestStore");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.mpacQueries = Objects.requireNonNull(mpacQueries, "mpacQueries");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins.available+json"})
    public Response get(@PathParam(value="key") PathSegment keySegment, @QueryParam(value="q") String q, @QueryParam(value="category") String category, @QueryParam(value="cost") String cost, @QueryParam(value="offset") @DefaultValue(value="0") int offset, @Context HttpServletRequest request) throws MpacException {
        String key = keySegment.getPath();
        this.permissionEnforcer.enforcePermission(Permission.GET_AVAILABLE_PLUGINS);
        Either r = (Either)UpmMarketplaceFilter.fromKey(key).fold(() -> {
            try {
                Iterator<AvailableAddonWithVersion> iterator = this.mpacQueries.getClient().getAvailablePlugin(UpmUriEscaper.unescape(key)).iterator();
                if (iterator.hasNext()) {
                    AvailableAddonWithVersion a = iterator.next();
                    return Either.right(Response.ok((Object)this.factory.createAvailablePluginRepresentation(a.getAddon(), a.getVersion())).build());
                }
            }
            catch (MpacException e) {
                return Either.left(e);
            }
            return Either.right(Response.status((Response.Status)Response.Status.NOT_FOUND).build());
        }, filter -> {
            Option<String> searchText = Option.option(StringUtils.trimToNull((String)q));
            AddonMarketplaceQueries.AvailableAddonSummaries result = this.mpacQueries.getAvailableAddonSummaries(filter.getMarketplaceView(), offset, category, cost, searchText);
            return Either.right(Response.ok((Object)this.factory.createAvailablePluginCollectionRepresentation(result.addons, result.sourceAddons, this.pluginRequestStore.getRequestsByUser(this.userManager.getRemoteUserKey()), new RequestContext(request).pacUnreachable(result.mpacUnreachable), (UpmMarketplaceFilter)((Object)filter), searchText)).build());
        });
        Iterator iterator = r.left().iterator();
        if (iterator.hasNext()) {
            MpacException e = (MpacException)iterator.next();
            throw e;
        }
        return (Response)r.right().get();
    }
}

