/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.PathSegment
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.upm.UpmSys;
import com.atlassian.upm.analytics.PluginLicenseChangeAnalyticHelper;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Either;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.license.PluginLicenses;
import com.atlassian.upm.license.internal.PluginLicenseError;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.license.internal.PluginLicenseValidator;
import com.atlassian.upm.license.internal.mac.LicenseReceiptValidator;
import com.atlassian.upm.mac.HamletClient;
import com.atlassian.upm.mac.HamletException;
import com.atlassian.upm.mac.HamletLicenseCollection;
import com.atlassian.upm.mac.HamletLicenseInfo;
import com.atlassian.upm.notification.NotificationCache;
import com.atlassian.upm.pac.AvailableAddonWithVersion;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.rest.representations.MacCredentialsRepresentation;
import com.atlassian.upm.rest.representations.PurchasedPluginUpdateResultCollectionRepresentation;
import com.atlassian.upm.rest.representations.PurchasedPluginUpdateResultRepresentation;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/purchased/update")
public class PurchasedPluginCheckResource {
    private static final Logger log = LoggerFactory.getLogger(PurchasedPluginCheckResource.class);
    public static final String AUTH_ERROR = "upm.purchased.addons.check.error.auth";
    public static final String UNKNOWN_ERROR = "upm.purchased.addons.check.error.unknown";
    private final PermissionEnforcer permissionEnforcer;
    private final HamletClient hamletClient;
    private final NotificationCache notificationCache;
    private final PluginLicenseRepository licenseRepository;
    private final AuditLogService auditLogger;
    private final PacClient pacClient;
    private final PluginLicenseValidator licenseValidator;
    private final LicenseReceiptValidator licenseReceiptValidator;
    private final UpmRepresentationFactory representationFactory;
    private final PluginLicenseChangeAnalyticHelper pluginLicenseChangeAnalyticHelper;

