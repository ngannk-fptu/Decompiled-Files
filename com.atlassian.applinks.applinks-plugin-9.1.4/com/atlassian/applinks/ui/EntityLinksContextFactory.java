/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.ApplicationTypeVisitor
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.api.application.bamboo.BambooApplicationType
 *  com.atlassian.applinks.api.application.bitbucket.BitbucketApplicationType
 *  com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType
 *  com.atlassian.applinks.api.application.crowd.CrowdApplicationType
 *  com.atlassian.applinks.api.application.fecru.FishEyeCrucibleApplicationType
 *  com.atlassian.applinks.api.application.generic.GenericApplicationType
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.applinks.api.application.refapp.RefAppApplicationType
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.link.MutatingEntityLinkService
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.annotate.JsonMethod
 *  org.codehaus.jackson.map.AnnotationIntrospector
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.xc.JaxbAnnotationIntrospector
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.ui;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.ApplicationTypeVisitor;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.application.bamboo.BambooApplicationType;
import com.atlassian.applinks.api.application.bitbucket.BitbucketApplicationType;
import com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType;
import com.atlassian.applinks.api.application.crowd.CrowdApplicationType;
import com.atlassian.applinks.api.application.fecru.FishEyeCrucibleApplicationType;
import com.atlassian.applinks.api.application.generic.GenericApplicationType;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.applinks.api.application.refapp.RefAppApplicationType;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.rest.model.EntityLinkEntity;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.rest.applink.data.RestApplinkDataProviders;
import com.atlassian.applinks.internal.rest.model.applink.RestExtendedApplicationLink;
import com.atlassian.applinks.spi.link.MutatingEntityLinkService;
import com.atlassian.applinks.ui.AbstractApplinksServlet;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ParametersAreNonnullByDefault
public class EntityLinksContextFactory {
    private static final String APPLINK_ICON_KEY = "iconUri";
    private static final Logger LOG = LoggerFactory.getLogger(EntityLinksContextFactory.class);
    private final InternalTypeAccessor typeAccessor;
    private final MutatingEntityLinkService mutatingEntityLinkService;
    private final ApplicationLinkService applicationLinkService;
    private final I18nResolver i18nResolver;
    private final InternalHostApplication internalHostApplication;
    private final MessageFactory messageFactory;
    private final RestApplinkDataProviders dataProviders;

    @Autowired
    public EntityLinksContextFactory(InternalTypeAccessor typeAccessor, MutatingEntityLinkService mutatingEntityLinkService, ApplicationLinkService applicationLinkService, I18nResolver i18nResolver, InternalHostApplication internalHostApplication, MessageFactory messageFactory, RestApplinkDataProviders dataProviders) {
        this.typeAccessor = Objects.requireNonNull(typeAccessor, "typeAccessor");
        this.mutatingEntityLinkService = Objects.requireNonNull(mutatingEntityLinkService, "entityLinksService");
        this.applicationLinkService = Objects.requireNonNull(applicationLinkService, "applinksService");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.internalHostApplication = Objects.requireNonNull(internalHostApplication, "internalHostApplication");
        this.messageFactory = Objects.requireNonNull(messageFactory, "messageFactory");
        this.dataProviders = Objects.requireNonNull(dataProviders, "dataProviders");
    }

    public Map<String, Object> createContext(String typeId, String projectKey) throws IOException {
        EntityType entityType = this.typeAccessor.loadEntityType(typeId);
        if (entityType == null || !this.internalHostApplication.doesEntityExist(projectKey, entityType.getClass())) {
            throw new AbstractApplinksServlet.BadRequestException(this.messageFactory.newLocalizedMessage(String.format("No entity exists with key %s of type %s", projectKey, typeId)));
        }
        if (!this.internalHostApplication.canManageEntityLinksFor(this.internalHostApplication.toEntityReference(projectKey, entityType.getClass()))) {
            throw new AbstractApplinksServlet.UnauthorizedException(this.messageFactory.newI18nMessage("applinks.entity.list.no.manage.permission", new Serializable[]{projectKey, entityType.getClass().getName()}));
        }
        return new ImmutableMap.Builder().put((Object)"projectKey", (Object)projectKey).put((Object)"meta", this.createMeta(projectKey)).put((Object)"data", (Object)this.createDataJson(projectKey, entityType)).build();
    }

