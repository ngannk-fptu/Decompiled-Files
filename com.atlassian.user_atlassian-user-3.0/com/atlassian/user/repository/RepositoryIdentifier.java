/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.repository;

import java.io.Serializable;

public interface RepositoryIdentifier
extends Serializable {
    public String getKey();

    public String getName();
}

