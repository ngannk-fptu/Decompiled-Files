/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.upgrade.constraint.dedup;

import com.atlassian.confluence.internal.upgrade.constraint.dedup.KeepOneDedupeStrategy;
import java.util.SortedSet;

public class KeepSmallestIdDedupeStrategy
extends KeepOneDedupeStrategy {
    public KeepSmallestIdDedupeStrategy(String table, String idColumn) {
        super(table, idColumn);
    }

    @Override
    protected Object getIdToKeep(SortedSet<Object> ids) {
        return ids.first();
    }
}

