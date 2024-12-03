/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EnumType
 *  javax.persistence.TemporalType
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.TemporalType;
import org.hibernate.boot.jaxb.mapping.spi.FetchableAttribute;
import org.hibernate.boot.jaxb.mapping.spi.JaxbAttributeOverride;
import org.hibernate.boot.jaxb.mapping.spi.JaxbConvert;
import org.hibernate.boot.jaxb.mapping.spi.JaxbForeignKey;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMapKey;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMapKeyClass;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMapKeyColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMapKeyJoinColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbOrderColumn;

public interface CollectionAttribute
extends FetchableAttribute {
    public String getOrderBy();

    public void setOrderBy(String var1);

    public JaxbOrderColumn getOrderColumn();

    public void setOrderColumn(JaxbOrderColumn var1);

    public JaxbMapKey getMapKey();

    public void setMapKey(JaxbMapKey var1);

    public JaxbMapKeyClass getMapKeyClass();

    public void setMapKeyClass(JaxbMapKeyClass var1);

    public TemporalType getMapKeyTemporal();

    public void setMapKeyTemporal(TemporalType var1);

    public EnumType getMapKeyEnumerated();

    public void setMapKeyEnumerated(EnumType var1);

    public List<JaxbAttributeOverride> getMapKeyAttributeOverride();

    public List<JaxbConvert> getMapKeyConvert();

    public JaxbMapKeyColumn getMapKeyColumn();

    public void setMapKeyColumn(JaxbMapKeyColumn var1);

    public List<JaxbMapKeyJoinColumn> getMapKeyJoinColumn();

    public JaxbForeignKey getMapKeyForeignKey();

    public void setMapKeyForeignKey(JaxbForeignKey var1);
}

