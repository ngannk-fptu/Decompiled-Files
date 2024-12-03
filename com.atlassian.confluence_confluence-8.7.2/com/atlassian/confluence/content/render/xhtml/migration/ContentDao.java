/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.core.bean.EntityObject;
import java.util.List;

public interface ContentDao
extends ContentEntityObjectDao<ContentEntityObject> {
    @Override
    public ContentEntityObject getById(long var1);

    public int getLatestContentCount();

    public List<ContentEntityObject> getLatestOrderedWikiContentFromContentId(long var1, int var3);

    public List<ContentEntityObject> getOrderedXhtmlContentFromContentId(long var1, int var3);

    public List<ContentEntityObject> getLatestOrderedXhtmlContentFromContentIds(long var1, long var3);

    public List<Long> getLatestOrderedXhtmlContentIds(long var1, int var3);

    public List<ContentEntityObject> getXhtmlSpaceDescriptionsFromContentId(long var1, int var3);

    public int getCountOfXhtmlContent();

    public int getCountOfLatestXhtmlContent();

    public int getCountOfXhtmlSpaceDescriptions();

    public void saveRawWithoutReindex(EntityObject var1);
}

