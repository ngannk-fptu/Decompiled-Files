/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.link.MutatingEntityLinkService
 *  com.atlassian.applinks.spi.util.TypeAccessor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.web.descriptors.ConditionalDescriptor
 *  com.atlassian.plugin.web.descriptors.WeightedDescriptorComparator
 *  com.google.common.collect.Collections2
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Options
 *  io.atlassian.fugue.Pair
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.navlink.producer.contentlinks.services;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.link.MutatingEntityLinkService;
import com.atlassian.applinks.spi.util.TypeAccessor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.web.descriptors.ConditionalDescriptor;
import com.atlassian.plugin.web.descriptors.WeightedDescriptorComparator;
import com.atlassian.plugins.navlink.consumer.http.UserAgentProperty;
import com.atlassian.plugins.navlink.consumer.menu.services.RemoteApplications;
import com.atlassian.plugins.navlink.consumer.projectshortcuts.rest.UnauthenticatedRemoteApplication;
import com.atlassian.plugins.navlink.producer.capabilities.CapabilityKey;
import com.atlassian.plugins.navlink.producer.capabilities.RemoteApplicationWithCapabilities;
import com.atlassian.plugins.navlink.producer.contentlinks.plugin.ContentLinkModuleDescriptor;
import com.atlassian.plugins.navlink.producer.contentlinks.rest.ContentLinkEntity;
import com.atlassian.plugins.navlink.producer.contentlinks.services.ContentLinkCapability;
import com.atlassian.plugins.navlink.producer.contentlinks.services.ContentLinkClient;
import com.atlassian.plugins.navlink.producer.contentlinks.services.ContentLinksService;
import com.atlassian.plugins.navlink.util.executor.DaemonExecutorService;
import com.google.common.collect.Collections2;
import io.atlassian.fugue.Either;
import io.atlassian.fugue.Options;
import io.atlassian.fugue.Pair;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultContentLinksService
implements ContentLinksService {
    public static final WeightedDescriptorComparator WEIGHTED_DESCRIPTOR_COMPARATOR = new WeightedDescriptorComparator();
    private static final Logger log = LoggerFactory.getLogger(DefaultContentLinksService.class);
    private final PluginAccessor pluginAccessor;
    private final MutatingEntityLinkService mutatingEntityLinkService;
    private final TypeAccessor typeAccessor;
    private final DaemonExecutorService executor;
    private final RemoteApplications remoteApplications;
    private final UserAgentProperty userAgentProperty;

    public DefaultContentLinksService(PluginAccessor pluginAccessor, MutatingEntityLinkService mutatingEntityLinkService, TypeAccessor typeAccessor, DaemonExecutorService executor, RemoteApplications remoteApplications, UserAgentProperty userAgentProperty) {
        this.pluginAccessor = pluginAccessor;
        this.mutatingEntityLinkService = mutatingEntityLinkService;
        this.typeAccessor = typeAccessor;
        this.executor = executor;
        this.remoteApplications = remoteApplications;
        this.userAgentProperty = userAgentProperty;
    }

    @Override
    @Nonnull
    public List<ContentLinkModuleDescriptor> getAllLocalContentLinks(@Nonnull Map<String, Object> context, @Nullable TypeId entityType) {
        List<ContentLinkModuleDescriptor> descriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(ContentLinkModuleDescriptor.class);
        descriptors = this.filterFragmentsByCondition(descriptors, context);
        if (entityType != null) {
            descriptors = this.filterFragmentsByTypeId(descriptors, entityType);
        }
        descriptors.sort((Comparator<ContentLinkModuleDescriptor>)WEIGHTED_DESCRIPTOR_COMPARATOR);
        return descriptors;
    }

    @Override
    @Nonnull
    public List<ContentLinkEntity> getAllRemoteContentLinks(@Nonnull String key, @Nonnull TypeId entityTypeId) {
        return new ArrayList<ContentLinkEntity>(this.executeRemoteContentLinksCollector(key, entityTypeId, contentLinkCapability -> () -> {
            try {
                ContentLinkClient client = new ContentLinkClient(this.userAgentProperty);
                return client.getContentLinks((ContentLinkCapability)contentLinkCapability);
            }
            catch (Exception ex) {
                log.error("Could not get project shortcuts for entity {}\n on app {}", (Object)contentLinkCapability.getEntityLink(), (Object)contentLinkCapability.getEntityLink().getApplicationLink().getName());
                log.debug("More details", (Throwable)ex);
                return Collections.emptyList();
            }
        }));
    }

    @Override
    @Nonnull
    public Pair<Iterable<ContentLinkEntity>, Iterable<UnauthenticatedRemoteApplication>> getAllRemoteContentLinksAndUnauthedApps(@Nonnull String key, @Nonnull TypeId entityTypeId) {
        return this.transformResults(this.executeRemoteContentLinksCollector(key, entityTypeId, contentLinkCapability -> () -> {
            ApplicationLink applicationLink = contentLinkCapability.getEntityLink().getApplicationLink();
            try {
                ContentLinkClient client = new ContentLinkClient(this.userAgentProperty);
                return Collections2.transform(client.getContentLinks((ContentLinkCapability)contentLinkCapability), Either::left);
            }
            catch (CredentialsRequiredException ex) {
                return Collections.singleton(Either.right((Object)new UnauthenticatedRemoteApplication(applicationLink.getId(), applicationLink.getName(), (Either<URI, String>)Either.right((Object)contentLinkCapability.getContentLinkUrl()), ex.getAuthorisationURI())));
            }
            catch (Exception ex) {
                log.error("Could not get project shortcuts for entity {}\n on app {}", (Object)contentLinkCapability.getEntityLink(), (Object)applicationLink.getName());
                log.debug("More details", (Throwable)ex);
                return Collections.emptyList();
            }
        }));
    }

    private <T> Collection<T> executeRemoteContentLinksCollector(String key, TypeId entityTypeId, Function<ContentLinkCapability, Callable<Collection<T>>> collector) {
        EntityType entityType = this.typeAccessor.loadEntityType(entityTypeId);
        if (entityType != null) {
            Set<RemoteApplicationWithCapabilities> applications = this.remoteApplications.capableOf(CapabilityKey.CONTENT_LINKS);
            if (!applications.iterator().hasNext()) {
                return Collections.emptyList();
            }
            List<ContentLinkCapability> contentLinkCapabilities = ContentLinkCapability.create(applications, this.mutatingEntityLinkService.getEntityLinksForKey(key, entityType.getClass()));
            List tasks = contentLinkCapabilities.stream().map(collector).collect(Collectors.toList());
            try {
                return this.executor.invokeAllAndGet(tasks, (long)DaemonExecutorService.DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            }
            catch (ExecutionException ex) {
                log.error("Error getting project shortcuts", (Throwable)ex);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
        return Collections.emptyList();
    }

    private Pair<Iterable<ContentLinkEntity>, Iterable<UnauthenticatedRemoteApplication>> transformResults(Collection<Either<ContentLinkEntity, UnauthenticatedRemoteApplication>> results) {
        return Pair.pair((Object)Options.flatten((Iterable)Collections2.transform(results, from -> from.left().toOption())), (Object)Options.flatten((Iterable)Collections2.transform(results, from -> from.right().toOption())));
    }

    private <T extends ConditionalDescriptor> List<T> filterFragmentsByCondition(List<T> relevantItems, Map<String, Object> context) {
        if (relevantItems.isEmpty()) {
            return relevantItems;
        }
        ArrayList<T> result = new ArrayList<T>(relevantItems);
        Iterator iterator = result.iterator();
        while (iterator.hasNext()) {
            ConditionalDescriptor descriptor = (ConditionalDescriptor)iterator.next();
            try {
                if (descriptor.getCondition() == null || descriptor.getCondition().shouldDisplay(context)) continue;
                iterator.remove();
            }
            catch (Exception e) {
                log.error("Could not evaluate condition '" + descriptor.getCondition() + "' for descriptor: " + descriptor, (Throwable)e);
                iterator.remove();
            }
        }
        return result;
    }

    private List<ContentLinkModuleDescriptor> filterFragmentsByTypeId(@Nonnull List<ContentLinkModuleDescriptor> descriptors, @Nonnull TypeId entityType) {
        ArrayList<ContentLinkModuleDescriptor> result = new ArrayList<ContentLinkModuleDescriptor>(descriptors);
        Iterator iterator = result.iterator();
        while (iterator.hasNext()) {
            ContentLinkModuleDescriptor projectShortcutModuleDescriptor = (ContentLinkModuleDescriptor)((Object)iterator.next());
            Set<TypeId> entityTypes = projectShortcutModuleDescriptor.getEntityTypes();
            if (entityTypes == null || entityTypes.isEmpty() || entityTypes.contains(entityType)) continue;
            iterator.remove();
        }
        return result;
    }
}

