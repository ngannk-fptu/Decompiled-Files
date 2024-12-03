/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.sql.Connection;
import org.hibernate.StatelessSession;

public interface StatelessSessionBuilder<T extends StatelessSessionBuilder> {
    public StatelessSession openStatelessSession();

    public T connection(Connection var1);

    public T tenantIdentifier(String var1);

    default public T setQueryParameterValidation(boolean enabled) {
        return (T)this;
    }
}

