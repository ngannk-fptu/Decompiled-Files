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
import org.hibernate.boot.jaxb.mapping.spi.AttributesContainer;
import org.hibernate.boot.jaxb.mapping.spi.JaxbBasic;
import org.hibernate.boot.jaxb.mapping.spi.JaxbElementCollection;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmbedded;
import org.hibernate.boot.jaxb.mapping.spi.JaxbManyToMany;
import org.hibernate.boot.jaxb.mapping.spi.JaxbManyToOne;
import org.hibernate.boot.jaxb.mapping.spi.JaxbOneToMany;
import org.hibernate.boot.jaxb.mapping.spi.JaxbOneToOne;
import org.hibernate.boot.jaxb.mapping.spi.JaxbTransient;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="embeddable-attributes", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"basic", "manyToOne", "oneToMany", "oneToOne", "manyToMany", "elementCollection", "embedded", "_transient"})
public class JaxbEmbeddableAttributes
implements Serializable,
AttributesContainer {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbBasic> basic;
    @XmlElement(name="many-to-one", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbManyToOne> manyToOne;
    @XmlElement(name="one-to-many", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbOneToMany> oneToMany;
    @XmlElement(name="one-to-one", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbOneToOne> oneToOne;
    @XmlElement(name="many-to-many", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbManyToMany> manyToMany;
    @XmlElement(name="element-collection", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbElementCollection> elementCollection;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbEmbedded> embedded;
    @XmlElement(name="transient", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbTransient> _transient;

    @Override
    public List<JaxbBasic> getBasic() {
        if (this.basic == null) {
            this.basic = new ArrayList<JaxbBasic>();
        }
        return this.basic;
    }

    @Override
    public List<JaxbManyToOne> getManyToOne() {
        if (this.manyToOne == null) {
            this.manyToOne = new ArrayList<JaxbManyToOne>();
        }
        return this.manyToOne;
    }

    @Override
    public List<JaxbOneToMany> getOneToMany() {
        if (this.oneToMany == null) {
            this.oneToMany = new ArrayList<JaxbOneToMany>();
        }
        return this.oneToMany;
    }

    @Override
    public List<JaxbOneToOne> getOneToOne() {
        if (this.oneToOne == null) {
            this.oneToOne = new ArrayList<JaxbOneToOne>();
        }
        return this.oneToOne;
    }

    @Override
    public List<JaxbManyToMany> getManyToMany() {
        if (this.manyToMany == null) {
            this.manyToMany = new ArrayList<JaxbManyToMany>();
        }
        return this.manyToMany;
    }

    @Override
    public List<JaxbElementCollection> getElementCollection() {
        if (this.elementCollection == null) {
            this.elementCollection = new ArrayList<JaxbElementCollection>();
        }
        return this.elementCollection;
    }

    @Override
    public List<JaxbEmbedded> getEmbedded() {
        if (this.embedded == null) {
            this.embedded = new ArrayList<JaxbEmbedded>();
        }
        return this.embedded;
    }

    @Override
    public List<JaxbTransient> getTransient() {
        if (this._transient == null) {
            this._transient = new ArrayList<JaxbTransient>();
        }
        return this._transient;
    }
}

