/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.model.application.DirectoryMapping
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.id.IdentifierGenerator
 *  org.hibernate.type.EntityType
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.impl.backuprestore.hibernate;

import com.atlassian.confluence.impl.backuprestore.hibernate.ArtificialHibernateEntity;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.model.application.DirectoryMapping;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class DirectoryMappingOperationEntityInfo
implements ExportableEntityInfo {
    private final List<HibernateField> fields;

    public DirectoryMappingOperationEntityInfo(SessionFactoryImplementor sessionFactoryImplementor) {
        this.fields = new ArrayList<HibernateField>(Arrays.asList(this.createDirectoryMappingIdField(sessionFactoryImplementor), this.createOperationTypeField(sessionFactoryImplementor)));
    }

    private HibernateField createDirectoryMappingIdField(SessionFactoryImplementor sessionFactoryImplementor) {
        EntityType referenceToDirectoryType = sessionFactoryImplementor.getTypeResolver().getTypeFactory().manyToOne(DirectoryMapping.class.getName());
        String[] columnNames = new String[]{"app_dir_mapping_id"};
        return new HibernateField((Type)referenceToDirectoryType, "directoryMappingId", columnNames, DirectoryMapping.class, false);
    }

    private HibernateField createOperationTypeField(SessionFactoryImplementor sessionFactoryImplementor) {
        Type operationType = sessionFactoryImplementor.getTypeResolver().getTypeFactory().byClass(OperationType.class, null);
        String[] columnNames = new String[]{"operation_type"};
        return new HibernateField(operationType, "operationType", columnNames, null, false);
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
        throw new IllegalStateException("getHibernateTypeByFieldName must not be called for directory mapping operation records because they should not be present in entities.xml");
    }

    @Override
    public List<HibernateField> getFields() {
        return this.fields;
    }

    @Override
    public String getTableName() {
        return "cwd_app_dir_operation";
    }

    @Override
    public Class<?> getEntityClass() {
        return EntityClass.class;
    }

    public static class EntityClass
    implements ArtificialHibernateEntity {
    }
}

