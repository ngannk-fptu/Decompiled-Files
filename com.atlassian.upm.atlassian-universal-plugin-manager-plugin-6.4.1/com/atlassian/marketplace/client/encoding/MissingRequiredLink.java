/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.marketplace.client.encoding;

import com.atlassian.marketplace.client.encoding.SchemaViolation;
import com.google.common.base.Preconditions;

public class MissingRequiredLink
extends SchemaViolation {
    private final String rel;

    public MissingRequiredLink(Class<?> schemaClass, String rel) {
        super(schemaClass);
        this.rel = (String)Preconditions.checkNotNull((Object)rel);
    }

    public String getRel() {
        return this.rel;
    }

    @Override
    public String getMessage() {
        return "missing required link \"" + this.rel + "\" in " + this.getSchemaClass().getSimpleName();
    }
}

