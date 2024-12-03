/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;

public class SequenceInformationExtractorHSQLDBDatabaseImpl
extends SequenceInformationExtractorLegacyImpl {
    public static final SequenceInformationExtractorHSQLDBDatabaseImpl INSTANCE = new SequenceInformationExtractorHSQLDBDatabaseImpl();

    @Override
    protected String sequenceStartValueColumn() {
        return "start_with";
    }
}

