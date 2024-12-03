/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.query;

import com.atlassian.user.repository.RepositoryIdentifier;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface QueryContext {
    public static final String ALL_REPOSITORIES = "_all_repositories_";

    public void addRepositoryKey(String var1);

    public List<String> getRepositoryKeys();

    public boolean contains(RepositoryIdentifier var1);
}

