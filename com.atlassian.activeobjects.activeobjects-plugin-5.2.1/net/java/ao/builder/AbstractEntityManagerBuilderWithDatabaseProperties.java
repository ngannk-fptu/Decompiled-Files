/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.builder;

import java.util.Objects;
import net.java.ao.DefaultSchemaConfiguration;
import net.java.ao.EntityManager;
import net.java.ao.EntityManagerConfiguration;
import net.java.ao.SchemaConfiguration;
import net.java.ao.builder.BuilderDatabaseProperties;
import net.java.ao.builder.SimpleNameConverters;
import net.java.ao.schema.CamelCaseFieldNameConverter;
import net.java.ao.schema.CamelCaseTableNameConverter;
import net.java.ao.schema.DefaultIndexNameConverter;
import net.java.ao.schema.DefaultSequenceNameConverter;
import net.java.ao.schema.DefaultTriggerNameConverter;
import net.java.ao.schema.DefaultUniqueNameConverter;
import net.java.ao.schema.FieldNameConverter;
import net.java.ao.schema.IndexNameConverter;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.SequenceNameConverter;
import net.java.ao.schema.TableAnnotationTableNameConverter;
import net.java.ao.schema.TableNameConverter;
import net.java.ao.schema.TriggerNameConverter;
import net.java.ao.schema.UniqueNameConverter;
import net.java.ao.schema.info.CachingEntityInfoResolverFactory;
import net.java.ao.schema.info.EntityInfoResolverFactory;

public abstract class AbstractEntityManagerBuilderWithDatabaseProperties<B extends AbstractEntityManagerBuilderWithDatabaseProperties> {
    private final BuilderDatabaseProperties databaseProperties;
    private final BuilderEntityManagerConfiguration configuration;

    AbstractEntityManagerBuilderWithDatabaseProperties(BuilderDatabaseProperties databaseProperties) {
        this(databaseProperties, new BuilderEntityManagerConfiguration());
    }

    AbstractEntityManagerBuilderWithDatabaseProperties(BuilderDatabaseProperties databaseProperties, BuilderEntityManagerConfiguration configuration) {
        this.databaseProperties = Objects.requireNonNull(databaseProperties, "databaseProperties can't be null");
        this.configuration = Objects.requireNonNull(configuration, "configuration can't be null");
    }

    public B schema(String schema) {
        this.databaseProperties.setSchema(schema);
        return this.cast();
    }

    public B tableNameConverter(TableNameConverter tableNameConverter) {
        this.configuration.setTableNameConverter(Objects.requireNonNull(tableNameConverter, "tableNameConverter can't be null"));
        return this.cast();
    }

    public B fieldNameConverter(FieldNameConverter fieldNameConverter) {
        this.configuration.setFieldNameConverter(Objects.requireNonNull(fieldNameConverter, "fieldNameConverter can't be null"));
        return this.cast();
    }

    public B sequenceNameConverter(SequenceNameConverter sequenceNameConverter) {
        this.configuration.setSequenceNameConverter(Objects.requireNonNull(sequenceNameConverter, "sequenceNameConverter can't be null"));
        return this.cast();
    }

    public B triggerNameConverter(TriggerNameConverter triggerNameConverter) {
        this.configuration.setTriggerNameConverter(Objects.requireNonNull(triggerNameConverter, "triggerNameConverter can't be null"));
        return this.cast();
    }

    public B indexNameConverter(IndexNameConverter indexNameConverter) {
        this.configuration.setIndexNameConverter(Objects.requireNonNull(indexNameConverter, "indexNameConverter can't be null"));
        return this.cast();
    }

    public B uniqueNameConverter(UniqueNameConverter uniqueNameConverter) {
        this.configuration.setUniqueNameConverter(Objects.requireNonNull(uniqueNameConverter, "uniqueNameConverter can't be null"));
        return this.cast();
    }

    public B schemaConfiguration(SchemaConfiguration schemaConfiguration) {
        this.configuration.setSchemaConfiguration(schemaConfiguration);
        return this.cast();
    }

    final BuilderDatabaseProperties getDatabaseProperties() {
        return this.databaseProperties;
    }

    final BuilderEntityManagerConfiguration getEntityManagerConfiguration() {
        return this.configuration;
    }

    public abstract EntityManager build();

    private B cast() {
        return (B)this;
    }

