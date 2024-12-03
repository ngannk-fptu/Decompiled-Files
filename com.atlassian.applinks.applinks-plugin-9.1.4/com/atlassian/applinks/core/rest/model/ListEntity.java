/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlSeeAlso
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.LinkAndAuthProviderEntity;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement(name="list")
@XmlSeeAlso(value={LinkAndAuthProviderEntity.class})
public class ListEntity<T> {
    @XmlElement(name="list")
    private List<T> list;

    public ListEntity() {
    }

    public ListEntity(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return this.list;
    }
}

