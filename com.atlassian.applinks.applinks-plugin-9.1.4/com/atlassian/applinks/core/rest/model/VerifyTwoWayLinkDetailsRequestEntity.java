/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import java.net.URI;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="verifyTwoWayLinkDetailsRequest")
public class VerifyTwoWayLinkDetailsRequestEntity {
    private String username;
    private String password;
    private URI remoteUrl;
    private URI rpcUrl;

    private VerifyTwoWayLinkDetailsRequestEntity() {
    }

    public VerifyTwoWayLinkDetailsRequestEntity(String username, String password, URI remoteUrl, URI rpcUrl) {
        this.username = username;
        this.password = password;
        this.remoteUrl = remoteUrl;
        this.rpcUrl = rpcUrl;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public URI getRemoteUrl() {
        return this.remoteUrl;
    }

    public URI getRpcUrl() {
        return this.rpcUrl;
    }
}

