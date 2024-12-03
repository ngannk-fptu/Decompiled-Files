/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.spi.Manifest
 *  com.atlassian.applinks.spi.manifest.ApplicationStatus
 *  com.atlassian.applinks.spi.manifest.ManifestNotFoundException
 *  com.atlassian.applinks.spi.manifest.ManifestProducer
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.predicate.ModuleDescriptorPredicate
 *  com.google.common.collect.Iterables
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core.manifest;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.core.manifest.AppLinksManifestDownloader;
import com.atlassian.applinks.core.plugin.ApplicationTypeModuleDescriptor;
import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.manifest.ApplicationStatus;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.applinks.spi.manifest.ManifestProducer;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.predicate.ModuleDescriptorPredicate;
import com.google.common.collect.Iterables;
import java.net.URI;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="manifestRetriever")
public class ManifestRetrieverDispatcher
implements ManifestRetriever {
    private final AppLinksManifestDownloader downloader;
    private final PluginAccessor pluginAccessor;

    @Autowired
    public ManifestRetrieverDispatcher(AppLinksManifestDownloader downloader, PluginAccessor pluginAccessor) {
        this.downloader = downloader;
        this.pluginAccessor = pluginAccessor;
    }

    public Manifest getManifest(URI uri) throws ManifestNotFoundException {
        return this.downloader.download(uri);
    }

    public Manifest getManifest(URI uri, ApplicationType type) throws ManifestNotFoundException {
        return this.getRequiredManifestProducer(type).getManifest(uri);
    }

    public ApplicationStatus getApplicationStatus(URI uri, ApplicationType type) {
        return this.getRequiredManifestProducer(type).getStatus(uri);
    }

    private ManifestProducer getRequiredManifestProducer(final ApplicationType type) throws IllegalStateException {
        Collection descriptors = this.pluginAccessor.getModuleDescriptors((ModuleDescriptorPredicate)new ModuleDescriptorPredicate<ApplicationType>(){

            public boolean matches(ModuleDescriptor<? extends ApplicationType> moduleDescriptor) {
                return moduleDescriptor instanceof ApplicationTypeModuleDescriptor && type.getClass().isAssignableFrom(((ApplicationType)moduleDescriptor.getModule()).getClass());
            }
        });
        if (!descriptors.isEmpty()) {
            return ((ApplicationTypeModuleDescriptor)((Object)Iterables.get((Iterable)descriptors, (int)0))).getManifestProducer();
        }
        throw new IllegalStateException("Cannot query application status for unknown application type \"" + type.getClass().getName() + "\"");
    }
}

