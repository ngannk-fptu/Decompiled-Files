/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.idmapping;

import java.util.List;

public interface PersistedObjectsRegister {
    public boolean isPersistedDatabaseId(Class<?> var1, Object var2);

    public void markIdsAsPersisted(Class<?> var1, List<Object> var2);
}

