/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.configuration;

import com.atlassian.user.configuration.RepositoryAccessor;
import java.util.List;

public interface DelegationAccessor
extends RepositoryAccessor {
    public RepositoryAccessor getRepositoryAccessor(String var1);

    public List getRepositoryAccessors();

    public void addRepositoryAccessor(RepositoryAccessor var1);
}

