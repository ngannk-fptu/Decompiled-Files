/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.id.IdentifierGenerator
 *  org.hibernate.type.EntityType
 *  org.hibernate.type.StringType
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.impl.backuprestore.hibernate;

import com.atlassian.confluence.impl.backuprestore.hibernate.ArtificialHibernateEntity;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.type.EntityType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

public class DirectoryAttributeEntityInfo
implements ExportableEntityInfo {
    private final List<HibernateField> fields;

    public DirectoryAttributeEntityInfo(SessionFactoryImplementor sessionFactoryImplementor) {
        this.fields = new ArrayList<HibernateField>(Arrays.asList(this.createDirectoryIdField(sessionFactoryImplementor), this.createAttributeNameField(sessionFactoryImplementor), this.createAttributeValueField(sessionFactoryImplementor)));
    }

    private HibernateField createDirectoryIdField(SessionFactoryImplementor sessionFactoryImplementor) {
        EntityType referenceToDirectoryType = sessionFactoryImplementor.getTypeResolver().getTypeFactory().manyToOne(DirectoryImpl.class.getName());
        String[] columnNames = new String[]{"directory_id"};
        return new HibernateField((Type)referenceToDirectoryType, "directoryId", columnNames, DirectoryImpl.class, false);
    }

    private HibernateField createAttributeNameField(SessionFactoryImplementor sessionFactoryImplementor) {
        Type type = sessionFactoryImplementor.getTypeResolver().getTypeFactory().byClass(StringType.class, null);
        String[] columnNames = new String[]{"attribute_name"};
        return new HibernateField(type, "attributeName", columnNames, null);
    }

    private HibernateField createAttributeValueField(SessionFactoryImplementor sessionFactoryImplementor) {
        Type type = sessionFactoryImplementor.getTypeResolver().getTypeFactory().byClass(StringType.class, null);
        String[] columnNames = new String[]{"attribute_value"};
        return new HibernateField(type, "attributeValue", columnNames, null);
    }

    @Override
    public HibernateField getId() {
        return null;
    }

    @Override
    public String getDiscriminatorColumnName() {
        return null;
    }

    @Override
    public Object getDiscriminatorValue() {
        return null;
    }

    @Override
    public IdentifierGenerator getIdentifierGenerator() {
        return null;
    }

    @Override
    public Type getHibernateTypeByFieldName(String propertyName) {
        throw new IllegalStateException("getHibernateTypeByFieldName must not be called for directory attribute records because they should not be present in entities.xml");
    }

    @Override
    public List<HibernateField> getFields() {
        return this.fields;
    }

    @Override
    public String getTableName() {
        return "cwd_directory_attribute";
    }

    @Override
    public Class<?> getEntityClass() {
        return EntityClass.class;
    }

    public static class EntityClass
    implements ArtificialHibernateEntity {
    }
}

