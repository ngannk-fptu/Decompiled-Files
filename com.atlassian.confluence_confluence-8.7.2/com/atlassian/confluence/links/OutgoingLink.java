/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.Hibernate
 *  com.atlassian.renderer.util.UrlUtil
 */
package com.atlassian.confluence.links;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.hibernate.Hibernate;
import com.atlassian.confluence.links.AbstractLink;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.renderer.util.UrlUtil;

public class OutgoingLink
extends AbstractLink
implements Comparable<OutgoingLink> {
    private String destinationSpaceKey;
    private String destinationPageTitle;
    private String lowerDestinationSpaceKey;
    private String lowerDestinationPageTitle;

    public OutgoingLink() {
    }

    public OutgoingLink(ContentEntityObject sourceContent, String destinationSpaceKey, String destinationPageTitle) {
        this.setSourceContent(sourceContent);
        this.setDestinationPageTitle(destinationPageTitle);
        this.setDestinationSpaceKey(destinationSpaceKey);
    }

    public String getDestinationPageTitle() {
        return this.destinationPageTitle;
    }

    public void setDestinationPageTitle(String destinationPageTitle) {
        if (destinationPageTitle != null && destinationPageTitle.length() > 255) {
            destinationPageTitle = destinationPageTitle.substring(0, 255);
        }
        this.destinationPageTitle = destinationPageTitle;
        this.lowerDestinationPageTitle = GeneralUtil.specialToLowerCase(destinationPageTitle);
    }

    public String getLowerDestinationPageTitle() {
        return this.lowerDestinationPageTitle;
    }

    private void setLowerDestinationPageTitle(String lowerDestinationPageTitle) {
        this.lowerDestinationPageTitle = lowerDestinationPageTitle;
    }

    public String getDestinationSpaceKey() {
        return this.destinationSpaceKey;
    }

    public void setDestinationSpaceKey(String destinationSpaceKey) {
        if (destinationSpaceKey != null && destinationSpaceKey.length() > 255) {
            destinationSpaceKey = destinationSpaceKey.substring(0, 255);
        }
        this.destinationSpaceKey = destinationSpaceKey;
        this.lowerDestinationSpaceKey = GeneralUtil.specialToLowerCase(destinationSpaceKey);
    }

    public String getLowerDestinationSpaceKey() {
        return this.lowerDestinationSpaceKey;
    }

    private void setLowerDestinationSpaceKey(String lowerDestinationSpaceKey) {
        this.lowerDestinationSpaceKey = lowerDestinationSpaceKey;
    }

    public boolean isUrlLink() {
        for (String protocol : UrlUtil.URL_PROTOCOLS) {
            int index = protocol.indexOf(58);
            if (index != -1) {
                protocol = protocol.substring(0, index);
            }
            if (!protocol.equalsIgnoreCase(this.getDestinationSpaceKey())) continue;
            return true;
        }
        return false;
    }

    public String getUrlLink() {
        return this.getDestinationSpaceKey() + ":" + this.getDestinationPageTitle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass((Object)this) != Hibernate.getClass((Object)o)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        OutgoingLink that = (OutgoingLink)o;
        if (this.getDestinationPageTitle() != null ? !this.getDestinationPageTitle().equals(that.getDestinationPageTitle()) : that.getDestinationPageTitle() != null) {
            return false;
        }
        return !(this.getDestinationSpaceKey() != null ? !this.getDestinationSpaceKey().equals(that.getDestinationSpaceKey()) : that.getDestinationSpaceKey() != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.getDestinationSpaceKey() != null ? this.getDestinationSpaceKey().hashCode() : 0);
        result = 29 * result + (this.getDestinationPageTitle() != null ? this.getDestinationPageTitle().hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(OutgoingLink other) {
        int comparison = this.getSourceContent().compareTo(other.getSourceContent());
        if (comparison == 0 && (comparison = this.getDestinationSpaceKey().compareTo(other.getDestinationSpaceKey())) == 0) {
            comparison = this.getDestinationPageTitle().compareTo(other.getDestinationPageTitle());
        }
        return comparison;
    }

    public String toString() {
        return this.getUrlLink();
    }

    public boolean isFrom(AbstractPage page) {
        ContentEntityObject source = this.getSourceContent();
        return page != null && source != null && source instanceof AbstractPage && source.getId() == page.getId();
    }
}

