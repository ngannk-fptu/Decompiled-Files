/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.preprocessing;

import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import java.util.Optional;

public interface ImportedObjectPreprocessor {
    public Optional<ImportedObjectV2> apply(ImportedObjectV2 var1);
}