    public PurchasedPluginCheckResource(PermissionEnforcer permissionEnforcer, HamletClient hamletClient, NotificationCache notificationCache, PluginLicenseRepository licenseRepository, AuditLogService auditLogger, PacClient pacClient, PluginLicenseValidator licenseValidator, LicenseReceiptValidator licenseReceiptValidator, UpmRepresentationFactory representationFactory, PluginLicenseChangeAnalyticHelper pluginLicenseChangeAnalyticHelper) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.hamletClient = Objects.requireNonNull(hamletClient, "hamletClient");
        this.notificationCache = Objects.requireNonNull(notificationCache, "notificationCache");
        this.licenseRepository = Objects.requireNonNull(licenseRepository, "licenseRepository");
        this.auditLogger = Objects.requireNonNull(auditLogger, "auditLogger");
        this.pacClient = Objects.requireNonNull(pacClient, "pacClient");
        this.licenseValidator = Objects.requireNonNull(licenseValidator, "licenseValidator");
        this.licenseReceiptValidator = Objects.requireNonNull(licenseReceiptValidator, "licenseReceiptValidator");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.pluginLicenseChangeAnalyticHelper = Objects.requireNonNull(pluginLicenseChangeAnalyticHelper, "pluginLicenseChangeAnalyticHelper");
    }

    @POST
    @Consumes(value={"application/vnd.atl.plugins+json"})
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response updateAllLicenses(MacCredentialsRepresentation credentials) {
        try {
            return this.updateAllLicenses(this.hamletClient.getPurchasedLicensesWithCredentials(credentials.getUsername(), credentials.getPassword()));
        }
        catch (HamletException e) {
            String error = e instanceof HamletException.InvalidCredentialsException ? AUTH_ERROR : UNKNOWN_ERROR;
            return Response.ok((Object)new PurchasedPluginUpdateResultCollectionRepresentation(error)).build();
        }
    }

    @GET
    @Path(value="signed")
    @Consumes(value={"application/vnd.atl.plugins+json"})
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response updateAllLicensesSigned() {
        try {
            return this.updateAllLicenses(this.hamletClient.getPurchasedLicensesWithJwtToken());
        }
        catch (HamletException e) {
            String error = e instanceof HamletException.InvalidCredentialsException ? AUTH_ERROR : UNKNOWN_ERROR;
            return Response.ok((Object)new PurchasedPluginUpdateResultCollectionRepresentation(error)).build();
        }
    }

    private Response updateAllLicenses(Option<HamletLicenseCollection> purchasedLicenses) {
        this.permissionEnforcer.enforcePermission(Permission.GET_PURCHASED_PLUGINS);
        Map<String, PluginLicense> licenseMap = StreamSupport.stream(this.licenseRepository.getPluginLicenses().spliterator(), false).filter(PluginLicenses.isEvaluation().negate()).collect(Collectors.toMap(PluginLicenses.licensePluginKey(), Function.identity()));
        ArrayList<String> updatedLicenseKeys = new ArrayList<String>();
        for (HamletLicenseCollection newData : purchasedLicenses) {
            List<HamletLicenseInfo> newLicenses = Collections.unmodifiableList(newData.getAddonLicenses().stream().filter(this.licenseIsNew(licenseMap)).collect(Collectors.toList()));
            if (newLicenses.isEmpty()) continue;
            for (HamletLicenseInfo newLicense : newLicenses) {
                try {
                    for (AvailableAddonWithVersion a : this.pacClient.getAvailablePlugin(newLicense.getKey())) {
                        this.auditLogger.logI18nMessage("upm.auditLog.plugin.license.add", a.getAddon().getName(), a.getAddon().getKey());
                    }
                }
                catch (MpacException e) {
                    log.warn("Could not contact Marketplace", (Throwable)e);
                }
                if (!this.setLicense(newLicense.getKey(), newLicense.getLicense())) continue;
                updatedLicenseKeys.add(newLicense.getKey());
            }
        }
        return Response.ok((Object)new PurchasedPluginUpdateResultCollectionRepresentation(updatedLicenseKeys)).build();
    }

    @POST
    @Path(value="{pluginKey}")
    @Consumes(value={"application/vnd.atl.plugins+json"})
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response updatePluginLicense(@PathParam(value="pluginKey") PathSegment pluginKeyPath) {
        this.permissionEnforcer.enforcePermission(Permission.GET_PURCHASED_PLUGINS);
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        if (!UpmSys.isCheckLicenseFeatureEnabled()) {
            return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.check.license.error.feature.not.enabled")).type("application/vnd.atl.plugins.error+json").build();
        }
        try {
            Iterator<HamletLicenseInfo> iterator = this.hamletClient.getPurchasedLicense(pluginKey).iterator();
            if (iterator.hasNext()) {
                HamletLicenseInfo licenseInfo = iterator.next();
                if (this.licenseIsNew(licenseInfo, this.licenseRepository.getPluginLicense(pluginKey))) {
                    try {
                        for (AvailableAddonWithVersion a : this.pacClient.getAvailablePlugin(licenseInfo.getKey())) {
                            this.auditLogger.logI18nMessage("upm.auditLog.plugin.license.add", a.getAddon().getName(), a.getAddon().getKey());
                        }
                    }
                    catch (MpacException e) {
                        log.warn("Could not contact Marketplace", (Throwable)e);
                    }
                    if (this.setLicense(licenseInfo.getKey(), licenseInfo.getLicense())) {
                        return Response.ok((Object)PurchasedPluginUpdateResultRepresentation.success(Option.none(String.class))).build();
                    }
                    return Response.ok((Object)PurchasedPluginUpdateResultRepresentation.warning("upm.check.license.warning.downgrade.found")).build();
                }
                return Response.ok((Object)PurchasedPluginUpdateResultRepresentation.success(Option.some("upm.check.license.success.same.found"))).build();
            }
            return Response.ok((Object)PurchasedPluginUpdateResultRepresentation.warning("upm.check.license.warning.none.found")).build();
        }
        catch (HamletException e) {
            return Response.ok((Object)PurchasedPluginUpdateResultRepresentation.error("upm.check.license.error.unknown")).build();
        }
    }

    private boolean setLicense(String pluginKey, String licenseKey) {
        Either<PluginLicenseError, PluginLicense> validation = this.licenseValidator.validate(pluginKey, licenseKey);
        for (PluginLicenseError licenseError : validation.left()) {
            log.info("Invalid license retrieved from MyAtlassian for plugin " + pluginKey + ": " + (Object)((Object)licenseError.getType()));
        }
        for (PluginLicense license : validation.right()) {
            Option<LicenseReceiptValidator.ValidationError> receiptError = this.licenseReceiptValidator.validateReceivedLicense(license, pluginKey);
            for (LicenseReceiptValidator.ValidationError error : receiptError) {
                log.info("License retrieved from MyAtlassian for plugin " + pluginKey + " is less than existing license: " + (Object)((Object)error));
            }
            if (receiptError.isDefined()) continue;
            this.licenseRepository.setPluginLicense(pluginKey, licenseKey);
            this.pluginLicenseChangeAnalyticHelper.logPluginLicenseChanged(pluginKey);
            log.info("Successfully saved plugin license from MyAtlassian for plugin " + pluginKey);
            return true;
        }
        return false;
    }

    private String normalizeLicense(String license) {
        return license.trim().replace("\n", "").replace("\r", "");
    }

    private Predicate<HamletLicenseInfo> licenseIsNew(Map<String, PluginLicense> existingLicenses) {
        return newLicense -> this.licenseIsNew((HamletLicenseInfo)newLicense, Option.option(existingLicenses.get(newLicense.getKey())));
    }

    private boolean licenseIsNew(HamletLicenseInfo newLicense, Option<PluginLicense> existingLicense) {
        Iterator<PluginLicense> iterator = existingLicense.iterator();
        if (iterator.hasNext()) {
            PluginLicense existing = iterator.next();
            return !this.normalizeLicense(existing.getRawLicense()).equals(this.normalizeLicense(newLicense.getLicense()));
        }
        return true;
    }
}

