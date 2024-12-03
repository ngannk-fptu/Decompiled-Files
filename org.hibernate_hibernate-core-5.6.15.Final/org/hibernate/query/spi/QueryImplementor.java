/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.spi;

import java.io.Serializable;
import org.hibernate.Incubating;
import org.hibernate.query.Query;
import org.hibernate.query.spi.QueryProducerImplementor;

@Incubating
public interface QueryImplementor<R>
extends Query<R> {
    @Override
    public QueryProducerImplementor getProducer();

    public void setOptionalId(Serializable var1);

    public void setOptionalEntityName(String var1);

    public void setOptionalObject(Object var1);
}

