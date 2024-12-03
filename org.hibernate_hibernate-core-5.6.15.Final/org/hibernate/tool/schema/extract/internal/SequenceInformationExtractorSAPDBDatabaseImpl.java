/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;

public class SequenceInformationExtractorSAPDBDatabaseImpl
extends SequenceInformationExtractorLegacyImpl {
    public static final SequenceInformationExtractorSAPDBDatabaseImpl INSTANCE = new SequenceInformationExtractorSAPDBDatabaseImpl();

    @Override
    protected String sequenceCatalogColumn() {
        return null;
    }

    @Override
    protected String sequenceSchemaColumn() {
        return "schemaname";
    }

    @Override
    protected String sequenceStartValueColumn() {
        return null;
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

