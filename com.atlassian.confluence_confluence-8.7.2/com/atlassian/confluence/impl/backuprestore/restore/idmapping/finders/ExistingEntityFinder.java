/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobSource
 */
package com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders;

import com.atlassian.confluence.api.model.backuprestore.JobSource;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import java.util.Collection;
import java.util.Map;

public interface ExistingEntityFinder {
    public Map<ImportedObjectV2, Object> findExistingObjectIds(Collection<ImportedObjectV2> var1);

    public Class<?> getSupportedClass();

    public boolean isSupportedJobSource(JobSource var1);
}

