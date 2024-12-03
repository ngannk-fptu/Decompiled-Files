/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.relations.Relatable
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 */
package com.atlassian.confluence.api.impl.service.relation;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.internal.relations.RelatableEntity;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUserResolver;

public class RelatableResolver {
    private final SpaceManager spaceManager;
    private final ContentEntityManagerInternal contentEntityManager;
    private final ConfluenceUserResolver confluenceUserResolver;

    public RelatableResolver(SpaceManager spaceManager, ContentEntityManagerInternal contentEntityManager, ConfluenceUserResolver confluenceUserResolver) {
        this.spaceManager = spaceManager;
        this.contentEntityManager = contentEntityManager;
        this.confluenceUserResolver = confluenceUserResolver;
    }

    public RelatableEntity resolve(Relatable relatable) {
        RelatableEntity entity = null;
        if (relatable instanceof Content) {
            Content content = (Content)relatable;
            entity = this.resolveContent(content);
            if (entity == null) {
                throw new NotFoundException("Could not find content with ID " + content.getId().asLong() + " and status " + content.getStatus().serialise());
            }
        } else if (relatable instanceof User) {
            User apiUser = (User)relatable;
            entity = apiUser.optionalUserKey().isPresent() ? this.confluenceUserResolver.getUserByKey(apiUser.optionalUserKey().orElse(null)) : this.confluenceUserResolver.getUserByName(apiUser.getUsername());
        } else if (relatable instanceof com.atlassian.confluence.api.model.content.Space) {
            Space space = this.spaceManager.getSpace(((com.atlassian.confluence.api.model.content.Space)relatable).getKey());
            if (space != null) {
                this.spaceManager.ensureSpaceDescriptionExists(space);
                entity = space.getDescription();
            }
        } else {
            throw new NotImplementedServiceException("Unknown relatable type : " + relatable.getClass());
        }
        if (entity == null) {
            throw new NotFoundException("Could not find entity : " + relatable, SimpleValidationResult.VALID);
        }
        return entity;
    }

    private RelatableEntity resolveContent(Content relatableContent) {
        if (ContentStatus.DRAFT.equals((Object)relatableContent.getStatus())) {
            return this.contentEntityManager.findDraftFor(relatableContent.getId().asLong());
        }
        if (ContentStatus.CURRENT.equals((Object)relatableContent.getStatus())) {
            ContentEntityObject ceo = this.contentEntityManager.getById(relatableContent.getId().asLong());
            return ceo != null && ContentStatus.CURRENT.equals((Object)ceo.getContentStatusObject()) ? ceo : null;
        }
        if (ContentStatus.HISTORICAL.equals((Object)relatableContent.getStatus())) {
            ContentEntityObject ceo = this.contentEntityManager.getById(relatableContent.getId().asLong());
            return ceo != null ? this.contentEntityManager.getOtherVersion(ceo, relatableContent.getVersion().getNumber()) : null;
        }
        throw new NotImplementedServiceException("Cannot resolve content with status " + relatableContent.getStatus().serialise());
    }
}

