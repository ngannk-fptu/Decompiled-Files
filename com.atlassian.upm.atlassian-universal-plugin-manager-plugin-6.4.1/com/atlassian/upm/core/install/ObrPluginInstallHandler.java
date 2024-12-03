/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.DefaultPluginArtifactFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginArtifactFactory
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.apache.commons.io.FileUtils
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.upm.core.install;

import com.atlassian.plugin.DefaultPluginArtifactFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginArtifactFactory;
import com.atlassian.plugin.PluginController;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.upm.Iterables;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.SafeModeAccessor;
import com.atlassian.upm.core.SafeModeException;
import com.atlassian.upm.core.install.AbstractPluginInstallHandler;
import com.atlassian.upm.core.install.ContentTypes;
import com.atlassian.upm.core.install.JarHelper;
import com.atlassian.upm.core.install.PluginDescriptor;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.spi.PluginInstallException;
import com.atlassian.upm.spi.PluginInstallResult;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.io.FileUtils;
import org.osgi.framework.BundleContext;
import org.osgi.service.obr.RepositoryAdmin;
import org.osgi.service.obr.Requirement;
import org.osgi.service.obr.Resolver;
import org.osgi.service.obr.Resource;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class ObrPluginInstallHandler
extends AbstractPluginInstallHandler
implements DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(ObrPluginInstallHandler.class);
    private final BundleContext bundleContext;
    private PluginArtifactFactory pluginArtifactFactory = new DefaultPluginArtifactFactory();
    private ServiceTracker repositoryAdminServiceTracker;
    private SafeModeAccessor safeMode;
    private static Predicate<PluginArtifact> isPrimaryApplicationPlugin = artifact -> {
        Iterator<JarHelper> iterator = JarHelper.fromFile(artifact.toFile()).iterator();
        if (iterator.hasNext()) {
            JarHelper jar = iterator.next();
            return jar.isPrimaryApplicationPlugin().getOrElse(false);
        }
        return false;
    };
    private final Function<Resource, PluginArtifact> pluginArtifactFromResource = res -> this.pluginArtifactFactory.create(URI.create(res.getURL().toString()));

    public ObrPluginInstallHandler(BundleContext bundleContext, DefaultHostApplicationInformation hostApplicationInformation, PermissionEnforcer permissionEnforcer, UpmPluginAccessor pluginAccessor, PluginController pluginController, TransactionTemplate txTemplate, SafeModeAccessor safeMode) {
        super(hostApplicationInformation, permissionEnforcer, pluginAccessor, pluginController, txTemplate);
        this.bundleContext = Objects.requireNonNull(bundleContext, "bundleContext");
        this.safeMode = Objects.requireNonNull(safeMode, "safeMode");
    }

    public void setRepositoryAdminServiceTracker(ServiceTracker repositoryAdminServiceTracker) {
        if (this.repositoryAdminServiceTracker != null) {
            this.repositoryAdminServiceTracker.close();
        }
        this.repositoryAdminServiceTracker = Objects.requireNonNull(repositoryAdminServiceTracker);
    }

    public void setPluginArtifactFactory(PluginArtifactFactory factory) {
        this.pluginArtifactFactory = factory;
    }

    public void destroy() {
        if (this.repositoryAdminServiceTracker != null) {
            this.repositoryAdminServiceTracker.close();
        }
    }

    @Override
    public boolean canInstallPlugin(File pluginFile, Option<String> contentType) {
        for (String ct : contentType) {
            if (ContentTypes.matchContentType("application/java-archive", ct)) continue;
            return false;
        }
        Iterator<Object> iterator = JarHelper.fromFile(pluginFile).iterator();
        if (iterator.hasNext()) {
            JarHelper jar = (JarHelper)iterator.next();
            return jar.isObr();
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected PluginInstallResult installPluginInternal(File pluginFile, Option<String> contentType) throws PluginInstallException {
        if (this.repositoryAdminServiceTracker == null) {
            ObrPluginInstallHandler obrPluginInstallHandler = this;
            synchronized (obrPluginInstallHandler) {
                if (this.repositoryAdminServiceTracker == null) {
                    this.repositoryAdminServiceTracker = new ServiceTracker(this.bundleContext, RepositoryAdmin.class.getName(), null);
                    this.repositoryAdminServiceTracker.open();
                }
            }
        }
        RepositoryAdmin repositoryAdmin = (RepositoryAdmin)Objects.requireNonNull(this.repositoryAdminServiceTracker.getService(), "couldn't locate RepositoryAdmin service");
        File obrDir = JarHelper.expandObrFile(pluginFile);
        URI repoUri = new File(obrDir, "obr.xml").toURI();
        try {
            String mainBundleName;
            repositoryAdmin.addRepository(repoUri.toURL());
            Resolver resolver = repositoryAdmin.resolver();
            Iterable<JarHelper> jars = JarHelper.findJarsToInstall(obrDir);
            if (Iterables.toList(jars).size() != 1) {
                throw new PluginInstallException("Attempted to install an OBR that does not have exactly one main plugin jar", false);
            }
            JarHelper mainJar = Iterables.getOnlyElement(jars);
            String mainPluginKey = mainBundleName = this.extractBundleName(mainJar);
            for (PluginDescriptor descriptor : this.validateJarIsInstallable(mainJar)) {
                Iterator<String> iterator = descriptor.getPluginKey().iterator();
                while (iterator.hasNext()) {
                    String key;
                    mainPluginKey = key = iterator.next();
                }
            }
            Resource resource = repositoryAdmin.discoverResources("(symbolicname=" + mainBundleName + ")")[0];
            resolver.add(resource);
            if (resolver.resolve()) {
                PluginInstallResult pluginInstallResult = this.installResources(resolver, mainPluginKey);
                return pluginInstallResult;
            }
            try {
                ObrPluginInstallHandler.logUnsatisfiedRequirements(pluginFile, resolver.getUnsatisfiedRequirements());
                throw new PluginInstallException("Failed to resolve plugin dependencies within OBR [" + pluginFile.getName() + "]. Please see the logs for more detailed information.", false);
            }
            catch (PluginInstallException e) {
                throw e;
            }
            catch (SafeModeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new PluginInstallException("Failed to install OBR jar artifact", e);
            }
        }
        finally {
            try {
                repositoryAdmin.removeRepository(repoUri.toURL());
                FileUtils.deleteDirectory((File)obrDir);
            }
            catch (Exception e) {
                log.warn("Failed to remove local OBR repository resources");
            }
        }
    }

    private static void logUnsatisfiedRequirements(File plugin, Requirement[] unsatisfiedRequirements) {
        if (unsatisfiedRequirements != null) {
            log.error("OBR resolver has {} unsatisfied requirements for installing {}", (Object)unsatisfiedRequirements.length, (Object)plugin.getName());
            for (Requirement requirement : unsatisfiedRequirements) {
                log.error("Unsatisfied requirement: {}", (Object)requirement.getFilter());
            }
        } else {
            log.error("OBR resolver failed to resolve, but provided no additional information");
        }
    }

    private PluginInstallResult installResources(Resolver resolver, String mainPluginKey) throws PluginInstallException, URISyntaxException {
        List requiredArtifacts = Arrays.stream(resolver.getRequiredResources()).map(this.pluginArtifactFromResource).collect(Collectors.toList());
        List optionalArtifacts = Arrays.stream(resolver.getOptionalResources()).map(this.pluginArtifactFromResource).collect(Collectors.toList());
        Iterable allDependencies = Stream.concat(requiredArtifacts.stream(), optionalArtifacts.stream()).collect(Collectors.toList());
        for (PluginArtifact dep : allDependencies) {
            for (JarHelper jarHelper : JarHelper.fromFile(dep.toFile())) {
                this.validateJarIsInstallable(jarHelper);
            }
        }
        Resource mainPluginResource = resolver.getAddedResources()[0];
        PluginArtifact mainArtifact = this.pluginArtifactFromResource.apply(mainPluginResource);
        Option<JarHelper> mainJar = JarHelper.fromFile(mainArtifact.toFile());
        for (JarHelper jar : mainJar) {
            this.validateJarIsInstallable(jar);
        }
        List<PluginArtifact> list = Collections.unmodifiableList(Stream.concat(Iterables.toStream(allDependencies), Stream.of(mainArtifact)).collect(Collectors.toList()));
        if (this.safeMode.isSafeMode() && Iterables.none(list, isPrimaryApplicationPlugin)) {
            throw new SafeModeException("Install OBR plugin is not allowed when system is in safe mode");
        }
        List<Plugin> allInstalled = this.installArtifacts(list);
        Optional<Plugin> mainInstalled = StreamSupport.stream(allInstalled.spliterator(), false).filter(plugin -> plugin.getKey().equals(mainPluginKey)).findFirst();
        if (mainInstalled.isPresent()) {
            List<Plugin> others = StreamSupport.stream(allInstalled.spliterator(), false).filter(plugin -> !plugin.getKey().equals(mainPluginKey)).collect(Collectors.toList());
            return new PluginInstallResult(mainInstalled.get(), others);
        }
        throw new PluginInstallException("Unknown error, plugin not installed");
    }

    String extractBundleName(JarHelper jar) throws IOException {
        Iterator<String> iterator = jar.getBundleSymbolicName().iterator();
        if (iterator.hasNext()) {
            String name = iterator.next();
            return name;
        }
        String name = jar.getFile().getName();
        return name.endsWith(".jar") ? name.substring(0, name.length() - ".jar".length()) : name;
    }
}

