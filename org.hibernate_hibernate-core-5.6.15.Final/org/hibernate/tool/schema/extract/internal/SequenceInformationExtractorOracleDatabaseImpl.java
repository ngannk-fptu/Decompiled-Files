/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;

public class SequenceInformationExtractorOracleDatabaseImpl
extends SequenceInformationExtractorLegacyImpl {
    public static final SequenceInformationExtractorOracleDatabaseImpl INSTANCE = new SequenceInformationExtractorOracleDatabaseImpl();
    private static final BigDecimal MIN_VALUE = BigDecimal.valueOf(Long.MIN_VALUE);
    private static final BigDecimal MAX_VALUE = BigDecimal.valueOf(Long.MAX_VALUE);

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
        return "min_value";
    }

    @Override
    protected String sequenceMaxValueColumn() {
        return "max_value";
    }

    @Override
    protected Long resultSetMinValue(ResultSet resultSet) throws SQLException {
        BigDecimal asDecimal = resultSet.getBigDecimal(this.sequenceMinValueColumn());
        if (asDecimal.compareTo(MIN_VALUE) == -1) {
            return Long.MIN_VALUE;
        }
        return asDecimal.longValue();
    }

    @Override
    protected Long resultSetMaxValue(ResultSet resultSet) throws SQLException {
        BigDecimal asDecimal = resultSet.getBigDecimal(this.sequenceMaxValueColumn());
        if (asDecimal.compareTo(MAX_VALUE) == 1) {
            return Long.MAX_VALUE;
        }
        return asDecimal.longValue();
    }

    @Override
    protected String sequenceIncrementColumn() {
        return "increment_by";
    }
}

