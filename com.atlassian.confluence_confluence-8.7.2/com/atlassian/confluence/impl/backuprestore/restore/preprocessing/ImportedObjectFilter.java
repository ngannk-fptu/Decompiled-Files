/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.preprocessing;

import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;

public interface ImportedObjectFilter {
    public boolean test(ImportedObject var1, Class<?> var2);
}

