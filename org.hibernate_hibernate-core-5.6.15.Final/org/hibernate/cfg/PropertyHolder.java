/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.ForeignKey
 *  javax.persistence.JoinColumn
 *  javax.persistence.JoinTable
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XProperty
 */
package org.hibernate.cfg;

import javax.persistence.Column;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;

public interface PropertyHolder {
    public String getClassName();

    public String getEntityOwnerClassName();

    public Table getTable();

    public void addProperty(Property var1, XClass var2);

    public void addProperty(Property var1, Ejb3Column[] var2, XClass var3);

    public KeyValue getIdentifier();

    public boolean isOrWithinEmbeddedId();

    public boolean isWithinElementCollection();

    public PersistentClass getPersistentClass();

    public boolean isComponent();

    public boolean isEntity();

    public void setParentProperty(String var1);

    public String getPath();

    public Column[] getOverriddenColumn(String var1);

    public JoinColumn[] getOverriddenJoinColumn(String var1);

    default public ForeignKey getOverriddenForeignKey(String propertyName) {
        return null;
    }

    public JoinTable getJoinTable(XProperty var1);

    public String getEntityName();

    public Join addJoin(JoinTable var1, boolean var2);

    public boolean isInIdClass();

    public void setInIdClass(Boolean var1);

    public void startingProperty(XProperty var1);

    public ConverterDescriptor resolveAttributeConverterDescriptor(XProperty var1);
}

