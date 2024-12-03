/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.api.application.refapp.RefAppApplicationType
 *  com.atlassian.applinks.api.application.refapp.RefAppCharlieEntityType
 *  com.atlassian.applinks.host.spi.AbstractInternalHostApplication
 *  com.atlassian.applinks.host.spi.DefaultEntityReference
 *  com.atlassian.applinks.host.spi.EntityReference
 *  com.atlassian.applinks.host.util.InstanceNameGenerator
 *  com.atlassian.net.NetworkUtils
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.project.ProjectManager
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core.refapp;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.application.refapp.RefAppApplicationType;
import com.atlassian.applinks.api.application.refapp.RefAppCharlieEntityType;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.host.spi.AbstractInternalHostApplication;
import com.atlassian.applinks.host.spi.DefaultEntityReference;
import com.atlassian.applinks.host.spi.EntityReference;
import com.atlassian.applinks.host.util.InstanceNameGenerator;
import com.atlassian.net.NetworkUtils;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.RefappOnly;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.project.ProjectManager;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Conditional(value={RefappOnly.class})
public class RefAppInternalHostApplication
extends AbstractInternalHostApplication
implements LifecycleAware {
    private static final Logger logger = LoggerFactory.getLogger(RefAppInternalHostApplication.class);
    public static final String BACKDOOR_REFAPP_APPLICATION_TYPE = "refapp.applinks.applicationType";
    public static final String BACKDOOR_REFAPP_BASEURL = "refapp.baseurl";
    public static final String REFAPP_PREFIX = "com.atlassian.applinks.host.refapp";
    public static final String INSTANCE_NAME_KEY = "com.atlassian.applinks.host.refapp.instanceName";
    public static final String SERVER_ID = "com.atlassian.applinks.host.refapp.serverId";
    private final ApplicationProperties applicationProperties;
    private final ProjectManager projectManager;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final UserManager userManager;
    private final InternalTypeAccessor typeAccessor;
    private final PluginSettings pluginSettings;
    private final Supplier<URI> baseUrl = Suppliers.memoize((Supplier)new Supplier<URI>(){

        public URI get() {
            URI url;
            String storedBaseUrl = RefAppInternalHostApplication.this.applicationProperties.getBaseUrl();
            try {
                url = new URI(storedBaseUrl);
            }
            catch (URISyntaxException e) {
                throw new RuntimeException(String.format("ApplicationProperties.getBaseUrl() returned invalid URI (%s). Reason: %s", storedBaseUrl, e.getReason()));
            }
            try {
                URL baseUrl = new URL(storedBaseUrl);
                if ("localhost".equalsIgnoreCase(baseUrl.getHost())) {
                    url = new URL(baseUrl.getProtocol(), NetworkUtils.getLocalHostName().toLowerCase(Locale.US), baseUrl.getPort(), baseUrl.getFile()).toURI();
                }
            }
            catch (Exception e) {
                logger.error("Failed to resolve local hostname. Returning localhost.", (Throwable)e);
            }
            return url;
        }
    });

    @Autowired
    public RefAppInternalHostApplication(PluginSettingsFactory pluginSettingsFactory, ApplicationProperties applicationProperties, PluginAccessor pluginAccessor, ProjectManager projectManager, InternalTypeAccessor typeAccessor, UserManager userManager) {
        super(pluginAccessor);
        this.applicationProperties = applicationProperties;
        this.projectManager = projectManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.userManager = userManager;
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
        this.typeAccessor = typeAccessor;
    }

    public URI getBaseUrl() {
        String baseUrlFromProperties = System.getProperty(BACKDOOR_REFAPP_BASEURL);
        if (baseUrlFromProperties == null) {
            return (URI)this.baseUrl.get();
        }
        try {
            return new URI(baseUrlFromProperties);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void onStart() {
        if (StringUtils.isEmpty((CharSequence)((String)this.pluginSettings.get(SERVER_ID)))) {
            this.pluginSettings.put(SERVER_ID, (Object)UUID.randomUUID().toString());
        }
        if (StringUtils.isEmpty((CharSequence)((String)this.pluginSettings.get(INSTANCE_NAME_KEY)))) {
            String baseUrl = this.applicationProperties.getBaseUrl();
            String instanceName = "RefApp";
            if (!StringUtils.isEmpty((CharSequence)baseUrl)) {
                try {
                    instanceName = instanceName + " - " + new InstanceNameGenerator().generateInstanceName(this.applicationProperties.getBaseUrl());
                }
                catch (MalformedURLException malformedURLException) {
                    // empty catch block
                }
            }
            this.pluginSettings.put(INSTANCE_NAME_KEY, (Object)instanceName);
        }
    }

    public void onStop() {
    }

    public String getName() {
        return (String)this.pluginSettings.get(INSTANCE_NAME_KEY);
    }

    public ApplicationType getType() {
        Class<? extends ApplicationType> appType = RefAppInternalHostApplication.getApplicationTypeClass();
        return Objects.requireNonNull(this.typeAccessor.getApplicationType(appType), appType.getName() + " is not installed!");
    }

    public boolean doesEntityExist(String key, Class<? extends EntityType> type) {
        return RefAppCharlieEntityType.class.isAssignableFrom(type) && this.projectManager.getAllProjectKeys().contains(key);
    }

    public boolean doesEntityExistNoPermissionCheck(String key, Class<? extends EntityType> type) {
        return this.doesEntityExist(key, type);
    }

    public EntityReference toEntityReference(Object domainObject) {
        if (!(domainObject instanceof String)) {
            throw new IllegalArgumentException("RefApp has no domain object, use a String key");
        }
        String key = (String)domainObject;
        if (!this.projectManager.getAllProjectKeys().contains(key)) {
            throw new IllegalArgumentException("Entity with key " + key + " does not exist");
        }
        return this.toEntityReference(key, RefAppCharlieEntityType.class);
    }

    public EntityReference toEntityReference(String key, Class<? extends EntityType> type) {
        String name = (String)this.pluginSettingsFactory.createSettingsForKey(key).get("charlie.name");
        return new DefaultEntityReference(key, name, Objects.requireNonNull(this.typeAccessor.getEntityType(RefAppCharlieEntityType.class), "Couldn't load RefAppCharlieEntityType"));
    }

    public Iterable<EntityReference> getLocalEntities() {
        return Iterables.transform((Iterable)this.projectManager.getAllProjectKeys(), (Function)new Function<String, EntityReference>(){

            public EntityReference apply(String key) {
                return RefAppInternalHostApplication.this.toEntityReference(key, RefAppCharlieEntityType.class);
            }
        });
    }

    public URI getDocumentationBaseUrl() {
        return URIUtil.uncheckedCreate("http://confluence.atlassian.com/display/APPLINKS");
    }

    public boolean canManageEntityLinksFor(EntityReference entityReference) {
        String username = this.userManager.getRemoteUsername();
        return username != null && this.userManager.isAdmin(username);
    }

    public ApplicationId getId() {
        return new ApplicationId((String)this.pluginSettings.get(SERVER_ID));
    }

    public boolean hasPublicSignup() {
        return false;
    }

    private static Class<? extends ApplicationType> getApplicationTypeClass() {
        String typeClass = System.getProperty(BACKDOOR_REFAPP_APPLICATION_TYPE);
        try {
            if (typeClass != null) {
                Class<?> applicationTypeClass = Class.forName(typeClass);
                Preconditions.checkState((boolean)ApplicationType.class.isAssignableFrom(applicationTypeClass), (Object)(applicationTypeClass.getName() + "does not implement ApplicationType"));
                return applicationTypeClass;
            }
        }
        catch (ClassNotFoundException e) {
            logger.warn("Cannot load backdoor application type class '{}'", (Object)typeClass);
        }
        return RefAppApplicationType.class;
    }
}

