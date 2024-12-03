/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.model;

import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveId;

@Deprecated
public class ReferenceProperty
extends ImportedProperty {
    private final String className;
    private final String packageName;
    private final PrimitiveId id;

    public ReferenceProperty(String name, String packageName, String className, PrimitiveId id) {
        super(name);
        this.className = className;
        this.packageName = packageName;
        this.id = id;
    }

    public String getClassName() {
        return this.className;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public PrimitiveId getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return super.toString() + this.packageName + "." + this.className + "[" + this.id + "]";
    }
}

