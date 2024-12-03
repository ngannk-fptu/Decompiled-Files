/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ListBuilder
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceType
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.confluence.spaces.SpacesQuery$Builder
 *  com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback
 *  com.atlassian.plugins.rest.common.expand.parameter.Indexes
 *  com.atlassian.user.User
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.plugins.rest.manager;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.entities.ContentEntityList;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntity;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntityList;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntityListContext;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntityUserProperties;
import com.atlassian.confluence.plugins.rest.entities.builders.EntityBuilderFactory;
import com.atlassian.confluence.plugins.rest.entities.builders.SpaceEntityBuilder;
import com.atlassian.confluence.plugins.rest.manager.DefaultRestContentManager;
import com.atlassian.confluence.plugins.rest.manager.RequestContext;
import com.atlassian.confluence.plugins.rest.manager.RequestContextThreadLocal;
import com.atlassian.confluence.plugins.rest.manager.RestSpaceManager;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback;
import com.atlassian.plugins.rest.common.expand.parameter.Indexes;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.util.Assert;

public class DefaultRestSpaceManager
implements RestSpaceManager {
    private static final int DEFAULT_MAX_SIZE = 50;
    private final SpaceManager spaceManager;
    private final PageManager pageManager;
    private final DefaultRestContentManager restContentManager;
    private final EntityBuilderFactory entityBuilderFactory;

    public DefaultRestSpaceManager(SpaceManager spaceManager, PageManager pageManager, DefaultRestContentManager restContentManager, EntityBuilderFactory entityBuilderFactory) {
        this.spaceManager = spaceManager;
        this.pageManager = pageManager;
        this.restContentManager = restContentManager;
        this.entityBuilderFactory = entityBuilderFactory;
    }

    private SpaceEntity expand(SpaceEntity spaceEntity, Space space) {
        spaceEntity.setHome(this.restContentManager.convertToContentEntity((ContentEntityObject)space.getHomePage()));
        ListBuilder builder = this.pageManager.getTopLevelPagesBuilder(space);
        int availableSize = builder.getAvailableSize();
        spaceEntity.setRootPages(new ContentEntityList(availableSize, new RootPagesListWrapperCallback((ListBuilder<Page>)builder)));
        spaceEntity.setUserProperties(new SpaceEntityUserProperties(space.getKey()));
        spaceEntity.setDescription(space.getDescription().getBodyContent().getBody());
        return spaceEntity;
    }

    @Override
    public SpaceEntity expand(SpaceEntity spaceEntity) {
        return this.expand(spaceEntity, Objects.requireNonNull(this.spaceManager.getSpace(spaceEntity.getKey())));
    }

    public SpaceEntity getSpaceEntity(Space space) {
        if (space == null) {
            return null;
        }
        return ((SpaceEntityBuilder)this.entityBuilderFactory.createBuilder(ContentTypeEnum.SPACE_DESCRIPTION.getRepresentation())).build(space);
    }

    private SpaceEntity getSpaceEntity(Space space, boolean expand) {
        SpaceEntity spaceEntity = this.getSpaceEntity(space);
        spaceEntity.setDescription(space.getDescription().getBodyContent().getBody());
        return expand ? this.expand(spaceEntity, space) : spaceEntity;
    }

    @Override
    public SpaceEntity getSpaceEntity(String spaceKey, boolean expand) {
        RequestContext requestContext = RequestContextThreadLocal.get();
        SpacesQuery query = SpacesQuery.newQuery().withSpaceKey(spaceKey).forUser(requestContext.getUser()).build();
        List spaces = this.spaceManager.getAllSpaces(query);
        if (spaces.isEmpty()) {
            return null;
        }
        return this.getSpaceEntity((Space)spaces.get(0), expand);
    }

    @Override
    public SpaceEntityList getSpaceEntityList(SpaceEntityListContext ctx) {
        Assert.notNull((Object)ctx, (String)"SpaceEntityListContext must not be null");
        RequestContext requestContext = RequestContextThreadLocal.get();
        User user = requestContext.getUser();
        SpacesQuery.Builder builder = SpacesQuery.newQuery().forUser(user);
        if (!"all".equals(ctx.getSpaceType())) {
            builder.withSpaceType(SpaceType.getSpaceType((String)ctx.getSpaceType()));
        }
        for (String spaceKey : ctx.getSpaceKeys()) {
            builder.withSpaceKey(spaceKey);
        }
        List spaces = this.spaceManager.getSpaces(builder.build()).getPage(ctx.getStartIndex() == null ? 0 : Math.max(0, ctx.getStartIndex()), ctx.getMaxResults() == null ? 50 : Math.min(50, ctx.getMaxResults()));
        SpaceEntityList result = new SpaceEntityList();
        for (Space s : spaces) {
            result.getSpaces().add(this.getSpaceEntity(s, false));
        }
        return result;
    }

    private class RootPagesListWrapperCallback
    implements ListWrapperCallback<ContentEntity> {
        private final ListBuilder<Page> delegate;

        public RootPagesListWrapperCallback(ListBuilder<Page> delegate) {
            this.delegate = delegate;
        }

        public List<ContentEntity> getItems(Indexes indexes) {
            int availableSize = this.delegate.getAvailableSize();
            int offset = indexes.getMinIndex(availableSize);
            int max = Math.min(50, indexes.getMaxIndex(availableSize) + 1 - offset);
            List pages = this.delegate.getPage(offset, max);
            ArrayList<ContentEntity> result = new ArrayList<ContentEntity>();
            for (Page p : pages) {
                result.add(DefaultRestSpaceManager.this.restContentManager.convertToContentEntity((ContentEntityObject)p));
            }
            return result;
        }
    }
}

