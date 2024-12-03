/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.hibernate.boot.model.relational.QualifiedSequenceName;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorNoOpImpl;
import org.hibernate.tool.schema.extract.internal.SequenceInformationImpl;
import org.hibernate.tool.schema.extract.spi.ExtractionContext;
import org.hibernate.tool.schema.extract.spi.SequenceInformation;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;

public class SequenceInformationExtractorLegacyImpl
implements SequenceInformationExtractor {
    public static final SequenceInformationExtractorLegacyImpl INSTANCE = new SequenceInformationExtractorLegacyImpl();

    @Override
    public Iterable<SequenceInformation> extractMetadata(ExtractionContext extractionContext) throws SQLException {
        String lookupSql = extractionContext.getJdbcEnvironment().getDialect().getQuerySequencesString();
        if (lookupSql == null) {
            return SequenceInformationExtractorNoOpImpl.INSTANCE.extractMetadata(extractionContext);
        }
        return extractionContext.getQueryResults(lookupSql, null, resultSet -> {
            IdentifierHelper identifierHelper = extractionContext.getJdbcEnvironment().getIdentifierHelper();
            ArrayList<SequenceInformationImpl> sequenceInformationList = new ArrayList<SequenceInformationImpl>();
            while (resultSet.next()) {
                sequenceInformationList.add(new SequenceInformationImpl(new QualifiedSequenceName(identifierHelper.toIdentifier(this.resultSetCatalogName(resultSet)), identifierHelper.toIdentifier(this.resultSetSchemaName(resultSet)), identifierHelper.toIdentifier(this.resultSetSequenceName(resultSet))), this.resultSetStartValueSize(resultSet), this.resultSetMinValue(resultSet), this.resultSetMaxValue(resultSet), this.resultSetIncrementValue(resultSet)));
            }
            return sequenceInformationList;
        });
    }

    protected String sequenceNameColumn() {
        return "sequence_name";
    }

    protected String sequenceCatalogColumn() {
        return "sequence_catalog";
    }

    protected String sequenceSchemaColumn() {
        return "sequence_schema";
    }

    protected String sequenceStartValueColumn() {
        return "start_value";
    }

    protected String sequenceMinValueColumn() {
        return "minimum_value";
    }

    protected String sequenceMaxValueColumn() {
        return "maximum_value";
    }

    protected String sequenceIncrementColumn() {
        return "increment";
    }

    protected String resultSetSequenceName(ResultSet resultSet) throws SQLException {
        return resultSet.getString(this.sequenceNameColumn());
    }

    protected String resultSetCatalogName(ResultSet resultSet) throws SQLException {
        String column = this.sequenceCatalogColumn();
        return column != null ? resultSet.getString(column) : null;
    }

    protected String resultSetSchemaName(ResultSet resultSet) throws SQLException {
        String column = this.sequenceSchemaColumn();
        return column != null ? resultSet.getString(column) : null;
    }

    protected Long resultSetStartValueSize(ResultSet resultSet) throws SQLException {
        String column = this.sequenceStartValueColumn();
        return column != null ? Long.valueOf(resultSet.getLong(column)) : null;
    }

    protected Long resultSetMinValue(ResultSet resultSet) throws SQLException {
        String column = this.sequenceMinValueColumn();
        return column != null ? Long.valueOf(resultSet.getLong(column)) : null;
    }

    protected Long resultSetMaxValue(ResultSet resultSet) throws SQLException {
        String column = this.sequenceMaxValueColumn();
        return column != null ? Long.valueOf(resultSet.getLong(column)) : null;
    }

    protected Long resultSetIncrementValue(ResultSet resultSet) throws SQLException {
        String column = this.sequenceIncrementColumn();
        return column != null ? Long.valueOf(resultSet.getLong(column)) : null;
    }
}

