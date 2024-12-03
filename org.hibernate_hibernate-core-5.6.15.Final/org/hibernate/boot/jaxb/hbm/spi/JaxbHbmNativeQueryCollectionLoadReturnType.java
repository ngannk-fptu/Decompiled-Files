/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.LockMode;
import org.hibernate.boot.jaxb.hbm.spi.Adapter8;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryPropertyReturnType;
import org.hibernate.boot.jaxb.hbm.spi.NativeQueryNonScalarRootReturn;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="NativeQueryCollectionLoadReturnType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"returnProperty"})
public class JaxbHbmNativeQueryCollectionLoadReturnType
implements Serializable,
NativeQueryNonScalarRootReturn {
    @XmlElement(name="return-property", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmNativeQueryPropertyReturnType> returnProperty;
    @XmlAttribute(name="alias", required=true)
    protected String alias;
    @XmlAttribute(name="lock-mode")
    @XmlJavaTypeAdapter(value=Adapter8.class)
    protected LockMode lockMode;
    @XmlAttribute(name="role", required=true)
    protected String role;

    @Override
    public List<JaxbHbmNativeQueryPropertyReturnType> getReturnProperty() {
        if (this.returnProperty == null) {
            this.returnProperty = new ArrayList<JaxbHbmNativeQueryPropertyReturnType>();
        }
        return this.returnProperty;
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String value) {
        this.alias = value;
    }

    @Override
    public LockMode getLockMode() {
        if (this.lockMode == null) {
            return new Adapter8().unmarshal("read");
        }
        return this.lockMode;
    }

    public void setLockMode(LockMode value) {
        this.lockMode = value;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String value) {
        this.role = value;
    }
}

