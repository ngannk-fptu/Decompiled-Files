/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.factory.card;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.plugins.mobile.model.card.Card;
import com.atlassian.confluence.plugins.mobile.model.card.CardObject;
import com.atlassian.confluence.plugins.mobile.service.factory.card.cardactivity.CardActivityFactory;
import com.atlassian.confluence.plugins.mobile.service.factory.card.cardobject.CardObjectFactory;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CardFactory {
    private final CommentManager commentManager;
    private final CardObjectFactory cardObjectFactory;
    private final CardActivityFactory cardActivityFactory;

    @Autowired
    public CardFactory(@ComponentImport CommentManager commentManager, CardObjectFactory cardObjectFactory, CardActivityFactory cardActivityFactory) {
        this.commentManager = commentManager;
        this.cardObjectFactory = cardObjectFactory;
        this.cardActivityFactory = cardActivityFactory;
    }

    public List<Card> build(List<SearchResult> results) {
        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<SearchResult>> groupByIdMap = results.stream().filter(this::filterFileComment).collect(Collectors.groupingBy(this::getContentId));
        ArrayList<Card> cards = new ArrayList<Card>();
        groupByIdMap.entrySet().stream().forEach(e -> {
            Card card = this.buildCard((Long)e.getKey(), (List)e.getValue());
            if (card != null) {
                cards.add(card);
            }
        });
        cards.sort((card1, card2) -> card2.getTime().compareTo(card1.getTime()));
        return cards;
    }

    private Card buildCard(Long contentId, List<SearchResult> results) {
        CardObject cardObject = this.cardObjectFactory.buildPageCardObject(contentId, results);
        if (cardObject == null) {
            return null;
        }
        SearchResult lastSearchResult = results.get(0);
        return Card.builder().cardObject(cardObject).activities(this.cardActivityFactory.buildCardActivities(lastSearchResult)).time(lastSearchResult.getLastModificationDate().getTime()).build();
    }

    private boolean filterFileComment(SearchResult result) {
        return !ContentType.COMMENT.getValue().equals(result.getType()) || !StringUtils.isBlank((CharSequence)result.getField("containingPageId"));
    }

    private Long getContentId(SearchResult result) throws ServiceException {
        Long contentId = null;
        if (ContentType.COMMENT.getValue().equals(result.getType())) {
            contentId = this.getPageIdOfComment(result);
        } else if (result.getHandle() instanceof HibernateHandle) {
            contentId = ((HibernateHandle)result.getHandle()).getId();
        }
        if (contentId == null) {
            throw new ServiceException("Cannot get content id");
        }
        return contentId;
    }

    private Long getPageIdOfComment(SearchResult result) {
        try {
            return Long.parseLong(result.getField("containingPageId"));
        }
        catch (NumberFormatException e) {
            if (result.getHandle() instanceof HibernateHandle) {
                Long commentId = ((HibernateHandle)result.getHandle()).getId();
                Comment comment = this.commentManager.getComment(commentId.longValue());
                return comment.getContainer() != null ? Long.valueOf(comment.getContainer().getId()) : null;
            }
            return null;
        }
    }
}

