/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
import org.hibernate.hql.spi.QueryTranslatorFactory;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class QueryTranslatorFactoryInitiator
implements StandardServiceInitiator<QueryTranslatorFactory> {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(QueryTranslatorFactoryInitiator.class);
    public static final QueryTranslatorFactoryInitiator INSTANCE = new QueryTranslatorFactoryInitiator();

    @Override
    public QueryTranslatorFactory initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        StrategySelector strategySelector = registry.getService(StrategySelector.class);
        QueryTranslatorFactory factory = strategySelector.resolveDefaultableStrategy(QueryTranslatorFactory.class, configurationValues.get("hibernate.query.factory_class"), ASTQueryTranslatorFactory.INSTANCE);
        log.debug("QueryTranslatorFactory: " + factory);
        return factory;
    }

    @Override
    public Class<QueryTranslatorFactory> getServiceInitiated() {
        return QueryTranslatorFactory.class;
    }
}

