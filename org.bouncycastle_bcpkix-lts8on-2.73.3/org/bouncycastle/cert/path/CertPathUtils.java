/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.path;

import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.cert.X509CertificateHolder;

class CertPathUtils {
    CertPathUtils() {
    }

    static Set getCriticalExtensionsOIDs(X509CertificateHolder[] certificates) {
        HashSet criticalExtensions = new HashSet();
        for (int i = 0; i != certificates.length; ++i) {
            criticalExtensions.addAll(certificates[i].getCriticalExtensionOIDs());
        }
        return criticalExtensions;
    }
}

