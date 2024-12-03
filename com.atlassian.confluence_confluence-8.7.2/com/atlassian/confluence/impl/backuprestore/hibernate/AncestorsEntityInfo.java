/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.id.IdentifierGenerator
 *  org.hibernate.type.EntityType
 *  org.hibernate.type.IntegerType
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.impl.backuprestore.hibernate;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.hibernate.ArtificialHibernateEntity;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.type.EntityType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

public class AncestorsEntityInfo
implements ExportableEntityInfo {
    private final List<HibernateField> fields;

    public AncestorsEntityInfo(SessionFactoryImplementor sessionFactoryImplementor) {
        this.fields = new ArrayList<HibernateField>(Arrays.asList(this.createDescendentIdField(sessionFactoryImplementor), this.createAncestorIdField(sessionFactoryImplementor), this.createPositionField(sessionFactoryImplementor)));
    }

    private HibernateField createAncestorIdField(SessionFactoryImplementor sessionFactoryImplementor) {
        EntityType referenceToContentType = sessionFactoryImplementor.getTypeResolver().getTypeFactory().manyToOne(ContentEntityObject.class.getName());
        String[] columnNames = new String[]{"ANCESTORID"};
        return new HibernateField((Type)referenceToContentType, "ancestorId", columnNames, ContentEntityObject.class, false);
    }

    private HibernateField createDescendentIdField(SessionFactoryImplementor sessionFactoryImplementor) {
        EntityType referenceToContentType = sessionFactoryImplementor.getTypeResolver().getTypeFactory().manyToOne(ContentEntityObject.class.getName());
        String[] columnNames = new String[]{"DESCENDENTID"};
        return new HibernateField((Type)referenceToContentType, "descendentId", columnNames, ContentEntityObject.class, false);
    }

    private HibernateField createPositionField(SessionFactoryImplementor sessionFactoryImplementor) {
        Type integerType = sessionFactoryImplementor.getTypeResolver().getTypeFactory().byClass(IntegerType.class, null);
        String[] columnNames = new String[]{"ANCESTORPOSITION"};
        return new HibernateField(integerType, "position", columnNames, null, false);
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
        throw new IllegalStateException("getHibernateTypeByFieldName must not be called for ancestor records because they must not present in XML file");
    }

    @Override
    public List<HibernateField> getFields() {
        return this.fields;
    }

    @Override
    public String getTableName() {
        return "CONFANCESTORS";
    }

    @Override
    public Class<?> getEntityClass() {
        return EntityClass.class;
    }

    public static class EntityClass
    implements ArtificialHibernateEntity {
    }
}

