/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.importexport.ImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.model.ReferenceProperty;
import java.util.Set;

public class SkipObjectWithReferencePreProcessor
implements ImportedObjectPreProcessor {
    private final Set<String> candidateTypes;
    private final Set<String> skippedReferenceTypes;

    public SkipObjectWithReferencePreProcessor(Set<String> candidateTypes, Set<String> skippedReferenceTypes) {
        this.candidateTypes = candidateTypes;
        this.skippedReferenceTypes = skippedReferenceTypes;
    }

    @Override
    public boolean handles(ImportedObject object) {
        String fullClassName = object.getPackageName() + "." + object.getClassName();
        if (!this.candidateTypes.contains(fullClassName)) {
            return false;
        }
        return object.getProperties().stream().filter(property -> property instanceof ReferenceProperty).map(property -> (ReferenceProperty)property).anyMatch(property -> {
            String type = property.getPackageName() + "." + property.getClassName();
            return this.skippedReferenceTypes.contains(type);
        });
    }

    @Override
    public ImportedObject process(ImportedObject object) {
        return null;
    }
}

