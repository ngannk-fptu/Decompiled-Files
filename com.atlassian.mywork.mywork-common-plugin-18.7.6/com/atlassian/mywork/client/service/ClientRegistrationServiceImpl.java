/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.concurrent.GuardedBy
 *  org.apache.commons.io.IOUtils
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.client.service;

import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.fugue.Option;
import com.atlassian.mywork.client.listener.ServiceListener;
import com.atlassian.mywork.model.Registration;
import com.atlassian.mywork.model.RegistrationBuilder;
import com.atlassian.mywork.service.ClientRegistrationService;
import com.atlassian.mywork.service.LocaleService;
import com.atlassian.mywork.service.RegistrationProvider;
import com.atlassian.mywork.service.RegistrationService;
import com.atlassian.mywork.util.Executors;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.ImmutableList;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.concurrent.GuardedBy;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRegistrationServiceImpl
implements ClientRegistrationService,
LifecycleAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientRegistrationServiceImpl.class);
    private final PluginAccessor pluginAccessor;
    private final I18nResolver i18nResolver;
    private final LocaleService localeService;
    private final ServiceListener serviceListener;
    private final RegistrationService registrationService;
    private final InternalHostApplication hostApplication;
    private final Map<String, ScheduledFuture> scheduledPlugins = new ConcurrentHashMap<String, ScheduledFuture>();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor("registrationExecutor");
    @GuardedBy(value="this")
    private Closeable closeableListener;

    public ClientRegistrationServiceImpl(PluginAccessor pluginAccessor, I18nResolver i18nResolver, LocaleService localeService, ServiceListener serviceListener, RegistrationService registrationService, InternalHostApplication hostApplication) {
        this.pluginAccessor = pluginAccessor;
        this.i18nResolver = i18nResolver;
        this.localeService = localeService;
        this.serviceListener = serviceListener;
        this.registrationService = registrationService;
        this.hostApplication = hostApplication;
    }

    @Override
    public synchronized Iterable<Registration> createRegistrations() {
        if (this.closeableListener != null) {
            try {
                this.closeableListener.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        AtomicBoolean collecting = new AtomicBoolean(true);
        ConcurrentLinkedQueue providers = new ConcurrentLinkedQueue();
        this.closeableListener = this.serviceListener.addListener(RegistrationProvider.class, provider -> {
            if (collecting.get()) {
                providers.add(provider);
            } else {
                this.registerAsynchronously(provider.getPluginId());
            }
            return null;
        });
        collecting.set(false);
        ArrayList<Registration> registrations = new ArrayList<Registration>();
        for (RegistrationProvider provider2 : providers) {
            try {
                registrations.add(this.createRegistration(provider2));
            }
            catch (IncompatibleClassChangeError e) {
                LOGGER.warn("Provider is not compatible with this version of MyWork: " + e.getMessage(), (Throwable)e);
            }
        }
        providers.clear();
        return registrations;
    }

    private void registerAsynchronously(final String pluginKey) {
        ScheduledFuture scheduledPlugin = this.scheduledPlugins.get(pluginKey);
        if (scheduledPlugin != null) {
            scheduledPlugin.cancel(false);
        }
        this.scheduledPlugins.put(pluginKey, this.executor.schedule(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                try {
                    Plugin plugin = ClientRegistrationServiceImpl.this.pluginAccessor.getEnabledPlugin(pluginKey);
                    if (plugin == null) {
                        return;
                    }
                    List descriptors = plugin.getModuleDescriptorsByModuleClass(RegistrationProvider.class);
                    for (ModuleDescriptor descriptor : descriptors) {
                        try {
                            RegistrationProvider provider = (RegistrationProvider)descriptor.getModule();
                            ClientRegistrationServiceImpl.this.register(provider);
                        }
                        catch (IncompatibleClassChangeError e) {
                            LOGGER.warn("Provider is not compatible with this version of MyWork: " + e.getMessage(), (Throwable)e);
                        }
                        if (!Thread.interrupted()) continue;
                        return;
                    }
                }
                finally {
                    ClientRegistrationServiceImpl.this.scheduledPlugins.remove(pluginKey);
                }
            }
        }, 10L, TimeUnit.SECONDS));
    }

    private void register(RegistrationProvider provider) {
        LOGGER.debug("Registering " + provider.getClass().getName());
        Registration registration = this.createRegistration(provider);
        this.registrationService.register((Iterable<Registration>)ImmutableList.of((Object)registration));
    }

    @Override
    public Registration createRegistration(RegistrationProvider provider) {
        RegistrationBuilder builder = new RegistrationBuilder(new Registration.RegistrationId(provider.getApplication(), ""));
        try {
            Plugin plugin = this.pluginAccessor.getPlugin(provider.getPluginId());
            if (plugin == null) {
                return null;
            }
            this.addI18n(provider, builder);
            builder.properties(this.getProperties(plugin, provider));
            builder.actions(this.getActions(plugin, provider));
            builder.templates(this.getTemplates(plugin, provider));
            builder.displayUrl(this.hostApplication.getBaseUrl().toString());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return builder.build();
    }

    private void addI18n(RegistrationProvider provider, RegistrationBuilder builder) {
        Locale defaultLocale = this.localeService.getDefaultLocale();
        for (Locale locale : this.localeService.getLocales()) {
            Locale key = locale.equals(defaultLocale) ? Locale.ROOT : locale;
            builder.addI18n(key, this.i18nResolver.getAllTranslationsForPrefix(provider.getApplication(), locale));
        }
    }

    private Properties getProperties(Plugin plugin, RegistrationProvider provider) throws IOException {
        Iterator iterator = ClientRegistrationServiceImpl.getResourceAsStream(plugin, provider, "registration.properties").iterator();
        if (iterator.hasNext()) {
            InputStream in = (InputStream)iterator.next();
            return ClientRegistrationServiceImpl.getProperties(in);
        }
        return new Properties();
    }

    private static Properties getProperties(InputStream in) throws IOException {
        Properties properties = new Properties();
        try {
            properties.load(in);
        }
        finally {
            in.close();
        }
        return properties;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JsonNode getActions(Plugin plugin, RegistrationProvider provider) throws IOException {
        Iterator iterator = ClientRegistrationServiceImpl.getResourceAsStream(plugin, provider, "registration-actions.json").iterator();
        if (iterator.hasNext()) {
            try (InputStream in = (InputStream)iterator.next();){
                JsonNode jsonNode = new ObjectMapper().readTree(in);
                return jsonNode;
            }
        }
        return new ObjectMapper().createObjectNode();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getTemplates(Plugin plugin, RegistrationProvider provider) throws IOException {
        Iterator iterator = ClientRegistrationServiceImpl.getResourceAsStream(plugin, provider, "registration-templates.soy").iterator();
        if (iterator.hasNext()) {
            try (InputStream in = (InputStream)iterator.next();){
                String string = IOUtils.toString((InputStream)in);
                return string;
            }
        }
        return null;
    }

    private static Option<InputStream> getResourceAsStream(Plugin plugin, RegistrationProvider provider, String file) {
        String resource = String.format("/%s/%s", provider.getPackage().replace(".", "/"), file);
        Option option = Option.option((Object)plugin.getResourceAsStream(resource));
        if (option.isEmpty()) {
            LOGGER.debug("Registration file not found {}", (Object)resource);
        }
        return option;
    }

    public void onStart() {
    }

    public synchronized void onStop() {
        if (this.closeableListener != null) {
            try {
                this.closeableListener.close();
            }
            catch (IOException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.warn("Exception thrown during shutdown of 'closeableListener' in " + ClientRegistrationServiceImpl.class.getSimpleName() + ".", (Throwable)e);
                }
                LOGGER.warn("Exception thrown during shutdown of 'closeableListener' in " + ClientRegistrationServiceImpl.class.getSimpleName() + ". Turn on debug logging for " + LOGGER.getName() + " to see the full stacktrace.");
            }
        }
        this.executor.shutdownNow();
    }
}

