/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.BodyContent
 *  com.atlassian.confluence.core.BodyType
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlValue
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="body")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class ContentBodyEntity {
    @XmlValue
    private String value;
    @XmlAttribute(name="type")
    private String type;

    public ContentBodyEntity() {
    }

    public ContentBodyEntity(String value, BodyType bodyType) {
        this.value = value;
        this.type = String.valueOf(bodyType.toInt());
    }

    public ContentBodyEntity(BodyContent bodyContent) {
        this.value = bodyContent.getBody();
        this.type = String.valueOf(bodyContent.getBodyType().toInt());
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return new StringJoiner(", ", ContentBodyEntity.class.getSimpleName() + "[", "]").add("value='" + this.value + "'").add("type='" + this.type + "'").toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContentBodyEntity)) {
            return false;
        }
        ContentBodyEntity that = (ContentBodyEntity)o;
        return Objects.equals(this.value, that.value) && Objects.equals(this.type, that.type);
    }

    public int hashCode() {
        return Objects.hash(this.value, this.type);
    }
}

