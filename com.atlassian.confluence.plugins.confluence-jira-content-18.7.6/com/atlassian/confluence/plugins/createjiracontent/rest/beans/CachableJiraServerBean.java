/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.createjiracontent.rest.beans;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CachableJiraServerBean
implements Serializable {
    @XmlElement
    protected final String id;
    @XmlElement
    protected final String name;
    @XmlElement
    protected final boolean selected;
    @XmlElement
    protected final String authUrl;
    @XmlElement
    protected final String url;
    @XmlElement
    protected final boolean supportedVersion;

    public CachableJiraServerBean(String id, String url, String name, boolean selected, String authUrl, boolean supportedVersion) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.selected = selected;
        this.authUrl = authUrl;
        this.supportedVersion = supportedVersion;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public String getAuthUrl() {
        return this.authUrl;
    }

    public String getUrl() {
        return this.url;
    }

    public boolean isSupportedVersion() {
        return this.supportedVersion;
    }
}

