/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.cert.path;

import java.util.Collections;
import java.util.Set;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.util.Arrays;

public class CertPathValidationResult {
    private final boolean isValid;
    private final CertPathValidationException cause;
    private final Set unhandledCriticalExtensionOIDs;
    private final int certIndex;
    private final int ruleIndex;
    private CertPathValidationException[] causes;
    private int[] certIndexes;
    private int[] ruleIndexes;

    public CertPathValidationResult(CertPathValidationContext context) {
        this.unhandledCriticalExtensionOIDs = Collections.unmodifiableSet(context.getUnhandledCriticalExtensionOIDs());
        this.isValid = this.unhandledCriticalExtensionOIDs.isEmpty();
        this.certIndex = -1;
        this.ruleIndex = -1;
        this.cause = null;
    }

    public CertPathValidationResult(CertPathValidationContext context, int certIndex, int ruleIndex, CertPathValidationException cause) {
        this.unhandledCriticalExtensionOIDs = Collections.unmodifiableSet(context.getUnhandledCriticalExtensionOIDs());
        this.isValid = false;
        this.certIndex = certIndex;
        this.ruleIndex = ruleIndex;
        this.cause = cause;
    }

    public CertPathValidationResult(CertPathValidationContext context, int[] certIndexes, int[] ruleIndexes, CertPathValidationException[] causes) {
        this.unhandledCriticalExtensionOIDs = Collections.unmodifiableSet(context.getUnhandledCriticalExtensionOIDs());
        this.isValid = false;
        this.cause = causes[0];
        this.certIndex = certIndexes[0];
        this.ruleIndex = ruleIndexes[0];
        this.causes = causes;
        this.certIndexes = certIndexes;
        this.ruleIndexes = ruleIndexes;
    }

    public boolean isValid() {
        return this.isValid;
    }

    public CertPathValidationException getCause() {
        if (this.cause != null) {
            return this.cause;
        }
        if (!this.unhandledCriticalExtensionOIDs.isEmpty()) {
            return new CertPathValidationException("Unhandled Critical Extensions");
        }
        return null;
    }

    public int getFailingCertIndex() {
        return this.certIndex;
    }

    public int getFailingRuleIndex() {
        return this.ruleIndex;
    }

    public Set getUnhandledCriticalExtensionOIDs() {
        return this.unhandledCriticalExtensionOIDs;
    }

    public boolean isDetailed() {
        return this.certIndexes != null;
    }

    public CertPathValidationException[] getCauses() {
        if (this.causes != null) {
            CertPathValidationException[] rv = new CertPathValidationException[this.causes.length];
            System.arraycopy(this.causes, 0, rv, 0, this.causes.length);
            return rv;
        }
        if (!this.unhandledCriticalExtensionOIDs.isEmpty()) {
            return new CertPathValidationException[]{new CertPathValidationException("Unhandled Critical Extensions")};
        }
        return null;
    }

    public int[] getFailingCertIndexes() {
        return Arrays.clone((int[])this.certIndexes);
    }

    public int[] getFailingRuleIndexes() {
        return Arrays.clone((int[])this.ruleIndexes);
    }
}