    private String createDataJson(String projectKey, EntityType entityType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setAnnotationIntrospector((AnnotationIntrospector)new JaxbAnnotationIntrospector());
        objectMapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper.writeValueAsString((Object)ImmutableMap.of((Object)"entityLinks", this.getEntityLinks(projectKey, entityType), (Object)"applicationLinks", this.getApplicationLinks(), (Object)"currentApp", (Object)this.i18nResolver.getText(this.internalHostApplication.getType().getI18nKey()), (Object)"projectKey", (Object)projectKey, (Object)"type", (Object)entityType.getClass().getName()));
    }

    private List<RestExtendedApplicationLink> getApplicationLinks() {
        return StreamSupport.stream(this.applicationLinkService.getApplicationLinks().spliterator(), false).map(link -> new RestExtendedApplicationLink((ApplicationLink)link, Collections.emptySet(), this.createData((ApplicationLink)link))).collect(Collectors.toList());
    }

    private Map<String, Object> createData(ApplicationLink link) {
        try {
            return ImmutableMap.builder().put((Object)APPLINK_ICON_KEY, this.dataProviders.getProvider(APPLINK_ICON_KEY).provide(APPLINK_ICON_KEY, link)).build();
        }
        catch (ServiceException e) {
            LOG.error("Did not enhance data for applink map because of:", (Throwable)e);
            return Collections.emptyMap();
        }
    }

    private List<EntityLinkEntity> getEntityLinks(String projectKey, EntityType entityType) {
        return StreamSupport.stream(this.mutatingEntityLinkService.getEntityLinksForKey(projectKey, entityType.getClass()).spliterator(), false).map(EntityLinkEntity::new).collect(Collectors.toList());
    }

    private Map<String, Object> createMeta(final String projectKey) {
        final ImmutableMap.Builder mapBuilder = new ImmutableMap.Builder();
        return ((ImmutableMap.Builder)this.internalHostApplication.getType().accept((ApplicationTypeVisitor)new ApplicationTypeVisitor<ImmutableMap.Builder<String, Object>>(){

            public ImmutableMap.Builder<String, Object> visit(@Nonnull BambooApplicationType type) {
                return mapBuilder.put((Object)"decorator", (Object)"atl.general");
            }

            public ImmutableMap.Builder<String, Object> visit(@Nonnull BitbucketApplicationType type) {
                return mapBuilder.put((Object)"projectKey", (Object)projectKey).put((Object)"decorator", (Object)"bitbucket.project.settings").put((Object)"activeTab", (Object)"project-settings-entity-links");
            }

            public ImmutableMap.Builder<String, Object> visit(@Nonnull ConfluenceApplicationType type) {
                return mapBuilder.put((Object)"decorator", (Object)"atl.admin");
            }

            public ImmutableMap.Builder<String, Object> visit(@Nonnull CrowdApplicationType type) {
                return mapBuilder.put((Object)"decorator", (Object)"atl.admin");
            }

            public ImmutableMap.Builder<String, Object> visit(@Nonnull FishEyeCrucibleApplicationType type) {
                return mapBuilder.put((Object)"decorator", (Object)"atl.admin");
            }

            public ImmutableMap.Builder<String, Object> visit(@Nonnull GenericApplicationType type) {
                return mapBuilder.put((Object)"decorator", (Object)"atl.admin");
            }

            public ImmutableMap.Builder<String, Object> visit(@Nonnull JiraApplicationType type) {
                return mapBuilder.put((Object)"projectKey", (Object)projectKey).put((Object)"decorator", (Object)"admin").put((Object)"admin.active.section", (Object)"atl.jira.proj.config/projectgroup4").put((Object)"admin.active.tab", (Object)"view_project_links");
            }

            public ImmutableMap.Builder<String, Object> visit(@Nonnull RefAppApplicationType type) {
                return mapBuilder.put((Object)"decorator", (Object)"atl.admin");
            }

            public ImmutableMap.Builder<String, Object> visitDefault(@Nonnull ApplicationType applicationType) {
                return mapBuilder.put((Object)"decorator", (Object)"atl.admin");
            }
        })).build();
    }
}

