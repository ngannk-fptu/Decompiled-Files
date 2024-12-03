/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.test.rest.resources;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.core.test.rest.resources.SysResource;
import java.util.Iterator;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/upm-sys")
public class UpmSysResource {
    private final PermissionEnforcer permissionEnforcer;
    private static Option<SysResource.SysUpdateValueRepresentation> dataCenterEnabled = Option.none();
    private static Option<SysResource.SysUpdateValueRepresentation> mailServerStatus = Option.none();
    private static Option<SenRepresentation> sen = Option.none();
    private static Option<SysResource.SysUpdateValueRepresentation> autoInstall = Option.none();
    private static Option<SysResource.SysUpdateValueRepresentation> purchasedAddonsFeature = Option.none();
    private static Option<SysResource.SysUpdateValueRepresentation> checkLicenseFeature = Option.none();

    public UpmSysResource(PermissionEnforcer permissionEnforcer) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
    }

    public static Option<Option<String>> getSen() {
        return sen.map(SenRepresentation::getSen);
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    @Path(value="sen")
    public Response getSenResource() {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        return Response.ok(sen).build();
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins+json"})
    @Path(value="sen")
    public Response setSen(SenRepresentation senRepresentation) {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        sen = Option.option(senRepresentation);
        return Response.ok(sen).type("application/vnd.atl.plugins+json").build();
    }

    @DELETE
    @Path(value="sen")
    public Response resetSen() {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        sen = Option.none();
        return Response.ok().build();
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    @Path(value="data-center")
    public Response getDataCenterEnabled() {
        return this.getBooleanResponse(dataCenterEnabled);
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins+json"})
    @Path(value="data-center")
    public Response setDataCenterEnabled(SysResource.SysUpdateValueRepresentation dataCenterEnabledRep) throws Exception {
        Iterator<Response> iterator = this.checkPermission().iterator();
        if (iterator.hasNext()) {
            Response resp = iterator.next();
            return resp;
        }
        dataCenterEnabled = Option.some(dataCenterEnabledRep);
        return Response.ok((Object)dataCenterEnabledRep).type("application/vnd.atl.plugins+json").build();
    }

    @DELETE
    @Path(value="data-center")
    public Response resetDataCenterEnabled() {
        Iterator<Response> iterator = this.checkPermission().iterator();
        if (iterator.hasNext()) {
            Response resp = iterator.next();
            return resp;
        }
        dataCenterEnabled = Option.none();
        return Response.ok().build();
    }

    public static Option<Boolean> isDataCenterEnabled() {
        return UpmSysResource.isValueEnabled(dataCenterEnabled);
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    @Path(value="mail-server")
    public Response getMailServerStatus() {
        return this.getBooleanResponse(mailServerStatus);
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins+json"})
    @Path(value="mail-server")
    public Response setMailServerStatus(SysResource.SysUpdateValueRepresentation mailServerConfiguredRepresentation) throws Exception {
        Iterator<Response> iterator = this.checkPermission().iterator();
        if (iterator.hasNext()) {
            Response resp = iterator.next();
            return resp;
        }
        mailServerStatus = Option.some(mailServerConfiguredRepresentation);
        return Response.ok((Object)mailServerConfiguredRepresentation).type("application/vnd.atl.plugins+json").build();
    }

    @DELETE
    @Path(value="mail-server")
    public Response resetMailServerStatus() {
        Iterator<Response> iterator = this.checkPermission().iterator();
        if (iterator.hasNext()) {
            Response resp = iterator.next();
            return resp;
        }
        mailServerStatus = Option.none();
        return Response.ok().build();
    }

    public static Option<Boolean> isMailServerConfigured() {
        return UpmSysResource.isValueEnabled(mailServerStatus);
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    @Path(value="auto-install-remote-plugins")
    public Response getAutoInstallRemotePlugins() {
        return this.getBooleanResponse(autoInstall);
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins+json"})
    @Path(value="auto-install-remote-plugins")
    public Response setAutoInstallRemotePlugins(SysResource.SysUpdateValueRepresentation rep) throws Exception {
        Iterator<Response> iterator = this.checkPermission().iterator();
        if (iterator.hasNext()) {
            Response resp = iterator.next();
            return resp;
        }
        autoInstall = Option.some(rep);
        return Response.ok((Object)rep).type("application/vnd.atl.plugins+json").build();
    }

    @DELETE
    @Path(value="auto-install-remote-plugins")
    public Response resetAutoInstallRemotePlugins() {
        Iterator<Response> iterator = this.checkPermission().iterator();
        if (iterator.hasNext()) {
            Response resp = iterator.next();
            return resp;
        }
        autoInstall = Option.none();
        return Response.ok().build();
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    @Path(value="check-license")
    public Response getCheckLicenseFeature() {
        return this.getBooleanResponse(checkLicenseFeature);
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins+json"})
    @Path(value="check-license")
    public Response setCheckLicenseFeature(SysResource.SysUpdateValueRepresentation rep) throws Exception {
        Iterator<Response> iterator = this.checkPermission().iterator();
        if (iterator.hasNext()) {
            Response resp = iterator.next();
            return resp;
        }
        checkLicenseFeature = Option.some(rep);
        return Response.ok((Object)rep).type("application/vnd.atl.plugins+json").build();
    }

    @DELETE
    @Path(value="check-license")
    public Response resetCheckLicenseFeature() {
        Iterator<Response> iterator = this.checkPermission().iterator();
        if (iterator.hasNext()) {
            Response resp = iterator.next();
            return resp;
        }
        checkLicenseFeature = Option.none();
        return Response.ok().build();
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    @Path(value="purchased-addons")
    public Response getPurchasedAddons() {
        return this.getBooleanResponse(purchasedAddonsFeature);
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins+json"})
    @Path(value="purchased-addons")
    public Response setPurchasedAddons(SysResource.SysUpdateValueRepresentation rep) throws Exception {
        Iterator<Response> iterator = this.checkPermission().iterator();
        if (iterator.hasNext()) {
            Response resp = iterator.next();
            return resp;
        }
        purchasedAddonsFeature = Option.some(rep);
        return Response.ok((Object)rep).type("application/vnd.atl.plugins+json").build();
    }

    @DELETE
    @Path(value="purchased-addons")
    public Response resetPurchasedAddons() {
        Iterator<Response> iterator = this.checkPermission().iterator();
        if (iterator.hasNext()) {
            Response resp = iterator.next();
            return resp;
        }
        purchasedAddonsFeature = Option.none();
        return Response.ok().build();
    }

    private Response getBooleanResponse(Option<SysResource.SysUpdateValueRepresentation> rep) {
        Iterator<Object> iterator = this.checkPermission().iterator();
        if (iterator.hasNext()) {
            Response resp = iterator.next();
            return resp;
        }
        iterator = rep.iterator();
        if (iterator.hasNext()) {
            SysResource.SysUpdateValueRepresentation value = (SysResource.SysUpdateValueRepresentation)iterator.next();
            return Response.ok((Object)value).build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    private Option<Response> checkPermission() {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Option.some(Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build());
        }
        return Option.none();
    }

    private static Option<Boolean> isValueEnabled(Option<SysResource.SysUpdateValueRepresentation> rep) {
        return rep.map(SysResource.SysUpdateValueRepresentation::getValue);
    }

    public static Option<Boolean> isAutoInstallRemotePluginsEnabled() {
        return UpmSysResource.isValueEnabled(autoInstall);
    }

    public static Option<Boolean> isCheckLicenseFeatureEnabled() {
        return UpmSysResource.isValueEnabled(checkLicenseFeature);
    }

    public static Option<Boolean> isPurchasedAddonsFeatureEnabled() {
        return UpmSysResource.isValueEnabled(purchasedAddonsFeature);
    }

    public static final class SenRepresentation {
        @JsonProperty
        private String sen;

        @JsonCreator
        public SenRepresentation(@JsonProperty(value="sen") String sen) {
            this.sen = sen;
        }

        public Option<String> getSen() {
            return this.sen == null || this.sen.equals("") ? Option.none(String.class) : Option.some(this.sen);
        }
    }
}

