/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

final class SignerProperty {
    private final Bundle bundle;
    private final String pattern;

    public SignerProperty(String pattern) {
        this.pattern = pattern;
        this.bundle = null;
    }

    SignerProperty(Bundle bundle) {
        this.bundle = bundle;
        this.pattern = null;
    }

    public boolean equals(Object o) {
        if (!(o instanceof SignerProperty)) {
            return false;
        }
        SignerProperty other = (SignerProperty)o;
        Bundle matchBundle = this.bundle != null ? this.bundle : other.bundle;
        String matchPattern = this.bundle != null ? other.pattern : this.pattern;
        Map<X509Certificate, List<X509Certificate>> signers = matchBundle.getSignerCertificates(2);
        for (List<X509Certificate> signerCerts : signers.values()) {
            ArrayList<String> dnChain = new ArrayList<String>(signerCerts.size());
            for (X509Certificate signerCert : signerCerts) {
                dnChain.add(signerCert.getSubjectDN().getName());
            }
            try {
                if (!FrameworkUtil.matchDistinguishedNameChain(matchPattern, dnChain)) continue;
                return true;
            }
            catch (IllegalArgumentException e) {
            }
        }
        return false;
    }

    public int hashCode() {
        return 31;
    }

    boolean isBundleSigned() {
        if (this.bundle == null) {
            return false;
        }
        Map<X509Certificate, List<X509Certificate>> signers = this.bundle.getSignerCertificates(2);
        return !signers.isEmpty();
    }
}

