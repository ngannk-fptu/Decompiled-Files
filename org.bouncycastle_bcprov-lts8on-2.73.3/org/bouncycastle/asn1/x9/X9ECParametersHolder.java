/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.math.ec.ECCurve;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class X9ECParametersHolder {
    private ECCurve curve;
    private X9ECParameters params;

    public synchronized ECCurve getCurve() {
        if (this.curve == null) {
            this.curve = this.createCurve();
        }
        return this.curve;
    }

    public synchronized X9ECParameters getParameters() {
        if (this.params == null) {
            this.params = this.createParameters();
        }
        return this.params;
    }

    protected ECCurve createCurve() {
        return this.createParameters().getCurve();
    }

    protected abstract X9ECParameters createParameters();
}

