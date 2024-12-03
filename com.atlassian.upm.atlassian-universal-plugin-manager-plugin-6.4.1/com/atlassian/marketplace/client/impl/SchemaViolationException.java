/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.encoding.SchemaViolation;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

public class SchemaViolationException
extends RuntimeException {
    private final ImmutableList<SchemaViolation> schemaViolations;

    public SchemaViolationException(Iterable<SchemaViolation> schemaViolations) {
        this.schemaViolations = ImmutableList.copyOf(schemaViolations);
    }

    public SchemaViolationException(SchemaViolation schemaViolation) {
        this((Iterable<SchemaViolation>)ImmutableList.of((Object)schemaViolation));
    }

    public Iterable<SchemaViolation> getSchemaViolations() {
        return this.schemaViolations;
    }

    @Override
    public String getMessage() {
        return Joiner.on((String)", ").join(this.schemaViolations);
    }
}

