/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.migration.ContentDao;
import com.atlassian.confluence.content.render.xhtml.migration.OrderedEntityObjectBatchableWorkSource;
import com.atlassian.confluence.core.ContentEntityObject;
import java.util.List;

public class LatestVersionXhtmlContentWorkSource
extends OrderedEntityObjectBatchableWorkSource<ContentEntityObject> {
    public LatestVersionXhtmlContentWorkSource(final ContentDao contentDao, int batchSize) {
        super(batchSize, new OrderedEntityObjectBatchableWorkSource.EntitySource<ContentEntityObject>(){

            @Override
            public List<Long> getLatestEntityIds(long startContentId, int maxRows) {
                return contentDao.getLatestOrderedXhtmlContentIds(startContentId, maxRows);
            }

            @Override
            public List<ContentEntityObject> getEntityObjects(long startContentId, long endContentId) {
                return contentDao.getLatestOrderedXhtmlContentFromContentIds(startContentId, endContentId);
            }

            @Override
            public synchronized int getTotalSize() {
                return contentDao.getCountOfLatestXhtmlContent();
            }
        });
    }
}

