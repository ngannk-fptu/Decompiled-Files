/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.query;

import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.query.QueryContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class DefaultQueryContext
implements QueryContext {
    private final List<String> repositoryKeys;

    public DefaultQueryContext() {
        this.repositoryKeys = new ArrayList<String>();
    }

    public DefaultQueryContext(RepositoryIdentifier ... repositories) {
        ArrayList<String> keys = new ArrayList<String>(repositories.length);
        for (RepositoryIdentifier repository : repositories) {
            keys.add(repository.getKey());
        }
        this.repositoryKeys = Collections.unmodifiableList(keys);
    }

    public DefaultQueryContext(String ... repositoryKeys) {
        this.repositoryKeys = Collections.unmodifiableList(Arrays.asList(repositoryKeys));
    }

    @Override
    public void addRepositoryKey(String key) throws IllegalArgumentException {
        if (this.repositoryKeys.contains(key) || this.repositoryKeys.contains("_all_repositories_")) {
            throw new IllegalArgumentException("Repository key [" + key + " is already listed in this query context.");
        }
        this.repositoryKeys.add(key);
    }

    @Override
    public List<String> getRepositoryKeys() {
        return Collections.unmodifiableList(this.repositoryKeys);
    }

    @Override
    public boolean contains(RepositoryIdentifier repositoryIdentifier) {
        if (this.getRepositoryKeys().contains("_all_repositories_")) {
            return true;
        }
        return this.getRepositoryKeys().contains(repositoryIdentifier.getKey());
    }
}

