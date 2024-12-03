/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.stash;

import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStash;

public interface ImportedObjectsStashFactory {
    public ImportedObjectsStash createStash(String var1);

    public ImportedObjectsStash createStash(String var1, int var2);
}

