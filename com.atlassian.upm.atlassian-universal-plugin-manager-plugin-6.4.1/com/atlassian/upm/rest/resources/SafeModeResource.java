/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.upm.SafeModeService;
import com.atlassian.upm.core.PluginsEnablementStateAccessor;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/safe-mode")
public class SafeModeResource {
    private final UpmRepresentationFactory representationFactory;
    private final SafeModeService safeMode;
    private final UpmUriBuilder uriBuilder;
    private final PermissionEnforcer permissionEnforcer;

    public SafeModeResource(UpmRepresentationFactory representationFactory, SafeModeService safeMode, UpmUriBuilder uriBuilder, PermissionEnforcer permissionEnforcer) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.safeMode = Objects.requireNonNull(safeMode, "safeMode");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins.safe.mode.flag+json"})
    public Response get() {
        this.permissionEnforcer.enforcePermission(Permission.GET_SAFE_MODE);
        return Response.ok((Object)this.retrieveSafeModeFlagEntity()).build();
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins.safe.mode.flag+json"})
    public Response put(@QueryParam(value="keepState") boolean keepState, SafeModeFlag flag) {
        this.permissionEnforcer.enforcePermission(Permission.MANAGE_SAFE_MODE);
        if (flag.isEnabled()) {
            if (this.safeMode.isSafeMode()) {
                return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.safeMode.error.already.entered.safeMode")).type("application/vnd.atl.plugins.error+json").build();
            }
            if (!this.triggerSafeMode()) {
                return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.safeMode.error.cannot.go.to.safe.mode")).type("application/vnd.atl.plugins.error+json").build();
            }
        } else {
            if (!this.safeMode.isSafeMode()) {
                return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.safeMode.error.already.exited.safeMode")).type("application/vnd.atl.plugins.error+json").build();
            }
            this.exitSafeMode(keepState);
        }
        return Response.ok((Object)this.retrieveSafeModeFlagEntity()).type("application/vnd.atl.plugins.safe.mode.flag+json").build();
    }

    private SafeModeFlag retrieveSafeModeFlagEntity() {
        if (this.safeMode.isSafeMode()) {
            HashMap<String, URI> result = new HashMap<String, URI>();
            result.put("exit-safe-mode-restore", this.uriBuilder.buildExitSafeModeUri(false));
            result.put("exit-safe-mode-keep", this.uriBuilder.buildExitSafeModeUri(true));
            return new SafeModeFlag(true, Collections.unmodifiableMap(result));
        }
        HashMap<String, URI> result = new HashMap<String, URI>();
        result.put("safe-mode", this.uriBuilder.buildSafeModeUri());
        result.put("enter-safe-mode", this.uriBuilder.buildSafeModeUri());
        return new SafeModeFlag(false, Collections.unmodifiableMap(result));
    }

    private boolean triggerSafeMode() {
        try {
            return this.safeMode.enterSafeMode();
        }
        catch (PluginsEnablementStateAccessor.PluginsEnablementStateStoreException e) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)this.representationFactory.createErrorRepresentation("upm.safeMode.error.cannot.save.configuration", e.getMessage())).type("application/vnd.atl.plugins.error+json").build());
        }
    }

    private void exitSafeMode(boolean keepState) {
        try {
            this.safeMode.exitSafeMode(keepState);
        }
        catch (SafeModeService.MissingSavedConfigurationException msce) {
            throw new WebApplicationException((Throwable)msce, Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)this.representationFactory.createErrorRepresentation("System failed to restore from Safe Mode.  Plugin system configuration from prior to entering safe mode is missing", "upm.safeMode.error.missing.configuration")).type("application/vnd.atl.plugins.error+json").build());
        }
        catch (SafeModeService.PluginStateUpdateException psue) {
            throw new WebApplicationException((Throwable)psue, Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)this.representationFactory.createSafeModeErrorReenablingPluginRepresentation(psue.getPlugin(), psue.isEnabling())).type("application/vnd.atl.plugins.safemode.error-reenabling-plugin+json").build());
        }
        catch (SafeModeService.PluginModuleStateUpdateException pmsue) {
            throw new WebApplicationException((Throwable)pmsue, Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)this.representationFactory.createSafeModeErrorReenablingPluginModuleRepresentation(pmsue.getModule(), pmsue.isEnabling())).type("application/vnd.atl.plugins.safemode.error-reenabling-plugin-module+json").build());
        }
    }

    public static class SafeModeFlag {
        @JsonProperty
        private boolean enabled;
        @JsonProperty
        final Map<String, URI> links;

        @JsonCreator
        public SafeModeFlag(@JsonProperty(value="enabled") boolean enabled, @JsonProperty(value="links") Map<String, URI> links) {
            this.enabled = enabled;
            this.links = links == null ? Collections.emptyMap() : Collections.unmodifiableMap(new HashMap<String, URI>(links));
        }

        public boolean isEnabled() {
            return this.enabled;
        }
    }
}

