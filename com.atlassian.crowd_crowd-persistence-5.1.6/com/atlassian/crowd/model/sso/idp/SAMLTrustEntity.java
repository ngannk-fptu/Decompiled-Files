/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.sso.idp;

import com.atlassian.crowd.model.sso.idp.PemCertificate;
import com.atlassian.crowd.model.sso.idp.PemPrivateKey;
import java.util.Objects;

public class SAMLTrustEntity {
    private Long id;
    private PemCertificate pemCertificate;
    private PemPrivateKey pemPrivateKey;

    public SAMLTrustEntity() {
    }

    public SAMLTrustEntity(PemCertificate pemCertificate, PemPrivateKey pemPrivateKey) {
        this.pemCertificate = pemCertificate;
        this.pemPrivateKey = pemPrivateKey;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PemCertificate getPemCertificate() {
        return this.pemCertificate;
    }

    public void setPemCertificate(PemCertificate pemCertificate) {
        this.pemCertificate = pemCertificate;
    }

    public PemPrivateKey getPemPrivateKey() {
        return this.pemPrivateKey;
    }

    public void setPemPrivateKey(PemPrivateKey pemPrivateKey) {
        this.pemPrivateKey = pemPrivateKey;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SAMLTrustEntity that = (SAMLTrustEntity)o;
        return Objects.equals(this.id, that.id) && Objects.equals(this.pemCertificate, that.pemCertificate) && Objects.equals(this.pemPrivateKey, that.pemPrivateKey);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.pemCertificate, this.pemPrivateKey);
    }

    public String toString() {
        return "SAMLTrustEntity{id=" + this.id + ", pemCertificate=" + this.pemCertificate + ", pemPrivateKey=" + this.pemPrivateKey + '}';
    }
}

