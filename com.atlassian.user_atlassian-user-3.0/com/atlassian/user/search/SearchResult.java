/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search;

import com.atlassian.user.search.page.Pager;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface SearchResult<T> {
    public Pager<T> pager();

    public Pager<T> pager(String var1);

    public Set<String> repositoryKeyset();
}

