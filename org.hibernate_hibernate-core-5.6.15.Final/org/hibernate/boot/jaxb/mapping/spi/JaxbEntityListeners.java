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
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityListener;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="entity-listeners", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"entityListener"})
public class JaxbEntityListeners
implements Serializable {
    @XmlElement(name="entity-listener", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbEntityListener> entityListener;

    public List<JaxbEntityListener> getEntityListener() {
        if (this.entityListener == null) {
            this.entityListener = new ArrayList<JaxbEntityListener>();
        }
        return this.entityListener;
    }
}

