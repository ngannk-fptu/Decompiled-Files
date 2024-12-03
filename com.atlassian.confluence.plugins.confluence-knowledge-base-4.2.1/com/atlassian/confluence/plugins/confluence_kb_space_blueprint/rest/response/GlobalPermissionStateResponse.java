/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown=true)
public class GlobalPermissionStateResponse {
    public boolean unlicensedAuthenticatedAccessEnabled;
    public boolean anonymousAccessEnabled;

    public GlobalPermissionStateResponse(boolean unlicensedAuthenticatedAccessEnabled, boolean anonymousAccessEnabled) {
        this.unlicensedAuthenticatedAccessEnabled = unlicensedAuthenticatedAccessEnabled;
        this.anonymousAccessEnabled = anonymousAccessEnabled;
    }

    public GlobalPermissionStateResponse() {
    }
}

