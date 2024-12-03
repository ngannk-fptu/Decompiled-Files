/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AccessType
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import javax.persistence.AccessType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.mapping.spi.Adapter1;
import org.hibernate.boot.jaxb.mapping.spi.EntityOrMappedSuperclass;
import org.hibernate.boot.jaxb.mapping.spi.JaxbAttributes;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmptyType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityListeners;
import org.hibernate.boot.jaxb.mapping.spi.JaxbIdClass;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostLoad;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostPersist;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostRemove;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostUpdate;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPrePersist;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPreRemove;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPreUpdate;
import org.hibernate.boot.jaxb.mapping.spi.LifecycleCallbackContainer;
import org.hibernate.boot.jaxb.mapping.spi.ManagedType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="mapped-superclass", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description", "idClass", "excludeDefaultListeners", "excludeSuperclassListeners", "entityListeners", "prePersist", "postPersist", "preRemove", "postRemove", "preUpdate", "postUpdate", "postLoad", "attributes"})
public class JaxbMappedSuperclass
implements Serializable,
EntityOrMappedSuperclass,
LifecycleCallbackContainer,
ManagedType {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlElement(name="id-class", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbIdClass idClass;
    @XmlElement(name="exclude-default-listeners", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbEmptyType excludeDefaultListeners;
    @XmlElement(name="exclude-superclass-listeners", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbEmptyType excludeSuperclassListeners;
    @XmlElement(name="entity-listeners", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbEntityListeners entityListeners;
    @XmlElement(name="pre-persist", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbPrePersist prePersist;
    @XmlElement(name="post-persist", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbPostPersist postPersist;
    @XmlElement(name="pre-remove", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbPreRemove preRemove;
    @XmlElement(name="post-remove", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbPostRemove postRemove;
    @XmlElement(name="pre-update", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbPreUpdate preUpdate;
    @XmlElement(name="post-update", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbPostUpdate postUpdate;
    @XmlElement(name="post-load", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbPostLoad postLoad;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbAttributes attributes;
    @XmlAttribute(name="class", required=true)
    protected String clazz;
    @XmlAttribute(name="access")
    @XmlJavaTypeAdapter(value=Adapter1.class)
    protected AccessType access;
    @XmlAttribute(name="metadata-complete")
    protected Boolean metadataComplete;

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String value) {
        this.description = value;
    }

    @Override
    public JaxbIdClass getIdClass() {
        return this.idClass;
    }

    @Override
    public void setIdClass(JaxbIdClass value) {
        this.idClass = value;
    }

    @Override
    public JaxbEmptyType getExcludeDefaultListeners() {
        return this.excludeDefaultListeners;
    }

    @Override
    public void setExcludeDefaultListeners(JaxbEmptyType value) {
        this.excludeDefaultListeners = value;
    }

    @Override
    public JaxbEmptyType getExcludeSuperclassListeners() {
        return this.excludeSuperclassListeners;
    }

    @Override
    public void setExcludeSuperclassListeners(JaxbEmptyType value) {
        this.excludeSuperclassListeners = value;
    }

    @Override
    public JaxbEntityListeners getEntityListeners() {
        return this.entityListeners;
    }

    @Override
    public void setEntityListeners(JaxbEntityListeners value) {
        this.entityListeners = value;
    }

    @Override
    public JaxbPrePersist getPrePersist() {
        return this.prePersist;
    }

    @Override
    public void setPrePersist(JaxbPrePersist value) {
        this.prePersist = value;
    }

    @Override
    public JaxbPostPersist getPostPersist() {
        return this.postPersist;
    }

    @Override
    public void setPostPersist(JaxbPostPersist value) {
        this.postPersist = value;
    }

    @Override
    public JaxbPreRemove getPreRemove() {
        return this.preRemove;
    }

    @Override
    public void setPreRemove(JaxbPreRemove value) {
        this.preRemove = value;
    }

    @Override
    public JaxbPostRemove getPostRemove() {
        return this.postRemove;
    }

    @Override
    public void setPostRemove(JaxbPostRemove value) {
        this.postRemove = value;
    }

    @Override
    public JaxbPreUpdate getPreUpdate() {
        return this.preUpdate;
    }

    @Override
    public void setPreUpdate(JaxbPreUpdate value) {
        this.preUpdate = value;
    }

    @Override
    public JaxbPostUpdate getPostUpdate() {
        return this.postUpdate;
    }

    @Override
    public void setPostUpdate(JaxbPostUpdate value) {
        this.postUpdate = value;
    }

    @Override
    public JaxbPostLoad getPostLoad() {
        return this.postLoad;
    }

    @Override
    public void setPostLoad(JaxbPostLoad value) {
        this.postLoad = value;
    }

    @Override
    public JaxbAttributes getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(JaxbAttributes value) {
        this.attributes = value;
    }

    @Override
    public String getClazz() {
        return this.clazz;
    }

    @Override
    public void setClazz(String value) {
        this.clazz = value;
    }

    @Override
    public AccessType getAccess() {
        return this.access;
    }

    @Override
    public void setAccess(AccessType value) {
        this.access = value;
    }

    @Override
    public Boolean isMetadataComplete() {
        return this.metadataComplete;
    }

    @Override
    public void setMetadataComplete(Boolean value) {
        this.metadataComplete = value;
    }
}

