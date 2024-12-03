/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.util.Memoable
 */
package org.bouncycastle.cert.path;

import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.util.Memoable;

public class CertPathValidationContext
implements Memoable {
    private Set criticalExtensions;
    private Set handledExtensions = new HashSet();
    private boolean endEntity;
    private int index;

    public CertPathValidationContext(Set criticalExtensionsOIDs) {
        this.criticalExtensions = criticalExtensionsOIDs;
    }

    public void addHandledExtension(ASN1ObjectIdentifier extensionIdentifier) {
        this.handledExtensions.add(extensionIdentifier);
    }

    public void setIsEndEntity(boolean isEndEntity) {
        this.endEntity = isEndEntity;
    }

    public Set getUnhandledCriticalExtensionOIDs() {
        HashSet rv = new HashSet(this.criticalExtensions);
        rv.removeAll(this.handledExtensions);
        return rv;
    }

    public boolean isEndEntity() {
        return this.endEntity;
    }

    public Memoable copy() {
        return null;
    }

    public void reset(Memoable other) {
    }
}

