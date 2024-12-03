/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BuildInformation
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.setup.BuildInformation;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="buildInfo")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class BuildInfoEntity {
    @XmlAttribute
    private String versionNumber;
    @XmlAttribute
    private Date buildDate;
    @XmlAttribute
    private String buildTimestamp;
    @XmlAttribute
    private String buildNumber;
    @XmlAttribute
    private String bambooBuildNumber;
    @XmlAttribute
    private String revisionNumber;

    public BuildInfoEntity() {
    }

    public BuildInfoEntity(String versionNumber, Date buildDate, String buildNumber, String bambooBuildNumber, String revisionNumber) {
        this.versionNumber = versionNumber;
        this.buildDate = buildDate;
        this.buildNumber = buildNumber;
        this.bambooBuildNumber = bambooBuildNumber;
        this.revisionNumber = revisionNumber;
    }

    public BuildInfoEntity(BuildInformation instance) {
        this(instance.getVersionNumber(), instance.getBuildDate(), instance.getBuildNumber(), instance.getBambooBuildNumber(), instance.getGitCommitHash());
        this.buildTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH).format(instance.getBuildTimestamp());
    }

    public String toString() {
        return new StringJoiner(", ", BuildInfoEntity.class.getSimpleName() + "[", "]").add("versionNumber='" + this.versionNumber + "'").add("buildDate=" + this.buildDate.toInstant().toString()).add("buildTimestamp='" + this.buildTimestamp + "'").add("buildNumber='" + this.buildNumber + "'").add("bambooBuildNumber='" + this.bambooBuildNumber + "'").add("revisionNumber='" + this.revisionNumber + "'").toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BuildInfoEntity)) {
            return false;
        }
        BuildInfoEntity that = (BuildInfoEntity)o;
        return Objects.equals(this.versionNumber, that.versionNumber) && Objects.equals(this.buildDate, that.buildDate) && Objects.equals(this.buildTimestamp, that.buildTimestamp) && Objects.equals(this.buildNumber, that.buildNumber) && Objects.equals(this.bambooBuildNumber, that.bambooBuildNumber) && Objects.equals(this.revisionNumber, that.revisionNumber);
    }

    public int hashCode() {
        return Objects.hash(this.versionNumber, this.buildDate, this.buildTimestamp, this.buildNumber, this.bambooBuildNumber, this.revisionNumber);
    }
}

