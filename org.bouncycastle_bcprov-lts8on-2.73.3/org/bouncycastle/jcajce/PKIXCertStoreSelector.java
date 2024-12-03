/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import java.io.IOException;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509CertSelector;
import java.util.Collection;
import org.bouncycastle.util.Selector;

public class PKIXCertStoreSelector<T extends Certificate>
implements Selector<T> {
    private final CertSelector baseSelector;

    private PKIXCertStoreSelector(CertSelector baseSelector) {
        this.baseSelector = baseSelector;
    }

    public Certificate getCertificate() {
        if (this.baseSelector instanceof X509CertSelector) {
            return ((X509CertSelector)this.baseSelector).getCertificate();
        }
        return null;
    }

    @Override
    public boolean match(Certificate cert) {
        return this.baseSelector.match(cert);
    }

    @Override
    public Object clone() {
        return new PKIXCertStoreSelector<T>(this.baseSelector);
    }

    public static Collection<? extends Certificate> getCertificates(PKIXCertStoreSelector selector, CertStore certStore) throws CertStoreException {
        return certStore.getCertificates(new SelectorClone(selector));
    }

    public static class Builder {
        private final CertSelector baseSelector;

        public Builder(CertSelector certSelector) {
            this.baseSelector = (CertSelector)certSelector.clone();
        }

        public PKIXCertStoreSelector<? extends Certificate> build() {
            return new PKIXCertStoreSelector(this.baseSelector);
        }
    }

    private static class SelectorClone
    extends X509CertSelector {
        private final PKIXCertStoreSelector selector;

        SelectorClone(PKIXCertStoreSelector selector) {
            this.selector = selector;
            if (selector.baseSelector instanceof X509CertSelector) {
                X509CertSelector baseSelector = (X509CertSelector)selector.baseSelector;
                this.setAuthorityKeyIdentifier(baseSelector.getAuthorityKeyIdentifier());
                this.setBasicConstraints(baseSelector.getBasicConstraints());
                this.setCertificate(baseSelector.getCertificate());
                this.setCertificateValid(baseSelector.getCertificateValid());
                this.setKeyUsage(baseSelector.getKeyUsage());
                this.setMatchAllSubjectAltNames(baseSelector.getMatchAllSubjectAltNames());
                this.setPrivateKeyValid(baseSelector.getPrivateKeyValid());
                this.setSerialNumber(baseSelector.getSerialNumber());
                this.setSubjectKeyIdentifier(baseSelector.getSubjectKeyIdentifier());
                this.setSubjectPublicKey(baseSelector.getSubjectPublicKey());
                try {
                    this.setExtendedKeyUsage(baseSelector.getExtendedKeyUsage());
                    this.setIssuer(baseSelector.getIssuerAsBytes());
                    this.setNameConstraints(baseSelector.getNameConstraints());
                    this.setPathToNames(baseSelector.getPathToNames());
                    this.setPolicy(baseSelector.getPolicy());
                    this.setSubject(baseSelector.getSubjectAsBytes());
                    this.setSubjectAlternativeNames(baseSelector.getSubjectAlternativeNames());
                    this.setSubjectPublicKeyAlgID(baseSelector.getSubjectPublicKeyAlgID());
                }
                catch (IOException e) {
                    throw new IllegalStateException("base selector invalid: " + e.getMessage(), e);
                }
            }
        }

        @Override
        public boolean match(Certificate certificate) {
            return this.selector == null ? certificate != null : this.selector.match(certificate);
        }
    }
}

