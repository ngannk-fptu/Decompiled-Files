/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.sso.idp;

import java.util.Date;
import java.util.Objects;

public class PemCertificate {
    private String pemCertificate;
    private Date createdDate;
    private Date expirationDate;

    public PemCertificate() {
    }

    public PemCertificate(String pemCertificate, Date createdDate, Date expirationDate) {
        this.pemCertificate = pemCertificate;
        this.createdDate = createdDate;
        this.expirationDate = expirationDate;
    }

    public String getPemCertificate() {
        return this.pemCertificate;
    }

    public void setPemCertificate(String pemCertificate) {
        this.pemCertificate = pemCertificate;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getExpirationDate() {
        return this.expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PemCertificate that = (PemCertificate)o;
        return Objects.equals(this.pemCertificate, that.pemCertificate) && Objects.equals(this.createdDate, that.createdDate) && Objects.equals(this.expirationDate, that.expirationDate);
    }

    public int hashCode() {
        return Objects.hash(this.pemCertificate, this.createdDate, this.expirationDate);
    }

    public String toString() {
        return "PemCertificate{pemCertificate='" + this.pemCertificate + '\'' + ", createdDate=" + this.createdDate + ", expirationDate=" + this.expirationDate + '}';
    }
}

