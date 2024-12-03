/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.internal;

import org.hibernate.loader.plan.spi.EntityQuerySpace;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.loader.plan.spi.QuerySpace;
import org.hibernate.loader.plan.spi.QuerySpaces;
import org.hibernate.loader.plan.spi.Return;

public class RootHelper {
    public static final RootHelper INSTANCE = new RootHelper();

    private RootHelper() {
    }

    public <T extends Return> T extractRootReturn(LoadPlan loadPlan, Class<T> returnType) {
        if (loadPlan.getReturns().size() == 0) {
            throw new IllegalStateException("LoadPlan contained no root returns");
        }
        if (loadPlan.getReturns().size() > 1) {
            throw new IllegalStateException("LoadPlan contained more than one root returns");
        }
        Return rootReturn = loadPlan.getReturns().get(0);
        if (!returnType.isInstance(rootReturn)) {
            throw new IllegalStateException(String.format("Unexpected LoadPlan root return; expecting %s, but found %s", returnType.getName(), rootReturn.getClass().getName()));
        }
        return (T)rootReturn;
    }

    public <T extends QuerySpace> T extractRootQuerySpace(QuerySpaces querySpaces, Class<EntityQuerySpace> returnType) {
        if (querySpaces.getRootQuerySpaces().size() == 0) {
            throw new IllegalStateException("LoadPlan contained no root query-spaces");
        }
        if (querySpaces.getRootQuerySpaces().size() > 1) {
            throw new IllegalStateException("LoadPlan contained more than one root query-space");
        }
        QuerySpace querySpace = querySpaces.getRootQuerySpaces().get(0);
        if (!returnType.isInstance(querySpace)) {
            throw new IllegalStateException(String.format("Unexpected LoadPlan root query-space; expecting %s, but found %s", returnType.getName(), querySpace.getClass().getName()));
        }
        return (T)querySpace;
    }
}

