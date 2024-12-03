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

import com.atlassian.confluence.plugins.rest.entities.LabelEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="labels")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class LabelEntityList {
    @XmlElement(name="label")
    List<LabelEntity> labels = new ArrayList<LabelEntity>();

    public List<LabelEntity> getLabels() {
        return this.labels;
    }

    public void setLabels(List<LabelEntity> labels) {
        this.labels = labels;
    }

    public void addLabel(LabelEntity label) {
        this.labels.add(label);
    }

    public String toString() {
        return new StringJoiner(", ", LabelEntityList.class.getSimpleName() + "[", "]").add("labels=" + this.labels).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LabelEntityList)) {
            return false;
        }
        LabelEntityList that = (LabelEntityList)o;
        return Objects.equals(this.labels, that.labels);
    }

    public int hashCode() {
        return Objects.hash(this.labels);
    }
}

