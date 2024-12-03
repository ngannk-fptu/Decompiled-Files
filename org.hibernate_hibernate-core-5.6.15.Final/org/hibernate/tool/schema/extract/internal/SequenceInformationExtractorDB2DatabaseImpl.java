/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;

public class SequenceInformationExtractorDB2DatabaseImpl
extends SequenceInformationExtractorLegacyImpl {
    public static final SequenceInformationExtractorDB2DatabaseImpl INSTANCE = new SequenceInformationExtractorDB2DatabaseImpl();

    @Override
    protected String sequenceNameColumn() {
        return "seqname";
    }

    @Override
    protected String sequenceCatalogColumn() {
        return null;
    }

    @Override
    protected String sequenceSchemaColumn() {
        return "seqschema";
    }

    @Override
    protected String sequenceStartValueColumn() {
        return "start";
    }

    @Override
    protected String sequenceMinValueColumn() {
        return "minvalue";
    }

    @Override
    protected String sequenceMaxValueColumn() {
        return "maxvalue";
    }
}

