/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface AbstractPageDao {
    public AbstractPage getAbstractPageById(long var1);

    public List<AbstractPage> getAbstractPageByIds(Iterable<Long> var1);

    public List<ContentEntityObject> getOrderedXhtmlContentFromContentId(long var1, long var3, int var5);

    public int getCountOfLatestXhtmlContent(long var1);

    public long getHighestCeoId();

    public List<ContentEntityObject> getPreviousVersionsOfPageWithTaskId(long var1, long var3, int var5);

    public List<ContentEntityObject> getStaleSharedDrafts();
}

