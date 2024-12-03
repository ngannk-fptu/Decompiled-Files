/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.annotations.common.reflection.XProperty
 */
package org.hibernate.mapping;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.hibernate.FetchMode;
import org.hibernate.MappingException;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.model.convert.internal.ClassBasedConverterDescriptor;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;
import org.hibernate.boot.model.convert.spi.JpaAttributeConverterCreationContext;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.IdentityGenerator;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Formula;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.mapping.ValueVisitor;
import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.hibernate.resource.beans.spi.ManagedBeanRegistry;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.BinaryType;
import org.hibernate.type.RowVersionType;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.JdbcTypeNameMapper;
import org.hibernate.type.descriptor.converter.AttributeConverterSqlTypeDescriptorAdapter;
import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
import org.hibernate.type.descriptor.java.BasicJavaDescriptor;
import org.hibernate.type.descriptor.java.spi.JavaTypeDescriptorRegistry;
import org.hibernate.type.descriptor.sql.LobTypeMappings;
import org.hibernate.type.descriptor.sql.NationalizedTypeMappings;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.usertype.DynamicParameterizedType;

public class SimpleValue
implements KeyValue {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(SimpleValue.class);
    public static final String DEFAULT_ID_GEN_STRATEGY = "assigned";
    private final MetadataImplementor metadata;
    private final List<Selectable> columns = new ArrayList<Selectable>();
    private final List<Boolean> insertability = new ArrayList<Boolean>();
    private final List<Boolean> updatability = new ArrayList<Boolean>();
    private String typeName;
    private Properties typeParameters;
    private boolean isVersion;
    private boolean isNationalized;
    private boolean isLob;
    private Properties identifierGeneratorProperties;
    private String identifierGeneratorStrategy = "assigned";
    private String nullValue;
    private Table table;
    private String foreignKeyName;
    private String foreignKeyDefinition;
    private boolean alternateUniqueKey;
    private boolean cascadeDeleteEnabled;
    private ConverterDescriptor attributeConverterDescriptor;
    private Type type;
    private IdentifierGenerator identifierGenerator;

    @Deprecated
    public SimpleValue(MetadataImplementor metadata) {
        this.metadata = metadata;
    }

    @Deprecated
    public SimpleValue(MetadataImplementor metadata, Table table) {
        this(metadata);
        this.table = table;
    }

    @Deprecated
    public SimpleValue(MetadataBuildingContext buildingContext) {
        this(buildingContext.getMetadataCollector());
    }

    public SimpleValue(MetadataBuildingContext buildingContext, Table table) {
        this.metadata = buildingContext.getMetadataCollector();
        this.table = table;
    }

    public MetadataImplementor getMetadata() {
        return this.metadata;
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return this.getMetadata().getMetadataBuildingOptions().getServiceRegistry();
    }

    @Override
    public boolean isCascadeDeleteEnabled() {
        return this.cascadeDeleteEnabled;
    }

    public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled) {
        this.cascadeDeleteEnabled = cascadeDeleteEnabled;
    }

    public void addColumn(Column column) {
        this.addColumn(column, true, true);
    }

    public void addColumn(Column column, boolean isInsertable, boolean isUpdatable) {
        int index = this.columns.indexOf(column);
        if (index == -1) {
            this.columns.add(column);
            this.insertability.add(isInsertable);
            this.updatability.add(isUpdatable);
        } else {
            if (this.insertability.get(index) != isInsertable) {
                throw new IllegalStateException("Same column is added more than once with different values for isInsertable");
            }
            if (this.updatability.get(index) != isUpdatable) {
                throw new IllegalStateException("Same column is added more than once with different values for isUpdatable");
            }
        }
        column.setValue(this);
        column.setTypeIndex(this.columns.size() - 1);
    }

    public void addFormula(Formula formula) {
        this.columns.add(formula);
        this.insertability.add(false);
        this.updatability.add(false);
    }

    @Override
    public boolean hasFormula() {
        Iterator<Selectable> iter = this.getColumnIterator();
        while (iter.hasNext()) {
            Selectable o = iter.next();
            if (!(o instanceof Formula)) continue;
            return true;
        }
        return false;
    }

    @Override
    public int getColumnSpan() {
        return this.columns.size();
    }

    @Override
    public Iterator<Selectable> getColumnIterator() {
        return this.columns.iterator();
    }

    public List getConstraintColumns() {
        return this.columns;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        if (typeName != null && typeName.startsWith("converted::")) {
            String converterClassName = typeName.substring("converted::".length());
            ClassLoaderService cls = this.getMetadata().getMetadataBuildingOptions().getServiceRegistry().getService(ClassLoaderService.class);
            try {
                Class converterClass = cls.classForName(converterClassName);
                this.attributeConverterDescriptor = new ClassBasedConverterDescriptor(converterClass, false, ((InFlightMetadataCollector)this.getMetadata()).getClassmateContext());
                return;
            }
            catch (Exception e) {
                log.logBadHbmAttributeConverterType(typeName, e.getMessage());
            }
        }
        this.typeName = typeName;
    }

    public void makeVersion() {
        this.isVersion = true;
    }

    public boolean isVersion() {
        return this.isVersion;
    }

    public void makeNationalized() {
        this.isNationalized = true;
    }

    public boolean isNationalized() {
        return this.isNationalized;
    }

    public void makeLob() {
        this.isLob = true;
    }

    public boolean isLob() {
        return this.isLob;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    @Override
    public void createForeignKey() throws MappingException {
    }

    @Override
    public void createForeignKeyOfEntity(String entityName) {
        if (!this.hasFormula() && !"none".equals(this.getForeignKeyName())) {
            ForeignKey fk = this.table.createForeignKey(this.getForeignKeyName(), this.getConstraintColumns(), entityName, this.getForeignKeyDefinition());
            fk.setCascadeDeleteEnabled(this.cascadeDeleteEnabled);
        }
    }

    public IdentifierGenerator getIdentifierGenerator() {
        return this.identifierGenerator;
    }

    @Override
    public IdentifierGenerator createIdentifierGenerator(IdentifierGeneratorFactory identifierGeneratorFactory, Dialect dialect, RootClass rootClass) throws MappingException {
        return this.createIdentifierGenerator(identifierGeneratorFactory, dialect, null, null, rootClass);
    }

    @Override
    public IdentifierGenerator createIdentifierGenerator(IdentifierGeneratorFactory identifierGeneratorFactory, Dialect dialect, String defaultCatalog, String defaultSchema, RootClass rootClass) throws MappingException {
        if (this.identifierGenerator != null) {
            return this.identifierGenerator;
        }
        Properties params = new Properties();
        if (defaultSchema != null) {
            params.setProperty("schema", defaultSchema);
        }
        if (defaultCatalog != null) {
            params.setProperty("catalog", defaultCatalog);
        }
        if (rootClass != null) {
            params.setProperty("entity_name", rootClass.getEntityName());
            params.setProperty("jpa_entity_name", rootClass.getJpaEntityName());
        }
        String tableName = this.getTable().getQuotedName(dialect);
        params.setProperty("target_table", tableName);
        String columnName = ((Column)this.getColumnIterator().next()).getQuotedName(dialect);
        params.setProperty("target_column", columnName);
        if (rootClass != null) {
            StringBuilder tables = new StringBuilder();
            Iterator<Table> iter = rootClass.getIdentityTables().iterator();
            while (iter.hasNext()) {
                Table table = iter.next();
                tables.append(table.getQuotedName(dialect));
                if (!iter.hasNext()) continue;
                tables.append(", ");
            }
            params.setProperty("identity_tables", tables.toString());
        } else {
            params.setProperty("identity_tables", tableName);
        }
        if (this.identifierGeneratorProperties != null) {
            params.putAll((Map<?, ?>)this.identifierGeneratorProperties);
        }
        ConfigurationService cs = this.metadata.getMetadataBuildingOptions().getServiceRegistry().getService(ConfigurationService.class);
        params.put("hibernate.id.optimizer.pooled.prefer_lo", cs.getSetting("hibernate.id.optimizer.pooled.prefer_lo", StandardConverters.BOOLEAN, Boolean.valueOf(false)));
        if (cs.getSettings().get("hibernate.id.optimizer.pooled.preferred") != null) {
            params.put("hibernate.id.optimizer.pooled.preferred", cs.getSettings().get("hibernate.id.optimizer.pooled.preferred"));
        }
        identifierGeneratorFactory.setDialect(dialect);
        this.identifierGenerator = identifierGeneratorFactory.createIdentifierGenerator(this.identifierGeneratorStrategy, this.getType(), params);
        return this.identifierGenerator;
    }

    @Override
    public boolean isUpdateable() {
        return true;
    }

    @Override
    public FetchMode getFetchMode() {
        return FetchMode.SELECT;
    }

    public Properties getIdentifierGeneratorProperties() {
        return this.identifierGeneratorProperties;
    }

    @Override
    public String getNullValue() {
        return this.nullValue;
    }

    @Override
    public Table getTable() {
        return this.table;
    }

    public String getIdentifierGeneratorStrategy() {
        return this.identifierGeneratorStrategy;
    }

    @Override
    public boolean isIdentityColumn(IdentifierGeneratorFactory identifierGeneratorFactory, Dialect dialect) {
        identifierGeneratorFactory.setDialect(dialect);
        return IdentityGenerator.class.isAssignableFrom(identifierGeneratorFactory.getIdentifierGeneratorClass(this.identifierGeneratorStrategy));
    }

    public void setIdentifierGeneratorProperties(Properties identifierGeneratorProperties) {
        this.identifierGeneratorProperties = identifierGeneratorProperties;
    }

    public void setIdentifierGeneratorProperties(Map identifierGeneratorProperties) {
        if (identifierGeneratorProperties != null) {
            Properties properties = new Properties();
            properties.putAll((Map<?, ?>)identifierGeneratorProperties);
            this.setIdentifierGeneratorProperties(properties);
        }
    }

    public void setIdentifierGeneratorStrategy(String identifierGeneratorStrategy) {
        this.identifierGeneratorStrategy = identifierGeneratorStrategy;
    }

    public void setNullValue(String nullValue) {
        this.nullValue = nullValue;
    }

    public String getForeignKeyName() {
        return this.foreignKeyName;
    }

    public void setForeignKeyName(String foreignKeyName) {
        this.foreignKeyName = foreignKeyName;
    }

    public String getForeignKeyDefinition() {
        return this.foreignKeyDefinition;
    }

    public void setForeignKeyDefinition(String foreignKeyDefinition) {
        this.foreignKeyDefinition = foreignKeyDefinition;
    }

    @Override
    public boolean isAlternateUniqueKey() {
        return this.alternateUniqueKey;
    }

    public void setAlternateUniqueKey(boolean unique) {
        this.alternateUniqueKey = unique;
    }

    @Override
    public boolean isNullable() {
        Iterator<Selectable> itr = this.getColumnIterator();
        while (itr.hasNext()) {
            Selectable selectable = itr.next();
            if (selectable instanceof Formula) {
                return true;
            }
            if (((Column)selectable).isNullable()) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isSimpleValue() {
        return true;
    }

    @Override
    public boolean isValid(Mapping mapping) throws MappingException {
        return this.getColumnSpan() == this.getType().getColumnSpan(mapping);
    }

    @Override
    public Type getType() throws MappingException {
        if (this.type != null) {
            return this.type;
        }
        if (this.typeName == null) {
            throw new MappingException("No type name");
        }
        if (this.typeParameters != null && Boolean.valueOf(this.typeParameters.getProperty("org.hibernate.type.ParameterType.dynamic")).booleanValue() && this.typeParameters.get("org.hibernate.type.ParameterType") == null) {
            this.createParameterImpl();
        }
        Type result = this.getMetadata().getTypeConfiguration().getTypeResolver().heuristicType(this.typeName, this.typeParameters);
        if (this.isVersion && BinaryType.class.isInstance(result)) {
            log.debug("version is BinaryType; changing to RowVersionType");
            result = RowVersionType.INSTANCE;
        }
        if (result == null) {
            String msg = "Could not determine type for: " + this.typeName;
            if (this.table != null) {
                msg = msg + ", at table: " + this.table.getName();
            }
            if (this.columns != null && this.columns.size() > 0) {
                msg = msg + ", for columns: " + this.columns;
            }
            throw new MappingException(msg);
        }
        this.type = result;
        return this.type;
    }

    @Override
    public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
        if (this.typeName != null) {
            return;
        }
        if (this.type != null) {
            return;
        }
        if (this.attributeConverterDescriptor == null) {
            if (className == null) {
                throw new MappingException("Attribute types for a dynamic entity must be explicitly specified: " + propertyName);
            }
            this.typeName = ReflectHelper.reflectedPropertyClass(className, propertyName, this.getMetadata().getMetadataBuildingOptions().getServiceRegistry().getService(ClassLoaderService.class)).getName();
            return;
        }
        this.type = this.buildAttributeConverterTypeAdapter();
    }

    private Type buildAttributeConverterTypeAdapter() {
        JpaAttributeConverter jpaAttributeConverter = this.attributeConverterDescriptor.createJpaAttributeConverter(new JpaAttributeConverterCreationContext(){

            @Override
            public ManagedBeanRegistry getManagedBeanRegistry() {
                return SimpleValue.this.getMetadata().getMetadataBuildingOptions().getServiceRegistry().getService(ManagedBeanRegistry.class);
            }

            @Override
            public JavaTypeDescriptorRegistry getJavaTypeDescriptorRegistry() {
                return SimpleValue.this.metadata.getTypeConfiguration().getJavaTypeDescriptorRegistry();
            }
        });
        BasicJavaDescriptor entityAttributeJavaTypeDescriptor = jpaAttributeConverter.getDomainJavaTypeDescriptor();
        SqlTypeDescriptor recommendedSqlType = jpaAttributeConverter.getRelationalJavaTypeDescriptor().getJdbcRecommendedSqlType(this.metadata::getTypeConfiguration);
        int jdbcTypeCode = recommendedSqlType.getSqlType();
        if (this.isLob()) {
            if (LobTypeMappings.isMappedToKnownLobCode(jdbcTypeCode)) {
                jdbcTypeCode = LobTypeMappings.getLobCodeTypeMapping(jdbcTypeCode);
            } else if (Serializable.class.isAssignableFrom(entityAttributeJavaTypeDescriptor.getJavaType())) {
                jdbcTypeCode = 2004;
            } else {
                throw new IllegalArgumentException(String.format(Locale.ROOT, "JDBC type-code [%s (%s)] not known to have a corresponding LOB equivalent, and Java type is not Serializable (to use BLOB)", jdbcTypeCode, JdbcTypeNameMapper.getTypeName(jdbcTypeCode)));
            }
        }
        if (this.isNationalized()) {
            jdbcTypeCode = NationalizedTypeMappings.toNationalizedTypeCode(jdbcTypeCode);
        }
        SqlTypeDescriptor sqlTypeDescriptor = this.getMetadata().getMetadataBuildingOptions().getServiceRegistry().getService(JdbcServices.class).getJdbcEnvironment().getDialect().remapSqlTypeDescriptor(this.metadata.getTypeConfiguration().getSqlTypeDescriptorRegistry().getDescriptor(jdbcTypeCode));
        AttributeConverterSqlTypeDescriptorAdapter sqlTypeDescriptorAdapter = new AttributeConverterSqlTypeDescriptorAdapter(jpaAttributeConverter, sqlTypeDescriptor, jpaAttributeConverter.getRelationalJavaTypeDescriptor());
        String name = "converted::" + jpaAttributeConverter.getConverterJavaTypeDescriptor().getJavaType().getName();
        String description = String.format("BasicType adapter for AttributeConverter<%s,%s>", jpaAttributeConverter.getDomainJavaTypeDescriptor().getJavaType().getSimpleName(), jpaAttributeConverter.getRelationalJavaTypeDescriptor().getJavaType().getSimpleName());
        return new AttributeConverterTypeAdapter(name, description, jpaAttributeConverter, sqlTypeDescriptorAdapter, jpaAttributeConverter.getDomainJavaTypeDescriptor().getJavaType(), jpaAttributeConverter.getRelationalJavaTypeDescriptor().getJavaType(), entityAttributeJavaTypeDescriptor);
    }

    public boolean isTypeSpecified() {
        return this.typeName != null;
    }

    public void setTypeParameters(Properties parameterMap) {
        this.typeParameters = parameterMap;
    }

    public void setTypeParameters(Map<String, String> parameters) {
        if (parameters != null) {
            Properties properties = new Properties();
            properties.putAll(parameters);
            this.setTypeParameters(properties);
        }
    }

    public Properties getTypeParameters() {
        return this.typeParameters;
    }

    public void copyTypeFrom(SimpleValue sourceValue) {
        this.setTypeName(sourceValue.getTypeName());
        this.setTypeParameters(sourceValue.getTypeParameters());
        this.type = sourceValue.type;
        this.attributeConverterDescriptor = sourceValue.attributeConverterDescriptor;
    }

    @Override
    public boolean isSame(Value other) {
        return this == other || other instanceof SimpleValue && this.isSame((SimpleValue)other);
    }

    protected static boolean isSame(Value v1, Value v2) {
        return v1 == v2 || v1 != null && v2 != null && v1.isSame(v2);
    }

    public boolean isSame(SimpleValue other) {
        return Objects.equals(this.columns, other.columns) && Objects.equals(this.typeName, other.typeName) && Objects.equals(this.typeParameters, other.typeParameters) && Objects.equals(this.table, other.table) && Objects.equals(this.foreignKeyName, other.foreignKeyName) && Objects.equals(this.foreignKeyDefinition, other.foreignKeyDefinition);
    }

    public String toString() {
        return this.getClass().getName() + '(' + this.columns.toString() + ')';
    }

    @Override
    public Object accept(ValueVisitor visitor) {
        return visitor.accept(this);
    }

    @Override
    public boolean[] getColumnInsertability() {
        return SimpleValue.extractBooleansFromList(this.insertability);
    }

    @Override
    public boolean[] getColumnUpdateability() {
        return SimpleValue.extractBooleansFromList(this.updatability);
    }

    private static boolean[] extractBooleansFromList(List<Boolean> list) {
        boolean[] array = new boolean[list.size()];
        int i = 0;
        for (Boolean value : list) {
            array[i++] = value;
        }
        return array;
    }

    public void setJpaAttributeConverterDescriptor(ConverterDescriptor descriptor) {
        this.attributeConverterDescriptor = descriptor;
    }

    private void createParameterImpl() {
        try {
            String[] columnsNames = new String[this.columns.size()];
            for (int i = 0; i < this.columns.size(); ++i) {
                Selectable column = this.columns.get(i);
                if (!(column instanceof Column)) continue;
                columnsNames[i] = ((Column)column).getName();
            }
            XProperty xProperty = (XProperty)this.typeParameters.get("org.hibernate.type.ParameterType.xproperty");
            Annotation[] annotations = xProperty == null ? null : xProperty.getAnnotations();
            ClassLoaderService classLoaderService = this.getMetadata().getMetadataBuildingOptions().getServiceRegistry().getService(ClassLoaderService.class);
            this.typeParameters.put("org.hibernate.type.ParameterType", new ParameterTypeImpl(classLoaderService.classForName(this.typeParameters.getProperty("org.hibernate.type.ParameterType.returnedClass")), annotations, this.table.getCatalog(), this.table.getSchema(), this.table.getName(), Boolean.valueOf(this.typeParameters.getProperty("org.hibernate.type.ParameterType.primaryKey")), columnsNames));
        }
        catch (ClassLoadingException e) {
            throw new MappingException("Could not create DynamicParameterizedType for type: " + this.typeName, (Throwable)((Object)e));
        }
    }

    private static final class ParameterTypeImpl
    implements DynamicParameterizedType.ParameterType {
        private final Class returnedClass;
        private final Annotation[] annotationsMethod;
        private final String catalog;
        private final String schema;
        private final String table;
        private final boolean primaryKey;
        private final String[] columns;

        private ParameterTypeImpl(Class returnedClass, Annotation[] annotationsMethod, String catalog, String schema, String table, boolean primaryKey, String[] columns) {
            this.returnedClass = returnedClass;
            this.annotationsMethod = annotationsMethod;
            this.catalog = catalog;
            this.schema = schema;
            this.table = table;
            this.primaryKey = primaryKey;
            this.columns = columns;
        }

        @Override
        public Class getReturnedClass() {
            return this.returnedClass;
        }

        @Override
        public Annotation[] getAnnotationsMethod() {
            return this.annotationsMethod;
        }

        @Override
        public String getCatalog() {
            return this.catalog;
        }

        @Override
        public String getSchema() {
            return this.schema;
        }

        @Override
        public String getTable() {
            return this.table;
        }

        @Override
        public boolean isPrimaryKey() {
            return this.primaryKey;
        }

        @Override
        public String[] getColumns() {
            return this.columns;
        }
    }
}

