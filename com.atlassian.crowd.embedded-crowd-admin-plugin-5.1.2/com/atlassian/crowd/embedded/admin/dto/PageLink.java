/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.embedded.admin.dto;

import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PageLink {
    @XmlElement
    private String text;
    @XmlElement
    private int pageNumber;
    @XmlElement
    private boolean isSelected;

    public PageLink(String text, int pageNumber) {
        this(text, pageNumber, false);
    }

    public PageLink(String text, int pageNumber, boolean isSelected) {
        this.text = text;
        this.pageNumber = pageNumber;
        this.isSelected = isSelected;
    }

    public String getText() {
        return this.text;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public boolean getIsSelected() {
        return this.isSelected;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PageLink)) {
            return false;
        }
        PageLink pageLink = (PageLink)o;
        return Objects.equals(this.pageNumber, pageLink.pageNumber) && Objects.equals(this.pageNumber, pageLink.pageNumber) && Objects.equals(this.isSelected, pageLink.isSelected) && Objects.equals(this.text, pageLink.text);
    }

    public int hashCode() {
        return Objects.hash(this.text, this.pageNumber, this.isSelected);
    }
}

