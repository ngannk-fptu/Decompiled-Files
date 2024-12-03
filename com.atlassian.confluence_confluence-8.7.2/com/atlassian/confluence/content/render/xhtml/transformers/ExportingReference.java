/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

public class ExportingReference {
    private final String propertyName;
    private final Class<?> referencedClazz;
    private final Object referencedId;

    public ExportingReference(String propertyName, Class<?> referencedClazz, Object referencedId) {
        this.propertyName = propertyName;
        this.referencedClazz = referencedClazz;
        this.referencedId = referencedId;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public Class<?> getReferencedClazz() {
        return this.referencedClazz;
    }

    public Object getReferencedId() {
        return this.referencedId;
    }
}

