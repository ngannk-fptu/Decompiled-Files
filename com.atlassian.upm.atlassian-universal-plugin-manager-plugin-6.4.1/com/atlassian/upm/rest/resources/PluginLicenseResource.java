/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.PathSegment
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.upm.analytics.PluginLicenseChangeAnalyticHelper;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Either;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.core.rest.resources.UpmResources;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.license.internal.LicenseDateFormatter;
import com.atlassian.upm.license.internal.PluginLicenseDowngradeError;
import com.atlassian.upm.license.internal.PluginLicenseError;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.license.internal.PluginLicenseValidator;
import com.atlassian.upm.rest.representations.PluginLicenseRepresentation;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import com.atlassian.upm.rest.representations.ValidatePluginLicenseRepresentation;
import com.atlassian.upm.rest.representations.ValidatePluginLicenseResultRepresentation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

@Path(value="/{pluginKey}/license")
public class PluginLicenseResource {
    public static final String VALIDATE_DOWNGRADE = "validate-downgrade";
    private final UpmRepresentationFactory representationFactory;
    private final PluginRetriever pluginRetriever;
    private final PermissionEnforcer permissionEnforcer;
    private final PluginLicenseRepository licenseRepository;
    private final AuditLogService auditLogger;
    private final PluginLicenseValidator licenseValidator;
    private final LicenseDateFormatter licenseDateFormatter;
    private final I18nResolver i18nResolver;
    private final String UNLIMITED_WORD;
    private final PluginLicenseChangeAnalyticHelper pluginLicenseChangeAnalyticHelper;
    private final LicensingUsageVerifier licensingUsageVerifier;

