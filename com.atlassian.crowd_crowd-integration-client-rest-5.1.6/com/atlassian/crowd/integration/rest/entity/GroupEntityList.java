/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElements
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.integration.rest.entity;

import com.atlassian.crowd.integration.rest.entity.GroupEntity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="groups")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class GroupEntityList
implements Iterable<GroupEntity> {
    @XmlElements(value={@XmlElement(name="group")})
    private final List<GroupEntity> groups;

    private GroupEntityList() {
        this.groups = new ArrayList<GroupEntity>();
    }

    public GroupEntityList(List<GroupEntity> groups) {
        this.groups = new ArrayList<GroupEntity>(groups);
    }

    public int size() {
        return this.groups.size();
    }

    public boolean isEmpty() {
        return this.groups.isEmpty();
    }

    public GroupEntity get(int index) {
        return this.groups.get(index);
    }

    @Override
    public Iterator<GroupEntity> iterator() {
        return this.groups.iterator();
    }
}

