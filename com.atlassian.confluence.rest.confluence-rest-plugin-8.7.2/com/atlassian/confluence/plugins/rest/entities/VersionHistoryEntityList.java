/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.plugins.rest.entities.VersionHistoryEntity;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="versions")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class VersionHistoryEntityList {
    @XmlElement(name="version")
    private List<VersionHistoryEntity> versions;

    public VersionHistoryEntityList(List<VersionHistoryEntity> versions) {
        this.versions = versions;
    }

    public String toString() {
        return new StringJoiner(", ", VersionHistoryEntityList.class.getSimpleName() + "[", "]").add("versions=" + this.versions).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VersionHistoryEntityList)) {
            return false;
        }
        VersionHistoryEntityList that = (VersionHistoryEntityList)o;
        return Objects.equals(this.versions, that.versions);
    }

    public int hashCode() {
        return Objects.hash(this.versions);
    }
}

