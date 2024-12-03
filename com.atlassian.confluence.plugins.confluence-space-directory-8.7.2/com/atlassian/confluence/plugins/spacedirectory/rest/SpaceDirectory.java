/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.spacedirectory.rest;

import com.atlassian.confluence.plugins.spacedirectory.rest.SpaceDirectoryEntity;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="concise-space-list")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class SpaceDirectory {
    @XmlElement(name="spaces")
    private List<SpaceDirectoryEntity> spaces = new ArrayList<SpaceDirectoryEntity>(0);
    @XmlElement
    private int totalSize = 0;

    public List<SpaceDirectoryEntity> getSpaces() {
        return this.spaces;
    }

    public void setSpaces(List<SpaceDirectoryEntity> spaces) {
        this.spaces = spaces;
    }

    public int getTotalSize() {
        return this.totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }
}

