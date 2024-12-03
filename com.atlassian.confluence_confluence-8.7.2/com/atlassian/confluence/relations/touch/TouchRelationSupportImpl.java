/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.people.KnownUser
 *  com.atlassian.confluence.api.model.relations.Relatable
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.api.model.relations.RelationInstance
 *  com.atlassian.confluence.api.model.relations.TouchedRelationDescriptor
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.relations.RelationService
 */
package com.atlassian.confluence.relations.touch;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.model.relations.RelationInstance;
import com.atlassian.confluence.api.model.relations.TouchedRelationDescriptor;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.relations.RelationService;
import com.atlassian.confluence.relations.touch.TouchRelationSupport;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class TouchRelationSupportImpl
implements TouchRelationSupport {
    private final ContentService contentService;
    private final RelationService relationService;

    public TouchRelationSupportImpl(ContentService contentService, RelationService relationService) {
        this.contentService = contentService;
        this.relationService = relationService;
    }

    @Override
    public void handleTouchRelations(Content input) {
        Objects.requireNonNull(input.getType());
        Objects.requireNonNull(input.getId());
        if (!ContentType.PAGE.equals((Object)input.getType()) && !ContentType.BLOG_POST.equals((Object)input.getType())) {
            return;
        }
        Map<ContentStatus, Optional<Content>> content = this.potentiallyResolveContent(input);
        Optional<Content> draft = content.get(ContentStatus.DRAFT);
        Optional<Content> current = content.get(ContentStatus.CURRENT);
        if (draft.isPresent()) {
            this.updateTouchRelation(draft.get());
            current.ifPresent(this::removeTouchRelation);
        } else {
            current.ifPresent(this::updateTouchRelation);
        }
    }

    private Map<ContentStatus, Optional<Content>> potentiallyResolveContent(Content inputContent) {
        HashMap output = new HashMap(2);
        List<ContentStatus> contentStatuses = Arrays.asList(ContentStatus.CURRENT, ContentStatus.DRAFT);
        contentStatuses.forEach(contentStatus -> output.put(contentStatus, this.contentService.find(new Expansion[0]).withStatus(new ContentStatus[]{contentStatus}).withId(inputContent.getId()).fetch()));
        return Collections.unmodifiableMap(output);
    }

    private Optional<KnownUser> getKnownUser() {
        return Optional.ofNullable(AuthenticatedUserThreadLocal.get()).map(user -> KnownUser.builder().userKey(user.getKey()).username(user.getName()).displayName(user.getFullName()).build());
    }

    private void updateTouchRelation(Content content) {
        this.getKnownUser().ifPresent(user -> {
            this.relationService.delete(RelationInstance.builder((Relatable)user, (RelationDescriptor)TouchedRelationDescriptor.TOUCHED, (Relatable)content).build());
            this.relationService.create(RelationInstance.builder((Relatable)user, (RelationDescriptor)TouchedRelationDescriptor.TOUCHED, (Relatable)content).build());
        });
    }

    private void removeTouchRelation(Content content) {
        this.getKnownUser().ifPresent(user -> this.relationService.delete(RelationInstance.builder((Relatable)user, (RelationDescriptor)TouchedRelationDescriptor.TOUCHED, (Relatable)content).build()));
    }
}

