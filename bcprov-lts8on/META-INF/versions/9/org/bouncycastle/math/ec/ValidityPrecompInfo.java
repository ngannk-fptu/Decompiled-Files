/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import org.bouncycastle.math.ec.PreCompInfo;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class ValidityPrecompInfo
implements PreCompInfo {
    static final String PRECOMP_NAME = "bc_validity";
    private boolean failed = false;
    private boolean curveEquationPassed = false;
    private boolean orderPassed = false;

    ValidityPrecompInfo() {
    }

    boolean hasFailed() {
        return this.failed;
    }

    void reportFailed() {
        this.failed = true;
    }

    boolean hasCurveEquationPassed() {
        return this.curveEquationPassed;
    }

    void reportCurveEquationPassed() {
        this.curveEquationPassed = true;
    }

    boolean hasOrderPassed() {
        return this.orderPassed;
    }

    void reportOrderPassed() {
        this.orderPassed = true;
    }
}

