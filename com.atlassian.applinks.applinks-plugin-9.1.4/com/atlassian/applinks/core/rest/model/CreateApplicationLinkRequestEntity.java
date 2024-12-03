/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.ApplicationLinkEntity;
import com.atlassian.applinks.core.rest.model.ConfigurationFormValuesEntity;
import com.atlassian.applinks.core.rest.model.OrphanedTrust;
import com.atlassian.applinks.core.rest.model.adapter.RequiredBaseURIAdapter;
import java.net.URI;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="createApplicationLinkRequest")
public class CreateApplicationLinkRequestEntity {
    private ApplicationLinkEntity applicationLink;
    private String username;
    private String password;
    private boolean customRpcURL;
    @XmlJavaTypeAdapter(value=RequiredBaseURIAdapter.class)
    private URI rpcUrl;
    private boolean createTwoWayLink;
    private ConfigurationFormValuesEntity configFormValues;
    private OrphanedTrust orphanedTrust;

    private CreateApplicationLinkRequestEntity() {
    }

    public CreateApplicationLinkRequestEntity(ApplicationLinkEntity applicationLink, String username, String password, boolean customRpcURL, URI rpcUrl, boolean createTwoWayLink, ConfigurationFormValuesEntity configFormValues) {
        this.applicationLink = applicationLink;
        this.username = username;
        this.password = password;
        this.customRpcURL = customRpcURL;
        this.rpcUrl = rpcUrl;
        this.createTwoWayLink = createTwoWayLink;
        this.configFormValues = configFormValues;
    }

    public ApplicationLinkEntity getApplicationLink() {
        return this.applicationLink;
    }

    public boolean createTwoWayLink() {
        return this.createTwoWayLink;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isCustomRpcURL() {
        return this.customRpcURL;
    }

    public URI getRpcUrl() {
        return this.rpcUrl;
    }

    public ConfigurationFormValuesEntity getConfigFormValues() {
        return this.configFormValues;
    }

    public OrphanedTrust getOrphanedTrust() {
        return this.orphanedTrust;
    }
}

