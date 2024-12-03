/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.cfg.Settings;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.IdentityGenerator;
import org.hibernate.internal.FilterAliasGenerator;
import org.hibernate.internal.StaticFilterAliasGenerator;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.internal.util.collections.JoinedIterator;
import org.hibernate.internal.util.collections.SingletonIterator;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.sql.SelectFragment;
import org.hibernate.sql.SimpleSelect;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class UnionSubclassEntityPersister
extends AbstractEntityPersister {
    private final String subquery;
    private final String tableName;
    private final String[] subclassClosure;
    private final String[] spaces;
    private final String[] subclassSpaces;
    private final Object discriminatorValue;
    private final String discriminatorSQLValue;
    private final Map subclassByDiscriminatorValue = new HashMap();
    private final String[] constraintOrderedTableNames;
    private final String[][] constraintOrderedKeyColumnNames;

    public UnionSubclassEntityPersister(PersistentClass persistentClass, EntityDataAccess cacheAccessStrategy, NaturalIdDataAccess naturalIdRegionAccessStrategy, PersisterCreationContext creationContext) throws HibernateException {
        super(persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, creationContext);
        if (this.getIdentifierGenerator() instanceof IdentityGenerator) {
            throw new MappingException("Cannot use identity column key generation with <union-subclass> mapping for: " + this.getEntityName());
        }
        SessionFactoryImplementor factory = creationContext.getSessionFactory();
        Database database = creationContext.getMetadata().getDatabase();
        this.tableName = this.determineTableName(persistentClass.getTable());
        boolean callable = false;
        ExecuteUpdateResultCheckStyle checkStyle = null;
        String sql = persistentClass.getCustomSQLInsert();
        boolean bl = callable = sql != null && persistentClass.isCustomInsertCallable();
        checkStyle = sql == null ? ExecuteUpdateResultCheckStyle.COUNT : (persistentClass.getCustomSQLInsertCheckStyle() == null ? ExecuteUpdateResultCheckStyle.determineDefault(sql, callable) : persistentClass.getCustomSQLInsertCheckStyle());
        this.customSQLInsert = new String[]{sql};
        this.insertCallable = new boolean[]{callable};
        this.insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[]{checkStyle};
        sql = persistentClass.getCustomSQLUpdate();
        boolean bl2 = callable = sql != null && persistentClass.isCustomUpdateCallable();
        checkStyle = sql == null ? ExecuteUpdateResultCheckStyle.COUNT : (persistentClass.getCustomSQLUpdateCheckStyle() == null ? ExecuteUpdateResultCheckStyle.determineDefault(sql, callable) : persistentClass.getCustomSQLUpdateCheckStyle());
        this.customSQLUpdate = new String[]{sql};
        this.updateCallable = new boolean[]{callable};
        this.updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[]{checkStyle};
        sql = persistentClass.getCustomSQLDelete();
        boolean bl3 = callable = sql != null && persistentClass.isCustomDeleteCallable();
        checkStyle = sql == null ? ExecuteUpdateResultCheckStyle.COUNT : (persistentClass.getCustomSQLDeleteCheckStyle() == null ? ExecuteUpdateResultCheckStyle.determineDefault(sql, callable) : persistentClass.getCustomSQLDeleteCheckStyle());
        this.customSQLDelete = new String[]{sql};
        this.deleteCallable = new boolean[]{callable};
        this.deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[]{checkStyle};
        this.discriminatorValue = persistentClass.getSubclassId();
        this.discriminatorSQLValue = String.valueOf(persistentClass.getSubclassId());
        int subclassSpan = persistentClass.getSubclassSpan() + 1;
        this.subclassClosure = new String[subclassSpan];
        this.subclassClosure[0] = this.getEntityName();
        this.subclassByDiscriminatorValue.put(persistentClass.getSubclassId(), persistentClass.getEntityName());
        if (persistentClass.isPolymorphic()) {
            Iterator subclassIter = persistentClass.getSubclassIterator();
            int k = 1;
            while (subclassIter.hasNext()) {
                Subclass subclass = (Subclass)subclassIter.next();
                this.subclassClosure[k++] = subclass.getEntityName();
                this.subclassByDiscriminatorValue.put(subclass.getSubclassId(), subclass.getEntityName());
            }
        }
        int spacesSize = 1 + persistentClass.getSynchronizedTables().size();
        this.spaces = new String[spacesSize];
        this.spaces[0] = this.tableName;
        Iterator iter = persistentClass.getSynchronizedTables().iterator();
        for (int i = 1; i < spacesSize; ++i) {
            this.spaces[i] = (String)iter.next();
        }
        HashSet<String> subclassTables = new HashSet<String>();
        Iterator subclassTableIter = persistentClass.getSubclassTableClosureIterator();
        while (subclassTableIter.hasNext()) {
            subclassTables.add(this.determineTableName((Table)subclassTableIter.next()));
        }
        this.subclassSpaces = ArrayHelper.toStringArray(subclassTables);
        this.subquery = this.generateSubquery(persistentClass, creationContext.getMetadata());
        if (this.isMultiTable()) {
            int idColumnSpan = this.getIdentifierColumnSpan();
            ArrayList<String> tableNames = new ArrayList<String>();
            ArrayList<String[]> keyColumns = new ArrayList<String[]>();
            Iterator tableIter = persistentClass.getSubclassTableClosureIterator();
            while (tableIter.hasNext()) {
                Table tab = (Table)tableIter.next();
                if (tab.isAbstractUnionTable()) continue;
                String tableName = this.determineTableName(tab);
                tableNames.add(tableName);
                String[] key = new String[idColumnSpan];
                Iterator<Column> citer = tab.getPrimaryKey().getColumnIterator();
                for (int k = 0; k < idColumnSpan; ++k) {
                    key[k] = citer.next().getQuotedName(factory.getDialect());
                }
                keyColumns.add(key);
            }
            this.constraintOrderedTableNames = ArrayHelper.toStringArray(tableNames);
            this.constraintOrderedKeyColumnNames = ArrayHelper.to2DStringArray(keyColumns);
        } else {
            this.constraintOrderedTableNames = new String[]{this.tableName};
            this.constraintOrderedKeyColumnNames = new String[][]{this.getIdentifierColumnNames()};
        }
        this.initSubclassPropertyAliasesMap(persistentClass);
        this.postConstruct(creationContext.getMetadata());
    }

    @Override
    public Serializable[] getQuerySpaces() {
        return this.subclassSpaces;
    }

    @Override
    public String getTableName() {
        return this.subquery;
    }

    @Override
    public Type getDiscriminatorType() {
        return StandardBasicTypes.INTEGER;
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
        return (String)this.subclassByDiscriminatorValue.get(value);
    }

    @Override
    public Serializable[] getPropertySpaces() {
        return this.spaces;
    }

    protected boolean isDiscriminatorFormula() {
        return false;
    }

    protected String generateSelectString(LockMode lockMode) {
        SimpleSelect select = new SimpleSelect(this.getFactory().getDialect()).setLockMode(lockMode).setTableName(this.getTableName()).addColumns(this.getIdentifierColumnNames()).addColumns(this.getSubclassColumnClosure(), this.getSubclassColumnAliasClosure(), this.getSubclassColumnLazyiness()).addColumns(this.getSubclassFormulaClosure(), this.getSubclassFormulaAliasClosure(), this.getSubclassFormulaLazyiness());
        if (this.hasSubclasses()) {
            if (this.isDiscriminatorFormula()) {
                select.addColumn(this.getDiscriminatorFormula(), this.getDiscriminatorAlias());
            } else {
                select.addColumn(this.getDiscriminatorColumnName(), this.getDiscriminatorAlias());
            }
        }
        if (this.getFactory().getSettings().isCommentsEnabled()) {
            select.setComment("load " + this.getEntityName());
        }
        return select.addCondition(this.getIdentifierColumnNames(), "=?").toStatementString();
    }

    protected String getDiscriminatorFormula() {
        return null;
    }

    @Override
    public String getTableName(int j) {
        return this.tableName;
    }

    @Override
    public String[] getKeyColumns(int j) {
        return this.getIdentifierColumnNames();
    }

    @Override
    public boolean isTableCascadeDeleteEnabled(int j) {
        return false;
    }

    @Override
    public boolean isPropertyOfTable(int property, int j) {
        return true;
    }

    @Override
    public String fromTableFragment(String name) {
        return this.getTableName() + ' ' + name;
    }

    @Override
    protected String filterFragment(String name) {
        return this.hasWhere() ? " and " + this.getSQLWhereString(name) : "";
    }

    @Override
    protected String filterFragment(String alias, Set<String> treatAsDeclarations) {
        return this.filterFragment(alias);
    }

    @Override
    public String getSubclassPropertyTableName(int i) {
        return this.getTableName();
    }

    @Override
    protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
        select.addColumn(name, this.getDiscriminatorColumnName(), this.getDiscriminatorAlias());
    }

    @Override
    protected int[] getPropertyTableNumbersInSelect() {
        return new int[this.getPropertySpan()];
    }

    @Override
    protected int getSubclassPropertyTableNumber(int i) {
        return 0;
    }

    @Override
    public int getSubclassPropertyTableNumber(String propertyName) {
        return 0;
    }

    @Override
    public boolean isMultiTable() {
        return this.isAbstract() || this.hasSubclasses();
    }

    @Override
    public int getTableSpan() {
        return 1;
    }

    @Override
    protected int[] getSubclassColumnTableNumberClosure() {
        return new int[this.getSubclassColumnClosure().length];
    }

    @Override
    protected int[] getSubclassFormulaTableNumberClosure() {
        return new int[this.getSubclassFormulaClosure().length];
    }

    protected boolean[] getTableHasColumns() {
        return new boolean[]{true};
    }

    @Override
    protected int[] getPropertyTableNumbers() {
        return new int[this.getPropertySpan()];
    }

    protected String generateSubquery(PersistentClass model, Mapping mapping) {
        Dialect dialect = this.getFactory().getDialect();
        Settings settings = this.getFactory().getSettings();
        SqlStringGenerationContext sqlStringGenerationContext = this.getFactory().getSqlStringGenerationContext();
        if (!model.hasSubclasses()) {
            return model.getTable().getQualifiedName(sqlStringGenerationContext);
        }
        LinkedHashSet<Column> columns = new LinkedHashSet<Column>();
        Iterator titer = model.getSubclassTableClosureIterator();
        while (titer.hasNext()) {
            Table table = (Table)titer.next();
            if (table.isAbstractUnionTable()) continue;
            Iterator<Column> citer = table.getColumnIterator();
            while (citer.hasNext()) {
                columns.add(citer.next());
            }
        }
        StringBuilder buf = new StringBuilder().append("( ");
        JoinedIterator siter = new JoinedIterator(new SingletonIterator<PersistentClass>(model), model.getSubclassIterator());
        while (siter.hasNext()) {
            PersistentClass clazz = (PersistentClass)siter.next();
            Table table = clazz.getTable();
            if (table.isAbstractUnionTable()) continue;
            buf.append("select ");
            for (Column col : columns) {
                if (!table.containsColumn(col)) {
                    int sqlType = col.getSqlTypeCode(mapping);
                    buf.append(dialect.getSelectClauseNullString(sqlType)).append(" as ");
                }
                buf.append(col.getQuotedName(dialect));
                buf.append(", ");
            }
            buf.append(clazz.getSubclassId()).append(" as clazz_");
            buf.append(" from ").append(table.getQualifiedName(sqlStringGenerationContext));
            buf.append(" union ");
            if (!dialect.supportsUnionAll()) continue;
            buf.append("all ");
        }
        if (buf.length() > 2) {
            buf.setLength(buf.length() - (dialect.supportsUnionAll() ? 11 : 7));
        }
        return buf.append(" )").toString();
    }

    @Override
    protected String[] getSubclassTableKeyColumns(int j) {
        if (j != 0) {
            throw new AssertionFailure("only one table");
        }
        return this.getIdentifierColumnNames();
    }

    @Override
    public String getSubclassTableName(int j) {
        if (j != 0) {
            throw new AssertionFailure("only one table");
        }
        return this.tableName;
    }

    @Override
    public int getSubclassTableSpan() {
        return 1;
    }

    @Override
    protected boolean isClassOrSuperclassTable(int j) {
        if (j != 0) {
            throw new AssertionFailure("only one table");
        }
        return true;
    }

    @Override
    public String getPropertyTableName(String propertyName) {
        return this.getTableName();
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
        return new StaticFilterAliasGenerator(rootAlias);
    }
}

