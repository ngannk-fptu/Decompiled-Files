/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.time.Duration;
import org.hibernate.MappingException;
import org.hibernate.dialect.MariaDB102Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorMariaDBDatabaseImpl;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
import org.hibernate.type.StandardBasicTypes;

public class MariaDB103Dialect
extends MariaDB102Dialect {
    public MariaDB103Dialect() {
        this.registerFunction("chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER));
    }

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public boolean supportsPooledSequences() {
        return true;
    }

    @Override
    public String getCreateSequenceString(String sequenceName) {
        return "create sequence " + sequenceName;
    }

    @Override
    protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) throws MappingException {
        String sequenceString = this.getCreateSequenceString(sequenceName) + " start with " + initialValue + " increment by " + incrementSize;
        if (incrementSize > 0 && initialValue < 0) {
            return sequenceString + " minvalue " + initialValue;
        }
        if (incrementSize < 0 && initialValue > 0) {
            return sequenceString + " maxvalue " + initialValue;
        }
        return sequenceString;
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "drop sequence " + sequenceName;
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "select " + this.getSelectSequenceNextValString(sequenceName);
    }

    @Override
    public String getSelectSequenceNextValString(String sequenceName) {
        return "nextval(" + sequenceName + ")";
    }

    @Override
    public String getQuerySequencesString() {
        return "select table_name from information_schema.tables where table_schema = database() and table_type = 'SEQUENCE'";
    }

    @Override
    public SequenceInformationExtractor getSequenceInformationExtractor() {
        return SequenceInformationExtractorMariaDBDatabaseImpl.INSTANCE;
    }

    @Override
    public String getWriteLockString(int timeout) {
        if (timeout == 0) {
            return this.getForUpdateNowaitString();
        }
        if (timeout > 0) {
            return this.getForUpdateString() + " wait " + MariaDB103Dialect.getLockWaitTimeoutInSeconds(timeout);
        }
        return this.getForUpdateString();
    }

    @Override
    public String getForUpdateNowaitString() {
        return this.getForUpdateString() + " nowait";
    }

    @Override
    public String getForUpdateNowaitString(String aliases) {
        return this.getForUpdateString(aliases) + " nowait";
    }

    private static long getLockWaitTimeoutInSeconds(int timeoutInMilliseconds) {
        Duration duration = Duration.ofMillis(timeoutInMilliseconds);
        return duration.getSeconds();
    }
}

