/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.spi;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
import org.hibernate.loader.plan.spi.FetchSource;
import org.hibernate.persister.walking.spi.AssociationKey;

public interface LoadPlanBuildingContext {
    public SessionFactoryImplementor getSessionFactory();

    public ExpandingQuerySpaces getQuerySpaces();

    public FetchSource registeredFetchSource(AssociationKey var1);
}

