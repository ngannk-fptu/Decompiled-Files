/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.crossstore;

import org.springframework.data.crossstore.ChangeSet;

public interface ChangeSetBacked {
    public ChangeSet getChangeSet();
}

