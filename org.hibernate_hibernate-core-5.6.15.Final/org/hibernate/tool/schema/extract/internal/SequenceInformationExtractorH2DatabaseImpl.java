/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;

public class SequenceInformationExtractorH2DatabaseImpl
extends SequenceInformationExtractorLegacyImpl {
    public static final SequenceInformationExtractorH2DatabaseImpl INSTANCE = new SequenceInformationExtractorH2DatabaseImpl();

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
}

