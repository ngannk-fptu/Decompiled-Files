/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.collection;

import java.util.ArrayList;
import java.util.Arrays;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.BasicLoader;
import org.hibernate.loader.OuterJoinableAssociation;
import org.hibernate.loader.collection.CollectionJoinWalker;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.Select;

public class OneToManyJoinWalker
extends CollectionJoinWalker {
    private final QueryableCollection oneToManyPersister;

    @Override
    protected boolean isDuplicateAssociation(String foreignKeyTable, String[] foreignKeyColumns) {
        boolean isSameJoin = this.oneToManyPersister.getTableName().equals(foreignKeyTable) && Arrays.equals(foreignKeyColumns, this.oneToManyPersister.getKeyColumnNames());
        return isSameJoin || super.isDuplicateAssociation(foreignKeyTable, foreignKeyColumns);
    }

    public OneToManyJoinWalker(QueryableCollection oneToManyPersister, int batchSize, String subquery, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        super(factory, loadQueryInfluencers);
        this.oneToManyPersister = oneToManyPersister;
        OuterJoinLoadable elementPersister = (OuterJoinLoadable)oneToManyPersister.getElementPersister();
        String alias = this.generateRootAlias(oneToManyPersister.getRole());
        this.walkEntityTree(elementPersister, alias);
        ArrayList<OuterJoinableAssociation> allAssociations = new ArrayList<OuterJoinableAssociation>(this.associations);
        allAssociations.add(OuterJoinableAssociation.createRoot(oneToManyPersister.getCollectionType(), alias, this.getFactory()));
        this.initPersisters(allAssociations, LockMode.NONE);
        this.initStatementString(elementPersister, alias, batchSize, subquery);
    }

    private void initStatementString(OuterJoinLoadable elementPersister, String alias, int batchSize, String subquery) throws MappingException {
        int joins = OneToManyJoinWalker.countEntityPersisters(this.associations);
        this.suffixes = BasicLoader.generateSuffixes(joins + 1);
        int collectionJoins = OneToManyJoinWalker.countCollectionPersisters(this.associations) + 1;
        this.collectionSuffixes = BasicLoader.generateSuffixes(joins + 1, collectionJoins);
        StringBuilder whereString = this.whereString(alias, this.oneToManyPersister.getKeyColumnNames(), subquery, batchSize);
        String filter = this.oneToManyPersister.filterFragment(alias, this.getLoadQueryInfluencers().getEnabledFilters());
        whereString.insert(0, StringHelper.moveAndToBeginning(filter));
        JoinFragment ojf = this.mergeOuterJoins(this.associations);
        Select select = new Select(this.getDialect()).setSelectClause(this.oneToManyPersister.selectFragment(null, null, alias, this.suffixes[joins], this.collectionSuffixes[0], true) + this.selectString(this.associations)).setFromClause(elementPersister.fromTableFragment(alias) + elementPersister.fromJoinFragment(alias, true, true)).setWhereClause(whereString.toString()).setOuterJoins(ojf.toFromFragmentString(), ojf.toWhereFragmentString() + elementPersister.whereJoinFragment(alias, true, true));
        select.setOrderByClause(this.orderBy(this.associations, this.oneToManyPersister.getSQLOrderByString(alias)));
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            select.setComment("load one-to-many " + this.oneToManyPersister.getRole());
        }
        this.sql = select.toStatementString();
    }

    public String toString() {
        return this.getClass().getName() + '(' + this.oneToManyPersister.getRole() + ')';
    }
}

