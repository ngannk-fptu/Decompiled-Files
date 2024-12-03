/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;

public class SequenceInformationExtractorMimerSQLDatabaseImpl
extends SequenceInformationExtractorLegacyImpl {
    public static final SequenceInformationExtractorMimerSQLDatabaseImpl INSTANCE = new SequenceInformationExtractorMimerSQLDatabaseImpl();

    @Override
    protected String sequenceStartValueColumn() {
        return "initial_value";
    }

    @Override
    protected String sequenceMinValueColumn() {
        return null;
    }
}

