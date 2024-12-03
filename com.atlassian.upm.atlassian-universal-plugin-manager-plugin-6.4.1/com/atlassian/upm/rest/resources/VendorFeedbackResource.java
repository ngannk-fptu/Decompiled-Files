/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.core.PathSegment
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.upm.analytics.event.VendorFeedbackEvent;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.analytics.AnalyticsLogger;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.license.internal.PluginLicensesInternal;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import com.atlassian.upm.rest.representations.VendorFeedbackRepresentation;
import java.io.Serializable;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

@WebSudoNotRequired
@Path(value="/{pluginKey}/vendor-feedback")
public class VendorFeedbackResource {
    private static final int DEFAULT_MAX_MESSAGE_LENGTH = 10000;
    private static final int UNPROCESSABLE_ENTITY = 422;
    private final PermissionEnforcer permissionEnforcer;
    private final AnalyticsLogger analytics;
    private final PluginLicenseRepository licenseRepository;
    private final UpmRepresentationFactory representationFactory;
    private final I18nResolver i18nResolver;

    public VendorFeedbackResource(PermissionEnforcer permissionEnforcer, AnalyticsLogger analytics, PluginLicenseRepository licenseRepository, UpmRepresentationFactory representationFactory, I18nResolver i18nResolver) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.analytics = Objects.requireNonNull(analytics, "analytics");
        this.licenseRepository = Objects.requireNonNull(licenseRepository, "licenseRepository");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.i18nResolver = i18nResolver;
    }

    @POST
    @Consumes(value={"application/vnd.atl.plugins+json"})
    public Response sendVendorFeedback(@PathParam(value="pluginKey") PathSegment pluginKeyPath, VendorFeedbackRepresentation rep) {
        int messageMaxLength = 0;
        try {
            messageMaxLength = Integer.valueOf(System.getProperty("upm.vendor.feedback.message.length", Integer.toString(10000)));
            if (messageMaxLength <= 0) {
                messageMaxLength = 10000;
            }
        }
        catch (NumberFormatException ne) {
            messageMaxLength = 10000;
        }
        if (rep.getMessage().length() > messageMaxLength) {
            return Response.status((int)422).entity((Object)this.representationFactory.createErrorRepresentation(this.i18nResolver.getText("upm.feedback.message.length.error", new Serializable[]{Integer.valueOf(messageMaxLength)}))).type("application/vnd.atl.plugins.error+json").build();
        }
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        if (rep.getType().equals("disable")) {
            this.permissionEnforcer.enforcePermission(Permission.MANAGE_PLUGIN_ENABLEMENT);
        } else if (rep.getType().equals("uninstall")) {
            this.permissionEnforcer.enforcePermission(Permission.MANAGE_PLUGIN_UNINSTALL);
        } else {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.representationFactory.createErrorRepresentation(this.i18nResolver.getText("upm.unsupported.feedback.type.error", new Serializable[]{rep.getType()}))).type("application/vnd.atl.plugins.error+json").build();
        }
        Option<String> addonSen = this.licenseRepository.getPluginLicense(pluginKey).flatMap(PluginLicensesInternal.licensePluginSen()::apply);
        this.analytics.log(new VendorFeedbackEvent(pluginKey, rep.getReasonCode(), rep.getMessage(), rep.getType(), rep.getPluginVersion(), Option.option(rep.getEmail()), Option.option(rep.getFullName()), addonSen));
        return Response.ok().build();
    }
}

