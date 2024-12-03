/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.expand.Expandable
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity;

import com.atlassian.crowd.plugin.rest.entity.GroupEntity;
import com.atlassian.plugins.rest.common.expand.Expandable;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement(name="groups")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class GroupEntityList
implements Iterable<GroupEntity> {
    public static final String GROUP_LIST_FIELD_NAME = "groups";
    @XmlAttribute
    private String expand;
    @Expandable(value="group")
    @XmlElement(name="group")
    @JsonProperty(value="groups")
    private final List<GroupEntity> groups;

    private GroupEntityList() {
        this.groups = new ArrayList<GroupEntity>();
    }

    public GroupEntityList(List<GroupEntity> groups) {
        this.groups = ImmutableList.copyOf((Collection)((Collection)Preconditions.checkNotNull(groups)));
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

