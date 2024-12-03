/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElementRef
 *  javax.xml.bind.annotation.XmlElementRefs
 *  javax.xml.bind.annotation.XmlMixed
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.boot.jaxb.hbm.spi.Adapter1;
import org.hibernate.boot.jaxb.hbm.spi.Adapter5;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="NamedNativeQueryType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"content"})
public class JaxbHbmNamedNativeQueryType
implements Serializable {
    @XmlElementRefs(value={@XmlElementRef(name="query-param", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JAXBElement.class), @XmlElementRef(name="return-scalar", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JAXBElement.class), @XmlElementRef(name="return", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JAXBElement.class), @XmlElementRef(name="return-join", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JAXBElement.class), @XmlElementRef(name="load-collection", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JAXBElement.class), @XmlElementRef(name="synchronize", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JAXBElement.class)})
    @XmlMixed
    protected List<Serializable> content;
    @XmlAttribute(name="cache-mode")
    @XmlJavaTypeAdapter(value=Adapter1.class)
    protected CacheMode cacheMode;
    @XmlAttribute(name="cache-region")
    protected String cacheRegion;
    @XmlAttribute(name="cacheable")
    protected Boolean cacheable;
    @XmlAttribute(name="comment")
    protected String comment;
    @XmlAttribute(name="fetch-size")
    protected Integer fetchSize;
    @XmlAttribute(name="flush-mode")
    @XmlJavaTypeAdapter(value=Adapter5.class)
    protected FlushMode flushMode;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="read-only")
    protected Boolean readOnly;
    @XmlAttribute(name="timeout")
    protected Integer timeout;
    @XmlAttribute(name="callable")
    protected Boolean callable;
    @XmlAttribute(name="resultset-ref")
    protected String resultsetRef;

    public List<Serializable> getContent() {
        if (this.content == null) {
            this.content = new ArrayList<Serializable>();
        }
        return this.content;
    }

    public CacheMode getCacheMode() {
        return this.cacheMode;
    }

    public void setCacheMode(CacheMode value) {
        this.cacheMode = value;
    }

    public String getCacheRegion() {
        return this.cacheRegion;
    }

    public void setCacheRegion(String value) {
        this.cacheRegion = value;
    }

    public boolean isCacheable() {
        if (this.cacheable == null) {
            return false;
        }
        return this.cacheable;
    }

    public void setCacheable(Boolean value) {
        this.cacheable = value;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    public Integer getFetchSize() {
        return this.fetchSize;
    }

    public void setFetchSize(Integer value) {
        this.fetchSize = value;
    }

    public FlushMode getFlushMode() {
        return this.flushMode;
    }

    public void setFlushMode(FlushMode value) {
        this.flushMode = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public boolean isReadOnly() {
        if (this.readOnly == null) {
            return false;
        }
        return this.readOnly;
    }

    public void setReadOnly(Boolean value) {
        this.readOnly = value;
    }

    public Integer getTimeout() {
        return this.timeout;
    }

    public void setTimeout(Integer value) {
        this.timeout = value;
    }

    public boolean isCallable() {
        if (this.callable == null) {
            return false;
        }
        return this.callable;
    }

    public void setCallable(Boolean value) {
        this.callable = value;
    }

    public String getResultsetRef() {
        return this.resultsetRef;
    }

    public void setResultsetRef(String value) {
        this.resultsetRef = value;
    }
}

