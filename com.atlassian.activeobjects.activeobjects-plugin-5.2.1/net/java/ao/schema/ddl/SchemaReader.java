/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 */
package net.java.ao.schema.ddl;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.java.ao.Common;
import net.java.ao.DatabaseProvider;
import net.java.ao.SchemaConfiguration;
import net.java.ao.schema.Case;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.ddl.DDLAction;
import net.java.ao.schema.ddl.DDLActionType;
import net.java.ao.schema.ddl.DDLField;
import net.java.ao.schema.ddl.DDLForeignKey;
import net.java.ao.schema.ddl.DDLIndex;
import net.java.ao.schema.ddl.DDLIndexField;
import net.java.ao.schema.ddl.DDLTable;
import net.java.ao.schema.helper.DatabaseMetaDataReader;
import net.java.ao.schema.helper.DatabaseMetaDataReaderImpl;
import net.java.ao.schema.helper.Field;
import net.java.ao.schema.helper.ForeignKey;
import net.java.ao.schema.helper.Index;
import net.java.ao.sql.SqlUtils;
import net.java.ao.types.TypeInfo;
import net.java.ao.types.TypeManager;
import net.java.ao.types.TypeQualifiers;

public final class SchemaReader {
    private static final long DEFAULT_MYSQL_TIME;

