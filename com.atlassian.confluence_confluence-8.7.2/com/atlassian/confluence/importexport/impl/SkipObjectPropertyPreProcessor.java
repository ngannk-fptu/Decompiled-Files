/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.importexport.ImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SkipObjectPropertyPreProcessor
implements ImportedObjectPreProcessor {
    private final Map<String, Set<String>> skippedFullClassNameToPropertyNames = new HashMap<String, Set<String>>();

    public SkipObjectPropertyPreProcessor(Set<String> skippedFullClassNameAndPropertyNamePairs) {
        for (String fullClassNameAndPropertyPair : skippedFullClassNameAndPropertyNamePairs) {
            String[] pair = fullClassNameAndPropertyPair.split("#");
            if (pair.length != 2) {
                throw new IllegalArgumentException("skippedFullClassNameAndPropertyNamePairs should be a set of fullClassName#propertyName pairs");
            }
            String fullClassName = pair[0];
            String propertyName = pair[1];
            Set<String> propertyNames = this.getOrInitPropertyNames(fullClassName);
            propertyNames.add(propertyName);
        }
    }

    @Override
    public boolean handles(ImportedObject object) {
        String fullClassName = object.getPackageName() + "." + object.getClassName();
        if (!this.skippedFullClassNameToPropertyNames.containsKey(fullClassName)) {
            return false;
        }
        Set<String> skippedPropertyNames = this.skippedFullClassNameToPropertyNames.get(fullClassName);
        Collection<ImportedProperty> ops = object.getProperties();
        for (ImportedProperty op : ops) {
            if (!skippedPropertyNames.contains(op.getName())) continue;
            return true;
        }
        return false;
    }

    @Override
    public ImportedObject process(ImportedObject object) {
        String fullClassName = object.getPackageName() + "." + object.getClassName();
        Set<String> skippedPropertyNames = this.skippedFullClassNameToPropertyNames.get(fullClassName);
        Collection<ImportedProperty> ops = object.getProperties();
        HashSet<ImportedProperty> nonskippedProperties = new HashSet<ImportedProperty>();
        for (ImportedProperty p : ops) {
            if (skippedPropertyNames.contains(p.getName())) continue;
            nonskippedProperties.add(p);
        }
        return nonskippedProperties.isEmpty() ? object : new ImportedObject(object.getClassName(), object.getPackageName(), nonskippedProperties, object.getCompositeId());
    }

    private Set<String> getOrInitPropertyNames(String fullClassName) {
        Set<String> propertyNames = this.skippedFullClassNameToPropertyNames.get(fullClassName);
        if (propertyNames == null) {
            propertyNames = new HashSet<String>();
            this.skippedFullClassNameToPropertyNames.put(fullClassName, propertyNames);
        }
        return propertyNames;
    }
}

