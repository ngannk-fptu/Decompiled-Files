/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown=true)
public class SetViewSpacePermissionRequest {
    public String spaceKey;
    public boolean enablePermission;

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public void setEnablePermission(boolean enablePermission) {
        this.enablePermission = enablePermission;
    }
}

