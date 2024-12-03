/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.gadgets.publishedgadgetsdirectory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
class PublishedGadgetBean
implements Comparable<PublishedGadgetBean> {
    @XmlElement
    private String title;
    @XmlElement
    private String description;
    @XmlElement
    private String author;
    @XmlElement
    private String url;
    @XmlElement
    private String thumbnail;

    PublishedGadgetBean() {
    }

    PublishedGadgetBean(String title, String description, String author, String url, String thumbnail) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.url = url;
        this.thumbnail = thumbnail;
    }

    @Override
    public int compareTo(PublishedGadgetBean o) {
        return this.title.compareTo(o.title);
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getUrl() {
        return this.url;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }
}

