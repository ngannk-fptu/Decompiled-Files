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

import com.atlassian.crowd.plugin.rest.entity.UserEntity;
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

@XmlRootElement(name="users")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class UserEntityList
implements Iterable<UserEntity> {
    public static final String USER_LIST_FIELD_NAME = "users";
    @XmlAttribute
    private String expand;
    @Expandable(value="user")
    @XmlElement(name="user")
    @JsonProperty(value="users")
    private final List<UserEntity> users;

    private UserEntityList() {
        this.users = new ArrayList<UserEntity>();
    }

    public UserEntityList(List<UserEntity> users) {
        this.users = ImmutableList.copyOf((Collection)((Collection)Preconditions.checkNotNull(users)));
    }

    public int size() {
        return this.users.size();
    }

    public boolean isEmpty() {
        return this.users.isEmpty();
    }

    public UserEntity get(int index) {
        return this.users.get(index);
    }

    @Override
    public Iterator<UserEntity> iterator() {
        return this.users.iterator();
    }
}

