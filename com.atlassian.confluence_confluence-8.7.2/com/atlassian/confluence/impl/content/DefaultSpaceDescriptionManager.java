/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.event.api.EventPublisher
 *  org.hibernate.SessionFactory
 */
package com.atlassian.confluence.impl.content;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.impl.content.DefaultContentEntityManager;
import com.atlassian.confluence.internal.relations.RelationManager;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.spaces.SpaceDescriptionManager;
import com.atlassian.confluence.xhtml.api.WikiToStorageConverter;
import com.atlassian.event.api.EventPublisher;
import org.hibernate.SessionFactory;

@ParametersAreNonnullByDefault
public class DefaultSpaceDescriptionManager
extends DefaultContentEntityManager
implements SpaceDescriptionManager {
    public DefaultSpaceDescriptionManager(ContentEntityObjectDao<ContentEntityObject> contentEntityObjectDao, SessionFactory sessionFactory, WikiToStorageConverter wikiToStorageConverter, EventPublisher eventPublisher, RelationManager relationManager, CollaborativeEditingHelper collaborativeEditingHelper, AuditingContext auditingContext, RetentionFeatureChecker retentionFeatureChecker) {
        super(contentEntityObjectDao, sessionFactory, wikiToStorageConverter, eventPublisher, relationManager, collaborativeEditingHelper, auditingContext, retentionFeatureChecker, DefaultContentEntityManager.EventFactory.noEvents());
    }
}

