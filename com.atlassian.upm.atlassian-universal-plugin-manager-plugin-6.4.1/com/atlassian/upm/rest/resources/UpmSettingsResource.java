/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmSettings;
import com.atlassian.upm.analytics.event.SettingsChangedEvent;
import com.atlassian.upm.core.analytics.AnalyticsLogger;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import com.atlassian.upm.rest.representations.UpmSettingsCollectionRepresentation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/settings")
public class UpmSettingsResource {
    private static final Logger log = LoggerFactory.getLogger(UpmSettingsResource.class);
    private final UpmRepresentationFactory factory;
    private final PermissionEnforcer permissionEnforcer;
    private final SysPersisted sysPersisted;
    private final AnalyticsLogger analytics;

    public UpmSettingsResource(UpmRepresentationFactory factory, PermissionEnforcer permissionEnforcer, SysPersisted sysPersisted, AnalyticsLogger analytics) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.factory = Objects.requireNonNull(factory, "factory");
        this.sysPersisted = Objects.requireNonNull(sysPersisted, "sysPersisted");
        this.analytics = Objects.requireNonNull(analytics, "analytics");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response get() {
        this.permissionEnforcer.enforcePermission(Permission.GET_SETTINGS);
        return Response.ok((Object)this.factory.createUpmSettingsCollectionRepresentation()).build();
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins+json"})
    public Response put(UpmSettingsCollectionRepresentation settingsRep) {
        Permission permission = Permission.MANAGE_ON_PREMISE_SETTINGS;
        this.permissionEnforcer.enforcePermission(permission);
        Collection<UpmSettingsCollectionRepresentation.UpmSettingRepresentation> settingsCollection = settingsRep.getSettings();
        for (UpmSettingsCollectionRepresentation.UpmSettingRepresentation rep : settingsCollection) {
            UpmSettings setting = UpmSettings.withKey(rep.getKey());
            this.permissionEnforcer.enforcePermission(setting.getPermission());
        }
        Map<UpmSettings, String> changedValues = this.getChangedValues(settingsCollection);
        if (!this.sysPersisted.is(UpmSettings.PAC_DISABLED) && changedValues.containsKey((Object)UpmSettings.PAC_DISABLED)) {
            this.sendChangedSettingsToAnalytics(changedValues);
        }
        for (Map.Entry<UpmSettings, String> changed : changedValues.entrySet()) {
            this.sysPersisted.set(changed.getKey(), Boolean.valueOf(changed.getValue()));
        }
        this.sendChangedSettingsToAnalytics(changedValues);
        return Response.ok().build();
    }

    private Map<UpmSettings, String> getChangedValues(Iterable<UpmSettingsCollectionRepresentation.UpmSettingRepresentation> settings) {
        HashMap<UpmSettings, String> changedValues = new HashMap<UpmSettings, String>();
        for (UpmSettingsCollectionRepresentation.UpmSettingRepresentation rep : settings) {
            UpmSettings setting = UpmSettings.withKey(rep.getKey());
            Boolean valueToSet = rep.getValue();
            if (valueToSet.booleanValue() == this.sysPersisted.is(setting)) continue;
            changedValues.put(setting, String.valueOf(valueToSet));
        }
        return Collections.unmodifiableMap(changedValues);
    }

    private void sendChangedSettingsToAnalytics(Map<UpmSettings, String> cv) {
        if (!cv.isEmpty()) {
            this.analytics.log(new SettingsChangedEvent(cv));
        }
    }
}

