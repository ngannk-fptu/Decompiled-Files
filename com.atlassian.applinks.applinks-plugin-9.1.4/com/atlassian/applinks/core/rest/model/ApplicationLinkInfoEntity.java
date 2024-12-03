/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="applicationLinkInfo")
public class ApplicationLinkInfoEntity {
    @XmlElement(name="configuredAuthProviders")
    List<String> configuredAuthProviders;
    @XmlElement(name="hostEntityTypes")
    List<String> hostEntityTypes;
    @XmlElement(name="remoteEntityTypes")
    ArrayList<String> remoteEntityTypes;
    @XmlElement(name="numConfiguredEntities")
    int numConfiguredEntities;

    public ApplicationLinkInfoEntity() {
    }

    public ApplicationLinkInfoEntity(List<String> configuredAuthProviders, int numConfiguredEntities, List<String> hostEntityTypes, ArrayList<String> remoteEntityTypes) {
        this.configuredAuthProviders = configuredAuthProviders;
        this.numConfiguredEntities = numConfiguredEntities;
        this.hostEntityTypes = hostEntityTypes;
        this.remoteEntityTypes = remoteEntityTypes;
    }

    public List<String> getConfiguredAuthProviders() {
        return this.configuredAuthProviders;
    }

    public List<String> getHostEntityTypes() {
        return this.hostEntityTypes;
    }

    public int getNumConfiguredEntities() {
        return this.numConfiguredEntities;
    }

    public ArrayList<String> getRemoteEntityTypes() {
        return this.remoteEntityTypes;
    }
}

