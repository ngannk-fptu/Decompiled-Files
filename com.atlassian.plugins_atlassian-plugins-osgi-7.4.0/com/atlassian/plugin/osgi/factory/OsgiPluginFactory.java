/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Application
 *  com.atlassian.plugin.JarPluginArtifact
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginArtifact$HasExtraModuleDescriptors
 *  com.atlassian.plugin.ReferenceMode
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.factories.AbstractPluginFactory
 *  com.atlassian.plugin.impl.UnloadablePlugin
 *  com.atlassian.plugin.parsers.CompositeDescriptorParserFactory
 *  com.atlassian.plugin.parsers.DescriptorParser
 *  com.atlassian.plugin.parsers.DescriptorParserFactory
 *  com.atlassian.plugin.parsers.XmlDescriptorParserUtils
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.Application;
import com.atlassian.plugin.JarPluginArtifact;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.ReferenceMode;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.factories.AbstractPluginFactory;
import com.atlassian.plugin.impl.UnloadablePlugin;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import com.atlassian.plugin.osgi.container.OsgiPersistentCache;
import com.atlassian.plugin.osgi.factory.OsgiChainedModuleDescriptorFactoryCreator;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import com.atlassian.plugin.osgi.factory.OsgiPluginXmlDescriptorParserFactory;
import com.atlassian.plugin.osgi.factory.transform.DefaultPluginTransformer;
import com.atlassian.plugin.osgi.factory.transform.PluginTransformationException;
import com.atlassian.plugin.osgi.factory.transform.PluginTransformer;
import com.atlassian.plugin.osgi.factory.transform.model.SystemExports;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import com.atlassian.plugin.parsers.CompositeDescriptorParserFactory;
import com.atlassian.plugin.parsers.DescriptorParser;
import com.atlassian.plugin.parsers.DescriptorParserFactory;
import com.atlassian.plugin.parsers.XmlDescriptorParserUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.Manifest;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OsgiPluginFactory
extends AbstractPluginFactory {
    private static final Logger log = LoggerFactory.getLogger(OsgiPluginFactory.class);
    private static final Predicate<Integer> IS_PLUGINS_2 = input -> input != null && input == 2;
    private final OsgiContainerManager osgi;
    private final String pluginDescriptorFileName;
    private final PluginEventManager pluginEventManager;
    private final Set<Application> applications;
    private final OsgiPersistentCache persistentCache;
    private final PluginTransformerFactory pluginTransformerFactory;
    private volatile PluginTransformer pluginTransformer;
    private final OsgiChainedModuleDescriptorFactoryCreator osgiChainedModuleDescriptorFactoryCreator;

    public OsgiPluginFactory(String pluginDescriptorFileName, Set<Application> applications, OsgiPersistentCache persistentCache, OsgiContainerManager osgi, PluginEventManager pluginEventManager) {
        this(pluginDescriptorFileName, applications, persistentCache, osgi, pluginEventManager, new DefaultPluginTransformerFactory());
    }

    public OsgiPluginFactory(String pluginDescriptorFileName, Set<Application> applications, OsgiPersistentCache persistentCache, OsgiContainerManager osgi, PluginEventManager pluginEventManager, PluginTransformerFactory pluginTransformerFactory) {
        super((DescriptorParserFactory)new OsgiPluginXmlDescriptorParserFactory(), applications);
        this.pluginDescriptorFileName = (String)Preconditions.checkNotNull((Object)pluginDescriptorFileName, (Object)"Plugin descriptor is required");
        this.osgi = (OsgiContainerManager)Preconditions.checkNotNull((Object)osgi, (Object)"The OSGi container is required");
        this.applications = (Set)Preconditions.checkNotNull(applications, (Object)"Applications is required!");
        this.persistentCache = (OsgiPersistentCache)Preconditions.checkNotNull((Object)persistentCache, (Object)"The osgi persistent cache is required");
        this.pluginEventManager = (PluginEventManager)Preconditions.checkNotNull((Object)pluginEventManager, (Object)"The plugin event manager is required");
        this.pluginTransformerFactory = (PluginTransformerFactory)Preconditions.checkNotNull((Object)pluginTransformerFactory, (Object)"The plugin transformer factory is required");
        this.osgiChainedModuleDescriptorFactoryCreator = new OsgiChainedModuleDescriptorFactoryCreator(osgi::getServiceTracker);
    }

    private PluginTransformer getPluginTransformer() {
        if (this.pluginTransformer == null) {
            String exportString = (String)this.osgi.getBundles()[0].getHeaders().get("Export-Package");
            SystemExports exports = new SystemExports(exportString);
            this.pluginTransformer = this.pluginTransformerFactory.newPluginTransformer(this.persistentCache, exports, this.applications, this.pluginDescriptorFileName, this.osgi);
        }
        return this.pluginTransformer;
    }

    public String canCreate(PluginArtifact pluginArtifact) {
        boolean isPlugin = this.hasDescriptor((PluginArtifact)Preconditions.checkNotNull((Object)pluginArtifact));
        boolean hasSpring = pluginArtifact.containsSpringContext();
        boolean isTransformless = this.getPluginKeyFromManifest((PluginArtifact)Preconditions.checkNotNull((Object)pluginArtifact)) != null;
        String key = null;
        if (isPlugin && !isTransformless || isTransformless && hasSpring) {
            key = isPlugin ? this.getPluginKeyFromDescriptor((PluginArtifact)Preconditions.checkNotNull((Object)pluginArtifact)) : this.getPluginKeyFromManifest(pluginArtifact);
        }
        return key;
    }

    protected InputStream getDescriptorInputStream(PluginArtifact pluginArtifact) {
        return pluginArtifact.getResourceAsStream(this.pluginDescriptorFileName);
    }

    protected Predicate<Integer> isValidPluginsVersion() {
        return IS_PLUGINS_2;
    }

    private String getPluginKeyFromManifest(PluginArtifact pluginArtifact) {
        Manifest mf = OsgiHeaderUtil.getManifest(pluginArtifact);
        if (mf != null) {
            String key = mf.getMainAttributes().getValue("Atlassian-Plugin-Key");
            String version = mf.getMainAttributes().getValue("Bundle-Version");
            if (key != null) {
                if (version != null) {
                    return key;
                }
                log.warn("Found plugin key '{}' in the manifest but no bundle version, so it can't be loaded as an OsgiPlugin", (Object)key);
            }
        }
        return null;
    }

    private Iterable<String> getScanFoldersFromManifest(PluginArtifact pluginArtifact) {
        String sf;
        HashSet scanFolders = Sets.newHashSet();
        Manifest mf = OsgiHeaderUtil.getManifest(pluginArtifact);
        if (mf != null && StringUtils.isNotBlank((CharSequence)(sf = mf.getMainAttributes().getValue("Atlassian-Scan-Folders")))) {
            String[] folders = sf.split(",");
            scanFolders.addAll(Arrays.asList(folders));
        }
        return scanFolders;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Plugin create(PluginArtifact pluginArtifact, ModuleDescriptorFactory moduleDescriptorFactory) {
        Plugin plugin;
        InputStream pluginDescriptor;
        block8: {
            UnloadablePlugin unloadablePlugin;
            Preconditions.checkNotNull((Object)pluginArtifact, (Object)"The plugin deployment unit is required");
            Preconditions.checkNotNull((Object)moduleDescriptorFactory, (Object)"The module descriptor factory is required");
            pluginDescriptor = null;
            try {
                pluginDescriptor = pluginArtifact.getResourceAsStream(this.pluginDescriptorFileName);
                if (pluginDescriptor != null) {
                    PluginArtifact artifactToInstall;
                    ModuleDescriptorFactory combinedFactory = this.getChainedModuleDescriptorFactory(moduleDescriptorFactory, pluginArtifact);
                    String pluginKeyFromManifest = this.getPluginKeyFromManifest(pluginArtifact);
                    if (pluginKeyFromManifest == null) {
                        log.debug("Plugin key NOT found in manifest at entry {}, undergoing transformation", (Object)"Atlassian-Plugin-Key");
                        artifactToInstall = this.createOsgiPluginJar(pluginArtifact);
                    } else {
                        log.debug("Plugin key found in manifest at entry {}, skipping transformation for '{}'", (Object)"Atlassian-Plugin-Key", (Object)pluginKeyFromManifest);
                        artifactToInstall = pluginArtifact;
                    }
                    DescriptorParser parser = this.createDescriptorParser(artifactToInstall, pluginDescriptor);
                    OsgiPlugin osgiPlugin = new OsgiPlugin(parser.getKey(), this.osgi, artifactToInstall, pluginArtifact, this.pluginEventManager);
                    plugin = parser.configurePlugin(combinedFactory, (Plugin)osgiPlugin);
                    break block8;
                }
                Manifest manifest = OsgiHeaderUtil.getManifest(pluginArtifact);
                if (manifest != null) {
                    plugin = OsgiPluginFactory.extractOsgiPlugin(pluginArtifact, manifest, this.osgi, this.pluginEventManager);
                    plugin.setPluginInformation(OsgiHeaderUtil.extractOsgiPluginInformation(manifest, true));
                    break block8;
                }
                log.warn("Unable to load plugin from '{}', no manifest", (Object)pluginArtifact);
                unloadablePlugin = new UnloadablePlugin("No manifest in PluginArtifact '" + pluginArtifact + "'");
            }
            catch (PluginTransformationException ex) {
                Plugin plugin2;
                try {
                    plugin2 = this.reportUnloadablePlugin(pluginArtifact.toFile(), ex);
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(pluginDescriptor);
                    throw throwable;
                }
                IOUtils.closeQuietly((InputStream)pluginDescriptor);
                return plugin2;
            }
            IOUtils.closeQuietly((InputStream)pluginDescriptor);
            return unloadablePlugin;
        }
        IOUtils.closeQuietly((InputStream)pluginDescriptor);
        return plugin;
    }

    public ModuleDescriptor<?> createModule(Plugin plugin, Element module, ModuleDescriptorFactory moduleDescriptorFactory) {
        if (plugin instanceof OsgiPlugin) {
            ModuleDescriptorFactory combinedFactory = this.osgiChainedModuleDescriptorFactoryCreator.create((String name) -> false, moduleDescriptorFactory);
            return XmlDescriptorParserUtils.addModule((ModuleDescriptorFactory)combinedFactory, (Plugin)plugin, (Element)module);
        }
        return null;
    }

    private DescriptorParser createDescriptorParser(PluginArtifact pluginArtifact, InputStream pluginDescriptor) {
        HashSet xmlPaths = Sets.newHashSet();
        for (String path : this.getScanFoldersFromManifest(pluginArtifact)) {
            xmlPaths.addAll(((PluginArtifact.HasExtraModuleDescriptors)pluginArtifact).extraModuleDescriptorFiles(path));
        }
        Iterable sources = Iterables.transform((Iterable)xmlPaths, arg_0 -> ((PluginArtifact)pluginArtifact).getResourceAsStream(arg_0));
        return ((CompositeDescriptorParserFactory)this.descriptorParserFactory).getInstance(pluginDescriptor, sources, this.applications);
    }

    private static Plugin extractOsgiPlugin(PluginArtifact pluginArtifact, Manifest mf, OsgiContainerManager osgi, PluginEventManager pluginEventManager) {
        String pluginKey = OsgiHeaderUtil.getNonEmptyAttribute(mf, "Atlassian-Plugin-Key");
        String bundleName = OsgiHeaderUtil.getAttributeWithoutValidation(mf, "Bundle-Name");
        OsgiPlugin plugin = new OsgiPlugin(pluginKey, osgi, pluginArtifact, pluginArtifact, pluginEventManager);
        plugin.setPluginsVersion(2);
        plugin.setName(bundleName);
        return plugin;
    }

    private ModuleDescriptorFactory getChainedModuleDescriptorFactory(ModuleDescriptorFactory originalFactory, PluginArtifact pluginArtifact) {
        return this.osgiChainedModuleDescriptorFactoryCreator.create(arg_0 -> ((PluginArtifact)pluginArtifact).doesResourceExist(arg_0), originalFactory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private PluginArtifact createOsgiPluginJar(PluginArtifact pluginArtifact) {
        long startTime = System.currentTimeMillis();
        try {
            File transformedFile = this.getPluginTransformer().transform(pluginArtifact, this.osgi.getHostComponentRegistrations());
            JarPluginArtifact jarPluginArtifact = new JarPluginArtifact(transformedFile, ReferenceMode.PERMIT_REFERENCE);
            return jarPluginArtifact;
        }
        finally {
            log.info("Plugin [{}] transformed in {}ms", (Object)pluginArtifact.getName(), (Object)(System.currentTimeMillis() - startTime));
        }
    }

    private Plugin reportUnloadablePlugin(File file, Exception e) {
        String msg = "Unable to load plugin: " + file;
        log.error(msg, (Throwable)e);
        UnloadablePlugin plugin = new UnloadablePlugin();
        plugin.setErrorText("Unable to load plugin: " + e.getMessage());
        return plugin;
    }

    public static class DefaultPluginTransformerFactory
    implements PluginTransformerFactory {
        @Override
        public PluginTransformer newPluginTransformer(OsgiPersistentCache cache, SystemExports systemExports, Set<Application> applicationKeys, String pluginDescriptorPath, OsgiContainerManager osgi) {
            return new DefaultPluginTransformer(cache, systemExports, applicationKeys, pluginDescriptorPath, osgi);
        }
    }

    public static interface PluginTransformerFactory {
        public PluginTransformer newPluginTransformer(OsgiPersistentCache var1, SystemExports var2, Set<Application> var3, String var4, OsgiContainerManager var5);
    }
}

