/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;

public class SequenceNameExtractorImpl
extends SequenceInformationExtractorLegacyImpl {
    public static final SequenceNameExtractorImpl INSTANCE = new SequenceNameExtractorImpl();

    @Override
    protected String resultSetSequenceName(ResultSet resultSet) throws SQLException {
        return resultSet.getString(1);
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

