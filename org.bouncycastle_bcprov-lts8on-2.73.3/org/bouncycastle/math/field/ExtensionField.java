/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.field;

import org.bouncycastle.math.field.FiniteField;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface ExtensionField
extends FiniteField {
    public FiniteField getSubfield();

    public int getDegree();
}

