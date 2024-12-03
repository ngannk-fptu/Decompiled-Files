/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.FormParam
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.test.rest.resources;

import com.atlassian.upm.api.util.Either;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.license.internal.PluginLicenseError;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import java.util.Iterator;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/test-license")
public class SetTestLicenseResource {
    private static final Logger log = LoggerFactory.getLogger(SetTestLicenseResource.class);
    private final PluginLicenseRepository licenseRepository;
    private final PermissionEnforcer permissionEnforcer;

    public SetTestLicenseResource(PluginLicenseRepository licenseRepository, PermissionEnforcer permissionEnforcer) {
        this.licenseRepository = Objects.requireNonNull(licenseRepository, "licenseRepository");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
    }

    @Path(value="{pluginKey}")
    @PUT
    @Consumes(value={"application/x-www-form-urlencoded"})
    public Response setLicense(@PathParam(value="pluginKey") String pluginKey, @FormParam(value="license") String licenseString) {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        if (StringUtils.isBlank((CharSequence)licenseString)) {
            this.licenseRepository.removePluginLicense(pluginKey);
            log.warn("Cleared license for " + pluginKey);
        } else {
            Either<PluginLicenseError, Option<String>> result = this.licenseRepository.setPluginLicense(pluginKey, licenseString);
            Iterator<PluginLicenseError> iterator = result.left().iterator();
            if (iterator.hasNext()) {
                PluginLicenseError e = iterator.next();
                log.error("Error trying to set license for " + pluginKey + ": " + e);
                return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
            }
            log.warn("Set license for " + pluginKey + " to " + licenseString);
        }
        return Response.ok().build();
    }
}

