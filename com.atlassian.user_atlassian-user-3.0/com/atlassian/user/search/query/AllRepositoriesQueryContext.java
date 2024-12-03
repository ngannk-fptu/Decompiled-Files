/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.query;

import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.query.QueryContext;
import java.util.Collections;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AllRepositoriesQueryContext
implements QueryContext {
    @Override
    public void addRepositoryKey(String key) {
    }

    @Override
    public List<String> getRepositoryKeys() {
        return Collections.singletonList("_all_repositories_");
    }

    @Override
    public boolean contains(RepositoryIdentifier repositoryIdentifier) {
        return true;
    }
}

