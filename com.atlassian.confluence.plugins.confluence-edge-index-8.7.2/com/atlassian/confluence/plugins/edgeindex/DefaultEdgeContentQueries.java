/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.like.LikeEntity
 *  com.atlassian.confluence.persistence.EntityManagerProvider
 *  com.atlassian.fugue.Pair
 *  javax.persistence.Query
 *  javax.persistence.TemporalType
 *  javax.persistence.TypedQuery
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.like.LikeEntity;
import com.atlassian.confluence.persistence.EntityManagerProvider;
import com.atlassian.confluence.plugins.edgeindex.EdgeContentQueries;
import com.atlassian.fugue.Pair;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultEdgeContentQueries
implements EdgeContentQueries {
    static final String LIKES_SINCE_QUERY = "select l.content, l from LikeEntity l where l.creationDate > :startDate";
    static final String CONTENT_SINCE_QUERY = "from ContentEntityObject content\nwhere content.originalVersion is null and\ncontent.contentStatus = 'current' and\n(type(content) = com.atlassian.confluence.pages.Page or\ntype(content) = com.atlassian.confluence.pages.BlogPost or\ntype(content) = com.atlassian.confluence.pages.Comment) and content.creationDate > :startDate";
    private final EntityManagerProvider entityManagerProvider;

    @Autowired
    public DefaultEdgeContentQueries(EntityManagerProvider entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public List<Pair<ContentEntityObject, LikeEntity>> getLikesSince(Date startDate) {
        Query query = this.entityManagerProvider.getEntityManager().createQuery(LIKES_SINCE_QUERY);
        List results = query.setParameter("startDate", startDate, TemporalType.DATE).getResultList();
        ArrayList<Pair<ContentEntityObject, LikeEntity>> likes = new ArrayList<Pair<ContentEntityObject, LikeEntity>>();
        for (Object[] result : results) {
            Pair contentWithLike = new Pair((Object)((ContentEntityObject)result[0]), (Object)((LikeEntity)result[1]));
            likes.add((Pair<ContentEntityObject, LikeEntity>)contentWithLike);
        }
        return likes;
    }

    @Override
    public List<ContentEntityObject> getContentCreatedSince(Date startDate) {
        TypedQuery query = this.entityManagerProvider.getEntityManager().createQuery(CONTENT_SINCE_QUERY, ContentEntityObject.class);
        return query.setParameter("startDate", startDate, TemporalType.DATE).getResultList();
    }
}

