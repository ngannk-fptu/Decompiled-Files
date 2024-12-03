/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.adapter.PermissionCodeAdapter;
import com.atlassian.applinks.core.rest.model.adapter.RequiredURIAdapter;
import com.atlassian.applinks.core.rest.permission.PermissionCode;
import java.net.URI;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class PermissionCodeEntity {
    @XmlJavaTypeAdapter(value=PermissionCodeAdapter.class)
    private PermissionCode code;
    @XmlJavaTypeAdapter(value=RequiredURIAdapter.class)
    private URI url;

    private PermissionCodeEntity() {
    }

    public PermissionCodeEntity(PermissionCode code) {
        this.code = code;
    }

    public PermissionCodeEntity(PermissionCode code, URI url) {
        this.code = code;
        this.url = url;
    }

    public PermissionCode getCode() {
        return this.code;
    }

    public URI getUrl() {
        return this.url;
    }
}

