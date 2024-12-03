/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.factory.card.cardactivity;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.plugins.mobile.model.card.ActivityObject;
import com.atlassian.confluence.plugins.mobile.model.card.ActivityType;
import com.atlassian.confluence.plugins.mobile.model.card.CardActivity;
import com.atlassian.confluence.plugins.mobile.model.card.ContentActivityObject;
import com.atlassian.confluence.plugins.mobile.service.factory.PersonFactory;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CardActivityFactory {
    private static final String DOCUMENT_EXCERPT_FIELD = "excerpt";
    private final PersonFactory personFactory;
    private final CommentManager commentManager;

    @Autowired
    public CardActivityFactory(PersonFactory personFactory, @ComponentImport CommentManager commentManager) {
        this.personFactory = personFactory;
        this.commentManager = commentManager;
    }

    public Map<String, CardActivity> buildCardActivities(SearchResult lastSearchResult) {
        if (lastSearchResult.getType().equals(ContentType.COMMENT.getValue())) {
            return this.buildCommentActivity(lastSearchResult);
        }
        return this.buildPageActivity(lastSearchResult);
    }

    private Map<String, CardActivity> buildPageActivity(SearchResult lastSearchResult) {
        return Collections.singletonMap(ContentType.PAGE.getValue(), CardActivity.builder().type(lastSearchResult.getContentVersion() == 1 ? ActivityType.CREATE : ActivityType.EDIT).count(1).lastActivity(this.buildActivityObject(lastSearchResult)).build());
    }

    private Map<String, CardActivity> buildCommentActivity(SearchResult lastSearchResult) {
        return Collections.singletonMap(ContentType.COMMENT.getValue(), CardActivity.builder().type(ActivityType.COMMENT).count(1).lastActivity(this.buildActivityObject(lastSearchResult)).build());
    }

    private ActivityObject buildActivityObject(SearchResult result) {
        Long id = ((HibernateHandle)result.getHandle()).getId();
        Person actionBy = this.personFactory.forUser(result.getLastModifierUser());
        String excerpt = result.getField(DOCUMENT_EXCERPT_FIELD);
        Map<String, Object> properties = this.buildActivityObjectProperties(id, result.getType());
        return new ContentActivityObject(id, actionBy, result.getLastModificationDate(), excerpt, properties);
    }

    private Map<String, Object> buildActivityObjectProperties(Long id, String type) {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<String, Object>();
        if (!ContentType.COMMENT.getValue().equals(type)) {
            return properties;
        }
        Comment comment = this.commentManager.getComment(id.longValue());
        properties.put("isInlineComment", comment.isInlineComment());
        if (comment.getParent() != null) {
            properties.put("parent", this.buildActivityObject(comment.getParent()));
        } else if (comment.isInlineComment()) {
            properties.put("highlightText", comment.getProperties().getStringProperty("inline-original-selection"));
        }
        return properties;
    }

    private ActivityObject buildActivityObject(Comment comment) {
        Long id = comment.getId();
        Person actionBy = this.personFactory.forUser(comment.getLastModifier());
        String excerpt = comment.getExcerpt();
        return new ContentActivityObject(id, actionBy, comment.getLastModificationDate(), excerpt, null);
    }
}

