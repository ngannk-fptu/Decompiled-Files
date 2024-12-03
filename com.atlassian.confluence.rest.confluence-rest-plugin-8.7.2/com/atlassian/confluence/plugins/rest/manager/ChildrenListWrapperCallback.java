/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback
 *  com.atlassian.plugins.rest.common.expand.parameter.Indexes
 */
package com.atlassian.confluence.plugins.rest.manager;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.entities.ContentEntityList;
import com.atlassian.confluence.plugins.rest.manager.ContentEntityMapping;
import com.atlassian.confluence.plugins.rest.manager.RestContentManager;
import com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback;
import com.atlassian.plugins.rest.common.expand.parameter.Indexes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChildrenListWrapperCallback
implements ListWrapperCallback<ContentEntity> {
    private RestContentManager restContentManager;
    private final ContentEntityObject parentConfluenceObject;
    private List<ContentEntityMapping> childrenItems = null;

    public ChildrenListWrapperCallback(RestContentManager restContentManager, ContentEntityObject parentConfluenceObject) {
        this.restContentManager = restContentManager;
        this.parentConfluenceObject = parentConfluenceObject;
    }

    public List<ContentEntity> getItems(Indexes indexes) {
        int size = this.getChildrenItems().size();
        if (size == 0) {
            return Collections.emptyList();
        }
        int startIndex = Math.max(0, indexes.getMinIndex(size));
        int endIndex = Math.max(0, indexes.getMaxIndex(size)) + 1;
        List<ContentEntityMapping> childrenEntities = this.getChildrenItems().subList(startIndex, endIndex);
        ArrayList<ContentEntity> result = new ArrayList<ContentEntity>();
        for (ContentEntityMapping item : childrenEntities) {
            if (item.getContentEntity().getChildren() == null) {
                ChildrenListWrapperCallback grandChildren = new ChildrenListWrapperCallback(this.restContentManager, item.getConfluenceObject());
                item.getContentEntity().setChildren(new ContentEntityList(grandChildren.getSize(), grandChildren));
            }
            result.add(item.getContentEntity());
        }
        return result;
    }

    public int getSize() {
        return this.getChildrenItems().size();
    }

    private List<ContentEntityMapping> getChildrenItems() {
        if (this.childrenItems == null) {
            this.childrenItems = new ArrayList<ContentEntityMapping>();
            List children = Collections.emptyList();
            if (this.parentConfluenceObject instanceof Page) {
                children = ((Page)this.parentConfluenceObject).getSortedChildren();
            } else if (this.parentConfluenceObject instanceof Comment) {
                children = ((Comment)this.parentConfluenceObject).getChildren();
            }
            for (ContentEntityObject child : children) {
                ContentEntity contentEntity = this.restContentManager.convertToContentEntity(child);
                if (contentEntity == null) continue;
                this.childrenItems.add(new ContentEntityMapping(contentEntity, child));
            }
        }
        return this.childrenItems;
    }
}

