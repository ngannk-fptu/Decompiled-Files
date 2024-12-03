/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.fugue.Pair
 *  com.atlassian.mywork.model.Notification
 *  com.atlassian.mywork.model.NotificationBuilder
 *  com.atlassian.mywork.model.Registration
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.JsonNode
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.service;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import com.atlassian.mywork.host.event.RegistrationChangedEvent;
import com.atlassian.mywork.host.service.HTMLServiceImpl;
import com.atlassian.mywork.host.service.LocalNotificationServiceImpl;
import com.atlassian.mywork.host.service.LocalRegistrationService;
import com.atlassian.mywork.host.service.NotificationRendererService;
import com.atlassian.mywork.host.soy.GetTextFunction;
import com.atlassian.mywork.host.soy.SoyUtils;
import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.model.NotificationBuilder;
import com.atlassian.mywork.model.Registration;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.LocaleResolver;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.multibindings.Multibinder;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.SoyModule;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.UnsafeSanitizedContentOrdainer;
import com.google.template.soy.data.restricted.NullData;
import com.google.template.soy.shared.restricted.SoyFunction;
import com.google.template.soy.tofu.SoyTofu;
import com.google.template.soy.tofu.SoyTofuException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class NotificationRendererServiceImpl
implements NotificationRendererService,
DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(LocalNotificationServiceImpl.class);
    private final LocalRegistrationService registrationService;
    private final LocaleResolver localeResolver;
    private final HTMLServiceImpl htmlService;
    private final HostApplication hostApplication;
    private final ApplicationLinkService applicationLinkService;
    private final EventPublisher eventPublisher;
    private final LoadingCache<String, Option<SoyTofu>> cache = CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<String, Option<SoyTofu>>(){

        public Option<SoyTofu> load(String appId) {
            List allRegistrations = (List)((Pair)NotificationRendererServiceImpl.this.registrationService.getAll(new Date(0L)).get()).left();
            Iterable registrations = Iterables.filter((Iterable)allRegistrations, input -> input.getAppId().equals(appId));
            final HashMap i18n = new HashMap();
            ArrayList<String> allTemplates = new ArrayList<String>();
            for (Registration registration : registrations) {
                for (Map.Entry entry : registration.getI18n().entrySet()) {
                    HashMap l10n = (HashMap)i18n.get(entry.getKey());
                    if (l10n == null) {
                        l10n = new HashMap();
                        i18n.put((String)entry.getKey(), l10n);
                    }
                    l10n.putAll((Map)entry.getValue());
                }
                String templates = registration.getTemplates();
                if (templates == null) continue;
                allTemplates.add(templates);
            }
            if (allTemplates.isEmpty()) {
                return Option.none();
            }
            Injector injector = Guice.createInjector(new AbstractModule(){

                @Override
                protected void configure() {
                    Multibinder<SoyFunction> soyFunctionsSetBinder = Multibinder.newSetBinder(this.binder(), SoyFunction.class);
                    soyFunctionsSetBinder.addBinding().toInstance(new GetTextFunction(NotificationRendererServiceImpl.this.localeResolver, i18n));
                }
            }, new SoyModule());
            SoyFileSet.Builder soyFileSetBuilder = injector.getInstance(SoyFileSet.Builder.class);
            for (String templates : allTemplates) {
                Matcher m = Pattern.compile("\\{namespace(.*?)\\}").matcher(templates);
                Object templateName = "Unknown template";
                if (m.find()) {
                    templateName = m.group(1);
                }
                templateName = (String)templateName + ": UUID[" + UUID.randomUUID().toString() + "]";
                soyFileSetBuilder.add(templates, (String)templateName);
            }
            SoyTofu tofu = soyFileSetBuilder.build().compileToTofu();
            return Option.some((Object)tofu);
        }
    });

    public NotificationRendererServiceImpl(LocalRegistrationService registrationService, @ComponentImport LocaleResolver localeResolver, HTMLServiceImpl htmlService, @ComponentImport @Qualifier(value="hostApplication") HostApplication hostApplication, ApplicationLinkService applicationLinkService, EventPublisher eventPublisher) {
        this.registrationService = registrationService;
        this.localeResolver = localeResolver;
        this.htmlService = htmlService;
        this.hostApplication = hostApplication;
        this.applicationLinkService = applicationLinkService;
        this.eventPublisher = eventPublisher;
        eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onRegistrationChangedEvent(RegistrationChangedEvent event) {
        this.cache.invalidate((Object)event.getRegistration().getAppId());
    }

    @EventListener
    public void onRegistrationChangedEvent(ClusterEventWrapper clusterEventWrapper) {
        if (clusterEventWrapper.getEvent() instanceof RegistrationChangedEvent) {
            this.onRegistrationChangedEvent((RegistrationChangedEvent)clusterEventWrapper.getEvent());
        }
    }

    @Override
    public Notification renderDescription(Notification notification) {
        return (Notification)Option.option((Object)notification.getApplicationLinkId()).flatMap(appId -> (Option)this.cache.getUnchecked(appId)).flatMap(tofu -> {
            try {
                String description = tofu.newRenderer(this.createDescriptionTemplateName(notification)).setData(this.getData(notification)).render();
                return Option.some((Object)new NotificationBuilder(notification).description(this.htmlService.clean(description)).createNotification());
            }
            catch (SoyTofuException e) {
                LOG.debug(e.getMessage());
                return Option.none();
            }
        }).getOrElse((Object)notification);
    }

    private SoyMapData getData(Notification notification) {
        String description = notification.getDescription();
        return new SoyMapData((Map<String, ?>)ImmutableMap.of((Object)"description", (Object)(description != null ? UnsafeSanitizedContentOrdainer.ordainAsSafe(description, SanitizedContent.ContentKind.HTML) : NullData.INSTANCE), (Object)"metadata", (Object)SoyUtils.toSoyData((JsonNode)notification.getMetadata()), (Object)"baseUrl", (Object)this.getBaseUrl(notification)));
    }

    @Override
    public Iterable<Notification> renderDescriptions(Iterable<Notification> notifications) {
        return Iterables.transform(notifications, input -> this.renderDescription((Notification)input));
    }

    private String createDescriptionTemplateName(Notification notification) {
        String partialName = notification.getEntity() + StringUtils.capitalize((String)notification.getAction()) + "Description";
        return notification.getApplication() + "." + partialName.replaceAll("[^\\w\\d]", "_");
    }

    private String getBaseUrl(Notification notification) {
        if (notification.getApplicationLinkId().equals("")) {
            return this.hostApplication.getBaseUrl().toASCIIString();
        }
        try {
            ApplicationId appId = new ApplicationId(notification.getApplicationLinkId());
            return this.applicationLinkService.getApplicationLink(appId).getDisplayUrl().toASCIIString();
        }
        catch (TypeNotInstalledException e) {
            throw new RuntimeException(e);
        }
    }
}

