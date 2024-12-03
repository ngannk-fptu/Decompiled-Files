/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.internal;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.plan.exec.internal.AbstractCollectionLoadQueryDetails;
import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
import org.hibernate.loader.plan.exec.query.internal.SelectStatementBuilder;
import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
import org.hibernate.loader.plan.spi.CollectionReturn;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.persister.entity.OuterJoinLoadable;

public class BasicCollectionLoadQueryDetails
extends AbstractCollectionLoadQueryDetails {
    BasicCollectionLoadQueryDetails(LoadPlan loadPlan, AliasResolutionContextImpl aliasResolutionContext, CollectionReturn rootReturn, QueryBuildingParameters buildingParameters, SessionFactoryImplementor factory) {
        super(loadPlan, aliasResolutionContext, rootReturn, buildingParameters, factory);
        this.generate();
    }

    @Override
    protected String getRootTableAlias() {
        return this.getCollectionReferenceAliases().getCollectionTableAlias();
    }

    @Override
    protected void applyRootReturnSelectFragments(SelectStatementBuilder selectStatementBuilder) {
        selectStatementBuilder.appendSelectClauseFragment(this.getQueryableCollection().selectFragment(this.getCollectionReferenceAliases().getCollectionTableAlias(), this.getCollectionReferenceAliases().getCollectionColumnAliases().getSuffix()));
        if (this.getQueryableCollection().isManyToMany()) {
            OuterJoinLoadable elementPersister = (OuterJoinLoadable)this.getQueryableCollection().getElementPersister();
            selectStatementBuilder.appendSelectClauseFragment(elementPersister.selectFragment(this.getCollectionReferenceAliases().getElementTableAlias(), this.getCollectionReferenceAliases().getEntityElementAliases().getColumnAliases().getSuffix()));
        }
        super.applyRootReturnSelectFragments(selectStatementBuilder);
    }

    @Override
    protected void applyRootReturnTableFragments(SelectStatementBuilder selectStatementBuilder) {
        selectStatementBuilder.appendFromClauseFragment(this.getQueryableCollection().getTableName(), this.getCollectionReferenceAliases().getCollectionTableAlias());
    }

    @Override
    protected void applyRootReturnOrderByFragments(SelectStatementBuilder selectStatementBuilder) {
        String manyToManyOrdering = this.getQueryableCollection().getManyToManyOrderByString(this.getCollectionReferenceAliases().getElementTableAlias());
        if (StringHelper.isNotEmpty(manyToManyOrdering)) {
            selectStatementBuilder.appendOrderByFragment(manyToManyOrdering);
        }
        super.applyRootReturnOrderByFragments(selectStatementBuilder);
    }
}

