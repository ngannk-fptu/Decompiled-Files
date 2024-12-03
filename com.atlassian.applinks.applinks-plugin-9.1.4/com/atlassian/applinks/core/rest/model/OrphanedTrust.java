/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.auth.OrphanedTrustCertificate;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="orphanedTrust")
public class OrphanedTrust {
    private String id;
    private String type;

    private OrphanedTrust() {
    }

    public OrphanedTrust(String id, OrphanedTrustCertificate.Type type) {
        this.id = id;
        this.type = type.name();
    }

    public String getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }
}

