/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;

public class SequenceInformationExtractorIngresDatabaseImpl
extends SequenceInformationExtractorLegacyImpl {
    public static final SequenceInformationExtractorIngresDatabaseImpl INSTANCE = new SequenceInformationExtractorIngresDatabaseImpl();

    @Override
    protected String sequenceNameColumn() {
        return "seq_name";
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
        return null;
    }

    @Override
    protected String sequenceMinValueColumn() {
        return null;
    }

    @Override
    protected String sequenceMaxValueColumn() {
        return null;
    }

    @Override
    protected String sequenceIncrementColumn() {
        return null;
    }
}

