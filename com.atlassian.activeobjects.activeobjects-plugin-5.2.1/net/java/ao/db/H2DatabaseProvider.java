/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 */
package net.java.ao.db;

import com.atlassian.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.java.ao.DatabaseProvider;
import net.java.ao.DisposableDataSource;
import net.java.ao.Query;
import net.java.ao.schema.IndexNameConverter;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.UniqueNameConverter;
import net.java.ao.schema.ddl.DDLField;
import net.java.ao.schema.ddl.DDLForeignKey;
import net.java.ao.schema.ddl.DDLIndex;
import net.java.ao.schema.ddl.DDLTable;
import net.java.ao.schema.ddl.SQLAction;
import net.java.ao.types.TypeManager;
import net.java.ao.util.H2VersionUtil;

public class H2DatabaseProvider
extends DatabaseProvider {
    private final H2VersionUtil h2VersionUtil;
    private static final Set<String> RESERVED_WORDS = ImmutableSet.of((Object)"ALL", (Object)"ARRAY", (Object)"CASE", (Object)"CHECK", (Object)"CONSTRAINT", (Object)"CROSS", (Object[])new String[]{"CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "DISTINCT", "EXCEPT", "EXISTS", "FALSE", "FETCH", "FOR", "FOREIGN", "FROM", "FULL", "GROUP", "HAVING", "IF", "INNER", "INTERSECT", "INTERSECTS", "INTERVAL", "IS", "JOIN", "LIKE", "LIMIT", "LOCALTIME", "LOCALTIMESTAMP", "MINUS", "NATURAL", "NOT", "NULL", "OFFSET", "ON", "ORDER", "PRIMARY", "QUALIFY", "ROW", "ROWNUM", "SELECT", "SYSDATE", "SYSTIME", "SYSTIMESTAMP", "TABLE", "TODAY", "TRUE", "UNION", "UNIQUE", "VALUES", "WHERE", "WINDOW", "WITH", "AND", "SECOND", "KEY", "VALUE", "SYSTEM_USER", "USER", "DAY", "DEFAULT", "END", "TO"});

    public H2DatabaseProvider(DisposableDataSource dataSource) {
        this(dataSource, "PUBLIC");
    }

    public H2DatabaseProvider(DisposableDataSource dataSource, String schema) {
        super(dataSource, schema, TypeManager.h2());
        this.h2VersionUtil = new H2VersionUtil(dataSource);
    }

    @Override
    protected String renderQueryLimit(Query query) {
        StringBuilder sql = new StringBuilder();
        if (query.getLimit() < 0 && query.getOffset() > 0 && !this.h2VersionUtil.isH2Latest2_1_X()) {
            sql.append(" LIMIT -1");
        }
        sql.append(super.renderQueryLimit(query));
        return sql.toString();
    }

    @Override
    protected Iterable<SQLAction> renderAlterTableAddColumn(NameConverters nameConverters, DDLTable table, DDLField field) {
        Iterable<SQLAction> back = super.renderAlterTableAddColumn(nameConverters, table, field);
        if (field.isUnique()) {
            return Iterables.concat(back, (Iterable)ImmutableList.of((Object)this.renderAddUniqueConstraint(nameConverters.getUniqueNameConverter(), table, field)));
        }
        return back;
    }

    @Override
    protected Iterable<SQLAction> renderAlterTableChangeColumn(NameConverters nameConverters, DDLTable table, DDLField oldField, DDLField field) {
        ImmutableList.Builder back = ImmutableList.builder();
        back.addAll(super.renderAlterTableChangeColumn(nameConverters, table, oldField, field));
        if (!field.isPrimaryKey()) {
            if (oldField.isUnique() && !field.isUnique()) {
                back.add((Object)this.renderDropUniqueConstraint(nameConverters.getUniqueNameConverter(), table, field));
            } else if (!oldField.isUnique() && field.isUnique()) {
                back.add((Object)this.renderAddUniqueConstraint(nameConverters.getUniqueNameConverter(), table, field));
            }
        }
        return back.build();
    }

    @Override
    protected SQLAction renderAlterTableChangeColumnStatement(NameConverters nameConverters, DDLTable table, DDLField oldField, DDLField field, DatabaseProvider.RenderFieldOptions options) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ");
        sql.append(this.withSchema(table.getName()));
        sql.append(" ALTER COLUMN ");
        if (this.h2VersionUtil.isH2Latest2_1_X() && oldField.isNotNull() && field.isNotNull() && field.isPrimaryKey() && !field.getType().equals(oldField.getType())) {
            sql.append(this.renderFieldForLatestH2(table, field));
        } else {
            sql.append(this.renderField(nameConverters, table, field, options));
            if (oldField.isNotNull() && !field.isNotNull()) {
                sql.append(" NULL ");
            }
        }
        return SQLAction.of(sql);
    }

    protected String renderFieldForLatestH2(DDLTable table, DDLField field) {
        StringBuilder back = new StringBuilder();
        back.append(field.getName());
        back.append(" ");
        back.append(field.getType().getSchemaProperties().getSqlTypeName());
        back.append(" GENERATED BY DEFAULT AS IDENTITY(RESTART WITH (SELECT MAX(");
        back.append(field.getName());
        back.append(") FROM ");
        back.append(this.withSchema(table.getName())).append(")+1)");
        return back.toString();
    }

    @Override
    protected String renderConstraints(NameConverters nameConverters, List<String> primaryKeys, DDLTable table) {
        StringBuilder back = new StringBuilder();
        if (primaryKeys.size() > 0) {
            back.append("    PRIMARY KEY(").append(this.processID(primaryKeys.get(0))).append(")");
        }
        back.append(this.renderConstraintsForTable(nameConverters.getUniqueNameConverter(), table));
        return back.toString();
    }

    @Override
    protected String renderFieldDefault(DDLTable table, DDLField field) {
        StringBuilder sql = new StringBuilder();
        if (field.getDefaultValue() != null) {
            sql.append(" DEFAULT ").append(this.renderValue(field.getDefaultValue()));
        }
        return sql.toString();
    }

    @Override
    protected SQLAction renderAlterTableDropKey(DDLForeignKey key) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ");
        sql.append(this.withSchema(key.getDomesticTable()));
        sql.append(" DROP CONSTRAINT ");
        sql.append(this.processID(key.getFKName()));
        return SQLAction.of(sql);
    }

    @Override
    protected SQLAction renderDropIndex(IndexNameConverter indexNameConverter, DDLIndex index) {
        return SQLAction.of(new StringBuilder().append("DROP INDEX IF EXISTS ").append(this.withSchema(index.getIndexName())));
    }

    @Override
    protected String renderConstraintsForTable(UniqueNameConverter uniqueNameConverter, DDLTable table) {
        StringBuilder sql = new StringBuilder();
        int count = 0;
        boolean uniqueCheck = false;
        int uniqueCount = 0;
        int checkUnique = 0;
        for (DDLField dDLField : table.getFields()) {
            if (!dDLField.isUnique()) continue;
            uniqueCheck = true;
            ++uniqueCount;
        }
        if (table.getForeignKeys().length > 0) {
            sql.append(",\n    ");
            for (DDLForeignKey dDLForeignKey : table.getForeignKeys()) {
                sql.append(this.renderForeignKey(dDLForeignKey));
                if (table.getForeignKeys().length <= ++count) continue;
                sql.append(",\n");
            }
        }
        if (uniqueCheck) {
            sql.append(",\n   ");
            for (DDLField dDLField : table.getFields()) {
                if (!dDLField.isUnique()) continue;
                sql.append(this.renderUniqueConstraint(uniqueNameConverter, table, dDLField));
                if (uniqueCount <= ++checkUnique) continue;
                sql.append(",\n   ");
            }
        }
        sql.append("\n");
        return sql.toString();
    }

    @Override
    protected String renderUnique(UniqueNameConverter uniqueNameConverter, DDLTable table, DDLField field) {
        return "";
    }

    @Override
    public Object parseValue(int type, String value) {
        if (value == null || value.equals("") || value.equals("NULL")) {
            return null;
        }
        switch (type) {
            case 12: 
            case 91: 
            case 92: 
            case 93: {
                Matcher matcher = Pattern.compile("'(.*)'.*").matcher(value);
                if (!matcher.find()) break;
                value = matcher.group(1);
            }
        }
        return super.parseValue(type, value);
    }

    @Override
    protected Set<String> getReservedWords() {
        return RESERVED_WORDS;
    }

    private SQLAction renderAddUniqueConstraint(UniqueNameConverter uniqueNameConverter, DDLTable table, DDLField field) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ");
        sql.append(this.withSchema(table.getName()));
        sql.append(" ADD ");
        sql.append(this.renderUniqueConstraint(uniqueNameConverter, table, field));
        return SQLAction.of(sql);
    }

    private SQLAction renderDropUniqueConstraint(UniqueNameConverter uniqueNameConverter, DDLTable table, DDLField field) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ");
        sql.append(this.withSchema(table.getName()));
        sql.append(" DROP CONSTRAINT ");
        sql.append(uniqueNameConverter.getName(table.getName(), field.getName()));
        return SQLAction.of(sql);
    }

    private String renderUniqueConstraint(UniqueNameConverter uniqueNameConverter, DDLTable table, DDLField field) {
        StringBuilder sql = new StringBuilder();
        sql.append(" CONSTRAINT ");
        sql.append(uniqueNameConverter.getName(table.getName(), field.getName()));
        sql.append(" UNIQUE(");
        sql.append(this.processID(field.getName()));
        sql.append(")");
        return sql.toString();
    }

    @VisibleForTesting
    public H2VersionUtil getH2VersionUtil() {
        return this.h2VersionUtil;
    }
}

