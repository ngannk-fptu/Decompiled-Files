/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.google.common.base.MoreObjects
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.sso;

import com.atlassian.crowd.plugin.rest.entity.sso.CertificateFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class IdpSamlConfigurationEntity {
    @JsonProperty(value="issuer")
    private final String issuer;
    @JsonProperty(value="ssoUrl")
    private final String ssoUrl;
    @JsonProperty(value="certificateFormat")
    private final CertificateFormat certificateFormat;
    @JsonProperty(value="certificate")
    private final String certificate;
    @JsonProperty(value="expirationDate")
    private final Long expirationDate;

    @JsonCreator
    public IdpSamlConfigurationEntity(@JsonProperty(value="issuer") String issuer, @JsonProperty(value="ssoUrl") String ssoUrl, @JsonProperty(value="certificateFormat") CertificateFormat certificateFormat, @JsonProperty(value="certificate") String certificate, @JsonProperty(value="expirationDate") Long expirationDate) {
        this.issuer = issuer;
        this.ssoUrl = ssoUrl;
        this.certificateFormat = certificateFormat;
        this.certificate = certificate;
        this.expirationDate = expirationDate;
    }

    public String getIssuer() {
        return this.issuer;
    }

    public String getSsoUrl() {
        return this.ssoUrl;
    }

    public CertificateFormat getCertificateFormat() {
        return this.certificateFormat;
    }

    public String getCertificate() {
        return this.certificate;
    }

    public Long getExpirationDate() {
        return this.expirationDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(IdpSamlConfigurationEntity data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        IdpSamlConfigurationEntity that = (IdpSamlConfigurationEntity)o;
        return Objects.equals(this.getIssuer(), that.getIssuer()) && Objects.equals(this.getSsoUrl(), that.getSsoUrl()) && Objects.equals((Object)this.getCertificateFormat(), (Object)that.getCertificateFormat()) && Objects.equals(this.getCertificate(), that.getCertificate()) && Objects.equals(this.getExpirationDate(), that.getExpirationDate());
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getIssuer(), this.getSsoUrl(), this.getCertificateFormat(), this.getCertificate(), this.getExpirationDate()});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("issuer", (Object)this.getIssuer()).add("ssoUrl", (Object)this.getSsoUrl()).add("certificateFormat", (Object)this.getCertificateFormat()).add("certificate", (Object)this.getCertificate()).add("expirationDate", (Object)this.getExpirationDate()).toString();
    }

    public static final class Builder {
        private String issuer;
        private String ssoUrl;
        private CertificateFormat certificateFormat;
        private String certificate;
        private Long expirationDate;

        private Builder() {
        }

        private Builder(IdpSamlConfigurationEntity initialData) {
            this.issuer = initialData.getIssuer();
            this.ssoUrl = initialData.getSsoUrl();
            this.certificateFormat = initialData.getCertificateFormat();
            this.certificate = initialData.getCertificate();
            this.expirationDate = initialData.getExpirationDate();
        }

        public Builder setIssuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder setSsoUrl(String ssoUrl) {
            this.ssoUrl = ssoUrl;
            return this;
        }

        public Builder setCertificateFormat(CertificateFormat certificateFormat) {
            this.certificateFormat = certificateFormat;
            return this;
        }

        public Builder setCertificate(String certificate) {
            this.certificate = certificate;
            return this;
        }

        public Builder setExpirationDate(Long expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public IdpSamlConfigurationEntity build() {
            return new IdpSamlConfigurationEntity(this.issuer, this.ssoUrl, this.certificateFormat, this.certificate, this.expirationDate);
        }
    }
}

