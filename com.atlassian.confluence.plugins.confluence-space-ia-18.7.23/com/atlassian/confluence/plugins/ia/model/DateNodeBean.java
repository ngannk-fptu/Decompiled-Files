/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.ia.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DateNodeBean {
    @XmlElement
    List<?> children = new ArrayList();
    @XmlElement
    boolean hasChildren = true;
    @XmlElement
    String title;
    @XmlElement
    int groupType;
    @XmlElement
    String groupValue;

    public DateNodeBean(String title, int groupType, String groupValue) {
        this.title = title;
        this.groupType = groupType;
        this.groupValue = groupValue;
    }

    public List<?> getChildren() {
        return this.children;
    }

    public void setChildren(List<?> children) {
        this.children = children;
    }

    public boolean isHasChildren() {
        return this.hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getGroupType() {
        return this.groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public String getGroupValue() {
        return this.groupValue;
    }

    public void setGroupValue(String groupValue) {
        this.groupValue = groupValue;
    }
}

