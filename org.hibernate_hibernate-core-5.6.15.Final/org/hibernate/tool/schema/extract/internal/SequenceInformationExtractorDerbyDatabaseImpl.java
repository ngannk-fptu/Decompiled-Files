/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;

public class SequenceInformationExtractorDerbyDatabaseImpl
extends SequenceInformationExtractorLegacyImpl {
    public static final SequenceInformationExtractorDerbyDatabaseImpl INSTANCE = new SequenceInformationExtractorDerbyDatabaseImpl();

    @Override
    protected String sequenceNameColumn() {
        return "sequencename";
    }

    @Override
    protected String sequenceCatalogColumn() {
        return null;
    }

    @Override
    protected String sequenceStartValueColumn() {
        return "startvalue";
    }

    @Override
    protected String sequenceMinValueColumn() {
        return "minimumvalue";
    }

    @Override
    protected String sequenceMaxValueColumn() {
        return "maximumvalue";
    }
}

