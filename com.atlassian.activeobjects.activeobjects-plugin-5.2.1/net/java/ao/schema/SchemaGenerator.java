/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.java.ao.schema;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import net.java.ao.ActiveObjectsConfigurationException;
import net.java.ao.AnnotationDelegate;
import net.java.ao.Common;
import net.java.ao.DatabaseProvider;
import net.java.ao.ManyToMany;
import net.java.ao.OneToMany;
import net.java.ao.OneToOne;
import net.java.ao.Polymorphic;
import net.java.ao.RawEntity;
import net.java.ao.SchemaConfiguration;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Default;
import net.java.ao.schema.FieldNameConverter;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.TableNameConverter;
import net.java.ao.schema.Unique;
import net.java.ao.schema.ddl.DDLAction;
import net.java.ao.schema.ddl.DDLField;
import net.java.ao.schema.ddl.DDLForeignKey;
import net.java.ao.schema.ddl.DDLIndex;
import net.java.ao.schema.ddl.DDLTable;
import net.java.ao.schema.ddl.SQLAction;
import net.java.ao.schema.ddl.SchemaReader;
import net.java.ao.schema.index.IndexParser;
import net.java.ao.types.TypeInfo;
import net.java.ao.types.TypeManager;
import net.java.ao.types.TypeQualifiers;
import net.java.ao.util.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SchemaGenerator {
    private static final Logger logger = LoggerFactory.getLogger(SchemaGenerator.class);
    private static final Set<Integer> AUTO_INCREMENT_LEGAL_TYPES = ImmutableSet.of((Object)4, (Object)-5);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void migrate(DatabaseProvider provider, SchemaConfiguration schemaConfiguration, NameConverters nameConverters, boolean executeDestructiveUpdates, Class<? extends RawEntity<?>> ... classes) throws SQLException {
        Iterable<Iterable<SQLAction>> actionGroups = SchemaGenerator.generateImpl(provider, schemaConfiguration, nameConverters, executeDestructiveUpdates, classes);
        try (Connection conn = provider.getConnection();
             Statement stmt = conn.createStatement();){
            HashSet<String> completedStatements = new HashSet<String>();
            for (Iterable<SQLAction> actionGroup : actionGroups) {
                Iterables.addAll(completedStatements, provider.executeUpdatesForActions(stmt, actionGroup, completedStatements));
            }
        }
    }

    private static Iterable<Iterable<SQLAction>> generateImpl(final DatabaseProvider provider, SchemaConfiguration schemaConfiguration, final NameConverters nameConverters, final boolean executeDestructiveUpdates, Class<? extends RawEntity<?>> ... classes) throws SQLException {
        DDLTable[] parsedTables = SchemaGenerator.parseDDL(provider, nameConverters, classes);
        DDLTable[] readTables = SchemaReader.readSchema(provider, nameConverters, schemaConfiguration);
        Object[] actions = SchemaReader.sortTopologically(SchemaReader.diffSchema(provider.getTypeManager(), parsedTables, readTables, provider.isCaseSensitive()));
        return Iterables.transform((Iterable)Iterables.filter((Iterable)ImmutableList.copyOf((Object[])actions), (Predicate)new Predicate<DDLAction>(){

            public boolean apply(DDLAction input) {
                switch (input.getActionType()) {
                    case DROP: 
                    case ALTER_DROP_COLUMN: {
                        return executeDestructiveUpdates;
                    }
                }
                return true;
            }
        }), (Function)new Function<DDLAction, Iterable<SQLAction>>(){

            public Iterable<SQLAction> apply(DDLAction from) {
                return provider.renderAction(nameConverters, from);
            }
        });
    }

    static DDLTable[] parseDDL(DatabaseProvider provider, NameConverters nameConverters, Class<? extends RawEntity<?>> ... classes) {
        HashMap deps = new HashMap();
        LinkedHashSet roots = new LinkedHashSet();
        for (Class<? extends RawEntity<?>> cls : classes) {
            SchemaGenerator.parseDependencies(nameConverters.getFieldNameConverter(), deps, roots, cls);
        }
        ArrayList<DDLTable> parsedTables = new ArrayList<DDLTable>();
        SchemaGenerator.parseDDLRoots(provider, nameConverters, deps, roots, parsedTables);
        if (!deps.isEmpty()) {
            throw new RuntimeException("Circular dependency detected");
        }
        return parsedTables.toArray(new DDLTable[parsedTables.size()]);
    }

    private static void parseDDLRoots(DatabaseProvider provider, NameConverters nameConverters, Map<Class<? extends RawEntity<?>>, Set<Class<? extends RawEntity<?>>>> deps, Set<Class<? extends RawEntity<?>>> roots, ArrayList<DDLTable> parsedTables) {
        while (!roots.isEmpty()) {
            Class<RawEntity<?>> clazz = roots.iterator().next();
            roots.remove(clazz);
            if (clazz.getAnnotation(Polymorphic.class) == null) {
                parsedTables.add(SchemaGenerator.parseInterface(provider, nameConverters, clazz));
            }
            LinkedList toRemove = new LinkedList();
            for (Class<RawEntity<?>> clazz2 : deps.keySet()) {
                Set<Class<RawEntity<?>>> individualDeps = deps.get(clazz2);
                individualDeps.remove(clazz);
                if (!individualDeps.isEmpty()) continue;
                roots.add(clazz2);
                toRemove.add(clazz2);
            }
            for (Class<RawEntity<Object>> clazz3 : toRemove) {
                deps.remove(clazz3);
            }
        }
    }

    private static void parseDependencies(FieldNameConverter fieldConverter, Map<Class<? extends RawEntity<?>>, Set<Class<? extends RawEntity<?>>>> deps, Set<Class<? extends RawEntity<?>>> roots, Class<? extends RawEntity<?>> clazz) {
        if (deps.containsKey(clazz)) {
            return;
        }
        LinkedHashSet individualDeps = new LinkedHashSet();
        for (Method method : clazz.getMethods()) {
            Class<?> type = Common.getAttributeTypeFromMethod(method);
            SchemaGenerator.validateManyToManyAnnotation(method);
            SchemaGenerator.validateOneToOneAnnotation(method);
            SchemaGenerator.validateOneToManyAnnotation(method);
            if (fieldConverter.getName(method) == null || type == null || type.equals(clazz) || !RawEntity.class.isAssignableFrom(type) || individualDeps.contains(type)) continue;
            individualDeps.add(type);
            SchemaGenerator.addDeps(deps, clazz, individualDeps);
            SchemaGenerator.parseDependencies(fieldConverter, deps, roots, type);
        }
        if (individualDeps.size() == 0) {
            roots.add(clazz);
        } else {
            SchemaGenerator.addDeps(deps, clazz, individualDeps);
        }
    }

    private static void addDeps(Map<Class<? extends RawEntity<?>>, Set<Class<? extends RawEntity<?>>>> deps, Class<? extends RawEntity<?>> clazz, Set<Class<? extends RawEntity<?>>> individualDeps) {
        Set<Class<RawEntity<?>>> classes = deps.get(clazz);
        if (classes != null) {
            classes.addAll(individualDeps);
        } else {
            deps.put(clazz, individualDeps);
        }
    }

    private static void validateManyToManyAnnotation(Method method) {
        ManyToMany manyToMany = method.getAnnotation(ManyToMany.class);
        if (manyToMany != null) {
            Class<RawEntity<?>> throughType = manyToMany.value();
            String reverse = manyToMany.reverse();
            if (reverse.length() != 0) {
                try {
                    throughType.getMethod(reverse, new Class[0]);
                }
                catch (NoSuchMethodException exception) {
                    throw new IllegalArgumentException(method + " has a ManyToMany annotation with an invalid reverse element value. It must be the name of the corresponding getter method on the joining entity.", exception);
                }
            }
            if (manyToMany.through().length() != 0) {
                try {
                    throughType.getMethod(manyToMany.through(), new Class[0]);
                }
                catch (NoSuchMethodException exception) {
                    throw new IllegalArgumentException(method + " has a ManyToMany annotation with an invalid through element value. It must be the name of the getter method on the joining entity that refers to the remote entities.", exception);
                }
            }
        }
    }

    private static void validateOneToManyAnnotation(Method method) {
        String reverse;
        OneToMany oneToMany = method.getAnnotation(OneToMany.class);
        if (oneToMany != null && (reverse = oneToMany.reverse()).length() != 0) {
            try {
                method.getReturnType().getComponentType().getMethod(reverse, new Class[0]);
            }
            catch (NoSuchMethodException exception) {
                throw new IllegalArgumentException(method + " has a OneToMany annotation with an invalid reverse element value. It must be the name of the corresponding getter method on the related entity.", exception);
            }
        }
    }

    private static void validateOneToOneAnnotation(Method method) {
        String reverse;
        OneToOne oneToOne = method.getAnnotation(OneToOne.class);
        if (oneToOne != null && (reverse = oneToOne.reverse()).length() != 0) {
            try {
                method.getReturnType().getMethod(reverse, new Class[0]);
            }
            catch (NoSuchMethodException exception) {
                throw new IllegalArgumentException(method + " has OneToMany annotation with an invalid reverse element value. It be the name of the corresponding getter method on the related entity.", exception);
            }
        }
    }

    public static DDLTable parseInterface(DatabaseProvider provider, NameConverters nameConverters, Class<? extends RawEntity<?>> clazz) {
        String sqlName = nameConverters.getTableNameConverter().getName(clazz);
        DDLTable table = new DDLTable();
        table.setName(sqlName);
        table.setFields(SchemaGenerator.parseFields(provider, nameConverters.getFieldNameConverter(), clazz));
        table.setForeignKeys(SchemaGenerator.parseForeignKeys(nameConverters.getTableNameConverter(), nameConverters.getFieldNameConverter(), clazz));
        table.setIndexes(SchemaGenerator.parseIndexes(provider, nameConverters, clazz));
        return table;
    }

    public static DDLField[] parseFields(DatabaseProvider provider, FieldNameConverter fieldConverter, Class<? extends RawEntity<?>> clazz) {
        ArrayList<DDLField> fields = new ArrayList<DDLField>();
        LinkedList<String> attributes = new LinkedList<String>();
        for (Method method : Common.getValueFieldsMethods(clazz, fieldConverter)) {
            String attributeName = fieldConverter.getName(method);
            Class<?> type = Common.getAttributeTypeFromMethod(method);
            if (attributeName == null || type == null) continue;
            SchemaGenerator.checkIsSupportedType(method, type);
            if (attributes.contains(attributeName)) continue;
            attributes.add(attributeName);
            AnnotationDelegate annotations = Common.getAnnotationDelegate(fieldConverter, method);
            DDLField field = new DDLField();
            field.setName(attributeName);
            TypeManager typeManager = provider.getTypeManager();
            TypeInfo<?> sqlType = SchemaGenerator.getSQLTypeFromMethod(typeManager, type, method, annotations);
            field.setType(sqlType);
            field.setJdbcType(sqlType.getJdbcWriteType());
            field.setPrimaryKey(SchemaGenerator.isPrimaryKey(annotations, field));
            field.setNotNull(annotations.isAnnotationPresent(NotNull.class) || annotations.isAnnotationPresent(Unique.class) || annotations.isAnnotationPresent(PrimaryKey.class));
            field.setUnique(annotations.isAnnotationPresent(Unique.class));
            boolean isAutoIncrement = SchemaGenerator.isAutoIncrement(type, annotations, field.getType());
            field.setAutoIncrement(isAutoIncrement);
            if (!isAutoIncrement) {
                if (annotations.isAnnotationPresent(Default.class)) {
                    Object defaultValue = SchemaGenerator.convertStringDefaultValue(annotations.getAnnotation(Default.class).value(), sqlType, method);
                    if (type.isEnum() && (Integer)defaultValue > EnumUtils.size(type) - 1) {
                        throw new ActiveObjectsConfigurationException("There is no enum value of '" + type + "'for which the ordinal is " + defaultValue);
                    }
                    field.setDefaultValue(defaultValue);
                } else if (ImmutableSet.of(Short.TYPE, Float.TYPE, Integer.TYPE, Long.TYPE, Double.TYPE).contains(type)) {
                    field.setDefaultValue(SchemaGenerator.convertStringDefaultValue("0", sqlType, method));
                }
            }
            if (field.isPrimaryKey()) {
                fields.add(0, field);
            } else {
                fields.add(field);
            }
            if (!RawEntity.class.isAssignableFrom(type) || type.getAnnotation(Polymorphic.class) == null) continue;
            field.setDefaultValue(null);
            attributeName = fieldConverter.getPolyTypeName(method);
            field = new DDLField();
            field.setName(attributeName);
            field.setType(typeManager.getType(String.class, TypeQualifiers.qualifiers().stringLength(127)));
            field.setJdbcType(12);
            if (annotations.getAnnotation(NotNull.class) != null) {
                field.setNotNull(true);
            }
            fields.add(field);
        }
        return fields.toArray(new DDLField[fields.size()]);
    }

    private static void checkIsSupportedType(Method method, Class<?> type) {
        if (type.equals(Date.class)) {
            throw new ActiveObjectsConfigurationException(Date.class.getName() + " is not supported! Please use " + java.util.Date.class.getName() + " instead.").forMethod(method);
        }
    }

    private static boolean isPrimaryKey(AnnotationDelegate annotations, DDLField field) {
        boolean isPrimaryKey = annotations.isAnnotationPresent(PrimaryKey.class);
        if (isPrimaryKey && !field.getType().isAllowedAsPrimaryKey()) {
            throw new ActiveObjectsConfigurationException(PrimaryKey.class.getName() + " is not supported for type: " + field.getType());
        }
        return isPrimaryKey;
    }

    private static boolean isAutoIncrement(Class<?> type, AnnotationDelegate annotations, TypeInfo<?> dbType) {
        boolean isAutoIncrement = annotations.isAnnotationPresent(AutoIncrement.class);
        if (isAutoIncrement && (!AUTO_INCREMENT_LEGAL_TYPES.contains(dbType.getJdbcWriteType()) || type.isEnum())) {
            throw new ActiveObjectsConfigurationException(AutoIncrement.class.getName() + " is not supported for type: " + dbType);
        }
        return isAutoIncrement;
    }

    public static TypeInfo<?> getSQLTypeFromMethod(TypeManager typeManager, Class<?> type, Method method, AnnotationDelegate annotations) {
        TypeQualifiers qualifiers = TypeQualifiers.qualifiers();
        StringLength lengthAnno = annotations.getAnnotation(StringLength.class);
        if (lengthAnno != null) {
            int length = lengthAnno.value();
            if (length > 450) {
                throw new ActiveObjectsConfigurationException("@StringLength must be <= 450 or UNLIMITED").forMethod(method);
            }
            try {
                qualifiers = qualifiers.stringLength(length);
            }
            catch (ActiveObjectsConfigurationException e) {
                throw new ActiveObjectsConfigurationException(e.getMessage()).forMethod(method);
            }
        }
        return typeManager.getType(type, qualifiers);
    }

    private static DDLForeignKey[] parseForeignKeys(TableNameConverter nameConverter, FieldNameConverter fieldConverter, Class<? extends RawEntity<?>> clazz) {
        LinkedHashSet<DDLForeignKey> back = new LinkedHashSet<DDLForeignKey>();
        for (Method method : clazz.getMethods()) {
            String attributeName = fieldConverter.getName(method);
            Class<?> type = Common.getAttributeTypeFromMethod(method);
            if (type == null || attributeName == null || !RawEntity.class.isAssignableFrom(type) || type.getAnnotation(Polymorphic.class) != null) continue;
            DDLForeignKey key = new DDLForeignKey();
            key.setField(attributeName);
            key.setTable(nameConverter.getName(type));
            key.setForeignField(Common.getPrimaryKeyField(type, fieldConverter));
            key.setDomesticTable(nameConverter.getName(clazz));
            back.add(key);
        }
        return back.toArray(new DDLForeignKey[back.size()]);
    }

    @VisibleForTesting
    static DDLIndex[] parseIndexes(DatabaseProvider provider, NameConverters nameConverters, Class<? extends RawEntity<?>> clazz) {
        IndexParser indexParser = new IndexParser(provider, nameConverters, provider.getTypeManager());
        Set<DDLIndex> indexes = indexParser.parseIndexes(clazz);
        return indexes.toArray(new DDLIndex[indexes.size()]);
    }

    private static Object convertStringDefaultValue(String value, TypeInfo<?> type, Method method) {
        if (value == null) {
            return null;
        }
        if (!type.getSchemaProperties().isDefaultValueAllowed()) {
            throw new ActiveObjectsConfigurationException("Default value is not allowed for database type " + type.getSchemaProperties().getSqlTypeName());
        }
        try {
            Object ret = type.getLogicalType().parseDefault(value);
            if (ret == null) {
                throw new ActiveObjectsConfigurationException("Default value cannot be empty").forMethod(method);
            }
            return ret;
        }
        catch (IllegalArgumentException e) {
            throw new ActiveObjectsConfigurationException(e.getMessage());
        }
    }
}