    public PluginLicenseResource(UpmRepresentationFactory representationFactory, PluginRetriever pluginRetriever, PermissionEnforcer permissionEnforcer, PluginLicenseRepository licenseRepository, AuditLogService auditLogger, PluginLicenseValidator licenseValidator, LicenseDateFormatter licenseDateFormatter, I18nResolver i18nResolver, PluginLicenseChangeAnalyticHelper pluginLicenseChangeAnalyticHelper, LicensingUsageVerifier licensingUsageVerifier) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.licenseRepository = Objects.requireNonNull(licenseRepository, "licenseRepository");
        this.auditLogger = Objects.requireNonNull(auditLogger, "auditLogger");
        this.licenseValidator = Objects.requireNonNull(licenseValidator, "licenseValidator");
        this.licenseDateFormatter = Objects.requireNonNull(licenseDateFormatter, "licenseDateFormatter");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.UNLIMITED_WORD = i18nResolver.getText("upm.messages.remote.update.app.license.warning.unlimited.number");
        this.pluginLicenseChangeAnalyticHelper = Objects.requireNonNull(pluginLicenseChangeAnalyticHelper, "pluginLicenseChangeAnalyticHelper");
        this.licensingUsageVerifier = Objects.requireNonNull(licensingUsageVerifier, "licensingUsageVerifier");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response getLicense(@PathParam(value="pluginKey") PathSegment pluginKeyPath) {
        this.permissionEnforcer.enforcePermission(Permission.GET_INSTALLED_PLUGINS);
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        return Response.ok((Object)this.representationFactory.createPluginLicenseRepresentation(pluginKey, this.pluginRetriever.getPlugin(pluginKey), this.licenseRepository.getPluginLicense(pluginKey))).build();
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins+json"})
    public Response updateLicense(@PathParam(value="pluginKey") PathSegment pluginKeyPath, PluginLicenseRepresentation licenseRepresentation) {
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        for (Plugin plugin : this.pluginRetriever.getPlugin(pluginKey)) {
            Iterator<Object> iterator;
            this.permissionEnforcer.enforcePermission(Permission.MANAGE_PLUGIN_LICENSE, plugin);
            Iterator<Response> iterator2 = UpmResources.licensingPreconditionFailed(plugin, this.representationFactory, this.licensingUsageVerifier).iterator();
            if (iterator2.hasNext()) {
                Response error = iterator2.next();
                return error;
            }
            Option<PluginLicense> previousLicense = this.licenseRepository.getPluginLicense(pluginKey);
            boolean licenseCurrentlyDefined = previousLicense.isDefined();
            String newRawLicense = (String)Option.option(licenseRepresentation).map(PluginLicenseRepresentation::getRawLicense).getOrElse("");
            if (!licenseCurrentlyDefined && !StringUtils.isBlank((CharSequence)newRawLicense)) {
                iterator = this.licenseRepository.setPluginLicense(pluginKey, newRawLicense).left().iterator();
                if (iterator.hasNext()) {
                    PluginLicenseError e = iterator.next();
                    return Response.status((int)e.getType().getStatusCode()).entity((Object)this.representationFactory.createI18nErrorRepresentation(e.getType().getSubCode())).type("application/vnd.atl.plugins.error+json").build();
                }
                this.pluginLicenseChangeAnalyticHelper.logPluginLicenseChanged(pluginKey);
                this.auditLogger.logI18nMessage("upm.auditLog.plugin.license.add", plugin.getName(), pluginKey);
            } else if (licenseCurrentlyDefined && !StringUtils.isBlank((CharSequence)newRawLicense) && !previousLicense.get().getRawLicense().equals(newRawLicense)) {
                iterator = this.licenseRepository.setPluginLicense(pluginKey, newRawLicense).left().iterator();
                if (iterator.hasNext()) {
                    PluginLicenseError e = iterator.next();
                    return Response.status((int)e.getType().getStatusCode()).entity((Object)this.representationFactory.createI18nErrorRepresentation(e.getType().getSubCode())).type("application/vnd.atl.plugins.error+json").build();
                }
                this.pluginLicenseChangeAnalyticHelper.logPluginLicenseChanged(pluginKey);
                this.auditLogger.logI18nMessage("upm.auditLog.plugin.license.update", plugin.getName(), pluginKey);
            } else {
                return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.plugin.license.error.invalid.update")).type("application/vnd.atl.plugins.error+json").build();
            }
            if (!(iterator = this.pluginRetriever.getPlugin(pluginKey).iterator()).hasNext()) continue;
            Plugin p = (Plugin)iterator.next();
            return Response.ok((Object)this.representationFactory.createPluginLicenseRepresentation(p.getKey(), Option.some(p), this.licenseRepository.getPluginLicense(p.getKey()))).type("application/vnd.atl.plugins+json").build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @DELETE
    public Response removeLicense(@PathParam(value="pluginKey") PathSegment pluginKeyPath) {
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        Iterator<Plugin> iterator = this.pluginRetriever.getPlugin(pluginKey).iterator();
        if (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            this.permissionEnforcer.enforcePermission(Permission.MANAGE_PLUGIN_LICENSE, plugin);
            Option<PluginLicense> previousLicense = this.licenseRepository.getPluginLicense(pluginKey);
            for (PluginLicense license : previousLicense) {
                if (!license.isEmbeddedWithinHostLicense()) continue;
                return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.plugin.error.cannot.remove.embedded.license")).type("application/vnd.atl.plugins.error+json").build();
            }
            PluginLicenseRepresentation rep = this.representationFactory.createPluginLicenseRepresentation(plugin.getKey(), Option.some(plugin), previousLicense);
            this.licenseRepository.removePluginLicense(pluginKey);
            if (this.licenseRepository.getPluginLicense(pluginKey).isDefined()) {
                return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.plugin.error.failed.to.remove.license")).type("application/vnd.atl.plugins.error+json").build();
            }
            this.auditLogger.logI18nMessage("upm.auditLog.plugin.license.remove", plugin.getName(), pluginKey);
            return Response.ok((Object)rep).type("application/vnd.atl.plugins+json").build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path(value="validate-downgrade")
    @Consumes(value={"application/vnd.atl.plugins+json"})
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response validateAnyDowngrade(@PathParam(value="pluginKey") PathSegment pluginKeyPath, ValidatePluginLicenseRepresentation licenseKey) {
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        Either<PluginLicenseError, PluginLicense> maybeLicense = this.licenseValidator.validate(pluginKey, licenseKey.getLicenseKey());
        Iterator<Object> iterator = maybeLicense.left().iterator();
        if (iterator.hasNext()) {
            PluginLicenseError error = iterator.next();
            return Response.status((int)error.getType().getStatusCode()).entity((Object)this.representationFactory.createI18nErrorRepresentation(error.getType().getSubCode())).type("application/vnd.atl.plugins.error+json").build();
        }
        iterator = maybeLicense.right().iterator();
        if (iterator.hasNext()) {
            PluginLicense newLicense = (PluginLicense)iterator.next();
            Option<PluginLicense> currentPluginLicense = this.licenseRepository.getPluginLicense(pluginKey);
            ArrayList<String> warnMsgList = new ArrayList<String>();
            for (PluginLicense currentLicense : currentPluginLicense) {
                block6: for (PluginLicenseDowngradeError error : this.licenseValidator.validateDowngrade(currentLicense, newLicense)) {
                    String key = error.getKey();
                    switch (error) {
                        case EXPIRY_DATE_DOWNGRADE: {
                            warnMsgList.add(this.i18nResolver.getText(key, new Serializable[]{this.describe(newLicense.getExpiryDate())}));
                            continue block6;
                        }
                        case MAINTENANCE_EXPIRY_DATE_DOWNGRADE: {
                            warnMsgList.add(this.i18nResolver.getText(key, new Serializable[]{this.describe(newLicense.getMaintenanceExpiryDate())}));
                            continue block6;
                        }
                        case USER_DOWNGRADE: 
                        case ROLE_DOWNGRADE: {
                            warnMsgList.add(this.i18nResolver.getText(key, new Serializable[]{this.describe(newLicense.getEdition())}));
                            continue block6;
                        }
                    }
                    warnMsgList.add(this.i18nResolver.getText(key));
                }
            }
            List<String> result = Collections.unmodifiableList(warnMsgList);
            return Response.ok((Object)(result.size() == 0 ? ValidatePluginLicenseResultRepresentation.success(Collections.emptyList()) : ValidatePluginLicenseResultRepresentation.warning(result))).build();
        }
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
    }

    private String describe(Option<?> value) {
        return (String)value.map(v -> v instanceof DateTime ? this.licenseDateFormatter.formatDate((DateTime)v) : v.toString()).getOrElse(this.UNLIMITED_WORD);
    }
}

