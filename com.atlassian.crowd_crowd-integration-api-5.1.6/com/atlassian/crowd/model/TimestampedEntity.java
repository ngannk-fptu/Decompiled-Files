/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model;

import java.util.Date;

public interface TimestampedEntity {
    public Date getCreatedDate();

    public Date getUpdatedDate();
}

