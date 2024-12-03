/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi;

import java.util.Map;
import org.hibernate.engine.query.spi.EntityGraphQueryHint;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.spi.FilterTranslator;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.service.Service;

public interface QueryTranslatorFactory
extends Service {
    public QueryTranslator createQueryTranslator(String var1, String var2, Map var3, SessionFactoryImplementor var4, EntityGraphQueryHint var5);

    public FilterTranslator createFilterTranslator(String var1, String var2, Map var3, SessionFactoryImplementor var4);
}

