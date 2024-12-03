/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Integers
 */
package org.bouncycastle.cert.path;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.cert.path.CertPathValidationResult;
import org.bouncycastle.util.Integers;

class CertPathValidationResultBuilder {
    private final CertPathValidationContext context;
    private final List<Integer> certIndexes = new ArrayList<Integer>();
    private final List<Integer> ruleIndexes = new ArrayList<Integer>();
    private final List<CertPathValidationException> exceptions = new ArrayList<CertPathValidationException>();

    CertPathValidationResultBuilder(CertPathValidationContext context) {
        this.context = context;
    }

    public CertPathValidationResult build() {
        if (this.exceptions.isEmpty()) {
            return new CertPathValidationResult(this.context);
        }
        return new CertPathValidationResult(this.context, this.toInts(this.certIndexes), this.toInts(this.ruleIndexes), this.exceptions.toArray(new CertPathValidationException[this.exceptions.size()]));
    }

    public void addException(int certIndex, int ruleIndex, CertPathValidationException exception) {
        this.certIndexes.add(Integers.valueOf((int)certIndex));
        this.ruleIndexes.add(Integers.valueOf((int)ruleIndex));
        this.exceptions.add(exception);
    }

    private int[] toInts(List<Integer> values) {
        int[] rv = new int[values.size()];
        for (int i = 0; i != rv.length; ++i) {
            rv[i] = values.get(i);
        }
        return rv;
    }
}

