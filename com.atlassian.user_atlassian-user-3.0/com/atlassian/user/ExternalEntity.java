/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user;

import com.atlassian.user.Entity;

public interface ExternalEntity
extends Entity {
    public long getId();

    public String getType();
}

