/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.hibernate.EntityMode;
import org.hibernate.MappingException;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.CompositeNestedGeneratedValueGenerator;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.internal.util.collections.JoinedIterator;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.MetaAttributable;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ValueVisitor;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.tuple.component.ComponentMetamodel;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;

public class Component
extends SimpleValue
implements MetaAttributable {
    private ArrayList<Property> properties = new ArrayList();
    private String componentClassName;
    private boolean embedded;
    private String parentProperty;
    private PersistentClass owner;
    private boolean dynamic;
    private Map metaAttributes;
    private boolean isKey;
    private String roleName;
    private Map<EntityMode, String> tuplizerImpls;
    private volatile Type type;
    private IdentifierGenerator builtIdentifierGenerator;

    @Deprecated
    public Component(MetadataImplementor metadata, PersistentClass owner) throws MappingException {
        this(metadata, owner.getTable(), owner);
    }

    @Deprecated
    public Component(MetadataImplementor metadata, Component component) throws MappingException {
        this(metadata, component.getTable(), component.getOwner());
    }

    @Deprecated
    public Component(MetadataImplementor metadata, Join join) throws MappingException {
        this(metadata, join.getTable(), join.getPersistentClass());
    }

    @Deprecated
    public Component(MetadataImplementor metadata, Collection collection) throws MappingException {
        this(metadata, collection.getCollectionTable(), collection.getOwner());
    }

    @Deprecated
    public Component(MetadataImplementor metadata, Table table, PersistentClass owner) throws MappingException {
        super(metadata, table);
        this.owner = owner;
    }

    public Component(MetadataBuildingContext metadata, PersistentClass owner) throws MappingException {
        this(metadata, owner.getTable(), owner);
    }

    public Component(MetadataBuildingContext metadata, Component component) throws MappingException {
        this(metadata, component.getTable(), component.getOwner());
    }

    public Component(MetadataBuildingContext metadata, Join join) throws MappingException {
        this(metadata, join.getTable(), join.getPersistentClass());
    }

    public Component(MetadataBuildingContext metadata, Collection collection) throws MappingException {
        this(metadata, collection.getCollectionTable(), collection.getOwner());
    }

    public Component(MetadataBuildingContext metadata, Table table, PersistentClass owner) throws MappingException {
        super(metadata, table);
        this.owner = owner;
    }

    public int getPropertySpan() {
        return this.properties.size();
    }

    public Iterator getPropertyIterator() {
        return this.properties.iterator();
    }

    public void addProperty(Property p) {
        this.properties.add(p);
    }

    @Override
    public void addColumn(Column column) {
        throw new UnsupportedOperationException("Cant add a column to a component");
    }

    @Override
    public int getColumnSpan() {
        int n = 0;
        Iterator iter = this.getPropertyIterator();
        while (iter.hasNext()) {
            Property p = (Property)iter.next();
            n += p.getColumnSpan();
        }
        return n;
    }

    @Override
    public Iterator<Selectable> getColumnIterator() {
        Iterator[] iters = new Iterator[this.getPropertySpan()];
        Iterator iter = this.getPropertyIterator();
        int i = 0;
        while (iter.hasNext()) {
            iters[i++] = ((Property)iter.next()).getColumnIterator();
        }
        return new JoinedIterator<Selectable>(iters);
    }

    public boolean isEmbedded() {
        return this.embedded;
    }

    public String getComponentClassName() {
        return this.componentClassName;
    }

    public Class getComponentClass() throws MappingException {
        ClassLoaderService classLoaderService = this.getMetadata().getMetadataBuildingOptions().getServiceRegistry().getService(ClassLoaderService.class);
        try {
            return classLoaderService.classForName(this.componentClassName);
        }
        catch (ClassLoadingException e) {
            throw new MappingException("component class not found: " + this.componentClassName, (Throwable)((Object)e));
        }
    }

    public PersistentClass getOwner() {
        return this.owner;
    }

    public String getParentProperty() {
        return this.parentProperty;
    }

    public void setComponentClassName(String componentClass) {
        this.componentClassName = componentClass;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public void setOwner(PersistentClass owner) {
        this.owner = owner;
    }

    public void setParentProperty(String parentProperty) {
        this.parentProperty = parentProperty;
    }

    public boolean isDynamic() {
        return this.dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Type getType() throws MappingException {
        Type localType = this.type;
        if (localType == null) {
            Component component = this;
            synchronized (component) {
                if (this.type == null) {
                    ComponentMetamodel metamodel = new ComponentMetamodel(this, this.getMetadata().getMetadataBuildingOptions());
                    TypeFactory factory = this.getMetadata().getTypeConfiguration().getTypeResolver().getTypeFactory();
                    this.type = localType = this.isEmbedded() ? factory.embeddedComponent(metamodel) : factory.component(metamodel);
                }
            }
        }
        return localType;
    }

    @Override
    public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
    }

    @Override
    public Map getMetaAttributes() {
        return this.metaAttributes;
    }

    @Override
    public MetaAttribute getMetaAttribute(String attributeName) {
        return this.metaAttributes == null ? null : (MetaAttribute)this.metaAttributes.get(attributeName);
    }

    @Override
    public void setMetaAttributes(Map metas) {
        this.metaAttributes = metas;
    }

    @Override
    public Object accept(ValueVisitor visitor) {
        return visitor.accept(this);
    }

    @Override
    public boolean isSame(SimpleValue other) {
        return other instanceof Component && this.isSame((Component)other);
    }

    public boolean isSame(Component other) {
        return super.isSame(other) && Objects.equals(this.properties, other.properties) && Objects.equals(this.componentClassName, other.componentClassName) && this.embedded == other.embedded && Objects.equals(this.parentProperty, other.parentProperty) && Objects.equals(this.metaAttributes, other.metaAttributes);
    }

    @Override
    public boolean[] getColumnInsertability() {
        boolean[] result = new boolean[this.getColumnSpan()];
        Iterator iter = this.getPropertyIterator();
        int i = 0;
        while (iter.hasNext()) {
            Property prop = (Property)iter.next();
            boolean[] chunk = prop.getValue().getColumnInsertability();
            if (prop.isInsertable()) {
                System.arraycopy(chunk, 0, result, i, chunk.length);
            }
            i += chunk.length;
        }
        return result;
    }

    @Override
    public boolean[] getColumnUpdateability() {
        boolean[] result = new boolean[this.getColumnSpan()];
        Iterator iter = this.getPropertyIterator();
        int i = 0;
        while (iter.hasNext()) {
            Property prop = (Property)iter.next();
            boolean[] chunk = prop.getValue().getColumnUpdateability();
            if (prop.isUpdateable()) {
                System.arraycopy(chunk, 0, result, i, chunk.length);
            }
            i += chunk.length;
        }
        return result;
    }

    public boolean isKey() {
        return this.isKey;
    }

    public void setKey(boolean isKey) {
        this.isKey = isKey;
    }

    public boolean hasPojoRepresentation() {
        return this.componentClassName != null;
    }

    public void addTuplizer(EntityMode entityMode, String implClassName) {
        if (this.tuplizerImpls == null) {
            this.tuplizerImpls = new HashMap<EntityMode, String>();
        }
        this.tuplizerImpls.put(entityMode, implClassName);
    }

    public String getTuplizerImplClassName(EntityMode mode) {
        if (this.tuplizerImpls == null) {
            return null;
        }
        return this.tuplizerImpls.get((Object)mode);
    }

    public Map getTuplizerMap() {
        if (this.tuplizerImpls == null) {
            return null;
        }
        return Collections.unmodifiableMap(this.tuplizerImpls);
    }

    public Property getProperty(int index) {
        return this.properties.get(index);
    }

    public Property getProperty(String propertyName) throws MappingException {
        Iterator iter = this.getPropertyIterator();
        while (iter.hasNext()) {
            Property prop = (Property)iter.next();
            if (!prop.getName().equals(propertyName)) continue;
            return prop;
        }
        throw new MappingException("component: " + this.componentClassName + " property not found: " + propertyName);
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + '(' + this.properties.toString() + ')';
    }

    @Override
    public IdentifierGenerator createIdentifierGenerator(IdentifierGeneratorFactory identifierGeneratorFactory, Dialect dialect, String defaultCatalog, String defaultSchema, RootClass rootClass) throws MappingException {
        if (this.builtIdentifierGenerator == null) {
            this.builtIdentifierGenerator = this.buildIdentifierGenerator(identifierGeneratorFactory, dialect, defaultCatalog, defaultSchema, rootClass);
        }
        return this.builtIdentifierGenerator;
    }

    private IdentifierGenerator buildIdentifierGenerator(IdentifierGeneratorFactory identifierGeneratorFactory, Dialect dialect, String defaultCatalog, String defaultSchema, RootClass rootClass) throws MappingException {
        boolean hasCustomGenerator;
        boolean bl = hasCustomGenerator = !"assigned".equals(this.getIdentifierGeneratorStrategy());
        if (hasCustomGenerator) {
            return super.createIdentifierGenerator(identifierGeneratorFactory, dialect, defaultCatalog, defaultSchema, rootClass);
        }
        Class entityClass = rootClass.getMappedClass();
        Class attributeDeclarer = rootClass.getIdentifierMapper() != null ? this.resolveComponentClass() : (rootClass.getIdentifierProperty() != null ? this.resolveComponentClass() : entityClass);
        StandardGenerationContextLocator locator = new StandardGenerationContextLocator(rootClass.getEntityName());
        CompositeNestedGeneratedValueGenerator generator = new CompositeNestedGeneratedValueGenerator(locator);
        Iterator itr = this.getPropertyIterator();
        while (itr.hasNext()) {
            SimpleValue value;
            Property property = (Property)itr.next();
            if (!property.getValue().isSimpleValue() || "assigned".equals((value = (SimpleValue)property.getValue()).getIdentifierGeneratorStrategy())) continue;
            IdentifierGenerator valueGenerator = value.createIdentifierGenerator(identifierGeneratorFactory, dialect, defaultCatalog, defaultSchema, rootClass);
            generator.addGeneratedValuePlan(new ValueGenerationPlan(valueGenerator, this.injector(property, attributeDeclarer)));
        }
        return generator;
    }

    private Setter injector(Property property, Class attributeDeclarer) {
        return property.getPropertyAccessStrategy(attributeDeclarer).buildPropertyAccess(attributeDeclarer, property.getName()).getSetter();
    }

    private Class resolveComponentClass() {
        try {
            return this.getComponentClass();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static class ValueGenerationPlan
    implements CompositeNestedGeneratedValueGenerator.GenerationPlan {
        private final IdentifierGenerator subGenerator;
        private final Setter injector;

        public ValueGenerationPlan(IdentifierGenerator subGenerator, Setter injector) {
            this.subGenerator = subGenerator;
            this.injector = injector;
        }

        @Override
        public void execute(SharedSessionContractImplementor session, Object incomingObject, Object injectionContext) {
            Serializable generatedValue = this.subGenerator.generate(session, incomingObject);
            this.injector.set(injectionContext, generatedValue, session.getFactory());
        }

        @Override
        public void registerExportables(Database database) {
            this.subGenerator.registerExportables(database);
        }

        @Override
        public void initialize(SqlStringGenerationContext context) {
            this.subGenerator.initialize(context);
        }
    }

    public static class StandardGenerationContextLocator
    implements CompositeNestedGeneratedValueGenerator.GenerationContextLocator {
        private final String entityName;

        public StandardGenerationContextLocator(String entityName) {
            this.entityName = entityName;
        }

        @Override
        public Serializable locateGenerationContext(SharedSessionContractImplementor session, Object incomingObject) {
            return session.getEntityPersister(this.entityName, incomingObject).getIdentifier(incomingObject, session);
        }
    }
}

