/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.ApplicationLinkEntity;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="upgradedApplicationLink")
public class UpgradeApplicationLinkResponseEntity {
    @XmlElement(name="applicationLink")
    private final ApplicationLinkEntity applicationLinkEntity;
    @XmlElement(name="message")
    private final List<String> messages;

    public UpgradeApplicationLinkResponseEntity(ApplicationLinkEntity applicationLinkEntity, List<String> messages) {
        this.applicationLinkEntity = applicationLinkEntity;
        this.messages = messages;
    }

    public ApplicationLinkEntity getApplicationLinkEntity() {
        return this.applicationLinkEntity;
    }

    public List<String> getMessages() {
        return this.messages;
    }
}

