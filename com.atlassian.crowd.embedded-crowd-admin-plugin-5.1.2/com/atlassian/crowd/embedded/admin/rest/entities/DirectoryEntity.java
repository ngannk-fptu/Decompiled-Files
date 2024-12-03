/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.embedded.admin.rest.entities;

import com.atlassian.crowd.embedded.admin.rest.entities.DirectorySynchronisationInformationEntity;
import com.atlassian.plugins.rest.common.Link;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="directory")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class DirectoryEntity {
    @XmlAttribute
    private String name;
    @XmlElement(name="link")
    private List<Link> links = new ArrayList<Link>();
    @XmlElement(name="synchronisation")
    private DirectorySynchronisationInformationEntity sync;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Link> getLinks() {
        return this.links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public DirectorySynchronisationInformationEntity getSync() {
        return this.sync;
    }

    public void setSync(DirectorySynchronisationInformationEntity sync) {
        this.sync = sync;
    }
}

