/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.ConfigurationFormValuesEntity;
import com.atlassian.applinks.core.rest.model.adapter.OptionalURIAdapter;
import java.net.URI;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="upgradeApplicationLink")
public class UpgradeApplicationLinkRequestEntity {
    private String username;
    private String password;
    private boolean createTwoWayLink;
    private boolean reciprocateEntityLinks;
    @XmlJavaTypeAdapter(value=OptionalURIAdapter.class)
    private URI rpcUrl;
    private ConfigurationFormValuesEntity configFormValues;

    public UpgradeApplicationLinkRequestEntity() {
    }

    public UpgradeApplicationLinkRequestEntity(ConfigurationFormValuesEntity configFormValues, boolean createTwoWayLink, String password, boolean reciprocateEntityLinks, String username, URI rpcUrl) {
        this.configFormValues = configFormValues;
        this.createTwoWayLink = createTwoWayLink;
        this.password = password;
        this.reciprocateEntityLinks = reciprocateEntityLinks;
        this.username = username;
        this.rpcUrl = rpcUrl;
    }

    public ConfigurationFormValuesEntity getConfigFormValues() {
        return this.configFormValues;
    }

    public boolean isCreateTwoWayLink() {
        return this.createTwoWayLink;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isReciprocateEntityLinks() {
        return this.reciprocateEntityLinks;
    }

    public String getUsername() {
        return this.username;
    }

    public URI getRpcUrl() {
        return this.rpcUrl;
    }
}

