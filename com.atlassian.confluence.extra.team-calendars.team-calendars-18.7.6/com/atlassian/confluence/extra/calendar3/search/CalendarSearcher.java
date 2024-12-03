/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.search.contentnames.QueryToken
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.search;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.search.contentnames.QueryToken;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public interface CalendarSearcher {
    public Set<String> findSubCalendars(String var1, int var2, int var3) throws InvalidSearchException;

    public Set<String> findSubCalendars(ConfluenceUser var1, String var2, int var3, int var4) throws InvalidSearchException;

    public <T> Collection<T> findSubCalendars(ConfluenceUser var1, List<QueryToken> var2, int var3, int var4, Function<Searchable, T> var5) throws InvalidSearchException;

    public <T> Collection<T> findSubCalendars(List<QueryToken> var1, int var2, Function<Searchable, T> var3) throws InvalidSearchException;
}

