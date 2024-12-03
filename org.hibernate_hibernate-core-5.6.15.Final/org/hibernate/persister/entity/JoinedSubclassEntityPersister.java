/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.persister.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.DynamicFilterAliasGenerator;
import org.hibernate.internal.FilterAliasGenerator;
import org.hibernate.internal.util.MarkerObject;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Formula;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.MappedSuperclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.sql.CaseFragment;
import org.hibernate.sql.Insert;
import org.hibernate.sql.SelectFragment;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class JoinedSubclassEntityPersister
extends AbstractEntityPersister {
    private static final Logger log = Logger.getLogger(JoinedSubclassEntityPersister.class);
    private static final String IMPLICIT_DISCRIMINATOR_ALIAS = "clazz_";
    private static final Object NULL_DISCRIMINATOR = new MarkerObject("<null discriminator>");
    private static final Object NOT_NULL_DISCRIMINATOR = new MarkerObject("<not null discriminator>");
    private final int tableSpan;
    private final String[] tableNames;
    private final String[] naturalOrderTableNames;
    private final String[][] tableKeyColumns;
    private final String[][] tableKeyColumnReaders;
    private final String[][] tableKeyColumnReaderTemplates;
    private final String[][] naturalOrderTableKeyColumns;
    private final String[][] naturalOrderTableKeyColumnReaders;
    private final String[][] naturalOrderTableKeyColumnReaderTemplates;
    private final boolean[] naturalOrderCascadeDeleteEnabled;
    private final String[] spaces;
    private final String[] subclassClosure;
    private final String[] subclassTableNameClosure;
    private final String[][] subclassTableKeyColumnClosure;
    private final boolean[] isClassOrSuperclassTable;
    private final int[] naturalOrderPropertyTableNumbers;
    private final int[] propertyTableNumbers;
    private final int[] subclassPropertyTableNumberClosure;
    private final int[] subclassColumnTableNumberClosure;
    private final int[] subclassFormulaTableNumberClosure;
    private final boolean[] subclassTableSequentialSelect;
    private final boolean[] subclassTableIsLazyClosure;
    private final boolean[] isInverseSubclassTable;
    private final boolean[] isNullableSubclassTable;
    private final Map<Object, String> subclassesByDiscriminatorValue = new HashMap<Object, String>();
    private final String[] discriminatorValues;
    private final String[] notNullColumnNames;
    private final int[] notNullColumnTableNumbers;
    private final String[] constraintOrderedTableNames;
    private final String[][] constraintOrderedKeyColumnNames;
    private final Object discriminatorValue;
    private final String discriminatorSQLString;
    private final DiscriminatorType discriminatorType;
    private final String explicitDiscriminatorColumnName;
    private final String discriminatorAlias;
    private final int coreTableSpan;
    private final boolean[] isNullableTable;
    private final boolean[] isInverseTable;
    private final String[][] subclassNamesBySubclassTable;

    public JoinedSubclassEntityPersister(PersistentClass persistentClass, EntityDataAccess cacheAccessStrategy, NaturalIdDataAccess naturalIdRegionAccessStrategy, PersisterCreationContext creationContext) throws HibernateException {
        super(persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, creationContext);
        int k;
        Iterator<Selectable> cItr;
        SessionFactoryImplementor factory = creationContext.getSessionFactory();
        Database database = creationContext.getMetadata().getDatabase();
        if (persistentClass.isPolymorphic()) {
            Value discriminatorMapping = persistentClass.getDiscriminator();
            if (discriminatorMapping != null) {
                log.debug((Object)"Encountered explicit discriminator mapping for joined inheritance");
                Selectable selectable = discriminatorMapping.getColumnIterator().next();
                if (Formula.class.isInstance(selectable)) {
                    throw new MappingException("Discriminator formulas on joined inheritance hierarchies not supported at this time");
                }
                Column column = (Column)selectable;
                this.explicitDiscriminatorColumnName = column.getQuotedName(factory.getDialect());
                this.discriminatorAlias = column.getAlias(factory.getDialect(), persistentClass.getRootTable());
                this.discriminatorType = (DiscriminatorType)persistentClass.getDiscriminator().getType();
                if (persistentClass.isDiscriminatorValueNull()) {
                    this.discriminatorValue = NULL_DISCRIMINATOR;
                    this.discriminatorSQLString = "null";
                } else if (persistentClass.isDiscriminatorValueNotNull()) {
                    this.discriminatorValue = NOT_NULL_DISCRIMINATOR;
                    this.discriminatorSQLString = "not null";
                } else {
                    try {
                        this.discriminatorValue = this.discriminatorType.stringToObject(persistentClass.getDiscriminatorValue());
                        this.discriminatorSQLString = this.discriminatorType.objectToSQLString(this.discriminatorValue, factory.getDialect());
                    }
                    catch (ClassCastException cce) {
                        throw new MappingException("Illegal discriminator type: " + this.discriminatorType.getName());
                    }
                    catch (Exception e) {
                        throw new MappingException("Could not format discriminator value to SQL string", e);
                    }
                }
            } else {
                this.explicitDiscriminatorColumnName = null;
                this.discriminatorAlias = IMPLICIT_DISCRIMINATOR_ALIAS;
                this.discriminatorType = StandardBasicTypes.INTEGER;
                try {
                    this.discriminatorValue = persistentClass.getSubclassId();
                    this.discriminatorSQLString = this.discriminatorValue.toString();
                }
                catch (Exception e) {
                    throw new MappingException("Could not format discriminator value to SQL string", e);
                }
            }
        } else {
            this.explicitDiscriminatorColumnName = null;
            this.discriminatorAlias = IMPLICIT_DISCRIMINATOR_ALIAS;
            this.discriminatorType = StandardBasicTypes.INTEGER;
            this.discriminatorValue = null;
            this.discriminatorSQLString = null;
        }
        if (this.optimisticLockStyle().isAllOrDirty()) {
            throw new MappingException("optimistic-lock=all|dirty not supported for joined-subclass mappings [" + this.getEntityName() + "]");
        }
        int idColumnSpan = this.getIdentifierColumnSpan();
        ArrayList<String> tableNames = new ArrayList<String>();
        ArrayList<String[]> keyColumns = new ArrayList<String[]>();
        ArrayList<String[]> keyColumnReaders = new ArrayList<String[]>();
        ArrayList<String[]> keyColumnReaderTemplates = new ArrayList<String[]>();
        ArrayList<Boolean> cascadeDeletes = new ArrayList<Boolean>();
        Iterator tItr = persistentClass.getTableClosureIterator();
        Iterator kItr = persistentClass.getKeyClosureIterator();
        while (tItr.hasNext()) {
            Table table = (Table)tItr.next();
            KeyValue key = (KeyValue)kItr.next();
            String tableName = this.determineTableName(table);
            tableNames.add(tableName);
            String[] keyCols = new String[idColumnSpan];
            String[] keyColReaders = new String[idColumnSpan];
            String[] keyColReaderTemplates = new String[idColumnSpan];
            Iterator<Selectable> cItr2 = key.getColumnIterator();
            for (int k2 = 0; k2 < idColumnSpan; ++k2) {
                Column column = (Column)cItr2.next();
                keyCols[k2] = column.getQuotedName(factory.getDialect());
                keyColReaders[k2] = column.getReadExpr(factory.getDialect());
                keyColReaderTemplates[k2] = column.getTemplate(factory.getDialect(), factory.getSqlFunctionRegistry());
            }
            keyColumns.add(keyCols);
            keyColumnReaders.add(keyColReaders);
            keyColumnReaderTemplates.add(keyColReaderTemplates);
            cascadeDeletes.add(key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete());
        }
        this.coreTableSpan = tableNames.size();
        this.tableSpan = persistentClass.getJoinClosureSpan() + this.coreTableSpan;
        this.isNullableTable = new boolean[this.tableSpan];
        this.isInverseTable = new boolean[this.tableSpan];
        Iterator joinItr = persistentClass.getJoinClosureIterator();
        int tableIndex = 0;
        while (joinItr.hasNext()) {
            Join join = (Join)joinItr.next();
            this.isNullableTable[tableIndex] = join.isOptional();
            this.isInverseTable[tableIndex] = join.isInverse();
            Table table = join.getTable();
            String tableName = this.determineTableName(table);
            tableNames.add(tableName);
            KeyValue key = join.getKey();
            int joinIdColumnSpan = key.getColumnSpan();
            String[] keyCols = new String[joinIdColumnSpan];
            String[] keyColReaders = new String[joinIdColumnSpan];
            String[] keyColReaderTemplates = new String[joinIdColumnSpan];
            cItr = key.getColumnIterator();
            for (k = 0; k < joinIdColumnSpan; ++k) {
                Column column = (Column)cItr.next();
                keyCols[k] = column.getQuotedName(factory.getDialect());
                keyColReaders[k] = column.getReadExpr(factory.getDialect());
                keyColReaderTemplates[k] = column.getTemplate(factory.getDialect(), factory.getSqlFunctionRegistry());
            }
            keyColumns.add(keyCols);
            keyColumnReaders.add(keyColReaders);
            keyColumnReaderTemplates.add(keyColReaderTemplates);
            cascadeDeletes.add(key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete());
            ++tableIndex;
        }
        this.naturalOrderTableNames = ArrayHelper.toStringArray(tableNames);
        this.naturalOrderTableKeyColumns = ArrayHelper.to2DStringArray(keyColumns);
        this.naturalOrderTableKeyColumnReaders = ArrayHelper.to2DStringArray(keyColumnReaders);
        this.naturalOrderTableKeyColumnReaderTemplates = ArrayHelper.to2DStringArray(keyColumnReaderTemplates);
        this.naturalOrderCascadeDeleteEnabled = ArrayHelper.toBooleanArray(cascadeDeletes);
        ArrayList<String> subclassTableNames = new ArrayList<String>();
        ArrayList<Boolean> isConcretes = new ArrayList<Boolean>();
        ArrayList<Boolean> isDeferreds = new ArrayList<Boolean>();
        ArrayList<Boolean> isLazies = new ArrayList<Boolean>();
        ArrayList<Boolean> isInverses = new ArrayList<Boolean>();
        ArrayList<Boolean> isNullables = new ArrayList<Boolean>();
        keyColumns = new ArrayList();
        tItr = persistentClass.getSubclassTableClosureIterator();
        while (tItr.hasNext()) {
            Table tab = (Table)tItr.next();
            isConcretes.add(persistentClass.isClassOrSuperclassTable(tab));
            isDeferreds.add(Boolean.FALSE);
            isLazies.add(Boolean.FALSE);
            isInverses.add(Boolean.FALSE);
            isNullables.add(Boolean.FALSE);
            String tableName = this.determineTableName(tab);
            subclassTableNames.add(tableName);
            String[] key = new String[idColumnSpan];
            cItr = tab.getPrimaryKey().getColumnIterator();
            for (k = 0; k < idColumnSpan; ++k) {
                key[k] = ((Column)cItr.next()).getQuotedName(factory.getDialect());
            }
            keyColumns.add(key);
        }
        joinItr = persistentClass.getSubclassJoinClosureIterator();
        while (joinItr.hasNext()) {
            Join join = (Join)joinItr.next();
            Table joinTable = join.getTable();
            isConcretes.add(persistentClass.isClassOrSuperclassTable(joinTable));
            isDeferreds.add(join.isSequentialSelect());
            isInverses.add(join.isInverse());
            isNullables.add(join.isOptional());
            isLazies.add(join.isLazy());
            String joinTableName = this.determineTableName(joinTable);
            subclassTableNames.add(joinTableName);
            String[] key = new String[idColumnSpan];
            Iterator<Column> citer = joinTable.getPrimaryKey().getColumnIterator();
            for (int k3 = 0; k3 < idColumnSpan; ++k3) {
                key[k3] = citer.next().getQuotedName(factory.getDialect());
            }
            keyColumns.add(key);
        }
        String[] naturalOrderSubclassTableNameClosure = ArrayHelper.toStringArray(subclassTableNames);
        String[][] naturalOrderSubclassTableKeyColumnClosure = ArrayHelper.to2DStringArray(keyColumns);
        this.isClassOrSuperclassTable = ArrayHelper.toBooleanArray(isConcretes);
        this.subclassTableSequentialSelect = ArrayHelper.toBooleanArray(isDeferreds);
        this.subclassTableIsLazyClosure = ArrayHelper.toBooleanArray(isLazies);
        this.isInverseSubclassTable = ArrayHelper.toBooleanArray(isInverses);
        this.isNullableSubclassTable = ArrayHelper.toBooleanArray(isNullables);
        this.constraintOrderedTableNames = new String[naturalOrderSubclassTableNameClosure.length];
        this.constraintOrderedKeyColumnNames = new String[naturalOrderSubclassTableNameClosure.length][];
        int currentPosition = 0;
        int i = naturalOrderSubclassTableNameClosure.length - 1;
        while (i >= 0) {
            this.constraintOrderedTableNames[currentPosition] = naturalOrderSubclassTableNameClosure[i];
            this.constraintOrderedKeyColumnNames[currentPosition] = naturalOrderSubclassTableKeyColumnClosure[i];
            --i;
            ++currentPosition;
        }
        this.tableNames = JoinedSubclassEntityPersister.reverse(this.naturalOrderTableNames, this.coreTableSpan);
        this.tableKeyColumns = JoinedSubclassEntityPersister.reverse(this.naturalOrderTableKeyColumns, this.coreTableSpan);
        this.tableKeyColumnReaders = JoinedSubclassEntityPersister.reverse(this.naturalOrderTableKeyColumnReaders, this.coreTableSpan);
        this.tableKeyColumnReaderTemplates = JoinedSubclassEntityPersister.reverse(this.naturalOrderTableKeyColumnReaderTemplates, this.coreTableSpan);
        this.subclassTableNameClosure = JoinedSubclassEntityPersister.reverse(naturalOrderSubclassTableNameClosure, this.coreTableSpan);
        this.subclassTableKeyColumnClosure = JoinedSubclassEntityPersister.reverse(naturalOrderSubclassTableKeyColumnClosure, this.coreTableSpan);
        this.spaces = ArrayHelper.join(this.tableNames, ArrayHelper.toStringArray(persistentClass.getSynchronizedTables()));
        this.customSQLInsert = new String[this.tableSpan];
        this.customSQLUpdate = new String[this.tableSpan];
        this.customSQLDelete = new String[this.tableSpan];
        this.insertCallable = new boolean[this.tableSpan];
        this.updateCallable = new boolean[this.tableSpan];
        this.deleteCallable = new boolean[this.tableSpan];
        this.insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[this.tableSpan];
        this.updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[this.tableSpan];
        this.deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[this.tableSpan];
        int jk = this.coreTableSpan - 1;
        for (PersistentClass pc = persistentClass; pc != null; pc = pc.getSuperclass()) {
            this.isNullableTable[jk] = false;
            this.isInverseTable[jk] = false;
            this.customSQLInsert[jk] = pc.getCustomSQLInsert();
            this.insertCallable[jk] = this.customSQLInsert[jk] != null && pc.isCustomInsertCallable();
            this.insertResultCheckStyles[jk] = pc.getCustomSQLInsertCheckStyle() == null ? ExecuteUpdateResultCheckStyle.determineDefault(this.customSQLInsert[jk], this.insertCallable[jk]) : pc.getCustomSQLInsertCheckStyle();
            this.customSQLUpdate[jk] = pc.getCustomSQLUpdate();
            this.updateCallable[jk] = this.customSQLUpdate[jk] != null && pc.isCustomUpdateCallable();
            this.updateResultCheckStyles[jk] = pc.getCustomSQLUpdateCheckStyle() == null ? ExecuteUpdateResultCheckStyle.determineDefault(this.customSQLUpdate[jk], this.updateCallable[jk]) : pc.getCustomSQLUpdateCheckStyle();
            this.customSQLDelete[jk] = pc.getCustomSQLDelete();
            this.deleteCallable[jk] = this.customSQLDelete[jk] != null && pc.isCustomDeleteCallable();
            this.deleteResultCheckStyles[jk] = pc.getCustomSQLDeleteCheckStyle() == null ? ExecuteUpdateResultCheckStyle.determineDefault(this.customSQLDelete[jk], this.deleteCallable[jk]) : pc.getCustomSQLDeleteCheckStyle();
            --jk;
        }
        if (jk != -1) {
            throw new AssertionFailure("Tablespan does not match height of joined-subclass hiearchy.");
        }
        joinItr = persistentClass.getJoinClosureIterator();
        int j = this.coreTableSpan;
        while (joinItr.hasNext()) {
            Join join = (Join)joinItr.next();
            this.isInverseTable[j] = join.isInverse();
            this.isNullableTable[j] = join.isOptional();
            this.customSQLInsert[j] = join.getCustomSQLInsert();
            this.insertCallable[j] = this.customSQLInsert[j] != null && join.isCustomInsertCallable();
            this.insertResultCheckStyles[j] = join.getCustomSQLInsertCheckStyle() == null ? ExecuteUpdateResultCheckStyle.determineDefault(this.customSQLInsert[j], this.insertCallable[j]) : join.getCustomSQLInsertCheckStyle();
            this.customSQLUpdate[j] = join.getCustomSQLUpdate();
            this.updateCallable[j] = this.customSQLUpdate[j] != null && join.isCustomUpdateCallable();
            this.updateResultCheckStyles[j] = join.getCustomSQLUpdateCheckStyle() == null ? ExecuteUpdateResultCheckStyle.determineDefault(this.customSQLUpdate[j], this.updateCallable[j]) : join.getCustomSQLUpdateCheckStyle();
            this.customSQLDelete[j] = join.getCustomSQLDelete();
            this.deleteCallable[j] = this.customSQLDelete[j] != null && join.isCustomDeleteCallable();
            this.deleteResultCheckStyles[j] = join.getCustomSQLDeleteCheckStyle() == null ? ExecuteUpdateResultCheckStyle.determineDefault(this.customSQLDelete[j], this.deleteCallable[j]) : join.getCustomSQLDeleteCheckStyle();
            ++j;
        }
        int hydrateSpan = this.getPropertySpan();
        this.naturalOrderPropertyTableNumbers = new int[hydrateSpan];
        this.propertyTableNumbers = new int[hydrateSpan];
        Iterator iter = persistentClass.getPropertyClosureIterator();
        int i2 = 0;
        while (iter.hasNext()) {
            Property prop = (Property)iter.next();
            String tabname = prop.getValue().getTable().getQualifiedName(factory.getSqlStringGenerationContext());
            this.propertyTableNumbers[i2] = JoinedSubclassEntityPersister.getTableId(tabname, this.tableNames);
            this.naturalOrderPropertyTableNumbers[i2] = JoinedSubclassEntityPersister.getTableId(tabname, this.naturalOrderTableNames);
            ++i2;
        }
        ArrayList<Integer> columnTableNumbers = new ArrayList<Integer>();
        ArrayList<Integer> formulaTableNumbers = new ArrayList<Integer>();
        ArrayList<Integer> propTableNumbers = new ArrayList<Integer>();
        iter = persistentClass.getSubclassPropertyClosureIterator();
        while (iter.hasNext()) {
            Property prop = (Property)iter.next();
            Table tab = prop.getValue().getTable();
            String tabname = tab.getQualifiedName(factory.getSqlStringGenerationContext());
            Integer tabnum = JoinedSubclassEntityPersister.getTableId(tabname, this.subclassTableNameClosure);
            propTableNumbers.add(tabnum);
            Iterator citer = prop.getColumnIterator();
            while (citer.hasNext()) {
                Selectable thing = (Selectable)citer.next();
                if (thing.isFormula()) {
                    formulaTableNumbers.add(tabnum);
                    continue;
                }
                columnTableNumbers.add(tabnum);
            }
        }
        this.subclassColumnTableNumberClosure = ArrayHelper.toIntArray(columnTableNumbers);
        this.subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propTableNumbers);
        this.subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formulaTableNumbers);
        int subclassSpan = persistentClass.getSubclassSpan() + 1;
        this.subclassClosure = new String[subclassSpan];
        this.subclassClosure[subclassSpan - 1] = this.getEntityName();
        if (persistentClass.isPolymorphic()) {
            int id;
            this.subclassesByDiscriminatorValue.put(this.discriminatorValue, this.getEntityName());
            this.discriminatorValues = new String[subclassSpan];
            this.discriminatorValues[subclassSpan - 1] = this.discriminatorSQLString;
            this.notNullColumnTableNumbers = new int[subclassSpan];
            this.notNullColumnTableNumbers[subclassSpan - 1] = id = JoinedSubclassEntityPersister.getTableId(persistentClass.getTable().getQualifiedName(factory.getSqlStringGenerationContext()), this.subclassTableNameClosure);
            this.notNullColumnNames = new String[subclassSpan];
            this.notNullColumnNames[subclassSpan - 1] = this.subclassTableKeyColumnClosure[id][0];
        } else {
            this.discriminatorValues = null;
            this.notNullColumnTableNumbers = null;
            this.notNullColumnNames = null;
        }
        iter = persistentClass.getSubclassIterator();
        int k4 = 0;
        while (iter.hasNext()) {
            Subclass sc = (Subclass)iter.next();
            this.subclassClosure[k4] = sc.getEntityName();
            try {
                if (persistentClass.isPolymorphic()) {
                    int id;
                    Object discriminatorValue;
                    if (this.explicitDiscriminatorColumnName != null) {
                        if (sc.isDiscriminatorValueNull()) {
                            discriminatorValue = NULL_DISCRIMINATOR;
                        } else if (sc.isDiscriminatorValueNotNull()) {
                            discriminatorValue = NOT_NULL_DISCRIMINATOR;
                        } else {
                            try {
                                discriminatorValue = this.discriminatorType.stringToObject(sc.getDiscriminatorValue());
                            }
                            catch (ClassCastException cce) {
                                throw new MappingException("Illegal discriminator type: " + this.discriminatorType.getName());
                            }
                            catch (Exception e) {
                                throw new MappingException("Could not format discriminator value to SQL string", e);
                            }
                        }
                    } else {
                        discriminatorValue = sc.getSubclassId();
                    }
                    this.subclassesByDiscriminatorValue.put(discriminatorValue, sc.getEntityName());
                    this.discriminatorValues[k4] = discriminatorValue.toString();
                    this.notNullColumnTableNumbers[k4] = id = JoinedSubclassEntityPersister.getTableId(sc.getTable().getQualifiedName(factory.getSqlStringGenerationContext()), this.subclassTableNameClosure);
                    this.notNullColumnNames[k4] = this.subclassTableKeyColumnClosure[id][0];
                }
            }
            catch (Exception e) {
                throw new MappingException("Error parsing discriminator value", e);
            }
            ++k4;
        }
        this.subclassNamesBySubclassTable = this.buildSubclassNamesBySubclassTableMapping(persistentClass, factory);
        this.initSubclassPropertyAliasesMap(persistentClass);
        this.postConstruct(creationContext.getMetadata());
    }

    private String[][] buildSubclassNamesBySubclassTableMapping(PersistentClass persistentClass, SessionFactoryImplementor factory) {
        int numberOfSubclassTables = this.subclassTableNameClosure.length - this.coreTableSpan;
        if (numberOfSubclassTables == 0) {
            return new String[0][];
        }
        String[][] mapping = new String[numberOfSubclassTables][];
        this.processPersistentClassHierarchy(persistentClass, true, factory, mapping);
        return mapping;
    }

    private Set<String> processPersistentClassHierarchy(PersistentClass persistentClass, boolean isBase, SessionFactoryImplementor factory, String[][] mapping) {
        HashSet<String> classNames = new HashSet<String>();
        Iterator itr = persistentClass.getDirectSubclasses();
        while (itr.hasNext()) {
            Subclass subclass = (Subclass)itr.next();
            Set<String> subclassSubclassNames = this.processPersistentClassHierarchy(subclass, false, factory, mapping);
            classNames.addAll(subclassSubclassNames);
        }
        classNames.add(persistentClass.getEntityName());
        if (!isBase) {
            for (MappedSuperclass msc = persistentClass.getSuperMappedSuperclass(); msc != null; msc = msc.getSuperMappedSuperclass()) {
                classNames.add(msc.getMappedClass().getName());
            }
            this.associateSubclassNamesToSubclassTableIndexes(persistentClass, classNames, mapping, factory);
        }
        return classNames;
    }

    private void associateSubclassNamesToSubclassTableIndexes(PersistentClass persistentClass, Set<String> classNames, String[][] mapping, SessionFactoryImplementor factory) {
        String tableName = persistentClass.getTable().getQualifiedName(factory.getSqlStringGenerationContext());
        this.associateSubclassNamesToSubclassTableIndex(tableName, classNames, mapping);
        Iterator itr = persistentClass.getJoinIterator();
        while (itr.hasNext()) {
            Join join = (Join)itr.next();
            String secondaryTableName = join.getTable().getQualifiedName(factory.getSqlStringGenerationContext());
            this.associateSubclassNamesToSubclassTableIndex(secondaryTableName, classNames, mapping);
        }
    }

    private void associateSubclassNamesToSubclassTableIndex(String tableName, Set<String> classNames, String[][] mapping) {
        boolean found = false;
        for (int i = 0; i < this.subclassTableNameClosure.length; ++i) {
            if (!this.subclassTableNameClosure[i].equals(tableName)) continue;
            found = true;
            int index = i - this.coreTableSpan;
            if (index < 0 || index >= mapping.length) {
                throw new IllegalStateException(String.format("Encountered 'subclass table index' [%s] was outside expected range ( [%s] < i < [%s] )", index, 0, mapping.length));
            }
            mapping[index] = classNames.toArray(new String[classNames.size()]);
            break;
        }
        if (!found) {
            throw new IllegalStateException(String.format("Was unable to locate subclass table [%s] in 'subclassTableNameClosure'", tableName));
        }
    }

    @Override
    public boolean isNullableTable(int j) {
        return this.isNullableTable[j];
    }

    @Override
    public boolean isInverseTable(int j) {
        return this.isInverseTable[j];
    }

    @Override
    protected boolean isSubclassTableSequentialSelect(int j) {
        return this.subclassTableSequentialSelect[j] && !this.isClassOrSuperclassTable[j];
    }

    @Override
    public String getSubclassPropertyTableName(int i) {
        return this.subclassTableNameClosure[this.subclassPropertyTableNumberClosure[i]];
    }

    @Override
    protected boolean isInverseSubclassTable(int j) {
        return this.isInverseSubclassTable[j];
    }

    @Override
    protected boolean isNullableSubclassTable(int j) {
        return this.isNullableSubclassTable[j];
    }

    @Override
    public Type getDiscriminatorType() {
        return this.discriminatorType;
    }

    @Override
    public Object getDiscriminatorValue() {
        return this.discriminatorValue;
    }

    @Override
    public String getDiscriminatorSQLValue() {
        return this.discriminatorSQLString;
    }

    @Override
    public String getDiscriminatorColumnName() {
        return this.explicitDiscriminatorColumnName == null ? super.getDiscriminatorColumnName() : this.explicitDiscriminatorColumnName;
    }

    @Override
    public String getDiscriminatorColumnReaders() {
        return this.getDiscriminatorColumnName();
    }

    @Override
    public String getDiscriminatorColumnReaderTemplate() {
        return this.getDiscriminatorColumnName();
    }

    @Override
    public String getDiscriminatorAlias() {
        return this.discriminatorAlias;
    }

    @Override
    public String getSubclassForDiscriminatorValue(Object value) {
        if (value == null) {
            return this.subclassesByDiscriminatorValue.get(NULL_DISCRIMINATOR);
        }
        String result = this.subclassesByDiscriminatorValue.get(value);
        if (result == null) {
            result = this.subclassesByDiscriminatorValue.get(NOT_NULL_DISCRIMINATOR);
        }
        return result;
    }

    @Override
    protected void addDiscriminatorToInsert(Insert insert) {
        if (this.explicitDiscriminatorColumnName != null) {
            insert.addColumn(this.explicitDiscriminatorColumnName, this.getDiscriminatorSQLValue());
        }
    }

    @Override
    public Serializable[] getPropertySpaces() {
        return this.spaces;
    }

    @Override
    public String getTableName(int j) {
        return this.naturalOrderTableNames[j];
    }

    @Override
    public String[] getKeyColumns(int j) {
        return this.naturalOrderTableKeyColumns[j];
    }

    @Override
    public boolean isTableCascadeDeleteEnabled(int j) {
        return this.naturalOrderCascadeDeleteEnabled[j];
    }

    @Override
    public boolean isPropertyOfTable(int property, int j) {
        return this.naturalOrderPropertyTableNumbers[property] == j;
    }

    private static String[] reverse(String[] objects, int n) {
        int i;
        int size = objects.length;
        String[] temp = new String[size];
        for (i = 0; i < n; ++i) {
            temp[i] = objects[n - i - 1];
        }
        for (i = n; i < size; ++i) {
            temp[i] = objects[i];
        }
        return temp;
    }

    private static String[][] reverse(String[][] objects, int n) {
        int i;
        int size = objects.length;
        String[][] temp = new String[size][];
        for (i = 0; i < n; ++i) {
            temp[i] = objects[n - i - 1];
        }
        for (i = n; i < size; ++i) {
            temp[i] = objects[i];
        }
        return temp;
    }

    @Override
    public String fromTableFragment(String alias) {
        return this.getTableName() + ' ' + alias;
    }

    @Override
    public String getTableName() {
        return this.tableNames[0];
    }

    @Override
    public void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
        if (this.hasSubclasses()) {
            if (this.explicitDiscriminatorColumnName == null) {
                select.setExtraSelectList(this.discriminatorFragment(name), this.getDiscriminatorAlias());
            } else {
                if (this.getEntityMetamodel().getSuperclass() != null) {
                    name = JoinedSubclassEntityPersister.generateTableAlias(name, this.getRootHierarchyClassTableIndex());
                }
                select.addColumn(name, this.explicitDiscriminatorColumnName, this.discriminatorAlias);
            }
        }
    }

    private int getRootHierarchyClassTableIndex() {
        String rootHierarchyClassTableName = this.naturalOrderTableNames[0];
        for (int i = 0; i < this.subclassTableNameClosure.length; ++i) {
            if (!this.subclassTableNameClosure[i].equals(rootHierarchyClassTableName)) continue;
            return i;
        }
        return 0;
    }

    private CaseFragment discriminatorFragment(String alias) {
        CaseFragment cases = this.getFactory().getDialect().createCaseFragment();
        for (int i = 0; i < this.discriminatorValues.length; ++i) {
            cases.addWhenColumnNotNull(JoinedSubclassEntityPersister.generateTableAlias(alias, this.notNullColumnTableNumbers[i]), this.notNullColumnNames[i], this.discriminatorValues[i]);
        }
        return cases;
    }

    @Override
    protected String filterFragment(String alias) {
        return this.hasWhere() ? " and " + this.getSQLWhereString(this.generateFilterConditionAlias(alias)) : "";
    }

    @Override
    protected String filterFragment(String alias, Set<String> treatAsDeclarations) {
        return this.filterFragment(alias);
    }

    @Override
    public String generateFilterConditionAlias(String rootAlias) {
        return JoinedSubclassEntityPersister.generateTableAlias(rootAlias, this.tableSpan - 1);
    }

    @Override
    public String[] getIdentifierColumnNames() {
        return this.tableKeyColumns[0];
    }

    @Override
    public String[] getIdentifierColumnReaderTemplates() {
        return this.tableKeyColumnReaderTemplates[0];
    }

    @Override
    public String[] getIdentifierColumnReaders() {
        return this.tableKeyColumnReaders[0];
    }

    @Override
    public String[] toColumns(String alias, String propertyName) throws QueryException {
        if ("class".equals(propertyName)) {
            if (this.explicitDiscriminatorColumnName == null) {
                return new String[]{this.discriminatorFragment(alias).toFragmentString()};
            }
            return new String[]{StringHelper.qualify(alias, this.explicitDiscriminatorColumnName)};
        }
        return super.toColumns(alias, propertyName);
    }

    @Override
    protected int[] getPropertyTableNumbersInSelect() {
        return this.propertyTableNumbers;
    }

    @Override
    protected int getSubclassPropertyTableNumber(int i) {
        return this.subclassPropertyTableNumberClosure[i];
    }

    @Override
    public int getTableSpan() {
        return this.tableSpan;
    }

    @Override
    public boolean isMultiTable() {
        return true;
    }

    @Override
    protected int[] getSubclassColumnTableNumberClosure() {
        return this.subclassColumnTableNumberClosure;
    }

    @Override
    protected int[] getSubclassFormulaTableNumberClosure() {
        return this.subclassFormulaTableNumberClosure;
    }

    @Override
    protected int[] getPropertyTableNumbers() {
        return this.naturalOrderPropertyTableNumbers;
    }

    @Override
    protected String[] getSubclassTableKeyColumns(int j) {
        return this.subclassTableKeyColumnClosure[j];
    }

    @Override
    public String getSubclassTableName(int j) {
        return this.subclassTableNameClosure[j];
    }

    @Override
    public int getSubclassTableSpan() {
        return this.subclassTableNameClosure.length;
    }

    @Override
    protected boolean isSubclassTableLazy(int j) {
        return this.subclassTableIsLazyClosure[j];
    }

    @Override
    protected boolean isClassOrSuperclassTable(int j) {
        return this.isClassOrSuperclassTable[j];
    }

    @Override
    protected boolean isSubclassTableIndicatedByTreatAsDeclarations(int subclassTableNumber, Set<String> treatAsDeclarations) {
        if (treatAsDeclarations == null || treatAsDeclarations.isEmpty()) {
            return false;
        }
        String[] inclusionSubclassNameClosure = this.getSubclassNameClosureBySubclassTable(subclassTableNumber);
        for (String subclassName : treatAsDeclarations) {
            for (String inclusionSubclassName : inclusionSubclassNameClosure) {
                if (!inclusionSubclassName.equals(subclassName)) continue;
                return true;
            }
        }
        return false;
    }

    private String[] getSubclassNameClosureBySubclassTable(int subclassTableNumber) {
        int index = subclassTableNumber - this.getTableSpan();
        if (index >= this.subclassNamesBySubclassTable.length) {
            throw new IllegalArgumentException("Given subclass table number is outside expected range [" + (this.subclassNamesBySubclassTable.length - 1) + "] as defined by subclassTableNameClosure/subclassClosure");
        }
        return this.subclassNamesBySubclassTable[index];
    }

    @Override
    public String getPropertyTableName(String propertyName) {
        Integer index = this.getEntityMetamodel().getPropertyIndexOrNull(propertyName);
        if (index == null) {
            return null;
        }
        return this.tableNames[this.propertyTableNumbers[index]];
    }

    @Override
    public String[] getConstraintOrderedTableNameClosure() {
        return this.constraintOrderedTableNames;
    }

    @Override
    public String[][] getContraintOrderedTableKeyColumnClosure() {
        return this.constraintOrderedKeyColumnNames;
    }

    @Override
    public String getRootTableName() {
        return this.naturalOrderTableNames[0];
    }

    @Override
    public String getRootTableAlias(String drivingAlias) {
        return JoinedSubclassEntityPersister.generateTableAlias(drivingAlias, JoinedSubclassEntityPersister.getTableId(this.getRootTableName(), this.tableNames));
    }

    @Override
    public Queryable.Declarer getSubclassPropertyDeclarer(String propertyPath) {
        if ("class".equals(propertyPath)) {
            return Queryable.Declarer.SUBCLASS;
        }
        return super.getSubclassPropertyDeclarer(propertyPath);
    }

    @Override
    public int determineTableNumberForColumn(String columnName) {
        int max = this.naturalOrderTableKeyColumns.length;
        for (int i = 0; i < max; ++i) {
            Object[] keyColumns = this.naturalOrderTableKeyColumns[i];
            if (!ArrayHelper.contains(keyColumns, columnName)) continue;
            return this.naturalOrderPropertyTableNumbers[i];
        }
        String[] subclassColumnNameClosure = this.getSubclassColumnClosure();
        int max2 = subclassColumnNameClosure.length;
        for (int i = 0; i < max2; ++i) {
            boolean quoted;
            boolean bl = quoted = subclassColumnNameClosure[i].startsWith("\"") && subclassColumnNameClosure[i].endsWith("\"");
            if (!(quoted ? subclassColumnNameClosure[i].equals(columnName) : subclassColumnNameClosure[i].equalsIgnoreCase(columnName))) continue;
            return this.getSubclassColumnTableNumberClosure()[i];
        }
        throw new HibernateException("Could not locate table which owns column [" + columnName + "] referenced in order-by mapping");
    }

    @Override
    public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
        return new DynamicFilterAliasGenerator(this.subclassTableNameClosure, rootAlias);
    }

    @Override
    public boolean canOmitSuperclassTableJoin() {
        return true;
    }
}

