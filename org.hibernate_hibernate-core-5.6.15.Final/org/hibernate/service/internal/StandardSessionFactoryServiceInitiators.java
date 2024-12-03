/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service.internal;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.engine.query.spi.NativeQueryInterpreterInitiator;
import org.hibernate.engine.spi.CacheInitiator;
import org.hibernate.service.spi.SessionFactoryServiceInitiator;
import org.hibernate.stat.internal.StatisticsInitiator;

public final class StandardSessionFactoryServiceInitiators {
    public static List<SessionFactoryServiceInitiator> buildStandardServiceInitiatorList() {
        ArrayList<SessionFactoryServiceInitiator> serviceInitiators = new ArrayList<SessionFactoryServiceInitiator>();
        serviceInitiators.add(StatisticsInitiator.INSTANCE);
        serviceInitiators.add(CacheInitiator.INSTANCE);
        serviceInitiators.add(NativeQueryInterpreterInitiator.INSTANCE);
        return serviceInitiators;
    }

    private StandardSessionFactoryServiceInitiators() {
    }
}

