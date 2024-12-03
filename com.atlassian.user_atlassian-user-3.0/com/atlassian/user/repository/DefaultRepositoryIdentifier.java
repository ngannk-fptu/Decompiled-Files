/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.repository;

import com.atlassian.user.repository.RepositoryIdentifier;

public class DefaultRepositoryIdentifier
implements RepositoryIdentifier {
    private final String key;
    private final String name;

    public DefaultRepositoryIdentifier(String key, String name) {
        if (key == null) {
            throw new IllegalArgumentException("Repository key cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Repository name cannot be null");
        }
        this.key = key;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getKey() {
        return this.key;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RepositoryIdentifier)) {
            return false;
        }
        RepositoryIdentifier repo = (RepositoryIdentifier)o;
        return this.key.equals(repo.getKey());
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    public String toString() {
        return this.name;
    }
}

