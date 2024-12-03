/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.ApplicationLinkEntity;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="createdApplicationLink")
public class CreatedApplicationLinkEntity {
    @XmlElement(name="applicationLink")
    private final ApplicationLinkEntity applicationLinkEntity;
    private boolean autoConfigurationSuccessful;

    public CreatedApplicationLinkEntity() {
        this(null, true);
    }

    public CreatedApplicationLinkEntity(ApplicationLinkEntity applicationLinkEntity, boolean autoconfigurationsuccessful) {
        this.applicationLinkEntity = applicationLinkEntity;
        this.autoConfigurationSuccessful = autoconfigurationsuccessful;
    }

    public ApplicationLinkEntity getApplicationLinkEntity() {
        return this.applicationLinkEntity;
    }

    public boolean isAutoConfigurationSuccessful() {
        return this.autoConfigurationSuccessful;
    }
}

