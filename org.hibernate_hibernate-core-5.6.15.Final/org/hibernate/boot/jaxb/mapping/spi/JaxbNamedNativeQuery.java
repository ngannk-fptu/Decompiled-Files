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
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbQueryHint;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="named-native-query", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description", "query", "hint"})
public class JaxbNamedNativeQuery
implements Serializable {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", required=true)
    protected String query;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbQueryHint> hint;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="result-class")
    protected String resultClass;
    @XmlAttribute(name="result-set-mapping")
    protected String resultSetMapping;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String value) {
        this.query = value;
    }

    public List<JaxbQueryHint> getHint() {
        if (this.hint == null) {
            this.hint = new ArrayList<JaxbQueryHint>();
        }
        return this.hint;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getResultClass() {
        return this.resultClass;
    }

    public void setResultClass(String value) {
        this.resultClass = value;
    }

    public String getResultSetMapping() {
        return this.resultSetMapping;
    }

    public void setResultSetMapping(String value) {
        this.resultSetMapping = value;
    }
}

