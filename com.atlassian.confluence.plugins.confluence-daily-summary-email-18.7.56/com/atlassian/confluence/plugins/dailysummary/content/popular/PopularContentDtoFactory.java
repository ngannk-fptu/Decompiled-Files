/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.dailysummary.content.popular;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.dailysummary.content.popular.PopularContentExcerptDto;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Date;

public interface PopularContentDtoFactory {
    public PopularContentExcerptDto createExcerpt(ContentEntityObject var1, ConfluenceUser var2, Date var3);
}

