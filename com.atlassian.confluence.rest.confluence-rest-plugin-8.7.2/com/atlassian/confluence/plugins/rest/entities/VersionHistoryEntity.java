/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.VersionHistory
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.core.VersionHistory;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="attachment")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class VersionHistoryEntity {
    @XmlAttribute
    private String versionTag;
    @XmlAttribute
    private int buildNumber;
    @XmlAttribute
    private Date installationDate;

    public VersionHistoryEntity(VersionHistory versionHistory) {
        this.installationDate = versionHistory.getInstallationDate();
        this.buildNumber = versionHistory.getBuildNumber();
        this.versionTag = versionHistory.getVersionTag();
    }

    public String toString() {
        return new StringJoiner(", ", VersionHistoryEntity.class.getSimpleName() + "[", "]").add("versionTag='" + this.versionTag + "'").add("buildNumber=" + this.buildNumber).add("installationDate=" + this.installationDate).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VersionHistoryEntity)) {
            return false;
        }
        VersionHistoryEntity that = (VersionHistoryEntity)o;
        return this.buildNumber == that.buildNumber && Objects.equals(this.versionTag, that.versionTag) && Objects.equals(this.installationDate, that.installationDate);
    }

    public int hashCode() {
        return Objects.hash(this.versionTag, this.buildNumber, this.installationDate);
    }
}

