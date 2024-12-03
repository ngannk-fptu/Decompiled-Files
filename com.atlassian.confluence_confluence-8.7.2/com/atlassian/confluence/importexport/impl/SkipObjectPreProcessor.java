/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.importexport.ImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import java.util.Set;

public class SkipObjectPreProcessor
implements ImportedObjectPreProcessor {
    private final Set<String> skippedFullClassNames;

    public SkipObjectPreProcessor(Set<String> skippedFullClassNames) {
        this.skippedFullClassNames = skippedFullClassNames;
    }

    @Override
    public boolean handles(ImportedObject object) {
        String fullClassName = object.getPackageName() + "." + object.getClassName();
        return this.skippedFullClassNames.contains(fullClassName);
    }

    @Override
    public ImportedObject process(ImportedObject object) {
        return null;
    }
}

