/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.QualifiedSequenceName;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorNoOpImpl;
import org.hibernate.tool.schema.extract.internal.SequenceInformationImpl;
import org.hibernate.tool.schema.extract.spi.ExtractionContext;
import org.hibernate.tool.schema.extract.spi.SequenceInformation;

public class SequenceInformationExtractorMariaDBDatabaseImpl
extends SequenceInformationExtractorLegacyImpl {
    public static final SequenceInformationExtractorMariaDBDatabaseImpl INSTANCE = new SequenceInformationExtractorMariaDBDatabaseImpl();
    private static final String SQL_SEQUENCE_QUERY = "SELECT '%1$s' as sequence_name, minimum_value, maximum_value, start_value, increment, cache_size FROM %2$s ";
    private static final String UNION_ALL = "UNION ALL ";

    @Override
    public Iterable<SequenceInformation> extractMetadata(ExtractionContext extractionContext) throws SQLException {
        String lookupSql = extractionContext.getJdbcEnvironment().getDialect().getQuerySequencesString();
        if (lookupSql == null) {
            return SequenceInformationExtractorNoOpImpl.INSTANCE.extractMetadata(extractionContext);
        }
        List sequenceNames = extractionContext.getQueryResults(lookupSql, null, resultSet -> {
            ArrayList<String> sequences = new ArrayList<String>();
            while (resultSet.next()) {
                sequences.add(this.resultSetSequenceName(resultSet));
            }
            return sequences;
        });
        if (!sequenceNames.isEmpty()) {
            StringBuilder sequenceInfoQueryBuilder = new StringBuilder();
            for (String sequenceName : sequenceNames) {
                if (sequenceInfoQueryBuilder.length() > 0) {
                    sequenceInfoQueryBuilder.append(UNION_ALL);
                }
                sequenceInfoQueryBuilder.append(String.format(SQL_SEQUENCE_QUERY, sequenceName, Identifier.toIdentifier(sequenceName, false, true)));
            }
            return extractionContext.getQueryResults(sequenceInfoQueryBuilder.toString(), null, resultSet -> {
                ArrayList<SequenceInformationImpl> sequenceInformationList = new ArrayList<SequenceInformationImpl>();
                IdentifierHelper identifierHelper = extractionContext.getJdbcEnvironment().getIdentifierHelper();
                while (resultSet.next()) {
                    SequenceInformationImpl sequenceInformation = new SequenceInformationImpl(new QualifiedSequenceName(null, null, identifierHelper.toIdentifier(this.resultSetSequenceName(resultSet))), this.resultSetStartValueSize(resultSet), this.resultSetMinValue(resultSet), this.resultSetMaxValue(resultSet), this.resultSetIncrementValue(resultSet));
                    sequenceInformationList.add(sequenceInformation);
                }
                return sequenceInformationList;
            });
        }
        return Collections.emptyList();
    }

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
}

