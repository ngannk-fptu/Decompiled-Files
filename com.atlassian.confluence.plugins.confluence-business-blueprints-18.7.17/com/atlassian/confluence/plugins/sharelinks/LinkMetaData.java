/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.sharelinks;

import java.net.URI;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LinkMetaData {
    @XmlElement
    private final String sourceURL;
    @XmlElement
    private String excerptedURL;
    @XmlElement
    private String title;
    @XmlElement
    private String description;
    @XmlElement
    private String imageURL;
    @XmlElement
    private String videoURL;
    @XmlElement
    private String faviconURL;
    @XmlElement
    private String domain;
    private URI responseHost;
    private String charset;

    public LinkMetaData(String url) {
        this.sourceURL = url;
    }

    public String getSourceURL() {
        return this.sourceURL;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getVideoURL() {
        return this.videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getFaviconURL() {
        return this.faviconURL;
    }

    public void setFaviconURL(String faviconURL) {
        this.faviconURL = faviconURL;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public URI getResponseHost() {
        return this.responseHost;
    }

    public void setResponseHost(URI responseHost) {
        this.responseHost = responseHost;
    }

    public String getExcerptedURL() {
        return this.excerptedURL;
    }

    public void setExcerptedURL(String excerptedURL) {
        this.excerptedURL = excerptedURL;
    }

    public String getCharset() {
        return this.charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}

