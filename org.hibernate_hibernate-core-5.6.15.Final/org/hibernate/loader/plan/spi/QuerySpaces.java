/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import java.util.List;
import org.hibernate.loader.plan.spi.QuerySpace;

public interface QuerySpaces {
    public List<QuerySpace> getRootQuerySpaces();

    public QuerySpace findQuerySpaceByUid(String var1);

    public QuerySpace getQuerySpaceByUid(String var1);
}

