/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;

public class SequenceInformationExtractorInformixDatabaseImpl
extends SequenceInformationExtractorLegacyImpl {
    public static final SequenceInformationExtractorInformixDatabaseImpl INSTANCE = new SequenceInformationExtractorInformixDatabaseImpl();

    @Override
    protected String sequenceCatalogColumn() {
        return null;
    }

    @Override
    protected String sequenceSchemaColumn() {
        return null;
    }

    @Override
    protected String sequenceStartValueColumn() {
        return "start_val";
    }

    @Override
    protected String sequenceMinValueColumn() {
        return "min_val";
    }

    @Override
    protected String sequenceMaxValueColumn() {
        return "max_val";
    }

    @Override
    protected String sequenceIncrementColumn() {
        return "inc_val";
    }
}

