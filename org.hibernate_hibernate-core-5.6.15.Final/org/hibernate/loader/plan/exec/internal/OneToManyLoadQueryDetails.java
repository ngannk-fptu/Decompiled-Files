/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.internal;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.plan.exec.internal.AbstractCollectionLoadQueryDetails;
import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
import org.hibernate.loader.plan.exec.query.internal.SelectStatementBuilder;
import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
import org.hibernate.loader.plan.spi.CollectionReturn;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.persister.entity.OuterJoinLoadable;

public class OneToManyLoadQueryDetails
extends AbstractCollectionLoadQueryDetails {
    OneToManyLoadQueryDetails(LoadPlan loadPlan, AliasResolutionContextImpl aliasResolutionContext, CollectionReturn rootReturn, QueryBuildingParameters buildingParameters, SessionFactoryImplementor factory) {
        super(loadPlan, aliasResolutionContext, rootReturn, buildingParameters, factory);
        this.generate();
    }

    @Override
    protected String getRootTableAlias() {
        return this.getElementEntityReferenceAliases().getTableAlias();
    }

    @Override
    protected void applyRootReturnSelectFragments(SelectStatementBuilder selectStatementBuilder) {
        selectStatementBuilder.appendSelectClauseFragment(this.getQueryableCollection().selectFragment(null, null, this.getElementEntityReferenceAliases().getTableAlias(), this.getElementEntityReferenceAliases().getColumnAliases().getSuffix(), this.getCollectionReferenceAliases().getCollectionColumnAliases().getSuffix(), true));
        super.applyRootReturnSelectFragments(selectStatementBuilder);
    }

    @Override
    protected void applyRootReturnTableFragments(SelectStatementBuilder selectStatementBuilder) {
        OuterJoinLoadable elementOuterJoinLoadable = (OuterJoinLoadable)this.getElementEntityReference().getEntityPersister();
        String tableAlias = this.getElementEntityReferenceAliases().getTableAlias();
        String fragment = elementOuterJoinLoadable.fromTableFragment(tableAlias) + elementOuterJoinLoadable.fromJoinFragment(tableAlias, true, true);
        selectStatementBuilder.appendFromClauseFragment(fragment);
    }

    private EntityReference getElementEntityReference() {
        return this.getRootCollectionReturn().getElementGraph().resolveEntityReference();
    }

    private EntityReferenceAliases getElementEntityReferenceAliases() {
        return this.getAliasResolutionContext().resolveEntityReferenceAliases(this.getElementEntityReference().getQuerySpaceUid());
    }
}

