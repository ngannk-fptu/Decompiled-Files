/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.ImportedObjectPreProcessor
 *  com.atlassian.confluence.importexport.xmlimport.model.ImportedObject
 *  com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty
 *  com.atlassian.confluence.importexport.xmlimport.model.PrimitiveId
 *  com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty
 *  com.atlassian.confluence.importexport.xmlimport.model.ReferenceProperty
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.efi.services;

import com.atlassian.confluence.efi.services.SpaceImportConfig;
import com.atlassian.confluence.importexport.ImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveId;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty;
import com.atlassian.confluence.importexport.xmlimport.model.ReferenceProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;

public class OnboardingSpaceImportObjectPreProcessor
implements ImportedObjectPreProcessor {
    private final Map<String, String> spaceAndPageOverrides;
    private final Map<String, String> commonOverrides;

    public OnboardingSpaceImportObjectPreProcessor(SpaceImportConfig spaceImportConfig) {
        String importDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(DateTime.now().toDate());
        this.commonOverrides = ImmutableMap.of((Object)"creationDate", (Object)importDateTime, (Object)"lastModificationDate", (Object)importDateTime);
        this.spaceAndPageOverrides = new HashMap<String, String>(this.commonOverrides);
        this.spaceAndPageOverrides.put("key", spaceImportConfig.getSpaceKey());
        this.spaceAndPageOverrides.put("name", spaceImportConfig.getSpaceTitle());
        this.spaceAndPageOverrides.put("title", spaceImportConfig.getHomepageTitle());
    }

    public boolean handles(ImportedObject object) {
        return this.isSpace(object) || this.isPage(object) || this.isAttachment(object) || this.isComment(object);
    }

    public ImportedObject process(ImportedObject object) {
        ArrayList properties = Lists.newArrayList((Iterable)object.getProperties());
        ArrayList processedProperties = Lists.newArrayList();
        for (ImportedProperty property : properties) {
            processedProperties.add(this.processProperty(property, this.isSpace(object) || this.isPage(object) ? this.spaceAndPageOverrides : this.commonOverrides));
        }
        return new ImportedObject(object.getClassName(), object.getPackageName(), (Collection)processedProperties, object.getCompositeId());
    }

    private ImportedProperty processProperty(ImportedProperty property, Map<String, String> propertyOverrides) {
        String propertyName = property.getName();
        if (property instanceof ReferenceProperty && ("creator".equals(property.getName()) || "lastModifier".equals(property.getName()))) {
            return new ReferenceProperty(propertyName, ((ReferenceProperty)property).getPackageName(), ((ReferenceProperty)property).getClassName(), new PrimitiveId("key", ""));
        }
        if (property instanceof PrimitiveProperty && propertyOverrides.containsKey(propertyName)) {
            return new PrimitiveProperty(propertyName, ((PrimitiveProperty)property).getType(), propertyOverrides.get(propertyName));
        }
        return property;
    }

    private boolean isPage(ImportedObject object) {
        return "com.atlassian.confluence.pages".equals(object.getPackageName()) && "Page".equals(object.getClassName());
    }

    private boolean isSpace(ImportedObject object) {
        return "com.atlassian.confluence.spaces".equals(object.getPackageName()) && "Space".equals(object.getClassName());
    }

    private boolean isAttachment(ImportedObject object) {
        return "com.atlassian.confluence.pages".equals(object.getPackageName()) && "Attachment".equals(object.getClassName());
    }

    private boolean isComment(ImportedObject object) {
        return "com.atlassian.confluence.pages".equals(object.getPackageName()) && "Comment".equals(object.getClassName());
    }
}

