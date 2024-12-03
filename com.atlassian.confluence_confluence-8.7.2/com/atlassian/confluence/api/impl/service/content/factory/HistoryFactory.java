/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.Contributors
 *  com.atlassian.confluence.api.model.content.History
 *  com.atlassian.confluence.api.model.content.History$HistoryBuilder
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.impl.service.content.factory.ContributorsFactory;
import com.atlassian.confluence.api.impl.service.content.factory.Fauxpansions;
import com.atlassian.confluence.api.impl.service.content.factory.PersonFactory;
import com.atlassian.confluence.api.impl.service.content.factory.VersionFactory;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.Contributors;
import com.atlassian.confluence.api.model.content.History;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HistoryFactory {
    private final PersonFactory personFactory;
    private final VersionFactory versionFactory;
    private final ContentEntityManager contentEntityManager;
    private final ContributorsFactory contributorsFactory;
    private final ContentEntityObjectDao contentEntityObjectDao;

    public HistoryFactory(PersonFactory personFactory, VersionFactory versionFactory, ContentEntityManager contentEntityManager, ContributorsFactory contributorsFactory, ContentEntityObjectDao contentEntityObjectDao) {
        this.personFactory = personFactory;
        this.versionFactory = versionFactory;
        this.contentEntityManager = contentEntityManager;
        this.contributorsFactory = contributorsFactory;
        this.contentEntityObjectDao = contentEntityObjectDao;
    }

    public Map<ContentEntityObject, Reference<History>> buildReferences(Iterable<ContentEntityObject> ceos, Fauxpansions historyExpansions, ContentFactory contentFactory) {
        Map<Object, Object> editContributors = Collections.emptyMap();
        if (historyExpansions.getSubExpansions().canExpand("contributors")) {
            editContributors = this.contentEntityObjectDao.getVersionEditContributors(ceos);
        }
        ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
        for (ContentEntityObject ceo : ceos) {
            Reference contentReference = Content.buildReference((ContentSelector)ceo.getSelector());
            mapBuilder.put((Object)ceo, this.buildRef(ceo, (Reference<Content>)contentReference, (List)editContributors.get(ceo.getId()), historyExpansions, contentFactory));
        }
        return mapBuilder.build();
    }

    public Reference<History> buildRef(ContentEntityObject entity, Reference<Content> contentReference, List<ConfluenceUser> editContributors, Fauxpansions fauxpansions, ContentFactory contentFactory) {
        Versioned versionForCreatedAndUpdatedFields;
        History.HistoryBuilder builder = History.builder().content(contentReference);
        if (!fauxpansions.canExpand()) {
            return Reference.collapsed((Object)builder.build());
        }
        Expansions expansions = fauxpansions.getSubExpansions();
        if (entity.isDraft()) {
            builder.latest(false);
            versionForCreatedAndUpdatedFields = entity.isLatestVersion() ? null : entity.getLatestVersion();
        } else {
            builder.latest(entity.isLatestVersion());
            versionForCreatedAndUpdatedFields = entity.getLatestVersion();
        }
        if (versionForCreatedAndUpdatedFields instanceof ContentEntityObject) {
            ContentEntityObject latestEntity = (ContentEntityObject)versionForCreatedAndUpdatedFields;
            builder.createdBy(this.personFactory.forUser(latestEntity.getCreator(), expansions.getSubExpansions("createdBy")));
            builder.createdDate(latestEntity.getCreationDate());
            builder.lastUpdated(this.versionFactory.buildRef(latestEntity, Fauxpansions.fauxpansions(expansions, "lastUpdated"), contentFactory));
        }
        builder.contributors(this.buildContributorsReference(editContributors, fauxpansions));
        if (expansions.canExpand("previousVersion") || expansions.canExpand("nextVersion")) {
            ContentEntityObject prev = this.contentEntityManager.getPreviousVersion(entity);
            ContentEntityObject next = this.contentEntityManager.getNextVersion(entity);
            builder.previousVersion(this.versionFactory.buildRef(prev, Fauxpansions.fauxpansions(expansions, "previousVersion"), contentFactory));
            builder.nextVersion(this.versionFactory.buildRef(next, Fauxpansions.fauxpansions(expansions, "nextVersion"), contentFactory));
        } else {
            builder.previousVersion(Reference.collapsed(Version.class));
            builder.nextVersion(Reference.collapsed(Version.class));
        }
        return Reference.to((Object)builder.build());
    }

    private Reference<Contributors> buildContributorsReference(List<ConfluenceUser> editContributors, Fauxpansions fauxpansions) {
        if (!fauxpansions.getSubExpansions().canExpand("contributors")) {
            return Reference.collapsed(Contributors.class);
        }
        Expansions contributorsExpansions = fauxpansions.getSubExpansions().getSubExpansions("contributors");
        return Reference.to((Object)this.contributorsFactory.buildFrom(editContributors, contributorsExpansions));
    }
}

