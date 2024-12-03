/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Membership
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.integration.rest.entity;

import com.atlassian.crowd.integration.rest.entity.MembershipEntity;
import com.atlassian.crowd.model.group.Membership;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="memberships")
public class MembershipsEntity {
    @XmlElement(name="membership")
    private List<MembershipEntity> memberships;

    public MembershipsEntity() {
        this(new ArrayList<MembershipEntity>());
    }

    public MembershipsEntity(List<MembershipEntity> list) {
        this.memberships = list;
    }

    public List<? extends Membership> getList() {
        return this.memberships;
    }

    public String toString() {
        return "Memberships:" + this.memberships;
    }
}

