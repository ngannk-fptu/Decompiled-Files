/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport;

import com.atlassian.confluence.importexport.xmlimport.ObjectPersister;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;

@Deprecated
public interface ObjectPersisterFactory {
    public ObjectPersister createPersisterFor(ImportedObject var1);
}

