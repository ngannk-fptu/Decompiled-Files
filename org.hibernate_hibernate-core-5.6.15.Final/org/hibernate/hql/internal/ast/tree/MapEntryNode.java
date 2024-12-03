/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import antlr.collections.AST;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.NameGenerator;
import org.hibernate.hql.internal.ast.tree.AbstractMapComponentNode;
import org.hibernate.hql.internal.ast.tree.AggregatedSelectExpression;
import org.hibernate.hql.internal.ast.tree.MapKeyEntityFromElement;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.sql.AliasGenerator;
import org.hibernate.sql.SelectExpression;
import org.hibernate.sql.SelectFragment;
import org.hibernate.transform.BasicTransformerAdapter;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class MapEntryNode
extends AbstractMapComponentNode
implements AggregatedSelectExpression {
    private int scalarColumnIndex = -1;
    private List types = new ArrayList(4);
    private static final String[] ALIASES = new String[]{null, null};
    private MapEntryBuilder mapEntryBuilder;

    @Override
    protected String expressionDescription() {
        return "entry(*)";
    }

    @Override
    public Class getAggregationResultType() {
        return Map.Entry.class;
    }

    @Override
    public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent, AST parentPredicate) throws SemanticException {
        if (parent != null) {
            throw new SemanticException(this.expressionDescription() + " expression cannot be further de-referenced");
        }
        super.resolve(generateJoin, implicitJoin, classAlias, parent, parentPredicate);
    }

    @Override
    protected Type resolveType(QueryableCollection collectionPersister) {
        Type keyType = collectionPersister.getIndexType();
        Type valueType = collectionPersister.getElementType();
        this.types.add(keyType);
        this.types.add(valueType);
        this.mapEntryBuilder = new MapEntryBuilder();
        return null;
    }

    @Override
    protected String[] resolveColumns(QueryableCollection collectionPersister) {
        ArrayList selections = new ArrayList();
        this.determineKeySelectExpressions(collectionPersister, selections);
        this.determineValueSelectExpressions(collectionPersister, selections);
        int columnNumber = selections.size();
        StringBuilder text = new StringBuilder(columnNumber * 12);
        String[] columns = new String[columnNumber];
        for (int i = 0; i < columnNumber; ++i) {
            SelectExpression selectExpression = (SelectExpression)selections.get(i);
            if (i != 0) {
                text.append(", ");
            }
            text.append(selectExpression.getExpression());
            text.append(" as ");
            text.append(selectExpression.getAlias());
            columns[i] = selectExpression.getExpression();
        }
        this.setText(text.toString());
        this.setResolved();
        return columns;
    }

    private void determineKeySelectExpressions(QueryableCollection collectionPersister, List selections) {
        LocalAliasGenerator aliasGenerator = new LocalAliasGenerator(0);
        this.appendSelectExpressions(collectionPersister.getIndexColumnNames(), selections, (AliasGenerator)aliasGenerator);
        Type keyType = collectionPersister.getIndexType();
        if (keyType.isEntityType()) {
            MapKeyEntityFromElement mapKeyEntityFromElement = this.findOrAddMapKeyEntityFromElement(collectionPersister);
            Queryable keyEntityPersister = mapKeyEntityFromElement.getQueryable();
            SelectFragment fragment = keyEntityPersister.propertySelectFragmentFragment(mapKeyEntityFromElement.getTableAlias(), null, false);
            this.appendSelectExpressions(fragment, selections, (AliasGenerator)aliasGenerator);
        }
    }

    private void appendSelectExpressions(String[] columnNames, List selections, AliasGenerator aliasGenerator) {
        for (int i = 0; i < columnNames.length; ++i) {
            selections.add(new BasicSelectExpression(this.collectionTableAlias() + '.' + columnNames[i], aliasGenerator.generateAlias(columnNames[i])));
        }
    }

    private void appendSelectExpressions(SelectFragment fragment, List selections, AliasGenerator aliasGenerator) {
        for (String column : fragment.getColumns()) {
            selections.add(new BasicSelectExpression(column, aliasGenerator.generateAlias(column)));
        }
    }

    private void determineValueSelectExpressions(QueryableCollection collectionPersister, List selections) {
        LocalAliasGenerator aliasGenerator = new LocalAliasGenerator(1);
        this.appendSelectExpressions(collectionPersister.getElementColumnNames(), selections, (AliasGenerator)aliasGenerator);
        Type valueType = collectionPersister.getElementType();
        if (valueType.isAssociationType()) {
            EntityType valueEntityType = (EntityType)valueType;
            Queryable valueEntityPersister = (Queryable)this.sfi().getEntityPersister(valueEntityType.getAssociatedEntityName(this.sfi()));
            SelectFragment fragment = valueEntityPersister.propertySelectFragmentFragment(this.elementTableAlias(), null, false);
            this.appendSelectExpressions(fragment, selections, (AliasGenerator)aliasGenerator);
        }
    }

    private String collectionTableAlias() {
        return this.getFromElement().getCollectionTableAlias() != null ? this.getFromElement().getCollectionTableAlias() : this.getFromElement().getTableAlias();
    }

    private String elementTableAlias() {
        return this.getFromElement().getTableAlias();
    }

    public SessionFactoryImplementor sfi() {
        return this.getSessionFactoryHelper().getFactory();
    }

    @Override
    public void setText(String s) {
        if (this.isResolved()) {
            return;
        }
        super.setText(s);
    }

    @Override
    public void setScalarColumn(int i) {
        this.scalarColumnIndex = i;
    }

    @Override
    public int getScalarColumnIndex() {
        return this.scalarColumnIndex;
    }

    @Override
    public void setScalarColumnText(int i) {
    }

    @Override
    public boolean isScalar() {
        return true;
    }

    @Override
    public List getAggregatedSelectionTypeList() {
        return this.types;
    }

    @Override
    public String[] getAggregatedAliases() {
        return ALIASES;
    }

    @Override
    public ResultTransformer getResultTransformer() {
        return this.mapEntryBuilder;
    }

    private static class EntryAdapter
    implements Map.Entry {
        private final Object key;
        private Object value;

        private EntryAdapter(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getValue() {
            return this.value;
        }

        public Object getKey() {
            return this.key;
        }

        public Object setValue(Object value) {
            Object old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            EntryAdapter that = (EntryAdapter)o;
            return (this.key == null ? that.key == null : this.key.equals(that.key)) && (this.value == null ? that.value == null : this.value.equals(that.value));
        }

        @Override
        public int hashCode() {
            int keyHash = this.key == null ? 0 : this.key.hashCode();
            int valueHash = this.value == null ? 0 : this.value.hashCode();
            return keyHash ^ valueHash;
        }
    }

    private static class MapEntryBuilder
    extends BasicTransformerAdapter {
        private MapEntryBuilder() {
        }

        @Override
        public Object transformTuple(Object[] tuple, String[] aliases) {
            if (tuple.length != 2) {
                throw new HibernateException("Expecting exactly 2 tuples to transform into Map.Entry");
            }
            return new EntryAdapter(tuple[0], tuple[1]);
        }
    }

    private static class BasicSelectExpression
    implements SelectExpression {
        private final String expression;
        private final String alias;

        private BasicSelectExpression(String expression, String alias) {
            this.expression = expression;
            this.alias = alias;
        }

        @Override
        public String getExpression() {
            return this.expression;
        }

        @Override
        public String getAlias() {
            return this.alias;
        }
    }

    private static class LocalAliasGenerator
    implements AliasGenerator {
        private final int base;
        private int counter;

        private LocalAliasGenerator(int base) {
            this.base = base;
        }

        @Override
        public String generateAlias(String sqlExpression) {
            return NameGenerator.scalarName(this.base, this.counter++);
        }
    }
}

