/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.delegation.repository;

import com.atlassian.user.repository.RepositoryIdentifier;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DelegatingRepository
implements RepositoryIdentifier {
    public static final String DELEGATING = "Delegating Repository: ";
    private final List<RepositoryIdentifier> repositories = new ArrayList<RepositoryIdentifier>();

    public DelegatingRepository(List<RepositoryIdentifier> repositories) {
        repositories.addAll(repositories);
    }

    @Override
    public String getKey() {
        StringBuffer key = new StringBuffer(DELEGATING.length() + 30 * this.repositories.size());
        key.append(DELEGATING);
        for (RepositoryIdentifier repository : this.repositories) {
            key.append(repository.getKey()).append(" ");
        }
        return key.toString();
    }

    @Override
    public String getName() {
        StringBuffer name = new StringBuffer(DELEGATING.length() + 50 * this.repositories.size());
        name.append(DELEGATING);
        for (RepositoryIdentifier repository : this.repositories) {
            name.append(repository.getName()).append(" ");
        }
        return name.toString();
    }

    public String getDescription() {
        return DELEGATING;
    }

    public List<RepositoryIdentifier> getRepositories() {
        return this.repositories;
    }
}

