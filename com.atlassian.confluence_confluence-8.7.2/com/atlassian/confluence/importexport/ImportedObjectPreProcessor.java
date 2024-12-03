/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;

@Deprecated
public interface ImportedObjectPreProcessor {
    public boolean handles(ImportedObject var1);

    public ImportedObject process(ImportedObject var1);
}

