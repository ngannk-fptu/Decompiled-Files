/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.factory.internal;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.MappingException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.id.Assigned;
import org.hibernate.id.ForeignGenerator;
import org.hibernate.id.GUIDGenerator;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.IdentityGenerator;
import org.hibernate.id.IncrementGenerator;
import org.hibernate.id.SelectGenerator;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.id.SequenceHiLoGenerator;
import org.hibernate.id.SequenceIdentityGenerator;
import org.hibernate.id.UUIDGenerator;
import org.hibernate.id.UUIDHexGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.internal.FallbackBeanInstanceProducer;
import org.hibernate.resource.beans.spi.ManagedBeanRegistry;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.type.Type;

public class DefaultIdentifierGeneratorFactory
implements MutableIdentifierGeneratorFactory,
Serializable,
ServiceRegistryAwareService {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DefaultIdentifierGeneratorFactory.class);
    private final boolean ignoreBeanContainer;
    private ServiceRegistry serviceRegistry;
    private Dialect dialect;
    private final ConcurrentHashMap<String, Class> generatorStrategyToClassNameMap = new ConcurrentHashMap();
    private BeanContainer beanContainer;

    public DefaultIdentifierGeneratorFactory() {
        this(false);
    }

    public DefaultIdentifierGeneratorFactory(boolean ignoreBeanContainer) {
        this.ignoreBeanContainer = ignoreBeanContainer;
        this.register("uuid2", UUIDGenerator.class);
        this.register("guid", GUIDGenerator.class);
        this.register("uuid", UUIDHexGenerator.class);
        this.register("uuid.hex", UUIDHexGenerator.class);
        this.register("assigned", Assigned.class);
        this.register("identity", IdentityGenerator.class);
        this.register("select", SelectGenerator.class);
        this.register("sequence", SequenceStyleGenerator.class);
        this.register("seqhilo", SequenceHiLoGenerator.class);
        this.register("increment", IncrementGenerator.class);
        this.register("foreign", ForeignGenerator.class);
        this.register("sequence-identity", SequenceIdentityGenerator.class);
        this.register("enhanced-sequence", SequenceStyleGenerator.class);
        this.register("enhanced-table", TableGenerator.class);
    }

    @Override
    public void register(String strategy, Class generatorClass) {
        LOG.debugf("Registering IdentifierGenerator strategy [%s] -> [%s]", strategy, generatorClass.getName());
        Class previous = this.generatorStrategyToClassNameMap.put(strategy, generatorClass);
        if (previous != null) {
            LOG.debugf("    - overriding [%s]", previous.getName());
        }
    }

    @Override
    public Dialect getDialect() {
        return this.dialect;
    }

    @Override
    public void setDialect(Dialect dialect) {
    }

    @Override
    public IdentifierGenerator createIdentifierGenerator(String strategy, Type type, Properties config) {
        try {
            Class clazz = this.getIdentifierGeneratorClass(strategy);
            IdentifierGenerator identifierGenerator = this.beanContainer == null || this.generatorStrategyToClassNameMap.containsKey(strategy) ? (IdentifierGenerator)clazz.newInstance() : (IdentifierGenerator)this.beanContainer.getBean(clazz, new BeanContainer.LifecycleOptions(){

                @Override
                public boolean canUseCachedReferences() {
                    return false;
                }

                @Override
                public boolean useJpaCompliantCreation() {
                    return true;
                }
            }, FallbackBeanInstanceProducer.INSTANCE).getBeanInstance();
            identifierGenerator.configure(type, config, this.serviceRegistry);
            return identifierGenerator;
        }
        catch (Exception e) {
            String entityName = config.getProperty("entity_name");
            throw new MappingException(String.format("Could not instantiate id generator [entity-name=%s]", entityName), e);
        }
    }

    @Override
    public Class getIdentifierGeneratorClass(String strategy) {
        if ("hilo".equals(strategy)) {
            throw new UnsupportedOperationException("Support for 'hilo' generator has been removed");
        }
        String resolvedStrategy = "native".equals(strategy) ? this.getDialect().getNativeIdentifierGeneratorStrategy() : strategy;
        Class generatorClass = this.generatorStrategyToClassNameMap.get(resolvedStrategy);
        try {
            if (generatorClass == null) {
                ClassLoaderService cls = this.serviceRegistry.getService(ClassLoaderService.class);
                generatorClass = cls.classForName(resolvedStrategy);
            }
        }
        catch (ClassLoadingException e) {
            throw new MappingException(String.format("Could not interpret id generator strategy [%s]", strategy));
        }
        return generatorClass;
    }

    @Override
    public void injectServices(ServiceRegistryImplementor serviceRegistry) {
        boolean useNewIdentifierGenerators;
        this.serviceRegistry = serviceRegistry;
        this.dialect = serviceRegistry.getService(JdbcEnvironment.class).getDialect();
        ConfigurationService configService = serviceRegistry.getService(ConfigurationService.class);
        if (!this.ignoreBeanContainer) {
            this.beanContainer = serviceRegistry.getService(ManagedBeanRegistry.class).getBeanContainer();
        }
        if (!(useNewIdentifierGenerators = configService.getSetting("hibernate.id.new_generator_mappings", StandardConverters.BOOLEAN, Boolean.valueOf(true)).booleanValue())) {
            this.register("sequence", SequenceGenerator.class);
        }
    }
}

