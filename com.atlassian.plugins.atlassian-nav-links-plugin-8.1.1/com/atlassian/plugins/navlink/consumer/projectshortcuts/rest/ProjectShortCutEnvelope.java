/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.plugins.navlink.consumer.projectshortcuts.rest;

import com.atlassian.plugins.navlink.consumer.projectshortcuts.rest.UnauthenticatedRemoteApplication;
import com.atlassian.plugins.navlink.producer.contentlinks.rest.ContentLinkEntity;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="project-shortcuts-envelope")
@XmlAccessorType(value=XmlAccessType.PROPERTY)
public class ProjectShortCutEnvelope {
    private final List<ContentLinkEntity> shortcuts;
    private final List<UnauthenticatedRemoteApplication> unauthenticatedRemoteApplications;

    public ProjectShortCutEnvelope(List<ContentLinkEntity> links, List<UnauthenticatedRemoteApplication> unauthenticatedRemoteApplications) {
        this.shortcuts = links;
        this.unauthenticatedRemoteApplications = unauthenticatedRemoteApplications;
    }

    public List<ContentLinkEntity> getShortcuts() {
        return this.shortcuts;
    }

    public List<UnauthenticatedRemoteApplication> getUnauthenticatedRemoteApplications() {
        return this.unauthenticatedRemoteApplications;
    }
}

