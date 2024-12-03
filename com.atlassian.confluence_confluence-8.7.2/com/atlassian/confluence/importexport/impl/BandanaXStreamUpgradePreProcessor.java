/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.importexport.ImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty;
import com.atlassian.confluence.setup.xstream.ConfluenceXStream;
import com.atlassian.confluence.setup.xstream.ConfluenceXStreamManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class BandanaXStreamUpgradePreProcessor
implements ImportedObjectPreProcessor {
    private static final Logger log = LoggerFactory.getLogger(BandanaXStreamUpgradePreProcessor.class);
    private static final String BANDANA_VALUE_FIELD = "value";
    private final ConfluenceXStreamManager xStreamManager;

    public BandanaXStreamUpgradePreProcessor(ConfluenceXStreamManager xStreamManager) {
        this.xStreamManager = xStreamManager;
    }

    @Override
    public boolean handles(ImportedObject object) {
        String className = object.getClassName();
        return className.equals("ConfluenceBandanaRecord");
    }

    @Override
    public ImportedObject process(ImportedObject object) {
        String migratedBandanaValue;
        String bandanaValue = object.getStringProperty(BANDANA_VALUE_FIELD);
        if (StringUtils.isEmpty((CharSequence)bandanaValue)) {
            return object;
        }
        ConfluenceXStream xStream = this.xStreamManager.getConfluenceXStream();
        try {
            migratedBandanaValue = xStream.toXML(xStream.fromXML(bandanaValue));
        }
        catch (Exception e) {
            log.debug("Couldn't migrate key '{}' in Bandana as corresponding Class is not available", (Object)object.getStringProperty("key"), (Object)e);
            return object;
        }
        Collection<ImportedProperty> updatedProperties = this.updateValueProperty(object.getProperties(), migratedBandanaValue);
        return new ImportedObject(object.getClassName(), object.getPackageName(), updatedProperties, object.getCompositeId());
    }

    private @NonNull Collection<ImportedProperty> updateValueProperty(Collection<ImportedProperty> properties, String migratedBandanaValue) {
        Collection updatedProperties = properties.stream().filter(p -> !BANDANA_VALUE_FIELD.equals(p.getName())).collect(Collectors.toCollection(ArrayList::new));
        updatedProperties.add(new PrimitiveProperty(BANDANA_VALUE_FIELD, null, migratedBandanaValue));
        return updatedProperties;
    }
}

