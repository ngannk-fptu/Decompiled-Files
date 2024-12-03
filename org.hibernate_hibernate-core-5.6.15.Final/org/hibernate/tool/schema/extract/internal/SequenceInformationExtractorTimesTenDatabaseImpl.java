/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;

public class SequenceInformationExtractorTimesTenDatabaseImpl
extends SequenceInformationExtractorLegacyImpl {
    public static final SequenceInformationExtractorTimesTenDatabaseImpl INSTANCE = new SequenceInformationExtractorTimesTenDatabaseImpl();

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
        return null;
    }

    @Override
    protected String sequenceMinValueColumn() {
        return "minval";
    }

    @Override
    protected String sequenceMaxValueColumn() {
        return "maxval";
    }
}

