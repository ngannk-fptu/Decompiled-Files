/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.pages.persistence;

import com.atlassian.confluence.internal.persistence.VersionedObjectDaoInternal;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.persistence.dao.BlogPostDao;

public interface BlogPostDaoInternal
extends BlogPostDao,
VersionedObjectDaoInternal<BlogPost> {
}

