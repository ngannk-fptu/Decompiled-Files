/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.LinkedEntity;
import com.atlassian.plugins.rest.common.Link;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement(name="authenticationProvider")
@JsonIgnoreProperties(ignoreUnknown=true)
public class AuthenticationProviderEntity
extends LinkedEntity {
    @XmlElement(name="config")
    private HashMap<String, String> config;
    @XmlElement(name="module")
    private String module;
    @XmlElement(name="provider")
    private String provider;

    public AuthenticationProviderEntity() {
    }

    public AuthenticationProviderEntity(Link self, String module, String provider, Map<String, String> config) {
        this.module = module;
        this.provider = provider;
        if (config != null) {
            this.config = new HashMap<String, String>(config);
        }
        this.addLink(self);
    }

    @Nullable
    public HashMap<String, String> getConfig() {
        return this.config;
    }

    @Nullable
    public String getModule() {
        return this.module;
    }

    @Nullable
    public String getProvider() {
        return this.provider;
    }
}

