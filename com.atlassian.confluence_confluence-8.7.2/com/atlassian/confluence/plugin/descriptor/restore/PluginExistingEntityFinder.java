/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.restore;

import com.atlassian.confluence.plugin.descriptor.restore.ImportedObjectModel;
import java.util.Collection;
import java.util.Map;

public interface PluginExistingEntityFinder {
    public Map<ImportedObjectModel, Object> findExistingObjectIds(Collection<ImportedObjectModel> var1);

    public Class<?> getSupportedClass();
}

