/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.spi;

import org.hibernate.loader.plan.build.spi.LoadPlanBuildingAssociationVisitationStrategy;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.walking.spi.MetamodelGraphWalker;

public final class MetamodelDrivenLoadPlanBuilder {
    private MetamodelDrivenLoadPlanBuilder() {
    }

    public static LoadPlan buildRootEntityLoadPlan(LoadPlanBuildingAssociationVisitationStrategy strategy, EntityPersister persister) {
        MetamodelGraphWalker.visitEntity(strategy, persister);
        return strategy.buildLoadPlan();
    }

    public static LoadPlan buildRootCollectionLoadPlan(LoadPlanBuildingAssociationVisitationStrategy strategy, CollectionPersister persister) {
        MetamodelGraphWalker.visitCollection(strategy, persister);
        return strategy.buildLoadPlan();
    }
}

