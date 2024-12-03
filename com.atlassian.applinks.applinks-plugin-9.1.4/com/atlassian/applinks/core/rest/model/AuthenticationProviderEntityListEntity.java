/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.AuthenticationProviderEntity;
import java.util.List;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="authenticationProviders")
public class AuthenticationProviderEntityListEntity {
    @XmlElement(name="authenticationProviders")
    private List<AuthenticationProviderEntity> authenticationProviders;

    public AuthenticationProviderEntityListEntity() {
    }

    public AuthenticationProviderEntityListEntity(List<AuthenticationProviderEntity> authenticationProviders) {
        this.authenticationProviders = authenticationProviders;
    }

    @Nullable
    public List<AuthenticationProviderEntity> getAuthenticationProviders() {
        return this.authenticationProviders;
    }
}

