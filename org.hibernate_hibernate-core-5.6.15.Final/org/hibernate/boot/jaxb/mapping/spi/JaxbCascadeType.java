/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmptyType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="cascade-type", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"cascadeAll", "cascadePersist", "cascadeMerge", "cascadeRemove", "cascadeRefresh", "cascadeDetach"})
public class JaxbCascadeType
implements Serializable {
    @XmlElement(name="cascade-all", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbEmptyType cascadeAll;
    @XmlElement(name="cascade-persist", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbEmptyType cascadePersist;
    @XmlElement(name="cascade-merge", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbEmptyType cascadeMerge;
    @XmlElement(name="cascade-remove", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbEmptyType cascadeRemove;
    @XmlElement(name="cascade-refresh", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbEmptyType cascadeRefresh;
    @XmlElement(name="cascade-detach", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbEmptyType cascadeDetach;

    public JaxbEmptyType getCascadeAll() {
        return this.cascadeAll;
    }

    public void setCascadeAll(JaxbEmptyType value) {
        this.cascadeAll = value;
    }

    public JaxbEmptyType getCascadePersist() {
        return this.cascadePersist;
    }

    public void setCascadePersist(JaxbEmptyType value) {
        this.cascadePersist = value;
    }

    public JaxbEmptyType getCascadeMerge() {
        return this.cascadeMerge;
    }

    public void setCascadeMerge(JaxbEmptyType value) {
        this.cascadeMerge = value;
    }

    public JaxbEmptyType getCascadeRemove() {
        return this.cascadeRemove;
    }

    public void setCascadeRemove(JaxbEmptyType value) {
        this.cascadeRemove = value;
    }

    public JaxbEmptyType getCascadeRefresh() {
        return this.cascadeRefresh;
    }

    public void setCascadeRefresh(JaxbEmptyType value) {
        this.cascadeRefresh = value;
    }

    public JaxbEmptyType getCascadeDetach() {
        return this.cascadeDetach;
    }

    public void setCascadeDetach(JaxbEmptyType value) {
        this.cascadeDetach = value;
    }
}

