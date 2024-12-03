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
import org.hibernate.boot.jaxb.mapping.spi.JaxbStoredProcedureParameter;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="named-stored-procedure-query", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description", "parameter", "resultClass", "resultSetMapping", "hint"})
public class JaxbNamedStoredProcedureQuery
implements Serializable {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbStoredProcedureParameter> parameter;
    @XmlElement(name="result-class", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<String> resultClass;
    @XmlElement(name="result-set-mapping", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<String> resultSetMapping;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbQueryHint> hint;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="procedure-name", required=true)
    protected String procedureName;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public List<JaxbStoredProcedureParameter> getParameter() {
        if (this.parameter == null) {
            this.parameter = new ArrayList<JaxbStoredProcedureParameter>();
        }
        return this.parameter;
    }

    public List<String> getResultClass() {
        if (this.resultClass == null) {
            this.resultClass = new ArrayList<String>();
        }
        return this.resultClass;
    }

    public List<String> getResultSetMapping() {
        if (this.resultSetMapping == null) {
            this.resultSetMapping = new ArrayList<String>();
        }
        return this.resultSetMapping;
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

    public String getProcedureName() {
        return this.procedureName;
    }

    public void setProcedureName(String value) {
        this.procedureName = value;
    }
}

