/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.analytics.client.api.browser;

import com.atlassian.analytics.client.api.ClientEvent;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
@XmlRootElement
public class BrowserEvent
implements ClientEvent {
    private final String name;
    private final Map<String, Object> properties;
    private final long clientTime;

    public BrowserEvent(String name, Map<String, Object> properties, long clientTime) {
        this.name = name;
        this.properties = properties;
        this.clientTime = clientTime;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    @Override
    public long getClientTime() {
        return this.clientTime;
    }
}

