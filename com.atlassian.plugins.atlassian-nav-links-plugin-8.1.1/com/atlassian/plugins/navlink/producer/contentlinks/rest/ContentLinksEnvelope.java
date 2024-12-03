/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.plugins.navlink.producer.contentlinks.rest;

import com.atlassian.plugins.navlink.producer.contentlinks.rest.ContentLinkEntity;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="content-links-envelope")
@XmlAccessorType(value=XmlAccessType.PROPERTY)
public class ContentLinksEnvelope {
    private List<ContentLinkEntity> contentLinks = new ArrayList<ContentLinkEntity>();

    public ContentLinksEnvelope() {
    }

    public ContentLinksEnvelope(List<ContentLinkEntity> contentLinks) {
        this.setContentLinks(contentLinks);
    }

    public List<ContentLinkEntity> getContentLinks() {
        return new ArrayList<ContentLinkEntity>(this.contentLinks);
    }

    public void setContentLinks(List<ContentLinkEntity> contentLinks) {
        this.contentLinks.clear();
        this.contentLinks.addAll(contentLinks);
    }
}

