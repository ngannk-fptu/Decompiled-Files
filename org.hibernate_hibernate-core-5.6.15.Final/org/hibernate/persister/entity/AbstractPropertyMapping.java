/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.type.AnyType;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.EmbeddedComponentType;
import org.hibernate.type.EntityType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.SpecialOneToOneType;
import org.hibernate.type.Type;

public abstract class AbstractPropertyMapping
implements PropertyMapping {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(AbstractPropertyMapping.class);
    private final Map<String, Type> typesByPropertyPath = new HashMap<String, Type>();
    private Set<String> duplicateIncompatiblePaths = null;
    private final Map<String, String[]> columnsByPropertyPath = new HashMap<String, String[]>();
    private final Map<String, String[]> columnReadersByPropertyPath = new HashMap<String, String[]>();
    private final Map<String, String[]> columnReaderTemplatesByPropertyPath = new HashMap<String, String[]>();
    private final Map<String, String[]> formulaTemplatesByPropertyPath = new HashMap<String, String[]>();

    public String[] getIdentifierColumnNames() {
        throw new UnsupportedOperationException("one-to-one is not supported here");
    }

    public String[] getIdentifierColumnReaderTemplates() {
        throw new UnsupportedOperationException("one-to-one is not supported here");
    }

    public String[] getIdentifierColumnReaders() {
        throw new UnsupportedOperationException("one-to-one is not supported here");
    }

    protected abstract String getEntityName();

    @Override
    public Type toType(String propertyName) throws QueryException {
        Type type = this.typesByPropertyPath.get(propertyName);
        if (type == null) {
            throw this.propertyException(propertyName);
        }
        return type;
    }

    protected final QueryException propertyException(String propertyName) throws QueryException {
        return new QueryException("could not resolve property: " + propertyName + " of: " + this.getEntityName());
    }

    public String[] getColumnNames(String propertyName) {
        String[] cols = this.columnsByPropertyPath.get(propertyName);
        if (cols == null) {
            throw new MappingException("unknown property: " + propertyName);
        }
        return cols;
    }

    @Override
    public String[] toColumns(String alias, String propertyName) throws QueryException {
        String[] columns = this.columnsByPropertyPath.get(propertyName);
        if (columns == null) {
            throw this.propertyException(propertyName);
        }
        String[] formulaTemplates = this.formulaTemplatesByPropertyPath.get(propertyName);
        String[] columnReaderTemplates = this.columnReaderTemplatesByPropertyPath.get(propertyName);
        String[] result = new String[columns.length];
        for (int i = 0; i < columns.length; ++i) {
            result[i] = columnReaderTemplates[i] == null ? StringHelper.replace(formulaTemplates[i], "$PlaceHolder$", alias) : StringHelper.replace(columnReaderTemplates[i], "$PlaceHolder$", alias);
        }
        return result;
    }

    @Override
    public String[] toColumns(String propertyName) throws QueryException {
        String[] columns = this.columnsByPropertyPath.get(propertyName);
        if (columns == null) {
            throw this.propertyException(propertyName);
        }
        String[] formulaTemplates = this.formulaTemplatesByPropertyPath.get(propertyName);
        String[] columnReaders = this.columnReadersByPropertyPath.get(propertyName);
        String[] result = new String[columns.length];
        for (int i = 0; i < columns.length; ++i) {
            result[i] = columnReaders[i] == null ? StringHelper.replace(formulaTemplates[i], "$PlaceHolder$", "") : columnReaders[i];
        }
        return result;
    }

    private void logDuplicateRegistration(String path, Type existingType, Type type) {
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Skipping duplicate registration of path [{0}], existing type = [{1}], incoming type = [{2}]", path, existingType, type);
        }
    }

    private void logIncompatibleRegistration(String path, Type existingType, Type type) {
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Skipped adding attribute [{1}] to base-type [{0}] as more than one sub-type defined the attribute using incompatible types (strictly speaking the attributes are not inherited); existing type = [{2}], incoming type = [{3}]", new Object[]{this.getEntityName(), path, existingType, type});
        }
    }

    @Deprecated
    protected void addPropertyPath(String path, Type type, String[] columns, String[] columnReaders, String[] columnReaderTemplates, String[] formulaTemplates) {
        this.addPropertyPath(path, type, columns, columnReaders, columnReaderTemplates, formulaTemplates, null);
    }

    protected void addPropertyPath(String path, Type type, String[] columns, String[] columnReaders, String[] columnReaderTemplates, String[] formulaTemplates, Mapping factory) {
        Type existingType = this.typesByPropertyPath.get(path);
        if (existingType != null || this.duplicateIncompatiblePaths != null && this.duplicateIncompatiblePaths.contains(path)) {
            if (type == existingType || existingType == null || !(type instanceof AssociationType)) {
                this.logDuplicateRegistration(path, existingType, type);
            } else if (!(existingType instanceof AssociationType)) {
                this.logDuplicateRegistration(path, existingType, type);
            } else if (!(type instanceof AnyType) || !(existingType instanceof AnyType)) {
                Type commonType = null;
                MetadataImplementor metadata = (MetadataImplementor)factory;
                if (type instanceof CollectionType && existingType instanceof CollectionType) {
                    Collection otherCollection;
                    Collection thisCollection = metadata.getCollectionBinding(((CollectionType)existingType).getRole());
                    if (thisCollection.isSame(otherCollection = metadata.getCollectionBinding(((CollectionType)type).getRole()))) {
                        this.logDuplicateRegistration(path, existingType, type);
                        return;
                    }
                    this.logIncompatibleRegistration(path, existingType, type);
                } else if (type instanceof EntityType && existingType instanceof EntityType) {
                    EntityType entityType1 = (EntityType)existingType;
                    EntityType entityType2 = (EntityType)type;
                    if (entityType1.getAssociatedEntityName().equals(entityType2.getAssociatedEntityName())) {
                        this.logDuplicateRegistration(path, existingType, type);
                        return;
                    }
                    commonType = this.getCommonType(metadata, entityType1, entityType2);
                } else {
                    this.logIncompatibleRegistration(path, existingType, type);
                }
                if (commonType == null) {
                    if (this.duplicateIncompatiblePaths == null) {
                        this.duplicateIncompatiblePaths = new HashSet<String>();
                    }
                    this.duplicateIncompatiblePaths.add(path);
                    this.typesByPropertyPath.remove(path);
                    String[] empty = new String[]{};
                    this.columnsByPropertyPath.put(path, empty);
                    this.columnReadersByPropertyPath.put(path, empty);
                    this.columnReaderTemplatesByPropertyPath.put(path, empty);
                    if (formulaTemplates != null) {
                        this.formulaTemplatesByPropertyPath.put(path, empty);
                    }
                } else {
                    this.typesByPropertyPath.put(path, commonType);
                }
            }
        } else {
            this.typesByPropertyPath.put(path, type);
            this.columnsByPropertyPath.put(path, columns);
            this.columnReadersByPropertyPath.put(path, columnReaders);
            this.columnReaderTemplatesByPropertyPath.put(path, columnReaderTemplates);
            if (formulaTemplates != null) {
                this.formulaTemplatesByPropertyPath.put(path, formulaTemplates);
            }
        }
    }

    private Type getCommonType(MetadataImplementor metadata, EntityType entityType1, EntityType entityType2) {
        PersistentClass otherClass;
        PersistentClass thisClass = metadata.getEntityBinding(entityType1.getAssociatedEntityName());
        PersistentClass commonClass = this.getCommonPersistentClass(thisClass, otherClass = metadata.getEntityBinding(entityType2.getAssociatedEntityName()));
        if (commonClass == null) {
            return null;
        }
        if (entityType1 instanceof ManyToOneType) {
            ManyToOneType t = (ManyToOneType)entityType1;
            return new ManyToOneType(t, commonClass.getEntityName());
        }
        if (entityType1 instanceof SpecialOneToOneType) {
            SpecialOneToOneType t = (SpecialOneToOneType)entityType1;
            return new SpecialOneToOneType(t, commonClass.getEntityName());
        }
        if (entityType1 instanceof OneToOneType) {
            OneToOneType t = (OneToOneType)entityType1;
            return new OneToOneType(t, commonClass.getEntityName());
        }
        throw new IllegalStateException("Unexpected entity type: " + entityType1);
    }

    private PersistentClass getCommonPersistentClass(PersistentClass clazz1, PersistentClass clazz2) {
        while (clazz2 != null && clazz2.getMappedClass() != null && clazz1.getMappedClass() != null && !clazz2.getMappedClass().isAssignableFrom(clazz1.getMappedClass())) {
            clazz2 = clazz2.getSuperclass();
        }
        return clazz2;
    }

    protected void initPropertyPaths(String path, Type type, String[] columns, String[] columnReaders, String[] columnReaderTemplates, String[] formulaTemplates, Mapping factory) throws MappingException {
        Type actype;
        assert (columns != null) : "Incoming columns should not be null : " + path;
        assert (type != null) : "Incoming type should not be null : " + path;
        if (columns.length != type.getColumnSpan(factory)) {
            throw new MappingException("broken column mapping for: " + path + " of: " + this.getEntityName());
        }
        if (type.isAssociationType()) {
            actype = (AssociationType)type;
            if (actype.useLHSPrimaryKey()) {
                columns = this.getIdentifierColumnNames();
                columnReaders = this.getIdentifierColumnReaders();
                columnReaderTemplates = this.getIdentifierColumnReaderTemplates();
            } else {
                String foreignKeyProperty = actype.getLHSPropertyName();
                if (foreignKeyProperty != null && !path.equals(foreignKeyProperty)) {
                    columns = this.columnsByPropertyPath.get(foreignKeyProperty);
                    if (columns == null) {
                        return;
                    }
                    columnReaders = this.columnReadersByPropertyPath.get(foreignKeyProperty);
                    columnReaderTemplates = this.columnReaderTemplatesByPropertyPath.get(foreignKeyProperty);
                }
            }
        }
        if (path != null) {
            this.addPropertyPath(path, type, columns, columnReaders, columnReaderTemplates, formulaTemplates, factory);
        }
        if (type.isComponentType()) {
            actype = (CompositeType)type;
            this.initComponentPropertyPaths(path, (CompositeType)actype, columns, columnReaders, columnReaderTemplates, formulaTemplates, factory);
            if (actype.isEmbedded()) {
                this.initComponentPropertyPaths(path == null ? null : StringHelper.qualifier(path), (CompositeType)actype, columns, columnReaders, columnReaderTemplates, formulaTemplates, factory);
            }
        } else if (type.isEntityType()) {
            this.initIdentifierPropertyPaths(path, (EntityType)type, columns, columnReaders, columnReaderTemplates, formulaTemplates != null && formulaTemplates.length > 0 ? formulaTemplates : null, factory);
        }
    }

    protected void initIdentifierPropertyPaths(String path, EntityType etype, String[] columns, String[] columnReaders, String[] columnReaderTemplates, Mapping factory) throws MappingException {
        this.initIdentifierPropertyPaths(path, etype, columns, columnReaders, columnReaderTemplates, null, factory);
    }

    protected void initIdentifierPropertyPaths(String path, EntityType etype, String[] columns, String[] columnReaders, String[] columnReaderTemplates, String[] formulaTemplates, Mapping factory) throws MappingException {
        Type idtype = etype.getIdentifierOrUniqueKeyType(factory);
        String idPropName = etype.getIdentifierOrUniqueKeyPropertyName(factory);
        boolean hasNonIdentifierPropertyNamedId = this.hasNonIdentifierPropertyNamedId(etype, factory);
        if (etype.isReferenceToPrimaryKey() && !hasNonIdentifierPropertyNamedId) {
            String idpath1 = AbstractPropertyMapping.extendPath(path, "id");
            this.addPropertyPath(idpath1, idtype, columns, columnReaders, columnReaderTemplates, formulaTemplates, factory);
            this.initPropertyPaths(idpath1, idtype, columns, columnReaders, columnReaderTemplates, formulaTemplates, factory);
        }
        if (!etype.isNullable()) {
            if (idPropName != null) {
                String idpath2 = AbstractPropertyMapping.extendPath(path, idPropName);
                this.addPropertyPath(idpath2, idtype, columns, columnReaders, columnReaderTemplates, formulaTemplates, factory);
                this.initPropertyPaths(idpath2, idtype, columns, columnReaders, columnReaderTemplates, formulaTemplates, factory);
            } else if (idtype.isComponentType() && idtype instanceof EmbeddedComponentType) {
                this.initComponentPropertyPaths(path, (CompositeType)idtype, columns, columnReaders, columnReaderTemplates, formulaTemplates, factory);
            }
        }
    }

    private boolean hasNonIdentifierPropertyNamedId(EntityType entityType, Mapping factory) {
        try {
            return factory.getReferencedPropertyType(entityType.getAssociatedEntityName(), "id") != null;
        }
        catch (MappingException e) {
            return false;
        }
    }

    protected void initComponentPropertyPaths(String path, CompositeType type, String[] columns, String[] columnReaders, String[] columnReaderTemplates, String[] formulaTemplates, Mapping factory) throws MappingException {
        Type[] types = type.getSubtypes();
        String[] properties = type.getPropertyNames();
        int begin = 0;
        for (int i = 0; i < properties.length; ++i) {
            String subpath = AbstractPropertyMapping.extendPath(path, properties[i]);
            try {
                int length = types[i].getColumnSpan(factory);
                String[] columnSlice = ArrayHelper.slice(columns, begin, length);
                String[] columnReaderSlice = ArrayHelper.slice(columnReaders, begin, length);
                String[] columnReaderTemplateSlice = ArrayHelper.slice(columnReaderTemplates, begin, length);
                String[] formulaSlice = formulaTemplates == null ? null : ArrayHelper.slice(formulaTemplates, begin, length);
                this.initPropertyPaths(subpath, types[i], columnSlice, columnReaderSlice, columnReaderTemplateSlice, formulaSlice, factory);
                begin += length;
                continue;
            }
            catch (Exception e) {
                throw new MappingException("bug in initComponentPropertyPaths", e);
            }
        }
    }

    private static String extendPath(String path, String property) {
        return StringHelper.isEmpty(path) ? property : StringHelper.qualify(path, property);
    }
}

