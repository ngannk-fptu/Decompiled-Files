/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Option
 *  com.atlassian.fugue.Options
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker$Customizer
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.java.ao.DBParam
 *  net.java.ao.RawEntity
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.log4j.Logger
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.plugin.notifications.config;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Options;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.notifications.api.ErrorCollection;
import com.atlassian.plugin.notifications.api.event.ServerConfigurationEvent;
import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.notification.NotificationStatusRepresentation;
import com.atlassian.plugin.notifications.config.DefaultServerConfiguration;
import com.atlassian.plugin.notifications.config.ServerConfigurationManager;
import com.atlassian.plugin.notifications.config.ao.ServerConfig;
import com.atlassian.plugin.notifications.config.ao.ServerParam;
import com.atlassian.plugin.notifications.module.NotificationMediumManager;
import com.atlassian.plugin.notifications.module.NotificationMediumModuleDescriptor;
import com.atlassian.plugin.notifications.util.PasswordEncrypter;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.java.ao.DBParam;
import net.java.ao.RawEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;

public class DefaultServerConfigurationManager
implements ServerConfigurationManager {
    private static final Logger log = Logger.getLogger(DefaultServerConfigurationManager.class);
    private static final String PASSWORD_PARAM = "password";
    private static final String CACHE_NAME = DefaultServerConfigurationManager.class.getName() + ".serverConfigurations";
    private static final String NOTIFICATIONS_ENABLED_KEY = "atlassian.notifications.enabled";
    public static final String CUSTOM_PATH_REGEX = "^[\\p{Alnum}\\/\\\\\\{\\}]+$";
    private static final String GROUP_SEPARATOR = ",";
    private final NotificationMediumManager notificationMediumManager;
    private final EventPublisher eventPublisher;
    private final I18nResolver i18n;
    private final PasswordEncrypter passwordEncrypter;
    private final UserManager userManager;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ActiveObjects ao;
    private final PluginModuleTracker<NotificationMedium, NotificationMediumModuleDescriptor> mediumTracker;
    private final ServerConfigurationsSupplier serverConfigurationsSupplier;

    public DefaultServerConfigurationManager(ActiveObjects ao, NotificationMediumManager notificationMediumManager, EventPublisher eventPublisher, @Qualifier(value="i18nResolver") I18nResolver i18nResolver, PasswordEncrypter passwordEncrypter, UserManager userManager, PluginSettingsFactory pluginSettingsFactory, PluginAccessor pluginAccessor, PluginEventManager pluginEventManager) {
        this.ao = ao;
        this.notificationMediumManager = notificationMediumManager;
        this.eventPublisher = eventPublisher;
        this.i18n = i18nResolver;
        this.passwordEncrypter = passwordEncrypter;
        this.userManager = userManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.mediumTracker = new DefaultPluginModuleTracker(pluginAccessor, pluginEventManager, NotificationMediumModuleDescriptor.class, (PluginModuleTracker.Customizer)new PluginModuleTracker.Customizer<NotificationMedium, NotificationMediumModuleDescriptor>(){

            public NotificationMediumModuleDescriptor adding(NotificationMediumModuleDescriptor descriptor) {
                if (descriptor.isConfigStatic()) {
                    return descriptor;
                }
                return null;
            }

            public void removed(NotificationMediumModuleDescriptor descriptor) {
            }
        });
        this.serverConfigurationsSupplier = new ServerConfigurationsSupplier();
    }

    @Override
    public Either<ErrorCollection, ServerConfigurationManager.ServerValidationResult> validateAddServer(ServerConfigurationManager.CommonServerConfig commonConfig, Map<String, String> params) {
        String notificationMedium;
        NotificationMedium medium;
        String customTemplatePath;
        ErrorCollection errors = new ErrorCollection();
        String name = commonConfig.getName();
        if (StringUtils.isBlank((CharSequence)name)) {
            errors.addError("server-name", this.i18n.getText("notifications.plugin.server.name.blank.error"));
        }
        if (StringUtils.isNotBlank((CharSequence)(customTemplatePath = commonConfig.getCustomTemplatePath())) && !customTemplatePath.matches(CUSTOM_PATH_REGEX)) {
            errors.addError("customTemplatePath", this.i18n.getText("notifications.plugin.custom.template.path.invalid"));
        }
        if ((medium = this.notificationMediumManager.getNotificationMedium(notificationMedium = commonConfig.getNotificationMedium())) == null) {
            errors.addErrorMessage(this.i18n.getText("notifications.plugin.invalid.medium"));
        } else {
            try {
                errors.addErrorCollection(medium.validateAddConfiguration(this.i18n, params));
            }
            catch (RuntimeException e) {
                errors.addErrorMessage(this.i18n.getText("notifications.plugin.unknown.error.validating.config"));
                log.error((Object)("Unknown error validating server '" + name + "'"), (Throwable)e);
            }
        }
        if (StringUtils.isBlank((CharSequence)params.get("template.user.id"))) {
            params.put("template.user.id", "{userName}");
        }
        if (errors.hasAnyErrors()) {
            return Either.left((Object)errors);
        }
        return Either.right((Object)new ServerConfigurationManager.ServerValidationResult(commonConfig, params));
    }

    @Override
    public ServerConfiguration addServer(ServerConfigurationManager.ServerValidationResult result) {
        try {
            ServerConfigurationManager.CommonServerConfig commonConfig = result.getCommonConfig();
            String groupString = StringUtils.join(commonConfig.getGroupsWithAccess().iterator(), (String)GROUP_SEPARATOR);
            ServerConfig serverConfig = (ServerConfig)this.ao.create(ServerConfig.class, new DBParam[]{new DBParam("NOTIFICATION_MEDIUM_KEY", (Object)commonConfig.getNotificationMedium()), new DBParam("SERVER_NAME", (Object)commonConfig.getName()), new DBParam("ENABLED_FOR_ALL_USERS", (Object)commonConfig.isEnabledForAll()), new DBParam("DEFAULT_USER_ID_TEMPLATE", (Object)result.getParams().get("template.user.id")), new DBParam("CUSTOM_TEMPLATE_PATH", (Object)commonConfig.getCustomTemplatePath()), new DBParam("GROUPS_WITH_ACCESS", (Object)groupString)});
            for (Map.Entry<String, String> paramEntry : result.getParams().entrySet()) {
                this.ao.create(ServerParam.class, new DBParam[]{new DBParam("PARAM_KEY", (Object)paramEntry.getKey()), new DBParam("PARAM_VALUE", (Object)this.encryptPasswordParam(paramEntry)), new DBParam("SERVER_CONFIG_ID", (Object)serverConfig.getID())});
            }
            int newId = serverConfig.getID();
            ServerConfiguration config = this.getServer(newId);
            this.eventPublisher.publish((Object)new ServerConfigurationEvent(ServerConfigurationEvent.ConfigEventType.CREATED, newId, config));
            return config;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeServer(int id) {
        ServerConfig serverConfig = (ServerConfig)this.ao.get(ServerConfig.class, (Object)id);
        this.ao.delete((RawEntity[])serverConfig.getServerParams());
        this.ao.delete(new RawEntity[]{serverConfig});
        this.eventPublisher.publish((Object)new ServerConfigurationEvent(ServerConfigurationEvent.ConfigEventType.REMOVED, id, null));
    }

    @Override
    public Either<ErrorCollection, ServerConfigurationManager.ServerValidationResult> validateUpdateServer(ServerConfigurationManager.CommonServerConfig commonConfig, Map<String, String> params) {
        NotificationMedium medium;
        ErrorCollection errors = new ErrorCollection();
        int id = commonConfig.getId();
        ServerConfiguration server = this.getServer(id);
        if (server == null) {
            errors.addErrorMessage(this.i18n.getText("notifications.plugin.server.error.none", new Serializable[]{Integer.valueOf(id)}));
            return Either.left((Object)errors);
        }
        String customTemplatePath = commonConfig.getCustomTemplatePath();
        if (StringUtils.isNotBlank((CharSequence)customTemplatePath) && !customTemplatePath.matches(CUSTOM_PATH_REGEX)) {
            errors.addError("customTemplatePath", this.i18n.getText("notifications.plugin.custom.template.path.invalid"));
        }
        if ((medium = server.getNotificationMedium()) == null) {
            errors.addErrorMessage(this.i18n.getText("notifications.plugin.invalid.medium"));
        } else {
            errors.addErrorCollection(medium.validateAddConfiguration(this.i18n, params));
        }
        if (errors.hasAnyErrors()) {
            return Either.left((Object)errors);
        }
        return Either.right((Object)new ServerConfigurationManager.ServerValidationResult(commonConfig, params));
    }

    @Override
    public ServerConfiguration updateServer(ServerConfigurationManager.ServerValidationResult result) {
        ServerConfigurationManager.CommonServerConfig commonConfig = result.getCommonConfig();
        ServerConfig serverConfig = (ServerConfig)this.ao.get(ServerConfig.class, (Object)commonConfig.getId());
        ServerParam[] serverParams = serverConfig.getServerParams();
        serverConfig.setEnabledForAllUsers(commonConfig.isEnabledForAll());
        serverConfig.setName(commonConfig.getName());
        serverConfig.setCustomTemplatePath(commonConfig.getCustomTemplatePath());
        String groupString = StringUtils.join(commonConfig.getGroupsWithAccess().iterator(), (String)GROUP_SEPARATOR);
        serverConfig.setGroupsWithAccess(groupString);
        if (result.getParams().containsKey("template.user.id")) {
            serverConfig.setDefaultUserIdTemplate(result.getParams().get("template.user.id"));
        }
        Map<String, String> newParams = result.getParams();
        HashMap keyToParam = Maps.newHashMap();
        for (ServerParam serverParam : serverParams) {
            if (!newParams.containsKey(serverParam.getParamKey())) {
                this.ao.delete(new RawEntity[]{serverParam});
                continue;
            }
            keyToParam.put(serverParam.getParamKey(), serverParam);
        }
        for (Map.Entry entry : result.getParams().entrySet()) {
            if (keyToParam.containsKey(entry.getKey())) {
                ServerParam serverParam = (ServerParam)keyToParam.get(entry.getKey());
                serverParam.setParamValue(this.encryptPasswordParam(entry));
                serverParam.save();
                continue;
            }
            this.ao.create(ServerParam.class, new DBParam[]{new DBParam("PARAM_KEY", entry.getKey()), new DBParam("PARAM_VALUE", (Object)this.encryptPasswordParam(entry)), new DBParam("SERVER_CONFIG_ID", (Object)serverConfig.getID())});
        }
        serverConfig.save();
        ServerConfiguration config = this.getServer(serverConfig.getID());
        this.eventPublisher.publish((Object)new ServerConfigurationEvent(ServerConfigurationEvent.ConfigEventType.UPDATED, commonConfig.getId(), config));
        return config;
    }

    @Override
    public Iterable<ServerConfiguration> getServers() {
        Iterable staticConfigurations = Options.flatten((Iterable)Iterables.transform((Iterable)this.mediumTracker.getModuleDescriptors(), (Function)new Function<NotificationMediumModuleDescriptor, Option<ServerConfiguration>>(){

            public Option<ServerConfiguration> apply(NotificationMediumModuleDescriptor input) {
                return input.getModule().getStaticConfiguration();
            }
        }));
        return Iterables.concat(this.serverConfigurationsSupplier.get(), (Iterable)staticConfigurations);
    }

    @Override
    public Iterable<ServerConfiguration> getServersForIndividual() {
        Iterable<ServerConfiguration> servers = this.getServers();
        return Sets.newLinkedHashSet((Iterable)Iterables.filter(servers, (Predicate)new Predicate<ServerConfiguration>(){

            public boolean apply(@Nullable ServerConfiguration input) {
                return input != null && input.getNotificationMedium().isIndividualNotificationSupported();
            }
        }));
    }

    @Override
    public ServerConfiguration getServer(int id) {
        ServerConfig serverConfig = (ServerConfig)this.ao.get(ServerConfig.class, (Object)id);
        if (serverConfig == null) {
            return null;
        }
        return this.transformConfig(serverConfig);
    }

    @Override
    public ErrorCollection validateToggleNotifications(String loggedInUser, boolean enabled) {
        NotificationStatusRepresentation notificationStatus;
        ErrorCollection errors = new ErrorCollection();
        if (!this.userManager.isSystemAdmin(loggedInUser)) {
            errors.addErrorMessage(this.i18n.getText("perm.violation.desc"), ErrorCollection.Reason.FORBIDDEN);
        }
        if (!(notificationStatus = this.getNotificationStatus()).isEnabled() && StringUtils.isNotBlank((CharSequence)notificationStatus.getJvmArg())) {
            errors.addErrorMessage(this.i18n.getText("notifications.plugin.error.notifications.status"), ErrorCollection.Reason.VALIDATION_FAILED);
        }
        return errors;
    }

    @Override
    public void toggleNotifications(boolean enabled) {
        this.pluginSettingsFactory.createGlobalSettings().put(NOTIFICATIONS_ENABLED_KEY, (Object)Boolean.toString(enabled));
    }

    @Override
    public NotificationStatusRepresentation getNotificationStatus() {
        String enabledString = (String)this.pluginSettingsFactory.createGlobalSettings().get(NOTIFICATIONS_ENABLED_KEY);
        boolean enabledSetting = enabledString == null || Boolean.parseBoolean(enabledString);
        boolean jvmDisabled = Boolean.getBoolean("atlassian.mail.senddisabled") || Boolean.getBoolean("atlassian.notifications.disabled");
        boolean enabled = !jvmDisabled && enabledSetting;
        String jvmArg = null;
        if (jvmDisabled) {
            jvmArg = Boolean.getBoolean("atlassian.mail.senddisabled") ? "atlassian.mail.senddisabled" : "atlassian.notifications.disabled";
        }
        return new NotificationStatusRepresentation(enabled, jvmArg);
    }

    private DefaultServerConfiguration transformConfig(ServerConfig serverConfig) {
        NotificationMedium notificationMedium = this.notificationMediumManager.getNotificationMedium(serverConfig.getNotificationMediumKey());
        if (notificationMedium == null) {
            return null;
        }
        HashMap params = Maps.newHashMap();
        for (ServerParam serverParam : serverConfig.getServerParams()) {
            params.put(serverParam.getParamKey(), this.decryptPasswordParam(serverParam));
        }
        String[] groups = StringUtils.split((String)serverConfig.getGroupsWithAccess(), (String)GROUP_SEPARATOR);
        return new DefaultServerConfiguration(serverConfig.getID(), serverConfig.getName(), serverConfig.getNotificationMediumKey(), serverConfig.isEnabledForAllUsers(), serverConfig.getDefaultUserIdTemplate(), params, this.notificationMediumManager, serverConfig.getCustomTemplatePath(), Arrays.asList(groups));
    }

    private Set<DefaultServerConfiguration> transformConfigs(ServerConfig[] serverConfigs) {
        LinkedHashSet ret = Sets.newLinkedHashSet();
        for (ServerConfig serverConfig : serverConfigs) {
            DefaultServerConfiguration config = this.transformConfig(serverConfig);
            if (config == null) continue;
            ret.add(config);
        }
        return ret;
    }

    private String decryptPasswordParam(ServerParam serverParam) {
        String value = serverParam.getParamValue();
        if (StringUtils.containsIgnoreCase((CharSequence)serverParam.getParamKey(), (CharSequence)PASSWORD_PARAM)) {
            value = this.passwordEncrypter.decrypt(value);
        }
        return value;
    }

    private String encryptPasswordParam(Map.Entry<String, String> paramEntry) {
        String value = paramEntry.getValue();
        if (StringUtils.containsIgnoreCase((CharSequence)paramEntry.getKey(), (CharSequence)PASSWORD_PARAM)) {
            value = this.passwordEncrypter.encrypt(value);
        }
        return value;
    }

    private class ServerConfigurationsSupplier
    implements Supplier<ImmutableList<DefaultServerConfiguration>> {
        private ServerConfigurationsSupplier() {
        }

        @Override
        public ImmutableList<DefaultServerConfiguration> get() {
            Set storedConfigurations = DefaultServerConfigurationManager.this.transformConfigs((ServerConfig[])DefaultServerConfigurationManager.this.ao.find(ServerConfig.class));
            return ImmutableList.builder().addAll((Iterable)storedConfigurations).build();
        }
    }
}

