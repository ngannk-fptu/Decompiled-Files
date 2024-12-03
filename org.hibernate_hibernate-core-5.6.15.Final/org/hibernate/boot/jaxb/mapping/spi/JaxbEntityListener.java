/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostLoad;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostPersist;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostRemove;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostUpdate;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPrePersist;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPreRemove;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPreUpdate;
import org.hibernate.boot.jaxb.mapping.spi.LifecycleCallbackContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="entity-listener", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description", "prePersist", "postPersist", "preRemove", "postRemove", "preUpdate", "postUpdate", "postLoad"})
public class JaxbEntityListener
implements Serializable,
LifecycleCallbackContainer {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
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
    @XmlAttribute(name="class", required=true)
    protected String clazz;

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String value) {
        this.description = value;
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
    public String getClazz() {
        return this.clazz;
    }

    @Override
    public void setClazz(String value) {
        this.clazz = value;
    }
}

