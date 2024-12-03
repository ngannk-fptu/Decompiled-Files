/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetParsingException
 *  com.atlassian.gadgets.GadgetRequestContextFactory
 *  com.atlassian.gadgets.GadgetSpecUriNotAllowedException
 *  com.atlassian.gadgets.dashboard.PermissionException
 *  com.atlassian.gadgets.directory.Directory
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.gadgets.directory.internal.rest;

import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetRequestContextFactory;
import com.atlassian.gadgets.GadgetSpecUriNotAllowedException;
import com.atlassian.gadgets.dashboard.PermissionException;
import com.atlassian.gadgets.directory.Directory;
import com.atlassian.gadgets.directory.internal.ConfigurableExternalGadgetSpecStore;
import com.atlassian.gadgets.directory.internal.DirectoryConfigurationPermissionChecker;
import com.atlassian.gadgets.directory.internal.impl.UnavailableFeatureException;
import com.atlassian.gadgets.directory.internal.jaxb.JAXBDirectoryContents;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/directory")
public class DirectoryResource {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Directory directory;
    private final GadgetRequestContextFactory gadgetRequestContextFactory;
    private final ConfigurableExternalGadgetSpecStore configurableDirectory;
    private final DirectoryConfigurationPermissionChecker gadgetUrlChecker;
    private final I18nResolver i18n;
    private final UserManager userManager;

    public DirectoryResource(Directory directory, GadgetRequestContextFactory gadgetRequestContextFactory, ConfigurableExternalGadgetSpecStore configurableDirectory, DirectoryConfigurationPermissionChecker gadgetUrlChecker, I18nResolver i18n, @ComponentImport UserManager userManager) {
        this.directory = directory;
        this.gadgetRequestContextFactory = gadgetRequestContextFactory;
        this.configurableDirectory = configurableDirectory;
        this.gadgetUrlChecker = gadgetUrlChecker;
        this.i18n = i18n;
        this.userManager = userManager;
    }

    @GET
    @AnonymousAllowed
    @Produces(value={"application/xml", "application/json"})
    @Deprecated
    public Response getDirectory(@Context HttpServletRequest request) {
        this.log.debug("DirectoryResource: GET received and answered (all users allowed)");
        if (!Boolean.getBoolean("gadgets.directory.anonymous.allow") && this.userManager.getRemoteUserKey() == null) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        return Response.ok((Object)JAXBDirectoryContents.getDirectoryContents(this.directory, this.gadgetRequestContextFactory.get(request))).build();
    }

    @POST
    @Consumes(value={"application/json"})
    public Response putGadgetInDirectory(@Context HttpServletRequest request, Reader jsonContent) {
        String gadgetUrl = "";
        try {
            JSONObject jsonObject = new JSONObject(IOUtils.toString((Reader)jsonContent));
            gadgetUrl = jsonObject.getString("url").trim();
            if (StringUtils.isEmpty((CharSequence)gadgetUrl)) {
                this.log.error("DirectoryResource: POST rejected due to missing 'url' parameter");
                return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.i18n.getText("directoryResource.missing.url.parameter")).type("text/plain").build();
            }
            this.log.debug("DirectoryResource: POST received: url=" + gadgetUrl);
            this.gadgetUrlChecker.checkForPermissionToConfigureDirectory(request);
            URI validGadgetUri = URI.create(gadgetUrl);
            this.configurableDirectory.add(validGadgetUri);
            this.log.debug("DirectoryResource: POST complete: new URL=" + validGadgetUri);
            return Response.created((URI)validGadgetUri).build();
        }
        catch (JSONException e) {
            this.log.error("DirectoryResource: POST rejected due to missing 'url' parameter");
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.i18n.getText("directoryResource.missing.url.parameter")).type("text/plain").build();
        }
        catch (GadgetSpecUriNotAllowedException e) {
            this.log.error("DirectoryResource: POST rejected: " + gadgetUrl + " is an invalid gadget spec", (Throwable)e);
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.i18n.getText("directoryResource.invalid.gadget.spec", new Serializable[]{gadgetUrl})).type("text/plain").build();
        }
        catch (GadgetParsingException e) {
            this.log.error("DirectoryResource: POST rejected: could not parse gadget at " + gadgetUrl, (Throwable)e);
            String message = e.getMessage();
            if (message != null && message.contains("HTTP error 403")) {
                return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.i18n.getText("directoryResource.no.applink.configured")).type("text/plain").build();
            }
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.i18n.getText("directoryResource.could.not.parse.gadget", new Serializable[]{gadgetUrl})).type("text/plain").build();
        }
        catch (UnavailableFeatureException e) {
            this.log.info("DirectoryResource: POST rejected: container does not support feature(s) " + e.getMessage() + " required for gadget at " + gadgetUrl, (Throwable)e);
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.i18n.getText("directoryResource.unsupported.feature", new Serializable[]{gadgetUrl, e.getMessage()})).type("text/plain").build();
        }
        catch (PermissionException e) {
            this.log.warn("DirectoryResource: POST rejected: current user not allowed to write to directory", (Throwable)e);
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)this.i18n.getText("directoryResource.no.write.permission")).type("text/plain").build();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DELETE
    @Path(value="/gadget/{gadgetId}")
    public Response deleteGadgetFromDirectory(@Context HttpServletRequest request, @PathParam(value="gadgetId") ExternalGadgetSpecId gadgetId) {
        try {
            this.log.debug("DirectoryResource: DELETE received: gadgetId = " + gadgetId);
            this.gadgetUrlChecker.checkForPermissionToConfigureDirectory(request);
            this.configurableDirectory.remove(gadgetId);
            this.log.debug("DirectoryResource: DELETE complete: gadgetId = " + gadgetId);
            return Response.ok().build();
        }
        catch (PermissionException e) {
            this.log.warn("DirectoryResource: DELETE rejected: current user not allowed to write to directory", (Throwable)e);
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)this.i18n.getText("directoryResource.no.write.permission")).type("text/plain").build();
        }
    }
}

