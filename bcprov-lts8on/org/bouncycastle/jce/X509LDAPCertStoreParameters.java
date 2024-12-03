/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce;

import java.security.cert.CertStoreParameters;
import java.security.cert.LDAPCertStoreParameters;

public class X509LDAPCertStoreParameters
implements CertStoreParameters {
    private String ldapURL;
    private String baseDN;
    private String userCertificateAttribute;
    private String cACertificateAttribute;
    private String crossCertificateAttribute;
    private String certificateRevocationListAttribute;
    private String deltaRevocationListAttribute;
    private String authorityRevocationListAttribute;
    private String attributeCertificateAttributeAttribute;
    private String aACertificateAttribute;
    private String attributeDescriptorCertificateAttribute;
    private String attributeCertificateRevocationListAttribute;
    private String attributeAuthorityRevocationListAttribute;
    private String ldapUserCertificateAttributeName;
    private String ldapCACertificateAttributeName;
    private String ldapCrossCertificateAttributeName;
    private String ldapCertificateRevocationListAttributeName;
    private String ldapDeltaRevocationListAttributeName;
    private String ldapAuthorityRevocationListAttributeName;
    private String ldapAttributeCertificateAttributeAttributeName;
    private String ldapAACertificateAttributeName;
    private String ldapAttributeDescriptorCertificateAttributeName;
    private String ldapAttributeCertificateRevocationListAttributeName;
    private String ldapAttributeAuthorityRevocationListAttributeName;
    private String userCertificateSubjectAttributeName;
    private String cACertificateSubjectAttributeName;
    private String crossCertificateSubjectAttributeName;
    private String certificateRevocationListIssuerAttributeName;
    private String deltaRevocationListIssuerAttributeName;
    private String authorityRevocationListIssuerAttributeName;
    private String attributeCertificateAttributeSubjectAttributeName;
    private String aACertificateSubjectAttributeName;
    private String attributeDescriptorCertificateSubjectAttributeName;
    private String attributeCertificateRevocationListIssuerAttributeName;
    private String attributeAuthorityRevocationListIssuerAttributeName;
    private String searchForSerialNumberIn;

    private X509LDAPCertStoreParameters(Builder builder) {
        this.ldapURL = builder.ldapURL;
        this.baseDN = builder.baseDN;
        this.userCertificateAttribute = builder.userCertificateAttribute;
        this.cACertificateAttribute = builder.cACertificateAttribute;
        this.crossCertificateAttribute = builder.crossCertificateAttribute;
        this.certificateRevocationListAttribute = builder.certificateRevocationListAttribute;
        this.deltaRevocationListAttribute = builder.deltaRevocationListAttribute;
        this.authorityRevocationListAttribute = builder.authorityRevocationListAttribute;
        this.attributeCertificateAttributeAttribute = builder.attributeCertificateAttributeAttribute;
        this.aACertificateAttribute = builder.aACertificateAttribute;
        this.attributeDescriptorCertificateAttribute = builder.attributeDescriptorCertificateAttribute;
        this.attributeCertificateRevocationListAttribute = builder.attributeCertificateRevocationListAttribute;
        this.attributeAuthorityRevocationListAttribute = builder.attributeAuthorityRevocationListAttribute;
        this.ldapUserCertificateAttributeName = builder.ldapUserCertificateAttributeName;
        this.ldapCACertificateAttributeName = builder.ldapCACertificateAttributeName;
        this.ldapCrossCertificateAttributeName = builder.ldapCrossCertificateAttributeName;
        this.ldapCertificateRevocationListAttributeName = builder.ldapCertificateRevocationListAttributeName;
        this.ldapDeltaRevocationListAttributeName = builder.ldapDeltaRevocationListAttributeName;
        this.ldapAuthorityRevocationListAttributeName = builder.ldapAuthorityRevocationListAttributeName;
        this.ldapAttributeCertificateAttributeAttributeName = builder.ldapAttributeCertificateAttributeAttributeName;
        this.ldapAACertificateAttributeName = builder.ldapAACertificateAttributeName;
        this.ldapAttributeDescriptorCertificateAttributeName = builder.ldapAttributeDescriptorCertificateAttributeName;
        this.ldapAttributeCertificateRevocationListAttributeName = builder.ldapAttributeCertificateRevocationListAttributeName;
        this.ldapAttributeAuthorityRevocationListAttributeName = builder.ldapAttributeAuthorityRevocationListAttributeName;
        this.userCertificateSubjectAttributeName = builder.userCertificateSubjectAttributeName;
        this.cACertificateSubjectAttributeName = builder.cACertificateSubjectAttributeName;
        this.crossCertificateSubjectAttributeName = builder.crossCertificateSubjectAttributeName;
        this.certificateRevocationListIssuerAttributeName = builder.certificateRevocationListIssuerAttributeName;
        this.deltaRevocationListIssuerAttributeName = builder.deltaRevocationListIssuerAttributeName;
        this.authorityRevocationListIssuerAttributeName = builder.authorityRevocationListIssuerAttributeName;
        this.attributeCertificateAttributeSubjectAttributeName = builder.attributeCertificateAttributeSubjectAttributeName;
        this.aACertificateSubjectAttributeName = builder.aACertificateSubjectAttributeName;
        this.attributeDescriptorCertificateSubjectAttributeName = builder.attributeDescriptorCertificateSubjectAttributeName;
        this.attributeCertificateRevocationListIssuerAttributeName = builder.attributeCertificateRevocationListIssuerAttributeName;
        this.attributeAuthorityRevocationListIssuerAttributeName = builder.attributeAuthorityRevocationListIssuerAttributeName;
        this.searchForSerialNumberIn = builder.searchForSerialNumberIn;
    }

    @Override
    public Object clone() {
        return this;
    }

    public boolean equal(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof X509LDAPCertStoreParameters)) {
            return false;
        }
        X509LDAPCertStoreParameters params = (X509LDAPCertStoreParameters)o;
        return this.checkField(this.ldapURL, params.ldapURL) && this.checkField(this.baseDN, params.baseDN) && this.checkField(this.userCertificateAttribute, params.userCertificateAttribute) && this.checkField(this.cACertificateAttribute, params.cACertificateAttribute) && this.checkField(this.crossCertificateAttribute, params.crossCertificateAttribute) && this.checkField(this.certificateRevocationListAttribute, params.certificateRevocationListAttribute) && this.checkField(this.deltaRevocationListAttribute, params.deltaRevocationListAttribute) && this.checkField(this.authorityRevocationListAttribute, params.authorityRevocationListAttribute) && this.checkField(this.attributeCertificateAttributeAttribute, params.attributeCertificateAttributeAttribute) && this.checkField(this.aACertificateAttribute, params.aACertificateAttribute) && this.checkField(this.attributeDescriptorCertificateAttribute, params.attributeDescriptorCertificateAttribute) && this.checkField(this.attributeCertificateRevocationListAttribute, params.attributeCertificateRevocationListAttribute) && this.checkField(this.attributeAuthorityRevocationListAttribute, params.attributeAuthorityRevocationListAttribute) && this.checkField(this.ldapUserCertificateAttributeName, params.ldapUserCertificateAttributeName) && this.checkField(this.ldapCACertificateAttributeName, params.ldapCACertificateAttributeName) && this.checkField(this.ldapCrossCertificateAttributeName, params.ldapCrossCertificateAttributeName) && this.checkField(this.ldapCertificateRevocationListAttributeName, params.ldapCertificateRevocationListAttributeName) && this.checkField(this.ldapDeltaRevocationListAttributeName, params.ldapDeltaRevocationListAttributeName) && this.checkField(this.ldapAuthorityRevocationListAttributeName, params.ldapAuthorityRevocationListAttributeName) && this.checkField(this.ldapAttributeCertificateAttributeAttributeName, params.ldapAttributeCertificateAttributeAttributeName) && this.checkField(this.ldapAACertificateAttributeName, params.ldapAACertificateAttributeName) && this.checkField(this.ldapAttributeDescriptorCertificateAttributeName, params.ldapAttributeDescriptorCertificateAttributeName) && this.checkField(this.ldapAttributeCertificateRevocationListAttributeName, params.ldapAttributeCertificateRevocationListAttributeName) && this.checkField(this.ldapAttributeAuthorityRevocationListAttributeName, params.ldapAttributeAuthorityRevocationListAttributeName) && this.checkField(this.userCertificateSubjectAttributeName, params.userCertificateSubjectAttributeName) && this.checkField(this.cACertificateSubjectAttributeName, params.cACertificateSubjectAttributeName) && this.checkField(this.crossCertificateSubjectAttributeName, params.crossCertificateSubjectAttributeName) && this.checkField(this.certificateRevocationListIssuerAttributeName, params.certificateRevocationListIssuerAttributeName) && this.checkField(this.deltaRevocationListIssuerAttributeName, params.deltaRevocationListIssuerAttributeName) && this.checkField(this.authorityRevocationListIssuerAttributeName, params.authorityRevocationListIssuerAttributeName) && this.checkField(this.attributeCertificateAttributeSubjectAttributeName, params.attributeCertificateAttributeSubjectAttributeName) && this.checkField(this.aACertificateSubjectAttributeName, params.aACertificateSubjectAttributeName) && this.checkField(this.attributeDescriptorCertificateSubjectAttributeName, params.attributeDescriptorCertificateSubjectAttributeName) && this.checkField(this.attributeCertificateRevocationListIssuerAttributeName, params.attributeCertificateRevocationListIssuerAttributeName) && this.checkField(this.attributeAuthorityRevocationListIssuerAttributeName, params.attributeAuthorityRevocationListIssuerAttributeName) && this.checkField(this.searchForSerialNumberIn, params.searchForSerialNumberIn);
    }

    private boolean checkField(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    public int hashCode() {
        int hash = 0;
        hash = this.addHashCode(hash, this.userCertificateAttribute);
        hash = this.addHashCode(hash, this.cACertificateAttribute);
        hash = this.addHashCode(hash, this.crossCertificateAttribute);
        hash = this.addHashCode(hash, this.certificateRevocationListAttribute);
        hash = this.addHashCode(hash, this.deltaRevocationListAttribute);
        hash = this.addHashCode(hash, this.authorityRevocationListAttribute);
        hash = this.addHashCode(hash, this.attributeCertificateAttributeAttribute);
        hash = this.addHashCode(hash, this.aACertificateAttribute);
        hash = this.addHashCode(hash, this.attributeDescriptorCertificateAttribute);
        hash = this.addHashCode(hash, this.attributeCertificateRevocationListAttribute);
        hash = this.addHashCode(hash, this.attributeAuthorityRevocationListAttribute);
        hash = this.addHashCode(hash, this.ldapUserCertificateAttributeName);
        hash = this.addHashCode(hash, this.ldapCACertificateAttributeName);
        hash = this.addHashCode(hash, this.ldapCrossCertificateAttributeName);
        hash = this.addHashCode(hash, this.ldapCertificateRevocationListAttributeName);
        hash = this.addHashCode(hash, this.ldapDeltaRevocationListAttributeName);
        hash = this.addHashCode(hash, this.ldapAuthorityRevocationListAttributeName);
        hash = this.addHashCode(hash, this.ldapAttributeCertificateAttributeAttributeName);
        hash = this.addHashCode(hash, this.ldapAACertificateAttributeName);
        hash = this.addHashCode(hash, this.ldapAttributeDescriptorCertificateAttributeName);
        hash = this.addHashCode(hash, this.ldapAttributeCertificateRevocationListAttributeName);
        hash = this.addHashCode(hash, this.ldapAttributeAuthorityRevocationListAttributeName);
        hash = this.addHashCode(hash, this.userCertificateSubjectAttributeName);
        hash = this.addHashCode(hash, this.cACertificateSubjectAttributeName);
        hash = this.addHashCode(hash, this.crossCertificateSubjectAttributeName);
        hash = this.addHashCode(hash, this.certificateRevocationListIssuerAttributeName);
        hash = this.addHashCode(hash, this.deltaRevocationListIssuerAttributeName);
        hash = this.addHashCode(hash, this.authorityRevocationListIssuerAttributeName);
        hash = this.addHashCode(hash, this.attributeCertificateAttributeSubjectAttributeName);
        hash = this.addHashCode(hash, this.aACertificateSubjectAttributeName);
        hash = this.addHashCode(hash, this.attributeDescriptorCertificateSubjectAttributeName);
        hash = this.addHashCode(hash, this.attributeCertificateRevocationListIssuerAttributeName);
        hash = this.addHashCode(hash, this.attributeAuthorityRevocationListIssuerAttributeName);
        hash = this.addHashCode(hash, this.searchForSerialNumberIn);
        return hash;
    }

    private int addHashCode(int hashCode, Object o) {
        return hashCode * 29 + (o == null ? 0 : o.hashCode());
    }

    public String getAACertificateAttribute() {
        return this.aACertificateAttribute;
    }

    public String getAACertificateSubjectAttributeName() {
        return this.aACertificateSubjectAttributeName;
    }

    public String getAttributeAuthorityRevocationListAttribute() {
        return this.attributeAuthorityRevocationListAttribute;
    }

    public String getAttributeAuthorityRevocationListIssuerAttributeName() {
        return this.attributeAuthorityRevocationListIssuerAttributeName;
    }

    public String getAttributeCertificateAttributeAttribute() {
        return this.attributeCertificateAttributeAttribute;
    }

    public String getAttributeCertificateAttributeSubjectAttributeName() {
        return this.attributeCertificateAttributeSubjectAttributeName;
    }

    public String getAttributeCertificateRevocationListAttribute() {
        return this.attributeCertificateRevocationListAttribute;
    }

    public String getAttributeCertificateRevocationListIssuerAttributeName() {
        return this.attributeCertificateRevocationListIssuerAttributeName;
    }

    public String getAttributeDescriptorCertificateAttribute() {
        return this.attributeDescriptorCertificateAttribute;
    }

    public String getAttributeDescriptorCertificateSubjectAttributeName() {
        return this.attributeDescriptorCertificateSubjectAttributeName;
    }

    public String getAuthorityRevocationListAttribute() {
        return this.authorityRevocationListAttribute;
    }

    public String getAuthorityRevocationListIssuerAttributeName() {
        return this.authorityRevocationListIssuerAttributeName;
    }

    public String getBaseDN() {
        return this.baseDN;
    }

    public String getCACertificateAttribute() {
        return this.cACertificateAttribute;
    }

    public String getCACertificateSubjectAttributeName() {
        return this.cACertificateSubjectAttributeName;
    }

    public String getCertificateRevocationListAttribute() {
        return this.certificateRevocationListAttribute;
    }

    public String getCertificateRevocationListIssuerAttributeName() {
        return this.certificateRevocationListIssuerAttributeName;
    }

    public String getCrossCertificateAttribute() {
        return this.crossCertificateAttribute;
    }

    public String getCrossCertificateSubjectAttributeName() {
        return this.crossCertificateSubjectAttributeName;
    }

    public String getDeltaRevocationListAttribute() {
        return this.deltaRevocationListAttribute;
    }

    public String getDeltaRevocationListIssuerAttributeName() {
        return this.deltaRevocationListIssuerAttributeName;
    }

    public String getLdapAACertificateAttributeName() {
        return this.ldapAACertificateAttributeName;
    }

    public String getLdapAttributeAuthorityRevocationListAttributeName() {
        return this.ldapAttributeAuthorityRevocationListAttributeName;
    }

    public String getLdapAttributeCertificateAttributeAttributeName() {
        return this.ldapAttributeCertificateAttributeAttributeName;
    }

    public String getLdapAttributeCertificateRevocationListAttributeName() {
        return this.ldapAttributeCertificateRevocationListAttributeName;
    }

    public String getLdapAttributeDescriptorCertificateAttributeName() {
        return this.ldapAttributeDescriptorCertificateAttributeName;
    }

    public String getLdapAuthorityRevocationListAttributeName() {
        return this.ldapAuthorityRevocationListAttributeName;
    }

    public String getLdapCACertificateAttributeName() {
        return this.ldapCACertificateAttributeName;
    }

    public String getLdapCertificateRevocationListAttributeName() {
        return this.ldapCertificateRevocationListAttributeName;
    }

    public String getLdapCrossCertificateAttributeName() {
        return this.ldapCrossCertificateAttributeName;
    }

    public String getLdapDeltaRevocationListAttributeName() {
        return this.ldapDeltaRevocationListAttributeName;
    }

    public String getLdapURL() {
        return this.ldapURL;
    }

    public String getLdapUserCertificateAttributeName() {
        return this.ldapUserCertificateAttributeName;
    }

    public String getSearchForSerialNumberIn() {
        return this.searchForSerialNumberIn;
    }

    public String getUserCertificateAttribute() {
        return this.userCertificateAttribute;
    }

    public String getUserCertificateSubjectAttributeName() {
        return this.userCertificateSubjectAttributeName;
    }

    public static X509LDAPCertStoreParameters getInstance(LDAPCertStoreParameters params) {
        String server = "ldap://" + params.getServerName() + ":" + params.getPort();
        X509LDAPCertStoreParameters _params = new Builder(server, "").build();
        return _params;
    }

    public static class Builder {
        private String ldapURL;
        private String baseDN;
        private String userCertificateAttribute;
        private String cACertificateAttribute;
        private String crossCertificateAttribute;
        private String certificateRevocationListAttribute;
        private String deltaRevocationListAttribute;
        private String authorityRevocationListAttribute;
        private String attributeCertificateAttributeAttribute;
        private String aACertificateAttribute;
        private String attributeDescriptorCertificateAttribute;
        private String attributeCertificateRevocationListAttribute;
        private String attributeAuthorityRevocationListAttribute;
        private String ldapUserCertificateAttributeName;
        private String ldapCACertificateAttributeName;
        private String ldapCrossCertificateAttributeName;
        private String ldapCertificateRevocationListAttributeName;
        private String ldapDeltaRevocationListAttributeName;
        private String ldapAuthorityRevocationListAttributeName;
        private String ldapAttributeCertificateAttributeAttributeName;
        private String ldapAACertificateAttributeName;
        private String ldapAttributeDescriptorCertificateAttributeName;
        private String ldapAttributeCertificateRevocationListAttributeName;
        private String ldapAttributeAuthorityRevocationListAttributeName;
        private String userCertificateSubjectAttributeName;
        private String cACertificateSubjectAttributeName;
        private String crossCertificateSubjectAttributeName;
        private String certificateRevocationListIssuerAttributeName;
        private String deltaRevocationListIssuerAttributeName;
        private String authorityRevocationListIssuerAttributeName;
        private String attributeCertificateAttributeSubjectAttributeName;
        private String aACertificateSubjectAttributeName;
        private String attributeDescriptorCertificateSubjectAttributeName;
        private String attributeCertificateRevocationListIssuerAttributeName;
        private String attributeAuthorityRevocationListIssuerAttributeName;
        private String searchForSerialNumberIn;

        public Builder() {
            this("ldap://localhost:389", "");
        }

        public Builder(String ldapURL, String baseDN) {
            this.ldapURL = ldapURL;
            this.baseDN = baseDN == null ? "" : baseDN;
            this.userCertificateAttribute = "userCertificate";
            this.cACertificateAttribute = "cACertificate";
            this.crossCertificateAttribute = "crossCertificatePair";
            this.certificateRevocationListAttribute = "certificateRevocationList";
            this.deltaRevocationListAttribute = "deltaRevocationList";
            this.authorityRevocationListAttribute = "authorityRevocationList";
            this.attributeCertificateAttributeAttribute = "attributeCertificateAttribute";
            this.aACertificateAttribute = "aACertificate";
            this.attributeDescriptorCertificateAttribute = "attributeDescriptorCertificate";
            this.attributeCertificateRevocationListAttribute = "attributeCertificateRevocationList";
            this.attributeAuthorityRevocationListAttribute = "attributeAuthorityRevocationList";
            this.ldapUserCertificateAttributeName = "cn";
            this.ldapCACertificateAttributeName = "cn ou o";
            this.ldapCrossCertificateAttributeName = "cn ou o";
            this.ldapCertificateRevocationListAttributeName = "cn ou o";
            this.ldapDeltaRevocationListAttributeName = "cn ou o";
            this.ldapAuthorityRevocationListAttributeName = "cn ou o";
            this.ldapAttributeCertificateAttributeAttributeName = "cn";
            this.ldapAACertificateAttributeName = "cn o ou";
            this.ldapAttributeDescriptorCertificateAttributeName = "cn o ou";
            this.ldapAttributeCertificateRevocationListAttributeName = "cn o ou";
            this.ldapAttributeAuthorityRevocationListAttributeName = "cn o ou";
            this.userCertificateSubjectAttributeName = "cn";
            this.cACertificateSubjectAttributeName = "o ou";
            this.crossCertificateSubjectAttributeName = "o ou";
            this.certificateRevocationListIssuerAttributeName = "o ou";
            this.deltaRevocationListIssuerAttributeName = "o ou";
            this.authorityRevocationListIssuerAttributeName = "o ou";
            this.attributeCertificateAttributeSubjectAttributeName = "cn";
            this.aACertificateSubjectAttributeName = "o ou";
            this.attributeDescriptorCertificateSubjectAttributeName = "o ou";
            this.attributeCertificateRevocationListIssuerAttributeName = "o ou";
            this.attributeAuthorityRevocationListIssuerAttributeName = "o ou";
            this.searchForSerialNumberIn = "uid serialNumber cn";
        }

        public Builder setUserCertificateAttribute(String userCertificateAttribute) {
            this.userCertificateAttribute = userCertificateAttribute;
            return this;
        }

        public Builder setCACertificateAttribute(String cACertificateAttribute) {
            this.cACertificateAttribute = cACertificateAttribute;
            return this;
        }

        public Builder setCrossCertificateAttribute(String crossCertificateAttribute) {
            this.crossCertificateAttribute = crossCertificateAttribute;
            return this;
        }

        public Builder setCertificateRevocationListAttribute(String certificateRevocationListAttribute) {
            this.certificateRevocationListAttribute = certificateRevocationListAttribute;
            return this;
        }

        public Builder setDeltaRevocationListAttribute(String deltaRevocationListAttribute) {
            this.deltaRevocationListAttribute = deltaRevocationListAttribute;
            return this;
        }

        public Builder setAuthorityRevocationListAttribute(String authorityRevocationListAttribute) {
            this.authorityRevocationListAttribute = authorityRevocationListAttribute;
            return this;
        }

        public Builder setAttributeCertificateAttributeAttribute(String attributeCertificateAttributeAttribute) {
            this.attributeCertificateAttributeAttribute = attributeCertificateAttributeAttribute;
            return this;
        }

        public Builder setAACertificateAttribute(String aACertificateAttribute) {
            this.aACertificateAttribute = aACertificateAttribute;
            return this;
        }

        public Builder setAttributeDescriptorCertificateAttribute(String attributeDescriptorCertificateAttribute) {
            this.attributeDescriptorCertificateAttribute = attributeDescriptorCertificateAttribute;
            return this;
        }

        public Builder setAttributeCertificateRevocationListAttribute(String attributeCertificateRevocationListAttribute) {
            this.attributeCertificateRevocationListAttribute = attributeCertificateRevocationListAttribute;
            return this;
        }

        public Builder setAttributeAuthorityRevocationListAttribute(String attributeAuthorityRevocationListAttribute) {
            this.attributeAuthorityRevocationListAttribute = attributeAuthorityRevocationListAttribute;
            return this;
        }

        public Builder setLdapUserCertificateAttributeName(String ldapUserCertificateAttributeName) {
            this.ldapUserCertificateAttributeName = ldapUserCertificateAttributeName;
            return this;
        }

        public Builder setLdapCACertificateAttributeName(String ldapCACertificateAttributeName) {
            this.ldapCACertificateAttributeName = ldapCACertificateAttributeName;
            return this;
        }

        public Builder setLdapCrossCertificateAttributeName(String ldapCrossCertificateAttributeName) {
            this.ldapCrossCertificateAttributeName = ldapCrossCertificateAttributeName;
            return this;
        }

        public Builder setLdapCertificateRevocationListAttributeName(String ldapCertificateRevocationListAttributeName) {
            this.ldapCertificateRevocationListAttributeName = ldapCertificateRevocationListAttributeName;
            return this;
        }

        public Builder setLdapDeltaRevocationListAttributeName(String ldapDeltaRevocationListAttributeName) {
            this.ldapDeltaRevocationListAttributeName = ldapDeltaRevocationListAttributeName;
            return this;
        }

        public Builder setLdapAuthorityRevocationListAttributeName(String ldapAuthorityRevocationListAttributeName) {
            this.ldapAuthorityRevocationListAttributeName = ldapAuthorityRevocationListAttributeName;
            return this;
        }

        public Builder setLdapAttributeCertificateAttributeAttributeName(String ldapAttributeCertificateAttributeAttributeName) {
            this.ldapAttributeCertificateAttributeAttributeName = ldapAttributeCertificateAttributeAttributeName;
            return this;
        }

        public Builder setLdapAACertificateAttributeName(String ldapAACertificateAttributeName) {
            this.ldapAACertificateAttributeName = ldapAACertificateAttributeName;
            return this;
        }

        public Builder setLdapAttributeDescriptorCertificateAttributeName(String ldapAttributeDescriptorCertificateAttributeName) {
            this.ldapAttributeDescriptorCertificateAttributeName = ldapAttributeDescriptorCertificateAttributeName;
            return this;
        }

        public Builder setLdapAttributeCertificateRevocationListAttributeName(String ldapAttributeCertificateRevocationListAttributeName) {
            this.ldapAttributeCertificateRevocationListAttributeName = ldapAttributeCertificateRevocationListAttributeName;
            return this;
        }

        public Builder setLdapAttributeAuthorityRevocationListAttributeName(String ldapAttributeAuthorityRevocationListAttributeName) {
            this.ldapAttributeAuthorityRevocationListAttributeName = ldapAttributeAuthorityRevocationListAttributeName;
            return this;
        }

        public Builder setUserCertificateSubjectAttributeName(String userCertificateSubjectAttributeName) {
            this.userCertificateSubjectAttributeName = userCertificateSubjectAttributeName;
            return this;
        }

        public Builder setCACertificateSubjectAttributeName(String cACertificateSubjectAttributeName) {
            this.cACertificateSubjectAttributeName = cACertificateSubjectAttributeName;
            return this;
        }

        public Builder setCrossCertificateSubjectAttributeName(String crossCertificateSubjectAttributeName) {
            this.crossCertificateSubjectAttributeName = crossCertificateSubjectAttributeName;
            return this;
        }

        public Builder setCertificateRevocationListIssuerAttributeName(String certificateRevocationListIssuerAttributeName) {
            this.certificateRevocationListIssuerAttributeName = certificateRevocationListIssuerAttributeName;
            return this;
        }

        public Builder setDeltaRevocationListIssuerAttributeName(String deltaRevocationListIssuerAttributeName) {
            this.deltaRevocationListIssuerAttributeName = deltaRevocationListIssuerAttributeName;
            return this;
        }

        public Builder setAuthorityRevocationListIssuerAttributeName(String authorityRevocationListIssuerAttributeName) {
            this.authorityRevocationListIssuerAttributeName = authorityRevocationListIssuerAttributeName;
            return this;
        }

        public Builder setAttributeCertificateAttributeSubjectAttributeName(String attributeCertificateAttributeSubjectAttributeName) {
            this.attributeCertificateAttributeSubjectAttributeName = attributeCertificateAttributeSubjectAttributeName;
            return this;
        }

        public Builder setAACertificateSubjectAttributeName(String aACertificateSubjectAttributeName) {
            this.aACertificateSubjectAttributeName = aACertificateSubjectAttributeName;
            return this;
        }

        public Builder setAttributeDescriptorCertificateSubjectAttributeName(String attributeDescriptorCertificateSubjectAttributeName) {
            this.attributeDescriptorCertificateSubjectAttributeName = attributeDescriptorCertificateSubjectAttributeName;
            return this;
        }

        public Builder setAttributeCertificateRevocationListIssuerAttributeName(String attributeCertificateRevocationListIssuerAttributeName) {
            this.attributeCertificateRevocationListIssuerAttributeName = attributeCertificateRevocationListIssuerAttributeName;
            return this;
        }

        public Builder setAttributeAuthorityRevocationListIssuerAttributeName(String attributeAuthorityRevocationListIssuerAttributeName) {
            this.attributeAuthorityRevocationListIssuerAttributeName = attributeAuthorityRevocationListIssuerAttributeName;
            return this;
        }

        public Builder setSearchForSerialNumberIn(String searchForSerialNumberIn) {
            this.searchForSerialNumberIn = searchForSerialNumberIn;
            return this;
        }

        public X509LDAPCertStoreParameters build() {
            if (this.ldapUserCertificateAttributeName == null || this.ldapCACertificateAttributeName == null || this.ldapCrossCertificateAttributeName == null || this.ldapCertificateRevocationListAttributeName == null || this.ldapDeltaRevocationListAttributeName == null || this.ldapAuthorityRevocationListAttributeName == null || this.ldapAttributeCertificateAttributeAttributeName == null || this.ldapAACertificateAttributeName == null || this.ldapAttributeDescriptorCertificateAttributeName == null || this.ldapAttributeCertificateRevocationListAttributeName == null || this.ldapAttributeAuthorityRevocationListAttributeName == null || this.userCertificateSubjectAttributeName == null || this.cACertificateSubjectAttributeName == null || this.crossCertificateSubjectAttributeName == null || this.certificateRevocationListIssuerAttributeName == null || this.deltaRevocationListIssuerAttributeName == null || this.authorityRevocationListIssuerAttributeName == null || this.attributeCertificateAttributeSubjectAttributeName == null || this.aACertificateSubjectAttributeName == null || this.attributeDescriptorCertificateSubjectAttributeName == null || this.attributeCertificateRevocationListIssuerAttributeName == null || this.attributeAuthorityRevocationListIssuerAttributeName == null) {
                throw new IllegalArgumentException("Necessary parameters not specified.");
            }
            return new X509LDAPCertStoreParameters(this);
        }
    }
}

