/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.user.User
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.dailysummary.content.popular;

import com.atlassian.confluence.plugins.dailysummary.content.popular.PopularContentExcerptDto;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;

public interface PopularContentManager {
    public List<PopularContentExcerptDto> getPopularContent(ConfluenceUser var1, Date var2, @Nullable Space var3, int var4, int var5);

    @Deprecated
    public List<PopularContentExcerptDto> getPopularContent(User var1, Date var2, @Nullable Space var3, int var4, int var5);
}

