/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.auth.OrphanedTrustCertificate;
import com.atlassian.applinks.core.rest.model.OrphanedTrust;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="orphanedTrustList")
public class OrphanedTrustEntityList {
    @XmlElement(name="orphanedTrust")
    private List<OrphanedTrust> orphanedTrustList;

    private OrphanedTrustEntityList() {
    }

    public OrphanedTrustEntityList(List<OrphanedTrustCertificate> orphanedTrustCertificates) {
        this.orphanedTrustList = Lists.transform(orphanedTrustCertificates, (Function)new Function<OrphanedTrustCertificate, OrphanedTrust>(){

            public OrphanedTrust apply(OrphanedTrustCertificate from) {
                return new OrphanedTrust(from.getId(), from.getType());
            }
        });
    }

    public List<OrphanedTrust> getOrphanedTrustList() {
        return this.orphanedTrustList;
    }
}

