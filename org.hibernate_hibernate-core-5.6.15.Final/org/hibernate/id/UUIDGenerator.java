/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.util.Properties;
import java.util.UUID;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.UUIDGenerationStrategy;
import org.hibernate.id.uuid.StandardRandomStrategy;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.java.UUIDTypeDescriptor;

public class UUIDGenerator
implements IdentifierGenerator {
    public static final String UUID_GEN_STRATEGY = "uuid_gen_strategy";
    public static final String UUID_GEN_STRATEGY_CLASS = "uuid_gen_strategy_class";
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(UUIDGenerator.class);
    private UUIDGenerationStrategy strategy;
    private UUIDTypeDescriptor.ValueTransformer valueTransformer;

    public static UUIDGenerator buildSessionFactoryUniqueIdentifierGenerator() {
        UUIDGenerator generator = new UUIDGenerator();
        generator.strategy = StandardRandomStrategy.INSTANCE;
        generator.valueTransformer = UUIDTypeDescriptor.ToStringTransformer.INSTANCE;
        return generator;
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        String strategyClassName;
        this.strategy = (UUIDGenerationStrategy)params.get(UUID_GEN_STRATEGY);
        if (this.strategy == null && (strategyClassName = params.getProperty(UUID_GEN_STRATEGY_CLASS)) != null) {
            try {
                ClassLoaderService cls = serviceRegistry.getService(ClassLoaderService.class);
                Class strategyClass = cls.classForName(strategyClassName);
                try {
                    this.strategy = (UUIDGenerationStrategy)strategyClass.newInstance();
                }
                catch (Exception ignore) {
                    LOG.unableToInstantiateUuidGenerationStrategy(ignore);
                }
            }
            catch (ClassLoadingException ignore) {
                LOG.unableToLocateUuidGenerationStrategy(strategyClassName);
            }
        }
        if (this.strategy == null) {
            this.strategy = StandardRandomStrategy.INSTANCE;
        }
        if (UUID.class.isAssignableFrom(type.getReturnedClass())) {
            this.valueTransformer = UUIDTypeDescriptor.PassThroughTransformer.INSTANCE;
        } else if (String.class.isAssignableFrom(type.getReturnedClass())) {
            this.valueTransformer = UUIDTypeDescriptor.ToStringTransformer.INSTANCE;
        } else if (byte[].class.isAssignableFrom(type.getReturnedClass())) {
            this.valueTransformer = UUIDTypeDescriptor.ToBytesTransformer.INSTANCE;
        } else {
            throw new HibernateException("Unanticipated return type [" + type.getReturnedClass().getName() + "] for UUID conversion");
        }
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return this.valueTransformer.transform(this.strategy.generateUUID(session));
    }
}

