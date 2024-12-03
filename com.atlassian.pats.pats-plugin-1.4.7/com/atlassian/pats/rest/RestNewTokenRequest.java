/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.pats.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
public class RestNewTokenRequest {
    @XmlElement
    private String name;
    @XmlElement
    private Integer expirationDuration;

    public String getName() {
        return this.name;
    }

    public Integer getExpirationDuration() {
        return this.expirationDuration;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExpirationDuration(Integer expirationDuration) {
        this.expirationDuration = expirationDuration;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestNewTokenRequest)) {
            return false;
        }
        RestNewTokenRequest other = (RestNewTokenRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$expirationDuration = this.getExpirationDuration();
        Integer other$expirationDuration = other.getExpirationDuration();
        if (this$expirationDuration == null ? other$expirationDuration != null : !((Object)this$expirationDuration).equals(other$expirationDuration)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        return !(this$name == null ? other$name != null : !this$name.equals(other$name));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestNewTokenRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $expirationDuration = this.getExpirationDuration();
        result = result * 59 + ($expirationDuration == null ? 43 : ((Object)$expirationDuration).hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        return result;
    }

    public String toString() {
        return "RestNewTokenRequest(name=" + this.getName() + ", expirationDuration=" + this.getExpirationDuration() + ")";
    }

    public RestNewTokenRequest() {
    }

    public RestNewTokenRequest(String name, Integer expirationDuration) {
        this.name = name;
        this.expirationDuration = expirationDuration;
    }
}

