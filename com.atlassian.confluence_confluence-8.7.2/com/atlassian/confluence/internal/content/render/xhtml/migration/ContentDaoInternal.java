/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.migration.ContentDao;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.persistence.ContentEntityObjectDaoInternal;

public interface ContentDaoInternal
extends ContentDao,
ContentEntityObjectDaoInternal<ContentEntityObject> {
}

