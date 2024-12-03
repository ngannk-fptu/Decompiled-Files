/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.embedded.admin.rest.entities;

import com.atlassian.crowd.embedded.admin.rest.entities.DirectoryEntity;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="directories")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class DirectoryList {
    @XmlElement(name="directory")
    private List<DirectoryEntity> directories = new ArrayList<DirectoryEntity>();

    public List<DirectoryEntity> getDirectories() {
        return this.directories;
    }

    public void setDirectories(List<DirectoryEntity> directories) {
        this.directories = directories;
    }
}

