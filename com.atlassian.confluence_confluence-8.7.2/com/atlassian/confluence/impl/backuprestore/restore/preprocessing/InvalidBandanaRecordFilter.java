/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.preprocessing;

import com.atlassian.confluence.impl.backuprestore.restore.preprocessing.ImportedObjectFilter;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvalidBandanaRecordFilter
implements ImportedObjectFilter {
    private static final Logger log = LoggerFactory.getLogger(InvalidBandanaRecordFilter.class);

    @Override
    public boolean test(ImportedObject importedObject, Class<?> entityClass) {
        if (!entityClass.equals(ConfluenceBandanaRecord.class)) {
            return true;
        }
        if (this.allRequireFieldsPresent(importedObject)) {
            return true;
        }
        log.debug("Imported Bandana record was skipped because either context or key field are nulls {}", (Object)importedObject);
        return false;
    }

    private boolean allRequireFieldsPresent(ImportedObject importedObject) {
        return !StringUtils.isEmpty((CharSequence)importedObject.getStringProperty("context")) && !StringUtils.isEmpty((CharSequence)importedObject.getStringProperty("key"));
    }
}