    public static DDLTable[] readSchema(DatabaseProvider provider, NameConverters nameConverters, SchemaConfiguration schemaConfiguration) throws SQLException {
        return SchemaReader.readSchema(provider, nameConverters, schemaConfiguration, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static DDLTable[] readSchema(DatabaseProvider provider, NameConverters nameConverters, SchemaConfiguration schemaConfiguration, boolean includeForeignKeys) throws SQLException {
        Connection connection = null;
        try {
            connection = provider.getConnection();
            DDLTable[] dDLTableArray = SchemaReader.readSchema(connection, provider, nameConverters, schemaConfiguration, includeForeignKeys);
            return dDLTableArray;
        }
        finally {
            SqlUtils.closeQuietly(connection);
        }
    }

    public static DDLTable[] readSchema(Connection connection, DatabaseProvider provider, NameConverters nameConverters, SchemaConfiguration schemaConfiguration, final boolean includeForeignKeys) throws SQLException {
        final DatabaseMetaDataReaderImpl databaseMetaDataReader = new DatabaseMetaDataReaderImpl(provider, nameConverters, schemaConfiguration);
        final DatabaseMetaData databaseMetaData = connection.getMetaData();
        ArrayList tables = Lists.newArrayList((Iterable)Iterables.transform(databaseMetaDataReader.getTableNames(databaseMetaData), (Function)new Function<String, DDLTable>(){

            public DDLTable apply(String tableName) {
                return SchemaReader.readTable(databaseMetaDataReader, databaseMetaData, tableName, includeForeignKeys);
            }
        }));
        return tables.toArray(new DDLTable[tables.size()]);
    }

    private static DDLTable readTable(DatabaseMetaDataReader databaseMetaDataReader, DatabaseMetaData databaseMetaData, String tableName, boolean includeForeignKeys) {
        DDLTable table = new DDLTable();
        table.setName(tableName);
        List<DDLField> fields = SchemaReader.readFields(databaseMetaDataReader, databaseMetaData, tableName);
        table.setFields(fields.toArray(new DDLField[fields.size()]));
        if (includeForeignKeys) {
            List<DDLForeignKey> foreignKeys = SchemaReader.readForeignKeys(databaseMetaDataReader, databaseMetaData, tableName);
            table.setForeignKeys(foreignKeys.toArray(new DDLForeignKey[foreignKeys.size()]));
        }
        List<DDLIndex> indexes = SchemaReader.readIndexes(databaseMetaDataReader, databaseMetaData, tableName);
        table.setIndexes(indexes.toArray(new DDLIndex[indexes.size()]));
        return table;
    }

    private static List<DDLField> readFields(DatabaseMetaDataReader databaseMetaDataReader, DatabaseMetaData databaseMetaData, String tableName) {
        return Lists.newArrayList((Iterable)Iterables.transform(databaseMetaDataReader.getFields(databaseMetaData, tableName), (Function)new Function<Field, DDLField>(){

            public DDLField apply(Field from) {
                DDLField field = new DDLField();
                field.setAutoIncrement(from.isAutoIncrement());
                field.setDefaultValue(from.getDefaultValue());
                field.setName(from.getName());
                field.setNotNull(from.isNotNull());
                field.setPrimaryKey(from.isPrimaryKey());
                field.setType(from.getDatabaseType());
                field.setJdbcType(from.getJdbcType());
                field.setUnique(from.isUnique());
                return field;
            }
        }));
    }

    private static List<DDLForeignKey> readForeignKeys(DatabaseMetaDataReader databaseMetaDataReader, DatabaseMetaData databaseMetaData, String tableName) {
        return Lists.newArrayList((Iterable)Iterables.transform(databaseMetaDataReader.getForeignKeys(databaseMetaData, tableName), (Function)new Function<ForeignKey, DDLForeignKey>(){

            public DDLForeignKey apply(ForeignKey from) {
                DDLForeignKey key = new DDLForeignKey();
                key.setForeignField(from.getForeignFieldName());
                key.setField(from.getLocalFieldName());
                key.setTable(from.getForeignTableName());
                key.setDomesticTable(from.getLocalTableName());
                return key;
            }
        }));
    }

    private static List<DDLIndex> readIndexes(DatabaseMetaDataReader databaseMetaDataReader, DatabaseMetaData databaseMetaData, String tableName) {
        Iterable<? extends Index> indexes = databaseMetaDataReader.getIndexes(databaseMetaData, tableName);
        return StreamSupport.stream(indexes.spliterator(), false).map(index -> SchemaReader.toDDLIndex(index, tableName)).collect(Collectors.toList());
    }

    private static DDLIndex toDDLIndex(Index index, String tableName) {
        return DDLIndex.builder().indexName(index.getIndexName()).table(tableName).fields((DDLIndexField[])index.getFieldNames().stream().map(SchemaReader::toDDLIndexField).toArray(DDLIndexField[]::new)).build();
    }

    private static DDLIndexField toDDLIndexField(String fieldName) {
        return DDLIndexField.builder().fieldName(fieldName).build();
    }

    /*
     * WARNING - void declaration
     */
    public static DDLAction[] diffSchema(TypeManager typeManager, DDLTable[] fromArray, DDLTable[] ontoArray, boolean caseSensitive) {
        void var12_26;
        void var12_24;
        void var12_22;
        String tableName;
        void var12_20;
        HashSet<DDLAction> actions = new HashSet<DDLAction>();
        ArrayList<DDLTable> createTables = new ArrayList<DDLTable>();
        ArrayList<DDLTable> dropTables = new ArrayList<DDLTable>();
        ArrayList<DDLTable> alterTables = new ArrayList<DDLTable>();
        HashMap<String, DDLTable> from = new HashMap<String, DDLTable>();
        HashMap<String, DDLTable> onto = new HashMap<String, DDLTable>();
        Object object = fromArray;
        int n = ((DDLTable[])object).length;
        boolean bl = false;
        while (var12_20 < n) {
            DDLTable dDLTable = object[var12_20];
            tableName = SchemaReader.transform(dDLTable.getName(), caseSensitive);
            from.put(tableName, dDLTable);
            ++var12_20;
        }
        object = ontoArray;
        int n2 = ((DDLTable[])object).length;
        boolean bl2 = false;
        while (var12_22 < n2) {
            DDLTable dDLTable = object[var12_22];
            tableName = SchemaReader.transform(dDLTable.getName(), caseSensitive);
            onto.put(tableName, dDLTable);
            ++var12_22;
        }
        object = fromArray;
        int n3 = ((DDLTable[])object).length;
        boolean bl3 = false;
        while (var12_24 < n3) {
            DDLTable dDLTable = object[var12_24];
            tableName = SchemaReader.transform(dDLTable.getName(), caseSensitive);
            if (onto.containsKey(tableName)) {
                alterTables.add(dDLTable);
            } else {
                createTables.add(dDLTable);
            }
            ++var12_24;
        }
        object = ontoArray;
        int n4 = ((DDLTable[])object).length;
        boolean bl4 = false;
        while (var12_26 < n4) {
            DDLTable dDLTable = object[var12_26];
            tableName = SchemaReader.transform(dDLTable.getName(), caseSensitive);
            if (!from.containsKey(tableName)) {
                dropTables.add(dDLTable);
            }
            ++var12_26;
        }
        for (DDLTable dDLTable : createTables) {
            DDLAction dDLAction = new DDLAction(DDLActionType.CREATE);
            dDLAction.setTable(dDLTable);
            actions.add(dDLAction);
            for (DDLIndex dDLIndex : dDLTable.getIndexes()) {
                DDLAction indexAction = new DDLAction(DDLActionType.CREATE_INDEX);
                indexAction.setIndex(dDLIndex);
                actions.add(indexAction);
            }
        }
        ArrayList<DDLForeignKey> dropKeys = new ArrayList<DDLForeignKey>();
        for (DDLTable dDLTable : dropTables) {
            DDLAction dDLAction = new DDLAction(DDLActionType.DROP);
            dDLAction.setTable(dDLTable);
            actions.add(dDLAction);
            dropKeys.addAll(Arrays.asList(dDLTable.getForeignKeys()));
            for (DDLTable dDLTable2 : alterTables) {
                for (DDLForeignKey fKey : dDLTable2.getForeignKeys()) {
                    if (!SchemaReader.equals(fKey.getTable(), dDLTable.getName(), caseSensitive)) continue;
                    dropKeys.add(fKey);
                }
            }
        }
        for (DDLTable dDLTable : alterTables) {
            boolean present;
            String fieldName;
            String string = dDLTable.getName();
            String tableName2 = SchemaReader.transform(string, caseSensitive);
            DDLTable dDLTable3 = (DDLTable)onto.get(tableName2);
            ArrayList<DDLField> arrayList = new ArrayList<DDLField>();
            ArrayList<DDLField> dropFields = new ArrayList<DDLField>();
            ArrayList<DDLField> alterFields = new ArrayList<DDLField>();
            HashMap<String, DDLField> fromFields = new HashMap<String, DDLField>();
            HashMap<String, DDLField> ontoFields = new HashMap<String, DDLField>();
            for (DDLField field : dDLTable.getFields()) {
                fieldName = SchemaReader.transform(field.getName(), caseSensitive);
                fromFields.put(fieldName, field);
            }
            for (DDLField field : dDLTable3.getFields()) {
                fieldName = SchemaReader.transform(field.getName(), caseSensitive);
                ontoFields.put(fieldName, field);
            }
            for (DDLField field : dDLTable.getFields()) {
                fieldName = SchemaReader.transform(field.getName(), caseSensitive);
                if (ontoFields.containsKey(fieldName)) {
                    alterFields.add(field);
                    continue;
                }
                arrayList.add(field);
            }
            for (DDLField field : dDLTable3.getFields()) {
                fieldName = SchemaReader.transform(field.getName(), caseSensitive);
                if (fromFields.containsKey(fieldName)) continue;
                dropFields.add(field);
            }
            for (DDLField field : arrayList) {
                DDLAction action3 = new DDLAction(DDLActionType.ALTER_ADD_COLUMN);
                action3.setTable(dDLTable);
                action3.setField(field);
                actions.add(action3);
            }
            for (DDLField field : dropFields) {
                DDLAction action4 = new DDLAction(DDLActionType.ALTER_DROP_COLUMN);
                action4.setTable(dDLTable);
                action4.setField(field);
                actions.add(action4);
            }
            for (DDLField fromField : alterFields) {
                String fieldName2 = SchemaReader.transform(fromField.getName(), caseSensitive);
                DDLField ontoField = (DDLField)ontoFields.get(fieldName2);
                if (fromField.getDefaultValue() == null && ontoField.getDefaultValue() != null) {
                    actions.add(SchemaReader.createColumnAlterAction(dDLTable, ontoField, fromField));
                    continue;
                }
                if (fromField.getDefaultValue() != null && !Common.fuzzyCompare(typeManager, fromField.getDefaultValue(), ontoField.getDefaultValue())) {
                    actions.add(SchemaReader.createColumnAlterAction(dDLTable, ontoField, fromField));
                    continue;
                }
                if (!SchemaReader.physicalTypesEqual(fromField.getType(), ontoField.getType())) {
                    actions.add(SchemaReader.createColumnAlterAction(dDLTable, ontoField, fromField));
                    continue;
                }
                if (fromField.isNotNull() != ontoField.isNotNull()) {
                    actions.add(SchemaReader.createColumnAlterAction(dDLTable, ontoField, fromField));
                    continue;
                }
                if (fromField.isPrimaryKey() || fromField.isUnique() == ontoField.isUnique()) continue;
                actions.add(SchemaReader.createColumnAlterAction(dDLTable, ontoField, fromField));
            }
            ArrayList<DDLForeignKey> addKeys = new ArrayList<DDLForeignKey>();
            for (DDLForeignKey fromKey : dDLTable.getForeignKeys()) {
                for (DDLForeignKey ontoKey : dDLTable3.getForeignKeys()) {
                    if (fromKey.getTable().equalsIgnoreCase(ontoKey.getTable()) && fromKey.getForeignField().equalsIgnoreCase(ontoKey.getForeignField()) || !fromKey.getField().equalsIgnoreCase(ontoKey.getField()) || !fromKey.getDomesticTable().equalsIgnoreCase(ontoKey.getDomesticTable())) continue;
                    addKeys.add(fromKey);
                }
            }
            for (DDLForeignKey ontoKey : dDLTable3.getForeignKeys()) {
                if (SchemaReader.containsField(dropFields, ontoKey.getField())) {
                    dropKeys.add(ontoKey);
                    continue;
                }
                for (DDLForeignKey fromKey : dDLTable.getForeignKeys()) {
                    if (ontoKey.getTable().equalsIgnoreCase(fromKey.getTable()) && ontoKey.getForeignField().equalsIgnoreCase(fromKey.getForeignField()) || !ontoKey.getField().equalsIgnoreCase(fromKey.getField()) || !ontoKey.getDomesticTable().equalsIgnoreCase(fromKey.getDomesticTable())) continue;
                    dropKeys.add(ontoKey);
                }
            }
            for (DDLForeignKey key : addKeys) {
                DDLAction action5 = new DDLAction(DDLActionType.ALTER_ADD_KEY);
                action5.setKey(key);
                actions.add(action5);
            }
            ArrayList<DDLIndex> addIndexes = new ArrayList<DDLIndex>();
            ArrayList<DDLIndex> dropIndexes = new ArrayList<DDLIndex>();
            for (DDLIndex fromIndex : dDLTable.getIndexes()) {
                present = Stream.of(dDLTable3.getIndexes()).filter(index -> index.equals(fromIndex)).findAny().isPresent();
                if (present) continue;
                addIndexes.add(fromIndex);
            }
            for (DDLIndex ontoIndex : dDLTable3.getIndexes()) {
                present = Stream.of(dDLTable.getIndexes()).filter(index -> index.equals(ontoIndex)).findAny().isPresent();
                if (present) continue;
                dropIndexes.add(ontoIndex);
            }
            for (DDLIndex index3 : addIndexes) {
                DDLAction action6 = new DDLAction(DDLActionType.CREATE_INDEX);
                action6.setIndex(index3);
                actions.add(action6);
            }
            for (DDLIndex index4 : dropIndexes) {
                DDLAction action7 = new DDLAction(DDLActionType.DROP_INDEX);
                action7.setIndex(index4);
                actions.add(action7);
            }
        }
        for (DDLForeignKey dDLForeignKey : dropKeys) {
            DDLAction dDLAction = new DDLAction(DDLActionType.ALTER_DROP_KEY);
            dDLAction.setKey(dDLForeignKey);
            actions.add(dDLAction);
        }
        return actions.toArray(new DDLAction[actions.size()]);
    }

    private static boolean physicalTypesEqual(TypeInfo from, TypeInfo onto) {
        return TypeQualifiers.areCompatible(from.getQualifiers(), onto.getQualifiers()) && from.getSchemaProperties().equals(onto.getSchemaProperties());
    }

    private static boolean equals(String s, String s1, boolean caseSensitive) {
        return SchemaReader.transform(s, caseSensitive).equals(SchemaReader.transform(s1, caseSensitive));
    }

    private static String transform(String s, boolean caseSensitive) {
        if (!caseSensitive) {
            return Case.LOWER.apply(s);
        }
        return s;
    }

    public static DDLAction[] sortTopologically(DDLAction[] actions) {
        LinkedList<DDLAction> back = new LinkedList<DDLAction>();
        HashMap<DDLAction, Set<DDLAction>> deps = new HashMap<DDLAction, Set<DDLAction>>();
        LinkedList<DDLAction> roots = new LinkedList<DDLAction>();
        HashSet<DDLAction> covered = new HashSet<DDLAction>();
        SchemaReader.performSort(actions, deps, roots);
        while (!roots.isEmpty()) {
            DDLAction[] rootsArray = roots.toArray(new DDLAction[roots.size()]);
            roots.remove(rootsArray[0]);
            if (covered.contains(rootsArray[0])) {
                throw new RuntimeException("Circular dependency detected in or below " + rootsArray[0].getTable().getName());
            }
            covered.add(rootsArray[0]);
            back.add(rootsArray[0]);
            LinkedList<DDLAction> toRemove = new LinkedList<DDLAction>();
            for (DDLAction depAction : deps.keySet()) {
                Set individualDeps = (Set)deps.get(depAction);
                individualDeps.remove(rootsArray[0]);
                if (!individualDeps.isEmpty()) continue;
                roots.add(depAction);
                toRemove.add(depAction);
            }
            for (DDLAction action : toRemove) {
                deps.remove(action);
            }
        }
        return back.toArray(new DDLAction[back.size()]);
    }

    private static void performSort(DDLAction[] actions, Map<DDLAction, Set<DDLAction>> deps, List<DDLAction> roots) {
        DDLForeignKey key;
        LinkedList<DDLAction> dropKeys = new LinkedList<DDLAction>();
        LinkedList<DDLAction> dropIndexes = new LinkedList<DDLAction>();
        LinkedList<DDLAction> dropColumns = new LinkedList<DDLAction>();
        LinkedList<DDLAction> changeColumns = new LinkedList<DDLAction>();
        LinkedList<DDLAction> drops = new LinkedList<DDLAction>();
        LinkedList<DDLAction> creates = new LinkedList<DDLAction>();
        LinkedList<DDLAction> addColumns = new LinkedList<DDLAction>();
        LinkedList<DDLAction> addKeys = new LinkedList<DDLAction>();
        LinkedList<DDLAction> createIndexes = new LinkedList<DDLAction>();
        block11: for (DDLAction action : actions) {
            switch (action.getActionType()) {
                case ALTER_DROP_KEY: {
                    dropKeys.add(action);
                    continue block11;
                }
                case DROP_INDEX: {
                    dropIndexes.add(action);
                    continue block11;
                }
                case ALTER_DROP_COLUMN: {
                    dropColumns.add(action);
                    continue block11;
                }
                case ALTER_CHANGE_COLUMN: {
                    changeColumns.add(action);
                    continue block11;
                }
                case DROP: {
                    drops.add(action);
                    continue block11;
                }
                case CREATE: {
                    creates.add(action);
                    continue block11;
                }
                case ALTER_ADD_COLUMN: {
                    addColumns.add(action);
                    continue block11;
                }
                case ALTER_ADD_KEY: {
                    addKeys.add(action);
                    continue block11;
                }
                case CREATE_INDEX: {
                    createIndexes.add(action);
                }
            }
        }
        roots.addAll(dropKeys);
        roots.addAll(dropIndexes);
        for (DDLAction action : dropColumns) {
            HashSet<DDLAction> dependencies = new HashSet<DDLAction>();
            for (DDLAction depAction : dropKeys) {
                key = depAction.getKey();
                if ((!key.getTable().equals(action.getTable().getName()) || !key.getForeignField().equals(action.getField().getName())) && (!key.getDomesticTable().equals(action.getTable().getName()) || !key.getField().equals(action.getField().getName()))) continue;
                dependencies.add(depAction);
            }
            if (dependencies.size() == 0) {
                roots.add(action);
                continue;
            }
            deps.put(action, dependencies);
        }
        for (DDLAction action : changeColumns) {
            HashSet<DDLAction> dependencies = new HashSet<DDLAction>();
            for (DDLAction depAction : dropKeys) {
                key = depAction.getKey();
                if ((!key.getTable().equals(action.getTable().getName()) || !key.getForeignField().equals(action.getField().getName())) && (!key.getDomesticTable().equals(action.getTable().getName()) || !key.getField().equals(action.getField().getName()))) continue;
                dependencies.add(depAction);
            }
            for (DDLAction depAction : dropColumns) {
                if ((!depAction.getTable().equals(action.getTable()) || !depAction.getField().equals(action.getField())) && (!depAction.getTable().equals(action.getTable()) || !depAction.getField().equals(action.getOldField()))) continue;
                dependencies.add(depAction);
            }
            if (dependencies.size() == 0) {
                roots.add(action);
                continue;
            }
            deps.put(action, dependencies);
        }
        for (DDLAction action : drops) {
            HashSet<DDLAction> dependencies = new HashSet<DDLAction>();
            for (DDLAction depAction : dropKeys) {
                key = depAction.getKey();
                if (!key.getTable().equals(action.getTable().getName()) && !key.getDomesticTable().equals(action.getTable().getName())) continue;
                dependencies.add(depAction);
            }
            for (DDLAction depAction : dropColumns) {
                if (!depAction.getTable().equals(action.getTable())) continue;
                dependencies.add(depAction);
            }
            for (DDLAction depAction : changeColumns) {
                if (!depAction.getTable().equals(action.getTable())) continue;
                dependencies.add(depAction);
            }
            if (dependencies.size() == 0) {
                roots.add(action);
                continue;
            }
            deps.put(action, dependencies);
        }
        for (DDLAction action : creates) {
            HashSet<DDLAction> dependencies = new HashSet<DDLAction>();
            for (DDLForeignKey key2 : action.getTable().getForeignKeys()) {
                for (DDLAction depAction : creates) {
                    if (depAction == action || !depAction.getTable().getName().equals(key2.getTable())) continue;
                    dependencies.add(depAction);
                }
                for (DDLAction depAction : addColumns) {
                    if (!depAction.getTable().getName().equals(key2.getTable()) || !depAction.getField().getName().equals(key2.getForeignField())) continue;
                    dependencies.add(depAction);
                }
                for (DDLAction depAction : changeColumns) {
                    if (!depAction.getTable().getName().equals(key2.getTable()) || !depAction.getField().getName().equals(key2.getForeignField())) continue;
                    dependencies.add(depAction);
                }
            }
            if (dependencies.size() == 0) {
                roots.add(action);
                continue;
            }
            deps.put(action, dependencies);
        }
        for (DDLAction action : addColumns) {
            HashSet<DDLAction> dependencies = new HashSet<DDLAction>();
            for (DDLAction depAction : creates) {
                if (!depAction.getTable().equals(action.getTable())) continue;
                dependencies.add(depAction);
            }
            if (dependencies.size() == 0) {
                roots.add(action);
                continue;
            }
            deps.put(action, dependencies);
        }
        for (DDLAction action : addKeys) {
            HashSet<DDLAction> dependencies = new HashSet<DDLAction>();
            DDLForeignKey key3 = action.getKey();
            for (DDLAction depAction : creates) {
                if (!depAction.getTable().getName().equals(key3.getTable()) && !depAction.getTable().getName().equals(key3.getDomesticTable())) continue;
                dependencies.add(depAction);
            }
            for (DDLAction depAction : addColumns) {
                if ((!depAction.getTable().getName().equals(key3.getTable()) || !depAction.getField().getName().equals(key3.getForeignField())) && (!depAction.getTable().getName().equals(key3.getDomesticTable()) || !depAction.getField().getName().equals(key3.getField()))) continue;
                dependencies.add(depAction);
            }
            for (DDLAction depAction : changeColumns) {
                if ((!depAction.getTable().getName().equals(key3.getTable()) || !depAction.getField().getName().equals(key3.getForeignField())) && (!depAction.getTable().getName().equals(key3.getDomesticTable()) || !depAction.getField().getName().equals(key3.getField()))) continue;
                dependencies.add(depAction);
            }
            if (dependencies.size() == 0) {
                roots.add(action);
                continue;
            }
            deps.put(action, dependencies);
        }
        for (DDLAction action : createIndexes) {
            HashSet<DDLAction> dependencies = new HashSet<DDLAction>();
            DDLIndex index = action.getIndex();
            List indexFieldNames = Stream.of(index.getFields()).map(DDLIndexField::getFieldName).collect(Collectors.toList());
            for (DDLAction depAction : creates) {
                if (!depAction.getTable().getName().equals(index.getTable())) continue;
                dependencies.add(depAction);
            }
            for (DDLAction depAction : addColumns) {
                if (!depAction.getTable().getName().equals(index.getTable()) && !indexFieldNames.contains(depAction.getField().getName())) continue;
                dependencies.add(depAction);
            }
            for (DDLAction depAction : changeColumns) {
                if (!depAction.getTable().getName().equals(index.getTable()) && !indexFieldNames.contains(depAction.getField().getName())) continue;
                dependencies.add(depAction);
            }
            if (dependencies.size() == 0) {
                roots.add(action);
                continue;
            }
            deps.put(action, dependencies);
        }
    }

    private static boolean containsField(Iterable<DDLField> fields, final String fieldName) {
        return Iterables.tryFind(fields, (Predicate)new Predicate<DDLField>(){

            public boolean apply(DDLField field) {
                return field.getName().equalsIgnoreCase(fieldName);
            }
        }).isPresent();
    }

    private static DDLAction createColumnAlterAction(DDLTable table, DDLField oldField, DDLField field) {
        DDLAction action = new DDLAction(DDLActionType.ALTER_CHANGE_COLUMN);
        action.setTable(table);
        action.setField(field);
        action.setOldField(oldField);
        return action;
    }

    static {
        try {
            DEFAULT_MYSQL_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("0000-00-00 00:00:00").getTime();
        }
        catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }
}

