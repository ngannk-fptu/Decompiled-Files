/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Option
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.edgeindex.EdgeFactory;
import com.atlassian.confluence.plugins.edgeindex.EdgeTypeRepository;
import com.atlassian.confluence.plugins.edgeindex.model.DefaultEdge;
import com.atlassian.confluence.plugins.edgeindex.model.Edge;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeType;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Option;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="edgeFactory")
public class DefaultEdgeFactory
implements EdgeFactory {
    private final EdgeTypeRepository edgeTypeRepository;

    @Autowired
    public DefaultEdgeFactory(EdgeTypeRepository edgeTypeRepository) {
        this.edgeTypeRepository = edgeTypeRepository;
    }

    @Override
    public Edge getCreateEdge(ContentEntityObject contentEntity) {
        if (contentEntity == null) {
            throw new IllegalArgumentException("content entity cannot be null");
        }
        return (Edge)this.getEdgeType(contentEntity).map(input -> {
            ContentEntityObject target = contentEntity;
            if (contentEntity instanceof Comment) {
                Comment comment = (Comment)contentEntity;
                if (comment.getParent() != null) {
                    target = comment.getParent();
                } else if (comment.getContainer() != null) {
                    target = comment.getContainer();
                } else {
                    throw new UnsupportedOperationException("comment without parent or owner is not supported.");
                }
            }
            return new DefaultEdge(contentEntity.getCreator(), (EdgeType)input, target, contentEntity.getCreationDate(), contentEntity.getIdAsString());
        }).getOrNull();
    }

    @Override
    public Edge getLikeEdge(ConfluenceUser liker, ContentEntityObject likedContent, Date likeDate) {
        String likeId = UUID.randomUUID().toString();
        return (Edge)this.edgeTypeRepository.getEdgeIndexTypeByKey("like.create").map(edgeType -> new DefaultEdge(liker, (EdgeType)edgeType, likedContent, likeDate, likeId)).getOrNull();
    }

    @Override
    public boolean canBuildCreatEdge(ContentEntityObject contentEntity) {
        return contentEntity instanceof Comment || contentEntity instanceof AbstractPage;
    }

    private Option<EdgeType> getEdgeType(ContentEntityObject contentEntity) {
        if (contentEntity instanceof Page) {
            return this.edgeTypeRepository.getEdgeIndexTypeByKey("page.create");
        }
        if (contentEntity instanceof BlogPost) {
            return this.edgeTypeRepository.getEdgeIndexTypeByKey("blogpost.create");
        }
        if (contentEntity instanceof Comment) {
            return this.edgeTypeRepository.getEdgeIndexTypeByKey("comment.create");
        }
        return Option.none();
    }
}

