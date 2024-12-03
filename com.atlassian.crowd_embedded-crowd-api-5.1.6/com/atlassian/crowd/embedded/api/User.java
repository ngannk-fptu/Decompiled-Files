/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.api;

import java.security.Principal;

public interface User
extends Comparable<User>,
Principal {
    public long getDirectoryId();

    public boolean isActive();

    public String getEmailAddress();

    public String getDisplayName();

    @Override
    public boolean equals(Object var1);

    @Override
    public int hashCode();

    @Override
    public int compareTo(User var1);

    default public boolean isMarkedAsDeleted() {
        return false;
    }
}

