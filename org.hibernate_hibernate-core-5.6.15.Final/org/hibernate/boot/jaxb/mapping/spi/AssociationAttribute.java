/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.mapping.spi;

import org.hibernate.boot.jaxb.mapping.spi.FetchableAttribute;
import org.hibernate.boot.jaxb.mapping.spi.JaxbCascadeType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbJoinTable;
import org.hibernate.boot.jaxb.mapping.spi.PersistentAttribute;

public interface AssociationAttribute
extends PersistentAttribute,
FetchableAttribute {
    public JaxbJoinTable getJoinTable();

    public void setJoinTable(JaxbJoinTable var1);

    public JaxbCascadeType getCascade();

    public void setCascade(JaxbCascadeType var1);

    public String getTargetEntity();

    public void setTargetEntity(String var1);
}

