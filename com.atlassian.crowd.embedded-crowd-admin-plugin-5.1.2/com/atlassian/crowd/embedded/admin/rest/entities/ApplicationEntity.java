/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.embedded.admin.rest.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="application")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class ApplicationEntity {
    @XmlAttribute
    private Boolean membershipAggregationEnabled;

    public Boolean isMembershipAggregationEnabled() {
        return this.membershipAggregationEnabled;
    }

    public void setMembershipAggregationEnabled(boolean membershipAggregationEnabled) {
        this.membershipAggregationEnabled = membershipAggregationEnabled;
    }
}

