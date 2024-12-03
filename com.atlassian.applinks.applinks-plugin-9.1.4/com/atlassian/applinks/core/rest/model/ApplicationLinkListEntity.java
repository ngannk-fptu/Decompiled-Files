/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.ApplicationLinkEntity;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="applicationLinks")
public class ApplicationLinkListEntity {
    @ApiModelProperty(name="applicationLinks")
    @XmlElement(name="applicationLinks")
    private List<ApplicationLinkEntity> applicationLinks;

    public ApplicationLinkListEntity() {
    }

    public ApplicationLinkListEntity(List<ApplicationLinkEntity> applicationLinks) {
        this.applicationLinks = applicationLinks;
    }

    public List<ApplicationLinkEntity> getApplications() {
        return this.applicationLinks;
    }
}

