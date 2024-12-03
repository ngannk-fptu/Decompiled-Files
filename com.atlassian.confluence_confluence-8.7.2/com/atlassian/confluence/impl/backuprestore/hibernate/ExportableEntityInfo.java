/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.id.IdentifierGenerator
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.impl.backuprestore.hibernate;

import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.type.Type;

public interface ExportableEntityInfo {
    public String getDiscriminatorColumnName();

    public HibernateField getId();

    public String getTableName();

    public Class<?> getEntityClass();

    public Object getDiscriminatorValue();

    public IdentifierGenerator getIdentifierGenerator();

    public Type getHibernateTypeByFieldName(String var1);

    public List<HibernateField> getFields();

    default public List<HibernateField> getPersistableFields() {
        return this.getFields().stream().filter(field -> !field.getType().isCollectionType()).collect(Collectors.toList());
    }

    default public Collection<HibernateField> getAllExternalReferences() {
        return Stream.concat(this.getStreamOfExternalReferencesForFields(), this.getStreamOfExternalReferenceId()).collect(Collectors.toList());
    }

    private Stream<HibernateField> getStreamOfExternalReferencesForFields() {
        return this.getFields().stream().filter(field -> field.getType().isAssociationType() && !field.getType().isCollectionType());
    }

    private Stream<HibernateField> getStreamOfExternalReferenceId() {
        HibernateField id = this.getId();
        return id != null && id.getReferencedClass() != null ? Stream.of(id) : Stream.empty();
    }
}

