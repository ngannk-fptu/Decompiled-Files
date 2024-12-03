/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.DynamicFilterAliasGenerator;
import org.hibernate.internal.FilterAliasGenerator;
import org.hibernate.internal.util.MarkerObject;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Formula;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.sql.InFragment;
import org.hibernate.sql.Insert;
import org.hibernate.sql.SelectFragment;
import org.hibernate.type.AssociationType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.Type;

public class SingleTableEntityPersister
extends AbstractEntityPersister {
    private final int joinSpan;
    private final String[] qualifiedTableNames;
    private final boolean[] isInverseTable;
    private final boolean[] isNullableTable;
    private final String[][] keyColumnNames;
    private final boolean[] cascadeDeleteEnabled;
    private final boolean hasSequentialSelects;
    private final String[] spaces;
    private final String[] subclassClosure;
    private final String[] subclassTableNameClosure;
    private final boolean[] subclassTableIsLazyClosure;
    private final boolean[] isInverseSubclassTable;
    private final boolean[] isNullableSubclassTable;
    private final boolean[] subclassTableSequentialSelect;
    private final String[][] subclassTableKeyColumnClosure;
    private final boolean[] isClassOrSuperclassTable;
    private final boolean[] isClassOrSuperclassJoin;
    private final int[] propertyTableNumbers;
    private final int[] subclassPropertyTableNumberClosure;
    private final int[] subclassColumnTableNumberClosure;
    private final int[] subclassFormulaTableNumberClosure;
    private final Map<Object, String> subclassesByDiscriminatorValue;
    private final boolean forceDiscriminator;
    private final String discriminatorColumnName;
    private final String discriminatorColumnReaders;
    private final String discriminatorColumnReaderTemplate;
    private final String discriminatorFormula;
    private final String discriminatorFormulaTemplate;
    private final String discriminatorAlias;
    private final Type discriminatorType;
    private final Object discriminatorValue;
    private final String discriminatorSQLValue;
    private final boolean discriminatorInsertable;
    private final String[] constraintOrderedTableNames;
    private final String[][] constraintOrderedKeyColumnNames;
    private final Map<String, Integer> propertyTableNumbersByNameAndSubclass;
    private final Map<String, String> sequentialSelectStringsByEntityName;
    private static final Object NULL_DISCRIMINATOR = new MarkerObject("<null discriminator>");
    private static final Object NOT_NULL_DISCRIMINATOR = new MarkerObject("<not null discriminator>");
    private String[] fullDiscriminatorValues;

    public SingleTableEntityPersister(PersistentClass persistentClass, EntityDataAccess cacheAccessStrategy, NaturalIdDataAccess naturalIdRegionAccessStrategy, PersisterCreationContext creationContext) throws HibernateException {
        super(persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, creationContext);
        SessionFactoryImplementor factory = creationContext.getSessionFactory();
        Database database = creationContext.getMetadata().getDatabase();
        this.joinSpan = persistentClass.getJoinClosureSpan() + 1;
        this.qualifiedTableNames = new String[this.joinSpan];
        this.isInverseTable = new boolean[this.joinSpan];
        this.isNullableTable = new boolean[this.joinSpan];
        this.keyColumnNames = new String[this.joinSpan][];
        Table table = persistentClass.getRootTable();
        this.qualifiedTableNames[0] = this.determineTableName(table);
        this.isInverseTable[0] = false;
        this.isNullableTable[0] = false;
        this.keyColumnNames[0] = this.getIdentifierColumnNames();
        this.cascadeDeleteEnabled = new boolean[this.joinSpan];
        this.customSQLInsert = new String[this.joinSpan];
        this.customSQLUpdate = new String[this.joinSpan];
        this.customSQLDelete = new String[this.joinSpan];
        this.insertCallable = new boolean[this.joinSpan];
        this.updateCallable = new boolean[this.joinSpan];
        this.deleteCallable = new boolean[this.joinSpan];
        this.insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[this.joinSpan];
        this.updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[this.joinSpan];
        this.deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[this.joinSpan];
        this.customSQLInsert[0] = persistentClass.getCustomSQLInsert();
        this.insertCallable[0] = this.customSQLInsert[0] != null && persistentClass.isCustomInsertCallable();
        this.insertResultCheckStyles[0] = persistentClass.getCustomSQLInsertCheckStyle() == null ? ExecuteUpdateResultCheckStyle.determineDefault(this.customSQLInsert[0], this.insertCallable[0]) : persistentClass.getCustomSQLInsertCheckStyle();
        this.customSQLUpdate[0] = persistentClass.getCustomSQLUpdate();
        this.updateCallable[0] = this.customSQLUpdate[0] != null && persistentClass.isCustomUpdateCallable();
        this.updateResultCheckStyles[0] = persistentClass.getCustomSQLUpdateCheckStyle() == null ? ExecuteUpdateResultCheckStyle.determineDefault(this.customSQLUpdate[0], this.updateCallable[0]) : persistentClass.getCustomSQLUpdateCheckStyle();
        this.customSQLDelete[0] = persistentClass.getCustomSQLDelete();
        this.deleteCallable[0] = this.customSQLDelete[0] != null && persistentClass.isCustomDeleteCallable();
        this.deleteResultCheckStyles[0] = persistentClass.getCustomSQLDeleteCheckStyle() == null ? ExecuteUpdateResultCheckStyle.determineDefault(this.customSQLDelete[0], this.deleteCallable[0]) : persistentClass.getCustomSQLDeleteCheckStyle();
        Iterator joinIter = persistentClass.getJoinClosureIterator();
        int j = 1;
        while (joinIter.hasNext()) {
            Join join = (Join)joinIter.next();
            this.qualifiedTableNames[j] = this.determineTableName(join.getTable());
            this.isInverseTable[j] = join.isInverse();
            this.isNullableTable[j] = join.isOptional();
            this.cascadeDeleteEnabled[j] = join.getKey().isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete();
            this.customSQLInsert[j] = join.getCustomSQLInsert();
            this.insertCallable[j] = this.customSQLInsert[j] != null && join.isCustomInsertCallable();
            this.insertResultCheckStyles[j] = join.getCustomSQLInsertCheckStyle() == null ? ExecuteUpdateResultCheckStyle.determineDefault(this.customSQLInsert[j], this.insertCallable[j]) : join.getCustomSQLInsertCheckStyle();
            this.customSQLUpdate[j] = join.getCustomSQLUpdate();
            this.updateCallable[j] = this.customSQLUpdate[j] != null && join.isCustomUpdateCallable();
            this.updateResultCheckStyles[j] = join.getCustomSQLUpdateCheckStyle() == null ? ExecuteUpdateResultCheckStyle.determineDefault(this.customSQLUpdate[j], this.updateCallable[j]) : join.getCustomSQLUpdateCheckStyle();
            this.customSQLDelete[j] = join.getCustomSQLDelete();
            this.deleteCallable[j] = this.customSQLDelete[j] != null && join.isCustomDeleteCallable();
            this.deleteResultCheckStyles[j] = join.getCustomSQLDeleteCheckStyle() == null ? ExecuteUpdateResultCheckStyle.determineDefault(this.customSQLDelete[j], this.deleteCallable[j]) : join.getCustomSQLDeleteCheckStyle();
            Iterator<Selectable> iter = join.getKey().getColumnIterator();
            this.keyColumnNames[j] = new String[join.getKey().getColumnSpan()];
            int i = 0;
            while (iter.hasNext()) {
                Column col = (Column)iter.next();
                this.keyColumnNames[j][i++] = col.getQuotedName(factory.getDialect());
            }
            ++j;
        }
        this.constraintOrderedTableNames = new String[this.qualifiedTableNames.length];
        this.constraintOrderedKeyColumnNames = new String[this.qualifiedTableNames.length][];
        int i = this.qualifiedTableNames.length - 1;
        int position = 0;
        while (i >= 0) {
            this.constraintOrderedTableNames[position] = this.qualifiedTableNames[i];
            this.constraintOrderedKeyColumnNames[position] = this.keyColumnNames[i];
            --i;
            ++position;
        }
        this.spaces = ArrayHelper.join(this.qualifiedTableNames, ArrayHelper.toStringArray(persistentClass.getSynchronizedTables()));
        boolean lazyAvailable = this.isInstrumented();
        boolean hasDeferred = false;
        ArrayList<String> subclassTables = new ArrayList<String>();
        ArrayList<String[]> joinKeyColumns = new ArrayList<String[]>();
        ArrayList<Boolean> isConcretes = new ArrayList<Boolean>();
        ArrayList<Boolean> isClassOrSuperclassJoins = new ArrayList<Boolean>();
        ArrayList<Boolean> isDeferreds = new ArrayList<Boolean>();
        ArrayList<Boolean> isInverses = new ArrayList<Boolean>();
        ArrayList<Boolean> isNullables = new ArrayList<Boolean>();
        ArrayList<Boolean> isLazies = new ArrayList<Boolean>();
        subclassTables.add(this.qualifiedTableNames[0]);
        joinKeyColumns.add(this.getIdentifierColumnNames());
        isConcretes.add(Boolean.TRUE);
        isClassOrSuperclassJoins.add(Boolean.TRUE);
        isDeferreds.add(Boolean.FALSE);
        isInverses.add(Boolean.FALSE);
        isNullables.add(Boolean.FALSE);
        isLazies.add(Boolean.FALSE);
        joinIter = persistentClass.getSubclassJoinClosureIterator();
        while (joinIter.hasNext()) {
            Join join = (Join)joinIter.next();
            isConcretes.add(persistentClass.isClassOrSuperclassTable(join.getTable()));
            isClassOrSuperclassJoins.add(persistentClass.isClassOrSuperclassJoin(join));
            isInverses.add(join.isInverse());
            isNullables.add(join.isOptional());
            isLazies.add(lazyAvailable && join.isLazy());
            boolean isDeferred = join.isSequentialSelect() && !persistentClass.isClassOrSuperclassJoin(join);
            isDeferreds.add(isDeferred);
            hasDeferred |= isDeferred;
            String joinTableName = this.determineTableName(join.getTable());
            subclassTables.add(joinTableName);
            Iterator<Selectable> iter = join.getKey().getColumnIterator();
            String[] keyCols = new String[join.getKey().getColumnSpan()];
            int i2 = 0;
            while (iter.hasNext()) {
                Column col = (Column)iter.next();
                keyCols[i2++] = col.getQuotedName(factory.getDialect());
            }
            joinKeyColumns.add(keyCols);
        }
        this.subclassTableSequentialSelect = ArrayHelper.toBooleanArray(isDeferreds);
        this.subclassTableNameClosure = ArrayHelper.toStringArray(subclassTables);
        this.subclassTableIsLazyClosure = ArrayHelper.toBooleanArray(isLazies);
        this.subclassTableKeyColumnClosure = ArrayHelper.to2DStringArray(joinKeyColumns);
        this.isClassOrSuperclassTable = ArrayHelper.toBooleanArray(isConcretes);
        this.isClassOrSuperclassJoin = ArrayHelper.toBooleanArray(isClassOrSuperclassJoins);
        this.isInverseSubclassTable = ArrayHelper.toBooleanArray(isInverses);
        this.isNullableSubclassTable = ArrayHelper.toBooleanArray(isNullables);
        this.hasSequentialSelects = hasDeferred;
        Map map = this.sequentialSelectStringsByEntityName = this.hasSequentialSelects ? new HashMap() : Collections.EMPTY_MAP;
        if (persistentClass.isPolymorphic()) {
            Value discrimValue = persistentClass.getDiscriminator();
            if (discrimValue == null) {
                throw new MappingException("discriminator mapping required for single table polymorphic persistence");
            }
            this.forceDiscriminator = persistentClass.isForceDiscriminator();
            Selectable selectable = discrimValue.getColumnIterator().next();
            if (discrimValue.hasFormula()) {
                Formula formula = (Formula)selectable;
                this.discriminatorFormula = formula.getFormula();
                this.discriminatorFormulaTemplate = formula.getTemplate(factory.getDialect(), factory.getSqlFunctionRegistry());
                this.discriminatorColumnName = null;
                this.discriminatorColumnReaders = null;
                this.discriminatorColumnReaderTemplate = null;
                this.discriminatorAlias = "clazz_";
            } else {
                Column column = (Column)selectable;
                this.discriminatorColumnName = column.getQuotedName(factory.getDialect());
                this.discriminatorColumnReaders = column.getReadExpr(factory.getDialect());
                this.discriminatorColumnReaderTemplate = column.getTemplate(factory.getDialect(), factory.getSqlFunctionRegistry());
                this.discriminatorAlias = column.getAlias(factory.getDialect(), persistentClass.getRootTable());
                this.discriminatorFormula = null;
                this.discriminatorFormulaTemplate = null;
            }
            this.discriminatorType = persistentClass.getDiscriminator().getType();
            if (persistentClass.isDiscriminatorValueNull()) {
                this.discriminatorValue = NULL_DISCRIMINATOR;
                this.discriminatorSQLValue = "null";
                this.discriminatorInsertable = false;
            } else if (persistentClass.isDiscriminatorValueNotNull()) {
                this.discriminatorValue = NOT_NULL_DISCRIMINATOR;
                this.discriminatorSQLValue = "not null";
                this.discriminatorInsertable = false;
            } else {
                this.discriminatorInsertable = persistentClass.isDiscriminatorInsertable() && !discrimValue.hasFormula();
                try {
                    DiscriminatorType dtype = (DiscriminatorType)this.discriminatorType;
                    this.discriminatorValue = dtype.stringToObject(persistentClass.getDiscriminatorValue());
                    this.discriminatorSQLValue = dtype.objectToSQLString(this.discriminatorValue, factory.getDialect());
                }
                catch (ClassCastException cce) {
                    throw new MappingException("Illegal discriminator type: " + this.discriminatorType.getName());
                }
                catch (Exception e) {
                    throw new MappingException("Could not format discriminator value to SQL string", e);
                }
            }
        } else {
            this.forceDiscriminator = false;
            this.discriminatorInsertable = false;
            this.discriminatorColumnName = null;
            this.discriminatorColumnReaders = null;
            this.discriminatorColumnReaderTemplate = null;
            this.discriminatorAlias = null;
            this.discriminatorType = null;
            this.discriminatorValue = null;
            this.discriminatorSQLValue = null;
            this.discriminatorFormula = null;
            this.discriminatorFormulaTemplate = null;
        }
        this.propertyTableNumbers = new int[this.getPropertySpan()];
        Iterator iter = persistentClass.getPropertyClosureIterator();
        int i3 = 0;
        while (iter.hasNext()) {
            Property prop = (Property)iter.next();
            this.propertyTableNumbers[i3++] = persistentClass.getJoinNumber(prop);
        }
        ArrayList<Integer> columnJoinNumbers = new ArrayList<Integer>();
        ArrayList<Integer> formulaJoinedNumbers = new ArrayList<Integer>();
        ArrayList<Integer> propertyJoinNumbers = new ArrayList<Integer>();
        HashMap<String, Integer> propertyTableNumbersByNameAndSubclassLocal = new HashMap<String, Integer>();
        HashMap<Object, String> subclassesByDiscriminatorValueLocal = new HashMap<Object, String>();
        iter = persistentClass.getSubclassPropertyClosureIterator();
        while (iter.hasNext()) {
            Property prop = (Property)iter.next();
            Integer join = persistentClass.getJoinNumber(prop);
            propertyJoinNumbers.add(join);
            propertyTableNumbersByNameAndSubclassLocal.put(prop.getPersistentClass().getEntityName() + '.' + prop.getName(), join);
            Iterator citer = prop.getColumnIterator();
            while (citer.hasNext()) {
                Selectable thing = (Selectable)citer.next();
                if (thing.isFormula()) {
                    formulaJoinedNumbers.add(join);
                    continue;
                }
                columnJoinNumbers.add(join);
            }
        }
        this.propertyTableNumbersByNameAndSubclass = CollectionHelper.toSmallMap(propertyTableNumbersByNameAndSubclassLocal);
        this.subclassColumnTableNumberClosure = ArrayHelper.toIntArray(columnJoinNumbers);
        this.subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formulaJoinedNumbers);
        this.subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propertyJoinNumbers);
        int subclassSpan = persistentClass.getSubclassSpan() + 1;
        this.subclassClosure = new String[subclassSpan];
        this.subclassClosure[0] = this.getEntityName();
        if (persistentClass.isPolymorphic()) {
            SingleTableEntityPersister.addSubclassByDiscriminatorValue(subclassesByDiscriminatorValueLocal, this.discriminatorValue, this.getEntityName());
        }
        if (persistentClass.isPolymorphic()) {
            iter = persistentClass.getSubclassIterator();
            int k = 1;
            while (iter.hasNext()) {
                Subclass sc = (Subclass)iter.next();
                this.subclassClosure[k++] = sc.getEntityName();
                if (sc.isDiscriminatorValueNull()) {
                    SingleTableEntityPersister.addSubclassByDiscriminatorValue(subclassesByDiscriminatorValueLocal, NULL_DISCRIMINATOR, sc.getEntityName());
                    continue;
                }
                if (sc.isDiscriminatorValueNotNull()) {
                    SingleTableEntityPersister.addSubclassByDiscriminatorValue(subclassesByDiscriminatorValueLocal, NOT_NULL_DISCRIMINATOR, sc.getEntityName());
                    continue;
                }
                try {
                    DiscriminatorType dtype = (DiscriminatorType)this.discriminatorType;
                    SingleTableEntityPersister.addSubclassByDiscriminatorValue(subclassesByDiscriminatorValueLocal, dtype.stringToObject(sc.getDiscriminatorValue()), sc.getEntityName());
                }
                catch (ClassCastException cce) {
                    throw new MappingException("Illegal discriminator type: " + this.discriminatorType.getName());
                }
                catch (Exception e) {
                    throw new MappingException("Error parsing discriminator value", e);
                }
            }
        }
        this.subclassesByDiscriminatorValue = CollectionHelper.toSmallMap(subclassesByDiscriminatorValueLocal);
        this.initSubclassPropertyAliasesMap(persistentClass);
        this.postConstruct(creationContext.getMetadata());
    }

    private static void addSubclassByDiscriminatorValue(Map<Object, String> subclassesByDiscriminatorValue, Object discriminatorValue, String entityName) {
        String mappedEntityName = subclassesByDiscriminatorValue.put(discriminatorValue, entityName);
        if (mappedEntityName != null) {
            throw new MappingException("Entities [" + entityName + "] and [" + mappedEntityName + "] are mapped with the same discriminator value '" + discriminatorValue + "'.");
        }
    }

    @Override
    public boolean isInverseTable(int j) {
        return this.isInverseTable[j];
    }

    @Override
    protected boolean isInverseSubclassTable(int j) {
        return this.isInverseSubclassTable[j];
    }

    @Override
    public String getDiscriminatorColumnName() {
        return this.discriminatorColumnName;
    }

    @Override
    public String getDiscriminatorColumnReaders() {
        return this.discriminatorColumnReaders;
    }

    @Override
    public String getDiscriminatorColumnReaderTemplate() {
        return this.discriminatorColumnReaderTemplate;
    }

    @Override
    public String getDiscriminatorAlias() {
        return this.discriminatorAlias;
    }

    @Override
    public String getDiscriminatorFormulaTemplate() {
        return this.discriminatorFormulaTemplate;
    }

    @Override
    public String getTableName() {
        return this.qualifiedTableNames[0];
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
        return this.discriminatorSQLValue;
    }

    public String[] getSubclassClosure() {
        return this.subclassClosure;
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
    public Serializable[] getPropertySpaces() {
        return this.spaces;
    }

    protected boolean isDiscriminatorFormula() {
        return this.discriminatorColumnName == null;
    }

    protected String getDiscriminatorFormula() {
        return this.discriminatorFormula;
    }

    @Override
    public String getTableName(int j) {
        return this.qualifiedTableNames[j];
    }

    @Override
    public String[] getKeyColumns(int j) {
        return this.keyColumnNames[j];
    }

    @Override
    public boolean isTableCascadeDeleteEnabled(int j) {
        return this.cascadeDeleteEnabled[j];
    }

    @Override
    public boolean isPropertyOfTable(int property, int j) {
        return this.propertyTableNumbers[property] == j;
    }

    @Override
    protected boolean isSubclassTableSequentialSelect(int j) {
        return this.subclassTableSequentialSelect[j] && !this.isClassOrSuperclassTable[j];
    }

    @Override
    public String fromTableFragment(String name) {
        return this.getTableName() + ' ' + name;
    }

    @Override
    protected String filterFragment(String alias) throws MappingException {
        String result = this.discriminatorFilterFragment(alias);
        if (this.hasWhere()) {
            result = result + " and " + this.getSQLWhereString(alias);
        }
        return result;
    }

    private String discriminatorFilterFragment(String alias) throws MappingException {
        return this.discriminatorFilterFragment(alias, null);
    }

    @Override
    public String oneToManyFilterFragment(String alias) throws MappingException {
        return this.forceDiscriminator ? this.discriminatorFilterFragment(alias, null) : "";
    }

    @Override
    public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
        return this.needsDiscriminator() ? this.discriminatorFilterFragment(alias, treatAsDeclarations) : "";
    }

    @Override
    protected String filterFragment(String alias, Set<String> treatAsDeclarations) {
        String result = this.discriminatorFilterFragment(alias, treatAsDeclarations);
        if (this.hasWhere()) {
            result = result + " and " + this.getSQLWhereString(alias);
        }
        return result;
    }

    private String discriminatorFilterFragment(String alias, Set<String> treatAsDeclarations) {
        boolean hasTreatAs;
        boolean bl = hasTreatAs = treatAsDeclarations != null && !treatAsDeclarations.isEmpty();
        if (!this.needsDiscriminator() && !hasTreatAs) {
            return "";
        }
        InFragment frag = new InFragment();
        if (this.isDiscriminatorFormula()) {
            frag.setFormula(alias, this.getDiscriminatorFormulaTemplate());
        } else {
            frag.setColumn(alias, this.getDiscriminatorColumnName());
        }
        if (hasTreatAs) {
            frag.addValues(this.decodeTreatAsRequests(treatAsDeclarations));
        } else {
            frag.addValues(this.fullDiscriminatorValues());
        }
        return " and " + frag.toFragmentString();
    }

    private boolean needsDiscriminator() {
        return this.forceDiscriminator || this.isInherited();
    }

    private String[] decodeTreatAsRequests(Set<String> treatAsDeclarations) {
        ArrayList<String> values = new ArrayList<String>();
        for (String subclass : treatAsDeclarations) {
            Queryable queryable = (Queryable)this.getFactory().getEntityPersister(subclass);
            if (!queryable.isAbstract()) {
                values.add(queryable.getDiscriminatorSQLValue());
                continue;
            }
            if (!queryable.hasSubclasses()) continue;
            Set actualSubClasses = queryable.getEntityMetamodel().getSubclassEntityNames();
            for (String actualSubClass : actualSubClasses) {
                Queryable actualQueryable;
                if (actualSubClass.equals(subclass) || (actualQueryable = (Queryable)this.getFactory().getEntityPersister(actualSubClass)).hasSubclasses()) continue;
                values.add(actualQueryable.getDiscriminatorSQLValue());
            }
        }
        return values.toArray(new String[values.size()]);
    }

    private String[] fullDiscriminatorValues() {
        if (this.fullDiscriminatorValues == null) {
            ArrayList<String> values = new ArrayList<String>();
            for (String subclass : this.getSubclassClosure()) {
                Queryable queryable = (Queryable)this.getFactory().getEntityPersister(subclass);
                if (queryable.isAbstract()) continue;
                values.add(queryable.getDiscriminatorSQLValue());
            }
            this.fullDiscriminatorValues = values.toArray(new String[values.size()]);
        }
        return this.fullDiscriminatorValues;
    }

    @Override
    public String getSubclassPropertyTableName(int i) {
        return this.subclassTableNameClosure[this.subclassPropertyTableNumberClosure[i]];
    }

    @Override
    protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
        if (this.isDiscriminatorFormula()) {
            select.addFormula(name, this.getDiscriminatorFormulaTemplate(), this.getDiscriminatorAlias());
        } else {
            select.addColumn(name, this.getDiscriminatorColumnName(), this.getDiscriminatorAlias());
        }
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
        return this.joinSpan;
    }

    @Override
    protected void addDiscriminatorToInsert(Insert insert) {
        if (this.discriminatorInsertable) {
            insert.addColumn(this.getDiscriminatorColumnName(), this.discriminatorSQLValue);
        }
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
        return this.propertyTableNumbers;
    }

    @Override
    protected boolean isSubclassPropertyDeferred(String propertyName, String entityName) {
        return this.hasSequentialSelects && this.isSubclassTableSequentialSelect(this.getSubclassPropertyTableNumber(propertyName, entityName));
    }

    @Override
    public boolean hasSequentialSelect() {
        return this.hasSequentialSelects;
    }

    private int getSubclassPropertyTableNumber(String propertyName, String entityName) {
        EntityPersister concreteEntityPersister = this.getEntityName().equals(entityName) ? this : this.getFactory().getMetamodel().entityPersister(entityName);
        Type type = concreteEntityPersister.getPropertyType(propertyName);
        if (type.isAssociationType() && ((AssociationType)type).useLHSPrimaryKey()) {
            return 0;
        }
        Integer tabnum = this.propertyTableNumbersByNameAndSubclass.get(entityName + '.' + propertyName);
        return tabnum == null ? 0 : tabnum;
    }

    @Override
    protected String getSequentialSelect(String entityName) {
        return this.sequentialSelectStringsByEntityName.get(entityName);
    }

    private String generateSequentialSelect(Loadable persister) {
        AbstractEntityPersister subclassPersister = (AbstractEntityPersister)persister;
        HashSet<Integer> tableNumbers = new HashSet<Integer>();
        String[] props = subclassPersister.getPropertyNames();
        String[] classes = subclassPersister.getPropertySubclassNames();
        for (int i = 0; i < props.length; ++i) {
            int propTableNumber = this.getSubclassPropertyTableNumber(props[i], classes[i]);
            if (!this.isSubclassTableSequentialSelect(propTableNumber) || this.isSubclassTableLazy(propTableNumber)) continue;
            tableNumbers.add(propTableNumber);
        }
        if (tableNumbers.isEmpty()) {
            return null;
        }
        ArrayList<Integer> columnNumbers = new ArrayList<Integer>();
        int[] columnTableNumbers = this.getSubclassColumnTableNumberClosure();
        for (int i = 0; i < this.getSubclassColumnClosure().length; ++i) {
            if (!tableNumbers.contains(columnTableNumbers[i])) continue;
            columnNumbers.add(i);
        }
        ArrayList<Integer> formulaNumbers = new ArrayList<Integer>();
        int[] formulaTableNumbers = this.getSubclassColumnTableNumberClosure();
        for (int i = 0; i < this.getSubclassFormulaTemplateClosure().length; ++i) {
            if (!tableNumbers.contains(formulaTableNumbers[i])) continue;
            formulaNumbers.add(i);
        }
        return this.renderSelect(ArrayHelper.toIntArray(tableNumbers), ArrayHelper.toIntArray(columnNumbers), ArrayHelper.toIntArray(formulaNumbers));
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
    protected boolean isClassOrSuperclassTable(int j) {
        return this.isClassOrSuperclassTable[j];
    }

    @Override
    protected boolean isClassOrSuperclassJoin(int j) {
        return this.isClassOrSuperclassJoin[j];
    }

    @Override
    protected boolean isSubclassTableLazy(int j) {
        return this.subclassTableIsLazyClosure[j];
    }

    @Override
    public boolean isNullableTable(int j) {
        return this.isNullableTable[j];
    }

    @Override
    protected boolean isNullableSubclassTable(int j) {
        return this.isNullableSubclassTable[j];
    }

    @Override
    public String getPropertyTableName(String propertyName) {
        Integer index = this.getEntityMetamodel().getPropertyIndexOrNull(propertyName);
        if (index == null) {
            return null;
        }
        return this.qualifiedTableNames[this.propertyTableNumbers[index]];
    }

    @Override
    protected void doPostInstantiate() {
        if (this.hasSequentialSelects) {
            String[] entityNames = this.getSubclassClosure();
            for (int i = 1; i < entityNames.length; ++i) {
                Loadable loadable = (Loadable)this.getFactory().getEntityPersister(entityNames[i]);
                if (loadable.isAbstract()) continue;
                String sequentialSelect = this.generateSequentialSelect(loadable);
                this.sequentialSelectStringsByEntityName.put(entityNames[i], sequentialSelect);
            }
        }
    }

    @Override
    public boolean canOmitSuperclassTableJoin() {
        return true;
    }

    @Override
    public boolean isMultiTable() {
        return this.getTableSpan() > 1;
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
    public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
        return new DynamicFilterAliasGenerator(this.qualifiedTableNames, rootAlias);
    }
}