    static class BuilderEntityManagerConfiguration
    implements EntityManagerConfiguration {
        private SchemaConfiguration schemaConfiguration;
        private TableNameConverter tableNameConverter;
        private FieldNameConverter fieldNameConverter;
        private SequenceNameConverter sequenceNameConverter;
        private TriggerNameConverter triggerNameConverter;
        private IndexNameConverter indexNameConverter;
        private UniqueNameConverter uniqueNameConverter;
        private EntityInfoResolverFactory entityInfoResolverFactory;

        BuilderEntityManagerConfiguration() {
        }

        @Override
        public boolean useWeakCache() {
            return false;
        }

        @Override
        public NameConverters getNameConverters() {
            return new SimpleNameConverters(this.getTableNameConverter(), this.getFieldNameConverter(), this.getSequenceNameConverter(), this.getTriggerNameConverter(), this.getIndexNameConverter(), this.getUniqueNameConverter());
        }

        private TableNameConverter getTableNameConverter() {
            return this.tableNameConverter != null ? this.tableNameConverter : BuilderEntityManagerConfiguration.defaultTableNameConverter();
        }

        private static TableNameConverter defaultTableNameConverter() {
            return new TableAnnotationTableNameConverter(new CamelCaseTableNameConverter());
        }

        private SequenceNameConverter getSequenceNameConverter() {
            return this.sequenceNameConverter != null ? this.sequenceNameConverter : this.defaultSequenceNameConverter();
        }

        private TriggerNameConverter getTriggerNameConverter() {
            return this.triggerNameConverter != null ? this.triggerNameConverter : this.defaultTriggerNameConverter();
        }

        private UniqueNameConverter getUniqueNameConverter() {
            return this.uniqueNameConverter != null ? this.uniqueNameConverter : this.defaultUniqueNameConverter();
        }

        private UniqueNameConverter defaultUniqueNameConverter() {
            return new DefaultUniqueNameConverter();
        }

        private IndexNameConverter getIndexNameConverter() {
            return this.indexNameConverter != null ? this.indexNameConverter : this.defaultIndexNameConverter();
        }

        private IndexNameConverter defaultIndexNameConverter() {
            return new DefaultIndexNameConverter();
        }

        private TriggerNameConverter defaultTriggerNameConverter() {
            return new DefaultTriggerNameConverter();
        }

        private SequenceNameConverter defaultSequenceNameConverter() {
            return new DefaultSequenceNameConverter();
        }

        private FieldNameConverter getFieldNameConverter() {
            return this.fieldNameConverter != null ? this.fieldNameConverter : BuilderEntityManagerConfiguration.defaultFieldNameConverter();
        }

        private static CamelCaseFieldNameConverter defaultFieldNameConverter() {
            return new CamelCaseFieldNameConverter();
        }

        public void setTableNameConverter(TableNameConverter tableNameConverter) {
            this.tableNameConverter = tableNameConverter;
        }

        public void setFieldNameConverter(FieldNameConverter fieldNameConverter) {
            this.fieldNameConverter = fieldNameConverter;
        }

        public void setSequenceNameConverter(SequenceNameConverter sequenceNameConverter) {
            this.sequenceNameConverter = sequenceNameConverter;
        }

        public void setTriggerNameConverter(TriggerNameConverter triggerNameConverter) {
            this.triggerNameConverter = triggerNameConverter;
        }

        public void setIndexNameConverter(IndexNameConverter indexNameConverter) {
            this.indexNameConverter = indexNameConverter;
        }

        public void setUniqueNameConverter(UniqueNameConverter uniqueNameConverter) {
            this.uniqueNameConverter = uniqueNameConverter;
        }

        @Override
        public SchemaConfiguration getSchemaConfiguration() {
            return this.schemaConfiguration != null ? this.schemaConfiguration : new DefaultSchemaConfiguration();
        }

        public void setSchemaConfiguration(SchemaConfiguration schemaConfiguration) {
            this.schemaConfiguration = schemaConfiguration;
        }

        @Override
        public EntityInfoResolverFactory getEntityInfoResolverFactory() {
            return this.entityInfoResolverFactory != null ? this.entityInfoResolverFactory : BuilderEntityManagerConfiguration.defaultSchemaInfoResolverFactory();
        }

        public void setEntityInfoResolverFactory(EntityInfoResolverFactory entityInfoResolverFactory) {
            this.entityInfoResolverFactory = entityInfoResolverFactory;
        }

        private static EntityInfoResolverFactory defaultSchemaInfoResolverFactory() {
            return new CachingEntityInfoResolverFactory();
        }
    }
}

