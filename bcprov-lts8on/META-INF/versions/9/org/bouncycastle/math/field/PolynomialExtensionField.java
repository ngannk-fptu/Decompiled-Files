/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.field;

import org.bouncycastle.math.field.ExtensionField;
import org.bouncycastle.math.field.Polynomial;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface PolynomialExtensionField
extends ExtensionField {
    public Polynomial getMinimalPolynomial();
}

