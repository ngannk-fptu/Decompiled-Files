/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.featurediscovery.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DiscoveredFeature {
    @XmlElement
    private final String pluginKey;
    @XmlElement
    private final String featureKey;
    @XmlElement
    private final String userKey;
    @XmlElement
    private final Date date;

    public DiscoveredFeature(String pluginKey, String featureKey, String userKey, Date date) {
        this.pluginKey = pluginKey;
        this.featureKey = featureKey;
        this.userKey = userKey;
        this.date = date;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public String getFeatureKey() {
        return this.featureKey;
    }

    public String getUserKey() {
        return this.userKey;
    }

    public Date getDate() {
        return this.date;
    }
}

