/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.backup;

import com.atlassian.activeobjects.backup.SqlUtils;
import com.atlassian.dbexporter.Column;
import com.atlassian.dbexporter.DatabaseInformation;
import com.atlassian.dbexporter.DatabaseInformations;
import com.atlassian.dbexporter.EntityNameProcessor;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.Table;
import com.atlassian.dbexporter.importer.TableCreator;
import com.atlassian.dbexporter.jdbc.JdbcUtils;
import com.atlassian.dbexporter.progress.ProgressMonitor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import net.java.ao.DatabaseProvider;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.ddl.DDLAction;
import net.java.ao.schema.ddl.DDLActionType;
import net.java.ao.schema.ddl.DDLField;
import net.java.ao.schema.ddl.DDLTable;
import net.java.ao.schema.ddl.SQLAction;
import net.java.ao.types.TypeInfo;
import net.java.ao.types.TypeManager;
import net.java.ao.types.TypeQualifiers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ActiveObjectsTableCreator
implements TableCreator {
    private final Logger logger = LoggerFactory.getLogger((String)"net.java.ao.sql");
    private final ImportExportErrorService errorService;
    private final DatabaseProvider provider;
    private final NameConverters converters;

    public ActiveObjectsTableCreator(ImportExportErrorService errorService, DatabaseProvider provider, NameConverters converters) {
        this.errorService = (ImportExportErrorService)Preconditions.checkNotNull((Object)errorService);
        this.provider = (DatabaseProvider)Preconditions.checkNotNull((Object)provider);
        this.converters = (NameConverters)Preconditions.checkNotNull((Object)converters);
    }

    @Override
    public void create(DatabaseInformation databaseInformation, Iterable<Table> tables, EntityNameProcessor entityNameProcessor, ProgressMonitor monitor) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.provider.getConnection();
            stmt = conn.createStatement();
            for (Table table : tables) {
                monitor.begin(ProgressMonitor.Task.TABLE_CREATION, entityNameProcessor.tableName(table.getName()));
                this.create(DatabaseInformations.database(databaseInformation), stmt, table, entityNameProcessor);
                monitor.end(ProgressMonitor.Task.TABLE_CREATION, entityNameProcessor.tableName(table.getName()));
            }
        }
        catch (SQLException e) {
            try {
                throw this.errorService.newImportExportSqlException(null, "", e);
            }
            catch (Throwable throwable) {
                JdbcUtils.closeQuietly(new Statement[]{stmt});
                JdbcUtils.closeQuietly(conn);
                throw throwable;
            }
        }
        JdbcUtils.closeQuietly(new Statement[]{stmt});
        JdbcUtils.closeQuietly(conn);
    }

    private void create(DatabaseInformations.Database db, Statement stmt, Table table, EntityNameProcessor entityNameProcessor) {
        DDLAction a = new DDLAction(DDLActionType.CREATE);
        a.setTable(this.toDdlTable(this.exportTypeManager(db), entityNameProcessor, table));
        Iterable<SQLAction> sqlStatements = this.provider.renderAction(this.converters, a);
        for (SQLAction sql : sqlStatements) {
            SqlUtils.executeUpdate(this.errorService, table.getName(), stmt, sql.getStatement());
        }
    }

    private DDLTable toDdlTable(TypeManager exportTypeManager, EntityNameProcessor entityNameProcessor, Table table) {
        DDLTable ddlTable = new DDLTable();
        ddlTable.setName(entityNameProcessor.tableName(table.getName()));
        ArrayList fields = Lists.newArrayList();
        for (Column column : table.getColumns()) {
            fields.add(this.toDdlField(exportTypeManager, entityNameProcessor, column));
        }
        ddlTable.setFields(fields.toArray(new DDLField[fields.size()]));
        return ddlTable;
    }

    private DDLField toDdlField(TypeManager exportTypeManager, EntityNameProcessor entityNameProcessor, Column column) {
        Boolean autoIncrement;
        DDLField ddlField = new DDLField();
        ddlField.setName(entityNameProcessor.columnName(column.getName()));
        TypeInfo<?> typeFromSchema = this.getTypeInfo(exportTypeManager, column);
        ddlField.setType(typeFromSchema);
        ddlField.setJdbcType(typeFromSchema.getJdbcWriteType());
        Boolean pk = column.isPrimaryKey();
        if (pk != null) {
            ddlField.setPrimaryKey(pk);
        }
        if ((autoIncrement = column.isAutoIncrement()) != null) {
            ddlField.setAutoIncrement(autoIncrement);
        }
        return ddlField;
    }

    private TypeInfo<?> getTypeInfo(TypeManager exportTypeManager, Column column) {
        TypeQualifiers qualifiers = this.getQualifiers(column);
        TypeInfo<?> exportedType = exportTypeManager.getTypeFromSchema(this.getSqlType(column), qualifiers);
        Class<?> javaType = exportedType.getLogicalType().getAllTypes().iterator().next();
        TypeInfo<?> type = this.provider.getTypeManager().getType(javaType, exportedType.getQualifiers());
        return type;
    }

    private int getSqlType(Column column) {
        if (column.getSqlType() == 2) {
            if (column.getScale() != null && column.getScale() > 0) {
                return 8;
            }
            if (column.getPrecision() != null) {
                switch (column.getPrecision()) {
                    case 1: {
                        return 16;
                    }
                    case 11: {
                        return 4;
                    }
                    case 126: {
                        return 8;
                    }
                }
                return -5;
            }
            throw new IllegalStateException("Could not determine the proper mapping from Oracle export, for column:" + column.getName());
        }
        return column.getSqlType();
    }

    private TypeQualifiers getQualifiers(Column column) {
        TypeQualifiers qualifiers = TypeQualifiers.qualifiers();
        if (this.isString(column)) {
            qualifiers = qualifiers.stringLength(column.getPrecision());
        }
        return qualifiers;
    }

    private boolean isString(Column column) {
        int sqlType = this.getSqlType(column);
        return sqlType == 1 || sqlType == -16 || sqlType == -15 || sqlType == 12 || sqlType == 2005 || sqlType == 2011 || sqlType == -9;
    }

    private TypeManager exportTypeManager(DatabaseInformations.Database db) {
        switch (db.getType()) {
            case H2: {
                return TypeManager.h2();
            }
            case HSQL: {
                return TypeManager.hsql();
            }
            case MYSQL: {
                return TypeManager.mysql();
            }
            case POSTGRES: {
                return TypeManager.postgres();
            }
            case MSSQL: {
                return TypeManager.sqlServer();
            }
            case ORACLE: {
                return TypeManager.oracle();
            }
        }
        throw this.errorService.newImportExportException(null, "Could not determine the source database");
    }
}

