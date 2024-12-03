/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.thoughtworks.xstream.XStreamException
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.preprocessing;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.preprocessing.ImportedObjectPreprocessor;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaRecord;
import com.atlassian.confluence.setup.xstream.ConfluenceXStream;
import com.atlassian.confluence.setup.xstream.ConfluenceXStreamManager;
import com.thoughtworks.xstream.XStreamException;
import java.util.Collections;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BandanaXStreamUpgradePreprocessor
implements ImportedObjectPreprocessor {
    @VisibleForTesting
    protected static final String BANDANA_VALUE_FIELD = "value";
    private static final Logger log = LoggerFactory.getLogger(BandanaXStreamUpgradePreprocessor.class);
    private final ConfluenceXStreamManager xStreamManager;

    public BandanaXStreamUpgradePreprocessor(ConfluenceXStreamManager xStreamManager) {
        this.xStreamManager = xStreamManager;
    }

    @Override
    public Optional<ImportedObjectV2> apply(ImportedObjectV2 importedObject) {
        String migratedBandanaValue;
        if (!ConfluenceBandanaRecord.class.equals(importedObject.getEntityClass())) {
            return Optional.of(importedObject);
        }
        String bandanaValue = (String)importedObject.getFieldValue(BANDANA_VALUE_FIELD);
        if (StringUtils.isEmpty((CharSequence)bandanaValue)) {
            return Optional.of(importedObject);
        }
        ConfluenceXStream xStream = this.xStreamManager.getConfluenceXStream();
        try {
            migratedBandanaValue = xStream.toXML(xStream.fromXML(bandanaValue));
        }
        catch (XStreamException e) {
            log.debug("Couldn't migrate key '{}' in Bandana as corresponding Class is not available", importedObject.getFieldValue("key"), (Object)e);
            return Optional.of(importedObject);
        }
        ImportedObjectV2 migratedObject = importedObject.overridePropertyValues(importedObject.getId(), Collections.singletonMap(BANDANA_VALUE_FIELD, migratedBandanaValue));
        return Optional.of(migratedObject);
    }
}

