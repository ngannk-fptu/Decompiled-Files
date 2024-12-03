/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.api.PropertySet
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core.property;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.PropertySet;
import com.atlassian.applinks.core.property.ApplicationLinkProperties;
import com.atlassian.applinks.core.property.EntityLinkProperties;
import com.atlassian.applinks.core.property.HashingLongPropertyKeysPluginSettings;
import com.atlassian.applinks.core.property.PropertyService;
import com.atlassian.applinks.core.property.SalPropertySet;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SalPropertyService
implements PropertyService {
    private final PluginSettingsFactory pluginSettingsFactory;
    private static final String APPLINKS = "applinks.";
    private static final String APPLINKS_GLOBAL_PREFIX = "applinks.global";
    private static final String APPLICATION_ADMIN_PREFIX = "applinks.admin";
    private static final String APPLICATION_PREFIX = "applinks.application";
    private static final String ENTITY_PREFIX = "applinks.entity";
    private static final String LOCAL_ENTITY_PREFIX = "applinks.local";

    @Autowired
    public SalPropertyService(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public PropertySet getProperties(ApplicationLink application) {
        return this.getPropertySet(this.key(application.getId()));
    }

    @Override
    public EntityLinkProperties getProperties(EntityLink entity) {
        return new EntityLinkProperties(this.getPropertySet(this.key(entity)));
    }

    protected PropertySet getPropertySet(String key) {
        return new SalPropertySet(new HashingLongPropertyKeysPluginSettings(this.pluginSettingsFactory.createGlobalSettings()), key);
    }

    private String key(ApplicationId applicationId) {
        return String.format("%s.%s", APPLICATION_PREFIX, SalPropertyService.escape(applicationId.get()));
    }

    private String key(EntityLink entity) {
        return String.format("%s.%s.%s.%s", ENTITY_PREFIX, SalPropertyService.escape(entity.getApplicationLink().getId().get()), SalPropertyService.escape(TypeId.getTypeId((EntityType)entity.getType()).get()), SalPropertyService.escape(entity.getKey()));
    }

    @Override
    public ApplicationLinkProperties getApplicationLinkProperties(ApplicationId id) {
        return new ApplicationLinkProperties(this.getPropertySet(String.format("%s.%s", APPLICATION_ADMIN_PREFIX, SalPropertyService.escape(id.get()))), this.getPropertySet(this.key(id)));
    }

    @Override
    public PropertySet getGlobalAdminProperties() {
        return this.getPropertySet(APPLINKS_GLOBAL_PREFIX);
    }

    @Override
    public PropertySet getLocalEntityProperties(String localEntityKey, TypeId localEntityTypeId) {
        return this.getPropertySet(String.format("%s.%s.%s", LOCAL_ENTITY_PREFIX, SalPropertyService.escape(localEntityKey), SalPropertyService.escape(localEntityTypeId.get())));
    }

    private static String escape(String s) {
        return s.replaceAll("_", "__").replaceAll("\\.", "_");
    }
}

