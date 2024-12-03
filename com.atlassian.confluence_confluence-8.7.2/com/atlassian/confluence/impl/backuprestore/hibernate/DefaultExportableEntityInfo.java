/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.DirectoryMapping
 *  com.atlassian.crowd.model.user.InternalUserCredentialRecord
 *  org.hibernate.SessionFactory
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.id.IdentifierGenerator
 *  org.hibernate.persister.entity.AbstractEntityPersister
 *  org.hibernate.type.EmbeddedComponentType
 *  org.hibernate.type.EntityType
 *  org.hibernate.type.IntegerType
 *  org.hibernate.type.ManyToOneType
 *  org.hibernate.type.OneToOneType
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.impl.backuprestore.hibernate;

import com.atlassian.confluence.content.ContentProperty;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.crowd.model.application.DirectoryMapping;
import com.atlassian.crowd.model.user.InternalUserCredentialRecord;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.EmbeddedComponentType;
import org.hibernate.type.EntityType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.Type;

public class DefaultExportableEntityInfo
implements ExportableEntityInfo {
    private final AbstractEntityPersister entityPersister;
    private final Class<?> entityClass;
    private final String tableName;
    private final HibernateField id;
    private final List<HibernateField> fields;
    private final Map<String, Type> hibernateTypeByPropertyNameMap;

    public DefaultExportableEntityInfo(AbstractEntityPersister entityPersister, SessionFactory sessionFactory) {
        Type[] types;
        this.entityPersister = entityPersister;
        this.tableName = entityPersister.getTableName();
        this.entityClass = entityPersister.getEntityTuplizer().getMappedClass();
        String idPropertyName = entityPersister.getIdentifierPropertyName();
        LinkedList<String> idPropertyNames = new LinkedList<String>();
        LinkedList<Type> idTypes = new LinkedList<Type>();
        HashMap<String, Type> idColumnNameByType = new HashMap<String, Type>();
        if (idPropertyName == null) {
            String[] idNames = ((EmbeddedComponentType)entityPersister.getIdentifierType()).getPropertyNames();
            types = ((EmbeddedComponentType)entityPersister.getIdentifierType()).getSubtypes();
            idPropertyNames.addAll(Arrays.asList(idNames));
            idTypes.addAll(Arrays.asList(types));
            for (int i = 0; i < types.length; ++i) {
                idColumnNameByType.put(idNames[i], types[i]);
            }
        } else {
            idColumnNameByType.put(idPropertyName, entityPersister.getIdentifierType());
            idPropertyNames.add(idPropertyName);
            idTypes.add(entityPersister.getIdentifierType());
        }
        Class<?> oneToOneReference = null;
        types = entityPersister.getPropertyTypes();
        String[] properties = entityPersister.getPropertyNames();
        ArrayList<HibernateField> fields = new ArrayList<HibernateField>();
        boolean[] propertyNullabilities = entityPersister.getPropertyNullability();
        for (int i = 0; i < types.length; ++i) {
            Type type = types[i];
            if (type instanceof OneToOneType) {
                oneToOneReference = this.getReferencedClass(type);
                continue;
            }
            String[] columnNames = entityPersister.getPropertyColumnNames(i);
            HibernateField propertyInfo = new HibernateField(type, properties[i], columnNames, this.getReferencedClass(type), propertyNullabilities[i]);
            fields.add(propertyInfo);
        }
        this.id = new HibernateField(idTypes, idPropertyNames, entityPersister.getKeyColumnNames(), oneToOneReference, false);
        if (this.entityClass.equals(ContentProperty.class)) {
            String[] contentColumn = new String[]{"CONTENTID"};
            EntityType manyType = ((SessionFactoryImplementor)sessionFactory).getTypeResolver().getTypeFactory().manyToOne(ContentEntityObject.class.getName());
            HibernateField contentField = new HibernateField((Type)manyType, "content", contentColumn, ContentEntityObject.class, false);
            fields.add(contentField);
        }
        if (this.entityClass.equals(DirectoryMapping.class) || this.entityClass.equals(InternalUserCredentialRecord.class)) {
            String[] listIndexColumn = new String[]{"list_index"};
            HibernateField listIndexField = new HibernateField((Type)IntegerType.INSTANCE, "listIndex", listIndexColumn, null, true);
            fields.add(listIndexField);
        }
        this.fields = Collections.unmodifiableList(fields);
        this.hibernateTypeByPropertyNameMap = this.buildHibernateFieldByNameMap(idColumnNameByType, this.fields);
    }

    private Map<String, Type> buildHibernateFieldByNameMap(Map<String, Type> idColumnNameByType, List<HibernateField> fields) {
        HashMap<String, Type> map = new HashMap<String, Type>(idColumnNameByType);
        map.putAll(fields.stream().collect(Collectors.toMap(HibernateField::getPropertyName, HibernateField::getType)));
        return map;
    }

    private Class<?> getReferencedClass(Type type) {
        if (type.getClass().equals(ManyToOneType.class) || type.getClass().equals(OneToOneType.class)) {
            EntityType entityType = (EntityType)type;
            String associatedEntityName = entityType.getAssociatedEntityName();
            try {
                return Class.forName(associatedEntityName);
            }
            catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Unable to find class for " + associatedEntityName + " provided by type " + type, e);
            }
        }
        return null;
    }

    @Override
    public List<HibernateField> getFields() {
        return this.fields;
    }

    @Override
    public Class<?> getEntityClass() {
        return this.entityClass;
    }

    @Override
    public HibernateField getId() {
        return this.id;
    }

    @Override
    public String getTableName() {
        return this.tableName;
    }

    @Override
    public String getDiscriminatorColumnName() {
        return this.entityPersister.getDiscriminatorColumnName();
    }

    @Override
    public Object getDiscriminatorValue() {
        return this.entityPersister.getDiscriminatorValue();
    }

    @Override
    public IdentifierGenerator getIdentifierGenerator() {
        return this.entityPersister.getIdentifierGenerator();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultExportableEntityInfo that = (DefaultExportableEntityInfo)o;
        return Objects.equals(this.entityClass, that.entityClass);
    }

    public int hashCode() {
        return Objects.hash(this.entityClass);
    }

    @Override
    public Type getHibernateTypeByFieldName(String propertyName) {
        return this.hibernateTypeByPropertyNameMap.get(propertyName);
    }
}

