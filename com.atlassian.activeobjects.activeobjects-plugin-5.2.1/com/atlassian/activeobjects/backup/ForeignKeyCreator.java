/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.backup;

import com.atlassian.dbexporter.EntityNameProcessor;
import com.atlassian.dbexporter.ForeignKey;

public interface ForeignKeyCreator {
    public void create(Iterable<ForeignKey> var1, EntityNameProcessor var2);
}

