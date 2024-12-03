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
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="NativeQueryPropertyReturnType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"returnColumn"})
public class JaxbHbmNativeQueryPropertyReturnType
implements Serializable {
    @XmlElement(name="return-column", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmReturnColumn> returnColumn;
    @XmlAttribute(name="column")
    protected String column;
    @XmlAttribute(name="name", required=true)
    protected String name;

    public List<JaxbHbmReturnColumn> getReturnColumn() {
        if (this.returnColumn == null) {
            this.returnColumn = new ArrayList<JaxbHbmReturnColumn>();
        }
        return this.returnColumn;
    }

    public String getColumn() {
        return this.column;
    }

    public void setColumn(String value) {
        this.column = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @XmlAccessorType(value=XmlAccessType.FIELD)
    @XmlType(name="")
    public static class JaxbHbmReturnColumn
    implements Serializable {
        @XmlAttribute(name="name", required=true)
        protected String name;

        public String getName() {
            return this.name;
        }

        public void setName(String value) {
            this.name = value;
        }
    }
}

