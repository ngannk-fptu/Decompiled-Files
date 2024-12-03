/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.EntityLinkEntity;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="entities")
public class RestEntityLinkList {
    @XmlElement(name="entity")
    private List<EntityLinkEntity> entities;

    public RestEntityLinkList() {
    }

    public RestEntityLinkList(List<EntityLinkEntity> entities) {
        this.entities = entities;
    }

    public List<EntityLinkEntity> getEntities() {
        return this.entities;
    }
}

