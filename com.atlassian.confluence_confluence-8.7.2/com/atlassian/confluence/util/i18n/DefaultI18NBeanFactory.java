/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.Resourced
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.apache.commons.lang3.LocaleUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.languages.LocaleParser;
import com.atlassian.confluence.languages.TranslationTransform;
import com.atlassian.confluence.plugin.NullPluginAccessor;
import com.atlassian.confluence.plugin.descriptor.LanguageModuleDescriptor;
import com.atlassian.confluence.plugin.descriptor.TranslationTransformModuleDescriptor;
import com.atlassian.confluence.plugin.descriptor.WeightedPluginModuleTracker;
import com.atlassian.confluence.util.i18n.ClasspathI18NResource;
import com.atlassian.confluence.util.i18n.CombinedResourceBundleFactory;
import com.atlassian.confluence.util.i18n.DefaultI18NBean;
import com.atlassian.confluence.util.i18n.HelpPathsI18NResource;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.I18NResource;
import com.atlassian.confluence.util.i18n.I18NResourceBundlesLoader;
import com.atlassian.confluence.util.i18n.I18nModeManager;
import com.atlassian.confluence.util.i18n.LanguagePluginI18NResource;
import com.atlassian.confluence.util.i18n.PluginI18NResource;
import com.atlassian.confluence.util.i18n.ResourceBundlesCollector;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.Resourced;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.LocaleUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class DefaultI18NBeanFactory
implements I18NBeanFactory,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DefaultI18NBean.class);
    private static final Supplier<Locale> DEFAULT_LOCALE_SUPPLIER = () -> LocaleManager.DEFAULT_LOCALE;
    public static final String HELP_PATHS_RESOURCE_NAME = "help-paths";
    public static final String HELP_PATHS_RESOURCE_TYPE = "helpPaths";
    public static final String I18N_RESOURCE_TYPE = "i18n";
    private PluginAccessor pluginAccessor;
    private I18nModeManager modeManager;
    private Supplier<Locale> localeSupplier;
    private PluginModuleTracker<TranslationTransform, TranslationTransformModuleDescriptor> pluginModuleTracker;

    public DefaultI18NBeanFactory() {
        this(new NullPluginAccessor());
    }

    public DefaultI18NBeanFactory(PluginAccessor pluginAccessor) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
        this.localeSupplier = DEFAULT_LOCALE_SUPPLIER;
    }

    public DefaultI18NBeanFactory(PluginAccessor pluginAccessor, ApplicationConfiguration applicationConfig) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
        Objects.requireNonNull(applicationConfig);
        this.localeSupplier = () -> {
            Locale setupLocale = LocaleParser.toLocale((String)applicationConfig.getProperty((Object)"confluence.setup.locale"));
            if (setupLocale == null) {
                return LocaleManager.DEFAULT_LOCALE;
            }
            return setupLocale;
        };
    }

    static List<ResourceBundle> buildBundles(Locale locale, PluginAccessor pluginAccessor) {
        boolean useCustomLocale = locale != null && !locale.equals(LocaleManager.DEFAULT_LOCALE);
        List<I18NResource> resources = DefaultI18NBeanFactory.getI18NResources(pluginAccessor, useCustomLocale);
        return DefaultI18NBeanFactory.buildBundles(locale, useCustomLocale, resources);
    }

    private static List<ResourceBundle> buildBundles(Locale locale, boolean useCustomLocale, List<I18NResource> resources) {
        ArrayList<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
        if (BootstrapUtils.getBootstrapManager().getHibernateConfig().isHibernateSetup()) {
            List<ResourceBundle> combinedResourceBundles = DefaultI18NBeanFactory.getStandardResourceBundles(locale, resources);
            bundles.addAll(combinedResourceBundles);
        } else if (useCustomLocale) {
            List<ResourceBundle> localeSpecificBundles = DefaultI18NBeanFactory.getLocaleSpecificResourceBundles(locale, resources);
            if (localeSpecificBundles != null) {
                bundles.addAll(localeSpecificBundles);
            }
            bundles.add(DefaultI18NBeanFactory.getCoreBundle());
        } else {
            ResourceBundle bundle = DefaultI18NBeanFactory.getDefaultResourceBundle();
            if (bundle != null) {
                bundles.add(bundle);
            }
        }
        return bundles;
    }

    private static List<I18NResource> getI18NResources(PluginAccessor pluginAccessor, boolean useCustomLocale) {
        List<I18NResource> resources = BootstrapUtils.getBootstrapManager().getHibernateConfig().isHibernateSetup() || useCustomLocale ? DefaultI18NBeanFactory.loadI18NResources(pluginAccessor) : Collections.emptyList();
        return resources;
    }

    private static void loadDefaultBundle(ResourceBundlesCollector bundlesCollector) {
        bundlesCollector.addBundle(DefaultI18NBeanFactory.getDefaultResourceBundle());
    }

    static ResourceBundle getDefaultResourceBundle() {
        ResourceBundle helpPathsBundle;
        ResourceBundle coreBundle = DefaultI18NBeanFactory.getCoreBundle();
        if (coreBundle == null) {
            return null;
        }
        ResourceBundle externalLinksBundle = DefaultI18NBeanFactory.getExternalLinksBundle();
        ArrayList<ResourceBundle> defaultBundles = new ArrayList<ResourceBundle>();
        defaultBundles.add(coreBundle);
        if (externalLinksBundle != null) {
            defaultBundles.add(externalLinksBundle);
        }
        if ((helpPathsBundle = DefaultI18NBeanFactory.getDefaultHelpPathsBundle()) != null) {
            defaultBundles.add(helpPathsBundle);
        }
        return CombinedResourceBundleFactory.createCombinedResourceBundle(defaultBundles);
    }

    private static ResourceBundle getCoreBundle() {
        String path = ConfluenceActionSupport.class.getName().replace('.', '/') + ".properties";
        return DefaultI18NBeanFactory.getResourceBundle(path);
    }

    private static List<ResourceBundle> getStandardResourceBundles(Locale locale, List<I18NResource> resources) {
        ResourceBundlesCollector bundlesCollector = new ResourceBundlesCollector();
        DefaultI18NBeanFactory.loadDefaultBundle(bundlesCollector);
        DefaultI18NBeanFactory.loadResourceBundles(bundlesCollector, locale, resources);
        return bundlesCollector.getCombinedResourceBundles();
    }

    private static List<ResourceBundle> getLocaleSpecificResourceBundles(Locale locale, List<I18NResource> resources) {
        ResourceBundlesCollector bundlesCollector = new ResourceBundlesCollector();
        ResourceBundle externalLinksBundle = DefaultI18NBeanFactory.getExternalLinksBundle();
        if (externalLinksBundle != null) {
            bundlesCollector.addBundle(externalLinksBundle);
        }
        DefaultI18NBeanFactory.loadResourceBundles(bundlesCollector, locale, resources);
        return bundlesCollector.getCombinedResourceBundles();
    }

    private static List<I18NResource> loadI18NResources(PluginAccessor pluginAccessor) {
        ImmutableList.Builder resources = ImmutableList.builder();
        resources.add((Object)new LanguagePluginI18NResource(pluginAccessor));
        resources.addAll(DefaultI18NBeanFactory.loadPluginResources(pluginAccessor, I18N_RESOURCE_TYPE, PluginI18NResource::new));
        resources.addAll(DefaultI18NBeanFactory.loadPluginResources(pluginAccessor, HELP_PATHS_RESOURCE_TYPE, HelpPathsI18NResource::new));
        resources.add((Object)new ClasspathI18NResource(HELP_PATHS_RESOURCE_NAME));
        return resources.build();
    }

    private static ResourceBundle getExternalLinksBundle() {
        return DefaultI18NBeanFactory.getResourceBundle("external-links.properties");
    }

    private static ResourceBundle getDefaultHelpPathsBundle() {
        return DefaultI18NBeanFactory.getResourceBundle("i18n/help-paths.properties");
    }

    private static List<I18NResource> loadPluginResources(PluginAccessor pluginAccessor, String resourceType, BiFunction<Plugin, ResourceDescriptor, I18NResource> resourceFactory) {
        ImmutableList.Builder builder = ImmutableList.builder();
        pluginAccessor.getEnabledPlugins().forEach(plugin -> {
            try {
                Stream.concat(Stream.of(plugin), plugin.getModuleDescriptors().stream()).flatMap(resourced -> ((Resourced)resourced).getResourceDescriptors().stream()).filter(resourceDescriptor -> resourceType.equals(resourceDescriptor.getType())).forEach(resourceDescriptor -> builder.add((Object)((I18NResource)resourceFactory.apply((Plugin)plugin, (ResourceDescriptor)resourceDescriptor))));
            }
            catch (Exception e) {
                log.error("Unable to load {} resources for: {}({})", new Object[]{resourceType, plugin.getName(), plugin.getKey(), e});
            }
        });
        return builder.build();
    }

    private static void loadResourceBundles(ResourceBundlesCollector bundlesCollector, Locale locale, List<I18NResource> resources) {
        for (I18NResource resource : resources) {
            Map<String, ResourceBundle> resourceBundles = I18NResourceBundlesLoader.getResourceBundles(resource, locale);
            bundlesCollector.addBundles(resourceBundles);
        }
    }

    private static ResourceBundle getResourceBundle(String path) {
        try {
            InputStream resourceAsStream = DefaultI18NBean.class.getClassLoader().getResourceAsStream(path);
            return new PropertyResourceBundle(resourceAsStream);
        }
        catch (IOException | NullPointerException e) {
            log.error("Error occurred reading the stream for " + path, (Throwable)e);
            return null;
        }
    }

    @Deprecated
    public static I18NBean getDefaultI18NBean() {
        ResourceBundle defaultResourceBundle = DefaultI18NBeanFactory.getDefaultResourceBundle();
        return new DefaultI18NBean((Locale)DEFAULT_LOCALE_SUPPLIER.get(), null, null, (List<ResourceBundle>)(defaultResourceBundle != null ? ImmutableList.of((Object)defaultResourceBundle) : Collections.emptyList()));
    }

    @Override
    public @NonNull I18NBean getI18NBean(@NonNull Locale locale) {
        if (LocaleUtils.isAvailableLocale((Locale)locale)) {
            log.debug("Creating i18nBean for locale [{}]", (Object)locale);
            boolean useCustomLocale = !locale.equals(LocaleManager.DEFAULT_LOCALE);
            List<I18NResource> resources = DefaultI18NBeanFactory.getI18NResources(this.pluginAccessor, useCustomLocale);
            return new DefaultI18NBean(locale, this.pluginModuleTracker, this.modeManager, DefaultI18NBeanFactory.buildBundles(locale, useCustomLocale, resources));
        }
        return this.getI18NBean();
    }

    @Override
    public @NonNull I18NBean getI18NBean() {
        return this.getI18NBean((Locale)this.localeSupplier.get());
    }

    @Override
    public @NonNull String getStateHash() {
        List hashablePluginProperties = this.pluginAccessor.getEnabledPlugins().stream().filter(DefaultI18NBeanFactory::affectsI18n).flatMap(plugin -> Stream.of(plugin.getKey(), plugin.getPluginInformation().getVersion())).collect(Collectors.toList());
        List hashableTransformerStates = this.pluginModuleTracker == null ? Collections.emptyList() : StreamSupport.stream(this.pluginModuleTracker.getModules().spliterator(), false).map(transform -> transform.getClass().getName() + transform.getStateHash()).collect(Collectors.toList());
        return Integer.toString(hashablePluginProperties.hashCode() + hashableTransformerStates.hashCode(), 36);
    }

    private static boolean affectsI18n(Plugin plugin) {
        return Stream.concat(Stream.of(plugin), plugin.getModuleDescriptors().stream()).anyMatch(resourced -> resourced instanceof LanguageModuleDescriptor || resourced instanceof TranslationTransformModuleDescriptor || ((Resourced)resourced).getResourceDescriptors().stream().anyMatch(resource -> HELP_PATHS_RESOURCE_TYPE.equals(resource.getType()) || I18N_RESOURCE_TYPE.equals(resource.getType())));
    }

    public void setModeManager(I18nModeManager modeManager) {
        this.modeManager = modeManager;
    }

    public void setPluginEventManager(PluginEventManager pluginEventManager) {
        this.pluginModuleTracker = WeightedPluginModuleTracker.create(this.pluginAccessor, pluginEventManager, TranslationTransformModuleDescriptor.class);
    }

    public void destroy() throws Exception {
        if (this.pluginModuleTracker != null) {
            this.pluginModuleTracker.close();
        }
    }
}

