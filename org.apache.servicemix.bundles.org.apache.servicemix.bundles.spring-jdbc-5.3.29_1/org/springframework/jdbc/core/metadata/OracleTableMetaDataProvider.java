/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.jdbc.core.metadata;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.metadata.GenericTableMetaDataProvider;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

public class OracleTableMetaDataProvider
extends GenericTableMetaDataProvider {
    private final boolean includeSynonyms;
    @Nullable
    private final String defaultSchema;

    public OracleTableMetaDataProvider(DatabaseMetaData databaseMetaData) throws SQLException {
        this(databaseMetaData, false);
    }

    public OracleTableMetaDataProvider(DatabaseMetaData databaseMetaData, boolean includeSynonyms) throws SQLException {
        super(databaseMetaData);
        this.includeSynonyms = includeSynonyms;
        this.defaultSchema = OracleTableMetaDataProvider.lookupDefaultSchema(databaseMetaData);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private static String lookupDefaultSchema(DatabaseMetaData databaseMetaData) {
        try (Statement cstmt = null;){
            Connection con = databaseMetaData.getConnection();
            if (con == null) {
                logger.debug((Object)"Cannot check default schema - no Connection from DatabaseMetaData");
                String string2 = null;
                return string2;
            }
            cstmt = con.prepareCall("{? = call sys_context('USERENV', 'CURRENT_SCHEMA')}");
            cstmt.registerOutParameter(1, 12);
            cstmt.execute();
            String string = cstmt.getString(1);
            return string;
        }
        catch (SQLException ex) {
            logger.debug((Object)"Exception encountered during default schema lookup", (Throwable)ex);
            return null;
        }
    }

    @Override
    @Nullable
    protected String getDefaultSchema() {
        if (this.defaultSchema != null) {
            return this.defaultSchema;
        }
        return super.getDefaultSchema();
    }

    @Override
    public void initializeWithTableColumnMetaData(DatabaseMetaData databaseMetaData, @Nullable String catalogName, @Nullable String schemaName, @Nullable String tableName) throws SQLException {
        Method setIncludeSynonyms;
        Boolean originalValueForIncludeSynonyms;
        if (!this.includeSynonyms) {
            logger.debug((Object)"Defaulting to no synonyms in table meta-data lookup");
            super.initializeWithTableColumnMetaData(databaseMetaData, catalogName, schemaName, tableName);
            return;
        }
        Connection con = databaseMetaData.getConnection();
        if (con == null) {
            logger.info((Object)"Unable to include synonyms in table meta-data lookup - no Connection from DatabaseMetaData");
            super.initializeWithTableColumnMetaData(databaseMetaData, catalogName, schemaName, tableName);
            return;
        }
        try {
            Class<?> oracleConClass = con.getClass().getClassLoader().loadClass("oracle.jdbc.OracleConnection");
            con = (Connection)con.unwrap(oracleConClass);
        }
        catch (ClassNotFoundException | SQLException ex) {
            if (logger.isInfoEnabled()) {
                logger.info((Object)("Unable to include synonyms in table meta-data lookup - no Oracle Connection: " + ex));
            }
            super.initializeWithTableColumnMetaData(databaseMetaData, catalogName, schemaName, tableName);
            return;
        }
        logger.debug((Object)"Including synonyms in table meta-data lookup");
        try {
            Method getIncludeSynonyms = con.getClass().getMethod("getIncludeSynonyms", new Class[0]);
            ReflectionUtils.makeAccessible((Method)getIncludeSynonyms);
            originalValueForIncludeSynonyms = (Boolean)getIncludeSynonyms.invoke((Object)con, new Object[0]);
            setIncludeSynonyms = con.getClass().getMethod("setIncludeSynonyms", Boolean.TYPE);
            ReflectionUtils.makeAccessible((Method)setIncludeSynonyms);
            setIncludeSynonyms.invoke((Object)con, Boolean.TRUE);
        }
        catch (Throwable ex) {
            throw new InvalidDataAccessApiUsageException("Could not prepare Oracle Connection", ex);
        }
        super.initializeWithTableColumnMetaData(databaseMetaData, catalogName, schemaName, tableName);
        try {
            setIncludeSynonyms.invoke((Object)con, originalValueForIncludeSynonyms);
        }
        catch (Throwable ex) {
            throw new InvalidDataAccessApiUsageException("Could not reset Oracle Connection", ex);
        }
    }
}

