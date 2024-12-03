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

import com.atlassian.crowd.integration.rest.entity.UserEntity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="users")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class UserEntityList
implements Iterable<UserEntity> {
    @XmlElements(value={@XmlElement(name="user")})
    private final List<UserEntity> users;

    private UserEntityList() {
        this.users = new ArrayList<UserEntity>();
    }

    public UserEntityList(List<UserEntity> users) {
        this.users = new ArrayList<UserEntity>(users);
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

