/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.LocaleResolver
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.RequestContext;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.license.PluginLicenses;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.pac.AvailableAddonWithVersion;
import com.atlassian.upm.pac.AvailableAddonWithVersionBase;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path(value="/purchased/available")
public class PurchasedPluginCollectionResource {
    private final UpmRepresentationFactory representationFactory;
    private final PermissionEnforcer permissionEnforcer;
    private final LocaleResolver localeResolver;
    private final PacClient pacClient;
    private final PluginRetriever pluginRetriever;
    private final PluginLicenseRepository licenseRepository;
    private final ApplicationPluginsManager applicationPluginsManager;
    private static final Function<AvailableAddonWithVersionBase, String> pluginSummaryToKey = a -> a.getAddonBase().getKey();

    public PurchasedPluginCollectionResource(UpmRepresentationFactory representationFactory, PermissionEnforcer permissionEnforcer, LocaleResolver localeResolver, PacClient pacClient, PluginRetriever pluginRetriever, PluginLicenseRepository licenseRepository, ApplicationPluginsManager applicationPluginsManager) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.localeResolver = Objects.requireNonNull(localeResolver, "localeResolver");
        this.pacClient = Objects.requireNonNull(pacClient, "pacClient");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.licenseRepository = Objects.requireNonNull(licenseRepository, "licenseRepository");
        this.applicationPluginsManager = Objects.requireNonNull(applicationPluginsManager, "licensingUsageVerifier");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response get(@Context HttpServletRequest request) {
        this.permissionEnforcer.enforcePermission(Permission.GET_PURCHASED_PLUGINS);
        Set<String> applicationPluginKeys = this.applicationPluginsManager.getApplicationRelatedPlugins(StreamSupport.stream(this.pluginRetriever.getPlugins().spliterator(), false).map(Plugins.toPlugPlugin).collect(Collectors.toList())).keySet();
        Predicate<PluginLicense> isApplicationPluginLicense = license -> applicationPluginKeys.contains(license.getPluginKey());
        List licenses = Collections.unmodifiableList(StreamSupport.stream(this.licenseRepository.getPluginLicenses().spliterator(), false).filter(PluginLicenses.isEvaluation().negate()).filter(PluginLicenses.isEmbeddedWithinHostLicense().negate()).filter(isApplicationPluginLicense.negate()).collect(Collectors.toList()));
        Map licenseMap = licenses.stream().collect(Collectors.toMap(PluginLicense::getPluginKey, Function.identity()));
        boolean pacUnreachable = !this.pacClient.isPacReachable();
        List<AvailableAddonWithVersion> plugins = Collections.unmodifiableList(new ArrayList<AvailableAddonWithVersion>(this.pacClient.getPlugins(licenseMap.keySet())));
        Comparator<AvailableAddonWithVersionBase> ordering = this.orderingWithInstalledPluginsLast().thenComparing(this.orderingByPluginName(this.localeResolver.getLocale(request)));
        Set compatiblePluginKeysReturnedByMpac = Collections.unmodifiableSet(plugins.stream().map(pluginSummaryToKey).collect(Collectors.toSet()));
        Set<String> incompatibleOrUnknownPluginKeys = licenseMap.keySet().stream().filter(plugin -> !compatiblePluginKeysReturnedByMpac.contains(plugin)).collect(Collectors.toSet());
        List<AvailableAddonWithVersionBase> incompatiblePlugins = Collections.unmodifiableList(new ArrayList<AvailableAddonWithVersion>(this.pacClient.getLatestVersionOfPlugins(incompatibleOrUnknownPluginKeys)));
        Set incompatiblePluginKeysReturnedByMpac = Collections.unmodifiableSet(incompatiblePlugins.stream().map(pluginSummaryToKey).collect(Collectors.toSet()));
        Set<String> unknownPluginKeys = incompatibleOrUnknownPluginKeys.stream().filter(plugin -> !incompatiblePluginKeysReturnedByMpac.contains(plugin)).collect(Collectors.toSet());
        return Response.ok((Object)this.representationFactory.createPurchasedPluginCollectionRepresentation(this.localeResolver.getLocale(request), plugins.stream().sorted(ordering).collect(Collectors.toList()), incompatiblePlugins, unknownPluginKeys, new RequestContext(request).pacUnreachable(pacUnreachable))).build();
    }

    private Comparator<AvailableAddonWithVersionBase> orderingWithInstalledPluginsLast() {
        return (a, b) -> {
            boolean bInstalled;
            boolean aInstalled = this.pluginRetriever.isPluginInstalled(a.getAddonBase().getKey());
            if (aInstalled == (bInstalled = this.pluginRetriever.isPluginInstalled(b.getAddonBase().getKey()))) {
                return 0;
            }
            return aInstalled ? 1 : -1;
        };
    }

    private Comparator<AvailableAddonWithVersionBase> orderingByPluginName(Locale locale) {
        Collator collator = Collator.getInstance(locale);
        return (a, b) -> collator.compare(a.getAddonBase().getName(), b.getAddonBase().getName());
    }
}

