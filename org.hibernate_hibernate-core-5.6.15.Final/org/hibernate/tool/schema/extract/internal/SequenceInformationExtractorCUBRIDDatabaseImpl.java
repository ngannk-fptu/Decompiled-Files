/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;

public class SequenceInformationExtractorCUBRIDDatabaseImpl
extends SequenceInformationExtractorLegacyImpl {
    public static final SequenceInformationExtractorCUBRIDDatabaseImpl INSTANCE = new SequenceInformationExtractorCUBRIDDatabaseImpl();

    @Override
    protected String sequenceNameColumn() {
        return "name";
    }

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
        return "started";
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
        return "increment_val";
    }
}

