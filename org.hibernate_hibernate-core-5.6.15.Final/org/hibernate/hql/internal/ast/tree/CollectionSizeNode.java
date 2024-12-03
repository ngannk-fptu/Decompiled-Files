/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.hql.internal.ast.tree;

import org.hibernate.AssertionFailure;
import org.hibernate.hql.internal.NameGenerator;
import org.hibernate.hql.internal.ast.tree.CollectionPathNode;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.SelectExpression;
import org.hibernate.hql.internal.ast.tree.SqlNode;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.persister.collection.CollectionPropertyMapping;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.type.StandardBasicTypes;
import org.jboss.logging.Logger;

public class CollectionSizeNode
extends SqlNode
implements SelectExpression {
    private static final Logger log = Logger.getLogger(CollectionSizeNode.class);
    private final CollectionPathNode collectionPathNode;
    private final CollectionPropertyMapping collectionPropertyMapping;
    private String alias;
    private String scalarName;

    public CollectionSizeNode(CollectionPathNode collectionPathNode) {
        this.collectionPathNode = collectionPathNode;
        this.collectionPropertyMapping = new CollectionPropertyMapping((QueryableCollection)collectionPathNode.getCollectionDescriptor());
        this.setType(95);
        this.setDataType(StandardBasicTypes.INTEGER);
        this.setText("collection-size");
    }

    public CollectionPathNode getCollectionPathNode() {
        return this.collectionPathNode;
    }

    public String toSqlExpression() {
        FromElement collectionOwnerFromElement = this.collectionPathNode.getCollectionOwnerFromElement();
        QueryableCollection collectionDescriptor = (QueryableCollection)this.collectionPathNode.getCollectionDescriptor();
        String collectionTableAlias = collectionOwnerFromElement.getFromClause().getAliasGenerator().createName(this.collectionPathNode.getCollectionPropertyName());
        String[] ownerKeyColumns = this.collectionPathNode.resolveOwnerKeyColumnExpressions();
        String[] collectionKeyColumns = StringHelper.qualify(collectionTableAlias, collectionDescriptor.getKeyColumnNames());
        if (collectionKeyColumns.length != ownerKeyColumns.length) {
            throw new AssertionFailure("Mismatch between collection key columns");
        }
        String[] sizeColumns = this.collectionPropertyMapping.toColumns(collectionTableAlias, "size");
        assert (sizeColumns.length == 1);
        String sizeColumn = sizeColumns[0];
        StringBuilder buffer = new StringBuilder("(select ").append(sizeColumn);
        buffer.append(" from ").append(collectionDescriptor.getTableName()).append(" ").append(collectionTableAlias);
        buffer.append(" where ");
        boolean firstPass = true;
        for (int i = 0; i < ownerKeyColumns.length; ++i) {
            if (firstPass) {
                firstPass = false;
            } else {
                buffer.append(" and ");
            }
            buffer.append(ownerKeyColumns[i]).append(" = ").append(collectionKeyColumns[i]);
        }
        buffer.append(collectionDescriptor.filterFragment(collectionTableAlias, collectionOwnerFromElement.getWalker().getEnabledFilters()));
        buffer.append(")");
        if (this.scalarName != null) {
            buffer.append(" as ").append(this.scalarName);
        }
        String subQuery = buffer.toString();
        log.debugf("toSqlExpression( size(%s) ) -> %s", (Object)this.collectionPathNode.getCollectionQueryPath(), (Object)subQuery);
        return subQuery;
    }

    @Override
    public void setScalarColumnText(int i) {
        log.debugf("setScalarColumnText(%s)", i);
        this.scalarName = NameGenerator.scalarName(i, 0);
    }

    @Override
    public void setScalarColumn(int i) {
        log.debugf("setScalarColumn(%s)", i);
        this.setScalarColumnText(i);
    }

    @Override
    public int getScalarColumnIndex() {
        return -1;
    }

    @Override
    public FromElement getFromElement() {
        return null;
    }

    @Override
    public boolean isConstructor() {
        return false;
    }

    @Override
    public boolean isReturnableEntity() {
        return false;
    }

    @Override
    public boolean isScalar() {
        return true;
    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return this.alias;
    }
}

