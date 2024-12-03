/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.importexport.ImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class ChainedImportedObjectPreProcessor
implements ImportedObjectPreProcessor {
    private static final Logger log = LoggerFactory.getLogger(ChainedImportedObjectPreProcessor.class);
    private final List<ImportedObjectPreProcessor> objectProcessors;

    public ChainedImportedObjectPreProcessor(List<ImportedObjectPreProcessor> processorList) {
        this.objectProcessors = new ArrayList<ImportedObjectPreProcessor>(processorList);
    }

    public static ChainedImportedObjectPreProcessor emptyList() {
        return new ChainedImportedObjectPreProcessor(new ArrayList<ImportedObjectPreProcessor>());
    }

    @Override
    public boolean handles(ImportedObject object) {
        for (ImportedObjectPreProcessor processor : this.objectProcessors) {
            if (!processor.handles(object)) continue;
            return true;
        }
        return false;
    }

    @Override
    public ImportedObject process(ImportedObject object) {
        for (ImportedObjectPreProcessor filter : this.objectProcessors) {
            if (!filter.handles(object)) continue;
            log.debug("Filtering with " + filter.getClass().getName());
            if ((object = filter.process(object)) != null) continue;
            return null;
        }
        return object;
    }
}

