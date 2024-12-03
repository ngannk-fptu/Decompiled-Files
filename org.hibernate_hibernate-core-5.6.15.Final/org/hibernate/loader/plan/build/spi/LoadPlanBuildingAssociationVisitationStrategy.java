/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.spi;

import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.persister.walking.spi.AssociationVisitationStrategy;

public interface LoadPlanBuildingAssociationVisitationStrategy
extends AssociationVisitationStrategy {
    public LoadPlan buildLoadPlan();
}

