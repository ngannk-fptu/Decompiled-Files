/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.model;

import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import java.util.Collection;

@Deprecated
public class CollectionProperty
extends ImportedProperty {
    private final Collection<ImportedProperty> values;

    public CollectionProperty(String name, Collection<ImportedProperty> values) {
        super(name);
        this.values = values;
    }

    public Collection<ImportedProperty> getValues() {
        return this.values;
    }
}

