/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Ticker
 *  org.hibernate.MappingException
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 *  org.hibernate.id.Configurable
 *  org.hibernate.id.IdentifierGenerator
 *  org.hibernate.service.ServiceRegistry
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.impl.hibernate;

import com.atlassian.confluence.impl.hibernate.ResettableTableHiLoGenerator;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import java.io.Serializable;
import java.util.Properties;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

public class ResettableTableHiLoGeneratorV5
extends ResettableTableHiLoGenerator
implements IdentifierGenerator,
Configurable {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        try (Ticker timer = ResettableTableHiLoGeneratorV5.startTimer(object.getClass());){
            Serializable serializable = super.generate(session, object);
            return serializable;
        }
    }

    private static Ticker startTimer(Class<?> entityType) {
        return Metrics.metric((String)ResettableTableHiLoGeneratorV5.class.getSimpleName()).tag("entityType", entityType.getTypeName()).startTimer();
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        super.configure(type, params, serviceRegistry);
    }
}

