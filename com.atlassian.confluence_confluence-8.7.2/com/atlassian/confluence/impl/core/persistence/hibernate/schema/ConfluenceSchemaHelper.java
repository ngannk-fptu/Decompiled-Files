/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.db.HibernateConfig
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  net.jcip.annotations.NotThreadSafe
 *  org.hibernate.boot.Metadata
 *  org.hibernate.boot.MetadataSources
 *  org.hibernate.boot.registry.StandardServiceRegistryBuilder
 *  org.hibernate.cfg.Configuration
 *  org.hibernate.service.ServiceRegistry
 *  org.hibernate.tool.hbm2ddl.SchemaUpdate
 *  org.hibernate.tool.schema.TargetType
 *  org.springframework.core.io.Resource
 */
package com.atlassian.confluence.impl.core.persistence.hibernate.schema;

import com.atlassian.annotations.Internal;
import com.atlassian.config.ConfigurationException;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.impl.core.persistence.hibernate.HibernateMetadataSource;
import com.atlassian.confluence.impl.core.persistence.hibernate.schema.ConfluenceThreadLocalStdoutSuppresser;
import com.atlassian.confluence.util.Cleanup;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;
import net.jcip.annotations.NotThreadSafe;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.schema.TargetType;
import org.springframework.core.io.Resource;

@NotThreadSafe
@Internal
public class ConfluenceSchemaHelper {
    public static final String COMPONENT_REFERENCE = "schemaHelper";
    private ResettableLazyReference<Configuration> configuration = new ResettableLazyReference<Configuration>(){

        protected Configuration create() throws Exception {
            Objects.requireNonNull(ConfluenceSchemaHelper.this.hibernateProperties, "usage of deprecated ctor, either setHibernateProperties or setHibernateConfig should have been set");
            Configuration configuration = new Configuration();
            configuration.setProperties(ConfluenceSchemaHelper.this.hibernateProperties.get());
            for (Resource mappingResource : ConfluenceSchemaHelper.this.mappingResources) {
                configuration.addInputStream(mappingResource.getInputStream());
            }
            return configuration;
        }
    };
    private final Iterable<Resource> mappingResources;
    private final Supplier<Properties> hibernateProperties;
    private final HibernateMetadataSource hibernateMetadataSource;

    public ConfluenceSchemaHelper(Iterable<Resource> mappingResources, HibernateConfig hibernateConfig, HibernateMetadataSource hibernateMetadataSource) {
        this.mappingResources = mappingResources;
        this.hibernateProperties = () -> ((HibernateConfig)hibernateConfig).getHibernateProperties();
        this.hibernateMetadataSource = hibernateMetadataSource;
    }

    public Configuration getConfiguration() {
        return (Configuration)this.configuration.get();
    }

    public void updateSchemaIfNeeded() throws ConfigurationException {
        this.updateSchemaIfNeeded(false);
    }

    public void validateSchemaUpdateIfNeeded() throws ConfigurationException {
        try (Cleanup cleanup = ConfluenceThreadLocalStdoutSuppresser.temporarilySuppressStdout();){
            Metadata metadata = this.hibernateMetadataSource.getMetadata();
            new SchemaUpdate().execute(EnumSet.of(TargetType.STDOUT), metadata);
        }
        catch (Exception e) {
            throw new ConfigurationException("Cannot update schema", (Throwable)e);
        }
    }

    public void updateSchemaIfNeeded(boolean showDDL) throws ConfigurationException {
        try {
            EnumSet<TargetType> targetTypes = EnumSet.of(TargetType.DATABASE);
            if (showDDL) {
                targetTypes.add(TargetType.STDOUT);
            }
            Metadata metadata = this.hibernateMetadataSource.getMetadata();
            new SchemaUpdate().execute(targetTypes, metadata);
        }
        catch (Exception e) {
            throw new ConfigurationException("Cannot update schema", (Throwable)e);
        }
    }

    public void updateVersionHistorySchemaIfNeeded() {
        Metadata metadata = new MetadataSources((ServiceRegistry)new StandardServiceRegistryBuilder().applySettings((Map)this.hibernateProperties.get()).build()).addResource("com/atlassian/confluence/core/VersionHistory.hbm.xml").buildMetadata();
        new SchemaUpdate().execute(EnumSet.of(TargetType.STDOUT, TargetType.DATABASE), metadata);
    }

    public void reset() {
        this.configuration.reset();
    }
}

