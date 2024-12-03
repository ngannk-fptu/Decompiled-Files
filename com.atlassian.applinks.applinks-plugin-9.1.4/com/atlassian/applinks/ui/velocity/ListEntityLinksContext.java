/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.host.spi.EntityReference
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.manifest.ManifestNotFoundException
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.ui.velocity;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.rest.util.ResourceUrlHandler;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.host.spi.EntityReference;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.application.IconUriResolver;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.applinks.ui.AbstractApplinksServlet;
import com.atlassian.applinks.ui.velocity.AbstractVelocityContext;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListEntityLinksContext
extends AbstractVelocityContext {
    private final String username;
    private final ApplicationLinkService linkService;
    private final ManifestRetriever manifestRetriever;
    private final DocumentationLinker documentationLinker;
    private final I18nResolver i18nResolver;
    private final MessageFactory messageFactory;
    private final boolean isAdmin;
    private String type;
    private String typeLabel;
    private String name;
    private String key;
    private static final Logger log = LoggerFactory.getLogger(ListEntityLinksContext.class);

    ListEntityLinksContext(ApplicationLinkService linkService, ManifestRetriever manifestRetriever, InternalHostApplication internalHostApplication, DocumentationLinker documentationLinker, I18nResolver i18nResolver, MessageFactory messageFactory, InternalTypeAccessor typeAccessor, String typeId, String key, String contextPath, String username, boolean isAdmin) {
        super(contextPath, internalHostApplication, typeAccessor, documentationLinker);
        this.username = username;
        this.linkService = linkService;
        this.manifestRetriever = manifestRetriever;
        this.documentationLinker = documentationLinker;
        this.i18nResolver = i18nResolver;
        this.messageFactory = messageFactory;
        this.key = key;
        this.isAdmin = isAdmin;
        this.parsePathParams(typeId);
    }

    private void parsePathParams(String typeId) {
        if (!this.internalHostApplication.doesEntityExist(this.key, this.typeAccessor.loadEntityType(typeId).getClass())) {
            throw new AbstractApplinksServlet.BadRequestException(this.messageFactory.newLocalizedMessage(String.format("No entity exists with key %s of type %s", this.key, typeId)));
        }
        EntityType entityType = this.typeAccessor.loadEntityType(typeId);
        this.assertPermission(entityType.getClass(), this.key);
        EntityReference entityReference = this.internalHostApplication.toEntityReference(this.key, entityType.getClass());
        this.type = typeId;
        this.typeLabel = this.messageFactory.newI18nMessage(entityType.getShortenedI18nKey(), new Serializable[0]).toString();
        this.name = entityReference.getName();
    }

    public String getType() {
        return this.type;
    }

    public String getTypeLabel() {
        return this.typeLabel;
    }

    public String getName() {
        return this.name;
    }

    public String getKey() {
        return this.key;
    }

    private void assertPermission(Class<? extends EntityType> entityType, String key) {
        if (!this.internalHostApplication.canManageEntityLinksFor(this.internalHostApplication.toEntityReference(key, entityType))) {
            throw new AbstractApplinksServlet.UnauthorizedException(this.messageFactory.newI18nMessage("applinks.entity.list.no.permission", new Serializable[0]));
        }
    }

    @Override
    public String getContextPath() {
        return this.contextPath;
    }

    public String getUserName() {
        return this.username;
    }

    public List<ApplicationOption> getApplications() {
        return Lists.newArrayList((Iterable)Iterables.filter((Iterable)Iterables.transform((Iterable)this.linkService.getApplicationLinks(), (Function)new Function<ApplicationLink, ApplicationOption>(){

            public ApplicationOption apply(ApplicationLink from) {
                boolean isUal = false;
                try {
                    isUal = ListEntityLinksContext.this.manifestRetriever.getManifest(from.getRpcUrl(), from.getType()).getAppLinksVersion() != null;
                }
                catch (ManifestNotFoundException manifestNotFoundException) {
                }
                catch (Exception ex) {
                    log.error("Could not retrieve manifest for applink, ignoring applink : {}", (Object)from);
                    log.warn("Stack trace: ", (Throwable)ex);
                    return null;
                }
                return new ApplicationOption(from.getId(), from.getName(), TypeId.getTypeId((ApplicationType)from.getType()), from.getType().getI18nKey(), isUal, IconUriResolver.resolveIconUri(from.getType()) == null ? null : IconUriResolver.resolveIconUri(from.getType()).toString());
            }
        }), (Predicate)Predicates.notNull()));
    }

    public ResourceUrlHandler getUrls() {
        return new ResourceUrlHandler(this.internalHostApplication.getBaseUrl().toString());
    }

    public String getApplicationType() {
        return TypeId.getTypeId((ApplicationType)this.internalHostApplication.getType()).get();
    }

    @HtmlSafe
    public String getNoApplinksAdminMessage() {
        return this.i18nResolver.getText("applinks.entity.links.no.applinks.admin", new Serializable[]{this.getApplicationType()});
    }

    public DocumentationLinker getDocumentationLinker() {
        return this.documentationLinker;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public static class ApplicationOption {
        private final String id;
        private final String name;
        private final String typeId;
        private final String typeI18nKey;
        private final boolean isUal;
        private final String iconUri;

        public String getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String getTypeId() {
            return this.typeId;
        }

        public String getTypeI18nKey() {
            return this.typeI18nKey;
        }

        public boolean isUal() {
            return this.isUal;
        }

        public String getIconUri() {
            return this.iconUri;
        }

        public ApplicationOption(ApplicationId id, String name, TypeId typeId, String typeI18nKey, boolean isUal, String iconUri) {
            this.id = id.get();
            this.name = name;
            this.typeId = typeId.get();
            this.typeI18nKey = typeI18nKey;
            this.isUal = isUal;
            this.iconUri = iconUri;
        }
    }
}

