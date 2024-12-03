/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.cfg.spi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="MappingReferenceType", namespace="http://www.hibernate.org/xsd/orm/cfg")
public class JaxbCfgMappingReferenceType {
    @XmlAttribute(name="class")
    protected String clazz;
    @XmlAttribute(name="file")
    protected String file;
    @XmlAttribute(name="jar")
    protected String jar;
    @XmlAttribute(name="package")
    protected String _package;
    @XmlAttribute(name="resource")
    protected String resource;

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String value) {
        this.clazz = value;
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String value) {
        this.file = value;
    }

    public String getJar() {
        return this.jar;
    }

    public void setJar(String value) {
        this.jar = value;
    }

    public String getPackage() {
        return this._package;
    }

    public void setPackage(String value) {
        this._package = value;
    }

    public String getResource() {
        return this.resource;
    }

    public void setResource(String value) {
        this.resource = value;
    }
}

