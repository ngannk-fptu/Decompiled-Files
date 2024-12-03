/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.util.Properties;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.id.enhanced.AccessCallback;
import org.hibernate.id.enhanced.LegacyHiLoAlgorithmOptimizer;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

@Deprecated
public class SequenceHiLoGenerator
extends SequenceGenerator {
    public static final String MAX_LO = "max_lo";
    private int maxLo;
    private LegacyHiLoAlgorithmOptimizer hiloOptimizer;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        super.configure(type, params, serviceRegistry);
        this.maxLo = ConfigurationHelper.getInt(MAX_LO, params, 9);
        if (this.maxLo >= 1) {
            this.hiloOptimizer = new LegacyHiLoAlgorithmOptimizer(this.getIdentifierType().getReturnedClass(), this.maxLo);
        }
    }

    @Override
    public synchronized Serializable generate(final SharedSessionContractImplementor session, Object obj) {
        if (this.maxLo < 1) {
            IntegralDataTypeHolder value = null;
            while (value == null || value.lt(0L)) {
                value = super.generateHolder(session);
            }
            return value.makeValue();
        }
        return this.hiloOptimizer.generate(new AccessCallback(){

            @Override
            public IntegralDataTypeHolder getNextValue() {
                return SequenceHiLoGenerator.this.generateHolder(session);
            }

            @Override
            public String getTenantIdentifier() {
                return session.getTenantIdentifier();
            }
        });
    }

    LegacyHiLoAlgorithmOptimizer getHiloOptimizer() {
        return this.hiloOptimizer;
    }
}

