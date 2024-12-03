/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;

public class SequenceInformationExtractorHANADatabaseImpl
extends SequenceInformationExtractorLegacyImpl {
    public static final SequenceInformationExtractorHANADatabaseImpl INSTANCE = new SequenceInformationExtractorHANADatabaseImpl();

    @Override
    protected String sequenceCatalogColumn() {
        return null;
    }

    @Override
    protected String sequenceSchemaColumn() {
        return "schema_name";
    }

    @Override
    protected String sequenceStartValueColumn() {
        return "start_number";
    }

    @Override
    protected String sequenceMinValueColumn() {
        return "min_value";
    }

    @Override
    protected String sequenceMaxValueColumn() {
        return "max_value";
    }

    @Override
    protected String sequenceIncrementColumn() {
        return "increment_by";
    }
}

