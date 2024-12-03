/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import java.security.cert.CertPathParameters;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.jcajce.PKIXCertStore;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;

public class PKIXExtendedParameters
implements CertPathParameters {
    public static final int PKIX_VALIDITY_MODEL = 0;
    public static final int CHAIN_VALIDITY_MODEL = 1;
    private final PKIXParameters baseParameters;
    private final PKIXCertStoreSelector targetConstraints;
    private final Date validityDate;
    private final Date date;
    private final List<PKIXCertStore> extraCertStores;
    private final Map<GeneralName, PKIXCertStore> namedCertificateStoreMap;
    private final List<PKIXCRLStore> extraCRLStores;
    private final Map<GeneralName, PKIXCRLStore> namedCRLStoreMap;
    private final boolean revocationEnabled;
    private final boolean useDeltas;
    private final int validityModel;
    private final Set<TrustAnchor> trustAnchors;

    private PKIXExtendedParameters(Builder builder) {
        this.baseParameters = builder.baseParameters;
        this.validityDate = builder.validityDate;
        this.date = builder.date;
        this.extraCertStores = Collections.unmodifiableList(builder.extraCertStores);
        this.namedCertificateStoreMap = Collections.unmodifiableMap(new HashMap(builder.namedCertificateStoreMap));
        this.extraCRLStores = Collections.unmodifiableList(builder.extraCRLStores);
        this.namedCRLStoreMap = Collections.unmodifiableMap(new HashMap(builder.namedCRLStoreMap));
        this.targetConstraints = builder.targetConstraints;
        this.revocationEnabled = builder.revocationEnabled;
        this.useDeltas = builder.useDeltas;
        this.validityModel = builder.validityModel;
        this.trustAnchors = Collections.unmodifiableSet(builder.trustAnchors);
    }

    public List<PKIXCertStore> getCertificateStores() {
        return this.extraCertStores;
    }

    public Map<GeneralName, PKIXCertStore> getNamedCertificateStoreMap() {
        return this.namedCertificateStoreMap;
    }

    public List<PKIXCRLStore> getCRLStores() {
        return this.extraCRLStores;
    }

    public Map<GeneralName, PKIXCRLStore> getNamedCRLStoreMap() {
        return this.namedCRLStoreMap;
    }

    public Date getValidityDate() {
        return null == this.validityDate ? null : new Date(this.validityDate.getTime());
    }

    public boolean isUseDeltasEnabled() {
        return this.useDeltas;
    }

    public int getValidityModel() {
        return this.validityModel;
    }

    @Override
    public Object clone() {
        return this;
    }

    public PKIXCertStoreSelector getTargetConstraints() {
        return this.targetConstraints;
    }

    public Set getTrustAnchors() {
        return this.trustAnchors;
    }

    public Set getInitialPolicies() {
        return this.baseParameters.getInitialPolicies();
    }

    public String getSigProvider() {
        return this.baseParameters.getSigProvider();
    }

    public boolean isExplicitPolicyRequired() {
        return this.baseParameters.isExplicitPolicyRequired();
    }

    public boolean isAnyPolicyInhibited() {
        return this.baseParameters.isAnyPolicyInhibited();
    }

    public boolean isPolicyMappingInhibited() {
        return this.baseParameters.isPolicyMappingInhibited();
    }

    public List getCertPathCheckers() {
        return this.baseParameters.getCertPathCheckers();
    }

    public List<CertStore> getCertStores() {
        return this.baseParameters.getCertStores();
    }

    public boolean isRevocationEnabled() {
        return this.revocationEnabled;
    }

    public boolean getPolicyQualifiersRejected() {
        return this.baseParameters.getPolicyQualifiersRejected();
    }

    public static class Builder {
        private final PKIXParameters baseParameters;
        private final Date validityDate;
        private final Date date;
        private PKIXCertStoreSelector targetConstraints;
        private List<PKIXCertStore> extraCertStores = new ArrayList<PKIXCertStore>();
        private Map<GeneralName, PKIXCertStore> namedCertificateStoreMap = new HashMap<GeneralName, PKIXCertStore>();
        private List<PKIXCRLStore> extraCRLStores = new ArrayList<PKIXCRLStore>();
        private Map<GeneralName, PKIXCRLStore> namedCRLStoreMap = new HashMap<GeneralName, PKIXCRLStore>();
        private boolean revocationEnabled;
        private int validityModel = 0;
        private boolean useDeltas = false;
        private Set<TrustAnchor> trustAnchors;

        public Builder(PKIXParameters baseParameters) {
            this.baseParameters = (PKIXParameters)baseParameters.clone();
            CertSelector constraints = baseParameters.getTargetCertConstraints();
            if (constraints != null) {
                this.targetConstraints = new PKIXCertStoreSelector.Builder(constraints).build();
            }
            this.validityDate = baseParameters.getDate();
            this.date = this.validityDate == null ? new Date() : this.validityDate;
            this.revocationEnabled = baseParameters.isRevocationEnabled();
            this.trustAnchors = baseParameters.getTrustAnchors();
        }

        public Builder(PKIXExtendedParameters baseParameters) {
            this.baseParameters = baseParameters.baseParameters;
            this.validityDate = baseParameters.validityDate;
            this.date = baseParameters.date;
            this.targetConstraints = baseParameters.targetConstraints;
            this.extraCertStores = new ArrayList<PKIXCertStore>(baseParameters.extraCertStores);
            this.namedCertificateStoreMap = new HashMap<GeneralName, PKIXCertStore>(baseParameters.namedCertificateStoreMap);
            this.extraCRLStores = new ArrayList<PKIXCRLStore>(baseParameters.extraCRLStores);
            this.namedCRLStoreMap = new HashMap<GeneralName, PKIXCRLStore>(baseParameters.namedCRLStoreMap);
            this.useDeltas = baseParameters.useDeltas;
            this.validityModel = baseParameters.validityModel;
            this.revocationEnabled = baseParameters.isRevocationEnabled();
            this.trustAnchors = baseParameters.getTrustAnchors();
        }

        public Builder addCertificateStore(PKIXCertStore store) {
            this.extraCertStores.add(store);
            return this;
        }

        public Builder addNamedCertificateStore(GeneralName issuerAltName, PKIXCertStore store) {
            this.namedCertificateStoreMap.put(issuerAltName, store);
            return this;
        }

        public Builder addCRLStore(PKIXCRLStore store) {
            this.extraCRLStores.add(store);
            return this;
        }

        public Builder addNamedCRLStore(GeneralName issuerAltName, PKIXCRLStore store) {
            this.namedCRLStoreMap.put(issuerAltName, store);
            return this;
        }

        public Builder setTargetConstraints(PKIXCertStoreSelector selector) {
            this.targetConstraints = selector;
            return this;
        }

        public Builder setUseDeltasEnabled(boolean useDeltas) {
            this.useDeltas = useDeltas;
            return this;
        }

        public Builder setValidityModel(int validityModel) {
            this.validityModel = validityModel;
            return this;
        }

        public Builder setTrustAnchor(TrustAnchor trustAnchor) {
            this.trustAnchors = Collections.singleton(trustAnchor);
            return this;
        }

        public Builder setTrustAnchors(Set<TrustAnchor> trustAnchors) {
            this.trustAnchors = trustAnchors;
            return this;
        }

        public void setRevocationEnabled(boolean revocationEnabled) {
            this.revocationEnabled = revocationEnabled;
        }

        public PKIXExtendedParameters build() {
            return new PKIXExtendedParameters(this);
        }
    }
}

